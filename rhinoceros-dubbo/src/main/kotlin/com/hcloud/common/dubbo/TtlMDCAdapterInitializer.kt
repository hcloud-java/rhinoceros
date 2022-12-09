package com.hcloud.common.dubbo

import org.slf4j.TtlMDCAdapter
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

class TtlMDCAdapterInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        TtlMDCAdapter.getInstance()
    }
}
