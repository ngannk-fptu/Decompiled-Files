/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AuiSize {
    XSMALL(16),
    SMALL(24),
    MEDIUM(32),
    LARGE(48),
    XLARGE(64),
    XXLARGE(96),
    XXXLARGE(128);

    private static final Logger LOGGER;
    private static final AuiSize DEFAULT;
    private final int pixelEdge;

    private AuiSize(int pixelEdge) {
        this.pixelEdge = pixelEdge;
    }

    public int getSize() {
        return this.pixelEdge;
    }

    public static AuiSize byName(String name) {
        try {
            return AuiSize.valueOf(name.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            LOGGER.debug("Unknown t-shirt name requested, '{}' falling back to default size '{}'", (Object)name, (Object)DEFAULT);
            return DEFAULT;
        }
    }

    public static int toPixels(String name) {
        return AuiSize.byName(name).getSize();
    }

    static {
        LOGGER = LoggerFactory.getLogger(AuiSize.class);
        DEFAULT = XXXLARGE;
    }
}

