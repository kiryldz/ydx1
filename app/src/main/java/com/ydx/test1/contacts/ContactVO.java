package com.ydx.test1.contacts;


public class ContactVO {
    private String ContactImage;
    private String ContactName;
    private String ContactNumber;
    private String ContactEmail;

    public String getContactEmail() {
        return ContactEmail;
    }

    public void setContactEmail(String contactEmail) {
        ContactEmail = contactEmail;
    }

    public String getContactImage() {
        return ContactImage;
    }

    public void setContactImage(String contactImage) {
        this.ContactImage = contactImage;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }
}
