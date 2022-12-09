package com.hcloud.common.prometheus.metrix

import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import java.time.Duration
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer

object Matrix {

    fun initP(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer {
            it.config().meterFilter(object : MeterFilter {
                override fun configure(
                    id: Meter.Id,
                    config: DistributionStatisticConfig
                ): DistributionStatisticConfig {
                    return if (id.type == Meter.Type.TIMER && id.name.matches(Regex("^(http|hystrix).*"))) {
                        DistributionStatisticConfig.builder()
                            .percentilesHistogram(true)
                            .percentiles(0.5, 0.90, 0.95, 0.99)
                            .serviceLevelObjectives(
                                Duration.ofMillis(50).toNanos().toDouble(),
                                Duration.ofMillis(100).toNanos().toDouble(),
                                Duration.ofMillis(200).toNanos().toDouble(),
                                Duration.ofSeconds(1).toNanos().toDouble(),
                                Duration.ofSeconds(5).toNanos().toDouble()
                            )
                            .minimumExpectedValue(Duration.ofMillis(1).toNanos().toDouble())
                            .maximumExpectedValue(Duration.ofSeconds(5).toNanos().toDouble())
                            .build()
                            .merge(config)
                    } else {
                        config
                    }
                }
            })
        }
    }
}
