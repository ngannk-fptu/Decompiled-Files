/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.tika.config.ServiceLoader;
import org.apache.tika.extractor.EmbeddedStreamTranslator;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.utils.ServiceLoaderUtils;

public class DefaultEmbeddedStreamTranslator
implements EmbeddedStreamTranslator {
    final List<EmbeddedStreamTranslator> translators;

    private static List<EmbeddedStreamTranslator> getDefaultFilters(ServiceLoader loader) {
        List<EmbeddedStreamTranslator> embeddedStreamTranslators = loader.loadServiceProviders(EmbeddedStreamTranslator.class);
        ServiceLoaderUtils.sortLoadedClasses(embeddedStreamTranslators);
        return embeddedStreamTranslators;
    }

    public DefaultEmbeddedStreamTranslator() {
        this(DefaultEmbeddedStreamTranslator.getDefaultFilters(new ServiceLoader()));
    }

    private DefaultEmbeddedStreamTranslator(List<EmbeddedStreamTranslator> translators) {
        this.translators = translators;
    }

    @Override
    public boolean shouldTranslate(InputStream inputStream, Metadata metadata) throws IOException {
        for (EmbeddedStreamTranslator translator : this.translators) {
            if (!translator.shouldTranslate(inputStream, metadata)) continue;
            return true;
        }
        return false;
    }

    @Override
    public InputStream translate(InputStream inputStream, Metadata metadata) throws IOException {
        for (EmbeddedStreamTranslator translator : this.translators) {
            InputStream translated = translator.translate(inputStream, metadata);
            if (translated == null) continue;
            return translated;
        }
        return inputStream;
    }
}

