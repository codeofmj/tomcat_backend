package com.smhrd.board.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class NCPBucketConfig {
	
	
	@Value("${ncp.region}")
	private String region;
	
	@Value("${ncp.end-point}")
	private String endPoint;
	
	@Value("${ncp.bucket-name}")
	private String bucketName;
	
	@Value("${ncp.access-key}")
	private String accessKey;
	
	@Value("${ncp.secret-key}")
	private String secretKey;
	
    /*
    1. S3Client
     - NCP Object Storage와 통신하는 역할을 하는 객체

    2. credentialProvider()
     - 인증 정보를 전달하기 위한 메소드 ( 접근키, 비밀키 )

    3. RequestChecksumCalculation
     - AWS SDK는 기본적으로 데이터 무결성을 높이기 위해 모든 요청에 CheckSum을 계산
     - NCP Object Storage처럼 S3와 100% 호환되지 않는 일부 스토리지에서는 AWS가 요구하는 특정 CheckSum 방식 지원 X
     - 이런 경우, AWS SDK가 보내는 CheckSum 때문에 "Access Denied" 오류 발생
     - WHEN_REQUIRED를 추가하여 불필요한 CheckSum을 비활성화하여 충돌없이 정상적으로 데이터 전송
       -> 호환성 문제를 피하고 오류를 해결하기 위한 조치

    4. endpointOverride()
     - AWS S3의 기본 서버 주소가 아닌 다른 서버 주소로 요청을 보내는 메소드
     - NCP를 사용하고 있기 때문에 Obejct Storage의 주소로 보내도록 설정

    */	
	@Bean // 개발자가 직접 제어가 불가능한 외부 라이브러리 등을 Bean으로 만들 때 사용
	public S3Client amazonS3() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey,secretKey)
                ))
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .endpointOverride(URI.create(endPoint))
                .build();
	}
	
}
