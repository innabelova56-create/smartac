#!/usr/bin/env python3
"""
Скрипт для проверки игрока на читы
"""

import pandas as pd
import requests
import sys
import os

API_URL = "http://localhost:5000"

def check_player_file(csv_file):
    """Проверка CSV файла игрока"""
    if not os.path.exists(csv_file):
        print(f"❌ Файл не найден: {csv_file}")
        return
    
    print(f"📂 Загрузка файла: {csv_file}")
    
    try:
        df = pd.read_csv(csv_file)
        print(f"✅ Загружено {len(df)} тиков")
    except Exception as e:
        print(f"❌ Ошибка чтения файла: {e}")
        return
    
    # Удаляем колонку is_cheating для предсказания
    if 'is_cheating' in df.columns:
        X = df.drop('is_cheating', axis=1)
    else:
        X = df
    
    # Разбиваем на последовательности по 40 тиков
    sequence_length = 40
    predictions = []
    
    print(f"\n🔍 Анализ последовательностей...")
    
    for i in range(0, len(X) - sequence_length + 1, sequence_length // 2):
        sequence = X.iloc[i:i + sequence_length]
        
        if len(sequence) < sequence_length:
            continue
        
        # Отправляем на API
        ticks = sequence.values.tolist()
        
        try:
            response = requests.post(
                f"{API_URL}/predict",
                json={"ticks": ticks},
                timeout=5
            )
            
            if response.status_code == 200:
                result = response.json()
                prob = result.get('probability', 0)
                predictions.append(prob)
                
                # Показываем каждую последовательность
                status = "🔴 ЧИТЕР" if prob > 0.5 else "🟢 ЛЕГИТ"
                print(f"  Тики {i:4d}-{i+sequence_length:4d}: {prob:.2%} {status}")
        except Exception as e:
            print(f"  ❌ Ошибка запроса: {e}")
            continue
    
    if not predictions:
        print("\n❌ Не удалось получить предсказания")
        return
    
    # Итоговая оценка
    avg_prob = sum(predictions) / len(predictions)
    max_prob = max(predictions)
    
    print("\n" + "="*50)
    print("📊 ИТОГОВАЯ ОЦЕНКА")
    print("="*50)
    print(f"Средняя вероятность: {avg_prob:.2%}")
    print(f"Максимальная вероятность: {max_prob:.2%}")
    print(f"Проверено последовательностей: {len(predictions)}")
    
    # Вердикт
    if avg_prob > 0.8:
        print("\n🔴 ВЕРДИКТ: ЧИТЕР (высокая уверенность)")
    elif avg_prob > 0.6:
        print("\n🟠 ВЕРДИКТ: ПОДОЗРИТЕЛЬНО (средняя уверенность)")
    elif avg_prob > 0.4:
        print("\n🟡 ВЕРДИКТ: СОМНИТЕЛЬНО (низкая уверенность)")
    else:
        print("\n🟢 ВЕРДИКТ: ЛЕГИТ")
    
    print("="*50)

def main():
    print("="*50)
    print("SmartAI - Проверка игрока")
    print("="*50)
    
    # Проверка API
    try:
        response = requests.get(f"{API_URL}/health", timeout=2)
        if response.status_code != 200:
            print("❌ API сервер не отвечает!")
            print("   Запусти: python api_server.py")
            return
        print("✅ API сервер работает\n")
    except:
        print("❌ API сервер не запущен!")
        print("   Запусти: python api_server.py")
        return
    
    # Получаем путь к файлу
    if len(sys.argv) > 1:
        csv_file = sys.argv[1]
    else:
        print("Использование:")
        print("  python check_player.py <путь_к_csv_файлу>")
        print("\nПример:")
        print("  python check_player.py plugins/SmartAI/datasets/LEGIT_Player_20260307-183945.csv")
        print("\nИли укажи путь:")
        csv_file = input("\nПуть к CSV файлу: ").strip()
    
    if not csv_file:
        print("❌ Путь не указан")
        return
    
    check_player_file(csv_file)

if __name__ == "__main__":
    main()
