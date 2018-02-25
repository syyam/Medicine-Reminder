package com.example.syyam.saifapplication;

/**
 * Created by syyam on 31-Aug-16.
 */
public class Profile {
    private String Email,Image,Name,Phone;

    public Profile()
    {

    }
    public Profile(String Email, String Image, String Name, String Phone) {
        this.Email = Email;
        this.Image = Image;
        this.Name = Name;
        this.Phone = Phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
}
