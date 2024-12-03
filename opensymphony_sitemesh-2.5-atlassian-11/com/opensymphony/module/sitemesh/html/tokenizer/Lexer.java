/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.tokenizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

abstract class Lexer {
    public static final int YYEOF = -1;
    private static final int YY_BUFFERSIZE = 2048;
    public static final int YYINITIAL = 0;
    public static final int ELEMENT = 1;
    private static final String yycmap_packed = "\b\u0000\u0002\u0013\u0001\u0013\u0002\u0000\u0001\u0013\u0012\u0000\u0001\u0013\u0001\u0002\u0001\u0015\u0004\u0000\u0001\u0016\u0005\u0000\u0001\u0003\u0001\u0000\u0001\u000f\f\u0000\u0001\u0001\u0001\u0014\u0001\u0005\u0001\u0006\u0001\u0000\u0001\t\u0001\u0000\u0001\u0007\u0001\b\u0004\u0000\u0001\u0012\u0003\u0000\u0001\r\u0002\u0000\u0001\u000e\u0001\u0000\u0001\u0011\u0001\u0010\u0001\n\u0003\u0000\u0001\f\u0002\u0000\u0001\u0004\u0001\u0000\u0001\u000b\u0003\u0000\u0001\t\u0001\u0000\u0001\u0007\u0001\b\u0004\u0000\u0001\u0012\u0003\u0000\u0001\r\u0002\u0000\u0001\u000e\u0001\u0000\u0001\u0011\u0001\u0010\u0001\n\u0003\u0000\u0001\f\uff87\u0000";
    private static final char[] yycmap = Lexer.yy_unpack_cmap("\b\u0000\u0002\u0013\u0001\u0013\u0002\u0000\u0001\u0013\u0012\u0000\u0001\u0013\u0001\u0002\u0001\u0015\u0004\u0000\u0001\u0016\u0005\u0000\u0001\u0003\u0001\u0000\u0001\u000f\f\u0000\u0001\u0001\u0001\u0014\u0001\u0005\u0001\u0006\u0001\u0000\u0001\t\u0001\u0000\u0001\u0007\u0001\b\u0004\u0000\u0001\u0012\u0003\u0000\u0001\r\u0002\u0000\u0001\u000e\u0001\u0000\u0001\u0011\u0001\u0010\u0001\n\u0003\u0000\u0001\f\u0002\u0000\u0001\u0004\u0001\u0000\u0001\u000b\u0003\u0000\u0001\t\u0001\u0000\u0001\u0007\u0001\b\u0004\u0000\u0001\u0012\u0003\u0000\u0001\r\u0002\u0000\u0001\u000e\u0001\u0000\u0001\u0011\u0001\u0010\u0001\n\u0003\u0000\u0001\f\uff87\u0000");
    private static final int YY_UNKNOWN_ERROR = 0;
    private static final int YY_ILLEGAL_STATE = 1;
    private static final int YY_NO_MATCH = 2;
    private static final int YY_PUSHBACK_2BIG = 3;
    private static final String[] YY_ERROR_MSG = new String[]{"Unkown internal scanner error", "Internal error: unknown state", "Error: could not match input", "Error: pushback value was too large"};
    private Reader yy_reader;
    private int yy_state;
    private int yy_lexical_state = 0;
    private char[] yy_buffer = new char[2048];
    private int yy_markedPos;
    private int yy_pushbackPos;
    private int yy_currentPos;
    private int yy_startRead;
    private int yy_endRead;
    private int yyline;
    private int yychar;
    private int yycolumn;
    private boolean yy_atBOL = true;
    private boolean yy_atEOF;
    private boolean yy_eof_done;

    protected int position() {
        return this.yychar;
    }

    protected int length() {
        return this.yy_markedPos - this.yy_startRead;
    }

    protected int line() {
        return -1;
    }

    protected int column() {
        return -1;
    }

    protected void resetLexerState() {
        this.yybegin(0);
    }

