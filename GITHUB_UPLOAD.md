# Загрузка SmartAI на GitHub

## Шаг 1: Создать репозиторий на GitHub

1. Зайди на https://github.com
2. Войди в аккаунт (или зарегистрируйся)
3. Нажми "+" в правом верхнем углу → "New repository"
4. Заполни:
   - Repository name: `smartai-anticheat`
   - Description: `AI-powered anti-cheat for Minecraft 1.16.5`
   - Public (чтобы Railway/Render могли подключиться)
   - НЕ ставь галочки на README, .gitignore, license
5. Нажми "Create repository"
6. Скопируй URL (будет вида: `https://github.com/ВАШ_ЮЗЕР/smartai-anticheat.git`)

## Шаг 2: Настроить Git (если первый раз)

```cmd
git config --global user.name "Твое Имя"
git config --global user.email "твой@email.com"
```

## Шаг 3: Загрузить проект

```cmd
cd C:\Users\Admin\Desktop\untitled

git init
git add .
git commit -m "Initial commit: SmartAI Anti-Cheat"
git branch -M main
git remote add origin https://github.com/ВАШ_ЮЗЕР/smartai-anticheat.git
git push -u origin main
```

## Шаг 4: Проверить

Обнови страницу на GitHub - должны появиться все файлы!

## Готово! 🎉

Теперь можешь деплоить на Railway/Render
