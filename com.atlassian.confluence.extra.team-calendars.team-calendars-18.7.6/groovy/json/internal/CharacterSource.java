/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

public interface CharacterSource {
    public void skipWhiteSpace();

    public int nextChar();

    public int currentChar();

    public boolean hasChar();

    public boolean consumeIfMatch(char[] var1);

    public int location();

    public int safeNextChar();

    public char[] findNextChar(int var1, int var2);

    public boolean hadEscape();

    public char[] readNumber();

    public String errorDetails(String var1);
}

