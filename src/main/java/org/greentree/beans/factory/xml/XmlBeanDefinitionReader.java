package org.greentree.beans.factory.xml;

import org.dom4j.Element;
import org.greentree.beans.factory.config.BeanDefinition;
import org.greentree.Resource;
import org.greentree.beans.factory.support.SimpleBeanFactory;
import org.greentree.beans.factory.BeanFactory;
import org.greentree.beans.factory.config.ConstructorArgumentValue;
import org.greentree.beans.factory.config.ConstructorArgumentValues;
import org.greentree.core.PropertyValue;
import org.greentree.core.PropertyValues;

import java.util.ArrayList;
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
            List<String> refs = new ArrayList<>();
            for (Element ele : propertyElements) {
                String pType = ele.attributeValue("type");
                String pName = ele.attributeValue("name");
                String pValue = ele.attributeValue("value");
                String pRef = ele.attributeValue("ref");
                String pV = "";
                boolean isRef = false;
                if (pValue != null && !pValue.isEmpty()) {
                    pV = pValue;
                } else if (pRef != null && !pRef.isEmpty()) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                pvs.addPropertyValue(new PropertyValue(pName, pV, pType, isRef));
            }
            beanDefinition.setPropertyValues(pvs);

            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);

            List<Element> constructorElements = element.elements("constructor-arg");
            ConstructorArgumentValues avs = new ConstructorArgumentValues();
            for (Element ele : constructorElements) {
                String aType = ele.attributeValue("type");
                String aName = ele.attributeValue("name");
                String aValue = ele.attributeValue("value");
                avs.addArgumentValue(new ConstructorArgumentValue(aValue, aType, aName));
            }
            beanDefinition.setArgumentValues(avs);

            beanFactory.registerBeanDefinition(beanDefinition);
        }
    }

}
