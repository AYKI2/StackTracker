#!/bin/bash

# Скрипт деплоя на Heroku с использованием Docker

APP_NAME="stocktracker-backend"
HEROKU_REMOTE="heroku"
BRANCH="feature/add-heroku"

echo "🚮 Очистка кэша Heroku..."
heroku builds:cache:purge -a "$APP_NAME"

echo "📁 Добавляем изменения..."
git add .

# Убираем staging с приватных/ненужных файлов
echo "🔄 Убираем из staged application-local.properties и скрипт..."
git restore --staged src/main/resources/application-local.properties
git restore --staged heroku.sh

echo "📝 Коммитим изменения..."
git commit -m "change docker-compose"

echo "🚀 Пушим изменения в ветку $BRANCH на Heroku..."
git push "$HEROKU_REMOTE" "$BRANCH"

echo "⚙️ Устанавливаем стек container для Heroku..."
heroku stack:set container --app "$APP_NAME"

# ➕ Добавление БД PostgreSQL, если ещё не создана
echo "🔍 Проверяем наличие базы данных..."
if ! heroku addons -a "$APP_NAME" | grep -q "heroku-postgresql"; then
  echo "➕ Создаём базу данных Heroku Postgres..."
  heroku addons:create heroku-postgresql:hobby-dev --app "$APP_NAME"
else
  echo "✅ База данных уже существует, пропускаем создание."
fi

echo "🐳 Собираем Docker-образ..."
docker build -t registry.heroku.com/"$APP_NAME"/web .

echo "📦 Пушим образ в Heroku Registry..."
docker push registry.heroku.com/"$APP_NAME"/web

echo "🚢 Деплоим контейнер в Heroku..."
heroku container:push web --app "$APP_NAME"

echo "🚀 Релизим приложение..."
heroku container:release web --app "$APP_NAME"

echo "✅ Деплой завершён!"
