/*
 * Decompiled with CFR 0.152.
 */
package org.aopalliance.intercept;

import java.lang.reflect.Method;
import org.aopalliance.intercept.Invocation;

public interface MethodInvocation
extends Invocation {
    public Method getMethod();
}

