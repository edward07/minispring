package org.greentree.beans.factory.support;

import org.greentree.beans.factory.config.BeanDefinition;
import org.greentree.BeanException;
import org.greentree.beans.factory.BeanFactory;
import org.greentree.beans.factory.config.ConstructorArgumentValue;
import org.greentree.beans.factory.config.ConstructorArgumentValues;
import org.greentree.core.PropertyValue;
import org.greentree.core.PropertyValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final List<String> beanDefinitionNames = new ArrayList<>(0);
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(256);

    public SimpleBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) throws BeanException {
        Object singleton = this.getSingleton(beanName);
        if (singleton == null) {
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition == null) {
                    return new BeanException("No such bean.");
                }
                singleton = createBean(beanDefinition);
                this.registerSingleton(beanDefinition.getId(), singleton);
            }
        }
        return singleton;
    }

    @Override
    public Boolean containsBean(String name) {
        return beanNames.contains(name);
    }

    @Override
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    public void registerBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanDefinition.getId(), beanDefinition);
        this.beanDefinitionNames.add(beanDefinition.getId());
    }

    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }

    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }

    public void removeBeanDefinition(String beanName) {
        this.beanDefinitionMap.remove(beanName);
        this.beanDefinitionNames.remove(beanName);
        this.removeSingleton(beanName);
    }

    public boolean isSingleton(String beanName) {
        return this.beanDefinitionMap.get(beanName).isSigleton();
    }

    public boolean isPrototype(String beanName) {
        return this.beanDefinitionMap.get(beanName).isPrototype();
    }

    public Class<?> getType(String beanName) {
        return this.beanDefinitionMap.get(beanName).getBeanClass();
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        Object obj = doCreateBean(beanDefinition);
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        handleProperties(beanDefinition, clz, obj);
        return obj;
    }

    private Object doCreateBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;
        try {
            clz = Class.forName(beanDefinition.getClassName());
            // 处理构造器参数
            ConstructorArgumentValues argumentValues = beanDefinition.getArgumentValues();
            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ConstructorArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    } else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.parseInt((String)argumentValue.getValue());
                    } else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.parseInt((String)argumentValue.getValue());
                    } else {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                try {
                    con = clz.getConstructor(paramTypes);
                    obj = con.newInstance(paramValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                obj = clz.newInstance();
            }
            // 处理属性参数
            //this.handleProperties(beanDefinition, clz, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        PropertyValues propertyValues = bd.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            List<PropertyValue> propertyValueList = propertyValues.getPropertyValueList();
            for (int i = 0; i < propertyValueList.size(); i++) {
                PropertyValue propertyValue = propertyValueList.get(i);
                String type = propertyValue.getType();
                String name = propertyValue.getName();
                Object value = propertyValue.getValue();
                boolean isRef = propertyValue.isRef();
                Class<?>[] paramTypes = new Class[1];
                Object[] paramValues = new Object[1];
                if (!isRef) {
                    if ("String".equals(type) || "java.lang.String".equals(type)) {
                        paramTypes[0] = String.class;
                    } else if ("Integer".equals(type) || "java.lang.Integer".equals(type)) {
                        paramTypes[0] = Integer.class;
                    } else if ("int".equals(type)) {
                        paramTypes[0] = int.class;
                    } else {
                        paramTypes[0] = String.class;
                    }
                    paramValues[0] = value;
                } else {
                    try {
                        paramTypes[0] = Class.forName(type);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        paramValues[0] = this.getBean((String)value);
                    } catch (BeanException e) {
                        e.printStackTrace();
                    }
                }
                String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method method = null;
                try {
                    method = clz.getMethod(setterName, paramTypes);
                    method.invoke(obj, paramValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                this.getBean(beanName);
            } catch (BeanException e) {
                e.printStackTrace();
            }
        }
    }

}
