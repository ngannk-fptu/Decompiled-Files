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
import groovyjarjarantlr.CharLiteralElement;
import groovyjarjarantlr.CharRangeElement;
import groovyjarjarantlr.CharStreamException;
import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.DefaultJavaCodeGeneratorPrintWriterManager;
import groovyjarjarantlr.ExceptionHandler;
import groovyjarjarantlr.ExceptionSpec;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.GrammarSymbol;
import groovyjarjarantlr.JavaBlockFinishingInfo;
import groovyjarjarantlr.JavaCharFormatter;
import groovyjarjarantlr.JavaCodeGeneratorPrintWriterManager;
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
import groovyjarjarantlr.actions.java.ActionLexer;
import groovyjarjarantlr.collections.impl.BitSet;
import groovyjarjarantlr.collections.impl.Vector;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

public class JavaCodeGenerator
extends CodeGenerator {
    public static final int NO_MAPPING = -999;
    public static final int CONTINUE_LAST_MAPPING = -888;
    private JavaCodeGeneratorPrintWriterManager printWriterManager;
    private int defaultLine = -999;
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
    RuleBlock currentRule;
    String currentASTResult;
    Hashtable treeVariableMap = new Hashtable();
    Hashtable declaredASTVariables = new Hashtable();
    int astVarNumber = 1;
    protected static final String NONUNIQUE = new String();
    public static final int caseSizeThreshold = 127;
    private Vector semPreds;

    public JavaCodeGenerator() {
        this.charFormatter = new JavaCharFormatter();
    }

    protected void printAction(String string) {
        this.printAction(string, this.defaultLine);
    }

    protected void printAction(String string, int n) {
        this.getPrintWriterManager().startMapping(n);
        super.printAction(string);
        this.getPrintWriterManager().endMapping();
    }

    public void println(String string) {
        this.println(string, this.defaultLine);
    }

    public void println(String string, int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startSingleSourceLineMapping(n);
        }
        super.println(string);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
    }

    protected void print(String string) {
        this.print(string, this.defaultLine);
    }

    protected void print(String string, int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startMapping(n);
        }
        super.print(string);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
    }

    protected void _print(String string) {
        this._print(string, this.defaultLine);
    }

    protected void _print(String string, int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startMapping(n);
        }
        super._print(string);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
    }

    protected void _println(String string) {
        this._println(string, this.defaultLine);
    }

    protected void _println(String string, int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startMapping(n);
        }
        super._println(string);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(ActionElement actionElement) {
        int n = this.defaultLine;
        try {
            this.defaultLine = actionElement.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("genAction(" + actionElement + ")");
            }
            if (actionElement.isSemPred) {
                this.genSemPred(actionElement.actionText, actionElement.line);
            } else {
                if (this.grammar.hasSyntacticPredicate) {
                    this.println("if ( inputState.guessing==0 ) {");
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
                    this.println("currentAST.child = " + actionTransInfo.refRuleRoot + "!=null &&" + actionTransInfo.refRuleRoot + ".getFirstChild()!=null ?", -999);
                    ++this.tabs;
                    this.println(actionTransInfo.refRuleRoot + ".getFirstChild() : " + actionTransInfo.refRuleRoot + ";");
                    --this.tabs;
                    this.println("currentAST.advanceChildToEnd();");
                }
                if (this.grammar.hasSyntacticPredicate) {
                    --this.tabs;
                    this.println("}", -999);
                }
            }
        }
        finally {
            this.defaultLine = n;
        }
    }

    public void gen(AlternativeBlock alternativeBlock) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen(" + alternativeBlock + ")");
        }
        this.println("{", -999);
        this.genBlockPreamble(alternativeBlock);
        this.genBlockInitAction(alternativeBlock);
        String string = this.currentASTResult;
        if (alternativeBlock.getLabel() != null) {
            this.currentASTResult = alternativeBlock.getLabel();
        }
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(alternativeBlock);
        JavaBlockFinishingInfo javaBlockFinishingInfo = this.genCommonBlock(alternativeBlock, true);
        this.genBlockFinish(javaBlockFinishingInfo, this.throwNoViable, alternativeBlock.getLine());
        this.println("}", -999);
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
            this.println(charLiteralElement.getLabel() + " = " + this.lt1Value + ";", charLiteralElement.getLine());
        }
        boolean bl = this.saveText;
        this.saveText = this.saveText && charLiteralElement.getAutoGenType() == 1;
        this.genMatch(charLiteralElement);
        this.saveText = bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(CharRangeElement charRangeElement) {
        int n = this.defaultLine;
        try {
            boolean bl;
            this.defaultLine = charRangeElement.getLine();
            if (charRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(charRangeElement.getLabel() + " = " + this.lt1Value + ";");
            }
            boolean bl2 = bl = this.grammar instanceof LexerGrammar && (!this.saveText || charRangeElement.getAutoGenType() == 3);
            if (bl) {
                this.println("_saveIndex=text.length();");
            }
            this.println("matchRange(" + charRangeElement.beginText + "," + charRangeElement.endText + ");");
            if (bl) {
                this.println("text.setLength(_saveIndex);");
            }
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(LexerGrammar lexerGrammar) throws IOException {
        int n = this.defaultLine;
        try {
            GrammarSymbol grammarSymbol;
            Object object;
            Object object2;
            Object object3;
            this.defaultLine = -999;
            if (lexerGrammar.debuggingOutput) {
                this.semPreds = new Vector();
            }
            this.setGrammar(lexerGrammar);
            if (!(this.grammar instanceof LexerGrammar)) {
                this.antlrTool.panic("Internal error generating lexer");
            }
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, this.grammar);
            this.genAST = false;
            this.saveText = true;
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("import java.io.InputStream;");
            this.println("import antlr.TokenStreamException;");
            this.println("import antlr.TokenStreamIOException;");
            this.println("import antlr.TokenStreamRecognitionException;");
            this.println("import antlr.CharStreamException;");
            this.println("import antlr.CharStreamIOException;");
            this.println("import antlr.ANTLRException;");
            this.println("import java.io.Reader;");
            this.println("import java.util.Hashtable;");
            this.println("import antlr." + this.grammar.getSuperClass() + ";");
            this.println("import antlr.InputBuffer;");
            this.println("import antlr.ByteBuffer;");
            this.println("import antlr.CharBuffer;");
            this.println("import antlr.Token;");
            this.println("import antlr.CommonToken;");
            this.println("import antlr.RecognitionException;");
            this.println("import antlr.NoViableAltForCharException;");
            this.println("import antlr.MismatchedCharException;");
            this.println("import antlr.TokenStream;");
            this.println("import antlr.ANTLRHashString;");
            this.println("import antlr.LexerSharedInputState;");
            this.println("import antlr.collections.impl.BitSet;");
            this.println("import antlr.SemanticException;");
            this.println(this.grammar.preambleAction.getText());
            String string = null;
            string = this.grammar.superClass != null ? this.grammar.superClass : "groovyjarjarantlr." + this.grammar.getSuperClass();
            if (this.grammar.comment != null) {
                this._println(this.grammar.comment);
            }
            String string2 = "public";
            Token token = (Token)this.grammar.options.get("classHeaderPrefix");
            if (token != null && (object3 = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                string2 = object3;
            }
            this.print(string2 + " ");
            this.print("class " + this.grammar.getClassName() + " extends " + string);
            this.println(" implements " + this.grammar.tokenManager.getName() + TokenTypesFileSuffix + ", TokenStream");
            object3 = (Token)this.grammar.options.get("classHeaderSuffix");
            if (object3 != null && (object2 = StringUtils.stripFrontBack(((Token)object3).getText(), "\"", "\"")) != null) {
                this.print(", " + (String)object2);
            }
            this.println(" {");
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null), this.grammar.classMemberAction.getLine());
            this.println("public " + this.grammar.getClassName() + "(InputStream in) {");
            ++this.tabs;
            this.println("this(new ByteBuffer(in));");
            --this.tabs;
            this.println("}");
            this.println("public " + this.grammar.getClassName() + "(Reader in) {");
            ++this.tabs;
            this.println("this(new CharBuffer(in));");
            --this.tabs;
            this.println("}");
            this.println("public " + this.grammar.getClassName() + "(InputBuffer ib) {");
            ++this.tabs;
            if (this.grammar.debuggingOutput) {
                this.println("this(new LexerSharedInputState(new antlr.debug.DebuggingInputBuffer(ib)));");
            } else {
                this.println("this(new LexerSharedInputState(ib));");
            }
            --this.tabs;
            this.println("}");
            this.println("public " + this.grammar.getClassName() + "(LexerSharedInputState state) {");
            ++this.tabs;
            this.println("super(state);");
            if (this.grammar.debuggingOutput) {
                this.println("  ruleNames  = _ruleNames;");
                this.println("  semPredNames = _semPredNames;");
                this.println("  setupDebugging();");
            }
            this.println("caseSensitiveLiterals = " + lexerGrammar.caseSensitiveLiterals + ";");
            this.println("setCaseSensitive(" + lexerGrammar.caseSensitive + ");");
            this.println("literals = new Hashtable();");
            object2 = this.grammar.tokenManager.getTokenSymbolKeys();
            while (object2.hasMoreElements()) {
                TokenSymbol tokenSymbol;
                object = (String)object2.nextElement();
                if (((String)object).charAt(0) != '\"' || !((tokenSymbol = this.grammar.tokenManager.getTokenSymbol((String)object)) instanceof StringLiteralSymbol)) continue;
                grammarSymbol = (StringLiteralSymbol)tokenSymbol;
                this.println("literals.put(new ANTLRHashString(" + grammarSymbol.getId() + ", this), new Integer(" + ((TokenSymbol)grammarSymbol).getTokenType() + "));");
            }
            --this.tabs;
            this.println("}");
            if (this.grammar.debuggingOutput) {
                this.println("private static final String _ruleNames[] = {");
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
            int n2 = 0;
            while (object.hasMoreElements()) {
                grammarSymbol = (RuleSymbol)object.nextElement();
                if (!grammarSymbol.getId().equals("mnextToken")) {
                    this.genRule((RuleSymbol)grammarSymbol, false, n2++);
                }
                this.exitIfError();
            }
            if (this.grammar.debuggingOutput) {
                this.genSemPredMap();
            }
            this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
            this.println("");
            this.println("}");
            this.getPrintWriterManager().finishOutput();
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(OneOrMoreBlock oneOrMoreBlock) {
        int n = this.defaultLine;
        try {
            Object object;
            this.defaultLine = oneOrMoreBlock.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen+(" + oneOrMoreBlock + ")");
            }
            this.println("{", -999);
            this.genBlockPreamble(oneOrMoreBlock);
            String string = oneOrMoreBlock.getLabel() != null ? "_cnt_" + oneOrMoreBlock.getLabel() : "_cnt" + oneOrMoreBlock.ID;
            this.println("int " + string + "=0;");
            String string2 = oneOrMoreBlock.getLabel() != null ? oneOrMoreBlock.getLabel() : "_loop" + oneOrMoreBlock.ID;
            this.println(string2 + ":");
            this.println("do {");
            ++this.tabs;
            this.genBlockInitAction(oneOrMoreBlock);
            String string3 = this.currentASTResult;
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
                if (this.DEBUG_CODE_GENERATOR) {
                    System.out.println("nongreedy (...)+ loop; exit depth is " + oneOrMoreBlock.exitLookaheadDepth);
                }
                object = this.getLookaheadTestExpression(oneOrMoreBlock.exitCache, n2);
                this.println("// nongreedy exit test", -999);
                this.println("if ( " + string + ">=1 && " + (String)object + ") break " + string2 + ";", -888);
            }
            object = this.genCommonBlock(oneOrMoreBlock, false);
            this.genBlockFinish((JavaBlockFinishingInfo)object, "if ( " + string + ">=1 ) { break " + string2 + "; } else {" + this.throwNoViable + "}", oneOrMoreBlock.getLine());
            this.println(string + "++;");
            --this.tabs;
            this.println("} while (true);");
            this.println("}");
            this.currentASTResult = string3;
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(ParserGrammar parserGrammar) throws IOException {
        int n = this.defaultLine;
        try {
            GrammarSymbol grammarSymbol;
            int n2;
            Object object;
            Object object2;
            this.defaultLine = -999;
            if (parserGrammar.debuggingOutput) {
                this.semPreds = new Vector();
            }
            this.setGrammar(parserGrammar);
            if (!(this.grammar instanceof ParserGrammar)) {
                this.antlrTool.panic("Internal error generating parser");
            }
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, this.grammar);
            this.genAST = this.grammar.buildAST;
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("import antlr.TokenBuffer;");
            this.println("import antlr.TokenStreamException;");
            this.println("import antlr.TokenStreamIOException;");
            this.println("import antlr.ANTLRException;");
            this.println("import antlr." + this.grammar.getSuperClass() + ";");
            this.println("import antlr.Token;");
            this.println("import antlr.TokenStream;");
            this.println("import antlr.RecognitionException;");
            this.println("import antlr.NoViableAltException;");
            this.println("import antlr.MismatchedTokenException;");
            this.println("import antlr.SemanticException;");
            this.println("import antlr.ParserSharedInputState;");
            this.println("import antlr.collections.impl.BitSet;");
            if (this.genAST) {
                this.println("import antlr.collections.AST;");
                this.println("import java.util.Hashtable;");
                this.println("import antlr.ASTFactory;");
                this.println("import antlr.ASTPair;");
                this.println("import antlr.collections.impl.ASTArray;");
            }
            this.println(this.grammar.preambleAction.getText());
            String string = null;
            string = this.grammar.superClass != null ? this.grammar.superClass : "groovyjarjarantlr." + this.grammar.getSuperClass();
            if (this.grammar.comment != null) {
                this._println(this.grammar.comment);
            }
            String string2 = "public";
            Token token = (Token)this.grammar.options.get("classHeaderPrefix");
            if (token != null && (object2 = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                string2 = object2;
            }
            this.print(string2 + " ");
            this.print("class " + this.grammar.getClassName() + " extends " + string);
            this.println("       implements " + this.grammar.tokenManager.getName() + TokenTypesFileSuffix);
            object2 = (Token)this.grammar.options.get("classHeaderSuffix");
            if (object2 != null && (object = StringUtils.stripFrontBack(((Token)object2).getText(), "\"", "\"")) != null) {
                this.print(", " + (String)object);
            }
            this.println(" {");
            if (this.grammar.debuggingOutput) {
                this.println("private static final String _ruleNames[] = {");
                object = this.grammar.rules.elements();
                n2 = 0;
                while (object.hasMoreElements()) {
                    grammarSymbol = (GrammarSymbol)object.nextElement();
                    if (!(grammarSymbol instanceof RuleSymbol)) continue;
                    this.println("  \"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                }
                this.println("};");
            }
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null), this.grammar.classMemberAction.getLine());
            this.println("");
            this.println("protected " + this.grammar.getClassName() + "(TokenBuffer tokenBuf, int k) {");
            this.println("  super(tokenBuf,k);");
            this.println("  tokenNames = _tokenNames;");
            if (this.grammar.debuggingOutput) {
                this.println("  ruleNames  = _ruleNames;");
                this.println("  semPredNames = _semPredNames;");
                this.println("  setupDebugging(tokenBuf);");
            }
            if (this.grammar.buildAST) {
                this.println("  buildTokenTypeASTClassMap();");
                this.println("  astFactory = new ASTFactory(getTokenTypeToASTClassMap());");
            }
            this.println("}");
            this.println("");
            this.println("public " + this.grammar.getClassName() + "(TokenBuffer tokenBuf) {");
            this.println("  this(tokenBuf," + this.grammar.maxk + ");");
            this.println("}");
            this.println("");
            this.println("protected " + this.grammar.getClassName() + "(TokenStream lexer, int k) {");
            this.println("  super(lexer,k);");
            this.println("  tokenNames = _tokenNames;");
            if (this.grammar.debuggingOutput) {
                this.println("  ruleNames  = _ruleNames;");
                this.println("  semPredNames = _semPredNames;");
                this.println("  setupDebugging(lexer);");
            }
            if (this.grammar.buildAST) {
                this.println("  buildTokenTypeASTClassMap();");
                this.println("  astFactory = new ASTFactory(getTokenTypeToASTClassMap());");
            }
            this.println("}");
            this.println("");
            this.println("public " + this.grammar.getClassName() + "(TokenStream lexer) {");
            this.println("  this(lexer," + this.grammar.maxk + ");");
            this.println("}");
            this.println("");
            this.println("public " + this.grammar.getClassName() + "(ParserSharedInputState state) {");
            this.println("  super(state," + this.grammar.maxk + ");");
            this.println("  tokenNames = _tokenNames;");
            if (this.grammar.buildAST) {
                this.println("  buildTokenTypeASTClassMap();");
                this.println("  astFactory = new ASTFactory(getTokenTypeToASTClassMap());");
            }
            this.println("}");
            this.println("");
            object = this.grammar.rules.elements();
            n2 = 0;
            while (object.hasMoreElements()) {
                grammarSymbol = (GrammarSymbol)object.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                    this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n2++);
                }
                this.exitIfError();
            }
            this.genTokenStrings();
            if (this.grammar.buildAST) {
                this.genTokenASTNodeMap();
            }
            this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
            if (this.grammar.debuggingOutput) {
                this.genSemPredMap();
            }
            this.println("");
            this.println("}");
            this.getPrintWriterManager().finishOutput();
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(RuleRefElement ruleRefElement) {
        int n = this.defaultLine;
        try {
            RuleSymbol ruleSymbol;
            this.defaultLine = ruleRefElement.getLine();
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
                this.println("_saveIndex=text.length();");
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
                this.println("text.setLength(_saveIndex);");
            }
            if (this.syntacticPredLevel == 0) {
                boolean bl;
                boolean bl2 = bl = this.grammar.hasSyntacticPredicate && (this.grammar.buildAST && ruleRefElement.getLabel() != null || this.genAST && ruleRefElement.getAutoGenType() == 1);
                if (bl) {
                    // empty if block
                }
                if (this.grammar.buildAST && ruleRefElement.getLabel() != null) {
                    this.println(ruleRefElement.getLabel() + "_AST = (" + this.labeledElementASTType + ")returnAST;");
                }
                if (this.genAST) {
                    switch (ruleRefElement.getAutoGenType()) {
                        case 1: {
                            this.println("astFactory.addASTChild(currentAST, returnAST);");
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
                    // empty if block
                }
            }
            this.genErrorCatchForElement(ruleRefElement);
        }
        finally {
            this.defaultLine = n;
        }
    }

    public void gen(StringLiteralElement stringLiteralElement) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genString(" + stringLiteralElement + ")");
        }
        if (stringLiteralElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(stringLiteralElement.getLabel() + " = " + this.lt1Value + ";", stringLiteralElement.getLine());
        }
        this.genElementAST(stringLiteralElement);
        boolean bl = this.saveText;
        this.saveText = this.saveText && stringLiteralElement.getAutoGenType() == 1;
        this.genMatch(stringLiteralElement);
        this.saveText = bl;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();", stringLiteralElement.getLine());
        }
    }

    public void gen(TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value + ";", tokenRangeElement.getLine());
        }
        this.genElementAST(tokenRangeElement);
        this.println("matchRange(" + tokenRangeElement.beginText + "," + tokenRangeElement.endText + ");", tokenRangeElement.getLine());
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
            this.println(tokenRefElement.getLabel() + " = " + this.lt1Value + ";", tokenRefElement.getLine());
        }
        this.genElementAST(tokenRefElement);
        this.genMatch(tokenRefElement);
        this.genErrorCatchForElement(tokenRefElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();", tokenRefElement.getLine());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(TreeElement treeElement) {
        int n = this.defaultLine;
        try {
            this.defaultLine = treeElement.getLine();
            this.println("AST __t" + treeElement.ID + " = _t;");
            if (treeElement.root.getLabel() != null) {
                this.println(treeElement.root.getLabel() + " = _t==ASTNULL ? null :(" + this.labeledElementASTType + ")_t;", treeElement.root.getLine());
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
                this.println("if ( _t==null ) throw new MismatchedTokenException();", treeElement.root.getLine());
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
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        int n = this.defaultLine;
        try {
            Object object;
            Object object2;
            this.defaultLine = -999;
            this.setGrammar(treeWalkerGrammar);
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                this.antlrTool.panic("Internal error generating tree-walker");
            }
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, this.grammar);
            this.genAST = this.grammar.buildAST;
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("import antlr." + this.grammar.getSuperClass() + ";");
            this.println("import antlr.Token;");
            this.println("import antlr.collections.AST;");
            this.println("import antlr.RecognitionException;");
            this.println("import antlr.ANTLRException;");
            this.println("import antlr.NoViableAltException;");
            this.println("import antlr.MismatchedTokenException;");
            this.println("import antlr.SemanticException;");
            this.println("import antlr.collections.impl.BitSet;");
            this.println("import antlr.ASTPair;");
            this.println("import antlr.collections.impl.ASTArray;");
            this.println(this.grammar.preambleAction.getText());
            String string = null;
            string = this.grammar.superClass != null ? this.grammar.superClass : "groovyjarjarantlr." + this.grammar.getSuperClass();
            this.println("");
            if (this.grammar.comment != null) {
                this._println(this.grammar.comment);
            }
            String string2 = "public";
            Token token = (Token)this.grammar.options.get("classHeaderPrefix");
            if (token != null && (object2 = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                string2 = object2;
            }
            this.print(string2 + " ");
            this.print("class " + this.grammar.getClassName() + " extends " + string);
            this.println("       implements " + this.grammar.tokenManager.getName() + TokenTypesFileSuffix);
            object2 = (Token)this.grammar.options.get("classHeaderSuffix");
            if (object2 != null && (object = StringUtils.stripFrontBack(((Token)object2).getText(), "\"", "\"")) != null) {
                this.print(", " + (String)object);
            }
            this.println(" {");
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null), this.grammar.classMemberAction.getLine());
            this.println("public " + this.grammar.getClassName() + "() {");
            ++this.tabs;
            this.println("tokenNames = _tokenNames;");
            --this.tabs;
            this.println("}");
            this.println("");
            object = this.grammar.rules.elements();
            int n2 = 0;
            String string3 = "";
            while (object.hasMoreElements()) {
                GrammarSymbol grammarSymbol = (GrammarSymbol)object.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                    this.genRule(ruleSymbol, ruleSymbol.references.size() == 0, n2++);
                }
                this.exitIfError();
            }
            this.genTokenStrings();
            this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
            this.println("}");
            this.println("");
            this.getPrintWriterManager().finishOutput();
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(WildcardElement wildcardElement) {
        int n = this.defaultLine;
        try {
            this.defaultLine = wildcardElement.getLine();
            if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(wildcardElement.getLabel() + " = " + this.lt1Value + ";");
            }
            this.genElementAST(wildcardElement);
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if ( _t==null ) throw new MismatchedTokenException();");
            } else if (this.grammar instanceof LexerGrammar) {
                if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                    this.println("_saveIndex=text.length();");
                }
                this.println("matchNot(EOF_CHAR);");
                if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                    this.println("text.setLength(_saveIndex);");
                }
            } else {
                this.println("matchNot(" + this.getValueString(1) + ");");
            }
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t.getNextSibling();");
            }
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void gen(ZeroOrMoreBlock zeroOrMoreBlock) {
        int n = this.defaultLine;
        try {
            Object object;
            this.defaultLine = zeroOrMoreBlock.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen*(" + zeroOrMoreBlock + ")");
            }
            this.println("{");
            this.genBlockPreamble(zeroOrMoreBlock);
            String string = zeroOrMoreBlock.getLabel() != null ? zeroOrMoreBlock.getLabel() : "_loop" + zeroOrMoreBlock.ID;
            this.println(string + ":");
            this.println("do {");
            ++this.tabs;
            this.genBlockInitAction(zeroOrMoreBlock);
            String string2 = this.currentASTResult;
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
                object = this.getLookaheadTestExpression(zeroOrMoreBlock.exitCache, n2);
                this.println("// nongreedy exit test");
                this.println("if (" + (String)object + ") break " + string + ";");
            }
            object = this.genCommonBlock(zeroOrMoreBlock, false);
            this.genBlockFinish((JavaBlockFinishingInfo)object, "break " + string + ";", zeroOrMoreBlock.getLine());
            --this.tabs;
            this.println("} while (true);");
            this.println("}");
            this.currentASTResult = string2;
        }
        finally {
            this.defaultLine = n;
        }
    }

    protected void genAlt(Alternative alternative, AlternativeBlock alternativeBlock) {
        boolean bl = this.genAST;
        this.genAST = this.genAST && alternative.getAutoGen();
        boolean bl2 = this.saveText;
        this.saveText = this.saveText && alternative.getAutoGen();
        Hashtable hashtable = this.treeVariableMap;
        this.treeVariableMap = new Hashtable();
        if (alternative.exceptionSpec != null) {
            this.println("try {      // for error handling", alternative.head.getLine());
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
                if (this.grammar.hasSyntacticPredicate) {
                    // empty if block
                }
                this.println(ruleBlock.getRuleName() + "_AST = (" + this.labeledElementASTType + ")currentAST.root;", -888);
                if (this.grammar.hasSyntacticPredicate) {
                    // empty if block
                }
            } else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules not yet supported", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
            }
        }
        if (alternative.exceptionSpec != null) {
            --this.tabs;
            this.println("}", -999);
            this.genErrorHandler(alternative.exceptionSpec);
        }
        this.genAST = bl;
        this.saveText = bl2;
        this.treeVariableMap = hashtable;
    }

    protected void genBitsets(Vector vector, int n) {
        this.println("", -999);
        for (int i = 0; i < vector.size(); ++i) {
            BitSet bitSet = (BitSet)vector.elementAt(i);
            bitSet.growToInclude(n);
            this.genBitSet(bitSet, i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void genBitSet(BitSet bitSet, int n) {
        int n2 = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.println("private static final long[] mk" + this.getBitsetName(n) + "() {");
            int n3 = bitSet.lengthInLongWords();
            if (n3 < 8) {
                this.println("\tlong[] data = { " + bitSet.toStringOfWords() + "};");
            } else {
                this.println("\tlong[] data = new long[" + n3 + "];");
                long[] lArray = bitSet.toPackedArray();
                int n4 = 0;
                while (n4 < lArray.length) {
                    int n5;
                    if (lArray[n4] == 0L) {
                        ++n4;
                        continue;
                    }
                    if (n4 + 1 == lArray.length || lArray[n4] != lArray[n4 + 1]) {
                        this.println("\tdata[" + n4 + "]=" + lArray[n4] + "L;");
                        ++n4;
                        continue;
                    }
                    for (n5 = n4 + 1; n5 < lArray.length && lArray[n5] == lArray[n4]; ++n5) {
                    }
                    this.println("\tfor (int i = " + n4 + "; i<=" + (n5 - 1) + "; i++) { data[i]=" + lArray[n4] + "L; }");
                    n4 = n5;
                }
            }
            this.println("\treturn data;");
            this.println("}");
            this.println("public static final BitSet " + this.getBitsetName(n) + " = new BitSet(" + "mk" + this.getBitsetName(n) + "()" + ");");
        }
        finally {
            this.defaultLine = n2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void genBlockFinish(JavaBlockFinishingInfo javaBlockFinishingInfo, String string, int n) {
        int n2 = this.defaultLine;
        try {
            this.defaultLine = n;
            if (javaBlockFinishingInfo.needAnErrorClause && (javaBlockFinishingInfo.generatedAnIf || javaBlockFinishingInfo.generatedSwitch)) {
                if (javaBlockFinishingInfo.generatedAnIf) {
                    this.println("else {");
                } else {
                    this.println("{");
                }
                ++this.tabs;
                this.println(string);
                --this.tabs;
                this.println("}");
            }
            if (javaBlockFinishingInfo.postscript != null) {
                this.println(javaBlockFinishingInfo.postscript);
            }
        }
        finally {
            this.defaultLine = n2;
        }
    }

    protected void genBlockInitAction(AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.printAction(this.processActionForSpecialSymbols(alternativeBlock.initAction, alternativeBlock.getLine(), this.currentRule, null), alternativeBlock.getLine());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void genBlockPreamble(AlternativeBlock alternativeBlock) {
        if (alternativeBlock instanceof RuleBlock) {
            RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
            if (ruleBlock.labeledElements != null) {
                for (int i = 0; i < ruleBlock.labeledElements.size(); ++i) {
                    AlternativeElement alternativeElement = (AlternativeElement)ruleBlock.labeledElements.elementAt(i);
                    int n = this.defaultLine;
                    try {
                        this.defaultLine = alternativeElement.getLine();
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
                                this.println("Token " + alternativeElement.getLabel() + "=null;");
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
                        continue;
                    }
                    finally {
                        this.defaultLine = n;
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void genCases(BitSet bitSet, int n) {
        int n2 = this.defaultLine;
        try {
            this.defaultLine = n;
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("genCases(" + bitSet + ")");
            }
            int[] nArray = bitSet.toArray();
            int n3 = this.grammar instanceof LexerGrammar ? 4 : 1;
            int n4 = 1;
            boolean bl = true;
            for (int i = 0; i < nArray.length; ++i) {
                if (n4 == 1) {
                    this.print("");
                } else {
                    this._print("  ");
                }
                this._print("case " + this.getValueString(nArray[i]) + ":");
                if (n4 == n3) {
                    this._println("");
                    bl = true;
                    n4 = 1;
                    continue;
                }
                ++n4;
                bl = false;
            }
            if (!bl) {
                this._println("");
            }
        }
        finally {
            this.defaultLine = n2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JavaBlockFinishingInfo genCommonBlock(AlternativeBlock alternativeBlock, boolean bl) {
        int n = this.defaultLine;
        try {
            int n2;
            Object object;
            this.defaultLine = alternativeBlock.getLine();
            int n3 = 0;
            boolean bl2 = false;
            int n4 = 0;
            JavaBlockFinishingInfo javaBlockFinishingInfo = new JavaBlockFinishingInfo();
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
                    string = "_t,";
                }
                this.println("match(" + string + this.getBitsetName(this.markBitsetForGen(lookahead.fset)) + ");");
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("_t = _t.getNextSibling();");
                }
                JavaBlockFinishingInfo javaBlockFinishingInfo2 = javaBlockFinishingInfo;
                return javaBlockFinishingInfo2;
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
                    JavaBlockFinishingInfo javaBlockFinishingInfo3 = javaBlockFinishingInfo;
                    return javaBlockFinishingInfo3;
                }
            }
            int n5 = 0;
            for (int i = 0; i < alternativeBlock.getAlternatives().size(); ++i) {
                Alternative alternative = alternativeBlock.getAlternativeAt(i);
                if (!JavaCodeGenerator.suitableForCaseExpression(alternative)) continue;
                ++n5;
            }
            if (n5 >= this.makeSwitchThreshold) {
                String string = this.lookaheadString(1);
                bl2 = true;
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("if (_t==null) _t=ASTNULL;");
                }
                this.println("switch ( " + string + ") {");
                for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
                    Alternative alternative = alternativeBlock.getAlternativeAt(i);
                    if (!JavaCodeGenerator.suitableForCaseExpression(alternative)) continue;
                    object = alternative.cache[1];
                    if (((Lookahead)object).fset.degree() == 0 && !((Lookahead)object).containsEpsilon()) {
                        this.antlrTool.warning("Alternate omitted due to empty prediction set", this.grammar.getFilename(), alternative.head.getLine(), alternative.head.getColumn());
                        continue;
                    }
                    this.genCases(((Lookahead)object).fset, alternative.head.getLine());
                    this.println("{", alternative.head.getLine());
                    ++this.tabs;
                    this.genAlt(alternative, alternativeBlock);
                    this.println("break;", -999);
                    --this.tabs;
                    this.println("}", -999);
                }
                this.println("default:");
                ++this.tabs;
            }
            for (int i = n2 = this.grammar instanceof LexerGrammar ? this.grammar.maxk : 0; i >= 0; --i) {
                if (this.DEBUG_CODE_GENERATOR) {
                    System.out.println("checking depth " + i);
                }
                for (int j = 0; j < alternativeBlock.alternatives.size(); ++j) {
                    String string;
                    int n6;
                    object = alternativeBlock.getAlternativeAt(j);
                    if (this.DEBUG_CODE_GENERATOR) {
                        System.out.println("genAlt: " + j);
                    }
                    if (bl2 && JavaCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                        if (!this.DEBUG_CODE_GENERATOR) continue;
                        System.out.println("ignoring alt because it was in the switch");
                        continue;
                    }
                    boolean bl6 = false;
                    if (this.grammar instanceof LexerGrammar) {
                        n6 = ((Alternative)object).lookaheadDepth;
                        if (n6 == Integer.MAX_VALUE) {
                            n6 = this.grammar.maxk;
                        }
                        while (n6 >= 1 && ((Alternative)object).cache[n6].containsEpsilon()) {
                            --n6;
                        }
                        if (n6 != i) {
                            if (!this.DEBUG_CODE_GENERATOR) continue;
                            System.out.println("ignoring alt because effectiveDepth!=altDepth;" + n6 + "!=" + i);
                            continue;
                        }
                        bl6 = this.lookaheadIsEmpty((Alternative)object, n6);
                        string = this.getLookaheadTestExpression((Alternative)object, n6);
                    } else {
                        bl6 = this.lookaheadIsEmpty((Alternative)object, this.grammar.maxk);
                        string = this.getLookaheadTestExpression((Alternative)object, this.grammar.maxk);
                    }
                    n6 = this.defaultLine;
                    try {
                        this.defaultLine = ((Alternative)object).head.getLine();
                        if (((Alternative)object).cache[1].fset.degree() > 127 && JavaCodeGenerator.suitableForCaseExpression((Alternative)object)) {
                            if (n3 == 0) {
                                this.println("if " + string + " {");
                            } else {
                                this.println("else if " + string + " {");
                            }
                        } else if (bl6 && ((Alternative)object).semPred == null && ((Alternative)object).synPred == null) {
                            if (n3 == 0) {
                                this.println("{");
                            } else {
                                this.println("else {");
                            }
                            javaBlockFinishingInfo.needAnErrorClause = false;
                        } else {
                            if (((Alternative)object).semPred != null) {
                                ActionTransInfo actionTransInfo = new ActionTransInfo();
                                String string2 = this.processActionForSpecialSymbols(((Alternative)object).semPred, alternativeBlock.line, this.currentRule, actionTransInfo);
                                string = (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar) && this.grammar.debuggingOutput ? "(" + string + "&& fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.PREDICTING," + this.addSemPred(this.charFormatter.escapeString(string2)) + "," + string2 + "))" : "(" + string + "&&(" + string2 + "))";
                            }
                            if (n3 > 0) {
                                if (((Alternative)object).synPred != null) {
                                    this.println("else {", ((Alternative)object).synPred.getLine());
                                    ++this.tabs;
                                    this.genSynPred(((Alternative)object).synPred, string);
                                    ++n4;
                                } else {
                                    this.println("else if " + string + " {");
                                }
                            } else if (((Alternative)object).synPred != null) {
                                this.genSynPred(((Alternative)object).synPred, string);
                            } else {
                                if (this.grammar instanceof TreeWalkerGrammar) {
                                    this.println("if (_t==null) _t=ASTNULL;");
                                }
                                this.println("if " + string + " {");
                            }
                        }
                    }
                    finally {
                        this.defaultLine = n6;
                    }
                    ++n3;
                    ++this.tabs;
                    this.genAlt((Alternative)object, alternativeBlock);
                    --this.tabs;
                    this.println("}");
                }
            }
            String string = "";
            for (int i = 1; i <= n4; ++i) {
                string = string + "}";
            }
            this.genAST = bl3;
            this.saveText = bl4;
            if (bl2) {
                --this.tabs;
                javaBlockFinishingInfo.postscript = string + "}";
                javaBlockFinishingInfo.generatedSwitch = true;
                javaBlockFinishingInfo.generatedAnIf = n3 > 0;
            } else {
                javaBlockFinishingInfo.postscript = string;
                javaBlockFinishingInfo.generatedSwitch = false;
                javaBlockFinishingInfo.generatedAnIf = n3 > 0;
            }
            JavaBlockFinishingInfo javaBlockFinishingInfo4 = javaBlockFinishingInfo;
            return javaBlockFinishingInfo4;
        }
        finally {
            this.defaultLine = n;
        }
    }

    private static boolean suitableForCaseExpression(Alternative alternative) {
        return alternative.lookaheadDepth == 1 && alternative.semPred == null && !alternative.cache[1].containsEpsilon() && alternative.cache[1].fset.degree() <= 127;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void genElementAST(AlternativeElement alternativeElement) {
        int n = this.defaultLine;
        try {
            this.defaultLine = alternativeElement.getLine();
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
                            this.println("astFactory.addASTChild(currentAST, " + (String)object + ");");
                            break;
                        }
                        case 2: {
                            this.println("astFactory.makeASTRoot(currentAST, " + (String)object + ");");
                            break;
                        }
                    }
                }
                if (bl) {
                    // empty if block
                }
            }
        }
        finally {
            this.defaultLine = n;
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
            this.println("}", alternativeElement.getLine());
            this.genErrorHandler(exceptionSpec);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void genErrorHandler(ExceptionSpec exceptionSpec) {
        for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
            ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
            int n = this.defaultLine;
            try {
                this.defaultLine = exceptionHandler.action.getLine();
                this.println("catch (" + exceptionHandler.exceptionTypeAndName.getText() + ") {", exceptionHandler.exceptionTypeAndName.getLine());
                ++this.tabs;
                if (this.grammar.hasSyntacticPredicate) {
                    this.println("if (inputState.guessing==0) {");
                    ++this.tabs;
                }
                ActionTransInfo actionTransInfo = new ActionTransInfo();
                this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, actionTransInfo));
                if (this.grammar.hasSyntacticPredicate) {
                    --this.tabs;
                    this.println("} else {");
                    ++this.tabs;
                    this.println("throw " + this.extractIdOfAction(exceptionHandler.exceptionTypeAndName) + ";");
                    --this.tabs;
                    this.println("}");
                }
                --this.tabs;
                this.println("}");
                continue;
            }
            finally {
                this.defaultLine = n;
            }
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
            this.println("try { // for error handling", alternativeElement.getLine());
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
        this.println("// $ANTLR " + Tool.version + ": " + "\"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"" + " -> " + "\"" + this.grammar.getClassName() + ".java\"$", -999);
    }

    private void genLiteralsTest() {
        this.println("_ttype = testLiteralsTable(_ttype);");
    }

    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = testLiteralsTable(new String(text.getBuffer(),_begin,text.length()-_begin),_ttype);");
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void genMatchUsingAtomText(GrammarAtom grammarAtom) {
        int n = this.defaultLine;
        try {
            this.defaultLine = grammarAtom.getLine();
            String string = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                string = "_t,";
            }
            if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
                this.println("_saveIndex=text.length();");
            }
            this.print(grammarAtom.not ? "matchNot(" : "match(");
            this._print(string, -999);
            if (grammarAtom.atomText.equals("EOF")) {
                this._print("Token.EOF_TYPE");
            } else {
                this._print(grammarAtom.atomText);
            }
            this._println(");");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
                this.println("text.setLength(_saveIndex);");
            }
        }
        finally {
            this.defaultLine = n;
        }
    }

    protected void genMatchUsingAtomTokenType(GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            string = "_t,";
        }
        Object var3_3 = null;
        String string2 = string + this.getValueString(grammarAtom.getType());
        this.println((grammarAtom.not ? "matchNot(" : "match(") + string2 + ");", grammarAtom.getLine());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void genNextToken() {
        int n = this.defaultLine;
        try {
            Object object;
            Object object2;
            RuleSymbol ruleSymbol;
            this.defaultLine = -999;
            boolean bl = false;
            for (int i = 0; i < this.grammar.rules.size(); ++i) {
                ruleSymbol = (RuleSymbol)this.grammar.rules.elementAt(i);
                if (!ruleSymbol.isDefined() || !ruleSymbol.access.equals("public")) continue;
                bl = true;
                break;
            }
            if (!bl) {
                this.println("");
                this.println("public Token nextToken() throws TokenStreamException {");
                this.println("\ttry {uponEOF();}");
                this.println("\tcatch(CharStreamIOException csioe) {");
                this.println("\t\tthrow new TokenStreamIOException(csioe.io);");
                this.println("\t}");
                this.println("\tcatch(CharStreamException cse) {");
                this.println("\t\tthrow new TokenStreamException(cse.getMessage());");
                this.println("\t}");
                this.println("\treturn new CommonToken(Token.EOF_TYPE, \"\");");
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
            this.println("public Token nextToken() throws TokenStreamException {");
            ++this.tabs;
            this.println("Token theRetToken=null;");
            this._println("tryAgain:");
            this.println("for (;;) {");
            ++this.tabs;
            this.println("Token _token = null;");
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
            this.println("try {   // for char stream error handling");
            ++this.tabs;
            this.println("try {   // for lexical error handling");
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
            object = "if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}";
            object = (String)object + string3 + "\t\t\t\t";
            object = ((LexerGrammar)this.grammar).filterMode ? (string == null ? (String)object + "else {consume(); continue tryAgain;}" : (String)object + "else {" + string3 + "\t\t\t\t\tcommit();" + string3 + "\t\t\t\t\ttry {m" + string + "(false);}" + string3 + "\t\t\t\t\tcatch(RecognitionException e) {" + string3 + "\t\t\t\t\t\t// catastrophic failure" + string3 + "\t\t\t\t\t\treportError(e);" + string3 + "\t\t\t\t\t\tconsume();" + string3 + "\t\t\t\t\t}" + string3 + "\t\t\t\t\tcontinue tryAgain;" + string3 + "\t\t\t\t}") : (String)object + "else {" + this.throwNoViable + "}";
            this.genBlockFinish((JavaBlockFinishingInfo)object2, (String)object, ruleBlock.getLine());
            if (((LexerGrammar)this.grammar).filterMode && string != null) {
                this.println("commit();");
            }
            this.println("if ( _returnToken==null ) continue tryAgain; // found SKIP token");
            this.println("_ttype = _returnToken.getType();");
            if (((LexerGrammar)this.grammar).getTestLiterals()) {
                this.genLiteralsTest();
            }
            this.println("_returnToken.setType(_ttype);");
            this.println("return _returnToken;");
            --this.tabs;
            this.println("}");
            this.println("catch (RecognitionException e) {");
            ++this.tabs;
            if (((LexerGrammar)this.grammar).filterMode) {
                if (string == null) {
                    this.println("if ( !getCommitToPath() ) {consume(); continue tryAgain;}");
                } else {
                    this.println("if ( !getCommitToPath() ) {");
                    ++this.tabs;
                    this.println("rewind(_m);");
                    this.println("resetText();");
                    this.println("try {m" + string + "(false);}");
                    this.println("catch(RecognitionException ee) {");
                    this.println("\t// horrendous failure: error in filter rule");
                    this.println("\treportError(ee);");
                    this.println("\tconsume();");
                    this.println("}");
                    this.println("continue tryAgain;");
                    --this.tabs;
                    this.println("}");
                }
            }
            if (ruleBlock.getDefaultErrorHandler()) {
                this.println("reportError(e);");
                this.println("consume();");
            } else {
                this.println("throw new TokenStreamRecognitionException(e);");
            }
            --this.tabs;
            this.println("}");
            --this.tabs;
            this.println("}");
            this.println("catch (CharStreamException cse) {");
            this.println("\tif ( cse instanceof CharStreamIOException ) {");
            this.println("\t\tthrow new TokenStreamIOException(((CharStreamIOException)cse).io);");
            this.println("\t}");
            this.println("\telse {");
            this.println("\t\tthrow new TokenStreamException(cse.getMessage());");
            this.println("\t}");
            this.println("}");
            --this.tabs;
            this.println("}");
            --this.tabs;
            this.println("}");
            this.println("");
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void genRule(RuleSymbol ruleSymbol, boolean bl, int n) {
        this.tabs = 1;
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genRule(" + ruleSymbol.getId() + ")");
        }
        if (!ruleSymbol.isDefined()) {
            this.antlrTool.error("undefined rule: " + ruleSymbol.getId());
            return;
        }
        RuleBlock ruleBlock = ruleSymbol.getBlock();
        int n2 = this.defaultLine;
        try {
            Object object;
            Object object2;
            this.defaultLine = ruleBlock.getLine();
            this.currentRule = ruleBlock;
            this.currentASTResult = ruleSymbol.getId();
            this.declaredASTVariables.clear();
            boolean bl2 = this.genAST;
            this.genAST = this.genAST && ruleBlock.getAutoGen();
            this.saveText = ruleBlock.getAutoGen();
            if (ruleSymbol.comment != null) {
                this._println(ruleSymbol.comment);
            }
            this.print(ruleSymbol.access + " final ");
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
            this._print(" throws " + this.exceptionThrown);
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
            this._println(" {");
            ++this.tabs;
            if (ruleBlock.returnAction != null) {
                this.println(ruleBlock.returnAction + ";");
            }
            this.println(this.commonLocalVars);
            if (this.grammar.traceRules) {
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("traceIn(\"" + ruleSymbol.getId() + "\",_t);");
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
                this.println("int _saveIndex;");
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
                this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST_in = (_t == ASTNULL) ? null : (" + this.labeledElementASTType + ")_t;", -999);
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
                this.genBlockFinish((JavaBlockFinishingInfo)object, this.throwNoViable, ruleBlock.getLine());
            }
            if (exceptionSpec != null || ruleBlock.getDefaultErrorHandler()) {
                --this.tabs;
                this.println("}");
            }
            if (exceptionSpec != null) {
                this.genErrorHandler(exceptionSpec);
            } else if (ruleBlock.getDefaultErrorHandler()) {
                this.println("catch (" + this.exceptionThrown + " ex) {");
                ++this.tabs;
                if (this.grammar.hasSyntacticPredicate) {
                    this.println("if (inputState.guessing==0) {");
                    ++this.tabs;
                }
                this.println("reportError(ex);");
                if (!(this.grammar instanceof TreeWalkerGrammar)) {
                    object2 = this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode);
                    object = this.getBitsetName(this.markBitsetForGen(((Lookahead)object2).fset));
                    this.println("recover(ex," + (String)object + ");");
                } else {
                    this.println("if (_t!=null) {_t = _t.getNextSibling();}");
                }
                if (this.grammar.hasSyntacticPredicate) {
                    --this.tabs;
                    this.println("} else {");
                    this.println("  throw ex;");
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
                this.println("if ( _createToken && _token==null && _ttype!=Token.SKIP ) {");
                this.println("\t_token = makeToken(_ttype);");
                this.println("\t_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));");
                this.println("}");
                this.println("_returnToken = _token;");
            }
            if (ruleBlock.returnAction != null) {
                this.println("return " + this.extractIdOfAction(ruleBlock.returnAction, ruleBlock.getLine(), ruleBlock.getColumn()) + ";");
            }
            if (this.grammar.debuggingOutput || this.grammar.traceRules) {
                --this.tabs;
                this.println("} finally { // debugging");
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
        finally {
            this.defaultLine = n2;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void GenRuleInvocation(RuleRefElement ruleRefElement) {
        int n = this.defaultLine;
        try {
            this.defaultLine = ruleRefElement.getLine();
            this.getPrintWriterManager().startSingleSourceLineMapping(ruleRefElement.getLine());
            this._print(ruleRefElement.targetRule + "(");
            this.getPrintWriterManager().endMapping();
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
                this.println("_t = _retTree;");
            }
        }
        finally {
            this.defaultLine = n;
        }
    }

    protected void genSemPred(String string, int n) {
        ActionTransInfo actionTransInfo = new ActionTransInfo();
        string = this.processActionForSpecialSymbols(string, n, this.currentRule, actionTransInfo);
        String string2 = this.charFormatter.escapeString(string);
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            string = "fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.VALIDATING," + this.addSemPred(string2) + "," + string + ")";
        }
        this.println("if (!(" + string + "))", n);
        this.println("  throw new SemanticException(\"" + string2 + "\");", n);
    }

    protected void genSemPredMap() {
        Enumeration enumeration = this.semPreds.elements();
        this.println("private String _semPredNames[] = {", -999);
        while (enumeration.hasMoreElements()) {
            this.println("\"" + enumeration.nextElement() + "\",", -999);
        }
        this.println("};", -999);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void genSynPred(SynPredBlock synPredBlock, String string) {
        int n = this.defaultLine;
        try {
            this.defaultLine = synPredBlock.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen=>(" + synPredBlock + ")");
            }
            this.println("boolean synPredMatched" + synPredBlock.ID + " = false;");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if (_t==null) _t=ASTNULL;");
            }
            this.println("if (" + string + ") {");
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
            this.println("catch (" + this.exceptionThrown + " pe) {");
            ++this.tabs;
            this.println("synPredMatched" + synPredBlock.ID + " = false;");
            --this.tabs;
            this.println("}");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = __t" + synPredBlock.ID + ";");
            } else {
                this.println("rewind(_m" + synPredBlock.ID + ");");
            }
            this._println("inputState.guessing--;");
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
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void genTokenStrings() {
        int n = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.println("");
            this.println("public static final String[] _tokenNames = {");
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
                    this._print(",");
                }
                this._println("");
            }
            --this.tabs;
            this.println("};");
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void genTokenASTNodeMap() {
        int n = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.println("");
            this.println("protected void buildTokenTypeASTClassMap() {");
            ++this.tabs;
            boolean bl = false;
            int n2 = 0;
            Vector vector = this.grammar.tokenManager.getVocabulary();
            for (int i = 0; i < vector.size(); ++i) {
                TokenSymbol tokenSymbol;
                String string = (String)vector.elementAt(i);
                if (string == null || (tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string)) == null || tokenSymbol.getASTNodeType() == null) continue;
                ++n2;
                if (!bl) {
                    this.println("tokenTypeToASTClassMap = new Hashtable();");
                    bl = true;
                }
                this.println("tokenTypeToASTClassMap.put(new Integer(" + tokenSymbol.getTokenType() + "), " + tokenSymbol.getASTNodeType() + ".class);");
            }
            if (n2 == 0) {
                this.println("tokenTypeToASTClassMap=null;");
            }
            --this.tabs;
            this.println("};");
        }
        finally {
            this.defaultLine = n;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void genTokenTypes(TokenManager tokenManager) throws IOException {
        int n = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, tokenManager.getName() + TokenTypesFileSuffix);
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("public interface " + tokenManager.getName() + TokenTypesFileSuffix + " {");
            ++this.tabs;
            Vector vector = tokenManager.getVocabulary();
            this.println("int EOF = 1;");
            this.println("int NULL_TREE_LOOKAHEAD = 3;");
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
                        this.println("int " + stringLiteralSymbol.label + " = " + i + ";");
                        continue;
                    }
                    String string2 = this.mangleLiteral(string);
                    if (string2 != null) {
                        this.println("int " + string2 + " = " + i + ";");
                        stringLiteralSymbol.label = string2;
                        continue;
                    }
                    this.println("// " + string + " = " + i);
                    continue;
                }
                if (string.startsWith("<")) continue;
                this.println("int " + string + " = " + i + ";");
            }
            --this.tabs;
            this.println("}");
            this.getPrintWriterManager().finishOutput();
            this.exitIfError();
        }
        finally {
            this.defaultLine = n;
        }
    }

    public String getASTCreateString(Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("(" + this.labeledElementASTType + ")astFactory.make( (new ASTArray(" + vector.size() + "))");
        for (int i = 0; i < vector.size(); ++i) {
            stringBuffer.append(".add(" + vector.elementAt(i) + ")");
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    public String getASTCreateString(GrammarAtom grammarAtom, String string) {
        if (grammarAtom != null && grammarAtom.getASTNodeType() != null) {
            return "(" + grammarAtom.getASTNodeType() + ")" + "astFactory.create(" + string + ",\"" + grammarAtom.getASTNodeType() + "\")";
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
                    string4 = ",\"\"";
                }
                if (string3 != null) {
                    return "(" + string3 + ")" + "astFactory.create(" + string + string4 + ",\"" + string3 + "\")";
                }
            }
            if (this.labeledElementASTType.equals("AST")) {
                return "astFactory.create(" + string + ")";
            }
            return "(" + this.labeledElementASTType + ")" + "astFactory.create(" + string + ")";
        }
        return "(" + this.labeledElementASTType + ")astFactory.create(" + string + ")";
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
        if (JavaCodeGenerator.elementsAreRange(nArray)) {
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
        if (!JavaCodeGenerator.elementsAreRange(nArray)) {
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
            return "_t.getType()";
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
        if (grammar instanceof ParserGrammar) {
            String string;
            Token token;
            this.labeledElementASTType = "AST";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
                this.labeledElementASTType = string;
            }
            this.labeledElementType = "Token ";
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
            this.commonExtraParams = "boolean _createToken";
            this.commonLocalVars = "int _ttype; Token _token=null; int _begin=text.length();";
            this.lt1Value = "LA(1)";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());";
        } else if (grammar instanceof TreeWalkerGrammar) {
            String string;
            Token token;
            this.labeledElementASTType = "AST";
            this.labeledElementType = "AST";
            if (grammar.hasOption("ASTLabelType") && (token = grammar.getOption("ASTLabelType")) != null && (string = StringUtils.stripFrontBack(token.getText(), "\"", "\"")) != null) {
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
            this.lt1Value = "(" + this.labeledElementASTType + ")_t";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltException(_t);";
        } else {
            this.antlrTool.panic("Unknown grammar type");
        }
    }

    public JavaCodeGeneratorPrintWriterManager getPrintWriterManager() {
        if (this.printWriterManager == null) {
            this.printWriterManager = new DefaultJavaCodeGeneratorPrintWriterManager();
        }
        return this.printWriterManager;
    }

    public void setPrintWriterManager(JavaCodeGeneratorPrintWriterManager javaCodeGeneratorPrintWriterManager) {
        this.printWriterManager = javaCodeGeneratorPrintWriterManager;
    }

    public void setTool(Tool tool) {
        super.setTool(tool);
    }
}

