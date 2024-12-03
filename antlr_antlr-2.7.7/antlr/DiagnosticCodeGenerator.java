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
import antlr.CodeGenerator;
import antlr.ExceptionHandler;
import antlr.ExceptionSpec;
import antlr.Grammar;
import antlr.GrammarAtom;
import antlr.GrammarSymbol;
import antlr.JavaCharFormatter;
import antlr.LexerGrammar;
import antlr.Lookahead;
import antlr.MakeGrammar;
import antlr.OneOrMoreBlock;
import antlr.ParserGrammar;
import antlr.RuleBlock;
import antlr.RuleRefElement;
import antlr.RuleSymbol;
import antlr.StringLiteralElement;
import antlr.StringLiteralSymbol;
import antlr.SynPredBlock;
import antlr.TokenManager;
import antlr.TokenRangeElement;
import antlr.TokenRefElement;
import antlr.Tool;
import antlr.TreeElement;
import antlr.TreeWalkerGrammar;
import antlr.WildcardElement;
import antlr.ZeroOrMoreBlock;
import antlr.collections.impl.Vector;
import java.io.IOException;
import java.util.Enumeration;

public class DiagnosticCodeGenerator
extends CodeGenerator {
    protected int syntacticPredLevel = 0;
    protected boolean doingLexRules = false;

    public DiagnosticCodeGenerator() {
        this.charFormatter = new JavaCharFormatter();
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
                ((Grammar)object).generate();
                if (!this.antlrTool.hasError()) continue;
                this.antlrTool.panic("Exiting due to errors.");
            }
            object = this.behavior.tokenManagers.elements();
            while (object.hasMoreElements()) {
                TokenManager tokenManager = (TokenManager)object.nextElement();
                if (tokenManager.isReadOnly()) continue;
                this.genTokenTypes(tokenManager);
            }
        }
        catch (IOException iOException) {
            this.antlrTool.reportException(iOException, null);
        }
    }

    public void gen(ActionElement actionElement) {
        if (!actionElement.isSemPred) {
            this.print("ACTION: ");
            this._printAction(actionElement.actionText);
        }
    }

    public void gen(AlternativeBlock alternativeBlock) {
        this.println("Start of alternative block.");
        ++this.tabs;
        this.genBlockPreamble(alternativeBlock);
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(alternativeBlock);
        if (!bl) {
            this.println("Warning: This alternative block is non-deterministic");
        }
        this.genCommonBlock(alternativeBlock);
        --this.tabs;
    }

    public void gen(BlockEndElement blockEndElement) {
    }

    public void gen(CharLiteralElement charLiteralElement) {
        this.print("Match character ");
        if (charLiteralElement.not) {
            this._print("NOT ");
        }
        this._print(charLiteralElement.atomText);
        if (charLiteralElement.label != null) {
            this._print(", label=" + charLiteralElement.label);
        }
        this._println("");
    }

    public void gen(CharRangeElement charRangeElement) {
        this.print("Match character range: " + charRangeElement.beginText + ".." + charRangeElement.endText);
        if (charRangeElement.label != null) {
            this._print(", label = " + charRangeElement.label);
        }
        this._println("");
    }

    public void gen(LexerGrammar lexerGrammar) throws IOException {
        GrammarSymbol grammarSymbol;
        this.setGrammar(lexerGrammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + TokenTypesFileExt);
        this.tabs = 0;
        this.doingLexRules = true;
        this.genHeader();
        this.println("");
        this.println("*** Lexer Preamble Action.");
        this.println("This action will appear before the declaration of your lexer class:");
        ++this.tabs;
        this.println(this.grammar.preambleAction.getText());
        --this.tabs;
        this.println("*** End of Lexer Preamble Action");
        this.println("");
        this.println("*** Your lexer class is called '" + this.grammar.getClassName() + "' and is a subclass of '" + this.grammar.getSuperClass() + "'.");
        this.println("");
        this.println("*** User-defined lexer  class members:");
        this.println("These are the member declarations that you defined for your class:");
        ++this.tabs;
        this.printAction(this.grammar.classMemberAction.getText());
        --this.tabs;
        this.println("*** End of user-defined lexer class members");
        this.println("");
        this.println("*** String literals used in the parser");
        this.println("The following string literals were used in the parser.");
        this.println("An actual code generator would arrange to place these literals");
        this.println("into a table in the generated lexer, so that actions in the");
        this.println("generated lexer could match token text against the literals.");
        this.println("String literals used in the lexer are not listed here, as they");
        this.println("are incorporated into the mainstream lexer processing.");
        ++this.tabs;
        Enumeration enumeration = this.grammar.getSymbols();
        while (enumeration.hasMoreElements()) {
            grammarSymbol = (GrammarSymbol)enumeration.nextElement();
            if (!(grammarSymbol instanceof StringLiteralSymbol)) continue;
            StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)grammarSymbol;
            this.println(stringLiteralSymbol.getId() + " = " + stringLiteralSymbol.getTokenType());
        }
        --this.tabs;
        this.println("*** End of string literals used by the parser");
        this.genNextToken();
        this.println("");
        this.println("*** User-defined Lexer rules:");
        ++this.tabs;
        enumeration = this.grammar.rules.elements();
        while (enumeration.hasMoreElements()) {
            grammarSymbol = (RuleSymbol)enumeration.nextElement();
            if (((RuleSymbol)grammarSymbol).id.equals("mnextToken")) continue;
            this.genRule((RuleSymbol)grammarSymbol);
        }
        --this.tabs;
        this.println("");
        this.println("*** End User-defined Lexer rules:");
        this.currentOutput.close();
        this.currentOutput = null;
        this.doingLexRules = false;
    }

    public void gen(OneOrMoreBlock oneOrMoreBlock) {
        this.println("Start ONE-OR-MORE (...)+ block:");
        ++this.tabs;
        this.genBlockPreamble(oneOrMoreBlock);
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(oneOrMoreBlock);
        if (!bl) {
            this.println("Warning: This one-or-more block is non-deterministic");
        }
        this.genCommonBlock(oneOrMoreBlock);
        --this.tabs;
        this.println("End ONE-OR-MORE block.");
    }

    public void gen(ParserGrammar parserGrammar) throws IOException {
        this.setGrammar(parserGrammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + TokenTypesFileExt);
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("*** Parser Preamble Action.");
        this.println("This action will appear before the declaration of your parser class:");
        ++this.tabs;
        this.println(this.grammar.preambleAction.getText());
        --this.tabs;
        this.println("*** End of Parser Preamble Action");
        this.println("");
        this.println("*** Your parser class is called '" + this.grammar.getClassName() + "' and is a subclass of '" + this.grammar.getSuperClass() + "'.");
        this.println("");
        this.println("*** User-defined parser class members:");
        this.println("These are the member declarations that you defined for your class:");
        ++this.tabs;
        this.printAction(this.grammar.classMemberAction.getText());
        --this.tabs;
        this.println("*** End of user-defined parser class members");
        this.println("");
        this.println("*** Parser rules:");
        ++this.tabs;
        Enumeration enumeration = this.grammar.rules.elements();
        while (enumeration.hasMoreElements()) {
            this.println("");
            GrammarSymbol grammarSymbol = (GrammarSymbol)enumeration.nextElement();
            if (!(grammarSymbol instanceof RuleSymbol)) continue;
            this.genRule((RuleSymbol)grammarSymbol);
        }
        --this.tabs;
        this.println("");
        this.println("*** End of parser rules");
        this.println("");
        this.println("*** End of parser");
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void gen(RuleRefElement ruleRefElement) {
        RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule);
        this.print("Rule Reference: " + ruleRefElement.targetRule);
        if (ruleRefElement.idAssign != null) {
            this._print(", assigned to '" + ruleRefElement.idAssign + "'");
        }
        if (ruleRefElement.args != null) {
            this._print(", arguments = " + ruleRefElement.args);
        }
        this._println("");
        if (ruleSymbol == null || !ruleSymbol.isDefined()) {
            this.println("Rule '" + ruleRefElement.targetRule + "' is referenced, but that rule is not defined.");
            this.println("\tPerhaps the rule is misspelled, or you forgot to define it.");
            return;
        }
        if (!(ruleSymbol instanceof RuleSymbol)) {
            this.println("Rule '" + ruleRefElement.targetRule + "' is referenced, but that is not a grammar rule.");
            return;
        }
        if (ruleRefElement.idAssign != null) {
            if (ruleSymbol.block.returnAction == null) {
                this.println("Error: You assigned from Rule '" + ruleRefElement.targetRule + "', but that rule has no return type.");
            }
        } else if (!(this.grammar instanceof LexerGrammar) && this.syntacticPredLevel == 0 && ruleSymbol.block.returnAction != null) {
            this.println("Warning: Rule '" + ruleRefElement.targetRule + "' returns a value");
        }
        if (ruleRefElement.args != null && ruleSymbol.block.argAction == null) {
            this.println("Error: Rule '" + ruleRefElement.targetRule + "' accepts no arguments.");
        }
    }

    public void gen(StringLiteralElement stringLiteralElement) {
        this.print("Match string literal ");
        this._print(stringLiteralElement.atomText);
        if (stringLiteralElement.label != null) {
            this._print(", label=" + stringLiteralElement.label);
        }
        this._println("");
    }

    public void gen(TokenRangeElement tokenRangeElement) {
        this.print("Match token range: " + tokenRangeElement.beginText + ".." + tokenRangeElement.endText);
        if (tokenRangeElement.label != null) {
            this._print(", label = " + tokenRangeElement.label);
        }
        this._println("");
    }

    public void gen(TokenRefElement tokenRefElement) {
        this.print("Match token ");
        if (tokenRefElement.not) {
            this._print("NOT ");
        }
        this._print(tokenRefElement.atomText);
        if (tokenRefElement.label != null) {
            this._print(", label=" + tokenRefElement.label);
        }
        this._println("");
    }

    public void gen(TreeElement treeElement) {
        this.print("Tree reference: " + treeElement);
    }

    public void gen(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.setGrammar(treeWalkerGrammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + TokenTypesFileExt);
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("*** Tree-walker Preamble Action.");
        this.println("This action will appear before the declaration of your tree-walker class:");
        ++this.tabs;
        this.println(this.grammar.preambleAction.getText());
        --this.tabs;
        this.println("*** End of tree-walker Preamble Action");
        this.println("");
        this.println("*** Your tree-walker class is called '" + this.grammar.getClassName() + "' and is a subclass of '" + this.grammar.getSuperClass() + "'.");
        this.println("");
        this.println("*** User-defined tree-walker class members:");
        this.println("These are the member declarations that you defined for your class:");
        ++this.tabs;
        this.printAction(this.grammar.classMemberAction.getText());
        --this.tabs;
        this.println("*** End of user-defined tree-walker class members");
        this.println("");
        this.println("*** tree-walker rules:");
        ++this.tabs;
        Enumeration enumeration = this.grammar.rules.elements();
        while (enumeration.hasMoreElements()) {
            this.println("");
            GrammarSymbol grammarSymbol = (GrammarSymbol)enumeration.nextElement();
            if (!(grammarSymbol instanceof RuleSymbol)) continue;
            this.genRule((RuleSymbol)grammarSymbol);
        }
        --this.tabs;
        this.println("");
        this.println("*** End of tree-walker rules");
        this.println("");
        this.println("*** End of tree-walker");
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void gen(WildcardElement wildcardElement) {
        this.print("Match wildcard");
        if (wildcardElement.getLabel() != null) {
            this._print(", label = " + wildcardElement.getLabel());
        }
        this._println("");
    }

    public void gen(ZeroOrMoreBlock zeroOrMoreBlock) {
        this.println("Start ZERO-OR-MORE (...)+ block:");
        ++this.tabs;
        this.genBlockPreamble(zeroOrMoreBlock);
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(zeroOrMoreBlock);
        if (!bl) {
            this.println("Warning: This zero-or-more block is non-deterministic");
        }
        this.genCommonBlock(zeroOrMoreBlock);
        --this.tabs;
        this.println("End ZERO-OR-MORE block.");
    }

    protected void genAlt(Alternative alternative) {
        AlternativeElement alternativeElement = alternative.head;
        while (!(alternativeElement instanceof BlockEndElement)) {
            alternativeElement.generate();
            alternativeElement = alternativeElement.next;
        }
        if (alternative.getTreeSpecifier() != null) {
            this.println("AST will be built as: " + alternative.getTreeSpecifier().getText());
        }
    }

    protected void genBlockPreamble(AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.printAction("Init action: " + alternativeBlock.initAction);
        }
    }

    public void genCommonBlock(AlternativeBlock alternativeBlock) {
        boolean bl = alternativeBlock.alternatives.size() == 1;
        this.println("Start of an alternative block.");
        ++this.tabs;
        this.println("The lookahead set for this block is:");
        ++this.tabs;
        this.genLookaheadSetForBlock(alternativeBlock);
        --this.tabs;
        if (bl) {
            this.println("This block has a single alternative");
            if (alternativeBlock.getAlternativeAt((int)0).synPred != null) {
                this.println("Warning: you specified a syntactic predicate for this alternative,");
                this.println("and it is the only alternative of a block and will be ignored.");
            }
        } else {
            this.println("This block has multiple alternatives:");
            ++this.tabs;
        }
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            Alternative alternative = alternativeBlock.getAlternativeAt(i);
            AlternativeElement alternativeElement = alternative.head;
            this.println("");
            if (i != 0) {
                this.print("Otherwise, ");
            } else {
                this.print("");
            }
            this._println("Alternate(" + (i + 1) + ") will be taken IF:");
            this.println("The lookahead set: ");
            ++this.tabs;
            this.genLookaheadSetForAlt(alternative);
            --this.tabs;
            if (alternative.semPred != null || alternative.synPred != null) {
                this.print("is matched, AND ");
            } else {
                this.println("is matched.");
            }
            if (alternative.semPred != null) {
                this._println("the semantic predicate:");
                ++this.tabs;
                this.println(alternative.semPred);
                if (alternative.synPred != null) {
                    this.print("is true, AND ");
                } else {
                    this.println("is true.");
                }
            }
            if (alternative.synPred != null) {
                this._println("the syntactic predicate:");
                ++this.tabs;
                this.genSynPred(alternative.synPred);
                --this.tabs;
                this.println("is matched.");
            }
            this.genAlt(alternative);
        }
        this.println("");
        this.println("OTHERWISE, a NoViableAlt exception will be thrown");
        this.println("");
        if (!bl) {
            --this.tabs;
            this.println("End of alternatives");
        }
        --this.tabs;
        this.println("End of alternative block.");
    }

    public void genFollowSetForRuleBlock(RuleBlock ruleBlock) {
        Lookahead lookahead = this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode);
        this.printSet(this.grammar.maxk, 1, lookahead);
    }

    protected void genHeader() {
        this.println("ANTLR-generated file resulting from grammar " + this.antlrTool.grammarFile);
        this.println("Diagnostic output");
        this.println("");
        this.println("Terence Parr, MageLang Institute");
        this.println("with John Lilley, Empathy Software");
        this.println("ANTLR Version " + Tool.version + "; 1989-2005");
        this.println("");
        this.println("*** Header Action.");
        this.println("This action will appear at the top of all generated files.");
        ++this.tabs;
        this.printAction(this.behavior.getHeaderAction(""));
        --this.tabs;
        this.println("*** End of Header Action");
        this.println("");
    }

    protected void genLookaheadSetForAlt(Alternative alternative) {
        if (this.doingLexRules && alternative.cache[1].containsEpsilon()) {
            this.println("MATCHES ALL");
            return;
        }
        int n = alternative.lookaheadDepth;
        if (n == Integer.MAX_VALUE) {
            n = this.grammar.maxk;
        }
        for (int i = 1; i <= n; ++i) {
            Lookahead lookahead = alternative.cache[i];
            this.printSet(n, i, lookahead);
        }
    }

    public void genLookaheadSetForBlock(AlternativeBlock alternativeBlock) {
        Object object;
        int n;
        int n2 = 0;
        for (n = 0; n < alternativeBlock.alternatives.size(); ++n) {
            object = alternativeBlock.getAlternativeAt(n);
            if (((Alternative)object).lookaheadDepth == Integer.MAX_VALUE) {
                n2 = this.grammar.maxk;
                break;
            }
            if (n2 >= ((Alternative)object).lookaheadDepth) continue;
            n2 = ((Alternative)object).lookaheadDepth;
        }
        for (n = 1; n <= n2; ++n) {
            object = this.grammar.theLLkAnalyzer.look(n, alternativeBlock);
            this.printSet(n2, n, (Lookahead)object);
        }
    }

    public void genNextToken() {
        this.println("");
        this.println("*** Lexer nextToken rule:");
        this.println("The lexer nextToken rule is synthesized from all of the user-defined");
        this.println("lexer rules.  It logically consists of one big alternative block with");
        this.println("each user-defined rule being an alternative.");
        this.println("");
        RuleBlock ruleBlock = MakeGrammar.createNextTokenRule(this.grammar, this.grammar.rules, "nextToken");
        RuleSymbol ruleSymbol = new RuleSymbol("mnextToken");
        ruleSymbol.setDefined();
        ruleSymbol.setBlock(ruleBlock);
        ruleSymbol.access = "private";
        this.grammar.define(ruleSymbol);
        if (!this.grammar.theLLkAnalyzer.deterministic(ruleBlock)) {
            this.println("The grammar analyzer has determined that the synthesized");
            this.println("nextToken rule is non-deterministic (i.e., it has ambiguities)");
            this.println("This means that there is some overlap of the character");
            this.println("lookahead for two or more of your lexer rules.");
        }
        this.genCommonBlock(ruleBlock);
        this.println("*** End of nextToken lexer rule.");
    }

    public void genRule(RuleSymbol ruleSymbol) {
        this.println("");
        String string = this.doingLexRules ? "Lexer" : "Parser";
        this.println("*** " + string + " Rule: " + ruleSymbol.getId());
        if (!ruleSymbol.isDefined()) {
            this.println("This rule is undefined.");
            this.println("This means that the rule was referenced somewhere in the grammar,");
            this.println("but a definition for the rule was not encountered.");
            this.println("It is also possible that syntax errors during the parse of");
            this.println("your grammar file prevented correct processing of the rule.");
            this.println("*** End " + string + " Rule: " + ruleSymbol.getId());
            return;
        }
        ++this.tabs;
        if (ruleSymbol.access.length() != 0) {
            this.println("Access: " + ruleSymbol.access);
        }
        RuleBlock ruleBlock = ruleSymbol.getBlock();
        if (ruleBlock.returnAction != null) {
            this.println("Return value(s): " + ruleBlock.returnAction);
            if (this.doingLexRules) {
                this.println("Error: you specified return value(s) for a lexical rule.");
                this.println("\tLexical rules have an implicit return type of 'int'.");
            }
        } else if (this.doingLexRules) {
            this.println("Return value: lexical rule returns an implicit token type");
        } else {
            this.println("Return value: none");
        }
        if (ruleBlock.argAction != null) {
            this.println("Arguments: " + ruleBlock.argAction);
        }
        this.genBlockPreamble(ruleBlock);
        boolean bl = this.grammar.theLLkAnalyzer.deterministic(ruleBlock);
        if (!bl) {
            this.println("Error: This rule is non-deterministic");
        }
        this.genCommonBlock(ruleBlock);
        ExceptionSpec exceptionSpec = ruleBlock.findExceptionSpec("");
        if (exceptionSpec != null) {
            this.println("You specified error-handler(s) for this rule:");
            ++this.tabs;
            for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
                if (i != 0) {
                    this.println("");
                }
                ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
                this.println("Error-handler(" + (i + 1) + ") catches [" + exceptionHandler.exceptionTypeAndName.getText() + "] and executes:");
                this.printAction(exceptionHandler.action.getText());
            }
            --this.tabs;
            this.println("End error-handlers.");
        } else if (!this.doingLexRules) {
            this.println("Default error-handling will be generated, which catches all");
            this.println("parser exceptions and consumes tokens until the follow-set is seen.");
        }
        if (!this.doingLexRules) {
            this.println("The follow set for this rule is:");
            ++this.tabs;
            this.genFollowSetForRuleBlock(ruleBlock);
            --this.tabs;
        }
        --this.tabs;
        this.println("*** End " + string + " Rule: " + ruleSymbol.getId());
    }

    protected void genSynPred(SynPredBlock synPredBlock) {
        ++this.syntacticPredLevel;
        this.gen(synPredBlock);
        --this.syntacticPredLevel;
    }

    protected void genTokenTypes(TokenManager tokenManager) throws IOException {
        this.antlrTool.reportProgress("Generating " + tokenManager.getName() + TokenTypesFileSuffix + TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(tokenManager.getName() + TokenTypesFileSuffix + TokenTypesFileExt);
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("*** Tokens used by the parser");
        this.println("This is a list of the token numeric values and the corresponding");
        this.println("token identifiers.  Some tokens are literals, and because of that");
        this.println("they have no identifiers.  Literals are double-quoted.");
        ++this.tabs;
        Vector vector = tokenManager.getVocabulary();
        for (int i = 4; i < vector.size(); ++i) {
            String string = (String)vector.elementAt(i);
            if (string == null) continue;
            this.println(string + " = " + i);
        }
        --this.tabs;
        this.println("*** End of tokens used by the parser");
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public String getASTCreateString(Vector vector) {
        return "***Create an AST from a vector here***" + System.getProperty("line.separator");
    }

    public String getASTCreateString(GrammarAtom grammarAtom, String string) {
        return "[" + string + "]";
    }

    protected String processActionForSpecialSymbols(String string, int n, RuleBlock ruleBlock, ActionTransInfo actionTransInfo) {
        return string;
    }

    public String mapTreeId(String string, ActionTransInfo actionTransInfo) {
        return string;
    }

    public void printSet(int n, int n2, Lookahead lookahead) {
        int n3 = 5;
        int[] nArray = lookahead.fset.toArray();
        if (n != 1) {
            this.print("k==" + n2 + ": {");
        } else {
            this.print("{ ");
        }
        if (nArray.length > n3) {
            this._println("");
            ++this.tabs;
            this.print("");
        }
        int n4 = 0;
        for (int i = 0; i < nArray.length; ++i) {
            if (++n4 > n3) {
                this._println("");
                this.print("");
                n4 = 0;
            }
            if (this.doingLexRules) {
                this._print(this.charFormatter.literalChar(nArray[i]));
            } else {
                this._print((String)this.grammar.tokenManager.getVocabulary().elementAt(nArray[i]));
            }
            if (i == nArray.length - 1) continue;
            this._print(", ");
        }
        if (nArray.length > n3) {
            this._println("");
            --this.tabs;
            this.print("");
        }
        this._println(" }");
    }
}

