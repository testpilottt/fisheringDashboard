package com.example.fisherydashboard.dto;

import com.example.fisherydashboard.enums.Country;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountrySetting {

    private Long countrySettingId;

    private Country country;

    private String latitude;
    private String longtitude;

    private Double ThreshHoldNorthEast;
    private Double ThreshHoldNorthWest;
    private Double ThreshHoldSouthEast;
    private Double ThreshHoldSouthWest;

    private Double totalWeightFished;
}
