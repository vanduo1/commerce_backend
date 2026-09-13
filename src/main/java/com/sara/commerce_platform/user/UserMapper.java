package com.sara.commerce_platform.user;

import com.sara.commerce_platform.auth.dto.RegisterRequest;
import com.sara.commerce_platform.user.dto.UserResponse;
import com.sara.commerce_platform.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest request);
    UserResponse toResponse(User user);
}
