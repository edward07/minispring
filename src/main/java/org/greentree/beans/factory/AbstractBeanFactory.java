package org.greentree.beans.factory;

import org.greentree.BeansException;
import org.greentree.beans.factory.config.BeanDefinition;
import org.greentree.beans.factory.config.ConstructorArgumentValue;
import org.greentree.beans.factory.config.ConstructorArgumentValues;
import org.greentree.beans.factory.support.BeanDefinitionRegistry;
import org.greentree.beans.factory.support.DefaultSingletonBeanRegistry;
import org.greentree.core.PropertyValue;
import org.greentree.core.PropertyValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry
        implements BeanFactory, BeanDefinitionRegistry {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

    public AbstractBeanFactory() {

    }

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                this.getBean(beanName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSigleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }

    @Override
    public Boolean containsBean(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanDefinitionMap.put(beanName, beanDefinition);
        this.beanDefinitionNames.add(beanName);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        this.beanDefinitionMap.remove(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        Object singleton = this.getSingleton(beanName);
        if (singleton == null) {
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                singleton = this.createBean(beanDefinition);
                this.registerBean(beanName, singleton);
                this.applyBeanPostProcessorBeforeInitialization(singleton, beanName);
                if (beanDefinition.getInitMethodName() != null && !beanDefinition.getInitMethodName().isEmpty()) {
                    invokeInitMethod(beanDefinition, singleton);
                }
                this.applyBeanPostProcessorAfterInitialization(singleton, beanName);
            }
        }
        return singleton;
    }

    private void invokeInitMethod(BeanDefinition beanDefinition, Object object) {
        Class<?> clz = beanDefinition.getClass();
        try {
            Method method = clz.getMethod(beanDefinition.getInitMethodName());
            method.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class<?> clz = null;
        Object obj = doCreateBean(beanDefinition);
        this.earlySingletonObjects.put(beanDefinition.getId(), obj);
        try {
            clz = Class.forName(beanDefinition.getClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        populateBean(beanDefinition, clz, obj);
        return obj;
    }

    /**
     * 只是毛坯，没有进行属性设置
     * 看代码怎么看？以终为始
     * 本方法只是为最终的构造器调用而服务。
     *
     * @param beanDefinition
     * @return
     */
    private Object doCreateBean(BeanDefinition beanDefinition) {
        Object obj = null;
        try {
            Class<?> clz = Class.forName(beanDefinition.getClassName());
            ConstructorArgumentValues argumentValues = beanDefinition.getArgumentValues();
            if (!argumentValues.isEmpty()) {
                int argumentCount = beanDefinition.getArgumentValues().getArgumentCount();
                Class<?>[] paramTypes = new Class<?>[argumentCount];
                Object[] paramValues = new Object[argumentCount];
                for (int i = 0; i < argumentCount; i++) {
                    ConstructorArgumentValue constructorArgumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(constructorArgumentValue.getType()) ||
                            "java.lang.String".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = constructorArgumentValue.getValue();
                    } else if ("Integer".equals(constructorArgumentValue.getType()) ||
                            "java.lang.Integer".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.parseInt((String) constructorArgumentValue.getValue());
                    } else if ("int".equals(constructorArgumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.parseInt((String) constructorArgumentValue.getValue());
                    } else {
                        paramTypes[i] = String.class;
                        paramValues[i] = constructorArgumentValue.getValue();
                    }
                }
                try {
                    // 上面的一大坨代码都只是为了得到paramTypes，paramValues这一对key-value数组
                    Constructor<?> constructor = clz.getConstructor(paramTypes);
                    obj = constructor.newInstance(paramValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    // 上面的一大坨代码都只是为了得到paramTypes，paramValues这一对key-value数组
                    Constructor<?> constructor = clz.getConstructor();
                    obj = constructor.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * populate是完善的意思，直接调用handleProperties不就行了吗？
     *
     * @param beanDefinition
     * @param clz
     * @param object
     */
    private void populateBean(BeanDefinition beanDefinition, Class<?> clz, Object object) {
        handleProperties(beanDefinition, clz, object);
    }

    private void handleProperties(BeanDefinition beanDefinition, Class<?> clz, Object object) {
        PropertyValues propertyValues = beanDefinition.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            System.out.println("handle property of bean: " + beanDefinition.getId());
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String name = propertyValue.getName();
                String type = propertyValue.getType();
                Object value = propertyValue.getValue();
                boolean isRef = propertyValue.isRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                if (!isRef) {
                    if ("String".equals(type)) {
                        paramTypes[0] = String.class;
                        paramValues[0] = value.toString();
                    } else if ("Integer".equals(type)) {
                        paramTypes[0] = Integer.class;
                        paramValues[0] = Integer.valueOf(type);
                    } else if ("int".equals(type)) {
                        paramTypes[0] = int.class;
                        paramValues[0] = Integer.valueOf(type);
                    } else {
                        paramTypes[0] = String.class;
                        paramValues[0] = value.toString();
                    }
                } else {
                    try {
                        paramTypes[0] = Class.forName(type);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        paramValues[0] = getBean((String) value);
                    } catch (BeansException e) {
                        throw new RuntimeException(e);
                    }
                    String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    try {
                        Method method = clz.getMethod(methodName, paramTypes[0]);
                        method.invoke(object, paramValues);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    abstract public Object applyBeanPostProcessorBeforeInitialization(Object existingBean, String beanName) throws BeansException;

    abstract public Object applyBeanPostProcessorAfterInitialization(Object existingBean, String beanName) throws BeansException;

}
