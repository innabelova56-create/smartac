# Облегченная Версия API для Слабых VPS

## Проблема
Тариф 172 MB RAM слишком мал для полной версии

## Решение: Упрощенная модель

### 1. Уменьшить модель
В `train_model.py`:
```python
model = RandomForestClassifier(
    n_estimators=50,      # Было 300
    max_depth=8,          # Было 15
    max_features='sqrt',  # Меньше признаков
    random_state=42,
    n_jobs=1              # Было -1
)
```

### 2. Облегченный API
Создать `api_server_lite.py`:
```python
from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)
model = joblib.load('smartai_model.pkl')

@app.route('/predict', methods=['POST'])
def predict():
    data = request.get_json()
    X = np.array(data.get('ticks', []))
    probs = model.predict_proba(X)[:, 1]
    return jsonify({'probability': float(np.mean(probs))})

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
```

### 3. Минимальные зависимости
`requirements_lite.txt`:
```
flask==3.0.0
scikit-learn==1.3.0
numpy==1.24.0
joblib==1.3.0
gunicorn==21.2.0
```

## Результат
- Потребление RAM: ~120-150 MB
- Может работать на 172 MB (впритык!)

## Но лучше использовать Railway/Render бесплатно!
