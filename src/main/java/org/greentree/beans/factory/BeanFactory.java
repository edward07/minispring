package org.greentree.beans.factory;

public interface BeanFactory {
    Object getBean(String beanName) throws Exception;
    Boolean containsBean(String name);
    void registerBean(String beanName, Object obj);
    boolean isSingleton(String beanName);
    boolean isPrototype(String beanName);
    Class<?> getType(String beanName);

}
