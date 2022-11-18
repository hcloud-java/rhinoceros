package com.hcloud.common.oss

import com.hcloud.common.oss.http.OssEndpoint
import com.hcloud.common.oss.service.OssTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@EnableConfigurationProperties(OssProperties::class)
open class OssAutoConfiguration(private val properties: OssProperties) {

    @Bean
    @ConditionalOnMissingBean(OssTemplate::class)
    @ConditionalOnProperty(name = ["oss.enable"], havingValue = "true", matchIfMissing = true)
    open fun ossTemplate(): OssTemplate {
        return OssTemplate(properties)
    }

    @Bean
    @ConditionalOnProperty(name = ["oss.info"], havingValue = "true")
    open fun ossEndpoint(template: OssTemplate): OssEndpoint {
        return OssEndpoint(template)
    }

}