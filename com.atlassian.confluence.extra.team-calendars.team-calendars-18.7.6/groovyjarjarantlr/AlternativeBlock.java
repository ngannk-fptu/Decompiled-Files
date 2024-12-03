/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.Alternative;
import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.RuleRefElement;
import groovyjarjarantlr.RuleSymbol;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.collections.impl.Vector;

class AlternativeBlock
extends AlternativeElement {
    protected String initAction = null;
    protected Vector alternatives = new Vector(5);
    protected String label;
    protected int alti;
    protected int altj;
    protected int analysisAlt;
    protected boolean hasAnAction = false;
    protected boolean hasASynPred = false;
    protected int ID = 0;
    protected static int nblks;
    boolean not = false;
    boolean greedy = true;
    boolean greedySet = false;
    protected boolean doAutoGen = true;
    protected boolean warnWhenFollowAmbig = true;
    protected boolean generateAmbigWarnings = true;

    public AlternativeBlock(Grammar grammar) {
        super(grammar);
        this.not = false;
        this.ID = ++nblks;
    }

    public AlternativeBlock(Grammar grammar, Token token, boolean bl) {
        super(grammar, token);
        this.not = bl;
        this.ID = ++nblks;
    }

    public void addAlternative(Alternative alternative) {
        this.alternatives.appendElement(alternative);
    }

    public void generate() {
        this.grammar.generator.gen(this);
    }

    public Alternative getAlternativeAt(int n) {
        return (Alternative)this.alternatives.elementAt(n);
    }

    public Vector getAlternatives() {
        return this.alternatives;
    }

    public boolean getAutoGen() {
        return this.doAutoGen;
    }

    public String getInitAction() {
        return this.initAction;
    }

    public String getLabel() {
        return this.label;
    }

    public Lookahead look(int n) {
        return this.grammar.theLLkAnalyzer.look(n, this);
    }

    public void prepareForAnalysis() {
        for (int i = 0; i < this.alternatives.size(); ++i) {
            Alternative alternative = (Alternative)this.alternatives.elementAt(i);
            alternative.cache = new Lookahead[this.grammar.maxk + 1];
            alternative.lookaheadDepth = -1;
        }
    }

    public void removeTrackingOfRuleRefs(Grammar grammar) {
        for (int i = 0; i < this.alternatives.size(); ++i) {
            Alternative alternative = this.getAlternativeAt(i);
            AlternativeElement alternativeElement = alternative.head;
            while (alternativeElement != null) {
                if (alternativeElement instanceof RuleRefElement) {
                    RuleRefElement ruleRefElement = (RuleRefElement)alternativeElement;
                    RuleSymbol ruleSymbol = (RuleSymbol)grammar.getSymbol(ruleRefElement.targetRule);
                    if (ruleSymbol == null) {
                        this.grammar.antlrTool.error("rule " + ruleRefElement.targetRule + " referenced in (...)=>, but not defined");
                    } else {
                        ruleSymbol.references.removeElement(ruleRefElement);
                    }
                } else if (alternativeElement instanceof AlternativeBlock) {
                    ((AlternativeBlock)alternativeElement).removeTrackingOfRuleRefs(grammar);
                }
                alternativeElement = alternativeElement.next;
            }
        }
    }

    public void setAlternatives(Vector vector) {
        this.alternatives = vector;
    }

    public void setAutoGen(boolean bl) {
        this.doAutoGen = bl;
    }

    public void setInitAction(String string) {
        this.initAction = string;
    }

    public void setLabel(String string) {
        this.label = string;
    }

    public void setOption(Token token, Token token2) {
        if (token.getText().equals("warnWhenFollowAmbig")) {
            if (token2.getText().equals("true")) {
                this.warnWhenFollowAmbig = true;
            } else if (token2.getText().equals("false")) {
                this.warnWhenFollowAmbig = false;
            } else {
                this.grammar.antlrTool.error("Value for warnWhenFollowAmbig must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        } else if (token.getText().equals("generateAmbigWarnings")) {
            if (token2.getText().equals("true")) {
                this.generateAmbigWarnings = true;
            } else if (token2.getText().equals("false")) {
                this.generateAmbigWarnings = false;
            } else {
                this.grammar.antlrTool.error("Value for generateAmbigWarnings must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        } else if (token.getText().equals("greedy")) {
            if (token2.getText().equals("true")) {
                this.greedy = true;
                this.greedySet = true;
            } else if (token2.getText().equals("false")) {
                this.greedy = false;
                this.greedySet = true;
            } else {
                this.grammar.antlrTool.error("Value for greedy must be true or false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        } else {
            this.grammar.antlrTool.error("Invalid subrule option: " + token.getText(), this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }

    public String toString() {
        String string = " (";
        if (this.initAction != null) {
            string = string + this.initAction;
        }
        for (int i = 0; i < this.alternatives.size(); ++i) {
            Alternative alternative = this.getAlternativeAt(i);
            Lookahead[] lookaheadArray = alternative.cache;
            int n = alternative.lookaheadDepth;
            if (n != -1) {
                if (n == Integer.MAX_VALUE) {
                    string = string + "{?}:";
                } else {
                    string = string + " {";
                    for (int j = 1; j <= n; ++j) {
                        string = string + lookaheadArray[j].toString(",", this.grammar.tokenManager.getVocabulary());
                        if (j >= n || lookaheadArray[j + 1] == null) continue;
                        string = string + ";";
                    }
                    string = string + "}:";
                }
            }
            AlternativeElement alternativeElement = alternative.head;
            String string2 = alternative.semPred;
            if (string2 != null) {
                string = string + string2;
            }
            while (alternativeElement != null) {
                string = string + alternativeElement;
                alternativeElement = alternativeElement.next;
            }
            if (i >= this.alternatives.size() - 1) continue;
            string = string + " |";
        }
        string = string + " )";
        return string;
    }
}

