/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.CommonToken;
import groovyjarjarantlr.GrammarSymbol;
import groovyjarjarantlr.LLkGrammarAnalyzer;
import groovyjarjarantlr.RuleSymbol;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenManager;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.collections.impl.Vector;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class Grammar {
    protected Tool antlrTool;
    protected CodeGenerator generator;
    protected LLkGrammarAnalyzer theLLkAnalyzer;
    protected Hashtable symbols;
    protected boolean buildAST = false;
    protected boolean analyzerDebug = false;
    protected boolean interactive = false;
    protected String superClass = null;
    protected TokenManager tokenManager;
    protected String exportVocab = null;
    protected String importVocab = null;
    protected Hashtable options;
    protected Vector rules;
    protected Token preambleAction = new CommonToken(0, "");
    protected String className = null;
    protected String fileName = null;
    protected Token classMemberAction = new CommonToken(0, "");
    protected boolean hasSyntacticPredicate = false;
    protected boolean hasUserErrorHandling = false;
    protected int maxk = 1;
    protected boolean traceRules = false;
    protected boolean debuggingOutput = false;
    protected boolean defaultErrorHandler = true;
    protected String comment = null;

    public Grammar(String string, Tool tool, String string2) {
        this.className = string;
        this.antlrTool = tool;
        this.symbols = new Hashtable();
        this.options = new Hashtable();
        this.rules = new Vector(100);
        this.superClass = string2;
    }

    public void define(RuleSymbol ruleSymbol) {
        this.rules.appendElement(ruleSymbol);
        this.symbols.put(ruleSymbol.getId(), ruleSymbol);
    }

    public abstract void generate() throws IOException;

    protected String getClassName() {
        return this.className;
    }

    public boolean getDefaultErrorHandler() {
        return this.defaultErrorHandler;
    }

    public String getFilename() {
        return this.fileName;
    }

    public int getIntegerOption(String string) throws NumberFormatException {
        Token token = (Token)this.options.get(string);
        if (token == null || token.getType() != 20) {
            throw new NumberFormatException();
        }
        return Integer.parseInt(token.getText());
    }

    public Token getOption(String string) {
        return (Token)this.options.get(string);
    }

    protected abstract String getSuperClass();

    public GrammarSymbol getSymbol(String string) {
        return (GrammarSymbol)this.symbols.get(string);
    }

    public Enumeration getSymbols() {
        return this.symbols.elements();
    }

    public boolean hasOption(String string) {
        return this.options.containsKey(string);
    }

    public boolean isDefined(String string) {
        return this.symbols.containsKey(string);
    }

    public abstract void processArguments(String[] var1);

    public void setCodeGenerator(CodeGenerator codeGenerator) {
        this.generator = codeGenerator;
    }

    public void setFilename(String string) {
        this.fileName = string;
    }

    public void setGrammarAnalyzer(LLkGrammarAnalyzer lLkGrammarAnalyzer) {
        this.theLLkAnalyzer = lLkGrammarAnalyzer;
    }

    public boolean setOption(String string, Token token) {
        this.options.put(string, token);
        String string2 = token.getText();
        if (string.equals("k")) {
            try {
                this.maxk = this.getIntegerOption("k");
                if (this.maxk <= 0) {
                    this.antlrTool.error("option 'k' must be greater than 0 (was " + token.getText() + ")", this.getFilename(), token.getLine(), token.getColumn());
                    this.maxk = 1;
                }
            }
            catch (NumberFormatException numberFormatException) {
                this.antlrTool.error("option 'k' must be an integer (was " + token.getText() + ")", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("codeGenMakeSwitchThreshold")) {
            try {
                int n = this.getIntegerOption("codeGenMakeSwitchThreshold");
            }
            catch (NumberFormatException numberFormatException) {
                this.antlrTool.error("option 'codeGenMakeSwitchThreshold' must be an integer", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("codeGenBitsetTestThreshold")) {
            try {
                int n = this.getIntegerOption("codeGenBitsetTestThreshold");
            }
            catch (NumberFormatException numberFormatException) {
                this.antlrTool.error("option 'codeGenBitsetTestThreshold' must be an integer", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("defaultErrorHandler")) {
            if (string2.equals("true")) {
                this.defaultErrorHandler = true;
            } else if (string2.equals("false")) {
                this.defaultErrorHandler = false;
            } else {
                this.antlrTool.error("Value for defaultErrorHandler must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("analyzerDebug")) {
            if (string2.equals("true")) {
                this.analyzerDebug = true;
            } else if (string2.equals("false")) {
                this.analyzerDebug = false;
            } else {
                this.antlrTool.error("option 'analyzerDebug' must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("codeGenDebug")) {
            if (string2.equals("true")) {
                this.analyzerDebug = true;
            } else if (string2.equals("false")) {
                this.analyzerDebug = false;
            } else {
                this.antlrTool.error("option 'codeGenDebug' must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (string.equals("classHeaderSuffix")) {
            return true;
        }
        if (string.equals("classHeaderPrefix")) {
            return true;
        }
        if (string.equals("namespaceAntlr")) {
            return true;
        }
        if (string.equals("namespaceStd")) {
            return true;
        }
        if (string.equals("genHashLines")) {
            return true;
        }
        return string.equals("noConstructors");
    }

    public void setTokenManager(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(20000);
        Enumeration enumeration = this.rules.elements();
        while (enumeration.hasMoreElements()) {
            RuleSymbol ruleSymbol = (RuleSymbol)enumeration.nextElement();
            if (ruleSymbol.id.equals("mnextToken")) continue;
            stringBuffer.append(ruleSymbol.getBlock().toString());
            stringBuffer.append("\n\n");
        }
        return stringBuffer.toString();
    }
}

