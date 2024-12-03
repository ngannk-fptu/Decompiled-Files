/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.xsrf;

import com.atlassian.confluence.impl.xsrf.SecurityLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityLevelConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityLevelConfig.class);
    public static final String CONFLUENCE_XWORK_XSRF_SECURITYLEVEL = "confluence.xwork.xsrf.securitylevel";

    public static SecurityLevel getSecurityLevel() {
        String securityLevelProp = System.getProperty(CONFLUENCE_XWORK_XSRF_SECURITYLEVEL);
        if (securityLevelProp != null) {
            try {
                return SecurityLevel.valueOf(securityLevelProp);
            }
            catch (IllegalArgumentException e) {
                log.error("Invalid security level. Using OPT_IN", (Throwable)e);
            }
        }
        return SecurityLevel.OPT_IN;
    }
}

