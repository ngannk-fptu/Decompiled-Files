/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.prebake.Coordinate
 */
package com.atlassian.plugins.less;

import com.atlassian.plugins.less.PrebakeStateResult;
import com.atlassian.webresource.api.prebake.Coordinate;
import java.net.URI;

public interface UriStateManager {
    public String getState(URI var1);

    public PrebakeStateResult getState(URI var1, Coordinate var2);
}

