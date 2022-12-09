package com.hcloud.common.spi


@SPI("spi")
interface ExtensionFactory {

    /**
     * 获取扩展
     * @param key key
     * @param clazz Class
     * @param <T> type
     * @return extension
    </T> */
    fun <T> getExtension(key: String?, clazz: Class<T>?): T?
}