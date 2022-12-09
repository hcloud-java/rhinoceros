package org.slf4j

import com.alibaba.ttl.TransmittableThreadLocal
import java.util.*
import org.slf4j.spi.MDCAdapter

class TtlMDCAdapter : MDCAdapter {

    private val copyOnInheritThreadLocal: ThreadLocal<MutableMap<String, String?>> = TransmittableThreadLocal()
    private val lastOperation = ThreadLocal<Int>()

    companion object {
        private var mtcMDCAdapter: TtlMDCAdapter = TtlMDCAdapter()
        fun getInstance(): TtlMDCAdapter {
            MDC.mdcAdapter = mtcMDCAdapter
            return mtcMDCAdapter
        }

        private const val WRITE_OPERATION = 1
        private const val MAP_COPY_OPERATION = 2
    }

    private fun getAndSetLastOperation(op: Int): Int? {
        val lastOp = lastOperation.get()
        lastOperation.set(op)
        return lastOp
    }

    private fun wasLastOpReadOrNull(lastOp: Int?): Boolean {
        return lastOp == null || lastOp == MAP_COPY_OPERATION
    }

    private fun duplicateAndInsertNewMap(oldMap: Map<String, String?>?): MutableMap<String, String?> {
        val newMap = Collections.synchronizedMap(HashMap<String, String>())
        if (oldMap != null) {
            synchronized(oldMap) { newMap.putAll(oldMap) }
        }
        copyOnInheritThreadLocal.set(newMap)
        return newMap
    }

    override fun put(key: String?, `val`: String?) {
        requireNotNull(key) { "key cannot be null" }

        val oldMap = copyOnInheritThreadLocal.get()
        val lastOp = getAndSetLastOperation(WRITE_OPERATION)

        if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
            val newMap = duplicateAndInsertNewMap(oldMap)
            newMap[key] = `val`
        } else {
            oldMap[key] = `val`
        }
    }

    override fun get(key: String?): String {
        val map: Map<String, String?>? = copyOnInheritThreadLocal.get()
        return if (map != null && key != null) {
            map[key] ?: ""
        } else {
            ""
        }
    }

    override fun remove(key: String?) {
        if (key == null) {
            return
        }
        val oldMap = copyOnInheritThreadLocal.get() ?: return

        val lastOp = getAndSetLastOperation(WRITE_OPERATION)

        if (wasLastOpReadOrNull(lastOp)) {
            val newMap: MutableMap<String, String?> = duplicateAndInsertNewMap(oldMap)
            newMap.remove(key)
        } else {
            oldMap.remove(key)
        }
    }

    override fun clear() {
        lastOperation.set(WRITE_OPERATION)
        copyOnInheritThreadLocal.remove()
    }

    override fun getCopyOfContextMap(): MutableMap<String, String?> {
        return copyOnInheritThreadLocal.get()
    }

    override fun setContextMap(contextMap: MutableMap<String, String?>?) {
        var copy: MutableMap<String, String?>? = null
        if (contextMap != null) {
            copy = contextMap
        }
        copyOnInheritThreadLocal.set(copy)
    }
}
