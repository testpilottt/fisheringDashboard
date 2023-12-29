package com.example.fisherydashboard.dto;

import com.example.fisherydashboard.enums.Country;
import com.example.fisherydashboard.enums.Region;
import lombok.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HarvestedFishRecords {

    private Long fisheringId;


    private Long membersId;

    private String location;

    private Blob pictureOfFish;

    private Double weightKg;

    private Country country;

    private Region region;

    private Long typeOfFishId;

    private LocalDateTime timeLog;

    public Long timestamp;
}
