/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Beanable
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 */
package com.atlassian.confluence.ext.code.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.LanguageRegistry;
import java.util.Comparator;
import java.util.List;

public class GetLanguagesAction
extends ConfluenceActionSupport
implements Beanable {
    private LanguageRegistry languageRegistry;

    public void setLanguageRegistry(LanguageRegistry languageRegistry) {
        this.languageRegistry = languageRegistry;
    }

    public Object getBean() {
        List<Language> languages = this.languageRegistry.listLanguages();
        languages.sort(Comparator.comparing(Language::getFriendlyName));
        return languages;
    }

    public String execute() throws Exception {
        return "success";
    }
}

