package org.greentree;

import lombok.Data;
import org.greentree.core.ArgumentValues;
import org.greentree.core.PropertyValues;

@Data
public class BeanDefinition {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    private boolean lazyInit = false;
    private String scope = SCOPE_SINGLETON;
    private String[] dependsOn;
    private ArgumentValues argumentValues;
    private PropertyValues propertyValues;
    private String initMethodName;
    private volatile Object beanClass;

    private String id;

    private String className;

    public BeanDefinition(String id, String className) {
        this.id = id;
        this.className = className;
    }

    public boolean isSigleton() {
        return SCOPE_SINGLETON.equals(scope);
    }

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(scope);
    }

    public Class<?> getBeanClass() {
        return (Class<?>) beanClass;
    }

}
