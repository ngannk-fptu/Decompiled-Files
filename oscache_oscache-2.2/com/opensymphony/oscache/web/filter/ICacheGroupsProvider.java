/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.oscache.web.filter;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import javax.servlet.http.HttpServletRequest;

public interface ICacheGroupsProvider {
    public String[] createCacheGroups(HttpServletRequest var1, ServletCacheAdministrator var2, Cache var3);
}

