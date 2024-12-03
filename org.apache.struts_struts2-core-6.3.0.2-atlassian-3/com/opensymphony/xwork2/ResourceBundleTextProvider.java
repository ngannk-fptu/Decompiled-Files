/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import java.util.ResourceBundle;

public interface ResourceBundleTextProvider
extends TextProvider {
    public void setBundle(ResourceBundle var1);

    public void setClazz(Class var1);

    public void setLocaleProvider(LocaleProvider var1);
}

