package com.hcloud.prometheus

import com.hcloud.prometheus.metrix.Matrix
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * base prometheus auto configuration
 * */
@Configuration
open class PrometheusAutoConfiguration {

    @Bean
    open fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return Matrix.initP()
    }
}