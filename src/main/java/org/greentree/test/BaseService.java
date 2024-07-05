package org.greentree.test;

import lombok.Data;
import org.greentree.beans.factory.annotation.Autowired;

@Data
public class BaseService {

    @Autowired
    private BaseBaseService bbs;

    public BaseService() {
    }

    public void sayHello() {
        System.out.println("Base Service says Hello");
        bbs.sayHello();
    }
}
