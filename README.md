# RestApiAwsS3

***RestApiAwsS3*** - это RESTful Service.

**Cтек технологий проекта:** _Java, PostgreSQL, Spring (IoC, Data, Security), AWS SDK S3, Docker, JUnit, Mockito, Gradle_.

### Описание:
**RestApiAwsS3** - это REST API, которое взаимодействует с файловым хранилищем AWS S3 и 
предоставляет возможность получать доступ к файлам и истории загрузок. Взаимодействие с 
S3 реализовано с помощью AWS SDK. Аутентификация - JWT, валидация данных - Spring, 
Hibernate.   
   
**Уровни доступа:**   
>*- ADMIN - полный доступ к приложению;*  
*- MODERATOR - добавление и удаление файлов;*   
*- USER - только чтение всех файлов, кроме USER;*

Описание всех эндпоинтов доступно по ссылке: http://localhost:8080/api/v1/api-docs