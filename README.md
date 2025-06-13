# QRScanner

This project was built as part of the Superformula Mobile Developer Coding Challenge.
This tool allows you to generate a QR code with an expiration time and later scan it to verify whether it’s still active or has expired.




https://github.com/user-attachments/assets/68551b6b-f073-41be-94c6-4fa87dd0b042



https://github.com/user-attachments/assets/8668329d-c2c0-427b-9908-d8b759f9031e

It includes:

- A backend service in Node.js to generate and validate QR seeds
- A Kotlin Android app to generate, display, and scan QR codes

## 📱 Android App

### Features

- Generate a new QR code from a seed fetched from the backend
- Automatically expires after 5 minutes
- Scan QR codes and validate them against the backend
- Clean UI with navigation across three screens

### How to run

1. Clone this repo
2. Open the project in Android Studio Hedgehog or later
3. Run the app on an emulator or physical device (Android 8+)
4. The backend is hosted on Render and available publicly

### Requirements

- Android Studio Hedgehog
- Android SDK 34
- Gradle Plugin 8.11.1
- Kotlin 1.9+


### Architecture

- **MVVM** pattern with clean separation between UI, business logic, and data layers.
- **Hilt** for dependency injection
- **Jetpack Compose** for UI
- **CameraX** for QR code scanning
- **Coroutines + StateFlow** for reactive state handling
- **Retrofit** for networking
- **Custom Result wrapper** for error handling

🧱 Simplified Class Tree

````

MainActivity
└── NavHost (Start, CreateQR, ScanQR)

UI
├── StartScreen
├── CreateQRScreen → SuccessScreen
└── ScanQRScreen → CameraView → QRCodeAnalyzer

ViewModels
├── MainViewModel → GenerateSeedUseCase
└── ScanQRViewModel → ValidateSeedUseCase

UseCases
├── GenerateSeedUseCase
└── ValidateSeedUseCase

Repository
└── QRRepositoryImpl → ApiService (Retrofit)

Model
├── Seed
└── SeedError (enum)

````




### 🧪 Testing
Unit tests are included for key components

ViewModel, UseCase and Repository layers are tested with MockK and Turbine

To run tests:

./gradlew test

## 🌐 Backend

- Built with **Node.js 20+** and **Express**
- Two endpoints: 
  - `POST /seed` to generate a seed
  - `GET /validate?seed=xyz` to validate if a seed is valid or expired
- Seeds expire after 5 minutes
- Data is stored in memory

### Deployment

The backend is hosted on Render and available publicly.

### How to run locally

```bash
git clone https://github.com/SolArabehety/QRScanner
cd qr-backend
npm install
npm start
```

Then the server will be running at http://localhost:3000.

For Android emulator:
change the BASE_URL [here](QRScanner/data/src/main/java/com/solara/data/networking/ApiService.kt) to "http://10.0.2.2:3000"

API Endpoints

curl -X POST http://10.0.2.2:3000/seed     

curl -X POST http://10.0.2.2:3000/validate 
  -H "Content-Type: application/json" \
  -d '{"seed": "exampleseed"}'

