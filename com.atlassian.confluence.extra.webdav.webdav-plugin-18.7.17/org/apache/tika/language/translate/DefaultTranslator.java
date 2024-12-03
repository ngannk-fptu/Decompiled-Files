/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language.translate;

import java.io.IOException;
import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.translate.Translator;
import org.apache.tika.utils.CompareUtils;

public class DefaultTranslator
implements Translator {
    private final transient ServiceLoader loader;

    public DefaultTranslator(ServiceLoader loader) {
        this.loader = loader;
    }

    public DefaultTranslator() {
        this(new ServiceLoader());
    }

    private static List<Translator> getDefaultTranslators(ServiceLoader loader) {
        List<Translator> translators = loader.loadStaticServiceProviders(Translator.class);
        translators.sort(CompareUtils::compareClassName);
        return translators;
    }

    private static Translator getFirstAvailable(ServiceLoader loader) {
        for (Translator t : DefaultTranslator.getDefaultTranslators(loader)) {
            if (!t.isAvailable()) continue;
            return t;
        }
        return null;
    }

    @Override
    public String translate(String text, String sourceLanguage, String targetLanguage) throws TikaException, IOException {
        Translator t = DefaultTranslator.getFirstAvailable(this.loader);
        if (t != null) {
            return t.translate(text, sourceLanguage, targetLanguage);
        }
        throw new TikaException("No translators currently available");
    }

    @Override
    public String translate(String text, String targetLanguage) throws TikaException, IOException {
        Translator t = DefaultTranslator.getFirstAvailable(this.loader);
        if (t != null) {
            return t.translate(text, targetLanguage);
        }
        throw new TikaException("No translators currently available");
    }

    public List<Translator> getTranslators() {
        return DefaultTranslator.getDefaultTranslators(this.loader);
    }

    public Translator getTranslator() {
        return DefaultTranslator.getFirstAvailable(this.loader);
    }

    @Override
    public boolean isAvailable() {
        return DefaultTranslator.getFirstAvailable(this.loader) != null;
    }
}

