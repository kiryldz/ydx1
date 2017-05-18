package com.ydx.test1;

import com.ydx.test1.contacts.*;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContactVOUnitTest {

    private ContactVO contactVO;

    public ContactVOUnitTest() {
        contactVO = new ContactVO();
    }

    @Test
    public void get_Contact_Email_test()  {
        contactVO.setContactEmail("fake@email.com");
        assertEquals(contactVO.getContactEmail(), "fake@email.com");
    }

    @Test
    public void get_Contact_Image_test()  {
        contactVO.setContactImage("image");
        assertEquals(contactVO.getContactImage(), "image");
    }

    @Test
    public void get_Contact_Name_test()  {
        contactVO.setContactName("Igor");
        assertEquals(contactVO.getContactName(), "Igor");
    }

    @Test
    public void get_Contact_Number_test()  {
        contactVO.setContactNumber("888888888");
        assertEquals(contactVO.getContactNumber(), "888888888");
    }

}
