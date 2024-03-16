package org.greentree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleBeanFactory implements BeanFactory {
    private List<BeanDefinition> beanDefinitions = new ArrayList<>();
    private List<String> beanNames = new ArrayList<>();
    private Map<String, Object> singletons = new HashMap<>();
    public SimpleBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) throws BeanException {
        if (singletons.containsKey(beanName)) {
            return singletons.get(beanName);
        }

        int i = beanNames.indexOf(beanName);
        if (i == -1) {
            return null;
        }
        BeanDefinition beanDefinition = beanDefinitions.get(i);
        try {
            Object bean = Class.forName(beanDefinition.getClassName()).newInstance();
            if (bean != null) {
                this.singletons.put(beanName, bean);
            }
            return bean;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new BeanException("Error creating bean with name '" + beanName + "': " + e.getMessage());
        }
    }

    @Override
    public Boolean containsBean(String name) {
        return beanNames.contains(name);
    }

    @Override
    public void registerBean(BeanDefinition beanDefinition) {
        beanNames.add(beanDefinition.getId());
        beanDefinitions.add(beanDefinition);
    }
}
