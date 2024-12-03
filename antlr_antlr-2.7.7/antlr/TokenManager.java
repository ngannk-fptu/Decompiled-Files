/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.TokenSymbol;
import antlr.collections.impl.Vector;
import java.util.Enumeration;

interface TokenManager {
    public Object clone();

    public void define(TokenSymbol var1);

    public String getName();

    public String getTokenStringAt(int var1);

    public TokenSymbol getTokenSymbol(String var1);

    public TokenSymbol getTokenSymbolAt(int var1);

    public Enumeration getTokenSymbolElements();

    public Enumeration getTokenSymbolKeys();

    public Vector getVocabulary();

    public boolean isReadOnly();

    public void mapToTokenSymbol(String var1, TokenSymbol var2);

    public int maxTokenType();

    public int nextTokenType();

    public void setName(String var1);

    public void setReadOnly(boolean var1);

    public boolean tokenDefined(String var1);
}

