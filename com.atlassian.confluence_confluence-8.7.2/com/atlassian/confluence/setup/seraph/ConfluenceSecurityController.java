/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.config.SecurityConfig
 *  com.atlassian.seraph.controller.SecurityController
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.seraph;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.seraph.config.SecurityConfig;
import com.atlassian.seraph.controller.SecurityController;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceSecurityController
implements SecurityController {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceSecurityController.class);

    public boolean isSecurityEnabled() {
        try {
            return GeneralUtil.isSetupComplete();
        }
        catch (NullPointerException e) {
            log.error("Bootstrap environment not configured: " + e, (Throwable)e);
            return false;
        }
    }

    public void init(Map map, SecurityConfig securityConfig) {
    }
}

