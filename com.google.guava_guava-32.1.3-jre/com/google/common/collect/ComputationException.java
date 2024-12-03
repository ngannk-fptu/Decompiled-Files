/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import javax.annotation.CheckForNull;

@Deprecated
@ElementTypesAreNonnullByDefault
@GwtCompatible
public class ComputationException
extends RuntimeException {
    private static final long serialVersionUID = 0L;

    public ComputationException(@CheckForNull Throwable cause) {
        super(cause);
    }
}

