package com.example.tugas4_crud

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var nrp: TextInputEditText
    private lateinit var nama: TextInputEditText
    private lateinit var dbku: SQLiteDatabase
    private lateinit var openDb: SQLiteOpenHelper
    private lateinit var rootView: View // Digunakan untuk Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootView = findViewById(android.R.id.content)

        nrp = findViewById(R.id.nrp)
        nama = findViewById(R.id.nama)

        findViewById<MaterialButton>(R.id.btnSimpan).setOnClickListener { simpan() }
        findViewById<MaterialButton>(R.id.btnCari).setOnClickListener { cari() }
        findViewById<MaterialButton>(R.id.btnUpdate).setOnClickListener { update() }
        findViewById<MaterialButton>(R.id.btnHapus).setOnClickListener { hapus() }

        openDb = object : SQLiteOpenHelper(this, "db_mahasiswa", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {}
            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        dbku = openDb.writableDatabase
        dbku.execSQL("create table if not exists mhs(nrp TEXT, nama TEXT);")
    }

    override fun onStop() {
        dbku.close()
        super.onStop()
    }

    private fun simpan() {
        if (nrp.text.isNullOrEmpty() || nama.text.isNullOrEmpty()) {
            showSnackbar("NRP dan Nama tidak boleh kosong!")
            return
        }
        val data = ContentValues().apply {
            put("nrp", nrp.text.toString())
            put("nama", nama.text.toString())
        }
        dbku.insert("mhs", null, data)
        showSnackbar("Data Mahasiswa Berhasil Disimpan \uD83D\uDCBE")
        clearInput()
    }

    private fun cari() {
        if (nrp.text.isNullOrEmpty()) {
            showSnackbar("Masukkan NRP untuk mencari data!")
            return
        }

        val query = "select * from mhs where nrp='${nrp.text}'"
        val cur: Cursor = dbku.rawQuery(query, null)

        if (cur.count > 0) {
            cur.moveToFirst()
            val columnIndex = cur.getColumnIndex("nama")
            if (columnIndex != -1) {
                nama.setText(cur.getString(columnIndex))
                showSnackbar("Data Ditemukan \uD83D\uDD0D")
            }
        } else {
            showSnackbar("Data dengan NRP tersebut tidak ditemukan")
            nama.setText("")
        }
        cur.close()
    }

    private fun update() {
        if (nrp.text.isNullOrEmpty()) return

        val data = ContentValues().apply {
            put("nrp", nrp.text.toString())
            put("nama", nama.text.toString())
        }
        val rowsAffected = dbku.update("mhs", data, "nrp='${nrp.text}'", null)

        if (rowsAffected > 0) {
            showSnackbar("Data Berhasil Diupdate \uD83D\uDD04")
            clearInput()
        } else {
            showSnackbar("Gagal update: Data tidak ditemukan")
        }
    }

    private fun hapus() {
        if (nrp.text.isNullOrEmpty()) return

        val rowsAffected = dbku.delete("mhs", "nrp='${nrp.text}'", null)

        if (rowsAffected > 0) {
            showSnackbar("Data Berhasil Dihapus 🗑️")
            clearInput()
        } else {
            showSnackbar("Gagal hapus: Data tidak ditemukan")
        }
    }

    private fun clearInput() {
        nrp.text?.clear()
        nama.text?.clear()
        nrp.requestFocus()
    }

    // Fungsi pembantu untuk memunculkan notifikasi modern
    private fun showSnackbar(pesan: String) {
        Snackbar.make(rootView, pesan, Snackbar.LENGTH_SHORT).show()
    }
}