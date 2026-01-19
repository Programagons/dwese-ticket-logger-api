package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.config;


import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services.CustomUserDetailsService;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Inyección de dependencias
    @Autowired
    private JwtUtil jwtUtil; // Utilidad para generar, extraer y validar tokens JWT

    @Autowired
    private CustomUserDetailsService userDetailsService; // Servicio personalizado para cargar detalles del usuario

}