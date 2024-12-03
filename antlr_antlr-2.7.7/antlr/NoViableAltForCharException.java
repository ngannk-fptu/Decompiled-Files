/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.CharScanner;
import antlr.RecognitionException;

public class NoViableAltForCharException
extends RecognitionException {
    public char foundChar;

    public NoViableAltForCharException(char c, CharScanner charScanner) {
        super("NoViableAlt", charScanner.getFilename(), charScanner.getLine(), charScanner.getColumn());
        this.foundChar = c;
    }

    public NoViableAltForCharException(char c, String string, int n) {
        this(c, string, n, -1);
    }

    public NoViableAltForCharException(char c, String string, int n, int n2) {
        super("NoViableAlt", string, n, n2);
        this.foundChar = c;
    }

    public String getMessage() {
        String string = "unexpected char: ";
        if (this.foundChar >= ' ' && this.foundChar <= '~') {
            string = string + '\'';
            string = string + this.foundChar;
            string = string + '\'';
        } else {
            string = string + "0x" + Integer.toHexString(this.foundChar).toUpperCase();
        }
        return string;
    }
}

