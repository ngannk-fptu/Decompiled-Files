/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.CharScanner;

public class ANTLRHashString {
    private String s;
    private char[] buf;
    private int len;
    private CharScanner lexer;
    private static final int prime = 151;

    public ANTLRHashString(char[] cArray, int n, CharScanner charScanner) {
        this.lexer = charScanner;
        this.setBuffer(cArray, n);
    }

    public ANTLRHashString(CharScanner charScanner) {
        this.lexer = charScanner;
    }

    public ANTLRHashString(String string, CharScanner charScanner) {
        this.lexer = charScanner;
        this.setString(string);
    }

    private final char charAt(int n) {
        return this.s != null ? this.s.charAt(n) : this.buf[n];
    }

    public boolean equals(Object object) {
        if (!(object instanceof ANTLRHashString) && !(object instanceof String)) {
            return false;
        }
        ANTLRHashString aNTLRHashString = object instanceof String ? new ANTLRHashString((String)object, this.lexer) : (ANTLRHashString)object;
        int n = this.length();
        if (aNTLRHashString.length() != n) {
            return false;
        }
        if (this.lexer.getCaseSensitiveLiterals()) {
            for (int i = 0; i < n; ++i) {
                if (this.charAt(i) == aNTLRHashString.charAt(i)) continue;
                return false;
            }
        } else {
            for (int i = 0; i < n; ++i) {
                if (this.lexer.toLower(this.charAt(i)) == this.lexer.toLower(aNTLRHashString.charAt(i))) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int n = 0;
        int n2 = this.length();
        if (this.lexer.getCaseSensitiveLiterals()) {
            for (int i = 0; i < n2; ++i) {
                n = n * 151 + this.charAt(i);
            }
        } else {
            for (int i = 0; i < n2; ++i) {
                n = n * 151 + this.lexer.toLower(this.charAt(i));
            }
        }
        return n;
    }

    private final int length() {
        return this.s != null ? this.s.length() : this.len;
    }

    public void setBuffer(char[] cArray, int n) {
        this.buf = cArray;
        this.len = n;
        this.s = null;
    }

    public void setString(String string) {
        this.s = string;
        this.buf = null;
    }
}

