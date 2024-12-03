/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.Tool;
import java.io.IOException;

class ParserGrammar
extends Grammar {
    ParserGrammar(String string, Tool tool, String string2) {
        super(string, tool, string2);
    }

    public void generate() throws IOException {
        this.generator.gen(this);
    }

    protected String getSuperClass() {
        if (this.debuggingOutput) {
            return "debug.LLkDebuggingParser";
        }
        return "LLkParser";
    }

    public void processArguments(String[] stringArray) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (stringArray[i].equals("-trace")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
                continue;
            }
            if (stringArray[i].equals("-traceParser")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
                continue;
            }
            if (!stringArray[i].equals("-debug")) continue;
            this.debuggingOutput = true;
            this.antlrTool.setArgOK(i);
        }
    }

    public boolean setOption(String string, Token token) {
        String string2 = token.getText();
        if (string.equals("buildAST")) {
            if (string2.equals("true")) {
                this.buildAST = true;
            } else if (string2.equals("false")) {
                this.buildAST = false;
            } else {
                this.antlrTool.error("buildAST option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
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
        if (string.equals("ASTLabelType")) {
            super.setOption(string, token);
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

