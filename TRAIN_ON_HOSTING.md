# Обучение Модели на Хостинге

## Проблема
Модель `smartai_model.pkl` слишком большая (162 MB) для GitHub (лимит 100 MB)

## Решение
Обучить модель прямо на хостинге после деплоя

## Вариант 1: Railway (Рекомендуется)

### После деплоя на Railway:

1. Открой проект в Railway
2. Перейди в Settings → Variables
3. Добавь переменную:
   - Key: `TRAIN_ON_START`
   - Value: `true`

4. Создай файл `train_on_start.py`:
```python
import os
if os.environ.get('TRAIN_ON_START') == 'true':
    print("Training model on first start...")
    exec(open('train_model.py').read())
```

5. Обнови `Procfile`:
```
web: python train_on_start.py && gunicorn api_server:app
```

## Вариант 2: Загрузить Готовую Модель

### Использовать облачное хранилище:

1. Загрузи модель на Google Drive / Dropbox
2. Получи прямую ссылку
3. Добавь в `api_server.py`:

```python
import urllib.request

model_url = "https://ваша-ссылка/smartai_model.pkl"
if not os.path.exists('smartai_model.pkl'):
    print("Downloading model...")
    urllib.request.urlretrieve(model_url, 'smartai_model.pkl')
```

## Вариант 3: Локальное Обучение + Деплой

### Самый простой:

1. Обучи модель локально:
```cmd
python train_model.py
```

2. Загрузи модель отдельно на хостинг через веб-интерфейс Railway/Render

3. Или используй Git LFS:
```cmd
git lfs install
git lfs track "*.pkl"
git add .gitattributes
git add smartai_model.pkl
git commit -m "Add model with LFS"
git push
```

## Рекомендация

Для начала используй **Вариант 2** - загрузи модель на Google Drive и скачивай при старте API.
