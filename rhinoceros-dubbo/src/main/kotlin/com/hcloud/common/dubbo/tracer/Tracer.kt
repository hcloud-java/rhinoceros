package com.hcloud.common.dubbo.tracer

import com.alibaba.ttl.TransmittableThreadLocal
import java.util.*
import org.apache.commons.lang3.StringUtils
import org.apache.skywalking.apm.toolkit.trace.Trace
import org.apache.skywalking.apm.toolkit.trace.TraceContext
import org.slf4j.MDC

class Tracer(private val traceId: String?, private val spanId: Int, private val parentId: Int) {
    companion object {
        var traceThreadLocal = TransmittableThreadLocal<Tracer>()

        const val TRACE_ID = "traceId"

        const val SPAN_ID = "spanId"

        const val PARENT_ID = "parentId"

        const val SKYWALKING_NO_ID = "[Ignored Trace]"

        fun trace(traceId: String, spanId: String, parentId: String) {
            var tracer = traceThreadLocal.get()
            if (null == tracer) {
                tracer = TracerBuilder().traceId(traceId).spanId(spanId.toInt())
                    .parentId(parentId.toInt()).build()
                tracer.buildSpan()
            } else {
                traceThreadLocal.remove()
            }
        }

        class TracerBuilder {
            private var traceId: String? = null
            private var spanId: Int = 0
            private var parentId: Int = 0
            fun traceId(traceId: String?): TracerBuilder {
                this.traceId = traceId
                return this
            }

            fun spanId(spanId: Int): TracerBuilder {
                this.spanId = spanId
                return this
            }

            fun parentId(parentId: Int): TracerBuilder {
                this.parentId = parentId
                return this
            }

            fun build(): Tracer {
                return Tracer(traceId, spanId, parentId)
            }
        }
    }

    init {
        traceThreadLocal.set(this)
    }

    @Trace
    fun buildSpan() {
        if (null == traceId) {
            if (StringUtils.isNotBlank(TraceContext.traceId()) && SKYWALKING_NO_ID != TraceContext.traceId()) {
                TraceContext.traceId()
            } else {
                UUID.randomUUID().toString()
            }
        }
        val parentId = parentId + 1
        val spanId = spanId + 1
        MDC.put(TRACE_ID, traceId)
        MDC.put(SPAN_ID, spanId.toString() + "")
        MDC.put(PARENT_ID, parentId.toString() + "")
    }
}
