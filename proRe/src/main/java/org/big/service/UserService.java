package org.big.service;

import org.big.dto.UserDto;

public interface UserService {
    void registerUser(UserDto userDto);
    UserDto findUserByUsername(String username);
}
