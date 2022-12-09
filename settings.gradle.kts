import java.nio.file.Paths

rootProject.name = "rhinoceros"
rootProject.buildFileName = "build.gradle.kts"

val projectSymbol = "/src"
val projects = HashSet<String>()

settingsDir.listFiles()!!.filter { it.isDirectory }.stream().flatMap {
    java.nio.file.Files.walk(Paths.get(it.canonicalPath))
}.map { File(it.toUri()) }.filter { it.isDirectory && it.absolutePath.endsWith(projectSymbol) }.forEach {
    include(it.parentFile.name)
//    project(":${it.parentFile.name}").projectDir = File(it.parentFile.name)
}
