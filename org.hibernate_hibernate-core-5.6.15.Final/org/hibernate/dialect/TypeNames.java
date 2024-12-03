/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.hibernate.MappingException;
import org.hibernate.internal.util.StringHelper;

public final class TypeNames {
    private final Map<Integer, String> defaults = new HashMap<Integer, String>();
    private final Map<Integer, Map<Long, String>> weighted = new HashMap<Integer, Map<Long, String>>();

    public String get(int typeCode) throws MappingException {
        Integer integer = typeCode;
        String result = this.defaults.get(integer);
        if (result == null) {
            throw new MappingException("No Dialect mapping for JDBC type: " + typeCode);
        }
        return result;
    }

    public String get(int typeCode, long size, int precision, int scale) throws MappingException {
        Integer integer = typeCode;
        Map<Long, String> map = this.weighted.get(integer);
        if (map != null && map.size() > 0) {
            for (Map.Entry<Long, String> entry : map.entrySet()) {
                if (size > entry.getKey()) continue;
                return TypeNames.replace(entry.getValue(), size, precision, scale);
            }
        }
        return TypeNames.replace(this.get(typeCode), size, precision, scale);
    }

    private static String replace(String type, long size, int precision, int scale) {
        type = StringHelper.replaceOnce(type, "$s", Integer.toString(scale));
        type = StringHelper.replaceOnce(type, "$l", Long.toString(size));
        return StringHelper.replaceOnce(type, "$p", Integer.toString(precision));
    }

    public void put(int typeCode, long capacity, String value) {
        Integer integer = typeCode;
        Map<Long, String> map = this.weighted.get(integer);
        if (map == null) {
            map = new TreeMap<Long, String>();
            this.weighted.put(integer, map);
        }
        map.put(capacity, value);
    }

    public void put(int typeCode, String value) {
        Integer integer = typeCode;
        this.defaults.put(integer, value);
    }

    public boolean containsTypeName(String typeName) {
        return this.defaults.containsValue(typeName);
    }
}

