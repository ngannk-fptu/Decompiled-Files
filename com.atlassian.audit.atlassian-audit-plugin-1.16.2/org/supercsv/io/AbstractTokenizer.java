/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;

public abstract class AbstractTokenizer
implements ITokenizer {
    private final CsvPreference preferences;
    private final LineNumberReader lnr;

    public AbstractTokenizer(Reader reader, CsvPreference preferences) {
        if (reader == null) {
            throw new NullPointerException("reader should not be null");
        }
        if (preferences == null) {
            throw new NullPointerException("preferences should not be null");
        }
        this.preferences = preferences;
        this.lnr = new LineNumberReader(reader);
    }

    public void close() throws IOException {
        this.lnr.close();
    }

    public int getLineNumber() {
        return this.lnr.getLineNumber();
    }

    protected String readLine() throws IOException {
        return this.lnr.readLine();
    }

    protected CsvPreference getPreferences() {
        return this.preferences;
    }
}

