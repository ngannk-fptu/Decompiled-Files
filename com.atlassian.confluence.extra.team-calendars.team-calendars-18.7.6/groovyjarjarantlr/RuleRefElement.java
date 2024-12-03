/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.Token;

class RuleRefElement
extends AlternativeElement {
    protected String targetRule;
    protected String args = null;
    protected String idAssign = null;
    protected String label;

    public RuleRefElement(Grammar grammar, Token token, int n) {
        super(grammar, token, n);
        this.targetRule = token.getText();
        if (token.type == 24) {
            this.targetRule = CodeGenerator.encodeLexerRuleName(this.targetRule);
        }
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public String getArgs() {
        return this.args;
    }

    public String getIdAssign() {
        return this.idAssign;
    }

    public String getLabel() {
        return this.label;
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public void setArgs(String string) {
        this.args = string;
    }

    public void setIdAssign(String string) {
        this.idAssign = string;
    }

    public void setLabel(String string) {
        this.label = string;
    }

    public String toString() {
        if (this.args != null) {
            return " " + this.targetRule + this.args;
        }
        return " " + this.targetRule;
    }
}

