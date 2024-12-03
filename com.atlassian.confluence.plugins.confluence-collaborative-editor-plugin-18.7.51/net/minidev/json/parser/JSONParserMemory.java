/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.parser;

import java.io.IOException;
import net.minidev.json.parser.JSONParserBase;
import net.minidev.json.parser.ParseException;

abstract class JSONParserMemory
extends JSONParserBase {
    protected int len;

    public JSONParserMemory(int permissiveMode) {
        super(permissiveMode);
    }

    @Override
    protected void readNQString(boolean[] stop) throws IOException {
        int start = this.pos;
        this.skipNQString(stop);
        this.extractStringTrim(start, this.pos);
    }

    @Override
    protected Object readNumber(boolean[] stop) throws ParseException, IOException {
        int start = this.pos;
        this.read();
        this.skipDigits();
        if (this.c != '.' && this.c != 'E' && this.c != 'e') {
            this.skipSpace();
            if (this.c >= '\u0000' && this.c < '~' && !stop[this.c] && this.c != '\u001a') {
                this.skipNQString(stop);
                this.extractStringTrim(start, this.pos);
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
                return this.xs;
            }
            this.extractStringTrim(start, this.pos);
            return this.parseNumber(this.xs);
        }
        if (this.c == '.') {
            this.read();
            this.skipDigits();
        }
        if (this.c != 'E' && this.c != 'e') {
            this.skipSpace();
            if (this.c >= '\u0000' && this.c < '~' && !stop[this.c] && this.c != '\u001a') {
                this.skipNQString(stop);
                this.extractStringTrim(start, this.pos);
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
                return this.xs;
            }
            this.extractStringTrim(start, this.pos);
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
                this.extractStringTrim(start, this.pos);
                if (!this.acceptNonQuote) {
                    throw new ParseException(this.pos, 1, this.xs);
                }
                return this.xs;
            }
            this.extractStringTrim(start, this.pos);
            return this.extractFloat();
        }
        this.skipNQString(stop);
        this.extractStringTrim(start, this.pos);
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
        int tmpP = this.indexOf(this.c, this.pos + 1);
        if (tmpP == -1) {
            throw new ParseException(this.len, 3, null);
        }
        this.extractString(this.pos + 1, tmpP);
        if (this.xs.indexOf(92) == -1) {
            this.checkControleChar();
            this.pos = tmpP;
            this.read();
            return;
        }
        this.sb.clear();
        this.readString2();
    }

    protected abstract void extractString(int var1, int var2);

    protected abstract int indexOf(char var1, int var2);

    protected abstract void extractStringTrim(int var1, int var2);
}

