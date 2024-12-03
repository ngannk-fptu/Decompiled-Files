/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.auth;

import com.atlassian.sal.api.page.PageCapability;
import com.atlassian.sal.api.user.UserRole;
import java.net.URI;
import java.util.EnumSet;

public interface LoginUriProvider {
    public URI getLoginUri(URI var1);

    public URI getLoginUri(URI var1, EnumSet<PageCapability> var2);

    public URI getLoginUriForRole(URI var1, UserRole var2);

    public URI getLoginUriForRole(URI var1, UserRole var2, EnumSet<PageCapability> var3);
}

