package com.hcloud.common.spi

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class SPI(val value: String = "")
