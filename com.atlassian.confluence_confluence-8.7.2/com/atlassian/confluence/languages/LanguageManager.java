/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.languages;

import com.atlassian.confluence.languages.Language;
import java.util.List;

public interface LanguageManager {
    public Language getLanguage(String var1);

    public List<Language> getLanguages();

    public Language getGlobalDefaultLanguage();
}

