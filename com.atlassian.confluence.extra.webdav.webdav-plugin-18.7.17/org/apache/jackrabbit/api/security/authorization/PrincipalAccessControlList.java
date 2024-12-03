/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.security.authorization;

import java.security.Principal;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.security.Privilege;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlEntry;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface PrincipalAccessControlList
extends JackrabbitAccessControlList {
    @NotNull
    public Principal getPrincipal();

    public boolean addEntry(@Nullable String var1, @NotNull Privilege[] var2) throws RepositoryException;

    public boolean addEntry(@Nullable String var1, @NotNull Privilege[] var2, @NotNull Map<String, Value> var3, @NotNull Map<String, Value[]> var4) throws RepositoryException;

    public static interface Entry
    extends JackrabbitAccessControlEntry {
        @Nullable
        public String getEffectivePath();
    }
}

