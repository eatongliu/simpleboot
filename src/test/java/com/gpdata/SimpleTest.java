package com.gpdata;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuyutong on 2017/9/13.
 */
public class SimpleTest {
    @Test
    public void test1 () {
        Map<String, Object> map = new HashMap<>();
        map.put("user", "lyt");
        System.out.println(map);

    }
}
