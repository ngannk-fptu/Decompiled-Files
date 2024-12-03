/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.conversion.TypeConverter;
import java.util.Map;

public interface TypeConverterHolder {
    public void addDefaultMapping(String var1, TypeConverter var2);

    public boolean containsDefaultMapping(String var1);

    public TypeConverter getDefaultMapping(String var1);

    public Map<String, Object> getMapping(Class var1);

    public void addMapping(Class var1, Map<String, Object> var2);

    public boolean containsNoMapping(Class var1);

    public void addNoMapping(Class var1);

    public boolean containsUnknownMapping(String var1);

    public void addUnknownMapping(String var1);
}

