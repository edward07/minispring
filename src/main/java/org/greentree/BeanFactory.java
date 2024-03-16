package org.greentree;

public interface BeanFactory {
    Object getBean(String beanName) throws Exception;

    Boolean containsBean(String name);

    void registerBean(BeanDefinition beanDefinition);

}
