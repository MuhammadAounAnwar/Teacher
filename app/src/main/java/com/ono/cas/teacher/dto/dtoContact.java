package com.ono.cas.teacher.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class dtoContact {

    @SerializedName("contact_id")
    @Expose
    private int contact_id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("avatarUrl")
    @Expose
    private String avatarUrl;
    @SerializedName("username")
    @Expose
    private String username;

    @Override
    public String toString() {
        return "dtoContact{" +
                "contact_id=" + contact_id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public dtoContact() {
    }

    public dtoContact(int contact_id, String name, String email) {
        this.contact_id = contact_id;
        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getContact_id() {
        return contact_id;
    }

    public void setContact_id(int contact_id) {
        this.contact_id = contact_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
