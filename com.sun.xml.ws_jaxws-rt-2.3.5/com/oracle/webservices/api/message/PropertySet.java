/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.api.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

public interface PropertySet {
    public boolean containsKey(Object var1);

    public Object get(Object var1);

    public Object put(String var1, Object var2);

    public boolean supports(Object var1);

    public Object remove(Object var1);

    @Deprecated
    public Map<String, Object> createMapView();

    public Map<String, Object> asMap();

    @Inherited
    @Retention(value=RetentionPolicy.RUNTIME)
    @Target(value={ElementType.FIELD, ElementType.METHOD})
    public static @interface Property {
        public String[] value();
    }
}

