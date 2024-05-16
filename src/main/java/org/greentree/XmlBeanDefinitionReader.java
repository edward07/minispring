package org.greentree;

import org.dom4j.Element;
import org.greentree.core.ArgumentValue;
import org.greentree.core.ArgumentValues;
import org.greentree.core.PropertyValue;
import org.greentree.core.PropertyValues;

import java.util.List;

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
            String beanId = element.attributeValue("id");
            String beanClassName = element.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClassName);

            List<Element> propertyElements = element.elements("property");
            PropertyValues pvs = new PropertyValues();
            for (Element ele : propertyElements) {
                String pType = ele.attributeValue("type");
                String pName = ele.attributeValue("name");
                String pValue = ele.attributeValue("value");
                pvs.addPropertyValue(new PropertyValue(pType, pName, pValue));
            }
            beanDefinition.setPropertyValues(pvs);

            List<Element> constructorElements = element.elements("constructor-arg");
            ArgumentValues avs = new ArgumentValues();
            for (Element ele : constructorElements) {
                String aType = ele.attributeValue("type");
                String aName = ele.attributeValue("name");
                String aValue = ele.attributeValue("value");
                avs.addArgumentValue(new ArgumentValue(aValue, aType, aName));
            }
            beanDefinition.setArgumentValues(avs);

            beanFactory.registerBeanDefinition(beanDefinition);
        }
    }

}
