/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.soy.spi.i18n;

import java.io.Serializable;
import java.util.Locale;

public interface I18nResolver {
    public String getText(String var1);

    public String getText(String var1, Serializable ... var2);

    public String getText(Locale var1, String var2);

    public String getRawText(Locale var1, String var2);
}

