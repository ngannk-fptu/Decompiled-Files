/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.BootstrapManager;

public class ConfluenceMockServletRequest {
    public String getContextPath() {
        return ((BootstrapManager)BootstrapUtils.getBootstrapManager()).getWebAppContextPath();
    }
}

