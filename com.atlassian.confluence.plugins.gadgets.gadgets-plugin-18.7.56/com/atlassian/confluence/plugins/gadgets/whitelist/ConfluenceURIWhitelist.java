/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.gadgets.whitelist;

import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.sal.api.user.UserKey;
import java.net.URI;

public class ConfluenceURIWhitelist
implements Whitelist {
    private final OutboundWhitelist whitelist;

    public ConfluenceURIWhitelist(OutboundWhitelist whitelist) {
        this.whitelist = whitelist;
    }

    public boolean allows(URI uri, UserKey userKey) {
        return this.whitelist.isAllowed(uri, userKey);
    }
}

