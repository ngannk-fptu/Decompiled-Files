/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.Pair
 *  com.atlassian.streams.spi.UriAuthenticationParameterProvider
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.common;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.Pair;
import com.atlassian.streams.spi.UriAuthenticationParameterProvider;
import com.google.common.base.Preconditions;

public final class OSUserBasicUriAuthenticationParameterProvider
implements UriAuthenticationParameterProvider {
    private final UserManager userManager;
    Pair<String, String> param = Pair.pair((Object)"os_authType", (Object)"basic");

    public OSUserBasicUriAuthenticationParameterProvider(UserManager userManager) {
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    public Option<Pair<String, String>> get() {
        if (this.userManager.getRemoteUsername() != null) {
            return Option.some(this.param);
        }
        return Option.none();
    }
}

