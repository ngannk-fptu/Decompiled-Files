/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ActionElement;
import antlr.ActionTransInfo;
import antlr.Alternative;
import antlr.AlternativeBlock;
import antlr.AlternativeElement;
import antlr.BlockEndElement;
import antlr.CharLiteralElement;
import antlr.CharRangeElement;
import antlr.CharStreamException;
import antlr.CodeGenerator;
import antlr.ExceptionHandler;
import antlr.ExceptionSpec;
import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.GrammarSymbol;
import antlr.LexerGrammar;
import antlr.Lookahead;
import antlr.MakeGrammar;
import antlr.OneOrMoreBlock;
import antlr.ParserGrammar;
import antlr.PythonBlockFinishingInfo;
import antlr.PythonCharFormatter;
import antlr.RecognitionException;
import antlr.RuleBlock;
import antlr.RuleRefElement;
import antlr.RuleSymbol;
import antlr.StringLiteralElement;
import antlr.StringLiteralSymbol;
import antlr.StringUtils;
import antlr.SynPredBlock;
import antlr.Token;
import antlr.TokenManager;
import antlr.TokenRangeElement;
import antlr.TokenRefElement;
import antlr.TokenStreamException;
import antlr.TokenSymbol;
import antlr.Tool;
import antlr.TreeElement;
import antlr.TreeWalkerGrammar;
import antlr.Utils;
import antlr.WildcardElement;
import antlr.ZeroOrMoreBlock;
import antlr.actions.python.ActionLexer;
import antlr.actions.python.CodeLexer;
import antlr.collections.impl.BitSet;
import antlr.collections.impl.Vector;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

