/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.reflection.FieldKey;
import com.thoughtworks.xstream.converters.reflection.FieldKeySorter;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class XStream12FieldKeySorter
implements FieldKeySorter {
    public Map sort(Class type, Map keyedByFieldKey) {
        TreeMap map = new TreeMap(new Comparator(){

            public int compare(Object o1, Object o2) {
                FieldKey fieldKey1 = (FieldKey)o1;
                FieldKey fieldKey2 = (FieldKey)o2;
                int i = fieldKey2.getDepth() - fieldKey1.getDepth();
                if (i == 0) {
                    i = fieldKey1.getOrder() - fieldKey2.getOrder();
                }
                return i;
            }
        });
        map.putAll(keyedByFieldKey);
        keyedByFieldKey.clear();
        keyedByFieldKey.putAll(map);
        return keyedByFieldKey;
    }
}

