package com.ydx.test1;

import com.ydx.test1.utils.Constants;

import org.junit.Test;

import static org.junit.Assert.*;


public class ConstantsUnitTest {

    @Test
    public void minimum_isCorrect_test1()  {
        assertEquals(Constants.MIN(1,2), 1);
    }

    @Test
    public void minimum_isCorrect_test2()  {
        assertEquals(Constants.MIN(2,1), 1);
    }

    @Test
    public void minimum_isCorrect_test3()  {
        assertEquals(Constants.MIN(null,null), 0);
    }

    @Test
    public void minimum_isCorrect_test4()  {
        assertEquals(Constants.MIN(-10000,null), -10000);
    }

    @Test
    public void minimum_isCorrect_test5()  {
        assertEquals(Constants.MIN(null,50000), 50000);
    }
}