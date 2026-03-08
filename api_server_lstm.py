#!/usr/bin/env python3
"""
Flask API сервер для LSTM модели
Требует: flask, tensorflow, joblib, numpy
"""

from flask import Flask, request, jsonify
import numpy as np
import joblib

try:
    import tensorflow as tf
    from tensorflow import keras
except ImportError:
    print("Установите TensorFlow: pip install tensorflow")
    exit(1)

app = Flask(__name__)

# Загрузка модели и scaler
try:
    model = keras.models.load_model('smartai_lstm_model.h5')
    scaler = joblib.load('smartai_scaler.pkl')
    print("✓ LSTM модель загружена")
    print("✓ Scaler загружен")
except Exception as e:
    print(f"Ошибка загрузки: {e}")
    model = None
    scaler = None

SEQUENCE_LENGTH = 40

@app.route('/predict', methods=['POST'])
def predict():
    """
    Endpoint для предсказаний
    Ожидает JSON:
    {
        "ticks": [
            [delta_yaw, delta_pitch, accel_yaw, accel_pitch, jerk_yaw, jerk_pitch, gcd_error_yaw, gcd_error_pitch],
            ... (40 тиков)
        ]
    }
    """
    if model is None or scaler is None:
        return jsonify({'error': 'Model not loaded'}), 500
    
    try:
        data = request.get_json()
        ticks = data.get('ticks', [])
        
        if not ticks:
            return jsonify({'error': 'No ticks provided'}), 400
        
        if len(ticks) != SEQUENCE_LENGTH:
            return jsonify({
                'error': f'Expected {SEQUENCE_LENGTH} ticks, got {len(ticks)}'
            }), 400
        
        # Преобразование и нормализация
        X = np.array(ticks).reshape(1, SEQUENCE_LENGTH, -1)
        X_scaled = scaler.transform(X.reshape(-1, X.shape[-1]))
        X_scaled = X_scaled.reshape(X.shape)
        
        # Предсказание
        probability = float(model.predict(X_scaled, verbose=0)[0][0])
        
        return jsonify({
            'probability': probability,
            'is_cheating': probability > 0.5,
            'confidence': abs(probability - 0.5) * 2,  # 0-1
            'model': 'LSTM'
        })
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/predict_batch', methods=['POST'])
def predict_batch():
    """
    Batch предсказания для нескольких последовательностей
    """
    if model is None or scaler is None:
        return jsonify({'error': 'Model not loaded'}), 500
    
    try:
        data = request.get_json()
        sequences = data.get('sequences', [])
        
        if not sequences:
            return jsonify({'error': 'No sequences provided'}), 400
        
        results = []
        for seq in sequences:
            if len(seq) != SEQUENCE_LENGTH:
                continue
            
            X = np.array(seq).reshape(1, SEQUENCE_LENGTH, -1)
            X_scaled = scaler.transform(X.reshape(-1, X.shape[-1]))
            X_scaled = X_scaled.reshape(X.shape)
            
            prob = float(model.predict(X_scaled, verbose=0)[0][0])
            results.append(prob)
        
        avg_prob = np.mean(results) if results else 0.0
        
        return jsonify({
            'average_probability': float(avg_prob),
            'predictions': results,
            'count': len(results)
        })
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    """Health check"""
    return jsonify({
        'status': 'ok',
        'model_loaded': model is not None,
        'scaler_loaded': scaler is not None,
        'model_type': 'LSTM',
        'sequence_length': SEQUENCE_LENGTH
    })

@app.route('/info', methods=['GET'])
def info():
    """Информация о модели"""
    if model is None:
        return jsonify({'error': 'Model not loaded'}), 500
    
    return jsonify({
        'model_type': 'LSTM',
        'sequence_length': SEQUENCE_LENGTH,
        'input_shape': str(model.input_shape),
        'output_shape': str(model.output_shape),
        'total_params': int(model.count_params())
    })

if __name__ == '__main__':
    print("=" * 50)
    print("SmartAI LSTM API Server")
    print("=" * 50)
    print("\nEndpoints:")
    print("  POST /predict       - Одно предсказание")
    print("  POST /predict_batch - Batch предсказания")
    print("  GET  /health        - Статус сервера")
    print("  GET  /info          - Информация о модели")
    print("\nStarting server on http://0.0.0.0:5000")
    print("=" * 50)
    
    app.run(host='0.0.0.0', port=5000, debug=False)
