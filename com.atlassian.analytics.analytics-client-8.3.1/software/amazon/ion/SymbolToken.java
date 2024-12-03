/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

public interface SymbolToken {
    public static final SymbolToken[] EMPTY_ARRAY = new SymbolToken[0];

    public String getText();

    public String assumeText();

    public int getSid();
}

