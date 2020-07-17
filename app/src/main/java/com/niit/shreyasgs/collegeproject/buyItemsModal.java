package com.niit.shreyasgs.collegeproject;

public class buyItemsModal {

    public String image;
    public String type;
    public String text;

    public buyItemsModal(){

    }

    public buyItemsModal(String image, String type, String text) {
        this.image = image;
        this.type = type;
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
