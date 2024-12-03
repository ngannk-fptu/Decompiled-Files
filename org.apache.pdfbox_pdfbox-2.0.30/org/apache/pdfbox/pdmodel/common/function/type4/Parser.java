/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function.type4;

public final class Parser {
    private Parser() {
    }

    public static void parse(CharSequence input, SyntaxHandler handler) {
        Tokenizer tokenizer = new Tokenizer(input, handler);
        tokenizer.tokenize();
    }

    private static final class Tokenizer {
        private static final char NUL = '\u0000';
        private static final char EOT = '\u0004';
        private static final char TAB = '\t';
        private static final char FF = '\f';
        private static final char CR = '\r';
        private static final char LF = '\n';
        private static final char SPACE = ' ';
        private final CharSequence input;
        private int index;
        private final SyntaxHandler handler;
        private State state = State.WHITESPACE;
        private final StringBuilder buffer = new StringBuilder();

        private Tokenizer(CharSequence text, SyntaxHandler syntaxHandler) {
            this.input = text;
            this.handler = syntaxHandler;
        }

        private boolean hasMore() {
            return this.index < this.input.length();
        }

        private char currentChar() {
            return this.input.charAt(this.index);
        }

        private char nextChar() {
            ++this.index;
            if (!this.hasMore()) {
                return '\u0004';
            }
            return this.currentChar();
        }

        private char peek() {
            if (this.index < this.input.length() - 1) {
                return this.input.charAt(this.index + 1);
            }
            return '\u0004';
        }

        private State nextState() {
            char ch = this.currentChar();
            switch (ch) {
                case '\n': 
                case '\f': 
                case '\r': {
                    this.state = State.NEWLINE;
                    break;
                }
                case '\u0000': 
                case '\t': 
                case ' ': {
                    this.state = State.WHITESPACE;
                    break;
                }
                case '%': {
                    this.state = State.COMMENT;
                    break;
                }
                default: {
                    this.state = State.TOKEN;
                }
            }
            return this.state;
        }

        private void tokenize() {
            block5: while (this.hasMore()) {
                this.buffer.setLength(0);
                this.nextState();
                switch (this.state) {
                    case NEWLINE: {
                        this.scanNewLine();
                        continue block5;
                    }
                    case WHITESPACE: {
                        this.scanWhitespace();
                        continue block5;
                    }
                    case COMMENT: {
                        this.scanComment();
                        continue block5;
                    }
                }
                this.scanToken();
            }
        }

        private void scanNewLine() {
            assert (this.state == State.NEWLINE);
            char ch = this.currentChar();
            this.buffer.append(ch);
            if (ch == '\r' && this.peek() == '\n') {
                this.buffer.append(this.nextChar());
            }
            this.handler.newLine(this.buffer);
            this.nextChar();
        }

        private void scanWhitespace() {
            assert (this.state == State.WHITESPACE);
            this.buffer.append(this.currentChar());
            block3: while (this.hasMore()) {
                char ch = this.nextChar();
                switch (ch) {
                    case '\u0000': 
                    case '\t': 
                    case ' ': {
                        this.buffer.append(ch);
                        continue block3;
                    }
                }
                break;
            }
            this.handler.whitespace(this.buffer);
        }

        private void scanComment() {
            assert (this.state == State.COMMENT);
            this.buffer.append(this.currentChar());
            block3: while (this.hasMore()) {
                char ch = this.nextChar();
                switch (ch) {
                    case '\n': 
                    case '\f': 
                    case '\r': {
                        break block3;
                    }
                    default: {
                        this.buffer.append(ch);
                        continue block3;
                    }
                }
            }
            this.handler.comment(this.buffer);
        }

        private void scanToken() {
            assert (this.state == State.TOKEN);
            char ch = this.currentChar();
            this.buffer.append(ch);
            switch (ch) {
                case '{': 
                case '}': {
                    this.handler.token(this.buffer);
                    this.nextChar();
                    return;
                }
            }
            block6: while (this.hasMore()) {
                ch = this.nextChar();
                switch (ch) {
                    case '\u0000': 
                    case '\u0004': 
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': 
                    case '{': 
                    case '}': {
                        break block6;
                    }
                    default: {
                        this.buffer.append(ch);
                        continue block6;
                    }
                }
            }
            this.handler.token(this.buffer);
        }
    }

    public static abstract class AbstractSyntaxHandler
    implements SyntaxHandler {
        @Override
        public void comment(CharSequence text) {
        }

        @Override
        public void newLine(CharSequence text) {
        }

        @Override
        public void whitespace(CharSequence text) {
        }
    }

    public static interface SyntaxHandler {
        public void newLine(CharSequence var1);

        public void whitespace(CharSequence var1);

        public void token(CharSequence var1);

        public void comment(CharSequence var1);
    }

    private static enum State {
        NEWLINE,
        WHITESPACE,
        COMMENT,
        TOKEN;

    }
}

