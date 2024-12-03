/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import java.util.Map;

public class ImmutableFieldKeySorter
implements FieldKeySorter {
    public Map sort(Class type, Map keyedByFieldKey) {
        return keyedByFieldKey;
    }
}

