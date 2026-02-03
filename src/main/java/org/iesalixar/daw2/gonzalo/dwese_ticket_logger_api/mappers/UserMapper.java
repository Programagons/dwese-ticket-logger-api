package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.mappers;


import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.UserDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.User;

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
        dto.setPassword(user.getPassword());
        dto.setEnabled(user.isEnabled());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setImage(user.getImage());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setLastModifiedDate(user.getLastModifiedDate());
        dto.setLastPasswordChangeDate(user.getLastPasswordChangeDate());
        return dto;
    }


    /**
     * Convierte un 'AutorDTO' a una entidad 'Autor'.
     *
     * @param user DTO de autor.
     * @return Entidad Autor
     */
    public User toEntity(UserDTO user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEnabled(user.isEnabled());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setImage(user.getImage());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setLastModifiedDate(user.getLastModifiedDate());
        dto.setLastPasswordChangeDate(user.getLastPasswordChangeDate());
        return dto;
    }


    /**
     * Convierte un 'RegionCreateDTO' a una entidad 'Autor' (para creación).
     * @param createDTO DTO para crear regiones
     * @return Entidad Autor
     */

    public Autor toEntity(RegionCreateDTO createDTO) {
        Autor autor = new Autor();
        autor.setCode(createDTO.getCode());
        autor.setName(createDTO.getName());
        return autor;
    }

}
