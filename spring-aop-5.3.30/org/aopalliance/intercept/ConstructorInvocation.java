/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.aopalliance.intercept;

import java.lang.reflect.Constructor;
import javax.annotation.Nonnull;
import org.aopalliance.intercept.Invocation;

public interface ConstructorInvocation
extends Invocation {
    @Nonnull
    public Constructor<?> getConstructor();
}

