package com.algo.nexus.userService.Controller;

import com.algo.nexus.userService.Model.Entities.User;
import com.algo.nexus.userService.Model.Entities.UserProfileImage;
import com.algo.nexus.userService.Repository.UserRepository;
import com.algo.nexus.userService.Serivce.CloudinaryImageService;
import com.algo.nexus.userService.Serivce.UserProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/cloudinary")
public class CloudinaryImageController {

    @Autowired
    private CloudinaryImageService cloudinaryImageService;
    @Autowired
    private UserProfileImageService userProfileImageService;
    @Autowired
    private  UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Map> uploadImage(@RequestParam("image") MultipartFile file , @RequestParam("userId") UUID userId) throws IOException {

        User user = userRepository.findById(userId);
        if (user != null) {
            UserProfileImage existingProfileImage = userProfileImageService.findByUser(user);
            if (existingProfileImage != null) {
                // Delete existing image from Cloudinary
                System.out.println(existingProfileImage.getId());

                cloudinaryImageService.delete(existingProfileImage.getImageId());
                userProfileImageService.delete(existingProfileImage.getId());
            }
            Map cloudinaryData = this.cloudinaryImageService.upload(file);

        UserProfileImage userProfileImage = new UserProfileImage();
//        userProfileImage.setId(UUID.randomUUID());
        userProfileImage.setName(file.getOriginalFilename()); // Set image name
        userProfileImage.setImageUrl(cloudinaryData.get("url").toString()); // Set image URL from Cloudinary
        userProfileImage.setImageId(cloudinaryData.get("public_id").toString()); // Set image ID from Cloudinary
        userProfileImage.setUser(user);

        userProfileImageService.saveUserProfileImage(userProfileImage);

        return new ResponseEntity<>(cloudinaryData, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "User not found"));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileImage> getImageByUserId(@PathVariable UUID userId) {

        User user = userRepository.findById(userId);
        if (user != null) {
            UserProfileImage userProfileImage = userProfileImageService.findByUser(user);
            if (userProfileImage != null) {
                return ResponseEntity.ok(userProfileImage);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
