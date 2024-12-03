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

    @Override
    protected int findPropertyId(String propName) {
        return -1;
    }

    @Override
    protected Object getProperty(int id) {
        return null;
    }

    @Override
    protected boolean setProperty(String propName, int id, Object value) {
        return false;
    }
}

