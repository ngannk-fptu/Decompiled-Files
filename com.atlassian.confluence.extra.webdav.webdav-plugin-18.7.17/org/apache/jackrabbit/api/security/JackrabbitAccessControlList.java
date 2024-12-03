/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.security;

import java.security.Principal;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface JackrabbitAccessControlList
extends JackrabbitAccessControlPolicy,
AccessControlList {
    @NotNull
    public String[] getRestrictionNames() throws RepositoryException;

    public int getRestrictionType(@NotNull String var1) throws RepositoryException;

    public boolean isMultiValueRestriction(@NotNull String var1) throws RepositoryException;

    public boolean isEmpty();

    public int size();

    public boolean addEntry(@NotNull Principal var1, @NotNull Privilege[] var2, boolean var3) throws AccessControlException, RepositoryException;

    public boolean addEntry(@NotNull Principal var1, @NotNull Privilege[] var2, boolean var3, @Nullable Map<String, Value> var4) throws AccessControlException, RepositoryException;

    public boolean addEntry(@NotNull Principal var1, @NotNull Privilege[] var2, boolean var3, @Nullable Map<String, Value> var4, @Nullable Map<String, Value[]> var5) throws AccessControlException, RepositoryException;

    public void orderBefore(@NotNull AccessControlEntry var1, @Nullable AccessControlEntry var2) throws AccessControlException, UnsupportedRepositoryOperationException, RepositoryException;
}

