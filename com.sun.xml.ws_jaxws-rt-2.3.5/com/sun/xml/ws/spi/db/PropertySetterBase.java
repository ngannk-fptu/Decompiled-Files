/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.PropertySetter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class PropertySetterBase
implements PropertySetter {
    protected Class type;
    private static final Map<Class, Object> uninitializedValues = new HashMap<Class, Object>();

    @Override
    public Class getType() {
        return this.type;
    }

    public static boolean setterPattern(Method method) {
        return method.getName().startsWith("set") && method.getName().length() > 3 && method.getReturnType().equals(Void.TYPE) && method.getParameterTypes() != null && method.getParameterTypes().length == 1;
    }

    protected static Object uninitializedValue(Class cls) {
        return uninitializedValues.get(cls);
    }

    static {
        uninitializedValues.put(Byte.TYPE, (byte)0);
        uninitializedValues.put(Boolean.TYPE, false);
        uninitializedValues.put(Character.TYPE, Character.valueOf('\u0000'));
        uninitializedValues.put(Float.TYPE, Float.valueOf(0.0f));
        uninitializedValues.put(Double.TYPE, 0.0);
        uninitializedValues.put(Integer.TYPE, 0);
        uninitializedValues.put(Long.TYPE, 0L);
        uninitializedValues.put(Short.TYPE, (short)0);
    }
}

