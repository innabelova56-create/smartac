# 🚀 SmartAI - Начни Здесь!

## ✅ Текущий Статус
Git настроен и готов к загрузке на GitHub!

## 📋 Быстрый Старт

### Шаг 1: Загрузить на GitHub
1. Открой https://github.com
2. Создай новый репозиторий `smartai-anticheat` (Public)
3. Запусти: `push_to_github.bat`
4. Готово!

### Шаг 2: Деплой API
1. Зайди на https://railway.app
2. Login with GitHub
3. New Project → Deploy from GitHub repo
4. Выбери `smartai-anticheat`
5. Settings → Generate Domain
6. Скопируй URL

### Шаг 3: Настроить Плагин
Отредактируй `config.yml`:
```yaml
detection:
  endpoint: "https://твой-url.up.railway.app"
```

### Шаг 4: Запустить
```cmd
gradlew build
copy build\libs\untitled-1.0.jar plugins\SmartAI.jar
```
Запусти сервер!

## 📚 Документация
- `GITHUB_READY.md` - Загрузка на GitHub
- `HOSTING_GUIDE.md` - Деплой API
- `QUICK_START.md` - Быстрый старт
- `USAGE.md` - Использование плагина

## 🆘 Помощь
Все готово! Просто следуй шагам выше.
