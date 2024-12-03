/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.fontbox.type1;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.type1.DamagedFontException;
import org.apache.fontbox.type1.Token;

class Type1Lexer {
    private static final Log LOG = LogFactory.getLog(Type1Lexer.class);
    private final ByteBuffer buffer;
    private Token aheadToken;
    private int openParens = 0;

    Type1Lexer(byte[] bytes) throws IOException {
        this.buffer = ByteBuffer.wrap(bytes);
        this.aheadToken = this.readToken(null);
    }

    public Token nextToken() throws IOException {
        Token curToken = this.aheadToken;
        this.aheadToken = this.readToken(curToken);
        return curToken;
    }

    public Token peekToken() {
        return this.aheadToken;
    }

    public boolean peekKind(Token.Kind kind) {
        return this.aheadToken != null && this.aheadToken.getKind() == kind;
    }

    private char getChar() throws IOException {
        try {
            return (char)this.buffer.get();
        }
        catch (BufferUnderflowException exception) {
            throw new IOException("Premature end of buffer reached");
        }
    }

    private Token readToken(Token prevToken) throws IOException {
        boolean skip;
        do {
            skip = false;
            while (this.buffer.hasRemaining()) {
                char c = this.getChar();
                if (c == '%') {
                    this.readComment();
                    continue;
                }
                if (c == '(') {
                    return this.readString();
                }
                if (c == ')') {
                    throw new IOException("unexpected closing parenthesis");
                }
                if (c == '[') {
                    return new Token(c, Token.START_ARRAY);
                }
                if (c == '{') {
                    return new Token(c, Token.START_PROC);
                }
                if (c == ']') {
                    return new Token(c, Token.END_ARRAY);
                }
                if (c == '}') {
                    return new Token(c, Token.END_PROC);
                }
                if (c == '/') {
                    String regular = this.readRegular();
                    if (regular == null) {
                        throw new DamagedFontException("Could not read token at position " + this.buffer.position());
                    }
                    return new Token(regular, Token.LITERAL);
                }
                if (c == '<') {
                    char c2 = this.getChar();
                    if (c2 == c) {
                        return new Token("<<", Token.START_DICT);
                    }
                    this.buffer.position(this.buffer.position() - 1);
                    return new Token(c, Token.NAME);
                }
                if (c == '>') {
                    char c2 = this.getChar();
                    if (c2 == c) {
                        return new Token(">>", Token.END_DICT);
                    }
                    this.buffer.position(this.buffer.position() - 1);
                    return new Token(c, Token.NAME);
                }
                if (Character.isWhitespace(c)) {
                    skip = true;
                    continue;
                }
                if (c == '\u0000') {
                    LOG.warn((Object)"NULL byte in font, skipped");
                    skip = true;
                    continue;
                }
                this.buffer.position(this.buffer.position() - 1);
                Token number = this.tryReadNumber();
                if (number != null) {
                    return number;
                }
                String name = this.readRegular();
                if (name == null) {
                    throw new DamagedFontException("Could not read token at position " + this.buffer.position());
                }
                if (name.equals("RD") || name.equals("-|")) {
                    if (prevToken != null && prevToken.getKind() == Token.INTEGER) {
                        return this.readCharString(prevToken.intValue());
                    }
                    throw new IOException("expected INTEGER before -| or RD");
                }
                return new Token(name, Token.NAME);
            }
        } while (skip);
        return null;
    }

