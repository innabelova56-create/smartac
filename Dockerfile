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

# Note: Railway will override this with railway.json startCommand
