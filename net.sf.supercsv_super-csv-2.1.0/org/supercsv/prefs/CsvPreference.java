/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.prefs;

import org.supercsv.comment.CommentMatcher;
import org.supercsv.encoder.CsvEncoder;
import org.supercsv.encoder.DefaultCsvEncoder;
import org.supercsv.quote.NormalQuoteMode;
import org.supercsv.quote.QuoteMode;

public final class CsvPreference {
    public static final CsvPreference STANDARD_PREFERENCE = new Builder('\"', 44, "\r\n").build();
    public static final CsvPreference EXCEL_PREFERENCE = new Builder('\"', 44, "\n").build();
    public static final CsvPreference EXCEL_NORTH_EUROPE_PREFERENCE = new Builder('\"', 59, "\n").build();
    public static final CsvPreference TAB_PREFERENCE = new Builder('\"', 9, "\n").build();
    private final char quoteChar;
    private final int delimiterChar;
    private final String endOfLineSymbols;
    private final boolean surroundingSpacesNeedQuotes;
    private final CsvEncoder encoder;
    private final QuoteMode quoteMode;
    private final CommentMatcher commentMatcher;

    private CsvPreference(Builder builder) {
        this.quoteChar = builder.quoteChar;
        this.delimiterChar = builder.delimiterChar;
        this.endOfLineSymbols = builder.endOfLineSymbols;
        this.surroundingSpacesNeedQuotes = builder.surroundingSpacesNeedQuotes;
        this.commentMatcher = builder.commentMatcher;
        this.encoder = builder.encoder;
        this.quoteMode = builder.quoteMode;
    }

    public int getDelimiterChar() {
        return this.delimiterChar;
    }

    public String getEndOfLineSymbols() {
        return this.endOfLineSymbols;
    }

    public int getQuoteChar() {
        return this.quoteChar;
    }

    public boolean isSurroundingSpacesNeedQuotes() {
        return this.surroundingSpacesNeedQuotes;
    }

    public CsvEncoder getEncoder() {
        return this.encoder;
    }

    public QuoteMode getQuoteMode() {
        return this.quoteMode;
    }

    public CommentMatcher getCommentMatcher() {
        return this.commentMatcher;
    }

    public static class Builder {
        private final char quoteChar;
        private final int delimiterChar;
        private final String endOfLineSymbols;
        private boolean surroundingSpacesNeedQuotes = false;
        private CsvEncoder encoder;
        private QuoteMode quoteMode;
        private CommentMatcher commentMatcher;

        public Builder(CsvPreference preference) {
            this.quoteChar = preference.quoteChar;
            this.delimiterChar = preference.delimiterChar;
            this.endOfLineSymbols = preference.endOfLineSymbols;
            this.surroundingSpacesNeedQuotes = preference.surroundingSpacesNeedQuotes;
            this.encoder = preference.encoder;
            this.quoteMode = preference.quoteMode;
            this.commentMatcher = preference.commentMatcher;
        }

        public Builder(char quoteChar, int delimiterChar, String endOfLineSymbols) {
            if (quoteChar == delimiterChar) {
                throw new IllegalArgumentException(String.format("quoteChar and delimiterChar should not be the same character: %c", Character.valueOf(quoteChar)));
            }
            if (endOfLineSymbols == null) {
                throw new NullPointerException("endOfLineSymbols should not be null");
            }
            this.quoteChar = quoteChar;
            this.delimiterChar = delimiterChar;
            this.endOfLineSymbols = endOfLineSymbols;
        }

        public Builder surroundingSpacesNeedQuotes(boolean surroundingSpacesNeedQuotes) {
            this.surroundingSpacesNeedQuotes = surroundingSpacesNeedQuotes;
            return this;
        }

        public Builder skipComments(CommentMatcher commentMatcher) {
            if (commentMatcher == null) {
                throw new NullPointerException("commentMatcher should not be null");
            }
            this.commentMatcher = commentMatcher;
            return this;
        }

        public Builder useEncoder(CsvEncoder encoder) {
            if (encoder == null) {
                throw new NullPointerException("encoder should not be null");
            }
            this.encoder = encoder;
            return this;
        }

        public Builder useQuoteMode(QuoteMode quoteMode) {
            if (quoteMode == null) {
                throw new NullPointerException("quoteMode should not be null");
            }
            this.quoteMode = quoteMode;
            return this;
        }

        public CsvPreference build() {
            if (this.encoder == null) {
                this.encoder = new DefaultCsvEncoder();
            }
            if (this.quoteMode == null) {
                this.quoteMode = new NormalQuoteMode();
            }
            return new CsvPreference(this);
        }
    }
}

