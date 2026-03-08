# Следующие Шаги - Загрузка на GitHub

## ✅ Готово
- Git настроен (innabelova56-create)
- Репозиторий инициализирован
- Файлы закоммичены

## 📝 Что Нужно Сделать

### Шаг 1: Создать репозиторий на GitHub
1. Открой https://github.com
2. Войди в аккаунт (innabelova56-create)
3. Нажми "+" в правом верхнем углу
4. Выбери "New repository"
5. Заполни:
   - Repository name: `smartai-anticheat`
   - Description: `AI-powered anti-cheat for Minecraft`
   - Выбери: **Public** (чтобы Railway/Render могли подключиться)
   - НЕ ставь галочки на "Add README" и "Add .gitignore"
6. Нажми "Create repository"

### Шаг 2: Скопировать URL
После создания GitHub покажет URL вида:
```
https://github.com/innabelova56-create/smartai-anticheat.git
```
Скопируй его!

### Шаг 3: Выполнить команды

Открой командную строку в папке проекта и выполни:

```cmd
git remote add origin https://github.com/innabelova56-create/smartai-anticheat.git
git branch -M main
git push -u origin main
```

Или используй готовый скрипт:
```cmd
push_to_github.bat
```

## 🎉 После Загрузки

1. Обнови страницу на GitHub - увидишь все файлы
2. Открой HOSTING_GUIDE.md
3. Задеплой API на Railway или Render
4. Готово!

## ⚠️ Если Нужна Авторизация

GitHub может попросить логин/пароль:
- Логин: innabelova56-create
- Пароль: используй Personal Access Token

Как создать токен:
1. GitHub → Settings → Developer settings
2. Personal access tokens → Tokens (classic)
3. Generate new token
4. Выбери права: `repo` (все галочки)
5. Generate token
6. Скопируй токен (он больше не покажется!)
7. Используй токен вместо пароля при push
