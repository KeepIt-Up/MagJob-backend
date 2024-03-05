package com.keepitup.magjobbackend.user.function;

import com.keepitup.magjobbackend.user.dto.PatchUserRequest;
import com.keepitup.magjobbackend.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;
@Component
public class UpdateUserWithRequestFunction implements BiFunction<User, PatchUserRequest, User> {

    @Override
    public User apply(User entity, PatchUserRequest request) {
        return User.builder()
                .id(entity.getId())
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .phoneNumber(request.getPhoneNumber())
                .password(entity.getPassword())
                .birthDate(request.getBirthDate())
                .build();
    }
}
