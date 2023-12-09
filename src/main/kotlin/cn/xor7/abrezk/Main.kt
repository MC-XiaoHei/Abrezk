package cn.xor7.abrezk

fun main(args: Array<String>) {
    // ResourcepackConverter.convert("C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2","C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2-convert")
    // println(ResourcepackConverter.createCacheDir().absolutePath)
    ResourcepackConverter.create().convert(
        "C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2",
        "C:\\Users\\33918\\Desktop\\Abrezk Development\\assets-1.12.2-convert"
    )
}