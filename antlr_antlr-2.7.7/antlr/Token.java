/*
 * Decompiled with CFR 0.152.
 */
package antlr;

public class Token
implements Cloneable {
    public static final int MIN_USER_TYPE = 4;
    public static final int NULL_TREE_LOOKAHEAD = 3;
    public static final int INVALID_TYPE = 0;
    public static final int EOF_TYPE = 1;
    public static final int SKIP = -1;
    protected int type = 0;
    public static Token badToken = new Token(0, "<no text>");

    public Token() {
    }

    public Token(int n) {
        this.type = n;
    }

    public Token(int n, String string) {
        this.type = n;
        this.setText(string);
    }

    public int getColumn() {
        return 0;
    }

    public int getLine() {
        return 0;
    }

    public String getFilename() {
        return null;
    }

    public void setFilename(String string) {
    }

    public String getText() {
        return "<no text>";
    }

    public void setText(String string) {
    }

    public void setColumn(int n) {
    }

    public void setLine(int n) {
    }

    public int getType() {
        return this.type;
    }

    public void setType(int n) {
        this.type = n;
    }

    public String toString() {
        return "[\"" + this.getText() + "\",<" + this.getType() + ">]";
    }
}

