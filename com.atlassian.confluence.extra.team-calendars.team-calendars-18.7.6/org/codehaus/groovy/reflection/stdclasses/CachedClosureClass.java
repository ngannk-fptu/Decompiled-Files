/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.reflection.ClassInfo;

public class CachedClosureClass
extends CachedClass {
    private final Class[] parameterTypes;
    private final int maximumNumberOfParameters;

    public CachedClosureClass(Class klazz, ClassInfo classInfo) {
        super(klazz, classInfo);
        CachedMethod[] methods = this.getMethods();
        int maximumNumberOfParameters = -1;
        Class[] parameterTypes = null;
        for (CachedMethod method : methods) {
            Class[] pt;
            if (!"doCall".equals(method.getName()) || (pt = method.getNativeParameterTypes()).length <= maximumNumberOfParameters) continue;
            parameterTypes = pt;
            maximumNumberOfParameters = parameterTypes.length;
        }
        this.maximumNumberOfParameters = maximumNumberOfParameters = Math.max(maximumNumberOfParameters, 0);
        this.parameterTypes = parameterTypes;
    }

    public Class[] getParameterTypes() {
        return this.parameterTypes;
    }

    public int getMaximumNumberOfParameters() {
        return this.maximumNumberOfParameters;
    }
}

