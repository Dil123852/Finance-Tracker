## 🚀 Overview
LabExam3 is a beginner-friendly personal finance tracker Android app built with Kotlin. It helps users log incomes and expenses, set a monthly budget, and visualize spending by category. It solves the “where does my money go?” problem with an offline, simple UI that runs entirely on-device.

## ✨ Features
- Add, edit, and delete transactions (income/expense) with category and notes
- Monthly budget tracking with progress and remaining amount
- Expense breakdown pie chart (by category)
- Recent transactions list
- Simple local auth (signup/login) with per-user preferences
- Settings: currency selection, backup/restore to JSON, reset data, logout

## 🛠️ Tech Stack
- **Frontend**: Kotlin, Android SDK, AndroidX, Material Design, ViewBinding, RecyclerView, Navigation Component
- **Backend**: None (fully offline)
- **Database**: SharedPreferences (JSON via Gson)
- **Tools / Libraries**:
  - MPAndroidChart (charts)
  - Gson (serialization)
  - AndroidX Lifecycle (ViewModel/StateFlow)
  - JUnit, Espresso (tests)

## 📁 Project Structure
project (Android Gradle project)
 ├── app/
 │   ├── src/main/
 │   │   ├── java/com/example/labexam3/
 │   │   │   ├── ui/ (fragments, activities, adapters)
 │   │   │   ├── model/ (Transaction, enums)
 │   │   │   ├── utils/ (PreferencesManager, AccountManager, NotificationHelper)
 │   │   │   └── viewmodel/ (TransactionViewModel)
 │   │   ├── res/ (layouts, drawables, navigation)
 │   │   └── AndroidManifest.xml
 │   └── build.gradle.kts
 ├── settings.gradle.kts
 ├── gradle/ (wrapper)
 └── build.gradle.kts

## ⚙️ Installation
Requirements:
- Android Studio (Giraffe or newer recommended)
- Android SDK 34, minSdk 25
- Java 11

Option 1: Android Studio
1. Open Android Studio → Open an existing project → select this folder
2. Let Gradle sync finish
3. Run on an emulator or a connected device

Option 2: Command line
```bash
# From project root
./gradlew assembleDebug           # Windows: gradlew.bat assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## ▶️ Usage
1. Launch the app; the landing screen redirects to Login.
2. Create an account (Signup) or log in.
3. On Home:
   - Tap “+” to add a transaction (type, category, amount, note).
   - Set a monthly budget; remaining and progress update automatically.
   - View “Expenses by Category” pie chart.
   - Recent transactions show latest entries.
4. Settings:
   - Choose currency, back up data to JSON, restore from JSON, reset all data, or log out.

All data is stored locally per user using SharedPreferences.

## 🧪 Tests
```bash
./gradlew testDebugUnitTest
./gradlew connectedDebugAndroidTest   # requires emulator/device
```

## 🤝 Contributing
Contributions are welcome!
1. Fork the repo
2. Create a feature branch
3. Commit with clear messages
4. Open a pull request


## 📜 License
Add your preferred license (e.g., MIT) as `LICENSE`.

## 👤 Author
Your Name  
GitHub: @your-username  
LinkedIn/Website: optional


