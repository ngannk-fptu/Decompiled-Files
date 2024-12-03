/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.apache.jackrabbit.api.security.user;

import java.security.Principal;
import javax.jcr.RepositoryException;
import javax.security.auth.Subject;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.jetbrains.annotations.NotNull;

public interface Impersonation {
    @NotNull
    public PrincipalIterator getImpersonators() throws RepositoryException;

    public boolean grantImpersonation(@NotNull Principal var1) throws RepositoryException;

    public boolean revokeImpersonation(@NotNull Principal var1) throws RepositoryException;

    public boolean allows(@NotNull Subject var1) throws RepositoryException;
}

