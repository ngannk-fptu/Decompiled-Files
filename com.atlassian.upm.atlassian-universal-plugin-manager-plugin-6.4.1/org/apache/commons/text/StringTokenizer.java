/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.commons.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.TextStringBuilder;
import org.apache.commons.text.matcher.StringMatcher;
import org.apache.commons.text.matcher.StringMatcherFactory;

public class StringTokenizer
implements ListIterator<String>,
Cloneable {
    private static final StringTokenizer CSV_TOKENIZER_PROTOTYPE = new StringTokenizer();
    private static final StringTokenizer TSV_TOKENIZER_PROTOTYPE;
    private char[] chars;
    private String[] tokens;
    private int tokenPos;
    private StringMatcher delimMatcher = StringMatcherFactory.INSTANCE.splitMatcher();
    private StringMatcher quoteMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
    private StringMatcher ignoredMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
    private StringMatcher trimmerMatcher = StringMatcherFactory.INSTANCE.noneMatcher();
    private boolean emptyAsNull;
    private boolean ignoreEmptyTokens = true;

    private static StringTokenizer getCSVClone() {
        return (StringTokenizer)CSV_TOKENIZER_PROTOTYPE.clone();
    }

    public static StringTokenizer getCSVInstance() {
        return StringTokenizer.getCSVClone();
    }

    public static StringTokenizer getCSVInstance(char[] input) {
        return StringTokenizer.getCSVClone().reset(input);
    }

    public static StringTokenizer getCSVInstance(String input) {
        return StringTokenizer.getCSVClone().reset(input);
    }

    private static StringTokenizer getTSVClone() {
        return (StringTokenizer)TSV_TOKENIZER_PROTOTYPE.clone();
    }

    public static StringTokenizer getTSVInstance() {
        return StringTokenizer.getTSVClone();
    }

    public static StringTokenizer getTSVInstance(char[] input) {
        return StringTokenizer.getTSVClone().reset(input);
    }

    public static StringTokenizer getTSVInstance(String input) {
        return StringTokenizer.getTSVClone().reset(input);
    }

    public StringTokenizer() {
        this.chars = null;
    }

    public StringTokenizer(char[] input) {
        this.chars = input != null ? (char[])input.clone() : null;
    }

    public StringTokenizer(char[] input, char delim) {
        this(input);
        this.setDelimiterChar(delim);
    }

    public StringTokenizer(char[] input, char delim, char quote) {
        this(input, delim);
        this.setQuoteChar(quote);
    }

    public StringTokenizer(char[] input, String delim) {
        this(input);
        this.setDelimiterString(delim);
    }

    public StringTokenizer(char[] input, StringMatcher delim) {
        this(input);
        this.setDelimiterMatcher(delim);
    }

    public StringTokenizer(char[] input, StringMatcher delim, StringMatcher quote) {
        this(input, delim);
        this.setQuoteMatcher(quote);
    }

    public StringTokenizer(String input) {
        this.chars = input != null ? input.toCharArray() : null;
    }

    public StringTokenizer(String input, char delim) {
        this(input);
        this.setDelimiterChar(delim);
    }

    public StringTokenizer(String input, char delim, char quote) {
        this(input, delim);
        this.setQuoteChar(quote);
    }

    public StringTokenizer(String input, String delim) {
        this(input);
        this.setDelimiterString(delim);
    }

    public StringTokenizer(String input, StringMatcher delim) {
        this(input);
        this.setDelimiterMatcher(delim);
    }

    public StringTokenizer(String input, StringMatcher delim, StringMatcher quote) {
        this(input, delim);
        this.setQuoteMatcher(quote);
    }

    @Override
    public void add(String obj) {
        throw new UnsupportedOperationException("add() is unsupported");
    }

    private void addToken(List<String> list, String tok) {
        if (tok == null || tok.isEmpty()) {
            if (this.isIgnoreEmptyTokens()) {
                return;
            }
            if (this.isEmptyTokenAsNull()) {
                tok = null;
            }
        }
        list.add(tok);
    }

    private void checkTokenized() {
        if (this.tokens == null) {
            List<String> split = this.chars == null ? this.tokenize(null, 0, 0) : this.tokenize(this.chars, 0, this.chars.length);
            this.tokens = split.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        }
    }

    public Object clone() {
        try {
            return this.cloneReset();
        }
        catch (CloneNotSupportedException ex) {
            return null;
        }
    }

    Object cloneReset() throws CloneNotSupportedException {
        StringTokenizer cloned = (StringTokenizer)super.clone();
        if (cloned.chars != null) {
            cloned.chars = (char[])cloned.chars.clone();
        }
        cloned.reset();
        return cloned;
    }

    public String getContent() {
        if (this.chars == null) {
            return null;
        }
        return new String(this.chars);
    }

    public StringMatcher getDelimiterMatcher() {
        return this.delimMatcher;
    }

    public StringMatcher getIgnoredMatcher() {
        return this.ignoredMatcher;
    }

    public StringMatcher getQuoteMatcher() {
        return this.quoteMatcher;
    }

    public String[] getTokenArray() {
        this.checkTokenized();
        return (String[])this.tokens.clone();
    }

    public List<String> getTokenList() {
        this.checkTokenized();
        return Arrays.asList(this.tokens);
    }

    public StringMatcher getTrimmerMatcher() {
        return this.trimmerMatcher;
    }

    @Override
    public boolean hasNext() {
        this.checkTokenized();
        return this.tokenPos < this.tokens.length;
    }

    @Override
    public boolean hasPrevious() {
        this.checkTokenized();
        return this.tokenPos > 0;
    }

    public boolean isEmptyTokenAsNull() {
        return this.emptyAsNull;
    }

    public boolean isIgnoreEmptyTokens() {
        return this.ignoreEmptyTokens;
    }

    private boolean isQuote(char[] srcChars, int pos, int len, int quoteStart, int quoteLen) {
        for (int i = 0; i < quoteLen; ++i) {
            if (pos + i < len && srcChars[pos + i] == srcChars[quoteStart + i]) continue;
            return false;
        }
        return true;
    }

    @Override
    public String next() {
        if (this.hasNext()) {
            return this.tokens[this.tokenPos++];
        }
        throw new NoSuchElementException();
    }

    @Override
    public int nextIndex() {
        return this.tokenPos;
    }

    public String nextToken() {
        if (this.hasNext()) {
            return this.tokens[this.tokenPos++];
        }
        return null;
    }

    @Override
    public String previous() {
        if (this.hasPrevious()) {
            return this.tokens[--this.tokenPos];
        }
        throw new NoSuchElementException();
    }

    @Override
    public int previousIndex() {
        return this.tokenPos - 1;
    }

    public String previousToken() {
        if (this.hasPrevious()) {
            return this.tokens[--this.tokenPos];
        }
        return null;
    }

    private int readNextToken(char[] srcChars, int start, int len, TextStringBuilder workArea, List<String> tokenList) {
        int removeLen;
        while (start < len && (removeLen = Math.max(this.getIgnoredMatcher().isMatch(srcChars, start, start, len), this.getTrimmerMatcher().isMatch(srcChars, start, start, len))) != 0 && this.getDelimiterMatcher().isMatch(srcChars, start, start, len) <= 0 && this.getQuoteMatcher().isMatch(srcChars, start, start, len) <= 0) {
            start += removeLen;
        }
        if (start >= len) {
            this.addToken(tokenList, "");
            return -1;
        }
        int delimLen = this.getDelimiterMatcher().isMatch(srcChars, start, start, len);
        if (delimLen > 0) {
            this.addToken(tokenList, "");
            return start + delimLen;
        }
        int quoteLen = this.getQuoteMatcher().isMatch(srcChars, start, start, len);
        if (quoteLen > 0) {
            return this.readWithQuotes(srcChars, start + quoteLen, len, workArea, tokenList, start, quoteLen);
        }
        return this.readWithQuotes(srcChars, start, len, workArea, tokenList, 0, 0);
    }

    private int readWithQuotes(char[] srcChars, int start, int len, TextStringBuilder workArea, List<String> tokenList, int quoteStart, int quoteLen) {
        workArea.clear();
        int pos = start;
        boolean quoting = quoteLen > 0;
        int trimStart = 0;
        while (pos < len) {
            if (quoting) {
                if (this.isQuote(srcChars, pos, len, quoteStart, quoteLen)) {
                    if (this.isQuote(srcChars, pos + quoteLen, len, quoteStart, quoteLen)) {
                        workArea.append(srcChars, pos, quoteLen);
                        pos += quoteLen * 2;
                        trimStart = workArea.size();
                        continue;
                    }
                    quoting = false;
                    pos += quoteLen;
                    continue;
                }
            } else {
                int delimLen = this.getDelimiterMatcher().isMatch(srcChars, pos, start, len);
                if (delimLen > 0) {
                    this.addToken(tokenList, workArea.substring(0, trimStart));
                    return pos + delimLen;
                }
                if (quoteLen > 0 && this.isQuote(srcChars, pos, len, quoteStart, quoteLen)) {
                    quoting = true;
                    pos += quoteLen;
                    continue;
                }
                int ignoredLen = this.getIgnoredMatcher().isMatch(srcChars, pos, start, len);
                if (ignoredLen > 0) {
                    pos += ignoredLen;
                    continue;
                }
                int trimmedLen = this.getTrimmerMatcher().isMatch(srcChars, pos, start, len);
                if (trimmedLen > 0) {
                    workArea.append(srcChars, pos, trimmedLen);
                    pos += trimmedLen;
                    continue;
                }
            }
            workArea.append(srcChars[pos++]);
            trimStart = workArea.size();
        }
        this.addToken(tokenList, workArea.substring(0, trimStart));
        return -1;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is unsupported");
    }

    public StringTokenizer reset() {
        this.tokenPos = 0;
        this.tokens = null;
        return this;
    }

    public StringTokenizer reset(char[] input) {
        this.reset();
        this.chars = input != null ? (char[])input.clone() : null;
        return this;
    }

    public StringTokenizer reset(String input) {
        this.reset();
        this.chars = input != null ? input.toCharArray() : null;
        return this;
    }

    @Override
    public void set(String obj) {
        throw new UnsupportedOperationException("set() is unsupported");
    }

    public StringTokenizer setDelimiterChar(char delim) {
        return this.setDelimiterMatcher(StringMatcherFactory.INSTANCE.charMatcher(delim));
    }

    public StringTokenizer setDelimiterMatcher(StringMatcher delim) {
        this.delimMatcher = delim == null ? StringMatcherFactory.INSTANCE.noneMatcher() : delim;
        return this;
    }

    public StringTokenizer setDelimiterString(String delim) {
        return this.setDelimiterMatcher(StringMatcherFactory.INSTANCE.stringMatcher(delim));
    }

    public StringTokenizer setEmptyTokenAsNull(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
        return this;
    }

    public StringTokenizer setIgnoredChar(char ignored) {
        return this.setIgnoredMatcher(StringMatcherFactory.INSTANCE.charMatcher(ignored));
    }

    public StringTokenizer setIgnoredMatcher(StringMatcher ignored) {
        if (ignored != null) {
            this.ignoredMatcher = ignored;
        }
        return this;
    }

    public StringTokenizer setIgnoreEmptyTokens(boolean ignoreEmptyTokens) {
        this.ignoreEmptyTokens = ignoreEmptyTokens;
        return this;
    }

    public StringTokenizer setQuoteChar(char quote) {
        return this.setQuoteMatcher(StringMatcherFactory.INSTANCE.charMatcher(quote));
    }

    public StringTokenizer setQuoteMatcher(StringMatcher quote) {
        if (quote != null) {
            this.quoteMatcher = quote;
        }
        return this;
    }

    public StringTokenizer setTrimmerMatcher(StringMatcher trimmer) {
        if (trimmer != null) {
            this.trimmerMatcher = trimmer;
        }
        return this;
    }

    public int size() {
        this.checkTokenized();
        return this.tokens.length;
    }

    protected List<String> tokenize(char[] srcChars, int offset, int count) {
        if (srcChars == null || count == 0) {
            return Collections.emptyList();
        }
        TextStringBuilder buf = new TextStringBuilder();
        ArrayList<String> tokenList = new ArrayList<String>();
        int pos = offset;
        while (pos >= 0 && pos < count) {
            if ((pos = this.readNextToken(srcChars, pos, count, buf, tokenList)) < count) continue;
            this.addToken(tokenList, "");
        }
        return tokenList;
    }

    public String toString() {
        if (this.tokens == null) {
            return "StringTokenizer[not tokenized yet]";
        }
        return "StringTokenizer" + this.getTokenList();
    }

    static {
        CSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StringMatcherFactory.INSTANCE.commaMatcher());
        CSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StringMatcherFactory.INSTANCE.doubleQuoteMatcher());
        CSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StringMatcherFactory.INSTANCE.noneMatcher());
        CSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StringMatcherFactory.INSTANCE.trimMatcher());
        CSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        CSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
        TSV_TOKENIZER_PROTOTYPE = new StringTokenizer();
        TSV_TOKENIZER_PROTOTYPE.setDelimiterMatcher(StringMatcherFactory.INSTANCE.tabMatcher());
        TSV_TOKENIZER_PROTOTYPE.setQuoteMatcher(StringMatcherFactory.INSTANCE.doubleQuoteMatcher());
        TSV_TOKENIZER_PROTOTYPE.setIgnoredMatcher(StringMatcherFactory.INSTANCE.noneMatcher());
        TSV_TOKENIZER_PROTOTYPE.setTrimmerMatcher(StringMatcherFactory.INSTANCE.trimMatcher());
        TSV_TOKENIZER_PROTOTYPE.setEmptyTokenAsNull(false);
        TSV_TOKENIZER_PROTOTYPE.setIgnoreEmptyTokens(false);
    }
}

