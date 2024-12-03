/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.DefaultLocaleProvider;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;

public class DefaultLocaleProviderFactory
implements LocaleProviderFactory {
    @Override
    public LocaleProvider createLocaleProvider() {
        return new DefaultLocaleProvider();
    }
}

