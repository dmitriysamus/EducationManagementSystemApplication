Education Management System Application
=============================

[![Actions Status](https://img.shields.io/badge/JAVA-1.8-brightgreen)](https://img.shields.io/badge/JAVA-1.8-brightgreen)
[![Actions Status](https://img.shields.io/badge/Apache%20Maven-3.6.0-blue)](https://img.shields.io/badge/Apache%20Maven-3.6.0-blue)
[![Actions Status](https://img.shields.io/badge/Docker-20.10.7-orange)](https://img.shields.io/badge/Docker-20.10.7-orange)
[![Actions Status](https://img.shields.io/badge/PostgreSQL-11.1-blue)](https://img.shields.io/badge/PostgreSQL-11.1-blue)


Education Management System Application это приложение образовательная платформа - backend
часть.

+ Для дополнительной информации по frontend части приложение,
  посетите [Education-Management-System-Application GUI](https://github.com/Habatoo/EducationManagementSystemGUI).

INSTALLATION
------------

Проект не требует установки. Необхдимый софт для старта приложения указан в разделе REQUIREMENTS.
Структура проекта:

      src/main/java/educationManagementSystem/config       конфигурирование url доступов проекта
      src/main/java/educationManagementSystem/controllers  контроллеры url backend части проекта
      src/main/java/educationManagementSystem/model        описание моделей сущностей проекта
      src/main/java/educationManagementSystem/payload      классы реализации сущностей для загрузки выгрузки данных
      src/main/java/educationManagementSystem/repository   методы для работы с БД по ORM
      src/main/java/educationManagementSystem/security     конфигурирование security доступов к url проекта по jwt
      src/test                                             тесты проекта
      pom.xml                                              настройка зависимостей проекта
      README                                               данный файл


REQUIREMENTS
------------

+ Java - 1.8 или старше.
+ Postgresql - 11 или старше.
+ Docker Engine Community - 20 или старше.
+ Apache Maven - 3.6.0 или старше.

QUICK START
-----------

Запускаем БД Postgresql. В командной строке запускаем следующую команду:

        $ sudo docker run --rm --name app -e POSTGRES_PASSWORD=1234567890 -e POSTGRES_USER=appuser -e POSTGRES_DB=app -d -p 5432:5432 -v app:/var/lib/postgresl/data  postgres
               (Linux)
        docker run --rm --name app -e POSTGRES_PASSWORD=1234567890 -e POSTGRES_USER=appuser -e POSTGRES_DB=app -d -p 5432:5432 -v app:/var/lib/postgresl/data  postgres
                  (Windows)

Далее стартуем EducationManagementSystemApplication.
Backend запущен.

AUTHORS
------------

+ Дмитрий Самусь dmitriysamus
+ Лаптенков Станислав habatoo