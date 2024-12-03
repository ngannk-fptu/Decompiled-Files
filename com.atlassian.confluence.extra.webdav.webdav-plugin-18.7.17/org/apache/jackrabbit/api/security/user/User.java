/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api.security.user;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Impersonation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface User
extends Authorizable {
    public boolean isAdmin();

    public boolean isSystemUser();

    @NotNull
    public Credentials getCredentials() throws RepositoryException;

    @NotNull
    public Impersonation getImpersonation() throws RepositoryException;

    public void changePassword(@Nullable String var1) throws RepositoryException;

    public void changePassword(@Nullable String var1, @NotNull String var2) throws RepositoryException;

    public void disable(@Nullable String var1) throws RepositoryException;

    public boolean isDisabled() throws RepositoryException;

    @Nullable
    public String getDisabledReason() throws RepositoryException;
}

