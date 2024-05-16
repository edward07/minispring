package org.greentree;

import org.greentree.core.ArgumentValue;
import org.greentree.core.ArgumentValues;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private List<String> beanDefinitionNames = new ArrayList<>(0);

    public SimpleBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) throws BeanException {
        Object singleton = this.getSingleton(beanName);
        if (singleton == null) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition == null) {
                return new BeanException("No such bean.");
            }
            try {
                singleton = Class.forName(beanDefinition.getClassName()).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new BeanException("Error occurs when creating bean " + beanDefinition.getId());
            }
            this.registerSingleton(beanDefinition.getId(), singleton);
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
        Object obj = null;
        Constructor<?> con = null;
        try {
            clz = Class.forName(beanDefinition.getClassName());
            // 处理构造器参数
            ArgumentValues argumentValues = beanDefinition.getArgumentValues();
            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
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
                    obj = con.newInstance(argumentValues);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 处理
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

}
