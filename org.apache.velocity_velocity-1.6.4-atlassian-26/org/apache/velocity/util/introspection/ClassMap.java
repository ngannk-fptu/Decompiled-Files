/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.util.introspection;

import java.lang.reflect.Method;
import org.apache.velocity.util.introspection.MethodMap;

public interface ClassMap {
    public Method findMethod(String var1, Object[] var2) throws MethodMap.AmbiguousException;
}

