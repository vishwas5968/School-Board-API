package com.school.sba.service;

import com.school.sba.dto.UserRequest;

public interface UserService {

	Object register(UserRequest userRequest);

	Object deleteUser(int userId);

}
