/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language.detect;

import java.io.IOException;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.language.detect.LanguageWriter;
import org.apache.tika.sax.WriteOutContentHandler;

public class LanguageHandler
extends WriteOutContentHandler {
    private final LanguageWriter writer;

    public LanguageHandler() throws IOException {
        this(new LanguageWriter(LanguageDetector.getDefaultLanguageDetector().loadModels()));
    }

    public LanguageHandler(LanguageWriter writer) {
        super(writer);
        this.writer = writer;
    }

    public LanguageHandler(LanguageDetector detector) {
        this(new LanguageWriter(detector));
    }

    public LanguageDetector getDetector() {
        return this.writer.getDetector();
    }

    public LanguageResult getLanguage() {
        return this.writer.getLanguage();
    }
}

