/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValueType;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigBoolean;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.ConfigNull;
import com.typesafe.config.impl.ConfigNumber;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.TokenType;
import com.typesafe.config.impl.Tokenizer;
import java.util.List;

final class Tokens {
    static final Token START = Token.newWithoutOrigin(TokenType.START, "start of file", "");
    static final Token END = Token.newWithoutOrigin(TokenType.END, "end of file", "");
    static final Token COMMA = Token.newWithoutOrigin(TokenType.COMMA, "','", ",");
    static final Token EQUALS = Token.newWithoutOrigin(TokenType.EQUALS, "'='", "=");
    static final Token COLON = Token.newWithoutOrigin(TokenType.COLON, "':'", ":");
    static final Token OPEN_CURLY = Token.newWithoutOrigin(TokenType.OPEN_CURLY, "'{'", "{");
    static final Token CLOSE_CURLY = Token.newWithoutOrigin(TokenType.CLOSE_CURLY, "'}'", "}");
    static final Token OPEN_SQUARE = Token.newWithoutOrigin(TokenType.OPEN_SQUARE, "'['", "[");
    static final Token CLOSE_SQUARE = Token.newWithoutOrigin(TokenType.CLOSE_SQUARE, "']'", "]");
    static final Token PLUS_EQUALS = Token.newWithoutOrigin(TokenType.PLUS_EQUALS, "'+='", "+=");

    Tokens() {
    }

    static boolean isValue(Token token) {
        return token instanceof Value;
    }

    static AbstractConfigValue getValue(Token token) {
        if (token instanceof Value) {
            return ((Value)token).value();
        }
        throw new ConfigException.BugOrBroken("tried to get value of non-value token " + token);
    }

    static boolean isValueWithType(Token t, ConfigValueType valueType) {
        return Tokens.isValue(t) && Tokens.getValue(t).valueType() == valueType;
    }

    static boolean isNewline(Token token) {
        return token instanceof Line;
    }

    static boolean isProblem(Token token) {
        return token instanceof Problem;
    }

