/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.core.test.util;

import com.atlassian.core.test.util.DuckTypeProxy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

public class JavaBeanMethodHandler
implements DuckTypeProxy.UnimplementedMethodHandler {
    private final Map<String, Object> data = new HashMap<String, Object>();

    @Override
    public Object methodNotImplemented(Method method, Object[] args) {
        String key;
        String name = method.getName();
        String string = key = name.length() < 4 ? null : name.substring(3, 4).toLowerCase() + name.substring(4);
        if (name.startsWith("get") && ArrayUtils.getLength((Object)args) == 0) {
            return this.data.get(key);
        }
        if (name.startsWith("set") && ArrayUtils.getLength((Object)args) == 1) {
            this.data.put(key, args[0]);
            return null;
        }
        throw new UnsupportedOperationException(method.toString());
    }
}

