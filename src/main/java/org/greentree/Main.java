package org.greentree;

import org.greentree.test.MyInterface;

public class Main {
    public static void main(String[] args) {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        MyInterface myService = (MyInterface) context.getBean("aservice");
        myService.sayHello();

    }
}