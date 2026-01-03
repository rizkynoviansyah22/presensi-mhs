# Apk Presensi - Student Attendance System Based on Android

Apk Presensi is a mobile application designed to simplify digital attendance for students. The application integrates cloud services for secure data storage and features a simple task management system.

## Team Members

| Name               | Role       | GitHub |
|--------------------|------------|--------|
| Vegli Raif Rafi'i  | Developer  | [@vegliraif](https://github.com/vegliraif) |
| Rizky Noviansyah   | Developer  | [@rizkynoviansyah22](https://github.com/rizkynoviansyah22) |
| Rizqi Adittiya     | Developer  | [@rizqii27](https://github.com/rizqii27) |
| Editya Chandra     | Developer  | [@EdityaChandra](https://github.com/EdityaChandra) |

## Main Features

This application includes various functional features to support academic activities:

* **User Authentication**: Student login and registration using email.
* **Digital Attendance**: The core feature for recording presence.
* **Attendance History**: View previously recorded attendance logs.
* **Task Management**: A feature to view or manage task lists.
* **Class Schedule**: Quick access to view lecture schedules.
* **User Profile**: Detailed student information and account settings.
* **Auto-Login**: The system remembers the user's login session so there is no need to log in repeatedly.

## Technologies Used

This project is built using modern technologies for Android development:

* **Programming Language**: [Kotlin](https://kotlinlang.org/)
* **Architecture & UI**: 
    * View Binding (for safe interaction with layout components).
    * Android Jetpack (Activity, ConstraintLayout, ViewModel).
    * Material Design Components.
* **Backend & Cloud (Firebase)**:
    * **Firebase Authentication**: Used for user login and registration systems.
    * **Cloud Firestore**: A NoSQL database for storing attendance and schedule data.
    * **Firebase Storage**: Used for uploading and storing photos (such as attendance proof).
* **Third-Party Libraries**:
    * **Glide**: A library to efficiently process and display images from URL/Cloud Storage.
    * **CardView**: For a clean and modern user interface.

## Installation Prerequisites

Before running this project, ensure you have met the following requirements:

1. **Android Studio** latest version (Ladybug or newer recommended).
2. **JDK 11** or higher.
3. Physical Android device or Emulator with **Minimum SDK 24** (Android 7.0 Nougat).
4. Active **Firebase** account.

## Project Preparation

1. **Clone Repository**:
    ```bash
    git clone [https://github.com/rizkynoviansyah22/presensi-mhs.git](https://github.com/rizkynoviansyah22/presensi-mhs.git)
    ```
2. **Firebase Configuration**:
    * Create a new project in the [Firebase Console](https://console.firebase.google.com/).
    * Register your Android app with the package name `com.project.apkpresensi`.
    * Download the `google-services.json` file and place it in the `app/` folder.
    * Enable *Authentication*, *Firestore Database*, and *Storage* in the Firebase console.
3. **Build Project**: Open the project in Android Studio and sync Gradle.

## Project Structure

The main folder structure in this application is as follows:

```text
app/src/main/java/com/project/apkpresensi/
├── LoginActivity.kt      # User's main entry point
├── RegisterActivity.kt   # New account registration page
├── MainActivity.kt       # Dashboard of the application
├── AbsenActivity.kt      # Attendance input feature
├── HistoryActivity.kt    # Displays attendance history logs
├── JadwalActivity.kt     # Displays course lecture schedules
├── TaskActivity.kt       # Student task management feature
└── ProfileActivity.kt    # Detailed user account information
