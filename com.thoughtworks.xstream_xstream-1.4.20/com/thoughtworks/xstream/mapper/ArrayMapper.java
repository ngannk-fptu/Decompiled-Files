/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.core.util.Primitives;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class ArrayMapper
extends MapperWrapper {
    public ArrayMapper(Mapper wrapped) {
        super(wrapped);
    }

    public String serializedClass(Class type) {
        StringBuffer arraySuffix = new StringBuffer();
        String name = null;
        while (type.isArray()) {
            name = super.serializedClass(type);
            if (!type.getName().equals(name)) break;
            type = type.getComponentType();
            arraySuffix.append("-array");
            name = null;
        }
        if (name == null) {
            name = this.boxedTypeName(type);
        }
        if (name == null) {
            name = super.serializedClass(type);
        }
        if (arraySuffix.length() > 0) {
            return name + arraySuffix;
        }
        return name;
    }

    public Class realClass(String elementName) {
        int dimensions = 0;
        while (elementName.endsWith("-array")) {
            elementName = elementName.substring(0, elementName.length() - 6);
            ++dimensions;
        }
        if (dimensions > 0) {
            Class<?> componentType = Primitives.primitiveType(elementName);
            if (componentType == null) {
                componentType = super.realClass(elementName);
            }
            while (componentType.isArray()) {
                componentType = componentType.getComponentType();
                ++dimensions;
            }
            return super.realClass(this.arrayType(dimensions, componentType));
        }
        return super.realClass(elementName);
    }

    private String arrayType(int dimensions, Class componentType) {
        StringBuffer className = new StringBuffer();
        for (int i = 0; i < dimensions; ++i) {
            className.append('[');
        }
        if (componentType.isPrimitive()) {
            className.append(Primitives.representingChar(componentType));
            return className.toString();
        }
        className.append('L').append(componentType.getName()).append(';');
        return className.toString();
    }

    private String boxedTypeName(Class type) {
        return Primitives.isBoxed(type) ? type.getName() : null;
    }
}

