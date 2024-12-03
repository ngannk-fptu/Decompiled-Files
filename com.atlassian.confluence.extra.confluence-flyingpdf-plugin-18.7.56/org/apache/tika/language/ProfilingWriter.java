/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.language;

import java.io.IOException;
import java.io.Writer;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;

@Deprecated
public class ProfilingWriter
extends Writer {
    private final LanguageProfile profile;
    private char[] buffer = new char[]{'\u0000', '\u0000', '_'};
    private int n = 1;

    public ProfilingWriter(LanguageProfile profile) {
        this.profile = profile;
    }

    public ProfilingWriter() {
        this(new LanguageProfile());
    }

    public LanguageProfile getProfile() {
        return this.profile;
    }

    public LanguageIdentifier getLanguage() {
        return new LanguageIdentifier(this.profile);
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        for (int i = 0; i < len; ++i) {
            char c = Character.toLowerCase(cbuf[off + i]);
            if (Character.isLetter(c)) {
                this.addLetter(c);
                continue;
            }
            this.addSeparator();
        }
    }

    private void addLetter(char c) {
        System.arraycopy(this.buffer, 1, this.buffer, 0, this.buffer.length - 1);
        this.buffer[this.buffer.length - 1] = c;
        ++this.n;
        if (this.n >= this.buffer.length) {
            this.profile.add(new String(this.buffer));
        }
    }

    private void addSeparator() {
        this.addLetter('_');
        this.n = 1;
    }

    @Override
    public void close() throws IOException {
        this.addSeparator();
    }

    @Override
    public void flush() {
    }
}

