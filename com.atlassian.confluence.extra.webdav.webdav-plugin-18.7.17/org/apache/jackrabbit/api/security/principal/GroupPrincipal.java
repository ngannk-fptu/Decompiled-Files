/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.security.principal;

import java.security.Principal;
import java.util.Enumeration;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface GroupPrincipal
extends Principal {
    public boolean isMember(@NotNull Principal var1);

    @NotNull
    public Enumeration<? extends Principal> members();
}

