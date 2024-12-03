/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.languages;

import com.atlassian.confluence.ext.code.languages.DuplicateLanguageException;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.UnknownLanguageException;
import java.util.List;

public interface LanguageRegistry {
    public boolean isLanguageRegistered(String var1);

    public Language getLanguage(String var1) throws UnknownLanguageException;

    public List<Language> listLanguages();

    public String getWebResourceForLanguage(String var1) throws UnknownLanguageException;

    public void addLanguage(Language var1) throws DuplicateLanguageException;

    public void unregisterLanguage(String var1);
}

