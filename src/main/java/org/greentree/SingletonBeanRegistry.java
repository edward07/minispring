package org.greentree;

import java.util.List;

public interface SingletonBeanRegistry {

    void registerSingleton(String beanName, Object singletonObject);
    Object getSingleton(String beanName);
    boolean containsSingleton(String beanName);
    List<String> getSingletonNames();

}
