package com.hcloud.common.spi

import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

@Suppress("UNCHECKED_CAST")
class ExtensionLoader<T> {
    private var log = LoggerFactory.getLogger(ExtensionLoader::class.java)

    /**
     * SPI配置扩展的文件位置
     * 扩展文件命名格式为 SPI接口的全路径名，如：com.hcloud.spi.test.TestSPI
     */
    private val DEFAULT_DIRECTORY = "META-INF/log-helper/"

    /**
     * 扩展接口 [Class]
     */
    private val tClass: Class<T>

    /**
     * 保存 "扩展" 实现的 [Class]
     */
    private val cachedClasses = Holder<Map<String?, Class<*>>?>()

    /**
     * "扩展名" 对应的 保存扩展对象的Holder的缓存
     */
    private val cachedInstances: MutableMap<String?, Holder<T>> = ConcurrentHashMap()

    /**
     * 扩展class 和 扩展点的实现对象的缓存
     */
    private val joinInstances: MutableMap<Class<*>, Any> = ConcurrentHashMap()

    /**
     * 扩展点默认的 "名称" 缓存
     */
    private var cacheDefaultName: String? = null

    constructor(tClass: Class<T>) {
        this.tClass = tClass
        if (tClass != ExtensionFactory::class.java) {
            getExtensionLoader(ExtensionFactory::class.java).getExtensionClasses()
        }
    }

    fun getDefaultJoin(): T? {
        getExtensionClasses()
        if (StringUtils.isNotBlank(cacheDefaultName)) {
            return getJoin(cacheDefaultName)
        }
        return null
    }

    fun getJoin(cacheDefaultName: String?): T? {
        // 扩展名 文件中的key
        if (StringUtils.isBlank(cacheDefaultName)) {
            throw IllegalArgumentException("join name is null")
        }
        // 扩展对象存储缓存
        var objectHolder = cachedInstances[cacheDefaultName]
        // 如果扩展对象的存储是空的，创建一个扩展对象存储并缓存
        if (null == objectHolder) {
            cachedInstances.putIfAbsent(cacheDefaultName, Holder())
            objectHolder = cachedInstances[cacheDefaultName]
        }
        // 从扩展对象的存储中获取扩展对象
        var value = objectHolder!!.t
        // 如果对象是空的，就触发创建扩展，否则直接返回扩展对象
        if (null == value) {
            synchronized((cacheDefaultName)!!) {
                value = objectHolder.t
                if (null == value) {
                    // 创建扩展对象
                    value = createExtension(cacheDefaultName)
                    value?.let { objectHolder.setT(it) }
                }
            }
        }
        return value
    }

