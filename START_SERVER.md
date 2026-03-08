# Как запустить сервер с SmartAI

## Шаг 1: Установка плагина

1. Скопируйте jar файл в папку plugins вашего сервера:
```
build/libs/SmartAI-1.0.jar  →  [путь_к_серверу]/plugins/
```

2. Если сервер запущен, перезапустите его или используйте:
```
/reload confirm
```

---

## Шаг 2: Запуск API сервера

API сервер должен быть запущен ДО запуска Minecraft сервера (или одновременно).

### Вариант 1: Запуск в отдельном терминале
```bash
python api_server.py
```

Вы увидите:
```
==================================================
Starting SmartAI API Server...
==================================================
Endpoints:
  POST /predict - Предсказание
  GET  /health  - Проверка статуса
  POST /reload  - Перезагрузка модели
==================================================
 * Running on http://0.0.0.0:5000
```

### Вариант 2: Запуск в фоне (Windows)
```bash
start python api_server.py
```

---

## Шаг 3: Запуск Minecraft сервера

Запустите ваш Paper 1.16.5 сервер как обычно:

```bash
java -jar paper-1.16.5.jar
```

Или используйте ваш start.bat/start.sh файл.

---

## Проверка работы

### 1. Проверьте логи сервера

Вы должны увидеть:
```
[SmartAI] [Голограммы] Включены!
[SmartAI] [AI] Подключено к API: http://localhost:5000
[SmartAI] [AI] AI проверка включена!
[SmartAI] [AI] Sample size: 40 ticks
[SmartAI] SmartAI включен! Используйте /smartai для сбора данных.
[SmartAI] AI детекция активна! Админы получат алерты при подозрительной активности.
```

### 2. Проверьте команды в игре

Зайдите на сервер и введите:
```
/smartai
```

Вы должны увидеть список всех команд.

### 3. Проверьте API

В браузере откройте:
```
http://localhost:5000/health
```

Должно показать:
```json
{
  "status": "ok",
  "model_loaded": true,
  "model_load_time": "2026-03-08T..."
}
```

---

## Если что-то не работает

### Проблема: "Не удалось подключиться к API"

**Решение:**
1. Убедитесь что API сервер запущен:
```bash
python api_server.py
```

2. Проверьте что порт 5000 свободен:
```bash
netstat -ano | findstr :5000
```

3. Проверьте config.yml:
```yaml
detection:
  enabled: true
  endpoint: "http://localhost:5000"
```

### Проблема: "Model not loaded"

**Решение:**
1. Убедитесь что файл `smartai_model.pkl` существует
2. Переобучите модель:
```bash
python train_model.py
```

3. Перезапустите API сервер

### Проблема: Плагин не загружается

**Решение:**
1. Проверьте версию сервера (должна быть Paper 1.16.5)
2. Проверьте логи на ошибки
3. Убедитесь что PacketEvents установлен (он встроен в плагин)

---

## Быстрый старт (все в одном)

### Windows:

1. **Терминал 1** (API):
```bash
cd C:\Users\Admin\Desktop\untitled
python api_server.py
```

2. **Терминал 2** (Сервер):
```bash
cd [путь_к_серверу]
java -jar paper-1.16.5.jar
```

### Или создайте start_all.bat:

```batch
@echo off
echo Starting SmartAI API Server...
start "SmartAI API" python api_server.py

timeout /t 3

echo Starting Minecraft Server...
cd [путь_к_серверу]
java -jar paper-1.16.5.jar

pause
```

---

## Тестирование

После запуска:

1. Зайдите на сервер
2. Дайте себе права админа:
```
/op [ваш_ник]
```

3. Проверьте команды:
```
/smartai datastatus
/smartai prob [ваш_ник]
```

4. Начните сбор данных:
```
/smartai start [ваш_ник] legit
```

5. Побейте моба/игрока несколько раз

6. Остановите сбор:
```
/smartai stop [ваш_ник]
```

7. Проверьте что файл создан:
```
plugins/SmartAI/datasets/LEGIT_[ваш_ник]_[дата].csv
```

---

## Порядок запуска (важно!)

1. ✅ Сначала API сервер (`python api_server.py`)
2. ✅ Потом Minecraft сервер
3. ✅ Зайти в игру и протестировать

Если запустить в обратном порядке - плагин не подключится к API и будет работать только сбор данных (без AI проверки).

---

## Остановка

1. Остановите Minecraft сервер: `/stop` или Ctrl+C
2. Остановите API сервер: Ctrl+C в терминале

---

## Автозапуск (опционально)

Если хотите чтобы API запускался автоматически с сервером, добавьте в start.bat:

```batch
@echo off
start "SmartAI API" python C:\Users\Admin\Desktop\untitled\api_server.py
timeout /t 3
java -jar paper-1.16.5.jar
pause
```
