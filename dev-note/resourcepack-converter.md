## MC资源包格式代码（pack_format）
* 1.6.1（13w24a）到1.8.9为`1`
* 1.9（15w31a）到1.10.2为`2`
* 1.11（16w32a）到1.12.2（17w47b）为`3`
* 1.13（17w48a）到1.14.4（19w46b）为`4`
* 1.15（1.15-pre1）到1.16.1（1.16.2-pre3）为`5`
* 1.16.2（1.16.2-rc1）到1.16.5为`6`
* 1.17（20w45a）到1.17.1（21w38a）为`7`
* 1.18（21w39a）到1.18.2为`8`
* 1.19（22w11a）到1.19.2为`9`
* 1.19.3快照22w42a到22w44a为`11`
* 1.19.3（22w45a）到1.19.4快照23w07a为`12`
* 1.19.4（1.19.4-pre1）到1.20快照23w13a为`13`
* 1.20快照23w14a到23w16a为`14`
* 1.20（23w17a）到1.20.1为`15`
* 1.20.2快照23w31a为`16`
* 1.20.2快照23w32a到1.20.2-pre1为`17`
* 1.20.2（1.20.2-pre2）到1.20.3快照23w41a为`18`
* 1.20.3（23w42a）及以上为`19`

其中`11` `16` `17` `18` `19` `20` 仅在快照版本中使用。

## TODO List
* 实现材质转换
* 实现GUI转换
* 实现模型转换

## 材质转换
**我将从1.8.9开始支持，1.8.8-的所有版本我不会支持。**

**MC在1.11开始强制要求材质文件必须全小写，所以我将会首先把所有材质文件名转换为小写。**

### 1.13(扁平化)

查阅wiki得知，第一次材质文件名变动是在1.13(扁平化)，所以我们首先要解决这个问题。

~~_扁平化怎么改了这么多啊啊啊啊啊_~~

~~我准备提供一个表，我将会在这个表中手动录入所有的新旧材质文件名映射~~

录了几行我发现Copilot的补全似乎过于强大了，所以我开始去GitHub上寻觅...

然后我找到了[simple-name-converter](https://github.com/kotmatross28729/simple-name-converter)这个项目，它提供了一个`names.txt`文件，里面包含了所有的新旧材质文件名映射。

我修改格式后，将其放入了`src/resourcepack-converter/src/main/kotlin/cn/xor7/abrezk/FlatteningMap.kt`中。

代码中`init`块的中的`map`键值，每一行表示一组映射，格式为`新材质名 旧材质名`，中间用空格隔开，不包含后缀名(全都是png)。

当然，如果材质名没变动，表中自然是没有的。

### 1.15

wiki中提到，1.15中旗帜图案、盾牌、附魔光效与箱子等贴图机制进行了改动。但我并没有在wiki中找到相关描述。

TODO: 对比1.14.4与1.15的材质，找出具体变动

### 1.16.2(MC-197275)

材质包格式变动是因为[MC-197275](https://bugs.mojang.com/browse/MC-197275)。

该问题只在**修改了围墙的方块状态**时才会出现，这里暂不解决。

### 1.17

部分材质名称发生变动。

TODO: 对比1.16和1.17的材质，找出具体变动

### 1.19.4

附魔光效材质拆分。

现在使用两个不同的纹理文件：`enchanted_glint_entity.png`和`enchanted_glint_item.png`

**好像差不多了，大抵就这些变动了，开写。**