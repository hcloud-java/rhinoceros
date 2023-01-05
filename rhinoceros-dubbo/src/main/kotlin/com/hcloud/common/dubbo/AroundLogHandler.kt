package com.hcloud.common.dubbo

import com.hcloud.common.dubbo.annotation.LogMarker
import com.hcloud.common.dubbo.proxy.AroundLogProxyChain
import com.hcloud.common.dubbo.support.AbstractInterceptor
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AroundLogHandler : AbstractInterceptor() {

    fun around(chain: AroundLogProxyChain): Any {
        getRealLogger(chain)
        return ""
    }

    fun mdcLogMarkerParam(chain: AroundLogProxyChain) {
        var method = chain.getMethod()
        if (method.getAnnotation(LogMarker::class.java) != null) {
            if (StringUtils.isNotBlank(method.getAnnotation(LogMarker::class.java).businessDescription)) {
//                MDC.put(LogUtil)
            }
        }
    }

    private fun getRealLogger(chain: AroundLogProxyChain): Logger {
        return LoggerFactory.getLogger(chain.getClazz())
    }
}
