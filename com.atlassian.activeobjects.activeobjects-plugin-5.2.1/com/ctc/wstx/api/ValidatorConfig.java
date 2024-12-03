/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.api;

import com.ctc.wstx.api.CommonConfig;

public final class ValidatorConfig
extends CommonConfig {
    static final ValidatorConfig sInstance = new ValidatorConfig(null);

    private ValidatorConfig(ValidatorConfig base) {
        super(base);
    }

    public static ValidatorConfig createDefaults() {
        return sInstance;
    }

    protected int findPropertyId(String propName) {
        return -1;
    }

    protected Object getProperty(int id) {
        return null;
    }

    protected boolean setProperty(String propName, int id, Object value) {
        return false;
    }
}

