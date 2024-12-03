/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.conversion;

import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.TypeConverterHolder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class StrutsTypeConverterHolder
implements TypeConverterHolder {
    private HashMap<String, TypeConverter> defaultMappings = new HashMap();
    private HashMap<Class, Map<String, Object>> mappings = new HashMap();
    private HashSet<Class> noMapping = new HashSet();
    protected HashSet<String> unknownMappings = new HashSet();

    @Override
    public void addDefaultMapping(String className, TypeConverter typeConverter) {
        this.defaultMappings.put(className, typeConverter);
        if (this.unknownMappings.contains(className)) {
            this.unknownMappings.remove(className);
        }
    }

    @Override
    public boolean containsDefaultMapping(String className) {
        return this.defaultMappings.containsKey(className);
    }

    @Override
    public TypeConverter getDefaultMapping(String className) {
        return this.defaultMappings.get(className);
    }

    @Override
    public Map<String, Object> getMapping(Class clazz) {
        return this.mappings.get(clazz);
    }

    @Override
    public void addMapping(Class clazz, Map<String, Object> mapping) {
        this.mappings.put(clazz, mapping);
    }

    @Override
    public boolean containsNoMapping(Class clazz) {
        return this.noMapping.contains(clazz);
    }

    @Override
    public void addNoMapping(Class clazz) {
        this.noMapping.add(clazz);
    }

    @Override
    public boolean containsUnknownMapping(String className) {
        return this.unknownMappings.contains(className);
    }

    @Override
    public void addUnknownMapping(String className) {
        this.unknownMappings.add(className);
    }
}

