package com.hcloud.prometheus

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint
import org.springframework.context.annotation.Configuration

/**
 * base prometheus auto configuration
 * */
@Configuration
@EnablePrometheusEndpoint
open class PrometheusAutoConfiguration