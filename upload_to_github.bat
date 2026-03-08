@echo off
echo ========================================
echo SmartAI - Upload to GitHub
echo ========================================
echo.

REM Проверка git
git --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Git не установлен!
    echo Скачай с https://git-scm.com/download/win
    pause
    exit /b 1
)

echo [1/6] Настройка Git...
echo.
set /p USERNAME="Введи свое имя для Git: "
set /p EMAIL="Введи свой email: "
git config --global user.name "%USERNAME%"
git config --global user.email "%EMAIL%"
echo Git настроен!
echo.

echo [2/6] Инициализация репозитория...
git init
echo.

echo [3/6] Добавление файлов...
git add .
echo.

echo [4/6] Создание коммита...
git commit -m "Initial commit: SmartAI Anti-Cheat"
echo.

echo [5/6] Настройка ветки main...
git branch -M main
echo.

echo [6/6] Добавление remote...
echo.
echo ВАЖНО: Сначала создай репозиторий на GitHub!
echo 1. Зайди на https://github.com
echo 2. Нажми + -> New repository
echo 3. Название: smartai-anticheat
echo 4. Public
echo 5. Создай репозиторий
echo 6. Скопируй URL (https://github.com/USER/smartai-anticheat.git)
echo.
set /p REPO_URL="Вставь URL репозитория: "

git remote add origin %REPO_URL%
echo.

echo Загружаю на GitHub...
git push -u origin main
echo.

if errorlevel 1 (
    echo.
    echo ERROR: Не удалось загрузить!
    echo Возможные причины:
    echo - Неправильный URL
    echo - Нет доступа к репозиторию
    echo - Нужна авторизация
    echo.
    echo Попробуй вручную:
    echo git push -u origin main
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! Проект загружен на GitHub!
echo ========================================
echo.
echo Теперь можешь деплоить на Railway/Render
echo См. HOSTING_GUIDE.md
echo.
pause
