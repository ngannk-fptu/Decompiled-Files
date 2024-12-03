/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.scheduler.cron.CronSyntaxException$Builder
 *  com.atlassian.scheduler.cron.ErrorCode
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.cron.parser;

import com.atlassian.scheduler.caesium.cron.parser.TokenType;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.scheduler.cron.ErrorCode;
import java.util.Objects;
import javax.annotation.Nullable;

class CronLexer {
    private final String cronExpression;
    private Token peeked;
    private int pos;

    CronLexer(String cronExpression) {
        this.cronExpression = Objects.requireNonNull(cronExpression, "cronExpression");
    }

    boolean hasMoreTokens() {
        return this.peekToken().getType() != TokenType.NOTHING;
    }

    Token peekToken() {
        if (this.peeked == null) {
            this.peeked = this.nextTokenInternal();
        }
        return this.peeked;
    }

    Token nextToken() {
        if (this.peeked != null) {
            Token token = this.peeked;
            this.peeked = null;
            return token;
        }
        return this.nextTokenInternal();
    }

    private Token nextTokenInternal() {
        int len = this.cronExpression.length();
        if (this.pos >= len) {
            return new Token(TokenType.NOTHING, len, len);
        }
        return this.nextToken(this.cronExpression.charAt(this.pos));
    }

    void moveTo(Token token) {
        this.peeked = token;
        this.pos = token.getEnd();
    }

    private Token nextToken(char c) {
        switch (c) {
            case ',': {
                return this.token(TokenType.COMMA);
            }
            case '-': {
                return this.token(TokenType.HYPHEN);
            }
            case '*': {
                return this.token(TokenType.ASTERISK);
            }
            case '/': {
                return this.token(TokenType.SLASH);
            }
            case '?': {
                return this.token(TokenType.QUESTION_MARK);
            }
            case '#': {
                return this.token(TokenType.HASH);
            }
            case '\t': 
            case ' ': {
                return this.whitespace();
            }
        }
        if (CronLexer.isDigit(c)) {
            return this.number();
        }
        if (CronLexer.isUpper(c)) {
            return this.name();
        }
        return this.token(TokenType.INVALID);
    }

    private Token token(TokenType type) {
        return this.token(type, this.pos + 1);
    }

    private Token token(TokenType type, int end) {
        Token token = new Token(type, this.pos, end);
        this.pos = end;
        return token;
    }

    private Token number() {
        int i;
        int len = this.cronExpression.length();
        for (i = this.pos + 1; i < len && CronLexer.isDigit(this.cronExpression.charAt(i)); ++i) {
        }
        return this.token(TokenType.NUMBER, i);
    }

    private Token whitespace() {
        int i;
        int len = this.cronExpression.length();
        for (i = this.pos + 1; i < len && CronLexer.isSpace(this.cronExpression.charAt(i)); ++i) {
        }
        return this.token(TokenType.WHITESPACE, i);
    }

    private Token name() {
        int i;
        int len = this.cronExpression.length();
        for (i = this.pos + 1; i < len && CronLexer.isUpper(this.cronExpression.charAt(i)); ++i) {
        }
        return this.name(i);
    }

    private Token name(int end) {
        switch (end - this.pos) {
            case 1: {
                return this.upperLen1();
            }
            case 2: {
                return this.upperLen2();
            }
        }
        return this.token(TokenType.NAME, end);
    }

    private Token upperLen1() {
        switch (this.cronExpression.charAt(this.pos)) {
            case 'L': {
                return this.token(TokenType.FLAG_L);
            }
            case 'W': {
                return this.token(TokenType.FLAG_W);
            }
        }
        return this.token(TokenType.NAME);
    }

    private Token upperLen2() {
        if (this.cronExpression.charAt(this.pos) == 'L' && this.cronExpression.charAt(this.pos + 1) == 'W') {
            return this.token(TokenType.FLAG_L);
        }
        return this.token(TokenType.NAME, this.pos + 2);
    }

    private static boolean isSpace(char c) {
        return c == ' ' || c == '\t';
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    class Token {
        private final TokenType type;
        private final int start;
        private final int end;

        Token(TokenType type, int start, int end) {
            this.type = Objects.requireNonNull(type, "type");
            this.start = start;
            this.end = end;
        }

        String getCronExpression() {
            return CronLexer.this.cronExpression;
        }

        TokenType getType() {
            return this.type;
        }

        int getStart() {
            return this.start;
        }

        int getEnd() {
            return this.end;
        }

        String getText() {
            if (this.start == this.end) {
                return "";
            }
            return CronLexer.this.cronExpression.substring(this.start, this.end);
        }

        char getChar() {
            if (this.start == this.end) {
                throw new IllegalStateException("Called getChar() on zero-length token: " + this);
            }
            return CronLexer.this.cronExpression.charAt(this.start);
        }

        public boolean equals(@Nullable Object o) {
            return this == o || o instanceof Token && this.equals((Token)o);
        }

        private boolean equals(Token other) {
            return this.type == other.type && this.start == other.start && this.end == other.end;
        }

        public int hashCode() {
            int result = this.type.hashCode();
            result = 31 * result + this.start;
            result = 31 * result + this.end;
            return result;
        }

        public String toString() {
            return "Token[" + (Object)((Object)this.type) + '[' + this.getText() + "],start=" + this.start + ']';
        }

        CronSyntaxException unexpected() {
            switch (this.type) {
                case HASH: {
                    return this.syntaxError(ErrorCode.UNEXPECTED_TOKEN_HASH).build();
                }
                case FLAG_L: {
                    return this.syntaxError(ErrorCode.UNEXPECTED_TOKEN_FLAG_L).build();
                }
                case FLAG_W: {
                    return this.syntaxError(ErrorCode.UNEXPECTED_TOKEN_FLAG_W).build();
                }
                case NOTHING: {
                    return this.syntaxError(ErrorCode.UNEXPECTED_END_OF_EXPRESSION).build();
                }
            }
            return this.syntaxError(ErrorCode.ILLEGAL_CHARACTER).value(CronLexer.this.cronExpression.charAt(this.start)).build();
        }

        private CronSyntaxException.Builder syntaxError(ErrorCode errorCode) {
            return CronSyntaxException.builder().cronExpression(CronLexer.this.cronExpression).errorCode(errorCode).errorOffset(this.start);
        }
    }
}

