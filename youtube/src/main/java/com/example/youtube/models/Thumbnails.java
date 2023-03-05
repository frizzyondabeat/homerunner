package com.example.youtube.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Thumbnails {
    private Default _default;
    private Medium medium;
    private High high;
    private Standard standard;
    private MaxRes maxres;


}
