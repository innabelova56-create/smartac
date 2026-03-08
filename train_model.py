#!/usr/bin/env python3
"""
Пример скрипта для обучения AI модели на собранных данных
Требует: pandas, scikit-learn, numpy
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, confusion_matrix
import glob
import os

def load_datasets(data_folder="plugins/SmartAI/datasets"):
    """Загрузка всех CSV файлов"""
    csv_files = glob.glob(os.path.join(data_folder, "*.csv"))
    
    if not csv_files:
        print(f"Нет CSV файлов в {data_folder}")
        return None
    
    print(f"Найдено {len(csv_files)} файлов")
    
    dfs = []
    for file in csv_files:
        try:
            df = pd.read_csv(file)
            dfs.append(df)
            print(f"Загружено: {os.path.basename(file)} ({len(df)} строк)")
        except Exception as e:
            print(f"Ошибка загрузки {file}: {e}")
    
    if not dfs:
        return None
    
    combined = pd.concat(dfs, ignore_index=True)
    print(f"\nВсего строк: {len(combined)}")
    print(f"Легитных: {len(combined[combined['is_cheating'] == 0])}")
    print(f"Читеров: {len(combined[combined['is_cheating'] == 1])}")
    
    return combined

def train_model(data):
    """Обучение модели Random Forest"""
    # Разделение на признаки и метки
    X = data.drop('is_cheating', axis=1)
    y = data['is_cheating']
    
    # Разделение на train/test
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    
    print("\nОбучение модели...")
    model = RandomForestClassifier(
        n_estimators=300,        # Больше деревьев для лучшей точности
        max_depth=15,            # Глубже для лучшего разделения
        min_samples_split=10,    # Требуем больше сэмплов для разделения
        min_samples_leaf=5,      # Требуем больше сэмплов в листе
        class_weight='balanced', # Балансировка классов
        random_state=42,
        n_jobs=-1
    )
    
    model.fit(X_train, y_train)
    
    # Оценка
    train_score = model.score(X_train, y_train)
    test_score = model.score(X_test, y_test)
    
    print(f"\nТочность на train: {train_score:.4f}")
    print(f"Точность на test: {test_score:.4f}")
    
    # Предсказания
    y_pred = model.predict(X_test)
    
    print("\nОтчет классификации:")
    print(classification_report(y_test, y_pred, 
                                target_names=['Легит', 'Читер']))
    
    print("\nМатрица ошибок:")
    print(confusion_matrix(y_test, y_pred))
    
    # Важность признаков
    feature_importance = pd.DataFrame({
        'feature': X.columns,
        'importance': model.feature_importances_
    }).sort_values('importance', ascending=False)
    
    print("\nВажность признаков:")
    print(feature_importance)
    
    return model

def main():
    print("=== SmartAI Model Training ===\n")
    
    # Загрузка данных
    data = load_datasets()
    if data is None:
        print("Не удалось загрузить данные")
        return
    
    # Проверка баланса классов
    class_balance = data['is_cheating'].value_counts()
    if len(class_balance) < 2:
        print("Нужны данные обоих классов (легит и читер)!")
        return
    
    # Обучение
    model = train_model(data)
    
    # Сохранение модели
    import joblib
    joblib.dump(model, 'smartai_model.pkl')
    print("\nМодель сохранена в smartai_model.pkl")

if __name__ == "__main__":
    main()
