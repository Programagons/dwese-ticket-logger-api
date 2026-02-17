package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.config;

import io.jsonwebtoken.Claims;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services.CustomUserDetailsService;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * Configuración de WebSockets con autenticación basada en JWT.
 * Se habilita el uso de STOMP para la mensajería y se configura la autenticación
 * mediante la extracción y validación de tokens JWT.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService; // Servicio para cargar detalles del usuario.

    /**
     * Registra el punto de conexión WebSocket en "/ws"-
     * Permite conexiones desde cualquier origen.
     *
     * @param registry Registro de los endpoints de Stomp.
     */

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }

    /**
     * Configura el broker de mensajes para manejar la comunicación WebSocket.
     * Se habilitan tópicos para mensajes generales y privados.
     *
     * @param registry Registro de configuración del broker de mensajes.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/queue", "/topic"); // Canales de comunicación
        registry.setUserDestinationPrefix("/user"); // Permite enviar mensajes privados a usuarios específicos
    }

    /**
     *
     * Interceptor para manejar la autenticación basada en JWT en los mensajes entrantes de WebSockets.
     * @param registration Registro del canal de entrada del cliente.
     */

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                String token = accessor.getFirstNativeHeader("Authorization");

                logger.info("⚡ WebSocket intentando autenticar...");

                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    String username = jwtUtil.extractUsername(token);
                    logger.info("📝 Token recibido en WebSocket: " + token);
                    logger.info("👤 Usuario extraído del token: " + username);

                    if (jwtUtil.validateToken(token, username)) {
                        var userDetails = userDetailsService.loadUserByUsername(username);
                        Claims claims = jwtUtil.extractAllClaims(token);
                        List<String> roles = claims.get("roles", List.class);

                        logger.info("✅ Usuario autenticado en WebSocket: " + username + " con roles: " + roles);

                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                        // Asocia la autenticación al contexto de seguridad de Spring
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        // Asocia la autenticación al WebSocket
                        accessor.setUser(authToken);
                    } else {
                        logger.warn("❌ Token inválido en WebSocket para el usuario: " + username);
                    }
                } else {
                    logger.warn("⚠️ No se encontró un token en la conexión WebSocket. Rechazando conexión.");
                }

                return message;
            }
        });
    }

}
