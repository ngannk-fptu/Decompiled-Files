/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language.translate;

import org.apache.tika.language.translate.Translator;

public class EmptyTranslator
implements Translator {
    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) {
        return null;
    }

    @Override
    public String translate(String text, String targetLanguage) {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}

