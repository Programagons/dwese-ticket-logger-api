package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.mappers;


import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.UserDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convierte una entidad 'User' a un 'UserDTO' (datos básicos).
     *
     * @param user Entidad de usuario.
     * @return DTO correspondiente.
     */

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setImage(user.getImage());
        return dto;
    }


    /**
     * Convierte un 'AutorDTO' a una entidad 'Autor'.
     *
     * @param dto DTO de autor.
     * @return Entidad Autor
     */
    public UserDTO toEntity(UserDTO dto) {
        UserDTO user = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setImage(user.getImage());
        return dto;
    }


}
