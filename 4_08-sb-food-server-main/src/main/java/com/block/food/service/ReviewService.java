package com.block.food.service;

import com.block.food.config.JwtConfig;
import com.block.food.dao.ReviewDAO;
import com.block.food.dto.ReviewRequest;
import com.block.food.util.UniqueFileNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class ReviewService {

    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    ReviewDAO reviewDAO;

    @Value("${cloud.aws.s3.bucket}")
    String bucketName;

    @Autowired
    S3Client s3Client;


    public int createReviewPhoto(String token, long restaurantId, long menuId,
                      String rating, String content, MultipartFile image){
        // 1. 토큰에서 userId를 뽑아낸다.
        long userId = Long.parseLong( jwtConfig.getTokenClaims(token.substring(7) ).getSubject() );

        if(image != null) {
            // 2. 이미지를 S3에 업로드한다.
            // 2-1. 이미지 파일 이름을 유니크하게 생성한다.
            String fileName = UniqueFileNameGenerator.generateUniqueFileName(userId, ".jpg");
            // 2-2. S3에 업로드 하기 위한 Request 객체를 생성한다.
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentLength(image.getSize())
                    .contentType(image.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            // 2-3. S3에 이미지를 업로드한다.
            try {
                s3Client.putObject(putObjectRequest,
                        RequestBody.fromInputStream(image.getInputStream(), image.getSize()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 3. 이미지 URL을 가져온다. (S3에 업로드한 이미지 URL)
            String imageUrl = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucketName, fileName);

            System.out.println("imageUrl = " + imageUrl);

            // 4. DB에 저장하도록 DAO에 요청한다.
            reviewDAO.createReviewPhoto(userId, restaurantId,
                    menuId, rating, content, imageUrl);
            return 0;
        } else {

            reviewDAO.createReviewPhoto(userId, restaurantId, menuId, rating, content);
            return 0;
        }

    }



    public int createReview(String token, ReviewRequest reviewRequest){
        // 토큰은 Bearer (한칸공백포함) 뒷부분만 토큰이다.
        Long userId = Long.parseLong( jwtConfig.getTokenClaims(token.substring(7) ).getSubject() );

        return reviewDAO.createReview(userId, reviewRequest);

    }

}
