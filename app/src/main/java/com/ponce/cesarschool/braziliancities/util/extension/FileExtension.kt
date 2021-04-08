package com.ponce.cesarschool.braziliancities.util.extension

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun File.unzip() :Boolean{
    val zis = ZipInputStream(BufferedInputStream(FileInputStream(this)))
    try {
        var ze: ZipEntry
        var count: Int
        val buffer = ByteArray(8192)
        while (zis.getNextEntry().also { ze = it } != null) {
            val file: File = File("${this.parent}/${this.nameWithoutExtension}", ze.getName())
            val dir = if (ze.isDirectory()) file else file.parentFile
            if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException(
                "Failed to ensure directory: " +
                        dir.absolutePath
            )
            if (ze.isDirectory()) continue
            val fout = FileOutputStream(file)
            try {
                while (zis.read(buffer).also { count = it } != -1) fout.write(buffer, 0, count)
            } finally {
                fout.close()
            }
        }
    } catch(e : IOException)
    {
        e.printStackTrace()
        return false
    }finally {
        zis.close()
        return true
    }
}