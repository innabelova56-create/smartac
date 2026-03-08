# Как перезапустить API сервер

## Проблема
API сервер запущен, но модель не загружена:
```json
{"model_loaded": false}
```

## Решение

### Шаг 1: Остановите старый API сервер

Найдите окно терминала где запущен `api_server.py` и нажмите:
```
Ctrl + C
```

Или убейте процесс:
```bash
taskkill /PID 10036 /F
```

### Шаг 2: Переобучите модель (если нужно)

Если вы обновили код обучения, переобучите модель:
```bash
python train_model.py
```

Или:
```bash
retrain.bat
```

Вы должны увидеть:
```
=== SmartAI Model Training ===

Найдено X файлов
Загружено: CHEAT_...
Загружено: LEGIT_...

Всего строк: XXXX
Легитных: XXX
Читеров: XXX

Обучение модели...

Точность на train: 0.95+
Точность на test: 0.90+

Модель сохранена в smartai_model.pkl
```

### Шаг 3: Запустите API сервер снова

```bash
python api_server.py
```

Должно показать:
```
==================================================
Starting SmartAI API Server...
==================================================
[HH:MM:SS] Модель загружена успешно
Endpoints:
  POST /predict - Предсказание
  GET  /health  - Проверка статуса
  POST /reload  - Перезагрузка модели
==================================================
 * Running on http://0.0.0.0:5000
```

**Важно:** Должно быть "Модель загружена успешно"!

### Шаг 4: Проверьте что модель загружена

В браузере или через curl:
```bash
curl http://localhost:5000/health
```

Должно показать:
```json
{
  "status": "ok",
  "model_loaded": true,
  "model_load_time": "2026-03-08T11:25:00.123456"
}
```

**Важно:** `"model_loaded": true`!

### Шаг 5: Перезапустите Minecraft сервер

Теперь перезапустите Minecraft сервер и проверьте логи:
```
[SmartAI] [AI] Подключено к API: http://localhost:5000
[SmartAI] [AI] AI проверка включена!
```

---

## Альтернатива: Перезагрузка модели без перезапуска API

Если API сервер уже запущен и вы переобучили модель:

```bash
curl -X POST http://localhost:5000/reload
```

Должно вернуть:
```json
{
  "status": "success",
  "message": "Model reloaded successfully",
  "load_time": "2026-03-08T11:25:00.123456"
}
```

---

## Проверка работы

После перезапуска:

1. Зайдите на сервер
2. Введите:
   ```
   /smartai prob [ваш_ник]
   ```
3. Побейте моба несколько раз
4. Должна появиться голограмма над головой с вероятностями

---

## Если модель все равно не загружается

### Проблема 1: Файл модели поврежден

Удалите старую модель и переобучите:
```bash
del smartai_model.pkl
python train_model.py
```

### Проблема 2: Недостаточно данных

Убедитесь что у вас есть данные в `plugins/SmartAI/datasets/`:
```bash
dir plugins\SmartAI\datasets\*.csv
```

Должно быть минимум:
- 1-2 файла LEGIT_*.csv
- 1-2 файла CHEAT_*.csv

Если данных нет, соберите их:
```
/smartai start [ник] legit
/smartai start [ник] cheat
```

### Проблема 3: Ошибка при загрузке

Запустите API в режиме отладки:
```bash
python api_server.py
```

И посмотрите на ошибки в консоли.

### Проблема 4: Неправильная версия библиотек

Переустановите зависимости:
```bash
pip install -r requirements.txt --upgrade
```

---

## Быстрая проверка всей цепочки

1. ✅ Модель существует:
   ```bash
   dir smartai_model.pkl
   ```

2. ✅ API запущен:
   ```bash
   curl http://localhost:5000/health
   ```

3. ✅ Модель загружена:
   ```json
   {"model_loaded": true}
   ```

4. ✅ Плагин подключен:
   ```
   [SmartAI] [AI] Подключено к API
   ```

5. ✅ Работает в игре:
   ```
   /smartai prob [ник]
   ```

Если все 5 пунктов ✅ - система работает!

---

## Автоматический скрипт перезапуска

Создайте `restart_api.bat`:
```batch
@echo off
echo Stopping old API server...
taskkill /F /IM python.exe /FI "WINDOWTITLE eq SmartAI*" 2>nul

echo Starting new API server...
start "SmartAI API" python api_server.py

echo Done! Check the new window.
pause
```

Запускайте этот файл когда нужно перезапустить API.
