/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package com.atlassian.confluence.setup;

import org.apache.commons.lang3.ArrayUtils;

public class DatabaseVerifyException
extends Exception {
    private final String titleKey;
    private final String key;
    private final String[] parameters;

    public DatabaseVerifyException(String titleKey, String key, String ... parameters) {
        this.titleKey = titleKey;
        this.key = key;
        this.parameters = parameters;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitleKey() {
        return this.titleKey;
    }

    public String[] getParameters() {
        return (String[])ArrayUtils.clone((Object[])this.parameters);
    }
}

