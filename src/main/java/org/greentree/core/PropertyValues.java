package org.greentree.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PropertyValues {

    private final List<PropertyValue> propertyValueList;

    public PropertyValues() {
        this.propertyValueList = new ArrayList<>(0);
    }

    public int size() {
        return propertyValueList.size();
    }

    public void addPropertyValue(PropertyValue pv) {
        this.propertyValueList.add(pv);
    }

    public void addPropertyValue(String propertyName, Object propertyValue) {
        this.addPropertyValue(new PropertyValue(propertyName, propertyValue, ""));
    }

    public void removePropertyValue(PropertyValue pv) {
        this.propertyValueList.remove(pv);
    }

    public void removePropertyValue(String propertyName) {
        this.propertyValueList.remove(getPropertyValue(propertyName));
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    public Object get(String propertyName) {
        PropertyValue propertyValue = this.getPropertyValue(propertyName);
        return propertyValue == null ? null : propertyValue.getValue();
    }

    public boolean contains(String propertyName) {
        return this.getPropertyValue(propertyName) != null;
    }

    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }

}
