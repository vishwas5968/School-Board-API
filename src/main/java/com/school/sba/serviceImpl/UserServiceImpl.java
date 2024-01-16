package com.school.sba.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.school.sba.dto.UserRequest;
import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.ConstraintViolationException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.UserRepo;
import com.school.sba.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepo userRepo;

	int count;

	@Override
	public Object register(UserRequest userRequest) {
		User user = mapToUser(userRequest);
		if (userRequest.getUserRole() == UserRole.ADMIN) {
			count += 1;
		}
		if (count == 1 || userRequest.getUserRole() != UserRole.ADMIN) {
			try {
				user = userRepo.save(user);
			} catch (Exception e) {
				throw new ConstraintViolationException("Duplicate Entry made", HttpStatus.IM_USED,
						"No duplicate entries are allowed");
			}
		} else {
			throw new ConstraintViolationException("There is already an admin", HttpStatus.IM_USED,
					"More than 1 admin is not allowed");
		}

		return "User Saved Successfully";
	}

	public User mapToUser(UserRequest userRequest) {
		return User.builder().username(userRequest.getUsername()).password(userRequest.getPassword())
				.firstName(userRequest.getFirstName()).lastName(userRequest.getLastName())
				.contactNo(userRequest.getContactNo()).email(userRequest.getEmail()).userRole(userRequest.getUserRole())
				.build();
	}

	@Override
	public Object deleteUser(int userId) {
		User user = new User();
		try {
			user = userRepo.findById(userId).get();
		} catch (Exception e) {
			throw new UserNotFoundException("User with given ID is not registered in the database",
					HttpStatus.NOT_FOUND, "No such user in database");
		}
		userRepo.delete(user);
		return "User Deleted Successfully";
	}

}
