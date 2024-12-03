/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.security.user;

import java.security.Principal;
import java.util.Iterator;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.AuthorizableTypeException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface UserManager {
    public static final int SEARCH_TYPE_USER = 1;
    public static final int SEARCH_TYPE_GROUP = 2;
    public static final int SEARCH_TYPE_AUTHORIZABLE = 3;

    @Nullable
    public Authorizable getAuthorizable(@NotNull String var1) throws RepositoryException;

    @Nullable
    public <T extends Authorizable> T getAuthorizable(@NotNull String var1, @NotNull Class<T> var2) throws AuthorizableTypeException, RepositoryException;

    @Nullable
    public Authorizable getAuthorizable(@NotNull Principal var1) throws RepositoryException;

    @Nullable
    public Authorizable getAuthorizableByPath(@NotNull String var1) throws UnsupportedRepositoryOperationException, RepositoryException;

    @NotNull
    public Iterator<Authorizable> findAuthorizables(@NotNull String var1, @Nullable String var2) throws RepositoryException;

    @NotNull
    public Iterator<Authorizable> findAuthorizables(@NotNull String var1, @Nullable String var2, int var3) throws RepositoryException;

    @NotNull
    public Iterator<Authorizable> findAuthorizables(@NotNull Query var1) throws RepositoryException;

    @NotNull
    public User createUser(@NotNull String var1, @Nullable String var2) throws AuthorizableExistsException, RepositoryException;

    @NotNull
    public User createUser(@NotNull String var1, @Nullable String var2, @NotNull Principal var3, @Nullable String var4) throws AuthorizableExistsException, RepositoryException;

    @NotNull
    public User createSystemUser(@NotNull String var1, @Nullable String var2) throws AuthorizableExistsException, RepositoryException;

    @NotNull
    public Group createGroup(@NotNull String var1) throws AuthorizableExistsException, RepositoryException;

    @NotNull
    public Group createGroup(@NotNull Principal var1) throws AuthorizableExistsException, RepositoryException;

    @NotNull
    public Group createGroup(@NotNull Principal var1, @Nullable String var2) throws AuthorizableExistsException, RepositoryException;

    @NotNull
    public Group createGroup(@NotNull String var1, @NotNull Principal var2, @Nullable String var3) throws AuthorizableExistsException, RepositoryException;

    public boolean isAutoSave();

    public void autoSave(boolean var1) throws UnsupportedRepositoryOperationException, RepositoryException;
}

