package com.hcloud.common.dubbo.proxy

import java.lang.reflect.Method
import java.security.Signature
import kotlin.jvm.Throws

interface AroundLogProxyChain {
    fun parameter(): Map<String, List<Any>>

    fun getArgs(): Array<Any>

    fun getTarget(): Any

    fun getMethod(): Method

    fun getClazz(): Class<*>

    @Throws(Throwable::class)
    fun getProceed(): Any

    fun getSignature(): Signature

    @Throws(Throwable::class)
    fun doProxyChain(arguments: Array<Any>): Any
}
