# Как Загрузить на GitHub - Пошагово

## Вариант 1: Автоматический (Проще)

### Шаг 1: Создать репозиторий на GitHub
1. Открой https://github.com
2. Войди в аккаунт
3. Нажми "+" → "New repository"
4. Заполни:
   - Name: `smartai-anticheat`
   - Public
   - НЕ добавляй README/gitignore
5. Нажми "Create repository"
6. Скопируй URL (например: `https://github.com/твой-ник/smartai-anticheat.git`)

### Шаг 2: Запустить скрипт
```cmd
upload_to_github.bat
```

Скрипт спросит:
1. Твое имя
2. Email
3. URL репозитория (вставь скопированный)

Готово! 🎉

## Вариант 2: Вручную

### 1. Настроить Git (первый раз)
```cmd
git config --global user.name "Твое Имя"
git config --global user.email "твой@email.com"
```

### 2. Инициализировать репозиторий
```cmd
git init
```

### 3. Добавить файлы
```cmd
git add .
```

### 4. Создать коммит
```cmd
git commit -m "Initial commit: SmartAI Anti-Cheat"
```

### 5. Настроить ветку
```cmd
git branch -M main
```

### 6. Добавить remote
```cmd
git remote add origin https://github.com/твой-ник/smartai-anticheat.git
```

### 7. Загрузить
```cmd
git push -u origin main
```

## Проблемы?

### "fatal: not a git repository"
Запусти: `git init`

### "failed to push"
1. Проверь URL репозитория
2. Убедись что репозиторий создан на GitHub
3. Проверь права доступа

### Нужна авторизация
GitHub может попросить логин/пароль или токен:
1. Зайди на GitHub → Settings → Developer settings
2. Personal access tokens → Generate new token
3. Выбери права: repo
4. Используй токен вместо пароля

## Что Дальше?

После загрузки на GitHub:
1. Открой HOSTING_GUIDE.md
2. Задеплой API на Railway/Render
3. Обнови config.yml с URL API
4. Запусти сервер!
