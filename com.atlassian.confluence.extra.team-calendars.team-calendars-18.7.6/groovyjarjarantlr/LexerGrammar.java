/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.collections.impl.BitSet;
import java.io.IOException;

class LexerGrammar
extends Grammar {
    protected BitSet charVocabulary;
    protected boolean testLiterals = true;
    protected boolean caseSensitiveLiterals = true;
    protected boolean caseSensitive = true;
    protected boolean filterMode = false;
    protected String filterRule = null;

    LexerGrammar(String string, Tool tool, String string2) {
        super(string, tool, string2);
        BitSet bitSet = new BitSet();
        for (int i = 0; i <= 127; ++i) {
            bitSet.add(i);
        }
        this.setCharVocabulary(bitSet);
        this.defaultErrorHandler = false;
    }

    public void generate() throws IOException {
        this.generator.gen(this);
    }

    public String getSuperClass() {
        if (this.debuggingOutput) {
            return "debug.DebuggingCharScanner";
        }
        return "CharScanner";
    }

    public boolean getTestLiterals() {
        return this.testLiterals;
    }

    public void processArguments(String[] stringArray) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (stringArray[i].equals("-trace")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
                continue;
            }
            if (stringArray[i].equals("-traceLexer")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
                continue;
            }
            if (!stringArray[i].equals("-debug")) continue;
            this.debuggingOutput = true;
            this.antlrTool.setArgOK(i);
        }
    }

    public void setCharVocabulary(BitSet bitSet) {
        this.charVocabulary = bitSet;
    }

    public boolean setOption(String string, Token token) {
        String string2 = token.getText();
        if (string.equals("buildAST")) {
            this.antlrTool.warning("buildAST option is not valid for lexer", this.getFilename(), token.getLine(), token.getColumn());
            return true;
        }
        if (string.equals("testLiterals")) {
            if (string2.equals("true")) {
                this.testLiterals = true;
            } else if (string2.equals("false")) {
                this.testLiterals = false;
            } else {
                this.antlrTool.warning("testLiterals option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("interactive")) {
            if (string2.equals("true")) {
                this.interactive = true;
            } else if (string2.equals("false")) {
                this.interactive = false;
            } else {
                this.antlrTool.error("interactive option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("caseSensitive")) {
            if (string2.equals("true")) {
                this.caseSensitive = true;
            } else if (string2.equals("false")) {
                this.caseSensitive = false;
            } else {
                this.antlrTool.warning("caseSensitive option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("caseSensitiveLiterals")) {
            if (string2.equals("true")) {
                this.caseSensitiveLiterals = true;
            } else if (string2.equals("false")) {
                this.caseSensitiveLiterals = false;
            } else {
                this.antlrTool.warning("caseSensitiveLiterals option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("filter")) {
            if (string2.equals("true")) {
                this.filterMode = true;
            } else if (string2.equals("false")) {
                this.filterMode = false;
            } else if (token.getType() == 24) {
                this.filterMode = true;
                this.filterRule = string2;
            } else {
                this.antlrTool.warning("filter option must be true, false, or a lexer rule name", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("longestPossible")) {
            this.antlrTool.warning("longestPossible option has been deprecated; ignoring it...", this.getFilename(), token.getLine(), token.getColumn());
            return true;
        }
        if (string.equals("className")) {
            super.setOption(string, token);
            return true;
        }
        if (super.setOption(string, token)) {
            return true;
        }
        this.antlrTool.error("Invalid option: " + string, this.getFilename(), token.getLine(), token.getColumn());
        return false;
    }
}

