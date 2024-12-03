/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package org.apache.jackrabbit.api.security.principal;

import java.security.Principal;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PrincipalManager {
    public static final int SEARCH_TYPE_NOT_GROUP = 1;
    public static final int SEARCH_TYPE_GROUP = 2;
    public static final int SEARCH_TYPE_ALL = 3;

    public boolean hasPrincipal(@NotNull String var1);

    @Nullable
    public Principal getPrincipal(@NotNull String var1);

    @NotNull
    public PrincipalIterator findPrincipals(@Nullable String var1);

    @NotNull
    public PrincipalIterator findPrincipals(@Nullable String var1, int var2);

    @NotNull
    public PrincipalIterator getPrincipals(int var1);

    @NotNull
    public PrincipalIterator getGroupMembership(@NotNull Principal var1);

    @NotNull
    public Principal getEveryone();
}

