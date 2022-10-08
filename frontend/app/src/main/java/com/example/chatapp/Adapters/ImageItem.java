package com.example.chatapp.Adapters;

public class ImageItem{
    String name;
    Integer resource;
    public ImageItem(String name, Integer resource){
        this.name = name;
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public Integer getResource() {
        return resource;
    }
}
