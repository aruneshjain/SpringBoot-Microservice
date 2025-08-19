package com.arunesh.Rating.RatingService.Entity;

public enum CsvHeader {

    CSV_FULL_DETAIL(
            "User-Name,User-Email,Hotel-Name,Hotel-Location,Rating-Rate,Rating-Feedback"
    ),
    CSV_FEEDBACK_DETAIL(
            "User-Name,User-Email,Hotel-Name,Rating-Feedback"
    ),
    CSV_RATING_DETAIL(
            "User-Name,User-Email,Hotel-Name,Rating-Rate,Rating-Feedback"
    );

    private String header;

    CsvHeader(String header){
        this.header= header;
    }

    public String getHeader() {
        return header;
    }
}
