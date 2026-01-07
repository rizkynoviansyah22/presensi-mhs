package com.project.apkpresensi
//file ini merupakan bagian dari proyek aplikasi presensi mahasiswa
import android.app.DatePickerDialog
//menampilkan dialog pemilihan tanggal deadline
import android.content.Intent
//navigasi antar halaman aplikasi
import android.graphics.Canvas
// keperluan grafis pada fitur swipe
import android.graphics.Color
//untuk mengisi area dengan warna solid sebagai background saat item di-swipe
import android.graphics.drawable.ColorDrawable
//warna solid untuk background swipe
import android.os.Bundle
// container untuk menyimpan dan mengirim data state Activity pada method onCreate
import android.view.LayoutInflater
// class untuk mengkonversi file XML layout menjadi objek View saat menampilkan dialog tambah tugas
import android.widget.Toast
//komponen untuk menampilkan pesan singkat seperti notifikasi validasi atau pesan error
import androidx.appcompat.app.AlertDialog
//container form penambahan tugas baru
import androidx.appcompat.app.AppCompatActivity
//base class Activity yang mendukung fitur-fitur modern Android pada berbagai versi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.apkpresensi.databinding.ActivityTaskBinding
import com.project.apkpresensi.databinding.DialogAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
// berfungsi untuk mendukung fitur-fitur utama pada halaman manajemen tugas
// meliputi komponen antarmuka pengguna, pengelolaan gesture swipe, navigasi antar
// halaman, integrasi dengan Firebase untuk autentikasi dan database,
// serta utilitas untuk pengelolaan format tanggal.


