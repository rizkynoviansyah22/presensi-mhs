package com.project.apkpresensi

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: TaskAdapter
    private val listTask = ArrayList<TaskModel>()

    // Format tanggal dibuat konstan agar konsisten
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSwipeToDelete() // Fitur baru: Geser untuk hapus
        setupBottomNav()
        loadTasks()

        binding.fabAdd.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupRecyclerView() {
        binding.rvTask.layoutManager = LinearLayoutManager(this)
        // Callback saat checkbox diklik
        adapter = TaskAdapter(listTask) { task ->
            updateTaskStatus(task)
        }
        binding.rvTask.adapter = adapter
    }

    // Fitur: Geser item ke samping untuk menghapus
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

                // Hapus dari list sementara (visual)
                listTask.removeAt(position)
                adapter.notifyItemRemoved(position)

                // Hapus dari Firestore
                db.collection("tasks").document(taskToDelete.id)
                    .delete()
                    .addOnSuccessListener {
                        Snackbar.make(binding.root, "Tugas dihapus", Snackbar.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        // Jika gagal, kembalikan item ke list
                        listTask.add(position, taskToDelete)
                        adapter.notifyItemInserted(position)
                        Toast.makeText(this@TaskActivity, "Gagal menghapus tugas", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvTask)
    }

    private fun updateTaskStatus(task: TaskModel) {
        db.collection("tasks").document(task.id)
            .update("isDone", task.isDone)
            .addOnSuccessListener {
                sortAndRefresh() // Sort ulang setelah status berubah
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal update status", Toast.LENGTH_SHORT).show()
                // Revert checkbox jika gagal (opsional, butuh logic di adapter)
            }
    }

    private fun sortAndRefresh() {
        listTask.sortWith(Comparator { o1, o2 ->
            // 1. Prioritas: Yang belum selesai (false) ditaruh di atas
            if (o1.isDone != o2.isDone) {
                return@Comparator if (o1.isDone) 1 else -1
            }
            // 2. Prioritas: Berdasarkan Tanggal Deadline terdekat
            val tgl1 = try { dateFormat.parse(o1.deadline)?.time ?: Long.MAX_VALUE } catch (e: Exception) { Long.MAX_VALUE }
            val tgl2 = try { dateFormat.parse(o2.deadline)?.time ?: Long.MAX_VALUE } catch (e: Exception) { Long.MAX_VALUE }
            return@Comparator tgl1.compareTo(tgl2)
        })
        adapter.notifyDataSetChanged()
    }

    private fun loadTasks() {
        val uid = auth.currentUser?.uid ?: return

        // UPDATE PENTING: Menambahkan 'this' agar listener mati saat Activity hancur
        db.collection("tasks")
            .whereEqualTo("userId", uid)
            .addSnapshotListener(this) { value, error ->
                if (error != null) {
                    Toast.makeText(this, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
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

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()

        dialogBinding.etDeadline.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog(this, { _, year, month, day ->
                val dateStr = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year)
                dialogBinding.etDeadline.setText(dateStr)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            // Set minimal tanggal hari ini
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        dialogBinding.btnSimpanTask.setOnClickListener {
            val matkul = dialogBinding.etMatkul.text.toString().trim()
            val judul = dialogBinding.etJudul.text.toString().trim()
            val deadline = dialogBinding.etDeadline.text.toString().trim()
            val deskripsi = dialogBinding.etDeskripsi.text.toString().trim()

            if (judul.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Judul & Deadline wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable tombol agar tidak double click
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
                "createdAt" to System.currentTimeMillis() // Opsional: untuk sorting default
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

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener { navigateTo(MainActivity::class.java) }
        binding.navJadwal.setOnClickListener { navigateTo(JadwalActivity::class.java) }
        binding.navProfile.setOnClickListener { navigateTo(ProfileActivity::class.java) }
    }

    // Helper function agar codingan navigasi lebih rapi
    private fun navigateTo(cls: Class<*>) {
        startActivity(Intent(this, cls))
        overridePendingTransition(0, 0)
        finish()
    }
}