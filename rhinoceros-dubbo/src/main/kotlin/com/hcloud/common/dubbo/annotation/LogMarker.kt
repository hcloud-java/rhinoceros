package com.hcloud.common.dubbo.annotation

import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@Inherited
@MustBeDocumented
annotation class LogMarker()
