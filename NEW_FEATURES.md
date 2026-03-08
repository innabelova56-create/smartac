# Новые возможности SmartAI

## Обновленные команды

### Все команды теперь используют messages.yml
Все сообщения можно настроить в `plugins/SmartAI/messages.yml`

### Новые команды:

#### 1. /smartai start <player|global> <cheat|legit> [customName]
Начать сбор данных для одного игрока или всех сразу

Примеры:
```
/smartai start MirsTam cheat           - Собрать данные читера
/smartai start GanryPro legit          - Собрать данные легита
/smartai start global legit session1   - Собрать данные всех игроков
```

#### 2. /smartai stop <player|global>
Остановить сбор данных

Примеры:
```
/smartai stop MirsTam    - Остановить для игрока
/smartai stop global     - Остановить для всех
```

#### 3. /smartai alerts
Включить/выключить алерты для себя (toggle)

```
/smartai alerts    - Переключить алерты
```

#### 4. /smartai prob <player>
Отслеживать вероятность игрока в реальном времени (actionbar)

```
/smartai prob MirsTam    - Начать отслеживание
/smartai prob MirsTam    - Остановить отслеживание (повторная команда)
```

#### 5. /smartai datastatus
Показать статус сбора данных

```
/smartai datastatus
```

#### 6. /smartai reload
Перезагрузить конфигурацию

```
/smartai reload
```

#### 7. /smartai suspects
Открыть GUI с подозрительными игроками (в разработке)

```
/smartai suspects
```

#### 8. /smartai punish <player>
Выполнить максимальное наказание (в разработке)

```
/smartai punish MirsTam
```

---

## Обновленный конфиг

### Новая структура config.yml

```yaml
detection:
  enabled: true
  endpoint: "http://localhost:5000"
  sample-size: 40        # Количество тиков для анализа
  sample-interval: 10    # Проверка каждые N ударов

alerts:
  threshold: 0.75        # Порог для алертов
  console: true          # Алерты в консоль

violation:
  threshold: 40          # Порог VL для наказания
  reset-value: 20        # Сброс VL после наказания
  multiplier: 100.0      # Множитель буфера
  decay: 0.35            # Затухание буфера

penalties:
  min-probability: 0.01
  animation:
    enabled: true
    duration: 80
  actions:
    1: "{CUSTOM_ALERT} ..."
    2: "{KICK} ..."
    3: "{BAN} ..."

nametags:
  enabled: true
  format: "&6▶ &7AVG: &f{AVG} &8| {HISTORY} &6◀"
  colors:
    low: "&a"      # < 0.6
    medium: "&6"   # 0.6-0.8
    high: "&c"     # 0.8-0.9
    critical: "&4" # >= 0.9

gui:
  title: "&cSmartAI &8> &7Suspects"
  size: 54
```

---

## Sample Interval

Теперь AI проверка происходит не на каждый удар, а через интервал:

- `sample-interval: 10` = проверка каждые 10 ударов
- Это снижает нагрузку на API
- Рекомендуемое значение: 5-15

---

## Messages.yml

Все сообщения теперь настраиваются в отдельном файле:

```yaml
prefix: "&6[SmartAI] &r"

alerts-enabled: "&aAlerts enabled"
alerts-disabled: "&eAlerts disabled"

alert-format: "&c{PLAYER} &7| &6Prob: &f{PROBABILITY} &7| &6Buffer: &f{BUFFER}"

usage-start: "&7  /smartai start <player|global> <cheat|legit> [customName]"
# ... и т.д.
```

Плейсхолдеры:
- `{PLAYER}` - имя игрока
- `{PROBABILITY}` - вероятность
- `{BUFFER}` - буфер
- `{VL}` - violation level
- `{COUNT}` - количество
- `{LABEL}` - метка (cheat/legit)

---

## Что дальше?

1. Переобучите модель: `python train_model.py`
2. Перезапустите API: `python api_server.py`
3. Установите новый jar: `build/libs/SmartAI-1.0.jar`
4. Настройте `config.yml` и `messages.yml`
5. Протестируйте новые команды

---

## В разработке

- GUI для подозрительных игроков (`/smartai suspects`)
- Система наказаний (`/smartai punish`)
- WorldGuard интеграция
- VL decay система
- Анимация бана
