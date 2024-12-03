/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.valves.rewrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.tomcat.util.res.StringManager;

public class QuotedStringTokenizer {
    protected static final StringManager sm = StringManager.getManager(QuotedStringTokenizer.class);
    private Iterator<String> tokenIterator;
    private int tokenCount;
    private int returnedTokens = 0;

    public QuotedStringTokenizer(String text) {
        List<Object> tokens = text != null ? this.tokenizeText(text) : Collections.emptyList();
        this.tokenCount = tokens.size();
        this.tokenIterator = tokens.iterator();
    }

    private List<String> tokenizeText(String inputText) {
        ArrayList<String> tokens = new ArrayList<String>();
        int length = inputText.length();
        WordMode currentMode = WordMode.SPACES;
        StringBuilder currentToken = new StringBuilder();
        block7: for (int pos = 0; pos < length; ++pos) {
            char currentChar = inputText.charAt(pos);
            switch (currentMode) {
                case SPACES: {
                    currentMode = this.handleSpaces(currentToken, currentChar);
                    continue block7;
                }
                case QUOTED: {
                    currentMode = this.handleQuoted(tokens, currentToken, currentChar);
                    continue block7;
                }
                case ESCAPED: {
                    currentToken.append(currentChar);
                    currentMode = WordMode.QUOTED;
                    continue block7;
                }
                case SIMPLE: {
                    currentMode = this.handleSimple(tokens, currentToken, currentChar);
                    continue block7;
                }
                case COMMENT: {
                    if (currentChar != '\r' && currentChar != '\n') continue block7;
                    currentMode = WordMode.SPACES;
                    continue block7;
                }
                default: {
                    throw new IllegalStateException(sm.getString("quotedStringTokenizer.tokenizeError", new Object[]{inputText, pos, currentMode}));
                }
            }
        }
        String possibleLastToken = currentToken.toString();
        if (!possibleLastToken.isEmpty()) {
            tokens.add(possibleLastToken);
        }
        return tokens;
    }

    private WordMode handleSimple(List<String> tokens, StringBuilder currentToken, char currentChar) {
        if (Character.isWhitespace(currentChar)) {
            tokens.add(currentToken.toString());
            currentToken.setLength(0);
            return WordMode.SPACES;
        }
        currentToken.append(currentChar);
        return WordMode.SIMPLE;
    }

    private WordMode handleQuoted(List<String> tokens, StringBuilder currentToken, char currentChar) {
        if (currentChar == '\"') {
            tokens.add(currentToken.toString());
            currentToken.setLength(0);
            return WordMode.SPACES;
        }
        if (currentChar == '\\') {
            return WordMode.ESCAPED;
        }
        currentToken.append(currentChar);
        return WordMode.QUOTED;
    }

    private WordMode handleSpaces(StringBuilder currentToken, char currentChar) {
        if (!Character.isWhitespace(currentChar)) {
            if (currentChar == '\"') {
                return WordMode.QUOTED;
            }
            if (currentChar == '#') {
                return WordMode.COMMENT;
            }
            currentToken.append(currentChar);
            return WordMode.SIMPLE;
        }
        return WordMode.SPACES;
    }

    public boolean hasMoreTokens() {
        return this.tokenIterator.hasNext();
    }

    public String nextToken() {
        ++this.returnedTokens;
        return this.tokenIterator.next();
    }

    public int countTokens() {
        return this.tokenCount - this.returnedTokens;
    }

    static enum WordMode {
        SPACES,
        QUOTED,
        ESCAPED,
        SIMPLE,
        COMMENT;

    }
}

