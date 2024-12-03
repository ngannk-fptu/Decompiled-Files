/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.applinks.internal.common.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.Validate;

public class StringTruncator {
    private static final int DEFAULT_MAX = 1000;
    private static final String LINE_ELLIPSIS = " (...)";
    private static final String FULL_ELLIPSIS_FORMAT = "(%d more lines ...)";
    private final Reader reader;
    private final int maxLines;
    private final int maxCharsInLine;

    private StringTruncator(Reader source, int maxLines, int maxCharsInLine) {
        Objects.requireNonNull(source, "source");
        Validate.isTrue((maxLines > 0 ? 1 : 0) != 0, (String)"maxLines must be greater than 0, was: %d", (long)maxLines);
        Validate.isTrue((maxCharsInLine > 0 ? 1 : 0) != 0, (String)"maxCharsInLine must be greater than 0, was: %d", (long)maxCharsInLine);
        this.reader = source;
        this.maxLines = maxLines;
        this.maxCharsInLine = maxCharsInLine;
    }

    @Nonnull
    public static StringTruncator forInput(@Nonnull Reader source) {
        return new StringTruncator(source, 1000, 1000);
    }

    public static StringTruncator forInput(@Nonnull String source) {
        return StringTruncator.forInput(new StringReader(Objects.requireNonNull(source, "source")));
    }

    @Nonnull
    public StringTruncator maxLines(int maxLines) {
        return new StringTruncator(this.reader, maxLines, this.maxCharsInLine);
    }

    @Nonnull
    public StringTruncator maxCharsInLine(int maxCharsInLine) {
        return new StringTruncator(this.reader, this.maxLines, maxCharsInLine);
    }

    @Nonnull
    public String truncate() {
        try {
            BufferedReader bufferedSource = this.createBufferedSource();
            StringBuilder builder = new StringBuilder();
            boolean eosReached = false;
            for (int lines = 0; lines < this.maxLines - 1; ++lines) {
                String line = bufferedSource.readLine();
                if (line != null) {
                    if (lines > 0) {
                        builder.append(System.lineSeparator());
                    }
                } else {
                    eosReached = true;
                    break;
                }
                builder.append(this.truncateLine(line));
            }
            if (!eosReached) {
                String line;
                int extraLines = 0;
                String lastLine = null;
                while ((line = bufferedSource.readLine()) != null) {
                    lastLine = line;
                    ++extraLines;
                }
                if (extraLines == 1) {
                    builder.append(System.lineSeparator()).append(lastLine);
                } else if (extraLines > 1) {
                    builder.append(System.lineSeparator()).append(String.format(FULL_ELLIPSIS_FORMAT, extraLines));
                }
            }
            return builder.toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader createBufferedSource() {
        return this.reader instanceof BufferedReader ? (BufferedReader)this.reader : new BufferedReader(this.reader);
    }

    private String truncateLine(String line) {
        if (line.length() <= this.maxCharsInLine) {
            return line;
        }
        return line.substring(0, this.maxCharsInLine - LINE_ELLIPSIS.length()) + LINE_ELLIPSIS;
    }
}

