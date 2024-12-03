/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.security;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.security.AccessControlEntry;
import org.apache.jackrabbit.api.security.authorization.PrivilegeCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface JackrabbitAccessControlEntry
extends AccessControlEntry {
    public boolean isAllow();

    @NotNull
    public String[] getRestrictionNames() throws RepositoryException;

    @Nullable
    public Value getRestriction(@NotNull String var1) throws ValueFormatException, RepositoryException;

    @Nullable
    public Value[] getRestrictions(@NotNull String var1) throws RepositoryException;

    @NotNull
    public PrivilegeCollection getPrivilegeCollection() throws RepositoryException;
}

