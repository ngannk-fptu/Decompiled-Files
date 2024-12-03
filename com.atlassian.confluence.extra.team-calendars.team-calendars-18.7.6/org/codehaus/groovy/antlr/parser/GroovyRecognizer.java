/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.parser;

import groovyjarjarantlr.ASTFactory;
import groovyjarjarantlr.ASTPair;
import groovyjarjarantlr.CommonToken;
import groovyjarjarantlr.InputBuffer;
import groovyjarjarantlr.LLkParser;
import groovyjarjarantlr.LexerSharedInputState;
import groovyjarjarantlr.NoViableAltException;
import groovyjarjarantlr.ParserSharedInputState;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.SemanticException;
import groovyjarjarantlr.Token;
import groovyjarjarantlr.TokenBuffer;
import groovyjarjarantlr.TokenStream;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.TokenStreamRecognitionException;
import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.collections.impl.ASTArray;
import groovyjarjarantlr.collections.impl.BitSet;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.SourceInfo;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

public class GroovyRecognizer
extends LLkParser
implements GroovyTokenTypes {
    private static GroovySourceAST dummyVariableToforceClassLoaderToFindASTClass = new GroovySourceAST();
    List warningList;
    GroovyLexer lexer;
    private SourceBuffer sourceBuffer;
    public static boolean tracing = false;
    private AST currentClass = null;
    private int sepToken = 1;
    private boolean argListHasLabels = false;
    private AST lastPathExpression = null;
    private final int LC_STMT = 1;
    private final int LC_INIT = 2;
    private int ltCounter = 0;
    private static final boolean ANTLR_LOOP_EXIT = false;
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "BLOCK", "MODIFIERS", "OBJBLOCK", "SLIST", "METHOD_DEF", "VARIABLE_DEF", "INSTANCE_INIT", "STATIC_INIT", "TYPE", "CLASS_DEF", "INTERFACE_DEF", "TRAIT_DEF", "PACKAGE_DEF", "ARRAY_DECLARATOR", "EXTENDS_CLAUSE", "IMPLEMENTS_CLAUSE", "PARAMETERS", "PARAMETER_DEF", "LABELED_STAT", "TYPECAST", "INDEX_OP", "POST_INC", "POST_DEC", "METHOD_CALL", "EXPR", "IMPORT", "UNARY_MINUS", "UNARY_PLUS", "CASE_GROUP", "ELIST", "FOR_INIT", "FOR_CONDITION", "FOR_ITERATOR", "EMPTY_STAT", "\"final\"", "\"abstract\"", "\"goto\"", "\"const\"", "\"do\"", "\"strictfp\"", "SUPER_CTOR_CALL", "CTOR_CALL", "CTOR_IDENT", "VARIABLE_PARAMETER_DEF", "STRING_CONSTRUCTOR", "STRING_CTOR_MIDDLE", "CLOSABLE_BLOCK", "IMPLICIT_PARAMETERS", "SELECT_SLOT", "DYNAMIC_MEMBER", "LABELED_ARG", "SPREAD_ARG", "SPREAD_MAP_ARG", "LIST_CONSTRUCTOR", "MAP_CONSTRUCTOR", "FOR_IN_ITERABLE", "STATIC_IMPORT", "ENUM_DEF", "ENUM_CONSTANT_DEF", "FOR_EACH_CLAUSE", "ANNOTATION_DEF", "ANNOTATIONS", "ANNOTATION", "ANNOTATION_MEMBER_VALUE_PAIR", "ANNOTATION_FIELD_DEF", "ANNOTATION_ARRAY_INIT", "TYPE_ARGUMENTS", "TYPE_ARGUMENT", "TYPE_PARAMETERS", "TYPE_PARAMETER", "WILDCARD_TYPE", "TYPE_UPPER_BOUNDS", "TYPE_LOWER_BOUNDS", "CLOSURE_LIST", "MULTICATCH", "MULTICATCH_TYPES", "a script header", "\"package\"", "\"import\"", "\"static\"", "\"def\"", "'['", "']'", "an identifier", "a string literal", "'<'", "'.'", "'('", "\"class\"", "\"interface\"", "\"enum\"", "\"trait\"", "'@'", "'?'", "\"extends\"", "\"super\"", "'>'", "','", "'>>'", "'>>>'", "\"void\"", "\"boolean\"", "\"byte\"", "\"char\"", "\"short\"", "\"int\"", "\"float\"", "\"long\"", "\"double\"", "'*'", "\"as\"", "\"private\"", "\"public\"", "\"protected\"", "\"transient\"", "\"native\"", "\"threadsafe\"", "\"synchronized\"", "\"volatile\"", "')'", "'='", "'&'", "'{'", "'}'", "';'", "\"default\"", "\"throws\"", "\"implements\"", "\"this\"", "'...'", "'|'", "'->'", "':'", "\"if\"", "\"else\"", "\"while\"", "\"switch\"", "\"for\"", "\"in\"", "\"return\"", "\"break\"", "\"continue\"", "\"throw\"", "\"assert\"", "'+'", "'-'", "\"case\"", "\"try\"", "\"finally\"", "\"catch\"", "'*.'", "'?.'", "'.&'", "\"false\"", "\"instanceof\"", "\"new\"", "\"null\"", "\"true\"", "'+='", "'-='", "'*='", "'/='", "'%='", "'>>='", "'>>>='", "'<<='", "'&='", "'^='", "'|='", "'**='", "'?:'", "'||'", "'&&'", "'^'", "'=~'", "'==~'", "'!='", "'=='", "'==='", "'!=='", "'<=>'", "'<='", "'>='", "'<<'", "'..'", "'..<'", "'++'", "'/'", "'%'", "'--'", "'**'", "'~'", "'!'", "STRING_CTOR_START", "a string literal end", "a numeric literal", "NUM_FLOAT", "NUM_LONG", "NUM_DOUBLE", "NUM_BIG_INT", "NUM_BIG_DECIMAL", "some newlines, whitespace or comments", "'$'", "whitespace", "a newline", "a single line comment", "a multi-line comment", "a string character", "a multiline regular expression literal", "a multiline dollar escaping regular expression literal", "a multiline regular expression literal end", "a multiline dollar escaping regular expression literal end", "ESCAPED_SLASH", "ESCAPED_DOLLAR", "a multiline regular expression character", "a multiline dollar escaping regular expression character", "an escape sequence", "a newline inside a string", "a hexadecimal digit", "a character", "a letter", "a digit", "a sequence of digits and underscores, bordered by digits", "a sequence of digits and underscores with maybe underscore starting", "an exponent", "a float or double suffix", "a big decimal suffix"};
    public static final BitSet _tokenSet_0 = new BitSet(GroovyRecognizer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(GroovyRecognizer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(GroovyRecognizer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(GroovyRecognizer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(GroovyRecognizer.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(GroovyRecognizer.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(GroovyRecognizer.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(GroovyRecognizer.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(GroovyRecognizer.mk_tokenSet_8());
    public static final BitSet _tokenSet_9 = new BitSet(GroovyRecognizer.mk_tokenSet_9());
    public static final BitSet _tokenSet_10 = new BitSet(GroovyRecognizer.mk_tokenSet_10());
    public static final BitSet _tokenSet_11 = new BitSet(GroovyRecognizer.mk_tokenSet_11());
    public static final BitSet _tokenSet_12 = new BitSet(GroovyRecognizer.mk_tokenSet_12());
    public static final BitSet _tokenSet_13 = new BitSet(GroovyRecognizer.mk_tokenSet_13());
    public static final BitSet _tokenSet_14 = new BitSet(GroovyRecognizer.mk_tokenSet_14());
    public static final BitSet _tokenSet_15 = new BitSet(GroovyRecognizer.mk_tokenSet_15());
    public static final BitSet _tokenSet_16 = new BitSet(GroovyRecognizer.mk_tokenSet_16());
    public static final BitSet _tokenSet_17 = new BitSet(GroovyRecognizer.mk_tokenSet_17());
    public static final BitSet _tokenSet_18 = new BitSet(GroovyRecognizer.mk_tokenSet_18());
    public static final BitSet _tokenSet_19 = new BitSet(GroovyRecognizer.mk_tokenSet_19());
    public static final BitSet _tokenSet_20 = new BitSet(GroovyRecognizer.mk_tokenSet_20());
    public static final BitSet _tokenSet_21 = new BitSet(GroovyRecognizer.mk_tokenSet_21());
    public static final BitSet _tokenSet_22 = new BitSet(GroovyRecognizer.mk_tokenSet_22());
    public static final BitSet _tokenSet_23 = new BitSet(GroovyRecognizer.mk_tokenSet_23());
    public static final BitSet _tokenSet_24 = new BitSet(GroovyRecognizer.mk_tokenSet_24());
    public static final BitSet _tokenSet_25 = new BitSet(GroovyRecognizer.mk_tokenSet_25());
    public static final BitSet _tokenSet_26 = new BitSet(GroovyRecognizer.mk_tokenSet_26());
    public static final BitSet _tokenSet_27 = new BitSet(GroovyRecognizer.mk_tokenSet_27());
    public static final BitSet _tokenSet_28 = new BitSet(GroovyRecognizer.mk_tokenSet_28());
    public static final BitSet _tokenSet_29 = new BitSet(GroovyRecognizer.mk_tokenSet_29());
    public static final BitSet _tokenSet_30 = new BitSet(GroovyRecognizer.mk_tokenSet_30());
    public static final BitSet _tokenSet_31 = new BitSet(GroovyRecognizer.mk_tokenSet_31());
    public static final BitSet _tokenSet_32 = new BitSet(GroovyRecognizer.mk_tokenSet_32());
    public static final BitSet _tokenSet_33 = new BitSet(GroovyRecognizer.mk_tokenSet_33());
    public static final BitSet _tokenSet_34 = new BitSet(GroovyRecognizer.mk_tokenSet_34());
    public static final BitSet _tokenSet_35 = new BitSet(GroovyRecognizer.mk_tokenSet_35());
    public static final BitSet _tokenSet_36 = new BitSet(GroovyRecognizer.mk_tokenSet_36());
    public static final BitSet _tokenSet_37 = new BitSet(GroovyRecognizer.mk_tokenSet_37());
    public static final BitSet _tokenSet_38 = new BitSet(GroovyRecognizer.mk_tokenSet_38());
    public static final BitSet _tokenSet_39 = new BitSet(GroovyRecognizer.mk_tokenSet_39());
    public static final BitSet _tokenSet_40 = new BitSet(GroovyRecognizer.mk_tokenSet_40());
    public static final BitSet _tokenSet_41 = new BitSet(GroovyRecognizer.mk_tokenSet_41());
    public static final BitSet _tokenSet_42 = new BitSet(GroovyRecognizer.mk_tokenSet_42());
    public static final BitSet _tokenSet_43 = new BitSet(GroovyRecognizer.mk_tokenSet_43());
    public static final BitSet _tokenSet_44 = new BitSet(GroovyRecognizer.mk_tokenSet_44());
    public static final BitSet _tokenSet_45 = new BitSet(GroovyRecognizer.mk_tokenSet_45());
    public static final BitSet _tokenSet_46 = new BitSet(GroovyRecognizer.mk_tokenSet_46());
    public static final BitSet _tokenSet_47 = new BitSet(GroovyRecognizer.mk_tokenSet_47());
    public static final BitSet _tokenSet_48 = new BitSet(GroovyRecognizer.mk_tokenSet_48());
    public static final BitSet _tokenSet_49 = new BitSet(GroovyRecognizer.mk_tokenSet_49());
    public static final BitSet _tokenSet_50 = new BitSet(GroovyRecognizer.mk_tokenSet_50());
    public static final BitSet _tokenSet_51 = new BitSet(GroovyRecognizer.mk_tokenSet_51());
    public static final BitSet _tokenSet_52 = new BitSet(GroovyRecognizer.mk_tokenSet_52());
    public static final BitSet _tokenSet_53 = new BitSet(GroovyRecognizer.mk_tokenSet_53());
    public static final BitSet _tokenSet_54 = new BitSet(GroovyRecognizer.mk_tokenSet_54());
    public static final BitSet _tokenSet_55 = new BitSet(GroovyRecognizer.mk_tokenSet_55());
    public static final BitSet _tokenSet_56 = new BitSet(GroovyRecognizer.mk_tokenSet_56());
    public static final BitSet _tokenSet_57 = new BitSet(GroovyRecognizer.mk_tokenSet_57());
    public static final BitSet _tokenSet_58 = new BitSet(GroovyRecognizer.mk_tokenSet_58());
    public static final BitSet _tokenSet_59 = new BitSet(GroovyRecognizer.mk_tokenSet_59());
    public static final BitSet _tokenSet_60 = new BitSet(GroovyRecognizer.mk_tokenSet_60());
    public static final BitSet _tokenSet_61 = new BitSet(GroovyRecognizer.mk_tokenSet_61());
    public static final BitSet _tokenSet_62 = new BitSet(GroovyRecognizer.mk_tokenSet_62());
    public static final BitSet _tokenSet_63 = new BitSet(GroovyRecognizer.mk_tokenSet_63());
    public static final BitSet _tokenSet_64 = new BitSet(GroovyRecognizer.mk_tokenSet_64());
    public static final BitSet _tokenSet_65 = new BitSet(GroovyRecognizer.mk_tokenSet_65());
    public static final BitSet _tokenSet_66 = new BitSet(GroovyRecognizer.mk_tokenSet_66());
    public static final BitSet _tokenSet_67 = new BitSet(GroovyRecognizer.mk_tokenSet_67());
    public static final BitSet _tokenSet_68 = new BitSet(GroovyRecognizer.mk_tokenSet_68());
    public static final BitSet _tokenSet_69 = new BitSet(GroovyRecognizer.mk_tokenSet_69());
    public static final BitSet _tokenSet_70 = new BitSet(GroovyRecognizer.mk_tokenSet_70());
    public static final BitSet _tokenSet_71 = new BitSet(GroovyRecognizer.mk_tokenSet_71());
    public static final BitSet _tokenSet_72 = new BitSet(GroovyRecognizer.mk_tokenSet_72());
    public static final BitSet _tokenSet_73 = new BitSet(GroovyRecognizer.mk_tokenSet_73());
    public static final BitSet _tokenSet_74 = new BitSet(GroovyRecognizer.mk_tokenSet_74());
    public static final BitSet _tokenSet_75 = new BitSet(GroovyRecognizer.mk_tokenSet_75());
    public static final BitSet _tokenSet_76 = new BitSet(GroovyRecognizer.mk_tokenSet_76());
    public static final BitSet _tokenSet_77 = new BitSet(GroovyRecognizer.mk_tokenSet_77());
    public static final BitSet _tokenSet_78 = new BitSet(GroovyRecognizer.mk_tokenSet_78());
    public static final BitSet _tokenSet_79 = new BitSet(GroovyRecognizer.mk_tokenSet_79());
    public static final BitSet _tokenSet_80 = new BitSet(GroovyRecognizer.mk_tokenSet_80());
    public static final BitSet _tokenSet_81 = new BitSet(GroovyRecognizer.mk_tokenSet_81());
    public static final BitSet _tokenSet_82 = new BitSet(GroovyRecognizer.mk_tokenSet_82());
    public static final BitSet _tokenSet_83 = new BitSet(GroovyRecognizer.mk_tokenSet_83());
    public static final BitSet _tokenSet_84 = new BitSet(GroovyRecognizer.mk_tokenSet_84());
    public static final BitSet _tokenSet_85 = new BitSet(GroovyRecognizer.mk_tokenSet_85());
    public static final BitSet _tokenSet_86 = new BitSet(GroovyRecognizer.mk_tokenSet_86());
    public static final BitSet _tokenSet_87 = new BitSet(GroovyRecognizer.mk_tokenSet_87());
    public static final BitSet _tokenSet_88 = new BitSet(GroovyRecognizer.mk_tokenSet_88());
    public static final BitSet _tokenSet_89 = new BitSet(GroovyRecognizer.mk_tokenSet_89());
    public static final BitSet _tokenSet_90 = new BitSet(GroovyRecognizer.mk_tokenSet_90());
    public static final BitSet _tokenSet_91 = new BitSet(GroovyRecognizer.mk_tokenSet_91());
    public static final BitSet _tokenSet_92 = new BitSet(GroovyRecognizer.mk_tokenSet_92());
    public static final BitSet _tokenSet_93 = new BitSet(GroovyRecognizer.mk_tokenSet_93());
    public static final BitSet _tokenSet_94 = new BitSet(GroovyRecognizer.mk_tokenSet_94());
    public static final BitSet _tokenSet_95 = new BitSet(GroovyRecognizer.mk_tokenSet_95());
    public static final BitSet _tokenSet_96 = new BitSet(GroovyRecognizer.mk_tokenSet_96());
    public static final BitSet _tokenSet_97 = new BitSet(GroovyRecognizer.mk_tokenSet_97());
    public static final BitSet _tokenSet_98 = new BitSet(GroovyRecognizer.mk_tokenSet_98());
    public static final BitSet _tokenSet_99 = new BitSet(GroovyRecognizer.mk_tokenSet_99());
    public static final BitSet _tokenSet_100 = new BitSet(GroovyRecognizer.mk_tokenSet_100());
    public static final BitSet _tokenSet_101 = new BitSet(GroovyRecognizer.mk_tokenSet_101());
    public static final BitSet _tokenSet_102 = new BitSet(GroovyRecognizer.mk_tokenSet_102());
    public static final BitSet _tokenSet_103 = new BitSet(GroovyRecognizer.mk_tokenSet_103());
    public static final BitSet _tokenSet_104 = new BitSet(GroovyRecognizer.mk_tokenSet_104());
    public static final BitSet _tokenSet_105 = new BitSet(GroovyRecognizer.mk_tokenSet_105());
    public static final BitSet _tokenSet_106 = new BitSet(GroovyRecognizer.mk_tokenSet_106());
    public static final BitSet _tokenSet_107 = new BitSet(GroovyRecognizer.mk_tokenSet_107());
    public static final BitSet _tokenSet_108 = new BitSet(GroovyRecognizer.mk_tokenSet_108());
    public static final BitSet _tokenSet_109 = new BitSet(GroovyRecognizer.mk_tokenSet_109());
    public static final BitSet _tokenSet_110 = new BitSet(GroovyRecognizer.mk_tokenSet_110());
    public static final BitSet _tokenSet_111 = new BitSet(GroovyRecognizer.mk_tokenSet_111());

    public static GroovyRecognizer make(GroovyLexer lexer) {
        GroovyRecognizer parser = new GroovyRecognizer(lexer.plumb());
        parser.lexer = lexer;
        lexer.parser = parser;
        parser.getASTFactory().setASTNodeClass(GroovySourceAST.class);
        parser.warningList = new ArrayList();
        return parser;
    }

    public static GroovyRecognizer make(InputStream in) {
        return GroovyRecognizer.make(new GroovyLexer(in));
    }

    public static GroovyRecognizer make(Reader in) {
        return GroovyRecognizer.make(new GroovyLexer(in));
    }

    public static GroovyRecognizer make(InputBuffer in) {
        return GroovyRecognizer.make(new GroovyLexer(in));
    }

    public static GroovyRecognizer make(LexerSharedInputState in) {
        return GroovyRecognizer.make(new GroovyLexer(in));
    }

    public List getWarningList() {
        return this.warningList;
    }

    public GroovyLexer getLexer() {
        return this.lexer;
    }

    @Override
    public void setFilename(String f) {
        super.setFilename(f);
        this.lexer.setFilename(f);
    }

    public void setSourceBuffer(SourceBuffer sourceBuffer) {
        this.sourceBuffer = sourceBuffer;
    }

    public AST create(int type, String txt, AST first) {
        AST t = this.astFactory.create(type, txt);
        if (t != null && first != null) {
            t.initialize(first);
            t.initialize(type, txt);
        }
        return t;
    }

    private AST attachLast(AST t, Object last) {
        if (t instanceof GroovySourceAST && last instanceof SourceInfo) {
            SourceInfo lastInfo = (SourceInfo)last;
            GroovySourceAST node = (GroovySourceAST)t;
            node.setColumnLast(lastInfo.getColumn());
            node.setLineLast(lastInfo.getLine());
        }
        return t;
    }

    public AST create(int type, String txt, Token first, Token last) {
        return this.attachLast(this.create(type, txt, this.astFactory.create(first)), last);
    }

    public AST create(int type, String txt, AST first, Token last) {
        return this.attachLast(this.create(type, txt, first), last);
    }

    public AST create(int type, String txt, AST first, AST last) {
        return this.attachLast(this.create(type, txt, first), last);
    }

    public Token cloneToken(Token t) {
        CommonToken clone = new CommonToken(t.getType(), t.getText());
        clone.setLine(t.getLine());
        clone.setColumn(t.getColumn());
        return clone;
    }

    @Override
    public void traceIn(String rname) throws TokenStreamException {
        if (!tracing) {
            return;
        }
        super.traceIn(rname);
    }

    @Override
    public void traceOut(String rname) throws TokenStreamException {
        if (!tracing) {
            return;
        }
        if (this.returnAST != null) {
            rname = rname + this.returnAST.toStringList();
        }
        super.traceOut(rname);
    }

    public void requireFailed(String problem, String solution) throws SemanticException {
        int colNum;
        int lineNum;
        block3: {
            Token lt = null;
            lineNum = Token.badToken.getLine();
            colNum = Token.badToken.getColumn();
            try {
                lt = this.LT(1);
                if (lt != null) {
                    lineNum = lt.getLine();
                    colNum = lt.getColumn();
                }
            }
            catch (TokenStreamException ee) {
                if (!(ee instanceof TokenStreamRecognitionException)) break block3;
                lineNum = ((TokenStreamRecognitionException)ee).recog.getLine();
                colNum = ((TokenStreamRecognitionException)ee).recog.getColumn();
            }
        }
        throw new SemanticException(problem + ";\n   solution: " + solution, this.getFilename(), lineNum, colNum);
    }

    public void addWarning(String warning, String solution) {
        Token lt = null;
        try {
            lt = this.LT(1);
        }
        catch (TokenStreamException tokenStreamException) {
            // empty catch block
        }
        if (lt == null) {
            lt = Token.badToken;
        }
        HashMap<String, Object> row = new HashMap<String, Object>();
        row.put("warning", warning);
        row.put("solution", solution);
        row.put("filename", this.getFilename());
        row.put("line", lt.getLine());
        row.put("column", lt.getColumn());
        this.warningList.add(row);
    }

    private void require(boolean z, String problem, String solution) throws SemanticException {
        if (!z) {
            this.requireFailed(problem, solution);
        }
    }

    private boolean matchGenericTypeBrackets(boolean z, String problem, String solution) throws SemanticException {
        if (!z) {
            this.matchGenericTypeBracketsFailed(problem, solution);
        }
        return z;
    }

    public void matchGenericTypeBracketsFailed(String problem, String solution) throws SemanticException {
        int colNum;
        int lineNum;
        block3: {
            Token lt = null;
            lineNum = Token.badToken.getLine();
            colNum = Token.badToken.getColumn();
            try {
                lt = this.LT(1);
                if (lt != null) {
                    lineNum = lt.getLine();
                    colNum = lt.getColumn();
                }
            }
            catch (TokenStreamException ee) {
                if (!(ee instanceof TokenStreamRecognitionException)) break block3;
                lineNum = ((TokenStreamRecognitionException)ee).recog.getLine();
                colNum = ((TokenStreamRecognitionException)ee).recog.getColumn();
            }
        }
        throw new SemanticException(problem + ";\n   solution: " + solution, this.getFilename(), lineNum, colNum);
    }

    private boolean isUpperCase(Token x) {
        if (x == null || x.getType() != 87) {
            return false;
        }
        String xtext = x.getText();
        return xtext.length() > 0 && Character.isUpperCase(xtext.charAt(0));
    }

    private boolean isConstructorIdent(Token x) {
        if (this.currentClass == null) {
            return false;
        }
        if (this.currentClass.getType() != 87) {
            return false;
        }
        String cname = this.currentClass.getText();
        if (x == null || x.getType() != 87) {
            return false;
        }
        return cname.equals(x.getText());
    }

    private void dumpTree(AST ast, String offset) {
        this.dump(ast, offset);
        for (AST node = ast.getFirstChild(); node != null; node = node.getNextSibling()) {
            this.dumpTree(node, offset + "\t");
        }
    }

    private void dump(AST node, String offset) {
        System.out.println(offset + "Type: " + this.getTokenName(node) + " text: " + node.getText());
    }

    private String getTokenName(AST node) {
        if (node == null) {
            return "null";
        }
        return this.getTokenName(node.getType());
    }

    protected GroovyRecognizer(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public GroovyRecognizer(TokenBuffer tokenBuf) {
        this(tokenBuf, 2);
    }

    protected GroovyRecognizer(TokenStream lexer, int k) {
        super(lexer, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public GroovyRecognizer(TokenStream lexer) {
        this(lexer, 2);
    }

    public GroovyRecognizer(ParserSharedInputState state) {
        super(state, 2);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public final void compilationUnit() throws RecognitionException, TokenStreamException {
        AST compilationUnit_AST;
        ASTPair currentAST;
        block17: {
            block18: {
                block16: {
                    this.returnAST = null;
                    currentAST = new ASTPair();
                    compilationUnit_AST = null;
                    switch (this.LA(1)) {
                        case 80: {
                            this.match(80);
                            break;
                        }
                        case 1: 
                        case 38: 
                        case 39: 
                        case 43: 
                        case 81: 
                        case 82: 
                        case 83: 
                        case 84: 
                        case 85: 
                        case 87: 
                        case 88: 
                        case 91: 
                        case 92: 
                        case 93: 
                        case 94: 
                        case 95: 
                        case 96: 
                        case 99: 
                        case 104: 
                        case 105: 
                        case 106: 
                        case 107: 
                        case 108: 
                        case 109: 
                        case 110: 
                        case 111: 
                        case 112: 
                        case 115: 
                        case 116: 
                        case 117: 
                        case 118: 
                        case 119: 
                        case 120: 
                        case 121: 
                        case 122: 
                        case 126: 
                        case 128: 
                        case 132: 
                        case 137: 
                        case 139: 
                        case 140: 
                        case 141: 
                        case 143: 
                        case 144: 
                        case 145: 
                        case 146: 
                        case 147: 
                        case 148: 
                        case 149: 
                        case 151: 
                        case 157: 
                        case 159: 
                        case 160: 
                        case 161: 
                        case 190: 
                        case 193: 
                        case 195: 
                        case 196: 
                        case 197: 
                        case 199: 
                        case 200: 
                        case 201: 
                        case 202: 
                        case 203: 
                        case 204: 
                        case 205: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.nls();
                    boolean synPredMatched5 = false;
                    if (!(this.LA(1) != 81 && this.LA(1) != 96 || this.LA(2) != 87 && this.LA(2) != 93)) {
                        int _m5 = this.mark();
                        synPredMatched5 = true;
                        ++this.inputState.guessing;
                        try {
                            this.annotationsOpt();
                            this.match(81);
                        }
                        catch (RecognitionException pe) {
                            synPredMatched5 = false;
                        }
                        this.rewind(_m5);
                        --this.inputState.guessing;
                    }
                    if (!synPredMatched5) break block16;
                    this.packageDefinition();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    break block17;
                }
                if (!_tokenSet_0.member(this.LA(1)) || !_tokenSet_1.member(this.LA(2))) break block18;
                switch (this.LA(1)) {
                    case 38: 
                    case 39: 
                    case 43: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 87: 
                    case 88: 
                    case 91: 
                    case 92: 
                    case 93: 
                    case 94: 
                    case 95: 
                    case 96: 
                    case 99: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 121: 
                    case 122: 
                    case 126: 
                    case 132: 
                    case 137: 
                    case 139: 
                    case 140: 
                    case 141: 
                    case 143: 
                    case 144: 
                    case 145: 
                    case 146: 
                    case 147: 
                    case 148: 
                    case 149: 
                    case 151: 
                    case 157: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 190: 
                    case 193: 
                    case 195: 
                    case 196: 
                    case 197: 
                    case 199: 
                    case 200: 
                    case 201: 
                    case 202: 
                    case 203: 
                    case 204: {
                        this.statement(1);
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break block17;
                    }
                    case 1: 
                    case 128: 
                    case 205: {
                        break block17;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        block14: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 99: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: 
                case 132: 
                case 137: 
                case 139: 
                case 140: 
                case 141: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 151: 
                case 157: 
                case 159: 
                case 160: 
                case 161: 
                case 190: 
                case 193: 
                case 195: 
                case 196: 
                case 197: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: {
                    this.statement(this.sepToken);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block14;
                }
                case 1: 
                case 128: 
                case 205: {
                    continue block14;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(1);
        this.returnAST = compilationUnit_AST = currentAST.root;
    }

    public final void nls() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object nls_AST = null;
        if (this.LA(1) == 205 && _tokenSet_2.member(this.LA(2))) {
            this.match(205);
        } else if (!_tokenSet_2.member(this.LA(1)) || !_tokenSet_3.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = nls_AST;
    }

    public final void annotationsOpt() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationsOpt_AST = null;
        Token first = this.LT(1);
        if (_tokenSet_4.member(this.LA(1)) && _tokenSet_5.member(this.LA(2))) {
            this.annotationsInternal();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_6.member(this.LA(1)) || !_tokenSet_7.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            annotationsOpt_AST = currentAST.root;
            currentAST.root = annotationsOpt_AST = this.astFactory.make(new ASTArray(2).add(this.create(65, "ANNOTATIONS", first, this.LT(1))).add(annotationsOpt_AST));
            currentAST.child = annotationsOpt_AST != null && annotationsOpt_AST.getFirstChild() != null ? annotationsOpt_AST.getFirstChild() : annotationsOpt_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotationsOpt_AST = currentAST.root;
    }

    public final void packageDefinition() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST packageDefinition_AST = null;
        AST an_AST = null;
        AST id_AST = null;
        Token first = this.LT(1);
        this.annotationsOpt();
        an_AST = this.returnAST;
        this.match(81);
        this.identifier();
        id_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            packageDefinition_AST = currentAST.root;
            currentAST.root = packageDefinition_AST = this.astFactory.make(new ASTArray(3).add(this.create(16, "package", first, this.LT(1))).add(an_AST).add(id_AST));
            currentAST.child = packageDefinition_AST != null && packageDefinition_AST.getFirstChild() != null ? packageDefinition_AST.getFirstChild() : packageDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = packageDefinition_AST = currentAST.root;
    }

    public final void statement(int prevToken) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST statement_AST = null;
        AST pfx_AST = null;
        AST es_AST = null;
        AST ale_AST = null;
        AST ifCbs_AST = null;
        AST elseCbs_AST = null;
        AST while_sce_AST = null;
        Token s = null;
        AST s_AST = null;
        AST while_cbs_AST = null;
        AST m_AST = null;
        AST switchSce_AST = null;
        AST cg_AST = null;
        AST synch_sce_AST = null;
        AST synch_cs_AST = null;
        boolean sce = false;
        Token first = this.LT(1);
        AST casesGroup_AST = null;
        switch (this.LA(1)) {
            case 137: {
                this.match(137);
                this.match(91);
                this.assignmentLessExpression();
                ale_AST = this.returnAST;
                this.match(123);
                this.nlsWarn();
                this.compatibleBodyStatement();
                ifCbs_AST = this.returnAST;
                boolean synPredMatched308 = false;
                if (_tokenSet_8.member(this.LA(1)) && _tokenSet_9.member(this.LA(2))) {
                    int _m308 = this.mark();
                    synPredMatched308 = true;
                    ++this.inputState.guessing;
                    try {
                        switch (this.LA(1)) {
                            case 128: 
                            case 205: {
                                this.sep();
                                break;
                            }
                            case 138: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        this.match(138);
                    }
                    catch (RecognitionException pe) {
                        synPredMatched308 = false;
                    }
                    this.rewind(_m308);
                    --this.inputState.guessing;
                }
                if (synPredMatched308) {
                    switch (this.LA(1)) {
                        case 128: 
                        case 205: {
                            this.sep();
                            break;
                        }
                        case 138: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.match(138);
                    this.nlsWarn();
                    this.compatibleBodyStatement();
                    elseCbs_AST = this.returnAST;
                } else if (!_tokenSet_10.member(this.LA(1)) || !_tokenSet_11.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                if (this.inputState.guessing == 0) {
                    statement_AST = currentAST.root;
                    currentAST.root = statement_AST = this.astFactory.make(new ASTArray(4).add(this.create(137, "if", first, this.LT(1))).add(ale_AST).add(ifCbs_AST).add(elseCbs_AST));
                    currentAST.child = statement_AST != null && statement_AST.getFirstChild() != null ? statement_AST.getFirstChild() : statement_AST;
                    currentAST.advanceChildToEnd();
                }
                statement_AST = currentAST.root;
                break;
            }
            case 141: {
                this.forStatement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                statement_AST = currentAST.root;
                break;
            }
            case 139: {
                this.match(139);
                this.match(91);
                sce = this.strictContextExpression(false);
                while_sce_AST = this.returnAST;
                this.match(123);
                this.nlsWarn();
                switch (this.LA(1)) {
                    case 128: {
                        s = this.LT(1);
                        s_AST = this.astFactory.create(s);
                        this.match(128);
                        break;
                    }
                    case 38: 
                    case 39: 
                    case 43: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 87: 
                    case 88: 
                    case 91: 
                    case 92: 
                    case 93: 
                    case 94: 
                    case 95: 
                    case 96: 
                    case 99: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 121: 
                    case 122: 
                    case 126: 
                    case 132: 
                    case 137: 
                    case 139: 
                    case 140: 
                    case 141: 
                    case 143: 
                    case 144: 
                    case 145: 
                    case 146: 
                    case 147: 
                    case 148: 
                    case 149: 
                    case 151: 
                    case 157: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 190: 
                    case 193: 
                    case 195: 
                    case 196: 
                    case 197: 
                    case 199: 
                    case 200: 
                    case 201: 
                    case 202: 
                    case 203: 
                    case 204: {
                        this.compatibleBodyStatement();
                        while_cbs_AST = this.returnAST;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    statement_AST = currentAST.root;
                    statement_AST = s_AST != null ? this.astFactory.make(new ASTArray(3).add(this.create(139, "Literal_while", first, this.LT(1))).add(while_sce_AST).add(s_AST)) : this.astFactory.make(new ASTArray(3).add(this.create(139, "Literal_while", first, this.LT(1))).add(while_sce_AST).add(while_cbs_AST));
                    currentAST.root = statement_AST;
                    currentAST.child = statement_AST != null && statement_AST.getFirstChild() != null ? statement_AST.getFirstChild() : statement_AST;
                    currentAST.advanceChildToEnd();
                }
                statement_AST = currentAST.root;
                break;
            }
            case 140: {
                this.match(140);
                this.match(91);
                sce = this.strictContextExpression(false);
                switchSce_AST = this.returnAST;
                this.match(123);
                this.nlsWarn();
                this.match(126);
                this.nls();
                while (this.LA(1) == 129 || this.LA(1) == 150) {
                    this.casesGroup();
                    cg_AST = this.returnAST;
                    if (this.inputState.guessing != 0) continue;
                    casesGroup_AST = this.astFactory.make(new ASTArray(3).add(null).add(casesGroup_AST).add(cg_AST));
                }
                this.match(127);
                if (this.inputState.guessing == 0) {
                    statement_AST = currentAST.root;
                    currentAST.root = statement_AST = this.astFactory.make(new ASTArray(3).add(this.create(140, "switch", first, this.LT(1))).add(switchSce_AST).add(casesGroup_AST));
                    currentAST.child = statement_AST != null && statement_AST.getFirstChild() != null ? statement_AST.getFirstChild() : statement_AST;
                    currentAST.advanceChildToEnd();
                }
                statement_AST = currentAST.root;
                break;
            }
            case 151: {
                this.tryBlock();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                statement_AST = currentAST.root;
                break;
            }
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: {
                this.branchStatement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                statement_AST = currentAST.root;
                break;
            }
            default: {
                boolean synPredMatched295 = false;
                if (_tokenSet_12.member(this.LA(1)) && _tokenSet_13.member(this.LA(2))) {
                    int _m295 = this.mark();
                    synPredMatched295 = true;
                    ++this.inputState.guessing;
                    try {
                        this.genericMethodStart();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched295 = false;
                    }
                    this.rewind(_m295);
                    --this.inputState.guessing;
                }
                if (synPredMatched295) {
                    this.genericMethod();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                boolean synPredMatched297 = false;
                if (_tokenSet_12.member(this.LA(1)) && _tokenSet_14.member(this.LA(2))) {
                    int _m297 = this.mark();
                    synPredMatched297 = true;
                    ++this.inputState.guessing;
                    try {
                        this.multipleAssignmentDeclarationStart();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched297 = false;
                    }
                    this.rewind(_m297);
                    --this.inputState.guessing;
                }
                if (synPredMatched297) {
                    this.multipleAssignmentDeclaration();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                boolean synPredMatched299 = false;
                if (_tokenSet_15.member(this.LA(1)) && _tokenSet_16.member(this.LA(2))) {
                    int _m299 = this.mark();
                    synPredMatched299 = true;
                    ++this.inputState.guessing;
                    try {
                        this.declarationStart();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched299 = false;
                    }
                    this.rewind(_m299);
                    --this.inputState.guessing;
                }
                if (synPredMatched299) {
                    this.declaration();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                boolean synPredMatched301 = false;
                if (this.LA(1) == 87 && this.LA(2) == 136) {
                    int _m301 = this.mark();
                    synPredMatched301 = true;
                    ++this.inputState.guessing;
                    try {
                        this.match(87);
                        this.match(136);
                    }
                    catch (RecognitionException pe) {
                        synPredMatched301 = false;
                    }
                    this.rewind(_m301);
                    --this.inputState.guessing;
                }
                if (synPredMatched301) {
                    this.statementLabelPrefix();
                    pfx_AST = this.returnAST;
                    if (this.inputState.guessing == 0) {
                        statement_AST = currentAST.root;
                        currentAST.root = statement_AST = pfx_AST;
                        currentAST.child = statement_AST != null && statement_AST.getFirstChild() != null ? statement_AST.getFirstChild() : statement_AST;
                        currentAST.advanceChildToEnd();
                    }
                    boolean synPredMatched304 = false;
                    if (this.LA(1) == 126 && _tokenSet_17.member(this.LA(2))) {
                        int _m304 = this.mark();
                        synPredMatched304 = true;
                        ++this.inputState.guessing;
                        try {
                            this.match(126);
                        }
                        catch (RecognitionException pe) {
                            synPredMatched304 = false;
                        }
                        this.rewind(_m304);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched304) {
                        this.openOrClosableBlock();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                    } else if (_tokenSet_18.member(this.LA(1)) && _tokenSet_1.member(this.LA(2))) {
                        this.statement(136);
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                    } else {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    statement_AST = currentAST.root;
                    break;
                }
                if (_tokenSet_19.member(this.LA(1)) && _tokenSet_1.member(this.LA(2))) {
                    this.expressionStatement(prevToken);
                    es_AST = this.returnAST;
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                boolean synPredMatched312 = false;
                if ((this.LA(1) == 82 || this.LA(1) == 96) && _tokenSet_20.member(this.LA(2))) {
                    int _m312 = this.mark();
                    synPredMatched312 = true;
                    ++this.inputState.guessing;
                    try {
                        this.annotationsOpt();
                        this.match(82);
                    }
                    catch (RecognitionException pe) {
                        synPredMatched312 = false;
                    }
                    this.rewind(_m312);
                    --this.inputState.guessing;
                }
                if (synPredMatched312) {
                    this.importStatement();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                if (_tokenSet_21.member(this.LA(1)) && _tokenSet_22.member(this.LA(2))) {
                    this.modifiersOpt();
                    m_AST = this.returnAST;
                    this.typeDefinitionInternal(m_AST);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                if (this.LA(1) == 121 && this.LA(2) == 91) {
                    this.match(121);
                    this.match(91);
                    sce = this.strictContextExpression(false);
                    synch_sce_AST = this.returnAST;
                    this.match(123);
                    this.nlsWarn();
                    this.compoundStatement();
                    synch_cs_AST = this.returnAST;
                    if (this.inputState.guessing == 0) {
                        statement_AST = currentAST.root;
                        currentAST.root = statement_AST = this.astFactory.make(new ASTArray(3).add(this.create(121, "synchronized", first, this.LT(1))).add(synch_sce_AST).add(synch_cs_AST));
                        currentAST.child = statement_AST != null && statement_AST.getFirstChild() != null ? statement_AST.getFirstChild() : statement_AST;
                        currentAST.advanceChildToEnd();
                    }
                    statement_AST = currentAST.root;
                    break;
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = statement_AST;
    }

    public final void sep() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object sep_AST = null;
        switch (this.LA(1)) {
            case 128: {
                this.match(128);
                while (this.LA(1) == 205 && _tokenSet_23.member(this.LA(2))) {
                    this.match(205);
                }
                if (this.inputState.guessing != 0) break;
                this.sepToken = 128;
                break;
            }
            case 205: {
                this.match(205);
                if (this.inputState.guessing == 0) {
                    this.sepToken = 205;
                }
                while (this.LA(1) == 128 && _tokenSet_23.member(this.LA(2))) {
                    this.match(128);
                    while (this.LA(1) == 205 && _tokenSet_23.member(this.LA(2))) {
                        this.match(205);
                    }
                    if (this.inputState.guessing != 0) continue;
                    this.sepToken = 128;
                }
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = sep_AST;
    }

    public final void snippetUnit() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST snippetUnit_AST = null;
        this.nls();
        this.blockBody(1);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = snippetUnit_AST = currentAST.root;
    }

    public final void blockBody(int prevToken) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST blockBody_AST = null;
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: 
            case 132: 
            case 137: 
            case 139: 
            case 140: 
            case 141: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 151: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.statement(prevToken);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 1: 
            case 127: 
            case 128: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        block8: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 99: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: 
                case 132: 
                case 137: 
                case 139: 
                case 140: 
                case 141: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 151: 
                case 157: 
                case 159: 
                case 160: 
                case 161: 
                case 190: 
                case 193: 
                case 195: 
                case 196: 
                case 197: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: {
                    this.statement(this.sepToken);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block8;
                }
                case 1: 
                case 127: 
                case 128: 
                case 205: {
                    continue block8;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = blockBody_AST = currentAST.root;
    }

    public final void identifier() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST identifier_AST = null;
        Token i1 = null;
        AST i1_AST = null;
        Token d = null;
        AST d_AST = null;
        Token i2 = null;
        AST i2_AST = null;
        Token first = this.LT(1);
        i1 = this.LT(1);
        i1_AST = this.astFactory.create(i1);
        this.match(87);
        while (this.LA(1) == 90) {
            d = this.LT(1);
            d_AST = this.astFactory.create(d);
            this.match(90);
            this.nls();
            i2 = this.LT(1);
            i2_AST = this.astFactory.create(i2);
            this.match(87);
            if (this.inputState.guessing != 0) continue;
            i1_AST = this.astFactory.make(new ASTArray(3).add(this.create(90, ".", first, this.LT(1))).add(i1_AST).add(i2_AST));
        }
        if (this.inputState.guessing == 0) {
            identifier_AST = currentAST.root;
            currentAST.root = identifier_AST = i1_AST;
            currentAST.child = identifier_AST != null && identifier_AST.getFirstChild() != null ? identifier_AST.getFirstChild() : identifier_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = identifier_AST = currentAST.root;
    }

    public final void importStatement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST importStatement_AST = null;
        AST an_AST = null;
        AST is_AST = null;
        Token first = this.LT(1);
        boolean isStatic = false;
        this.annotationsOpt();
        an_AST = this.returnAST;
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(82);
        switch (this.LA(1)) {
            case 83: {
                this.match(83);
                if (this.inputState.guessing != 0) break;
                isStatic = true;
                break;
            }
            case 87: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.identifierStar();
        is_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            importStatement_AST = currentAST.root;
            importStatement_AST = isStatic ? this.astFactory.make(new ASTArray(3).add(this.create(60, "static_import", first, this.LT(1))).add(an_AST).add(is_AST)) : this.astFactory.make(new ASTArray(3).add(this.create(29, "import", first, this.LT(1))).add(an_AST).add(is_AST));
            currentAST.root = importStatement_AST;
            currentAST.child = importStatement_AST != null && importStatement_AST.getFirstChild() != null ? importStatement_AST.getFirstChild() : importStatement_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = importStatement_AST = currentAST.root;
    }

    public final void identifierStar() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST identifierStar_AST = null;
        Token i1 = null;
        AST i1_AST = null;
        Token d1 = null;
        AST d1_AST = null;
        Token i2 = null;
        AST i2_AST = null;
        Token d2 = null;
        AST d2_AST = null;
        Token s = null;
        AST s_AST = null;
        Token alias = null;
        AST alias_AST = null;
        Token first = this.LT(1);
        i1 = this.LT(1);
        i1_AST = this.astFactory.create(i1);
        this.match(87);
        while (this.LA(1) == 90 && (this.LA(2) == 87 || this.LA(2) == 205)) {
            d1 = this.LT(1);
            d1_AST = this.astFactory.create(d1);
            this.match(90);
            this.nls();
            i2 = this.LT(1);
            i2_AST = this.astFactory.create(i2);
            this.match(87);
            if (this.inputState.guessing != 0) continue;
            i1_AST = this.astFactory.make(new ASTArray(3).add(this.create(90, ".", first, this.LT(1))).add(i1_AST).add(i2_AST));
        }
        switch (this.LA(1)) {
            case 90: {
                d2 = this.LT(1);
                d2_AST = this.astFactory.create(d2);
                this.match(90);
                this.nls();
                s = this.LT(1);
                s_AST = this.astFactory.create(s);
                this.match(113);
                if (this.inputState.guessing != 0) break;
                i1_AST = this.astFactory.make(new ASTArray(3).add(this.create(90, ".", first, this.LT(1))).add(i1_AST).add(s_AST));
                break;
            }
            case 114: {
                this.match(114);
                this.nls();
                alias = this.LT(1);
                alias_AST = this.astFactory.create(alias);
                this.match(87);
                if (this.inputState.guessing != 0) break;
                i1_AST = this.astFactory.make(new ASTArray(3).add(this.create(114, "as", first, this.LT(1))).add(i1_AST).add(alias_AST));
                break;
            }
            case 1: 
            case 127: 
            case 128: 
            case 129: 
            case 138: 
            case 150: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            identifierStar_AST = currentAST.root;
            currentAST.root = identifierStar_AST = i1_AST;
            currentAST.child = identifierStar_AST != null && identifierStar_AST.getFirstChild() != null ? identifierStar_AST.getFirstChild() : identifierStar_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = identifierStar_AST = currentAST.root;
    }

    protected final void typeDefinitionInternal(AST mods) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeDefinitionInternal_AST = null;
        AST cd_AST = null;
        AST td_AST = null;
        AST id_AST = null;
        AST ed_AST = null;
        AST ad_AST = null;
        switch (this.LA(1)) {
            case 92: {
                this.classDefinition(mods);
                cd_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.inputState.guessing == 0) {
                    typeDefinitionInternal_AST = currentAST.root;
                    currentAST.root = typeDefinitionInternal_AST = cd_AST;
                    currentAST.child = typeDefinitionInternal_AST != null && typeDefinitionInternal_AST.getFirstChild() != null ? typeDefinitionInternal_AST.getFirstChild() : typeDefinitionInternal_AST;
                    currentAST.advanceChildToEnd();
                }
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            case 95: {
                this.traitDefinition(mods);
                td_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.inputState.guessing == 0) {
                    typeDefinitionInternal_AST = currentAST.root;
                    currentAST.root = typeDefinitionInternal_AST = td_AST;
                    currentAST.child = typeDefinitionInternal_AST != null && typeDefinitionInternal_AST.getFirstChild() != null ? typeDefinitionInternal_AST.getFirstChild() : typeDefinitionInternal_AST;
                    currentAST.advanceChildToEnd();
                }
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            case 93: {
                this.interfaceDefinition(mods);
                id_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.inputState.guessing == 0) {
                    typeDefinitionInternal_AST = currentAST.root;
                    currentAST.root = typeDefinitionInternal_AST = id_AST;
                    currentAST.child = typeDefinitionInternal_AST != null && typeDefinitionInternal_AST.getFirstChild() != null ? typeDefinitionInternal_AST.getFirstChild() : typeDefinitionInternal_AST;
                    currentAST.advanceChildToEnd();
                }
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            case 94: {
                this.enumDefinition(mods);
                ed_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.inputState.guessing == 0) {
                    typeDefinitionInternal_AST = currentAST.root;
                    currentAST.root = typeDefinitionInternal_AST = ed_AST;
                    currentAST.child = typeDefinitionInternal_AST != null && typeDefinitionInternal_AST.getFirstChild() != null ? typeDefinitionInternal_AST.getFirstChild() : typeDefinitionInternal_AST;
                    currentAST.advanceChildToEnd();
                }
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            case 96: {
                this.annotationDefinition(mods);
                ad_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.inputState.guessing == 0) {
                    typeDefinitionInternal_AST = currentAST.root;
                    currentAST.root = typeDefinitionInternal_AST = ad_AST;
                    currentAST.child = typeDefinitionInternal_AST != null && typeDefinitionInternal_AST.getFirstChild() != null ? typeDefinitionInternal_AST.getFirstChild() : typeDefinitionInternal_AST;
                    currentAST.advanceChildToEnd();
                }
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = typeDefinitionInternal_AST;
    }

    public final void classDefinition(AST modifiers) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST classDefinition_AST = null;
        AST tp_AST = null;
        AST sc_AST = null;
        AST ic_AST = null;
        AST cb_AST = null;
        Token first = this.cloneToken(this.LT(1));
        AST prevCurrentClass = this.currentClass;
        if (modifiers != null) {
            first.setLine(modifiers.getLine());
            first.setColumn(modifiers.getColumn());
        }
        this.match(92);
        AST tmp29_AST = null;
        tmp29_AST = this.astFactory.create(this.LT(1));
        this.match(87);
        this.nls();
        if (this.inputState.guessing == 0) {
            this.currentClass = tmp29_AST;
        }
        switch (this.LA(1)) {
            case 89: {
                this.typeParameters();
                tp_AST = this.returnAST;
                this.nls();
                break;
            }
            case 98: 
            case 126: 
            case 131: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.superClassClause();
        sc_AST = this.returnAST;
        this.implementsClause();
        ic_AST = this.returnAST;
        this.classBlock();
        cb_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            classDefinition_AST = currentAST.root;
            currentAST.root = classDefinition_AST = this.astFactory.make(new ASTArray(7).add(this.create(13, "CLASS_DEF", first, this.LT(1))).add(modifiers).add(tmp29_AST).add(tp_AST).add(sc_AST).add(ic_AST).add(cb_AST));
            currentAST.child = classDefinition_AST != null && classDefinition_AST.getFirstChild() != null ? classDefinition_AST.getFirstChild() : classDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        if (this.inputState.guessing == 0) {
            this.currentClass = prevCurrentClass;
        }
        this.returnAST = classDefinition_AST;
    }

    public final void traitDefinition(AST modifiers) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST traitDefinition_AST = null;
        AST tp_AST = null;
        AST sc_AST = null;
        AST ic_AST = null;
        AST cb_AST = null;
        Token first = this.cloneToken(this.LT(1));
        AST prevCurrentClass = this.currentClass;
        if (modifiers != null) {
            first.setLine(modifiers.getLine());
            first.setColumn(modifiers.getColumn());
        }
        this.match(95);
        AST tmp31_AST = null;
        tmp31_AST = this.astFactory.create(this.LT(1));
        this.match(87);
        this.nls();
        if (this.inputState.guessing == 0) {
            this.currentClass = tmp31_AST;
        }
        switch (this.LA(1)) {
            case 89: {
                this.typeParameters();
                tp_AST = this.returnAST;
                this.nls();
                break;
            }
            case 98: 
            case 126: 
            case 131: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.superClassClause();
        sc_AST = this.returnAST;
        this.implementsClause();
        ic_AST = this.returnAST;
        this.classBlock();
        cb_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            traitDefinition_AST = currentAST.root;
            currentAST.root = traitDefinition_AST = this.astFactory.make(new ASTArray(7).add(this.create(15, "TRAIT_DEF", first, this.LT(1))).add(modifiers).add(tmp31_AST).add(tp_AST).add(sc_AST).add(ic_AST).add(cb_AST));
            currentAST.child = traitDefinition_AST != null && traitDefinition_AST.getFirstChild() != null ? traitDefinition_AST.getFirstChild() : traitDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        if (this.inputState.guessing == 0) {
            this.currentClass = prevCurrentClass;
        }
        this.returnAST = traitDefinition_AST;
    }

    public final void interfaceDefinition(AST modifiers) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST interfaceDefinition_AST = null;
        AST tp_AST = null;
        AST ie_AST = null;
        AST ib_AST = null;
        Token first = this.cloneToken(this.LT(1));
        if (modifiers != null) {
            first.setLine(modifiers.getLine());
            first.setColumn(modifiers.getColumn());
        }
        this.match(93);
        AST tmp33_AST = null;
        tmp33_AST = this.astFactory.create(this.LT(1));
        this.match(87);
        this.nls();
        switch (this.LA(1)) {
            case 89: {
                this.typeParameters();
                tp_AST = this.returnAST;
                this.nls();
                break;
            }
            case 98: 
            case 126: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.interfaceExtends();
        ie_AST = this.returnAST;
        this.interfaceBlock();
        ib_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            interfaceDefinition_AST = currentAST.root;
            currentAST.root = interfaceDefinition_AST = this.astFactory.make(new ASTArray(6).add(this.create(14, "INTERFACE_DEF", first, this.LT(1))).add(modifiers).add(tmp33_AST).add(tp_AST).add(ie_AST).add(ib_AST));
            currentAST.child = interfaceDefinition_AST != null && interfaceDefinition_AST.getFirstChild() != null ? interfaceDefinition_AST.getFirstChild() : interfaceDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = interfaceDefinition_AST;
    }

    public final void enumDefinition(AST modifiers) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumDefinition_AST = null;
        AST ic_AST = null;
        AST eb_AST = null;
        Token first = this.cloneToken(this.LT(1));
        AST prevCurrentClass = this.currentClass;
        if (modifiers != null) {
            first.setLine(modifiers.getLine());
            first.setColumn(modifiers.getColumn());
        }
        this.match(94);
        AST tmp35_AST = null;
        tmp35_AST = this.astFactory.create(this.LT(1));
        this.match(87);
        if (this.inputState.guessing == 0) {
            this.currentClass = tmp35_AST;
        }
        this.nls();
        this.implementsClause();
        ic_AST = this.returnAST;
        this.nls();
        this.enumBlock();
        eb_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            enumDefinition_AST = currentAST.root;
            currentAST.root = enumDefinition_AST = this.astFactory.make(new ASTArray(5).add(this.create(61, "ENUM_DEF", first, this.LT(1))).add(modifiers).add(tmp35_AST).add(ic_AST).add(eb_AST));
            currentAST.child = enumDefinition_AST != null && enumDefinition_AST.getFirstChild() != null ? enumDefinition_AST.getFirstChild() : enumDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        if (this.inputState.guessing == 0) {
            this.currentClass = prevCurrentClass;
        }
        this.returnAST = enumDefinition_AST;
    }

    public final void annotationDefinition(AST modifiers) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationDefinition_AST = null;
        AST ab_AST = null;
        Token first = this.cloneToken(this.LT(1));
        if (modifiers != null) {
            first.setLine(modifiers.getLine());
            first.setColumn(modifiers.getColumn());
        }
        AST tmp36_AST = null;
        tmp36_AST = this.astFactory.create(this.LT(1));
        this.match(96);
        this.match(93);
        AST tmp38_AST = null;
        tmp38_AST = this.astFactory.create(this.LT(1));
        this.match(87);
        this.nls();
        this.annotationBlock();
        ab_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            annotationDefinition_AST = currentAST.root;
            currentAST.root = annotationDefinition_AST = this.astFactory.make(new ASTArray(4).add(this.create(64, "ANNOTATION_DEF", first, this.LT(1))).add(modifiers).add(tmp38_AST).add(ab_AST));
            currentAST.child = annotationDefinition_AST != null && annotationDefinition_AST.getFirstChild() != null ? annotationDefinition_AST.getFirstChild() : annotationDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotationDefinition_AST;
    }

    public final void declaration() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST declaration_AST = null;
        AST m_AST = null;
        AST t_AST = null;
        AST v_AST = null;
        AST t2_AST = null;
        AST v2_AST = null;
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 96: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: {
                this.modifiers();
                m_AST = this.returnAST;
                if (_tokenSet_24.member(this.LA(1)) && _tokenSet_25.member(this.LA(2))) {
                    this.typeSpec(false);
                    t_AST = this.returnAST;
                } else if (this.LA(1) != 87 && this.LA(1) != 88 || !_tokenSet_26.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                this.variableDefinitions(m_AST, t_AST);
                v_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                declaration_AST = currentAST.root;
                currentAST.root = declaration_AST = v_AST;
                currentAST.child = declaration_AST != null && declaration_AST.getFirstChild() != null ? declaration_AST.getFirstChild() : declaration_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            case 87: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.typeSpec(false);
                t2_AST = this.returnAST;
                this.variableDefinitions(null, t2_AST);
                v2_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                declaration_AST = currentAST.root;
                currentAST.root = declaration_AST = v2_AST;
                currentAST.child = declaration_AST != null && declaration_AST.getFirstChild() != null ? declaration_AST.getFirstChild() : declaration_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = declaration_AST;
    }

    public final void modifiers() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST modifiers_AST = null;
        Token first = this.LT(1);
        this.modifiersInternal();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            modifiers_AST = currentAST.root;
            currentAST.root = modifiers_AST = this.astFactory.make(new ASTArray(2).add(this.create(5, "MODIFIERS", first, this.LT(1))).add(modifiers_AST));
            currentAST.child = modifiers_AST != null && modifiers_AST.getFirstChild() != null ? modifiers_AST.getFirstChild() : modifiers_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = modifiers_AST = currentAST.root;
    }

    public final void typeSpec(boolean addImagNode) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeSpec_AST = null;
        switch (this.LA(1)) {
            case 87: {
                this.classTypeSpec(addImagNode);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeSpec_AST = currentAST.root;
                break;
            }
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.builtInTypeSpec(addImagNode);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeSpec_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = typeSpec_AST;
    }

    public final void variableDefinitions(AST mods, AST t) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST variableDefinitions_AST = null;
        Token id = null;
        AST id_AST = null;
        Token qid = null;
        AST qid_AST = null;
        AST param_AST = null;
        AST tc_AST = null;
        AST mb_AST = null;
        Token first = this.cloneToken(this.LT(1));
        if (mods != null) {
            first.setLine(mods.getLine());
            first.setColumn(mods.getColumn());
        } else if (t != null) {
            first.setLine(t.getLine());
            first.setColumn(t.getColumn());
        }
        if (this.LA(1) == 87 && _tokenSet_27.member(this.LA(2))) {
            this.listOfVariables(mods, t, first);
            this.astFactory.addASTChild(currentAST, this.returnAST);
            variableDefinitions_AST = currentAST.root;
        } else if ((this.LA(1) == 87 || this.LA(1) == 88) && this.LA(2) == 91) {
            switch (this.LA(1)) {
                case 87: {
                    id = this.LT(1);
                    id_AST = this.astFactory.create(id);
                    this.astFactory.addASTChild(currentAST, id_AST);
                    this.match(87);
                    break;
                }
                case 88: {
                    qid = this.LT(1);
                    qid_AST = this.astFactory.create(qid);
                    this.astFactory.addASTChild(currentAST, qid_AST);
                    this.match(88);
                    if (this.inputState.guessing != 0) break;
                    qid_AST.setType(87);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(91);
            this.parameterDeclarationList();
            param_AST = this.returnAST;
            this.match(123);
            boolean synPredMatched240 = false;
            if ((this.LA(1) == 130 || this.LA(1) == 205) && _tokenSet_28.member(this.LA(2))) {
                int _m240 = this.mark();
                synPredMatched240 = true;
                ++this.inputState.guessing;
                try {
                    this.nls();
                    this.match(130);
                }
                catch (RecognitionException pe) {
                    synPredMatched240 = false;
                }
                this.rewind(_m240);
                --this.inputState.guessing;
            }
            if (synPredMatched240) {
                this.throwsClause();
                tc_AST = this.returnAST;
            } else if (!_tokenSet_29.member(this.LA(1)) || !_tokenSet_11.member(this.LA(2))) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            boolean synPredMatched243 = false;
            if ((this.LA(1) == 126 || this.LA(1) == 205) && _tokenSet_30.member(this.LA(2))) {
                int _m243 = this.mark();
                synPredMatched243 = true;
                ++this.inputState.guessing;
                try {
                    this.nls();
                    this.match(126);
                }
                catch (RecognitionException pe) {
                    synPredMatched243 = false;
                }
                this.rewind(_m243);
                --this.inputState.guessing;
            }
            if (synPredMatched243) {
                this.nlsWarn();
                this.openBlock();
                mb_AST = this.returnAST;
            } else if (!_tokenSet_10.member(this.LA(1)) || !_tokenSet_11.member(this.LA(2))) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            if (this.inputState.guessing == 0) {
                variableDefinitions_AST = currentAST.root;
                if (qid_AST != null) {
                    id_AST = qid_AST;
                }
                currentAST.root = variableDefinitions_AST = this.astFactory.make(new ASTArray(7).add(this.create(8, "METHOD_DEF", first, this.LT(1))).add(mods).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t))).add(id_AST).add(param_AST).add(tc_AST).add(mb_AST));
                currentAST.child = variableDefinitions_AST != null && variableDefinitions_AST.getFirstChild() != null ? variableDefinitions_AST.getFirstChild() : variableDefinitions_AST;
                currentAST.advanceChildToEnd();
            }
            variableDefinitions_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = variableDefinitions_AST;
    }

    public final void genericMethod() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST genericMethod_AST = null;
        AST m_AST = null;
        AST p_AST = null;
        AST t_AST = null;
        AST v_AST = null;
        this.modifiers();
        m_AST = this.returnAST;
        this.typeParameters();
        p_AST = this.returnAST;
        this.typeSpec(false);
        t_AST = this.returnAST;
        this.variableDefinitions(m_AST, t_AST);
        v_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            genericMethod_AST = currentAST.root;
            genericMethod_AST = v_AST;
            AST old = v_AST.getFirstChild();
            genericMethod_AST.setFirstChild(p_AST);
            p_AST.setNextSibling(old);
            currentAST.root = genericMethod_AST;
            currentAST.child = genericMethod_AST != null && genericMethod_AST.getFirstChild() != null ? genericMethod_AST.getFirstChild() : genericMethod_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = genericMethod_AST;
    }

    public final void typeParameters() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeParameters_AST = null;
        Token first = this.LT(1);
        int currentLtLevel = 0;
        if (this.inputState.guessing == 0) {
            currentLtLevel = this.ltCounter;
        }
        this.match(89);
        if (this.inputState.guessing == 0) {
            ++this.ltCounter;
        }
        this.nls();
        this.typeParameter();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101) {
            this.match(101);
            this.nls();
            this.typeParameter();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.nls();
        switch (this.LA(1)) {
            case 100: 
            case 102: 
            case 103: {
                this.typeArgumentsOrParametersEnd();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 87: 
            case 88: 
            case 98: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 126: 
            case 131: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (!this.matchGenericTypeBrackets(currentLtLevel != 0 || this.ltCounter == currentLtLevel, "Missing closing bracket '>' for generics types", "Please specify the missing bracket!")) {
            throw new SemanticException("matchGenericTypeBrackets(((currentLtLevel != 0) || ltCounter == currentLtLevel),\n        \"Missing closing bracket '>' for generics types\", \"Please specify the missing bracket!\")");
        }
        if (this.inputState.guessing == 0) {
            typeParameters_AST = currentAST.root;
            currentAST.root = typeParameters_AST = this.astFactory.make(new ASTArray(2).add(this.create(72, "TYPE_PARAMETERS", first, this.LT(1))).add(typeParameters_AST));
            currentAST.child = typeParameters_AST != null && typeParameters_AST.getFirstChild() != null ? typeParameters_AST.getFirstChild() : typeParameters_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeParameters_AST = currentAST.root;
    }

    public final void singleDeclarationNoInit() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST singleDeclarationNoInit_AST = null;
        AST m_AST = null;
        AST t_AST = null;
        AST v_AST = null;
        AST t2_AST = null;
        AST v2_AST = null;
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 96: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: {
                this.modifiers();
                m_AST = this.returnAST;
                if (_tokenSet_24.member(this.LA(1)) && _tokenSet_31.member(this.LA(2))) {
                    this.typeSpec(false);
                    t_AST = this.returnAST;
                } else if (this.LA(1) != 87 || !_tokenSet_32.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                this.singleVariable(m_AST, t_AST);
                v_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                singleDeclarationNoInit_AST = currentAST.root;
                currentAST.root = singleDeclarationNoInit_AST = v_AST;
                currentAST.child = singleDeclarationNoInit_AST != null && singleDeclarationNoInit_AST.getFirstChild() != null ? singleDeclarationNoInit_AST.getFirstChild() : singleDeclarationNoInit_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            case 87: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.typeSpec(false);
                t2_AST = this.returnAST;
                this.singleVariable(null, t2_AST);
                v2_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                singleDeclarationNoInit_AST = currentAST.root;
                currentAST.root = singleDeclarationNoInit_AST = v2_AST;
                currentAST.child = singleDeclarationNoInit_AST != null && singleDeclarationNoInit_AST.getFirstChild() != null ? singleDeclarationNoInit_AST.getFirstChild() : singleDeclarationNoInit_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = singleDeclarationNoInit_AST;
    }

    public final void singleVariable(AST mods, AST t) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST singleVariable_AST = null;
        AST id_AST = null;
        Token first = this.LT(1);
        this.variableName();
        id_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            singleVariable_AST = currentAST.root;
            currentAST.root = singleVariable_AST = this.astFactory.make(new ASTArray(4).add(this.create(9, "VARIABLE_DEF", first, this.LT(1))).add(mods).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t))).add(id_AST));
            currentAST.child = singleVariable_AST != null && singleVariable_AST.getFirstChild() != null ? singleVariable_AST.getFirstChild() : singleVariable_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = singleVariable_AST;
    }

    public final void singleDeclaration() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST singleDeclaration_AST = null;
        AST sd_AST = null;
        this.singleDeclarationNoInit();
        sd_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            singleDeclaration_AST = currentAST.root;
            currentAST.root = singleDeclaration_AST = sd_AST;
            currentAST.child = singleDeclaration_AST != null && singleDeclaration_AST.getFirstChild() != null ? singleDeclaration_AST.getFirstChild() : singleDeclaration_AST;
            currentAST.advanceChildToEnd();
        }
        switch (this.LA(1)) {
            case 124: {
                this.varInitializer();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 1: 
            case 86: 
            case 101: 
            case 123: 
            case 128: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = singleDeclaration_AST = currentAST.root;
    }

    public final void varInitializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST varInitializer_AST = null;
        AST tmp43_AST = null;
        tmp43_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp43_AST);
        this.match(124);
        this.nls();
        this.expressionStatementNoCheck();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = varInitializer_AST = currentAST.root;
    }

    public final void declarationStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object declarationStart_AST = null;
        int _cnt29 = 0;
        block13: while (true) {
            switch (this.LA(1)) {
                case 84: {
                    this.match(84);
                    this.nls();
                    break;
                }
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: {
                    this.modifier();
                    this.nls();
                    break;
                }
                case 96: {
                    this.annotation();
                    this.nls();
                    break;
                }
                default: {
                    if (_tokenSet_24.member(this.LA(1)) && _tokenSet_33.member(this.LA(2))) {
                        if (this.LA(1) == 87 && _tokenSet_34.member(this.LA(2))) {
                            this.upperCaseIdent();
                        } else if (this.LA(1) >= 104 && this.LA(1) <= 112) {
                            this.builtInType();
                        } else if (this.LA(1) == 87 && this.LA(2) == 90) {
                            this.qualifiedTypeName();
                        } else {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        switch (this.LA(1)) {
                            case 89: {
                                this.typeArguments();
                                break;
                            }
                            case 38: 
                            case 39: 
                            case 43: 
                            case 83: 
                            case 84: 
                            case 85: 
                            case 87: 
                            case 88: 
                            case 96: 
                            case 104: 
                            case 105: 
                            case 106: 
                            case 107: 
                            case 108: 
                            case 109: 
                            case 110: 
                            case 111: 
                            case 112: 
                            case 115: 
                            case 116: 
                            case 117: 
                            case 118: 
                            case 119: 
                            case 120: 
                            case 121: 
                            case 122: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        while (this.LA(1) == 85) {
                            AST tmp45_AST = null;
                            tmp45_AST = this.astFactory.create(this.LT(1));
                            this.match(85);
                            this.balancedTokens();
                            AST tmp46_AST = null;
                            tmp46_AST = this.astFactory.create(this.LT(1));
                            this.match(86);
                        }
                        break;
                    }
                    if (_cnt29 >= 1) break block13;
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            ++_cnt29;
        }
        switch (this.LA(1)) {
            case 87: {
                AST tmp47_AST = null;
                tmp47_AST = this.astFactory.create(this.LT(1));
                this.match(87);
                break;
            }
            case 88: {
                AST tmp48_AST = null;
                tmp48_AST = this.astFactory.create(this.LT(1));
                this.match(88);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = declarationStart_AST;
    }

    public final void modifier() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST modifier_AST = null;
        switch (this.LA(1)) {
            case 115: {
                AST tmp49_AST = null;
                tmp49_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp49_AST);
                this.match(115);
                modifier_AST = currentAST.root;
                break;
            }
            case 116: {
                AST tmp50_AST = null;
                tmp50_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp50_AST);
                this.match(116);
                modifier_AST = currentAST.root;
                break;
            }
            case 117: {
                AST tmp51_AST = null;
                tmp51_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp51_AST);
                this.match(117);
                modifier_AST = currentAST.root;
                break;
            }
            case 83: {
                AST tmp52_AST = null;
                tmp52_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp52_AST);
                this.match(83);
                modifier_AST = currentAST.root;
                break;
            }
            case 118: {
                AST tmp53_AST = null;
                tmp53_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp53_AST);
                this.match(118);
                modifier_AST = currentAST.root;
                break;
            }
            case 38: {
                AST tmp54_AST = null;
                tmp54_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp54_AST);
                this.match(38);
                modifier_AST = currentAST.root;
                break;
            }
            case 39: {
                AST tmp55_AST = null;
                tmp55_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp55_AST);
                this.match(39);
                modifier_AST = currentAST.root;
                break;
            }
            case 119: {
                AST tmp56_AST = null;
                tmp56_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp56_AST);
                this.match(119);
                modifier_AST = currentAST.root;
                break;
            }
            case 120: {
                AST tmp57_AST = null;
                tmp57_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp57_AST);
                this.match(120);
                modifier_AST = currentAST.root;
                break;
            }
            case 121: {
                AST tmp58_AST = null;
                tmp58_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp58_AST);
                this.match(121);
                modifier_AST = currentAST.root;
                break;
            }
            case 122: {
                AST tmp59_AST = null;
                tmp59_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp59_AST);
                this.match(122);
                modifier_AST = currentAST.root;
                break;
            }
            case 43: {
                AST tmp60_AST = null;
                tmp60_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp60_AST);
                this.match(43);
                modifier_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = modifier_AST;
    }

    public final void annotation() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotation_AST = null;
        AST i_AST = null;
        AST args_AST = null;
        Token first = this.LT(1);
        this.match(96);
        this.identifier();
        i_AST = this.returnAST;
        this.nls();
        if (this.LA(1) == 91 && _tokenSet_35.member(this.LA(2))) {
            this.match(91);
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 98: 
                case 99: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: 
                case 129: 
                case 130: 
                case 131: 
                case 132: 
                case 137: 
                case 138: 
                case 139: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 190: 
                case 193: 
                case 195: 
                case 196: 
                case 197: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: {
                    this.annotationArguments();
                    args_AST = this.returnAST;
                    break;
                }
                case 123: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(123);
        } else if (!_tokenSet_36.member(this.LA(1)) || !_tokenSet_37.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            annotation_AST = currentAST.root;
            currentAST.root = annotation_AST = this.astFactory.make(new ASTArray(3).add(this.create(66, "ANNOTATION", first, this.LT(1))).add(i_AST).add(args_AST));
            currentAST.child = annotation_AST != null && annotation_AST.getFirstChild() != null ? annotation_AST.getFirstChild() : annotation_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotation_AST;
    }

    public final void upperCaseIdent() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST upperCaseIdent_AST = null;
        if (!this.isUpperCase(this.LT(1))) {
            throw new SemanticException("isUpperCase(LT(1))");
        }
        AST tmp64_AST = null;
        tmp64_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp64_AST);
        this.match(87);
        this.returnAST = upperCaseIdent_AST = currentAST.root;
    }

    public final void builtInType() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST builtInType_AST = null;
        switch (this.LA(1)) {
            case 104: {
                AST tmp65_AST = null;
                tmp65_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp65_AST);
                this.match(104);
                builtInType_AST = currentAST.root;
                break;
            }
            case 105: {
                AST tmp66_AST = null;
                tmp66_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp66_AST);
                this.match(105);
                builtInType_AST = currentAST.root;
                break;
            }
            case 106: {
                AST tmp67_AST = null;
                tmp67_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp67_AST);
                this.match(106);
                builtInType_AST = currentAST.root;
                break;
            }
            case 107: {
                AST tmp68_AST = null;
                tmp68_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp68_AST);
                this.match(107);
                builtInType_AST = currentAST.root;
                break;
            }
            case 108: {
                AST tmp69_AST = null;
                tmp69_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp69_AST);
                this.match(108);
                builtInType_AST = currentAST.root;
                break;
            }
            case 109: {
                AST tmp70_AST = null;
                tmp70_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp70_AST);
                this.match(109);
                builtInType_AST = currentAST.root;
                break;
            }
            case 110: {
                AST tmp71_AST = null;
                tmp71_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp71_AST);
                this.match(110);
                builtInType_AST = currentAST.root;
                break;
            }
            case 111: {
                AST tmp72_AST = null;
                tmp72_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp72_AST);
                this.match(111);
                builtInType_AST = currentAST.root;
                break;
            }
            case 112: {
                AST tmp73_AST = null;
                tmp73_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp73_AST);
                this.match(112);
                builtInType_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = builtInType_AST;
    }

    public final void qualifiedTypeName() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object qualifiedTypeName_AST = null;
        AST tmp74_AST = null;
        tmp74_AST = this.astFactory.create(this.LT(1));
        this.match(87);
        AST tmp75_AST = null;
        tmp75_AST = this.astFactory.create(this.LT(1));
        this.match(90);
        while (this.LA(1) == 87 && this.LA(2) == 90) {
            AST tmp76_AST = null;
            tmp76_AST = this.astFactory.create(this.LT(1));
            this.match(87);
            AST tmp77_AST = null;
            tmp77_AST = this.astFactory.create(this.LT(1));
            this.match(90);
        }
        this.upperCaseIdent();
        this.returnAST = qualifiedTypeName_AST;
    }

    public final void typeArguments() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArguments_AST = null;
        Token first = this.LT(1);
        int currentLtLevel = 0;
        if (this.inputState.guessing == 0) {
            currentLtLevel = this.ltCounter;
        }
        this.match(89);
        if (this.inputState.guessing == 0) {
            ++this.ltCounter;
        }
        this.nls();
        this.typeArgument();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101 && _tokenSet_38.member(this.LA(2)) && (this.inputState.guessing != 0 || this.ltCounter == currentLtLevel + 1)) {
            this.match(101);
            this.nls();
            this.typeArgument();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.nls();
        if (_tokenSet_39.member(this.LA(1)) && _tokenSet_40.member(this.LA(2))) {
            this.typeArgumentsOrParametersEnd();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_40.member(this.LA(1)) || !_tokenSet_3.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (!this.matchGenericTypeBrackets(currentLtLevel != 0 || this.ltCounter == currentLtLevel, "Missing closing bracket '>' for generics types", "Please specify the missing bracket!")) {
            throw new SemanticException("matchGenericTypeBrackets(((currentLtLevel != 0) || ltCounter == currentLtLevel),\n        \"Missing closing bracket '>' for generics types\", \"Please specify the missing bracket!\")");
        }
        if (this.inputState.guessing == 0) {
            typeArguments_AST = currentAST.root;
            currentAST.root = typeArguments_AST = this.astFactory.make(new ASTArray(2).add(this.create(70, "TYPE_ARGUMENTS", first, this.LT(1))).add(typeArguments_AST));
            currentAST.child = typeArguments_AST != null && typeArguments_AST.getFirstChild() != null ? typeArguments_AST.getFirstChild() : typeArguments_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeArguments_AST = currentAST.root;
    }

    public final void balancedTokens() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object balancedTokens_AST = null;
        while (true) {
            if (_tokenSet_41.member(this.LA(1))) {
                this.balancedBrackets();
                continue;
            }
            if (!_tokenSet_42.member(this.LA(1))) break;
            this.match(_tokenSet_42);
        }
        this.returnAST = balancedTokens_AST;
    }

    public final void genericMethodStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object genericMethodStart_AST = null;
        int _cnt33 = 0;
        block5: while (true) {
            switch (this.LA(1)) {
                case 84: {
                    this.match(84);
                    this.nls();
                    break;
                }
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: {
                    this.modifier();
                    this.nls();
                    break;
                }
                case 96: {
                    this.annotation();
                    this.nls();
                    break;
                }
                default: {
                    if (_cnt33 >= 1) break block5;
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            ++_cnt33;
        }
        AST tmp82_AST = null;
        tmp82_AST = this.astFactory.create(this.LT(1));
        this.match(89);
        this.returnAST = genericMethodStart_AST;
    }

    public final void constructorStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object constructorStart_AST = null;
        Token id = null;
        AST id_AST = null;
        this.modifiersOpt();
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.match(87);
        if (!this.isConstructorIdent(id)) {
            throw new SemanticException("isConstructorIdent(id)");
        }
        this.nls();
        this.match(91);
        this.returnAST = constructorStart_AST;
    }

    public final void modifiersOpt() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST modifiersOpt_AST = null;
        Token first = this.LT(1);
        if (_tokenSet_12.member(this.LA(1)) && _tokenSet_43.member(this.LA(2))) {
            this.modifiersInternal();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_44.member(this.LA(1)) || !_tokenSet_45.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            modifiersOpt_AST = currentAST.root;
            currentAST.root = modifiersOpt_AST = this.astFactory.make(new ASTArray(2).add(this.create(5, "MODIFIERS", first, this.LT(1))).add(modifiersOpt_AST));
            currentAST.child = modifiersOpt_AST != null && modifiersOpt_AST.getFirstChild() != null ? modifiersOpt_AST.getFirstChild() : modifiersOpt_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = modifiersOpt_AST = currentAST.root;
    }

    public final void typeDeclarationStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object typeDeclarationStart_AST = null;
        this.modifiersOpt();
        switch (this.LA(1)) {
            case 92: {
                this.match(92);
                break;
            }
            case 93: {
                this.match(93);
                break;
            }
            case 94: {
                this.match(94);
                break;
            }
            case 95: {
                this.match(95);
                break;
            }
            case 96: {
                AST tmp88_AST = null;
                tmp88_AST = this.astFactory.create(this.LT(1));
                this.match(96);
                this.match(93);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = typeDeclarationStart_AST;
    }

    public final void classTypeSpec(boolean addImagNode) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST classTypeSpec_AST = null;
        AST ct_AST = null;
        Token first = this.LT(1);
        this.classOrInterfaceType(false);
        ct_AST = this.returnAST;
        this.declaratorBrackets(ct_AST);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            classTypeSpec_AST = currentAST.root;
            if (addImagNode) {
                classTypeSpec_AST = this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(classTypeSpec_AST));
            }
            currentAST.root = classTypeSpec_AST;
            currentAST.child = classTypeSpec_AST != null && classTypeSpec_AST.getFirstChild() != null ? classTypeSpec_AST.getFirstChild() : classTypeSpec_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = classTypeSpec_AST = currentAST.root;
    }

    public final void builtInTypeSpec(boolean addImagNode) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST builtInTypeSpec_AST = null;
        AST bt_AST = null;
        Token first = this.LT(1);
        this.builtInType();
        bt_AST = this.returnAST;
        this.declaratorBrackets(bt_AST);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            builtInTypeSpec_AST = currentAST.root;
            if (addImagNode) {
                builtInTypeSpec_AST = this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(builtInTypeSpec_AST));
            }
            currentAST.root = builtInTypeSpec_AST;
            currentAST.child = builtInTypeSpec_AST != null && builtInTypeSpec_AST.getFirstChild() != null ? builtInTypeSpec_AST.getFirstChild() : builtInTypeSpec_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = builtInTypeSpec_AST = currentAST.root;
    }

    public final void classOrInterfaceType(boolean addImagNode) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST classOrInterfaceType_AST = null;
        Token i1 = null;
        AST i1_AST = null;
        Token d = null;
        AST d_AST = null;
        Token i2 = null;
        AST i2_AST = null;
        AST ta_AST = null;
        Token first = this.LT(1);
        i1 = this.LT(1);
        i1_AST = this.astFactory.create(i1);
        this.astFactory.makeASTRoot(currentAST, i1_AST);
        this.match(87);
        if (this.LA(1) == 89 && _tokenSet_38.member(this.LA(2))) {
            this.typeArguments();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) == 89 && this.LA(2) == 100) {
            this.typeArgumentsDiamond();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_40.member(this.LA(1))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        while (this.LA(1) == 90 && this.LA(2) == 87) {
            d = this.LT(1);
            d_AST = this.astFactory.create(d);
            this.match(90);
            i2 = this.LT(1);
            i2_AST = this.astFactory.create(i2);
            this.match(87);
            switch (this.LA(1)) {
                case 89: {
                    this.typeArguments();
                    ta_AST = this.returnAST;
                    break;
                }
                case 1: 
                case 38: 
                case 39: 
                case 40: 
                case 41: 
                case 42: 
                case 43: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 114: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 127: 
                case 128: 
                case 129: 
                case 130: 
                case 131: 
                case 132: 
                case 133: 
                case 134: 
                case 135: 
                case 136: 
                case 137: 
                case 138: 
                case 139: 
                case 140: 
                case 141: 
                case 142: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 150: 
                case 151: 
                case 152: 
                case 153: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 167: 
                case 168: 
                case 169: 
                case 170: 
                case 171: 
                case 172: 
                case 173: 
                case 174: 
                case 175: 
                case 176: 
                case 177: 
                case 178: 
                case 179: 
                case 180: 
                case 181: 
                case 182: 
                case 183: 
                case 184: 
                case 190: 
                case 193: 
                case 195: 
                case 196: 
                case 197: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: 
                case 205: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            if (this.inputState.guessing != 0) continue;
            i1_AST = this.astFactory.make(new ASTArray(4).add(this.create(90, ".", first, this.LT(1))).add(i1_AST).add(i2_AST).add(ta_AST));
        }
        if (this.inputState.guessing == 0) {
            classOrInterfaceType_AST = currentAST.root;
            classOrInterfaceType_AST = i1_AST;
            if (addImagNode) {
                classOrInterfaceType_AST = this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(classOrInterfaceType_AST));
            }
            currentAST.root = classOrInterfaceType_AST;
            currentAST.child = classOrInterfaceType_AST != null && classOrInterfaceType_AST.getFirstChild() != null ? classOrInterfaceType_AST.getFirstChild() : classOrInterfaceType_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = classOrInterfaceType_AST = currentAST.root;
    }

    public final void declaratorBrackets(AST typ) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST declaratorBrackets_AST = null;
        if (this.inputState.guessing == 0) {
            declaratorBrackets_AST = currentAST.root;
            currentAST.root = declaratorBrackets_AST = typ;
            currentAST.child = declaratorBrackets_AST != null && declaratorBrackets_AST.getFirstChild() != null ? declaratorBrackets_AST.getFirstChild() : declaratorBrackets_AST;
            currentAST.advanceChildToEnd();
        }
        while (this.LA(1) == 85 && this.LA(2) == 86) {
            this.match(85);
            this.match(86);
            if (this.inputState.guessing != 0) continue;
            declaratorBrackets_AST = currentAST.root;
            currentAST.root = declaratorBrackets_AST = this.astFactory.make(new ASTArray(2).add(this.create(17, "[", typ, this.LT(1))).add(declaratorBrackets_AST));
            currentAST.child = declaratorBrackets_AST != null && declaratorBrackets_AST.getFirstChild() != null ? declaratorBrackets_AST.getFirstChild() : declaratorBrackets_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = declaratorBrackets_AST = currentAST.root;
    }

    public final void typeArgumentsDiamond() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArgumentsDiamond_AST = null;
        Token first = this.LT(1);
        this.match(89);
        this.match(100);
        this.nls();
        if (this.inputState.guessing == 0) {
            typeArgumentsDiamond_AST = currentAST.root;
            currentAST.root = typeArgumentsDiamond_AST = this.astFactory.make(new ASTArray(2).add(this.create(70, "TYPE_ARGUMENTS", first, this.LT(1))).add(typeArgumentsDiamond_AST));
            currentAST.child = typeArgumentsDiamond_AST != null && typeArgumentsDiamond_AST.getFirstChild() != null ? typeArgumentsDiamond_AST.getFirstChild() : typeArgumentsDiamond_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeArgumentsDiamond_AST = currentAST.root;
    }

    public final void typeArgumentSpec() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArgumentSpec_AST = null;
        switch (this.LA(1)) {
            case 87: {
                this.classTypeSpec(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeArgumentSpec_AST = currentAST.root;
                break;
            }
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.builtInTypeArraySpec(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeArgumentSpec_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = typeArgumentSpec_AST;
    }

    public final void builtInTypeArraySpec(boolean addImagNode) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST builtInTypeArraySpec_AST = null;
        AST bt_AST = null;
        Token first = this.LT(1);
        this.builtInType();
        bt_AST = this.returnAST;
        boolean synPredMatched66 = false;
        if (_tokenSet_40.member(this.LA(1)) && _tokenSet_3.member(this.LA(2))) {
            int _m66 = this.mark();
            synPredMatched66 = true;
            ++this.inputState.guessing;
            try {
                this.match(85);
            }
            catch (RecognitionException pe) {
                synPredMatched66 = false;
            }
            this.rewind(_m66);
            --this.inputState.guessing;
        }
        if (synPredMatched66) {
            this.declaratorBrackets(bt_AST);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (_tokenSet_40.member(this.LA(1)) && _tokenSet_3.member(this.LA(2))) {
            if (this.inputState.guessing == 0) {
                this.require(false, "primitive type parameters not allowed here", "use the corresponding wrapper type, such as Integer for int");
            }
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            builtInTypeArraySpec_AST = currentAST.root;
            if (addImagNode) {
                builtInTypeArraySpec_AST = this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(builtInTypeArraySpec_AST));
            }
            currentAST.root = builtInTypeArraySpec_AST;
            currentAST.child = builtInTypeArraySpec_AST != null && builtInTypeArraySpec_AST.getFirstChild() != null ? builtInTypeArraySpec_AST.getFirstChild() : builtInTypeArraySpec_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = builtInTypeArraySpec_AST = currentAST.root;
    }

    public final void typeArgument() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArgument_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 87: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.typeArgumentSpec();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 97: {
                this.wildcardType();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            typeArgument_AST = currentAST.root;
            currentAST.root = typeArgument_AST = this.astFactory.make(new ASTArray(2).add(this.create(71, "TYPE_ARGUMENT", first, this.LT(1))).add(typeArgument_AST));
            currentAST.child = typeArgument_AST != null && typeArgument_AST.getFirstChild() != null ? typeArgument_AST.getFirstChild() : typeArgument_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeArgument_AST = currentAST.root;
    }

    public final void wildcardType() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST wildcardType_AST = null;
        AST tmp94_AST = null;
        tmp94_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp94_AST);
        this.match(97);
        boolean synPredMatched54 = false;
        if (!(this.LA(1) != 98 && this.LA(1) != 99 || this.LA(2) != 87 && this.LA(2) != 205)) {
            int _m54 = this.mark();
            synPredMatched54 = true;
            ++this.inputState.guessing;
            try {
                switch (this.LA(1)) {
                    case 98: {
                        this.match(98);
                        break;
                    }
                    case 99: {
                        this.match(99);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException pe) {
                synPredMatched54 = false;
            }
            this.rewind(_m54);
            --this.inputState.guessing;
        }
        if (synPredMatched54) {
            this.typeArgumentBounds();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_40.member(this.LA(1)) || !_tokenSet_3.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            wildcardType_AST = currentAST.root;
            wildcardType_AST.setType(74);
        }
        this.returnAST = wildcardType_AST = currentAST.root;
    }

    public final void typeArgumentBounds() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArgumentBounds_AST = null;
        Token first = this.LT(1);
        boolean isUpperBounds = false;
        switch (this.LA(1)) {
            case 98: {
                this.match(98);
                if (this.inputState.guessing != 0) break;
                isUpperBounds = true;
                break;
            }
            case 99: {
                this.match(99);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.nls();
        this.classOrInterfaceType(true);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.nls();
        if (this.inputState.guessing == 0) {
            typeArgumentBounds_AST = currentAST.root;
            typeArgumentBounds_AST = isUpperBounds ? this.astFactory.make(new ASTArray(2).add(this.create(75, "TYPE_UPPER_BOUNDS", first, this.LT(1))).add(typeArgumentBounds_AST)) : this.astFactory.make(new ASTArray(2).add(this.create(76, "TYPE_LOWER_BOUNDS", first, this.LT(1))).add(typeArgumentBounds_AST));
            currentAST.root = typeArgumentBounds_AST;
            currentAST.child = typeArgumentBounds_AST != null && typeArgumentBounds_AST.getFirstChild() != null ? typeArgumentBounds_AST.getFirstChild() : typeArgumentBounds_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeArgumentBounds_AST = currentAST.root;
    }

    protected final void typeArgumentsOrParametersEnd() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArgumentsOrParametersEnd_AST = null;
        switch (this.LA(1)) {
            case 100: {
                this.match(100);
                if (this.inputState.guessing == 0) {
                    --this.ltCounter;
                }
                typeArgumentsOrParametersEnd_AST = currentAST.root;
                break;
            }
            case 102: {
                this.match(102);
                if (this.inputState.guessing == 0) {
                    this.ltCounter -= 2;
                }
                typeArgumentsOrParametersEnd_AST = currentAST.root;
                break;
            }
            case 103: {
                this.match(103);
                if (this.inputState.guessing == 0) {
                    this.ltCounter -= 3;
                }
                typeArgumentsOrParametersEnd_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = typeArgumentsOrParametersEnd_AST;
    }

    public final void type() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST type_AST = null;
        switch (this.LA(1)) {
            case 87: {
                this.classOrInterfaceType(false);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                type_AST = currentAST.root;
                break;
            }
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.builtInType();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                type_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = type_AST;
    }

    public final void modifiersInternal() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST modifiersInternal_AST = null;
        int seenDef = 0;
        int _cnt79 = 0;
        while (true) {
            if (this.LA(1) == 84 && seenDef++ == 0) {
                this.match(84);
                this.nls();
            } else if (_tokenSet_46.member(this.LA(1))) {
                this.modifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.nls();
            } else if (this.LA(1) == 96 && this.LA(2) == 93) {
                if (this.inputState.guessing == 0) break;
                AST tmp101_AST = null;
                tmp101_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp101_AST);
                this.match(96);
                AST tmp102_AST = null;
                tmp102_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp102_AST);
                this.match(93);
            } else if (this.LA(1) == 96 && this.LA(2) == 87) {
                this.annotation();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.nls();
            } else {
                if (_cnt79 >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            ++_cnt79;
        }
        this.returnAST = modifiersInternal_AST = currentAST.root;
    }

    public final void annotationArguments() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationArguments_AST = null;
        AST v_AST = null;
        if (_tokenSet_47.member(this.LA(1)) && _tokenSet_48.member(this.LA(2))) {
            this.annotationMemberValueInitializer();
            v_AST = this.returnAST;
            this.astFactory.addASTChild(currentAST, this.returnAST);
            if (this.inputState.guessing == 0) {
                annotationArguments_AST = currentAST.root;
                Token itkn = new Token(87, "value");
                AST i = this.astFactory.make(new ASTArray(1).add(this.create(87, "value", itkn, itkn)));
                currentAST.root = annotationArguments_AST = this.astFactory.make(new ASTArray(3).add(this.create(67, "ANNOTATION_MEMBER_VALUE_PAIR", this.LT(1), this.LT(1))).add(i).add(v_AST));
                currentAST.child = annotationArguments_AST != null && annotationArguments_AST.getFirstChild() != null ? annotationArguments_AST.getFirstChild() : annotationArguments_AST;
                currentAST.advanceChildToEnd();
            }
            annotationArguments_AST = currentAST.root;
        } else if (_tokenSet_49.member(this.LA(1)) && this.LA(2) == 124) {
            this.annotationMemberValuePairs();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            annotationArguments_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = annotationArguments_AST;
    }

    public final void annotationsInternal() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationsInternal_AST = null;
        while (true) {
            if (this.LA(1) == 96 && this.LA(2) == 93) {
                if (this.inputState.guessing == 0) break;
                AST tmp103_AST = null;
                tmp103_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp103_AST);
                this.match(96);
                AST tmp104_AST = null;
                tmp104_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp104_AST);
                this.match(93);
                continue;
            }
            if (this.LA(1) != 96 || this.LA(2) != 87) break;
            this.annotation();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            this.nls();
        }
        this.returnAST = annotationsInternal_AST = currentAST.root;
    }

    public final void annotationMemberValueInitializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationMemberValueInitializer_AST = null;
        switch (this.LA(1)) {
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 126: 
            case 132: 
            case 148: 
            case 149: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.conditionalExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                annotationMemberValueInitializer_AST = currentAST.root;
                break;
            }
            case 96: {
                this.annotation();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                annotationMemberValueInitializer_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = annotationMemberValueInitializer_AST;
    }

    public final void annotationMemberValuePairs() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationMemberValuePairs_AST = null;
        this.annotationMemberValuePair();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101) {
            this.match(101);
            this.nls();
            this.annotationMemberValuePair();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = annotationMemberValuePairs_AST = currentAST.root;
    }

    public final void annotationMemberValuePair() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationMemberValuePair_AST = null;
        AST i_AST = null;
        AST v_AST = null;
        Token first = this.LT(1);
        this.annotationIdent();
        i_AST = this.returnAST;
        this.match(124);
        this.nls();
        this.annotationMemberValueInitializer();
        v_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            annotationMemberValuePair_AST = currentAST.root;
            currentAST.root = annotationMemberValuePair_AST = this.astFactory.make(new ASTArray(3).add(this.create(67, "ANNOTATION_MEMBER_VALUE_PAIR", first, this.LT(1))).add(i_AST).add(v_AST));
            currentAST.child = annotationMemberValuePair_AST != null && annotationMemberValuePair_AST.getFirstChild() != null ? annotationMemberValuePair_AST.getFirstChild() : annotationMemberValuePair_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotationMemberValuePair_AST;
    }

    public final void annotationIdent() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationIdent_AST = null;
        switch (this.LA(1)) {
            case 87: {
                AST tmp107_AST = null;
                tmp107_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp107_AST);
                this.match(87);
                annotationIdent_AST = currentAST.root;
                break;
            }
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 98: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: {
                this.keywordPropertyNames();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                annotationIdent_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = annotationIdent_AST;
    }

    public final void keywordPropertyNames() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST keywordPropertyNames_AST = null;
        switch (this.LA(1)) {
            case 114: {
                AST tmp108_AST = null;
                tmp108_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp108_AST);
                this.match(114);
                break;
            }
            case 147: {
                AST tmp109_AST = null;
                tmp109_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp109_AST);
                this.match(147);
                break;
            }
            case 144: {
                AST tmp110_AST = null;
                tmp110_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp110_AST);
                this.match(144);
                break;
            }
            case 150: {
                AST tmp111_AST = null;
                tmp111_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp111_AST);
                this.match(150);
                break;
            }
            case 153: {
                AST tmp112_AST = null;
                tmp112_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp112_AST);
                this.match(153);
                break;
            }
            case 92: {
                AST tmp113_AST = null;
                tmp113_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp113_AST);
                this.match(92);
                break;
            }
            case 41: {
                AST tmp114_AST = null;
                tmp114_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp114_AST);
                this.match(41);
                break;
            }
            case 145: {
                AST tmp115_AST = null;
                tmp115_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp115_AST);
                this.match(145);
                break;
            }
            case 84: {
                AST tmp116_AST = null;
                tmp116_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp116_AST);
                this.match(84);
                break;
            }
            case 129: {
                AST tmp117_AST = null;
                tmp117_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp117_AST);
                this.match(129);
                break;
            }
            case 42: {
                AST tmp118_AST = null;
                tmp118_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp118_AST);
                this.match(42);
                break;
            }
            case 138: {
                AST tmp119_AST = null;
                tmp119_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp119_AST);
                this.match(138);
                break;
            }
            case 94: {
                AST tmp120_AST = null;
                tmp120_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp120_AST);
                this.match(94);
                break;
            }
            case 98: {
                AST tmp121_AST = null;
                tmp121_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp121_AST);
                this.match(98);
                break;
            }
            case 157: {
                AST tmp122_AST = null;
                tmp122_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp122_AST);
                this.match(157);
                break;
            }
            case 152: {
                AST tmp123_AST = null;
                tmp123_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp123_AST);
                this.match(152);
                break;
            }
            case 141: {
                AST tmp124_AST = null;
                tmp124_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp124_AST);
                this.match(141);
                break;
            }
            case 40: {
                AST tmp125_AST = null;
                tmp125_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp125_AST);
                this.match(40);
                break;
            }
            case 137: {
                AST tmp126_AST = null;
                tmp126_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp126_AST);
                this.match(137);
                break;
            }
            case 131: {
                AST tmp127_AST = null;
                tmp127_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp127_AST);
                this.match(131);
                break;
            }
            case 82: {
                AST tmp128_AST = null;
                tmp128_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp128_AST);
                this.match(82);
                break;
            }
            case 142: {
                AST tmp129_AST = null;
                tmp129_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp129_AST);
                this.match(142);
                break;
            }
            case 158: {
                AST tmp130_AST = null;
                tmp130_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp130_AST);
                this.match(158);
                break;
            }
            case 93: {
                AST tmp131_AST = null;
                tmp131_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp131_AST);
                this.match(93);
                break;
            }
            case 159: {
                AST tmp132_AST = null;
                tmp132_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp132_AST);
                this.match(159);
                break;
            }
            case 160: {
                AST tmp133_AST = null;
                tmp133_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp133_AST);
                this.match(160);
                break;
            }
            case 81: {
                AST tmp134_AST = null;
                tmp134_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp134_AST);
                this.match(81);
                break;
            }
            case 143: {
                AST tmp135_AST = null;
                tmp135_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp135_AST);
                this.match(143);
                break;
            }
            case 99: {
                AST tmp136_AST = null;
                tmp136_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp136_AST);
                this.match(99);
                break;
            }
            case 140: {
                AST tmp137_AST = null;
                tmp137_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp137_AST);
                this.match(140);
                break;
            }
            case 132: {
                AST tmp138_AST = null;
                tmp138_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp138_AST);
                this.match(132);
                break;
            }
            case 146: {
                AST tmp139_AST = null;
                tmp139_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp139_AST);
                this.match(146);
                break;
            }
            case 130: {
                AST tmp140_AST = null;
                tmp140_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp140_AST);
                this.match(130);
                break;
            }
            case 95: {
                AST tmp141_AST = null;
                tmp141_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp141_AST);
                this.match(95);
                break;
            }
            case 161: {
                AST tmp142_AST = null;
                tmp142_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp142_AST);
                this.match(161);
                break;
            }
            case 151: {
                AST tmp143_AST = null;
                tmp143_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp143_AST);
                this.match(151);
                break;
            }
            case 139: {
                AST tmp144_AST = null;
                tmp144_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp144_AST);
                this.match(139);
                break;
            }
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: {
                this.modifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.builtInType();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            keywordPropertyNames_AST = currentAST.root;
            keywordPropertyNames_AST.setType(87);
        }
        this.returnAST = keywordPropertyNames_AST = currentAST.root;
    }

    public final void conditionalExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST conditionalExpression_AST = null;
        this.logicalOrExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        boolean synPredMatched446 = false;
        if ((this.LA(1) == 174 || this.LA(1) == 205) && _tokenSet_50.member(this.LA(2))) {
            int _m446 = this.mark();
            synPredMatched446 = true;
            ++this.inputState.guessing;
            try {
                this.nls();
                this.match(174);
            }
            catch (RecognitionException pe) {
                synPredMatched446 = false;
            }
            this.rewind(_m446);
            --this.inputState.guessing;
        }
        if (synPredMatched446) {
            this.nls();
            AST tmp145_AST = null;
            tmp145_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp145_AST);
            this.match(174);
            this.nls();
            this.conditionalExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else {
            boolean synPredMatched448 = false;
            if ((this.LA(1) == 97 || this.LA(1) == 205) && _tokenSet_51.member(this.LA(2))) {
                int _m448 = this.mark();
                synPredMatched448 = true;
                ++this.inputState.guessing;
                try {
                    this.nls();
                    this.match(97);
                }
                catch (RecognitionException pe) {
                    synPredMatched448 = false;
                }
                this.rewind(_m448);
                --this.inputState.guessing;
            }
            if (synPredMatched448) {
                this.nls();
                AST tmp146_AST = null;
                tmp146_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp146_AST);
                this.match(97);
                this.nls();
                this.assignmentExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.nls();
                this.match(136);
                this.nls();
                this.conditionalExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
            } else if (!_tokenSet_52.member(this.LA(1)) || !_tokenSet_53.member(this.LA(2))) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = conditionalExpression_AST = currentAST.root;
    }

    public final void superClassClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST superClassClause_AST = null;
        AST c_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 98: {
                this.match(98);
                this.nls();
                this.classOrInterfaceType(false);
                c_AST = this.returnAST;
                this.nls();
                break;
            }
            case 126: 
            case 131: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            superClassClause_AST = currentAST.root;
            currentAST.root = superClassClause_AST = this.astFactory.make(new ASTArray(2).add(this.create(18, "EXTENDS_CLAUSE", first, this.LT(1))).add(c_AST));
            currentAST.child = superClassClause_AST != null && superClassClause_AST.getFirstChild() != null ? superClassClause_AST.getFirstChild() : superClassClause_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = superClassClause_AST;
    }

    public final void implementsClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST implementsClause_AST = null;
        Token i = null;
        AST i_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 131: {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.match(131);
                this.nls();
                this.classOrInterfaceType(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 101) {
                    this.match(101);
                    this.nls();
                    this.classOrInterfaceType(true);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                this.nls();
                break;
            }
            case 126: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            implementsClause_AST = currentAST.root;
            currentAST.root = implementsClause_AST = this.astFactory.make(new ASTArray(2).add(this.create(19, "IMPLEMENTS_CLAUSE", first, this.LT(1))).add(implementsClause_AST));
            currentAST.child = implementsClause_AST != null && implementsClause_AST.getFirstChild() != null ? implementsClause_AST.getFirstChild() : implementsClause_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = implementsClause_AST = currentAST.root;
    }

    public final void classBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST classBlock_AST = null;
        Token first = this.LT(1);
        this.match(126);
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 87: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: {
                this.classField();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 127: 
            case 128: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        block8: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 84: 
                case 87: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: {
                    this.classField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block8;
                }
                case 127: 
                case 128: 
                case 205: {
                    continue block8;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(127);
        if (this.inputState.guessing == 0) {
            classBlock_AST = currentAST.root;
            currentAST.root = classBlock_AST = this.astFactory.make(new ASTArray(2).add(this.create(6, "OBJBLOCK", first, this.LT(1))).add(classBlock_AST));
            currentAST.child = classBlock_AST != null && classBlock_AST.getFirstChild() != null ? classBlock_AST.getFirstChild() : classBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = classBlock_AST = currentAST.root;
    }

    public final void interfaceExtends() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST interfaceExtends_AST = null;
        Token e = null;
        AST e_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 98: {
                e = this.LT(1);
                e_AST = this.astFactory.create(e);
                this.match(98);
                this.nls();
                this.classOrInterfaceType(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 101) {
                    this.match(101);
                    this.nls();
                    this.classOrInterfaceType(true);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                this.nls();
                break;
            }
            case 126: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            interfaceExtends_AST = currentAST.root;
            currentAST.root = interfaceExtends_AST = this.astFactory.make(new ASTArray(2).add(this.create(18, "EXTENDS_CLAUSE", first, this.LT(1))).add(interfaceExtends_AST));
            currentAST.child = interfaceExtends_AST != null && interfaceExtends_AST.getFirstChild() != null ? interfaceExtends_AST.getFirstChild() : interfaceExtends_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = interfaceExtends_AST = currentAST.root;
    }

    public final void interfaceBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST interfaceBlock_AST = null;
        Token first = this.LT(1);
        this.match(126);
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 87: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: {
                this.interfaceField();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 127: 
            case 128: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        block8: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 84: 
                case 87: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: {
                    this.interfaceField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block8;
                }
                case 127: 
                case 128: 
                case 205: {
                    continue block8;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(127);
        if (this.inputState.guessing == 0) {
            interfaceBlock_AST = currentAST.root;
            currentAST.root = interfaceBlock_AST = this.astFactory.make(new ASTArray(2).add(this.create(6, "OBJBLOCK", first, this.LT(1))).add(interfaceBlock_AST));
            currentAST.child = interfaceBlock_AST != null && interfaceBlock_AST.getFirstChild() != null ? interfaceBlock_AST.getFirstChild() : interfaceBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = interfaceBlock_AST = currentAST.root;
    }

    public final void enumBlock() throws RecognitionException, TokenStreamException {
        Token first;
        AST enumBlock_AST;
        ASTPair currentAST;
        block14: {
            block15: {
                block13: {
                    this.returnAST = null;
                    currentAST = new ASTPair();
                    enumBlock_AST = null;
                    first = this.LT(1);
                    this.match(126);
                    this.nls();
                    boolean synPredMatched137 = false;
                    if ((this.LA(1) == 87 || this.LA(1) == 96) && _tokenSet_54.member(this.LA(2))) {
                        int _m137 = this.mark();
                        synPredMatched137 = true;
                        ++this.inputState.guessing;
                        try {
                            this.enumConstantsStart();
                        }
                        catch (RecognitionException pe) {
                            synPredMatched137 = false;
                        }
                        this.rewind(_m137);
                        --this.inputState.guessing;
                    }
                    if (!synPredMatched137) break block13;
                    this.enumConstants();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    break block14;
                }
                if (!_tokenSet_55.member(this.LA(1)) || !_tokenSet_56.member(this.LA(2))) break block15;
                switch (this.LA(1)) {
                    case 38: 
                    case 39: 
                    case 43: 
                    case 83: 
                    case 84: 
                    case 87: 
                    case 92: 
                    case 93: 
                    case 94: 
                    case 95: 
                    case 96: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 121: 
                    case 122: 
                    case 126: {
                        this.classField();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break block14;
                    }
                    case 127: 
                    case 128: 
                    case 205: {
                        break block14;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        block10: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 84: 
                case 87: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: {
                    this.classField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block10;
                }
                case 127: 
                case 128: 
                case 205: {
                    continue block10;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(127);
        if (this.inputState.guessing == 0) {
            enumBlock_AST = currentAST.root;
            currentAST.root = enumBlock_AST = this.astFactory.make(new ASTArray(2).add(this.create(6, "OBJBLOCK", first, this.LT(1))).add(enumBlock_AST));
            currentAST.child = enumBlock_AST != null && enumBlock_AST.getFirstChild() != null ? enumBlock_AST.getFirstChild() : enumBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = enumBlock_AST = currentAST.root;
    }

    public final void annotationBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationBlock_AST = null;
        Token first = this.LT(1);
        this.match(126);
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 87: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: {
                this.annotationField();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 127: 
            case 128: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        block8: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 84: 
                case 87: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: {
                    this.annotationField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block8;
                }
                case 127: 
                case 128: 
                case 205: {
                    continue block8;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(127);
        if (this.inputState.guessing == 0) {
            annotationBlock_AST = currentAST.root;
            currentAST.root = annotationBlock_AST = this.astFactory.make(new ASTArray(2).add(this.create(6, "OBJBLOCK", first, this.LT(1))).add(annotationBlock_AST));
            currentAST.child = annotationBlock_AST != null && annotationBlock_AST.getFirstChild() != null ? annotationBlock_AST.getFirstChild() : annotationBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotationBlock_AST = currentAST.root;
    }

    public final void typeParameter() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeParameter_AST = null;
        Token id = null;
        AST id_AST = null;
        Token first = this.LT(1);
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.astFactory.addASTChild(currentAST, id_AST);
        this.match(87);
        if (this.LA(1) == 98 && (this.LA(2) == 87 || this.LA(2) == 205)) {
            this.typeParameterBounds();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_57.member(this.LA(1)) || !_tokenSet_58.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            typeParameter_AST = currentAST.root;
            currentAST.root = typeParameter_AST = this.astFactory.make(new ASTArray(2).add(this.create(73, "TYPE_PARAMETER", first, this.LT(1))).add(typeParameter_AST));
            currentAST.child = typeParameter_AST != null && typeParameter_AST.getFirstChild() != null ? typeParameter_AST.getFirstChild() : typeParameter_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeParameter_AST = currentAST.root;
    }

    public final void typeParameterBounds() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeParameterBounds_AST = null;
        Token first = this.LT(1);
        this.match(98);
        this.nls();
        this.classOrInterfaceType(true);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 125) {
            this.match(125);
            this.nls();
            this.classOrInterfaceType(true);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            typeParameterBounds_AST = currentAST.root;
            currentAST.root = typeParameterBounds_AST = this.astFactory.make(new ASTArray(2).add(this.create(75, "TYPE_UPPER_BOUNDS", first, this.LT(1))).add(typeParameterBounds_AST));
            currentAST.child = typeParameterBounds_AST != null && typeParameterBounds_AST.getFirstChild() != null ? typeParameterBounds_AST.getFirstChild() : typeParameterBounds_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeParameterBounds_AST = currentAST.root;
    }

    public final void classField() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST classField_AST = null;
        AST mc_AST = null;
        AST ctor_AST = null;
        AST dg_AST = null;
        AST mad_AST = null;
        AST dd_AST = null;
        AST mods_AST = null;
        AST td_AST = null;
        AST s3_AST = null;
        AST s4_AST = null;
        Token first = this.LT(1);
        boolean synPredMatched197 = false;
        if (_tokenSet_59.member(this.LA(1)) && _tokenSet_60.member(this.LA(2))) {
            int _m197 = this.mark();
            synPredMatched197 = true;
            ++this.inputState.guessing;
            try {
                this.constructorStart();
            }
            catch (RecognitionException pe) {
                synPredMatched197 = false;
            }
            this.rewind(_m197);
            --this.inputState.guessing;
        }
        if (synPredMatched197) {
            this.modifiersOpt();
            mc_AST = this.returnAST;
            this.constructorDefinition(mc_AST);
            ctor_AST = this.returnAST;
            if (this.inputState.guessing == 0) {
                classField_AST = currentAST.root;
                currentAST.root = classField_AST = ctor_AST;
                currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                currentAST.advanceChildToEnd();
            }
        } else {
            boolean synPredMatched199 = false;
            if (_tokenSet_12.member(this.LA(1)) && _tokenSet_13.member(this.LA(2))) {
                int _m199 = this.mark();
                synPredMatched199 = true;
                ++this.inputState.guessing;
                try {
                    this.genericMethodStart();
                }
                catch (RecognitionException pe) {
                    synPredMatched199 = false;
                }
                this.rewind(_m199);
                --this.inputState.guessing;
            }
            if (synPredMatched199) {
                this.genericMethod();
                dg_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    classField_AST = currentAST.root;
                    currentAST.root = classField_AST = dg_AST;
                    currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                    currentAST.advanceChildToEnd();
                }
            } else {
                boolean synPredMatched201 = false;
                if (_tokenSet_12.member(this.LA(1)) && _tokenSet_14.member(this.LA(2))) {
                    int _m201 = this.mark();
                    synPredMatched201 = true;
                    ++this.inputState.guessing;
                    try {
                        this.multipleAssignmentDeclarationStart();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched201 = false;
                    }
                    this.rewind(_m201);
                    --this.inputState.guessing;
                }
                if (synPredMatched201) {
                    this.multipleAssignmentDeclaration();
                    mad_AST = this.returnAST;
                    if (this.inputState.guessing == 0) {
                        classField_AST = currentAST.root;
                        currentAST.root = classField_AST = mad_AST;
                        currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                        currentAST.advanceChildToEnd();
                    }
                } else {
                    boolean synPredMatched203 = false;
                    if (_tokenSet_15.member(this.LA(1)) && _tokenSet_16.member(this.LA(2))) {
                        int _m203 = this.mark();
                        synPredMatched203 = true;
                        ++this.inputState.guessing;
                        try {
                            this.declarationStart();
                        }
                        catch (RecognitionException pe) {
                            synPredMatched203 = false;
                        }
                        this.rewind(_m203);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched203) {
                        this.declaration();
                        dd_AST = this.returnAST;
                        if (this.inputState.guessing == 0) {
                            classField_AST = currentAST.root;
                            currentAST.root = classField_AST = dd_AST;
                            currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                            currentAST.advanceChildToEnd();
                        }
                    } else {
                        boolean synPredMatched205 = false;
                        if (_tokenSet_21.member(this.LA(1)) && _tokenSet_22.member(this.LA(2))) {
                            int _m205 = this.mark();
                            synPredMatched205 = true;
                            ++this.inputState.guessing;
                            try {
                                this.typeDeclarationStart();
                            }
                            catch (RecognitionException pe) {
                                synPredMatched205 = false;
                            }
                            this.rewind(_m205);
                            --this.inputState.guessing;
                        }
                        if (synPredMatched205) {
                            this.modifiersOpt();
                            mods_AST = this.returnAST;
                            this.typeDefinitionInternal(mods_AST);
                            td_AST = this.returnAST;
                            if (this.inputState.guessing == 0) {
                                classField_AST = currentAST.root;
                                currentAST.root = classField_AST = td_AST;
                                currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                                currentAST.advanceChildToEnd();
                            }
                        } else if (this.LA(1) == 83 && (this.LA(2) == 126 || this.LA(2) == 205)) {
                            this.match(83);
                            this.nls();
                            this.compoundStatement();
                            s3_AST = this.returnAST;
                            if (this.inputState.guessing == 0) {
                                classField_AST = currentAST.root;
                                currentAST.root = classField_AST = this.astFactory.make(new ASTArray(2).add(this.create(11, "STATIC_INIT", first, this.LT(1))).add(s3_AST));
                                currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                                currentAST.advanceChildToEnd();
                            }
                        } else if (this.LA(1) == 126) {
                            this.compoundStatement();
                            s4_AST = this.returnAST;
                            if (this.inputState.guessing == 0) {
                                classField_AST = currentAST.root;
                                currentAST.root = classField_AST = this.astFactory.make(new ASTArray(2).add(this.create(10, "INSTANCE_INIT", first, this.LT(1))).add(s4_AST));
                                currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                                currentAST.advanceChildToEnd();
                            }
                        } else {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                }
            }
        }
        this.returnAST = classField_AST;
    }

    public final void interfaceField() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST interfaceField_AST = null;
        AST d_AST = null;
        AST dg_AST = null;
        AST mods_AST = null;
        AST td_AST = null;
        boolean synPredMatched209 = false;
        if (_tokenSet_15.member(this.LA(1)) && _tokenSet_16.member(this.LA(2))) {
            int _m209 = this.mark();
            synPredMatched209 = true;
            ++this.inputState.guessing;
            try {
                this.declarationStart();
            }
            catch (RecognitionException pe) {
                synPredMatched209 = false;
            }
            this.rewind(_m209);
            --this.inputState.guessing;
        }
        if (synPredMatched209) {
            this.declaration();
            d_AST = this.returnAST;
            if (this.inputState.guessing == 0) {
                interfaceField_AST = currentAST.root;
                currentAST.root = interfaceField_AST = d_AST;
                currentAST.child = interfaceField_AST != null && interfaceField_AST.getFirstChild() != null ? interfaceField_AST.getFirstChild() : interfaceField_AST;
                currentAST.advanceChildToEnd();
            }
        } else {
            boolean synPredMatched211 = false;
            if (_tokenSet_12.member(this.LA(1)) && _tokenSet_13.member(this.LA(2))) {
                int _m211 = this.mark();
                synPredMatched211 = true;
                ++this.inputState.guessing;
                try {
                    this.genericMethodStart();
                }
                catch (RecognitionException pe) {
                    synPredMatched211 = false;
                }
                this.rewind(_m211);
                --this.inputState.guessing;
            }
            if (synPredMatched211) {
                this.genericMethod();
                dg_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    interfaceField_AST = currentAST.root;
                    currentAST.root = interfaceField_AST = dg_AST;
                    currentAST.child = interfaceField_AST != null && interfaceField_AST.getFirstChild() != null ? interfaceField_AST.getFirstChild() : interfaceField_AST;
                    currentAST.advanceChildToEnd();
                }
            } else {
                boolean synPredMatched213 = false;
                if (_tokenSet_21.member(this.LA(1)) && _tokenSet_22.member(this.LA(2))) {
                    int _m213 = this.mark();
                    synPredMatched213 = true;
                    ++this.inputState.guessing;
                    try {
                        this.typeDeclarationStart();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched213 = false;
                    }
                    this.rewind(_m213);
                    --this.inputState.guessing;
                }
                if (synPredMatched213) {
                    this.modifiersOpt();
                    mods_AST = this.returnAST;
                    this.typeDefinitionInternal(mods_AST);
                    td_AST = this.returnAST;
                    if (this.inputState.guessing == 0) {
                        interfaceField_AST = currentAST.root;
                        currentAST.root = interfaceField_AST = td_AST;
                        currentAST.child = interfaceField_AST != null && interfaceField_AST.getFirstChild() != null ? interfaceField_AST.getFirstChild() : interfaceField_AST;
                        currentAST.advanceChildToEnd();
                    }
                } else {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
        }
        this.returnAST = interfaceField_AST;
    }

    public final void annotationField() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationField_AST = null;
        AST mods_AST = null;
        AST td_AST = null;
        AST t_AST = null;
        Token i = null;
        AST i_AST = null;
        AST amvi_AST = null;
        AST v_AST = null;
        Token first = this.LT(1);
        this.modifiersOpt();
        mods_AST = this.returnAST;
        switch (this.LA(1)) {
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: {
                this.typeDefinitionInternal(mods_AST);
                td_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                annotationField_AST = currentAST.root;
                currentAST.root = annotationField_AST = td_AST;
                currentAST.child = annotationField_AST != null && annotationField_AST.getFirstChild() != null ? annotationField_AST.getFirstChild() : annotationField_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            case 87: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.typeSpec(false);
                t_AST = this.returnAST;
                boolean synPredMatched161 = false;
                if (this.LA(1) == 87 && this.LA(2) == 91) {
                    int _m161 = this.mark();
                    synPredMatched161 = true;
                    ++this.inputState.guessing;
                    try {
                        this.match(87);
                        this.match(91);
                    }
                    catch (RecognitionException pe) {
                        synPredMatched161 = false;
                    }
                    this.rewind(_m161);
                    --this.inputState.guessing;
                }
                if (synPredMatched161) {
                    i = this.LT(1);
                    i_AST = this.astFactory.create(i);
                    this.match(87);
                    this.match(91);
                    this.match(123);
                    switch (this.LA(1)) {
                        case 129: {
                            this.match(129);
                            this.nls();
                            this.annotationMemberValueInitializer();
                            amvi_AST = this.returnAST;
                            break;
                        }
                        case 127: 
                        case 128: 
                        case 205: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    if (this.inputState.guessing != 0) break;
                    annotationField_AST = currentAST.root;
                    currentAST.root = annotationField_AST = this.astFactory.make(new ASTArray(5).add(this.create(68, "ANNOTATION_FIELD_DEF", first, this.LT(1))).add(mods_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t_AST))).add(i_AST).add(amvi_AST));
                    currentAST.child = annotationField_AST != null && annotationField_AST.getFirstChild() != null ? annotationField_AST.getFirstChild() : annotationField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                if ((this.LA(1) == 87 || this.LA(1) == 88) && _tokenSet_61.member(this.LA(2))) {
                    this.variableDefinitions(mods_AST, t_AST);
                    v_AST = this.returnAST;
                    if (this.inputState.guessing != 0) break;
                    annotationField_AST = currentAST.root;
                    currentAST.root = annotationField_AST = v_AST;
                    currentAST.child = annotationField_AST != null && annotationField_AST.getFirstChild() != null ? annotationField_AST.getFirstChild() : annotationField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = annotationField_AST;
    }

    public final void enumConstantsStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumConstantsStart_AST = null;
        this.annotationsOpt();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        AST tmp165_AST = null;
        tmp165_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp165_AST);
        this.match(87);
        block0 : switch (this.LA(1)) {
            case 126: {
                AST tmp166_AST = null;
                tmp166_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp166_AST);
                this.match(126);
                break;
            }
            case 91: {
                AST tmp167_AST = null;
                tmp167_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp167_AST);
                this.match(91);
                break;
            }
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 87: 
            case 96: 
            case 101: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 127: 
            case 128: 
            case 205: {
                this.nls();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 128: {
                        AST tmp168_AST = null;
                        tmp168_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp168_AST);
                        this.match(128);
                        break block0;
                    }
                    case 101: {
                        AST tmp169_AST = null;
                        tmp169_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp169_AST);
                        this.match(101);
                        break block0;
                    }
                    case 38: 
                    case 39: 
                    case 43: 
                    case 83: 
                    case 84: 
                    case 87: 
                    case 96: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 121: 
                    case 122: {
                        this.declarationStart();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break block0;
                    }
                    case 127: {
                        AST tmp170_AST = null;
                        tmp170_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp170_AST);
                        this.match(127);
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = enumConstantsStart_AST = currentAST.root;
    }

    public final void enumConstants() throws RecognitionException, TokenStreamException {
        AST enumConstants_AST;
        ASTPair currentAST;
        block22: {
            block23: {
                this.returnAST = null;
                currentAST = new ASTPair();
                enumConstants_AST = null;
                this.enumConstant();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (true) {
                    boolean synPredMatched149 = false;
                    if (_tokenSet_62.member(this.LA(1)) && _tokenSet_63.member(this.LA(2))) {
                        int _m149 = this.mark();
                        synPredMatched149 = true;
                        ++this.inputState.guessing;
                        try {
                            this.nls();
                            switch (this.LA(1)) {
                                case 128: {
                                    this.match(128);
                                    break;
                                }
                                case 127: {
                                    this.match(127);
                                    break;
                                }
                                case 38: 
                                case 39: 
                                case 43: 
                                case 83: 
                                case 84: 
                                case 87: 
                                case 92: 
                                case 93: 
                                case 94: 
                                case 95: 
                                case 96: 
                                case 104: 
                                case 105: 
                                case 106: 
                                case 107: 
                                case 108: 
                                case 109: 
                                case 110: 
                                case 111: 
                                case 112: 
                                case 115: 
                                case 116: 
                                case 117: 
                                case 118: 
                                case 119: 
                                case 120: 
                                case 121: 
                                case 122: 
                                case 126: {
                                    this.classField();
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                        }
                        catch (RecognitionException pe) {
                            synPredMatched149 = false;
                        }
                        this.rewind(_m149);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched149) {
                        if (this.inputState.guessing != 0) continue;
                        break block22;
                    }
                    if (this.LA(1) != 101 && this.LA(1) != 205 || !_tokenSet_64.member(this.LA(2))) break block22;
                    this.nls();
                    this.match(101);
                    boolean synPredMatched152 = false;
                    if (_tokenSet_65.member(this.LA(1)) && _tokenSet_66.member(this.LA(2))) {
                        int _m152 = this.mark();
                        synPredMatched152 = true;
                        ++this.inputState.guessing;
                        try {
                            this.nls();
                            this.annotationsOpt();
                            this.match(87);
                        }
                        catch (RecognitionException pe) {
                            synPredMatched152 = false;
                        }
                        this.rewind(_m152);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched152) {
                        this.nls();
                        this.enumConstant();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        continue;
                    }
                    boolean synPredMatched155 = false;
                    if (_tokenSet_62.member(this.LA(1)) && _tokenSet_63.member(this.LA(2))) {
                        int _m155 = this.mark();
                        synPredMatched155 = true;
                        ++this.inputState.guessing;
                        try {
                            this.nls();
                            switch (this.LA(1)) {
                                case 128: {
                                    this.match(128);
                                    break;
                                }
                                case 127: {
                                    this.match(127);
                                    break;
                                }
                                case 38: 
                                case 39: 
                                case 43: 
                                case 83: 
                                case 84: 
                                case 87: 
                                case 92: 
                                case 93: 
                                case 94: 
                                case 95: 
                                case 96: 
                                case 104: 
                                case 105: 
                                case 106: 
                                case 107: 
                                case 108: 
                                case 109: 
                                case 110: 
                                case 111: 
                                case 112: 
                                case 115: 
                                case 116: 
                                case 117: 
                                case 118: 
                                case 119: 
                                case 120: 
                                case 121: 
                                case 122: 
                                case 126: {
                                    this.classField();
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                        }
                        catch (RecognitionException pe) {
                            synPredMatched155 = false;
                        }
                        this.rewind(_m155);
                        --this.inputState.guessing;
                    }
                    if (!synPredMatched155) break block23;
                    if (this.inputState.guessing == 0) break;
                }
                break block22;
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = enumConstants_AST = currentAST.root;
    }

    public final void enumConstant() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumConstant_AST = null;
        AST an_AST = null;
        Token i = null;
        AST i_AST = null;
        AST a_AST = null;
        AST b_AST = null;
        Token first = this.LT(1);
        this.annotationsOpt();
        an_AST = this.returnAST;
        i = this.LT(1);
        i_AST = this.astFactory.create(i);
        this.match(87);
        switch (this.LA(1)) {
            case 91: {
                this.match(91);
                this.argList();
                a_AST = this.returnAST;
                this.match(123);
                break;
            }
            case 101: 
            case 126: 
            case 127: 
            case 128: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 126: {
                this.enumConstantBlock();
                b_AST = this.returnAST;
                break;
            }
            case 101: 
            case 127: 
            case 128: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            enumConstant_AST = currentAST.root;
            currentAST.root = enumConstant_AST = this.astFactory.make(new ASTArray(5).add(this.create(62, "ENUM_CONSTANT_DEF", first, this.LT(1))).add(an_AST).add(i_AST).add(a_AST).add(b_AST));
            currentAST.child = enumConstant_AST != null && enumConstant_AST.getFirstChild() != null ? enumConstant_AST.getFirstChild() : enumConstant_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = enumConstant_AST;
    }

    /*
     * Unable to fully structure code
     */
    public final void argList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        currentAST = new ASTPair();
        argList_AST = null;
        first = this.LT(1);
        lastComma = null;
        hls = 0;
        hls2 = 0;
        hasClosureList = false;
        trailingComma = false;
        sce = false;
        block0 : switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 98: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 113: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                hls = this.argument();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 128: {
                        _cnt549 = 0;
                        while (true) {
                            if (this.LA(1) != 128) ** GOTO lbl34
                            this.match(128);
                            if (this.inputState.guessing == 0) {
                                hasClosureList = true;
                            }
                            switch (this.LA(1)) {
                                case 38: 
                                case 39: 
                                case 43: 
                                case 83: 
                                case 84: 
                                case 85: 
                                case 87: 
                                case 88: 
                                case 91: 
                                case 96: 
                                case 99: 
                                case 104: 
                                case 105: 
                                case 106: 
                                case 107: 
                                case 108: 
                                case 109: 
                                case 110: 
                                case 111: 
                                case 112: 
                                case 115: 
                                case 116: 
                                case 117: 
                                case 118: 
                                case 119: 
                                case 120: 
                                case 121: 
                                case 122: 
                                case 126: 
                                case 132: 
                                case 143: 
                                case 144: 
                                case 145: 
                                case 146: 
                                case 147: 
                                case 148: 
                                case 149: 
                                case 157: 
                                case 159: 
                                case 160: 
                                case 161: 
                                case 190: 
                                case 193: 
                                case 195: 
                                case 196: 
                                case 197: 
                                case 199: 
                                case 200: 
                                case 201: 
                                case 202: 
                                case 203: 
                                case 204: {
                                    sce = this.strictContextExpression(true);
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    ** GOTO lbl36
                                }
                                case 86: 
                                case 123: 
                                case 128: {
                                    if (this.inputState.guessing == 0) {
                                        this.astFactory.addASTChild(currentAST, this.astFactory.create(37, "EMPTY_STAT"));
                                    }
                                    ** GOTO lbl36
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
lbl34:
                            // 1 sources

                            if (_cnt549 >= 1) break;
                            throw new NoViableAltException(this.LT(1), this.getFilename());
lbl36:
                            // 2 sources

                            ++_cnt549;
                        }
                        if (this.inputState.guessing != 0) break block0;
                        argList_AST = currentAST.root;
                        currentAST.root = argList_AST = this.astFactory.make(new ASTArray(2).add(this.create(77, "CLOSURE_LIST", first, this.LT(1))).add(argList_AST));
                        currentAST.child = argList_AST != null && argList_AST.getFirstChild() != null ? argList_AST.getFirstChild() : argList_AST;
                        currentAST.advanceChildToEnd();
                        break block0;
                    }
                    case 86: 
                    case 101: 
                    case 123: {
                        block17: while (this.LA(1) == 101) {
                            if (this.inputState.guessing == 0) {
                                lastComma = this.LT(1);
                            }
                            this.match(101);
                            switch (this.LA(1)) {
                                case 38: 
                                case 39: 
                                case 40: 
                                case 41: 
                                case 42: 
                                case 43: 
                                case 81: 
                                case 82: 
                                case 83: 
                                case 84: 
                                case 85: 
                                case 87: 
                                case 88: 
                                case 91: 
                                case 92: 
                                case 93: 
                                case 94: 
                                case 95: 
                                case 96: 
                                case 98: 
                                case 99: 
                                case 104: 
                                case 105: 
                                case 106: 
                                case 107: 
                                case 108: 
                                case 109: 
                                case 110: 
                                case 111: 
                                case 112: 
                                case 113: 
                                case 114: 
                                case 115: 
                                case 116: 
                                case 117: 
                                case 118: 
                                case 119: 
                                case 120: 
                                case 121: 
                                case 122: 
                                case 126: 
                                case 129: 
                                case 130: 
                                case 131: 
                                case 132: 
                                case 137: 
                                case 138: 
                                case 139: 
                                case 140: 
                                case 141: 
                                case 142: 
                                case 143: 
                                case 144: 
                                case 145: 
                                case 146: 
                                case 147: 
                                case 148: 
                                case 149: 
                                case 150: 
                                case 151: 
                                case 152: 
                                case 153: 
                                case 157: 
                                case 158: 
                                case 159: 
                                case 160: 
                                case 161: 
                                case 190: 
                                case 193: 
                                case 195: 
                                case 196: 
                                case 197: 
                                case 199: 
                                case 200: 
                                case 201: 
                                case 202: 
                                case 203: 
                                case 204: {
                                    hls2 = this.argument();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    if (this.inputState.guessing != 0) continue block17;
                                    hls |= hls2;
                                    continue block17;
                                }
                                case 86: 
                                case 101: 
                                case 123: {
                                    if (this.inputState.guessing != 0) continue block17;
                                    if (trailingComma) {
                                        throw new NoViableAltException(lastComma, this.getFilename());
                                    }
                                    trailingComma = true;
                                    continue block17;
                                }
                            }
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        if (this.inputState.guessing != 0) break block0;
                        argList_AST = currentAST.root;
                        currentAST.root = argList_AST = this.astFactory.make(new ASTArray(2).add(this.create(33, "ELIST", first, this.LT(1))).add(argList_AST));
                        currentAST.child = argList_AST != null && argList_AST.getFirstChild() != null ? argList_AST.getFirstChild() : argList_AST;
                        currentAST.advanceChildToEnd();
                        break block0;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            case 86: 
            case 123: {
                if (this.inputState.guessing != 0) break;
                argList_AST = currentAST.root;
                currentAST.root = argList_AST = this.create(33, "ELIST", first, this.LT(1));
                currentAST.child = argList_AST != null && argList_AST.getFirstChild() != null ? argList_AST.getFirstChild() : argList_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.argListHasLabels = (hls & 1) != 0;
        }
        this.returnAST = argList_AST = currentAST.root;
    }

    public final void enumConstantBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumConstantBlock_AST = null;
        Token first = this.LT(1);
        this.match(126);
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 87: 
            case 89: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: {
                this.enumConstantField();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 127: 
            case 128: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        block8: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 84: 
                case 87: 
                case 89: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: {
                    this.enumConstantField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block8;
                }
                case 127: 
                case 128: 
                case 205: {
                    continue block8;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(127);
        if (this.inputState.guessing == 0) {
            enumConstantBlock_AST = currentAST.root;
            currentAST.root = enumConstantBlock_AST = this.astFactory.make(new ASTArray(2).add(this.create(6, "OBJBLOCK", first, this.LT(1))).add(enumConstantBlock_AST));
            currentAST.child = enumConstantBlock_AST != null && enumConstantBlock_AST.getFirstChild() != null ? enumConstantBlock_AST.getFirstChild() : enumConstantBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = enumConstantBlock_AST = currentAST.root;
    }

    public final void enumConstantField() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumConstantField_AST = null;
        AST mods_AST = null;
        AST td_AST = null;
        AST m1_AST = null;
        AST tp1_AST = null;
        AST t1_AST = null;
        AST e1_AST = null;
        AST m2_AST = null;
        AST tp2_AST = null;
        AST t2_AST = null;
        AST e2_AST = null;
        AST cs_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 87: 
            case 89: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: {
                boolean synPredMatched174 = false;
                if (_tokenSet_21.member(this.LA(1)) && _tokenSet_22.member(this.LA(2))) {
                    int _m174 = this.mark();
                    synPredMatched174 = true;
                    ++this.inputState.guessing;
                    try {
                        this.typeDeclarationStart();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched174 = false;
                    }
                    this.rewind(_m174);
                    --this.inputState.guessing;
                }
                if (synPredMatched174) {
                    this.modifiersOpt();
                    mods_AST = this.returnAST;
                    this.typeDefinitionInternal(mods_AST);
                    td_AST = this.returnAST;
                    if (this.inputState.guessing != 0) break;
                    enumConstantField_AST = currentAST.root;
                    currentAST.root = enumConstantField_AST = td_AST;
                    currentAST.child = enumConstantField_AST != null && enumConstantField_AST.getFirstChild() != null ? enumConstantField_AST.getFirstChild() : enumConstantField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                boolean synPredMatched176 = false;
                if (_tokenSet_12.member(this.LA(1)) && _tokenSet_67.member(this.LA(2))) {
                    int _m176 = this.mark();
                    synPredMatched176 = true;
                    ++this.inputState.guessing;
                    try {
                        this.modifiers();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched176 = false;
                    }
                    this.rewind(_m176);
                    --this.inputState.guessing;
                }
                if (synPredMatched176) {
                    this.modifiers();
                    m1_AST = this.returnAST;
                    switch (this.LA(1)) {
                        case 89: {
                            this.typeParameters();
                            tp1_AST = this.returnAST;
                            break;
                        }
                        case 87: 
                        case 88: 
                        case 104: 
                        case 105: 
                        case 106: 
                        case 107: 
                        case 108: 
                        case 109: 
                        case 110: 
                        case 111: 
                        case 112: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    if (_tokenSet_24.member(this.LA(1)) && _tokenSet_25.member(this.LA(2))) {
                        this.typeSpec(false);
                        t1_AST = this.returnAST;
                    } else if (this.LA(1) != 87 && this.LA(1) != 88 || !_tokenSet_61.member(this.LA(2))) {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    this.enumConstantFieldInternal(m1_AST, tp1_AST, t1_AST, first);
                    e1_AST = this.returnAST;
                    if (this.inputState.guessing != 0) break;
                    enumConstantField_AST = currentAST.root;
                    currentAST.root = enumConstantField_AST = e1_AST;
                    currentAST.child = enumConstantField_AST != null && enumConstantField_AST.getFirstChild() != null ? enumConstantField_AST.getFirstChild() : enumConstantField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                if (_tokenSet_68.member(this.LA(1)) && _tokenSet_16.member(this.LA(2))) {
                    this.modifiersOpt();
                    m2_AST = this.returnAST;
                    switch (this.LA(1)) {
                        case 89: {
                            this.typeParameters();
                            tp2_AST = this.returnAST;
                            break;
                        }
                        case 87: 
                        case 104: 
                        case 105: 
                        case 106: 
                        case 107: 
                        case 108: 
                        case 109: 
                        case 110: 
                        case 111: 
                        case 112: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.typeSpec(false);
                    t2_AST = this.returnAST;
                    this.enumConstantFieldInternal(m2_AST, tp2_AST, t2_AST, first);
                    e2_AST = this.returnAST;
                    if (this.inputState.guessing != 0) break;
                    enumConstantField_AST = currentAST.root;
                    currentAST.root = enumConstantField_AST = e2_AST;
                    currentAST.child = enumConstantField_AST != null && enumConstantField_AST.getFirstChild() != null ? enumConstantField_AST.getFirstChild() : enumConstantField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 126: {
                this.compoundStatement();
                cs_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                enumConstantField_AST = currentAST.root;
                currentAST.root = enumConstantField_AST = this.astFactory.make(new ASTArray(2).add(this.create(10, "INSTANCE_INIT", first, this.LT(1))).add(cs_AST));
                currentAST.child = enumConstantField_AST != null && enumConstantField_AST.getFirstChild() != null ? enumConstantField_AST.getFirstChild() : enumConstantField_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = enumConstantField_AST;
    }

    protected final void enumConstantFieldInternal(AST mods, AST tp, AST t, Token first) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumConstantFieldInternal_AST = null;
        AST param_AST = null;
        AST tc_AST = null;
        AST s2_AST = null;
        AST v_AST = null;
        boolean synPredMatched182 = false;
        if (this.LA(1) == 87 && this.LA(2) == 91) {
            int _m182 = this.mark();
            synPredMatched182 = true;
            ++this.inputState.guessing;
            try {
                this.match(87);
                this.match(91);
            }
            catch (RecognitionException pe) {
                synPredMatched182 = false;
            }
            this.rewind(_m182);
            --this.inputState.guessing;
        }
        if (synPredMatched182) {
            AST tmp178_AST = null;
            tmp178_AST = this.astFactory.create(this.LT(1));
            this.match(87);
            this.match(91);
            this.parameterDeclarationList();
            param_AST = this.returnAST;
            this.match(123);
            boolean synPredMatched185 = false;
            if ((this.LA(1) == 130 || this.LA(1) == 205) && _tokenSet_28.member(this.LA(2))) {
                int _m185 = this.mark();
                synPredMatched185 = true;
                ++this.inputState.guessing;
                try {
                    this.nls();
                    this.match(130);
                }
                catch (RecognitionException pe) {
                    synPredMatched185 = false;
                }
                this.rewind(_m185);
                --this.inputState.guessing;
            }
            if (synPredMatched185) {
                this.throwsClause();
                tc_AST = this.returnAST;
            } else if (!_tokenSet_69.member(this.LA(1)) || !_tokenSet_70.member(this.LA(2))) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            switch (this.LA(1)) {
                case 126: {
                    this.compoundStatement();
                    s2_AST = this.returnAST;
                    break;
                }
                case 127: 
                case 128: 
                case 205: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            if (this.inputState.guessing == 0) {
                enumConstantFieldInternal_AST = currentAST.root;
                enumConstantFieldInternal_AST = this.astFactory.make(new ASTArray(7).add(this.create(8, "METHOD_DEF", first, this.LT(1))).add(mods).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t))).add(tmp178_AST).add(param_AST).add(tc_AST).add(s2_AST));
                if (tp != null) {
                    AST old = enumConstantFieldInternal_AST.getFirstChild();
                    enumConstantFieldInternal_AST.setFirstChild(tp);
                    tp.setNextSibling(old);
                }
                currentAST.root = enumConstantFieldInternal_AST;
                currentAST.child = enumConstantFieldInternal_AST != null && enumConstantFieldInternal_AST.getFirstChild() != null ? enumConstantFieldInternal_AST.getFirstChild() : enumConstantFieldInternal_AST;
                currentAST.advanceChildToEnd();
            }
        } else if ((this.LA(1) == 87 || this.LA(1) == 88) && _tokenSet_61.member(this.LA(2))) {
            this.variableDefinitions(mods, t);
            v_AST = this.returnAST;
            if (this.inputState.guessing == 0) {
                enumConstantFieldInternal_AST = currentAST.root;
                currentAST.root = enumConstantFieldInternal_AST = v_AST;
                currentAST.child = enumConstantFieldInternal_AST != null && enumConstantFieldInternal_AST.getFirstChild() != null ? enumConstantFieldInternal_AST.getFirstChild() : enumConstantFieldInternal_AST;
                currentAST.advanceChildToEnd();
            }
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = enumConstantFieldInternal_AST;
    }

    public final void compoundStatement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST compoundStatement_AST = null;
        this.openBlock();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = compoundStatement_AST = currentAST.root;
    }

    public final void parameterDeclarationList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST parameterDeclarationList_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 38: 
            case 84: 
            case 87: 
            case 96: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 133: {
                this.parameterDeclaration();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 101) {
                    this.match(101);
                    this.nls();
                    this.parameterDeclaration();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                break;
            }
            case 123: 
            case 135: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            parameterDeclarationList_AST = currentAST.root;
            currentAST.root = parameterDeclarationList_AST = this.astFactory.make(new ASTArray(2).add(this.create(20, "PARAMETERS", first, this.LT(1))).add(parameterDeclarationList_AST));
            currentAST.child = parameterDeclarationList_AST != null && parameterDeclarationList_AST.getFirstChild() != null ? parameterDeclarationList_AST.getFirstChild() : parameterDeclarationList_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = parameterDeclarationList_AST = currentAST.root;
    }

    public final void throwsClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST throwsClause_AST = null;
        this.nls();
        AST tmp182_AST = null;
        tmp182_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp182_AST);
        this.match(130);
        this.nls();
        this.identifier();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101) {
            this.match(101);
            this.nls();
            this.identifier();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = throwsClause_AST = currentAST.root;
    }

    public final void constructorDefinition(AST mods) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST constructorDefinition_AST = null;
        Token id = null;
        AST id_AST = null;
        AST param_AST = null;
        AST tc_AST = null;
        AST cb_AST = null;
        Token first = this.cloneToken(this.LT(1));
        if (mods != null) {
            first.setLine(mods.getLine());
            first.setColumn(mods.getColumn());
        }
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.astFactory.addASTChild(currentAST, id_AST);
        this.match(87);
        this.match(91);
        this.parameterDeclarationList();
        param_AST = this.returnAST;
        this.match(123);
        boolean synPredMatched248 = false;
        if ((this.LA(1) == 130 || this.LA(1) == 205) && _tokenSet_28.member(this.LA(2))) {
            int _m248 = this.mark();
            synPredMatched248 = true;
            ++this.inputState.guessing;
            try {
                this.nls();
                this.match(130);
            }
            catch (RecognitionException pe) {
                synPredMatched248 = false;
            }
            this.rewind(_m248);
            --this.inputState.guessing;
        }
        if (synPredMatched248) {
            this.throwsClause();
            tc_AST = this.returnAST;
        } else if (this.LA(1) != 126 && this.LA(1) != 205 || !_tokenSet_71.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.nlsWarn();
        if (this.inputState.guessing == 0) {
            this.isConstructorIdent(id);
        }
        this.constructorBody();
        cb_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            constructorDefinition_AST = currentAST.root;
            currentAST.root = constructorDefinition_AST = this.astFactory.make(new ASTArray(5).add(this.create(46, "CTOR_IDENT", first, this.LT(1))).add(mods).add(param_AST).add(tc_AST).add(cb_AST));
            currentAST.child = constructorDefinition_AST != null && constructorDefinition_AST.getFirstChild() != null ? constructorDefinition_AST.getFirstChild() : constructorDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = constructorDefinition_AST = currentAST.root;
    }

    public final void multipleAssignmentDeclarationStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST multipleAssignmentDeclarationStart_AST = null;
        block4: while (true) {
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: {
                    this.modifier();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.nls();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 96: {
                    this.annotation();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.nls();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
            }
            break;
        }
        AST tmp186_AST = null;
        tmp186_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp186_AST);
        this.match(84);
        this.nls();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        AST tmp187_AST = null;
        tmp187_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp187_AST);
        this.match(91);
        this.returnAST = multipleAssignmentDeclarationStart_AST = currentAST.root;
    }

    public final void multipleAssignmentDeclaration() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST multipleAssignmentDeclaration_AST = null;
        AST mods_AST = null;
        AST t_AST = null;
        Token first = this.cloneToken(this.LT(1));
        this.modifiers();
        mods_AST = this.returnAST;
        switch (this.LA(1)) {
            case 87: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.typeSpec(false);
                t_AST = this.returnAST;
                break;
            }
            case 91: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        AST tmp188_AST = null;
        tmp188_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp188_AST);
        this.match(91);
        this.nls();
        this.typeNamePairs(mods_AST, first);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(123);
        AST tmp190_AST = null;
        tmp190_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp190_AST);
        this.match(124);
        this.nls();
        this.assignmentExpression(0);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            multipleAssignmentDeclaration_AST = currentAST.root;
            currentAST.root = multipleAssignmentDeclaration_AST = this.astFactory.make(new ASTArray(4).add(this.create(9, "VARIABLE_DEF", first, this.LT(1))).add(mods_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t_AST))).add(multipleAssignmentDeclaration_AST));
            currentAST.child = multipleAssignmentDeclaration_AST != null && multipleAssignmentDeclaration_AST.getFirstChild() != null ? multipleAssignmentDeclaration_AST.getFirstChild() : multipleAssignmentDeclaration_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = multipleAssignmentDeclaration_AST = currentAST.root;
    }

    public final void constructorBody() throws RecognitionException, TokenStreamException {
        Token first;
        AST bb2_AST;
        AST bb1_AST;
        AST eci_AST;
        AST constructorBody_AST;
        ASTPair currentAST;
        block11: {
            block10: {
                this.returnAST = null;
                currentAST = new ASTPair();
                constructorBody_AST = null;
                eci_AST = null;
                bb1_AST = null;
                bb2_AST = null;
                first = this.LT(1);
                this.match(126);
                this.nls();
                boolean synPredMatched218 = false;
                if (_tokenSet_72.member(this.LA(1)) && _tokenSet_73.member(this.LA(2))) {
                    int _m218 = this.mark();
                    synPredMatched218 = true;
                    ++this.inputState.guessing;
                    try {
                        this.explicitConstructorInvocation();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched218 = false;
                    }
                    this.rewind(_m218);
                    --this.inputState.guessing;
                }
                if (!synPredMatched218) break block10;
                this.explicitConstructorInvocation();
                eci_AST = this.returnAST;
                switch (this.LA(1)) {
                    case 128: 
                    case 205: {
                        this.sep();
                        this.blockBody(this.sepToken);
                        bb1_AST = this.returnAST;
                        break block11;
                    }
                    case 127: {
                        break block11;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            if (_tokenSet_30.member(this.LA(1)) && _tokenSet_74.member(this.LA(2))) {
                this.blockBody(1);
                bb2_AST = this.returnAST;
            } else {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(127);
        if (this.inputState.guessing == 0) {
            constructorBody_AST = currentAST.root;
            constructorBody_AST = eci_AST != null ? this.astFactory.make(new ASTArray(3).add(this.create(7, "{", first, this.LT(1))).add(eci_AST).add(bb1_AST)) : this.astFactory.make(new ASTArray(2).add(this.create(7, "{", first, this.LT(1))).add(bb2_AST));
            currentAST.root = constructorBody_AST;
            currentAST.child = constructorBody_AST != null && constructorBody_AST.getFirstChild() != null ? constructorBody_AST.getFirstChild() : constructorBody_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = constructorBody_AST = currentAST.root;
    }

    public final void explicitConstructorInvocation() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST explicitConstructorInvocation_AST = null;
        Token lp1 = null;
        AST lp1_AST = null;
        Token lp2 = null;
        AST lp2_AST = null;
        switch (this.LA(1)) {
            case 89: {
                this.typeArguments();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 99: 
            case 132: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 132: {
                this.match(132);
                lp1 = this.LT(1);
                lp1_AST = this.astFactory.create(lp1);
                this.astFactory.makeASTRoot(currentAST, lp1_AST);
                this.match(91);
                this.argList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(123);
                if (this.inputState.guessing != 0) break;
                lp1_AST.setType(45);
                break;
            }
            case 99: {
                this.match(99);
                lp2 = this.LT(1);
                lp2_AST = this.astFactory.create(lp2);
                this.astFactory.makeASTRoot(currentAST, lp2_AST);
                this.match(91);
                this.argList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(123);
                if (this.inputState.guessing != 0) break;
                lp2_AST.setType(44);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = explicitConstructorInvocation_AST = currentAST.root;
    }

    public final void listOfVariables(AST mods, AST t, Token first) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST listOfVariables_AST = null;
        this.variableDeclarator(this.getASTFactory().dupTree(mods), this.getASTFactory().dupTree(t), first);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101) {
            this.match(101);
            this.nls();
            if (this.inputState.guessing == 0) {
                first = this.LT(1);
            }
            this.variableDeclarator(this.getASTFactory().dupTree(mods), this.getASTFactory().dupTree(t), first);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = listOfVariables_AST = currentAST.root;
    }

    public final void variableDeclarator(AST mods, AST t, Token first) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST variableDeclarator_AST = null;
        AST id_AST = null;
        AST v_AST = null;
        this.variableName();
        id_AST = this.returnAST;
        switch (this.LA(1)) {
            case 124: {
                this.varInitializer();
                v_AST = this.returnAST;
                break;
            }
            case 1: 
            case 101: 
            case 123: 
            case 127: 
            case 128: 
            case 129: 
            case 138: 
            case 150: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            variableDeclarator_AST = currentAST.root;
            currentAST.root = variableDeclarator_AST = this.astFactory.make(new ASTArray(5).add(this.create(9, "VARIABLE_DEF", first, this.LT(1))).add(mods).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t))).add(id_AST).add(v_AST));
            currentAST.child = variableDeclarator_AST != null && variableDeclarator_AST.getFirstChild() != null ? variableDeclarator_AST.getFirstChild() : variableDeclarator_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = variableDeclarator_AST;
    }

    public final void typeNamePairs(AST mods, Token first) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeNamePairs_AST = null;
        AST t_AST = null;
        AST tn_AST = null;
        if (_tokenSet_24.member(this.LA(1)) && _tokenSet_31.member(this.LA(2))) {
            this.typeSpec(false);
            t_AST = this.returnAST;
        } else if (this.LA(1) != 87 || this.LA(2) != 101 && this.LA(2) != 123) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.singleVariable(this.getASTFactory().dupTree(mods), t_AST);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101) {
            this.match(101);
            this.nls();
            if (this.inputState.guessing == 0) {
                first = this.LT(1);
            }
            if (_tokenSet_24.member(this.LA(1)) && _tokenSet_31.member(this.LA(2))) {
                this.typeSpec(false);
                tn_AST = this.returnAST;
            } else if (this.LA(1) != 87 || this.LA(2) != 101 && this.LA(2) != 123) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.singleVariable(this.getASTFactory().dupTree(mods), tn_AST);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = typeNamePairs_AST = currentAST.root;
    }

    public final void assignmentExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST assignmentExpression_AST = null;
        this.conditionalExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        switch (this.LA(1)) {
            case 124: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 169: 
            case 170: 
            case 171: 
            case 172: 
            case 173: {
                switch (this.LA(1)) {
                    case 124: {
                        AST tmp199_AST = null;
                        tmp199_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp199_AST);
                        this.match(124);
                        break;
                    }
                    case 162: {
                        AST tmp200_AST = null;
                        tmp200_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp200_AST);
                        this.match(162);
                        break;
                    }
                    case 163: {
                        AST tmp201_AST = null;
                        tmp201_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp201_AST);
                        this.match(163);
                        break;
                    }
                    case 164: {
                        AST tmp202_AST = null;
                        tmp202_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp202_AST);
                        this.match(164);
                        break;
                    }
                    case 165: {
                        AST tmp203_AST = null;
                        tmp203_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp203_AST);
                        this.match(165);
                        break;
                    }
                    case 166: {
                        AST tmp204_AST = null;
                        tmp204_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp204_AST);
                        this.match(166);
                        break;
                    }
                    case 167: {
                        AST tmp205_AST = null;
                        tmp205_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp205_AST);
                        this.match(167);
                        break;
                    }
                    case 168: {
                        AST tmp206_AST = null;
                        tmp206_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp206_AST);
                        this.match(168);
                        break;
                    }
                    case 169: {
                        AST tmp207_AST = null;
                        tmp207_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp207_AST);
                        this.match(169);
                        break;
                    }
                    case 170: {
                        AST tmp208_AST = null;
                        tmp208_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp208_AST);
                        this.match(170);
                        break;
                    }
                    case 171: {
                        AST tmp209_AST = null;
                        tmp209_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp209_AST);
                        this.match(171);
                        break;
                    }
                    case 172: {
                        AST tmp210_AST = null;
                        tmp210_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp210_AST);
                        this.match(172);
                        break;
                    }
                    case 173: {
                        AST tmp211_AST = null;
                        tmp211_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp211_AST);
                        this.match(173);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.nls();
                this.expressionStatementNoCheck();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 1: 
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 86: 
            case 87: 
            case 88: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 98: 
            case 99: 
            case 101: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 123: 
            case 126: 
            case 127: 
            case 128: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 135: 
            case 136: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = assignmentExpression_AST = currentAST.root;
    }

    public final void nlsWarn() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object nlsWarn_AST = null;
        boolean synPredMatched594 = false;
        if (_tokenSet_75.member(this.LA(1)) && _tokenSet_1.member(this.LA(2))) {
            int _m594 = this.mark();
            synPredMatched594 = true;
            ++this.inputState.guessing;
            try {
                this.match(205);
            }
            catch (RecognitionException pe) {
                synPredMatched594 = false;
            }
            this.rewind(_m594);
            --this.inputState.guessing;
        }
        if (synPredMatched594) {
            if (this.inputState.guessing == 0) {
                this.addWarning("A newline at this point does not follow the Groovy Coding Conventions.", "Keep this statement on one line, or use curly braces to break across multiple lines.");
            }
        } else if (!_tokenSet_75.member(this.LA(1)) || !_tokenSet_1.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.nls();
        this.returnAST = nlsWarn_AST;
    }

    public final void openBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST openBlock_AST = null;
        AST bb_AST = null;
        Token first = this.LT(1);
        this.match(126);
        this.nls();
        this.blockBody(1);
        bb_AST = this.returnAST;
        this.match(127);
        if (this.inputState.guessing == 0) {
            openBlock_AST = currentAST.root;
            currentAST.root = openBlock_AST = this.astFactory.make(new ASTArray(2).add(this.create(7, "{", first, this.LT(1))).add(bb_AST));
            currentAST.child = openBlock_AST != null && openBlock_AST.getFirstChild() != null ? openBlock_AST.getFirstChild() : openBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = openBlock_AST = currentAST.root;
    }

    public final void variableName() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST variableName_AST = null;
        AST tmp214_AST = null;
        tmp214_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp214_AST);
        this.match(87);
        this.returnAST = variableName_AST = currentAST.root;
    }

    public final void expressionStatementNoCheck() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST expressionStatementNoCheck_AST = null;
        AST head_AST = null;
        AST cmd_AST = null;
        boolean isPathExpr = true;
        this.expression(1);
        head_AST = this.returnAST;
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            boolean bl = isPathExpr = head_AST == this.lastPathExpression;
        }
        if (_tokenSet_76.member(this.LA(1)) && _tokenSet_53.member(this.LA(2)) && this.LA(1) != 138 && isPathExpr) {
            this.commandArgumentsGreedy(head_AST);
            cmd_AST = this.returnAST;
            if (this.inputState.guessing == 0) {
                expressionStatementNoCheck_AST = currentAST.root;
                currentAST.root = expressionStatementNoCheck_AST = cmd_AST;
                currentAST.child = expressionStatementNoCheck_AST != null && expressionStatementNoCheck_AST.getFirstChild() != null ? expressionStatementNoCheck_AST.getFirstChild() : expressionStatementNoCheck_AST;
                currentAST.advanceChildToEnd();
            }
        } else if (!_tokenSet_76.member(this.LA(1)) || !_tokenSet_53.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = expressionStatementNoCheck_AST = currentAST.root;
    }

    public final void parameterDeclaration() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST parameterDeclaration_AST = null;
        AST pm_AST = null;
        AST t_AST = null;
        Token id = null;
        AST id_AST = null;
        AST exp_AST = null;
        Token first = this.LT(1);
        boolean spreadParam = false;
        this.parameterModifiersOpt();
        pm_AST = this.returnAST;
        if (_tokenSet_24.member(this.LA(1)) && _tokenSet_77.member(this.LA(2))) {
            this.typeSpec(false);
            t_AST = this.returnAST;
        } else if (this.LA(1) != 87 && this.LA(1) != 133 || !_tokenSet_78.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        switch (this.LA(1)) {
            case 133: {
                this.match(133);
                if (this.inputState.guessing != 0) break;
                spreadParam = true;
                break;
            }
            case 87: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.match(87);
        switch (this.LA(1)) {
            case 124: {
                this.varInitializer();
                exp_AST = this.returnAST;
                break;
            }
            case 101: 
            case 123: 
            case 135: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            parameterDeclaration_AST = currentAST.root;
            parameterDeclaration_AST = spreadParam ? this.astFactory.make(new ASTArray(5).add(this.create(47, "VARIABLE_PARAMETER_DEF", first, this.LT(1))).add(pm_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t_AST))).add(id_AST).add(exp_AST)) : this.astFactory.make(new ASTArray(5).add(this.create(21, "PARAMETER_DEF", first, this.LT(1))).add(pm_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(t_AST))).add(id_AST).add(exp_AST));
            currentAST.root = parameterDeclaration_AST;
            currentAST.child = parameterDeclaration_AST != null && parameterDeclaration_AST.getFirstChild() != null ? parameterDeclaration_AST.getFirstChild() : parameterDeclaration_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = parameterDeclaration_AST;
    }

    public final void parameterModifiersOpt() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST parameterModifiersOpt_AST = null;
        Token first = this.LT(1);
        int seenDef = 0;
        block4: while (true) {
            switch (this.LA(1)) {
                case 38: {
                    AST tmp216_AST = null;
                    tmp216_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.addASTChild(currentAST, tmp216_AST);
                    this.match(38);
                    this.nls();
                    continue block4;
                }
                case 96: {
                    this.annotation();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.nls();
                    continue block4;
                }
            }
            if (this.LA(1) != 84 || seenDef++ != 0) break;
            this.match(84);
            this.nls();
        }
        if (this.inputState.guessing == 0) {
            parameterModifiersOpt_AST = currentAST.root;
            currentAST.root = parameterModifiersOpt_AST = this.astFactory.make(new ASTArray(2).add(this.create(5, "MODIFIERS", first, this.LT(1))).add(parameterModifiersOpt_AST));
            currentAST.child = parameterModifiersOpt_AST != null && parameterModifiersOpt_AST.getFirstChild() != null ? parameterModifiersOpt_AST.getFirstChild() : parameterModifiersOpt_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = parameterModifiersOpt_AST = currentAST.root;
    }

    public final void multicatch_types() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST multicatch_types_AST = null;
        Token first = this.LT(1);
        this.nls();
        this.classOrInterfaceType(false);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 134) {
            this.match(134);
            this.nls();
            this.classOrInterfaceType(false);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            multicatch_types_AST = currentAST.root;
            currentAST.root = multicatch_types_AST = this.astFactory.make(new ASTArray(2).add(this.create(79, "MULTICATCH_TYPES", first, this.LT(1))).add(multicatch_types_AST));
            currentAST.child = multicatch_types_AST != null && multicatch_types_AST.getFirstChild() != null ? multicatch_types_AST.getFirstChild() : multicatch_types_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = multicatch_types_AST = currentAST.root;
    }

    public final void multicatch() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST multicatch_AST = null;
        AST m_AST = null;
        Token id = null;
        AST id_AST = null;
        Token first = this.LT(1);
        this.nls();
        switch (this.LA(1)) {
            case 38: {
                AST tmp219_AST = null;
                tmp219_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp219_AST);
                this.match(38);
                break;
            }
            case 84: 
            case 87: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 84: {
                AST tmp220_AST = null;
                tmp220_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp220_AST);
                this.match(84);
                break;
            }
            case 87: 
            case 205: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if ((this.LA(1) == 87 || this.LA(1) == 205) && _tokenSet_79.member(this.LA(2))) {
            this.multicatch_types();
            m_AST = this.returnAST;
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) != 87 || this.LA(2) != 123) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.match(87);
        if (this.inputState.guessing == 0) {
            multicatch_AST = currentAST.root;
            currentAST.root = multicatch_AST = this.astFactory.make(new ASTArray(3).add(this.create(78, "MULTICATCH", first, this.LT(1))).add(m_AST).add(id_AST));
            currentAST.child = multicatch_AST != null && multicatch_AST.getFirstChild() != null ? multicatch_AST.getFirstChild() : multicatch_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = multicatch_AST = currentAST.root;
    }

    public final void closableBlockParamsOpt(boolean addImplicit) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST closableBlockParamsOpt_AST = null;
        boolean synPredMatched280 = false;
        if (_tokenSet_80.member(this.LA(1)) && _tokenSet_81.member(this.LA(2))) {
            int _m280 = this.mark();
            synPredMatched280 = true;
            ++this.inputState.guessing;
            try {
                this.parameterDeclarationList();
                this.nls();
                this.match(135);
            }
            catch (RecognitionException pe) {
                synPredMatched280 = false;
            }
            this.rewind(_m280);
            --this.inputState.guessing;
        }
        if (synPredMatched280) {
            this.parameterDeclarationList();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            this.nls();
            this.match(135);
            this.nls();
            closableBlockParamsOpt_AST = currentAST.root;
        } else if (_tokenSet_30.member(this.LA(1)) && _tokenSet_53.member(this.LA(2)) && addImplicit) {
            this.implicitParameters();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            closableBlockParamsOpt_AST = currentAST.root;
        } else if (_tokenSet_30.member(this.LA(1)) && _tokenSet_53.member(this.LA(2))) {
            closableBlockParamsOpt_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = closableBlockParamsOpt_AST;
    }

    public final void implicitParameters() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST implicitParameters_AST = null;
        Token first = this.LT(1);
        if (this.inputState.guessing == 0) {
            implicitParameters_AST = currentAST.root;
            currentAST.root = implicitParameters_AST = this.astFactory.make(new ASTArray(1).add(this.create(51, "IMPLICIT_PARAMETERS", first, this.LT(1))));
            currentAST.child = implicitParameters_AST != null && implicitParameters_AST.getFirstChild() != null ? implicitParameters_AST.getFirstChild() : implicitParameters_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = implicitParameters_AST = currentAST.root;
    }

    public final void closableBlockParamsStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object closableBlockParamsStart_AST = null;
        this.nls();
        this.parameterDeclarationList();
        this.nls();
        AST tmp222_AST = null;
        tmp222_AST = this.astFactory.create(this.LT(1));
        this.match(135);
        this.returnAST = closableBlockParamsStart_AST;
    }

    public final void closableBlockParam() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST closableBlockParam_AST = null;
        Token id = null;
        AST id_AST = null;
        Token first = this.LT(1);
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.match(87);
        if (this.inputState.guessing == 0) {
            closableBlockParam_AST = currentAST.root;
            currentAST.root = closableBlockParam_AST = this.astFactory.make(new ASTArray(4).add(this.create(21, "PARAMETER_DEF", first, this.LT(1))).add(this.astFactory.make(new ASTArray(1).add(this.create(5, "MODIFIERS", first, this.LT(1))))).add(this.astFactory.make(new ASTArray(1).add(this.create(12, "TYPE", first, this.LT(1))))).add(id_AST));
            currentAST.child = closableBlockParam_AST != null && closableBlockParam_AST.getFirstChild() != null ? closableBlockParam_AST.getFirstChild() : closableBlockParam_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = closableBlockParam_AST;
    }

    public final void closableBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST closableBlock_AST = null;
        AST cbp_AST = null;
        AST bb_AST = null;
        Token first = this.LT(1);
        this.match(126);
        this.nls();
        this.closableBlockParamsOpt(true);
        cbp_AST = this.returnAST;
        this.blockBody(1);
        bb_AST = this.returnAST;
        this.match(127);
        if (this.inputState.guessing == 0) {
            closableBlock_AST = currentAST.root;
            currentAST.root = closableBlock_AST = this.astFactory.make(new ASTArray(3).add(this.create(50, "{", first, this.LT(1))).add(cbp_AST).add(bb_AST));
            currentAST.child = closableBlock_AST != null && closableBlock_AST.getFirstChild() != null ? closableBlock_AST.getFirstChild() : closableBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = closableBlock_AST = currentAST.root;
    }

    public final void openOrClosableBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST openOrClosableBlock_AST = null;
        AST cp_AST = null;
        AST bb_AST = null;
        Token first = this.LT(1);
        this.match(126);
        this.nls();
        this.closableBlockParamsOpt(false);
        cp_AST = this.returnAST;
        this.blockBody(1);
        bb_AST = this.returnAST;
        this.match(127);
        if (this.inputState.guessing == 0) {
            openOrClosableBlock_AST = currentAST.root;
            openOrClosableBlock_AST = cp_AST == null ? this.astFactory.make(new ASTArray(2).add(this.create(7, "{", first, this.LT(1))).add(bb_AST)) : this.astFactory.make(new ASTArray(3).add(this.create(50, "{", first, this.LT(1))).add(cp_AST).add(bb_AST));
            currentAST.root = openOrClosableBlock_AST;
            currentAST.child = openOrClosableBlock_AST != null && openOrClosableBlock_AST.getFirstChild() != null ? openOrClosableBlock_AST.getFirstChild() : openOrClosableBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = openOrClosableBlock_AST = currentAST.root;
    }

    public final void statementLabelPrefix() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST statementLabelPrefix_AST = null;
        Token c = null;
        AST c_AST = null;
        AST tmp227_AST = null;
        tmp227_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp227_AST);
        this.match(87);
        c = this.LT(1);
        c_AST = this.astFactory.create(c);
        this.astFactory.makeASTRoot(currentAST, c_AST);
        this.match(136);
        if (this.inputState.guessing == 0) {
            c_AST.setType(22);
        }
        this.nls();
        this.returnAST = statementLabelPrefix_AST = currentAST.root;
    }

    public final void expressionStatement(int prevToken) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST expressionStatement_AST = null;
        AST esn_AST = null;
        Token first = this.LT(1);
        boolean synPredMatched346 = false;
        if (_tokenSet_19.member(this.LA(1)) && _tokenSet_1.member(this.LA(2))) {
            int _m346 = this.mark();
            synPredMatched346 = true;
            ++this.inputState.guessing;
            try {
                this.suspiciousExpressionStatementStart();
            }
            catch (RecognitionException pe) {
                synPredMatched346 = false;
            }
            this.rewind(_m346);
            --this.inputState.guessing;
        }
        if (synPredMatched346) {
            this.checkSuspiciousExpressionStatement(prevToken);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_19.member(this.LA(1)) || !_tokenSet_1.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.expressionStatementNoCheck();
        esn_AST = this.returnAST;
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            expressionStatement_AST = currentAST.root;
            currentAST.root = expressionStatement_AST = this.astFactory.make(new ASTArray(2).add(this.create(28, "EXPR", first, this.LT(1))).add(esn_AST));
            currentAST.child = expressionStatement_AST != null && expressionStatement_AST.getFirstChild() != null ? expressionStatement_AST.getFirstChild() : expressionStatement_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = expressionStatement_AST = currentAST.root;
    }

    public final void assignmentLessExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST assignmentLessExpression_AST = null;
        Token first = this.LT(1);
        this.conditionalExpression(0);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            assignmentLessExpression_AST = currentAST.root;
            currentAST.root = assignmentLessExpression_AST = this.astFactory.make(new ASTArray(2).add(this.create(28, "EXPR", first, this.LT(1))).add(assignmentLessExpression_AST));
            currentAST.child = assignmentLessExpression_AST != null && assignmentLessExpression_AST.getFirstChild() != null ? assignmentLessExpression_AST.getFirstChild() : assignmentLessExpression_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = assignmentLessExpression_AST = currentAST.root;
    }

    public final void compatibleBodyStatement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST compatibleBodyStatement_AST = null;
        AST de_AST = null;
        Token first = this.LT(1);
        boolean synPredMatched332 = false;
        if (this.LA(1) == 126 && _tokenSet_30.member(this.LA(2))) {
            int _m332 = this.mark();
            synPredMatched332 = true;
            ++this.inputState.guessing;
            try {
                this.match(126);
            }
            catch (RecognitionException pe) {
                synPredMatched332 = false;
            }
            this.rewind(_m332);
            --this.inputState.guessing;
        }
        if (synPredMatched332) {
            this.compoundStatement();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            compatibleBodyStatement_AST = currentAST.root;
        } else {
            boolean synPredMatched335 = false;
            if (_tokenSet_15.member(this.LA(1)) && _tokenSet_16.member(this.LA(2))) {
                int _m335 = this.mark();
                synPredMatched335 = true;
                ++this.inputState.guessing;
                try {
                    this.declarationStart();
                    switch (this.LA(1)) {
                        case 124: {
                            this.varInitializer();
                            break;
                        }
                        case 101: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.match(101);
                }
                catch (RecognitionException pe) {
                    synPredMatched335 = false;
                }
                this.rewind(_m335);
                --this.inputState.guessing;
            }
            if (synPredMatched335) {
                this.declaration();
                de_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.inputState.guessing == 0) {
                    compatibleBodyStatement_AST = currentAST.root;
                    currentAST.root = compatibleBodyStatement_AST = this.astFactory.make(new ASTArray(2).add(this.create(7, "CBSLIST", first, this.LT(1))).add(de_AST));
                    currentAST.child = compatibleBodyStatement_AST != null && compatibleBodyStatement_AST.getFirstChild() != null ? compatibleBodyStatement_AST.getFirstChild() : compatibleBodyStatement_AST;
                    currentAST.advanceChildToEnd();
                }
                compatibleBodyStatement_AST = currentAST.root;
            } else if (_tokenSet_18.member(this.LA(1)) && _tokenSet_1.member(this.LA(2))) {
                this.statement(1);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                compatibleBodyStatement_AST = currentAST.root;
            } else {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = compatibleBodyStatement_AST;
    }

    public final void forStatement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forStatement_AST = null;
        AST cl_AST = null;
        AST fic_AST = null;
        Token s = null;
        AST s_AST = null;
        AST forCbs_AST = null;
        Token first = this.LT(1);
        this.match(141);
        this.match(91);
        boolean synPredMatched319 = false;
        if (_tokenSet_82.member(this.LA(1)) && _tokenSet_83.member(this.LA(2))) {
            int _m319 = this.mark();
            synPredMatched319 = true;
            ++this.inputState.guessing;
            try {
                switch (this.LA(1)) {
                    case 128: {
                        this.match(128);
                        break;
                    }
                    case 38: 
                    case 39: 
                    case 43: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 87: 
                    case 88: 
                    case 91: 
                    case 96: 
                    case 99: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 121: 
                    case 122: 
                    case 126: 
                    case 132: 
                    case 143: 
                    case 144: 
                    case 145: 
                    case 146: 
                    case 147: 
                    case 148: 
                    case 149: 
                    case 157: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 190: 
                    case 193: 
                    case 195: 
                    case 196: 
                    case 197: 
                    case 199: 
                    case 200: 
                    case 201: 
                    case 202: 
                    case 203: 
                    case 204: {
                        this.strictContextExpression(true);
                        this.match(128);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException pe) {
                synPredMatched319 = false;
            }
            this.rewind(_m319);
            --this.inputState.guessing;
        }
        if (synPredMatched319) {
            this.closureList();
            cl_AST = this.returnAST;
        } else if (_tokenSet_15.member(this.LA(1)) && _tokenSet_84.member(this.LA(2))) {
            this.forInClause();
            fic_AST = this.returnAST;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(123);
        this.nls();
        switch (this.LA(1)) {
            case 128: {
                s = this.LT(1);
                s_AST = this.astFactory.create(s);
                this.match(128);
                break;
            }
            case 38: 
            case 39: 
            case 43: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: 
            case 132: 
            case 137: 
            case 139: 
            case 140: 
            case 141: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 151: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.compatibleBodyStatement();
                forCbs_AST = this.returnAST;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            forStatement_AST = currentAST.root;
            forStatement_AST = cl_AST != null ? (s_AST != null ? this.astFactory.make(new ASTArray(3).add(this.create(141, "for", first, this.LT(1))).add(cl_AST).add(s_AST)) : this.astFactory.make(new ASTArray(3).add(this.create(141, "for", first, this.LT(1))).add(cl_AST).add(forCbs_AST))) : (s_AST != null ? this.astFactory.make(new ASTArray(3).add(this.create(141, "for", first, this.LT(1))).add(fic_AST).add(s_AST)) : this.astFactory.make(new ASTArray(3).add(this.create(141, "for", first, this.LT(1))).add(fic_AST).add(forCbs_AST)));
            currentAST.root = forStatement_AST;
            currentAST.child = forStatement_AST != null && forStatement_AST.getFirstChild() != null ? forStatement_AST.getFirstChild() : forStatement_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = forStatement_AST = currentAST.root;
    }

    public final boolean strictContextExpression(boolean allowDeclaration) throws RecognitionException, TokenStreamException {
        boolean hasDeclaration = false;
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST strictContextExpression_AST = null;
        Token first = this.LT(1);
        boolean synPredMatched529 = false;
        if (_tokenSet_15.member(this.LA(1)) && _tokenSet_85.member(this.LA(2))) {
            int _m529 = this.mark();
            synPredMatched529 = true;
            ++this.inputState.guessing;
            try {
                if (!allowDeclaration) {
                    throw new SemanticException("allowDeclaration");
                }
                this.declarationStart();
            }
            catch (RecognitionException pe) {
                synPredMatched529 = false;
            }
            this.rewind(_m529);
            --this.inputState.guessing;
        }
        if (synPredMatched529) {
            if (this.inputState.guessing == 0) {
                hasDeclaration = true;
            }
            this.singleDeclaration();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (_tokenSet_19.member(this.LA(1)) && _tokenSet_37.member(this.LA(2))) {
            this.expression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) >= 143 && this.LA(1) <= 147) {
            this.branchStatement();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) == 96 && this.LA(2) == 87) {
            this.annotation();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            strictContextExpression_AST = currentAST.root;
            currentAST.root = strictContextExpression_AST = this.astFactory.make(new ASTArray(2).add(this.create(28, "EXPR", first, this.LT(1))).add(strictContextExpression_AST));
            currentAST.child = strictContextExpression_AST != null && strictContextExpression_AST.getFirstChild() != null ? strictContextExpression_AST.getFirstChild() : strictContextExpression_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = strictContextExpression_AST = currentAST.root;
        return hasDeclaration;
    }

    public final void casesGroup() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST casesGroup_AST = null;
        Token first = this.LT(1);
        int _cnt359 = 0;
        while (true) {
            if (this.LA(1) != 129 && this.LA(1) != 150) {
                if (_cnt359 >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.aCase();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            ++_cnt359;
        }
        this.caseSList();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            casesGroup_AST = currentAST.root;
            currentAST.root = casesGroup_AST = this.astFactory.make(new ASTArray(2).add(this.create(32, "CASE_GROUP", first, this.LT(1))).add(casesGroup_AST));
            currentAST.child = casesGroup_AST != null && casesGroup_AST.getFirstChild() != null ? casesGroup_AST.getFirstChild() : casesGroup_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = casesGroup_AST = currentAST.root;
    }

    public final void tryBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST tryBlock_AST = null;
        AST tryCs_AST = null;
        AST h_AST = null;
        AST fc_AST = null;
        Token first = this.LT(1);
        ArrayList catchNodes = new ArrayList();
        AST newHandler_AST = null;
        this.match(151);
        this.nlsWarn();
        this.compoundStatement();
        tryCs_AST = this.returnAST;
        while (!(this.LA(1) != 153 && this.LA(1) != 205 || this.LA(2) != 91 && this.LA(2) != 153 || this.LA(1) == 205 && this.LA(2) == 91)) {
            this.nls();
            this.handler();
            h_AST = this.returnAST;
            if (this.inputState.guessing != 0) continue;
            newHandler_AST = this.astFactory.make(new ASTArray(3).add(null).add(newHandler_AST).add(h_AST));
        }
        if ((this.LA(1) == 152 || this.LA(1) == 205) && _tokenSet_86.member(this.LA(2))) {
            this.nls();
            this.finallyClause();
            fc_AST = this.returnAST;
        } else if (!_tokenSet_10.member(this.LA(1)) || !_tokenSet_11.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            tryBlock_AST = currentAST.root;
            currentAST.root = tryBlock_AST = this.astFactory.make(new ASTArray(4).add(this.create(151, "try", first, this.LT(1))).add(tryCs_AST).add(newHandler_AST).add(fc_AST));
            currentAST.child = tryBlock_AST != null && tryBlock_AST.getFirstChild() != null ? tryBlock_AST.getFirstChild() : tryBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = tryBlock_AST = currentAST.root;
    }

    public final void branchStatement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST branchStatement_AST = null;
        AST returnE_AST = null;
        Token breakI = null;
        AST breakI_AST = null;
        Token contI = null;
        AST contI_AST = null;
        AST throwE_AST = null;
        AST assertAle_AST = null;
        AST assertE_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 143: {
                this.match(143);
                switch (this.LA(1)) {
                    case 85: 
                    case 87: 
                    case 88: 
                    case 91: 
                    case 99: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 126: 
                    case 132: 
                    case 148: 
                    case 149: 
                    case 157: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 190: 
                    case 193: 
                    case 195: 
                    case 196: 
                    case 197: 
                    case 199: 
                    case 200: 
                    case 201: 
                    case 202: 
                    case 203: 
                    case 204: {
                        this.expression(0);
                        returnE_AST = this.returnAST;
                        break;
                    }
                    case 1: 
                    case 86: 
                    case 101: 
                    case 123: 
                    case 127: 
                    case 128: 
                    case 129: 
                    case 138: 
                    case 150: 
                    case 205: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    branchStatement_AST = currentAST.root;
                    currentAST.root = branchStatement_AST = this.astFactory.make(new ASTArray(2).add(this.create(143, "return", first, this.LT(1))).add(returnE_AST));
                    currentAST.child = branchStatement_AST != null && branchStatement_AST.getFirstChild() != null ? branchStatement_AST.getFirstChild() : branchStatement_AST;
                    currentAST.advanceChildToEnd();
                }
                branchStatement_AST = currentAST.root;
                break;
            }
            case 144: {
                this.match(144);
                switch (this.LA(1)) {
                    case 87: {
                        breakI = this.LT(1);
                        breakI_AST = this.astFactory.create(breakI);
                        this.match(87);
                        break;
                    }
                    case 1: 
                    case 86: 
                    case 101: 
                    case 123: 
                    case 127: 
                    case 128: 
                    case 129: 
                    case 138: 
                    case 150: 
                    case 205: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    branchStatement_AST = currentAST.root;
                    currentAST.root = branchStatement_AST = this.astFactory.make(new ASTArray(2).add(this.create(144, "break", first, this.LT(1))).add(breakI_AST));
                    currentAST.child = branchStatement_AST != null && branchStatement_AST.getFirstChild() != null ? branchStatement_AST.getFirstChild() : branchStatement_AST;
                    currentAST.advanceChildToEnd();
                }
                branchStatement_AST = currentAST.root;
                break;
            }
            case 145: {
                this.match(145);
                switch (this.LA(1)) {
                    case 87: {
                        contI = this.LT(1);
                        contI_AST = this.astFactory.create(contI);
                        this.match(87);
                        break;
                    }
                    case 1: 
                    case 86: 
                    case 101: 
                    case 123: 
                    case 127: 
                    case 128: 
                    case 129: 
                    case 138: 
                    case 150: 
                    case 205: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    branchStatement_AST = currentAST.root;
                    currentAST.root = branchStatement_AST = this.astFactory.make(new ASTArray(2).add(this.create(145, "continue", first, this.LT(1))).add(contI_AST));
                    currentAST.child = branchStatement_AST != null && branchStatement_AST.getFirstChild() != null ? branchStatement_AST.getFirstChild() : branchStatement_AST;
                    currentAST.advanceChildToEnd();
                }
                branchStatement_AST = currentAST.root;
                break;
            }
            case 146: {
                this.match(146);
                this.expression(0);
                throwE_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    branchStatement_AST = currentAST.root;
                    currentAST.root = branchStatement_AST = this.astFactory.make(new ASTArray(2).add(this.create(146, "throw", first, this.LT(1))).add(throwE_AST));
                    currentAST.child = branchStatement_AST != null && branchStatement_AST.getFirstChild() != null ? branchStatement_AST.getFirstChild() : branchStatement_AST;
                    currentAST.advanceChildToEnd();
                }
                branchStatement_AST = currentAST.root;
                break;
            }
            case 147: {
                this.match(147);
                this.assignmentLessExpression();
                assertAle_AST = this.returnAST;
                if ((this.LA(1) == 101 || this.LA(1) == 136) && _tokenSet_87.member(this.LA(2))) {
                    switch (this.LA(1)) {
                        case 101: {
                            this.match(101);
                            this.nls();
                            break;
                        }
                        case 136: {
                            this.match(136);
                            this.nls();
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.expression(0);
                    assertE_AST = this.returnAST;
                } else if (!_tokenSet_88.member(this.LA(1)) || !_tokenSet_11.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                if (this.inputState.guessing == 0) {
                    branchStatement_AST = currentAST.root;
                    currentAST.root = branchStatement_AST = this.astFactory.make(new ASTArray(3).add(this.create(147, "assert", first, this.LT(1))).add(assertAle_AST).add(assertE_AST));
                    currentAST.child = branchStatement_AST != null && branchStatement_AST.getFirstChild() != null ? branchStatement_AST.getFirstChild() : branchStatement_AST;
                    currentAST.advanceChildToEnd();
                }
                branchStatement_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = branchStatement_AST;
    }

    public final void closureList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST closureList_AST = null;
        Token first = this.LT(1);
        boolean sce = false;
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 96: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: 
            case 132: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                sce = this.strictContextExpression(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 128: {
                if (this.inputState.guessing != 0) break;
                this.astFactory.addASTChild(currentAST, this.astFactory.create(37, "EMPTY_STAT"));
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        int _cnt324 = 0;
        while (true) {
            if (this.LA(1) == 128 && _tokenSet_89.member(this.LA(2))) {
                this.match(128);
                sce = this.strictContextExpression(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
            } else if (this.LA(1) == 128 && (this.LA(2) == 123 || this.LA(2) == 128)) {
                this.match(128);
                if (this.inputState.guessing == 0) {
                    this.astFactory.addASTChild(currentAST, this.astFactory.create(37, "EMPTY_STAT"));
                }
            } else {
                if (_cnt324 >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            ++_cnt324;
        }
        if (this.inputState.guessing == 0) {
            closureList_AST = currentAST.root;
            currentAST.root = closureList_AST = this.astFactory.make(new ASTArray(2).add(this.create(77, "CLOSURE_LIST", first, this.LT(1))).add(closureList_AST));
            currentAST.child = closureList_AST != null && closureList_AST.getFirstChild() != null ? closureList_AST.getFirstChild() : closureList_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = closureList_AST = currentAST.root;
    }

    public final void forInClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forInClause_AST = null;
        AST decl_AST = null;
        Token i = null;
        AST i_AST = null;
        Token c = null;
        AST c_AST = null;
        boolean synPredMatched328 = false;
        if (_tokenSet_15.member(this.LA(1)) && _tokenSet_85.member(this.LA(2))) {
            int _m328 = this.mark();
            synPredMatched328 = true;
            ++this.inputState.guessing;
            try {
                this.declarationStart();
            }
            catch (RecognitionException pe) {
                synPredMatched328 = false;
            }
            this.rewind(_m328);
            --this.inputState.guessing;
        }
        if (synPredMatched328) {
            this.singleDeclarationNoInit();
            decl_AST = this.returnAST;
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) == 87 && (this.LA(2) == 136 || this.LA(2) == 142)) {
            AST tmp241_AST = null;
            tmp241_AST = this.astFactory.create(this.LT(1));
            this.astFactory.addASTChild(currentAST, tmp241_AST);
            this.match(87);
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        switch (this.LA(1)) {
            case 142: {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.astFactory.makeASTRoot(currentAST, i_AST);
                this.match(142);
                if (this.inputState.guessing == 0) {
                    i_AST.setType(59);
                }
                this.shiftExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 136: {
                if (this.inputState.guessing == 0) {
                    this.addWarning("A colon at this point is legal Java but not recommended in Groovy.", "Use the 'in' keyword.");
                    this.require(decl_AST != null, "Java-style for-each statement requires a type declaration.", "Use the 'in' keyword, as for (x in y) {...}");
                }
                c = this.LT(1);
                c_AST = this.astFactory.create(c);
                this.astFactory.makeASTRoot(currentAST, c_AST);
                this.match(136);
                if (this.inputState.guessing == 0) {
                    c_AST.setType(59);
                }
                this.expression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = forInClause_AST = currentAST.root;
    }

    public final void shiftExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST shiftExpression_AST = null;
        this.additiveExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (_tokenSet_90.member(this.LA(1))) {
            block0 : switch (this.LA(1)) {
                case 102: 
                case 103: 
                case 187: {
                    switch (this.LA(1)) {
                        case 187: {
                            AST tmp242_AST = null;
                            tmp242_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp242_AST);
                            this.match(187);
                            break block0;
                        }
                        case 102: {
                            AST tmp243_AST = null;
                            tmp243_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp243_AST);
                            this.match(102);
                            break block0;
                        }
                        case 103: {
                            AST tmp244_AST = null;
                            tmp244_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp244_AST);
                            this.match(103);
                            break block0;
                        }
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                case 188: {
                    AST tmp245_AST = null;
                    tmp245_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp245_AST);
                    this.match(188);
                    break;
                }
                case 189: {
                    AST tmp246_AST = null;
                    tmp246_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp246_AST);
                    this.match(189);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.nls();
            this.additiveExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = shiftExpression_AST = currentAST.root;
    }

    public final void expression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST expression_AST = null;
        AST m_AST = null;
        boolean synPredMatched404 = false;
        if (this.LA(1) == 91 && (this.LA(2) == 87 || this.LA(2) == 205)) {
            int _m404 = this.mark();
            synPredMatched404 = true;
            ++this.inputState.guessing;
            try {
                this.match(91);
                this.nls();
                this.match(87);
                while (this.LA(1) == 101) {
                    this.match(101);
                    this.nls();
                    this.match(87);
                }
                this.match(123);
                this.match(124);
            }
            catch (RecognitionException pe) {
                synPredMatched404 = false;
            }
            this.rewind(_m404);
            --this.inputState.guessing;
        }
        if (synPredMatched404) {
            this.multipleAssignment(lc_stmt);
            m_AST = this.returnAST;
            this.astFactory.addASTChild(currentAST, this.returnAST);
            if (this.inputState.guessing == 0) {
                expression_AST = currentAST.root;
                currentAST.root = expression_AST = m_AST;
                currentAST.child = expression_AST != null && expression_AST.getFirstChild() != null ? expression_AST.getFirstChild() : expression_AST;
                currentAST.advanceChildToEnd();
            }
            expression_AST = currentAST.root;
        } else if (_tokenSet_19.member(this.LA(1)) && _tokenSet_37.member(this.LA(2))) {
            this.assignmentExpression(lc_stmt);
            this.astFactory.addASTChild(currentAST, this.returnAST);
            expression_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = expression_AST;
    }

    public final void suspiciousExpressionStatementStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST suspiciousExpressionStatementStart_AST = null;
        block0 : switch (this.LA(1)) {
            case 148: 
            case 149: {
                switch (this.LA(1)) {
                    case 148: {
                        AST tmp247_AST = null;
                        tmp247_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp247_AST);
                        this.match(148);
                        break block0;
                    }
                    case 149: {
                        AST tmp248_AST = null;
                        tmp248_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp248_AST);
                        this.match(149);
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 85: 
            case 91: 
            case 126: {
                switch (this.LA(1)) {
                    case 85: {
                        AST tmp249_AST = null;
                        tmp249_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp249_AST);
                        this.match(85);
                        break block0;
                    }
                    case 91: {
                        AST tmp250_AST = null;
                        tmp250_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp250_AST);
                        this.match(91);
                        break block0;
                    }
                    case 126: {
                        AST tmp251_AST = null;
                        tmp251_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp251_AST);
                        this.match(126);
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = suspiciousExpressionStatementStart_AST = currentAST.root;
    }

    public final void checkSuspiciousExpressionStatement(int prevToken) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST checkSuspiciousExpressionStatement_AST = null;
        boolean synPredMatched351 = false;
        if (_tokenSet_19.member(this.LA(1)) && _tokenSet_1.member(this.LA(2))) {
            int _m351;
            block17: {
                _m351 = this.mark();
                synPredMatched351 = true;
                ++this.inputState.guessing;
                try {
                    if (_tokenSet_91.member(this.LA(1))) {
                        this.matchNot(126);
                        break block17;
                    }
                    if (this.LA(1) == 126) {
                        this.match(126);
                        this.closableBlockParamsStart();
                        break block17;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException pe) {
                    synPredMatched351 = false;
                }
            }
            this.rewind(_m351);
            --this.inputState.guessing;
        }
        if (synPredMatched351) {
            if (_tokenSet_19.member(this.LA(1)) && _tokenSet_1.member(this.LA(2)) && prevToken == 205) {
                if (this.inputState.guessing == 0) {
                    this.addWarning("Expression statement looks like it may continue a previous statement", "Either remove the previous newline, or add an explicit semicolon ';'.");
                }
            } else if (!_tokenSet_19.member(this.LA(1)) || !_tokenSet_1.member(this.LA(2))) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            checkSuspiciousExpressionStatement_AST = currentAST.root;
        } else if (_tokenSet_19.member(this.LA(1)) && _tokenSet_1.member(this.LA(2)) && prevToken == 205) {
            if (this.inputState.guessing == 0) {
                this.require(false, "Ambiguous expression could be a parameterless closure expression, an isolated open code block, or it may continue a previous statement", "Add an explicit parameter list, e.g. {it -> ...}, or force it to be treated as an open block by giving it a label, e.g. L:{...}, and also either remove the previous newline, or add an explicit semicolon ';'");
            }
            checkSuspiciousExpressionStatement_AST = currentAST.root;
        } else if (_tokenSet_19.member(this.LA(1)) && _tokenSet_1.member(this.LA(2)) && prevToken != 205) {
            if (this.inputState.guessing == 0) {
                this.require(false, "Ambiguous expression could be either a parameterless closure expression or an isolated open code block", "Add an explicit closure parameter list, e.g. {it -> ...}, or force it to be treated as an open block by giving it a label, e.g. L:{...}");
            }
            checkSuspiciousExpressionStatement_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = checkSuspiciousExpressionStatement_AST;
    }

    public final void commandArgumentsGreedy(AST head) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST commandArgumentsGreedy_AST = null;
        AST first_AST = null;
        AST pre_AST = null;
        AST pc_AST = null;
        AST ca_AST = null;
        AST prev = head;
        boolean synPredMatched386 = false;
        if (_tokenSet_92.member(this.LA(1)) && _tokenSet_37.member(this.LA(2))) {
            int _m386 = this.mark();
            synPredMatched386 = true;
            ++this.inputState.guessing;
            try {
                if (prev != null && prev.getType() == 27) {
                    throw new SemanticException("prev==null || prev.getType()!=METHOD_CALL");
                }
                this.commandArgument();
            }
            catch (RecognitionException pe) {
                synPredMatched386 = false;
            }
            this.rewind(_m386);
            --this.inputState.guessing;
        }
        if (synPredMatched386) {
            this.commandArguments(head);
            first_AST = this.returnAST;
            if (this.inputState.guessing == 0) {
                prev = first_AST;
            }
        } else if (!_tokenSet_76.member(this.LA(1)) || !_tokenSet_53.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        while (_tokenSet_93.member(this.LA(1)) && _tokenSet_94.member(this.LA(2))) {
            this.primaryExpression();
            pre_AST = this.returnAST;
            if (this.inputState.guessing == 0) {
                prev = this.astFactory.make(new ASTArray(3).add(this.create(90, ".", prev)).add(prev).add(pre_AST));
            }
            boolean synPredMatched392 = false;
            if (_tokenSet_95.member(this.LA(1)) && _tokenSet_96.member(this.LA(2))) {
                int _m392 = this.mark();
                synPredMatched392 = true;
                ++this.inputState.guessing;
                try {
                    this.pathElementStart();
                }
                catch (RecognitionException pe) {
                    synPredMatched392 = false;
                }
                this.rewind(_m392);
                --this.inputState.guessing;
            }
            if (synPredMatched392) {
                this.pathChain(1, prev);
                pc_AST = this.returnAST;
                if (this.inputState.guessing != 0) continue;
                prev = pc_AST;
                continue;
            }
            if (_tokenSet_92.member(this.LA(1)) && _tokenSet_37.member(this.LA(2))) {
                this.commandArguments(prev);
                ca_AST = this.returnAST;
                if (this.inputState.guessing != 0) continue;
                prev = ca_AST;
                continue;
            }
            if (_tokenSet_76.member(this.LA(1)) && _tokenSet_53.member(this.LA(2))) continue;
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            commandArgumentsGreedy_AST = currentAST.root;
            currentAST.root = commandArgumentsGreedy_AST = prev;
            currentAST.child = commandArgumentsGreedy_AST != null && commandArgumentsGreedy_AST.getFirstChild() != null ? commandArgumentsGreedy_AST.getFirstChild() : commandArgumentsGreedy_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = commandArgumentsGreedy_AST = currentAST.root;
    }

    public final void aCase() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST aCase_AST = null;
        switch (this.LA(1)) {
            case 150: {
                AST tmp252_AST = null;
                tmp252_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp252_AST);
                this.match(150);
                this.expression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 129: {
                AST tmp253_AST = null;
                tmp253_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp253_AST);
                this.match(129);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(136);
        this.nls();
        this.returnAST = aCase_AST = currentAST.root;
    }

    public final void caseSList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST caseSList_AST = null;
        Token first = this.LT(1);
        this.statement(136);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        block4: while (this.LA(1) == 128 || this.LA(1) == 205) {
            this.sep();
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 96: 
                case 99: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: 
                case 132: 
                case 137: 
                case 139: 
                case 140: 
                case 141: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 151: 
                case 157: 
                case 159: 
                case 160: 
                case 161: 
                case 190: 
                case 193: 
                case 195: 
                case 196: 
                case 197: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: {
                    this.statement(this.sepToken);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 127: 
                case 128: 
                case 129: 
                case 150: 
                case 205: {
                    continue block4;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            caseSList_AST = currentAST.root;
            currentAST.root = caseSList_AST = this.astFactory.make(new ASTArray(2).add(this.create(7, "SLIST", first, this.LT(1))).add(caseSList_AST));
            currentAST.child = caseSList_AST != null && caseSList_AST.getFirstChild() != null ? caseSList_AST.getFirstChild() : caseSList_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = caseSList_AST = currentAST.root;
    }

    public final void forInit() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forInit_AST = null;
        Token first = this.LT(1);
        boolean synPredMatched368 = false;
        if (_tokenSet_15.member(this.LA(1)) && _tokenSet_16.member(this.LA(2))) {
            int _m368 = this.mark();
            synPredMatched368 = true;
            ++this.inputState.guessing;
            try {
                this.declarationStart();
            }
            catch (RecognitionException pe) {
                synPredMatched368 = false;
            }
            this.rewind(_m368);
            --this.inputState.guessing;
        }
        if (synPredMatched368) {
            this.declaration();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            forInit_AST = currentAST.root;
        } else if (_tokenSet_97.member(this.LA(1)) && _tokenSet_98.member(this.LA(2))) {
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 91: 
                case 96: 
                case 99: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: 
                case 132: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 157: 
                case 159: 
                case 160: 
                case 161: 
                case 190: 
                case 193: 
                case 195: 
                case 196: 
                case 197: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: {
                    this.controlExpressionList();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    break;
                }
                case 1: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            if (this.inputState.guessing == 0) {
                forInit_AST = currentAST.root;
                currentAST.root = forInit_AST = this.astFactory.make(new ASTArray(2).add(this.create(34, "FOR_INIT", first, this.LT(1))).add(forInit_AST));
                currentAST.child = forInit_AST != null && forInit_AST.getFirstChild() != null ? forInit_AST.getFirstChild() : forInit_AST;
                currentAST.advanceChildToEnd();
            }
            forInit_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = forInit_AST;
    }

    public final void controlExpressionList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST controlExpressionList_AST = null;
        Token first = this.LT(1);
        boolean sce = false;
        sce = this.strictContextExpression(false);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101) {
            this.match(101);
            this.nls();
            sce = this.strictContextExpression(false);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            controlExpressionList_AST = currentAST.root;
            currentAST.root = controlExpressionList_AST = this.astFactory.make(new ASTArray(2).add(this.create(33, "ELIST", first, this.LT(1))).add(controlExpressionList_AST));
            currentAST.child = controlExpressionList_AST != null && controlExpressionList_AST.getFirstChild() != null ? controlExpressionList_AST.getFirstChild() : controlExpressionList_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = controlExpressionList_AST = currentAST.root;
    }

    public final void forCond() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forCond_AST = null;
        Token first = this.LT(1);
        boolean sce = false;
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 96: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: 
            case 132: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                sce = this.strictContextExpression(false);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 1: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            forCond_AST = currentAST.root;
            currentAST.root = forCond_AST = this.astFactory.make(new ASTArray(2).add(this.create(35, "FOR_CONDITION", first, this.LT(1))).add(forCond_AST));
            currentAST.child = forCond_AST != null && forCond_AST.getFirstChild() != null ? forCond_AST.getFirstChild() : forCond_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = forCond_AST = currentAST.root;
    }

    public final void forIter() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forIter_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 43: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 96: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: 
            case 132: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 148: 
            case 149: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 190: 
            case 193: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.controlExpressionList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 1: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            forIter_AST = currentAST.root;
            currentAST.root = forIter_AST = this.astFactory.make(new ASTArray(2).add(this.create(36, "FOR_ITERATOR", first, this.LT(1))).add(forIter_AST));
            currentAST.child = forIter_AST != null && forIter_AST.getFirstChild() != null ? forIter_AST.getFirstChild() : forIter_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = forIter_AST = currentAST.root;
    }

    public final void handler() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST handler_AST = null;
        AST pd_AST = null;
        AST handlerCs_AST = null;
        Token first = this.LT(1);
        this.match(153);
        this.match(91);
        this.multicatch();
        pd_AST = this.returnAST;
        this.match(123);
        this.nlsWarn();
        this.compoundStatement();
        handlerCs_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            handler_AST = currentAST.root;
            currentAST.root = handler_AST = this.astFactory.make(new ASTArray(3).add(this.create(153, "catch", first, this.LT(1))).add(pd_AST).add(handlerCs_AST));
            currentAST.child = handler_AST != null && handler_AST.getFirstChild() != null ? handler_AST.getFirstChild() : handler_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = handler_AST = currentAST.root;
    }

    public final void finallyClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST finallyClause_AST = null;
        AST finallyCs_AST = null;
        Token first = this.LT(1);
        this.match(152);
        this.nlsWarn();
        this.compoundStatement();
        finallyCs_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            finallyClause_AST = currentAST.root;
            currentAST.root = finallyClause_AST = this.astFactory.make(new ASTArray(2).add(this.create(152, "finally", first, this.LT(1))).add(finallyCs_AST));
            currentAST.child = finallyClause_AST != null && finallyClause_AST.getFirstChild() != null ? finallyClause_AST.getFirstChild() : finallyClause_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = finallyClause_AST = currentAST.root;
    }

    public final void commandArguments(AST head) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST commandArguments_AST = null;
        Token first = this.LT(1);
        this.commandArgument();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 101 && _tokenSet_99.member(this.LA(2))) {
            this.match(101);
            this.nls();
            this.commandArgument();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            AST headid;
            commandArguments_AST = currentAST.root;
            AST elist = this.astFactory.make(new ASTArray(2).add(this.create(33, "ELIST", first, this.LT(1))).add(commandArguments_AST));
            currentAST.root = commandArguments_AST = (headid = this.astFactory.make(new ASTArray(3).add(this.create(27, "<command>", first, this.LT(1))).add(head).add(elist)));
            currentAST.child = commandArguments_AST != null && commandArguments_AST.getFirstChild() != null ? commandArguments_AST.getFirstChild() : commandArguments_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = commandArguments_AST = currentAST.root;
    }

    public final void commandArgument() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST commandArgument_AST = null;
        Token c = null;
        AST c_AST = null;
        boolean synPredMatched398 = false;
        if (_tokenSet_100.member(this.LA(1)) && _tokenSet_101.member(this.LA(2))) {
            int _m398 = this.mark();
            synPredMatched398 = true;
            ++this.inputState.guessing;
            try {
                this.argumentLabel();
                this.match(136);
                this.nls();
            }
            catch (RecognitionException pe) {
                synPredMatched398 = false;
            }
            this.rewind(_m398);
            --this.inputState.guessing;
        }
        if (synPredMatched398) {
            this.argumentLabel();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            c = this.LT(1);
            c_AST = this.astFactory.create(c);
            this.astFactory.makeASTRoot(currentAST, c_AST);
            this.match(136);
            this.nls();
            this.expression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
            if (this.inputState.guessing == 0) {
                c_AST.setType(54);
            }
            commandArgument_AST = currentAST.root;
        } else if (_tokenSet_19.member(this.LA(1)) && _tokenSet_37.member(this.LA(2))) {
            this.expression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
            commandArgument_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = commandArgument_AST;
    }

    public final void primaryExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST primaryExpression_AST = null;
        AST pe_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 87: {
                AST tmp261_AST = null;
                tmp261_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp261_AST);
                this.match(87);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 88: 
            case 157: 
            case 160: 
            case 161: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.constant();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 159: {
                this.newExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 132: {
                AST tmp262_AST = null;
                tmp262_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp262_AST);
                this.match(132);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 99: {
                AST tmp263_AST = null;
                tmp263_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp263_AST);
                this.match(99);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 91: {
                this.parenthesizedExpression();
                pe_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    primaryExpression_AST = currentAST.root;
                    currentAST.root = primaryExpression_AST = this.astFactory.make(new ASTArray(2).add(this.create(28, "EXPR", first, this.LT(1))).add(pe_AST));
                    currentAST.child = primaryExpression_AST != null && primaryExpression_AST.getFirstChild() != null ? primaryExpression_AST.getFirstChild() : primaryExpression_AST;
                    currentAST.advanceChildToEnd();
                }
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 126: {
                this.closableBlockConstructorExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 85: {
                this.listOrMapConstructorExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 197: {
                this.stringConstructorExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                this.builtInType();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = primaryExpression_AST;
    }

    public final void pathElementStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object pathElementStart_AST = null;
        block0 : switch (this.LA(1)) {
            case 90: 
            case 154: 
            case 155: 
            case 156: 
            case 205: {
                this.nls();
                switch (this.LA(1)) {
                    case 90: {
                        AST tmp264_AST = null;
                        tmp264_AST = this.astFactory.create(this.LT(1));
                        this.match(90);
                        break block0;
                    }
                    case 154: {
                        AST tmp265_AST = null;
                        tmp265_AST = this.astFactory.create(this.LT(1));
                        this.match(154);
                        break block0;
                    }
                    case 155: {
                        AST tmp266_AST = null;
                        tmp266_AST = this.astFactory.create(this.LT(1));
                        this.match(155);
                        break block0;
                    }
                    case 156: {
                        AST tmp267_AST = null;
                        tmp267_AST = this.astFactory.create(this.LT(1));
                        this.match(156);
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 85: {
                AST tmp268_AST = null;
                tmp268_AST = this.astFactory.create(this.LT(1));
                this.match(85);
                break;
            }
            case 91: {
                AST tmp269_AST = null;
                tmp269_AST = this.astFactory.create(this.LT(1));
                this.match(91);
                break;
            }
            case 126: {
                AST tmp270_AST = null;
                tmp270_AST = this.astFactory.create(this.LT(1));
                this.match(126);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = pathElementStart_AST;
    }

    public final void pathChain(int lc_stmt, AST prefix) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST pathChain_AST = null;
        AST pe_AST = null;
        AST apb_AST = null;
        int _cnt415 = 0;
        while (true) {
            boolean synPredMatched412 = false;
            if (_tokenSet_95.member(this.LA(1)) && _tokenSet_96.member(this.LA(2))) {
                int _m412 = this.mark();
                synPredMatched412 = true;
                ++this.inputState.guessing;
                try {
                    this.pathElementStart();
                }
                catch (RecognitionException pe) {
                    synPredMatched412 = false;
                }
                this.rewind(_m412);
                --this.inputState.guessing;
            }
            if (synPredMatched412) {
                this.nls();
                this.pathElement(prefix);
                pe_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    prefix = pe_AST;
                }
            } else {
                boolean synPredMatched414 = false;
                if (!(this.LA(1) != 126 && this.LA(1) != 205 || !_tokenSet_17.member(this.LA(2)) || lc_stmt != 1 && lc_stmt != 2)) {
                    int _m414 = this.mark();
                    synPredMatched414 = true;
                    ++this.inputState.guessing;
                    try {
                        this.nls();
                        this.match(126);
                    }
                    catch (RecognitionException pe) {
                        synPredMatched414 = false;
                    }
                    this.rewind(_m414);
                    --this.inputState.guessing;
                }
                if (synPredMatched414) {
                    this.nlsWarn();
                    this.appendedBlock(prefix);
                    apb_AST = this.returnAST;
                    if (this.inputState.guessing == 0) {
                        prefix = apb_AST;
                    }
                } else {
                    if (_cnt415 >= 1) break;
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            ++_cnt415;
        }
        if (this.inputState.guessing == 0) {
            pathChain_AST = currentAST.root;
            currentAST.root = pathChain_AST = prefix;
            currentAST.child = pathChain_AST != null && pathChain_AST.getFirstChild() != null ? pathChain_AST.getFirstChild() : pathChain_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = pathChain_AST = currentAST.root;
    }

    public final void argumentLabel() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST argumentLabel_AST = null;
        Token id = null;
        AST id_AST = null;
        AST kw_AST = null;
        boolean synPredMatched564 = false;
        if (this.LA(1) == 87 && this.LA(2) == 136) {
            int _m564 = this.mark();
            synPredMatched564 = true;
            ++this.inputState.guessing;
            try {
                this.match(87);
            }
            catch (RecognitionException pe) {
                synPredMatched564 = false;
            }
            this.rewind(_m564);
            --this.inputState.guessing;
        }
        if (synPredMatched564) {
            id = this.LT(1);
            id_AST = this.astFactory.create(id);
            this.astFactory.addASTChild(currentAST, id_AST);
            this.match(87);
            if (this.inputState.guessing == 0) {
                id_AST.setType(88);
            }
            argumentLabel_AST = currentAST.root;
        } else {
            boolean synPredMatched566 = false;
            if (_tokenSet_102.member(this.LA(1)) && this.LA(2) == 136) {
                int _m566 = this.mark();
                synPredMatched566 = true;
                ++this.inputState.guessing;
                try {
                    this.keywordPropertyNames();
                }
                catch (RecognitionException pe) {
                    synPredMatched566 = false;
                }
                this.rewind(_m566);
                --this.inputState.guessing;
            }
            if (synPredMatched566) {
                this.keywordPropertyNames();
                kw_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.inputState.guessing == 0) {
                    kw_AST.setType(88);
                }
                argumentLabel_AST = currentAST.root;
            } else if (_tokenSet_93.member(this.LA(1)) && _tokenSet_101.member(this.LA(2))) {
                this.primaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                argumentLabel_AST = currentAST.root;
            } else {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = argumentLabel_AST;
    }

    public final void multipleAssignment(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST multipleAssignment_AST = null;
        Token first = this.cloneToken(this.LT(1));
        AST tmp271_AST = null;
        tmp271_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp271_AST);
        this.match(91);
        this.nls();
        this.listOfVariables(null, null, first);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(123);
        AST tmp273_AST = null;
        tmp273_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp273_AST);
        this.match(124);
        this.nls();
        this.assignmentExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = multipleAssignment_AST = currentAST.root;
    }

    public final void pathElement(AST prefix) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST pathElement_AST = null;
        AST ta_AST = null;
        AST np_AST = null;
        AST mca_AST = null;
        AST apb_AST = null;
        AST ipa_AST = null;
        Token operator = this.LT(1);
        switch (this.LA(1)) {
            case 90: 
            case 154: 
            case 155: 
            case 156: 
            case 205: {
                if (this.inputState.guessing == 0) {
                    pathElement_AST = currentAST.root;
                    currentAST.root = pathElement_AST = prefix;
                    currentAST.child = pathElement_AST != null && pathElement_AST.getFirstChild() != null ? pathElement_AST.getFirstChild() : pathElement_AST;
                    currentAST.advanceChildToEnd();
                }
                this.nls();
                switch (this.LA(1)) {
                    case 154: {
                        this.match(154);
                        break;
                    }
                    case 155: {
                        this.match(155);
                        break;
                    }
                    case 156: {
                        this.match(156);
                        break;
                    }
                    case 90: {
                        this.match(90);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.nls();
                switch (this.LA(1)) {
                    case 89: {
                        this.typeArguments();
                        ta_AST = this.returnAST;
                        break;
                    }
                    case 38: 
                    case 39: 
                    case 40: 
                    case 41: 
                    case 42: 
                    case 43: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 87: 
                    case 88: 
                    case 91: 
                    case 92: 
                    case 93: 
                    case 94: 
                    case 95: 
                    case 96: 
                    case 98: 
                    case 99: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 114: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 121: 
                    case 122: 
                    case 126: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 137: 
                    case 138: 
                    case 139: 
                    case 140: 
                    case 141: 
                    case 142: 
                    case 143: 
                    case 144: 
                    case 145: 
                    case 146: 
                    case 147: 
                    case 150: 
                    case 151: 
                    case 152: 
                    case 153: 
                    case 157: 
                    case 158: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 197: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.namePart();
                np_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    pathElement_AST = currentAST.root;
                    currentAST.root = pathElement_AST = this.astFactory.make(new ASTArray(4).add(this.create(operator.getType(), operator.getText(), prefix, this.LT(1))).add(prefix).add(ta_AST).add(np_AST));
                    currentAST.child = pathElement_AST != null && pathElement_AST.getFirstChild() != null ? pathElement_AST.getFirstChild() : pathElement_AST;
                    currentAST.advanceChildToEnd();
                }
                pathElement_AST = currentAST.root;
                break;
            }
            case 91: {
                this.methodCallArgs(prefix);
                mca_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    pathElement_AST = currentAST.root;
                    currentAST.root = pathElement_AST = mca_AST;
                    currentAST.child = pathElement_AST != null && pathElement_AST.getFirstChild() != null ? pathElement_AST.getFirstChild() : pathElement_AST;
                    currentAST.advanceChildToEnd();
                }
                pathElement_AST = currentAST.root;
                break;
            }
            case 126: {
                this.appendedBlock(prefix);
                apb_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    pathElement_AST = currentAST.root;
                    currentAST.root = pathElement_AST = apb_AST;
                    currentAST.child = pathElement_AST != null && pathElement_AST.getFirstChild() != null ? pathElement_AST.getFirstChild() : pathElement_AST;
                    currentAST.advanceChildToEnd();
                }
                pathElement_AST = currentAST.root;
                break;
            }
            case 85: {
                this.indexPropertyArgs(prefix);
                ipa_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    pathElement_AST = currentAST.root;
                    currentAST.root = pathElement_AST = ipa_AST;
                    currentAST.child = pathElement_AST != null && pathElement_AST.getFirstChild() != null ? pathElement_AST.getFirstChild() : pathElement_AST;
                    currentAST.advanceChildToEnd();
                }
                pathElement_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = pathElement_AST;
    }

    public final void appendedBlock(AST callee) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST appendedBlock_AST = null;
        AST cb_AST = null;
        this.closableBlock();
        cb_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            appendedBlock_AST = currentAST.root;
            appendedBlock_AST = callee != null && callee.getType() == 27 ? this.astFactory.make(new ASTArray(3).add(this.create(27, "(", callee, this.LT(1))).add(callee.getFirstChild()).add(cb_AST)) : this.astFactory.make(new ASTArray(3).add(this.create(27, "{", callee, this.LT(1))).add(callee).add(cb_AST));
            currentAST.root = appendedBlock_AST;
            currentAST.child = appendedBlock_AST != null && appendedBlock_AST.getFirstChild() != null ? appendedBlock_AST.getFirstChild() : appendedBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = appendedBlock_AST = currentAST.root;
    }

    public final void pathExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST pathExpression_AST = null;
        AST pre_AST = null;
        AST pe_AST = null;
        AST apb_AST = null;
        AST prefix = null;
        this.primaryExpression();
        pre_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            prefix = pre_AST;
        }
        while (true) {
            boolean synPredMatched419 = false;
            if (_tokenSet_95.member(this.LA(1)) && _tokenSet_96.member(this.LA(2))) {
                int _m419 = this.mark();
                synPredMatched419 = true;
                ++this.inputState.guessing;
                try {
                    this.pathElementStart();
                }
                catch (RecognitionException pe) {
                    synPredMatched419 = false;
                }
                this.rewind(_m419);
                --this.inputState.guessing;
            }
            if (synPredMatched419) {
                this.nls();
                this.pathElement(prefix);
                pe_AST = this.returnAST;
                if (this.inputState.guessing != 0) continue;
                prefix = pe_AST;
                continue;
            }
            boolean synPredMatched421 = false;
            if (!(this.LA(1) != 126 && this.LA(1) != 205 || !_tokenSet_17.member(this.LA(2)) || lc_stmt != 1 && lc_stmt != 2)) {
                int _m421 = this.mark();
                synPredMatched421 = true;
                ++this.inputState.guessing;
                try {
                    this.nls();
                    this.match(126);
                }
                catch (RecognitionException pe) {
                    synPredMatched421 = false;
                }
                this.rewind(_m421);
                --this.inputState.guessing;
            }
            if (!synPredMatched421) break;
            this.nlsWarn();
            this.appendedBlock(prefix);
            apb_AST = this.returnAST;
            if (this.inputState.guessing != 0) continue;
            prefix = apb_AST;
        }
        if (this.inputState.guessing == 0) {
            pathExpression_AST = currentAST.root;
            this.lastPathExpression = pathExpression_AST = prefix;
            currentAST.root = pathExpression_AST;
            currentAST.child = pathExpression_AST != null && pathExpression_AST.getFirstChild() != null ? pathExpression_AST.getFirstChild() : pathExpression_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = pathExpression_AST = currentAST.root;
    }

    public final void namePart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST namePart_AST = null;
        Token ats = null;
        AST ats_AST = null;
        Token sl = null;
        AST sl_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 96: {
                ats = this.LT(1);
                ats_AST = this.astFactory.create(ats);
                this.astFactory.makeASTRoot(currentAST, ats_AST);
                this.match(96);
                if (this.inputState.guessing != 0) break;
                ats_AST.setType(52);
                break;
            }
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 87: 
            case 88: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 98: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 126: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 197: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 87: {
                AST tmp278_AST = null;
                tmp278_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp278_AST);
                this.match(87);
                break;
            }
            case 88: {
                sl = this.LT(1);
                sl_AST = this.astFactory.create(sl);
                this.astFactory.addASTChild(currentAST, sl_AST);
                this.match(88);
                if (this.inputState.guessing != 0) break;
                sl_AST.setType(87);
                break;
            }
            case 91: 
            case 197: {
                this.dynamicMemberName();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 126: {
                this.openBlock();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 98: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: {
                this.keywordPropertyNames();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = namePart_AST = currentAST.root;
    }

    public final void methodCallArgs(AST callee) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST methodCallArgs_AST = null;
        AST al_AST = null;
        this.match(91);
        this.argList();
        al_AST = this.returnAST;
        this.match(123);
        if (this.inputState.guessing == 0) {
            methodCallArgs_AST = currentAST.root;
            methodCallArgs_AST = callee != null && callee.getFirstChild() != null ? this.astFactory.make(new ASTArray(3).add(this.create(27, "(", callee.getFirstChild(), this.LT(1))).add(callee).add(al_AST)) : this.astFactory.make(new ASTArray(3).add(this.create(27, "(", callee, this.LT(1))).add(callee).add(al_AST));
            currentAST.root = methodCallArgs_AST;
            currentAST.child = methodCallArgs_AST != null && methodCallArgs_AST.getFirstChild() != null ? methodCallArgs_AST.getFirstChild() : methodCallArgs_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = methodCallArgs_AST = currentAST.root;
    }

    public final void indexPropertyArgs(AST indexee) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST indexPropertyArgs_AST = null;
        Token lb = null;
        AST lb_AST = null;
        AST al_AST = null;
        lb = this.LT(1);
        lb_AST = this.astFactory.create(lb);
        this.astFactory.addASTChild(currentAST, lb_AST);
        this.match(85);
        this.argList();
        al_AST = this.returnAST;
        this.match(86);
        if (this.inputState.guessing == 0) {
            indexPropertyArgs_AST = currentAST.root;
            indexPropertyArgs_AST = indexee != null && indexee.getFirstChild() != null ? this.astFactory.make(new ASTArray(4).add(this.create(24, "INDEX_OP", indexee.getFirstChild(), this.LT(1))).add(lb_AST).add(indexee).add(al_AST)) : this.astFactory.make(new ASTArray(4).add(this.create(24, "INDEX_OP", indexee, this.LT(1))).add(lb_AST).add(indexee).add(al_AST));
            currentAST.root = indexPropertyArgs_AST;
            currentAST.child = indexPropertyArgs_AST != null && indexPropertyArgs_AST.getFirstChild() != null ? indexPropertyArgs_AST.getFirstChild() : indexPropertyArgs_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = indexPropertyArgs_AST = currentAST.root;
    }

    public final void dynamicMemberName() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST dynamicMemberName_AST = null;
        AST pe_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 91: {
                this.parenthesizedExpression();
                pe_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                dynamicMemberName_AST = currentAST.root;
                currentAST.root = dynamicMemberName_AST = this.astFactory.make(new ASTArray(2).add(this.create(28, "EXPR", first, this.LT(1))).add(pe_AST));
                currentAST.child = dynamicMemberName_AST != null && dynamicMemberName_AST.getFirstChild() != null ? dynamicMemberName_AST.getFirstChild() : dynamicMemberName_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            case 197: {
                this.stringConstructorExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            dynamicMemberName_AST = currentAST.root;
            currentAST.root = dynamicMemberName_AST = this.astFactory.make(new ASTArray(2).add(this.create(53, "DYNAMIC_MEMBER", first, this.LT(1))).add(dynamicMemberName_AST));
            currentAST.child = dynamicMemberName_AST != null && dynamicMemberName_AST.getFirstChild() != null ? dynamicMemberName_AST.getFirstChild() : dynamicMemberName_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = dynamicMemberName_AST = currentAST.root;
    }

    public final void parenthesizedExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST parenthesizedExpression_AST = null;
        Token first = this.LT(1);
        Token declaration = null;
        boolean hasClosureList = false;
        boolean firstContainsDeclaration = false;
        boolean sce = false;
        this.match(91);
        if (this.inputState.guessing == 0) {
            declaration = this.LT(1);
        }
        firstContainsDeclaration = this.strictContextExpression(true);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        block4: while (this.LA(1) == 128) {
            this.match(128);
            if (this.inputState.guessing == 0) {
                hasClosureList = true;
            }
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 43: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 91: 
                case 96: 
                case 99: 
                case 104: 
                case 105: 
                case 106: 
                case 107: 
                case 108: 
                case 109: 
                case 110: 
                case 111: 
                case 112: 
                case 115: 
                case 116: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 121: 
                case 122: 
                case 126: 
                case 132: 
                case 143: 
                case 144: 
                case 145: 
                case 146: 
                case 147: 
                case 148: 
                case 149: 
                case 157: 
                case 159: 
                case 160: 
                case 161: 
                case 190: 
                case 193: 
                case 195: 
                case 196: 
                case 197: 
                case 199: 
                case 200: 
                case 201: 
                case 202: 
                case 203: 
                case 204: {
                    sce = this.strictContextExpression(true);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 123: 
                case 128: {
                    if (this.inputState.guessing != 0) continue block4;
                    this.astFactory.addASTChild(currentAST, this.astFactory.create(37, "EMPTY_STAT"));
                    continue block4;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0 && firstContainsDeclaration && !hasClosureList) {
            throw new NoViableAltException(declaration, this.getFilename());
        }
        this.match(123);
        if (this.inputState.guessing == 0) {
            parenthesizedExpression_AST = currentAST.root;
            if (hasClosureList) {
                parenthesizedExpression_AST = this.astFactory.make(new ASTArray(2).add(this.create(77, "CLOSURE_LIST", first, this.LT(1))).add(parenthesizedExpression_AST));
            }
            currentAST.root = parenthesizedExpression_AST;
            currentAST.child = parenthesizedExpression_AST != null && parenthesizedExpression_AST.getFirstChild() != null ? parenthesizedExpression_AST.getFirstChild() : parenthesizedExpression_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = parenthesizedExpression_AST = currentAST.root;
    }

    public final void stringConstructorExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST stringConstructorExpression_AST = null;
        Token cs = null;
        AST cs_AST = null;
        Token cm = null;
        AST cm_AST = null;
        Token ce = null;
        AST ce_AST = null;
        Token first = this.LT(1);
        cs = this.LT(1);
        cs_AST = this.astFactory.create(cs);
        this.astFactory.addASTChild(currentAST, cs_AST);
        this.match(197);
        if (this.inputState.guessing == 0) {
            cs_AST.setType(88);
        }
        this.stringConstructorValuePart();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 49) {
            cm = this.LT(1);
            cm_AST = this.astFactory.create(cm);
            this.astFactory.addASTChild(currentAST, cm_AST);
            this.match(49);
            if (this.inputState.guessing == 0) {
                cm_AST.setType(88);
            }
            this.stringConstructorValuePart();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        ce = this.LT(1);
        ce_AST = this.astFactory.create(ce);
        this.astFactory.addASTChild(currentAST, ce_AST);
        this.match(198);
        if (this.inputState.guessing == 0) {
            stringConstructorExpression_AST = currentAST.root;
            ce_AST.setType(88);
            currentAST.root = stringConstructorExpression_AST = this.astFactory.make(new ASTArray(2).add(this.create(48, "STRING_CONSTRUCTOR", first, this.LT(1))).add(stringConstructorExpression_AST));
            currentAST.child = stringConstructorExpression_AST != null && stringConstructorExpression_AST.getFirstChild() != null ? stringConstructorExpression_AST.getFirstChild() : stringConstructorExpression_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = stringConstructorExpression_AST = currentAST.root;
    }

    public final void logicalOrExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST logicalOrExpression_AST = null;
        this.logicalAndExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 175) {
            AST tmp285_AST = null;
            tmp285_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp285_AST);
            this.match(175);
            this.nls();
            this.logicalAndExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = logicalOrExpression_AST = currentAST.root;
    }

    public final void logicalAndExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST logicalAndExpression_AST = null;
        this.inclusiveOrExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 176) {
            AST tmp286_AST = null;
            tmp286_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp286_AST);
            this.match(176);
            this.nls();
            this.inclusiveOrExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = logicalAndExpression_AST = currentAST.root;
    }

    public final void inclusiveOrExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST inclusiveOrExpression_AST = null;
        this.exclusiveOrExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 134) {
            AST tmp287_AST = null;
            tmp287_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp287_AST);
            this.match(134);
            this.nls();
            this.exclusiveOrExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = inclusiveOrExpression_AST = currentAST.root;
    }

    public final void exclusiveOrExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST exclusiveOrExpression_AST = null;
        this.andExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 177) {
            AST tmp288_AST = null;
            tmp288_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp288_AST);
            this.match(177);
            this.nls();
            this.andExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = exclusiveOrExpression_AST = currentAST.root;
    }

    public final void andExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST andExpression_AST = null;
        this.regexExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 125) {
            AST tmp289_AST = null;
            tmp289_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp289_AST);
            this.match(125);
            this.nls();
            this.regexExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = andExpression_AST = currentAST.root;
    }

    public final void regexExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST regexExpression_AST = null;
        this.equalityExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 178 || this.LA(1) == 179) {
            switch (this.LA(1)) {
                case 178: {
                    AST tmp290_AST = null;
                    tmp290_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp290_AST);
                    this.match(178);
                    break;
                }
                case 179: {
                    AST tmp291_AST = null;
                    tmp291_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp291_AST);
                    this.match(179);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.nls();
            this.equalityExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = regexExpression_AST = currentAST.root;
    }

    public final void equalityExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST equalityExpression_AST = null;
        this.relationalExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) >= 180 && this.LA(1) <= 184) {
            switch (this.LA(1)) {
                case 180: {
                    AST tmp292_AST = null;
                    tmp292_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp292_AST);
                    this.match(180);
                    break;
                }
                case 181: {
                    AST tmp293_AST = null;
                    tmp293_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp293_AST);
                    this.match(181);
                    break;
                }
                case 182: {
                    AST tmp294_AST = null;
                    tmp294_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp294_AST);
                    this.match(182);
                    break;
                }
                case 183: {
                    AST tmp295_AST = null;
                    tmp295_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp295_AST);
                    this.match(183);
                    break;
                }
                case 184: {
                    AST tmp296_AST = null;
                    tmp296_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp296_AST);
                    this.match(184);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.nls();
            this.relationalExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = equalityExpression_AST = currentAST.root;
    }

    public final void relationalExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST relationalExpression_AST = null;
        this.shiftExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (_tokenSet_103.member(this.LA(1)) && _tokenSet_87.member(this.LA(2))) {
            switch (this.LA(1)) {
                case 89: {
                    AST tmp297_AST = null;
                    tmp297_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp297_AST);
                    this.match(89);
                    break;
                }
                case 100: {
                    AST tmp298_AST = null;
                    tmp298_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp298_AST);
                    this.match(100);
                    break;
                }
                case 185: {
                    AST tmp299_AST = null;
                    tmp299_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp299_AST);
                    this.match(185);
                    break;
                }
                case 186: {
                    AST tmp300_AST = null;
                    tmp300_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp300_AST);
                    this.match(186);
                    break;
                }
                case 142: {
                    AST tmp301_AST = null;
                    tmp301_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp301_AST);
                    this.match(142);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.nls();
            this.shiftExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) == 158 && _tokenSet_104.member(this.LA(2))) {
            AST tmp302_AST = null;
            tmp302_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp302_AST);
            this.match(158);
            this.nls();
            this.typeSpec(true);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) == 114 && _tokenSet_104.member(this.LA(2))) {
            AST tmp303_AST = null;
            tmp303_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp303_AST);
            this.match(114);
            this.nls();
            this.typeSpec(true);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_105.member(this.LA(1)) || !_tokenSet_53.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = relationalExpression_AST = currentAST.root;
    }

    public final void additiveExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST additiveExpression_AST = null;
        this.multiplicativeExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while ((this.LA(1) == 148 || this.LA(1) == 149) && _tokenSet_87.member(this.LA(2))) {
            switch (this.LA(1)) {
                case 148: {
                    AST tmp304_AST = null;
                    tmp304_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp304_AST);
                    this.match(148);
                    break;
                }
                case 149: {
                    AST tmp305_AST = null;
                    tmp305_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp305_AST);
                    this.match(149);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.nls();
            this.multiplicativeExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = additiveExpression_AST = currentAST.root;
    }

    public final void multiplicativeExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST multiplicativeExpression_AST = null;
        switch (this.LA(1)) {
            case 190: {
                AST tmp306_AST = null;
                tmp306_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp306_AST);
                this.match(190);
                this.nls();
                this.powerExpressionNotPlusMinus(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (_tokenSet_106.member(this.LA(1))) {
                    switch (this.LA(1)) {
                        case 113: {
                            AST tmp307_AST = null;
                            tmp307_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp307_AST);
                            this.match(113);
                            break;
                        }
                        case 191: {
                            AST tmp308_AST = null;
                            tmp308_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp308_AST);
                            this.match(191);
                            break;
                        }
                        case 192: {
                            AST tmp309_AST = null;
                            tmp309_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp309_AST);
                            this.match(192);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.nls();
                    this.powerExpression(0);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                multiplicativeExpression_AST = currentAST.root;
                break;
            }
            case 193: {
                AST tmp310_AST = null;
                tmp310_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp310_AST);
                this.match(193);
                this.nls();
                this.powerExpressionNotPlusMinus(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (_tokenSet_106.member(this.LA(1))) {
                    switch (this.LA(1)) {
                        case 113: {
                            AST tmp311_AST = null;
                            tmp311_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp311_AST);
                            this.match(113);
                            break;
                        }
                        case 191: {
                            AST tmp312_AST = null;
                            tmp312_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp312_AST);
                            this.match(191);
                            break;
                        }
                        case 192: {
                            AST tmp313_AST = null;
                            tmp313_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp313_AST);
                            this.match(192);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.nls();
                    this.powerExpression(0);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                multiplicativeExpression_AST = currentAST.root;
                break;
            }
            case 149: {
                AST tmp314_AST = null;
                tmp314_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp314_AST);
                this.match(149);
                if (this.inputState.guessing == 0) {
                    tmp314_AST.setType(30);
                }
                this.nls();
                this.powerExpressionNotPlusMinus(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (_tokenSet_106.member(this.LA(1))) {
                    switch (this.LA(1)) {
                        case 113: {
                            AST tmp315_AST = null;
                            tmp315_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp315_AST);
                            this.match(113);
                            break;
                        }
                        case 191: {
                            AST tmp316_AST = null;
                            tmp316_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp316_AST);
                            this.match(191);
                            break;
                        }
                        case 192: {
                            AST tmp317_AST = null;
                            tmp317_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp317_AST);
                            this.match(192);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.nls();
                    this.powerExpression(0);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                multiplicativeExpression_AST = currentAST.root;
                break;
            }
            case 148: {
                AST tmp318_AST = null;
                tmp318_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp318_AST);
                this.match(148);
                if (this.inputState.guessing == 0) {
                    tmp318_AST.setType(31);
                }
                this.nls();
                this.powerExpressionNotPlusMinus(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (_tokenSet_106.member(this.LA(1))) {
                    switch (this.LA(1)) {
                        case 113: {
                            AST tmp319_AST = null;
                            tmp319_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp319_AST);
                            this.match(113);
                            break;
                        }
                        case 191: {
                            AST tmp320_AST = null;
                            tmp320_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp320_AST);
                            this.match(191);
                            break;
                        }
                        case 192: {
                            AST tmp321_AST = null;
                            tmp321_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp321_AST);
                            this.match(192);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.nls();
                    this.powerExpression(0);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                multiplicativeExpression_AST = currentAST.root;
                break;
            }
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 126: 
            case 132: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.powerExpressionNotPlusMinus(lc_stmt);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (_tokenSet_106.member(this.LA(1))) {
                    switch (this.LA(1)) {
                        case 113: {
                            AST tmp322_AST = null;
                            tmp322_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp322_AST);
                            this.match(113);
                            break;
                        }
                        case 191: {
                            AST tmp323_AST = null;
                            tmp323_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp323_AST);
                            this.match(191);
                            break;
                        }
                        case 192: {
                            AST tmp324_AST = null;
                            tmp324_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp324_AST);
                            this.match(192);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.nls();
                    this.powerExpression(0);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                multiplicativeExpression_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = multiplicativeExpression_AST;
    }

    public final void powerExpressionNotPlusMinus(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST powerExpressionNotPlusMinus_AST = null;
        this.unaryExpressionNotPlusMinus(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 194) {
            AST tmp325_AST = null;
            tmp325_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp325_AST);
            this.match(194);
            this.nls();
            this.unaryExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = powerExpressionNotPlusMinus_AST = currentAST.root;
    }

    public final void powerExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST powerExpression_AST = null;
        this.unaryExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 194) {
            AST tmp326_AST = null;
            tmp326_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp326_AST);
            this.match(194);
            this.nls();
            this.unaryExpression(0);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = powerExpression_AST = currentAST.root;
    }

    public final void unaryExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST unaryExpression_AST = null;
        switch (this.LA(1)) {
            case 190: {
                AST tmp327_AST = null;
                tmp327_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp327_AST);
                this.match(190);
                this.nls();
                this.unaryExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 193: {
                AST tmp328_AST = null;
                tmp328_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp328_AST);
                this.match(193);
                this.nls();
                this.unaryExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 149: {
                AST tmp329_AST = null;
                tmp329_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp329_AST);
                this.match(149);
                if (this.inputState.guessing == 0) {
                    tmp329_AST.setType(30);
                }
                this.nls();
                this.unaryExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 148: {
                AST tmp330_AST = null;
                tmp330_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp330_AST);
                this.match(148);
                if (this.inputState.guessing == 0) {
                    tmp330_AST.setType(31);
                }
                this.nls();
                this.unaryExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 126: 
            case 132: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 195: 
            case 196: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.unaryExpressionNotPlusMinus(lc_stmt);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = unaryExpression_AST;
    }

    public final void unaryExpressionNotPlusMinus(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST unaryExpressionNotPlusMinus_AST = null;
        Token lpb = null;
        AST lpb_AST = null;
        Token lp = null;
        AST lp_AST = null;
        switch (this.LA(1)) {
            case 195: {
                AST tmp331_AST = null;
                tmp331_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp331_AST);
                this.match(195);
                this.nls();
                this.unaryExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpressionNotPlusMinus_AST = currentAST.root;
                break;
            }
            case 196: {
                AST tmp332_AST = null;
                tmp332_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp332_AST);
                this.match(196);
                this.nls();
                this.unaryExpression(0);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpressionNotPlusMinus_AST = currentAST.root;
                break;
            }
            case 85: 
            case 87: 
            case 88: 
            case 91: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 126: 
            case 132: 
            case 157: 
            case 159: 
            case 160: 
            case 161: 
            case 197: 
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                boolean synPredMatched516 = false;
                if (this.LA(1) == 91 && this.LA(2) >= 104 && this.LA(2) <= 112) {
                    int _m516 = this.mark();
                    synPredMatched516 = true;
                    ++this.inputState.guessing;
                    try {
                        this.match(91);
                        this.builtInTypeSpec(true);
                        this.match(123);
                        this.unaryExpression(0);
                    }
                    catch (RecognitionException pe) {
                        synPredMatched516 = false;
                    }
                    this.rewind(_m516);
                    --this.inputState.guessing;
                }
                if (synPredMatched516) {
                    lpb = this.LT(1);
                    lpb_AST = this.astFactory.create(lpb);
                    this.astFactory.makeASTRoot(currentAST, lpb_AST);
                    this.match(91);
                    if (this.inputState.guessing == 0) {
                        lpb_AST.setType(23);
                    }
                    this.builtInTypeSpec(true);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.match(123);
                    this.unaryExpression(0);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                } else {
                    boolean synPredMatched518 = false;
                    if (this.LA(1) == 91 && this.LA(2) == 87) {
                        int _m518 = this.mark();
                        synPredMatched518 = true;
                        ++this.inputState.guessing;
                        try {
                            this.match(91);
                            this.classTypeSpec(true);
                            this.match(123);
                            this.unaryExpressionNotPlusMinus(0);
                        }
                        catch (RecognitionException pe) {
                            synPredMatched518 = false;
                        }
                        this.rewind(_m518);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched518) {
                        lp = this.LT(1);
                        lp_AST = this.astFactory.create(lp);
                        this.astFactory.makeASTRoot(currentAST, lp_AST);
                        this.match(91);
                        if (this.inputState.guessing == 0) {
                            lp_AST.setType(23);
                        }
                        this.classTypeSpec(true);
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        this.match(123);
                        this.unaryExpressionNotPlusMinus(0);
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                    } else if (_tokenSet_93.member(this.LA(1)) && _tokenSet_37.member(this.LA(2))) {
                        this.postfixExpression(lc_stmt);
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                    } else {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                unaryExpressionNotPlusMinus_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = unaryExpressionNotPlusMinus_AST;
    }

    public final void postfixExpression(int lc_stmt) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST postfixExpression_AST = null;
        Token in = null;
        AST in_AST = null;
        Token de = null;
        AST de_AST = null;
        this.pathExpression(lc_stmt);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.LA(1) == 190 && _tokenSet_107.member(this.LA(2))) {
            in = this.LT(1);
            in_AST = this.astFactory.create(in);
            this.astFactory.makeASTRoot(currentAST, in_AST);
            this.match(190);
            if (this.inputState.guessing == 0) {
                in_AST.setType(25);
            }
        } else if (this.LA(1) == 193 && _tokenSet_107.member(this.LA(2))) {
            de = this.LT(1);
            de_AST = this.astFactory.create(de);
            this.astFactory.makeASTRoot(currentAST, de_AST);
            this.match(193);
            if (this.inputState.guessing == 0) {
                de_AST.setType(26);
            }
        } else if (!_tokenSet_107.member(this.LA(1)) || !_tokenSet_53.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = postfixExpression_AST = currentAST.root;
    }

    public final void constant() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST constant_AST = null;
        switch (this.LA(1)) {
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.constantNumber();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                constant_AST = currentAST.root;
                break;
            }
            case 88: {
                AST tmp335_AST = null;
                tmp335_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp335_AST);
                this.match(88);
                constant_AST = currentAST.root;
                break;
            }
            case 161: {
                AST tmp336_AST = null;
                tmp336_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp336_AST);
                this.match(161);
                constant_AST = currentAST.root;
                break;
            }
            case 157: {
                AST tmp337_AST = null;
                tmp337_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp337_AST);
                this.match(157);
                constant_AST = currentAST.root;
                break;
            }
            case 160: {
                AST tmp338_AST = null;
                tmp338_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp338_AST);
                this.match(160);
                constant_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = constant_AST;
    }

    public final void newExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST newExpression_AST = null;
        AST ta_AST = null;
        AST t_AST = null;
        AST mca_AST = null;
        AST cb_AST = null;
        AST ad_AST = null;
        Token first = this.LT(1);
        this.match(159);
        this.nls();
        switch (this.LA(1)) {
            case 89: {
                this.typeArguments();
                ta_AST = this.returnAST;
                break;
            }
            case 87: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.type();
        t_AST = this.returnAST;
        switch (this.LA(1)) {
            case 91: 
            case 205: {
                this.nls();
                this.methodCallArgs(null);
                mca_AST = this.returnAST;
                if (this.LA(1) == 126 && _tokenSet_55.member(this.LA(2))) {
                    this.classBlock();
                    cb_AST = this.returnAST;
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                } else if (!_tokenSet_108.member(this.LA(1)) || !_tokenSet_53.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                if (this.inputState.guessing != 0) break;
                newExpression_AST = currentAST.root;
                mca_AST = mca_AST.getFirstChild();
                currentAST.root = newExpression_AST = this.astFactory.make(new ASTArray(5).add(this.create(159, "new", first, this.LT(1))).add(ta_AST).add(t_AST).add(mca_AST).add(cb_AST));
                currentAST.child = newExpression_AST != null && newExpression_AST.getFirstChild() != null ? newExpression_AST.getFirstChild() : newExpression_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            case 85: {
                this.newArrayDeclarator();
                ad_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                newExpression_AST = currentAST.root;
                currentAST.root = newExpression_AST = this.astFactory.make(new ASTArray(4).add(this.create(159, "new", first, this.LT(1))).add(ta_AST).add(t_AST).add(ad_AST));
                currentAST.child = newExpression_AST != null && newExpression_AST.getFirstChild() != null ? newExpression_AST.getFirstChild() : newExpression_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = newExpression_AST = currentAST.root;
    }

    public final void closableBlockConstructorExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST closableBlockConstructorExpression_AST = null;
        this.closableBlock();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = closableBlockConstructorExpression_AST = currentAST.root;
    }

    public final void listOrMapConstructorExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST listOrMapConstructorExpression_AST = null;
        Token lcon = null;
        AST lcon_AST = null;
        AST args_AST = null;
        Token emcon = null;
        AST emcon_AST = null;
        boolean hasLabels = false;
        if (this.LA(1) == 85 && _tokenSet_109.member(this.LA(2))) {
            lcon = this.LT(1);
            lcon_AST = this.astFactory.create(lcon);
            this.match(85);
            this.argList();
            args_AST = this.returnAST;
            this.astFactory.addASTChild(currentAST, this.returnAST);
            if (this.inputState.guessing == 0) {
                hasLabels |= this.argListHasLabels;
            }
            this.match(86);
            if (this.inputState.guessing == 0) {
                listOrMapConstructorExpression_AST = currentAST.root;
                int type = hasLabels ? 58 : 57;
                currentAST.root = listOrMapConstructorExpression_AST = this.astFactory.make(new ASTArray(2).add(this.create(type, "[", lcon_AST, this.LT(1))).add(args_AST));
                currentAST.child = listOrMapConstructorExpression_AST != null && listOrMapConstructorExpression_AST.getFirstChild() != null ? listOrMapConstructorExpression_AST.getFirstChild() : listOrMapConstructorExpression_AST;
                currentAST.advanceChildToEnd();
            }
            listOrMapConstructorExpression_AST = currentAST.root;
        } else if (this.LA(1) == 85 && this.LA(2) == 136) {
            emcon = this.LT(1);
            emcon_AST = this.astFactory.create(emcon);
            this.astFactory.makeASTRoot(currentAST, emcon_AST);
            this.match(85);
            this.match(136);
            this.match(86);
            if (this.inputState.guessing == 0) {
                emcon_AST.setType(58);
            }
            listOrMapConstructorExpression_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = listOrMapConstructorExpression_AST;
    }

    public final void stringConstructorValuePart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST stringConstructorValuePart_AST = null;
        switch (this.LA(1)) {
            case 87: {
                this.identifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 132: {
                AST tmp343_AST = null;
                tmp343_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp343_AST);
                this.match(132);
                break;
            }
            case 99: {
                AST tmp344_AST = null;
                tmp344_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp344_AST);
                this.match(99);
                break;
            }
            case 126: {
                this.openOrClosableBlock();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = stringConstructorValuePart_AST = currentAST.root;
    }

    public final void newArrayDeclarator() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST newArrayDeclarator_AST = null;
        Token lb = null;
        AST lb_AST = null;
        int _cnt574 = 0;
        while (true) {
            if (this.LA(1) == 85 && _tokenSet_110.member(this.LA(2))) {
                lb = this.LT(1);
                lb_AST = this.astFactory.create(lb);
                this.astFactory.makeASTRoot(currentAST, lb_AST);
                this.match(85);
                if (this.inputState.guessing == 0) {
                    lb_AST.setType(17);
                }
                switch (this.LA(1)) {
                    case 85: 
                    case 87: 
                    case 88: 
                    case 91: 
                    case 99: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 126: 
                    case 132: 
                    case 148: 
                    case 149: 
                    case 157: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 190: 
                    case 193: 
                    case 195: 
                    case 196: 
                    case 197: 
                    case 199: 
                    case 200: 
                    case 201: 
                    case 202: 
                    case 203: 
                    case 204: {
                        this.expression(0);
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 86: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            } else {
                if (_cnt574 >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.match(86);
            ++_cnt574;
        }
        this.returnAST = newArrayDeclarator_AST = currentAST.root;
    }

    public final byte argument() throws RecognitionException, TokenStreamException {
        boolean sce;
        AST argument_AST;
        ASTPair currentAST;
        byte hasLabelOrSpread;
        block16: {
            block17: {
                AST sp_AST;
                Token sp;
                block15: {
                    hasLabelOrSpread = 0;
                    this.returnAST = null;
                    currentAST = new ASTPair();
                    argument_AST = null;
                    Token c = null;
                    AST c_AST = null;
                    sp = null;
                    sp_AST = null;
                    sce = false;
                    boolean synPredMatched560 = false;
                    if (_tokenSet_100.member(this.LA(1)) && _tokenSet_101.member(this.LA(2))) {
                        int _m560 = this.mark();
                        synPredMatched560 = true;
                        ++this.inputState.guessing;
                        try {
                            this.argumentLabelStart();
                        }
                        catch (RecognitionException pe) {
                            synPredMatched560 = false;
                        }
                        this.rewind(_m560);
                        --this.inputState.guessing;
                    }
                    if (!synPredMatched560) break block15;
                    this.argumentLabel();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    c = this.LT(1);
                    c_AST = this.astFactory.create(c);
                    this.astFactory.makeASTRoot(currentAST, c_AST);
                    this.match(136);
                    if (this.inputState.guessing == 0) {
                        c_AST.setType(54);
                    }
                    if (this.inputState.guessing == 0) {
                        hasLabelOrSpread = (byte)(hasLabelOrSpread | 1);
                    }
                    break block16;
                }
                if (this.LA(1) != 113) break block17;
                sp = this.LT(1);
                sp_AST = this.astFactory.create(sp);
                this.astFactory.makeASTRoot(currentAST, sp_AST);
                this.match(113);
                if (this.inputState.guessing == 0) {
                    sp_AST.setType(55);
                }
                if (this.inputState.guessing == 0) {
                    hasLabelOrSpread = (byte)(hasLabelOrSpread | 2);
                }
                switch (this.LA(1)) {
                    case 136: {
                        this.match(136);
                        if (this.inputState.guessing == 0) {
                            sp_AST.setType(56);
                        }
                        if (this.inputState.guessing == 0) {
                            hasLabelOrSpread = (byte)(hasLabelOrSpread | 1);
                        }
                        break block16;
                    }
                    case 38: 
                    case 39: 
                    case 43: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 87: 
                    case 88: 
                    case 91: 
                    case 96: 
                    case 99: 
                    case 104: 
                    case 105: 
                    case 106: 
                    case 107: 
                    case 108: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 112: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 121: 
                    case 122: 
                    case 126: 
                    case 132: 
                    case 143: 
                    case 144: 
                    case 145: 
                    case 146: 
                    case 147: 
                    case 148: 
                    case 149: 
                    case 157: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 190: 
                    case 193: 
                    case 195: 
                    case 196: 
                    case 197: 
                    case 199: 
                    case 200: 
                    case 201: 
                    case 202: 
                    case 203: 
                    case 204: {
                        break block16;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            if (!_tokenSet_89.member(this.LA(1)) || !_tokenSet_111.member(this.LA(2))) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        sce = this.strictContextExpression(true);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            this.require(this.LA(1) != 136, "illegal colon after argument expression", "a complex label expression before a colon must be parenthesized");
        }
        this.returnAST = argument_AST = currentAST.root;
        return hasLabelOrSpread;
    }

    public final void argumentLabelStart() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object argumentLabelStart_AST = null;
        switch (this.LA(1)) {
            case 87: {
                AST tmp347_AST = null;
                tmp347_AST = this.astFactory.create(this.LT(1));
                this.match(87);
                break;
            }
            case 38: 
            case 39: 
            case 40: 
            case 41: 
            case 42: 
            case 43: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 98: 
            case 99: 
            case 104: 
            case 105: 
            case 106: 
            case 107: 
            case 108: 
            case 109: 
            case 110: 
            case 111: 
            case 112: 
            case 114: 
            case 115: 
            case 116: 
            case 117: 
            case 118: 
            case 119: 
            case 120: 
            case 121: 
            case 122: 
            case 129: 
            case 130: 
            case 131: 
            case 132: 
            case 137: 
            case 138: 
            case 139: 
            case 140: 
            case 141: 
            case 142: 
            case 143: 
            case 144: 
            case 145: 
            case 146: 
            case 147: 
            case 150: 
            case 151: 
            case 152: 
            case 153: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: {
                this.keywordPropertyNames();
                break;
            }
            case 199: 
            case 200: 
            case 201: 
            case 202: 
            case 203: 
            case 204: {
                this.constantNumber();
                break;
            }
            case 88: {
                AST tmp348_AST = null;
                tmp348_AST = this.astFactory.create(this.LT(1));
                this.match(88);
                break;
            }
            case 85: 
            case 91: 
            case 126: 
            case 197: {
                this.balancedBrackets();
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        AST tmp349_AST = null;
        tmp349_AST = this.astFactory.create(this.LT(1));
        this.match(136);
        this.returnAST = argumentLabelStart_AST;
    }

    public final void constantNumber() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST constantNumber_AST = null;
        switch (this.LA(1)) {
            case 199: {
                AST tmp350_AST = null;
                tmp350_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp350_AST);
                this.match(199);
                constantNumber_AST = currentAST.root;
                break;
            }
            case 200: {
                AST tmp351_AST = null;
                tmp351_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp351_AST);
                this.match(200);
                constantNumber_AST = currentAST.root;
                break;
            }
            case 201: {
                AST tmp352_AST = null;
                tmp352_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp352_AST);
                this.match(201);
                constantNumber_AST = currentAST.root;
                break;
            }
            case 202: {
                AST tmp353_AST = null;
                tmp353_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp353_AST);
                this.match(202);
                constantNumber_AST = currentAST.root;
                break;
            }
            case 203: {
                AST tmp354_AST = null;
                tmp354_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp354_AST);
                this.match(203);
                constantNumber_AST = currentAST.root;
                break;
            }
            case 204: {
                AST tmp355_AST = null;
                tmp355_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp355_AST);
                this.match(204);
                constantNumber_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = constantNumber_AST;
    }

    public final void balancedBrackets() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        Object balancedBrackets_AST = null;
        switch (this.LA(1)) {
            case 91: {
                AST tmp356_AST = null;
                tmp356_AST = this.astFactory.create(this.LT(1));
                this.match(91);
                this.balancedTokens();
                AST tmp357_AST = null;
                tmp357_AST = this.astFactory.create(this.LT(1));
                this.match(123);
                break;
            }
            case 85: {
                AST tmp358_AST = null;
                tmp358_AST = this.astFactory.create(this.LT(1));
                this.match(85);
                this.balancedTokens();
                AST tmp359_AST = null;
                tmp359_AST = this.astFactory.create(this.LT(1));
                this.match(86);
                break;
            }
            case 126: {
                AST tmp360_AST = null;
                tmp360_AST = this.astFactory.create(this.LT(1));
                this.match(126);
                this.balancedTokens();
                AST tmp361_AST = null;
                tmp361_AST = this.astFactory.create(this.LT(1));
                this.match(127);
                break;
            }
            case 197: {
                AST tmp362_AST = null;
                tmp362_AST = this.astFactory.create(this.LT(1));
                this.match(197);
                this.balancedTokens();
                AST tmp363_AST = null;
                tmp363_AST = this.astFactory.create(this.LT(1));
                this.match(198);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = balancedBrackets_AST;
    }

    protected void buildTokenTypeASTClassMap() {
        this.tokenTypeToASTClassMap = null;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[8];
        data[0] = 9620726743042L;
        data[1] = 5186456864203472896L;
        data[2] = 4611686034009209361L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -576460889742508032L;
        data[2] = -1L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -131072L;
        data[2] = 0x41FFFFFFFFFFFFFFL;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[16];
        data[0] = -14L;
        for (int i = 1; i <= 2; ++i) {
            data[i] = -1L;
        }
        data[3] = 0x7FFFFFFFFFL;
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[]{0L, 4303749120L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -4036915073080885248L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_6() {
        long[] data = new long[]{0L, 0x860000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_7() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -4036915073617756160L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_8() {
        long[] data = new long[8];
        data[2] = 1025L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_9() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 5186456864203472896L;
        data[2] = 4611686034009210385L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_10() {
        long[] data = new long[8];
        data[0] = 2L;
        data[1] = Long.MIN_VALUE;
        data[2] = 0x400403L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_11() {
        long[] data = new long[8];
        data[0] = 580267261558786L;
        data[1] = -131072L;
        data[2] = -33L;
        data[3] = 16383L;
        return data;
    }

    private static final long[] mk_tokenSet_12() {
        long[] data = new long[]{0x8C000000000L, 574208956786278400L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_13() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574208957365092352L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_14() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574770807907549184L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_15() {
        long[] data = new long[]{0x8C000000000L, 574770807236460544L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_16() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574770807892869120L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_17() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -4036915172651302912L;
        data[2] = 4611686034009209521L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_18() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 5186456864203472896L;
        data[2] = 4611686034009209360L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_19() {
        long[] data = new long[8];
        data[1] = 4612247903390400512L;
        data[2] = 4611686033999790096L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_20() {
        long[] data = new long[]{0L, 0x20880000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_21() {
        long[] data = new long[]{0x8C000000000L, 574208960812810240L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_22() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574208960821198848L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_23() {
        long[] data = new long[8];
        data[0] = 9620726743042L;
        data[1] = -4036915172617748480L;
        data[2] = 4611686034013404691L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_24() {
        long[] data = new long[]{0L, 561850450182144L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_25() {
        long[] data = new long[]{0L, 0x7A00000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_26() {
        long[] data = new long[8];
        data[0] = 2L;
        data[1] = -8070450394674757632L;
        data[2] = 0x400403L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_27() {
        long[] data = new long[8];
        data[0] = 2L;
        data[1] = -8070450394808975360L;
        data[2] = 0x400403L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_28() {
        long[] data = new long[8];
        data[1] = 0x800000L;
        data[2] = 4L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_29() {
        long[] data = new long[8];
        data[0] = 2L;
        data[1] = -4611686018427387904L;
        data[2] = 0x400403L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_30() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -4036915172651302912L;
        data[2] = 4611686034009209361L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_31() {
        long[] data = new long[]{0L, 0x6A00000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_32() {
        long[] data = new long[]{2L, 1729382394353418240L, 16641L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_33() {
        long[] data = new long[]{0x8C000000000L, 574770807355998208L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_34() {
        long[] data = new long[]{0x8C000000000L, 574770807288889344L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_35() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = 5764043533593739264L;
        data[2] = 4611686035137494558L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_36() {
        long[] data = new long[8];
        data[0] = 9620726743042L;
        data[1] = -8072140335660269568L;
        data[2] = 33L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_37() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -131072L;
        data[2] = -1L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_38() {
        long[] data = new long[8];
        data[1] = 561859040116736L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_39() {
        long[] data = new long[]{0L, 0xD000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_40() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -562949987106816L;
        data[2] = 4755801206033481727L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_41() {
        long[] data = new long[8];
        data[1] = 4611686018563702784L;
        data[3] = 32L;
        return data;
    }

    private static final long[] mk_tokenSet_42() {
        long[] data = new long[16];
        data[0] = -16L;
        data[1] = 4035225265983455231L;
        data[2] = -1L;
        data[3] = 0x7FFFFFFF9FL;
        return data;
    }

    private static final long[] mk_tokenSet_43() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574770811296546816L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_44() {
        long[] data = new long[]{0L, 561858805235712L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_45() {
        long[] data = new long[8];
        data[0] = 2L;
        data[1] = 799014912L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_46() {
        long[] data = new long[]{0x8C000000000L, 574208952490262528L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_47() {
        long[] data = new long[8];
        data[1] = 4612247907685367808L;
        data[2] = 4611686033999790096L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_48() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = -1152921642045931520L;
        data[2] = -70351564308481L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_49() {
        long[] data = new long[]{0xFC000000000L, 575896758414868480L, 16706960926L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_50() {
        long[] data = new long[8];
        data[1] = 4612247903390400512L;
        data[2] = 4611756402743967760L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_51() {
        long[] data = new long[8];
        data[1] = 4612247911980335104L;
        data[2] = 4611686033999790096L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_52() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -2306406865506009088L;
        data[2] = 4611756386701803423L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_53() {
        long[] data = new long[8];
        data[0] = 580267261558786L;
        data[1] = -131072L;
        data[2] = -1L;
        data[3] = 16383L;
        return data;
    }

    private static final long[] mk_tokenSet_54() {
        long[] data = new long[8];
        data[1] = -4611685880308957184L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_55() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -4036915207164395520L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_56() {
        long[] data = new long[8];
        data[0] = 9620726743042L;
        data[1] = -4036915172550639616L;
        data[2] = 4611686034013404691L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_57() {
        long[] data = new long[8];
        data[1] = 4612248916866367488L;
        data[2] = 8L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_58() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -2883992654331772928L;
        data[2] = 9L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_59() {
        long[] data = new long[]{0x8C000000000L, 574208956794667008L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_60() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574208957465755648L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_61() {
        long[] data = new long[8];
        data[1] = -8070450394674757632L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_62() {
        long[] data = new long[8];
        data[1] = -9223371899415822336L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_63() {
        long[] data = new long[8];
        data[0] = 9620726743042L;
        data[1] = -4036915069725442048L;
        data[2] = 0x400403L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_64() {
        long[] data = new long[8];
        data[1] = -9223371895112466432L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_65() {
        long[] data = new long[8];
        data[1] = 0x100800000L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_66() {
        long[] data = new long[8];
        data[1] = -4611685876013989888L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_67() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574770807823663104L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_68() {
        long[] data = new long[]{0x8C000000000L, 574770807270014976L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_69() {
        long[] data = new long[8];
        data[1] = -4611686018427387904L;
        data[2] = 1L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_70() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -4036915035178795008L;
        data[2] = 4611686034009209361L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_71() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -4036915172617748480L;
        data[2] = 4611686034009209361L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_72() {
        long[] data = new long[]{0L, 0x802000000L, 16L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_73() {
        long[] data = new long[8];
        data[1] = 561859174334464L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_74() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = -576460889742508032L;
        data[2] = -1L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_75() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 5186456864203472896L;
        data[2] = 4611686034009209361L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_76() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -3459328370112856064L;
        data[2] = 4611686035137494943L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_77() {
        long[] data = new long[]{0L, 0x6A00000L, 32L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_78() {
        long[] data = new long[8];
        data[1] = 1729382394357612544L;
        data[2] = 128L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_79() {
        long[] data = new long[]{0L, 0x6800000L, 64L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_80() {
        long[] data = new long[8];
        data[0] = 0x4000000000L;
        data[1] = 561854746198016L;
        data[2] = 160L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_81() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = -2883993530504839168L;
        data[2] = 4611686034009209521L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_82() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 5186456860176678912L;
        data[2] = 4611686034000805905L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_83() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = -137439084544L;
        data[2] = -1L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_84() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574770807876091904L;
        data[2] = 16640L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_85() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 574770807876091904L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_86() {
        long[] data = new long[8];
        data[1] = 0x4000000000000000L;
        data[2] = 0x1000000L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_87() {
        long[] data = new long[8];
        data[1] = 4612247903390400512L;
        data[2] = 4611686033999790096L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_88() {
        long[] data = new long[8];
        data[0] = 2L;
        data[1] = -8646911147108204544L;
        data[2] = 0x400403L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_89() {
        long[] data = new long[8];
        data[0] = 0x8C000000000L;
        data[1] = 5186456860176678912L;
        data[2] = 4611686034000805904L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_90() {
        long[] data = new long[]{0L, 0xC000000000L, 0x3800000000000000L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_91() {
        long[] data = new long[8];
        data[0] = -16L;
        data[1] = -4611686018427387905L;
        data[2] = -1L;
        data[3] = 0x7FFFFFFFFFL;
        return data;
    }

    private static final long[] mk_tokenSet_92() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = 5187582776995348480L;
        data[2] = 4611686035137494558L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_93() {
        long[] data = new long[8];
        data[1] = 4612247903390400512L;
        data[2] = 15569256464L;
        data[3] = 8096L;
        return data;
    }

    private static final long[] mk_tokenSet_94() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -3458765415763804160L;
        data[2] = 4611686035607257023L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_95() {
        long[] data = new long[8];
        data[1] = 4611686018630811648L;
        data[2] = 0x1C000000L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_96() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = -3458765553202757632L;
        data[2] = 4611686035607256767L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_97() {
        long[] data = new long[8];
        data[0] = 9620726743042L;
        data[1] = 5186456860176678912L;
        data[2] = 4611686034000805904L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_98() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -576460752303554560L;
        data[2] = -1L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_99() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = 5187582776995348480L;
        data[2] = 4611686035137494558L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_100() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = 5187582776995348480L;
        data[2] = 16706960926L;
        data[3] = 8096L;
        return data;
    }

    private static final long[] mk_tokenSet_101() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = -4035226305573289984L;
        data[2] = 4611686035137494975L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_102() {
        long[] data = new long[]{0xFC000000000L, 575896758406479872L, 16706960926L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_103() {
        long[] data = new long[]{0L, 0x1002000000L, 0x600000000004000L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_104() {
        long[] data = new long[8];
        data[1] = 561850450182144L;
        data[3] = 8192L;
        return data;
    }

    private static final long[] mk_tokenSet_105() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -563847702380544L;
        data[2] = 4755801206033481695L;
        data[3] = 16314L;
        return data;
    }

    private static final long[] mk_tokenSet_106() {
        long[] data = new long[8];
        data[1] = 0x2000000000000L;
        data[2] = Long.MIN_VALUE;
        data[3] = 1L;
        return data;
    }

    private static final long[] mk_tokenSet_107() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -4362207232L;
        data[2] = -469762081L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_108() {
        long[] data = new long[8];
        data[0] = 17317308137474L;
        data[1] = -4295098368L;
        data[2] = -33L;
        data[3] = 16319L;
        return data;
    }

    private static final long[] mk_tokenSet_109() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = 5188145731247931392L;
        data[2] = 4611686035137494558L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_110() {
        long[] data = new long[8];
        data[1] = 4612247903394594816L;
        data[2] = 4611686033999790096L;
        data[3] = 8122L;
        return data;
    }

    private static final long[] mk_tokenSet_111() {
        long[] data = new long[8];
        data[0] = 0xFC000000000L;
        data[1] = -131072L;
        data[2] = -1L;
        data[3] = 16319L;
        return data;
    }
}

