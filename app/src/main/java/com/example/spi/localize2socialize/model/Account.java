package com.example.spi.localize2socialize.model;

import android.net.Uri;

import java.util.Objects;

public class Account {
    private Long id;
    private String personName;
    private String personGivenName;
    private String personFamilyName;
    private String personEmail;
    private String personId;
    private transient Uri photo;
    private String encodedPhoto;

    public Account() {
    }

    public Account(String personName, String personGivenName, String personFamilyName,
                   String personEmail, String personId, Uri photo) {
        this.personName = personName;
        this.personGivenName = personGivenName;
        this.personFamilyName = personFamilyName;
        this.personEmail = personEmail;
        this.personId = personId;
        this.photo = photo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getPersonGivenName() {
        return personGivenName;
    }

    public void setPersonGivenName(String personGivenName) {
        this.personGivenName = personGivenName;
    }

    public String getPersonFamilyName() {
        return personFamilyName;
    }

    public void setPersonFamilyName(String personFamilyName) {
        this.personFamilyName = personFamilyName;
    }

    public String getPersonEmail() {
        return personEmail;
    }

    public void setPersonEmail(String personEmail) {
        this.personEmail = personEmail;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    public void setEncodedPhoto(String encodedPhoto) {
        this.encodedPhoto = encodedPhoto;
    }

    @Override
    public String toString() {
        StringBuilder name = new StringBuilder(personFamilyName);
        name.append(" ").append(personGivenName).append("(").append(personEmail.split("@")[0]).append(")");
        return name.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Account) {
            Account account = (Account) obj;
            return this.personId.equals(account.personId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId);
    }
}
