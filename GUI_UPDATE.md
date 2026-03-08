# GUI Update - 21 Hits Display

## Changes Made

### 1. HologramManager.java
- Updated `addProbability()` method to store 21 hits instead of 10
- This allows the GUI to display all 21 hits (3 rows of 7)
- Hologram still shows only the last 5 hits above player's head
- Comment updated: "Храним последние 21 для GUI (3 ряда по 7), показываем последние 5 в голограмме"

### 2. SuspectsGUI.java (Already Updated)
- Title format: "▶ PlayerName [■■■■■■] 77%" with colored progress bar
- Shows 21 last hits in 3 rows of 7 hits each
- Section "Последние проверки:" displays the hits
- Section "Средний риск:" shows "AVG 0.xxxxxx"
- Bottom text: "▶ Нажмите, чтобы следить"
- All text in Russian

## How It Works

1. When a player hits someone, the probability is calculated
2. ViolationTracker adjusts the probability (adds buffer for cheaters, reduces for legit)
3. The adjusted probability is stored in HologramManager (up to 21 values)
4. Hologram shows last 5 hits above player's head
5. GUI shows all 21 hits when opened with `/smartai suspects`

## Color Coding

- Green: < 0.6 (legit)
- Orange: 0.6 - 0.8 (suspicious)
- Red: >= 0.8 (cheater)

## Testing

To test the GUI:
1. Start the server
2. Have players fight each other
3. Use `/smartai suspects` to open the GUI
4. Click on a player's head to see their 21 last hits

## Build Status

✅ Build successful - no compilation errors
