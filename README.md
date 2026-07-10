```markdown
# Notification Platform

Микросервисная платформа для управления контактами и рассылки уведомлений.
Реализована в рамках производственной практики.

## Архитектура

Проект состоит из 5 микросервисов:

| Сервис | Порт | Назначение | Статус |
|--------|------|------------|--------|
| API Gateway | 8080 | Единая точка входа, маршрутизация, проверка JWT | Фаза 2 |
| User Service | 8081 | Аутентификация, управление контактами, CSV-загрузка | ✅ Готов |
| Template Service | 8082 | CRUD шаблонов уведомлений | Фаза 2 |
| Dispatch Service | 8083 | Оркестрация рассылок, Kafka Producer | Фаза 2 |
| Delivery Worker | 8084 | Отправка уведомлений (Email/SMS/Telegram), Kafka Consumer | Фаза 2 |

## Технологический стек

- **Java 21**, **Spring Boot 4.1.0**
- **Spring Security** — JWT-аутентификация (access + refresh токены, version-based revocation)
- **Spring Data JPA** — работа с PostgreSQL
- **Flyway** — миграции БД
- **PostgreSQL 15** — хранение данных
- **Apache Kafka** — асинхронное взаимодействие (подготовлено)
- **Docker Compose** — инфраструктура
- **Apache Commons CSV** — парсинг CSV-файлов
- **JJWT 0.12.6** — генерация и валидация JWT

## Инфраструктура

```bash
docker compose up -d --build
```

| Контейнер | Порт | Назначение |
|-----------|------|------------|
| user-service | 8081 | User Service |
| postgres-users | 5432 | БД пользователей и контактов |
| postgres-templates | 5433 | БД шаблонов |
| postgres-dispatch | 5434 | БД задач рассылки |
| postgres-delivery | 5435 | БД логов доставки |
| zookeeper | 2181 | Координатор Kafka |
| kafka | 9092 | Брокер сообщений |
| maildev | 1025, 1080 | Тестовый SMTP-сервер |
| postfix | 2525 | Почтовый сервер для продакшена |

## User Service — API

### Аутентификация

| Метод | URL | Заголовки | Тело запроса | Ответ |
|-------|-----|-----------|-------------|-------|
| POST | /auth/register | Content-Type: application/json | `{ "email": "...", "password": "...", "fullName": "..." }` | `{ "accessToken": "...", "refreshToken": "...", "email": "...", "fullName": "..." }` |
| POST | /auth/login | Content-Type: application/json | `{ "email": "...", "password": "..." }` | `{ "accessToken": "...", "refreshToken": "...", "email": "...", "fullName": "..." }` |
| POST | /auth/refresh | Authorization: Bearer `<refresh-token>` | — | `{ "accessToken": "...", "refreshToken": "...", "email": "...", "fullName": "..." }` |
| POST | /auth/change-password | Authorization: Bearer `<access-token>` | `{ "oldPassword": "...", "newPassword": "..." }` | `{ "accessToken": "...", "refreshToken": "...", "email": "...", "fullName": "..." }` |
| PUT | /auth/profile | Authorization: Bearer `<access-token>` | `{ "fullName": "..." }` | 204 No Content |
| GET | /auth/validate | Authorization: Bearer `<access-token>` | — | 200 OK |

### Контакты

| Метод | URL | Заголовки | Тело запроса | Ответ |
|-------|-----|-----------|-------------|-------|
| GET | /api/contacts | Authorization: Bearer `<access-token>` | — | `[{ "id": "...", "ownerId": "...", "name": "...", "email": "...", "phone": "...", "telegramId": "..." }]` |
| POST | /api/contacts | Authorization: Bearer `<access-token>`, Content-Type: application/json | `{ "name": "...", "email": "...", "phone": "...", "telegramId": "..." }` | `{ "id": "...", "ownerId": "...", "name": "...", "email": "...", "phone": "...", "telegramId": "..." }` |
| GET | /api/contacts/{id} | Authorization: Bearer `<access-token>` | — | `{ "id": "...", "ownerId": "...", "name": "...", "email": "...", "phone": "...", "telegramId": "..." }` |
| PATCH | /api/contacts/{id} | Authorization: Bearer `<access-token>`, Content-Type: application/json | `{ "name": "..." }` (любое подмножество полей) | `{ "id": "...", "ownerId": "...", "name": "...", "email": "...", "phone": "...", "telegramId": "..." }` |
| DELETE | /api/contacts/{id} | Authorization: Bearer `<access-token>` | — | 204 No Content |
| POST | /api/contacts/upload-csv | Authorization: Bearer `<access-token>`, Content-Type: multipart/form-data | `file` — CSV-файл | `{ "taskId": "...", "status": "COMPLETED", "created": N, "errors": N, "errorDetails": {...} }` |
| GET | /api/contacts/search?email= | Authorization: Bearer `<access-token>` | — | `{ "id": "...", "ownerId": "...", "name": "...", "email": "...", "phone": "...", "telegramId": "..." }` |

### Формат CSV для загрузки

```csv
name,email,phone,telegram_id
Иван Петров,ivan@mail.com,+79001234567,123456789
Мария Сидорова,maria@mail.com,,
```

Обязательные колонки: `name`, `email`. Опциональные: `phone`, `telegram_id`.

## Безопасность

- **Access-токен**: JWT, 15 минут, подпись HMAC-SHA256
- **Refresh-токен**: JWT, 7 дней, содержит `version` для отзыва
- При смене пароля `version` увеличивается — все refresh-токены инвалидируются
- Пароли хранятся в виде BCrypt-хешей
- Публичные эндпоинты: `/auth/register`, `/auth/login`
- Все эндпоинты `/api/**` требуют access-токен

## Запуск

```bash
# 1. Клонировать репозиторий
git clone <repo-url>
cd notification-platform

# 2. Создать .env файл
cp .env.example .env

# 3. Запустить
docker compose up -d --build

# 4. Тестировать
# User Service доступен на http://localhost:8081
```

## Переменные окружения (.env)

```
POSTGRES_PASSWORD=secret
JWT_SECRET=my-256-bit-secret-key-for-development-only
```

## Структура проекта

```
notification-platform/
├── docker-compose.yml
├── .env
├── .env.example
├── api-gateway/
├── user-service/
│   ├── Dockerfile
│   └── src/
├── template-service/
├── dispatch-service/
└── delivery-worker/
```