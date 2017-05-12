package com.ydx.test1.contacts;


class ContactVO {
    private String ContactImage;
    private String ContactName;
    private String ContactNumber;
    private String ContactEmail;

    String getContactEmail() {
        return ContactEmail;
    }

    void setContactEmail(String contactEmail) {
        ContactEmail = contactEmail;
    }

    String getContactImage() {
        return ContactImage;
    }

    void setContactImage(String contactImage) {
        this.ContactImage = contactImage;
    }

    String getContactName() {
        return ContactName;
    }

    void setContactName(String contactName) {
        ContactName = contactName;
    }

    String getContactNumber() {
        return ContactNumber;
    }

    void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }
}
