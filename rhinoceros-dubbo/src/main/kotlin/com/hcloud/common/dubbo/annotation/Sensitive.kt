package com.hcloud.common.dubbo.annotation

import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Inherited
@MustBeDocumented
annotation class Sensitive(val start: Int = Int.MIN_VALUE, val end: Int = Int.MAX_VALUE)
