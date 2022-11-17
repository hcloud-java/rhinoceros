package com.hcloud.prometheus.annotation

import java.lang.annotation.Inherited

/**
 *  you can see impl in [com.hcloud.prometheus.aop.TpAop]
 *  This indicator serves TP99 ...
 * */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class Tp(
    /**
     * It's description for the indicator that tell developer how to understand.
     * */
    val description: String = ""
)
