package com.example.youtube.services;

import com.example.youtube.models.YoutubeResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.model.File;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;


@RequiredArgsConstructor
@Slf4j
@Service
public class YoutubeServiceImpl implements YoutubeService {

    @Value(value = "${key}")
    private String API_KEY;
    @Value(value = "${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;
    @Value(value = "${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;
    @Value(value = "${spring.application.name}")
    private String APPLICATION_NAME;
    @Value(value = "${storage.bucket}")
    private String BUCKET_NAME;

    private final Storage storage;



    @Override
    public YoutubeResponse channelVideos(String query, String channelId, int maxResults, String order) throws IOException, GeneralSecurityException {

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .build();

        YouTube youtubeService = new YouTube.Builder(httpTransport, jsonFactory, credential).setApplicationName(APPLICATION_NAME).build();

        YouTube.Search.List request = youtubeService.search().list(Collections.singletonList("id,snippet"));
        request.setKey(API_KEY);
        request.setChannelId(channelId);
        request.setQ(query);
        request.setOrder(order);
        request.setMaxResults((long) maxResults);

        SearchListResponse response = request.execute();
        ModelMapper modelMapper = new ModelMapper();
        YoutubeResponse youtubeResponse = modelMapper.map(response, YoutubeResponse.class);
        log.info("youtube_response: {}", youtubeResponse);
        Arrays.stream(youtubeResponse.getItems()).forEach(item -> log.info("title: {}", item.getSnippet().getTitle()));
        return youtubeResponse;
    }

    @Override
    public String uploadFileToDrive(MultipartFile file) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .build();
        File fileMetadata = new File();
        fileMetadata.setName(file.getName());
        java.io.File filePath = new java.io.File(file.getName());
        try {
            File uploadFile = new com.google.api.services.drive.Drive.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build()
                    .files()
                    .create(fileMetadata, new FileContent(file.getContentType(), filePath))
                    .setKey(API_KEY)
                    .setFields("id")
                    .execute();
            log.info("File uploaded: {}", uploadFile.getName());
            log.info("File ID: {}", uploadFile.getId());
            return uploadFile.getId();
        } catch (GoogleJsonResponseException exception){
            log.error("Unable to upload file: {}", exception.getDetails().getMessage());
            throw new IllegalStateException("Unable to upload file: " + exception.getDetails().getMessage());
        }
    }

    @Override
    public String uploadFileToCloudStorage(MultipartFile file) throws IOException {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(BUCKET_NAME, file.getName());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        byte[] bytes = file.getBytes();
        storage.create(blobInfo, bytes);
        log.info("File uploaded: {}", file.getName());
        return blobId.getName();
    }
}
