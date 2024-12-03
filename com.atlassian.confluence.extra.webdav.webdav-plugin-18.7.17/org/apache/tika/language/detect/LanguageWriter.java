/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language.detect;

import java.io.IOException;
import java.io.Writer;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

public class LanguageWriter
extends Writer {
    private final LanguageDetector detector;

    public LanguageWriter(LanguageDetector detector) {
        this.detector = detector;
        detector.reset();
    }

    public LanguageDetector getDetector() {
        return this.detector;
    }

    public LanguageResult getLanguage() {
        return this.detector.detect();
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        this.detector.addText(cbuf, off, len);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void flush() {
    }

    public void reset() {
        this.detector.reset();
    }
}

