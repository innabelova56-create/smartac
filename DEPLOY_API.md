# Деплой API на Бесплатный Хостинг

## Лучшие Бесплатные Варианты 2026

### 1. 🚀 Railway (Рекомендуется)
- ✅ $5 бесплатных кредитов в месяц
- ✅ Автоматический деплой из GitHub
- ✅ Простая настройка
- ✅ Поддержка Python/Flask
- ⚠️ Спит после неактивности

### 2. 🌐 Render
- ✅ Бесплатный tier
- ✅ Автодеплой из GitHub
- ✅ Простой интерфейс
- ⚠️ Холодный старт (спит после 15 мин)

### 3. ✈️ Fly.io
- ✅ Бесплатный tier
- ✅ Глобальное распределение
- ✅ Низкая задержка
- ⚠️ Требует кредитную карту

## Быстрый Старт: Railway

### Шаг 1: Подготовка файлов
Файлы уже созданы в проекте (см. ниже)

### Шаг 2: Загрузка на GitHub
```cmd
git init
git add .
git commit -m "SmartAI API"
git remote add origin https://github.com/ВАШ_ЮЗЕР/smartai-api.git
git push -u origin main
```

### Шаг 3: Деплой на Railway
1. Зайди на https://railway.app
2. Войди через GitHub
3. Нажми "New Project" → "Deploy from GitHub repo"
4. Выбери репозиторий smartai-api
5. Railway автоматически определит Python и задеплоит

### Шаг 4: Получить URL
1. Открой проект в Railway
2. Перейди в Settings → Networking
3. Нажми "Generate Domain"
4. Скопируй URL (например: smartai-api.up.railway.app)

### Шаг 5: Обновить конфиг плагина
В `config.yml`:
```yaml
detection:
  endpoint: "https://smartai-api.up.railway.app"
```
