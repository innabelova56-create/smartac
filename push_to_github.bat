@echo off
echo ========================================
echo SmartAI - Push to GitHub
echo ========================================
echo.

echo ВАЖНО: Сначала создай репозиторий на GitHub!
echo.
echo 1. Зайди на https://github.com
echo 2. Нажми + -^> New repository
echo 3. Название: smartai-anticheat
echo 4. Public
echo 5. НЕ добавляй README/gitignore
echo 6. Создай репозиторий
echo 7. Скопируй URL
echo.
echo Пример URL: https://github.com/innabelova56-create/smartai-anticheat.git
echo.

set /p REPO_URL="Вставь URL репозитория (или Enter для дефолтного): "

if "%REPO_URL%"=="" (
    set REPO_URL=https://github.com/innabelova56-create/smartai-anticheat.git
    echo Используется: %REPO_URL%
)

echo.
echo [1/3] Добавление remote...
git remote add origin %REPO_URL% 2>nul
if errorlevel 1 (
    echo Remote уже существует, обновляю...
    git remote set-url origin %REPO_URL%
)

echo [2/3] Настройка ветки main...
git branch -M main

echo [3/3] Загрузка на GitHub...
echo.
git push -u origin main

if errorlevel 1 (
    echo.
    echo ========================================
    echo ОШИБКА при загрузке!
    echo ========================================
    echo.
    echo Возможные причины:
    echo 1. Репозиторий не создан на GitHub
    echo 2. Неправильный URL
    echo 3. Нужна авторизация
    echo.
    echo Для авторизации:
    echo - Логин: innabelova56-create
    echo - Пароль: используй Personal Access Token
    echo.
    echo Как создать токен:
    echo 1. GitHub -^> Settings -^> Developer settings
    echo 2. Personal access tokens -^> Generate new token
    echo 3. Выбери права: repo
    echo 4. Скопируй токен и используй как пароль
    echo.
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! Проект на GitHub!
echo ========================================
echo.
echo Проверь: https://github.com/innabelova56-create/smartai-anticheat
echo.
echo Теперь можешь деплоить на Railway/Render!
echo См. HOSTING_GUIDE.md
echo.
pause
