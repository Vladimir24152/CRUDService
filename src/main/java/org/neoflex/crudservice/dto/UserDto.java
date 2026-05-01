package org.neoflex.crudservice.dto;

import java.time.LocalDate;

public record UserDto(Long userId,String lastName,String firstName,String login,LocalDate birthDate) {
}
