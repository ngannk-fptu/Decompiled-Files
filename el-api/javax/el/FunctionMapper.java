/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.lang.reflect.Method;

public abstract class FunctionMapper {
    public abstract Method resolveFunction(String var1, String var2);

    public void mapFunction(String prefix, String localName, Method method) {
    }
}

