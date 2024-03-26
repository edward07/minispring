package org.greentree;

import org.dom4j.Element;

public class XmlBeanDefinitionReader {
    SimpleBeanFactory beanFactory;

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public XmlBeanDefinitionReader(SimpleBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String id = element.attributeValue("id");
            String className = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(id, className);
            beanFactory.registerBeanDefinition(beanDefinition);
        }
    }

}
