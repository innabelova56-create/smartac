#!/usr/bin/env python3
"""
Скрипт для тестирования API сервера
"""

import requests
import json
import numpy as np

API_URL = "http://localhost:5000"

def test_health():
    """Проверка health endpoint"""
    print("🔍 Проверка health...")
    try:
        response = requests.get(f"{API_URL}/health", timeout=5)
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Сервер работает: {data}")
            return True
        else:
            print(f"❌ Ошибка: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ Не удалось подключиться: {e}")
        print("   Убедись что API сервер запущен: python api_server.py")
        return False

def test_predict():
    """Тест предсказания"""
    print("\n🔍 Тест предсказания...")
    
    # Генерация тестовых данных (40 тиков)
    # Имитация легитных движений
    legit_ticks = []
    for i in range(40):
        legit_ticks.append([
            np.random.normal(2.0, 1.0),   # delta_yaw
            np.random.normal(1.0, 0.5),   # delta_pitch
            np.random.normal(0.0, 0.3),   # accel_yaw
            np.random.normal(0.0, 0.2),   # accel_pitch
            np.random.normal(0.0, 0.1),   # jerk_yaw
            np.random.normal(0.0, 0.1),   # jerk_pitch
            np.random.uniform(0.0, 0.5),  # gcd_error_yaw
            np.random.uniform(0.0, 0.5),  # gcd_error_pitch
        ])
    
    try:
        response = requests.post(
            f"{API_URL}/predict",
            json={"ticks": legit_ticks},
            timeout=10
        )
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Предсказание получено:")
            print(f"   Вероятность читерства: {data.get('probability', 0):.4f}")
            print(f"   Читер: {data.get('is_cheating', False)}")
            return True
        else:
            print(f"❌ Ошибка: {response.status_code}")
            print(f"   {response.text}")
            return False
    except Exception as e:
        print(f"❌ Ошибка запроса: {e}")
        return False

def test_cheat_detection():
    """Тест детекции читера"""
    print("\n🔍 Тест детекции читера...")
    
    # Имитация читерских движений (идеальные ротации)
    cheat_ticks = []
    for i in range(40):
        cheat_ticks.append([
            5.0,   # delta_yaw - постоянная
            2.0,   # delta_pitch - постоянная
            0.0,   # accel_yaw - нет ускорения
            0.0,   # accel_pitch - нет ускорения
            0.0,   # jerk_yaw - нет рывков
            0.0,   # jerk_pitch - нет рывков
            0.0,   # gcd_error_yaw - идеальная
            0.0,   # gcd_error_pitch - идеальная
        ])
    
    try:
        response = requests.post(
            f"{API_URL}/predict",
            json={"ticks": cheat_ticks},
            timeout=10
        )
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ Предсказание получено:")
            print(f"   Вероятность читерства: {data.get('probability', 0):.4f}")
            print(f"   Читер: {data.get('is_cheating', False)}")
            
            if data.get('probability', 0) > 0.5:
                print("   ✅ Читер правильно определен!")
            else:
                print("   ⚠️  Модель не определила читера (нужно больше данных)")
            return True
        else:
            print(f"❌ Ошибка: {response.status_code}")
            return False
    except Exception as e:
        print(f"❌ Ошибка запроса: {e}")
        return False

def main():
    print("=" * 50)
    print("SmartAI API Test")
    print("=" * 50)
    
    # Проверка health
    if not test_health():
        return
    
    # Тест предсказания
    test_predict()
    
    # Тест детекции читера
    test_cheat_detection()
    
    print("\n" + "=" * 50)
    print("Тестирование завершено!")
    print("=" * 50)

if __name__ == "__main__":
    main()
