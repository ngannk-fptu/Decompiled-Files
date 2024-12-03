/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.SemanticException;
import antlr.Token;
import antlr.collections.impl.BitSet;

public interface ANTLRGrammarParseBehavior {
    public void abortGrammar();

    public void beginAlt(boolean var1);

    public void beginChildList();

    public void beginExceptionGroup();

    public void beginExceptionSpec(Token var1);

    public void beginSubRule(Token var1, Token var2, boolean var3);

    public void beginTree(Token var1) throws SemanticException;

    public void defineRuleName(Token var1, String var2, boolean var3, String var4) throws SemanticException;

    public void defineToken(Token var1, Token var2);

    public void endAlt();

    public void endChildList();

    public void endExceptionGroup();

    public void endExceptionSpec();

    public void endGrammar();

    public void endOptions();

    public void endRule(String var1);

    public void endSubRule();

    public void endTree();

    public void hasError();

    public void noASTSubRule();

    public void oneOrMoreSubRule();

    public void optionalSubRule();

    public void refAction(Token var1);

    public void refArgAction(Token var1);

    public void setUserExceptions(String var1);

    public void refCharLiteral(Token var1, Token var2, boolean var3, int var4, boolean var5);

    public void refCharRange(Token var1, Token var2, Token var3, int var4, boolean var5);

    public void refElementOption(Token var1, Token var2);

    public void refTokensSpecElementOption(Token var1, Token var2, Token var3);

    public void refExceptionHandler(Token var1, Token var2);

    public void refHeaderAction(Token var1, Token var2);

    public void refInitAction(Token var1);

    public void refMemberAction(Token var1);

    public void refPreambleAction(Token var1);

    public void refReturnAction(Token var1);

    public void refRule(Token var1, Token var2, Token var3, Token var4, int var5);

    public void refSemPred(Token var1);

    public void refStringLiteral(Token var1, Token var2, int var3, boolean var4);

    public void refToken(Token var1, Token var2, Token var3, Token var4, boolean var5, int var6, boolean var7);

    public void refTokenRange(Token var1, Token var2, Token var3, int var4, boolean var5);

    public void refTreeSpecifier(Token var1);

    public void refWildcard(Token var1, Token var2, int var3);

    public void setArgOfRuleRef(Token var1);

    public void setCharVocabulary(BitSet var1);

    public void setFileOption(Token var1, Token var2, String var3);

    public void setGrammarOption(Token var1, Token var2);

    public void setRuleOption(Token var1, Token var2);

    public void setSubruleOption(Token var1, Token var2);

    public void startLexer(String var1, Token var2, String var3, String var4);

    public void startParser(String var1, Token var2, String var3, String var4);

    public void startTreeWalker(String var1, Token var2, String var3, String var4);

    public void synPred();

    public void zeroOrMoreSubRule();
}

