/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.supercsv.comment.CommentMatcher;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.AbstractTokenizer;
import org.supercsv.prefs.CsvPreference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Tokenizer
extends AbstractTokenizer {
    private static final char NEWLINE = '\n';
    private static final char SPACE = ' ';
    private final StringBuilder currentColumn = new StringBuilder();
    private final StringBuilder currentRow = new StringBuilder();
    private final int quoteChar;
    private final int delimeterChar;
    private final boolean surroundingSpacesNeedQuotes;
    private final CommentMatcher commentMatcher;

    public Tokenizer(Reader reader, CsvPreference preferences) {
        super(reader, preferences);
        this.quoteChar = preferences.getQuoteChar();
        this.delimeterChar = preferences.getDelimiterChar();
        this.surroundingSpacesNeedQuotes = preferences.isSurroundingSpacesNeedQuotes();
        this.commentMatcher = preferences.getCommentMatcher();
    }

    @Override
    public boolean readColumns(List<String> columns) throws IOException {
        String line;
        if (columns == null) {
            throw new NullPointerException("columns should not be null");
        }
        columns.clear();
        this.currentColumn.setLength(0);
        this.currentRow.setLength(0);
        do {
            if ((line = this.readLine()) != null) continue;
            return false;
        } while (line.length() == 0 || this.commentMatcher != null && this.commentMatcher.isComment(line));
        this.currentRow.append(line);
        line = line + '\n';
        TokenizerState state = TokenizerState.NORMAL;
        int quoteScopeStartingLine = -1;
        int potentialSpaces = 0;
        int charIndex = 0;
        while (true) {
            char c = line.charAt(charIndex);
            if (TokenizerState.NORMAL.equals((Object)state)) {
                if (c == this.delimeterChar) {
                    if (!this.surroundingSpacesNeedQuotes) {
                        Tokenizer.appendSpaces(this.currentColumn, potentialSpaces);
                    }
                    columns.add(this.currentColumn.length() > 0 ? this.currentColumn.toString() : null);
                    potentialSpaces = 0;
                    this.currentColumn.setLength(0);
                } else if (c == ' ') {
                    ++potentialSpaces;
                } else {
                    if (c == '\n') {
                        if (!this.surroundingSpacesNeedQuotes) {
                            Tokenizer.appendSpaces(this.currentColumn, potentialSpaces);
                        }
                        columns.add(this.currentColumn.length() > 0 ? this.currentColumn.toString() : null);
                        return true;
                    }
                    if (c == this.quoteChar) {
                        state = TokenizerState.QUOTE_MODE;
                        quoteScopeStartingLine = this.getLineNumber();
                        if (!this.surroundingSpacesNeedQuotes || this.currentColumn.length() > 0) {
                            Tokenizer.appendSpaces(this.currentColumn, potentialSpaces);
                        }
                        potentialSpaces = 0;
                    } else {
                        if (!this.surroundingSpacesNeedQuotes || this.currentColumn.length() > 0) {
                            Tokenizer.appendSpaces(this.currentColumn, potentialSpaces);
                        }
                        potentialSpaces = 0;
                        this.currentColumn.append(c);
                    }
                }
            } else if (c == '\n') {
                this.currentColumn.append('\n');
                this.currentRow.append('\n');
                charIndex = -1;
                line = this.readLine();
                if (line == null) {
                    throw new SuperCsvException(String.format("unexpected end of file while reading quoted column beginning on line %d and ending on line %d", quoteScopeStartingLine, this.getLineNumber()));
                }
                this.currentRow.append(line);
                line = line + '\n';
            } else if (c == this.quoteChar) {
                if (line.charAt(charIndex + 1) == this.quoteChar) {
                    this.currentColumn.append(c);
                    ++charIndex;
                } else {
                    state = TokenizerState.NORMAL;
                    quoteScopeStartingLine = -1;
                }
            } else {
                this.currentColumn.append(c);
            }
            ++charIndex;
        }
    }

    private static void appendSpaces(StringBuilder sb, int spaces) {
        for (int i = 0; i < spaces; ++i) {
            sb.append(' ');
        }
    }

    @Override
    public String getUntokenizedRow() {
        return this.currentRow.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum TokenizerState {
        NORMAL,
        QUOTE_MODE;

    }
}

