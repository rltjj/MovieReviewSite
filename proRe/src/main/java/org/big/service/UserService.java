package org.big.service;

import org.big.dto.UserDto;

public interface UserService {
    void registerUser(UserDto userDto);
    UserDto findUserByUsername(String username);
    boolean checkPassword(String rawPassword, String encodedPassword);
	boolean updatePassword(String username, String oldPassword, String newPassword);
	void deleteAccount(String username);
	Long findUserIdByUsername(String username);
	boolean isUsernameTaken(String username);
	boolean isEmailTaken(String email);
}
