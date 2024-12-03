/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Joinpoint {
    @Nullable
    public Object proceed() throws Throwable;

    @Nullable
    public Object getThis();

    @Nonnull
    public AccessibleObject getStaticPart();
}

