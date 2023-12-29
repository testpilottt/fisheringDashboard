package com.example.fisherydashboard.dto;

import com.example.fisherydashboard.enums.Country;
import lombok.*;

import java.sql.Blob;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Members {

    private Long memberId;

    private List<HarvestedFishRecords> harvestedFishRecords;

    private String username;
    private String firstName;

    private String lastName;
    private String password;

    private AccessLevel accessLevel;
    private Long hoursLogged;
    private Blob profilePicture;

    private byte[] profilePictureBase64;

    //blockchain
    private String membersHash;

    private Country country;
}
