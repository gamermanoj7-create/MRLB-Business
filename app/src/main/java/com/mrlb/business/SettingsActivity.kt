package com.mrlb.business

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrlb.business.data.AppDatabase
import com.mrlb.business.databinding.ActivitySettingsBinding
import com.mrlb.business.utils.BackupManager
import com.mrlb.business.utils.ExportUtils
import kotlinx.coroutines.launch

class SettingsActivity: AppCompatActivity() {
    private val db by lazy { AppDatabase.get(this) }
    private lateinit var b: ActivitySettingsBinding
    private var pendingBackupPin: String? = null
    private var pendingRestorePin: String? = null

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        b = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.pin.setOnClickListener { startActivity(Intent(this, PinActivity::class.java)) }

        b.exportPdf.setOnClickListener {
            lifecycleScope.launch {
                db.orders().all().collect { list ->
                    val lines = list.map { "#ORD-${it.id} | Total ₹${it.total} | Paid ₹${it.paid} | Due ₹${(it.total-it.paid).coerceAtLeast(0.0)}" }
                    val f = ExportUtils.createPdf(this@SettingsActivity, "Sales Report", lines)
                    ExportUtils.share(this@SettingsActivity, f, "application/pdf")
                    return@collect
                }
            }
        }

        b.exportCsv.setOnClickListener {
            lifecycleScope.launch {
                db.orders().all().collect { list ->
                    val f = ExportUtils.createCsv(this@SettingsActivity, db)
                    ExportUtils.writeCsv(f, list.map {
                        arrayOf(it.id.toString(), it.customerId.toString(), it.date.toString(),
                            it.total.toString(), it.paid.toString(), (it.total-it.paid).coerceAtLeast(0.0).toString(), it.status)
                    })
                    ExportUtils.share(this@SettingsActivity, f, "text/csv")
                    return@collect
                }
            }
        }

        b.backup.setOnClickListener { askPinForBackup() }
        b.restore.setOnClickListener { askPinForRestore() }
        b.whatsapp.setOnClickListener {
            ExportUtils.whatsapp(this, "", "MR Luxe Box & Bags: Please contact us regarding your account.")
        }
    }

    private fun askPinForBackup() {
        val input = android.widget.EditText(this).apply { hint = "Enter PIN" }
        android.app.AlertDialog.Builder(this).setTitle("Encrypt Backup").setView(input)
            .setPositiveButton("Choose file") { _, _ ->
                pendingBackupPin = input.text.toString()
                if (pendingBackupPin!!.length >= 4)
                    startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        type = "application/octet-stream"; putExtra(Intent.EXTRA_TITLE, "MRLB_Backup.mrlb")
                    }, REQ_BACKUP)
                else toast("PIN must be 4+ digits")
            }.setNegativeButton("Cancel", null).show()
    }

    private fun askPinForRestore() {
        val input = android.widget.EditText(this).apply { hint = "Enter backup PIN" }
        android.app.AlertDialog.Builder(this).setTitle("Restore Backup").setView(input)
            .setPositiveButton("Choose backup") { _, _ ->
                pendingRestorePin = input.text.toString()
                if (pendingRestorePin!!.length >= 4)
                    startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = "application/octet-stream"; addCategory(Intent.CATEGORY_OPENABLE)
                    }, REQ_RESTORE)
                else toast("PIN must be 4+ digits")
            }.setNegativeButton("Cancel", null).show()
    }

    override fun onActivityResult(requestCode:Int,resultCode:Int,data:Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
        if(resultCode != Activity.RESULT_OK || data?.data == null) return
        val uri = data.data!!
        try {
            when(requestCode) {
                REQ_BACKUP -> contentResolver.openOutputStream(uri)!!.use {
                    BackupManager.createEncryptedBackup(this, pendingBackupPin!!, it)
                    toast("Encrypted backup saved")
                }
                REQ_RESTORE -> {
                    val confirmed = android.app.AlertDialog.Builder(this)
                        .setTitle("Replace current data?")
                        .setMessage("Restore will replace the current local database.")
                        .setPositiveButton("Restore") { _, _ ->
                            try {
                                contentResolver.openInputStream(uri)!!.use {
                                    BackupManager.restoreEncryptedBackup(this, pendingRestorePin!!, it)
                                }
                                toast("Restore completed. Restart the app.")
                            } catch(e:Exception) { toast("Restore failed: ${e.message}") }
                        }.setNegativeButton("Cancel", null).create()
                    confirmed.show()
                }
            }
        } catch(e:Exception) { toast("Operation failed: ${e.message}") }
    }
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    companion object { const val REQ_BACKUP=2001; const val REQ_RESTORE=2002 }
}
