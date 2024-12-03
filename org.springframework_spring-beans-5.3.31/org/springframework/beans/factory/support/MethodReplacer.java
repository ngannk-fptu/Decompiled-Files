/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.lang.reflect.Method;

public interface MethodReplacer {
    public Object reimplement(Object var1, Method var2, Object[] var3) throws Throwable;
}

