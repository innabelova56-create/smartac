FROM python:3.11-slim

WORKDIR /app

# Copy requirements first for better caching
COPY requirements.txt .

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY api_server.py .
COPY train_model.py .

# Note: Model will be downloaded from Google Drive on first start
# No dummy model creation to avoid conflicts

# Expose port
EXPOSE 8080
