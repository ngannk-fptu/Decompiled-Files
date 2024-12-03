/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.conversion;

import com.atlassian.plugins.conversion.AsposeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AsposeAware {
    private static final Logger log = LoggerFactory.getLogger(AsposeUtils.class);

    static {
        try {
            log.info("Initializing Aspose licenses and fonts");
            AsposeUtils.license();
            AsposeUtils.configureFonts();
        }
        catch (RuntimeException ex) {
            log.error("Aspose initializiation failed", (Throwable)ex);
        }
    }
}

