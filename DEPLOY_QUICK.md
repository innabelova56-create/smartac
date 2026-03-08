# Быстрый Деплой на Railway

## 1. Загрузить на GitHub
```cmd
git init
git add api_server.py requirements.txt smartai_model.pkl Procfile runtime.txt railway.json
git commit -m "SmartAI API"
git branch -M main
git remote add origin https://github.com/ВАШ_ЮЗЕР/smartai-api.git
git push -u origin main
```

## 2. Деплой на Railway
1. https://railway.app → Login with GitHub
2. New Project → Deploy from GitHub repo
3. Выбрать `smartai-api`
4. Settings → Generate Domain
5. Скопировать URL

## 3. Обновить config.yml
```yaml
detection:
  endpoint: "https://ВАШ-URL.up.railway.app"
```

## 4. Проверить
```cmd
curl https://ВАШ-URL.up.railway.app/health
```

Готово! 🚀
