package org.greentree;

import org.greentree.test.AService;

public class Main {
    public static void main(String[] args) throws Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        AService myService = (AService) context.getBean("aservice");
        myService.sayHello();

    }
}