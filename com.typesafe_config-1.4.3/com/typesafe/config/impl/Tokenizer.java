/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigSyntax;
import com.typesafe.config.impl.ConfigImplUtil;
import com.typesafe.config.impl.SimpleConfigOrigin;
import com.typesafe.config.impl.Token;
import com.typesafe.config.impl.Tokens;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

final class Tokenizer {
    Tokenizer() {
    }

    private static String asString(int codepoint) {
        if (codepoint == 10) {
            return "newline";
        }
        if (codepoint == 9) {
            return "tab";
        }
        if (codepoint == -1) {
            return "end of file";
        }
        if (ConfigImplUtil.isC0Control(codepoint)) {
            return String.format("control character 0x%x", codepoint);
        }
        return String.format("%c", codepoint);
    }

    static Iterator<Token> tokenize(ConfigOrigin origin, Reader input, ConfigSyntax flavor) {
        return new TokenIterator(origin, input, flavor != ConfigSyntax.JSON);
    }

    static String render(Iterator<Token> tokens) {
        StringBuilder renderedText = new StringBuilder();
        while (tokens.hasNext()) {
            renderedText.append(tokens.next().tokenText());
        }
        return renderedText.toString();
    }

    private static class TokenIterator
    implements Iterator<Token> {
        private final SimpleConfigOrigin origin;
        private final Reader input;
        private final LinkedList<Integer> buffer;
        private int lineNumber;
        private ConfigOrigin lineOrigin;
        private final Queue<Token> tokens;
        private final WhitespaceSaver whitespaceSaver;
        private final boolean allowComments;
        static final String firstNumberChars = "0123456789-";
        static final String numberChars = "0123456789eE+-.";
        static final String notInUnquotedText = "$\"{}[]:=,+#`^?!@*&\\";

        TokenIterator(ConfigOrigin origin, Reader input, boolean allowComments) {
            this.origin = (SimpleConfigOrigin)origin;
            this.input = input;
            this.allowComments = allowComments;
            this.buffer = new LinkedList();
            this.lineNumber = 1;
            this.lineOrigin = this.origin.withLineNumber(this.lineNumber);
            this.tokens = new LinkedList<Token>();
            this.tokens.add(Tokens.START);
            this.whitespaceSaver = new WhitespaceSaver();
        }

        private int nextCharRaw() {
            if (this.buffer.isEmpty()) {
                try {
                    return this.input.read();
                }
                catch (IOException e) {
                    throw new ConfigException.IO(this.origin, "read error: " + e.getMessage(), e);
                }
            }
            int c = this.buffer.pop();
            return c;
        }

        private void putBack(int c) {
            if (this.buffer.size() > 2) {
                throw new ConfigException.BugOrBroken("bug: putBack() three times, undesirable look-ahead");
            }
            this.buffer.push(c);
        }

        static boolean isWhitespace(int c) {
            return ConfigImplUtil.isWhitespace(c);
        }

        static boolean isWhitespaceNotNewline(int c) {
            return c != 10 && ConfigImplUtil.isWhitespace(c);
        }

        private boolean startOfComment(int c) {
            if (c == -1) {
                return false;
            }
            if (this.allowComments) {
                if (c == 35) {
                    return true;
                }
                if (c == 47) {
                    int maybeSecondSlash = this.nextCharRaw();
                    this.putBack(maybeSecondSlash);
                    return maybeSecondSlash == 47;
                }
                return false;
            }
            return false;
        }

        private int nextCharAfterWhitespace(WhitespaceSaver saver) {
            int c;
            while (true) {
                if ((c = this.nextCharRaw()) == -1) {
                    return -1;
                }
                if (!TokenIterator.isWhitespaceNotNewline(c)) break;
                saver.add(c);
            }
            return c;
        }

        private ProblemException problem(String message) {
            return this.problem("", message, null);
        }

        private ProblemException problem(String what, String message) {
            return this.problem(what, message, null);
        }

        private ProblemException problem(String what, String message, boolean suggestQuotes) {
            return this.problem(what, message, suggestQuotes, null);
        }

        private ProblemException problem(String what, String message, Throwable cause) {
            return TokenIterator.problem(this.lineOrigin, what, message, cause);
        }

        private ProblemException problem(String what, String message, boolean suggestQuotes, Throwable cause) {
            return TokenIterator.problem(this.lineOrigin, what, message, suggestQuotes, cause);
        }

        private static ProblemException problem(ConfigOrigin origin, String what, String message, Throwable cause) {
            return TokenIterator.problem(origin, what, message, false, cause);
        }

        private static ProblemException problem(ConfigOrigin origin, String what, String message, boolean suggestQuotes, Throwable cause) {
            if (what == null || message == null) {
                throw new ConfigException.BugOrBroken("internal error, creating bad ProblemException");
            }
            return new ProblemException(Tokens.newProblem(origin, what, message, suggestQuotes, cause));
        }

        private static ProblemException problem(ConfigOrigin origin, String message) {
            return TokenIterator.problem(origin, "", message, null);
        }

        private static ConfigOrigin lineOrigin(ConfigOrigin baseOrigin, int lineNumber) {
            return ((SimpleConfigOrigin)baseOrigin).withLineNumber(lineNumber);
        }

        private Token pullComment(int firstChar) {
            boolean doubleSlash = false;
            if (firstChar == 47) {
                int discard = this.nextCharRaw();
                if (discard != 47) {
                    throw new ConfigException.BugOrBroken("called pullComment but // not seen");
                }
                doubleSlash = true;
            }
            StringBuilder sb = new StringBuilder();
            while (true) {
                int c;
                if ((c = this.nextCharRaw()) == -1 || c == 10) {
                    this.putBack(c);
                    if (doubleSlash) {
                        return Tokens.newCommentDoubleSlash(this.lineOrigin, sb.toString());
                    }
                    return Tokens.newCommentHash(this.lineOrigin, sb.toString());
                }
                sb.appendCodePoint(c);
            }
        }

        private Token pullUnquotedText() {
            String s;
            ConfigOrigin origin = this.lineOrigin;
            StringBuilder sb = new StringBuilder();
            int c = this.nextCharRaw();
            while (c != -1 && notInUnquotedText.indexOf(c) < 0 && !TokenIterator.isWhitespace(c) && !this.startOfComment(c)) {
                sb.appendCodePoint(c);
                if (sb.length() == 4) {
                    s = sb.toString();
                    if (s.equals("true")) {
                        return Tokens.newBoolean(origin, true);
                    }
                    if (s.equals("null")) {
                        return Tokens.newNull(origin);
                    }
                } else if (sb.length() == 5 && (s = sb.toString()).equals("false")) {
                    return Tokens.newBoolean(origin, false);
                }
                c = this.nextCharRaw();
            }
            this.putBack(c);
            s = sb.toString();
            return Tokens.newUnquotedText(origin, s);
        }

        private Token pullNumber(int firstChar) throws ProblemException {
            StringBuilder sb = new StringBuilder();
            sb.appendCodePoint(firstChar);
            boolean containedDecimalOrE = false;
            int c = this.nextCharRaw();
            while (c != -1 && numberChars.indexOf(c) >= 0) {
                if (c == 46 || c == 101 || c == 69) {
                    containedDecimalOrE = true;
                }
                sb.appendCodePoint(c);
                c = this.nextCharRaw();
            }
            this.putBack(c);
            String s = sb.toString();
            try {
                if (containedDecimalOrE) {
                    return Tokens.newDouble(this.lineOrigin, Double.parseDouble(s), s);
                }
                return Tokens.newLong(this.lineOrigin, Long.parseLong(s), s);
            }
            catch (NumberFormatException e) {
                for (char u : s.toCharArray()) {
                    if (notInUnquotedText.indexOf(u) < 0) continue;
                    throw this.problem(Tokenizer.asString(u), "Reserved character '" + Tokenizer.asString(u) + "' is not allowed outside quotes", true);
                }
                return Tokens.newUnquotedText(this.lineOrigin, s);
            }
        }

        private void pullEscapeSequence(StringBuilder sb, StringBuilder sbOrig) throws ProblemException {
            int escaped = this.nextCharRaw();
            if (escaped == -1) {
                throw this.problem("End of input but backslash in string had nothing after it");
            }
            sbOrig.appendCodePoint(92);
            sbOrig.appendCodePoint(escaped);
            switch (escaped) {
                case 34: {
                    sb.append('\"');
                    break;
                }
                case 92: {
                    sb.append('\\');
                    break;
                }
                case 47: {
                    sb.append('/');
                    break;
                }
                case 98: {
                    sb.append('\b');
                    break;
                }
                case 102: {
                    sb.append('\f');
                    break;
                }
                case 110: {
                    sb.append('\n');
                    break;
                }
                case 114: {
                    sb.append('\r');
                    break;
                }
                case 116: {
                    sb.append('\t');
                    break;
                }
                case 117: {
                    char[] a = new char[4];
                    for (int i = 0; i < 4; ++i) {
                        int c = this.nextCharRaw();
                        if (c == -1) {
                            throw this.problem("End of input but expecting 4 hex digits for \\uXXXX escape");
                        }
                        a[i] = (char)c;
                    }
                    String digits = new String(a);
                    sbOrig.append(a);
                    try {
                        sb.appendCodePoint(Integer.parseInt(digits, 16));
                        break;
                    }
                    catch (NumberFormatException e) {
                        throw this.problem(digits, String.format("Malformed hex digits after \\u escape in string: '%s'", digits), e);
                    }
                }
                default: {
                    throw this.problem(Tokenizer.asString(escaped), String.format("backslash followed by '%s', this is not a valid escape sequence (quoted strings use JSON escaping, so use double-backslash \\\\ for literal backslash)", Tokenizer.asString(escaped)));
                }
            }
        }

        private void appendTripleQuotedString(StringBuilder sb, StringBuilder sbOrig) throws ProblemException {
            int c;
            int consecutiveQuotes = 0;
            while (true) {
                if ((c = this.nextCharRaw()) == 34) {
                    ++consecutiveQuotes;
                } else {
                    if (consecutiveQuotes >= 3) break;
                    consecutiveQuotes = 0;
                    if (c == -1) {
                        throw this.problem("End of input but triple-quoted string was still open");
                    }
                    if (c == 10) {
                        ++this.lineNumber;
                        this.lineOrigin = this.origin.withLineNumber(this.lineNumber);
                    }
                }
                sb.appendCodePoint(c);
                sbOrig.appendCodePoint(c);
            }
            sb.setLength(sb.length() - 3);
            this.putBack(c);
        }

        private Token pullQuotedString() throws ProblemException {
            int c;
            StringBuilder sb = new StringBuilder();
            StringBuilder sbOrig = new StringBuilder();
            sbOrig.appendCodePoint(34);
            while (true) {
                if ((c = this.nextCharRaw()) == -1) {
                    throw this.problem("End of input but string quote was still open");
                }
                if (c == 92) {
                    this.pullEscapeSequence(sb, sbOrig);
                    continue;
                }
                if (c == 34) break;
                if (ConfigImplUtil.isC0Control(c)) {
                    throw this.problem(Tokenizer.asString(c), "JSON does not allow unescaped " + Tokenizer.asString(c) + " in quoted strings, use a backslash escape");
                }
                sb.appendCodePoint(c);
                sbOrig.appendCodePoint(c);
            }
            sbOrig.appendCodePoint(c);
            if (sb.length() == 0) {
                int third = this.nextCharRaw();
                if (third == 34) {
                    sbOrig.appendCodePoint(third);
                    this.appendTripleQuotedString(sb, sbOrig);
                } else {
                    this.putBack(third);
                }
            }
            return Tokens.newString(this.lineOrigin, sb.toString(), sbOrig.toString());
        }

        private Token pullPlusEquals() throws ProblemException {
            int c = this.nextCharRaw();
            if (c != 61) {
                throw this.problem(Tokenizer.asString(c), "'+' not followed by =, '" + Tokenizer.asString(c) + "' not allowed after '+'", true);
            }
            return Tokens.PLUS_EQUALS;
        }

        private Token pullSubstitution() throws ProblemException {
            Token t;
            ConfigOrigin origin = this.lineOrigin;
            int c = this.nextCharRaw();
            if (c != 123) {
                throw this.problem(Tokenizer.asString(c), "'$' not followed by {, '" + Tokenizer.asString(c) + "' not allowed after '$'", true);
            }
            boolean optional = false;
            c = this.nextCharRaw();
            if (c == 63) {
                optional = true;
            } else {
                this.putBack(c);
            }
            WhitespaceSaver saver = new WhitespaceSaver();
            ArrayList<Token> expression = new ArrayList<Token>();
            while ((t = this.pullNextToken(saver)) != Tokens.CLOSE_CURLY) {
                if (t == Tokens.END) {
                    throw TokenIterator.problem(origin, "Substitution ${ was not closed with a }");
                }
                Token whitespace = saver.check(t, origin, this.lineNumber);
                if (whitespace != null) {
                    expression.add(whitespace);
                }
                expression.add(t);
            }
            return Tokens.newSubstitution(origin, optional, expression);
        }

        private Token pullNextToken(WhitespaceSaver saver) throws ProblemException {
            Token t;
            int c = this.nextCharAfterWhitespace(saver);
            if (c == -1) {
                return Tokens.END;
            }
            if (c == 10) {
                Token line = Tokens.newLine(this.lineOrigin);
                ++this.lineNumber;
                this.lineOrigin = this.origin.withLineNumber(this.lineNumber);
                return line;
            }
            if (this.startOfComment(c)) {
                t = this.pullComment(c);
            } else {
                switch (c) {
                    case 34: {
                        t = this.pullQuotedString();
                        break;
                    }
                    case 36: {
                        t = this.pullSubstitution();
                        break;
                    }
                    case 58: {
                        t = Tokens.COLON;
                        break;
                    }
                    case 44: {
                        t = Tokens.COMMA;
                        break;
                    }
                    case 61: {
                        t = Tokens.EQUALS;
                        break;
                    }
                    case 123: {
                        t = Tokens.OPEN_CURLY;
                        break;
                    }
                    case 125: {
                        t = Tokens.CLOSE_CURLY;
                        break;
                    }
                    case 91: {
                        t = Tokens.OPEN_SQUARE;
                        break;
                    }
                    case 93: {
                        t = Tokens.CLOSE_SQUARE;
                        break;
                    }
                    case 43: {
                        t = this.pullPlusEquals();
                        break;
                    }
                    default: {
                        t = null;
                    }
                }
                if (t == null) {
                    if (firstNumberChars.indexOf(c) >= 0) {
                        t = this.pullNumber(c);
                    } else {
                        if (notInUnquotedText.indexOf(c) >= 0) {
                            throw this.problem(Tokenizer.asString(c), "Reserved character '" + Tokenizer.asString(c) + "' is not allowed outside quotes", true);
                        }
                        this.putBack(c);
                        t = this.pullUnquotedText();
                    }
                }
            }
            if (t == null) {
                throw new ConfigException.BugOrBroken("bug: failed to generate next token");
            }
            return t;
        }

        private static boolean isSimpleValue(Token t) {
            return Tokens.isSubstitution(t) || Tokens.isUnquotedText(t) || Tokens.isValue(t);
        }

        private void queueNextToken() throws ProblemException {
            Token t = this.pullNextToken(this.whitespaceSaver);
            Token whitespace = this.whitespaceSaver.check(t, this.origin, this.lineNumber);
            if (whitespace != null) {
                this.tokens.add(whitespace);
            }
            this.tokens.add(t);
        }

        @Override
        public boolean hasNext() {
            return !this.tokens.isEmpty();
        }

        @Override
        public Token next() {
            Token t = this.tokens.remove();
            if (this.tokens.isEmpty() && t != Tokens.END) {
                try {
                    this.queueNextToken();
                }
                catch (ProblemException e) {
                    this.tokens.add(e.problem());
                }
                if (this.tokens.isEmpty()) {
                    throw new ConfigException.BugOrBroken("bug: tokens queue should not be empty here");
                }
            }
            return t;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Does not make sense to remove items from token stream");
        }

        private static class WhitespaceSaver {
            private StringBuilder whitespace = new StringBuilder();
            private boolean lastTokenWasSimpleValue = false;

            WhitespaceSaver() {
            }

            void add(int c) {
                this.whitespace.appendCodePoint(c);
            }

            Token check(Token t, ConfigOrigin baseOrigin, int lineNumber) {
                if (TokenIterator.isSimpleValue(t)) {
                    return this.nextIsASimpleValue(baseOrigin, lineNumber);
                }
                return this.nextIsNotASimpleValue(baseOrigin, lineNumber);
            }

            private Token nextIsNotASimpleValue(ConfigOrigin baseOrigin, int lineNumber) {
                this.lastTokenWasSimpleValue = false;
                return this.createWhitespaceTokenFromSaver(baseOrigin, lineNumber);
            }

            private Token nextIsASimpleValue(ConfigOrigin baseOrigin, int lineNumber) {
                Token t = this.createWhitespaceTokenFromSaver(baseOrigin, lineNumber);
                if (!this.lastTokenWasSimpleValue) {
                    this.lastTokenWasSimpleValue = true;
                }
                return t;
            }

            private Token createWhitespaceTokenFromSaver(ConfigOrigin baseOrigin, int lineNumber) {
                if (this.whitespace.length() > 0) {
                    Token t = this.lastTokenWasSimpleValue ? Tokens.newUnquotedText(TokenIterator.lineOrigin(baseOrigin, lineNumber), this.whitespace.toString()) : Tokens.newIgnoredWhitespace(TokenIterator.lineOrigin(baseOrigin, lineNumber), this.whitespace.toString());
                    this.whitespace.setLength(0);
                    return t;
                }
                return null;
            }
        }
    }

    private static class ProblemException
    extends Exception {
        private static final long serialVersionUID = 1L;
        private final Token problem;

        ProblemException(Token problem) {
            this.problem = problem;
        }

        Token problem() {
            return this.problem;
        }
    }
}

