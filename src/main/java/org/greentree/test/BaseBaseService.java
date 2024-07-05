package org.greentree.test;

import lombok.Data;

@Data
public class BaseBaseService {

    public AServiceImpl as;

    public BaseBaseService() {

    }

    public void sayHello() {
        System.out.println("Hello from BaseBaseService...");
    }

}
