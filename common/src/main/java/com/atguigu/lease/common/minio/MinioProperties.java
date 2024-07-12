package com.atguigu.lease.common.minio;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ClassName: MinioProperties
 * Package: com.atguigu.lease.common.minio
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
