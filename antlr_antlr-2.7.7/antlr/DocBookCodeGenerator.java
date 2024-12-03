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

public class DocBookCodeGenerator
extends CodeGenerator {
    protected int syntacticPredLevel = 0;
    protected boolean doingLexRules = false;
    protected boolean firstElementInAlt;
    protected AlternativeElement prevAltElem = null;

    public DocBookCodeGenerator() {
        this.charFormatter = new JavaCharFormatter();
    }

    static String HTMLEncode(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c == '&') {
                stringBuffer.append("&amp;");
                continue;
            }
            if (c == '\"') {
                stringBuffer.append("&quot;");
                continue;
            }
            if (c == '\'') {
                stringBuffer.append("&#039;");
                continue;
            }
            if (c == '<') {
                stringBuffer.append("&lt;");
                continue;
            }
            if (c == '>') {
                stringBuffer.append("&gt;");
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    static String QuoteForId(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c == '_') {
                stringBuffer.append(".");
                continue;
            }
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }

    public void gen() {
        try {
            Enumeration enumeration = this.behavior.grammars.elements();
            while (enumeration.hasMoreElements()) {
                Grammar grammar = (Grammar)enumeration.nextElement();
                grammar.setCodeGenerator(this);
                grammar.generate();
                if (!this.antlrTool.hasError()) continue;
                this.antlrTool.fatalError("Exiting due to errors.");
            }
        }
        catch (IOException iOException) {
            this.antlrTool.reportException(iOException, null);
        }
    }

    public void gen(ActionElement actionElement) {
    }

    public void gen(AlternativeBlock alternativeBlock) {
        this.genGenericBlock(alternativeBlock, "");
    }

    public void gen(BlockEndElement blockEndElement) {
    }

    public void gen(CharLiteralElement charLiteralElement) {
        if (charLiteralElement.not) {
            this._print("~");
        }
        this._print(DocBookCodeGenerator.HTMLEncode(charLiteralElement.atomText) + " ");
    }

    public void gen(CharRangeElement charRangeElement) {
        this.print(charRangeElement.beginText + ".." + charRangeElement.endText + " ");
    }

    public void gen(LexerGrammar lexerGrammar) throws IOException {
        this.setGrammar(lexerGrammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".sgml");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".sgml");
        this.tabs = 0;
        this.doingLexRules = true;
        this.genHeader();
        this.println("");
        if (this.grammar.comment != null) {
            this._println(DocBookCodeGenerator.HTMLEncode(this.grammar.comment));
        }
        this.println("<para>Definition of lexer " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".</para>");
        this.genNextToken();
        Enumeration enumeration = this.grammar.rules.elements();
        while (enumeration.hasMoreElements()) {
            RuleSymbol ruleSymbol = (RuleSymbol)enumeration.nextElement();
            if (ruleSymbol.id.equals("mnextToken")) continue;
            this.genRule(ruleSymbol);
        }
        this.currentOutput.close();
        this.currentOutput = null;
        this.doingLexRules = false;
    }

    public void gen(OneOrMoreBlock oneOrMoreBlock) {
        this.genGenericBlock(oneOrMoreBlock, "+");
    }

    public void gen(ParserGrammar parserGrammar) throws IOException {
        this.setGrammar(parserGrammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".sgml");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".sgml");
        this.tabs = 0;
        this.genHeader();
        this.println("");
        if (this.grammar.comment != null) {
            this._println(DocBookCodeGenerator.HTMLEncode(this.grammar.comment));
        }
        this.println("<para>Definition of parser " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".</para>");
        Enumeration enumeration = this.grammar.rules.elements();
        while (enumeration.hasMoreElements()) {
            this.println("");
            GrammarSymbol grammarSymbol = (GrammarSymbol)enumeration.nextElement();
            if (!(grammarSymbol instanceof RuleSymbol)) continue;
            this.genRule((RuleSymbol)grammarSymbol);
        }
        --this.tabs;
        this.println("");
        this.genTail();
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void gen(RuleRefElement ruleRefElement) {
        RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule);
        this._print("<link linkend=\"" + DocBookCodeGenerator.QuoteForId(ruleRefElement.targetRule) + "\">");
        this._print(ruleRefElement.targetRule);
        this._print("</link>");
        this._print(" ");
    }

    public void gen(StringLiteralElement stringLiteralElement) {
        if (stringLiteralElement.not) {
            this._print("~");
        }
        this._print(DocBookCodeGenerator.HTMLEncode(stringLiteralElement.atomText));
        this._print(" ");
    }

    public void gen(TokenRangeElement tokenRangeElement) {
        this.print(tokenRangeElement.beginText + ".." + tokenRangeElement.endText + " ");
    }

    public void gen(TokenRefElement tokenRefElement) {
        if (tokenRefElement.not) {
            this._print("~");
        }
        this._print(tokenRefElement.atomText);
        this._print(" ");
    }

    public void gen(TreeElement treeElement) {
        this.print(treeElement + " ");
    }

    public void gen(TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.setGrammar(treeWalkerGrammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".sgml");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".sgml");
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("");
        if (this.grammar.comment != null) {
            this._println(DocBookCodeGenerator.HTMLEncode(this.grammar.comment));
        }
        this.println("<para>Definition of tree parser " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".</para>");
        this.println("");
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
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public void gen(WildcardElement wildcardElement) {
        this._print(". ");
    }

    public void gen(ZeroOrMoreBlock zeroOrMoreBlock) {
        this.genGenericBlock(zeroOrMoreBlock, "*");
    }

    protected void genAlt(Alternative alternative) {
        if (alternative.getTreeSpecifier() != null) {
            this._print(alternative.getTreeSpecifier().getText());
        }
        this.prevAltElem = null;
        AlternativeElement alternativeElement = alternative.head;
        while (!(alternativeElement instanceof BlockEndElement)) {
            alternativeElement.generate();
            this.firstElementInAlt = false;
            this.prevAltElem = alternativeElement;
            alternativeElement = alternativeElement.next;
        }
    }

    public void genCommonBlock(AlternativeBlock alternativeBlock) {
        if (alternativeBlock.alternatives.size() > 1) {
            this.println("<itemizedlist mark=\"none\">");
        }
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            Alternative alternative = alternativeBlock.getAlternativeAt(i);
            AlternativeElement alternativeElement = alternative.head;
            if (alternativeBlock.alternatives.size() > 1) {
                this.print("<listitem><para>");
            }
            if (i > 0 && alternativeBlock.alternatives.size() > 1) {
                this._print("| ");
            }
            boolean bl = this.firstElementInAlt;
            this.firstElementInAlt = true;
            ++this.tabs;
            this.genAlt(alternative);
            --this.tabs;
            this.firstElementInAlt = bl;
            if (alternativeBlock.alternatives.size() <= 1) continue;
            this._println("</para></listitem>");
        }
        if (alternativeBlock.alternatives.size() > 1) {
            this.println("</itemizedlist>");
        }
    }

    public void genFollowSetForRuleBlock(RuleBlock ruleBlock) {
        Lookahead lookahead = this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode);
        this.printSet(this.grammar.maxk, 1, lookahead);
    }

    protected void genGenericBlock(AlternativeBlock alternativeBlock, String string) {
        if (alternativeBlock.alternatives.size() > 1) {
            this._println("");
            if (!this.firstElementInAlt) {
                this._println("(");
            } else {
                this._print("(");
            }
        } else {
            this._print("( ");
        }
        this.genCommonBlock(alternativeBlock);
        if (alternativeBlock.alternatives.size() > 1) {
            this._println("");
            this.print(")" + string + " ");
            if (!(alternativeBlock.next instanceof BlockEndElement)) {
                this._println("");
                this.print("");
            }
        } else {
            this._print(")" + string + " ");
        }
    }

    protected void genHeader() {
        this.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        this.println("<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook V3.1//EN\">");
        this.println("<book lang=\"en\">");
        this.println("<bookinfo>");
        this.println("<title>Grammar " + this.grammar.getClassName() + "</title>");
        this.println("  <author>");
        this.println("    <firstname></firstname>");
        this.println("    <othername></othername>");
        this.println("    <surname></surname>");
        this.println("    <affiliation>");
        this.println("     <address>");
        this.println("     <email></email>");
        this.println("     </address>");
        this.println("    </affiliation>");
        this.println("  </author>");
        this.println("  <othercredit>");
        this.println("    <contrib>");
        this.println("    Generated by <ulink url=\"http://www.ANTLR.org/\">ANTLR</ulink>" + Tool.version);
        this.println("    from " + this.antlrTool.grammarFile);
        this.println("    </contrib>");
        this.println("  </othercredit>");
        this.println("  <pubdate></pubdate>");
        this.println("  <abstract>");
        this.println("  <para>");
        this.println("  </para>");
        this.println("  </abstract>");
        this.println("</bookinfo>");
        this.println("<chapter>");
        this.println("<title></title>");
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
        this.println("/** Lexer nextToken rule:");
        this.println(" *  The lexer nextToken rule is synthesized from all of the user-defined");
        this.println(" *  lexer rules.  It logically consists of one big alternative block with");
        this.println(" *  each user-defined rule being an alternative.");
        this.println(" */");
        RuleBlock ruleBlock = MakeGrammar.createNextTokenRule(this.grammar, this.grammar.rules, "nextToken");
        RuleSymbol ruleSymbol = new RuleSymbol("mnextToken");
        ruleSymbol.setDefined();
        ruleSymbol.setBlock(ruleBlock);
        ruleSymbol.access = "private";
        this.grammar.define(ruleSymbol);
        this.genCommonBlock(ruleBlock);
    }

    public void genRule(RuleSymbol ruleSymbol) {
        if (ruleSymbol == null || !ruleSymbol.isDefined()) {
            return;
        }
        this.println("");
        if (ruleSymbol.access.length() != 0 && !ruleSymbol.access.equals("public")) {
            this._print("<para>" + ruleSymbol.access + " </para>");
        }
        this.println("<section id=\"" + DocBookCodeGenerator.QuoteForId(ruleSymbol.getId()) + "\">");
        this.println("<title>" + ruleSymbol.getId() + "</title>");
        if (ruleSymbol.comment != null) {
            this._println("<para>" + DocBookCodeGenerator.HTMLEncode(ruleSymbol.comment) + "</para>");
        }
        this.println("<para>");
        RuleBlock ruleBlock = ruleSymbol.getBlock();
        this._println("");
        this.print(ruleSymbol.getId() + ":\t");
        ++this.tabs;
        this.genCommonBlock(ruleBlock);
        this._println("");
        --this.tabs;
        this._println("</para>");
        this._println("</section><!-- section \"" + ruleSymbol.getId() + "\" -->");
    }

    protected void genSynPred(SynPredBlock synPredBlock) {
    }

    public void genTail() {
        this.println("</chapter>");
        this.println("</book>");
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

    protected String processActionForSpecialSymbols(String string, int n, RuleBlock ruleBlock, ActionTransInfo actionTransInfo) {
        return string;
    }

    public String getASTCreateString(Vector vector) {
        return null;
    }

    public String getASTCreateString(GrammarAtom grammarAtom, String string) {
        return null;
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

