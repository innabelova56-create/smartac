# Полное Руководство: Деплой API на Бесплатный Хостинг

## 🎯 Цель
Разместить SmartAI API на бесплатном хостинге, чтобы Minecraft сервер мог к нему подключаться

## 📋 Подготовка (Один раз)

### 1. Создать GitHub репозиторий
1. Зайди на https://github.com
2. Нажми "New repository"
3. Название: `smartai-api`
4. Сделай Public
5. НЕ добавляй README/gitignore
6. Создай репозиторий

### 2. Загрузить код на GitHub
```cmd
cd C:\Users\Admin\Desktop\untitled
git init
git add api_server.py requirements.txt smartai_model.pkl Procfile runtime.txt railway.json render.yaml
git commit -m "SmartAI API for hosting"
git branch -M main
git remote add origin https://github.com/ВАШ_ЮЗЕР/smartai-api.git
git push -u origin main
```

## 🚀 Вариант 1: Railway (Проще всего)

### Шаг 1: Регистрация
1. Зайди на https://railway.app
2. Нажми "Login" → "Login with GitHub"
3. Разреши доступ к репозиториям

### Шаг 2: Создать проект
1. Нажми "New Project"
2. Выбери "Deploy from GitHub repo"
3. Найди и выбери `smartai-api`
4. Railway автоматически начнет деплой

### Шаг 3: Дождаться деплоя
- Статус: Building → Deploying → Active
- Займет 2-3 минуты

### Шаг 4: Получить URL
1. Открой проект
2. Перейди в Settings
3. Найди "Networking" → "Generate Domain"
4. Скопируй URL (например: `smartai-api-production.up.railway.app`)

### Шаг 5: Проверить работу
```cmd
curl https://ВАШ-URL.up.railway.app/health
```
Должен вернуть: `{"status":"ok","model_loaded":true}`

## 🌐 Вариант 2: Render

### Шаг 1: Регистрация
1. Зайди на https://render.com
2. Нажми "Get Started" → "Sign up with GitHub"

### Шаг 2: Создать Web Service
1. Нажми "New +" → "Web Service"
2. Подключи GitHub репозиторий `smartai-api`
3. Настройки:
   - Name: `smartai-api`
   - Environment: `Python 3`
   - Build Command: `pip install -r requirements.txt`
   - Start Command: `gunicorn api_server:app`
   - Plan: `Free`

### Шаг 3: Deploy
1. Нажми "Create Web Service"
2. Дождись деплоя (3-5 минут)
3. Скопируй URL (например: `smartai-api.onrender.com`)

## ⚙️ Настройка Плагина

### Обновить config.yml
```yaml
detection:
  enabled: true
  endpoint: "https://ВАШ-URL"  # Вставь свой URL
  sample-size: 40
  sample-interval: 10
```

### Перезапустить сервер
```cmd
stop
start
```

## 🧪 Тестирование

### 1. Проверить API
```cmd
curl https://ВАШ-URL/health
```

### 2. Проверить плагин
1. Зайди на сервер
2. Посмотри логи: `logs/latest.log`
3. Должно быть: `[AI] Подключено к API: https://ВАШ-URL`

### 3. Проверить детекцию
1. Побей кого-то 10 раз
2. Используй `/smartai suspects`
3. Должна показаться вероятность

## ⚠️ Важные Моменты

### Railway
- ✅ $5 кредитов в месяц (хватит на ~500 часов)
- ⚠️ Спит после неактивности
- 💡 Первый запрос может быть медленным (холодный старт)

### Render
- ✅ Полностью бесплатный
- ⚠️ Спит после 15 минут неактивности
- ⚠️ Холодный старт 30-60 секунд
- 💡 Ограничение: 750 часов в месяц

## 🔄 Обновление Модели

### Если переобучил модель:
```cmd
git add smartai_model.pkl
git commit -m "Update model"
git push
```

Railway/Render автоматически передеплоят!

## 🆘 Проблемы

### API не отвечает
1. Проверь логи на Railway/Render
2. Убедись что модель загружена
3. Проверь URL в config.yml

### Плагин не подключается
1. Проверь `detection.enabled: true`
2. Проверь правильность URL
3. Перезапусти сервер

### Холодный старт
- Это нормально для бесплатных хостингов
- Первый запрос может занять 30-60 секунд
- Последующие запросы будут быстрыми
