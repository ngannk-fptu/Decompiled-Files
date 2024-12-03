/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugins.whitelist;

import com.atlassian.annotations.PublicApi;
import java.net.URI;

@PublicApi
public interface Whitelist {
    public boolean isAllowed(URI var1);
}

