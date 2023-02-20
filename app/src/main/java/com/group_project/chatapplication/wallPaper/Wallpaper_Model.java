package com.group_project.chatapplication.wallPaper;

public class Wallpaper_Model {

    String wallpaper_id, wallpaper_image;

    public Wallpaper_Model() {
    }

    public Wallpaper_Model(String wallpaper_id, String wallpaper_image) {
        this.wallpaper_id = wallpaper_id;
        this.wallpaper_image = wallpaper_image;
    }

    public String getWallpaper_id() {
        return wallpaper_id;
    }

    public void setWallpaper_id(String wallpaper_id) {
        this.wallpaper_id = wallpaper_id;
    }

    public String getWallpaper_image() {
        return wallpaper_image;
    }

    public void setWallpaper_image(String wallpaper_image) {
        this.wallpaper_image = wallpaper_image;
    }

}
