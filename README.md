# SmartAI Anti-Cheat

AI-powered anti-cheat plugin for Minecraft 1.16.5 Paper servers using machine learning to detect aim assistance cheats.

## Features

- 🤖 Machine Learning detection (Random Forest)
- 📊 Real-time probability tracking
- 🎯 Hologram display above players
- 📈 Violation accumulation system
- 🎮 Suspects GUI with spectator mode
- 🔔 Alert system for moderators
- 📝 Data collection for model training

## Requirements

### Minecraft Server
- Paper 1.16.5
- Java 11+

### API Server
- Python 3.11+
- Flask
- scikit-learn
- NumPy, Pandas

## Quick Start

### 1. Install Plugin
```bash
# Build plugin
./gradlew build

# Copy to server
cp build/libs/untitled-1.0.jar plugins/SmartAI.jar
```

### 2. Deploy API
See [HOSTING_GUIDE.md](HOSTING_GUIDE.md) for Railway/Render deployment

### 3. Configure
Edit `plugins/SmartAI/config.yml`:
```yaml
detection:
  enabled: true
  endpoint: "https://your-api-url.com"
```

### 4. Train Model
```bash
# Collect data first
/smartai start GLOBAL CHEAT
/smartai start GLOBAL LEGIT

# Train model
python train_model.py

# Restart API
python api_server.py
```

## Commands

- `/smartai suspects` - Open suspects GUI
- `/smartai prob <player>` - Track player probability
- `/smartai alerts` - Toggle alerts
- `/smartai start <NICK|GLOBAL> <CHEAT|LEGIT>` - Start data collection
- `/smartai stop <NICK|GLOBAL>` - Stop data collection

## Documentation

- [Quick Start Guide](QUICK_START.md)
- [Hosting Guide](HOSTING_GUIDE.md)
- [Model Training](IMPROVE_MODEL.md)
- [Violation System](VIOLATION_SYSTEM.md)

## Tech Stack

- **Plugin**: Java 11, Paper API, PacketEvents
- **API**: Python, Flask, scikit-learn
- **Model**: Random Forest (300 trees, depth 15)

## License

MIT License - feel free to use and modify!

## Credits

Developed for Minecraft 1.16.5 anti-cheat detection
"# smartac" 
"# smartac" 