public class PythonCodeGenerator
extends CodeGenerator {
    protected int syntacticPredLevel = 0;
    protected boolean genAST = false;
    protected boolean saveText = false;
    String labeledElementType;
    String labeledElementASTType;
    String labeledElementInit;
    String commonExtraArgs;
    String commonExtraParams;
    String commonLocalVars;
    String lt1Value;
    String exceptionThrown;
    String throwNoViable;
    public static final String initHeaderAction = "__init__";
    public static final String mainHeaderAction = "__main__";
    String lexerClassName;
    String parserClassName;
    String treeWalkerClassName;
    RuleBlock currentRule;
    String currentASTResult;
    Hashtable treeVariableMap = new Hashtable();
    Hashtable declaredASTVariables = new Hashtable();
    int astVarNumber = 1;
    protected static final String NONUNIQUE = new String();
    public static final int caseSizeThreshold = 127;
    private Vector semPreds;

    protected void printTabs() {
        for (int i = 0; i < this.tabs; ++i) {
            this.currentOutput.print("    ");
        }
    }

    public PythonCodeGenerator() {
        this.charFormatter = new PythonCharFormatter();
        this.DEBUG_CODE_GENERATOR = true;
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

    protected void checkCurrentOutputStream() {
        try {
            if (this.currentOutput == null) {
                throw new NullPointerException();
            }
        }
        catch (Exception exception) {
            Utils.error("current output is not set");
        }
    }

    protected String extractIdOfAction(String string, int n, int n2) {
        string = this.removeAssignmentFromDeclaration(string);
        string = string.trim();
        return string;
    }

    protected String extractTypeOfAction(String string, int n, int n2) {
        return "";
    }

    protected void flushTokens() {
        try {
            boolean bl = false;
            this.checkCurrentOutputStream();
            this.println("");
            this.println("### import antlr.Token ");
            this.println("from antlr import Token");
            this.println("### >>>The Known Token Types <<<");
            PrintWriter printWriter = this.currentOutput;
            Enumeration enumeration = this.behavior.tokenManagers.elements();
            while (enumeration.hasMoreElements()) {
                TokenManager tokenManager = (TokenManager)enumeration.nextElement();
                if (!tokenManager.isReadOnly()) {
                    if (!bl) {
                        this.genTokenTypes(tokenManager);
                        bl = true;
                    }
                    this.currentOutput = printWriter;
                    this.genTokenInterchange(tokenManager);
                    this.currentOutput = printWriter;
                }
                this.exitIfError();
            }
        }
        catch (Exception exception) {
            this.exitIfError();
        }
        this.checkCurrentOutputStream();
        this.println("");
    }

    public void gen() {
        try {
            Enumeration enumeration = this.behavior.grammars.elements();
            while (enumeration.hasMoreElements()) {
                Grammar grammar = (Grammar)enumeration.nextElement();
                grammar.setGrammarAnalyzer(this.analyzer);
                grammar.setCodeGenerator(this);
                this.analyzer.setGrammar(grammar);
                this.setupGrammarParameters(grammar);
                grammar.generate();
                this.exitIfError();
            }
        }
        catch (IOException iOException) {
            this.antlrTool.reportException(iOException, null);
        }
    }

    public void gen(ActionElement actionElement) {
        if (actionElement.isSemPred) {
            this.genSemPred(actionElement.actionText, actionElement.line);
        } else {
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if not self.inputState.guessing:");
                ++this.tabs;
            }
            ActionTransInfo actionTransInfo = new ActionTransInfo();
            String string = this.processActionForSpecialSymbols(actionElement.actionText, actionElement.getLine(), this.currentRule, actionTransInfo);
            if (actionTransInfo.refRuleRoot != null) {
                this.println(actionTransInfo.refRuleRoot + " = currentAST.root");
            }
            this.printAction(string);
            if (actionTransInfo.assignToRoot) {
                this.println("currentAST.root = " + actionTransInfo.refRuleRoot + "");
                this.println("if (" + actionTransInfo.refRuleRoot + " != None) and (" + actionTransInfo.refRuleRoot + ".getFirstChild() != None):");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot + ".getFirstChild()");
                --this.tabs;
                this.println("else:");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot);
                --this.tabs;
                this.println("currentAST.advanceChildToEnd()");
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
            }
        }
    }

    public void gen(AlternativeBlock alternativeBlock) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen(" + alternativeBlock + ")");
        }
        this.genBlockPreamble(alternativeBlock);
        this.genBlockInitAction(alternativeBlock);
        String string = this.currentASTResult;
        if (alternativeBlock.getLabel() != null) {
            this.currentASTResult = alternativeBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(alternativeBlock);
        int n = this.tabs;
        PythonBlockFinishingInfo pythonBlockFinishingInfo = this.genCommonBlock(alternativeBlock, true);
        this.genBlockFinish(pythonBlockFinishingInfo, this.throwNoViable);
        this.tabs = n;
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
            this.println(charLiteralElement.getLabel() + " = " + this.lt1Value);
        }
        boolean bl = this.saveText;
        this.saveText = this.saveText && charLiteralElement.getAutoGenType() == 1;
        this.genMatch(charLiteralElement);
        this.saveText = bl;
    }

    String toString(boolean bl) {
        String string = bl ? "True" : "False";
        return string;
    }

    public void gen(CharRangeElement charRangeElement) {
        boolean bl;
        if (charRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(charRangeElement.getLabel() + " = " + this.lt1Value);
        }
        boolean bl2 = bl = this.grammar instanceof LexerGrammar && (!this.saveText || charRangeElement.getAutoGenType() == 3);
        if (bl) {
            this.println("_saveIndex = self.text.length()");
        }
        this.println("self.matchRange(u" + charRangeElement.beginText + ", u" + charRangeElement.endText + ")");
        if (bl) {
            this.println("self.text.setLength(_saveIndex)");
        }
    }

    public void gen(LexerGrammar lexerGrammar) throws IOException {
        GrammarSymbol grammarSymbol;
        Object object;
        Object object2;
        if (lexerGrammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        this.setGrammar(lexerGrammar);
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.panic("Internal error generating lexer");
        }
        this.setupOutput(this.grammar.getClassName());
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.genHeader();
        this.println("### import antlr and other modules ..");
        this.println("import sys");
        this.println("import antlr");
        this.println("");
        this.println("version = sys.version.split()[0]");
        this.println("if version < '2.2.1':");
        ++this.tabs;
        this.println("False = 0");
        --this.tabs;
        this.println("if version < '2.3':");
        ++this.tabs;
        this.println("True = not False");
        --this.tabs;
        this.println("### header action >>> ");
        this.printActionCode(this.behavior.getHeaderAction(""), 0);
        this.println("### header action <<< ");
        this.println("### preamble action >>> ");
        this.printActionCode(this.grammar.preambleAction.getText(), 0);
        this.println("### preamble action <<< ");
        String string = null;
        string = this.grammar.superClass != null ? this.grammar.superClass : "antlr." + this.grammar.getSuperClass();
        String string2 = "";
        Token token = (Token)this.grammar.options.get("classHeaderPrefix");
        if (token != null && (object2 = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
            string2 = object2;
        }
        this.println("### >>>The Literals<<<");
        this.println("literals = {}");
        object2 = this.grammar.tokenManager.getTokenSymbolKeys();
        while (object2.hasMoreElements()) {
            TokenSymbol tokenSymbol;
            object = (String)object2.nextElement();
            if (((String)object).charAt(0) != '\"' || !((tokenSymbol = this.grammar.tokenManager.getTokenSymbol((String)object)) instanceof StringLiteralSymbol)) continue;
            grammarSymbol = (StringLiteralSymbol)tokenSymbol;
            this.println("literals[u" + grammarSymbol.getId() + "] = " + ((TokenSymbol)grammarSymbol).getTokenType());
        }
        this.println("");
        this.flushTokens();
        this.genJavadocComment(this.grammar);
        this.println("class " + this.lexerClassName + "(" + string + ") :");
        ++this.tabs;
        this.printGrammarAction(this.grammar);
        this.println("def __init__(self, *argv, **kwargs) :");
        ++this.tabs;
        this.println(string + ".__init__(self, *argv, **kwargs)");
        this.println("self.caseSensitiveLiterals = " + this.toString(lexerGrammar.caseSensitiveLiterals));
        this.println("self.setCaseSensitive(" + this.toString(lexerGrammar.caseSensitive) + ")");
        this.println("self.literals = literals");
        if (this.grammar.debuggingOutput) {
            this.println("ruleNames[] = [");
            object = this.grammar.rules.elements();
            boolean bl = false;
            ++this.tabs;
            while (object.hasMoreElements()) {
                grammarSymbol = (GrammarSymbol)object.nextElement();
                if (!(grammarSymbol instanceof RuleSymbol)) continue;
                this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
            }
            --this.tabs;
            this.println("]");
        }
        this.genHeaderInit(this.grammar);
        --this.tabs;
        this.genNextToken();
        this.println("");
        object = this.grammar.rules.elements();
        int n = 0;
        while (object.hasMoreElements()) {
            grammarSymbol = (RuleSymbol)object.nextElement();
            if (!grammarSymbol.getId().equals("mnextToken")) {
                this.genRule((RuleSymbol)grammarSymbol, false, n++);
            }
            this.exitIfError();
        }
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
        this.println("");
        this.genHeaderMain(this.grammar);
        this.currentOutput.close();
        this.currentOutput = null;
    }

    protected void genHeaderMain(Grammar grammar) {
        String string = grammar.getClassName() + "." + mainHeaderAction;
        String string2 = this.behavior.getHeaderAction(string);
        if (PythonCodeGenerator.isEmpty(string2)) {
            string2 = this.behavior.getHeaderAction(mainHeaderAction);
        }
        if (PythonCodeGenerator.isEmpty(string2)) {
            if (grammar instanceof LexerGrammar) {
                int n = this.tabs;
                this.tabs = 0;
                this.println("### __main__ header action >>> ");
                this.genLexerTest();
                this.tabs = 0;
                this.println("### __main__ header action <<< ");
                this.tabs = n;
            }
        } else {
            int n = this.tabs;
            this.tabs = 0;
            this.println("");
            this.println("### __main__ header action >>> ");
            this.printMainFunc(string2);
            this.tabs = 0;
            this.println("### __main__ header action <<< ");
            this.tabs = n;
        }
    }

    protected void genHeaderInit(Grammar grammar) {
        String string = grammar.getClassName() + "." + initHeaderAction;
        String string2 = this.behavior.getHeaderAction(string);
        if (PythonCodeGenerator.isEmpty(string2)) {
            string2 = this.behavior.getHeaderAction(initHeaderAction);
        }
        if (!PythonCodeGenerator.isEmpty(string2)) {
            int n = this.tabs;
            this.println("### __init__ header action >>> ");
            this.printActionCode(string2, 0);
            this.tabs = n;
            this.println("### __init__ header action <<< ");
        }
    }

    protected void printMainFunc(String string) {
        int n = this.tabs;
        this.tabs = 0;
        this.println("if __name__ == '__main__':");
        ++this.tabs;
        this.printActionCode(string, 0);
        --this.tabs;
        this.tabs = n;
    }

    public void gen(OneOrMoreBlock oneOrMoreBlock) {
        int n = this.tabs;
        this.genBlockPreamble(oneOrMoreBlock);
        String string = oneOrMoreBlock.getLabel() != null ? "_cnt_" + oneOrMoreBlock.getLabel() : "_cnt" + oneOrMoreBlock.ID;
        this.println("" + string + "= 0");
        this.println("while True:");
        ++this.tabs;
        n = this.tabs;
        this.genBlockInitAction(oneOrMoreBlock);
        String string2 = this.currentASTResult;
        if (oneOrMoreBlock.getLabel() != null) {
            this.currentASTResult = oneOrMoreBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(oneOrMoreBlock);
        boolean bl2 = false;
        int n2 = this.grammar.maxk;
        if (!oneOrMoreBlock.greedy && oneOrMoreBlock.exitLookaheadDepth <= this.grammar.maxk && oneOrMoreBlock.exitCache[oneOrMoreBlock.exitLookaheadDepth].containsEpsilon()) {
            bl2 = true;
            n2 = oneOrMoreBlock.exitLookaheadDepth;
        } else if (!oneOrMoreBlock.greedy && oneOrMoreBlock.exitLookaheadDepth == Integer.MAX_VALUE) {
            bl2 = true;
        }
        if (bl2) {
            this.println("### nongreedy (...)+ loop; exit depth is " + oneOrMoreBlock.exitLookaheadDepth);
            String string3 = this.getLookaheadTestExpression(oneOrMoreBlock.exitCache, n2);
            this.println("### nongreedy exit test");
            this.println("if " + string + " >= 1 and " + string3 + ":");
            ++this.tabs;
            this.println("break");
            --this.tabs;
        }
        int n3 = this.tabs;
        PythonBlockFinishingInfo pythonBlockFinishingInfo = this.genCommonBlock(oneOrMoreBlock, false);
        this.genBlockFinish(pythonBlockFinishingInfo, "break");
        this.tabs = n3;
        this.tabs = n;
        this.println(string + " += 1");
        this.tabs = n;
        --this.tabs;
        this.println("if " + string + " < 1:");
        ++this.tabs;
        this.println(this.throwNoViable);
        --this.tabs;
        this.currentASTResult = string2;
    }

    public void gen(ParserGrammar parserGrammar) throws IOException {
        GrammarSymbol grammarSymbol;
        int n;
        Object object;
        if (parserGrammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        this.setGrammar(parserGrammar);
        if (!(this.grammar instanceof ParserGrammar)) {
            this.antlrTool.panic("Internal error generating parser");
        }
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println("### import antlr and other modules ..");
        this.println("import sys");
        this.println("import antlr");
        this.println("");
        this.println("version = sys.version.split()[0]");
        this.println("if version < '2.2.1':");
        ++this.tabs;
        this.println("False = 0");
        --this.tabs;
        this.println("if version < '2.3':");
        ++this.tabs;
        this.println("True = not False");
        --this.tabs;
        this.println("### header action >>> ");
        this.printActionCode(this.behavior.getHeaderAction(""), 0);
        this.println("### header action <<< ");
        this.println("### preamble action>>>");
        this.printActionCode(this.grammar.preambleAction.getText(), 0);
        this.println("### preamble action <<<");
        this.flushTokens();
        String string = null;
        string = this.grammar.superClass != null ? this.grammar.superClass : "antlr." + this.grammar.getSuperClass();
        this.genJavadocComment(this.grammar);
        String string2 = "";
        Token token = (Token)this.grammar.options.get("classHeaderPrefix");
        if (token != null && (object = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
            string2 = object;
        }
        this.print("class " + this.parserClassName + "(" + string);
        this.println("):");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("_ruleNames = [");
            object = this.grammar.rules.elements();
            n = 0;
            ++this.tabs;
            while (object.hasMoreElements()) {
                grammarSymbol = (GrammarSymbol)object.nextElement();
                if (!(grammarSymbol instanceof RuleSymbol)) continue;
                this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
            }
            --this.tabs;
            this.println("]");
        }
        this.printGrammarAction(this.grammar);
        this.println("");
        this.println("def __init__(self, *args, **kwargs):");
        ++this.tabs;
        this.println(string + ".__init__(self, *args, **kwargs)");
        this.println("self.tokenNames = _tokenNames");
        if (this.grammar.debuggingOutput) {
            this.println("self.ruleNames  = _ruleNames");
            this.println("self.semPredNames = _semPredNames");
            this.println("self.setupDebugging(self.tokenBuf)");
        }
        if (this.grammar.buildAST) {
            this.println("self.buildTokenTypeASTClassMap()");
            this.println("self.astFactory = antlr.ASTFactory(self.getTokenTypeToASTClassMap())");
            if (this.labeledElementASTType != null) {
                this.println("self.astFactory.setASTNodeClass(" + this.labeledElementASTType + ")");
            }
        }
        this.genHeaderInit(this.grammar);
        this.println("");
        object = this.grammar.rules.elements();
        n = 0;
        while (object.hasMoreElements()) {
            grammarSymbol = (GrammarSymbol)object.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n++);
            }
            this.exitIfError();
        }
        if (this.grammar.buildAST) {
            this.genTokenASTNodeMap();
        }
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.println("");
        this.tabs = 0;
        this.genHeaderMain(this.grammar);
        this.currentOutput.close();
        this.currentOutput = null;
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
            this.println(ruleRefElement.getLabel() + " = antlr.ifelse(_t == antlr.ASTNULL, None, " + this.lt1Value + ")");
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || ruleRefElement.getAutoGenType() == 3)) {
            this.println("_saveIndex = self.text.length()");
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
            this.println("self.text.setLength(_saveIndex)");
        }
        if (this.syntacticPredLevel == 0) {
            boolean bl;
            boolean bl2 = bl = this.grammar.hasSyntacticPredicate && (this.grammar.buildAST && ruleRefElement.getLabel() != null || this.genAST && ruleRefElement.getAutoGenType() == 1);
            if (bl) {
                // empty if block
            }
            if (this.grammar.buildAST && ruleRefElement.getLabel() != null) {
                this.println(ruleRefElement.getLabel() + "_AST = self.returnAST");
            }
            if (this.genAST) {
                switch (ruleRefElement.getAutoGenType()) {
                    case 1: {
                        this.println("self.addASTChild(currentAST, self.returnAST)");
                        break;
                    }
                    case 2: {
                        this.antlrTool.error("Internal: encountered ^ after rule reference");
                        break;
                    }
                }
            }
            if (this.grammar instanceof LexerGrammar && ruleRefElement.getLabel() != null) {
                this.println(ruleRefElement.getLabel() + " = self._returnToken");
            }
            if (bl) {
                // empty if block
            }
        }
        this.genErrorCatchForElement(ruleRefElement);
    }

    public void gen(StringLiteralElement stringLiteralElement) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genString(" + stringLiteralElement + ")");
        }
        if (stringLiteralElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(stringLiteralElement.getLabel() + " = " + this.lt1Value + "");
        }
        this.genElementAST(stringLiteralElement);
        boolean bl = this.saveText;
        this.saveText = this.saveText && stringLiteralElement.getAutoGenType() == 1;
        this.genMatch(stringLiteralElement);
        this.saveText = bl;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling()");
        }
    }

    public void gen(TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value);
        }
        this.genElementAST(tokenRangeElement);
        this.println("self.matchRange(u" + tokenRangeElement.beginText + ", u" + tokenRangeElement.endText + ")");
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
            this.println(tokenRefElement.getLabel() + " = " + this.lt1Value + "");
        }
        this.genElementAST(tokenRefElement);
        this.genMatch(tokenRefElement);
        this.genErrorCatchForElement(tokenRefElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling()");
        }
    }

    public void gen(TreeElement treeElement) {
        this.println("_t" + treeElement.ID + " = _t");
        if (treeElement.root.getLabel() != null) {
            this.println(treeElement.root.getLabel() + " = antlr.ifelse(_t == antlr.ASTNULL, None, _t)");
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
            this.println("_currentAST" + treeElement.ID + " = currentAST.copy()");
            this.println("currentAST.root = currentAST.child");
            this.println("currentAST.child = None");
        }
        if (treeElement.root instanceof WildcardElement) {
            this.println("if not _t: raise antlr.MismatchedTokenException()");
        } else {
            this.genMatch(treeElement.root);
        }
        this.println("_t = _t.getFirstChild()");
        for (int i = 0; i < treeElement.getAlternatives().size(); ++i) {
            Alternative alternative = treeElement.getAlternativeAt(i);
            AlternativeElement alternativeElement = alternative.head;
            while (alternativeElement != null) {
                alternativeElement.generate();
                alternativeElement = alternativeElement.next;
            }
        }
        if (this.grammar.buildAST) {
            this.println("currentAST = _currentAST" + treeElement.ID + "");
        }
        this.println("_t = _t" + treeElement.ID + "");
        this.println("_t = _t.getNextSibling()");
    }

    public void gen(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        Object object;
        this.setGrammar(treeWalkerGrammar);
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.antlrTool.panic("Internal error generating tree-walker");
        }
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println("### import antlr and other modules ..");
        this.println("import sys");
        this.println("import antlr");
        this.println("");
        this.println("version = sys.version.split()[0]");
        this.println("if version < '2.2.1':");
        ++this.tabs;
        this.println("False = 0");
        --this.tabs;
        this.println("if version < '2.3':");
        ++this.tabs;
        this.println("True = not False");
        --this.tabs;
        this.println("### header action >>> ");
        this.printActionCode(this.behavior.getHeaderAction(""), 0);
        this.println("### header action <<< ");
        this.flushTokens();
        this.println("### user code>>>");
        this.printActionCode(this.grammar.preambleAction.getText(), 0);
        this.println("### user code<<<");
        String string = null;
        string = this.grammar.superClass != null ? this.grammar.superClass : "antlr." + this.grammar.getSuperClass();
        this.println("");
        String string2 = "";
        Token token = (Token)this.grammar.options.get("classHeaderPrefix");
        if (token != null && (object = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
            string2 = object;
        }
        this.genJavadocComment(this.grammar);
        this.println("class " + this.treeWalkerClassName + "(" + string + "):");
        ++this.tabs;
        this.println("");
        this.println("# ctor ..");
        this.println("def __init__(self, *args, **kwargs):");
        ++this.tabs;
        this.println(string + ".__init__(self, *args, **kwargs)");
        this.println("self.tokenNames = _tokenNames");
        this.genHeaderInit(this.grammar);
        --this.tabs;
        this.println("");
        this.printGrammarAction(this.grammar);
        object = this.grammar.rules.elements();
        int n = 0;
        String string3 = "";
        while (object.hasMoreElements()) {
            GrammarSymbol grammarSymbol = (GrammarSymbol)object.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n++);
            }
            this.exitIfError();
        }
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        this.tabs = 0;
        this.genHeaderMain(this.grammar);
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void gen(WildcardElement wildcardElement) {
        if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(wildcardElement.getLabel() + " = " + this.lt1Value + "");
        }
        this.genElementAST(wildcardElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if not _t:");
            ++this.tabs;
            this.println("raise antlr.MismatchedTokenException()");
            --this.tabs;
        } else if (this.grammar instanceof LexerGrammar) {
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("_saveIndex = self.text.length()");
            }
            this.println("self.matchNot(antlr.EOF_CHAR)");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("self.text.setLength(_saveIndex)");
            }
        } else {
            this.println("self.matchNot(" + this.getValueString(1, false) + ")");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling()");
        }
    }

    public void gen(ZeroOrMoreBlock zeroOrMoreBlock) {
        int n = this.tabs++;
        this.genBlockPreamble(zeroOrMoreBlock);
        this.println("while True:");
        n = this.tabs;
        this.genBlockInitAction(zeroOrMoreBlock);
        String string = this.currentASTResult;
        if (zeroOrMoreBlock.getLabel() != null) {
            this.currentASTResult = zeroOrMoreBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(zeroOrMoreBlock);
        boolean bl2 = false;
        int n2 = this.grammar.maxk;
        if (!zeroOrMoreBlock.greedy && zeroOrMoreBlock.exitLookaheadDepth <= this.grammar.maxk && zeroOrMoreBlock.exitCache[zeroOrMoreBlock.exitLookaheadDepth].containsEpsilon()) {
            bl2 = true;
            n2 = zeroOrMoreBlock.exitLookaheadDepth;
        } else if (!zeroOrMoreBlock.greedy && zeroOrMoreBlock.exitLookaheadDepth == Integer.MAX_VALUE) {
            bl2 = true;
        }
        if (bl2) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("nongreedy (...)* loop; exit depth is " + zeroOrMoreBlock.exitLookaheadDepth);
            }
            String string2 = this.getLookaheadTestExpression(zeroOrMoreBlock.exitCache, n2);
            this.println("###  nongreedy exit test");
            this.println("if (" + string2 + "):");
            ++this.tabs;
            this.println("break");
            --this.tabs;
        }
        int n3 = this.tabs;
        PythonBlockFinishingInfo pythonBlockFinishingInfo = this.genCommonBlock(zeroOrMoreBlock, false);
        this.genBlockFinish(pythonBlockFinishingInfo, "break");
        this.tabs = n3;
        this.tabs = n;
        --this.tabs;
        this.currentASTResult = string;
    }

    protected void genAlt(Alternative alternative, AlternativeBlock alternativeBlock) {
        boolean bl = this.genAST;
        this.genAST = this.genAST && alternative.getAutoGen();
        boolean bl2 = this.saveText;
        this.saveText = this.saveText && alternative.getAutoGen();
        Hashtable hashtable = this.treeVariableMap;
        this.treeVariableMap = new Hashtable();
        if (alternative.exceptionSpec != null) {
            this.println("try:");
            ++this.tabs;
        }
        this.println("pass");
        AlternativeElement alternativeElement = alternative.head;
        while (!(alternativeElement instanceof BlockEndElement)) {
            alternativeElement.generate();
            alternativeElement = alternativeElement.next;
        }
        if (this.genAST) {
            if (alternativeBlock instanceof RuleBlock) {
                RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
                if (this.grammar.hasSyntacticPredicate) {
                    // empty if block
                }
                this.println(ruleBlock.getRuleName() + "_AST = currentAST.root");
                if (this.grammar.hasSyntacticPredicate) {
                    // empty if block
                }
            } else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules not yet supported", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
            }
        }
        if (alternative.exceptionSpec != null) {
            --this.tabs;
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
        int n2 = this.tabs;
        this.tabs = 0;
        this.println("");
        this.println("### generate bit set");
        this.println("def mk" + this.getBitsetName(n) + "(): ");
        ++this.tabs;
        int n3 = bitSet.lengthInLongWords();
        if (n3 < 8) {
            this.println("### var1");
            this.println("data = [ " + bitSet.toStringOfWords() + "]");
        } else {
            this.println("data = [0L] * " + n3 + " ### init list");
            long[] lArray = bitSet.toPackedArray();
            int n4 = 0;
            while (n4 < lArray.length) {
                int n5;
                if (lArray[n4] == 0L) {
                    ++n4;
                    continue;
                }
                if (n4 + 1 == lArray.length || lArray[n4] != lArray[n4 + 1]) {
                    this.println("data[" + n4 + "] =" + lArray[n4] + "L");
                    ++n4;
                    continue;
                }
                for (n5 = n4 + 1; n5 < lArray.length && lArray[n5] == lArray[n4]; ++n5) {
                }
                long l = lArray[n4];
                this.println("for x in xrange(" + n4 + ", " + n5 + "):");
                ++this.tabs;
                this.println("data[x] = " + l + "L");
                --this.tabs;
                n4 = n5;
            }
        }
        this.println("return data");
        --this.tabs;
        this.println(this.getBitsetName(n) + " = antlr.BitSet(mk" + this.getBitsetName(n) + "())");
        this.tabs = n2;
    }

    private void genBlockFinish(PythonBlockFinishingInfo pythonBlockFinishingInfo, String string) {
        if (pythonBlockFinishingInfo.needAnErrorClause && (pythonBlockFinishingInfo.generatedAnIf || pythonBlockFinishingInfo.generatedSwitch)) {
            if (pythonBlockFinishingInfo.generatedAnIf) {
                this.println("else:");
            }
            ++this.tabs;
            this.println(string);
            --this.tabs;
        }
        if (pythonBlockFinishingInfo.postscript != null) {
            this.println(pythonBlockFinishingInfo.postscript);
        }
    }

    private void genBlockFinish1(PythonBlockFinishingInfo pythonBlockFinishingInfo, String string) {
        if (pythonBlockFinishingInfo.needAnErrorClause && (pythonBlockFinishingInfo.generatedAnIf || pythonBlockFinishingInfo.generatedSwitch)) {
            if (pythonBlockFinishingInfo.generatedAnIf) {
                this.println("else:");
            }
            ++this.tabs;
            this.println(string);
            --this.tabs;
            if (pythonBlockFinishingInfo.generatedAnIf) {
                // empty if block
            }
        }
        if (pythonBlockFinishingInfo.postscript != null) {
            this.println(pythonBlockFinishingInfo.postscript);
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
                            this.println(alternativeElement.getLabel() + " = " + this.labeledElementInit);
                            if (!this.grammar.buildAST) continue;
                            this.genASTDeclaration(alternativeElement);
                            continue;
                        }
                        if (this.grammar.buildAST) {
                            this.genASTDeclaration(alternativeElement);
                        }
                        if (this.grammar instanceof LexerGrammar) {
                            this.println(alternativeElement.getLabel() + " = None");
                        }
                        if (!(this.grammar instanceof TreeWalkerGrammar)) continue;
                        this.println(alternativeElement.getLabel() + " = " + this.labeledElementInit);
                        continue;
                    }
                    this.println(alternativeElement.getLabel() + " = " + this.labeledElementInit);
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

    protected void genCases(BitSet bitSet) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genCases(" + bitSet + ")");
        }
        int[] nArray = bitSet.toArray();
        int n = this.grammar instanceof LexerGrammar ? 4 : 1;
        boolean bl = true;
        boolean bl2 = true;
        this.print("elif la1 and la1 in ");
        if (this.grammar instanceof LexerGrammar) {
            this._print("u'");
            for (int i = 0; i < nArray.length; ++i) {
                this._print(this.getValueString(nArray[i], false));
            }
            this._print("':\n");
            return;
        }
        this._print("[");
        for (int i = 0; i < nArray.length; ++i) {
            this._print(this.getValueString(nArray[i], false));
            if (i + 1 >= nArray.length) continue;
            this._print(",");
        }
        this._print("]:\n");
    }

    public PythonBlockFinishingInfo genCommonBlock(AlternativeBlock alternativeBlock, boolean bl) {
        int n;
        Object object;
        int n2 = this.tabs;
        int n3 = 0;
        boolean bl2 = false;
        int n4 = 0;
        PythonBlockFinishingInfo pythonBlockFinishingInfo = new PythonBlockFinishingInfo();
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
                this.println(alternativeBlock.getLabel() + " = " + this.lt1Value);
            }
            this.genElementAST(alternativeBlock);
            String string = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                string = "_t, ";
            }
            this.println("self.match(" + string + this.getBitsetName(this.markBitsetForGen(lookahead.fset)) + ")");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t.getNextSibling()");
            }
            return pythonBlockFinishingInfo;
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
                return pythonBlockFinishingInfo;
            }
        }
        int n5 = 0;
        for (int i = 0; i < alternativeBlock.getAlternatives().size(); ++i) {
            Alternative alternative = alternativeBlock.getAlternativeAt(i);
            if (!PythonCodeGenerator.suitableForCaseExpression(alternative)) continue;
            ++n5;
        }
        if (n5 >= this.makeSwitchThreshold) {
            String string = this.lookaheadString(1);
            bl2 = true;
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if not _t:");
                ++this.tabs;
                this.println("_t = antlr.ASTNULL");
                --this.tabs;
            }
            this.println("la1 = " + string);
            this.println("if False:");
            ++this.tabs;
            this.println("pass");
            --this.tabs;
            for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
                Alternative alternative = alternativeBlock.getAlternativeAt(i);
                if (!PythonCodeGenerator.suitableForCaseExpression(alternative)) continue;
                object = alternative.cache[1];
                if (((Lookahead)object).fset.degree() == 0 && !((Lookahead)object).containsEpsilon()) {
                    this.antlrTool.warning("Alternate omitted due to empty prediction set", this.grammar.getFilename(), alternative.head.getLine(), alternative.head.getColumn());
                    continue;
                }
                this.genCases(((Lookahead)object).fset);
                ++this.tabs;
                this.genAlt(alternative, alternativeBlock);
                --this.tabs;
            }
            this.println("else:");
            ++this.tabs;
        }
        for (int i = n = this.grammar instanceof LexerGrammar ? this.grammar.maxk : 0; i >= 0; --i) {
            for (int j = 0; j < alternativeBlock.alternatives.size(); ++j) {
                String string;
                object = alternativeBlock.getAlternativeAt(j);
                if (this.DEBUG_CODE_GENERATOR) {
                    System.out.println("genAlt: " + j);
                }
                if (bl2 && PythonCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                    if (!this.DEBUG_CODE_GENERATOR) continue;
                    System.out.println("ignoring alt because it was in the switch");
                    continue;
                }
                boolean bl6 = false;
                if (this.grammar instanceof LexerGrammar) {
                    int n6 = ((Alternative)object).lookaheadDepth;
                    if (n6 == Integer.MAX_VALUE) {
                        n6 = this.grammar.maxk;
                    }
                    while (n6 >= 1 && ((Alternative)object).cache[n6].containsEpsilon()) {
                        --n6;
                    }
                    if (n6 != i) {
                        if (!this.DEBUG_CODE_GENERATOR) continue;
                        System.out.println("ignoring alt because effectiveDepth!=altDepth" + n6 + "!=" + i);
                        continue;
                    }
                    bl6 = this.lookaheadIsEmpty((Alternative)object, n6);
                    string = this.getLookaheadTestExpression((Alternative)object, n6);
                } else {
                    bl6 = this.lookaheadIsEmpty((Alternative)object, this.grammar.maxk);
                    string = this.getLookaheadTestExpression((Alternative)object, this.grammar.maxk);
                }
                if (((Alternative)object).cache[1].fset.degree() > 127 && PythonCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                    if (n3 == 0) {
                        this.println("<m1> if " + string + ":");
                    } else {
                        this.println("<m2> elif " + string + ":");
                    }
                } else if (bl6 && ((Alternative)object).semPred == null && ((Alternative)object).synPred == null) {
                    if (n3 == 0) {
                        this.println("##<m3> <closing");
                    } else {
                        this.println("else: ## <m4>");
                        ++this.tabs;
                    }
                    pythonBlockFinishingInfo.needAnErrorClause = false;
                } else {
                    if (((Alternative)object).semPred != null) {
                        ActionTransInfo actionTransInfo = new ActionTransInfo();
                        String string2 = this.processActionForSpecialSymbols(((Alternative)object).semPred, alternativeBlock.line, this.currentRule, actionTransInfo);
                        string = (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar) && this.grammar.debuggingOutput ? "(" + string + " and fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.PREDICTING, " + this.addSemPred(this.charFormatter.escapeString(string2)) + ", " + string2 + "))" : "(" + string + " and (" + string2 + "))";
                    }
                    if (n3 > 0) {
                        if (((Alternative)object).synPred != null) {
                            this.println("else:");
                            ++this.tabs;
                            this.genSynPred(((Alternative)object).synPred, string);
                            ++n4;
                        } else {
                            this.println("elif " + string + ":");
                        }
                    } else if (((Alternative)object).synPred != null) {
                        this.genSynPred(((Alternative)object).synPred, string);
                    } else {
                        if (this.grammar instanceof TreeWalkerGrammar) {
                            this.println("if not _t:");
                            ++this.tabs;
                            this.println("_t = antlr.ASTNULL");
                            --this.tabs;
                        }
                        this.println("if " + string + ":");
                    }
                }
                ++n3;
                ++this.tabs;
                this.genAlt((Alternative)object, alternativeBlock);
                --this.tabs;
            }
        }
        String string = "";
        this.genAST = bl3;
        this.saveText = bl4;
        if (bl2) {
            pythonBlockFinishingInfo.postscript = string;
            pythonBlockFinishingInfo.generatedSwitch = true;
            pythonBlockFinishingInfo.generatedAnIf = n3 > 0;
        } else {
            pythonBlockFinishingInfo.postscript = string;
            pythonBlockFinishingInfo.generatedSwitch = false;
            pythonBlockFinishingInfo.generatedAnIf = n3 > 0;
        }
        return pythonBlockFinishingInfo;
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
                this.println(string2 + "_in = " + string);
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
                this.println((String)object + "_in = None");
            }
            if (bl) {
                // empty if block
            }
            if (alternativeElement.getLabel() != null) {
                if (alternativeElement instanceof GrammarAtom) {
                    this.println((String)object + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, string3) + "");
                } else {
                    this.println((String)object + " = " + this.getASTCreateString(string3) + "");
                }
            }
            if (alternativeElement.getLabel() == null && bl2) {
                string3 = this.lt1Value;
                if (alternativeElement instanceof GrammarAtom) {
                    this.println((String)object + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, string3) + "");
                } else {
                    this.println((String)object + " = " + this.getASTCreateString(string3) + "");
                }
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println((String)object + "_in = " + string3 + "");
                }
            }
            if (this.genAST) {
                switch (alternativeElement.getAutoGenType()) {
                    case 1: {
                        this.println("self.addASTChild(currentAST, " + (String)object + ")");
                        break;
                    }
                    case 2: {
                        this.println("self.makeASTRoot(currentAST, " + (String)object + ")");
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
            this.genErrorHandler(exceptionSpec);
        }
    }

    private void genErrorHandler(ExceptionSpec exceptionSpec) {
        for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
            ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
            String string = "";
            String string2 = "";
            String string3 = exceptionHandler.exceptionTypeAndName.getText();
            string3 = this.removeAssignmentFromDeclaration(string3);
            string3 = string3.trim();
            for (int j = string3.length() - 1; j >= 0; --j) {
                if (Character.isLetterOrDigit(string3.charAt(j)) || string3.charAt(j) == '_') continue;
                string = string3.substring(0, j);
                string2 = string3.substring(j + 1);
                break;
            }
            this.println("except " + string + ", " + string2 + ":");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if not self.inputState.guessing:");
                ++this.tabs;
            }
            ActionTransInfo actionTransInfo = new ActionTransInfo();
            this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, actionTransInfo));
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("else:");
                ++this.tabs;
                this.println("raise " + string2);
                --this.tabs;
            }
            --this.tabs;
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
            this.println("try: # for error handling");
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
        this.println(string + "_AST = None");
        this.declaredASTVariables.put(alternativeElement, alternativeElement);
    }

    protected void genHeader() {
        this.println("### $ANTLR " + Tool.version + ": " + "\"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"" + " -> " + "\"" + this.grammar.getClassName() + ".py\"$");
    }

    protected void genLexerTest() {
        String string = this.grammar.getClassName();
        this.println("if __name__ == '__main__' :");
        ++this.tabs;
        this.println("import sys");
        this.println("import antlr");
        this.println("import " + string);
        this.println("");
        this.println("### create lexer - shall read from stdin");
        this.println("try:");
        ++this.tabs;
        this.println("for token in " + string + ".Lexer():");
        ++this.tabs;
        this.println("print token");
        this.println("");
        --this.tabs;
        --this.tabs;
        this.println("except antlr.TokenStreamException, e:");
        ++this.tabs;
        this.println("print \"error: exception caught while lexing: \", e");
        --this.tabs;
        --this.tabs;
    }

    private void genLiteralsTest() {
        this.println("### option { testLiterals=true } ");
        this.println("_ttype = self.testLiteralsTable(_ttype)");
    }

    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = self.testLiteralsTable(self.text.getString(), _begin, self.text.length()-_begin, _ttype)");
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
            string = "_t,";
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("_saveIndex = self.text.length()");
        }
        this.print(grammarAtom.not ? "self.matchNot(" : "self.match(");
        this._print(string);
        if (grammarAtom.atomText.equals("EOF")) {
            this._print("EOF_TYPE");
        } else {
            this._print(grammarAtom.atomText);
        }
        this._println(")");
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("self.text.setLength(_saveIndex)");
        }
    }

    protected void genMatchUsingAtomTokenType(GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            string = "_t,";
        }
        Object var3_3 = null;
        String string2 = string + this.getValueString(grammarAtom.getType(), true);
        this.println((grammarAtom.not ? "self.matchNot(" : "self.match(") + string2 + ")");
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
            this.println("def nextToken(self): ");
            ++this.tabs;
            this.println("try:");
            ++this.tabs;
            this.println("self.uponEOF()");
            --this.tabs;
            this.println("except antlr.CharStreamIOException, csioe:");
            ++this.tabs;
            this.println("raise antlr.TokenStreamIOException(csioe.io)");
            --this.tabs;
            this.println("except antlr.CharStreamException, cse:");
            ++this.tabs;
            this.println("raise antlr.TokenStreamException(str(cse))");
            --this.tabs;
            this.println("return antlr.CommonToken(type=EOF_TYPE, text=\"\")");
            --this.tabs;
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
        this.println("def nextToken(self):");
        ++this.tabs;
        this.println("while True:");
        ++this.tabs;
        this.println("try: ### try again ..");
        ++this.tabs;
        this.println("while True:");
        ++this.tabs;
        int n = this.tabs;
        this.println("_token = None");
        this.println("_ttype = INVALID_TYPE");
        if (((LexerGrammar)this.grammar).filterMode) {
            this.println("self.setCommitToPath(False)");
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
                this.println("_m = self.mark()");
            }
        }
        this.println("self.resetText()");
        this.println("try: ## for char stream error handling");
        ++this.tabs;
        n = this.tabs++;
        this.println("try: ##for lexical error handling");
        n = this.tabs;
        for (int i = 0; i < ruleBlock.getAlternatives().size(); ++i) {
            object2 = ruleBlock.getAlternativeAt(i);
            if (!((Alternative)object2).cache[1].containsEpsilon()) continue;
            object = (RuleRefElement)((Alternative)object2).head;
            String string2 = CodeGenerator.decodeLexerRuleName(((RuleRefElement)object).targetRule);
            this.antlrTool.warning("public lexical rule " + string2 + " is optional (can match \"nothing\")");
        }
        String string3 = System.getProperty("line.separator");
        object2 = this.genCommonBlock(ruleBlock, false);
        object = "";
        object = ((LexerGrammar)this.grammar).filterMode ? (string == null ? (String)object + "self.filterdefault(self.LA(1))" : (String)object + "self.filterdefault(self.LA(1), self.m" + string + ", False)") : "self.default(self.LA(1))";
        this.genBlockFinish1((PythonBlockFinishingInfo)object2, (String)object);
        this.tabs = n;
        if (((LexerGrammar)this.grammar).filterMode && string != null) {
            this.println("self.commit()");
        }
        this.println("if not self._returnToken:");
        ++this.tabs;
        this.println("raise antlr.TryAgain ### found SKIP token");
        --this.tabs;
        if (((LexerGrammar)this.grammar).getTestLiterals()) {
            this.println("### option { testLiterals=true } ");
            this.println("self.testForLiteral(self._returnToken)");
        }
        this.println("### return token to caller");
        this.println("return self._returnToken");
        --this.tabs;
        this.println("### handle lexical errors ....");
        this.println("except antlr.RecognitionException, e:");
        ++this.tabs;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (string == null) {
                this.println("if not self.getCommitToPath():");
                ++this.tabs;
                this.println("self.consume()");
                this.println("raise antlr.TryAgain()");
                --this.tabs;
            } else {
                this.println("if not self.getCommitToPath(): ");
                ++this.tabs;
                this.println("self.rewind(_m)");
                this.println("self.resetText()");
                this.println("try:");
                ++this.tabs;
                this.println("self.m" + string + "(False)");
                --this.tabs;
                this.println("except antlr.RecognitionException, ee:");
                ++this.tabs;
                this.println("### horrendous failure: error in filter rule");
                this.println("self.reportError(ee)");
                this.println("self.consume()");
                --this.tabs;
                this.println("raise antlr.TryAgain()");
                --this.tabs;
            }
        }
        if (ruleBlock.getDefaultErrorHandler()) {
            this.println("self.reportError(e)");
            this.println("self.consume()");
        } else {
            this.println("raise antlr.TokenStreamRecognitionException(e)");
        }
        --this.tabs;
        --this.tabs;
        this.println("### handle char stream errors ...");
        this.println("except antlr.CharStreamException,cse:");
        ++this.tabs;
        this.println("if isinstance(cse, antlr.CharStreamIOException):");
        ++this.tabs;
        this.println("raise antlr.TokenStreamIOException(cse.io)");
        --this.tabs;
        this.println("else:");
        ++this.tabs;
        this.println("raise antlr.TokenStreamException(str(cse))");
        --this.tabs;
        --this.tabs;
        --this.tabs;
        --this.tabs;
        this.println("except antlr.TryAgain:");
        ++this.tabs;
        this.println("pass");
        --this.tabs;
        --this.tabs;
    }

    public void genRule(RuleSymbol ruleSymbol, boolean bl, int n) {
        Object object;
        Object object2;
        RuleBlock ruleBlock;
        this.tabs = 1;
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
        this.genJavadocComment(ruleSymbol);
        this.print("def " + ruleSymbol.getId() + "(");
        this._print(this.commonExtraParams);
        if (this.commonExtraParams.length() != 0 && ruleBlock.argAction != null) {
            this._print(",");
        }
        if (ruleBlock.argAction != null) {
            this._println("");
            ++this.tabs;
            this.println(ruleBlock.argAction);
            --this.tabs;
            this.print("):");
        } else {
            this._print("):");
        }
        this.println("");
        ++this.tabs;
        if (ruleBlock.returnAction != null) {
            if (ruleBlock.returnAction.indexOf(61) >= 0) {
                this.println(ruleBlock.returnAction);
            } else {
                this.println(this.extractIdOfAction(ruleBlock.returnAction, ruleBlock.getLine(), ruleBlock.getColumn()) + " = None");
            }
        }
        this.println(this.commonLocalVars);
        if (this.grammar.traceRules) {
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("self.traceIn(\"" + ruleSymbol.getId() + "\",_t)");
            } else {
                this.println("self.traceIn(\"" + ruleSymbol.getId() + "\")");
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            if (ruleSymbol.getId().equals("mEOF")) {
                this.println("_ttype = EOF_TYPE");
            } else {
                this.println("_ttype = " + ruleSymbol.getId().substring(1));
            }
            this.println("_saveIndex = 0");
        }
        if (this.grammar.debuggingOutput) {
            if (this.grammar instanceof ParserGrammar) {
                this.println("self.fireEnterRule(" + n + ", 0)");
            } else if (this.grammar instanceof LexerGrammar) {
                this.println("self.fireEnterRule(" + n + ", _ttype)");
            }
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            this.println("try: ### debugging");
            ++this.tabs;
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(ruleSymbol.getId() + "_AST_in = None");
            this.println("if _t != antlr.ASTNULL:");
            ++this.tabs;
            this.println(ruleSymbol.getId() + "_AST_in = _t");
            --this.tabs;
        }
        if (this.grammar.buildAST) {
            this.println("self.returnAST = None");
            this.println("currentAST = antlr.ASTPair()");
            this.println(ruleSymbol.getId() + "_AST = None");
        }
        this.genBlockPreamble(ruleBlock);
        this.genBlockInitAction(ruleBlock);
        ExceptionSpec exceptionSpec = ruleBlock.findExceptionSpec("");
        if (exceptionSpec != null || ruleBlock.getDefaultErrorHandler()) {
            this.println("try:      ## for error handling");
            ++this.tabs;
        }
        int n2 = this.tabs;
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
            this.genBlockFinish((PythonBlockFinishingInfo)object, this.throwNoViable);
        }
        this.tabs = n2;
        if (exceptionSpec != null || ruleBlock.getDefaultErrorHandler()) {
            --this.tabs;
            this.println("");
        }
        if (exceptionSpec != null) {
            this.genErrorHandler(exceptionSpec);
        } else if (ruleBlock.getDefaultErrorHandler()) {
            this.println("except " + this.exceptionThrown + ", ex:");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if not self.inputState.guessing:");
                ++this.tabs;
            }
            this.println("self.reportError(ex)");
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                object2 = this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode);
                object = this.getBitsetName(this.markBitsetForGen(((Lookahead)object2).fset));
                this.println("self.consume()");
                this.println("self.consumeUntil(" + (String)object + ")");
            } else {
                this.println("if _t:");
                ++this.tabs;
                this.println("_t = _t.getNextSibling()");
                --this.tabs;
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("else:");
                ++this.tabs;
                this.println("raise ex");
                --this.tabs;
            }
            --this.tabs;
            this.println("");
        }
        if (this.grammar.buildAST) {
            this.println("self.returnAST = " + ruleSymbol.getId() + "_AST");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("self._retTree = _t");
        }
        if (ruleBlock.getTestLiterals()) {
            if (ruleSymbol.access.equals("protected")) {
                this.genLiteralsTestForPartialToken();
            } else {
                this.genLiteralsTest();
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            this.println("self.set_return_token(_createToken, _token, _ttype, _begin)");
        }
        if (ruleBlock.returnAction != null) {
            this.println("return " + this.extractIdOfAction(ruleBlock.returnAction, ruleBlock.getLine(), ruleBlock.getColumn()) + "");
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            --this.tabs;
            this.println("finally:  ### debugging");
            ++this.tabs;
            if (this.grammar.debuggingOutput) {
                if (this.grammar instanceof ParserGrammar) {
                    this.println("self.fireExitRule(" + n + ", 0)");
                } else if (this.grammar instanceof LexerGrammar) {
                    this.println("self.fireExitRule(" + n + ", _ttype)");
                }
            }
            if (this.grammar.traceRules) {
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("self.traceOut(\"" + ruleSymbol.getId() + "\", _t)");
                } else {
                    this.println("self.traceOut(\"" + ruleSymbol.getId() + "\")");
                }
            }
            --this.tabs;
        }
        --this.tabs;
        this.println("");
        this.genAST = bl2;
    }

    private void GenRuleInvocation(RuleRefElement ruleRefElement) {
        this._print("self." + ruleRefElement.targetRule + "(");
        if (this.grammar instanceof LexerGrammar) {
            if (ruleRefElement.getLabel() != null) {
                this._print("True");
            } else {
                this._print("False");
            }
            if (this.commonExtraArgs.length() != 0 || ruleRefElement.args != null) {
                this._print(", ");
            }
        }
        this._print(this.commonExtraArgs);
        if (this.commonExtraArgs.length() != 0 && ruleRefElement.args != null) {
            this._print(", ");
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
        this._println(")");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = self._retTree");
        }
    }

    protected void genSemPred(String string, int n) {
        ActionTransInfo actionTransInfo = new ActionTransInfo();
        string = this.processActionForSpecialSymbols(string, n, this.currentRule, actionTransInfo);
        String string2 = this.charFormatter.escapeString(string);
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            string = "fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.VALIDATING," + this.addSemPred(string2) + ", " + string + ")";
        }
        this.println("if not " + string + ":");
        ++this.tabs;
        this.println("raise antlr.SemanticException(\"" + string2 + "\")");
        --this.tabs;
    }

    protected void genSemPredMap() {
        Enumeration enumeration = this.semPreds.elements();
        this.println("_semPredNames = [");
        ++this.tabs;
        while (enumeration.hasMoreElements()) {
            this.println("\"" + enumeration.nextElement() + "\",");
        }
        --this.tabs;
        this.println("]");
    }

    protected void genSynPred(SynPredBlock synPredBlock, String string) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen=>(" + synPredBlock + ")");
        }
        this.println("synPredMatched" + synPredBlock.ID + " = False");
        this.println("if " + string + ":");
        ++this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t" + synPredBlock.ID + " = _t");
        } else {
            this.println("_m" + synPredBlock.ID + " = self.mark()");
        }
        this.println("synPredMatched" + synPredBlock.ID + " = True");
        this.println("self.inputState.guessing += 1");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("self.fireSyntacticPredicateStarted()");
        }
        ++this.syntacticPredLevel;
        this.println("try:");
        ++this.tabs;
        this.gen(synPredBlock);
        --this.tabs;
        this.println("except " + this.exceptionThrown + ", pe:");
        ++this.tabs;
        this.println("synPredMatched" + synPredBlock.ID + " = False");
        --this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t" + synPredBlock.ID + "");
        } else {
            this.println("self.rewind(_m" + synPredBlock.ID + ")");
        }
        this.println("self.inputState.guessing -= 1");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("if synPredMatched" + synPredBlock.ID + ":");
            ++this.tabs;
            this.println("self.fireSyntacticPredicateSucceeded()");
            --this.tabs;
            this.println("else:");
            ++this.tabs;
            this.println("self.fireSyntacticPredicateFailed()");
            --this.tabs;
        }
        --this.syntacticPredLevel;
        --this.tabs;
        this.println("if synPredMatched" + synPredBlock.ID + ":");
    }

    public void genTokenStrings() {
        int n = this.tabs;
        this.tabs = 0;
        this.println("");
        this.println("_tokenNames = [");
        ++this.tabs;
        Vector vector = this.grammar.tokenManager.getVocabulary();
        for (int i = 0; i < vector.size(); ++i) {
            TokenSymbol tokenSymbol;
            String string = (String)vector.elementAt(i);
            if (string == null) {
                string = "<" + String.valueOf(i) + ">";
            }
            if (!string.startsWith("\"") && !string.startsWith("<") && (tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string)) != null && tokenSymbol.getParaphrase() != null) {
                string = StringUtils.stripFrontBack(tokenSymbol.getParaphrase(), "\"", "\"");
            }
            this.print(this.charFormatter.literalString(string));
            if (i != vector.size() - 1) {
                this._print(", ");
            }
            this._println("");
        }
        --this.tabs;
        this.println("]");
        this.tabs = n;
    }

    protected void genTokenASTNodeMap() {
        this.println("");
        this.println("def buildTokenTypeASTClassMap(self):");
        ++this.tabs;
        boolean bl = false;
        int n = 0;
        Vector vector = this.grammar.tokenManager.getVocabulary();
        for (int i = 0; i < vector.size(); ++i) {
            TokenSymbol tokenSymbol;
            String string = (String)vector.elementAt(i);
            if (string == null || (tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string)) == null || tokenSymbol.getASTNodeType() == null) continue;
            ++n;
            if (!bl) {
                this.println("self.tokenTypeToASTClassMap = {}");
                bl = true;
            }
            this.println("self.tokenTypeToASTClassMap[" + tokenSymbol.getTokenType() + "] = " + tokenSymbol.getASTNodeType());
        }
        if (n == 0) {
            this.println("self.tokenTypeToASTClassMap = None");
        }
        --this.tabs;
    }

    protected void genTokenTypes(TokenManager tokenManager) throws IOException {
        this.tabs = 0;
        Vector vector = tokenManager.getVocabulary();
        this.println("SKIP                = antlr.SKIP");
        this.println("INVALID_TYPE        = antlr.INVALID_TYPE");
        this.println("EOF_TYPE            = antlr.EOF_TYPE");
        this.println("EOF                 = antlr.EOF");
        this.println("NULL_TREE_LOOKAHEAD = antlr.NULL_TREE_LOOKAHEAD");
        this.println("MIN_USER_TYPE       = antlr.MIN_USER_TYPE");
        for (int i = 4; i < vector.size(); ++i) {
            String string = (String)vector.elementAt(i);
            if (string == null) continue;
            if (string.startsWith("\"")) {
                StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(string);
                if (stringLiteralSymbol == null) {
                    this.antlrTool.panic("String literal " + string + " not in symbol table");
                }
                if (stringLiteralSymbol.label != null) {
                    this.println(stringLiteralSymbol.label + " = " + i);
                    continue;
                }
                String string2 = this.mangleLiteral(string);
                if (string2 != null) {
                    this.println(string2 + " = " + i);
                    stringLiteralSymbol.label = string2;
                    continue;
                }
                this.println("### " + string + " = " + i);
                continue;
            }
            if (string.startsWith("<")) continue;
            this.println(string + " = " + i);
        }
        --this.tabs;
        this.exitIfError();
    }

    public String getASTCreateString(Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("antlr.make(");
        for (int i = 0; i < vector.size(); ++i) {
            stringBuffer.append(vector.elementAt(i));
            if (i + 1 >= vector.size()) continue;
            stringBuffer.append(", ");
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    public String getASTCreateString(GrammarAtom grammarAtom, String string) {
        if (grammarAtom != null && grammarAtom.getASTNodeType() != null) {
            return "self.astFactory.create(" + string + ", " + grammarAtom.getASTNodeType() + ")";
        }
        return this.getASTCreateString(string);
    }

    public String getASTCreateString(String string) {
        int n;
        if (string == null) {
            string = "";
        }
        int n2 = 0;
        for (n = 0; n < string.length(); ++n) {
            if (string.charAt(n) != ',') continue;
            ++n2;
        }
        if (n2 < 2) {
            TokenSymbol tokenSymbol;
            n = string.indexOf(44);
            int n3 = string.lastIndexOf(44);
            String string2 = string;
            if (n2 > 0) {
                string2 = string.substring(0, n);
            }
            if ((tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string2)) != null) {
                String string3 = tokenSymbol.getASTNodeType();
                String string4 = "";
                if (n2 == 0) {
                    string4 = ", \"\"";
                }
                if (string3 != null) {
                    return "self.astFactory.create(" + string + string4 + ", " + string3 + ")";
                }
            }
            if (this.labeledElementASTType.equals("AST")) {
                return "self.astFactory.create(" + string + ")";
            }
            return "self.astFactory.create(" + string + ")";
        }
        return "self.astFactory.create(" + string + ")";
    }

    protected String getLookaheadTestExpression(Lookahead[] lookaheadArray, int n) {
        StringBuffer stringBuffer = new StringBuffer(100);
        boolean bl = true;
        stringBuffer.append("(");
        for (int i = 1; i <= n; ++i) {
            BitSet bitSet = lookaheadArray[i].fset;
            if (!bl) {
                stringBuffer.append(") and (");
            }
            bl = false;
            if (lookaheadArray[i].containsEpsilon()) {
                stringBuffer.append("True");
                continue;
            }
            stringBuffer.append(this.getLookaheadTestTerm(i, bitSet));
        }
        stringBuffer.append(")");
        String string = stringBuffer.toString();
        return string;
    }

    protected String getLookaheadTestExpression(Alternative alternative, int n) {
        int n2 = alternative.lookaheadDepth;
        if (n2 == Integer.MAX_VALUE) {
            n2 = this.grammar.maxk;
        }
        if (n == 0) {
            return "True";
        }
        return this.getLookaheadTestExpression(alternative.cache, n2);
    }

    protected String getLookaheadTestTerm(int n, BitSet bitSet) {
        String string = this.lookaheadString(n);
        int[] nArray = bitSet.toArray();
        if (PythonCodeGenerator.elementsAreRange(nArray)) {
            String string2 = this.getRangeExpression(n, nArray);
            return string2;
        }
        int n2 = bitSet.degree();
        if (n2 == 0) {
            return "True";
        }
        if (n2 >= this.bitsetTestThreshold) {
            int n3 = this.markBitsetForGen(bitSet);
            return this.getBitsetName(n3) + ".member(" + string + ")";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < nArray.length; ++i) {
            String string3 = this.getValueString(nArray[i], true);
            if (i > 0) {
                stringBuffer.append(" or ");
            }
            stringBuffer.append(string);
            stringBuffer.append("==");
            stringBuffer.append(string3);
        }
        String string4 = stringBuffer.toString();
        return stringBuffer.toString();
    }

    public String getRangeExpression(int n, int[] nArray) {
        if (!PythonCodeGenerator.elementsAreRange(nArray)) {
            this.antlrTool.panic("getRangeExpression called with non-range");
        }
        int n2 = nArray[0];
        int n3 = nArray[nArray.length - 1];
        return "(" + this.lookaheadString(n) + " >= " + this.getValueString(n2, true) + " and " + this.lookaheadString(n) + " <= " + this.getValueString(n3, true) + ")";
    }

    private String getValueString(int n, boolean bl) {
        String string;
        if (this.grammar instanceof LexerGrammar) {
            String string2 = this.charFormatter.literalChar(n);
            if (bl) {
                string2 = "u'" + string2 + "'";
            }
            return string2;
        }
        TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbolAt(n);
        if (tokenSymbol == null) {
            String string3 = "" + n;
            return string3;
        }
        String string4 = tokenSymbol.getId();
        if (!(tokenSymbol instanceof StringLiteralSymbol)) {
            String string5 = string4;
            return string5;
        }
        StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenSymbol;
        String string6 = stringLiteralSymbol.getLabel();
        if (string6 != null) {
            string = string6;
        } else {
            string = this.mangleLiteral(string4);
            if (string == null) {
                string = String.valueOf(n);
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
            return "_t.getType()";
        }
        return "self.LA(" + n + ")";
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
        if (PythonCodeGenerator.isEmpty(string)) {
            return "";
        }
        if (this.grammar == null) {
            return string;
        }
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
        }
        catch (TokenStreamException tokenStreamException) {
            this.antlrTool.panic("Error reading action:" + string);
        }
        catch (CharStreamException charStreamException) {
            this.antlrTool.panic("Error reading action:" + string);
        }
        return string;
    }

    static boolean isEmpty(String string) {
        boolean bl = true;
        block3: for (int i = 0; bl && i < string.length(); ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    continue block3;
                }
                default: {
                    bl = false;
                }
            }
        }
        return bl;
    }

    protected String processActionCode(String string, int n) {
        if (string == null || PythonCodeGenerator.isEmpty(string)) {
            return "";
        }
        CodeLexer codeLexer = new CodeLexer(string, this.grammar.getFilename(), n, this.antlrTool);
        try {
            codeLexer.mACTION(true);
            string = codeLexer.getTokenObject().getText();
        }
        catch (RecognitionException recognitionException) {
            codeLexer.reportError(recognitionException);
        }
        catch (TokenStreamException tokenStreamException) {
            this.antlrTool.panic("Error reading action:" + string);
        }
        catch (CharStreamException charStreamException) {
            this.antlrTool.panic("Error reading action:" + string);
        }
        return string;
    }

    protected void printActionCode(String string, int n) {
        string = this.processActionCode(string, n);
        this.printAction(string);
    }

    private void setupGrammarParameters(Grammar grammar) {
        if (grammar instanceof ParserGrammar) {
            String string;
            Token token;
            this.labeledElementASTType = "";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.labeledElementASTType = string;
            }
            this.labeledElementType = "";
            this.labeledElementInit = "None";
            this.commonExtraArgs = "";
            this.commonExtraParams = "self";
            this.commonLocalVars = "";
            this.lt1Value = "self.LT(1)";
            this.exceptionThrown = "antlr.RecognitionException";
            this.throwNoViable = "raise antlr.NoViableAltException(self.LT(1), self.getFilename())";
            this.parserClassName = "Parser";
            if (grammar.hasOption("className") && (token = grammar.getOption("className")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.parserClassName = string;
            }
            return;
        }
        if (grammar instanceof LexerGrammar) {
            String string;
            Token token;
            this.labeledElementType = "char ";
            this.labeledElementInit = "'\\0'";
            this.commonExtraArgs = "";
            this.commonExtraParams = "self, _createToken";
            this.commonLocalVars = "_ttype = 0\n        _token = None\n        _begin = self.text.length()";
            this.lt1Value = "self.LA(1)";
            this.exceptionThrown = "antlr.RecognitionException";
            this.throwNoViable = "self.raise_NoViableAlt(self.LA(1))";
            this.lexerClassName = "Lexer";
            if (grammar.hasOption("className") && (token = grammar.getOption("className")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.lexerClassName = string;
            }
            return;
        }
        if (grammar instanceof TreeWalkerGrammar) {
            String string;
            Token token;
            this.labeledElementASTType = "";
            this.labeledElementType = "";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.labeledElementASTType = string;
                this.labeledElementType = string;
            }
            if (!grammar.hasOption("ASTLabelType")) {
                grammar.setOption("ASTLabelType", new Token(6, "<4>AST"));
            }
            this.labeledElementInit = "None";
            this.commonExtraArgs = "_t";
            this.commonExtraParams = "self, _t";
            this.commonLocalVars = "";
            this.lt1Value = "_t";
            this.exceptionThrown = "antlr.RecognitionException";
            this.throwNoViable = "raise antlr.NoViableAltException(_t)";
            this.treeWalkerClassName = "Walker";
            if (grammar.hasOption("className") && (token = grammar.getOption("className")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.treeWalkerClassName = string;
            }
            return;
        }
        this.antlrTool.panic("Unknown grammar type");
    }

    public void setupOutput(String string) throws IOException {
        this.currentOutput = this.antlrTool.openOutputFile(string + ".py");
    }

    protected boolean isspace(char c) {
        boolean bl = true;
        switch (c) {
            case '\t': 
            case '\n': 
            case '\r': 
            case ' ': {
                break;
            }
            default: {
                bl = false;
            }
        }
        return bl;
    }

    protected void _printAction(String string) {
        char c;
        if (string == null) {
            return;
        }
        int n = 0;
        int n2 = string.length();
        int n3 = 0;
        boolean bl = true;
        block15: while (n < n2 && bl) {
            c = string.charAt(n++);
            switch (c) {
                case '\n': {
                    n3 = n;
                    continue block15;
                }
                case '\r': {
                    if (n > n2 || string.charAt(n) == '\n') {
                        // empty if block
                    }
                    n3 = ++n;
                    continue block15;
                }
                case ' ': {
                    continue block15;
                }
            }
            bl = false;
        }
        if (!bl) {
            --n;
        }
        n3 = n - n3;
        --n2;
        while (n2 > n && this.isspace(string.charAt(n2))) {
            --n2;
        }
        boolean bl2 = false;
        block17: for (int i = n; i <= n2; ++i) {
            c = string.charAt(i);
            switch (c) {
                case '\n': {
                    bl2 = true;
                    break;
                }
                case '\r': {
                    bl2 = true;
                    if (i + 1 > n2 || string.charAt(i + 1) != '\n') break;
                    ++i;
                    break;
                }
                case '\t': {
                    System.err.println("warning: tab characters used in Python action");
                    this.currentOutput.print("        ");
                    break;
                }
                case ' ': {
                    this.currentOutput.print(" ");
                    break;
                }
                default: {
                    this.currentOutput.print(c);
                }
            }
            if (!bl2) continue;
            this.currentOutput.print("\n");
            this.printTabs();
            int n4 = 0;
            bl2 = false;
            ++i;
            while (i <= n2) {
                c = string.charAt(i);
                if (!this.isspace(c)) {
                    --i;
                    continue block17;
                }
                switch (c) {
                    case '\n': {
                        bl2 = true;
                        break;
                    }
                    case '\r': {
                        if (i + 1 <= n2 && string.charAt(i + 1) == '\n') {
                            ++i;
                        }
                        bl2 = true;
                    }
                }
                if (bl2) {
                    this.currentOutput.print("\n");
                    this.printTabs();
                    n4 = 0;
                    bl2 = false;
                } else {
                    if (n4 >= n3) continue block17;
                    ++n4;
                }
                ++i;
            }
        }
        this.currentOutput.println();
    }

    protected void od(String string, int n, int n2, String string2) {
        System.out.println(string2);
        block5: for (int i = n; i <= n2; ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '\n': {
                    System.out.print(" nl ");
                    continue block5;
                }
                case '\t': {
                    System.out.print(" ht ");
                    continue block5;
                }
                case ' ': {
                    System.out.print(" sp ");
                    continue block5;
                }
                default: {
                    System.out.print(" " + c + " ");
                }
            }
        }
        System.out.println("");
    }

    protected void printAction(String string) {
        if (string != null) {
            this.printTabs();
            this._printAction(string);
        }
    }

    protected void printGrammarAction(Grammar grammar) {
        this.println("### user action >>>");
        this.printAction(this.processActionForSpecialSymbols(grammar.classMemberAction.getText(), grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("### user action <<<");
    }

    protected void _printJavadoc(String string) {
        int n = string.length();
        int n2 = 0;
        boolean bl = false;
        this.currentOutput.print("\n");
        this.printTabs();
        this.currentOutput.print("###");
        for (int i = n2; i < n; ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '\n': {
                    bl = true;
                    break;
                }
                case '\r': {
                    bl = true;
                    if (i + 1 > n || string.charAt(i + 1) != '\n') break;
                    ++i;
                    break;
                }
                case '\t': {
                    this.currentOutput.print("\t");
                    break;
                }
                case ' ': {
                    this.currentOutput.print(" ");
                    break;
                }
                default: {
                    this.currentOutput.print(c);
                }
            }
            if (!bl) continue;
            this.currentOutput.print("\n");
            this.printTabs();
            this.currentOutput.print("###");
            bl = false;
        }
        this.currentOutput.println();
    }

    protected void genJavadocComment(Grammar grammar) {
        if (grammar.comment != null) {
            this._printJavadoc(grammar.comment);
        }
    }

    protected void genJavadocComment(RuleSymbol ruleSymbol) {
        if (ruleSymbol.comment != null) {
            this._printJavadoc(ruleSymbol.comment);
        }
    }
}

