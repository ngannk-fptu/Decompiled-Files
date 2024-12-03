/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import java.io.Serializable;

class Token
implements Serializable {
    private static final long serialVersionUID = 1L;
    public int kind;
    public int beginLine;
    public int beginColumn;
    public int endLine;
    public int endColumn;
    public String image;
    public Token next;
    public Token specialToken;

    public Object getValue() {
        return null;
    }

    public Token() {
    }

    public Token(int kind) {
        this(kind, null);
    }

    public Token(int kind, String image) {
        this.kind = kind;
        this.image = image;
    }

    public String toString() {
        return this.image;
    }

    public static Token newToken(int ofKind, String image) {
        switch (ofKind) {
            default: 
        }
        return new Token(ofKind, image);
    }

    public static Token newToken(int ofKind) {
        return Token.newToken(ofKind, null);
    }
}

