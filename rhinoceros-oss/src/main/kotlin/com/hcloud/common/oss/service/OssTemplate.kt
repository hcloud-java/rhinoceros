package com.hcloud.common.oss.service

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.*
import com.amazonaws.util.IOUtils
import com.hcloud.common.oss.OssProperties
import org.springframework.beans.factory.InitializingBean
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.*
import java.util.stream.Collectors


class OssTemplate(private val ossProperties: OssProperties) : InitializingBean {

    private var amazonS3: AmazonS3? = null

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    fun createBucket(bucketName: String?) {
        if (!amazonS3?.doesBucketExistV2(bucketName)!! ?: return) {
            amazonS3?.createBucket(bucketName)
        }
    }

    fun getAllBuckets(): List<Bucket>? {
        return amazonS3?.listBuckets()
    }

    fun getBucket(bucketName: String?): Optional<Bucket>? {
        return amazonS3?.listBuckets()?.stream()?.filter { it.name.equals(bucketName) }?.findFirst()
    }

    fun removeBucket(bucketName: String?) {
        amazonS3?.deleteBucket(bucketName)
    }

    /**
     * 根据文件前置查询文件
     *
     * @param bucketName bucket名称
     * @param prefix     前缀
     * @param recursive  是否递归查询
     * @return S3ObjectSummary 列表
     * @see [AWS
     * API Documentation](http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/ListObjects)
     */
    fun getAllObjectsByPrefix(bucketName: String?, prefix: String?, recursive: Boolean): List<S3ObjectSummary>? {
        val listObjects = amazonS3?.listObjects(bucketName, prefix)
        return listObjects?.objectSummaries
    }

    /**
     * 获取文件外链
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param expires    过期时间
     * @return url
     * @see AmazonS3.generatePresignedUrl
     */
    fun getObjectURL(bucketName: String?, objectName: String?, expires: Int?): String {
        val date = Date()
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, expires!!)
        val url = amazonS3?.generatePresignedUrl(bucketName, objectName, calendar.time)
        return url.toString()
    }

    /**
     * 获取文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @return 二进制流
     * @see [AWS
     * API Documentation](http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject)
     */
    fun getObject(bucketName: String?, objectName: String?): S3Object? {
        return amazonS3?.getObject(bucketName, objectName)
    }

    /**
     * 上传文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @param stream     文件流
     * @throws Exception
     */
    @Throws(Exception::class)
    fun putObject(bucketName: String?, objectName: String?, stream: InputStream) {
        putObject(bucketName, objectName, stream, stream.available().toLong(), "application/octet-stream")
    }

    /**
     * 上传文件
     *
     * @param bucketName  bucket名称
     * @param objectName  文件名称
     * @param stream      文件流
     * @param size        大小
     * @param contextType 类型
     * @throws Exception
     * @see [AWS
     * API Documentation](http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/PutObject)
     */
    @Throws(Exception::class)
    fun putObject(
        bucketName: String?, objectName: String?, stream: InputStream?, size: Long,
        contextType: String?
    ): PutObjectResult? {
        val objectMetadata = ObjectMetadata()
        objectMetadata.contentLength = size
        objectMetadata.contentType = contextType
        var byteArrayInputStream: ByteArrayInputStream? = null
        return try {
            val bytes: ByteArray = IOUtils.toByteArray(stream)
            byteArrayInputStream = ByteArrayInputStream(bytes)
            // 上传
            amazonS3?.putObject(bucketName, objectName, byteArrayInputStream, objectMetadata)
        } finally {
            byteArrayInputStream?.close()
        }
    }

    /**
     * 获取文件信息
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception
     * @see [AWS
     * API Documentation](http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/GetObject)
     */
    @Throws(Exception::class)
    fun getObjectInfo(bucketName: String?, objectName: String?): S3Object? {
        return amazonS3?.getObject(bucketName, objectName)
    }

    /**
     * 删除文件
     *
     * @param bucketName bucket名称
     * @param objectName 文件名称
     * @throws Exception
     * @see [AWS API
     * Documentation](http://docs.aws.amazon.com/goto/WebAPI/s3-2006-03-01/DeleteObject)
     */
    @Throws(Exception::class)
    fun removeObject(bucketName: String?, objectName: String?) {
        amazonS3?.deleteObject(bucketName, objectName)
    }

    fun removeObjects(bucketName: String?, keys: List<String?>) {
        val keyVersions: List<DeleteObjectsRequest.KeyVersion> = keys.stream().map {
            DeleteObjectsRequest.KeyVersion(
                it
            )
        }.collect(Collectors.toList())
        val deleteObjectsRequest = DeleteObjectsRequest(bucketName)
        deleteObjectsRequest.keys = keyVersions
        amazonS3?.deleteObjects(deleteObjectsRequest)
    }

    override fun afterPropertiesSet() {
        val clientConfiguration = ClientConfiguration()
        clientConfiguration.maxConnections = ossProperties.maxConnections
        val endpointConfiguration: AwsClientBuilder.EndpointConfiguration = AwsClientBuilder.EndpointConfiguration(
            ossProperties.endpoint, ossProperties.region
        )
        val awsCredentials: AWSCredentials = BasicAWSCredentials(
            ossProperties.accessKey,
            ossProperties.secretKey
        )
        val awsCredentialsProvider: AWSCredentialsProvider = AWSStaticCredentialsProvider(awsCredentials)
        amazonS3 = AmazonS3Client.builder().withEndpointConfiguration(endpointConfiguration)
            .withClientConfiguration(clientConfiguration).withCredentials(awsCredentialsProvider)
            .disableChunkedEncoding().withPathStyleAccessEnabled(ossProperties.pathStyleAccess).build()
    }

}