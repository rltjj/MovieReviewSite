package org.big.service;

import org.big.dto.UserDto;
import org.big.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder; //비밀번호

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void registerUser(UserDto userDto) {
    	userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userMapper.insertUser(userDto);
    }

    @Override
    public UserDto findUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

	@Override
	public boolean checkPassword(String rawPassword, String encodedPassword) {
		 return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	@Override
	public boolean updatePassword(String username, String oldPassword, String newPassword) {
        UserDto user = userMapper.findByUsername(username);
        if (user != null && passwordEncoder.matches(oldPassword, user.getPassword())) {
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            userMapper.updatePassword(username, encodedNewPassword); // 비밀번호 업데이트
            return true;
        }
        return false; // 비밀번호가 일치하지 않으면 false 반환
    }

	@Override
	public void deleteAccount(String username) {
        userMapper.deleteUser(username); // 사용자 삭제
    }

	@Override
	public Long findUserIdByUsername(String username) {
		// TODO Auto-generated method stub
		return userMapper.findUserIdByUsername(username);
	}

	@Override
	public boolean isUsernameTaken(String username) {
		// TODO Auto-generated method stub
		return userMapper.findByUsername(username) != null;
	}

	@Override
	public boolean isEmailTaken(String email) {
		// TODO Auto-generated method stub
		return userMapper.findByEmail(email) != null;
	}
}
