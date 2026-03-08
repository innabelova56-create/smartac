#!/usr/bin/env python3
"""
Простой Flask API сервер для предсказаний
Требует: flask, joblib, numpy, scikit-learn
"""

from flask import Flask, request, jsonify
import joblib
import numpy as np
import os
from datetime import datetime

app = Flask(__name__)

# Глобальные переменные
model = None
model_load_time = None
model_path = 'smartai_model.pkl'

def load_model():
    """Загрузка или перезагрузка модели"""
    global model, model_load_time
    try:
        if os.path.exists(model_path):
            model = joblib.load(model_path)
            model_load_time = datetime.now()
            print(f"[{model_load_time.strftime('%H:%M:%S')}] Модель загружена успешно")
            return True
        else:
            print(f"⚠️ Файл модели не найден: {model_path}")
            print("Обучите модель командой: python train_model.py")
            print("Или загрузите готовую модель в корень проекта")
            return False
    except Exception as e:
        print(f"Ошибка загрузки модели: {e}")
        return False

# Загрузка модели при старте
load_model()

@app.route('/predict', methods=['POST'])
def predict():
    """
    Endpoint для предсказаний
    Ожидает JSON с массивом тиков:
    {
        "ticks": [
            [delta_yaw, delta_pitch, accel_yaw, accel_pitch, jerk_yaw, jerk_pitch, gcd_error_yaw, gcd_error_pitch],
            ...
        ]
    }
    """
    if model is None:
        return jsonify({'error': 'Model not loaded'}), 500
    
    try:
        data = request.get_json()
        ticks = data.get('ticks', [])
        
        if not ticks:
            return jsonify({'error': 'No ticks provided'}), 400
        
        # Преобразование в numpy array
        X = np.array(ticks)
        
        # Предсказание вероятностей
        probabilities = model.predict_proba(X)[:, 1]  # Вероятность читерства
        
        # Консервативная агрегация для снижения ложных срабатываний
        # Используем взвешенную комбинацию статистик
        avg_prob = np.mean(probabilities)
        median_prob = np.median(probabilities)
        p75_prob = np.percentile(probabilities, 75)
        p90_prob = np.percentile(probabilities, 90)
        
        # Взвешенная формула (более консервативная)
        # 40% среднее, 30% медиана, 20% p75, 10% p90
        final_prob = (
            avg_prob * 0.40 +
            median_prob * 0.30 +
            p75_prob * 0.20 +
            p90_prob * 0.10
        )
        
        return jsonify({
            'probability': float(final_prob),
            'is_cheating': bool(final_prob > 0.6),
            'samples': len(ticks),
            'stats': {
                'avg': float(avg_prob),
                'median': float(median_prob),
                'p75': float(p75_prob),
                'p90': float(p90_prob)
            }
        })
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({
        'status': 'ok',
        'model_loaded': model is not None,
        'model_load_time': model_load_time.isoformat() if model_load_time else None
    })

@app.route('/reload', methods=['POST'])
def reload():
    """Reload model endpoint"""
    if load_model():
        return jsonify({
            'status': 'success',
            'message': 'Model reloaded successfully',
            'load_time': model_load_time.isoformat()
        })
    else:
        return jsonify({
            'status': 'error',
            'message': 'Failed to reload model'
        }), 500

@app.route('/train', methods=['POST'])
def train():
    """Train model endpoint - trains model from datasets folder"""
    try:
        import subprocess
        result = subprocess.run(['python', 'train_model.py'], 
                              capture_output=True, text=True, timeout=300)
        
        if result.returncode == 0:
            # Reload model after training
            load_model()
            return jsonify({
                'status': 'success',
                'message': 'Model trained successfully',
                'output': result.stdout
            })
        else:
            return jsonify({
                'status': 'error',
                'message': 'Training failed',
                'error': result.stderr
            }), 500
    except Exception as e:
        return jsonify({
            'status': 'error',
            'message': str(e)
        }), 500

if __name__ == '__main__':
    print("=" * 50)
    print("Starting SmartAI API Server...")
    print("=" * 50)
    print("Endpoints:")
    print("  POST /predict - Предсказание")
    print("  GET  /health  - Проверка статуса")
    print("  POST /reload  - Перезагрузка модели")
    print("=" * 50)
    
    # Получаем порт из переменной окружения (для Railway/Render)
    port = int(os.environ.get('PORT', 5000))
    host = os.environ.get('HOST', '0.0.0.0')
    
    print(f"Server running on {host}:{port}")
    app.run(host=host, port=port, debug=False)
