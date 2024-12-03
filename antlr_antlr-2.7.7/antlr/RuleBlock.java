/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.AlternativeBlock;
import antlr.ExceptionSpec;
import antlr.Grammar;
import antlr.LexerGrammar;
import antlr.Lookahead;
import antlr.ParserGrammar;
import antlr.RuleEndElement;
import antlr.Token;
import antlr.TokenSymbol;
import antlr.collections.impl.Vector;
import java.util.Hashtable;

public class RuleBlock
extends AlternativeBlock {
    protected String ruleName;
    protected String argAction = null;
    protected String throwsSpec = null;
    protected String returnAction = null;
    protected RuleEndElement endNode;
    protected boolean testLiterals = false;
    Vector labeledElements;
    protected boolean[] lock;
    protected Lookahead[] cache;
    Hashtable exceptionSpecs;
    protected boolean defaultErrorHandler = true;
    protected String ignoreRule = null;

    public RuleBlock(Grammar grammar, String string) {
        super(grammar);
        this.ruleName = string;
        this.labeledElements = new Vector();
        this.cache = new Lookahead[grammar.maxk + 1];
        this.exceptionSpecs = new Hashtable();
        this.setAutoGen(grammar instanceof ParserGrammar);
    }

    public RuleBlock(Grammar grammar, String string, int n, boolean bl) {
        this(grammar, string);
        this.line = n;
        this.setAutoGen(bl);
    }

    public void addExceptionSpec(ExceptionSpec exceptionSpec) {
        if (this.findExceptionSpec(exceptionSpec.label) != null) {
            if (exceptionSpec.label != null) {
                this.grammar.antlrTool.error("Rule '" + this.ruleName + "' already has an exception handler for label: " + exceptionSpec.label);
            } else {
                this.grammar.antlrTool.error("Rule '" + this.ruleName + "' already has an exception handler");
            }
        } else {
            this.exceptionSpecs.put(exceptionSpec.label == null ? "" : exceptionSpec.label.getText(), exceptionSpec);
        }
    }

    public ExceptionSpec findExceptionSpec(Token token) {
        return (ExceptionSpec)this.exceptionSpecs.get(token == null ? "" : token.getText());
    }

    public ExceptionSpec findExceptionSpec(String string) {
        return (ExceptionSpec)this.exceptionSpecs.get(string == null ? "" : string);
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public boolean getDefaultErrorHandler() {
        return this.defaultErrorHandler;
    }

    public RuleEndElement getEndElement() {
        return this.endNode;
    }

    public String getIgnoreRule() {
        return this.ignoreRule;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public boolean getTestLiterals() {
        return this.testLiterals;
    }

    public boolean isLexerAutoGenRule() {
        return this.ruleName.equals("nextToken");
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public void prepareForAnalysis() {
        super.prepareForAnalysis();
        this.lock = new boolean[this.grammar.maxk + 1];
    }

    public void setDefaultErrorHandler(boolean bl) {
        this.defaultErrorHandler = bl;
    }

    public void setEndElement(RuleEndElement ruleEndElement) {
        this.endNode = ruleEndElement;
    }

    public void setOption(Token token, Token token2) {
        if (token.getText().equals("defaultErrorHandler")) {
            if (token2.getText().equals("true")) {
                this.defaultErrorHandler = true;
            } else if (token2.getText().equals("false")) {
                this.defaultErrorHandler = false;
            } else {
                this.grammar.antlrTool.error("Value for defaultErrorHandler must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        } else if (token.getText().equals("testLiterals")) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.grammar.antlrTool.error("testLiterals option only valid for lexer rules", this.grammar.getFilename(), token.getLine(), token.getColumn());
            } else if (token2.getText().equals("true")) {
                this.testLiterals = true;
            } else if (token2.getText().equals("false")) {
                this.testLiterals = false;
            } else {
                this.grammar.antlrTool.error("Value for testLiterals must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        } else if (token.getText().equals("ignore")) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.grammar.antlrTool.error("ignore option only valid for lexer rules", this.grammar.getFilename(), token.getLine(), token.getColumn());
            } else {
                this.ignoreRule = token2.getText();
            }
        } else if (token.getText().equals("paraphrase")) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.grammar.antlrTool.error("paraphrase option only valid for lexer rules", this.grammar.getFilename(), token.getLine(), token.getColumn());
            } else {
                TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(this.ruleName);
                if (tokenSymbol == null) {
                    this.grammar.antlrTool.panic("cannot find token associated with rule " + this.ruleName);
                }
                tokenSymbol.setParaphrase(token2.getText());
            }
        } else if (token.getText().equals("generateAmbigWarnings")) {
            if (token2.getText().equals("true")) {
                this.generateAmbigWarnings = true;
            } else if (token2.getText().equals("false")) {
                this.generateAmbigWarnings = false;
            } else {
                this.grammar.antlrTool.error("Value for generateAmbigWarnings must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        } else {
            this.grammar.antlrTool.error("Invalid rule option: " + token.getText(), this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }

    public String toString() {
        String string = " FOLLOW={";
        Lookahead[] lookaheadArray = this.endNode.cache;
        int n = this.grammar.maxk;
        boolean bl = true;
        for (int i = 1; i <= n; ++i) {
            if (lookaheadArray[i] == null) continue;
            string = string + lookaheadArray[i].toString(",", this.grammar.tokenManager.getVocabulary());
            bl = false;
            if (i >= n || lookaheadArray[i + 1] == null) continue;
            string = string + ";";
        }
        string = string + "}";
        if (bl) {
            string = "";
        }
        return this.ruleName + ": " + super.toString() + " ;" + string;
    }
}

