/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.subst;

import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.subst.Token;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    final String pattern;
    final int patternLength;
    TokenizerState state = TokenizerState.LITERAL_STATE;
    int pointer = 0;

    public Tokenizer(String pattern) {
        this.pattern = pattern;
        this.patternLength = pattern.length();
    }

    List<Token> tokenize() throws ScanException {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        StringBuilder buf = new StringBuilder();
        while (this.pointer < this.patternLength) {
            char c = this.pattern.charAt(this.pointer);
            ++this.pointer;
            switch (this.state.ordinal()) {
                case 0: {
                    this.handleLiteralState(c, tokenList, buf);
                    break;
                }
                case 1: {
                    this.handleStartState(c, tokenList, buf);
                    break;
                }
                case 2: {
                    this.handleDefaultValueState(c, tokenList, buf);
                }
            }
        }
        switch (this.state.ordinal()) {
            case 0: {
                this.addLiteralToken(tokenList, buf);
                break;
            }
            case 2: {
                buf.append(':');
                this.addLiteralToken(tokenList, buf);
                break;
            }
            case 1: {
                buf.append('$');
                this.addLiteralToken(tokenList, buf);
            }
        }
        return tokenList;
    }

    private void handleDefaultValueState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        switch (c) {
            case '-': {
                tokenList.add(Token.DEFAULT_SEP_TOKEN);
                this.state = TokenizerState.LITERAL_STATE;
                break;
            }
            case '$': {
                stringBuilder.append(':');
                this.addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                this.state = TokenizerState.START_STATE;
                break;
            }
            case '{': {
                stringBuilder.append(':');
                this.addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                tokenList.add(Token.CURLY_LEFT_TOKEN);
                this.state = TokenizerState.LITERAL_STATE;
                break;
            }
            default: {
                stringBuilder.append(':').append(c);
                this.state = TokenizerState.LITERAL_STATE;
            }
        }
    }

    private void handleStartState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        if (c == '{') {
            tokenList.add(Token.START_TOKEN);
        } else {
            stringBuilder.append('$').append(c);
        }
        this.state = TokenizerState.LITERAL_STATE;
    }

    private void handleLiteralState(char c, List<Token> tokenList, StringBuilder stringBuilder) {
        switch (c) {
            case '$': {
                this.addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                this.state = TokenizerState.START_STATE;
                break;
            }
            case ':': {
                this.addLiteralToken(tokenList, stringBuilder);
                stringBuilder.setLength(0);
                this.state = TokenizerState.DEFAULT_VAL_STATE;
                break;
            }
            case '{': {
                this.addLiteralToken(tokenList, stringBuilder);
                tokenList.add(Token.CURLY_LEFT_TOKEN);
                stringBuilder.setLength(0);
                break;
            }
            case '}': {
                this.addLiteralToken(tokenList, stringBuilder);
                tokenList.add(Token.CURLY_RIGHT_TOKEN);
                stringBuilder.setLength(0);
                break;
            }
            default: {
                stringBuilder.append(c);
            }
        }
    }

    private void addLiteralToken(List<Token> tokenList, StringBuilder stringBuilder) {
        if (stringBuilder.length() == 0) {
            return;
        }
        tokenList.add(new Token(Token.Type.LITERAL, stringBuilder.toString()));
    }

    static enum TokenizerState {
        LITERAL_STATE,
        START_STATE,
        DEFAULT_VAL_STATE;

    }
}

