package com.hcloud.common.spi

@Join
open class SpiExtensionFactory : ExtensionFactory {
    override fun <T> getExtension(key: String?, clazz: Class<T>?): T? {
        if (clazz!!.isAnnotationPresent(SPI::class.java) && clazz.isInterface) {
            val extensionLoader = ExtensionLoader.getExtensionLoader(clazz)
            return extensionLoader.getDefaultJoin()
        }
        return null
    }
}
