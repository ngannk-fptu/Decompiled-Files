/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.TranslationMode;
import javax.servlet.http.HttpServletRequest;

public interface I18nModeManager {
    public TranslationMode getTranslationMode();

    public void setTranslationMode(HttpServletRequest var1, TranslationMode var2);

    public TranslationMode getModeForString(String var1);
}

