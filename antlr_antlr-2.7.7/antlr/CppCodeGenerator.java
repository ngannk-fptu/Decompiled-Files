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
import antlr.CppBlockFinishingInfo;
import antlr.CppCharFormatter;
import antlr.ExceptionHandler;
import antlr.ExceptionSpec;
import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.GrammarElement;
import antlr.GrammarSymbol;
import antlr.LexerGrammar;
import antlr.Lookahead;
import antlr.MakeGrammar;
import antlr.NameSpace;
import antlr.OneOrMoreBlock;
import antlr.ParserGrammar;
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
import antlr.WildcardElement;
import antlr.ZeroOrMoreBlock;
import antlr.actions.cpp.ActionLexer;
import antlr.collections.impl.BitSet;
import antlr.collections.impl.Vector;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class CppCodeGenerator
extends CodeGenerator {
    boolean DEBUG_CPP_CODE_GENERATOR = false;
    protected int syntacticPredLevel = 0;
    protected boolean genAST = false;
    protected boolean saveText = false;
    protected boolean genHashLines = true;
    protected boolean noConstructors = false;
    protected int outputLine;
    protected String outputFile;
    boolean usingCustomAST = false;
    String labeledElementType;
    String labeledElementASTType;
    String labeledElementASTInit;
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
    private Vector astTypes;
    private static String namespaceStd = "ANTLR_USE_NAMESPACE(std)";
    private static String namespaceAntlr = "ANTLR_USE_NAMESPACE(antlr)";
    private static NameSpace nameSpace = null;
    private static final String preIncludeCpp = "pre_include_cpp";
    private static final String preIncludeHpp = "pre_include_hpp";
    private static final String postIncludeCpp = "post_include_cpp";
    private static final String postIncludeHpp = "post_include_hpp";

    public CppCodeGenerator() {
        this.charFormatter = new CppCharFormatter();
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

    protected int countLines(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); ++i) {
            if (string.charAt(i) != '\n') continue;
            ++n;
        }
        return n;
    }

    protected void _print(String string) {
        if (string != null) {
            this.outputLine += this.countLines(string);
            this.currentOutput.print(string);
        }
    }

    protected void _printAction(String string) {
        if (string != null) {
            this.outputLine += this.countLines(string) + 1;
            super._printAction(string);
        }
    }

    public void printAction(Token token) {
        if (token != null) {
            this.genLineNo(token.getLine());
            this.printTabs();
            this._printAction(this.processActionForSpecialSymbols(token.getText(), token.getLine(), null, null));
            this.genLineNo2();
        }
    }

    public void printHeaderAction(String string) {
        Token token = (Token)this.behavior.headerActions.get(string);
        if (token != null) {
            this.genLineNo(token.getLine());
            this.println(this.processActionForSpecialSymbols(token.getText(), token.getLine(), null, null));
            this.genLineNo2();
        }
    }

    protected void _println(String string) {
        if (string != null) {
            this.outputLine += this.countLines(string) + 1;
            this.currentOutput.println(string);
        }
    }

    protected void println(String string) {
        if (string != null) {
            this.printTabs();
            this.outputLine += this.countLines(string) + 1;
            this.currentOutput.println(string);
        }
    }

    public void genLineNo(int n) {
        if (n == 0) {
            ++n;
        }
        if (this.genHashLines) {
            this._println("#line " + n + " \"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"");
        }
    }

    public void genLineNo(GrammarElement grammarElement) {
        if (grammarElement != null) {
            this.genLineNo(grammarElement.getLine());
        }
    }

    public void genLineNo(Token token) {
        if (token != null) {
            this.genLineNo(token.getLine());
        }
    }

    public void genLineNo2() {
        if (this.genHashLines) {
            this._println("#line " + (this.outputLine + 1) + " \"" + this.outputFile + "\"");
        }
    }

    private boolean charIsDigit(String string, int n) {
        return n < string.length() && Character.isDigit(string.charAt(n));
    }

    private String convertJavaToCppString(String string, boolean bl) {
        String string2 = new String();
        String string3 = string;
        int n = 0;
        int n2 = 0;
        if (bl) {
            if (!string.startsWith("'") || !string.endsWith("'")) {
                this.antlrTool.error("Invalid character literal: '" + string + "'");
            }
        } else if (!string.startsWith("\"") || !string.endsWith("\"")) {
            this.antlrTool.error("Invalid character string: '" + string + "'");
        }
        string3 = string.substring(1, string.length() - 1);
        String string4 = "";
        int n3 = 255;
        if (this.grammar instanceof LexerGrammar && (n3 = ((LexerGrammar)this.grammar).charVocabulary.size() - 1) > 255) {
            string4 = "L";
        }
        while (n < string3.length()) {
            if (string3.charAt(n) == '\\') {
                if (string3.length() == n + 1) {
                    this.antlrTool.error("Invalid escape in char literal: '" + string + "' looking at '" + string3.substring(n) + "'");
                }
                switch (string3.charAt(n + 1)) {
                    case 'a': {
                        n2 = 7;
                        n += 2;
                        break;
                    }
                    case 'b': {
                        n2 = 8;
                        n += 2;
                        break;
                    }
                    case 't': {
                        n2 = 9;
                        n += 2;
                        break;
                    }
                    case 'n': {
                        n2 = 10;
                        n += 2;
                        break;
                    }
                    case 'f': {
                        n2 = 12;
                        n += 2;
                        break;
                    }
                    case 'r': {
                        n2 = 13;
                        n += 2;
                        break;
                    }
                    case '\"': 
                    case '\'': 
                    case '\\': {
                        n2 = string3.charAt(n + 1);
                        n += 2;
                        break;
                    }
                    case 'u': {
                        if (n + 5 < string3.length()) {
                            n2 = Character.digit(string3.charAt(n + 2), 16) * 16 * 16 * 16 + Character.digit(string3.charAt(n + 3), 16) * 16 * 16 + Character.digit(string3.charAt(n + 4), 16) * 16 + Character.digit(string3.charAt(n + 5), 16);
                            n += 6;
                            break;
                        }
                        this.antlrTool.error("Invalid escape in char literal: '" + string + "' looking at '" + string3.substring(n) + "'");
                        break;
                    }
                    case '0': 
                    case '1': 
                    case '2': 
                    case '3': {
                        if (this.charIsDigit(string3, n + 2)) {
                            if (this.charIsDigit(string3, n + 3)) {
                                n2 = (string3.charAt(n + 1) - 48) * 8 * 8 + (string3.charAt(n + 2) - 48) * 8 + (string3.charAt(n + 3) - 48);
                                n += 4;
                                break;
                            }
                            n2 = (string3.charAt(n + 1) - 48) * 8 + (string3.charAt(n + 2) - 48);
                            n += 3;
                            break;
                        }
                        n2 = string3.charAt(n + 1) - 48;
                        n += 2;
                        break;
                    }
                    case '4': 
                    case '5': 
                    case '6': 
                    case '7': {
                        if (this.charIsDigit(string3, n + 2)) {
                            n2 = (string3.charAt(n + 1) - 48) * 8 + (string3.charAt(n + 2) - 48);
                            n += 3;
                        } else {
                            n2 = string3.charAt(n + 1) - 48;
                            n += 2;
                        }
                    }
                    default: {
                        this.antlrTool.error("Unhandled escape in char literal: '" + string + "' looking at '" + string3.substring(n) + "'");
                        n2 = 0;
                        break;
                    }
                }
            } else {
                n2 = string3.charAt(n++);
            }
            if (this.grammar instanceof LexerGrammar && n2 > n3) {
                String string5 = 32 <= n2 && n2 < 127 ? this.charFormatter.escapeChar(n2, true) : "0x" + Integer.toString(n2, 16);
                this.antlrTool.error("Character out of range in " + (bl ? "char literal" : "string constant") + ": '" + string3 + "'");
                this.antlrTool.error("Vocabulary size: " + n3 + " Character " + string5);
            }
            if (bl) {
                if (n != string3.length()) {
                    this.antlrTool.error("Invalid char literal: '" + string + "'");
                }
                if (n3 <= 255) {
                    if (n2 <= 255 && (n2 & 0x80) != 0) {
                        string2 = "static_cast<unsigned char>('" + this.charFormatter.escapeChar(n2, true) + "')";
                        continue;
                    }
                    string2 = "'" + this.charFormatter.escapeChar(n2, true) + "'";
                    continue;
                }
                string2 = "L'" + this.charFormatter.escapeChar(n2, true) + "'";
                continue;
            }
            string2 = string2 + this.charFormatter.escapeChar(n2, true);
        }
        if (!bl) {
            string2 = string4 + "\"" + string2 + "\"";
        }
        return string2;
    }

    public void gen() {
        try {
            Object object;
            Enumeration enumeration = this.behavior.grammars.elements();
            while (enumeration.hasMoreElements()) {
                object = (Grammar)enumeration.nextElement();
                if (((Grammar)object).debuggingOutput) {
                    this.antlrTool.error(((Grammar)object).getFilename() + ": C++ mode does not support -debug");
                }
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
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genAction(" + actionElement + ")");
        }
        if (actionElement.isSemPred) {
            this.genSemPred(actionElement.actionText, actionElement.line);
        } else {
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if ( inputState->guessing==0 ) {");
                ++this.tabs;
            }
            ActionTransInfo actionTransInfo = new ActionTransInfo();
            String string = this.processActionForSpecialSymbols(actionElement.actionText, actionElement.getLine(), this.currentRule, actionTransInfo);
            if (actionTransInfo.refRuleRoot != null) {
                this.println(actionTransInfo.refRuleRoot + " = " + this.labeledElementASTType + "(currentAST.root);");
            }
            this.genLineNo(actionElement);
            this.printAction(string);
            this.genLineNo2();
            if (actionTransInfo.assignToRoot) {
                this.println("currentAST.root = " + actionTransInfo.refRuleRoot + ";");
                this.println("if ( " + actionTransInfo.refRuleRoot + "!=" + this.labeledElementASTInit + " &&");
                ++this.tabs;
                this.println(actionTransInfo.refRuleRoot + "->getFirstChild() != " + this.labeledElementASTInit + " )");
                this.println("  currentAST.child = " + actionTransInfo.refRuleRoot + "->getFirstChild();");
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
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen(" + alternativeBlock + ")");
        }
        this.println("{");
        this.genBlockPreamble(alternativeBlock);
        this.genBlockInitAction(alternativeBlock);
        String string = this.currentASTResult;
        if (alternativeBlock.getLabel() != null) {
            this.currentASTResult = alternativeBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(alternativeBlock);
        CppBlockFinishingInfo cppBlockFinishingInfo = this.genCommonBlock(alternativeBlock, true);
        this.genBlockFinish(cppBlockFinishingInfo, this.throwNoViable);
        this.println("}");
        this.currentASTResult = string;
    }

    public void gen(BlockEndElement blockEndElement) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genRuleEnd(" + blockEndElement + ")");
        }
    }

    public void gen(CharLiteralElement charLiteralElement) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genChar(" + charLiteralElement + ")");
        }
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.error("cannot ref character literals in grammar: " + charLiteralElement);
        }
        if (charLiteralElement.getLabel() != null) {
            this.println(charLiteralElement.getLabel() + " = " + this.lt1Value + ";");
        }
        boolean bl = this.saveText;
        boolean bl2 = this.saveText = this.saveText && charLiteralElement.getAutoGenType() == 1;
        if (!this.saveText || charLiteralElement.getAutoGenType() == 3) {
            this.println("_saveIndex = text.length();");
        }
        this.print(charLiteralElement.not ? "matchNot(" : "match(");
        this._print(this.convertJavaToCppString(charLiteralElement.atomText, true));
        this._println(" /* charlit */ );");
        if (!this.saveText || charLiteralElement.getAutoGenType() == 3) {
            this.println("text.erase(_saveIndex);");
        }
        this.saveText = bl;
    }

    public void gen(CharRangeElement charRangeElement) {
        boolean bl;
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genCharRangeElement(" + charRangeElement.beginText + ".." + charRangeElement.endText + ")");
        }
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.error("cannot ref character range in grammar: " + charRangeElement);
        }
        if (charRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(charRangeElement.getLabel() + " = " + this.lt1Value + ";");
        }
        boolean bl2 = bl = this.grammar instanceof LexerGrammar && (!this.saveText || charRangeElement.getAutoGenType() == 3);
        if (bl) {
            this.println("_saveIndex=text.length();");
        }
        this.println("matchRange(" + this.convertJavaToCppString(charRangeElement.beginText, true) + "," + this.convertJavaToCppString(charRangeElement.endText, true) + ");");
        if (bl) {
            this.println("text.erase(_saveIndex);");
        }
    }

    public void gen(LexerGrammar lexerGrammar) throws IOException {
        if (lexerGrammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        if (lexerGrammar.charVocabulary.size() > 256) {
            this.antlrTool.warning(lexerGrammar.getFilename() + ": Vocabularies of this size still experimental in C++ mode (vocabulary size now: " + lexerGrammar.charVocabulary.size() + ")");
        }
        this.setGrammar(lexerGrammar);
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.panic("Internal error generating lexer");
        }
        this.genBody(lexerGrammar);
        this.genInclude(lexerGrammar);
    }

    public void gen(OneOrMoreBlock oneOrMoreBlock) {
        Object object;
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen+(" + oneOrMoreBlock + ")");
        }
        this.println("{ // ( ... )+");
        this.genBlockPreamble(oneOrMoreBlock);
        String string = oneOrMoreBlock.getLabel() != null ? "_cnt_" + oneOrMoreBlock.getLabel() : "_cnt" + oneOrMoreBlock.ID;
        this.println("int " + string + "=0;");
        String string2 = oneOrMoreBlock.getLabel() != null ? oneOrMoreBlock.getLabel() : "_loop" + oneOrMoreBlock.ID;
        this.println("for (;;) {");
        ++this.tabs;
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
            if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                System.out.println("nongreedy (...)+ loop; exit depth is " + oneOrMoreBlock.exitLookaheadDepth);
            }
            object = this.getLookaheadTestExpression(oneOrMoreBlock.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if ( " + string + ">=1 && " + (String)object + ") goto " + string2 + ";");
        }
        object = this.genCommonBlock(oneOrMoreBlock, false);
        this.genBlockFinish((CppBlockFinishingInfo)object, "if ( " + string + ">=1 ) { goto " + string2 + "; } else {" + this.throwNoViable + "}");
        this.println(string + "++;");
        --this.tabs;
        this.println("}");
        this.println(string2 + ":;");
        this.println("}  // ( ... )+");
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
        this.genInclude(parserGrammar);
    }

    public void gen(RuleRefElement ruleRefElement) {
        RuleSymbol ruleSymbol;
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
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
            this.println(ruleRefElement.getLabel() + " = (_t == ASTNULL) ? " + this.labeledElementASTInit + " : " + this.lt1Value + ";");
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || ruleRefElement.getAutoGenType() == 3)) {
            this.println("_saveIndex = text.length();");
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
            this.println("text.erase(_saveIndex);");
        }
        if (this.syntacticPredLevel == 0) {
            boolean bl;
            boolean bl2 = bl = this.grammar.hasSyntacticPredicate && (this.grammar.buildAST && ruleRefElement.getLabel() != null || this.genAST && ruleRefElement.getAutoGenType() == 1);
            if (bl) {
                this.println("if (inputState->guessing==0) {");
                ++this.tabs;
            }
            if (this.grammar.buildAST && ruleRefElement.getLabel() != null) {
                this.println(ruleRefElement.getLabel() + "_AST = returnAST;");
            }
            if (this.genAST) {
                switch (ruleRefElement.getAutoGenType()) {
                    case 1: {
                        if (this.usingCustomAST) {
                            this.println("astFactory->addASTChild(currentAST, " + namespaceAntlr + "RefAST(returnAST));");
                            break;
                        }
                        this.println("astFactory->addASTChild( currentAST, returnAST );");
                        break;
                    }
                    case 2: {
                        this.antlrTool.error("Internal: encountered ^ after rule reference");
                        break;
                    }
                }
            }
            if (this.grammar instanceof LexerGrammar && ruleRefElement.getLabel() != null) {
                this.println(ruleRefElement.getLabel() + "=_returnToken;");
            }
            if (bl) {
                --this.tabs;
                this.println("}");
            }
        }
        this.genErrorCatchForElement(ruleRefElement);
    }

    public void gen(StringLiteralElement stringLiteralElement) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
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
            this.println("_t = _t->getNextSibling();");
        }
    }

    public void gen(TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(tokenRangeElement);
        this.println("matchRange(" + tokenRangeElement.beginText + "," + tokenRangeElement.endText + ");");
        this.genErrorCatchForElement(tokenRangeElement);
    }

    public void gen(TokenRefElement tokenRefElement) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
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
            this.println("_t = _t->getNextSibling();");
        }
    }

    public void gen(TreeElement treeElement) {
        this.println(this.labeledElementType + " __t" + treeElement.ID + " = _t;");
        if (treeElement.root.getLabel() != null) {
            this.println(treeElement.root.getLabel() + " = (_t == " + this.labeledElementType + "(ASTNULL)) ? " + this.labeledElementASTInit + " : _t;");
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
            this.println(namespaceAntlr + "ASTPair __currentAST" + treeElement.ID + " = currentAST;");
            this.println("currentAST.root = currentAST.child;");
            this.println("currentAST.child = " + this.labeledElementASTInit + ";");
        }
        if (treeElement.root instanceof WildcardElement) {
            this.println("if ( _t == ASTNULL ) throw " + namespaceAntlr + "MismatchedTokenException();");
        } else {
            this.genMatch(treeElement.root);
        }
        this.println("_t = _t->getFirstChild();");
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
        this.println("_t = _t->getNextSibling();");
    }

    public void gen(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.setGrammar(treeWalkerGrammar);
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.antlrTool.panic("Internal error generating tree-walker");
        }
        this.genBody(treeWalkerGrammar);
        this.genInclude(treeWalkerGrammar);
    }

    public void gen(WildcardElement wildcardElement) {
        if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(wildcardElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(wildcardElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if ( _t == " + this.labeledElementASTInit + " ) throw " + namespaceAntlr + "MismatchedTokenException();");
        } else if (this.grammar instanceof LexerGrammar) {
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("_saveIndex = text.length();");
            }
            this.println("matchNot(EOF/*_CHAR*/);");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("text.erase(_saveIndex);");
            }
        } else {
            this.println("matchNot(" + this.getValueString(1) + ");");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t->getNextSibling();");
        }
    }

    public void gen(ZeroOrMoreBlock zeroOrMoreBlock) {
        Object object;
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen*(" + zeroOrMoreBlock + ")");
        }
        this.println("{ // ( ... )*");
        this.genBlockPreamble(zeroOrMoreBlock);
        String string = zeroOrMoreBlock.getLabel() != null ? zeroOrMoreBlock.getLabel() : "_loop" + zeroOrMoreBlock.ID;
        this.println("for (;;) {");
        ++this.tabs;
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
            if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                System.out.println("nongreedy (...)* loop; exit depth is " + zeroOrMoreBlock.exitLookaheadDepth);
            }
            object = this.getLookaheadTestExpression(zeroOrMoreBlock.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if (" + (String)object + ") goto " + string + ";");
        }
        object = this.genCommonBlock(zeroOrMoreBlock, false);
        this.genBlockFinish((CppBlockFinishingInfo)object, "goto " + string + ";");
        --this.tabs;
        this.println("}");
        this.println(string + ":;");
        this.println("} // ( ... )*");
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
            this.println("try {      // for error handling");
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
                    this.println(ruleBlock.getRuleName() + "_AST = " + this.labeledElementASTType + "(currentAST.root);");
                } else {
                    this.println(ruleBlock.getRuleName() + "_AST = currentAST.root;");
                }
            } else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules are not implemented", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
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

    protected void genBitsets(Vector vector, int n, String string) {
        TokenManager tokenManager = this.grammar.tokenManager;
        this.println("");
        for (int i = 0; i < vector.size(); ++i) {
            BitSet bitSet = (BitSet)vector.elementAt(i);
            bitSet.growToInclude(n);
            this.println("const unsigned long " + string + this.getBitsetName(i) + "_data_" + "[] = { " + bitSet.toStringOfHalfWords() + " };");
            String string2 = "// ";
            for (int j = 0; j < tokenManager.getVocabulary().size(); ++j) {
                if (!bitSet.member(j)) continue;
                string2 = this.grammar instanceof LexerGrammar ? (32 <= j && j < 127 && j != 92 ? string2 + this.charFormatter.escapeChar(j, true) + " " : string2 + "0x" + Integer.toString(j, 16) + " ") : string2 + tokenManager.getTokenStringAt(j) + " ";
                if (string2.length() <= 70) continue;
                this.println(string2);
                string2 = "// ";
            }
            if (string2 != "// ") {
                this.println(string2);
            }
            this.println("const " + namespaceAntlr + "BitSet " + string + this.getBitsetName(i) + "(" + this.getBitsetName(i) + "_data_," + bitSet.size() / 32 + ");");
        }
    }

    protected void genBitsetsHeader(Vector vector, int n) {
        this.println("");
        for (int i = 0; i < vector.size(); ++i) {
            BitSet bitSet = (BitSet)vector.elementAt(i);
            bitSet.growToInclude(n);
            this.println("static const unsigned long " + this.getBitsetName(i) + "_data_" + "[];");
            this.println("static const " + namespaceAntlr + "BitSet " + this.getBitsetName(i) + ";");
        }
    }

    private void genBlockFinish(CppBlockFinishingInfo cppBlockFinishingInfo, String string) {
        if (cppBlockFinishingInfo.needAnErrorClause && (cppBlockFinishingInfo.generatedAnIf || cppBlockFinishingInfo.generatedSwitch)) {
            if (cppBlockFinishingInfo.generatedAnIf) {
                this.println("else {");
            } else {
                this.println("{");
            }
            ++this.tabs;
            this.println(string);
            --this.tabs;
            this.println("}");
        }
        if (cppBlockFinishingInfo.postscript != null) {
            this.println(cppBlockFinishingInfo.postscript);
        }
    }

    protected void genBlockInitAction(AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.genLineNo(alternativeBlock);
            this.printAction(this.processActionForSpecialSymbols(alternativeBlock.initAction, alternativeBlock.line, this.currentRule, null));
            this.genLineNo2();
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
                            this.println(namespaceAntlr + "RefToken " + alternativeElement.getLabel() + ";");
                        }
                        if (!(this.grammar instanceof TreeWalkerGrammar)) continue;
                        this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                        continue;
                    }
                    this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                    if (!this.grammar.buildAST) continue;
                    if (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
                        GrammarAtom grammarAtom = (GrammarAtom)alternativeElement;
                        this.genASTDeclaration(alternativeElement, "Ref" + grammarAtom.getASTNodeType());
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
        this.outputFile = this.grammar.getClassName() + ".cpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.genHeader(this.outputFile);
        this.printHeaderAction(preIncludeCpp);
        this.println("#include \"" + this.grammar.getClassName() + ".hpp\"");
        this.println("#include <antlr/CharBuffer.hpp>");
        this.println("#include <antlr/TokenStreamException.hpp>");
        this.println("#include <antlr/TokenStreamIOException.hpp>");
        this.println("#include <antlr/TokenStreamRecognitionException.hpp>");
        this.println("#include <antlr/CharStreamException.hpp>");
        this.println("#include <antlr/CharStreamIOException.hpp>");
        this.println("#include <antlr/NoViableAltForCharException.hpp>");
        if (this.grammar.debuggingOutput) {
            this.println("#include <antlr/DebuggingInputBuffer.hpp>");
        }
        this.println("");
        this.printHeaderAction(postIncludeCpp);
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printAction(this.grammar.preambleAction);
        String string = null;
        if (this.grammar.superClass != null) {
            string = this.grammar.superClass;
        } else {
            string = this.grammar.getSuperClass();
            if (string.lastIndexOf(46) != -1) {
                string = string.substring(string.lastIndexOf(46) + 1);
            }
            string = namespaceAntlr + string;
        }
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "(" + namespaceStd + "istream& in)");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println(": " + string + "(new " + namespaceAntlr + "DebuggingInputBuffer(new " + namespaceAntlr + "CharBuffer(in))," + lexerGrammar.caseSensitive + ")");
        } else {
            this.println(": " + string + "(new " + namespaceAntlr + "CharBuffer(in)," + lexerGrammar.caseSensitive + ")");
        }
        --this.tabs;
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("setRuleNames(_ruleNames);");
            this.println("setSemPredNames(_semPredNames);");
            this.println("setupDebugging();");
        }
        this.println("initLiterals();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "(" + namespaceAntlr + "InputBuffer& ib)");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println(": " + string + "(new " + namespaceAntlr + "DebuggingInputBuffer(ib)," + lexerGrammar.caseSensitive + ")");
        } else {
            this.println(": " + string + "(ib," + lexerGrammar.caseSensitive + ")");
        }
        --this.tabs;
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("setRuleNames(_ruleNames);");
            this.println("setSemPredNames(_semPredNames);");
            this.println("setupDebugging();");
        }
        this.println("initLiterals();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "(const " + namespaceAntlr + "LexerSharedInputState& state)");
        ++this.tabs;
        this.println(": " + string + "(state," + lexerGrammar.caseSensitive + ")");
        --this.tabs;
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("setRuleNames(_ruleNames);");
            this.println("setSemPredNames(_semPredNames);");
            this.println("setupDebugging();");
        }
        this.println("initLiterals();");
        --this.tabs;
        this.println("}");
        this.println("");
        if (this.noConstructors) {
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
        }
        this.println("void " + this.grammar.getClassName() + "::initLiterals()");
        this.println("{");
        ++this.tabs;
        Enumeration enumeration = this.grammar.tokenManager.getTokenSymbolKeys();
        while (enumeration.hasMoreElements()) {
            TokenSymbol tokenSymbol;
            object = (String)enumeration.nextElement();
            if (((String)object).charAt(0) != '\"' || !((tokenSymbol = this.grammar.tokenManager.getTokenSymbol((String)object)) instanceof StringLiteralSymbol)) continue;
            grammarSymbol = (StringLiteralSymbol)tokenSymbol;
            this.println("literals[" + grammarSymbol.getId() + "] = " + ((TokenSymbol)grammarSymbol).getTokenType() + ";");
        }
        --this.tabs;
        this.println("}");
        if (this.grammar.debuggingOutput) {
            this.println("const char* " + this.grammar.getClassName() + "::_ruleNames[] = {");
            ++this.tabs;
            object = this.grammar.rules.elements();
            boolean bl = false;
            while (object.hasMoreElements()) {
                grammarSymbol = (GrammarSymbol)object.nextElement();
                if (!(grammarSymbol instanceof RuleSymbol)) continue;
                this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
            }
            this.println("0");
            --this.tabs;
            this.println("};");
        }
        this.genNextToken();
        object = this.grammar.rules.elements();
        int n = 0;
        while (object.hasMoreElements()) {
            grammarSymbol = (RuleSymbol)object.nextElement();
            if (!grammarSymbol.getId().equals("mnextToken")) {
                this.genRule((RuleSymbol)grammarSymbol, false, n++, this.grammar.getClassName() + "::");
            }
            this.exitIfError();
        }
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap(this.grammar.getClassName() + "::");
        }
        this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size(), this.grammar.getClassName() + "::");
        this.println("");
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void genInitFactory(Grammar grammar) {
        String string = "factory ";
        if (!grammar.buildAST) {
            string = "";
        }
        this.println("void " + grammar.getClassName() + "::initializeASTFactory( " + namespaceAntlr + "ASTFactory& " + string + ")");
        this.println("{");
        ++this.tabs;
        if (grammar.buildAST) {
            Object object;
            TokenManager tokenManager = this.grammar.tokenManager;
            Enumeration enumeration = tokenManager.getTokenSymbolKeys();
            while (enumeration.hasMoreElements()) {
                String string2 = (String)enumeration.nextElement();
                object = tokenManager.getTokenSymbol(string2);
                if (((TokenSymbol)object).getASTNodeType() == null) continue;
                this.astTypes.ensureCapacity(((TokenSymbol)object).getTokenType());
                String string3 = (String)this.astTypes.elementAt(((TokenSymbol)object).getTokenType());
                if (string3 == null) {
                    this.astTypes.setElementAt(((TokenSymbol)object).getASTNodeType(), ((TokenSymbol)object).getTokenType());
                    continue;
                }
                if (((TokenSymbol)object).getASTNodeType().equals(string3)) continue;
                this.antlrTool.warning("Token " + string2 + " taking most specific AST type", this.grammar.getFilename(), 1, 1);
                this.antlrTool.warning("  using " + string3 + " ignoring " + ((TokenSymbol)object).getASTNodeType(), this.grammar.getFilename(), 1, 1);
            }
            for (int i = 0; i < this.astTypes.size(); ++i) {
                object = (String)this.astTypes.elementAt(i);
                if (object == null) continue;
                this.println("factory.registerFactory(" + i + ", \"" + (String)object + "\", " + (String)object + "::factory);");
            }
            this.println("factory.setMaxNodeType(" + this.grammar.tokenManager.maxTokenType() + ");");
        }
        --this.tabs;
        this.println("}");
    }

    public void genBody(ParserGrammar parserGrammar) throws IOException {
        GrammarSymbol grammarSymbol;
        int n;
        Enumeration enumeration;
        this.outputFile = this.grammar.getClassName() + ".cpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader(this.outputFile);
        this.printHeaderAction(preIncludeCpp);
        this.println("#include \"" + this.grammar.getClassName() + ".hpp\"");
        this.println("#include <antlr/NoViableAltException.hpp>");
        this.println("#include <antlr/SemanticException.hpp>");
        this.println("#include <antlr/ASTFactory.hpp>");
        this.printHeaderAction(postIncludeCpp);
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printAction(this.grammar.preambleAction);
        String string = null;
        if (this.grammar.superClass != null) {
            string = this.grammar.superClass;
        } else {
            string = this.grammar.getSuperClass();
            if (string.lastIndexOf(46) != -1) {
                string = string.substring(string.lastIndexOf(46) + 1);
            }
            string = namespaceAntlr + string;
        }
        if (this.grammar.debuggingOutput) {
            this.println("const char* " + this.grammar.getClassName() + "::_ruleNames[] = {");
            ++this.tabs;
            enumeration = this.grammar.rules.elements();
            n = 0;
            while (enumeration.hasMoreElements()) {
                grammarSymbol = (GrammarSymbol)enumeration.nextElement();
                if (!(grammarSymbol instanceof RuleSymbol)) continue;
                this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
            }
            this.println("0");
            --this.tabs;
            this.println("};");
        }
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + namespaceAntlr + "TokenBuffer& tokenBuf, int k)");
        this.println(": " + string + "(tokenBuf,k)");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + namespaceAntlr + "TokenBuffer& tokenBuf)");
        this.println(": " + string + "(tokenBuf," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + namespaceAntlr + "TokenStream& lexer, int k)");
        this.println(": " + string + "(lexer,k)");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + namespaceAntlr + "TokenStream& lexer)");
        this.println(": " + string + "(lexer," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(const " + namespaceAntlr + "ParserSharedInputState& state)");
        this.println(": " + string + "(state," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        if (this.noConstructors) {
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
        }
        this.astTypes = new Vector();
        enumeration = this.grammar.rules.elements();
        n = 0;
        while (enumeration.hasMoreElements()) {
            grammarSymbol = (GrammarSymbol)enumeration.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n++, this.grammar.getClassName() + "::");
            }
            this.exitIfError();
        }
        this.genInitFactory(parserGrammar);
        this.genTokenStrings(this.grammar.getClassName() + "::");
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType(), this.grammar.getClassName() + "::");
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap(this.grammar.getClassName() + "::");
        }
        this.println("");
        this.println("");
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void genBody(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.outputFile = this.grammar.getClassName() + ".cpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader(this.outputFile);
        this.printHeaderAction(preIncludeCpp);
        this.println("#include \"" + this.grammar.getClassName() + ".hpp\"");
        this.println("#include <antlr/Token.hpp>");
        this.println("#include <antlr/AST.hpp>");
        this.println("#include <antlr/NoViableAltException.hpp>");
        this.println("#include <antlr/MismatchedTokenException.hpp>");
        this.println("#include <antlr/SemanticException.hpp>");
        this.println("#include <antlr/BitSet.hpp>");
        this.printHeaderAction(postIncludeCpp);
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printAction(this.grammar.preambleAction);
        String string = null;
        if (this.grammar.superClass != null) {
            string = this.grammar.superClass;
        } else {
            string = this.grammar.getSuperClass();
            if (string.lastIndexOf(46) != -1) {
                string = string.substring(string.lastIndexOf(46) + 1);
            }
            string = namespaceAntlr + string;
        }
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "()");
        this.println("\t: " + namespaceAntlr + "TreeParser() {");
        ++this.tabs;
        --this.tabs;
        this.println("}");
        if (this.noConstructors) {
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
        }
        this.println("");
        this.astTypes = new Vector();
        Enumeration enumeration = this.grammar.rules.elements();
        int n = 0;
        String string2 = "";
        while (enumeration.hasMoreElements()) {
            GrammarSymbol grammarSymbol = (GrammarSymbol)enumeration.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n++, this.grammar.getClassName() + "::");
            }
            this.exitIfError();
        }
        this.genInitFactory(this.grammar);
        this.genTokenStrings(this.grammar.getClassName() + "::");
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType(), this.grammar.getClassName() + "::");
        this.println("");
        this.println("");
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }

    protected void genCases(BitSet bitSet) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genCases(" + bitSet + ")");
        }
        int[] nArray = bitSet.toArray();
        int n = 1;
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

    public CppBlockFinishingInfo genCommonBlock(AlternativeBlock alternativeBlock, boolean bl) {
        int n;
        Object object;
        int n2 = 0;
        boolean bl2 = false;
        int n3 = 0;
        CppBlockFinishingInfo cppBlockFinishingInfo = new CppBlockFinishingInfo();
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genCommonBlk(" + alternativeBlock + ")");
        }
        boolean bl3 = this.genAST;
        this.genAST = this.genAST && alternativeBlock.getAutoGen();
        boolean bl4 = this.saveText;
        boolean bl5 = this.saveText = this.saveText && alternativeBlock.getAutoGen();
        if (alternativeBlock.not && this.analyzer.subruleCanBeInverted(alternativeBlock, this.grammar instanceof LexerGrammar)) {
            Lookahead lookahead = this.analyzer.look(1, alternativeBlock);
            if (alternativeBlock.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(alternativeBlock.getLabel() + " = " + this.lt1Value + ";");
            }
            this.genElementAST(alternativeBlock);
            String string = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                string = this.usingCustomAST ? namespaceAntlr + "RefAST" + "(_t)," : "_t,";
            }
            this.println("match(" + string + this.getBitsetName(this.markBitsetForGen(lookahead.fset)) + ");");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t->getNextSibling();");
            }
            return cppBlockFinishingInfo;
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
                return cppBlockFinishingInfo;
            }
        }
        int n4 = 0;
        for (int i = 0; i < alternativeBlock.getAlternatives().size(); ++i) {
            Alternative alternative = alternativeBlock.getAlternativeAt(i);
            if (!CppCodeGenerator.suitableForCaseExpression(alternative)) continue;
            ++n4;
        }
        if (n4 >= this.makeSwitchThreshold) {
            String string = this.lookaheadString(1);
            bl2 = true;
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if (_t == " + this.labeledElementASTInit + " )");
                ++this.tabs;
                this.println("_t = ASTNULL;");
                --this.tabs;
            }
            this.println("switch ( " + string + ") {");
            for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
                Alternative alternative = alternativeBlock.getAlternativeAt(i);
                if (!CppCodeGenerator.suitableForCaseExpression(alternative)) continue;
                object = alternative.cache[1];
                if (((Lookahead)object).fset.degree() == 0 && !((Lookahead)object).containsEpsilon()) {
                    this.antlrTool.warning("Alternate omitted due to empty prediction set", this.grammar.getFilename(), alternative.head.getLine(), alternative.head.getColumn());
                    continue;
                }
                this.genCases(((Lookahead)object).fset);
                this.println("{");
                ++this.tabs;
                this.genAlt(alternative, alternativeBlock);
                this.println("break;");
                --this.tabs;
                this.println("}");
            }
            this.println("default:");
            ++this.tabs;
        }
        for (int i = n = this.grammar instanceof LexerGrammar ? this.grammar.maxk : 0; i >= 0; --i) {
            if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                System.out.println("checking depth " + i);
            }
            for (int j = 0; j < alternativeBlock.alternatives.size(); ++j) {
                String string;
                object = alternativeBlock.getAlternativeAt(j);
                if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                    System.out.println("genAlt: " + j);
                }
                if (bl2 && CppCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                    if (!this.DEBUG_CODE_GENERATOR && !this.DEBUG_CPP_CODE_GENERATOR) continue;
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
                        if (!this.DEBUG_CODE_GENERATOR && !this.DEBUG_CPP_CODE_GENERATOR) continue;
                        System.out.println("ignoring alt because effectiveDepth!=altDepth;" + n5 + "!=" + i);
                        continue;
                    }
                    bl6 = this.lookaheadIsEmpty((Alternative)object, n5);
                    string = this.getLookaheadTestExpression((Alternative)object, n5);
                } else {
                    bl6 = this.lookaheadIsEmpty((Alternative)object, this.grammar.maxk);
                    string = this.getLookaheadTestExpression((Alternative)object, this.grammar.maxk);
                }
                if (((Alternative)object).cache[1].fset.degree() > 127 && CppCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                    if (n2 == 0) {
                        if (this.grammar instanceof TreeWalkerGrammar) {
                            this.println("if (_t == " + this.labeledElementASTInit + " )");
                            ++this.tabs;
                            this.println("_t = ASTNULL;");
                            --this.tabs;
                        }
                        this.println("if " + string + " {");
                    } else {
                        this.println("else if " + string + " {");
                    }
                } else if (bl6 && ((Alternative)object).semPred == null && ((Alternative)object).synPred == null) {
                    if (n2 == 0) {
                        this.println("{");
                    } else {
                        this.println("else {");
                    }
                    cppBlockFinishingInfo.needAnErrorClause = false;
                } else {
                    if (((Alternative)object).semPred != null) {
                        ActionTransInfo actionTransInfo = new ActionTransInfo();
                        String string2 = this.processActionForSpecialSymbols(((Alternative)object).semPred, alternativeBlock.line, this.currentRule, actionTransInfo);
                        string = this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar) ? "(" + string + "&& fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.PREDICTING," + this.addSemPred(this.charFormatter.escapeString(string2)) + "," + string2 + "))" : "(" + string + "&&(" + string2 + "))";
                    }
                    if (n2 > 0) {
                        if (((Alternative)object).synPred != null) {
                            this.println("else {");
                            ++this.tabs;
                            this.genSynPred(((Alternative)object).synPred, string);
                            ++n3;
                        } else {
                            this.println("else if " + string + " {");
                        }
                    } else if (((Alternative)object).synPred != null) {
                        this.genSynPred(((Alternative)object).synPred, string);
                    } else {
                        if (this.grammar instanceof TreeWalkerGrammar) {
                            this.println("if (_t == " + this.labeledElementASTInit + " )");
                            ++this.tabs;
                            this.println("_t = ASTNULL;");
                            --this.tabs;
                        }
                        this.println("if " + string + " {");
                    }
                }
                ++n2;
                ++this.tabs;
                this.genAlt((Alternative)object, alternativeBlock);
                --this.tabs;
                this.println("}");
            }
        }
        String string = "";
        for (int i = 1; i <= n3; ++i) {
            --this.tabs;
            string = string + "}";
        }
        this.genAST = bl3;
        this.saveText = bl4;
        if (bl2) {
            --this.tabs;
            cppBlockFinishingInfo.postscript = string + "}";
            cppBlockFinishingInfo.generatedSwitch = true;
            cppBlockFinishingInfo.generatedAnIf = n2 > 0;
        } else {
            cppBlockFinishingInfo.postscript = string;
            cppBlockFinishingInfo.generatedSwitch = false;
            cppBlockFinishingInfo.generatedAnIf = n2 > 0;
        }
        return cppBlockFinishingInfo;
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
                        this.genASTDeclaration(alternativeElement, string, "Ref" + ((GrammarAtom)object).getASTNodeType());
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
                this.println(this.labeledElementASTType + " " + (String)object + "_in = " + this.labeledElementASTInit + ";");
            }
            if (bl) {
                this.println("if ( inputState->guessing == 0 ) {");
                ++this.tabs;
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
                            this.println("astFactory->addASTChild(currentAST, " + namespaceAntlr + "RefAST(" + (String)object + "));");
                            break;
                        }
                        this.println("astFactory->addASTChild(currentAST, " + (String)object + ");");
                        break;
                    }
                    case 2: {
                        if (this.usingCustomAST || alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
                            this.println("astFactory->makeASTRoot(currentAST, " + namespaceAntlr + "RefAST(" + (String)object + "));");
                            break;
                        }
                        this.println("astFactory->makeASTRoot(currentAST, " + (String)object + ");");
                        break;
                    }
                }
            }
            if (bl) {
                --this.tabs;
                this.println("}");
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
            this.println("catch (" + exceptionHandler.exceptionTypeAndName.getText() + ") {");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (inputState->guessing==0) {");
                ++this.tabs;
            }
            ActionTransInfo actionTransInfo = new ActionTransInfo();
            this.genLineNo(exceptionHandler.action);
            this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, actionTransInfo));
            this.genLineNo2();
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("} else {");
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
            this.println("try { // for error handling");
            ++this.tabs;
        }
    }

    protected void genHeader(String string) {
        this.println("/* $ANTLR " + Tool.version + ": " + "\"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"" + " -> " + "\"" + string + "\"$ */");
    }

    public void genInclude(LexerGrammar lexerGrammar) throws IOException {
        Object object;
        this.outputFile = this.grammar.getClassName() + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.println("#ifndef INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("#define INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("");
        this.printHeaderAction(preIncludeHpp);
        this.println("#include <antlr/config.hpp>");
        this.genHeader(this.outputFile);
        this.println("#include <antlr/CommonToken.hpp>");
        this.println("#include <antlr/InputBuffer.hpp>");
        this.println("#include <antlr/BitSet.hpp>");
        this.println("#include \"" + this.grammar.tokenManager.getName() + TokenTypesFileSuffix + ".hpp\"");
        String string = null;
        if (this.grammar.superClass != null) {
            string = this.grammar.superClass;
            this.println("\n// Include correct superclass header with a header statement for example:");
            this.println("// header \"post_include_hpp\" {");
            this.println("// #include \"" + string + ".hpp\"");
            this.println("// }");
            this.println("// Or....");
            this.println("// header {");
            this.println("// #include \"" + string + ".hpp\"");
            this.println("// }\n");
        } else {
            string = this.grammar.getSuperClass();
            if (string.lastIndexOf(46) != -1) {
                string = string.substring(string.lastIndexOf(46) + 1);
            }
            this.println("#include <antlr/" + string + ".hpp>");
            string = namespaceAntlr + string;
        }
        this.printHeaderAction(postIncludeHpp);
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printHeaderAction("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        this.print("class CUSTOM_API " + this.grammar.getClassName() + " : public " + string);
        this.println(", public " + this.grammar.tokenManager.getName() + TokenTypesFileSuffix);
        Token token = (Token)this.grammar.options.get("classHeaderSuffix");
        if (token != null && (object = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
            this.print(", " + (String)object);
        }
        this.println("{");
        if (this.grammar.classMemberAction != null) {
            this.genLineNo(this.grammar.classMemberAction);
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
            this.genLineNo2();
        }
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        this.println("void initLiterals();");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println("bool getCaseSensitiveLiterals() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + lexerGrammar.caseSensitiveLiterals + ";");
        --this.tabs;
        this.println("}");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.tabs = 1;
        }
        this.println(this.grammar.getClassName() + "(" + namespaceStd + "istream& in);");
        this.println(this.grammar.getClassName() + "(" + namespaceAntlr + "InputBuffer& ib);");
        this.println(this.grammar.getClassName() + "(const " + namespaceAntlr + "LexerSharedInputState& state);");
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
            this.tabs = 1;
        }
        this.println(namespaceAntlr + "RefToken nextToken();");
        object = this.grammar.rules.elements();
        while (object.hasMoreElements()) {
            RuleSymbol ruleSymbol = (RuleSymbol)object.nextElement();
            if (!ruleSymbol.getId().equals("mnextToken")) {
                this.genRuleHeader(ruleSymbol, false);
            }
            this.exitIfError();
        }
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        if (this.grammar.debuggingOutput) {
            this.println("static const char* _ruleNames[];");
        }
        if (this.grammar.debuggingOutput) {
            this.println("static const char* _semPredNames[];");
        }
        this.genBitsetsHeader(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
        this.tabs = 0;
        this.println("};");
        this.println("");
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + this.grammar.getClassName() + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void genInclude(ParserGrammar parserGrammar) throws IOException {
        Object object;
        this.outputFile = this.grammar.getClassName() + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.println("#ifndef INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("#define INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("");
        this.printHeaderAction(preIncludeHpp);
        this.println("#include <antlr/config.hpp>");
        this.genHeader(this.outputFile);
        this.println("#include <antlr/TokenStream.hpp>");
        this.println("#include <antlr/TokenBuffer.hpp>");
        this.println("#include \"" + this.grammar.tokenManager.getName() + TokenTypesFileSuffix + ".hpp\"");
        String string = null;
        if (this.grammar.superClass != null) {
            string = this.grammar.superClass;
            this.println("\n// Include correct superclass header with a header statement for example:");
            this.println("// header \"post_include_hpp\" {");
            this.println("// #include \"" + string + ".hpp\"");
            this.println("// }");
            this.println("// Or....");
            this.println("// header {");
            this.println("// #include \"" + string + ".hpp\"");
            this.println("// }\n");
        } else {
            string = this.grammar.getSuperClass();
            if (string.lastIndexOf(46) != -1) {
                string = string.substring(string.lastIndexOf(46) + 1);
            }
            this.println("#include <antlr/" + string + ".hpp>");
            string = namespaceAntlr + string;
        }
        this.println("");
        this.printHeaderAction(postIncludeHpp);
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printHeaderAction("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        this.print("class CUSTOM_API " + this.grammar.getClassName() + " : public " + string);
        this.println(", public " + this.grammar.tokenManager.getName() + TokenTypesFileSuffix);
        Token token = (Token)this.grammar.options.get("classHeaderSuffix");
        if (token != null && (object = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
            this.print(", " + (String)object);
        }
        this.println("{");
        if (this.grammar.debuggingOutput) {
            this.println("public: static const char* _ruleNames[];");
        }
        if (this.grammar.classMemberAction != null) {
            this.genLineNo(this.grammar.classMemberAction.getLine());
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
            this.genLineNo2();
        }
        this.println("public:");
        this.tabs = 1;
        this.println("void initializeASTFactory( " + namespaceAntlr + "ASTFactory& factory );");
        this.tabs = 0;
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.println("protected:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + namespaceAntlr + "TokenBuffer& tokenBuf, int k);");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + namespaceAntlr + "TokenBuffer& tokenBuf);");
        this.tabs = 0;
        this.println("protected:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + namespaceAntlr + "TokenStream& lexer, int k);");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + namespaceAntlr + "TokenStream& lexer);");
        this.println(this.grammar.getClassName() + "(const " + namespaceAntlr + "ParserSharedInputState& state);");
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
            this.tabs = 1;
        }
        this.println("int getNumTokens() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::NUM_TOKENS;");
        --this.tabs;
        this.println("}");
        this.println("const char* getTokenName( int type ) const");
        this.println("{");
        ++this.tabs;
        this.println("if( type > getNumTokens() ) return 0;");
        this.println("return " + this.grammar.getClassName() + "::tokenNames[type];");
        --this.tabs;
        this.println("}");
        this.println("const char* const* getTokenNames() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::tokenNames;");
        --this.tabs;
        this.println("}");
        object = this.grammar.rules.elements();
        while (object.hasMoreElements()) {
            GrammarSymbol grammarSymbol = (GrammarSymbol)object.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRuleHeader(ruleSymbol, ruleSymbol.references.size() == 0);
            }
            this.exitIfError();
        }
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(namespaceAntlr + "RefAST getAST()");
        this.println("{");
        if (this.usingCustomAST) {
            ++this.tabs;
            this.println("return " + namespaceAntlr + "RefAST(returnAST);");
            --this.tabs;
        } else {
            ++this.tabs;
            this.println("return returnAST;");
            --this.tabs;
        }
        this.println("}");
        this.println("");
        this.tabs = 0;
        this.println("protected:");
        this.tabs = 1;
        this.println(this.labeledElementASTType + " returnAST;");
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        this.println("static const char* tokenNames[];");
        this._println("#ifndef NO_STATIC_CONSTS");
        this.println("static const int NUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size() + ";");
        this._println("#else");
        this.println("enum {");
        this.println("\tNUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size());
        this.println("};");
        this._println("#endif");
        this.genBitsetsHeader(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        if (this.grammar.debuggingOutput) {
            this.println("static const char* _semPredNames[];");
        }
        this.tabs = 0;
        this.println("};");
        this.println("");
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + this.grammar.getClassName() + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void genInclude(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        Object object;
        this.outputFile = this.grammar.getClassName() + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.println("#ifndef INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("#define INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("");
        this.printHeaderAction(preIncludeHpp);
        this.println("#include <antlr/config.hpp>");
        this.println("#include \"" + this.grammar.tokenManager.getName() + TokenTypesFileSuffix + ".hpp\"");
        this.genHeader(this.outputFile);
        String string = null;
        if (this.grammar.superClass != null) {
            string = this.grammar.superClass;
            this.println("\n// Include correct superclass header with a header statement for example:");
            this.println("// header \"post_include_hpp\" {");
            this.println("// #include \"" + string + ".hpp\"");
            this.println("// }");
            this.println("// Or....");
            this.println("// header {");
            this.println("// #include \"" + string + ".hpp\"");
            this.println("// }\n");
        } else {
            string = this.grammar.getSuperClass();
            if (string.lastIndexOf(46) != -1) {
                string = string.substring(string.lastIndexOf(46) + 1);
            }
            this.println("#include <antlr/" + string + ".hpp>");
            string = namespaceAntlr + string;
        }
        this.println("");
        this.printHeaderAction(postIncludeHpp);
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printHeaderAction("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        this.print("class CUSTOM_API " + this.grammar.getClassName() + " : public " + string);
        this.println(", public " + this.grammar.tokenManager.getName() + TokenTypesFileSuffix);
        Token token = (Token)this.grammar.options.get("classHeaderSuffix");
        if (token != null && (object = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
            this.print(", " + (String)object);
        }
        this.println("{");
        if (this.grammar.classMemberAction != null) {
            this.genLineNo(this.grammar.classMemberAction.getLine());
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
            this.genLineNo2();
        }
        this.tabs = 0;
        this.println("public:");
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "();");
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("#endif");
            this.tabs = 1;
        }
        this.println("static void initializeASTFactory( " + namespaceAntlr + "ASTFactory& factory );");
        this.println("int getNumTokens() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::NUM_TOKENS;");
        --this.tabs;
        this.println("}");
        this.println("const char* getTokenName( int type ) const");
        this.println("{");
        ++this.tabs;
        this.println("if( type > getNumTokens() ) return 0;");
        this.println("return " + this.grammar.getClassName() + "::tokenNames[type];");
        --this.tabs;
        this.println("}");
        this.println("const char* const* getTokenNames() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::tokenNames;");
        --this.tabs;
        this.println("}");
        object = this.grammar.rules.elements();
        String string2 = "";
        while (object.hasMoreElements()) {
            GrammarSymbol grammarSymbol = (GrammarSymbol)object.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRuleHeader(ruleSymbol, ruleSymbol.references.size() == 0);
            }
            this.exitIfError();
        }
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(namespaceAntlr + "RefAST getAST()");
        this.println("{");
        if (this.usingCustomAST) {
            ++this.tabs;
            this.println("return " + namespaceAntlr + "RefAST(returnAST);");
            --this.tabs;
        } else {
            ++this.tabs;
            this.println("return returnAST;");
            --this.tabs;
        }
        this.println("}");
        this.println("");
        this.tabs = 0;
        this.println("protected:");
        this.tabs = 1;
        this.println(this.labeledElementASTType + " returnAST;");
        this.println(this.labeledElementASTType + " _retTree;");
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        this.println("static const char* tokenNames[];");
        this._println("#ifndef NO_STATIC_CONSTS");
        this.println("static const int NUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size() + ";");
        this._println("#else");
        this.println("enum {");
        this.println("\tNUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size());
        this.println("};");
        this._println("#endif");
        this.genBitsetsHeader(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        this.tabs = 0;
        this.println("};");
        this.println("");
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + this.grammar.getClassName() + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
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
        String string3 = this.labeledElementASTInit;
        if (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
            string3 = "Ref" + ((GrammarAtom)alternativeElement).getASTNodeType() + "(" + this.labeledElementASTInit + ")";
        }
        this.println(string2 + " " + string + "_AST = " + string3 + ";");
        this.declaredASTVariables.put(alternativeElement, alternativeElement);
    }

    private void genLiteralsTest() {
        this.println("_ttype = testLiteralsTable(_ttype);");
    }

    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = testLiteralsTable(text.substr(_begin, text.length()-_begin),_ttype);");
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
            this.antlrTool.error("cannot ref character literals in grammar: " + grammarAtom);
        } else if (grammarAtom instanceof TokenRefElement) {
            this.genMatchUsingAtomTokenType(grammarAtom);
        } else if (grammarAtom instanceof WildcardElement) {
            this.gen((WildcardElement)grammarAtom);
        }
    }

    protected void genMatchUsingAtomText(GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            string = this.usingCustomAST ? namespaceAntlr + "RefAST" + "(_t)," : "_t,";
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("_saveIndex = text.length();");
        }
        this.print(grammarAtom.not ? "matchNot(" : "match(");
        this._print(string);
        if (grammarAtom.atomText.equals("EOF")) {
            this._print(namespaceAntlr + "Token::EOF_TYPE");
        } else if (this.grammar instanceof LexerGrammar) {
            String string2 = this.convertJavaToCppString(grammarAtom.atomText, false);
            this._print(string2);
        } else {
            this._print(grammarAtom.atomText);
        }
        this._println(");");
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("text.erase(_saveIndex);");
        }
    }

    protected void genMatchUsingAtomTokenType(GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            string = this.usingCustomAST ? namespaceAntlr + "RefAST" + "(_t)," : "_t,";
        }
        String string2 = string + this.getValueString(grammarAtom.getType());
        this.println((grammarAtom.not ? "matchNot(" : "match(") + string2 + ");");
    }

    public void genNextToken() {
        Object object;
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
            this.println(namespaceAntlr + "RefToken " + this.grammar.getClassName() + "::nextToken() { return " + namespaceAntlr + "RefToken(new " + namespaceAntlr + "CommonToken(" + namespaceAntlr + "Token::EOF_TYPE, \"\")); }");
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
        this.println(namespaceAntlr + "RefToken " + this.grammar.getClassName() + "::nextToken()");
        this.println("{");
        ++this.tabs;
        this.println(namespaceAntlr + "RefToken theRetToken;");
        this.println("for (;;) {");
        ++this.tabs;
        this.println(namespaceAntlr + "RefToken theRetToken;");
        this.println("int _ttype = " + namespaceAntlr + "Token::INVALID_TYPE;");
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
        this.println("try {   // for lexical and char stream error handling");
        ++this.tabs;
        for (int i = 0; i < ruleBlock.getAlternatives().size(); ++i) {
            object = ruleBlock.getAlternativeAt(i);
            if (!((Alternative)object).cache[1].containsEpsilon()) continue;
            this.antlrTool.warning("found optional path in nextToken()");
        }
        String string2 = System.getProperty("line.separator");
        object = this.genCommonBlock(ruleBlock, false);
        String string3 = "if (LA(1)==EOF_CHAR)" + string2 + "\t\t\t\t{" + string2 + "\t\t\t\t\tuponEOF();" + string2 + "\t\t\t\t\t_returnToken = makeToken(" + namespaceAntlr + "Token::EOF_TYPE);" + string2 + "\t\t\t\t}";
        string3 = string3 + string2 + "\t\t\t\t";
        string3 = ((LexerGrammar)this.grammar).filterMode ? (string == null ? string3 + "else {consume(); goto tryAgain;}" : string3 + "else {" + string2 + "\t\t\t\t\tcommit();" + string2 + "\t\t\t\t\ttry {m" + string + "(false);}" + string2 + "\t\t\t\t\tcatch(" + namespaceAntlr + "RecognitionException& e) {" + string2 + "\t\t\t\t\t\t// catastrophic failure" + string2 + "\t\t\t\t\t\treportError(e);" + string2 + "\t\t\t\t\t\tconsume();" + string2 + "\t\t\t\t\t}" + string2 + "\t\t\t\t\tgoto tryAgain;" + string2 + "\t\t\t\t}") : string3 + "else {" + this.throwNoViable + "}";
        this.genBlockFinish((CppBlockFinishingInfo)object, string3);
        if (((LexerGrammar)this.grammar).filterMode && string != null) {
            this.println("commit();");
        }
        this.println("if ( !_returnToken )" + string2 + "\t\t\t\tgoto tryAgain; // found SKIP token" + string2);
        this.println("_ttype = _returnToken->getType();");
        if (((LexerGrammar)this.grammar).getTestLiterals()) {
            this.genLiteralsTest();
        }
        this.println("_returnToken->setType(_ttype);");
        this.println("return _returnToken;");
        --this.tabs;
        this.println("}");
        this.println("catch (" + namespaceAntlr + "RecognitionException& e) {");
        ++this.tabs;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (string == null) {
                this.println("if ( !getCommitToPath() ) {");
                ++this.tabs;
                this.println("consume();");
                this.println("goto tryAgain;");
                --this.tabs;
                this.println("}");
            } else {
                this.println("if ( !getCommitToPath() ) {");
                ++this.tabs;
                this.println("rewind(_m);");
                this.println("resetText();");
                this.println("try {m" + string + "(false);}");
                this.println("catch(" + namespaceAntlr + "RecognitionException& ee) {");
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
            this.println("throw " + namespaceAntlr + "TokenStreamRecognitionException(e);");
            --this.tabs;
        }
        --this.tabs;
        this.println("}");
        this.println("catch (" + namespaceAntlr + "CharStreamIOException& csie) {");
        this.println("\tthrow " + namespaceAntlr + "TokenStreamIOException(csie.io);");
        this.println("}");
        this.println("catch (" + namespaceAntlr + "CharStreamException& cse) {");
        this.println("\tthrow " + namespaceAntlr + "TokenStreamException(cse.getMessage());");
        this.println("}");
        this._println("tryAgain:;");
        --this.tabs;
        this.println("}");
        --this.tabs;
        this.println("}");
        this.println("");
    }

    public void genRule(RuleSymbol ruleSymbol, boolean bl, int n, String string) {
        Object object;
        Object object2;
        Object object3;
        RuleBlock ruleBlock;
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
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
        if (ruleBlock.returnAction != null) {
            this._print(this.extractTypeOfAction(ruleBlock.returnAction, ruleBlock.getLine(), ruleBlock.getColumn()) + " ");
        } else {
            this._print("void ");
        }
        this._print(string + ruleSymbol.getId() + "(");
        this._print(this.commonExtraParams);
        if (this.commonExtraParams.length() != 0 && ruleBlock.argAction != null) {
            this._print(",");
        }
        if (ruleBlock.argAction != null) {
            this._println("");
            ++this.tabs;
            object3 = ruleBlock.argAction;
            object2 = "";
            object = "";
            int n2 = ((String)object3).indexOf(61);
            if (n2 != -1) {
                int n3 = 0;
                while (n3 != -1 && n2 != -1) {
                    object2 = (String)object2 + (String)object + ((String)object3).substring(0, n2).trim();
                    object = ", ";
                    n3 = ((String)object3).indexOf(44, n2);
                    if (n3 == -1 || (n2 = ((String)(object3 = ((String)object3).substring(n3 + 1).trim())).indexOf(61)) != -1) continue;
                    object2 = (String)object2 + (String)object + (String)object3;
                }
            } else {
                object2 = object3;
            }
            this.println((String)object2);
            --this.tabs;
            this.print(") ");
        } else {
            this._print(") ");
        }
        this._println("{");
        ++this.tabs;
        if (this.grammar.traceRules) {
            if (this.grammar instanceof TreeWalkerGrammar) {
                if (this.usingCustomAST) {
                    this.println("Tracer traceInOut(this,\"" + ruleSymbol.getId() + "\"," + namespaceAntlr + "RefAST" + "(_t));");
                } else {
                    this.println("Tracer traceInOut(this,\"" + ruleSymbol.getId() + "\",_t);");
                }
            } else {
                this.println("Tracer traceInOut(this, \"" + ruleSymbol.getId() + "\");");
            }
        }
        if (ruleBlock.returnAction != null) {
            this.genLineNo(ruleBlock);
            this.println(ruleBlock.returnAction + ";");
            this.genLineNo2();
        }
        if (!this.commonLocalVars.equals("")) {
            this.println(this.commonLocalVars);
        }
        if (this.grammar instanceof LexerGrammar) {
            if (ruleSymbol.getId().equals("mEOF")) {
                this.println("_ttype = " + namespaceAntlr + "Token::EOF_TYPE;");
            } else {
                this.println("_ttype = " + ruleSymbol.getId().substring(1) + ";");
            }
            this.println(namespaceStd + "string::size_type _saveIndex;");
        }
        if (this.grammar.debuggingOutput) {
            if (this.grammar instanceof ParserGrammar) {
                this.println("fireEnterRule(" + n + ",0);");
            } else if (this.grammar instanceof LexerGrammar) {
                this.println("fireEnterRule(" + n + ",_ttype);");
            }
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST_in = (_t == " + this.labeledElementASTType + "(ASTNULL)) ? " + this.labeledElementASTInit + " : _t;");
        }
        if (this.grammar.buildAST) {
            this.println("returnAST = " + this.labeledElementASTInit + ";");
            this.println(namespaceAntlr + "ASTPair currentAST;");
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST = " + this.labeledElementASTInit + ";");
        }
        this.genBlockPreamble(ruleBlock);
        this.genBlockInitAction(ruleBlock);
        this.println("");
        object3 = ruleBlock.findExceptionSpec("");
        if (object3 != null || ruleBlock.getDefaultErrorHandler()) {
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
            this.genBlockFinish((CppBlockFinishingInfo)object, this.throwNoViable);
        }
        if (object3 != null || ruleBlock.getDefaultErrorHandler()) {
            --this.tabs;
            this.println("}");
        }
        if (object3 != null) {
            this.genErrorHandler((ExceptionSpec)object3);
        } else if (ruleBlock.getDefaultErrorHandler()) {
            this.println("catch (" + this.exceptionThrown + "& ex) {");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if( inputState->guessing == 0 ) {");
                ++this.tabs;
            }
            this.println("reportError(ex);");
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                Lookahead lookahead = this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode);
                object = this.getBitsetName(this.markBitsetForGen(lookahead.fset));
                this.println("recover(ex," + (String)object + ");");
            } else {
                this.println("if ( _t != " + this.labeledElementASTInit + " )");
                ++this.tabs;
                this.println("_t = _t->getNextSibling();");
                --this.tabs;
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("} else {");
                ++this.tabs;
                this.println("throw;");
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
            this.println("_retTree = _t;");
        }
        if (ruleBlock.getTestLiterals()) {
            if (ruleSymbol.access.equals("protected")) {
                this.genLiteralsTestForPartialToken();
            } else {
                this.genLiteralsTest();
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            this.println("if ( _createToken && _token==" + namespaceAntlr + "nullToken && _ttype!=" + namespaceAntlr + "Token::SKIP ) {");
            this.println("   _token = makeToken(_ttype);");
            this.println("   _token->setText(text.substr(_begin, text.length()-_begin));");
            this.println("}");
            this.println("_returnToken = _token;");
            this.println("_saveIndex=0;");
        }
        if (ruleBlock.returnAction != null) {
            this.println("return " + this.extractIdOfAction(ruleBlock.returnAction, ruleBlock.getLine(), ruleBlock.getColumn()) + ";");
        }
        --this.tabs;
        this.println("}");
        this.println("");
        this.genAST = bl2;
    }

    public void genRuleHeader(RuleSymbol ruleSymbol, boolean bl) {
        RuleBlock ruleBlock;
        this.tabs = 1;
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genRuleHeader(" + ruleSymbol.getId() + ")");
        }
        if (!ruleSymbol.isDefined()) {
            this.antlrTool.error("undefined rule: " + ruleSymbol.getId());
            return;
        }
        this.currentRule = ruleBlock = ruleSymbol.getBlock();
        this.currentASTResult = ruleSymbol.getId();
        boolean bl2 = this.genAST;
        this.genAST = this.genAST && ruleBlock.getAutoGen();
        this.saveText = ruleBlock.getAutoGen();
        this.print(ruleSymbol.access + ": ");
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
        this._println(";");
        --this.tabs;
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
            String string = this.processActionForSpecialSymbols(ruleRefElement.args, ruleRefElement.line, this.currentRule, actionTransInfo);
            if (actionTransInfo.assignToRoot || actionTransInfo.refRuleRoot != null) {
                this.antlrTool.error("Arguments of rule reference '" + ruleRefElement.targetRule + "' cannot set or ref #" + this.currentRule.getRuleName() + " on line " + ruleRefElement.getLine());
            }
            this._print(string);
            if (ruleSymbol.block.argAction == null) {
                this.antlrTool.warning("Rule '" + ruleRefElement.targetRule + "' accepts no arguments", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            }
        }
        this._println(");");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _retTree;");
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
        ++this.tabs;
        this.println("throw " + namespaceAntlr + "SemanticException(\"" + string2 + "\");");
        --this.tabs;
    }

    protected void genSemPredMap(String string) {
        Enumeration enumeration = this.semPreds.elements();
        this.println("const char* " + string + "_semPredNames[] = {");
        ++this.tabs;
        while (enumeration.hasMoreElements()) {
            this.println("\"" + enumeration.nextElement() + "\",");
        }
        this.println("0");
        --this.tabs;
        this.println("};");
    }

    protected void genSynPred(SynPredBlock synPredBlock, String string) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen=>(" + synPredBlock + ")");
        }
        this.println("bool synPredMatched" + synPredBlock.ID + " = false;");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if (_t == " + this.labeledElementASTInit + " )");
            ++this.tabs;
            this.println("_t = ASTNULL;");
            --this.tabs;
        }
        this.println("if (" + string + ") {");
        ++this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(this.labeledElementType + " __t" + synPredBlock.ID + " = _t;");
        } else {
            this.println("int _m" + synPredBlock.ID + " = mark();");
        }
        this.println("synPredMatched" + synPredBlock.ID + " = true;");
        this.println("inputState->guessing++;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("fireSyntacticPredicateStarted();");
        }
        ++this.syntacticPredLevel;
        this.println("try {");
        ++this.tabs;
        this.gen(synPredBlock);
        --this.tabs;
        this.println("}");
        this.println("catch (" + this.exceptionThrown + "& pe) {");
        ++this.tabs;
        this.println("synPredMatched" + synPredBlock.ID + " = false;");
        --this.tabs;
        this.println("}");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = __t" + synPredBlock.ID + ";");
        } else {
            this.println("rewind(_m" + synPredBlock.ID + ");");
        }
        this.println("inputState->guessing--;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("if (synPredMatched" + synPredBlock.ID + ")");
            this.println("  fireSyntacticPredicateSucceeded();");
            this.println("else");
            this.println("  fireSyntacticPredicateFailed();");
        }
        --this.syntacticPredLevel;
        --this.tabs;
        this.println("}");
        this.println("if ( synPredMatched" + synPredBlock.ID + " ) {");
    }

    public void genTokenStrings(String string) {
        this.println("const char* " + string + "tokenNames[] = {");
        ++this.tabs;
        Vector vector = this.grammar.tokenManager.getVocabulary();
        for (int i = 0; i < vector.size(); ++i) {
            TokenSymbol tokenSymbol;
            String string2 = (String)vector.elementAt(i);
            if (string2 == null) {
                string2 = "<" + String.valueOf(i) + ">";
            }
            if (!string2.startsWith("\"") && !string2.startsWith("<") && (tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string2)) != null && tokenSymbol.getParaphrase() != null) {
                string2 = StringUtils.stripFrontBack(tokenSymbol.getParaphrase(), "\"", "\"");
            }
            this.print(this.charFormatter.literalString(string2));
            this._println(",");
        }
        this.println("0");
        --this.tabs;
        this.println("};");
    }

    protected void genTokenTypes(TokenManager tokenManager) throws IOException {
        this.outputFile = tokenManager.getName() + TokenTypesFileSuffix + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.tabs = 0;
        this.println("#ifndef INC_" + tokenManager.getName() + TokenTypesFileSuffix + "_hpp_");
        this.println("#define INC_" + tokenManager.getName() + TokenTypesFileSuffix + "_hpp_");
        this.println("");
        if (nameSpace != null) {
            nameSpace.emitDeclarations(this.currentOutput);
        }
        this.genHeader(this.outputFile);
        this.println("");
        this.println("#ifndef CUSTOM_API");
        this.println("# define CUSTOM_API");
        this.println("#endif");
        this.println("");
        this.println("#ifdef __cplusplus");
        this.println("struct CUSTOM_API " + tokenManager.getName() + TokenTypesFileSuffix + " {");
        this.println("#endif");
        ++this.tabs;
        this.println("enum {");
        ++this.tabs;
        Vector vector = tokenManager.getVocabulary();
        this.println("EOF_ = 1,");
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
                    this.println(stringLiteralSymbol.label + " = " + i + ",");
                    continue;
                }
                String string2 = this.mangleLiteral(string);
                if (string2 != null) {
                    this.println(string2 + " = " + i + ",");
                    stringLiteralSymbol.label = string2;
                    continue;
                }
                this.println("// " + string + " = " + i);
                continue;
            }
            if (string.startsWith("<")) continue;
            this.println(string + " = " + i + ",");
        }
        this.println("NULL_TREE_LOOKAHEAD = 3");
        --this.tabs;
        this.println("};");
        --this.tabs;
        this.println("#ifdef __cplusplus");
        this.println("};");
        this.println("#endif");
        if (nameSpace != null) {
            nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + tokenManager.getName() + TokenTypesFileSuffix + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
        this.exitIfError();
    }

    public String processStringForASTConstructor(String string) {
        if (this.usingCustomAST && (this.grammar instanceof TreeWalkerGrammar || this.grammar instanceof ParserGrammar) && !this.grammar.tokenManager.tokenDefined(string)) {
            return namespaceAntlr + "RefAST(" + string + ")";
        }
        return string;
    }

    public String getASTCreateString(Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(this.labeledElementASTType + "(astFactory->make((new " + namespaceAntlr + "ASTArray(" + vector.size() + "))");
        for (int i = 0; i < vector.size(); ++i) {
            stringBuffer.append("->add(" + vector.elementAt(i) + ")");
        }
        stringBuffer.append("))");
        return stringBuffer.toString();
    }

    public String getASTCreateString(GrammarAtom grammarAtom, String string) {
        if (grammarAtom != null && grammarAtom.getASTNodeType() != null) {
            this.astTypes.ensureCapacity(grammarAtom.getType());
            String string2 = (String)this.astTypes.elementAt(grammarAtom.getType());
            if (string2 == null) {
                this.astTypes.setElementAt(grammarAtom.getASTNodeType(), grammarAtom.getType());
            } else if (!grammarAtom.getASTNodeType().equals(string2)) {
                this.antlrTool.warning("Attempt to redefine AST type for " + grammarAtom.getText(), this.grammar.getFilename(), grammarAtom.getLine(), grammarAtom.getColumn());
                this.antlrTool.warning(" from \"" + string2 + "\" to \"" + grammarAtom.getASTNodeType() + "\" sticking to \"" + string2 + "\"", this.grammar.getFilename(), grammarAtom.getLine(), grammarAtom.getColumn());
            } else {
                this.astTypes.setElementAt(grammarAtom.getASTNodeType(), grammarAtom.getType());
            }
            return "astFactory->create(" + string + ")";
        }
        boolean bl = false;
        if (string.indexOf(44) != -1) {
            bl = this.grammar.tokenManager.tokenDefined(string.substring(0, string.indexOf(44)));
        }
        if (this.usingCustomAST && this.grammar instanceof TreeWalkerGrammar && !this.grammar.tokenManager.tokenDefined(string) && !bl) {
            return "astFactory->create(" + namespaceAntlr + "RefAST(" + string + "))";
        }
        return "astFactory->create(" + string + ")";
    }

    public String getASTCreateString(String string) {
        if (this.usingCustomAST) {
            return this.labeledElementASTType + "(astFactory->create(" + namespaceAntlr + "RefAST(" + string + ")))";
        }
        return "astFactory->create(" + string + ")";
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
            return "true";
        }
        return "(" + this.getLookaheadTestExpression(alternative.cache, n2) + ")";
    }

    protected String getLookaheadTestTerm(int n, BitSet bitSet) {
        String string = this.lookaheadString(n);
        int[] nArray = bitSet.toArray();
        if (CppCodeGenerator.elementsAreRange(nArray)) {
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
                stringBuffer.append(" || ");
            }
            stringBuffer.append(string);
            stringBuffer.append(" == ");
            stringBuffer.append(string2);
        }
        return stringBuffer.toString();
    }

    public String getRangeExpression(int n, int[] nArray) {
        if (!CppCodeGenerator.elementsAreRange(nArray)) {
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
                string = string2.equals("EOF") ? namespaceAntlr + "Token::EOF_TYPE" : string2;
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
            return "_t->getType()";
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
            }
            if (string2.length() > 3 && string2.lastIndexOf("_in") == string2.length() - 3) {
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

    private String fixNameSpaceOption(String string) {
        if ((string = StringUtils.stripFrontBack(string, "\"", "\"")).length() > 2 && !string.substring(string.length() - 2, string.length()).equals("::")) {
            string = string + "::";
        }
        return string;
    }

    private void setupGrammarParameters(Grammar grammar) {
        String string;
        Token token;
        if (grammar instanceof ParserGrammar || grammar instanceof LexerGrammar || grammar instanceof TreeWalkerGrammar) {
            if (this.antlrTool.nameSpace != null) {
                nameSpace = this.antlrTool.nameSpace;
            }
            if (this.antlrTool.namespaceStd != null) {
                namespaceStd = this.fixNameSpaceOption(this.antlrTool.namespaceStd);
            }
            if (this.antlrTool.namespaceAntlr != null) {
                namespaceAntlr = this.fixNameSpaceOption(this.antlrTool.namespaceAntlr);
            }
            this.genHashLines = this.antlrTool.genHashLines;
            if (grammar.hasOption("namespace") && (token = grammar.getOption("namespace")) != null) {
                nameSpace = new NameSpace(token.getText());
            }
            if (grammar.hasOption("namespaceAntlr") && (token = grammar.getOption("namespaceAntlr")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                if (string.length() > 2 && !string.substring(string.length() - 2, string.length()).equals("::")) {
                    string = string + "::";
                }
                namespaceAntlr = string;
            }
            if (grammar.hasOption("namespaceStd") && (token = grammar.getOption("namespaceStd")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                if (string.length() > 2 && !string.substring(string.length() - 2, string.length()).equals("::")) {
                    string = string + "::";
                }
                namespaceStd = string;
            }
            if (grammar.hasOption("genHashLines") && (token = grammar.getOption("genHashLines")) != null) {
                string = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
                this.genHashLines = string.equals("true");
            }
            this.noConstructors = this.antlrTool.noConstructors;
            if (grammar.hasOption("noConstructors")) {
                token = grammar.getOption("noConstructors");
                if (token != null && !token.getText().equals("true") && !token.getText().equals("false")) {
                    this.antlrTool.error("noConstructors option must be true or false", this.antlrTool.getGrammarFile(), token.getLine(), token.getColumn());
                }
                this.noConstructors = token.getText().equals("true");
            }
        }
        if (grammar instanceof ParserGrammar) {
            this.labeledElementASTType = namespaceAntlr + "RefAST";
            this.labeledElementASTInit = namespaceAntlr + "nullAST";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.usingCustomAST = true;
                this.labeledElementASTType = string;
                this.labeledElementASTInit = string + "(" + namespaceAntlr + "nullAST)";
            }
            this.labeledElementType = namespaceAntlr + "RefToken ";
            this.labeledElementInit = namespaceAntlr + "nullToken";
            this.commonExtraArgs = "";
            this.commonExtraParams = "";
            this.commonLocalVars = "";
            this.lt1Value = "LT(1)";
            this.exceptionThrown = namespaceAntlr + "RecognitionException";
            this.throwNoViable = "throw " + namespaceAntlr + "NoViableAltException(LT(1), getFilename());";
        } else if (grammar instanceof LexerGrammar) {
            this.labeledElementType = "char ";
            this.labeledElementInit = "'\\0'";
            this.commonExtraArgs = "";
            this.commonExtraParams = "bool _createToken";
            this.commonLocalVars = "int _ttype; " + namespaceAntlr + "RefToken _token; " + namespaceStd + "string::size_type _begin = text.length();";
            this.lt1Value = "LA(1)";
            this.exceptionThrown = namespaceAntlr + "RecognitionException";
            this.throwNoViable = "throw " + namespaceAntlr + "NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());";
        } else if (grammar instanceof TreeWalkerGrammar) {
            this.labeledElementInit = namespaceAntlr + "nullAST";
            this.labeledElementASTInit = namespaceAntlr + "nullAST";
            this.labeledElementASTType = namespaceAntlr + "RefAST";
            this.labeledElementType = namespaceAntlr + "RefAST";
            this.commonExtraParams = namespaceAntlr + "RefAST _t";
            this.throwNoViable = "throw " + namespaceAntlr + "NoViableAltException(_t);";
            this.lt1Value = "_t";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.usingCustomAST = true;
                this.labeledElementASTType = string;
                this.labeledElementType = string;
                this.labeledElementASTInit = this.labeledElementInit = string + "(" + namespaceAntlr + "nullAST)";
                this.commonExtraParams = string + " _t";
                this.throwNoViable = "throw " + namespaceAntlr + "NoViableAltException(" + namespaceAntlr + "RefAST(_t));";
                this.lt1Value = "_t";
            }
            if (!grammar.hasOption("ASTLabelType")) {
                grammar.setOption("ASTLabelType", new Token(6, namespaceAntlr + "RefAST"));
            }
            this.commonExtraArgs = "_t";
            this.commonLocalVars = "";
            this.exceptionThrown = namespaceAntlr + "RecognitionException";
        } else {
            this.antlrTool.panic("Unknown grammar type");
        }
    }
}

