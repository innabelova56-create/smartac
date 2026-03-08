#!/usr/bin/env python3
"""
Обучение LSTM модели для детекции читеров
Требует: tensorflow, pandas, numpy, scikit-learn
"""

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import glob
import os

try:
    import tensorflow as tf
    from tensorflow import keras
    from tensorflow.keras import layers
except ImportError:
    print("Установите TensorFlow: pip install tensorflow")
    exit(1)

def load_and_prepare_sequences(data_folder="plugins/SmartAI/datasets", sequence_length=40):
    """Загрузка данных и создание последовательностей"""
    csv_files = glob.glob(os.path.join(data_folder, "*.csv"))
    
    if not csv_files:
        print(f"Нет CSV файлов в {data_folder}")
        return None, None, None
    
    print(f"Найдено {len(csv_files)} файлов")
    
    sequences = []
    labels = []
    
    for file in csv_files:
        try:
            df = pd.read_csv(file)
            if len(df) < sequence_length:
                continue
            
            # Извлечение признаков и метки
            features = df.drop('is_cheating', axis=1).values
            label = df['is_cheating'].iloc[0]  # Метка для всего файла
            
            # Создание последовательностей
            for i in range(0, len(features) - sequence_length + 1, sequence_length // 2):
                seq = features[i:i + sequence_length]
                if len(seq) == sequence_length:
                    sequences.append(seq)
                    labels.append(label)
            
            print(f"Обработано: {os.path.basename(file)}")
        except Exception as e:
            print(f"Ошибка: {file}: {e}")
    
    if not sequences:
        return None, None, None
    
    X = np.array(sequences)
    y = np.array(labels)
    
    print(f"\nВсего последовательностей: {len(X)}")
    print(f"Форма данных: {X.shape}")
    print(f"Легитных: {np.sum(y == 0)}")
    print(f"Читеров: {np.sum(y == 1)}")
    
    # Нормализация
    scaler = StandardScaler()
    X_reshaped = X.reshape(-1, X.shape[-1])
    X_scaled = scaler.fit_transform(X_reshaped)
    X = X_scaled.reshape(X.shape)
    
    return X, y, scaler

def create_lstm_model(sequence_length, n_features):
    """Создание LSTM модели"""
    model = keras.Sequential([
        layers.Input(shape=(sequence_length, n_features)),
        
        # Первый LSTM слой
        layers.LSTM(64, return_sequences=True),
        layers.Dropout(0.3),
        
        # Второй LSTM слой
        layers.LSTM(32, return_sequences=False),
        layers.Dropout(0.3),
        
        # Dense слои
        layers.Dense(16, activation='relu'),
        layers.Dropout(0.2),
        
        # Выходной слой
        layers.Dense(1, activation='sigmoid')
    ])
    
    model.compile(
        optimizer='adam',
        loss='binary_crossentropy',
        metrics=['accuracy', 
                 keras.metrics.Precision(name='precision'),
                 keras.metrics.Recall(name='recall')]
    )
    
    return model

def train_model(X, y, epochs=50, batch_size=32):
    """Обучение модели"""
    # Разделение данных
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    
    print("\nСоздание модели...")
    model = create_lstm_model(X.shape[1], X.shape[2])
    model.summary()
    
    # Callbacks
    early_stop = keras.callbacks.EarlyStopping(
        monitor='val_loss',
        patience=10,
        restore_best_weights=True
    )
    
    reduce_lr = keras.callbacks.ReduceLROnPlateau(
        monitor='val_loss',
        factor=0.5,
        patience=5,
        min_lr=0.00001
    )
    
    # Обучение
    print("\nОбучение модели...")
    history = model.fit(
        X_train, y_train,
        validation_data=(X_test, y_test),
        epochs=epochs,
        batch_size=batch_size,
        callbacks=[early_stop, reduce_lr],
        verbose=1
    )
    
    # Оценка
    print("\nОценка на тестовых данных:")
    test_loss, test_acc, test_prec, test_rec = model.evaluate(X_test, y_test)
    print(f"Accuracy: {test_acc:.4f}")
    print(f"Precision: {test_prec:.4f}")
    print(f"Recall: {test_rec:.4f}")
    print(f"F1-Score: {2 * (test_prec * test_rec) / (test_prec + test_rec):.4f}")
    
    # Предсказания
    y_pred_prob = model.predict(X_test)
    y_pred = (y_pred_prob > 0.5).astype(int).flatten()
    
    from sklearn.metrics import classification_report, confusion_matrix
    print("\nОтчет классификации:")
    print(classification_report(y_test, y_pred, target_names=['Легит', 'Читер']))
    
    print("\nМатрица ошибок:")
    print(confusion_matrix(y_test, y_pred))
    
    return model, history

def main():
    print("=== SmartAI LSTM Model Training ===\n")
    
    SEQUENCE_LENGTH = 40
    
    # Загрузка данных
    X, y, scaler = load_and_prepare_sequences(sequence_length=SEQUENCE_LENGTH)
    
    if X is None:
        print("Не удалось загрузить данные")
        return
    
    # Проверка баланса
    if len(np.unique(y)) < 2:
        print("Нужны данные обоих классов!")
        return
    
    # Обучение
    model, history = train_model(X, y, epochs=50, batch_size=32)
    
    # Сохранение
    model.save('smartai_lstm_model.h5')
    print("\nМодель сохранена в smartai_lstm_model.h5")
    
    # Сохранение scaler
    import joblib
    joblib.dump(scaler, 'smartai_scaler.pkl')
    print("Scaler сохранен в smartai_scaler.pkl")
    
    # График обучения
    try:
        import matplotlib.pyplot as plt
        
        plt.figure(figsize=(12, 4))
        
        plt.subplot(1, 2, 1)
        plt.plot(history.history['accuracy'], label='Train')
        plt.plot(history.history['val_accuracy'], label='Validation')
        plt.title('Model Accuracy')
        plt.xlabel('Epoch')
        plt.ylabel('Accuracy')
        plt.legend()
        
        plt.subplot(1, 2, 2)
        plt.plot(history.history['loss'], label='Train')
        plt.plot(history.history['val_loss'], label='Validation')
        plt.title('Model Loss')
        plt.xlabel('Epoch')
        plt.ylabel('Loss')
        plt.legend()
        
        plt.tight_layout()
        plt.savefig('training_history.png')
        print("График сохранен в training_history.png")
    except ImportError:
        print("Установите matplotlib для графиков: pip install matplotlib")

if __name__ == "__main__":
    main()
