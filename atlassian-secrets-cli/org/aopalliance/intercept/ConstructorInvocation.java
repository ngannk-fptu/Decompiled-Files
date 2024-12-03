/*
 * Decompiled with CFR 0.152.
 */
package org.aopalliance.intercept;

import java.lang.reflect.Constructor;
import org.aopalliance.intercept.Invocation;

public interface ConstructorInvocation
extends Invocation {
    public Constructor<?> getConstructor();
}

