package com.hcloud.common.dubbo.support

import com.hcloud.common.dubbo.tracer.Tracer
import org.slf4j.MDC

abstract class AbstractInterceptor {

    fun tracId(): String = MDC.get(Tracer.TRACE_ID);
    fun spandId(): String = MDC.get(Tracer.SPAN_ID)
    fun parentId(): String = MDC.get(Tracer.PARENT_ID)
}
