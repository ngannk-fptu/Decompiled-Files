/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.languages.impl;

import com.atlassian.confluence.ext.code.languages.DuplicateLanguageException;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import com.atlassian.confluence.ext.code.languages.UnknownLanguageException;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public final class LanguageRegistryImpl
implements LanguageRegistry {
    private Map<String, Language> languages = new Hashtable<String, Language>();
    private Map<String, Language> languagesByName = new HashMap<String, Language>();

    @Override
    public boolean isLanguageRegistered(String alias) {
        return this.languages.containsKey(alias);
    }

    @Override
    public Language getLanguage(String name) throws UnknownLanguageException {
        Language language = this.languagesByName.get(name);
        if (language == null) {
            language = this.languages.get(name);
        }
        if (language == null) {
            throw new UnknownLanguageException(name);
        }
        return language;
    }

    @Override
    public String getWebResourceForLanguage(String alias) throws UnknownLanguageException {
        Language lang = this.languages.get(alias);
        if (lang == null) {
            throw new UnknownLanguageException(alias);
        }
        return lang.getWebResource();
    }

    @Override
    public List<Language> listLanguages() {
        return Lists.newArrayList(this.languagesByName.values());
    }

    @Override
    public void addLanguage(Language language) throws DuplicateLanguageException {
        for (Language lang : this.listLanguages()) {
            if (!lang.getName().equals(language.getName())) continue;
            throw new DuplicateLanguageException("newcode.language.register.duplicate.name", language.getName());
        }
        for (String alias : language.getAliases()) {
            if (!this.isLanguageRegistered(alias)) continue;
            throw new DuplicateLanguageException("newcode.language.register.duplicate.alias", alias);
        }
        for (String alias : language.getAliases()) {
            this.languages.put(alias, language);
        }
        this.languagesByName.put(language.getName(), language);
    }

    @Override
    public void unregisterLanguage(String name) {
        this.languages.entrySet().removeIf(entry -> ((Language)entry.getValue()).getName().equals(name) && !((Language)entry.getValue()).isBuiltIn());
        this.languagesByName.remove(name);
    }
}

