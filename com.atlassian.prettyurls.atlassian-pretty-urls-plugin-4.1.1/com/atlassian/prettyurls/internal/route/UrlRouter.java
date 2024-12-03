/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.prettyurls.internal.route;

import com.atlassian.plugin.servlet.filter.FilterLocation;
import javax.servlet.http.HttpServletRequest;

public interface UrlRouter {
    public Result route(HttpServletRequest var1, FilterLocation var2);

    public static interface Result {
        public String toURI();

        public boolean isRouted();
    }
}

