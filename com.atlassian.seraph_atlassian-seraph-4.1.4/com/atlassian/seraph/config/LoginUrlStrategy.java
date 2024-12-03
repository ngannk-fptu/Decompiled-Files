/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.config;

import com.atlassian.seraph.Initable;
import com.atlassian.seraph.config.SecurityConfig;

public interface LoginUrlStrategy
extends Initable {
    public String getLoginURL(SecurityConfig var1, String var2);

    public String getLogoutURL(SecurityConfig var1, String var2);

    public String getLinkLoginURL(SecurityConfig var1, String var2);
}

