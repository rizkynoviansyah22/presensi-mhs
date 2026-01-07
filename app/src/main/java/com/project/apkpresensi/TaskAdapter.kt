package com.project.apkpresensi

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.apkpresensi.databinding.ItemTaskBinding
//File TaskAdapter.kt diawali dengan deklarasi package com.project.apkpresensi yang menunjukkan file ini merupakan bagian dari
// proyek aplikasi presensi mahasiswa. Di dalamnya terdapat import Color dan Paint untuk manipulasi warna dan efek strikethrough
// pada teks, LayoutInflater dan ViewGroup untuk membuat tampilan item, ContextCompat untuk mengakses resource warna
// secara kompatibel, RecyclerView sebagai base class adapter, serta ItemTaskBinding untuk mengakses komponen UI pada layout
// item tugas.
data class TaskModel(
    var id: String = "",
    val matkul: String = "",
    val judul: String = "",
    val deadline: String = "",
    val deskripsi: String = "",
    var isDone: Boolean = false
)
//Data class TaskModel berfungsi sebagai struktur data untuk merepresentasikan objek tugas yang terdiri dari field id
// untuk menyimpan ID dokumen Firestore, matkul untuk nama mata kuliah, judul untuk judul tugas, deadline untuk tanggal batas
// pengumpulan, deskripsi untuk keterangan tambahan, serta isDone untuk menandai status penyelesaian tugas.

class TaskAdapter(
    private val listTask: ArrayList<TaskModel>,
    private val onCheckChanged: (TaskModel, Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var isBinding = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }
    //Class TaskAdapter merupakan adapter untuk RecyclerView yang menerima parameter listTask sebagai sumber data dan
    // onCheckChanged sebagai callback ketika status checkbox berubah. Di dalamnya terdapat inner class TaskViewHolder yang
    // menyimpan referensi binding layout item serta flag isBinding untuk mencegah trigger listener saat proses binding berlangsung.
    // Method onCreateViewHolder() berfungsi untuk membuat instance ViewHolder baru dengan meng-inflate layout item_task.xml
    // menggunakan View Binding.

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = listTask[position]
        val context = holder.itemView.context

        holder.isBinding = true

        holder.binding.tvMatkul.text = task.matkul
        holder.binding.tvJudul.text = task.judul
        holder.binding.tvDeadline.text = task.deadline
        holder.binding.tvDeskripsi.text = task.deskripsi

        // Hapus listener sebelum set checked
        holder.binding.cbTask.setOnCheckedChangeListener(null)

        // Set checkbox state
        holder.binding.cbTask.isChecked = task.isDone

        // Update visual berdasarkan status isDone
        updateVisual(holder, task.isDone, context)

        // Set flag binding = false setelah selesai
        holder.isBinding = false

        // Pasang listener baru
        holder.binding.cbTask.setOnCheckedChangeListener { _, isChecked ->
            // Cek flag binding
            if (holder.isBinding) return@setOnCheckedChangeListener

            val adapterPosition = holder.bindingAdapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < listTask.size) {
                // Cek apakah status benar-benar berubah
                if (listTask[adapterPosition].isDone != isChecked) {
                    listTask[adapterPosition].isDone = isChecked
                    // Update visual langsung saat diklik
                    updateVisual(holder, isChecked, context)
                    onCheckChanged(listTask[adapterPosition], adapterPosition)
                }
            }
        }
    }
    //onBindViewHolder() berfungsi untuk mengikat data TaskModel ke tampilan item pada posisi tertentu.
    // roses binding diawali dengan mengaktifkan flag isBinding untuk mencegah trigger listener yang tidak diinginkan,
    // kemudian mengisi data ke masing-masing TextView dan mengatur state checkbox. Listener checkbox dihapus terlebih dahulu
    // sebelum mengatur nilai checked untuk menghindari callback yang tidak diperlukan. Setelah binding selesai,
    // listener baru dipasang untuk menangani perubahan status tugas dengan validasi posisi dan pengecekan perubahan status
    // sebelum memanggil callback onCheckChanged.

    private fun updateVisual(
        holder: TaskViewHolder,
        isDone: Boolean,
        context: android.content.Context
    ) { //updateVisual() merupakan fungsi privat yang menerima tiga parameter yaitu holder untuk mengakses komponen tampilan item,
        // isDone untuk mengetahui status penyelesaian tugas, dan context untuk mengakses resource warna dari sistem.
        if (isDone) {
            // Tugas selesai
            holder.binding.tvJudul.paintFlags =
                holder.binding.tvJudul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvMatkul.paintFlags =
                holder.binding.tvMatkul.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            holder.binding.tvJudul.setTextColor(Color.DKGRAY)
            holder.binding.tvMatkul.setTextColor(Color.GRAY)
            holder.binding.tvDeskripsi.setTextColor(Color.GRAY)
            holder.binding.tvDeadline.setTextColor(Color.GRAY)

            holder.binding.root.alpha = 0.5f
        } else {
            // Tugas belum selesai: normal
            holder.binding.tvJudul.paintFlags =
                holder.binding.tvJudul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.tvMatkul.paintFlags =
                holder.binding.tvMatkul.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            holder.binding.tvJudul.setTextColor(
                ContextCompat.getColor(context, R.color.text_brown)
            )
            holder.binding.tvMatkul.setTextColor(
                ContextCompat.getColor(context, R.color.sage_dark)
            )
            holder.binding.tvDeskripsi.setTextColor(
                ContextCompat.getColor(context, R.color.text_brown)
            )
            holder.binding.tvDeadline.setTextColor(Color.parseColor("#EF4444"))

            holder.binding.root.alpha = 1.0f
        }
    }
    //updateVisual() berfungsi untuk mengubah tampilan item berdasarkan status penyelesaian tugas. Jika tugas sudah selesai,
    // teks judul dan mata kuliah akan diberi efek coret (strikethrough), seluruh warna teks diubah menjadi abu-abu,
    // dan opacity card dikurangi menjadi 50%. Sebaliknya jika tugas belum selesai, efek coret dihapus,
    // warna teks dikembalikan ke warna aslinya sesuai resource, dan opacity card dikembalikan menjadi 100%.

    override fun getItemCount(): Int = listTask.size
}
//mengembalikan jumlah total item dalam listTask yang akan ditampilkan pada RecyclerView.