package org.neoflex.crudservice.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neoflex.crudservice.dto.UserDto;
import org.neoflex.crudservice.mapper.UserMapper;
import org.neoflex.crudservice.model.User;
import org.neoflex.crudservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDto get(@NonNull Long userId) {
        log.info("Поиск пользователя с ID: {}", userId);
        log.debug("Выполнение запроса к БД для userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден в системе", userId);
                    return new IllegalArgumentException(String.format("Пользователь с Id %s отсутствует", userId));
                });

        log.debug("Пользователь найден: {}", user.getLogin());
        log.info("Успешное получение данных пользователя с ID: {}", userId);
        return userMapper.toUserDTO(user);
    }

    @Transactional
    public UserDto create(@NonNull UserDto request) {
        log.info("Создание нового пользователя с логином: {}", request.login());
        log.debug("Детали создания: фамилия={}, имя={}, дата рождения={}",
                request.lastName(), request.firstName(), request.birthDate());

        if (request.login() == null || request.login().isBlank()) {
            log.warn("Попытка создания пользователя с пустым логином");
            throw new IllegalArgumentException("Логин не может быть пустым");
        }

        User user = userMapper.toUser(request);
        log.trace("Маппинг DTO в Entity выполнен: userId={}", user.getUserId());

        User savedUser = userRepository.save(user);
        log.info("Пользователь успешно создан с ID: {}", savedUser.getUserId());
        log.debug("Сохраненный пользователь: логин={}, ID={}", savedUser.getLogin(), savedUser.getUserId());

        return userMapper.toUserDTO(savedUser);
    }

    @Transactional
    public UserDto delete(@NonNull Long userId) {
        log.warn("Запрос на удаление пользователя с ID: {}", userId);
        log.debug("Проверка существования пользователя перед удалением");

        User deletedUser = userRepository.removeUserByUserId(userId);

        if (deletedUser == null) {
            log.error("Попытка удаления несуществующего пользователя с ID: {}", userId);
            throw new IllegalArgumentException(String.format("Пользователь с Id %s отсутствует", userId));
        }

        log.info("Пользователь с ID {} успешно удален из системы", userId);
        log.debug("Удаленный пользователь: логин={}", deletedUser.getLogin());

        return userMapper.toUserDTO(deletedUser);
    }

    @Transactional
    public UserDto updateLogin(@NonNull Long userId, @NonNull String login) {
        log.info("Обновление логина для пользователя ID: {}", userId);
        log.debug("Новый логин: '{}'", login);

        if (login == null || login.isBlank()) {
            log.warn("Попытка установить пустой логин для пользователя ID: {}", userId);
            throw new IllegalArgumentException("Логин не может быть пустым");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID {} не найден для обновления логина", userId);
                    return new IllegalArgumentException(String.format("Пользователь с Id %s отсутствует", userId));
                });

        String oldLogin = user.getLogin();
        log.trace("Текущий логин пользователя: '{}'", oldLogin);

        user.setLogin(login);
        User updatedUser = userRepository.save(user);

        log.info("Логин пользователя ID {} обновлен с '{}' на '{}'", userId, oldLogin, login);
        log.debug("Обновление логина завершено успешно");

        return userMapper.toUserDTO(updatedUser);
    }

    @Transactional
    public String highLoadMethod(@NonNull Integer iterations) {
        log.warn("Запуск нагрузочного метода с параметром iterations={}", iterations);
        log.info("Начало выполнения высоконагруженной операции");

        int actualIterations = Math.abs(Math.min(iterations, 1_000_000));
        log.debug("Фактическое количество итераций после валидации: {}", actualIterations);

        StringBuilder result = new StringBuilder();
        long startTime = System.currentTimeMillis();

        log.info("Старт цикла нагрузочного тестирования");

        for (int i = 0; i < actualIterations; i++) {
            if (i % 1000 == 0 && i > 0) {
                log.debug("Прогресс нагрузочного метода: выполнено {} из {} итераций", i, actualIterations);
            }

            long sum = 0;
            for (int j = 0; j < 10000; j++) {
                sum += j * i;
                sum = sum % 1000000;
            }

            double calc = Math.sin(i) * Math.cos(i) * Math.tan(i);
            result.append((int)(calc * 1000));

            int[] arr = {i * 3, i * 5, i * 7, i * 9, i * 11};
            java.util.Arrays.sort(arr);

            if (i == actualIterations - 1) {
                log.trace("Последняя итерация {} завершена, результат вычислений: {}", i, calc);
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("Нагрузочный метод завершен за {} мс", (endTime - startTime));
        log.warn("Результат нагрузочного теста: длина строки = {}", result.length());
        log.debug("Нагрузочный метод выполнен с параметром iterations={}", iterations);

        return "Загрузка завершена: " + result.length();
    }
}