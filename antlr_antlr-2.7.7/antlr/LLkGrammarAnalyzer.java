/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ActionElement;
import antlr.AlternativeBlock;
import antlr.BlockEndElement;
import antlr.CharLiteralElement;
import antlr.CharRangeElement;
import antlr.Grammar;
import antlr.GrammarAnalyzer;
import antlr.GrammarAtom;
import antlr.Lookahead;
import antlr.OneOrMoreBlock;
import antlr.RuleBlock;
import antlr.RuleEndElement;
import antlr.RuleRefElement;
import antlr.StringLiteralElement;
import antlr.SynPredBlock;
import antlr.TokenRangeElement;
import antlr.TreeElement;
import antlr.WildcardElement;
import antlr.ZeroOrMoreBlock;

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

