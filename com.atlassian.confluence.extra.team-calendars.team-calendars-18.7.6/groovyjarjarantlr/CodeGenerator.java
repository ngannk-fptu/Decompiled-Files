/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ActionElement;
import groovyjarjarantlr.ActionTransInfo;
import groovyjarjarantlr.AlternativeBlock;
import groovyjarjarantlr.BlockEndElement;
import groovyjarjarantlr.CharFormatter;
import groovyjarjarantlr.CharLiteralElement;
import groovyjarjarantlr.CharRangeElement;
import groovyjarjarantlr.DefineGrammarSymbols;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.GrammarAtom;
import groovyjarjarantlr.GrammarSymbol;
import groovyjarjarantlr.LLkGrammarAnalyzer;
import groovyjarjarantlr.LexerGrammar;
import groovyjarjarantlr.Lookahead;
import groovyjarjarantlr.OneOrMoreBlock;
import groovyjarjarantlr.ParserGrammar;
import groovyjarjarantlr.RuleBlock;
import groovyjarjarantlr.RuleRefElement;
import groovyjarjarantlr.RuleSymbol;
import groovyjarjarantlr.StringLiteralElement;
import groovyjarjarantlr.StringLiteralSymbol;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenManager;
import groovyjarjarantlr.TokenRangeElement;
import groovyjarjarantlr.TokenRefElement;
import groovyjarjarantlr.TokenSymbol;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.TreeElement;
import groovyjarjarantlr.TreeWalkerGrammar;
import groovyjarjarantlr.WildcardElement;
import groovyjarjarantlr.ZeroOrMoreBlock;
import groovyjarjarantlr.collections.impl.BitSet;
import groovyjarjarantlr.collections.impl.Vector;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class CodeGenerator {
    protected Tool antlrTool;
    protected int tabs = 0;
    protected transient PrintWriter currentOutput;
    protected Grammar grammar = null;
    protected Vector bitsetsUsed;
    protected DefineGrammarSymbols behavior;
    protected LLkGrammarAnalyzer analyzer;
    protected CharFormatter charFormatter;
    protected boolean DEBUG_CODE_GENERATOR = false;
    protected static final int DEFAULT_MAKE_SWITCH_THRESHOLD = 2;
    protected static final int DEFAULT_BITSET_TEST_THRESHOLD = 4;
    protected static final int BITSET_OPTIMIZE_INIT_THRESHOLD = 8;
    protected int makeSwitchThreshold = 2;
    protected int bitsetTestThreshold = 4;
    private static boolean OLD_ACTION_TRANSLATOR = true;
    public static String TokenTypesFileSuffix = "TokenTypes";
    public static String TokenTypesFileExt = ".txt";

    protected void _print(String string) {
        if (string != null) {
            this.currentOutput.print(string);
        }
    }

    protected void _printAction(String string) {
        int n;
        int n2;
        if (string == null) {
            return;
        }
        for (n2 = 0; n2 < string.length() && Character.isSpaceChar(string.charAt(n2)); ++n2) {
        }
        for (n = string.length() - 1; n > n2 && Character.isSpaceChar(string.charAt(n)); --n) {
        }
        char c = '\u0000';
        int n3 = n2;
        while (n3 <= n) {
            c = string.charAt(n3);
            ++n3;
            boolean bl = false;
            switch (c) {
                case '\n': {
                    bl = true;
                    break;
                }
                case '\r': {
                    if (n3 <= n && string.charAt(n3) == '\n') {
                        ++n3;
                    }
                    bl = true;
                    break;
                }
                default: {
                    this.currentOutput.print(c);
                }
            }
            if (!bl) continue;
            this.currentOutput.println();
            this.printTabs();
            while (n3 <= n && Character.isSpaceChar(string.charAt(n3))) {
                ++n3;
            }
            bl = false;
        }
        this.currentOutput.println();
    }

    protected void _println(String string) {
        if (string != null) {
            this.currentOutput.println(string);
        }
    }

    public static boolean elementsAreRange(int[] nArray) {
        if (nArray.length == 0) {
            return false;
        }
        int n = nArray[0];
        int n2 = nArray[nArray.length - 1];
        if (nArray.length <= 2) {
            return false;
        }
        if (n2 - n + 1 > nArray.length) {
            return false;
        }
        int n3 = n + 1;
        for (int i = 1; i < nArray.length - 1; ++i) {
            if (n3 != nArray[i]) {
                return false;
            }
            ++n3;
        }
        return true;
    }

    protected String extractIdOfAction(Token token) {
        return this.extractIdOfAction(token.getText(), token.getLine(), token.getColumn());
    }

    protected String extractIdOfAction(String string, int n, int n2) {
        string = this.removeAssignmentFromDeclaration(string);
        for (int i = string.length() - 2; i >= 0; --i) {
            if (Character.isLetterOrDigit(string.charAt(i)) || string.charAt(i) == '_') continue;
            return string.substring(i + 1);
        }
        this.antlrTool.warning("Ill-formed action", this.grammar.getFilename(), n, n2);
        return "";
    }

    protected String extractTypeOfAction(Token token) {
        return this.extractTypeOfAction(token.getText(), token.getLine(), token.getColumn());
    }

    protected String extractTypeOfAction(String string, int n, int n2) {
        string = this.removeAssignmentFromDeclaration(string);
        for (int i = string.length() - 2; i >= 0; --i) {
            if (Character.isLetterOrDigit(string.charAt(i)) || string.charAt(i) == '_') continue;
            return string.substring(0, i + 1);
        }
        this.antlrTool.warning("Ill-formed action", this.grammar.getFilename(), n, n2);
        return "";
    }

    public abstract void gen();

    public abstract void gen(ActionElement var1);

    public abstract void gen(AlternativeBlock var1);

    public abstract void gen(BlockEndElement var1);

    public abstract void gen(CharLiteralElement var1);

    public abstract void gen(CharRangeElement var1);

    public abstract void gen(LexerGrammar var1) throws IOException;

    public abstract void gen(OneOrMoreBlock var1);

    public abstract void gen(ParserGrammar var1) throws IOException;

    public abstract void gen(RuleRefElement var1);

    public abstract void gen(StringLiteralElement var1);

    public abstract void gen(TokenRangeElement var1);

    public abstract void gen(TokenRefElement var1);

    public abstract void gen(TreeElement var1);

    public abstract void gen(TreeWalkerGrammar var1) throws IOException;

    public abstract void gen(WildcardElement var1);

    public abstract void gen(ZeroOrMoreBlock var1);

    protected void genTokenInterchange(TokenManager tokenManager) throws IOException {
        String string = tokenManager.getName() + TokenTypesFileSuffix + TokenTypesFileExt;
        this.currentOutput = this.antlrTool.openOutputFile(string);
        this.println("// $ANTLR " + Tool.version + ": " + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + " -> " + string + "$");
        this.tabs = 0;
        this.println(tokenManager.getName() + "    // output token vocab name");
        Vector vector = tokenManager.getVocabulary();
        for (int i = 4; i < vector.size(); ++i) {
            TokenSymbol tokenSymbol;
            String string2 = (String)vector.elementAt(i);
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen persistence file entry for: " + string2);
            }
            if (string2 == null || string2.startsWith("<")) continue;
            if (string2.startsWith("\"")) {
                tokenSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(string2);
                if (tokenSymbol != null && ((StringLiteralSymbol)tokenSymbol).label != null) {
                    this.print(((StringLiteralSymbol)tokenSymbol).label + "=");
                }
                this.println(string2 + "=" + i);
                continue;
            }
            this.print(string2);
            tokenSymbol = tokenManager.getTokenSymbol(string2);
            if (tokenSymbol == null) {
                this.antlrTool.warning("undefined token symbol: " + string2);
            } else if (tokenSymbol.getParaphrase() != null) {
                this.print("(" + tokenSymbol.getParaphrase() + ")");
            }
            this.println("=" + i);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }

    public String processStringForASTConstructor(String string) {
        return string;
    }

    public abstract String getASTCreateString(Vector var1);

    public abstract String getASTCreateString(GrammarAtom var1, String var2);

    protected String getBitsetName(int n) {
        return "_tokenSet_" + n;
    }

    public static String encodeLexerRuleName(String string) {
        return "m" + string;
    }

    public static String decodeLexerRuleName(String string) {
        if (string == null) {
            return null;
        }
        return string.substring(1, string.length());
    }

    public abstract String mapTreeId(String var1, ActionTransInfo var2);

    protected int markBitsetForGen(BitSet bitSet) {
        for (int i = 0; i < this.bitsetsUsed.size(); ++i) {
            BitSet bitSet2 = (BitSet)this.bitsetsUsed.elementAt(i);
            if (!bitSet.equals(bitSet2)) continue;
            return i;
        }
        this.bitsetsUsed.appendElement(bitSet.clone());
        return this.bitsetsUsed.size() - 1;
    }

    protected void print(String string) {
        if (string != null) {
            this.printTabs();
            this.currentOutput.print(string);
        }
    }

    protected void printAction(String string) {
        if (string != null) {
            this.printTabs();
            this._printAction(string);
        }
    }

    protected void println(String string) {
        if (string != null) {
            this.printTabs();
            this.currentOutput.println(string);
        }
    }

    protected void printTabs() {
        for (int i = 1; i <= this.tabs; ++i) {
            this.currentOutput.print("\t");
        }
    }

    protected abstract String processActionForSpecialSymbols(String var1, int var2, RuleBlock var3, ActionTransInfo var4);

    public String getFOLLOWBitSet(String string, int n) {
        GrammarSymbol grammarSymbol = this.grammar.getSymbol(string);
        if (!(grammarSymbol instanceof RuleSymbol)) {
            return null;
        }
        RuleBlock ruleBlock = ((RuleSymbol)grammarSymbol).getBlock();
        Lookahead lookahead = this.grammar.theLLkAnalyzer.FOLLOW(n, ruleBlock.endNode);
        String string2 = this.getBitsetName(this.markBitsetForGen(lookahead.fset));
        return string2;
    }

    public String getFIRSTBitSet(String string, int n) {
        GrammarSymbol grammarSymbol = this.grammar.getSymbol(string);
        if (!(grammarSymbol instanceof RuleSymbol)) {
            return null;
        }
        RuleBlock ruleBlock = ((RuleSymbol)grammarSymbol).getBlock();
        Lookahead lookahead = this.grammar.theLLkAnalyzer.look(n, ruleBlock);
        String string2 = this.getBitsetName(this.markBitsetForGen(lookahead.fset));
        return string2;
    }

    protected String removeAssignmentFromDeclaration(String string) {
        if (string.indexOf(61) >= 0) {
            string = string.substring(0, string.indexOf(61)).trim();
        }
        return string;
    }

    private void reset() {
        this.tabs = 0;
        this.bitsetsUsed = new Vector();
        this.currentOutput = null;
        this.grammar = null;
        this.DEBUG_CODE_GENERATOR = false;
        this.makeSwitchThreshold = 2;
        this.bitsetTestThreshold = 4;
    }

    public static String reverseLexerRuleName(String string) {
        return string.substring(1, string.length());
    }

    public void setAnalyzer(LLkGrammarAnalyzer lLkGrammarAnalyzer) {
        this.analyzer = lLkGrammarAnalyzer;
    }

    public void setBehavior(DefineGrammarSymbols defineGrammarSymbols) {
        this.behavior = defineGrammarSymbols;
    }

    protected void setGrammar(Grammar grammar) {
        Token token;
        this.reset();
        this.grammar = grammar;
        if (this.grammar.hasOption("codeGenMakeSwitchThreshold")) {
            try {
                this.makeSwitchThreshold = this.grammar.getIntegerOption("codeGenMakeSwitchThreshold");
            }
            catch (NumberFormatException numberFormatException) {
                token = this.grammar.getOption("codeGenMakeSwitchThreshold");
                this.antlrTool.error("option 'codeGenMakeSwitchThreshold' must be an integer", this.grammar.getClassName(), token.getLine(), token.getColumn());
            }
        }
        if (this.grammar.hasOption("codeGenBitsetTestThreshold")) {
            try {
                this.bitsetTestThreshold = this.grammar.getIntegerOption("codeGenBitsetTestThreshold");
            }
            catch (NumberFormatException numberFormatException) {
                token = this.grammar.getOption("codeGenBitsetTestThreshold");
                this.antlrTool.error("option 'codeGenBitsetTestThreshold' must be an integer", this.grammar.getClassName(), token.getLine(), token.getColumn());
            }
        }
        if (this.grammar.hasOption("codeGenDebug")) {
            Token token2 = this.grammar.getOption("codeGenDebug");
            if (token2.getText().equals("true")) {
                this.DEBUG_CODE_GENERATOR = true;
            } else if (token2.getText().equals("false")) {
                this.DEBUG_CODE_GENERATOR = false;
            } else {
                this.antlrTool.error("option 'codeGenDebug' must be true or false", this.grammar.getClassName(), token2.getLine(), token2.getColumn());
            }
        }
    }

    public void setTool(Tool tool) {
        this.antlrTool = tool;
    }
}

