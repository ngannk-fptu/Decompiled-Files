/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api.security.user;

import java.security.Principal;
import java.util.Iterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import org.apache.jackrabbit.api.security.user.Group;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Authorizable {
    @NotNull
    public String getID() throws RepositoryException;

    public boolean isGroup();

    @NotNull
    public Principal getPrincipal() throws RepositoryException;

    @NotNull
    public Iterator<Group> declaredMemberOf() throws RepositoryException;

    @NotNull
    public Iterator<Group> memberOf() throws RepositoryException;

    public void remove() throws RepositoryException;

    @NotNull
    public Iterator<String> getPropertyNames() throws RepositoryException;

    @NotNull
    public Iterator<String> getPropertyNames(@NotNull String var1) throws RepositoryException;

    public boolean hasProperty(@NotNull String var1) throws RepositoryException;

    public void setProperty(@NotNull String var1, @Nullable Value var2) throws RepositoryException;

    public void setProperty(@NotNull String var1, @Nullable Value[] var2) throws RepositoryException;

    @Nullable
    public Value[] getProperty(@NotNull String var1) throws RepositoryException;

    public boolean removeProperty(@NotNull String var1) throws RepositoryException;

    @NotNull
    public String getPath() throws UnsupportedRepositoryOperationException, RepositoryException;
}

