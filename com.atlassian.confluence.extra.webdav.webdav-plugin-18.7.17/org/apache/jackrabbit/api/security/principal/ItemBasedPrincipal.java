/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.apache.jackrabbit.api.security.principal;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.principal.JackrabbitPrincipal;
import org.jetbrains.annotations.NotNull;

public interface ItemBasedPrincipal
extends JackrabbitPrincipal {
    @NotNull
    public String getPath() throws RepositoryException;
}

