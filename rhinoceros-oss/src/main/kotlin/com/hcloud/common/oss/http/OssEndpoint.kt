package com.hcloud.common.oss.http

import com.amazonaws.services.s3.model.Bucket
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.hcloud.common.oss.service.OssTemplate
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(value = ["/oss"])
class OssEndpoint(private val template: OssTemplate) {
    /**
     * Bucket Endpoints
     */
    @PostMapping("/bucket/{bucketName}")
    fun createBucket(@PathVariable bucketName: String): Bucket? {
        template.createBucket(bucketName)
        return template.getBucket(bucketName)?.get()
    }

    /**
     * 获取所有桶
     */
    @GetMapping("/bucket")
    fun getBuckets(): List<Bucket?>? {
        return template.getAllBuckets()
    }

    /**
     * 获取指定桶信息
     */
    @GetMapping("/bucket/{bucketName}")
    fun getBucket(@PathVariable bucketName: String): Bucket? {
        return template.getBucket(bucketName)?.orElseThrow { IllegalArgumentException("Bucket Name not found!") }
    }

    /**
     * 删除桶
     */
    @DeleteMapping("/bucket/{bucketName}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun deleteBucket(@PathVariable bucketName: String?) {
        template.removeBucket(bucketName)
    }

    /**
     * 上传图片
     */
    @PostMapping("/object/{bucketName}")
    fun createObject(@RequestBody file: MultipartFile, @PathVariable bucketName: String?): S3Object? {
        val name: String = file.originalFilename ?: throw IllegalArgumentException("not find file original file name")
        template.putObject(bucketName, name, file.inputStream, file.size, file.contentType)
        return template.getObjectInfo(bucketName, name)
    }

    /**
     * 上传图片
     */
    @PostMapping("/object/{bucketName}/{objectName}")
    fun createObject(
        @RequestBody file: MultipartFile, @PathVariable bucketName: String?,
        @PathVariable objectName: String?
    ): S3Object? {
        template.putObject(bucketName, objectName, file.inputStream, file.size, file.contentType)
        return template.getObjectInfo(bucketName, objectName)
    }

    /**
     * 上传图片
     */
    @PostMapping("/object/{bucketName}/{subDic}/{objectName}")
    fun createObject(
        @RequestBody file: MultipartFile, @PathVariable bucketName: String, @PathVariable subDic: String,
        @PathVariable objectName: String?
    ): S3Object? {
        val subBuckName = "$bucketName/$subDic"
        template.putObject(
            subBuckName,
            objectName,
            file.inputStream,
            file.size,
            file.contentType
        )
        return template.getObjectInfo(subBuckName, objectName)
    }

    /**
     * 获取图片
     */
    @GetMapping("/object/{bucketName}/{objectName}")
    fun filterObject(@PathVariable bucketName: String?, @PathVariable objectName: String?): List<S3ObjectSummary?>? {
        return template.getAllObjectsByPrefix(bucketName, objectName, true)
    }

    /**
     * 获取图片内容
     */
    @GetMapping("/object/{bucketName}/{objectName}/{expires}")
    fun getObject(
        @PathVariable bucketName: String?, @PathVariable objectName: String?,
        @PathVariable expires: Int?
    ): Map<String, Any?> {
        val responseBody = HashMap<String, Any?>();
        responseBody["bucket"] = bucketName
        responseBody["object"] = objectName
        responseBody["url"] = template.getObjectURL(bucketName, objectName, expires)
        responseBody["expires"] = expires
        return responseBody
    }

    /**
     * 删除图片
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/object/{bucketName}/{objectName}/")
    fun deleteObject(@PathVariable bucketName: String?, @PathVariable objectName: String?) {
        template.removeObject(bucketName, objectName)
    }
}