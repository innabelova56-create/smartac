@echo off
echo ========================================
echo SmartAI API Server Restart
echo ========================================
echo.

echo [1/3] Stopping old API server...
taskkill /F /PID 10036 2>nul
timeout /t 2 >nul

echo [2/3] Starting new API server...
start "SmartAI API" python api_server.py
timeout /t 3 >nul

echo [3/3] Checking status...
curl http://localhost:5000/health
echo.

echo ========================================
echo API server restarted!
echo Check the new window for logs.
echo ========================================
pause
