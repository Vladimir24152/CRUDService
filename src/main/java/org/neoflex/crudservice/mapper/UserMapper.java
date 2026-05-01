package org.neoflex.crudservice.mapper;

import org.mapstruct.Mapper;
import org.neoflex.crudservice.dto.UserDto;
import org.neoflex.crudservice.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDTO(User user);

    User toUser(UserDto userDto);
}
