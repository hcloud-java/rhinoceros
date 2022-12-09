package com.hcloud.common.dubbo.annotation

import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Inherited
@MustBeDocumented
annotation class  FieldIgnore()