    static String getProblemWhat(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).what();
        }
        throw new ConfigException.BugOrBroken("tried to get problem what from " + token);
    }

    static String getProblemMessage(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).message();
        }
        throw new ConfigException.BugOrBroken("tried to get problem message from " + token);
    }

    static boolean getProblemSuggestQuotes(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).suggestQuotes();
        }
        throw new ConfigException.BugOrBroken("tried to get problem suggestQuotes from " + token);
    }

    static Throwable getProblemCause(Token token) {
        if (token instanceof Problem) {
            return ((Problem)token).cause();
        }
        throw new ConfigException.BugOrBroken("tried to get problem cause from " + token);
    }

    static boolean isComment(Token token) {
        return token instanceof Comment;
    }

    static String getCommentText(Token token) {
        if (token instanceof Comment) {
            return ((Comment)token).text();
        }
        throw new ConfigException.BugOrBroken("tried to get comment text from " + token);
    }

    static boolean isUnquotedText(Token token) {
        return token instanceof UnquotedText;
    }

    static String getUnquotedText(Token token) {
        if (token instanceof UnquotedText) {
            return ((UnquotedText)token).value();
        }
        throw new ConfigException.BugOrBroken("tried to get unquoted text from " + token);
    }

    static boolean isIgnoredWhitespace(Token token) {
        return token instanceof IgnoredWhitespace;
    }

    static boolean isSubstitution(Token token) {
        return token instanceof Substitution;
    }

    static List<Token> getSubstitutionPathExpression(Token token) {
        if (token instanceof Substitution) {
            return ((Substitution)token).value();
        }
        throw new ConfigException.BugOrBroken("tried to get substitution from " + token);
    }

    static boolean getSubstitutionOptional(Token token) {
        if (token instanceof Substitution) {
            return ((Substitution)token).optional();
        }
        throw new ConfigException.BugOrBroken("tried to get substitution optionality from " + token);
    }

    static Token newLine(ConfigOrigin origin) {
        return new Line(origin);
    }

    static Token newProblem(ConfigOrigin origin, String what, String message, boolean suggestQuotes, Throwable cause) {
        return new Problem(origin, what, message, suggestQuotes, cause);
    }

    static Token newCommentDoubleSlash(ConfigOrigin origin, String text) {
        return new Comment.DoubleSlashComment(origin, text);
    }

    static Token newCommentHash(ConfigOrigin origin, String text) {
        return new Comment.HashComment(origin, text);
    }

    static Token newUnquotedText(ConfigOrigin origin, String s) {
        return new UnquotedText(origin, s);
    }

    static Token newIgnoredWhitespace(ConfigOrigin origin, String s) {
        return new IgnoredWhitespace(origin, s);
    }

    static Token newSubstitution(ConfigOrigin origin, boolean optional, List<Token> expression) {
        return new Substitution(origin, optional, expression);
    }

    static Token newValue(AbstractConfigValue value) {
        return new Value(value);
    }

    static Token newValue(AbstractConfigValue value, String origText) {
        return new Value(value, origText);
    }

    static Token newString(ConfigOrigin origin, String value, String origText) {
        return Tokens.newValue(new ConfigString.Quoted(origin, value), origText);
    }

    static Token newInt(ConfigOrigin origin, int value, String origText) {
        return Tokens.newValue(ConfigNumber.newNumber(origin, value, origText), origText);
    }

    static Token newDouble(ConfigOrigin origin, double value, String origText) {
        return Tokens.newValue(ConfigNumber.newNumber(origin, value, origText), origText);
    }

    static Token newLong(ConfigOrigin origin, long value, String origText) {
        return Tokens.newValue(ConfigNumber.newNumber(origin, value, origText), origText);
    }

    static Token newNull(ConfigOrigin origin) {
        return Tokens.newValue(new ConfigNull(origin), "null");
    }

    static Token newBoolean(ConfigOrigin origin, boolean value) {
        return Tokens.newValue(new ConfigBoolean(origin, value), "" + value);
    }

    private static class Substitution
    extends Token {
        private final boolean optional;
        private final List<Token> value;

        Substitution(ConfigOrigin origin, boolean optional, List<Token> expression) {
            super(TokenType.SUBSTITUTION, origin);
            this.optional = optional;
            this.value = expression;
        }

        boolean optional() {
            return this.optional;
        }

        List<Token> value() {
            return this.value;
        }

        @Override
        public String tokenText() {
            return "${" + (this.optional ? "?" : "") + Tokenizer.render(this.value.iterator()) + "}";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Token t : this.value) {
                sb.append(t.toString());
            }
            return "'${" + sb.toString() + "}'";
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Substitution;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((Substitution)other).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }
    }

    private static abstract class Comment
    extends Token {
        private final String text;

        Comment(ConfigOrigin origin, String text) {
            super(TokenType.COMMENT, origin);
            this.text = text;
        }

        String text() {
            return this.text;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("'#");
            sb.append(this.text);
            sb.append("' (COMMENT)");
            return sb.toString();
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Comment;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((Comment)other).text.equals(this.text);
        }

        @Override
        public int hashCode() {
            int h = 41 * (41 + super.hashCode());
            h = 41 * (h + this.text.hashCode());
            return h;
        }

        static final class HashComment
        extends Comment {
            HashComment(ConfigOrigin origin, String text) {
                super(origin, text);
            }

            @Override
            public String tokenText() {
                return "#" + ((Comment)this).text;
            }
        }

        static final class DoubleSlashComment
        extends Comment {
            DoubleSlashComment(ConfigOrigin origin, String text) {
                super(origin, text);
            }

            @Override
            public String tokenText() {
                return "//" + ((Comment)this).text;
            }
        }
    }

    private static class Problem
    extends Token {
        private final String what;
        private final String message;
        private final boolean suggestQuotes;
        private final Throwable cause;

        Problem(ConfigOrigin origin, String what, String message, boolean suggestQuotes, Throwable cause) {
            super(TokenType.PROBLEM, origin);
            this.what = what;
            this.message = message;
            this.suggestQuotes = suggestQuotes;
            this.cause = cause;
        }

        String what() {
            return this.what;
        }

        String message() {
            return this.message;
        }

        boolean suggestQuotes() {
            return this.suggestQuotes;
        }

        Throwable cause() {
            return this.cause;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('\'');
            sb.append(this.what);
            sb.append('\'');
            sb.append(" (");
            sb.append(this.message);
            sb.append(")");
            return sb.toString();
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Problem;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((Problem)other).what.equals(this.what) && ((Problem)other).message.equals(this.message) && ((Problem)other).suggestQuotes == this.suggestQuotes && ConfigImplUtil.equalsHandlingNull(((Problem)other).cause, this.cause);
        }

        @Override
        public int hashCode() {
            int h = 41 * (41 + super.hashCode());
            h = 41 * (h + this.what.hashCode());
            h = 41 * (h + this.message.hashCode());
            h = 41 * (h + Boolean.valueOf(this.suggestQuotes).hashCode());
            if (this.cause != null) {
                h = 41 * (h + this.cause.hashCode());
            }
            return h;
        }
    }

    private static class IgnoredWhitespace
    extends Token {
        private final String value;

        IgnoredWhitespace(ConfigOrigin origin, String s) {
            super(TokenType.IGNORED_WHITESPACE, origin);
            this.value = s;
        }

        @Override
        public String toString() {
            return "'" + this.value + "' (WHITESPACE)";
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof IgnoredWhitespace;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((IgnoredWhitespace)other).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }

        @Override
        public String tokenText() {
            return this.value;
        }
    }

    private static class UnquotedText
    extends Token {
        private final String value;

        UnquotedText(ConfigOrigin origin, String s) {
            super(TokenType.UNQUOTED_TEXT, origin);
            this.value = s;
        }

        String value() {
            return this.value;
        }

        @Override
        public String toString() {
            return "'" + this.value + "'";
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof UnquotedText;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((UnquotedText)other).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }

        @Override
        public String tokenText() {
            return this.value;
        }
    }

    private static class Line
    extends Token {
        Line(ConfigOrigin origin) {
            super(TokenType.NEWLINE, origin);
        }

        @Override
        public String toString() {
            return "'\\n'@" + this.lineNumber();
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Line;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((Line)other).lineNumber() == this.lineNumber();
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.lineNumber();
        }

        @Override
        public String tokenText() {
            return "\n";
        }
    }

    private static class Value
    extends Token {
        private final AbstractConfigValue value;

        Value(AbstractConfigValue value) {
            this(value, null);
        }

        Value(AbstractConfigValue value, String origText) {
            super(TokenType.VALUE, value.origin(), origText);
            this.value = value;
        }

        AbstractConfigValue value() {
            return this.value;
        }

        @Override
        public String toString() {
            if (this.value().resolveStatus() == ResolveStatus.RESOLVED) {
                return "'" + this.value().unwrapped() + "' (" + this.value.valueType().name() + ")";
            }
            return "'<unresolved value>' (" + this.value.valueType().name() + ")";
        }

        @Override
        protected boolean canEqual(Object other) {
            return other instanceof Value;
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other) && ((Value)other).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + this.value.hashCode();
        }
    }
}

