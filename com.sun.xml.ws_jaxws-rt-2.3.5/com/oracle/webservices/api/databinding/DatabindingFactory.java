/*
 * Decompiled with CFR 0.152.
 */
package com.oracle.webservices.api.databinding;

import com.oracle.webservices.api.databinding.Databinding;
import java.util.Map;

public abstract class DatabindingFactory {
    static final String ImplClass = "com.sun.xml.ws.db.DatabindingFactoryImpl";

    public abstract Databinding.Builder createBuilder(Class<?> var1, Class<?> var2);

    public abstract Map<String, Object> properties();

    public static DatabindingFactory newInstance() {
        try {
            Class<?> cls = Class.forName(ImplClass);
            return DatabindingFactory.convertIfNecessary(cls);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DatabindingFactory convertIfNecessary(Class<?> cls) throws InstantiationException, IllegalAccessException {
        return (DatabindingFactory)cls.newInstance();
    }
}

