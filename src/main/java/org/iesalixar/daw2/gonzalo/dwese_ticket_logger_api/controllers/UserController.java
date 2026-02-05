package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.controllers;

import io.jsonwebtoken.Claims;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.UserDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(RegionController.class);


    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<UserDTO> getUser(Authentication authentication){
        logger.info("Solicitando la información del usuario logueado");
        Claims claims = (Claims) authentication.getDetails();
        Long id = (Long) claims.get("id");

        try {
            UserDTO userDTO = userService.getUserById(id);
            logger.info("Se ha encontrado el usuario con identificador {}.", id);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.error("Error al obtener el usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
