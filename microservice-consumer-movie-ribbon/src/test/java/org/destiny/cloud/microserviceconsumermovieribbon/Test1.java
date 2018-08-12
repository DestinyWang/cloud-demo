package org.destiny.cloud.microserviceconsumermovieribbon;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author 王康
 * hzwangkang1@corp.netease.com
 * ------------------------------------------------------------------
 * <p></p>
 * ------------------------------------------------------------------
 * Corpright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * @version JDK 1.8.0_101
 * @since 2018/8/5 18:14
 */
public class Test1 {

    /**
     * 每个测试用例执行前都会调用一次
     */
    @Before
    public void setUp() {
        System.out.println("Test1.setUp");
    }

    /**
     * 每个测试用例执行后都会调用一次
     */
    @After
    public void tearDown() {
        System.out.println("Test1.tearDown");
    }

    @Test
    public void test() {
        System.out.println("Test1.test");
    }

}
