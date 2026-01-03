# Apk Presensi - Sistem Presensi Mahasiswa Berbasis Android

Apk Presensi adalah aplikasi mobile yang dirancang untuk memudahkan mahasiswa dalam melakukan presensi secara digital. Aplikasi ini mengintegrasikan layanan cloud untuk penyimpanan data yang aman dan fitur manajemen tugas sederhana.

## ✨ Fitur Utama

Aplikasi ini mencakup berbagai fitur fungsionalitas untuk mendukung kegiatan akademik:

* **Autentikasi Pengguna**: Login dan pendaftaran mahasiswa menggunakan email.
* **Presensi Digital**: Fitur utama untuk melakukan absensi kehadiran.
* **Riwayat Presensi**: Melihat catatan kehadiran yang telah dilakukan sebelumnya.
* **Manajemen Tugas (Task)**: Fitur untuk melihat atau mengelola daftar tugas.
* **Jadwal Kuliah**: Akses cepat untuk melihat jadwal perkuliahan.
* **Profil Pengguna**: Informasi detail mahasiswa dan pengaturan akun.
* **Auto-Login**: Sistem akan mengingat sesi login pengguna sehingga tidak perlu masuk berulang kali.

## 🚀 Teknologi yang Digunakan

Proyek ini dibangun menggunakan teknologi modern untuk pengembangan Android:

* **Bahasa Pemrograman**: [Kotlin](https://kotlinlang.org/)
* **Arsitektur & UI**: 
    * View Binding (untuk interaksi komponen layout yang aman).
    * Android Jetpack (Activity, ConstraintLayout, ViewModel).
    * Material Design Components.
* **Backend & Cloud (Firebase)**:
    * **Firebase Authentication**: Digunakan untuk sistem login dan pendaftaran pengguna.
    * **Cloud Firestore**: Basis data NoSQL untuk menyimpan data presensi dan jadwal.
    * **Firebase Storage**: Digunakan untuk mengunggah dan menyimpan foto (seperti foto bukti absen).
* **Library Pihak Ketiga**:
    * **Glide**: Library untuk memproses dan menampilkan gambar dari URL/Cloud Storage secara efisien.
    * **CardView**: Untuk antarmuka pengguna yang bersih dan modern.

## 📋 Prasyarat Instalasi

Sebelum menjalankan proyek ini, pastikan Anda telah memenuhi persyaratan berikut:

1.  **Android Studio** versi terbaru (disarankan Ladybug atau lebih baru).
2.  **JDK 11** atau versi yang lebih tinggi.
3.  Perangkat fisik Android atau Emulator dengan **Minimal SDK 24** (Android 7.0 Nougat).
4.  Akun **Firebase** aktif.

## 🛠️ Persiapan Proyek

1.  **Clone Repositori**:
    ```bash
    git clone [https://github.com/rizkynoviansyah22/presensi-mhs.git](https://github.com/rizkynoviansyah22/presensi-mhs.git)
    ```
2.  **Konfigurasi Firebase**:
    * Buat proyek baru di [Firebase Console](https://console.firebase.google.com/).
    * Daftarkan aplikasi Android Anda dengan paket name `com.project.apkpresensi`.
    * Unduh file `google-services.json` dan letakkan di dalam folder `app/`.
    * Aktifkan *Authentication*, *Firestore Database*, dan *Storage* di konsol Firebase.
3.  **Build Project**: Buka proyek di Android Studio dan sinkronkan Gradle.

## 📂 Susunan Proyek

Struktur folder utama dalam aplikasi ini adalah sebagai berikut:

```text
app/src/main/java/com/project/apkpresensi/
├── LoginActivity.kt      # Halaman masuk utama pengguna
├── RegisterActivity.kt   # Halaman pendaftaran akun baru
├── MainActivity.kt       # Dashboard utama aplikasi
├── AbsenActivity.kt      # Fitur input presensi
├── HistoryActivity.kt    # Menampilkan riwayat kehadiran
├── JadwalActivity.kt     # Menampilkan jadwal perkuliahan
├── TaskActivity.kt       # Manajemen tugas mahasiswa
└── ProfileActivity.kt    # Detail informasi pengguna
