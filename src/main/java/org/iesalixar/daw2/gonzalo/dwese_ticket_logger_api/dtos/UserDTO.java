package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Clase DTO (Data Transfer Object) que representa una usuario.
 *
 * Esta clase se utiliza para transferir datos de una usuario
 * entre las capas de la aplicación, especialmente para exponerlos
 * a través de la API sin incluir información innecesaria o sensible.
 *
 */
@Getter
@Setter
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private boolean enabled;
    private String firstName;
    private String lastName;
    private String image;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDateTime lastPasswordChangeDate;

}
