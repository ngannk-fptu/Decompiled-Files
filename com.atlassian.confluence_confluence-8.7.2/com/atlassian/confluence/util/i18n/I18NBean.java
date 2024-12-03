/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.util.i18n.Message;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface I18NBean {
    public static final String DEFAULT_RESOURCE_BUNDLE = ConfluenceActionSupport.class.getName();

    public String getText(@Nullable String var1);

    public String getText(@Nullable String var1, @Nullable Object[] var2);

    default public String getText(String key, Object[] args, boolean onlyRawValue) {
        return this.getText(key, args);
    }

    public String getText(@Nullable String var1, @Nullable List var2);

    public String getText(Message var1);

    public String getTextStrict(String var1);

    public ResourceBundle getResourceBundle();

    public Map<String, String> getTranslationsForPrefix(String var1);

    public String getUntransformedRawText(String var1);
}

