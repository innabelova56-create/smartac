#!/usr/bin/env python3
"""
Создает пустую модель для первого запуска API
Используется только для деплоя, потом нужно обучить настоящую модель
"""
import joblib
from sklearn.ensemble import RandomForestClassifier
import numpy as np

# Создаем простую модель
model = RandomForestClassifier(n_estimators=10, max_depth=5, random_state=42)

# Обучаем на фейковых данных (8 признаков как в реальной модели)
X_dummy = np.random.rand(100, 8)
y_dummy = np.random.randint(0, 2, 100)
model.fit(X_dummy, y_dummy)

# Сохраняем
joblib.dump(model, 'smartai_model.pkl')
print("✓ Dummy model created: smartai_model.pkl")
print("⚠️ This is a placeholder! Train real model with: python train_model.py")
