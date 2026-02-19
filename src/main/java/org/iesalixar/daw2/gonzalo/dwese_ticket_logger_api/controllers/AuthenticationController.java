package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.controllers;


import jakarta.validation.Valid;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.AuthRequestDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.AuthResponseDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.TwoFactorDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.Role;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.User;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services.EmailService;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services.UserService;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador responsable de gestionar las solicitudes relacionadas con la autenticación.
 * Proporciona un endpoint para autenticar usuarios y generar un token JWT en caso de éxito.
 */
@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager; // Maneja la lógica de autenticación
    @Autowired
    private JwtUtil jwtUtil; // Utilidad personalizada para manejar tokens JWT
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;


    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDTO> authenticate(@Valid @RequestBody AuthRequestDTO authRequest) {
        try {
            // Validar datos de entrada (opcional si no usas validación adicional en DTO)
            if (authRequest.getUsername() == null || authRequest.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthResponseDTO(null, "El nombre de usuario y la contraseña son obligatorios.", false));
            }
            // Intenta autenticar al usuario con las credenciales proporcionadas
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            // Obtiene el nombre de usuario autenticado
            String username = authentication.getName();
            // Extrae los roles del usuario autenticado desde las autoridades asignadas
            List<String> roles = authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority()) // Convierte cada autoridad en su representación de texto
                    .toList();
            // Genera un token JWT para el usuario autenticado, incluyendo sus roles
            Long id = userService.getIdByUsername(username);
            String token = jwtUtil.generateToken(username, roles, id, true);
            String code = generateCode();
            userService.saveCode(code, id);
            // Retorna una respuesta con el token JWT y un mensaje de éxito
            emailService.sendEmail("gpulcab051@g.educaand.es", "Primer email", "hola mundo");
            return ResponseEntity.ok(new AuthResponseDTO(token, "Authentication successful", true));
        } catch (BadCredentialsException e) {
            // Manejo de credenciales inválidas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDTO(null, "Credenciales inválidas. Por favor, verifica tus datos.", false));
        } catch (Exception e) {
            // Manejo de cualquier otro error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponseDTO(null, "Ocurrió un error inesperado. Por favor, inténtalo de nuevo más tarde.", false));
        }
    }


    /**
     * Maneja excepciones no controladas que puedan ocurrir en el controlador.
     *
     * @param e La excepción lanzada.
     * @return Una respuesta HTTP con el mensaje de error y el estado HTTP correspondiente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResponseDTO> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthResponseDTO(null, "Ocurrió un error inesperado:" + e.getMessage(), false));

    }


    private String generateCode(){
        return RandomStringUtils.randomNumeric(6);
    }


    private void generateEmail(String email, String code){
        try{
            emailService.sendEmail(email, "Código de autenticación", "Su código de autenticación es: " + code);

        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    private void saveCode(){

    }

    @PostMapping("/twofactor")
    public ResponseEntity<AuthResponseDTO> twofactor(@Valid @RequestBody TwoFactorDTO twoFactorDTO, @RequestHeader("Authorization") String tokenHeader) {
        logger.info("Iniciando two-factor authentication" + twoFactorDTO.toString());


        // 1. Limpiamos el prefijo "Bearer "
        String token = tokenHeader.replace("Bearer ", "");


        // 2. Usamos tu servicio JWT/Utilidad para extraer el ID
        Long id = jwtUtil.extractClaim(token, claims -> claims.get("id", Long.class));


        User user =  userService.getUserById(id);
        if (user != null && StringUtils.equals(twoFactorDTO.getCode(), user.getCode())) {
            List<String> roles = user.getRoles().stream()
                    .map(Role::getName) // Ajusta según tu entidad User/Role
                    .toList();


            String tokenRenew = jwtUtil.generateToken(user.getUsername(), roles, id, false);


            userService.clear2FactorCode(id);


            return ResponseEntity.ok(new AuthResponseDTO(tokenRenew, "Autenticación exitosa", false));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponseDTO(null, "El código enviado no es correcto.", false));
    }






}

