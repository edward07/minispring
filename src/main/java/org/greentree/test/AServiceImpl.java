package org.greentree.test;

import lombok.Data;

@Data
public class AServiceImpl implements AService {

    private String property1;
    private String property2;
    private BaseService ref1;
    private String name;
    private int level;

    public AServiceImpl(String name, int level) {
        this.name = name;
        this.level = level;
    }

    @Override
    public void sayHello() {
        System.out.println("Hello from AServiceImpl...");
    }

}
