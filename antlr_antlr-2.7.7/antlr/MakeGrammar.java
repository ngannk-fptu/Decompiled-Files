/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.ANTLRLexer;
import antlr.ActionElement;
import antlr.Alternative;
import antlr.AlternativeBlock;
import antlr.AlternativeElement;
import antlr.BlockContext;
import antlr.BlockEndElement;
import antlr.CharLiteralElement;
import antlr.CharRangeElement;
import antlr.CodeGenerator;
import antlr.CommonToken;
import antlr.DefineGrammarSymbols;
import antlr.ExceptionHandler;
import antlr.ExceptionSpec;
import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.LLkAnalyzer;
import antlr.LexerGrammar;
import antlr.OneOrMoreBlock;
import antlr.RuleBlock;
import antlr.RuleEndElement;
import antlr.RuleRefElement;
import antlr.RuleSymbol;
import antlr.SemanticException;
import antlr.StringLiteralElement;
import antlr.StringUtils;
import antlr.SynPredBlock;
import antlr.Token;
import antlr.TokenRangeElement;
import antlr.TokenRefElement;
import antlr.TokenSymbol;
import antlr.Tool;
import antlr.TreeBlockContext;
import antlr.TreeElement;
import antlr.TreeWalkerGrammar;
import antlr.WildcardElement;
import antlr.ZeroOrMoreBlock;
import antlr.collections.Stack;
import antlr.collections.impl.LList;
import antlr.collections.impl.Vector;

