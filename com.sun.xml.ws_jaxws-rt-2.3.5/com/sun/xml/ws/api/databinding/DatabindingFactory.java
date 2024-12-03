/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.databinding;

import com.oracle.webservices.api.databinding.Databinding;
import com.sun.xml.ws.api.databinding.DatabindingConfig;
import com.sun.xml.ws.db.DatabindingFactoryImpl;
import java.util.Map;

public abstract class DatabindingFactory
extends com.oracle.webservices.api.databinding.DatabindingFactory {
    static final String ImplClass = DatabindingFactoryImpl.class.getName();

    public abstract Databinding createRuntime(DatabindingConfig var1);

    @Override
    public abstract Map<String, Object> properties();

    public static DatabindingFactory newInstance() {
        try {
            Class<?> cls = Class.forName(ImplClass);
            return (DatabindingFactory)cls.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

