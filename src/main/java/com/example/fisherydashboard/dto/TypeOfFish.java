package com.example.fisherydashboard.dto;


public class TypeOfFish {

    private Long typeOfFishId;

    private String typeOfFishName;

    private byte[] typeOfFishPictureByte;

    private Boolean isActive;


    public Long getTypeOfFishId() {
        return typeOfFishId;
    }

    public void setTypeOfFishId(Long typeOfFishId) {
        this.typeOfFishId = typeOfFishId;
    }

    public String getTypeOfFishName() {
        return typeOfFishName;
    }

    public void setTypeOfFishName(String typeOfFishName) {
        this.typeOfFishName = typeOfFishName;
    }

    public byte[] getTypeOfFishPictureBase64() {
        return typeOfFishPictureByte;
    }

    public void setTypeOfFishPictureBase64(byte[] typeOfFishPicture) {
        this.typeOfFishPictureByte = typeOfFishPicture;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
