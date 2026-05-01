package org.neoflex.crudservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neoflex.crudservice.dto.UserDto;
import org.neoflex.crudservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> get(@PathVariable Long userId){
        log.info("Входящий GET запрос на получение пользователя с ID: {}", userId);
        UserDto result = userService.get(userId);
        log.debug("GET запрос обработан успешно для userId={}", userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping()
    public ResponseEntity<UserDto> create(@RequestBody UserDto request){
        log.info("Входящий POST запрос на создание пользователя с логином: {}", request.login());
        log.debug("Тело запроса: lastName={}, firstName={}, birthDate={}",
                request.lastName(), request.firstName(), request.birthDate());

        UserDto result = userService.create(request);
        log.info("Создан пользователь с ID: {}", result.userId());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserDto> delete(@PathVariable Long userId){
        log.warn("Входящий DELETE запрос на удаление пользователя с ID: {}", userId);
        UserDto result = userService.delete(userId);
        log.info("Пользователь с ID {} удален", userId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserDto> updateLogin(@RequestParam Long userId, @RequestParam String login){
        log.info("Входящий PATCH запрос на обновление логина пользователя ID: {} на '{}'", userId, login);
        UserDto result = userService.updateLogin(userId, login);
        log.info("Логин пользователя ID {} обновлен", userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/heavy/{iterations}")
    public ResponseEntity<String> highLoadMethod(@PathVariable Integer iterations){
        log.warn("Входящий запрос на выполнение нагрузочного метода с {} итерациями", iterations);
        String result = userService.highLoadMethod(iterations);
        log.info("Нагрузочный метод завершен для параметра iterations={}", iterations);
        return ResponseEntity.ok(result);
    }
}