public class MakeGrammar
extends DefineGrammarSymbols {
    protected Stack blocks = new LList();
    protected RuleRefElement lastRuleRef;
    protected RuleEndElement ruleEnd;
    protected RuleBlock ruleBlock;
    protected int nested = 0;
    protected boolean grammarError = false;
    ExceptionSpec currentExceptionSpec = null;

    public MakeGrammar(Tool tool, String[] stringArray, LLkAnalyzer lLkAnalyzer) {
        super(tool, stringArray, lLkAnalyzer);
    }

    public void abortGrammar() {
        String string = "unknown grammar";
        if (this.grammar != null) {
            string = this.grammar.getClassName();
        }
        this.tool.error("aborting grammar '" + string + "' due to errors");
        super.abortGrammar();
    }

    protected void addElementToCurrentAlt(AlternativeElement alternativeElement) {
        alternativeElement.enclosingRuleName = this.ruleBlock.ruleName;
        this.context().addAlternativeElement(alternativeElement);
    }

    public void beginAlt(boolean bl) {
        super.beginAlt(bl);
        Alternative alternative = new Alternative();
        alternative.setAutoGen(bl);
        this.context().block.addAlternative(alternative);
    }

    public void beginChildList() {
        super.beginChildList();
        this.context().block.addAlternative(new Alternative());
    }

    public void beginExceptionGroup() {
        super.beginExceptionGroup();
        if (!(this.context().block instanceof RuleBlock)) {
            this.tool.panic("beginExceptionGroup called outside of rule block");
        }
    }

    public void beginExceptionSpec(Token token) {
        if (token != null) {
            token.setText(StringUtils.stripFront(StringUtils.stripBack(token.getText(), " \n\r\t"), " \n\r\t"));
        }
        super.beginExceptionSpec(token);
        this.currentExceptionSpec = new ExceptionSpec(token);
    }

    public void beginSubRule(Token token, Token token2, boolean bl) {
        super.beginSubRule(token, token2, bl);
        this.blocks.push(new BlockContext());
        this.context().block = new AlternativeBlock(this.grammar, token2, bl);
        this.context().altNum = 0;
        ++this.nested;
        this.context().blockEnd = new BlockEndElement(this.grammar);
        this.context().blockEnd.block = this.context().block;
        this.labelElement(this.context().block, token);
    }

    public void beginTree(Token token) throws SemanticException {
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.tool.error("Trees only allowed in TreeParser", this.grammar.getFilename(), token.getLine(), token.getColumn());
            throw new SemanticException("Trees only allowed in TreeParser");
        }
        super.beginTree(token);
        this.blocks.push(new TreeBlockContext());
        this.context().block = new TreeElement(this.grammar, token);
        this.context().altNum = 0;
    }

    public BlockContext context() {
        if (this.blocks.height() == 0) {
            return null;
        }
        return (BlockContext)this.blocks.top();
    }

    public static RuleBlock createNextTokenRule(Grammar grammar, Vector vector, String string) {
        RuleBlock ruleBlock = new RuleBlock(grammar, string);
        ruleBlock.setDefaultErrorHandler(grammar.getDefaultErrorHandler());
        RuleEndElement ruleEndElement = new RuleEndElement(grammar);
        ruleBlock.setEndElement(ruleEndElement);
        ruleEndElement.block = ruleBlock;
        for (int i = 0; i < vector.size(); ++i) {
            Object object;
            RuleSymbol ruleSymbol = (RuleSymbol)vector.elementAt(i);
            if (!ruleSymbol.isDefined()) {
                grammar.antlrTool.error("Lexer rule " + ruleSymbol.id.substring(1) + " is not defined");
                continue;
            }
            if (!ruleSymbol.access.equals("public")) continue;
            Alternative alternative = new Alternative();
            RuleBlock ruleBlock2 = ruleSymbol.getBlock();
            Vector vector2 = ruleBlock2.getAlternatives();
            if (vector2 != null && vector2.size() == 1) {
                object = (Alternative)vector2.elementAt(0);
                if (((Alternative)object).semPred != null) {
                    alternative.semPred = ((Alternative)object).semPred;
                }
            }
            object = new RuleRefElement(grammar, new CommonToken(41, ruleSymbol.getId()), 1);
            ((RuleRefElement)object).setLabel("theRetToken");
            ((RuleRefElement)object).enclosingRuleName = "nextToken";
            ((RuleRefElement)object).next = ruleEndElement;
            alternative.addElement((AlternativeElement)object);
            alternative.setAutoGen(true);
            ruleBlock.addAlternative(alternative);
            ruleSymbol.addReference((RuleRefElement)object);
        }
        ruleBlock.setAutoGen(true);
        ruleBlock.prepareForAnalysis();
        return ruleBlock;
    }

    private AlternativeBlock createOptionalRuleRef(String string, Token token) {
        AlternativeBlock alternativeBlock = new AlternativeBlock(this.grammar, token, false);
        String string2 = CodeGenerator.encodeLexerRuleName(string);
        if (!this.grammar.isDefined(string2)) {
            this.grammar.define(new RuleSymbol(string2));
        }
        CommonToken commonToken = new CommonToken(24, string);
        ((Token)commonToken).setLine(token.getLine());
        ((Token)commonToken).setLine(token.getColumn());
        RuleRefElement ruleRefElement = new RuleRefElement(this.grammar, commonToken, 1);
        ruleRefElement.enclosingRuleName = this.ruleBlock.ruleName;
        BlockEndElement blockEndElement = new BlockEndElement(this.grammar);
        blockEndElement.block = alternativeBlock;
        Alternative alternative = new Alternative(ruleRefElement);
        alternative.addElement(blockEndElement);
        alternativeBlock.addAlternative(alternative);
        Alternative alternative2 = new Alternative();
        alternative2.addElement(blockEndElement);
        alternativeBlock.addAlternative(alternative2);
        alternativeBlock.prepareForAnalysis();
        return alternativeBlock;
    }

    public void defineRuleName(Token token, String string, boolean bl, String string2) throws SemanticException {
        if (token.type == 24) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.tool.error("Lexical rule " + token.getText() + " defined outside of lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
                token.setText(token.getText().toLowerCase());
            }
        } else if (this.grammar instanceof LexerGrammar) {
            this.tool.error("Lexical rule names must be upper case, '" + token.getText() + "' is not", this.grammar.getFilename(), token.getLine(), token.getColumn());
            token.setText(token.getText().toUpperCase());
        }
        super.defineRuleName(token, string, bl, string2);
        String string3 = token.getText();
        if (token.type == 24) {
            string3 = CodeGenerator.encodeLexerRuleName(string3);
        }
        RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string3);
        RuleBlock ruleBlock = new RuleBlock(this.grammar, token.getText(), token.getLine(), bl);
        ruleBlock.setDefaultErrorHandler(this.grammar.getDefaultErrorHandler());
        this.ruleBlock = ruleBlock;
        this.blocks.push(new BlockContext());
        this.context().block = ruleBlock;
        ruleSymbol.setBlock(ruleBlock);
        this.ruleEnd = new RuleEndElement(this.grammar);
        ruleBlock.setEndElement(this.ruleEnd);
        this.nested = 0;
    }

    public void endAlt() {
        super.endAlt();
        if (this.nested == 0) {
            this.addElementToCurrentAlt(this.ruleEnd);
        } else {
            this.addElementToCurrentAlt(this.context().blockEnd);
        }
        ++this.context().altNum;
    }

    public void endChildList() {
        super.endChildList();
        BlockEndElement blockEndElement = new BlockEndElement(this.grammar);
        blockEndElement.block = this.context().block;
        this.addElementToCurrentAlt(blockEndElement);
    }

    public void endExceptionGroup() {
        super.endExceptionGroup();
    }

    public void endExceptionSpec() {
        super.endExceptionSpec();
        if (this.currentExceptionSpec == null) {
            this.tool.panic("exception processing internal error -- no active exception spec");
        }
        if (this.context().block instanceof RuleBlock) {
            ((RuleBlock)this.context().block).addExceptionSpec(this.currentExceptionSpec);
        } else if (this.context().currentAlt().exceptionSpec != null) {
            this.tool.error("Alternative already has an exception specification", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        } else {
            this.context().currentAlt().exceptionSpec = this.currentExceptionSpec;
        }
        this.currentExceptionSpec = null;
    }

    public void endGrammar() {
        if (this.grammarError) {
            this.abortGrammar();
        } else {
            super.endGrammar();
        }
    }

    public void endRule(String string) {
        super.endRule(string);
        BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.ruleEnd.block = blockContext.block;
        this.ruleEnd.block.prepareForAnalysis();
    }

    public void endSubRule() {
        Object object;
        super.endSubRule();
        --this.nested;
        BlockContext blockContext = (BlockContext)this.blocks.pop();
        AlternativeBlock alternativeBlock = blockContext.block;
        if (!(!alternativeBlock.not || alternativeBlock instanceof SynPredBlock || alternativeBlock instanceof ZeroOrMoreBlock || alternativeBlock instanceof OneOrMoreBlock || this.analyzer.subruleCanBeInverted(alternativeBlock, this.grammar instanceof LexerGrammar))) {
            object = System.getProperty("line.separator");
            this.tool.error("This subrule cannot be inverted.  Only subrules of the form:" + (String)object + "    (T1|T2|T3...) or" + (String)object + "    ('c1'|'c2'|'c3'...)" + (String)object + "may be inverted (ranges are also allowed).", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
        }
        if (alternativeBlock instanceof SynPredBlock) {
            object = (SynPredBlock)alternativeBlock;
            this.context().block.hasASynPred = true;
            this.context().currentAlt().synPred = object;
            this.grammar.hasSyntacticPredicate = true;
            ((AlternativeBlock)object).removeTrackingOfRuleRefs(this.grammar);
        } else {
            this.addElementToCurrentAlt(alternativeBlock);
        }
        blockContext.blockEnd.block.prepareForAnalysis();
    }

    public void endTree() {
        super.endTree();
        BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.addElementToCurrentAlt(blockContext.block);
    }

    public void hasError() {
        this.grammarError = true;
    }

    private void labelElement(AlternativeElement alternativeElement, Token token) {
        if (token != null) {
            for (int i = 0; i < this.ruleBlock.labeledElements.size(); ++i) {
                AlternativeElement alternativeElement2 = (AlternativeElement)this.ruleBlock.labeledElements.elementAt(i);
                String string = alternativeElement2.getLabel();
                if (string == null || !string.equals(token.getText())) continue;
                this.tool.error("Label '" + token.getText() + "' has already been defined", this.grammar.getFilename(), token.getLine(), token.getColumn());
                return;
            }
            alternativeElement.setLabel(token.getText());
            this.ruleBlock.labeledElements.appendElement(alternativeElement);
        }
    }

    public void noAutoGenSubRule() {
        this.context().block.setAutoGen(false);
    }

    public void oneOrMoreSubRule() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to (...)* subrule", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        OneOrMoreBlock oneOrMoreBlock = new OneOrMoreBlock(this.grammar);
        MakeGrammar.setBlock(oneOrMoreBlock, this.context().block);
        BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.blocks.push(new BlockContext());
        this.context().block = oneOrMoreBlock;
        this.context().blockEnd = blockContext.blockEnd;
        this.context().blockEnd.block = oneOrMoreBlock;
    }

    public void optionalSubRule() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to (...)? subrule", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        this.beginAlt(false);
        this.endAlt();
    }

    public void refAction(Token token) {
        super.refAction(token);
        this.context().block.hasAnAction = true;
        this.addElementToCurrentAlt(new ActionElement(this.grammar, token));
    }

    public void setUserExceptions(String string) {
        ((RuleBlock)this.context().block).throwsSpec = string;
    }

    public void refArgAction(Token token) {
        ((RuleBlock)this.context().block).argAction = token.getText();
    }

    public void refCharLiteral(Token token, Token token2, boolean bl, int n, boolean bl2) {
        if (!(this.grammar instanceof LexerGrammar)) {
            this.tool.error("Character literal only valid in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        super.refCharLiteral(token, token2, bl, n, bl2);
        CharLiteralElement charLiteralElement = new CharLiteralElement((LexerGrammar)this.grammar, token, bl, n);
        if (!((LexerGrammar)this.grammar).caseSensitive && charLiteralElement.getType() < 128 && Character.toLowerCase((char)charLiteralElement.getType()) != (char)charLiteralElement.getType()) {
            this.tool.warning("Character literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
        this.addElementToCurrentAlt(charLiteralElement);
        this.labelElement(charLiteralElement, token2);
        String string = this.ruleBlock.getIgnoreRule();
        if (!bl2 && string != null) {
            this.addElementToCurrentAlt(this.createOptionalRuleRef(string, token));
        }
    }

    public void refCharRange(Token token, Token token2, Token token3, int n, boolean bl) {
        if (!(this.grammar instanceof LexerGrammar)) {
            this.tool.error("Character range only valid in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        int n2 = ANTLRLexer.tokenTypeForCharLiteral(token.getText());
        int n3 = ANTLRLexer.tokenTypeForCharLiteral(token2.getText());
        if (n3 < n2) {
            this.tool.error("Malformed range.", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        if (!((LexerGrammar)this.grammar).caseSensitive) {
            if (n2 < 128 && Character.toLowerCase((char)n2) != (char)n2) {
                this.tool.warning("Character literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            if (n3 < 128 && Character.toLowerCase((char)n3) != (char)n3) {
                this.tool.warning("Character literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        }
        super.refCharRange(token, token2, token3, n, bl);
        CharRangeElement charRangeElement = new CharRangeElement((LexerGrammar)this.grammar, token, token2, n);
        this.addElementToCurrentAlt(charRangeElement);
        this.labelElement(charRangeElement, token3);
        String string = this.ruleBlock.getIgnoreRule();
        if (!bl && string != null) {
            this.addElementToCurrentAlt(this.createOptionalRuleRef(string, token));
        }
    }

    public void refTokensSpecElementOption(Token token, Token token2, Token token3) {
        TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(token.getText());
        if (tokenSymbol == null) {
            this.tool.panic("cannot find " + token.getText() + "in tokens {...}");
        }
        if (token2.getText().equals("AST")) {
            tokenSymbol.setASTNodeType(token3.getText());
        } else {
            this.grammar.antlrTool.error("invalid tokens {...} element option:" + token2.getText(), this.grammar.getFilename(), token2.getLine(), token2.getColumn());
        }
    }

    public void refElementOption(Token token, Token token2) {
        AlternativeElement alternativeElement = this.context().currentElement();
        if (alternativeElement instanceof StringLiteralElement || alternativeElement instanceof TokenRefElement || alternativeElement instanceof WildcardElement) {
            ((GrammarAtom)alternativeElement).setOption(token, token2);
        } else {
            this.tool.error("cannot use element option (" + token.getText() + ") for this kind of element", this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }

    public void refExceptionHandler(Token token, Token token2) {
        super.refExceptionHandler(token, token2);
        if (this.currentExceptionSpec == null) {
            this.tool.panic("exception handler processing internal error");
        }
        this.currentExceptionSpec.addHandler(new ExceptionHandler(token, token2));
    }

    public void refInitAction(Token token) {
        super.refAction(token);
        this.context().block.setInitAction(token.getText());
    }

    public void refMemberAction(Token token) {
        this.grammar.classMemberAction = token;
    }

    public void refPreambleAction(Token token) {
        super.refPreambleAction(token);
    }

    public void refReturnAction(Token token) {
        if (this.grammar instanceof LexerGrammar) {
            String string = CodeGenerator.encodeLexerRuleName(((RuleBlock)this.context().block).getRuleName());
            RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string);
            if (ruleSymbol.access.equals("public")) {
                this.tool.warning("public Lexical rules cannot specify return type", this.grammar.getFilename(), token.getLine(), token.getColumn());
                return;
            }
        }
        ((RuleBlock)this.context().block).returnAction = token.getText();
    }

    public void refRule(Token token, Token token2, Token token3, Token token4, int n) {
        if (this.grammar instanceof LexerGrammar) {
            if (token2.type != 24) {
                this.tool.error("Parser rule " + token2.getText() + " referenced in lexer");
                return;
            }
            if (n == 2) {
                this.tool.error("AST specification ^ not allowed in lexer", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        }
        super.refRule(token, token2, token3, token4, n);
        this.lastRuleRef = new RuleRefElement(this.grammar, token2, n);
        if (token4 != null) {
            this.lastRuleRef.setArgs(token4.getText());
        }
        if (token != null) {
            this.lastRuleRef.setIdAssign(token.getText());
        }
        this.addElementToCurrentAlt(this.lastRuleRef);
        String string = token2.getText();
        if (token2.type == 24) {
            string = CodeGenerator.encodeLexerRuleName(string);
        }
        RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string);
        ruleSymbol.addReference(this.lastRuleRef);
        this.labelElement(this.lastRuleRef, token3);
    }

    public void refSemPred(Token token) {
        super.refSemPred(token);
        if (this.context().currentAlt().atStart()) {
            this.context().currentAlt().semPred = token.getText();
        } else {
            ActionElement actionElement = new ActionElement(this.grammar, token);
            actionElement.isSemPred = true;
            this.addElementToCurrentAlt(actionElement);
        }
    }

    public void refStringLiteral(Token token, Token token2, int n, boolean bl) {
        super.refStringLiteral(token, token2, n, bl);
        if (this.grammar instanceof TreeWalkerGrammar && n == 2) {
            this.tool.error("^ not allowed in here for tree-walker", this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
        StringLiteralElement stringLiteralElement = new StringLiteralElement(this.grammar, token, n);
        if (this.grammar instanceof LexerGrammar && !((LexerGrammar)this.grammar).caseSensitive) {
            for (int i = 1; i < token.getText().length() - 1; ++i) {
                char c = token.getText().charAt(i);
                if (c >= '\u0080' || Character.toLowerCase(c) == c) continue;
                this.tool.warning("Characters of string literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token.getLine(), token.getColumn());
                break;
            }
        }
        this.addElementToCurrentAlt(stringLiteralElement);
        this.labelElement(stringLiteralElement, token2);
        String string = this.ruleBlock.getIgnoreRule();
        if (!bl && string != null) {
            this.addElementToCurrentAlt(this.createOptionalRuleRef(string, token));
        }
    }

    public void refToken(Token token, Token token2, Token token3, Token token4, boolean bl, int n, boolean bl2) {
        if (this.grammar instanceof LexerGrammar) {
            if (n == 2) {
                this.tool.error("AST specification ^ not allowed in lexer", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
            if (bl) {
                this.tool.error("~TOKEN is not allowed in lexer", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
            this.refRule(token, token2, token3, token4, n);
            String string = this.ruleBlock.getIgnoreRule();
            if (!bl2 && string != null) {
                this.addElementToCurrentAlt(this.createOptionalRuleRef(string, token2));
            }
        } else {
            if (token != null) {
                this.tool.error("Assignment from token reference only allowed in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            if (token4 != null) {
                this.tool.error("Token reference arguments only allowed in lexer", this.grammar.getFilename(), token4.getLine(), token4.getColumn());
            }
            super.refToken(token, token2, token3, token4, bl, n, bl2);
            TokenRefElement tokenRefElement = new TokenRefElement(this.grammar, token2, bl, n);
            this.addElementToCurrentAlt(tokenRefElement);
            this.labelElement(tokenRefElement, token3);
        }
    }

    public void refTokenRange(Token token, Token token2, Token token3, int n, boolean bl) {
        if (this.grammar instanceof LexerGrammar) {
            this.tool.error("Token range not allowed in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        super.refTokenRange(token, token2, token3, n, bl);
        TokenRangeElement tokenRangeElement = new TokenRangeElement(this.grammar, token, token2, n);
        if (tokenRangeElement.end < tokenRangeElement.begin) {
            this.tool.error("Malformed range.", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        this.addElementToCurrentAlt(tokenRangeElement);
        this.labelElement(tokenRangeElement, token3);
    }

    public void refTreeSpecifier(Token token) {
        this.context().currentAlt().treeSpecifier = token;
    }

    public void refWildcard(Token token, Token token2, int n) {
        super.refWildcard(token, token2, n);
        WildcardElement wildcardElement = new WildcardElement(this.grammar, token, n);
        this.addElementToCurrentAlt(wildcardElement);
        this.labelElement(wildcardElement, token2);
    }

    public void reset() {
        super.reset();
        this.blocks = new LList();
        this.lastRuleRef = null;
        this.ruleEnd = null;
        this.ruleBlock = null;
        this.nested = 0;
        this.currentExceptionSpec = null;
        this.grammarError = false;
    }

    public void setArgOfRuleRef(Token token) {
        super.setArgOfRuleRef(token);
        this.lastRuleRef.setArgs(token.getText());
    }

    public static void setBlock(AlternativeBlock alternativeBlock, AlternativeBlock alternativeBlock2) {
        alternativeBlock.setAlternatives(alternativeBlock2.getAlternatives());
        alternativeBlock.initAction = alternativeBlock2.initAction;
        alternativeBlock.label = alternativeBlock2.label;
        alternativeBlock.hasASynPred = alternativeBlock2.hasASynPred;
        alternativeBlock.hasAnAction = alternativeBlock2.hasAnAction;
        alternativeBlock.warnWhenFollowAmbig = alternativeBlock2.warnWhenFollowAmbig;
        alternativeBlock.generateAmbigWarnings = alternativeBlock2.generateAmbigWarnings;
        alternativeBlock.line = alternativeBlock2.line;
        alternativeBlock.greedy = alternativeBlock2.greedy;
        alternativeBlock.greedySet = alternativeBlock2.greedySet;
    }

    public void setRuleOption(Token token, Token token2) {
        this.ruleBlock.setOption(token, token2);
    }

    public void setSubruleOption(Token token, Token token2) {
        this.context().block.setOption(token, token2);
    }

    public void synPred() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to syntactic predicate", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        SynPredBlock synPredBlock = new SynPredBlock(this.grammar);
        MakeGrammar.setBlock(synPredBlock, this.context().block);
        BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.blocks.push(new BlockContext());
        this.context().block = synPredBlock;
        this.context().blockEnd = blockContext.blockEnd;
        this.context().blockEnd.block = synPredBlock;
    }

    public void zeroOrMoreSubRule() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to (...)+ subrule", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        ZeroOrMoreBlock zeroOrMoreBlock = new ZeroOrMoreBlock(this.grammar);
        MakeGrammar.setBlock(zeroOrMoreBlock, this.context().block);
        BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.blocks.push(new BlockContext());
        this.context().block = zeroOrMoreBlock;
        this.context().blockEnd = blockContext.blockEnd;
        this.context().blockEnd.block = zeroOrMoreBlock;
    }
}

