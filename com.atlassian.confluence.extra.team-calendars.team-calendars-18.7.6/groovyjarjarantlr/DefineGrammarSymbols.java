/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ANTLRGrammarParseBehavior;
import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.CommonToken;
import groovyjarjarantlr.Grammar;
import groovyjarjarantlr.ImportVocabTokenManager;
import groovyjarjarantlr.LLkAnalyzer;
import groovyjarjarantlr.LexerGrammar;
import groovyjarjarantlr.ParserGrammar;
import groovyjarjarantlr.RuleSymbol;
import groovyjarjarantlr.SemanticException;
import groovyjarjarantlr.SimpleTokenManager;
import groovyjarjarantlr.StringLiteralSymbol;
import groovyjarjarantlr.StringUtils;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenManager;
import groovyjarjarantlr.TokenSymbol;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.TreeWalkerGrammar;
import groovyjarjarantlr.collections.impl.BitSet;
import java.util.Hashtable;

public class DefineGrammarSymbols
implements ANTLRGrammarParseBehavior {
    protected Hashtable grammars = new Hashtable();
    protected Hashtable tokenManagers = new Hashtable();
    protected Grammar grammar;
    protected Tool tool;
    LLkAnalyzer analyzer;
    String[] args;
    static final String DEFAULT_TOKENMANAGER_NAME = "*default";
    protected Hashtable headerActions = new Hashtable();
    Token thePreambleAction = new CommonToken(0, "");
    String language = "Java";
    protected int numLexers = 0;
    protected int numParsers = 0;
    protected int numTreeParsers = 0;

    public DefineGrammarSymbols(Tool tool, String[] stringArray, LLkAnalyzer lLkAnalyzer) {
        this.tool = tool;
        this.args = stringArray;
        this.analyzer = lLkAnalyzer;
    }

    public void _refStringLiteral(Token token, Token token2, int n, boolean bl) {
        if (!(this.grammar instanceof LexerGrammar)) {
            String string = token.getText();
            if (this.grammar.tokenManager.getTokenSymbol(string) != null) {
                return;
            }
            StringLiteralSymbol stringLiteralSymbol = new StringLiteralSymbol(string);
            int n2 = this.grammar.tokenManager.nextTokenType();
            stringLiteralSymbol.setTokenType(n2);
            this.grammar.tokenManager.define(stringLiteralSymbol);
        }
    }

    public void _refToken(Token token, Token token2, Token token3, Token token4, boolean bl, int n, boolean bl2) {
        String string = token2.getText();
        if (!this.grammar.tokenManager.tokenDefined(string)) {
            int n2 = this.grammar.tokenManager.nextTokenType();
            TokenSymbol tokenSymbol = new TokenSymbol(string);
            tokenSymbol.setTokenType(n2);
            this.grammar.tokenManager.define(tokenSymbol);
        }
    }

    public void abortGrammar() {
        if (this.grammar != null && this.grammar.getClassName() != null) {
            this.grammars.remove(this.grammar.getClassName());
        }
        this.grammar = null;
    }

    public void beginAlt(boolean bl) {
    }

    public void beginChildList() {
    }

    public void beginExceptionGroup() {
    }

    public void beginExceptionSpec(Token token) {
    }

    public void beginSubRule(Token token, Token token2, boolean bl) {
    }

    public void beginTree(Token token) throws SemanticException {
    }

    public void defineRuleName(Token token, String string, boolean bl, String string2) throws SemanticException {
        RuleSymbol ruleSymbol;
        String string3 = token.getText();
        if (token.type == 24) {
            string3 = CodeGenerator.encodeLexerRuleName(string3);
            if (!this.grammar.tokenManager.tokenDefined(token.getText())) {
                int n = this.grammar.tokenManager.nextTokenType();
                TokenSymbol tokenSymbol = new TokenSymbol(token.getText());
                tokenSymbol.setTokenType(n);
                this.grammar.tokenManager.define(tokenSymbol);
            }
        }
        if (this.grammar.isDefined(string3)) {
            ruleSymbol = (RuleSymbol)this.grammar.getSymbol(string3);
            if (ruleSymbol.isDefined()) {
                this.tool.error("redefinition of rule " + string3, this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        } else {
            ruleSymbol = new RuleSymbol(string3);
            this.grammar.define(ruleSymbol);
        }
        ruleSymbol.setDefined();
        ruleSymbol.access = string;
        ruleSymbol.comment = string2;
    }

    public void defineToken(Token token, Token token2) {
        String string = null;
        String string2 = null;
        if (token != null) {
            string = token.getText();
        }
        if (token2 != null) {
            string2 = token2.getText();
        }
        if (string2 != null) {
            TokenSymbol tokenSymbol;
            StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)this.grammar.tokenManager.getTokenSymbol(string2);
            if (stringLiteralSymbol != null) {
                if (string == null || stringLiteralSymbol.getLabel() != null) {
                    this.tool.warning("Redefinition of literal in tokens {...}: " + string2, this.grammar.getFilename(), token2.getLine(), token2.getColumn());
                    return;
                }
                if (string != null) {
                    stringLiteralSymbol.setLabel(string);
                    this.grammar.tokenManager.mapToTokenSymbol(string, stringLiteralSymbol);
                }
            }
            if (string != null && (tokenSymbol = this.grammar.tokenManager.getTokenSymbol(string)) != null) {
                if (tokenSymbol instanceof StringLiteralSymbol) {
                    this.tool.warning("Redefinition of token in tokens {...}: " + string, this.grammar.getFilename(), token2.getLine(), token2.getColumn());
                    return;
                }
                int n = tokenSymbol.getTokenType();
                stringLiteralSymbol = new StringLiteralSymbol(string2);
                stringLiteralSymbol.setTokenType(n);
                stringLiteralSymbol.setLabel(string);
                this.grammar.tokenManager.define(stringLiteralSymbol);
                this.grammar.tokenManager.mapToTokenSymbol(string, stringLiteralSymbol);
                return;
            }
            stringLiteralSymbol = new StringLiteralSymbol(string2);
            int n = this.grammar.tokenManager.nextTokenType();
            stringLiteralSymbol.setTokenType(n);
            stringLiteralSymbol.setLabel(string);
            this.grammar.tokenManager.define(stringLiteralSymbol);
            if (string != null) {
                this.grammar.tokenManager.mapToTokenSymbol(string, stringLiteralSymbol);
            }
        } else {
            if (this.grammar.tokenManager.tokenDefined(string)) {
                this.tool.warning("Redefinition of token in tokens {...}: " + string, this.grammar.getFilename(), token.getLine(), token.getColumn());
                return;
            }
            int n = this.grammar.tokenManager.nextTokenType();
            TokenSymbol tokenSymbol = new TokenSymbol(string);
            tokenSymbol.setTokenType(n);
            this.grammar.tokenManager.define(tokenSymbol);
        }
    }

    public void endAlt() {
    }

    public void endChildList() {
    }

    public void endExceptionGroup() {
    }

    public void endExceptionSpec() {
    }

    public void endGrammar() {
    }

    public void endOptions() {
        if (this.grammar.exportVocab == null && this.grammar.importVocab == null) {
            this.grammar.exportVocab = this.grammar.getClassName();
            if (this.tokenManagers.containsKey(DEFAULT_TOKENMANAGER_NAME)) {
                this.grammar.exportVocab = DEFAULT_TOKENMANAGER_NAME;
                TokenManager tokenManager = (TokenManager)this.tokenManagers.get(DEFAULT_TOKENMANAGER_NAME);
                this.grammar.setTokenManager(tokenManager);
                return;
            }
            SimpleTokenManager simpleTokenManager = new SimpleTokenManager(this.grammar.exportVocab, this.tool);
            this.grammar.setTokenManager(simpleTokenManager);
            this.tokenManagers.put(this.grammar.exportVocab, simpleTokenManager);
            this.tokenManagers.put(DEFAULT_TOKENMANAGER_NAME, simpleTokenManager);
            return;
        }
        if (this.grammar.exportVocab == null && this.grammar.importVocab != null) {
            this.grammar.exportVocab = this.grammar.getClassName();
            if (this.grammar.importVocab.equals(this.grammar.exportVocab)) {
                this.tool.warning("Grammar " + this.grammar.getClassName() + " cannot have importVocab same as default output vocab (grammar name); ignored.");
                this.grammar.importVocab = null;
                this.endOptions();
                return;
            }
            if (this.tokenManagers.containsKey(this.grammar.importVocab)) {
                TokenManager tokenManager = (TokenManager)this.tokenManagers.get(this.grammar.importVocab);
                TokenManager tokenManager2 = (TokenManager)tokenManager.clone();
                tokenManager2.setName(this.grammar.exportVocab);
                tokenManager2.setReadOnly(false);
                this.grammar.setTokenManager(tokenManager2);
                this.tokenManagers.put(this.grammar.exportVocab, tokenManager2);
                return;
            }
            ImportVocabTokenManager importVocabTokenManager = new ImportVocabTokenManager(this.grammar, this.grammar.importVocab + CodeGenerator.TokenTypesFileSuffix + CodeGenerator.TokenTypesFileExt, this.grammar.exportVocab, this.tool);
            importVocabTokenManager.setReadOnly(false);
            this.tokenManagers.put(this.grammar.exportVocab, importVocabTokenManager);
            this.grammar.setTokenManager(importVocabTokenManager);
            if (!this.tokenManagers.containsKey(DEFAULT_TOKENMANAGER_NAME)) {
                this.tokenManagers.put(DEFAULT_TOKENMANAGER_NAME, importVocabTokenManager);
            }
            return;
        }
        if (this.grammar.exportVocab != null && this.grammar.importVocab == null) {
            if (this.tokenManagers.containsKey(this.grammar.exportVocab)) {
                TokenManager tokenManager = (TokenManager)this.tokenManagers.get(this.grammar.exportVocab);
                this.grammar.setTokenManager(tokenManager);
                return;
            }
            SimpleTokenManager simpleTokenManager = new SimpleTokenManager(this.grammar.exportVocab, this.tool);
            this.grammar.setTokenManager(simpleTokenManager);
            this.tokenManagers.put(this.grammar.exportVocab, simpleTokenManager);
            if (!this.tokenManagers.containsKey(DEFAULT_TOKENMANAGER_NAME)) {
                this.tokenManagers.put(DEFAULT_TOKENMANAGER_NAME, simpleTokenManager);
            }
            return;
        }
        if (this.grammar.exportVocab != null && this.grammar.importVocab != null) {
            if (this.grammar.importVocab.equals(this.grammar.exportVocab)) {
                this.tool.error("exportVocab of " + this.grammar.exportVocab + " same as importVocab; probably not what you want");
            }
            if (this.tokenManagers.containsKey(this.grammar.importVocab)) {
                TokenManager tokenManager = (TokenManager)this.tokenManagers.get(this.grammar.importVocab);
                TokenManager tokenManager3 = (TokenManager)tokenManager.clone();
                tokenManager3.setName(this.grammar.exportVocab);
                tokenManager3.setReadOnly(false);
                this.grammar.setTokenManager(tokenManager3);
                this.tokenManagers.put(this.grammar.exportVocab, tokenManager3);
                return;
            }
            ImportVocabTokenManager importVocabTokenManager = new ImportVocabTokenManager(this.grammar, this.grammar.importVocab + CodeGenerator.TokenTypesFileSuffix + CodeGenerator.TokenTypesFileExt, this.grammar.exportVocab, this.tool);
            importVocabTokenManager.setReadOnly(false);
            this.tokenManagers.put(this.grammar.exportVocab, importVocabTokenManager);
            this.grammar.setTokenManager(importVocabTokenManager);
            if (!this.tokenManagers.containsKey(DEFAULT_TOKENMANAGER_NAME)) {
                this.tokenManagers.put(DEFAULT_TOKENMANAGER_NAME, importVocabTokenManager);
            }
            return;
        }
    }

    public void endRule(String string) {
    }

    public void endSubRule() {
    }

    public void endTree() {
    }

    public void hasError() {
    }

    public void noASTSubRule() {
    }

    public void oneOrMoreSubRule() {
    }

    public void optionalSubRule() {
    }

    public void setUserExceptions(String string) {
    }

    public void refAction(Token token) {
    }

    public void refArgAction(Token token) {
    }

    public void refCharLiteral(Token token, Token token2, boolean bl, int n, boolean bl2) {
    }

    public void refCharRange(Token token, Token token2, Token token3, int n, boolean bl) {
    }

    public void refElementOption(Token token, Token token2) {
    }

    public void refTokensSpecElementOption(Token token, Token token2, Token token3) {
    }

    public void refExceptionHandler(Token token, Token token2) {
    }

    public void refHeaderAction(Token token, Token token2) {
        String string = token == null ? "" : StringUtils.stripFrontBack(token.getText(), "\"", "\"");
        if (this.headerActions.containsKey(string)) {
            if (string.equals("")) {
                this.tool.error(token2.getLine() + ": header action already defined");
            } else {
                this.tool.error(token2.getLine() + ": header action '" + string + "' already defined");
            }
        }
        this.headerActions.put(string, token2);
    }

    public String getHeaderAction(String string) {
        Token token = (Token)this.headerActions.get(string);
        if (token == null) {
            return "";
        }
        return token.getText();
    }

    public int getHeaderActionLine(String string) {
        Token token = (Token)this.headerActions.get(string);
        if (token == null) {
            return 0;
        }
        return token.getLine();
    }

    public void refInitAction(Token token) {
    }

    public void refMemberAction(Token token) {
    }

    public void refPreambleAction(Token token) {
        this.thePreambleAction = token;
    }

    public void refReturnAction(Token token) {
    }

    public void refRule(Token token, Token token2, Token token3, Token token4, int n) {
        String string = token2.getText();
        if (token2.type == 24) {
            string = CodeGenerator.encodeLexerRuleName(string);
        }
        if (!this.grammar.isDefined(string)) {
            this.grammar.define(new RuleSymbol(string));
        }
    }

    public void refSemPred(Token token) {
    }

    public void refStringLiteral(Token token, Token token2, int n, boolean bl) {
        this._refStringLiteral(token, token2, n, bl);
    }

    public void refToken(Token token, Token token2, Token token3, Token token4, boolean bl, int n, boolean bl2) {
        this._refToken(token, token2, token3, token4, bl, n, bl2);
    }

    public void refTokenRange(Token token, Token token2, Token token3, int n, boolean bl) {
        if (token.getText().charAt(0) == '\"') {
            this.refStringLiteral(token, null, 1, bl);
        } else {
            this._refToken(null, token, null, null, false, 1, bl);
        }
        if (token2.getText().charAt(0) == '\"') {
            this._refStringLiteral(token2, null, 1, bl);
        } else {
            this._refToken(null, token2, null, null, false, 1, bl);
        }
    }

    public void refTreeSpecifier(Token token) {
    }

    public void refWildcard(Token token, Token token2, int n) {
    }

    public void reset() {
        this.grammar = null;
    }

    public void setArgOfRuleRef(Token token) {
    }

    public void setCharVocabulary(BitSet bitSet) {
        ((LexerGrammar)this.grammar).setCharVocabulary(bitSet);
    }

    public void setFileOption(Token token, Token token2, String string) {
        if (token.getText().equals("language")) {
            if (token2.getType() == 6) {
                this.language = StringUtils.stripBack(StringUtils.stripFront(token2.getText(), '\"'), '\"');
            } else if (token2.getType() == 24 || token2.getType() == 41) {
                this.language = token2.getText();
            } else {
                this.tool.error("language option must be string or identifier", string, token2.getLine(), token2.getColumn());
            }
        } else if (token.getText().equals("mangleLiteralPrefix")) {
            if (token2.getType() == 6) {
                this.tool.literalsPrefix = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
            } else {
                this.tool.error("mangleLiteralPrefix option must be string", string, token2.getLine(), token2.getColumn());
            }
        } else if (token.getText().equals("upperCaseMangledLiterals")) {
            if (token2.getText().equals("true")) {
                this.tool.upperCaseMangledLiterals = true;
            } else if (token2.getText().equals("false")) {
                this.tool.upperCaseMangledLiterals = false;
            } else {
                this.grammar.antlrTool.error("Value for upperCaseMangledLiterals must be true or false", string, token.getLine(), token.getColumn());
            }
        } else if (token.getText().equals("namespaceStd") || token.getText().equals("namespaceAntlr") || token.getText().equals("genHashLines")) {
            if (!this.language.equals("Cpp")) {
                this.tool.error(token.getText() + " option only valid for C++", string, token.getLine(), token.getColumn());
            } else if (token.getText().equals("noConstructors")) {
                if (!token2.getText().equals("true") && !token2.getText().equals("false")) {
                    this.tool.error("noConstructors option must be true or false", string, token2.getLine(), token2.getColumn());
                }
                this.tool.noConstructors = token2.getText().equals("true");
            } else if (token.getText().equals("genHashLines")) {
                if (!token2.getText().equals("true") && !token2.getText().equals("false")) {
                    this.tool.error("genHashLines option must be true or false", string, token2.getLine(), token2.getColumn());
                }
                this.tool.genHashLines = token2.getText().equals("true");
            } else if (token2.getType() != 6) {
                this.tool.error(token.getText() + " option must be a string", string, token2.getLine(), token2.getColumn());
            } else if (token.getText().equals("namespaceStd")) {
                this.tool.namespaceStd = token2.getText();
            } else if (token.getText().equals("namespaceAntlr")) {
                this.tool.namespaceAntlr = token2.getText();
            }
        } else if (token.getText().equals("namespace")) {
            if (!this.language.equals("Cpp") && !this.language.equals("CSharp")) {
                this.tool.error(token.getText() + " option only valid for C++ and C# (a.k.a CSharp)", string, token.getLine(), token.getColumn());
            } else if (token2.getType() != 6) {
                this.tool.error(token.getText() + " option must be a string", string, token2.getLine(), token2.getColumn());
            } else if (token.getText().equals("namespace")) {
                this.tool.setNameSpace(token2.getText());
            }
        } else {
            this.tool.error("Invalid file-level option: " + token.getText(), string, token.getLine(), token2.getColumn());
        }
    }

    public void setGrammarOption(Token token, Token token2) {
        if (token.getText().equals("tokdef") || token.getText().equals("tokenVocabulary")) {
            this.tool.error("tokdef/tokenVocabulary options are invalid >= ANTLR 2.6.0.\n  Use importVocab/exportVocab instead.  Please see the documentation.\n  The previous options were so heinous that Terence changed the whole\n  vocabulary mechanism; it was better to change the names rather than\n  subtly change the functionality of the known options.  Sorry!", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
        } else if (token.getText().equals("literal") && this.grammar instanceof LexerGrammar) {
            this.tool.error("the literal option is invalid >= ANTLR 2.6.0.\n  Use the \"tokens {...}\" mechanism instead.", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
        } else if (token.getText().equals("exportVocab")) {
            if (token2.getType() == 41 || token2.getType() == 24) {
                this.grammar.exportVocab = token2.getText();
            } else {
                this.tool.error("exportVocab must be an identifier", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        } else if (token.getText().equals("importVocab")) {
            if (token2.getType() == 41 || token2.getType() == 24) {
                this.grammar.importVocab = token2.getText();
            } else {
                this.tool.error("importVocab must be an identifier", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        } else if (token.getText().equals("k")) {
            if (this.grammar instanceof TreeWalkerGrammar && !token2.getText().equals("1")) {
                this.tool.error("Treewalkers only support k=1", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            } else {
                this.grammar.setOption(token.getText(), token2);
            }
        } else {
            this.grammar.setOption(token.getText(), token2);
        }
    }

    public void setRuleOption(Token token, Token token2) {
    }

    public void setSubruleOption(Token token, Token token2) {
    }

    public void startLexer(String string, Token token, String string2, String string3) {
        if (this.numLexers > 0) {
            this.tool.panic("You may only have one lexer per grammar file: class " + token.getText());
        }
        ++this.numLexers;
        this.reset();
        Grammar grammar = (Grammar)this.grammars.get(token);
        if (grammar != null) {
            if (!(grammar instanceof LexerGrammar)) {
                this.tool.panic("'" + token.getText() + "' is already defined as a non-lexer");
            } else {
                this.tool.panic("Lexer '" + token.getText() + "' is already defined");
            }
        } else {
            LexerGrammar lexerGrammar = new LexerGrammar(token.getText(), this.tool, string2);
            lexerGrammar.comment = string3;
            lexerGrammar.processArguments(this.args);
            lexerGrammar.setFilename(string);
            this.grammars.put(lexerGrammar.getClassName(), lexerGrammar);
            lexerGrammar.preambleAction = this.thePreambleAction;
            this.thePreambleAction = new CommonToken(0, "");
            this.grammar = lexerGrammar;
        }
    }

    public void startParser(String string, Token token, String string2, String string3) {
        if (this.numParsers > 0) {
            this.tool.panic("You may only have one parser per grammar file: class " + token.getText());
        }
        ++this.numParsers;
        this.reset();
        Grammar grammar = (Grammar)this.grammars.get(token);
        if (grammar != null) {
            if (!(grammar instanceof ParserGrammar)) {
                this.tool.panic("'" + token.getText() + "' is already defined as a non-parser");
            } else {
                this.tool.panic("Parser '" + token.getText() + "' is already defined");
            }
        } else {
            this.grammar = new ParserGrammar(token.getText(), this.tool, string2);
            this.grammar.comment = string3;
            this.grammar.processArguments(this.args);
            this.grammar.setFilename(string);
            this.grammars.put(this.grammar.getClassName(), this.grammar);
            this.grammar.preambleAction = this.thePreambleAction;
            this.thePreambleAction = new CommonToken(0, "");
        }
    }

    public void startTreeWalker(String string, Token token, String string2, String string3) {
        if (this.numTreeParsers > 0) {
            this.tool.panic("You may only have one tree parser per grammar file: class " + token.getText());
        }
        ++this.numTreeParsers;
        this.reset();
        Grammar grammar = (Grammar)this.grammars.get(token);
        if (grammar != null) {
            if (!(grammar instanceof TreeWalkerGrammar)) {
                this.tool.panic("'" + token.getText() + "' is already defined as a non-tree-walker");
            } else {
                this.tool.panic("Tree-walker '" + token.getText() + "' is already defined");
            }
        } else {
            this.grammar = new TreeWalkerGrammar(token.getText(), this.tool, string2);
            this.grammar.comment = string3;
            this.grammar.processArguments(this.args);
            this.grammar.setFilename(string);
            this.grammars.put(this.grammar.getClassName(), this.grammar);
            this.grammar.preambleAction = this.thePreambleAction;
            this.thePreambleAction = new CommonToken(0, "");
        }
    }

    public void synPred() {
    }

    public void zeroOrMoreSubRule() {
    }
}

