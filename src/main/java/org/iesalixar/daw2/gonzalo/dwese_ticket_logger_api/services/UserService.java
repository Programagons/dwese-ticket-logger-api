package org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.services;

import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.dtos.UserDTO;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.entities.User;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.mappers.UserMapper;
import org.iesalixar.daw2.gonzalo.dwese_ticket_logger_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public Long getIdByUsername(String username){
        return userRepository.getIdByUsername(username);
    }

    public UserDTO getUserById (Long id){
    Optional <User> userOpt = userRepository.findById(id);

    if (userOpt.isPresent()){
        return userMapper.toDTO(userOpt.get());
    }
    throw new RuntimeException("El usuario con identificador " + id + " no existe");
    }
}

