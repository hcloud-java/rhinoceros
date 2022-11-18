package com.hcloud.common.oss

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oss")
data class OssProperties(
    /**
     *  对象存储服务的URL
     * */
    val endpoint: String,
    /**
     * 自定义域名
     * */
    var customDomain: String,
    /**
     * nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname} false supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style模式{http://bucketname.endpoint}
     **/
    var pathStyleAccess: Boolean = true,
    /**
     * 应用ID
     * */
    var appId: String,
    /**
     * 区域
     * */
    var region: String,
    /**
     * 用户ID
     * */
    var accessKey: String,
    /**
     * 密码
     * */
    var secretKey: String,
    /**
     * 桶名
     * */
    var bucketName: String,
    /**
     * 最大线程数
     * */
    var maxConnections: Int = 100
)
