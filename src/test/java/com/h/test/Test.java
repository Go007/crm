package com.h.test;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by John on 2017/7/31.
 */
public class Test {
    @org.junit.Test
    public void test1() {
        String salt = "QDF&*^%&#$%$$%#123123^%^#%$#";
        String password = "123456" + salt;
        String s = DigestUtils.md5Hex(password);
        System.out.println(s);
    }
}
