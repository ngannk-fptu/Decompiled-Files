/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.pulp;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.user.ConfluenceUser;

@ParametersAreNonnullByDefault
public interface PulpRedirectDao {
    public boolean addRedirect(ConfluenceUser var1, String var2);

    public int getRedirectCount(String var1);

    public boolean hasBeenRedirected(ConfluenceUser var1, String var2);
}

