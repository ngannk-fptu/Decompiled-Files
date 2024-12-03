/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.localization;

import com.sun.jersey.localization.Localizable;
import com.sun.jersey.localization.LocalizableMessage;

public class LocalizableMessageFactory {
    private final String _bundlename;

    public LocalizableMessageFactory(String bundlename) {
        this._bundlename = bundlename;
    }

    public Localizable getMessage(String key, Object ... args) {
        return new LocalizableMessage(this._bundlename, key, args);
    }
}