class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: TaskAdapter
    private val listTask = ArrayList<TaskModel>()

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    //Class TaskActivity merupakan turunan dari AppCompatActivity() yang menjadi base class untuk
    // Activity dengan dukungan fitur modern Android. Di dalam class ini terdapat variabel binding untuk
    // mengakses komponen UI, auth untuk autentikasi Firebase, db untuk akses database Firestore, adapter untuk
    // menghubungkan data dengan RecyclerView, listTask untuk menyimpan kumpulan data tugas, serta dateFormat
    // dengan pola "dd-MM-yyyy" untuk parsing tanggal saat proses sorting.
    private var isUpdatingStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSwipeToDelete()
        setupBottomNav()
        loadTasks()

        binding.fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }
    //Variabel isUpdatingStatus berfungsi sebagai flag untuk mencegah konflik data saat proses update status
    // tugas sedang berlangsung. Method onCreate() merupakan method utama yang dipanggil saat Activity pertama kali dibuat,
    // di dalamnya dilakukan inisialisasi View Binding, Firebase Auth, Firebase Firestore, pengaturan RecyclerView,
    // fitur swipe-to-delete, navigasi bawah, pemuatan data tugas dari database, serta listener pada tombol tambah tugas.

    private fun setupRecyclerView() {
        binding.rvTask.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(listTask) { task, position ->
            updateTaskStatus(task, position)
        }
        binding.rvTask.adapter = adapter
    }
    //setupRecyclerView() berfungsi untuk mengkonfigurasi RecyclerView dengan mengatur LinearLayoutManager
    // sebagai layout manager untuk menampilkan daftar tugas secara vertikal, menginisialisasi TaskAdapter
    // dengan data listTask beserta callback untuk menangani perubahan status tugas, kemudian menghubungkan
    // adapter tersebut ke RecyclerView.

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val taskToDelete = listTask[position]

                listTask.removeAt(position)
                adapter.notifyItemRemoved(position)

                db.collection("tasks").document(taskToDelete.id)
                    .delete()
                    .addOnSuccessListener {
                        Snackbar.make(binding.root, "Tugas dihapus", Snackbar.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        listTask.add(position, taskToDelete)
                        adapter.notifyItemInserted(position)
                        Toast.makeText(this@TaskActivity, "Gagal menghapus tugas", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvTask)
    }
    //setupSwipeToDelete() berfungsi untuk mengimplementasikan fitur hapus tugas dengan gesture swipe ke kiri
    // atau kanan menggunakan ItemTouchHelper. Ketika item di-swipe, sistem akan menghapus data dari list lokal
    // dan memperbarui tampilan, kemudian menghapus dokumen dari Firebase Firestore. Jika penghapusan berhasil
    // akan menampilkan Snackbar konfirmasi, namun jika gagal maka data akan dikembalikan ke posisi semula dan
    // menampilkan pesan error melalui Toast.

    private fun updateTaskStatus(task: TaskModel, position: Int) {
        isUpdatingStatus = true

        db.collection("tasks").document(task.id)
            .update("isDone", task.isDone)
            .addOnSuccessListener {
                // Delay sorting agar visual update dulu
                binding.root.postDelayed({
                    sortAndRefresh()
                    isUpdatingStatus = false
                }, 300)
            }
            .addOnFailureListener {
                // Revert status jika gagal
                task.isDone = !task.isDone
                adapter.notifyItemChanged(position)
                isUpdatingStatus = false
                Toast.makeText(this, "Gagal update status", Toast.LENGTH_SHORT).show()
            }
    }
    //updateTaskStatus() berfungsi untuk memperbarui status penyelesaian tugas ke Firebase Firestore.
    // Saat proses update dimulai, flag isUpdatingStatus diaktifkan untuk mencegah konflik data. Jika berhasil,
    // sistem akan menunggu 300 milidetik sebelum melakukan sorting agar animasi visual selesai terlebih dahulu.
    // Jika gagal, status tugas akan dikembalikan ke kondisi semula dan menampilkan pesan error.

    private fun sortAndRefresh() {
        listTask.sortWith { o1, o2 ->
            // 1. Prioritas: Yang belum selesai (false) ditaruh di atas
            if (o1.isDone != o2.isDone) {
                return@sortWith if (o1.isDone) 1 else -1
            }
            // 2. Prioritas: Berdasarkan Tanggal Deadline terdekat
            val tgl1 = try { dateFormat.parse(o1.deadline)?.time ?: Long.MAX_VALUE } catch (e: Exception) { Long.MAX_VALUE }
            val tgl2 = try { dateFormat.parse(o2.deadline)?.time ?: Long.MAX_VALUE } catch (e: Exception) { Long.MAX_VALUE }
            return@sortWith tgl1.compareTo(tgl2)
        }
        adapter.notifyDataSetChanged()
    }
    //sortAndRefresh() berfungsi untuk mengurutkan daftar tugas berdasarkan dua kriteria yaitu tugas yang belum selesai
    // akan ditampilkan di bagian atas, kemudian diurutkan berdasarkan deadline terdekat. Setelah proses sorting selesai,
    // adapter akan memperbarui tampilan RecyclerView.

    private fun loadTasks() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("tasks")
            .whereEqualTo("userId", uid)
            .addSnapshotListener(this) { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                // Skip reload jika sedang update status (untuk mencegah conflict)
                if (isUpdatingStatus) {
                    return@addSnapshotListener
                }

                listTask.clear()
                if (value != null) {
                    for (doc in value) {
                        val task = doc.toObject(TaskModel::class.java)
                        task.id = doc.id
                        listTask.add(task)
                    }
                }
                sortAndRefresh()
            }
    }
    //loadTasks() berfungsi untuk mengambil data tugas dari Firebase Firestore berdasarkan userId pengguna yang sedang login.
    // Method ini menggunakan addSnapshotListener sehingga data akan diperbarui secara real-time setiap ada perubahan di database.
    // Terdapat pengecekan flag isUpdatingStatus untuk mencegah konflik saat proses update status sedang berlangsung.
    // Data yang berhasil diambil kemudian dikonversi ke objek TaskModel dan ditambahkan ke listTask, lalu dilakukan sorting
    // dan refresh tampilan.

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()

        dialogBinding.etDeadline.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                val dateStr = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year)
                dialogBinding.etDeadline.setText(dateStr)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }
        //showAddTaskDialog() berfungsi untuk menampilkan dialog form penambahan tugas baru menggunakan AlertDialog
        // dengan layout DialogAddTaskBinding. Pada field deadline terdapat listener yang akan menampilkan DatePickerDialog
        // ketika diklik, dengan pengaturan tanggal minimal adalah hari ini agar pengguna tidak dapat memilih tanggal yang
        // sudah lewat. Tanggal yang dipilih kemudian diformat menjadi "dd-MM-yyyy" dan ditampilkan pada EditText deadline.

        dialogBinding.btnSimpanTask.setOnClickListener {
            val matkul = dialogBinding.etMatkul.text.toString().trim()
            val judul = dialogBinding.etJudul.text.toString().trim()
            val deadline = dialogBinding.etDeadline.text.toString().trim()
            val deskripsi = dialogBinding.etDeskripsi.text.toString().trim()

            if (judul.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Judul & Deadline wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //Pada bagian ini terdapat listener untuk tombol simpan yang akan mengambil nilai input dari form yaitu mata kuliah,
            // judul, deadline, dan deskripsi. Sebelum data disimpan, dilakukan validasi untuk memastikan field judul dan
            // deadline tidak kosong. Jika validasi gagal, sistem akan menampilkan pesan peringatan melalui Toast dan proses
            // penyimpanan dibatalkan.

            dialogBinding.btnSimpanTask.isEnabled = false
            dialogBinding.btnSimpanTask.text = "Menyimpan..."

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val newTask = hashMapOf(
                "userId" to uid,
                "matkul" to matkul,
                "judul" to judul,
                "deadline" to deadline,
                "deskripsi" to deskripsi,
                "isDone" to false,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("tasks").add(newTask)
                .addOnSuccessListener {
                    Toast.makeText(this, "Tugas berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
                    dialogBinding.btnSimpanTask.isEnabled = true
                    dialogBinding.btnSimpanTask.text = "Simpan"
                }
        }
        dialog.show()
    }
    //Data tugas baru kemudian disusun dalam bentuk HashMap yang berisi userId, matkul, judul, deadline, deskripsi,
    // status isDone dengan nilai false, serta timestamp pembuatan. Data tersebut selanjutnya disimpan ke Firebase Firestore
    // collection "tasks". Jika berhasil akan menampilkan pesan sukses dan menutup dialog, namun jika gagal akan
    // menampilkan pesan error dan mengaktifkan kembali tombol simpan.

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener { navigateTo(MainActivity::class.java) }
        binding.navJadwal.setOnClickListener { navigateTo(JadwalActivity::class.java) }
        binding.navProfile.setOnClickListener { navigateTo(ProfileActivity::class.java) }
    }
    //setupBottomNav() berfungsi untuk mengatur listener pada setiap menu bottom navigation.
    // Ketika menu Home, Jadwal, atau Profil diklik, sistem akan memanggil method navigateTo() untuk berpindah ke
    // Activity yang sesuai yaitu MainActivity, JadwalActivity, atau ProfileActivity.

    private fun navigateTo(cls: Class<*>) {
        startActivity(Intent(this, cls))
        overridePendingTransition(0, 0)
        finish()
    }
}
//navigateTo() berfungsi untuk melakukan perpindahan halaman ke Activity lain yang ditentukan melalui parameter class.
// Perpindahan dilakukan tanpa animasi transisi menggunakan overridePendingTransition(0, 0) agar terasa lebih responsif,
// kemudian Activity saat ini ditutup dengan memanggil finish().