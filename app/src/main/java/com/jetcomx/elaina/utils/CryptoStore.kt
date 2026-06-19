package com.jetcomx.elaina.utils

import android.content.Context
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoStore {
    private const val KEY_ALIAS = "aidsfgdifbsu"
    private const val FILE_NAME = "aidsfgdifbsu"
    private const val GCM_TAG_LENGTH = 128

    @Volatile
    private var verified: Boolean? = null
    private var file: File? = null

    fun init(context: Context) {
        if (file == null) {
            synchronized(this) {
                if (file == null) {
                    file = File(context.filesDir, FILE_NAME)
                    getOrCreateKey()
                }
            }
        }
    }

    fun getAidsfgdifbsu(): Boolean {
        verified?.let { return it }
        val f = file ?: return false
        val value = readEncrypted(f)
        verified = value
        return value
    }

    fun setAidsfgdifbsu(value: Boolean) {
        verified = value
        val f = file ?: return
        writeEncrypted(f, value)
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        keyStore.getEntry(KEY_ALIAS, null)?.let {
            return (it as KeyStore.SecretKeyEntry).secretKey
        }
        val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
        keyGenerator.init(
            android.security.keystore.KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or
                        android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private fun readEncrypted(f: File): Boolean {
        if (!f.exists()) return false
        try {
            val data = f.readBytes()
            val dis = DataInputStream(ByteArrayInputStream(data))
            val ivSize = dis.readInt()
            val iv = ByteArray(ivSize).also { dis.readFully(it) }
            val ctSize = dis.readInt()
            val ct = ByteArray(ctSize).also { dis.readFully(it) }
            dis.close()

            val key = getOrCreateKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
            val plain = cipher.doFinal(ct)
            return plain[0] == 1.toByte()
        } catch (_: Exception) {
            return false
        }
    }

    private fun writeEncrypted(f: File, value: Boolean) {
        try {
            val key = getOrCreateKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val ct = cipher.doFinal(byteArrayOf(if (value) 1 else 0))
            val iv = cipher.iv

            val baos = ByteArrayOutputStream()
            val dos = DataOutputStream(baos)
            dos.writeInt(iv.size)
            dos.write(iv)
            dos.writeInt(ct.size)
            dos.write(ct)
            dos.close()

            f.writeBytes(baos.toByteArray())
        } catch (_: Exception) { }
    }
}
