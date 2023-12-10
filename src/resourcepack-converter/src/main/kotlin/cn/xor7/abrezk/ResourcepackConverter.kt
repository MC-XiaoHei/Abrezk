package cn.xor7.abrezk

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
import kotlin.properties.Delegates

class ResourcepackConverter(
    private val destFormat: Int,
    private val cacheDir: File = Files.createTempDirectory("abrezk-resourcepack-converter").toFile(),
    deleteCacheDirOnExit: Boolean = true,
) {
    private val suffix = ".png"
    private var srcFormat by Delegates.notNull<Int>()

    init {
        if (deleteCacheDirOnExit) {
            cacheDir.deleteOnExit()
        }
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

    // 你可以在创建对象时指定deleteCacheDirOnExit参数为false，然后直接调用此函数以避免复制文件带来的io开销
    // You can specify the deleteCacheDirOnExit parameter as false when creating the object, and then call this function directly to avoid the io overhead of copying files.
    @Suppress("MemberVisibilityCanBePrivate")
    fun convert0() {
        println("convert start. destFormat: $destFormat")
        parsePackMcmeta()
        when {
            srcFormat < destFormat -> {
                for (i in srcFormat..<destFormat) {
                    when (i) {
                        3 -> flattening()
                    }
                }
            }

            srcFormat > destFormat -> {
                for (i in srcFormat downTo destFormat + 1) {
                    when (i) {
                        2 -> antiFlattening()
                    }
                }
            }

            else -> return
        }
    }

    private fun parsePackMcmeta() {
        println("parse pack.mcmeta")
        val packMcmetaFile = File(cacheDir, "pack.mcmeta")
        if (!packMcmetaFile.exists()) {
            throw IllegalResourcepackException("pack.mcmeta not found")
        }
        val packMcmeta = Json.decodeFromString<PackMeta>(packMcmetaFile.readText())
        if (packMcmeta.pack_format == destFormat) return
        packMcmeta.pack_format = destFormat
        srcFormat = packMcmeta.pack_format
        packMcmetaFile.writeText(Json.encodeToString(packMcmeta))
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
        if (!File(outputFolder).exists()) {
            File(outputFolder).mkdirs()
        }
        cacheDir.toPath().copyToRecursively(Paths.get(outputFolder), followLinks = true, overwrite = true)
    }

    private fun flattening() {
        cacheDir.walk().forEach { file ->
            if (!file.isFile || !file.name.endsWith(suffix)) return@forEach
            val flatteningName = FlatteningMap.toFlatName(file.name.removeSuffix(suffix)).plus(suffix)
            if (flatteningName == file.name) return@forEach
            println("flattening: rename ${file.name} to $flatteningName")
            file.renameTo(File(file.absolutePath.replace(file.name, flatteningName)))
        }
    }

    private fun antiFlattening() {
        cacheDir.walk().forEach { file ->
            if (!file.isFile || !file.name.endsWith(suffix)) return@forEach
            val antiFlatteningName = FlatteningMap.fromFlatName(file.name.removeSuffix(suffix)).plus(suffix)
            if (antiFlatteningName == file.name) return@forEach
            println("anti flattening: rename ${file.name} to $antiFlatteningName")
            file.renameTo(File(file.absolutePath.replace(file.name, antiFlatteningName)))
        }
    }
}