/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public interface I18nHelper {
    public String getText(String var1);

    public String getText(String var1, String var2);

    public String getText(String var1, String var2, String var3);

    public String getText(String var1, Object var2);

    public String getUnescapedText(String var1);

    public String getUnescapedText(Locale var1, String var2);

    public Map<String, String> getAllTranslationsForPrefix(String var1);

    public String getText(Locale var1, String var2, Serializable ... var3);
}

