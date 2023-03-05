package com.example.youtube.controllers;

import com.example.youtube.models.YoutubeResponse;
import com.example.youtube.services.YoutubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
@Slf4j
public class YoutubeController {

    private final YoutubeService youtubeService;

    @GetMapping("search")
    public ResponseEntity<YoutubeResponse> search(
            @RequestParam String query,
            @RequestParam(required = false) String channelId,
            @RequestParam(defaultValue = "10", required = false) int maxResults,
            @RequestParam(required = false, defaultValue = "relevance") String order) throws Exception {
        YoutubeResponse youtubeResponse = youtubeService.channelVideos(query, channelId, maxResults, order);
        return ResponseEntity.ok(youtubeResponse);
    }

    @PostMapping("upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws GeneralSecurityException, IOException {
        log.info("Uploading file: {}", file.getOriginalFilename());
        String uploadId = youtubeService.uploadFileToCloudStorage(file);
        return ResponseEntity.ok(uploadId);
    }

}
