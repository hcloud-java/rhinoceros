package org.slf4j

import com.alibaba.ttl.TransmittableThreadLocal
import org.slf4j.spi.MDCAdapter
import java.util.*

class TtlMDCAdapter : MDCAdapter{

    private val copyOnInheritThreadLocal: ThreadLocal<Map<String, String>> = TransmittableThreadLocal()
    override fun put(key: String?, `val`: String?) {
        TODO("Not yet implemented")
    }

    override fun get(key: String?): String {
        TODO("Not yet implemented")
    }

    override fun remove(key: String?) {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun getCopyOfContextMap(): MutableMap<String, String> {
        TODO("Not yet implemented")
    }

    override fun setContextMap(contextMap: MutableMap<String, String>?) {
        TODO("Not yet implemented")
    }

    override fun pushByKey(key: String?, value: String?) {
        TODO("Not yet implemented")
    }

    override fun popByKey(key: String?): String {
        TODO("Not yet implemented")
    }

    override fun getCopyOfDequeByKey(key: String?): Deque<String> {
        TODO("Not yet implemented")
    }

    override fun clearDequeByKey(key: String?) {
        TODO("Not yet implemented")
    }
}