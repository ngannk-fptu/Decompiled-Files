/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ANTLRTokdefLexer;
import groovyjarjarantlr.ANTLRTokdefParser;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.SimpleTokenManager;
import groovyjarjarantlr.StringLiteralSymbol;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.TokenSymbol;
import groovyjarjarantlr.Tool;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

class ImportVocabTokenManager
extends SimpleTokenManager
implements Cloneable {
    private String filename;
    protected Grammar grammar;

    ImportVocabTokenManager(Grammar grammar, String string, String string2, Tool tool) {
        super(string2, tool);
        this.grammar = grammar;
        this.filename = string;
        File file = new File(this.filename);
        if (!file.exists() && !(file = new File(this.antlrTool.getOutputDirectory(), this.filename)).exists()) {
            this.antlrTool.panic("Cannot find importVocab file '" + this.filename + "'");
        }
        this.setReadOnly(true);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            ANTLRTokdefLexer aNTLRTokdefLexer = new ANTLRTokdefLexer(bufferedReader);
            ANTLRTokdefParser aNTLRTokdefParser = new ANTLRTokdefParser(aNTLRTokdefLexer);
            aNTLRTokdefParser.setTool(this.antlrTool);
            aNTLRTokdefParser.setFilename(this.filename);
            aNTLRTokdefParser.file(this);
        }
        catch (FileNotFoundException fileNotFoundException) {
            this.antlrTool.panic("Cannot find importVocab file '" + this.filename + "'");
        }
        catch (RecognitionException recognitionException) {
            this.antlrTool.panic("Error parsing importVocab file '" + this.filename + "': " + recognitionException.toString());
        }
        catch (TokenStreamException tokenStreamException) {
            this.antlrTool.panic("Error reading importVocab file '" + this.filename + "'");
        }
    }

    public Object clone() {
        ImportVocabTokenManager importVocabTokenManager = (ImportVocabTokenManager)super.clone();
        importVocabTokenManager.filename = this.filename;
        importVocabTokenManager.grammar = this.grammar;
        return importVocabTokenManager;
    }

    public void define(TokenSymbol tokenSymbol) {
        super.define(tokenSymbol);
    }

    public void define(String string, int n) {
        TokenSymbol tokenSymbol = null;
        tokenSymbol = string.startsWith("\"") ? new StringLiteralSymbol(string) : new TokenSymbol(string);
        tokenSymbol.setTokenType(n);
        super.define(tokenSymbol);
        this.maxToken = n + 1 > this.maxToken ? n + 1 : this.maxToken;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public int nextTokenType() {
        return super.nextTokenType();
    }
}

