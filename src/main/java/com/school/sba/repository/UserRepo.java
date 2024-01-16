package com.school.sba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.User;

public interface UserRepo extends JpaRepository<User, Integer>{

}
