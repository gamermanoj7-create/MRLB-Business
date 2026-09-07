package com.mrlb.business.utils

import android.content.Context
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object BackupManager {
    private val MAGIC = "MRLB1".toByteArray(Charsets.US_ASCII)
    private const val SALT_LEN = 16
    private const val IV_LEN = 12
    private const val TAG_BITS = 128

    fun createEncryptedBackup(context: Context, pin: String, out: OutputStream) {
        val db = context.getDatabasePath("mrlb_business.db")
        require(db.exists()) { "Database does not exist" }
        val salt = SecurityUtils.randomBytes(SALT_LEN)
        val iv = SecurityUtils.randomBytes(IV_LEN)
        val key = SecurityUtils.deriveKey(pin, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(TAG_BITS, iv))
        val encrypted = cipher.doFinal(db.readBytes())
        out.write(MAGIC)
        out.write(byteArrayOf(SALT_LEN.toByte(), IV_LEN.toByte()))
        out.write(salt)
        out.write(iv)
        out.write(ByteBuffer.allocate(4).putInt(encrypted.size).array())
        out.write(encrypted)
        out.flush()
    }

    fun restoreEncryptedBackup(context: Context, pin: String, input: InputStream) {
        val all = input.readBytes()
        require(all.size > MAGIC.size + 2 + SALT_LEN + IV_LEN + 4) { "Invalid backup" }
        require(all.copyOfRange(0, MAGIC.size).contentEquals(MAGIC)) { "Invalid MRLB backup file" }
        var pos = MAGIC.size
        val saltLen = all[pos++].toInt()
        val ivLen = all[pos++].toInt()
        require(saltLen == SALT_LEN && ivLen == IV_LEN) { "Unsupported backup format" }
        val salt = all.copyOfRange(pos, pos + saltLen); pos += saltLen
        val iv = all.copyOfRange(pos, pos + ivLen); pos += ivLen
        val size = ByteBuffer.wrap(all, pos, 4).int; pos += 4
        require(size > 0 && pos + size == all.size) { "Corrupt backup" }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(SecurityUtils.deriveKey(pin, salt), "AES"),
            GCMParameterSpec(TAG_BITS, iv))
        val plain = cipher.doFinal(all.copyOfRange(pos, all.size))

        val db = context.getDatabasePath("mrlb_business.db")
        val tmp = File(context.cacheDir, "mrlb_restore.tmp")
        tmp.writeBytes(plain)
        require(tmp.length() > 0) { "Empty database backup" }
        context.closeDatabase()
        if (db.exists()) db.delete()
        tmp.copyTo(db, overwrite=true)
        tmp.delete()
    }

    private fun Context.closeDatabase() {
        // Database singleton is closed by AppDatabase when the process is restarted.
    }
}
