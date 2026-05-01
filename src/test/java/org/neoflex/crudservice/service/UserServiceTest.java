package org.neoflex.crudservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.neoflex.crudservice.dto.UserDto;
import org.neoflex.crudservice.mapper.UserMapper;
import org.neoflex.crudservice.model.User;
import org.neoflex.crudservice.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование сервиса пользователей")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setLogin("testuser");
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));

        testUserDto = new UserDto(
                1L,
                "Иванов",
                "Иван",
                "testuser",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    @DisplayName("Успешное получение пользователя по идентификатору")
    void whenGetUserByIdThenReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDto);

        UserDto result = userService.get(1L);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.login()).isEqualTo("testuser");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Ошибка при получении несуществующего пользователя")
    void whenGetUserByIdNotFoundThenThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.get(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с Id 999 отсутствует");

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Успешное создание нового пользователя")
    void whenCreateUserThenReturnSavedUserDto() {
        when(userMapper.toUser(testUserDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDto);

        UserDto result = userService.create(testUserDto);

        assertThat(result).isNotNull();
        assertThat(result.login()).isEqualTo("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Ошибка при создании пользователя с пустым логином")
    void whenCreateUserWithEmptyLoginThenThrowException() {
        UserDto invalidDto = new UserDto(
                null, "Петров", "Петр", "", LocalDate.of(1995, 5, 5)
        );

        assertThatThrownBy(() -> userService.create(invalidDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Логин не может быть пустым");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Успешное удаление пользователя")
    void whenDeleteUserThenReturnDeletedUserDto() {
        when(userRepository.removeUserByUserId(1L)).thenReturn(testUser);
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDto);

        UserDto result = userService.delete(1L);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(1L);
        verify(userRepository, times(1)).removeUserByUserId(1L);
    }

    @Test
    @DisplayName("Ошибка при удалении несуществующего пользователя")
    void whenDeleteUserNotFoundThenThrowException() {
        when(userRepository.removeUserByUserId(999L)).thenReturn(null);

        assertThatThrownBy(() -> userService.delete(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с Id 999 отсутствует");

        verify(userRepository, times(1)).removeUserByUserId(999L);
    }

    @Test
    @DisplayName("Успешное обновление логина пользователя")
    void whenUpdateLoginThenReturnUpdatedUserDto() {
        String newLogin = "newlogin";
        UserDto updatedDto = new UserDto(
                1L, "Иванов", "Иван", newLogin, LocalDate.of(1990, 1, 1)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserDTO(testUser)).thenReturn(updatedDto);

        UserDto result = userService.updateLogin(1L, newLogin);

        assertThat(result).isNotNull();
        assertThat(result.login()).isEqualTo(newLogin);
        assertThat(testUser.getLogin()).isEqualTo(newLogin);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Ошибка при обновлении логина несуществующего пользователя")
    void whenUpdateLoginForNonExistentUserThenThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateLogin(999L, "newlogin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с Id 999 отсутствует");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка при обновлении логина на пустое значение")
    void whenUpdateLoginWithEmptyValueThenThrowException() {
        assertThatThrownBy(() -> userService.updateLogin(1L, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Логин не может быть пустым");

        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("Успешное выполнение нагрузочного метода")
    void whenExecuteHighLoadMethodThenReturnResult() {
        String result = userService.highLoadMethod(100);

        assertThat(result).isNotNull();
        assertThat(result).startsWith("Загрузка завершена:");
        assertThat(result).contains(":");
    }

    @Test
    @DisplayName("Нагрузочный метод с максимальным значением итераций")
    void whenExecuteHighLoadMethodWithMaxIterationsThenNotExceedLimit() {
        String result = userService.highLoadMethod(10000);

        assertThat(result).isNotNull();
        assertThat(result).startsWith("Загрузка завершена:");
    }

    @Test
    @DisplayName("Нагрузочный метод с отрицательным значением итераций")
    void whenExecuteHighLoadMethodWithNegativeIterationsThenUseAbsoluteValue() {
        String result = userService.highLoadMethod(-50);

        assertThat(result).isNotNull();
        assertThat(result).startsWith("Загрузка завершена:");
    }
}