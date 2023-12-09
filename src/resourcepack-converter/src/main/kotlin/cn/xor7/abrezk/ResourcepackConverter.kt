package cn.xor7.abrezk

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

    @Suppress("unused")
    fun convert(inputFolder: String, outputFile: File) {
        loadFolder(inputFolder)
        convert0()
        saveToZipFile(outputFile)
    }

    @Suppress("unused")
    fun convert(inputFolder: String, outputFolder: String) {
        loadFolder(inputFolder)
        convert0()
        saveToFolder(outputFolder)
    }

    @Suppress("unused")
    fun convert(inputFile: File, outputFile: File) {
        loadZipFile(inputFile)
        convert0()
        saveToZipFile(outputFile)
    }

    @Suppress("unused")
    fun convert(inputFile: File, outputFolder: String) {
        loadZipFile(inputFile)
        convert0()
        saveToFolder(outputFolder)
    }

    private fun convert0(){
        cacheDir.walk().forEach { file ->
            when {
                file.absolutePath.startsWith("assets\\minecraft\\textures\\items\\") -> {
                    println(file.absolutePath)
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
        val input = File(folder)
        if (!input.exists()) {
            throw Exception("Input folder does not exist")
        }
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
        if(!File(outputFolder).exists()){
            File(outputFolder).mkdirs()
        }
        cacheDir.toPath().copyToRecursively(Paths.get(outputFolder), followLinks = true, overwrite = true)
    }
}