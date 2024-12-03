/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.text.translate.CharSequenceTranslator;

public class AggregateTranslator
extends CharSequenceTranslator {
    private final List<CharSequenceTranslator> translators = new ArrayList<CharSequenceTranslator>();

    public AggregateTranslator(CharSequenceTranslator ... translators) {
        if (translators != null) {
            Stream.of(translators).filter(Objects::nonNull).forEach(this.translators::add);
        }
    }

    @Override
    public int translate(CharSequence input, int index, Writer writer) throws IOException {
        for (CharSequenceTranslator translator : this.translators) {
            int consumed = translator.translate(input, index, writer);
            if (consumed == 0) continue;
            return consumed;
        }
        return 0;
    }
}

