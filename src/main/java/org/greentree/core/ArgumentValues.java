package org.greentree.core;

import java.util.LinkedList;
import java.util.List;

public class ArgumentValues {

    private final List<ArgumentValue> argumentValueList = new LinkedList<>();

    public ArgumentValues() {
    }

    public void addArgumentValue(ArgumentValue newValue) {
        this.argumentValueList.add(newValue);
    }

    public ArgumentValue getIndexedArgumentValue(int index) {
        return this.argumentValueList.get(index);
    }

    public int getArgumentCount() {
        return this.argumentValueList.size();
    }

    public boolean isEmpty() {
        return this.argumentValueList.isEmpty();
    }

}
