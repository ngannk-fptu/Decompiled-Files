/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api.security.authorization;

import javax.jcr.AccessDeniedException;
import javax.jcr.NamespaceException;
import javax.jcr.RepositoryException;
import javax.jcr.security.AccessControlException;
import javax.jcr.security.Privilege;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PrivilegeManager {
    @NotNull
    public Privilege[] getRegisteredPrivileges() throws RepositoryException;

    @NotNull
    public Privilege getPrivilege(@NotNull String var1) throws AccessControlException, RepositoryException;

    @NotNull
    public Privilege registerPrivilege(@NotNull String var1, boolean var2, @Nullable String[] var3) throws AccessDeniedException, NamespaceException, RepositoryException;
}

