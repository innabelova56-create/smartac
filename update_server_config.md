# Быстрое решение - включить AI

## Проблема
В логах: `[SmartAI] [AI] AI проверка отключена в конфиге`

Это значит конфиг на сервере старый и AI выключен.

## Решение

### Вариант 1: Ручное редактирование (быстро)

1. Откройте файл:
```
C:\Users\Admin\Desktop\test\plugins\SmartAI\config.yml
```

2. Найдите строку:
```yaml
ai:
  enabled: false
```

3. Измените на:
```yaml
detection:
  enabled: true
  endpoint: "http://localhost:5000"
  sample-size: 40
  sample-interval: 10
```

4. Сохраните файл

5. В игре введите:
```
/smartai reload
```

Или перезапустите сервер.

---

### Вариант 2: Замена всего конфига (рекомендуется)

1. Остановите сервер: `/stop`

2. Удалите старый конфиг:
```
C:\Users\Admin\Desktop\test\plugins\SmartAI\config.yml
```

3. Скопируйте новый конфиг:
```
Из: C:\Users\Admin\Desktop\untitled\src\main\resources\config.yml
В:   C:\Users\Admin\Desktop\test\plugins\SmartAI\config.yml
```

4. Запустите сервер

---

## Проверка

После изменений в логах должно быть:
```
[SmartAI] [Голограммы] Включены!
[SmartAI] [AI] Подключено к API: http://localhost:5000
[SmartAI] [AI] AI проверка включена!
[SmartAI] [AI] Sample size: 40 ticks
[SmartAI] AI детекция активна!
```

---

## Тестирование

1. Зайдите в игру
2. Введите: `/smartai prob GanryPro`
3. Побейте моба 10+ раз
4. Над головой должна появиться голограмма с вероятностями
5. В actionbar должны показываться проценты

---

## Если все равно не работает

Убедитесь что:
1. ✅ API сервер запущен (`python api_server.py`)
2. ✅ Модель загружена (проверьте: `curl http://localhost:5000/health`)
3. ✅ В конфиге `detection.enabled: true`
4. ✅ Сервер перезапущен после изменения конфига
