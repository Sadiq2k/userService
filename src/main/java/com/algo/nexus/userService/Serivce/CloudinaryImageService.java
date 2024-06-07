package com.algo.nexus.userService.Serivce;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryImageService {
   public Map upload(MultipartFile file);


    void delete(String imageId) throws IOException;

}
