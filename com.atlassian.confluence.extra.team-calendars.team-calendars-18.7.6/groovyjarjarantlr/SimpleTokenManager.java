/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.TokenManager;
import groovyjarjarantlr.TokenSymbol;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.collections.impl.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

class SimpleTokenManager
implements TokenManager,
Cloneable {
    protected int maxToken = 4;
    protected Vector vocabulary;
    private Hashtable table;
    protected Tool antlrTool;
    protected String name;
    protected boolean readOnly = false;

    SimpleTokenManager(String string, Tool tool) {
        this.antlrTool = tool;
        this.name = string;
        this.vocabulary = new Vector(1);
        this.table = new Hashtable();
        TokenSymbol tokenSymbol = new TokenSymbol("EOF");
        tokenSymbol.setTokenType(1);
        this.define(tokenSymbol);
        this.vocabulary.ensureCapacity(3);
        this.vocabulary.setElementAt("NULL_TREE_LOOKAHEAD", 3);
    }

    public Object clone() {
        SimpleTokenManager simpleTokenManager;
        try {
            simpleTokenManager = (SimpleTokenManager)super.clone();
            simpleTokenManager.vocabulary = (Vector)this.vocabulary.clone();
            simpleTokenManager.table = (Hashtable)this.table.clone();
            simpleTokenManager.maxToken = this.maxToken;
            simpleTokenManager.antlrTool = this.antlrTool;
            simpleTokenManager.name = this.name;
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            this.antlrTool.panic("cannot clone token manager");
            return null;
        }
        return simpleTokenManager;
    }

    public void define(TokenSymbol tokenSymbol) {
        this.vocabulary.ensureCapacity(tokenSymbol.getTokenType());
        this.vocabulary.setElementAt(tokenSymbol.getId(), tokenSymbol.getTokenType());
        this.mapToTokenSymbol(tokenSymbol.getId(), tokenSymbol);
    }

    public String getName() {
        return this.name;
    }

    public String getTokenStringAt(int n) {
        return (String)this.vocabulary.elementAt(n);
    }

    public TokenSymbol getTokenSymbol(String string) {
        return (TokenSymbol)this.table.get(string);
    }

    public TokenSymbol getTokenSymbolAt(int n) {
        return this.getTokenSymbol(this.getTokenStringAt(n));
    }

    public Enumeration getTokenSymbolElements() {
        return this.table.elements();
    }

    public Enumeration getTokenSymbolKeys() {
        return this.table.keys();
    }

    public Vector getVocabulary() {
        return this.vocabulary;
    }

    public boolean isReadOnly() {
        return false;
    }

    public void mapToTokenSymbol(String string, TokenSymbol tokenSymbol) {
        this.table.put(string, tokenSymbol);
    }

    public int maxTokenType() {
        return this.maxToken - 1;
    }

    public int nextTokenType() {
        return this.maxToken++;
    }

    public void setName(String string) {
        this.name = string;
    }

    public void setReadOnly(boolean bl) {
        this.readOnly = bl;
    }

    public boolean tokenDefined(String string) {
        return this.table.containsKey(string);
    }
}