    private Token tryReadNumber() throws IOException {
        this.buffer.mark();
        StringBuilder sb = new StringBuilder();
        StringBuilder radix = null;
        char c = this.getChar();
        boolean hasDigit = false;
        if (c == '+' || c == '-') {
            sb.append(c);
            c = this.getChar();
        }
        while (Character.isDigit(c)) {
            sb.append(c);
            c = this.getChar();
            hasDigit = true;
        }
        if (c == '.') {
            sb.append(c);
            c = this.getChar();
        } else if (c == '#') {
            radix = sb;
            sb = new StringBuilder();
            c = this.getChar();
        } else {
            if (sb.length() == 0 || !hasDigit) {
                this.buffer.reset();
                return null;
            }
            if (c != 'e' && c != 'E') {
                this.buffer.position(this.buffer.position() - 1);
                return new Token(sb.toString(), Token.INTEGER);
            }
        }
        if (Character.isDigit(c)) {
            sb.append(c);
            c = this.getChar();
        } else if (c != 'e' && c != 'E') {
            this.buffer.reset();
            return null;
        }
        while (Character.isDigit(c)) {
            sb.append(c);
            c = this.getChar();
        }
        if (c == 'E' || c == 'e') {
            sb.append(c);
            c = this.getChar();
            if (c == '-') {
                sb.append(c);
                c = this.getChar();
            }
            if (Character.isDigit(c)) {
                sb.append(c);
                c = this.getChar();
            } else {
                this.buffer.reset();
                return null;
            }
            while (Character.isDigit(c)) {
                sb.append(c);
                c = this.getChar();
            }
        }
        this.buffer.position(this.buffer.position() - 1);
        if (radix != null) {
            int val;
            try {
                val = Integer.parseInt(sb.toString(), Integer.parseInt(radix.toString()));
            }
            catch (NumberFormatException ex) {
                throw new IOException("Invalid number '" + sb + "'", ex);
            }
            return new Token(Integer.toString(val), Token.INTEGER);
        }
        return new Token(sb.toString(), Token.REAL);
    }

    private String readRegular() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (this.buffer.hasRemaining()) {
            this.buffer.mark();
            char c = this.getChar();
            if (Character.isWhitespace(c) || c == '(' || c == ')' || c == '<' || c == '>' || c == '[' || c == ']' || c == '{' || c == '}' || c == '/' || c == '%') {
                this.buffer.reset();
                break;
            }
            sb.append(c);
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    private String readComment() throws IOException {
        char c;
        StringBuilder sb = new StringBuilder();
        while (this.buffer.hasRemaining() && (c = this.getChar()) != '\r' && c != '\n') {
            sb.append(c);
        }
        return sb.toString();
    }

    private Token readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        block17: while (this.buffer.hasRemaining()) {
            char c = this.getChar();
            switch (c) {
                case '(': {
                    ++this.openParens;
                    sb.append('(');
                    break;
                }
                case ')': {
                    if (this.openParens == 0) {
                        return new Token(sb.toString(), Token.STRING);
                    }
                    sb.append(')');
                    --this.openParens;
                    break;
                }
                case '\\': {
                    char c1 = this.getChar();
                    switch (c1) {
                        case 'n': 
                        case 'r': {
                            sb.append("\n");
                            break;
                        }
                        case 't': {
                            sb.append('\t');
                            break;
                        }
                        case 'b': {
                            sb.append('\b');
                            break;
                        }
                        case 'f': {
                            sb.append('\f');
                            break;
                        }
                        case '\\': {
                            sb.append('\\');
                            break;
                        }
                        case '(': {
                            sb.append('(');
                            break;
                        }
                        case ')': {
                            sb.append(')');
                            break;
                        }
                    }
                    if (!Character.isDigit(c1)) continue block17;
                    String num = String.valueOf(new char[]{c1, this.getChar(), this.getChar()});
                    try {
                        int code = Integer.parseInt(num, 8);
                        sb.append((char)code);
                        break;
                    }
                    catch (NumberFormatException ex) {
                        throw new IOException(ex);
                    }
                }
                case '\n': 
                case '\r': {
                    sb.append("\n");
                    break;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        return null;
    }

    private Token readCharString(int length) throws IOException {
        try {
            this.buffer.get();
            byte[] data = new byte[length];
            this.buffer.get(data);
            return new Token(data, Token.CHARSTRING);
        }
        catch (BufferUnderflowException exception) {
            throw new IOException("Premature end of buffer reached");
        }
    }
}

