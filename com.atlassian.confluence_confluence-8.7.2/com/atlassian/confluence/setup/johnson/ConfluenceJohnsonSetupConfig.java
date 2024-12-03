/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.setup.SetupConfig
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.johnson;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.johnson.setup.SetupConfig;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceJohnsonSetupConfig
implements SetupConfig {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceJohnsonSetupConfig.class);
    private static Iterable<String> setupPaths = ImmutableList.of((Object)"/setup/", (Object)"/bootstrap/", (Object)"/download", (Object)"/rest/landlord", (Object)"/rest/healthCheck", (Object)"/rest/webResource", (Object)"/rest/wrm");

    public boolean isSetupPage(String uri) {
        try {
            if (uri == null) {
                return true;
            }
            for (String setupPath : setupPaths) {
                if (!uri.startsWith(setupPath)) continue;
                return true;
            }
            return false;
        }
        catch (Exception e) {
            log.warn("Caught exception whilst checking requested URI against setup paths.", (Throwable)e);
            return true;
        }
    }

    public boolean isSetup() {
        return GeneralUtil.isSetupComplete();
    }
}

