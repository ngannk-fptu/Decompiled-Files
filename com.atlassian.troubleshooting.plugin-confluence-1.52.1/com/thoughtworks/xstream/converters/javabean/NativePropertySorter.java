/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.javabean;

import com.thoughtworks.xstream.converters.javabean.PropertySorter;
import java.util.Map;

public class NativePropertySorter
implements PropertySorter {
    public Map sort(Class type, Map nameMap) {
        return nameMap;
    }
}

