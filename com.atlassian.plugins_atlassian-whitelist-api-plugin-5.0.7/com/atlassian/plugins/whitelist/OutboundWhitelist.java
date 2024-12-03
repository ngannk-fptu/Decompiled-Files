/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.whitelist.Whitelist;
import com.atlassian.sal.api.user.UserKey;
import java.net.URI;

@PublicApi
public interface OutboundWhitelist
extends Whitelist {
    public boolean isAllowed(URI var1, UserKey var2);
}

