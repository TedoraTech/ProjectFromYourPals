package com.niit.shreyasgs.collegeproject;

public class projects {

    private String name;
    private String description;
    private String probstatement;
    private String reference;
    private String mentor;
    private String videourl;
    private String repurl;
    private String imageurl;
    private String yos;
    private String otherUserId;
    private String currentUserId;
    private String projectId;

    public projects(){

    }

    public projects(String name, String description, String probstatement, String reference, String mentor, String videourl, String repurl, String imageurl, String yos, String otherUserId, String currentUserId, String projectId) {
        this.name = name;
        this.description = description;
        this.probstatement = probstatement;
        this.reference = reference;
        this.mentor = mentor;
        this.videourl = videourl;
        this.repurl = repurl;
        this.imageurl = imageurl;
        this.yos = yos;
        this.otherUserId = otherUserId;
        this.currentUserId = currentUserId;
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProbstatement() {
        return probstatement;
    }

    public void setProbstatement(String probstatement) {
        this.probstatement = probstatement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMentor() {
        return mentor;
    }

    public void setMentor(String mentor) {
        this.mentor = mentor;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getRepurl() {
        return repurl;
    }

    public void setRepurl(String repurl) {
        this.repurl = repurl;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getYos() {
        return yos;
    }

    public void setYos(String yos) {
        this.yos = yos;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}

