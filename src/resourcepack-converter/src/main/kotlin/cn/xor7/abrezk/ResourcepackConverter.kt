package cn.xor7.abrezk

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively

class ResourcepackConverter private constructor() {
    private val cacheDir = Files.createTempDirectory("abrezk-resourcepack-converter").toFile()

    companion object {
        fun create(): ResourcepackConverter {
            return ResourcepackConverter()
        }
    }

    init {
        cacheDir.deleteOnExit()
    }

    fun convert(inputFolder: String, outputFile: File) {
        convert(inputFolder, cacheDir.absolutePath)
        saveToZipFile(outputFile)
    }

    fun convert(inputFolder: String, outputFolder: String) {
        val input = File(inputFolder)
        val output = File(outputFolder)
        if (!input.exists()) {
            throw Exception("Input folder does not exist")
        }
        input.walk().forEach { file ->
            when {
                file.absolutePath.startsWith("assets\\minecraft\\textures\\items\\") -> {
                    println(file.absolutePath)
                }
            }
        }
        if (!output.exists()) {
            output.mkdirs()
        }
    }

    fun convert(inputFile: File, outputFile: File) {
        loadZipFile(inputFile)
        convert(cacheDir.absolutePath, "${cacheDir.absolutePath}-convert")
    }

    fun convert(inputFile: File, outputFolder: String) {
        loadZipFile(inputFile)
        convert(cacheDir.absolutePath, outputFolder)
    }

    private fun convert0(input: ZipInputStream): ByteArrayOutputStream {
        input.use { zipInputStream ->
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                ZipOutputStream(byteArrayOutputStream).use { zipOutputStream ->
                    zipOutputStream.setMethod(ZipOutputStream.STORED)
                    var zipEntry: ZipEntry? = zipInputStream.nextEntry
                    while (zipEntry != null) {
                        val entryFilePath = zipEntry.name
                        when {
                            entryFilePath.startsWith("minecraft\\textures\\items\\") -> {
                                println(entryFilePath)
                            }
                        }

                        zipInputStream.closeEntry()
                        zipEntry = zipInputStream.nextEntry
                    }
                    return byteArrayOutputStream
                }
            }
        }
    }

    private fun loadZipFile(inputFile: File) {
        val buffer = ByteArray(1024)
        ZipInputStream(FileInputStream(inputFile)).use { zipInputStream ->
            var zipEntry: ZipEntry? = zipInputStream.nextEntry
            while (zipEntry != null) {
                val entryFilePath = cacheDir.absolutePath + File.separator + zipEntry.name

                if (zipEntry.isDirectory) {
                    File(entryFilePath).mkdirs()
                } else {
                    FileOutputStream(entryFilePath).use { outputStream ->
                        var len: Int
                        while (zipInputStream.read(buffer).also { len = it } > 0) {
                            outputStream.write(buffer, 0, len)
                        }
                    }
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun loadFolder(folder: String) {
        Paths.get(folder).copyToRecursively(cacheDir.toPath(), followLinks = true, overwrite = true)
    }

    private fun saveToZipFile(outputFile: File) {
        val buffer = ByteArray(1024)
        ZipOutputStream(FileOutputStream(outputFile)).use { zipOutputStream ->
            cacheDir.walk().forEach { file ->
                if (file.isFile) {
                    val zipEntry = ZipEntry(file.absolutePath.substring(cacheDir.absolutePath.length + 1))
                    zipOutputStream.putNextEntry(zipEntry)
                    FileInputStream(file).use { inputStream ->
                        var len: Int
                        while (inputStream.read(buffer).also { len = it } > 0) {
                            zipOutputStream.write(buffer, 0, len)
                        }
                    }
                    zipOutputStream.closeEntry()
                }
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    private fun saveToFolder(outputFolder: String) {
        cacheDir.toPath().copyToRecursively(Paths.get(outputFolder), followLinks = true, overwrite = true)
    }
}