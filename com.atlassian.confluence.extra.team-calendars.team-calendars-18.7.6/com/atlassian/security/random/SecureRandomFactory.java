/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.security.random;

import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SecureRandomFactory {
    private static final Logger log = LoggerFactory.getLogger(SecureRandomFactory.class);

    private SecureRandomFactory() {
    }

    public static SecureRandom newInstance() {
        log.debug("Starting creation of new SecureRandom");
        long start = System.currentTimeMillis();
        SecureRandom random = new SecureRandom();
        random.nextBytes(new byte[1]);
        long end = System.currentTimeMillis();
        log.debug("Finished creation new SecureRandom in {} ms", (Object)(end - start));
        return random;
    }
}

