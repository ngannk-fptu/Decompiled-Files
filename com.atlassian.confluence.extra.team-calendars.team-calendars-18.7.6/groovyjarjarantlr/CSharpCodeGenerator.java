/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ActionElement;
import groovyjarjarantlr.ActionTransInfo;
import groovyjarjarantlr.Alternative;
import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.AlternativeElement;
import groovyjarjarantlr.BlockEndElement;
import groovyjarjarantlr.CSharpBlockFinishingInfo;
import groovyjarjarantlr.CSharpCharFormatter;
import groovyjarjarantlr.CSharpNameSpace;
import groovyjarjarantlr.CharLiteralElement;
import groovyjarjarantlr.CharRangeElement;
import groovyjarjarantlr.CharStreamException;
import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.ExceptionHandler;
import groovyjarjarantlr.ExceptionSpec;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.GrammarSymbol;
import groovyjarjarantlr.LexerGrammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.MakeGrammar;
import groovyjarjarantlr.OneOrMoreBlock;
import groovyjarjarantlr.ParserGrammar;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.RuleBlock;
import groovyjarjarantlr.RuleRefElement;
import groovyjarjarantlr.RuleSymbol;
import groovyjarjarantlr.StringLiteralElement;
import groovyjarjarantlr.StringLiteralSymbol;
import groovyjarjarantlr.StringUtils;
import groovyjarjarantlr.SynPredBlock;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenManager;
import groovyjarjarantlr.TokenRangeElement;
import groovyjarjarantlr.TokenRefElement;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.TokenSymbol;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.TreeElement;
import groovyjarjarantlr.TreeWalkerGrammar;
import groovyjarjarantlr.WildcardElement;
import groovyjarjarantlr.ZeroOrMoreBlock;
import groovyjarjarantlr.actions.csharp.ActionLexer;
import groovyjarjarantlr.collections.impl.BitSet;
import groovyjarjarantlr.collections.impl.Vector;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class CSharpCodeGenerator
extends CodeGenerator {
    protected int syntacticPredLevel = 0;
    protected boolean genAST = false;
    protected boolean saveText = false;
    boolean usingCustomAST = false;
    String labeledElementType;
    String labeledElementASTType;
    String labeledElementInit;
    String commonExtraArgs;
    String commonExtraParams;
    String commonLocalVars;
    String lt1Value;
    String exceptionThrown;
    String throwNoViable;
    RuleBlock currentRule;
    String currentASTResult;
    Hashtable treeVariableMap = new Hashtable();
    Hashtable declaredASTVariables = new Hashtable();
    int astVarNumber = 1;
    protected static final String NONUNIQUE = new String();
    public static final int caseSizeThreshold = 127;
    private Vector semPreds;
    private java.util.Vector astTypes;
    private static CSharpNameSpace nameSpace = null;
    int saveIndexCreateLevel;
    int blockNestingLevel;

    public CSharpCodeGenerator() {
        this.charFormatter = new CSharpCharFormatter();
    }

    protected int addSemPred(String string) {
        this.semPreds.appendElement(string);
        return this.semPreds.size() - 1;
    }

    public void exitIfError() {
        if (this.antlrTool.hasError()) {
            this.antlrTool.fatalError("Exiting due to errors.");
        }
    }

    public void gen() {
        try {
            Object object;
            Enumeration enumeration = this.behavior.grammars.elements();
            while (enumeration.hasMoreElements()) {
                object = (Grammar)enumeration.nextElement();
                ((Grammar)object).setGrammarAnalyzer(this.analyzer);
                ((Grammar)object).setCodeGenerator(this);
                this.analyzer.setGrammar((Grammar)object);
                this.setupGrammarParameters((Grammar)object);
                ((Grammar)object).generate();
                this.exitIfError();
            }
            object = this.behavior.tokenManagers.elements();
            while (object.hasMoreElements()) {
                TokenManager tokenManager = (TokenManager)object.nextElement();
                if (!tokenManager.isReadOnly()) {
                    this.genTokenTypes(tokenManager);
                    this.genTokenInterchange(tokenManager);
                }
                this.exitIfError();
            }
        }
        catch (IOException iOException) {
            this.antlrTool.reportException(iOException, null);
        }
    }

    public void gen(ActionElement actionElement) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genAction(" + actionElement + ")");
        }
        if (actionElement.isSemPred) {
            this.genSemPred(actionElement.actionText, actionElement.line);
        } else {
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (0==inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            ActionTransInfo actionTransInfo = new ActionTransInfo();
            String string = this.processActionForSpecialSymbols(actionElement.actionText, actionElement.getLine(), this.currentRule, actionTransInfo);
            if (actionTransInfo.refRuleRoot != null) {
                this.println(actionTransInfo.refRuleRoot + " = (" + this.labeledElementASTType + ")currentAST.root;");
            }
            this.printAction(string);
            if (actionTransInfo.assignToRoot) {
                this.println("currentAST.root = " + actionTransInfo.refRuleRoot + ";");
                this.println("if ( (null != " + actionTransInfo.refRuleRoot + ") && (null != " + actionTransInfo.refRuleRoot + ".getFirstChild()) )");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot + ".getFirstChild();");
                --this.tabs;
                this.println("else");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot + ";");
                --this.tabs;
                this.println("currentAST.advanceChildToEnd();");
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("}");
            }
        }
    }

    public void gen(AlternativeBlock alternativeBlock) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen(" + alternativeBlock + ")");
        }
        this.println("{");
        ++this.tabs;
        this.genBlockPreamble(alternativeBlock);
        this.genBlockInitAction(alternativeBlock);
        String string = this.currentASTResult;
        if (alternativeBlock.getLabel() != null) {
            this.currentASTResult = alternativeBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(alternativeBlock);
        CSharpBlockFinishingInfo cSharpBlockFinishingInfo = this.genCommonBlock(alternativeBlock, true);
        this.genBlockFinish(cSharpBlockFinishingInfo, this.throwNoViable);
        --this.tabs;
        this.println("}");
        this.currentASTResult = string;
    }

    public void gen(BlockEndElement blockEndElement) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genRuleEnd(" + blockEndElement + ")");
        }
    }

    public void gen(CharLiteralElement charLiteralElement) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genChar(" + charLiteralElement + ")");
        }
        if (charLiteralElement.getLabel() != null) {
            this.println(charLiteralElement.getLabel() + " = " + this.lt1Value + ";");
        }
        boolean bl = this.saveText;
        this.saveText = this.saveText && charLiteralElement.getAutoGenType() == 1;
        this.genMatch(charLiteralElement);
        this.saveText = bl;
    }

    public void gen(CharRangeElement charRangeElement) {
        boolean bl;
        if (charRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(charRangeElement.getLabel() + " = " + this.lt1Value + ";");
        }
        boolean bl2 = bl = this.grammar instanceof LexerGrammar && (!this.saveText || charRangeElement.getAutoGenType() == 3);
        if (bl) {
            this.println("_saveIndex = text.Length;");
        }
        this.println("matchRange(" + CSharpCodeGenerator.OctalToUnicode(charRangeElement.beginText) + "," + CSharpCodeGenerator.OctalToUnicode(charRangeElement.endText) + ");");
        if (bl) {
            this.println("text.Length = _saveIndex;");
        }
    }

    public void gen(LexerGrammar lexerGrammar) throws IOException {
        if (lexerGrammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        this.setGrammar(lexerGrammar);
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.panic("Internal error generating lexer");
        }
        this.genBody(lexerGrammar);
    }

    public void gen(OneOrMoreBlock oneOrMoreBlock) {
        Object object;
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen+(" + oneOrMoreBlock + ")");
        }
        this.println("{ // ( ... )+");
        ++this.tabs;
        ++this.blockNestingLevel;
        this.genBlockPreamble(oneOrMoreBlock);
        String string = oneOrMoreBlock.getLabel() != null ? "_cnt_" + oneOrMoreBlock.getLabel() : "_cnt" + oneOrMoreBlock.ID;
        this.println("int " + string + "=0;");
        String string2 = oneOrMoreBlock.getLabel() != null ? oneOrMoreBlock.getLabel() : "_loop" + oneOrMoreBlock.ID;
        this.println("for (;;)");
        this.println("{");
        ++this.tabs;
        ++this.blockNestingLevel;
        this.genBlockInitAction(oneOrMoreBlock);
        String string3 = this.currentASTResult;
        if (oneOrMoreBlock.getLabel() != null) {
            this.currentASTResult = oneOrMoreBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(oneOrMoreBlock);
        boolean bl2 = false;
        int n = this.grammar.maxk;
        if (!oneOrMoreBlock.greedy && oneOrMoreBlock.exitLookaheadDepth <= this.grammar.maxk && oneOrMoreBlock.exitCache[oneOrMoreBlock.exitLookaheadDepth].containsEpsilon()) {
            bl2 = true;
            n = oneOrMoreBlock.exitLookaheadDepth;
        } else if (!oneOrMoreBlock.greedy && oneOrMoreBlock.exitLookaheadDepth == Integer.MAX_VALUE) {
            bl2 = true;
        }
        if (bl2) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("nongreedy (...)+ loop; exit depth is " + oneOrMoreBlock.exitLookaheadDepth);
            }
            object = this.getLookaheadTestExpression(oneOrMoreBlock.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if ((" + string + " >= 1) && " + (String)object + ") goto " + string2 + "_breakloop;");
        }
        object = this.genCommonBlock(oneOrMoreBlock, false);
        this.genBlockFinish((CSharpBlockFinishingInfo)object, "if (" + string + " >= 1) { goto " + string2 + "_breakloop; } else { " + this.throwNoViable + "; }");
        this.println(string + "++;");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}");
        this._print(string2 + "_breakloop:");
        this.println(";");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}    // ( ... )+");
        this.currentASTResult = string3;
    }

    public void gen(ParserGrammar parserGrammar) throws IOException {
        if (parserGrammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        this.setGrammar(parserGrammar);
        if (!(this.grammar instanceof ParserGrammar)) {
            this.antlrTool.panic("Internal error generating parser");
        }
        this.genBody(parserGrammar);
    }

    public void gen(RuleRefElement ruleRefElement) {
        RuleSymbol ruleSymbol;
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genRR(" + ruleRefElement + ")");
        }
        if ((ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule)) == null || !ruleSymbol.isDefined()) {
            this.antlrTool.error("Rule '" + ruleRefElement.targetRule + "' is not defined", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            return;
        }
        if (!(ruleSymbol instanceof RuleSymbol)) {
            this.antlrTool.error("'" + ruleRefElement.targetRule + "' does not name a grammar rule", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            return;
        }
        this.genErrorTryForElement(ruleRefElement);
        if (this.grammar instanceof TreeWalkerGrammar && ruleRefElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(ruleRefElement.getLabel() + " = _t==ASTNULL ? null : " + this.lt1Value + ";");
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || ruleRefElement.getAutoGenType() == 3)) {
            this.declareSaveIndexVariableIfNeeded();
            this.println("_saveIndex = text.Length;");
        }
        this.printTabs();
        if (ruleRefElement.idAssign != null) {
            if (ruleSymbol.block.returnAction == null) {
                this.antlrTool.warning("Rule '" + ruleRefElement.targetRule + "' has no return type", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            }
            this._print(ruleRefElement.idAssign + "=");
        } else if (!(this.grammar instanceof LexerGrammar) && this.syntacticPredLevel == 0 && ruleSymbol.block.returnAction != null) {
            this.antlrTool.warning("Rule '" + ruleRefElement.targetRule + "' returns a value", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
        }
        this.GenRuleInvocation(ruleRefElement);
        if (this.grammar instanceof LexerGrammar && (!this.saveText || ruleRefElement.getAutoGenType() == 3)) {
            this.declareSaveIndexVariableIfNeeded();
            this.println("text.Length = _saveIndex;");
        }
        if (this.syntacticPredLevel == 0) {
            boolean bl;
            boolean bl2 = bl = this.grammar.hasSyntacticPredicate && (this.grammar.buildAST && ruleRefElement.getLabel() != null || this.genAST && ruleRefElement.getAutoGenType() == 1);
            if (bl) {
                this.println("if (0 == inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            if (this.grammar.buildAST && ruleRefElement.getLabel() != null) {
                this.println(ruleRefElement.getLabel() + "_AST = (" + this.labeledElementASTType + ")returnAST;");
            }
            if (this.genAST) {
                switch (ruleRefElement.getAutoGenType()) {
                    case 1: {
                        if (this.usingCustomAST) {
                            this.println("astFactory.addASTChild(ref currentAST, (AST)returnAST);");
                            break;
                        }
                        this.println("astFactory.addASTChild(ref currentAST, returnAST);");
                        break;
                    }
                    case 2: {
                        this.antlrTool.error("Internal: encountered ^ after rule reference");
                        break;
                    }
                }
            }
            if (this.grammar instanceof LexerGrammar && ruleRefElement.getLabel() != null) {
                this.println(ruleRefElement.getLabel() + " = returnToken_;");
            }
            if (bl) {
                --this.tabs;
                this.println("}");
            }
        }
        this.genErrorCatchForElement(ruleRefElement);
    }

    public void gen(StringLiteralElement stringLiteralElement) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genString(" + stringLiteralElement + ")");
        }
        if (stringLiteralElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(stringLiteralElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(stringLiteralElement);
        boolean bl = this.saveText;
        this.saveText = this.saveText && stringLiteralElement.getAutoGenType() == 1;
        this.genMatch(stringLiteralElement);
        this.saveText = bl;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();");
        }
    }

    public void gen(TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(tokenRangeElement);
        this.println("matchRange(" + CSharpCodeGenerator.OctalToUnicode(tokenRangeElement.beginText) + "," + CSharpCodeGenerator.OctalToUnicode(tokenRangeElement.endText) + ");");
        this.genErrorCatchForElement(tokenRangeElement);
    }

    public void gen(TokenRefElement tokenRefElement) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genTokenRef(" + tokenRefElement + ")");
        }
        if (this.grammar instanceof LexerGrammar) {
            this.antlrTool.panic("Token reference found in lexer");
        }
        this.genErrorTryForElement(tokenRefElement);
        if (tokenRefElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRefElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(tokenRefElement);
        this.genMatch(tokenRefElement);
        this.genErrorCatchForElement(tokenRefElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();");
        }
    }

    public void gen(TreeElement treeElement) {
        this.println("AST __t" + treeElement.ID + " = _t;");
        if (treeElement.root.getLabel() != null) {
            this.println(treeElement.root.getLabel() + " = (ASTNULL == _t) ? null : (" + this.labeledElementASTType + ")_t;");
        }
        if (treeElement.root.getAutoGenType() == 3) {
            this.antlrTool.error("Suffixing a root node with '!' is not implemented", this.grammar.getFilename(), treeElement.getLine(), treeElement.getColumn());
            treeElement.root.setAutoGenType(1);
        }
        if (treeElement.root.getAutoGenType() == 2) {
            this.antlrTool.warning("Suffixing a root node with '^' is redundant; already a root", this.grammar.getFilename(), treeElement.getLine(), treeElement.getColumn());
            treeElement.root.setAutoGenType(1);
        }
        this.genElementAST(treeElement.root);
        if (this.grammar.buildAST) {
            this.println("ASTPair __currentAST" + treeElement.ID + " = currentAST.copy();");
            this.println("currentAST.root = currentAST.child;");
            this.println("currentAST.child = null;");
        }
        if (treeElement.root instanceof WildcardElement) {
            this.println("if (null == _t) throw new MismatchedTokenException();");
        } else {
            this.genMatch(treeElement.root);
        }
        this.println("_t = _t.getFirstChild();");
        for (int i = 0; i < treeElement.getAlternatives().size(); ++i) {
            Alternative alternative = treeElement.getAlternativeAt(i);
            AlternativeElement alternativeElement = alternative.head;
            while (alternativeElement != null) {
                alternativeElement.generate();
                alternativeElement = alternativeElement.next;
            }
        }
        if (this.grammar.buildAST) {
            this.println("currentAST = __currentAST" + treeElement.ID + ";");
        }
        this.println("_t = __t" + treeElement.ID + ";");
        this.println("_t = _t.getNextSibling();");
    }

    public void gen(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.setGrammar(treeWalkerGrammar);
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.antlrTool.panic("Internal error generating tree-walker");
        }
        this.genBody(treeWalkerGrammar);
    }

    public void gen(WildcardElement wildcardElement) {
        if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(wildcardElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(wildcardElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if (null == _t) throw new MismatchedTokenException();");
        } else if (this.grammar instanceof LexerGrammar) {
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.declareSaveIndexVariableIfNeeded();
                this.println("_saveIndex = text.Length;");
            }
            this.println("matchNot(EOF/*_CHAR*/);");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.declareSaveIndexVariableIfNeeded();
                this.println("text.Length = _saveIndex;");
            }
        } else {
            this.println("matchNot(" + this.getValueString(1) + ");");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();");
        }
    }

    public void gen(ZeroOrMoreBlock zeroOrMoreBlock) {
        Object object;
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen*(" + zeroOrMoreBlock + ")");
        }
        this.println("{    // ( ... )*");
        ++this.tabs;
        ++this.blockNestingLevel;
        this.genBlockPreamble(zeroOrMoreBlock);
        String string = zeroOrMoreBlock.getLabel() != null ? zeroOrMoreBlock.getLabel() : "_loop" + zeroOrMoreBlock.ID;
        this.println("for (;;)");
        this.println("{");
        ++this.tabs;
        ++this.blockNestingLevel;
        this.genBlockInitAction(zeroOrMoreBlock);
        String string2 = this.currentASTResult;
        if (zeroOrMoreBlock.getLabel() != null) {
            this.currentASTResult = zeroOrMoreBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(zeroOrMoreBlock);
        boolean bl2 = false;
        int n = this.grammar.maxk;
        if (!zeroOrMoreBlock.greedy && zeroOrMoreBlock.exitLookaheadDepth <= this.grammar.maxk && zeroOrMoreBlock.exitCache[zeroOrMoreBlock.exitLookaheadDepth].containsEpsilon()) {
            bl2 = true;
            n = zeroOrMoreBlock.exitLookaheadDepth;
        } else if (!zeroOrMoreBlock.greedy && zeroOrMoreBlock.exitLookaheadDepth == Integer.MAX_VALUE) {
            bl2 = true;
        }
        if (bl2) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("nongreedy (...)* loop; exit depth is " + zeroOrMoreBlock.exitLookaheadDepth);
            }
            object = this.getLookaheadTestExpression(zeroOrMoreBlock.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if (" + (String)object + ") goto " + string + "_breakloop;");
        }
        object = this.genCommonBlock(zeroOrMoreBlock, false);
        this.genBlockFinish((CSharpBlockFinishingInfo)object, "goto " + string + "_breakloop;");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}");
        this._print(string + "_breakloop:");
        this.println(";");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}    // ( ... )*");
        this.currentASTResult = string2;
    }

    protected void genAlt(Alternative alternative, AlternativeBlock alternativeBlock) {
        boolean bl = this.genAST;
        this.genAST = this.genAST && alternative.getAutoGen();
        boolean bl2 = this.saveText;
        this.saveText = this.saveText && alternative.getAutoGen();
        Hashtable hashtable = this.treeVariableMap;
        this.treeVariableMap = new Hashtable();
        if (alternative.exceptionSpec != null) {
            this.println("try        // for error handling");
            this.println("{");
            ++this.tabs;
        }
        AlternativeElement alternativeElement = alternative.head;
        while (!(alternativeElement instanceof BlockEndElement)) {
            alternativeElement.generate();
            alternativeElement = alternativeElement.next;
        }
        if (this.genAST) {
            if (alternativeBlock instanceof RuleBlock) {
                RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
                if (this.usingCustomAST) {
                    this.println(ruleBlock.getRuleName() + "_AST = (" + this.labeledElementASTType + ")currentAST.root;");
                } else {
                    this.println(ruleBlock.getRuleName() + "_AST = currentAST.root;");
                }
            } else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules not yet supported", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
            }
        }
        if (alternative.exceptionSpec != null) {
            --this.tabs;
            this.println("}");
            this.genErrorHandler(alternative.exceptionSpec);
        }
        this.genAST = bl;
        this.saveText = bl2;
        this.treeVariableMap = hashtable;
    }

    protected void genBitsets(Vector vector, int n) {
        this.println("");
        for (int i = 0; i < vector.size(); ++i) {
            BitSet bitSet = (BitSet)vector.elementAt(i);
            bitSet.growToInclude(n);
            this.genBitSet(bitSet, i);
        }
    }

    private void genBitSet(BitSet bitSet, int n) {
        this.println("private static long[] mk_" + this.getBitsetName(n) + "()");
        this.println("{");
        ++this.tabs;
        int n2 = bitSet.lengthInLongWords();
        if (n2 < 8) {
            this.println("long[] data = { " + bitSet.toStringOfWords() + "};");
        } else {
            this.println("long[] data = new long[" + n2 + "];");
            long[] lArray = bitSet.toPackedArray();
            int n3 = 0;
            while (n3 < lArray.length) {
                int n4;
                if (n3 + 1 == lArray.length || lArray[n3] != lArray[n3 + 1]) {
                    this.println("data[" + n3 + "]=" + lArray[n3] + "L;");
                    ++n3;
                    continue;
                }
                for (n4 = n3 + 1; n4 < lArray.length && lArray[n4] == lArray[n3]; ++n4) {
                }
                this.println("for (int i = " + n3 + "; i<=" + (n4 - 1) + "; i++) { data[i]=" + lArray[n3] + "L; }");
                n3 = n4;
            }
        }
        this.println("return data;");
        --this.tabs;
        this.println("}");
        this.println("public static readonly BitSet " + this.getBitsetName(n) + " = new BitSet(" + "mk_" + this.getBitsetName(n) + "()" + ");");
    }

    protected String getBitsetName(int n) {
        return "tokenSet_" + n + "_";
    }

    private void genBlockFinish(CSharpBlockFinishingInfo cSharpBlockFinishingInfo, String string) {
        if (cSharpBlockFinishingInfo.needAnErrorClause && (cSharpBlockFinishingInfo.generatedAnIf || cSharpBlockFinishingInfo.generatedSwitch)) {
            if (cSharpBlockFinishingInfo.generatedAnIf) {
                this.println("else");
                this.println("{");
            } else {
                this.println("{");
            }
            ++this.tabs;
            this.println(string);
            --this.tabs;
            this.println("}");
        }
        if (cSharpBlockFinishingInfo.postscript != null) {
            if (cSharpBlockFinishingInfo.needAnErrorClause && cSharpBlockFinishingInfo.generatedSwitch && !cSharpBlockFinishingInfo.generatedAnIf && string != null) {
                if (string.indexOf("throw") == 0 || string.indexOf("goto") == 0) {
                    int n = cSharpBlockFinishingInfo.postscript.indexOf("break;") + 6;
                    String string2 = cSharpBlockFinishingInfo.postscript.substring(n);
                    this.println(string2);
                } else {
                    this.println(cSharpBlockFinishingInfo.postscript);
                }
            } else {
                this.println(cSharpBlockFinishingInfo.postscript);
            }
        }
    }

    protected void genBlockInitAction(AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.printAction(this.processActionForSpecialSymbols(alternativeBlock.initAction, alternativeBlock.getLine(), this.currentRule, null));
        }
    }

    protected void genBlockPreamble(AlternativeBlock alternativeBlock) {
        if (alternativeBlock instanceof RuleBlock) {
            RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
            if (ruleBlock.labeledElements != null) {
                for (int i = 0; i < ruleBlock.labeledElements.size(); ++i) {
                    AlternativeElement alternativeElement = (AlternativeElement)ruleBlock.labeledElements.elementAt(i);
                    if (alternativeElement instanceof RuleRefElement || alternativeElement instanceof AlternativeBlock && !(alternativeElement instanceof RuleBlock) && !(alternativeElement instanceof SynPredBlock)) {
                        if (!(alternativeElement instanceof RuleRefElement) && ((AlternativeBlock)alternativeElement).not && this.analyzer.subruleCanBeInverted((AlternativeBlock)alternativeElement, this.grammar instanceof LexerGrammar)) {
                            this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                            if (!this.grammar.buildAST) continue;
                            this.genASTDeclaration(alternativeElement);
                            continue;
                        }
                        if (this.grammar.buildAST) {
                            this.genASTDeclaration(alternativeElement);
                        }
                        if (this.grammar instanceof LexerGrammar) {
                            this.println("IToken " + alternativeElement.getLabel() + " = null;");
                        }
                        if (!(this.grammar instanceof TreeWalkerGrammar)) continue;
                        this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                        continue;
                    }
                    this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                    if (!this.grammar.buildAST) continue;
                    if (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
                        GrammarAtom grammarAtom = (GrammarAtom)alternativeElement;
                        this.genASTDeclaration(alternativeElement, grammarAtom.getASTNodeType());
                        continue;
                    }
                    this.genASTDeclaration(alternativeElement);
                }
            }
        }
    }

    public void genBody(LexerGrammar lexerGrammar) throws IOException {
        GrammarSymbol grammarSymbol;
        Object object;
        Object object2;
        Object object3;
        Token token;
        this.setupOutput(this.grammar.getClassName());
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("// Generate header specific to lexer CSharp file");
        this.println("using System;");
        this.println("using Stream                          = System.IO.Stream;");
        this.println("using TextReader                      = System.IO.TextReader;");
        this.println("using Hashtable                       = System.Collections.Hashtable;");
        this.println("using Comparer                        = System.Collections.Comparer;");
        if (!lexerGrammar.caseSensitiveLiterals) {
            this.println("using CaseInsensitiveHashCodeProvider = System.Collections.CaseInsensitiveHashCodeProvider;");
            this.println("using CaseInsensitiveComparer         = System.Collections.CaseInsensitiveComparer;");
        }
        this.println("");
        this.println("using TokenStreamException            = antlr.TokenStreamException;");
        this.println("using TokenStreamIOException          = antlr.TokenStreamIOException;");
        this.println("using TokenStreamRecognitionException = antlr.TokenStreamRecognitionException;");
        this.println("using CharStreamException             = antlr.CharStreamException;");
        this.println("using CharStreamIOException           = antlr.CharStreamIOException;");
        this.println("using ANTLRException                  = antlr.ANTLRException;");
        this.println("using CharScanner                     = antlr.CharScanner;");
        this.println("using InputBuffer                     = antlr.InputBuffer;");
        this.println("using ByteBuffer                      = antlr.ByteBuffer;");
        this.println("using CharBuffer                      = antlr.CharBuffer;");
        this.println("using Token                           = antlr.Token;");
        this.println("using IToken                          = antlr.IToken;");
        this.println("using CommonToken                     = antlr.CommonToken;");
        this.println("using SemanticException               = antlr.SemanticException;");
        this.println("using RecognitionException            = antlr.RecognitionException;");
        this.println("using NoViableAltForCharException     = antlr.NoViableAltForCharException;");
        this.println("using MismatchedCharException         = antlr.MismatchedCharException;");
        this.println("using TokenStream                     = antlr.TokenStream;");
        this.println("using LexerSharedInputState           = antlr.LexerSharedInputState;");
        this.println("using BitSet                          = antlr.collections.impl.BitSet;");
        this.println(this.grammar.preambleAction.getText());
        String string = null;
        string = this.grammar.superClass != null ? this.grammar.superClass : "groovyjarjarantlr." + this.grammar.getSuperClass();
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        if ((token = (Token)this.grammar.options.get("classHeaderPrefix")) == null) {
            this.print("public ");
        } else {
            object3 = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (object3 == null) {
                this.print("public ");
            } else {
                this.print((String)object3 + " ");
            }
        }
        this.print("class " + this.grammar.getClassName() + " : " + string);
        this.println(", TokenStream");
        object3 = (Token)this.grammar.options.get("classHeaderSuffix");
        if (object3 != null && (object2 = StringUtils.stripFrontBack(((Token)object3).getText(), "\"", "\"")) != null) {
            this.print(", " + (String)object2);
        }
        this.println(" {");
        ++this.tabs;
        this.genTokenDefinitions(this.grammar.tokenManager);
        this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("public " + this.grammar.getClassName() + "(Stream ins) : this(new ByteBuffer(ins))");
        this.println("{");
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(TextReader r) : this(new CharBuffer(r))");
        this.println("{");
        this.println("}");
        this.println("");
        this.print("public " + this.grammar.getClassName() + "(InputBuffer ib)");
        if (this.grammar.debuggingOutput) {
            this.println(" : this(new LexerSharedInputState(new antlr.debug.DebuggingInputBuffer(ib)))");
        } else {
            this.println(" : this(new LexerSharedInputState(ib))");
        }
        this.println("{");
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(LexerSharedInputState state) : base(state)");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("private void initialize()");
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("ruleNames  = _ruleNames;");
            this.println("semPredNames = _semPredNames;");
            this.println("setupDebugging();");
        }
        this.println("caseSensitiveLiterals = " + lexerGrammar.caseSensitiveLiterals + ";");
        this.println("setCaseSensitive(" + lexerGrammar.caseSensitive + ");");
        if (lexerGrammar.caseSensitiveLiterals) {
            this.println("literals = new Hashtable(100, (float) 0.4, null, Comparer.Default);");
        } else {
            this.println("literals = new Hashtable(100, (float) 0.4, CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);");
        }
        object2 = this.grammar.tokenManager.getTokenSymbolKeys();
        while (object2.hasMoreElements()) {
            TokenSymbol tokenSymbol;
            object = (String)object2.nextElement();
            if (((String)object).charAt(0) != '\"' || !((tokenSymbol = this.grammar.tokenManager.getTokenSymbol((String)object)) instanceof StringLiteralSymbol)) continue;
            grammarSymbol = (StringLiteralSymbol)tokenSymbol;
            this.println("literals.Add(" + grammarSymbol.getId() + ", " + ((TokenSymbol)grammarSymbol).getTokenType() + ");");
        }
        --this.tabs;
        this.println("}");
        if (this.grammar.debuggingOutput) {
            this.println("private static readonly string[] _ruleNames = new string[] {");
            object = this.grammar.rules.elements();
            boolean bl = false;
            while (object.hasMoreElements()) {
                grammarSymbol = (GrammarSymbol)object.nextElement();
                if (!(grammarSymbol instanceof RuleSymbol)) continue;
                this.println("  \"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
            }
            this.println("};");
        }
        this.genNextToken();
        object = this.grammar.rules.elements();
        int n = 0;
        while (object.hasMoreElements()) {
            grammarSymbol = (RuleSymbol)object.nextElement();
            if (!grammarSymbol.getId().equals("mnextToken")) {
                this.genRule((RuleSymbol)grammarSymbol, false, n++, this.grammar.tokenManager);
            }
            this.exitIfError();
        }
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
        this.println("");
        --this.tabs;
        this.println("}");
        --this.tabs;
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void genInitFactory(Grammar grammar) {
        if (grammar.buildAST) {
            this.println("static public void initializeASTFactory( ASTFactory factory )");
            this.println("{");
            ++this.tabs;
            this.println("factory.setMaxNodeType(" + grammar.tokenManager.maxTokenType() + ");");
            Vector vector = grammar.tokenManager.getVocabulary();
            for (int i = 0; i < vector.size(); ++i) {
                TokenSymbol tokenSymbol;
                String string = (String)vector.elementAt(i);
                if (string == null || (tokenSymbol = grammar.tokenManager.getTokenSymbol(string)) == null || tokenSymbol.getASTNodeType() == null) continue;
                this.println("factory.setTokenTypeASTNodeType(" + string + ", \"" + tokenSymbol.getASTNodeType() + "\");");
            }
            --this.tabs;
            this.println("}");
        }
    }

    public void genBody(ParserGrammar parserGrammar) throws IOException {
        GrammarSymbol grammarSymbol;
        int n;
        Object object;
        Object object2;
        Token token;
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("// Generate the header common to all output files.");
        this.println("using System;");
        this.println("");
        this.println("using TokenBuffer              = antlr.TokenBuffer;");
        this.println("using TokenStreamException     = antlr.TokenStreamException;");
        this.println("using TokenStreamIOException   = antlr.TokenStreamIOException;");
        this.println("using ANTLRException           = antlr.ANTLRException;");
        String string = this.grammar.getSuperClass();
        String[] stringArray = this.split(string, ".");
        this.println("using " + stringArray[stringArray.length - 1] + " = antlr." + string + ";");
        this.println("using Token                    = antlr.Token;");
        this.println("using IToken                   = antlr.IToken;");
        this.println("using TokenStream              = antlr.TokenStream;");
        this.println("using RecognitionException     = antlr.RecognitionException;");
        this.println("using NoViableAltException     = antlr.NoViableAltException;");
        this.println("using MismatchedTokenException = antlr.MismatchedTokenException;");
        this.println("using SemanticException        = antlr.SemanticException;");
        this.println("using ParserSharedInputState   = antlr.ParserSharedInputState;");
        this.println("using BitSet                   = antlr.collections.impl.BitSet;");
        if (this.genAST) {
            this.println("using AST                      = antlr.collections.AST;");
            this.println("using ASTPair                  = antlr.ASTPair;");
            this.println("using ASTFactory               = antlr.ASTFactory;");
            this.println("using ASTArray                 = antlr.collections.impl.ASTArray;");
        }
        this.println(this.grammar.preambleAction.getText());
        String string2 = null;
        string2 = this.grammar.superClass != null ? this.grammar.superClass : "groovyjarjarantlr." + this.grammar.getSuperClass();
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        if ((token = (Token)this.grammar.options.get("classHeaderPrefix")) == null) {
            this.print("public ");
        } else {
            object2 = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (object2 == null) {
                this.print("public ");
            } else {
                this.print((String)object2 + " ");
            }
        }
        this.println("class " + this.grammar.getClassName() + " : " + string2);
        object2 = (Token)this.grammar.options.get("classHeaderSuffix");
        if (object2 != null && (object = StringUtils.stripFrontBack(((Token)object2).getText(), "\"", "\"")) != null) {
            this.print("              , " + (String)object);
        }
        this.println("{");
        ++this.tabs;
        this.genTokenDefinitions(this.grammar.tokenManager);
        if (this.grammar.debuggingOutput) {
            this.println("private static readonly string[] _ruleNames = new string[] {");
            ++this.tabs;
            object = this.grammar.rules.elements();
            n = 0;
            while (object.hasMoreElements()) {
                grammarSymbol = (GrammarSymbol)object.nextElement();
                if (!(grammarSymbol instanceof RuleSymbol)) continue;
                this.println("  \"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
            }
            --this.tabs;
            this.println("};");
        }
        this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("");
        this.println("protected void initialize()");
        this.println("{");
        ++this.tabs;
        this.println("tokenNames = tokenNames_;");
        if (this.grammar.buildAST) {
            this.println("initializeFactory();");
        }
        if (this.grammar.debuggingOutput) {
            this.println("ruleNames  = _ruleNames;");
            this.println("semPredNames = _semPredNames;");
            this.println("setupDebugging(tokenBuf);");
        }
        --this.tabs;
        this.println("}");
        this.println("");
        this.println("");
        this.println("protected " + this.grammar.getClassName() + "(TokenBuffer tokenBuf, int k) : base(tokenBuf, k)");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(TokenBuffer tokenBuf) : this(tokenBuf," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.println("protected " + this.grammar.getClassName() + "(TokenStream lexer, int k) : base(lexer,k)");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(TokenStream lexer) : this(lexer," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(ParserSharedInputState state) : base(state," + this.grammar.maxk + ")");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.astTypes = new java.util.Vector(100);
        object = this.grammar.rules.elements();
        n = 0;
        while (object.hasMoreElements()) {
            grammarSymbol = (GrammarSymbol)object.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n++, this.grammar.tokenManager);
            }
            this.exitIfError();
        }
        if (this.usingCustomAST) {
            this.println("public new " + this.labeledElementASTType + " getAST()");
            this.println("{");
            ++this.tabs;
            this.println("return (" + this.labeledElementASTType + ") returnAST;");
            --this.tabs;
            this.println("}");
            this.println("");
        }
        this.println("private void initializeFactory()");
        this.println("{");
        ++this.tabs;
        if (this.grammar.buildAST) {
            this.println("if (astFactory == null)");
            this.println("{");
            ++this.tabs;
            if (this.usingCustomAST) {
                this.println("astFactory = new ASTFactory(\"" + this.labeledElementASTType + "\");");
            } else {
                this.println("astFactory = new ASTFactory();");
            }
            --this.tabs;
            this.println("}");
            this.println("initializeASTFactory( astFactory );");
        }
        --this.tabs;
        this.println("}");
        this.genInitFactory(parserGrammar);
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.println("");
        --this.tabs;
        this.println("}");
        --this.tabs;
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void genBody(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        Object object;
        Object object2;
        Token token;
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("// Generate header specific to the tree-parser CSharp file");
        this.println("using System;");
        this.println("");
        this.println("using " + this.grammar.getSuperClass() + " = antlr." + this.grammar.getSuperClass() + ";");
        this.println("using Token                    = antlr.Token;");
        this.println("using IToken                   = antlr.IToken;");
        this.println("using AST                      = antlr.collections.AST;");
        this.println("using RecognitionException     = antlr.RecognitionException;");
        this.println("using ANTLRException           = antlr.ANTLRException;");
        this.println("using NoViableAltException     = antlr.NoViableAltException;");
        this.println("using MismatchedTokenException = antlr.MismatchedTokenException;");
        this.println("using SemanticException        = antlr.SemanticException;");
        this.println("using BitSet                   = antlr.collections.impl.BitSet;");
        this.println("using ASTPair                  = antlr.ASTPair;");
        this.println("using ASTFactory               = antlr.ASTFactory;");
        this.println("using ASTArray                 = antlr.collections.impl.ASTArray;");
        this.println(this.grammar.preambleAction.getText());
        String string = null;
        string = this.grammar.superClass != null ? this.grammar.superClass : "groovyjarjarantlr." + this.grammar.getSuperClass();
        this.println("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        if ((token = (Token)this.grammar.options.get("classHeaderPrefix")) == null) {
            this.print("public ");
        } else {
            object2 = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (object2 == null) {
                this.print("public ");
            } else {
                this.print((String)object2 + " ");
            }
        }
        this.println("class " + this.grammar.getClassName() + " : " + string);
        object2 = (Token)this.grammar.options.get("classHeaderSuffix");
        if (object2 != null && (object = StringUtils.stripFrontBack(((Token)object2).getText(), "\"", "\"")) != null) {
            this.print("              , " + (String)object);
        }
        this.println("{");
        ++this.tabs;
        this.genTokenDefinitions(this.grammar.tokenManager);
        this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("public " + this.grammar.getClassName() + "()");
        this.println("{");
        ++this.tabs;
        this.println("tokenNames = tokenNames_;");
        --this.tabs;
        this.println("}");
        this.println("");
        this.astTypes = new java.util.Vector();
        object = this.grammar.rules.elements();
        int n = 0;
        String string2 = "";
        while (object.hasMoreElements()) {
            GrammarSymbol grammarSymbol = (GrammarSymbol)object.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n++, this.grammar.tokenManager);
            }
            this.exitIfError();
        }
        if (this.usingCustomAST) {
            this.println("public new " + this.labeledElementASTType + " getAST()");
            this.println("{");
            ++this.tabs;
            this.println("return (" + this.labeledElementASTType + ") returnAST;");
            --this.tabs;
            this.println("}");
            this.println("");
        }
        this.genInitFactory(this.grammar);
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        --this.tabs;
        this.println("}");
        this.println("");
        --this.tabs;
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }

    protected void genCases(BitSet bitSet) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genCases(" + bitSet + ")");
        }
        int[] nArray = bitSet.toArray();
        int n = this.grammar instanceof LexerGrammar ? 4 : 1;
        int n2 = 1;
        boolean bl = true;
        for (int i = 0; i < nArray.length; ++i) {
            if (n2 == 1) {
                this.print("");
            } else {
                this._print("  ");
            }
            this._print("case " + this.getValueString(nArray[i]) + ":");
            if (n2 == n) {
                this._println("");
                bl = true;
                n2 = 1;
                continue;
            }
            ++n2;
            bl = false;
        }
        if (!bl) {
            this._println("");
        }
    }

    public CSharpBlockFinishingInfo genCommonBlock(AlternativeBlock alternativeBlock, boolean bl) {
        int n;
        Object object;
        int n2 = 0;
        boolean bl2 = false;
        int n3 = 0;
        CSharpBlockFinishingInfo cSharpBlockFinishingInfo = new CSharpBlockFinishingInfo();
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genCommonBlock(" + alternativeBlock + ")");
        }
        boolean bl3 = this.genAST;
        this.genAST = this.genAST && alternativeBlock.getAutoGen();
        boolean bl4 = this.saveText;
        boolean bl5 = this.saveText = this.saveText && alternativeBlock.getAutoGen();
        if (alternativeBlock.not && this.analyzer.subruleCanBeInverted(alternativeBlock, this.grammar instanceof LexerGrammar)) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("special case: ~(subrule)");
            }
            Lookahead lookahead = this.analyzer.look(1, alternativeBlock);
            if (alternativeBlock.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(alternativeBlock.getLabel() + " = " + this.lt1Value + ";");
            }
            this.genElementAST(alternativeBlock);
            String string = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                string = this.usingCustomAST ? "(AST)_t," : "_t,";
            }
            this.println("match(" + string + this.getBitsetName(this.markBitsetForGen(lookahead.fset)) + ");");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t.getNextSibling();");
            }
            return cSharpBlockFinishingInfo;
        }
        if (alternativeBlock.getAlternatives().size() == 1) {
            Alternative alternative = alternativeBlock.getAlternativeAt(0);
            if (alternative.synPred != null) {
                this.antlrTool.warning("Syntactic predicate superfluous for single alternative", this.grammar.getFilename(), alternativeBlock.getAlternativeAt((int)0).synPred.getLine(), alternativeBlock.getAlternativeAt((int)0).synPred.getColumn());
            }
            if (bl) {
                if (alternative.semPred != null) {
                    this.genSemPred(alternative.semPred, alternativeBlock.line);
                }
                this.genAlt(alternative, alternativeBlock);
                return cSharpBlockFinishingInfo;
            }
        }
        int n4 = 0;
        for (int i = 0; i < alternativeBlock.getAlternatives().size(); ++i) {
            Alternative alternative = alternativeBlock.getAlternativeAt(i);
            if (!CSharpCodeGenerator.suitableForCaseExpression(alternative)) continue;
            ++n4;
        }
        if (n4 >= this.makeSwitchThreshold) {
            String string = this.lookaheadString(1);
            bl2 = true;
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if (null == _t)");
                ++this.tabs;
                this.println("_t = ASTNULL;");
                --this.tabs;
            }
            this.println("switch ( " + string + " )");
            this.println("{");
            ++this.blockNestingLevel;
            for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
                Alternative alternative = alternativeBlock.getAlternativeAt(i);
                if (!CSharpCodeGenerator.suitableForCaseExpression(alternative)) continue;
                object = alternative.cache[1];
                if (((Lookahead)object).fset.degree() == 0 && !((Lookahead)object).containsEpsilon()) {
                    this.antlrTool.warning("Alternate omitted due to empty prediction set", this.grammar.getFilename(), alternative.head.getLine(), alternative.head.getColumn());
                    continue;
                }
                this.genCases(((Lookahead)object).fset);
                this.println("{");
                ++this.tabs;
                ++this.blockNestingLevel;
                this.genAlt(alternative, alternativeBlock);
                this.println("break;");
                if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
                    this.saveIndexCreateLevel = 0;
                }
                --this.tabs;
                this.println("}");
            }
            this.println("default:");
            ++this.tabs;
        }
        for (int i = n = this.grammar instanceof LexerGrammar ? this.grammar.maxk : 0; i >= 0; --i) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("checking depth " + i);
            }
            for (int j = 0; j < alternativeBlock.alternatives.size(); ++j) {
                String string;
                object = alternativeBlock.getAlternativeAt(j);
                if (this.DEBUG_CODE_GENERATOR) {
                    System.out.println("genAlt: " + j);
                }
                if (bl2 && CSharpCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                    if (!this.DEBUG_CODE_GENERATOR) continue;
                    System.out.println("ignoring alt because it was in the switch");
                    continue;
                }
                boolean bl6 = false;
                if (this.grammar instanceof LexerGrammar) {
                    int n5 = ((Alternative)object).lookaheadDepth;
                    if (n5 == Integer.MAX_VALUE) {
                        n5 = this.grammar.maxk;
                    }
                    while (n5 >= 1 && ((Alternative)object).cache[n5].containsEpsilon()) {
                        --n5;
                    }
                    if (n5 != i) {
                        if (!this.DEBUG_CODE_GENERATOR) continue;
                        System.out.println("ignoring alt because effectiveDepth!=altDepth;" + n5 + "!=" + i);
                        continue;
                    }
                    bl6 = this.lookaheadIsEmpty((Alternative)object, n5);
                    string = this.getLookaheadTestExpression((Alternative)object, n5);
                } else {
                    bl6 = this.lookaheadIsEmpty((Alternative)object, this.grammar.maxk);
                    string = this.getLookaheadTestExpression((Alternative)object, this.grammar.maxk);
                }
                if (((Alternative)object).cache[1].fset.degree() > 127 && CSharpCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                    if (n2 == 0) {
                        this.println("if " + string);
                        this.println("{");
                    } else {
                        this.println("else if " + string);
                        this.println("{");
                    }
                } else if (bl6 && ((Alternative)object).semPred == null && ((Alternative)object).synPred == null) {
                    if (n2 == 0) {
                        this.println("{");
                    } else {
                        this.println("else {");
                    }
                    cSharpBlockFinishingInfo.needAnErrorClause = false;
                } else {
                    if (((Alternative)object).semPred != null) {
                        ActionTransInfo actionTransInfo = new ActionTransInfo();
                        String string2 = this.processActionForSpecialSymbols(((Alternative)object).semPred, alternativeBlock.line, this.currentRule, actionTransInfo);
                        string = (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar) && this.grammar.debuggingOutput ? "(" + string + "&& fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEventArgs.PREDICTING," + this.addSemPred(this.charFormatter.escapeString(string2)) + "," + string2 + "))" : "(" + string + "&&(" + string2 + "))";
                    }
                    if (n2 > 0) {
                        if (((Alternative)object).synPred != null) {
                            this.println("else {");
                            ++this.tabs;
                            ++this.blockNestingLevel;
                            this.genSynPred(((Alternative)object).synPred, string);
                            ++n3;
                        } else {
                            this.println("else if " + string + " {");
                        }
                    } else if (((Alternative)object).synPred != null) {
                        this.genSynPred(((Alternative)object).synPred, string);
                    } else {
                        if (this.grammar instanceof TreeWalkerGrammar) {
                            this.println("if (_t == null)");
                            ++this.tabs;
                            this.println("_t = ASTNULL;");
                            --this.tabs;
                        }
                        this.println("if " + string);
                        this.println("{");
                    }
                }
                ++this.blockNestingLevel;
                ++n2;
                ++this.tabs;
                this.genAlt((Alternative)object, alternativeBlock);
                --this.tabs;
                if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
                    this.saveIndexCreateLevel = 0;
                }
                this.println("}");
            }
        }
        String string = "";
        for (int i = 1; i <= n3; ++i) {
            string = string + "}";
            if (this.blockNestingLevel-- != this.saveIndexCreateLevel) continue;
            this.saveIndexCreateLevel = 0;
        }
        this.genAST = bl3;
        this.saveText = bl4;
        if (bl2) {
            --this.tabs;
            cSharpBlockFinishingInfo.postscript = string + "break; }";
            if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
                this.saveIndexCreateLevel = 0;
            }
            cSharpBlockFinishingInfo.generatedSwitch = true;
            cSharpBlockFinishingInfo.generatedAnIf = n2 > 0;
        } else {
            cSharpBlockFinishingInfo.postscript = string;
            cSharpBlockFinishingInfo.generatedSwitch = false;
            cSharpBlockFinishingInfo.generatedAnIf = n2 > 0;
        }
        return cSharpBlockFinishingInfo;
    }

    private static boolean suitableForCaseExpression(Alternative alternative) {
        return alternative.lookaheadDepth == 1 && alternative.semPred == null && !alternative.cache[1].containsEpsilon() && alternative.cache[1].fset.degree() <= 127;
    }

    private void genElementAST(AlternativeElement alternativeElement) {
        if (this.grammar instanceof TreeWalkerGrammar && !this.grammar.buildAST) {
            if (alternativeElement.getLabel() == null) {
                String string = this.lt1Value;
                String string2 = "tmp" + this.astVarNumber + "_AST";
                ++this.astVarNumber;
                this.mapTreeVariable(alternativeElement, string2);
                this.println(this.labeledElementASTType + " " + string2 + "_in = " + string + ";");
            }
            return;
        }
        if (this.grammar.buildAST && this.syntacticPredLevel == 0) {
            Object object;
            String string;
            String string3;
            boolean bl;
            boolean bl2;
            boolean bl3 = bl2 = this.genAST && (alternativeElement.getLabel() != null || alternativeElement.getAutoGenType() != 3);
            if (alternativeElement.getAutoGenType() != 3 && alternativeElement instanceof TokenRefElement) {
                bl2 = true;
            }
            boolean bl4 = bl = this.grammar.hasSyntacticPredicate && bl2;
            if (alternativeElement.getLabel() != null) {
                string3 = alternativeElement.getLabel();
                string = alternativeElement.getLabel();
            } else {
                string3 = this.lt1Value;
                string = "tmp" + this.astVarNumber;
                ++this.astVarNumber;
            }
            if (bl2) {
                if (alternativeElement instanceof GrammarAtom) {
                    object = (GrammarAtom)alternativeElement;
                    if (((GrammarAtom)object).getASTNodeType() != null) {
                        this.genASTDeclaration(alternativeElement, string, ((GrammarAtom)object).getASTNodeType());
                    } else {
                        this.genASTDeclaration(alternativeElement, string, this.labeledElementASTType);
                    }
                } else {
                    this.genASTDeclaration(alternativeElement, string, this.labeledElementASTType);
                }
            }
            object = string + "_AST";
            this.mapTreeVariable(alternativeElement, (String)object);
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println(this.labeledElementASTType + " " + (String)object + "_in = null;");
            }
            if (bl) {
                // empty if block
            }
            if (alternativeElement.getLabel() != null) {
                if (alternativeElement instanceof GrammarAtom) {
                    this.println((String)object + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, string3) + ";");
                } else {
                    this.println((String)object + " = " + this.getASTCreateString(string3) + ";");
                }
            }
            if (alternativeElement.getLabel() == null && bl2) {
                string3 = this.lt1Value;
                if (alternativeElement instanceof GrammarAtom) {
                    this.println((String)object + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, string3) + ";");
                } else {
                    this.println((String)object + " = " + this.getASTCreateString(string3) + ";");
                }
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println((String)object + "_in = " + string3 + ";");
                }
            }
            if (this.genAST) {
                switch (alternativeElement.getAutoGenType()) {
                    case 1: {
                        if (this.usingCustomAST || alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
                            this.println("astFactory.addASTChild(ref currentAST, (AST)" + (String)object + ");");
                            break;
                        }
                        this.println("astFactory.addASTChild(ref currentAST, " + (String)object + ");");
                        break;
                    }
                    case 2: {
                        if (this.usingCustomAST || alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
                            this.println("astFactory.makeASTRoot(ref currentAST, (AST)" + (String)object + ");");
                            break;
                        }
                        this.println("astFactory.makeASTRoot(ref currentAST, " + (String)object + ");");
                        break;
                    }
                }
            }
            if (bl) {
                // empty if block
            }
        }
    }

    private void genErrorCatchForElement(AlternativeElement alternativeElement) {
        ExceptionSpec exceptionSpec;
        RuleSymbol ruleSymbol;
        if (alternativeElement.getLabel() == null) {
            return;
        }
        String string = alternativeElement.enclosingRuleName;
        if (this.grammar instanceof LexerGrammar) {
            string = CodeGenerator.encodeLexerRuleName(alternativeElement.enclosingRuleName);
        }
        if ((ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string)) == null) {
            this.antlrTool.panic("Enclosing rule not found!");
        }
        if ((exceptionSpec = ruleSymbol.block.findExceptionSpec(alternativeElement.getLabel())) != null) {
            --this.tabs;
            this.println("}");
            this.genErrorHandler(exceptionSpec);
        }
    }

    private void genErrorHandler(ExceptionSpec exceptionSpec) {
        for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
            ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
            this.println("catch (" + exceptionHandler.exceptionTypeAndName.getText() + ")");
            this.println("{");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (0 == inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            ActionTransInfo actionTransInfo = new ActionTransInfo();
            this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, actionTransInfo));
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("}");
                this.println("else");
                this.println("{");
                ++this.tabs;
                this.println("throw;");
                --this.tabs;
                this.println("}");
            }
            --this.tabs;
            this.println("}");
        }
    }

    private void genErrorTryForElement(AlternativeElement alternativeElement) {
        ExceptionSpec exceptionSpec;
        RuleSymbol ruleSymbol;
        if (alternativeElement.getLabel() == null) {
            return;
        }
        String string = alternativeElement.enclosingRuleName;
        if (this.grammar instanceof LexerGrammar) {
            string = CodeGenerator.encodeLexerRuleName(alternativeElement.enclosingRuleName);
        }
        if ((ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string)) == null) {
            this.antlrTool.panic("Enclosing rule not found!");
        }
        if ((exceptionSpec = ruleSymbol.block.findExceptionSpec(alternativeElement.getLabel())) != null) {
            this.println("try   // for error handling");
            this.println("{");
            ++this.tabs;
        }
    }

    protected void genASTDeclaration(AlternativeElement alternativeElement) {
        this.genASTDeclaration(alternativeElement, this.labeledElementASTType);
    }

    protected void genASTDeclaration(AlternativeElement alternativeElement, String string) {
        this.genASTDeclaration(alternativeElement, alternativeElement.getLabel(), string);
    }

    protected void genASTDeclaration(AlternativeElement alternativeElement, String string, String string2) {
        if (this.declaredASTVariables.contains(alternativeElement)) {
            return;
        }
        this.println(string2 + " " + string + "_AST = null;");
        this.declaredASTVariables.put(alternativeElement, alternativeElement);
    }

    protected void genHeader() {
        this.println("// $ANTLR " + Tool.version + ": " + "\"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"" + " -> " + "\"" + this.grammar.getClassName() + ".cs\"$");
    }

    private void genLiteralsTest() {
        this.println("_ttype = testLiteralsTable(_ttype);");
    }

    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = testLiteralsTable(text.ToString(_begin, text.Length-_begin), _ttype);");
    }

    protected void genMatch(BitSet bitSet) {
    }

    protected void genMatch(GrammarAtom grammarAtom) {
        if (grammarAtom instanceof StringLiteralElement) {
            if (this.grammar instanceof LexerGrammar) {
                this.genMatchUsingAtomText(grammarAtom);
            } else {
                this.genMatchUsingAtomTokenType(grammarAtom);
            }
        } else if (grammarAtom instanceof CharLiteralElement) {
            if (this.grammar instanceof LexerGrammar) {
                this.genMatchUsingAtomText(grammarAtom);
            } else {
                this.antlrTool.error("cannot ref character literals in grammar: " + grammarAtom);
            }
        } else if (grammarAtom instanceof TokenRefElement) {
            this.genMatchUsingAtomText(grammarAtom);
        } else if (grammarAtom instanceof WildcardElement) {
            this.gen((WildcardElement)grammarAtom);
        }
    }

    protected void genMatchUsingAtomText(GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            string = this.usingCustomAST ? "(AST)_t," : "_t,";
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.declareSaveIndexVariableIfNeeded();
            this.println("_saveIndex = text.Length;");
        }
        this.print(grammarAtom.not ? "matchNot(" : "match(");
        this._print(string);
        if (grammarAtom.atomText.equals("EOF")) {
            this._print("Token.EOF_TYPE");
        } else {
            this._print(grammarAtom.atomText);
        }
        this._println(");");
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.declareSaveIndexVariableIfNeeded();
            this.println("text.Length = _saveIndex;");
        }
    }

    protected void genMatchUsingAtomTokenType(GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            string = this.usingCustomAST ? "(AST)_t," : "_t,";
        }
        Object var3_3 = null;
        String string2 = string + this.getValueString(grammarAtom.getType());
        this.println((grammarAtom.not ? "matchNot(" : "match(") + string2 + ");");
    }

    public void genNextToken() {
        Object object;
        Object object2;
        RuleSymbol ruleSymbol;
        boolean bl = false;
        for (int i = 0; i < this.grammar.rules.size(); ++i) {
            ruleSymbol = (RuleSymbol)this.grammar.rules.elementAt(i);
            if (!ruleSymbol.isDefined() || !ruleSymbol.access.equals("public")) continue;
            bl = true;
            break;
        }
        if (!bl) {
            this.println("");
            this.println("override public IToken nextToken()\t\t\t//throws TokenStreamException");
            this.println("{");
            ++this.tabs;
            this.println("try");
            this.println("{");
            ++this.tabs;
            this.println("uponEOF();");
            --this.tabs;
            this.println("}");
            this.println("catch(CharStreamIOException csioe)");
            this.println("{");
            ++this.tabs;
            this.println("throw new TokenStreamIOException(csioe.io);");
            --this.tabs;
            this.println("}");
            this.println("catch(CharStreamException cse)");
            this.println("{");
            ++this.tabs;
            this.println("throw new TokenStreamException(cse.Message);");
            --this.tabs;
            this.println("}");
            this.println("return new CommonToken(Token.EOF_TYPE, \"\");");
            --this.tabs;
            this.println("}");
            this.println("");
            return;
        }
        RuleBlock ruleBlock = MakeGrammar.createNextTokenRule(this.grammar, this.grammar.rules, "nextToken");
        ruleSymbol = new RuleSymbol("mnextToken");
        ruleSymbol.setDefined();
        ruleSymbol.setBlock(ruleBlock);
        ruleSymbol.access = "private";
        this.grammar.define(ruleSymbol);
        boolean bl2 = this.grammar.theLLkAnalyzer.deterministic(ruleBlock);
        String string = null;
        if (((LexerGrammar)this.grammar).filterMode) {
            string = ((LexerGrammar)this.grammar).filterRule;
        }
        this.println("");
        this.println("override public IToken nextToken()\t\t\t//throws TokenStreamException");
        this.println("{");
        ++this.tabs;
        this.blockNestingLevel = 1;
        this.saveIndexCreateLevel = 0;
        this.println("IToken theRetToken = null;");
        this._println("tryAgain:");
        this.println("for (;;)");
        this.println("{");
        ++this.tabs;
        this.println("IToken _token = null;");
        this.println("int _ttype = Token.INVALID_TYPE;");
        if (((LexerGrammar)this.grammar).filterMode) {
            this.println("setCommitToPath(false);");
            if (string != null) {
                if (!this.grammar.isDefined(CodeGenerator.encodeLexerRuleName(string))) {
                    this.grammar.antlrTool.error("Filter rule " + string + " does not exist in this lexer");
                } else {
                    RuleSymbol ruleSymbol2 = (RuleSymbol)this.grammar.getSymbol(CodeGenerator.encodeLexerRuleName(string));
                    if (!ruleSymbol2.isDefined()) {
                        this.grammar.antlrTool.error("Filter rule " + string + " does not exist in this lexer");
                    } else if (ruleSymbol2.access.equals("public")) {
                        this.grammar.antlrTool.error("Filter rule " + string + " must be protected");
                    }
                }
                this.println("int _m;");
                this.println("_m = mark();");
            }
        }
        this.println("resetText();");
        this.println("try     // for char stream error handling");
        this.println("{");
        ++this.tabs;
        this.println("try     // for lexical error handling");
        this.println("{");
        ++this.tabs;
        for (int i = 0; i < ruleBlock.getAlternatives().size(); ++i) {
            object2 = ruleBlock.getAlternativeAt(i);
            if (!((Alternative)object2).cache[1].containsEpsilon()) continue;
            object = (RuleRefElement)((Alternative)object2).head;
            String string2 = CodeGenerator.decodeLexerRuleName(((RuleRefElement)object).targetRule);
            this.antlrTool.warning("public lexical rule " + string2 + " is optional (can match \"nothing\")");
        }
        String string3 = System.getProperty("line.separator");
        object2 = this.genCommonBlock(ruleBlock, false);
        object = "if (cached_LA1==EOF_CHAR) { uponEOF(); returnToken_ = makeToken(Token.EOF_TYPE); }";
        object = (String)object + string3 + "\t\t\t\t";
        if (((LexerGrammar)this.grammar).filterMode) {
            if (string == null) {
                object = (String)object + "\t\t\t\telse";
                object = (String)object + "\t\t\t\t{";
                object = (String)object + "\t\t\t\t\tconsume();";
                object = (String)object + "\t\t\t\t\tgoto tryAgain;";
                object = (String)object + "\t\t\t\t}";
            } else {
                object = (String)object + "\t\t\t\t\telse" + string3 + "\t\t\t\t\t{" + string3 + "\t\t\t\t\tcommit();" + string3 + "\t\t\t\t\ttry {m" + string + "(false);}" + string3 + "\t\t\t\t\tcatch(RecognitionException e)" + string3 + "\t\t\t\t\t{" + string3 + "\t\t\t\t\t\t// catastrophic failure" + string3 + "\t\t\t\t\t\treportError(e);" + string3 + "\t\t\t\t\t\tconsume();" + string3 + "\t\t\t\t\t}" + string3 + "\t\t\t\t\tgoto tryAgain;" + string3 + "\t\t\t\t}";
            }
        } else {
            object = (String)object + "else {" + this.throwNoViable + "}";
        }
        this.genBlockFinish((CSharpBlockFinishingInfo)object2, (String)object);
        if (((LexerGrammar)this.grammar).filterMode && string != null) {
            this.println("commit();");
        }
        this.println("if ( null==returnToken_ ) goto tryAgain; // found SKIP token");
        this.println("_ttype = returnToken_.Type;");
        if (((LexerGrammar)this.grammar).getTestLiterals()) {
            this.genLiteralsTest();
        }
        this.println("returnToken_.Type = _ttype;");
        this.println("return returnToken_;");
        --this.tabs;
        this.println("}");
        this.println("catch (RecognitionException e) {");
        ++this.tabs;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (string == null) {
                this.println("if (!getCommitToPath())");
                this.println("{");
                ++this.tabs;
                this.println("consume();");
                this.println("goto tryAgain;");
                --this.tabs;
                this.println("}");
            } else {
                this.println("if (!getCommitToPath())");
                this.println("{");
                ++this.tabs;
                this.println("rewind(_m);");
                this.println("resetText();");
                this.println("try {m" + string + "(false);}");
                this.println("catch(RecognitionException ee) {");
                this.println("\t// horrendous failure: error in filter rule");
                this.println("\treportError(ee);");
                this.println("\tconsume();");
                this.println("}");
                --this.tabs;
                this.println("}");
                this.println("else");
            }
        }
        if (ruleBlock.getDefaultErrorHandler()) {
            this.println("{");
            ++this.tabs;
            this.println("reportError(e);");
            this.println("consume();");
            --this.tabs;
            this.println("}");
        } else {
            ++this.tabs;
            this.println("throw new TokenStreamRecognitionException(e);");
            --this.tabs;
        }
        --this.tabs;
        this.println("}");
        --this.tabs;
        this.println("}");
        this.println("catch (CharStreamException cse) {");
        this.println("\tif ( cse is CharStreamIOException ) {");
        this.println("\t\tthrow new TokenStreamIOException(((CharStreamIOException)cse).io);");
        this.println("\t}");
        this.println("\telse {");
        this.println("\t\tthrow new TokenStreamException(cse.Message);");
        this.println("\t}");
        this.println("}");
        --this.tabs;
        this.println("}");
        --this.tabs;
        this.println("}");
        this.println("");
    }

    public void genRule(RuleSymbol ruleSymbol, boolean bl, int n, TokenManager tokenManager) {
        Object object;
        Object object2;
        RuleBlock ruleBlock;
        this.tabs = 1;
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genRule(" + ruleSymbol.getId() + ")");
        }
        if (!ruleSymbol.isDefined()) {
            this.antlrTool.error("undefined rule: " + ruleSymbol.getId());
            return;
        }
        this.currentRule = ruleBlock = ruleSymbol.getBlock();
        this.currentASTResult = ruleSymbol.getId();
        this.declaredASTVariables.clear();
        boolean bl2 = this.genAST;
        this.genAST = this.genAST && ruleBlock.getAutoGen();
        this.saveText = ruleBlock.getAutoGen();
        if (ruleSymbol.comment != null) {
            this._println(ruleSymbol.comment);
        }
        this.print(ruleSymbol.access + " ");
        if (ruleBlock.returnAction != null) {
            this._print(this.extractTypeOfAction(ruleBlock.returnAction, ruleBlock.getLine(), ruleBlock.getColumn()) + " ");
        } else {
            this._print("void ");
        }
        this._print(ruleSymbol.getId() + "(");
        this._print(this.commonExtraParams);
        if (this.commonExtraParams.length() != 0 && ruleBlock.argAction != null) {
            this._print(",");
        }
        if (ruleBlock.argAction != null) {
            this._println("");
            ++this.tabs;
            this.println(ruleBlock.argAction);
            --this.tabs;
            this.print(")");
        } else {
            this._print(")");
        }
        this._print(" //throws " + this.exceptionThrown);
        if (this.grammar instanceof ParserGrammar) {
            this._print(", TokenStreamException");
        } else if (this.grammar instanceof LexerGrammar) {
            this._print(", CharStreamException, TokenStreamException");
        }
        if (ruleBlock.throwsSpec != null) {
            if (this.grammar instanceof LexerGrammar) {
                this.antlrTool.error("user-defined throws spec not allowed (yet) for lexer rule " + ruleBlock.ruleName);
            } else {
                this._print(", " + ruleBlock.throwsSpec);
            }
        }
        this._println("");
        this._println("{");
        ++this.tabs;
        if (ruleBlock.returnAction != null) {
            this.println(ruleBlock.returnAction + ";");
        }
        this.println(this.commonLocalVars);
        if (this.grammar.traceRules) {
            if (this.grammar instanceof TreeWalkerGrammar) {
                if (this.usingCustomAST) {
                    this.println("traceIn(\"" + ruleSymbol.getId() + "\",(AST)_t);");
                } else {
                    this.println("traceIn(\"" + ruleSymbol.getId() + "\",_t);");
                }
            } else {
                this.println("traceIn(\"" + ruleSymbol.getId() + "\");");
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            if (ruleSymbol.getId().equals("mEOF")) {
                this.println("_ttype = Token.EOF_TYPE;");
            } else {
                this.println("_ttype = " + ruleSymbol.getId().substring(1) + ";");
            }
            this.blockNestingLevel = 1;
            this.saveIndexCreateLevel = 0;
        }
        if (this.grammar.debuggingOutput) {
            if (this.grammar instanceof ParserGrammar) {
                this.println("fireEnterRule(" + n + ",0);");
            } else if (this.grammar instanceof LexerGrammar) {
                this.println("fireEnterRule(" + n + ",_ttype);");
            }
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            this.println("try { // debugging");
            ++this.tabs;
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST_in = (" + this.labeledElementASTType + ")_t;");
        }
        if (this.grammar.buildAST) {
            this.println("returnAST = null;");
            this.println("ASTPair currentAST = new ASTPair();");
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST = null;");
        }
        this.genBlockPreamble(ruleBlock);
        this.genBlockInitAction(ruleBlock);
        this.println("");
        ExceptionSpec exceptionSpec = ruleBlock.findExceptionSpec("");
        if (exceptionSpec != null || ruleBlock.getDefaultErrorHandler()) {
            this.println("try {      // for error handling");
            ++this.tabs;
        }
        if (ruleBlock.alternatives.size() == 1) {
            object2 = ruleBlock.getAlternativeAt(0);
            object = ((Alternative)object2).semPred;
            if (object != null) {
                this.genSemPred((String)object, this.currentRule.line);
            }
            if (((Alternative)object2).synPred != null) {
                this.antlrTool.warning("Syntactic predicate ignored for single alternative", this.grammar.getFilename(), ((Alternative)object2).synPred.getLine(), ((Alternative)object2).synPred.getColumn());
            }
            this.genAlt((Alternative)object2, ruleBlock);
        } else {
            boolean bl3 = this.grammar.theLLkAnalyzer.deterministic(ruleBlock);
            object = this.genCommonBlock(ruleBlock, false);
            this.genBlockFinish((CSharpBlockFinishingInfo)object, this.throwNoViable);
        }
        if (exceptionSpec != null || ruleBlock.getDefaultErrorHandler()) {
            --this.tabs;
            this.println("}");
        }
        if (exceptionSpec != null) {
            this.genErrorHandler(exceptionSpec);
        } else if (ruleBlock.getDefaultErrorHandler()) {
            this.println("catch (" + this.exceptionThrown + " ex)");
            this.println("{");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (0 == inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            this.println("reportError(ex);");
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                object2 = this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode);
                object = this.getBitsetName(this.markBitsetForGen(((Lookahead)object2).fset));
                this.println("recover(ex," + (String)object + ");");
            } else {
                this.println("if (null != _t)");
                this.println("{");
                ++this.tabs;
                this.println("_t = _t.getNextSibling();");
                --this.tabs;
                this.println("}");
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("}");
                this.println("else");
                this.println("{");
                ++this.tabs;
                this.println("throw ex;");
                --this.tabs;
                this.println("}");
            }
            --this.tabs;
            this.println("}");
        }
        if (this.grammar.buildAST) {
            this.println("returnAST = " + ruleSymbol.getId() + "_AST;");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("retTree_ = _t;");
        }
        if (ruleBlock.getTestLiterals()) {
            if (ruleSymbol.access.equals("protected")) {
                this.genLiteralsTestForPartialToken();
            } else {
                this.genLiteralsTest();
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            this.println("if (_createToken && (null == _token) && (_ttype != Token.SKIP))");
            this.println("{");
            ++this.tabs;
            this.println("_token = makeToken(_ttype);");
            this.println("_token.setText(text.ToString(_begin, text.Length-_begin));");
            --this.tabs;
            this.println("}");
            this.println("returnToken_ = _token;");
        }
        if (ruleBlock.returnAction != null) {
            this.println("return " + this.extractIdOfAction(ruleBlock.returnAction, ruleBlock.getLine(), ruleBlock.getColumn()) + ";");
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            --this.tabs;
            this.println("}");
            this.println("finally");
            this.println("{ // debugging");
            ++this.tabs;
            if (this.grammar.debuggingOutput) {
                if (this.grammar instanceof ParserGrammar) {
                    this.println("fireExitRule(" + n + ",0);");
                } else if (this.grammar instanceof LexerGrammar) {
                    this.println("fireExitRule(" + n + ",_ttype);");
                }
            }
            if (this.grammar.traceRules) {
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("traceOut(\"" + ruleSymbol.getId() + "\",_t);");
                } else {
                    this.println("traceOut(\"" + ruleSymbol.getId() + "\");");
                }
            }
            --this.tabs;
            this.println("}");
        }
        --this.tabs;
        this.println("}");
        this.println("");
        this.genAST = bl2;
    }

    private void GenRuleInvocation(RuleRefElement ruleRefElement) {
        this._print(ruleRefElement.targetRule + "(");
        if (this.grammar instanceof LexerGrammar) {
            if (ruleRefElement.getLabel() != null) {
                this._print("true");
            } else {
                this._print("false");
            }
            if (this.commonExtraArgs.length() != 0 || ruleRefElement.args != null) {
                this._print(",");
            }
        }
        this._print(this.commonExtraArgs);
        if (this.commonExtraArgs.length() != 0 && ruleRefElement.args != null) {
            this._print(",");
        }
        RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule);
        if (ruleRefElement.args != null) {
            ActionTransInfo actionTransInfo = new ActionTransInfo();
            String string = this.processActionForSpecialSymbols(ruleRefElement.args, 0, this.currentRule, actionTransInfo);
            if (actionTransInfo.assignToRoot || actionTransInfo.refRuleRoot != null) {
                this.antlrTool.error("Arguments of rule reference '" + ruleRefElement.targetRule + "' cannot set or ref #" + this.currentRule.getRuleName(), this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            }
            this._print(string);
            if (ruleSymbol.block.argAction == null) {
                this.antlrTool.warning("Rule '" + ruleRefElement.targetRule + "' accepts no arguments", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            }
        } else if (ruleSymbol.block.argAction != null) {
            this.antlrTool.warning("Missing parameters on reference to rule " + ruleRefElement.targetRule, this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
        }
        this._println(");");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = retTree_;");
        }
    }

    protected void genSemPred(String string, int n) {
        ActionTransInfo actionTransInfo = new ActionTransInfo();
        string = this.processActionForSpecialSymbols(string, n, this.currentRule, actionTransInfo);
        String string2 = this.charFormatter.escapeString(string);
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            string = "fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.VALIDATING," + this.addSemPred(string2) + "," + string + ")";
        }
        this.println("if (!(" + string + "))");
        this.println("  throw new SemanticException(\"" + string2 + "\");");
    }

    protected void genSemPredMap() {
        Enumeration enumeration = this.semPreds.elements();
        this.println("private string[] _semPredNames = {");
        ++this.tabs;
        while (enumeration.hasMoreElements()) {
            this.println("\"" + enumeration.nextElement() + "\",");
        }
        --this.tabs;
        this.println("};");
    }

    protected void genSynPred(SynPredBlock synPredBlock, String string) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen=>(" + synPredBlock + ")");
        }
        this.println("bool synPredMatched" + synPredBlock.ID + " = false;");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if (_t==null) _t=ASTNULL;");
        }
        this.println("if (" + string + ")");
        this.println("{");
        ++this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("AST __t" + synPredBlock.ID + " = _t;");
        } else {
            this.println("int _m" + synPredBlock.ID + " = mark();");
        }
        this.println("synPredMatched" + synPredBlock.ID + " = true;");
        this.println("inputState.guessing++;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("fireSyntacticPredicateStarted();");
        }
        ++this.syntacticPredLevel;
        this.println("try {");
        ++this.tabs;
        this.gen(synPredBlock);
        --this.tabs;
        this.println("}");
        this.println("catch (" + this.exceptionThrown + ")");
        this.println("{");
        ++this.tabs;
        this.println("synPredMatched" + synPredBlock.ID + " = false;");
        --this.tabs;
        this.println("}");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = __t" + synPredBlock.ID + ";");
        } else {
            this.println("rewind(_m" + synPredBlock.ID + ");");
        }
        this.println("inputState.guessing--;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("if (synPredMatched" + synPredBlock.ID + ")");
            this.println("  fireSyntacticPredicateSucceeded();");
            this.println("else");
            this.println("  fireSyntacticPredicateFailed();");
        }
        --this.syntacticPredLevel;
        --this.tabs;
        this.println("}");
        this.println("if ( synPredMatched" + synPredBlock.ID + " )");
        this.println("{");
    }

    public void genTokenStrings() {
        this.println("");
        this.println("public static readonly string[] tokenNames_ = new string[] {");
        ++this.tabs;
        Vector vector = this.grammar.tokenManager.getVocabulary();
        for (int i = 0; i < vector.size(); ++i) {
            String string = (String)vector.elementAt(i);
            if (string == null) {
                string = "<" + String.valueOf(i) + ">";
            }
            if (!string.startsWith("\"") && !string.startsWith("<")) {
                TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string);
                if (tokenSymbol != null && tokenSymbol.getParaphrase() != null) {
                    string = StringUtils.stripFrontBack(tokenSymbol.getParaphrase(), "\"", "\"");
                }
            } else if (string.startsWith("\"")) {
                string = StringUtils.stripFrontBack(string, "\"", "\"");
            }
            this.print(this.charFormatter.literalString(string));
            if (i != vector.size() - 1) {
                this._print(",");
            }
            this._println("");
        }
        --this.tabs;
        this.println("};");
    }

    protected void genTokenTypes(TokenManager tokenManager) throws IOException {
        this.setupOutput(tokenManager.getName() + TokenTypesFileSuffix);
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("public class " + tokenManager.getName() + TokenTypesFileSuffix);
        this.println("{");
        ++this.tabs;
        this.genTokenDefinitions(tokenManager);
        --this.tabs;
        this.println("}");
        --this.tabs;
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
        this.exitIfError();
    }

    protected void genTokenDefinitions(TokenManager tokenManager) throws IOException {
        Vector vector = tokenManager.getVocabulary();
        this.println("public const int EOF = 1;");
        this.println("public const int NULL_TREE_LOOKAHEAD = 3;");
        for (int i = 4; i < vector.size(); ++i) {
            String string = (String)vector.elementAt(i);
            if (string == null) continue;
            if (string.startsWith("\"")) {
                StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(string);
                if (stringLiteralSymbol == null) {
                    this.antlrTool.panic("String literal " + string + " not in symbol table");
                    continue;
                }
                if (stringLiteralSymbol.label != null) {
                    this.println("public const int " + stringLiteralSymbol.label + " = " + i + ";");
                    continue;
                }
                String string2 = this.mangleLiteral(string);
                if (string2 != null) {
                    this.println("public const int " + string2 + " = " + i + ";");
                    stringLiteralSymbol.label = string2;
                    continue;
                }
                this.println("// " + string + " = " + i);
                continue;
            }
            if (string.startsWith("<")) continue;
            this.println("public const int " + string + " = " + i + ";");
        }
        this.println("");
    }

    public String processStringForASTConstructor(String string) {
        if (this.usingCustomAST && (this.grammar instanceof TreeWalkerGrammar || this.grammar instanceof ParserGrammar) && !this.grammar.tokenManager.tokenDefined(string)) {
            return "(AST)" + string;
        }
        return string;
    }

    public String getASTCreateString(Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("(" + this.labeledElementASTType + ") astFactory.make(");
        stringBuffer.append(vector.elementAt(0));
        for (int i = 1; i < vector.size(); ++i) {
            stringBuffer.append(", " + vector.elementAt(i));
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    public String getASTCreateString(GrammarAtom grammarAtom, String string) {
        String string2 = "astFactory.create(" + string + ")";
        if (grammarAtom == null) {
            return this.getASTCreateString(string);
        }
        if (grammarAtom.getASTNodeType() != null) {
            TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(grammarAtom.getText());
            if (tokenSymbol == null || tokenSymbol.getASTNodeType() != grammarAtom.getASTNodeType()) {
                string2 = "(" + grammarAtom.getASTNodeType() + ") astFactory.create(" + string + ", \"" + grammarAtom.getASTNodeType() + "\")";
            } else if (tokenSymbol != null && tokenSymbol.getASTNodeType() != null) {
                string2 = "(" + tokenSymbol.getASTNodeType() + ") " + string2;
            }
        } else if (this.usingCustomAST) {
            string2 = "(" + this.labeledElementASTType + ") " + string2;
        }
        return string2;
    }

    public String getASTCreateString(String string) {
        TokenSymbol tokenSymbol;
        if (string == null) {
            string = "";
        }
        String string2 = "astFactory.create(" + string + ")";
        String string3 = string;
        String string4 = null;
        boolean bl = false;
        int n = string.indexOf(44);
        if (n != -1) {
            string3 = string.substring(0, n);
            string4 = string.substring(n + 1, string.length());
            if ((n = string4.indexOf(44)) != -1) {
                bl = true;
            }
        }
        if (null != (tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string3)) && null != tokenSymbol.getASTNodeType()) {
            string2 = "(" + tokenSymbol.getASTNodeType() + ") " + string2;
        } else if (this.usingCustomAST) {
            string2 = "(" + this.labeledElementASTType + ") " + string2;
        }
        return string2;
    }

    protected String getLookaheadTestExpression(Lookahead[] lookaheadArray, int n) {
        StringBuffer stringBuffer = new StringBuffer(100);
        boolean bl = true;
        stringBuffer.append("(");
        for (int i = 1; i <= n; ++i) {
            BitSet bitSet = lookaheadArray[i].fset;
            if (!bl) {
                stringBuffer.append(") && (");
            }
            bl = false;
            if (lookaheadArray[i].containsEpsilon()) {
                stringBuffer.append("true");
                continue;
            }
            stringBuffer.append(this.getLookaheadTestTerm(i, bitSet));
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    protected String getLookaheadTestExpression(Alternative alternative, int n) {
        int n2 = alternative.lookaheadDepth;
        if (n2 == Integer.MAX_VALUE) {
            n2 = this.grammar.maxk;
        }
        if (n == 0) {
            return "( true )";
        }
        return "(" + this.getLookaheadTestExpression(alternative.cache, n2) + ")";
    }

    protected String getLookaheadTestTerm(int n, BitSet bitSet) {
        String string = this.lookaheadString(n);
        int[] nArray = bitSet.toArray();
        if (CSharpCodeGenerator.elementsAreRange(nArray)) {
            return this.getRangeExpression(n, nArray);
        }
        int n2 = bitSet.degree();
        if (n2 == 0) {
            return "true";
        }
        if (n2 >= this.bitsetTestThreshold) {
            int n3 = this.markBitsetForGen(bitSet);
            return this.getBitsetName(n3) + ".member(" + string + ")";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < nArray.length; ++i) {
            String string2 = this.getValueString(nArray[i]);
            if (i > 0) {
                stringBuffer.append("||");
            }
            stringBuffer.append(string);
            stringBuffer.append("==");
            stringBuffer.append(string2);
        }
        return stringBuffer.toString();
    }

    public String getRangeExpression(int n, int[] nArray) {
        if (!CSharpCodeGenerator.elementsAreRange(nArray)) {
            this.antlrTool.panic("getRangeExpression called with non-range");
        }
        int n2 = nArray[0];
        int n3 = nArray[nArray.length - 1];
        return "(" + this.lookaheadString(n) + " >= " + this.getValueString(n2) + " && " + this.lookaheadString(n) + " <= " + this.getValueString(n3) + ")";
    }

    private String getValueString(int n) {
        String string;
        if (this.grammar instanceof LexerGrammar) {
            string = this.charFormatter.literalChar(n);
        } else {
            TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbolAt(n);
            if (tokenSymbol == null) {
                return "" + n;
            }
            String string2 = tokenSymbol.getId();
            if (tokenSymbol instanceof StringLiteralSymbol) {
                StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenSymbol;
                String string3 = stringLiteralSymbol.getLabel();
                if (string3 != null) {
                    string = string3;
                } else {
                    string = this.mangleLiteral(string2);
                    if (string == null) {
                        string = String.valueOf(n);
                    }
                }
            } else {
                string = string2;
            }
        }
        return string;
    }

    protected boolean lookaheadIsEmpty(Alternative alternative, int n) {
        int n2 = alternative.lookaheadDepth;
        if (n2 == Integer.MAX_VALUE) {
            n2 = this.grammar.maxk;
        }
        for (int i = 1; i <= n2 && i <= n; ++i) {
            BitSet bitSet = alternative.cache[i].fset;
            if (bitSet.degree() == 0) continue;
            return false;
        }
        return true;
    }

    private String lookaheadString(int n) {
        if (this.grammar instanceof TreeWalkerGrammar) {
            return "_t.Type";
        }
        if (this.grammar instanceof LexerGrammar) {
            if (n == 1) {
                return "cached_LA1";
            }
            if (n == 2) {
                return "cached_LA2";
            }
        }
        return "LA(" + n + ")";
    }

    private String mangleLiteral(String string) {
        String string2 = this.antlrTool.literalsPrefix;
        for (int i = 1; i < string.length() - 1; ++i) {
            if (!Character.isLetter(string.charAt(i)) && string.charAt(i) != '_') {
                return null;
            }
            string2 = string2 + string.charAt(i);
        }
        if (this.antlrTool.upperCaseMangledLiterals) {
            string2 = string2.toUpperCase();
        }
        return string2;
    }

    public String mapTreeId(String string, ActionTransInfo actionTransInfo) {
        Object object;
        if (this.currentRule == null) {
            return string;
        }
        boolean bl = false;
        String string2 = string;
        if (this.grammar instanceof TreeWalkerGrammar) {
            if (!this.grammar.buildAST) {
                bl = true;
            } else if (string2.length() > 3 && string2.lastIndexOf("_in") == string2.length() - 3) {
                string2 = string2.substring(0, string2.length() - 3);
                bl = true;
            }
        }
        for (int i = 0; i < this.currentRule.labeledElements.size(); ++i) {
            object = (AlternativeElement)this.currentRule.labeledElements.elementAt(i);
            if (!((AlternativeElement)object).getLabel().equals(string2)) continue;
            return bl ? string2 : string2 + "_AST";
        }
        String string3 = (String)this.treeVariableMap.get(string2);
        if (string3 != null) {
            if (string3 == NONUNIQUE) {
                this.antlrTool.error("Ambiguous reference to AST element " + string2 + " in rule " + this.currentRule.getRuleName());
                return null;
            }
            if (string3.equals(this.currentRule.getRuleName())) {
                this.antlrTool.error("Ambiguous reference to AST element " + string2 + " in rule " + this.currentRule.getRuleName());
                return null;
            }
            return bl ? string3 + "_in" : string3;
        }
        if (string2.equals(this.currentRule.getRuleName())) {
            Object object2 = object = bl ? string2 + "_AST_in" : string2 + "_AST";
            if (actionTransInfo != null && !bl) {
                actionTransInfo.refRuleRoot = object;
            }
            return object;
        }
        return string2;
    }

    private void mapTreeVariable(AlternativeElement alternativeElement, String string) {
        if (alternativeElement instanceof TreeElement) {
            this.mapTreeVariable(((TreeElement)alternativeElement).root, string);
            return;
        }
        String string2 = null;
        if (alternativeElement.getLabel() == null) {
            if (alternativeElement instanceof TokenRefElement) {
                string2 = ((TokenRefElement)alternativeElement).atomText;
            } else if (alternativeElement instanceof RuleRefElement) {
                string2 = ((RuleRefElement)alternativeElement).targetRule;
            }
        }
        if (string2 != null) {
            if (this.treeVariableMap.get(string2) != null) {
                this.treeVariableMap.remove(string2);
                this.treeVariableMap.put(string2, NONUNIQUE);
            } else {
                this.treeVariableMap.put(string2, string);
            }
        }
    }

    protected String processActionForSpecialSymbols(String string, int n, RuleBlock ruleBlock, ActionTransInfo actionTransInfo) {
        if (string == null || string.length() == 0) {
            return null;
        }
        if (this.grammar == null) {
            return string;
        }
        if (this.grammar.buildAST && string.indexOf(35) != -1 || this.grammar instanceof TreeWalkerGrammar || (this.grammar instanceof LexerGrammar || this.grammar instanceof ParserGrammar) && string.indexOf(36) != -1) {
            ActionLexer actionLexer = new ActionLexer(string, ruleBlock, this, actionTransInfo);
            actionLexer.setLineOffset(n);
            actionLexer.setFilename(this.grammar.getFilename());
            actionLexer.setTool(this.antlrTool);
            try {
                actionLexer.mACTION(true);
                string = actionLexer.getTokenObject().getText();
            }
            catch (RecognitionException recognitionException) {
                actionLexer.reportError(recognitionException);
                return string;
            }
            catch (TokenStreamException tokenStreamException) {
                this.antlrTool.panic("Error reading action:" + string);
                return string;
            }
            catch (CharStreamException charStreamException) {
                this.antlrTool.panic("Error reading action:" + string);
                return string;
            }
        }
        return string;
    }

    private void setupGrammarParameters(Grammar grammar) {
        Token token;
        if (grammar instanceof ParserGrammar || grammar instanceof LexerGrammar || grammar instanceof TreeWalkerGrammar) {
            if (this.antlrTool.nameSpace != null) {
                nameSpace = new CSharpNameSpace(this.antlrTool.nameSpace.getName());
            }
            if (grammar.hasOption("namespace") && (token = grammar.getOption("namespace")) != null) {
                nameSpace = new CSharpNameSpace(token.getText());
            }
        }
        if (grammar instanceof ParserGrammar) {
            String string;
            this.labeledElementASTType = "AST";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.usingCustomAST = true;
                this.labeledElementASTType = string;
            }
            this.labeledElementType = "IToken ";
            this.labeledElementInit = "null";
            this.commonExtraArgs = "";
            this.commonExtraParams = "";
            this.commonLocalVars = "";
            this.lt1Value = "LT(1)";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltException(LT(1), getFilename());";
        } else if (grammar instanceof LexerGrammar) {
            this.labeledElementType = "char ";
            this.labeledElementInit = "'\\0'";
            this.commonExtraArgs = "";
            this.commonExtraParams = "bool _createToken";
            this.commonLocalVars = "int _ttype; IToken _token=null; int _begin=text.Length;";
            this.lt1Value = "cached_LA1";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());";
        } else if (grammar instanceof TreeWalkerGrammar) {
            String string;
            this.labeledElementASTType = "AST";
            this.labeledElementType = "AST";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.usingCustomAST = true;
                this.labeledElementASTType = string;
                this.labeledElementType = string;
            }
            if (!grammar.hasOption("ASTLabelType")) {
                grammar.setOption("ASTLabelType", new Token(6, "AST"));
            }
            this.labeledElementInit = "null";
            this.commonExtraArgs = "_t";
            this.commonExtraParams = "AST _t";
            this.commonLocalVars = "";
            this.lt1Value = this.usingCustomAST ? "(_t==ASTNULL) ? null : (" + this.labeledElementASTType + ")_t" : "_t";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltException(_t);";
        } else {
            this.antlrTool.panic("Unknown grammar type");
        }
    }

    public void setupOutput(String string) throws IOException {
        this.currentOutput = this.antlrTool.openOutputFile(string + ".cs");
    }

    private static String OctalToUnicode(String string) {
        if (4 <= string.length() && '\'' == string.charAt(0) && '\\' == string.charAt(1) && '0' <= string.charAt(2) && '7' >= string.charAt(2) && '\'' == string.charAt(string.length() - 1)) {
            Integer n = Integer.valueOf(string.substring(2, string.length() - 1), 8);
            return "'\\x" + Integer.toHexString(n) + "'";
        }
        return string;
    }

    public String getTokenTypesClassName() {
        TokenManager tokenManager = this.grammar.tokenManager;
        return new String(tokenManager.getName() + TokenTypesFileSuffix);
    }

    private void declareSaveIndexVariableIfNeeded() {
        if (this.saveIndexCreateLevel == 0) {
            this.println("int _saveIndex = 0;");
            this.saveIndexCreateLevel = this.blockNestingLevel;
        }
    }

    public String[] split(String string, String string2) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, string2);
        int n = stringTokenizer.countTokens();
        String[] stringArray = new String[n];
        int n2 = 0;
        while (stringTokenizer.hasMoreTokens()) {
            stringArray[n2] = stringTokenizer.nextToken();
            ++n2;
        }
        return stringArray;
    }
}

