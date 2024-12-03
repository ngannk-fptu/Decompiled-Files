/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ActionElement;
import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.BlockEndElement;
import groovyjarjarantlr.CharLiteralElement;
import groovyjarjarantlr.CharRangeElement;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.GrammarAnalyzer;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.OneOrMoreBlock;
import groovyjarjarantlr.RuleBlock;
import groovyjarjarantlr.RuleEndElement;
import groovyjarjarantlr.RuleRefElement;
import groovyjarjarantlr.StringLiteralElement;
import groovyjarjarantlr.SynPredBlock;
import groovyjarjarantlr.TokenRangeElement;
import groovyjarjarantlr.TreeElement;
import groovyjarjarantlr.WildcardElement;
import groovyjarjarantlr.ZeroOrMoreBlock;

public interface LLkGrammarAnalyzer
extends GrammarAnalyzer {
    public boolean deterministic(AlternativeBlock var1);

    public boolean deterministic(OneOrMoreBlock var1);

    public boolean deterministic(ZeroOrMoreBlock var1);

    public Lookahead FOLLOW(int var1, RuleEndElement var2);

    public Lookahead look(int var1, ActionElement var2);

    public Lookahead look(int var1, AlternativeBlock var2);

    public Lookahead look(int var1, BlockEndElement var2);

    public Lookahead look(int var1, CharLiteralElement var2);

    public Lookahead look(int var1, CharRangeElement var2);

    public Lookahead look(int var1, GrammarAtom var2);

    public Lookahead look(int var1, OneOrMoreBlock var2);

    public Lookahead look(int var1, RuleBlock var2);

    public Lookahead look(int var1, RuleEndElement var2);

    public Lookahead look(int var1, RuleRefElement var2);

    public Lookahead look(int var1, StringLiteralElement var2);

    public Lookahead look(int var1, SynPredBlock var2);

    public Lookahead look(int var1, TokenRangeElement var2);

    public Lookahead look(int var1, TreeElement var2);

    public Lookahead look(int var1, WildcardElement var2);

    public Lookahead look(int var1, ZeroOrMoreBlock var2);

    public Lookahead look(int var1, String var2);

    public void setGrammar(Grammar var1);

    public boolean subruleCanBeInverted(AlternativeBlock var1, boolean var2);
}

