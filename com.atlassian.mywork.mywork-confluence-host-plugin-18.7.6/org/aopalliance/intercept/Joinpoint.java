/*
 * Decompiled with CFR 0.152.
 */
package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;

public interface Joinpoint {
    public Object proceed() throws Throwable;

    public Object getThis();

    public AccessibleObject getStaticPart();
}

