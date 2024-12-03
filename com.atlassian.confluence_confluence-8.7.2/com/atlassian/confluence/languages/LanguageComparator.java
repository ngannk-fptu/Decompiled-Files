/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.languages;

import com.atlassian.confluence.languages.Language;
import java.text.Collator;
import java.util.Comparator;

public class LanguageComparator
implements Comparator<Language> {
    private Collator collator = Collator.getInstance();

    @Override
    public int compare(Language o1, Language o2) {
        return this.collator.compare(o1.getDisplayLanguage(), o2.getDisplayLanguage());
    }
}

