package org.greentree;

import org.greentree.beans.factory.BeanFactory;
import org.greentree.beans.factory.support.SimpleBeanFactory;
import org.greentree.beans.factory.xml.XmlBeanDefinitionReader;
import org.greentree.core.ApplicationEvent;
import org.greentree.core.ApplicationEventPublisher;

public class ClassPathXmlApplicationContext implements BeanFactory, ApplicationEventPublisher {
    private SimpleBeanFactory beanFactory;

    public ClassPathXmlApplicationContext(String fileName) {
        this(fileName, true);
    }

    //构造器获取外部配置，解析出Bean的定义，形成内存映像
    public ClassPathXmlApplicationContext(String fileName, boolean isRefresh) {
        ClassPathXmlResource resource = new ClassPathXmlResource(fileName);
        this.beanFactory = new SimpleBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);
        if (isRefresh) {
            this.beanFactory.refresh();
        }
    }

    //这是对外的一个方法，让外部程序从容器中获取Bean实例，会逐步演化成核心方法
    @Override
    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }

    @Override
    public Boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    @Override
    public void registerBean(String beanName, Object obj) {
        beanFactory.registerBean(beanName, obj);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return beanFactory.isSingleton(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) {
        return beanFactory.isPrototype(beanName);
    }

    @Override
    public Class<?> getType(String beanName) {
        return beanFactory.getType(beanName);
    }

    @Override
    public void publish(ApplicationEvent applicationEvent) {

    }
}
