package org.big.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.big.dto.UserDto;

@Mapper
public interface UserMapper {
    
    void insertUser(UserDto userDto);
    UserDto findByUsername(String username);
    UserDto findByEmail(String email);
	void updatePassword(String username, String encodedNewPassword);
	void deleteUser(String username);
	Long findUserIdByUsername(String username);
}
