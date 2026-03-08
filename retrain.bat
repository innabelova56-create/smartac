@echo off
echo ========================================
echo SmartAI Model Retraining
echo ========================================
echo.
echo This will retrain the model with improved parameters
echo to reduce false positives for legit players.
echo.
pause

python train_model.py

echo.
echo ========================================
echo Retraining complete!
echo.
echo Next steps:
echo 1. Restart the API server (api_server.py)
echo 2. Test the new model on your server
echo ========================================
pause