    protected abstract void reportError(String var1, int var2, int var3);

    Lexer(Reader in) {
        this.yy_reader = in;
    }

    Lexer(InputStream in) {
        this(new InputStreamReader(in));
    }

    private static char[] yy_unpack_cmap(String packed) {
        char[] map = new char[65536];
        int i = 0;
        int j = 0;
        while (i < 118) {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);
            do {
                map[j++] = value;
            } while (--count > 0);
        }
        return map;
    }

    private boolean yy_refill() throws IOException {
        int numRead;
        if (this.yy_startRead > 0) {
            System.arraycopy(this.yy_buffer, this.yy_startRead, this.yy_buffer, 0, this.yy_endRead - this.yy_startRead);
            this.yy_endRead -= this.yy_startRead;
            this.yy_currentPos -= this.yy_startRead;
            this.yy_markedPos -= this.yy_startRead;
            this.yy_pushbackPos -= this.yy_startRead;
            this.yy_startRead = 0;
        }
        if (this.yy_currentPos >= this.yy_buffer.length) {
            char[] newBuffer = new char[this.yy_currentPos * 2];
            System.arraycopy(this.yy_buffer, 0, newBuffer, 0, this.yy_buffer.length);
            this.yy_buffer = newBuffer;
        }
        if ((numRead = this.yy_reader.read(this.yy_buffer, this.yy_endRead, this.yy_buffer.length - this.yy_endRead)) < 0) {
            return true;
        }
        this.yy_endRead += numRead;
        return false;
    }

    public final void yyclose() throws IOException {
        this.yy_atEOF = true;
        this.yy_endRead = this.yy_startRead;
        if (this.yy_reader != null) {
            this.yy_reader.close();
        }
    }

    public final void yyreset(Reader reader) throws IOException {
        this.yyclose();
        this.yy_reader = reader;
        this.yy_atBOL = true;
        this.yy_atEOF = false;
        this.yy_startRead = 0;
        this.yy_endRead = 0;
        this.yy_pushbackPos = 0;
        this.yy_markedPos = 0;
        this.yy_currentPos = 0;
        this.yycolumn = 0;
        this.yychar = 0;
        this.yyline = 0;
        this.yy_lexical_state = 0;
    }

    public final int yystate() {
        return this.yy_lexical_state;
    }

    public final void yybegin(int newState) {
        this.yy_lexical_state = newState;
    }

    public final String yytext() {
        return new String(this.yy_buffer, this.yy_startRead, this.yy_markedPos - this.yy_startRead);
    }

    public final char yycharat(int pos) {
        return this.yy_buffer[this.yy_startRead + pos];
    }

    public final int yylength() {
        return this.yy_markedPos - this.yy_startRead;
    }

    private void yy_ScanError(int errorCode) {
        String message;
        try {
            message = YY_ERROR_MSG[errorCode];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            message = YY_ERROR_MSG[0];
        }
        throw new Error(message);
    }

    private void yypushback(int number) {
        if (number > this.yylength()) {
            this.yy_ScanError(3);
        }
        this.yy_markedPos -= number;
    }

    private void yy_do_eof() throws IOException {
        if (!this.yy_eof_done) {
            this.yy_eof_done = true;
            this.yyclose();
        }
    }

    public int yylex() throws IOException {
        int yy_endRead_l = this.yy_endRead;
        char[] yy_buffer_l = this.yy_buffer;
        char[] yycmap_l = yycmap;
        block273: while (true) {
            int yy_input;
            int yy_markedPos_l = this.yy_markedPos;
            this.yychar += yy_markedPos_l - this.yy_startRead;
            int yy_action = -1;
            this.yy_currentPos = this.yy_startRead = yy_markedPos_l;
            int yy_currentPos_l = this.yy_startRead;
            int yy_startRead_l = this.yy_startRead;
            this.yy_state = this.yy_lexical_state;
            block274: while (true) {
                if (yy_currentPos_l < yy_endRead_l) {
                    yy_input = yy_buffer_l[yy_currentPos_l++];
                } else {
                    if (this.yy_atEOF) {
                        yy_input = -1;
                        break;
                    }
                    this.yy_currentPos = yy_currentPos_l;
                    this.yy_markedPos = yy_markedPos_l;
                    boolean eof = this.yy_refill();
                    yy_currentPos_l = this.yy_currentPos;
                    yy_markedPos_l = this.yy_markedPos;
                    yy_buffer_l = this.yy_buffer;
                    yy_endRead_l = this.yy_endRead;
                    if (eof) {
                        yy_input = -1;
                        break;
                    }
                    yy_input = yy_buffer_l[yy_currentPos_l++];
                }
                yy_input = yycmap_l[yy_input];
                boolean yy_isFinal = false;
                boolean yy_noLookAhead = false;
                block0 : switch (this.yy_state) {
                    case 0: {
                        switch (yy_input) {
                            case 1: {
                                yy_isFinal = true;
                                this.yy_state = 3;
                                break block0;
                            }
                        }
                        yy_isFinal = true;
                        this.yy_state = 2;
                        break;
                    }
                    case 1: {
                        switch (yy_input) {
                            case 5: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 5;
                                break block0;
                            }
                            case 11: {
                                yy_isFinal = true;
                                this.yy_state = 6;
                                break block0;
                            }
                            case 15: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 7;
                                break block0;
                            }
                            case 19: {
                                yy_isFinal = true;
                                this.yy_state = 8;
                                break block0;
                            }
                            case 20: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 9;
                                break block0;
                            }
                            case 21: {
                                yy_isFinal = true;
                                this.yy_state = 10;
                                break block0;
                            }
                            case 22: {
                                yy_isFinal = true;
                                this.yy_state = 11;
                                break block0;
                            }
                        }
                        yy_isFinal = true;
                        this.yy_state = 4;
                        break;
                    }
                    case 2: {
                        switch (yy_input) {
                            case 1: {
                                break block274;
                            }
                            default: {
                                yy_isFinal = true;
                                this.yy_state = 2;
                                break;
                            }
                        }
                        break;
                    }
                    case 3: {
                        switch (yy_input) {
                            case 2: {
                                this.yy_state = 12;
                                break block0;
                            }
                            case 6: {
                                this.yy_state = 13;
                                break block0;
                            }
                            case 12: {
                                this.yy_state = 14;
                                break block0;
                            }
                            case 16: {
                                this.yy_state = 15;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 4: {
                        switch (yy_input) {
                            case 5: 
                            case 11: 
                            case 15: 
                            case 19: 
                            case 20: {
                                break block274;
                            }
                            default: {
                                yy_isFinal = true;
                                this.yy_state = 4;
                                break;
                            }
                        }
                        break;
                    }
                    case 6: {
                        switch (yy_input) {
                            case 5: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 5;
                                break block0;
                            }
                            case 3: {
                                this.yy_state = 16;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 8: {
                        switch (yy_input) {
                            case 19: {
                                yy_isFinal = true;
                                this.yy_state = 8;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 10: {
                        switch (yy_input) {
                            case 21: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 18;
                                break block0;
                            }
                        }
                        this.yy_state = 17;
                        break;
                    }
                    case 11: {
                        switch (yy_input) {
                            case 22: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 18;
                                break block0;
                            }
                        }
                        this.yy_state = 19;
                        break;
                    }
                    case 12: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 21;
                                break block0;
                            }
                            case 4: {
                                yy_isFinal = true;
                                this.yy_state = 22;
                                break block0;
                            }
                        }
                        this.yy_state = 20;
                        break;
                    }
                    case 13: {
                        switch (yy_input) {
                            case 6: {
                                this.yy_state = 23;
                                break block0;
                            }
                        }
                        this.yy_state = 13;
                        break;
                    }
                    case 14: {
                        switch (yy_input) {
                            case 13: {
                                this.yy_state = 24;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 15: {
                        switch (yy_input) {
                            case 7: {
                                this.yy_state = 25;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 16: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 26;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 17: {
                        switch (yy_input) {
                            case 21: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 18;
                                break block0;
                            }
                        }
                        this.yy_state = 17;
                        break;
                    }
                    case 19: {
                        switch (yy_input) {
                            case 22: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 18;
                                break block0;
                            }
                        }
                        this.yy_state = 19;
                        break;
                    }
                    case 20: {
                        switch (yy_input) {
                            case 5: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 27;
                                break block0;
                            }
                        }
                        this.yy_state = 20;
                        break;
                    }
                    case 21: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 28;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 22: {
                        switch (yy_input) {
                            case 7: {
                                this.yy_state = 29;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 23: {
                        switch (yy_input) {
                            case 6: {
                                this.yy_state = 23;
                                break block0;
                            }
                            case 5: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 27;
                                break block0;
                            }
                        }
                        this.yy_state = 13;
                        break;
                    }
                    case 24: {
                        switch (yy_input) {
                            case 14: {
                                this.yy_state = 30;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 25: {
                        switch (yy_input) {
                            case 17: {
                                this.yy_state = 31;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 26: {
                        switch (yy_input) {
                            case 5: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 5;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 28: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 33;
                                break block0;
                            }
                            case 4: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 34;
                                break block0;
                            }
                        }
                        this.yy_state = 32;
                        break;
                    }
                    case 29: {
                        switch (yy_input) {
                            case 8: {
                                this.yy_state = 35;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 30: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 36;
                                break block0;
                            }
                        }
                        this.yy_state = 30;
                        break;
                    }
                    case 31: {
                        switch (yy_input) {
                            case 18: {
                                this.yy_state = 37;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 32: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 38;
                                break block0;
                            }
                        }
                        this.yy_state = 32;
                        break;
                    }
                    case 33: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 39;
                                break block0;
                            }
                        }
                        this.yy_state = 32;
                        break;
                    }
                    case 35: {
                        switch (yy_input) {
                            case 9: {
                                this.yy_state = 40;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 36: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 36;
                                break block0;
                            }
                            case 15: {
                                this.yy_state = 41;
                                break block0;
                            }
                        }
                        this.yy_state = 30;
                        break;
                    }
                    case 37: {
                        switch (yy_input) {
                            case 14: {
                                this.yy_state = 42;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 38: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 43;
                                break block0;
                            }
                        }
                        this.yy_state = 32;
                        break;
                    }
                    case 39: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 43;
                                break block0;
                            }
                            case 5: {
                                yy_isFinal = true;
                                this.yy_state = 44;
                                break block0;
                            }
                        }
                        this.yy_state = 32;
                        break;
                    }
                    case 40: {
                        switch (yy_input) {
                            case 10: {
                                this.yy_state = 45;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 41: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 36;
                                break block0;
                            }
                            case 12: {
                                this.yy_state = 46;
                                break block0;
                            }
                        }
                        this.yy_state = 30;
                        break;
                    }
                    case 42: {
                        switch (yy_input) {
                            case 10: {
                                this.yy_state = 47;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 43: {
                        switch (yy_input) {
                            case 5: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 27;
                                break block0;
                            }
                            case 3: {
                                this.yy_state = 43;
                                break block0;
                            }
                        }
                        this.yy_state = 32;
                        break;
                    }
                    case 44: {
                        switch (yy_input) {
                            case 3: {
                                this.yy_state = 38;
                                break block0;
                            }
                        }
                        this.yy_state = 32;
                        break;
                    }
                    case 45: {
                        switch (yy_input) {
                            case 9: {
                                this.yy_state = 48;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 46: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 36;
                                break block0;
                            }
                            case 13: {
                                this.yy_state = 49;
                                break block0;
                            }
                        }
                        this.yy_state = 30;
                        break;
                    }
                    case 47: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    case 48: {
                        switch (yy_input) {
                            case 4: {
                                this.yy_state = 51;
                                break block0;
                            }
                        }
                        break block274;
                    }
                    case 49: {
                        switch (yy_input) {
                            case 14: {
                                this.yy_state = 20;
                                break block0;
                            }
                            case 1: {
                                this.yy_state = 36;
                                break block0;
                            }
                        }
                        this.yy_state = 30;
                        break;
                    }
                    case 50: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                            case 15: {
                                this.yy_state = 52;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    case 51: {
                        switch (yy_input) {
                            case 11: {
                                this.yy_state = 53;
                                break block0;
                            }
                        }
                        this.yy_state = 51;
                        break;
                    }
                    case 52: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                            case 16: {
                                this.yy_state = 54;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    case 53: {
                        switch (yy_input) {
                            case 11: {
                                this.yy_state = 55;
                                break block0;
                            }
                        }
                        this.yy_state = 51;
                        break;
                    }
                    case 54: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                            case 7: {
                                this.yy_state = 56;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    case 55: {
                        switch (yy_input) {
                            case 5: {
                                yy_isFinal = true;
                                yy_noLookAhead = true;
                                this.yy_state = 27;
                                break block0;
                            }
                            case 11: {
                                this.yy_state = 55;
                                break block0;
                            }
                        }
                        this.yy_state = 51;
                        break;
                    }
                    case 56: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                            case 17: {
                                this.yy_state = 57;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    case 57: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                            case 18: {
                                this.yy_state = 58;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    case 58: {
                        switch (yy_input) {
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                            case 14: {
                                this.yy_state = 59;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    case 59: {
                        switch (yy_input) {
                            case 10: {
                                this.yy_state = 20;
                                break block0;
                            }
                            case 1: {
                                this.yy_state = 50;
                                break block0;
                            }
                        }
                        this.yy_state = 47;
                        break;
                    }
                    default: {
                        this.yy_ScanError(1);
                    }
                }
                if (!yy_isFinal) continue;
                yy_action = this.yy_state;
                yy_markedPos_l = yy_currentPos_l;
                if (yy_noLookAhead) break;
            }
            this.yy_markedPos = yy_markedPos_l;
            switch (yy_action) {
                case 5: {
                    this.yybegin(0);
                    return 265;
                }
                case 61: {
                    continue block273;
                }
                case 3: {
                    this.yybegin(1);
                    return 264;
                }
                case 62: {
                    continue block273;
                }
                case 18: {
                    return 263;
                }
                case 63: {
                    continue block273;
                }
                case 7: {
                    return 257;
                }
                case 64: {
                    continue block273;
                }
                case 9: {
                    return 259;
                }
                case 65: {
                    continue block273;
                }
                case 44: {
                    return 262;
                }
                case 66: {
                    continue block273;
                }
                case 34: {
                    this.yybegin(1);
                    return 266;
                }
                case 67: {
                    continue block273;
                }
                case 27: {
                    return 262;
                }
                case 68: {
                    continue block273;
                }
                case 22: {
                    this.yybegin(1);
                    return 267;
                }
                case 69: {
                    continue block273;
                }
                case 2: {
                    return 262;
                }
                case 70: {
                    continue block273;
                }
                case 4: {
                    return 261;
                }
                case 71: {
                    continue block273;
                }
                case 8: {
                    return 258;
                }
                case 72: {
                    continue block273;
                }
                case 6: 
                case 10: 
                case 11: {
                    this.reportError("Illegal character <" + this.yytext() + ">", this.line(), this.column());
                    return 262;
                }
                case 73: {
                    continue block273;
                }
            }
            if (yy_input == -1 && this.yy_startRead == this.yy_currentPos) {
                this.yy_atEOF = true;
                this.yy_do_eof();
                return 0;
            }
            this.yy_ScanError(2);
        }
    }
}

