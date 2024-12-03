/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.localization;

import com.sun.jersey.localization.Localizable;

public final class LocalizableMessage
implements Localizable {
    private final String _bundlename;
    private final String _key;
    private final Object[] _args;

    public LocalizableMessage(String bundlename, String key, Object ... args) {
        this._bundlename = bundlename;
        this._key = key;
        if (args == null) {
            args = new Object[]{};
        }
        this._args = args;
    }

    @Override
    public String getKey() {
        return this._key;
    }

    @Override
    public Object[] getArguments() {
        return this._args;
    }

    @Override
    public String getResourceBundleName() {
        return this._bundlename;
    }
}

