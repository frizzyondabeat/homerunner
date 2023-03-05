package com.example.youtube.models.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class YoutubeRequest {
    private String query;
    private int maxResults;
    private Order order;
}
