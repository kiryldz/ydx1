package com.ydx.test1;

import com.ydx.test1.welcomescreen.WelcomeScreenInstance;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WelcomeScreenInstanceUnitTest {

    @Test
    public void get_wsInstance_Image_test()  {
        WelcomeScreenInstance wsInstance = new WelcomeScreenInstance("text", -5);
        assertEquals(wsInstance.getImgRes(), -5);
        wsInstance = new WelcomeScreenInstance(null, 10);
        assertEquals(wsInstance.getImgRes(), 10);
    }

    @Test
    public void get_wsInstance_Text_test()  {
        WelcomeScreenInstance wsInstance = new WelcomeScreenInstance("text", 0);
        assertEquals(wsInstance.getText(), "text");
        wsInstance = new WelcomeScreenInstance(null, 0);
        assertEquals(wsInstance.getText(), null);
    }
}
