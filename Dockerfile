FROM python:3.11-slim

WORKDIR /app

# Copy requirements first for better caching
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY api_server.py .
COPY create_dummy_model.py .
COPY train_model.py .

# Create dummy model
RUN python create_dummy_model.py

# Expose port
EXPOSE 8080

# Start command - use shell form to allow variable substitution
CMD ["sh", "-c", "gunicorn api_server:app --bind 0.0.0.0:${PORT:-8080} --workers 2 --timeout 120"]
