package org.greentree;

import org.greentree.test.BaseBaseService;

public class Main {
    public static void main(String[] args) throws Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        BaseBaseService myService = (BaseBaseService) context.getBean("bbs");
        myService.sayHello();

    }
}