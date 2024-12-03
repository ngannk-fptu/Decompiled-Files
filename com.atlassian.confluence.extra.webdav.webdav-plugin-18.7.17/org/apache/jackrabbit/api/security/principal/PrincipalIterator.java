/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.apache.jackrabbit.api.security.principal;

import java.security.Principal;
import javax.jcr.RangeIterator;
import org.jetbrains.annotations.NotNull;

public interface PrincipalIterator
extends RangeIterator {
    @NotNull
    public Principal nextPrincipal();
}

