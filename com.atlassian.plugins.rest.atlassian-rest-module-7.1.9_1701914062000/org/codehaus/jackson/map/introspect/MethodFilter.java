/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.Method;

public interface MethodFilter {
    public boolean includeMethod(Method var1);
}

