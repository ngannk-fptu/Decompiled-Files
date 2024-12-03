/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package org.aopalliance.intercept;

import javax.annotation.Nonnull;
import org.aopalliance.intercept.Joinpoint;

public interface Invocation
extends Joinpoint {
    @Nonnull
    public Object[] getArguments();
}

