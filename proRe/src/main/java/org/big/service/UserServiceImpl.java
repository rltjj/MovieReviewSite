package org.big.service;

import org.big.dto.UserDto;
import org.big.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void registerUser(UserDto userDto) {
        userMapper.insertUser(userDto);
    }

    @Override
    public UserDto findUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
