package org.big.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.big.dto.UserDto;

@Mapper
public interface UserMapper {
    
    void insertUser(UserDto userDto);
    UserDto findByUsername(String username);
}
