markdown
# CRUD Service with Monitoring

REST сервис для управления пользователями.

## Технологии

- **Java 17**
- **Spring Boot 3.1.5**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok + MapStruct**
- **Spring Boot Actuator + Prometheus**
- **Grafana** для визуализации
- **Docker + Docker Compose**

## Функциональность

- CRUD операции для пользователей
- Нагрузочный метод для тестирования производительности
- Логирование всех уровней (TRACE, DEBUG, INFO, WARN, ERROR)
- Метрики через Prometheus
- Дампы потоков и памяти для анализа

## API Endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/v1/user/{userId}` | Получить пользователя по ID |
| POST | `/api/v1/user` | Создать пользователя |
| DELETE | `/api/v1/user/delete/{userId}` | Удалить пользователя |
| PATCH | `/api/v1/user/update?userId=&login=` | Обновить логин |
| PATCH | `/api/v1/user/heavy/{iterations}` | Нагрузочный метод |


## Анализ дампов потоков

### Топ 3 потока по нагрузке

| Название потока | Время жизни (elapsed ms) | Время работы (cpu ms) | Процент нагрузки |
|----------------|-------------------------|----------------------|------------------|
| http-nio-8080-exec-7 | 222820 | 33125.00 | 14.87% |
| UserService-highLoadMethod | 222750 | 3093.75 | 1.39% |
| C2 CompilerThread0 | 222820 | 3078.12 | 1.38% |

## Мониторинг с Prometheus и Grafana

### Скриншоты дашбордов

#### Стандартный из лекции
![img_1.png](img_1.png)

#### Собственный дашборд для CRUD сервиса

![img.png](img.png)

### Запросы PromQL и описание панелей

| Панель | PromQL запрос | Описание метрики |
|--------|---------------|------------------|
| **JVM Heap Memory** | `jvm_memory_used_bytes{area="heap"}` | Показывает использование heap памяти JVM в реальном времени. Метрика берется из Micrometer и отображает объем памяти, занятый объектами в куче. |
| **Requests Per Second (RPS)** | `rate(http_server_requests_seconds_count[5m])` | Отображает количество HTTP запросов в секунду за последние 5 минут. Позволяет оценить нагрузку на сервер и выявить пиковые значения. |
| **Average Request Time by Endpoint** | `sum by(uri, method) (rate(http_server_requests_seconds_sum[5m])) / sum by(uri, method) (rate(http_server_requests_seconds_count[5m]))` | Показывает среднее время выполнения запросов для каждого эндпоинта в секундах. Группировка по HTTP методу (GET, POST, PATCH) и URI позволяет выявить медленные операции. |


## Запуск проекта

### Запуск в IntelliJ IDEA

1. Открыть проект: `File → Open` → выбрать папку `CRUDService`

2. Дождаться загрузки Maven зависимостей

3. Открыть класс `CrudServiceApplication`
   (`src/main/java/org/neoflex/crudservice/CrudServiceApplication.java`)

4. Нажать зеленую стрелку рядом с методом `main()` или `Shift + F10`

5. Открыть в браузере: `http://localhost:8080`

### Запуск в Docker

1. Собрать JAR файл:
   mvn clean package

2. Запустить все сервисы:
   docker-compose up -d

3. Открыть сервисы в браузере:
   Приложение: http://localhost:8080
   Prometheus: http://localhost:9090
   Grafana: http://localhost:3000 (admin/admin)



