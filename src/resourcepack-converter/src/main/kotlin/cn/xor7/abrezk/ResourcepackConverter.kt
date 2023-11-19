package cn.xor7.abrezk

import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object ResourcepackConverter {
    fun convert(input: ZipInputStream): ZipOutputStream {
        input.use { inputSteam ->
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                ZipOutputStream(byteArrayOutputStream).use { zipOutputStream ->
                    println("笑死，啥也没做呢")
                    return zipOutputStream
                }
            }
        }
    }

    fun convert(inputFolder: Path): ZipOutputStream {
        try {
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                ZipOutputStream(byteArrayOutputStream).use { zipOutputStream ->
                    zipOutputStream.setMethod(ZipOutputStream.STORED)
                    Files.walk(inputFolder)
                        .filter { Files.isRegularFile(it) }
                        .forEach { filePath ->
                            try {
                                val entryName = "${inputFolder.toAbsolutePath()}${File.separator}${filePath.fileName}"
                                val entry = ZipEntry(entryName)
                                zipOutputStream.putNextEntry(entry)

                                val buffer = ByteArray(1024)
                                FileInputStream(filePath.toFile()).use { fileInputStream ->
                                    var bytesRead: Int
                                    while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
                                        zipOutputStream.write(buffer, 0, bytesRead)
                                    }
                                }

                                zipOutputStream.closeEntry()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }

                    byteArrayOutputStream.close()
                    zipOutputStream.close()
                    return convert(ZipInputStream(ByteArrayInputStream(byteArrayOutputStream.toByteArray())))
                }
            }
        } catch (e: IOException) {
            throw e
        }
    }
}