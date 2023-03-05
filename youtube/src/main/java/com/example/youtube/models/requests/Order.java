package com.example.youtube.models.requests;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Order {
    DATE ("date"),
    RATING ("rating"),
    RELEVANCE ("relevance"),
    TITLE ("title"),
    VIDEOCOUNT ("videoCount"),
    VIEWCOUNT ("viewCount");

    private String value;

}
