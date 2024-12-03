/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.AbstractYamlNode;
import com.hazelcast.internal.yaml.MutableYamlScalar;
import com.hazelcast.internal.yaml.YamlException;
import com.hazelcast.internal.yaml.YamlNode;

public class YamlScalarImpl
extends AbstractYamlNode
implements MutableYamlScalar {
    private Object value;

    public YamlScalarImpl(YamlNode parent, String nodeName, Object value) {
        super(parent, nodeName);
        this.value = value;
    }

    @Override
    public <T> boolean isA(Class<T> type) {
        return this.value != null && this.value.getClass().isAssignableFrom(type);
    }

    @Override
    public <T> T nodeValue() {
        return (T)this.value;
    }

    @Override
    public <T> T nodeValue(Class<T> type) {
        if (!this.isA(type)) {
            throw new YamlException("The scalar's type " + this.value.getClass() + " is not the expected " + type);
        }
        return (T)this.value;
    }

    @Override
    public void setValue(Object newValue) {
        this.value = newValue;
    }

    public String toString() {
        return "YamlScalarImpl{nodeName=" + this.nodeName() + ", value=" + this.value + '}';
    }
}

