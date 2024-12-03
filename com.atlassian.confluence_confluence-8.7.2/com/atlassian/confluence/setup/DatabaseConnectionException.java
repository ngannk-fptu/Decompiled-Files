/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.BootstrapException
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.bootstrap.BootstrapException;

public class DatabaseConnectionException
extends RuntimeException {
    private final String key;

    public DatabaseConnectionException(BootstrapException e, String key, String ... parameters) {
        super(e);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}

