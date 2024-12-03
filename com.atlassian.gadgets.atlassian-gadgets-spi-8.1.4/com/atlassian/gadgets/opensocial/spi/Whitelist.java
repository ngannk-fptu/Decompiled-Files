/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nullable
 */
package com.atlassian.gadgets.opensocial.spi;

import com.atlassian.sal.api.user.UserKey;
import java.net.URI;
import javax.annotation.Nullable;

public interface Whitelist {
    @Deprecated
    default public boolean allows(URI uri) {
        return this.allows(uri, null);
    }

    public boolean allows(URI var1, @Nullable UserKey var2);
}

