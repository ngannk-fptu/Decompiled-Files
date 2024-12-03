/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.aopalliance.intercept;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import org.aopalliance.intercept.Invocation;

public interface MethodInvocation
extends Invocation {
    @Nonnull
    public Method getMethod();
}

