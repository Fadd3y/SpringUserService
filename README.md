## Для корректной работы нужно:
- В файле application.properties заполнить поля значениями для подключения к локальной базе данных:
```
        spring.datasource.url=jdbc:postgresql://localhost:5432/user_service
        spring.datasource.username=postgres
        spring.datasource.password=admin
```
## API
Для теста UserService http://localhost:8080/swagger-ui/index.html#/
### SpringUserService http://localhost:8080

- Создание пользователя          __POST /api/users__
```
{
    "name": "example",
    "email": "example@gmail.com",
    "age": 17
}
```
- Получение пользователя         __GET /api/users/{id}__

- Получение всех пользователей   __GET /api/users__     

- Обновление полей пользователя  __PUT /api/users/{id}__
```
{
    "name": "example",
    "email": "example@gmail.com",
    "age": 17
}
```
- Удаление пользователя          __DELETE /api/users/{id}__
