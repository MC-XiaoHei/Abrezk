package cn.xor7.abrezk

import cn.xor7.abrezk.resourcepack.convertor.ResourcepackConverter

fun main(args: Array<String>) {
    // ResourcepackConverter.convert("C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2","C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2-convert")
    // println(ResourcepackConverter.createCacheDir().absolutePath)
    ResourcepackConverter(destFormat = 19).convert(
        "C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2",
        "C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2-convert"
    )
}