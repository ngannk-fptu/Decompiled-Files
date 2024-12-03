/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class HierarchicalStreams {
    public static Class readClassType(HierarchicalStreamReader reader, Mapper mapper) {
        String classAttribute = HierarchicalStreams.readClassAttribute(reader, mapper);
        Class type = classAttribute == null ? mapper.realClass(reader.getNodeName()) : mapper.realClass(classAttribute);
        return type;
    }

    public static String readClassAttribute(HierarchicalStreamReader reader, Mapper mapper) {
        String classAttribute;
        String attributeName = mapper.aliasForSystemAttribute("resolves-to");
        String string = classAttribute = attributeName == null ? null : reader.getAttribute(attributeName);
        if (classAttribute == null && (attributeName = mapper.aliasForSystemAttribute("class")) != null) {
            classAttribute = reader.getAttribute(attributeName);
        }
        return classAttribute;
    }
}

