package com.example.youtube.services;

import com.example.youtube.models.YoutubeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface YoutubeService {

    YoutubeResponse channelVideos(String query, String channelId, int maxResults, String order) throws IOException, GeneralSecurityException;
    String uploadFileToDrive(MultipartFile file) throws IOException, GeneralSecurityException;
    String uploadFileToCloudStorage(MultipartFile file) throws IOException, GeneralSecurityException;
}
