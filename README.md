# Apk Presensi - Student Attendance System

**Apk Presensi** is an Android-based application designed to streamline student attendance management. By leveraging cloud-based services, the app ensures secure data handling, real-time updates, and an intuitive user interface for managing academic schedules and tasks.

## 👥 Team Members

| **Vegli Raif Rafi'i** | Developer | [@vegliraif](https://github.com/vegliraif) |
| **Rizky Noviansyah** | Developer | [@rizkynoviansyah22](https://github.com/rizkynoviansyah22) |
| **Rizqi Adittiya** | Developer | [@rizqii27](https://github.com/rizqii27) |
| **Editya Chandra** | Developer | [@EdityaChandra](https://github.com/EdityaChandra) |

## ✨ Main Features

The application provides a comprehensive suite of tools for students:

* **Secure Authentication**: User registration and login powered by Firebase Authentication.
* **Digital Attendance**: A dedicated module for recording presence in real-time.
* **Attendance History**: View a detailed log of past attendance records.
* **Task Management**: Create and track academic tasks and assignments.
* **Class Schedule**: View daily or weekly course schedules.
* **User Profile**: Manage personal information and account details.
* **Session Persistence**: Automatically remembers logged-in users to provide a seamless experience.

## 🚀 Technologies Used

This project is built using modern Android development standards:

* **Primary Language**: [Kotlin](https://kotlinlang.org/).
* **UI/UX Framework**: 
    * **View Binding**: For safe and efficient interaction with layout components.
    * **Material Design**: Following modern design principles for a clean look.
    * **ConstraintLayout**: For responsive and flexible UI designs.
* **Cloud Backend (Firebase)**:
    * **Firebase Auth**: For secure user management.
    * **Cloud Firestore**: For storing attendance data and schedules in a NoSQL database.
    * **Firebase Storage**: For hosting media and documents (e.g., attendance photos).
* **Libraries**:
    * **Glide**: For high-performance image loading and caching.
    * **AndroidX**: For core modern Android components.

## 📋 Prerequisites

To set up and run this project, you will need:

* **Android Studio**: Latest version (e.g., Ladybug or newer).
* **SDK Platform**: Minimum SDK version 24 (Android 7.0) and Target SDK version 36.
* **Java Development Kit (JDK)**: Version 11.
* **Firebase Account**: An active Firebase project to connect the app's backend.

## 📂 Project Structure

The core logic is organized into specific Activities and Adapters:

```text
com.project.apkpresensi/
├── AbsenActivity.kt      # Logic for recording attendance
├── HistoryActivity.kt    # Logic for displaying attendance logs
├── HistoryAdapter.kt     # Data binding for attendance history lists
├── JadwalActivity.kt     # Logic for course schedules
├── JadwalAdapter.kt      # Data binding for schedule lists
├── LoginActivity.kt      # Main entry point and user login
├── MainActivity.kt       # Dashboard navigation hub
├── ProfileActivity.kt    # User profile management
├── RegisterActivity.kt   # Account creation logic
├── RekapAdapter.kt       # Data binding for summary views
├── TaskActivity.kt       # Logic for managing tasks
└── TaskAdapter.kt       # Data binding for task lists