    private fun createExtension(cacheDefaultName: String?): T? {
        // 根据扩展名字获取扩展的Class，从Holder中获取 key-value缓存，然后根据名字从Map中获取扩展实现Class
        val aClass =
            getExtensionClasses()!![cacheDefaultName] ?: throw IllegalArgumentException("extension class is null")
        var o = joinInstances[aClass]
        if (null == o) {
            try {
                // 创建扩展对象并放到缓存中
                joinInstances.putIfAbsent(aClass, aClass.getDeclaredConstructor().newInstance())
                o = joinInstances[aClass]
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return o as? T
    }

    companion object {
        /**
         * 扩展接口 和 扩展加载器 [ExtensionLoader] 的缓存
         */
        private val MAP: MutableMap<Class<*>, ExtensionLoader<*>> = ConcurrentHashMap()

        fun <T> getExtensionLoader(tClass: Class<T>?): ExtensionLoader<T> {
            // 参数非空校验
            if (null == tClass) {
                throw NullPointerException("tClass is null !")
            }
            // 参数应该是接口
            if (!tClass.isInterface) {
                throw IllegalArgumentException("tClass :$tClass is not interface !")
            }
            // 参数要包含@SPI注解
            if (!tClass.isAnnotationPresent(SPI::class.java)) {
                throw IllegalArgumentException("tClass " + tClass + "without @" + SPI::class.java + " Annotation !")
            }
            // 从缓存中获取扩展加载器，如果存在直接返回，如果不存在就创建一个扩展加载器并放到缓存中
            val extensionLoader: ExtensionLoader<T>? = MAP[tClass] as ExtensionLoader<T>?
            if (null != extensionLoader) {
                return extensionLoader
            }
            MAP.putIfAbsent(tClass, ExtensionLoader(tClass))
            return MAP[tClass] as ExtensionLoader<T>
        }
    }

    private fun getExtensionClasses(): Map<String?, Class<*>?>? {
        // 扩区SPI扩展实现的缓存，对应的就是扩展文件中的 key - value
        var classes = cachedClasses.t
        if (null == classes) {
            synchronized(cachedClasses) {
                classes = cachedClasses.t
                if (null == classes) {
                    // 加载扩展
                    classes = loadExtensionClass()
                    // 缓存扩展实现集合
                    cachedClasses.setT(classes)
                }
            }
        }
        return classes
    }

    private fun loadExtensionClass(): Map<String?, Class<*>>? {
        // 扩展接口tClass，必须包含SPI注解
        val annotation = tClass.getAnnotation(SPI::class.java)
        if (null != annotation) {
            val v = annotation.value
            if (StringUtils.isNotBlank(v)) {
                // 如果有默认的扩展实现名，用默认的
                cacheDefaultName = v
            }
        }
        val classes: MutableMap<String?, Class<*>> = HashMap(16)
        // 从文件加载
        loadDirectory(classes)
        return classes
    }

    private fun loadDirectory(classes: MutableMap<String?, Class<*>>) {
        // 文件名
        val fileName = DEFAULT_DIRECTORY + tClass.name
        try {
            val classLoader = ExtensionLoader::class.java.classLoader
            // 读取配置文件
            val urls: Enumeration<URL>? =
                if (classLoader != null) classLoader.getResources(fileName) else ClassLoader.getSystemResources(fileName)
            if (urls != null) {
                // 获取所有的配置文件
                while (urls.hasMoreElements()) {
                    val url: URL = urls.nextElement()
                    // 加载资源
                    loadResources(classes, url)
                }
            }
        } catch (e: IOException) {
            log.error("load directory error {}", fileName, e)
        }
    }

    private fun loadResources(classes: MutableMap<String?, Class<*>>, url: URL) {
        try {
            url.openStream().use { inputStream ->
                val properties = Properties()
                properties.load(inputStream)
                properties.forEach { k, v ->
                    // 扩展实现的名字
                    val name: String = k as? String ?: ""
                    // 扩展实现的Class的全路径
                    val classPath: String = v as? String ?: ""
                    if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(classPath)) {
                        try {
                            // 加载扩展实现Class，就是想其缓存起来，缓存到集合中
                            loadClass(classes, name, classPath)
                        } catch (e: ClassNotFoundException) {
                            log.error("load class not found", e)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            log.error("load resouces error", e)
        }
    }

    @Throws(ClassNotFoundException::class)
    private fun loadClass(classes: MutableMap<String?, Class<*>>, name: String, classPath: String) {
        // 反射创建扩展实现的Class
        val subClass = Class.forName(classPath)
        // 扩展实现的Class要是扩展接口的实现类
        if (!tClass.isAssignableFrom(subClass)) {
            throw IllegalArgumentException("load extension class error $subClass not sub type of $tClass")
        }
        // 扩展实现要有Join注解
        subClass.getAnnotation(Join::class.java) ?: throw IllegalArgumentException(
            ("load extension class error $subClass without @JoinAnnotation")
        )
        // 缓存扩展实现Class
        val oldClass = classes[name]
        if (oldClass == null) {
            classes[name] = subClass
        } else if (oldClass != subClass) {
            log.error("load extension class error, Duplicate class oldClass is " + oldClass + "subClass is" + subClass)
        }
    }

    class Holder<T>() {
        @Volatile
        var t: T? = null
            private set

        fun setT(t: T) {
            this.t = t
        }
    }
}
