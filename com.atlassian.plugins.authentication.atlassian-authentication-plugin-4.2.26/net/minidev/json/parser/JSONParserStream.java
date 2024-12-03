/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.parser;

import java.io.IOException;
import net.minidev.json.parser.JSONParserBase;
import net.minidev.json.parser.ParseException;

abstract class JSONParserStream
extends JSONParserBase {
    public JSONParserStream(int permissiveMode) {
        super(permissiveMode);
    }

    @Override
    protected void readNQString(boolean[] stop) throws IOException {
        this.sb.clear();
        this.skipNQString(stop);
        this.xs = this.sb.toString().trim();
    }

    @Override
    protected Object readNumber(boolean[] stop) throws ParseException, IOException {
        this.sb.clear();
        this.sb.append(this.c);
        this.read();
        this.skipDigits();
        if (this.c != '.' && this.c != 'E' && this.c != 'e') {
            this.skipSpace();
            if (this.c >= '\u0000' && this.c < '~' && !stop[this.c] && this.c != '\u001a') {
                this.skipNQString(stop);
                this.xs = this.sb.toString().trim();
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
                return this.xs;
            }
            this.xs = this.sb.toString().trim();
            return this.parseNumber(this.xs);
        }
        if (this.c == '.') {
            this.sb.append(this.c);
            this.read();
            this.skipDigits();
        }
        if (this.c != 'E' && this.c != 'e') {
            this.skipSpace();
            if (this.c >= '\u0000' && this.c < '~' && !stop[this.c] && this.c != '\u001a') {
                this.skipNQString(stop);
                this.xs = this.sb.toString().trim();
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
                return this.xs;
            }
            this.xs = this.sb.toString().trim();
            return this.extractFloat();
        }
        this.sb.append('E');
        this.read();
        if (this.c == '+' || this.c == '-' || this.c >= '0' && this.c <= '9') {
            this.sb.append(this.c);
            this.read();
            this.skipDigits();
            this.skipSpace();
            if (this.c >= '\u0000' && this.c < '~' && !stop[this.c] && this.c != '\u001a') {
                this.skipNQString(stop);
                this.xs = this.sb.toString().trim();
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
                return this.xs;
            }
            this.xs = this.sb.toString().trim();
            return this.extractFloat();
        }
        this.skipNQString(stop);
        this.xs = this.sb.toString().trim();
        if (!this.acceptNonQuote) {
            throw new ParseException(this.pos, 1, this.xs);
        }
        if (!this.acceptLeadinZero) {
            this.checkLeadinZero();
        }
        return this.xs;
    }

    @Override
    protected void readString() throws ParseException, IOException {
        if (!this.acceptSimpleQuote && this.c == '\'') {
            if (this.acceptNonQuote) {
                this.readNQString(stopAll);
                return;
            }
            throw new ParseException(this.pos, 0, Character.valueOf(this.c));
        }
        this.sb.clear();
        this.readString2();
    }
}

