/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.io.File;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.types.selectors.TokenizedPath;

public class TokenizedPattern {
    public static final TokenizedPattern EMPTY_PATTERN = new TokenizedPattern("", new String[0]);
    private final String pattern;
    private final String[] tokenizedPattern;

    public TokenizedPattern(String pattern) {
        this(pattern, SelectorUtils.tokenizePathAsArray(pattern));
    }

    TokenizedPattern(String pattern, String[] tokens) {
        this.pattern = pattern;
        this.tokenizedPattern = tokens;
    }

    public boolean matchPath(TokenizedPath path, boolean isCaseSensitive) {
        return SelectorUtils.matchPath(this.tokenizedPattern, path.getTokens(), isCaseSensitive);
    }

    public boolean matchStartOf(TokenizedPath path, boolean caseSensitive) {
        return SelectorUtils.matchPatternStart(this.tokenizedPattern, path.getTokens(), caseSensitive);
    }

    public String toString() {
        return this.pattern;
    }

    public String getPattern() {
        return this.pattern;
    }

    public boolean equals(Object o) {
        return o instanceof TokenizedPattern && this.pattern.equals(((TokenizedPattern)o).pattern);
    }

    public int hashCode() {
        return this.pattern.hashCode();
    }

    public int depth() {
        return this.tokenizedPattern.length;
    }

    public boolean containsPattern(String pat) {
        return Stream.of(this.tokenizedPattern).anyMatch(Predicate.isEqual(pat));
    }

    public TokenizedPath rtrimWildcardTokens() {
        int newLen;
        StringBuilder sb = new StringBuilder();
        for (newLen = 0; newLen < this.tokenizedPattern.length && !SelectorUtils.hasWildcards(this.tokenizedPattern[newLen]); ++newLen) {
            if (newLen > 0 && sb.charAt(sb.length() - 1) != File.separatorChar) {
                sb.append(File.separator);
            }
            sb.append(this.tokenizedPattern[newLen]);
        }
        if (newLen == 0) {
            return TokenizedPath.EMPTY_PATH;
        }
        String[] newPats = new String[newLen];
        System.arraycopy(this.tokenizedPattern, 0, newPats, 0, newLen);
        return new TokenizedPath(sb.toString(), newPats);
    }

    public boolean endsWith(String s) {
        return this.tokenizedPattern.length > 0 && this.tokenizedPattern[this.tokenizedPattern.length - 1].equals(s);
    }

    public TokenizedPattern withoutLastToken() {
        if (this.tokenizedPattern.length == 0) {
            throw new IllegalStateException("can't strip a token from nothing");
        }
        if (this.tokenizedPattern.length == 1) {
            return EMPTY_PATTERN;
        }
        String toStrip = this.tokenizedPattern[this.tokenizedPattern.length - 1];
        int index = this.pattern.lastIndexOf(toStrip);
        String[] tokens = new String[this.tokenizedPattern.length - 1];
        System.arraycopy(this.tokenizedPattern, 0, tokens, 0, this.tokenizedPattern.length - 1);
        return new TokenizedPattern(this.pattern.substring(0, index), tokens);
    }
}

