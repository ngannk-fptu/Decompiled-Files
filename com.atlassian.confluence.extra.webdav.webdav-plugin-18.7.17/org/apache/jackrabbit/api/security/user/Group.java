/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.apache.jackrabbit.api.security.user;

import java.util.Iterator;
import java.util.Set;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.jetbrains.annotations.NotNull;

public interface Group
extends Authorizable {
    @NotNull
    public Iterator<Authorizable> getDeclaredMembers() throws RepositoryException;

    @NotNull
    public Iterator<Authorizable> getMembers() throws RepositoryException;

    public boolean isDeclaredMember(@NotNull Authorizable var1) throws RepositoryException;

    public boolean isMember(@NotNull Authorizable var1) throws RepositoryException;

    public boolean addMember(@NotNull Authorizable var1) throws RepositoryException;

    @NotNull
    public Set<String> addMembers(String ... var1) throws RepositoryException;

    public boolean removeMember(@NotNull Authorizable var1) throws RepositoryException;

    @NotNull
    public Set<String> removeMembers(String ... var1) throws RepositoryException;
}

