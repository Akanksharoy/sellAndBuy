package com.example.dell.blog_app;

/**
 * Created by dell on 25/2/18.
 */

public class blog {

   private String title;
   private String images;
   private String desc;



    private String email;
    public  blog(){

    }

    public blog(String title, String images, String desc,String emai) {
        this.title = title;
        this.images = images;
        this.desc = desc;
        this.email=emai;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
