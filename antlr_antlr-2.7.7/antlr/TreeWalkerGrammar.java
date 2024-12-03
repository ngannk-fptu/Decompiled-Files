/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.Grammar;
import antlr.Token;
import antlr.Tool;
import java.io.IOException;

class TreeWalkerGrammar
extends Grammar {
    protected boolean transform = false;

    TreeWalkerGrammar(String string, Tool tool, String string2) {
        super(string, tool, string2);
    }

    public void generate() throws IOException {
        this.generator.gen(this);
    }

    protected String getSuperClass() {
        return "TreeParser";
    }

    public void processArguments(String[] stringArray) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (stringArray[i].equals("-trace")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
                continue;
            }
            if (!stringArray[i].equals("-traceTreeParser")) continue;
            this.traceRules = true;
            this.antlrTool.setArgOK(i);
        }
    }

    public boolean setOption(String string, Token token) {
        if (string.equals("buildAST")) {
            if (token.getText().equals("true")) {
                this.buildAST = true;
            } else if (token.getText().equals("false")) {
                this.buildAST = false;
            } else {
                this.antlrTool.error("buildAST option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
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

