/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.I18NBean;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface I18NBeanFactory {
    public @NonNull I18NBean getI18NBean(@NonNull Locale var1);

    public @NonNull I18NBean getI18NBean();

    public @NonNull String getStateHash();
}

