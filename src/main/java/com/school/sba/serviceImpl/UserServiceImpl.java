package com.school.sba.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.school.sba.entity.User;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.ConstraintViolationException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.UserRequest;
import com.school.sba.responsedto.UserResponse;
import com.school.sba.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private	UserRepo userRepo;

	public User mapToUser(UserRequest userRequest) {
		return User.builder().username(userRequest.getUsername()).password(userRequest.getPassword())
				.firstName(userRequest.getFirstName()).lastName(userRequest.getLastName())
				.contactNo(userRequest.getContactNo()).email(userRequest.getEmail()).userRole(userRequest.getUserRole())
				.build();
	}

	public UserResponse mapToUserResponse(User user) {
		return UserResponse.builder().username(user.getUsername()).firstName(user.getFirstName())
				.lastName(user.getLastName()).email(user.getEmail()).userRole(user.getUserRole()).build();
	}

	@Override
	public Object register(UserRequest userRequest) {
		User user = mapToUser(userRequest);
		user.setDeleted(false);
		boolean existsByUserRole = userRepo.existsByUserRole(UserRole.ADMIN);
		if (existsByUserRole == false || user.getUserRole()!=UserRole.ADMIN) {
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

	@Override
	public Object deleteUser(int userId) {
		User user = userRepo.findById(userId).orElseThrow(() ->  new UserNotFoundException("User with given ID is not registered in the database",
				HttpStatus.NOT_FOUND, "No such user in database"));
		user.setDeleted(true);
		userRepo.delete(user);
		return "User Deleted Successfully";
	}

	@Override
	public UserResponse findUserById(int userId) {
		User user = userRepo.findById(userId).orElseThrow(() ->  new UserNotFoundException("User with given ID is not registered in the database",
				HttpStatus.NOT_FOUND, "No such user in database"));
		return mapToUserResponse(user);
	}

}
