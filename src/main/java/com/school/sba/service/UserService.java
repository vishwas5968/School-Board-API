package com.school.sba.service;

import com.school.sba.requestdto.UserRequest;

public interface UserService {

	Object register(UserRequest userRequest);

	Object deleteUser(int userId);

	Object findUserById(int userId);

}
