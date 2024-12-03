/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.antlr.java;

import groovyjarjarantlr.ASTFactory;
import groovyjarjarantlr.ASTPair;
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
import groovyjarjarantlr.collections.AST;
import groovyjarjarantlr.collections.impl.ASTArray;
import groovyjarjarantlr.collections.impl.BitSet;
import java.io.InputStream;
import java.io.Reader;
import org.codehaus.groovy.antlr.GroovySourceAST;
import org.codehaus.groovy.antlr.SourceBuffer;
import org.codehaus.groovy.antlr.java.JavaLexer;
import org.codehaus.groovy.antlr.java.JavaTokenTypes;

public class JavaRecognizer
extends LLkParser
implements JavaTokenTypes {
    private static GroovySourceAST dummyVariableToforceClassLoaderToFindASTClass = new GroovySourceAST();
    JavaLexer lexer;
    private SourceBuffer sourceBuffer;
    private int ltCounter = 0;
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "BLOCK", "MODIFIERS", "OBJBLOCK", "SLIST", "METHOD_DEF", "VARIABLE_DEF", "INSTANCE_INIT", "STATIC_INIT", "TYPE", "CLASS_DEF", "INTERFACE_DEF", "PACKAGE_DEF", "ARRAY_DECLARATOR", "EXTENDS_CLAUSE", "IMPLEMENTS_CLAUSE", "PARAMETERS", "PARAMETER_DEF", "LABELED_STAT", "TYPECAST", "INDEX_OP", "POST_INC", "POST_DEC", "METHOD_CALL", "EXPR", "ARRAY_INIT", "IMPORT", "UNARY_MINUS", "UNARY_PLUS", "CASE_GROUP", "ELIST", "FOR_INIT", "FOR_CONDITION", "FOR_ITERATOR", "EMPTY_STAT", "\"final\"", "\"abstract\"", "\"strictfp\"", "SUPER_CTOR_CALL", "CTOR_CALL", "VARIABLE_PARAMETER_DEF", "STATIC_IMPORT", "ENUM_DEF", "ENUM_CONSTANT_DEF", "FOR_EACH_CLAUSE", "ANNOTATION_DEF", "ANNOTATIONS", "ANNOTATION", "ANNOTATION_MEMBER_VALUE_PAIR", "ANNOTATION_FIELD_DEF", "ANNOTATION_ARRAY_INIT", "TYPE_ARGUMENTS", "TYPE_ARGUMENT", "TYPE_PARAMETERS", "TYPE_PARAMETER", "WILDCARD_TYPE", "TYPE_UPPER_BOUNDS", "TYPE_LOWER_BOUNDS", "\"package\"", "SEMI", "\"import\"", "\"static\"", "LBRACK", "RBRACK", "IDENT", "DOT", "QUESTION", "\"extends\"", "\"super\"", "LT", "GT", "COMMA", "SR", "BSR", "\"void\"", "\"boolean\"", "\"byte\"", "\"char\"", "\"short\"", "\"int\"", "\"float\"", "\"long\"", "\"double\"", "STAR", "\"private\"", "\"public\"", "\"protected\"", "\"transient\"", "\"native\"", "\"threadsafe\"", "\"synchronized\"", "\"volatile\"", "AT", "LPAREN", "RPAREN", "ASSIGN", "LCURLY", "RCURLY", "\"class\"", "\"interface\"", "\"enum\"", "BAND", "\"default\"", "\"implements\"", "\"this\"", "\"throws\"", "TRIPLE_DOT", "COLON", "\"if\"", "\"else\"", "\"while\"", "\"do\"", "\"break\"", "\"continue\"", "\"return\"", "\"switch\"", "\"throw\"", "\"assert\"", "\"for\"", "\"case\"", "\"try\"", "\"finally\"", "\"catch\"", "PLUS_ASSIGN", "MINUS_ASSIGN", "STAR_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "SR_ASSIGN", "BSR_ASSIGN", "SL_ASSIGN", "BAND_ASSIGN", "BXOR_ASSIGN", "BOR_ASSIGN", "LOR", "LAND", "BOR", "BXOR", "NOT_EQUAL", "EQUAL", "LE", "GE", "\"instanceof\"", "SL", "PLUS", "MINUS", "DIV", "MOD", "INC", "DEC", "BNOT", "LNOT", "\"true\"", "\"false\"", "\"null\"", "\"new\"", "NUM_INT", "CHAR_LITERAL", "STRING_LITERAL", "NUM_FLOAT", "NUM_LONG", "NUM_DOUBLE", "WS", "SL_COMMENT", "ML_COMMENT", "ESC", "HEX_DIGIT", "VOCAB", "EXPONENT", "FLOAT_SUFFIX"};
    public static final BitSet _tokenSet_0 = new BitSet(JavaRecognizer.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(JavaRecognizer.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(JavaRecognizer.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(JavaRecognizer.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(JavaRecognizer.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(JavaRecognizer.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(JavaRecognizer.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(JavaRecognizer.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(JavaRecognizer.mk_tokenSet_8());
    public static final BitSet _tokenSet_9 = new BitSet(JavaRecognizer.mk_tokenSet_9());
    public static final BitSet _tokenSet_10 = new BitSet(JavaRecognizer.mk_tokenSet_10());
    public static final BitSet _tokenSet_11 = new BitSet(JavaRecognizer.mk_tokenSet_11());
    public static final BitSet _tokenSet_12 = new BitSet(JavaRecognizer.mk_tokenSet_12());
    public static final BitSet _tokenSet_13 = new BitSet(JavaRecognizer.mk_tokenSet_13());
    public static final BitSet _tokenSet_14 = new BitSet(JavaRecognizer.mk_tokenSet_14());
    public static final BitSet _tokenSet_15 = new BitSet(JavaRecognizer.mk_tokenSet_15());
    public static final BitSet _tokenSet_16 = new BitSet(JavaRecognizer.mk_tokenSet_16());
    public static final BitSet _tokenSet_17 = new BitSet(JavaRecognizer.mk_tokenSet_17());
    public static final BitSet _tokenSet_18 = new BitSet(JavaRecognizer.mk_tokenSet_18());
    public static final BitSet _tokenSet_19 = new BitSet(JavaRecognizer.mk_tokenSet_19());
    public static final BitSet _tokenSet_20 = new BitSet(JavaRecognizer.mk_tokenSet_20());
    public static final BitSet _tokenSet_21 = new BitSet(JavaRecognizer.mk_tokenSet_21());
    public static final BitSet _tokenSet_22 = new BitSet(JavaRecognizer.mk_tokenSet_22());
    public static final BitSet _tokenSet_23 = new BitSet(JavaRecognizer.mk_tokenSet_23());
    public static final BitSet _tokenSet_24 = new BitSet(JavaRecognizer.mk_tokenSet_24());
    public static final BitSet _tokenSet_25 = new BitSet(JavaRecognizer.mk_tokenSet_25());
    public static final BitSet _tokenSet_26 = new BitSet(JavaRecognizer.mk_tokenSet_26());
    public static final BitSet _tokenSet_27 = new BitSet(JavaRecognizer.mk_tokenSet_27());
    public static final BitSet _tokenSet_28 = new BitSet(JavaRecognizer.mk_tokenSet_28());
    public static final BitSet _tokenSet_29 = new BitSet(JavaRecognizer.mk_tokenSet_29());
    public static final BitSet _tokenSet_30 = new BitSet(JavaRecognizer.mk_tokenSet_30());
    public static final BitSet _tokenSet_31 = new BitSet(JavaRecognizer.mk_tokenSet_31());
    public static final BitSet _tokenSet_32 = new BitSet(JavaRecognizer.mk_tokenSet_32());
    public static final BitSet _tokenSet_33 = new BitSet(JavaRecognizer.mk_tokenSet_33());
    public static final BitSet _tokenSet_34 = new BitSet(JavaRecognizer.mk_tokenSet_34());
    public static final BitSet _tokenSet_35 = new BitSet(JavaRecognizer.mk_tokenSet_35());
    public static final BitSet _tokenSet_36 = new BitSet(JavaRecognizer.mk_tokenSet_36());
    public static final BitSet _tokenSet_37 = new BitSet(JavaRecognizer.mk_tokenSet_37());
    public static final BitSet _tokenSet_38 = new BitSet(JavaRecognizer.mk_tokenSet_38());
    public static final BitSet _tokenSet_39 = new BitSet(JavaRecognizer.mk_tokenSet_39());
    public static final BitSet _tokenSet_40 = new BitSet(JavaRecognizer.mk_tokenSet_40());
    public static final BitSet _tokenSet_41 = new BitSet(JavaRecognizer.mk_tokenSet_41());
    public static final BitSet _tokenSet_42 = new BitSet(JavaRecognizer.mk_tokenSet_42());
    public static final BitSet _tokenSet_43 = new BitSet(JavaRecognizer.mk_tokenSet_43());
    public static final BitSet _tokenSet_44 = new BitSet(JavaRecognizer.mk_tokenSet_44());
    public static final BitSet _tokenSet_45 = new BitSet(JavaRecognizer.mk_tokenSet_45());
    public static final BitSet _tokenSet_46 = new BitSet(JavaRecognizer.mk_tokenSet_46());
    public static final BitSet _tokenSet_47 = new BitSet(JavaRecognizer.mk_tokenSet_47());
    public static final BitSet _tokenSet_48 = new BitSet(JavaRecognizer.mk_tokenSet_48());
    public static final BitSet _tokenSet_49 = new BitSet(JavaRecognizer.mk_tokenSet_49());

    public static JavaRecognizer make(JavaLexer lexer) {
        JavaRecognizer parser = new JavaRecognizer(lexer.plumb());
        parser.lexer = lexer;
        lexer.parser = parser;
        parser.setASTNodeClass("org.codehaus.groovy.antlr.GroovySourceAST");
        return parser;
    }

    public static JavaRecognizer make(InputStream in) {
        return JavaRecognizer.make(new JavaLexer(in));
    }

    public static JavaRecognizer make(Reader in) {
        return JavaRecognizer.make(new JavaLexer(in));
    }

    public static JavaRecognizer make(InputBuffer in) {
        return JavaRecognizer.make(new JavaLexer(in));
    }

    public static JavaRecognizer make(LexerSharedInputState in) {
        return JavaRecognizer.make(new JavaLexer(in));
    }

    public JavaLexer getLexer() {
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

    public AST create(int type, String txt, Token first, Token last) {
        AST t = this.astFactory.create(type, txt);
        if (t != null && first != null) {
            t.initialize(first);
            t.initialize(type, txt);
        }
        if (t instanceof GroovySourceAST && last != null) {
            GroovySourceAST node = (GroovySourceAST)t;
            node.setLast(last);
        }
        return t;
    }

    protected JavaRecognizer(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public JavaRecognizer(TokenBuffer tokenBuf) {
        this(tokenBuf, 2);
    }

    protected JavaRecognizer(TokenStream lexer, int k) {
        super(lexer, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public JavaRecognizer(TokenStream lexer) {
        this(lexer, 2);
    }

    public JavaRecognizer(ParserSharedInputState state) {
        super(state, 2);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public final void compilationUnit() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST compilationUnit_AST = null;
        boolean synPredMatched4 = false;
        if ((this.LA(1) == 61 || this.LA(1) == 95) && this.LA(2) == 67) {
            int _m4 = this.mark();
            synPredMatched4 = true;
            ++this.inputState.guessing;
            try {
                this.annotations();
                this.match(61);
            }
            catch (RecognitionException pe) {
                synPredMatched4 = false;
            }
            this.rewind(_m4);
            --this.inputState.guessing;
        }
        if (synPredMatched4) {
            this.packageDefinition();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_0.member(this.LA(1)) || !_tokenSet_1.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        while (this.LA(1) == 63) {
            this.importDefinition();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        while (_tokenSet_2.member(this.LA(1))) {
            this.typeDefinition();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.match(1);
        this.returnAST = compilationUnit_AST = currentAST.root;
    }

    public final void annotations() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotations_AST = null;
        Token first = this.LT(1);
        while (this.LA(1) == 95) {
            this.annotation();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            annotations_AST = currentAST.root;
            currentAST.root = annotations_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(49, "ANNOTATIONS")).add(annotations_AST));
            currentAST.child = annotations_AST != null && annotations_AST.getFirstChild() != null ? annotations_AST.getFirstChild() : annotations_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotations_AST = currentAST.root;
    }

    public final void packageDefinition() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST packageDefinition_AST = null;
        Token p = null;
        AST p_AST = null;
        try {
            this.annotations();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            p = this.LT(1);
            p_AST = this.astFactory.create(p);
            this.astFactory.makeASTRoot(currentAST, p_AST);
            this.match(61);
            if (this.inputState.guessing == 0) {
                p_AST.setType(15);
            }
            this.identifier();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            this.match(62);
            packageDefinition_AST = currentAST.root;
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
            throw ex;
        }
        this.returnAST = packageDefinition_AST;
    }

    public final void importDefinition() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST importDefinition_AST = null;
        Token i = null;
        AST i_AST = null;
        boolean isStatic = false;
        try {
            i = this.LT(1);
            i_AST = this.astFactory.create(i);
            this.astFactory.makeASTRoot(currentAST, i_AST);
            this.match(63);
            if (this.inputState.guessing == 0) {
                i_AST.setType(29);
            }
            switch (this.LA(1)) {
                case 64: {
                    this.match(64);
                    if (this.inputState.guessing != 0) break;
                    i_AST.setType(44);
                    break;
                }
                case 67: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.identifierStar();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            this.match(62);
            importDefinition_AST = currentAST.root;
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
            throw ex;
        }
        this.returnAST = importDefinition_AST;
    }

    public final void typeDefinition() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeDefinition_AST = null;
        AST m_AST = null;
        try {
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 40: 
                case 64: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 101: 
                case 102: 
                case 103: {
                    this.modifiers();
                    m_AST = this.returnAST;
                    this.typeDefinitionInternal(m_AST);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    typeDefinition_AST = currentAST.root;
                    break;
                }
                case 62: {
                    this.match(62);
                    typeDefinition_AST = currentAST.root;
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_3);
            }
            throw ex;
        }
        this.returnAST = typeDefinition_AST;
    }

    public final void identifier() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST identifier_AST = null;
        AST tmp6_AST = null;
        tmp6_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp6_AST);
        this.match(67);
        while (this.LA(1) == 68) {
            AST tmp7_AST = null;
            tmp7_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp7_AST);
            this.match(68);
            AST tmp8_AST = null;
            tmp8_AST = this.astFactory.create(this.LT(1));
            this.astFactory.addASTChild(currentAST, tmp8_AST);
            this.match(67);
        }
        this.returnAST = identifier_AST = currentAST.root;
    }

    public final void identifierStar() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST identifierStar_AST = null;
        AST tmp9_AST = null;
        tmp9_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp9_AST);
        this.match(67);
        while (this.LA(1) == 68 && this.LA(2) == 67) {
            AST tmp10_AST = null;
            tmp10_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp10_AST);
            this.match(68);
            AST tmp11_AST = null;
            tmp11_AST = this.astFactory.create(this.LT(1));
            this.astFactory.addASTChild(currentAST, tmp11_AST);
            this.match(67);
        }
        switch (this.LA(1)) {
            case 68: {
                AST tmp12_AST = null;
                tmp12_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp12_AST);
                this.match(68);
                AST tmp13_AST = null;
                tmp13_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp13_AST);
                this.match(86);
                break;
            }
            case 62: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = identifierStar_AST = currentAST.root;
    }

    public final void modifiers() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST modifiers_AST = null;
        Token first = this.LT(1);
        while (true) {
            if (_tokenSet_4.member(this.LA(1))) {
                this.modifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                continue;
            }
            if (this.LA(1) != 95 || this.LA(2) != 67 || this.LA(1) != 95 || this.LT(2).getText().equals("interface")) break;
            this.annotation();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            modifiers_AST = currentAST.root;
            currentAST.root = modifiers_AST = this.astFactory.make(new ASTArray(2).add(this.create(5, "MODIFIERS", first, this.LT(1))).add(modifiers_AST));
            currentAST.child = modifiers_AST != null && modifiers_AST.getFirstChild() != null ? modifiers_AST.getFirstChild() : modifiers_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = modifiers_AST = currentAST.root;
    }

    protected final void typeDefinitionInternal(AST mods) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeDefinitionInternal_AST = null;
        switch (this.LA(1)) {
            case 101: {
                this.classDefinition(mods);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            case 102: {
                this.interfaceDefinition(mods);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            case 103: {
                this.enumDefinition(mods);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeDefinitionInternal_AST = currentAST.root;
                break;
            }
            case 95: {
                this.annotationDefinition(mods);
                this.astFactory.addASTChild(currentAST, this.returnAST);
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
        Token first = this.LT(1);
        this.match(101);
        AST tmp15_AST = null;
        tmp15_AST = this.astFactory.create(this.LT(1));
        this.match(67);
        switch (this.LA(1)) {
            case 72: {
                this.typeParameters();
                tp_AST = this.returnAST;
                break;
            }
            case 70: 
            case 99: 
            case 106: {
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
            currentAST.root = classDefinition_AST = this.astFactory.make(new ASTArray(7).add(this.create(13, "CLASS_DEF", first, this.LT(1))).add(modifiers).add(tmp15_AST).add(tp_AST).add(sc_AST).add(ic_AST).add(cb_AST));
            currentAST.child = classDefinition_AST != null && classDefinition_AST.getFirstChild() != null ? classDefinition_AST.getFirstChild() : classDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = classDefinition_AST;
    }

    public final void interfaceDefinition(AST modifiers) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST interfaceDefinition_AST = null;
        AST tp_AST = null;
        AST ie_AST = null;
        AST ib_AST = null;
        Token first = this.LT(1);
        this.match(102);
        AST tmp17_AST = null;
        tmp17_AST = this.astFactory.create(this.LT(1));
        this.match(67);
        switch (this.LA(1)) {
            case 72: {
                this.typeParameters();
                tp_AST = this.returnAST;
                break;
            }
            case 70: 
            case 99: {
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
            currentAST.root = interfaceDefinition_AST = this.astFactory.make(new ASTArray(6).add(this.create(14, "INTERFACE_DEF", first, this.LT(1))).add(modifiers).add(tmp17_AST).add(tp_AST).add(ie_AST).add(ib_AST));
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
        Token first = this.LT(1);
        this.match(103);
        AST tmp19_AST = null;
        tmp19_AST = this.astFactory.create(this.LT(1));
        this.match(67);
        this.implementsClause();
        ic_AST = this.returnAST;
        this.enumBlock();
        eb_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            enumDefinition_AST = currentAST.root;
            currentAST.root = enumDefinition_AST = this.astFactory.make(new ASTArray(5).add(this.create(45, "ENUM_DEF", first, this.LT(1))).add(modifiers).add(tmp19_AST).add(ic_AST).add(eb_AST));
            currentAST.child = enumDefinition_AST != null && enumDefinition_AST.getFirstChild() != null ? enumDefinition_AST.getFirstChild() : enumDefinition_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = enumDefinition_AST;
    }

    public final void annotationDefinition(AST modifiers) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationDefinition_AST = null;
        AST ab_AST = null;
        Token first = this.LT(1);
        AST tmp20_AST = null;
        tmp20_AST = this.astFactory.create(this.LT(1));
        this.match(95);
        this.match(102);
        AST tmp22_AST = null;
        tmp22_AST = this.astFactory.create(this.LT(1));
        this.match(67);
        this.annotationBlock();
        ab_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            annotationDefinition_AST = currentAST.root;
            currentAST.root = annotationDefinition_AST = this.astFactory.make(new ASTArray(4).add(this.create(48, "ANNOTATION_DEF", first, this.LT(1))).add(modifiers).add(tmp22_AST).add(ab_AST));
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
        this.modifiers();
        m_AST = this.returnAST;
        this.typeSpec(false);
        t_AST = this.returnAST;
        this.variableDefinitions(m_AST, t_AST);
        v_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            declaration_AST = currentAST.root;
            currentAST.root = declaration_AST = v_AST;
            currentAST.child = declaration_AST != null && declaration_AST.getFirstChild() != null ? declaration_AST.getFirstChild() : declaration_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = declaration_AST;
    }

    public final void typeSpec(boolean addImagNode) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeSpec_AST = null;
        switch (this.LA(1)) {
            case 67: {
                this.classTypeSpec(addImagNode);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeSpec_AST = currentAST.root;
                break;
            }
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
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
        this.variableDeclarator(this.getASTFactory().dupTree(mods), this.getASTFactory().dupTree(t));
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 74) {
            this.match(74);
            this.variableDeclarator(this.getASTFactory().dupTree(mods), this.getASTFactory().dupTree(t));
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = variableDefinitions_AST = currentAST.root;
    }

    public final void classTypeSpec(boolean addImagNode) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST classTypeSpec_AST = null;
        Token lb = null;
        AST lb_AST = null;
        Token first = this.LT(1);
        this.classOrInterfaceType(false);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 65 && this.LA(2) == 66) {
            lb = this.LT(1);
            lb_AST = this.astFactory.create(lb);
            this.astFactory.makeASTRoot(currentAST, lb_AST);
            this.match(65);
            if (this.inputState.guessing == 0) {
                lb_AST.setType(16);
            }
            this.match(66);
        }
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
        Token lb = null;
        AST lb_AST = null;
        Token first = this.LT(1);
        this.builtInType();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 65) {
            lb = this.LT(1);
            lb_AST = this.astFactory.create(lb);
            this.astFactory.makeASTRoot(currentAST, lb_AST);
            this.match(65);
            if (this.inputState.guessing == 0) {
                lb_AST.setType(16);
            }
            this.match(66);
        }
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
        Token first = this.LT(1);
        AST tmp26_AST = null;
        tmp26_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp26_AST);
        this.match(67);
        if (this.LA(1) == 72 && _tokenSet_5.member(this.LA(2))) {
            this.typeArguments();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) == 72 && this.LA(2) == 73) {
            this.typeArgumentsDiamond();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_6.member(this.LA(1))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        block4: while (this.LA(1) == 68 && this.LA(2) == 67) {
            AST tmp27_AST = null;
            tmp27_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp27_AST);
            this.match(68);
            AST tmp28_AST = null;
            tmp28_AST = this.astFactory.create(this.LT(1));
            this.astFactory.addASTChild(currentAST, tmp28_AST);
            this.match(67);
            switch (this.LA(1)) {
                case 72: {
                    this.typeArguments();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 62: 
                case 65: 
                case 66: 
                case 67: 
                case 68: 
                case 69: 
                case 70: 
                case 71: 
                case 73: 
                case 74: 
                case 75: 
                case 76: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 96: 
                case 97: 
                case 98: 
                case 99: 
                case 100: 
                case 104: 
                case 106: 
                case 107: 
                case 109: 
                case 110: 
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
                case 142: {
                    continue block4;
                }
            }
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            classOrInterfaceType_AST = currentAST.root;
            if (addImagNode) {
                classOrInterfaceType_AST = this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(classOrInterfaceType_AST));
            }
            currentAST.root = classOrInterfaceType_AST;
            currentAST.child = classOrInterfaceType_AST != null && classOrInterfaceType_AST.getFirstChild() != null ? classOrInterfaceType_AST.getFirstChild() : classOrInterfaceType_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = classOrInterfaceType_AST = currentAST.root;
    }

    public final void typeArguments() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArguments_AST = null;
        int currentLtLevel = 0;
        Token first = this.LT(1);
        if (this.inputState.guessing == 0) {
            currentLtLevel = this.ltCounter;
        }
        this.match(72);
        if (this.inputState.guessing == 0) {
            ++this.ltCounter;
        }
        this.typeArgument();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 74 && _tokenSet_5.member(this.LA(2)) && (this.inputState.guessing != 0 || this.ltCounter == currentLtLevel + 1)) {
            this.match(74);
            this.typeArgument();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (_tokenSet_7.member(this.LA(1)) && _tokenSet_6.member(this.LA(2))) {
            this.typeArgumentsOrParametersEnd();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_6.member(this.LA(1)) || !_tokenSet_8.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (currentLtLevel == 0 && this.ltCounter != currentLtLevel) {
            throw new SemanticException("(currentLtLevel != 0) || ltCounter == currentLtLevel");
        }
        if (this.inputState.guessing == 0) {
            typeArguments_AST = currentAST.root;
            currentAST.root = typeArguments_AST = this.astFactory.make(new ASTArray(2).add(this.create(54, "TYPE_ARGUMENTS", first, this.LT(1))).add(typeArguments_AST));
            currentAST.child = typeArguments_AST != null && typeArguments_AST.getFirstChild() != null ? typeArguments_AST.getFirstChild() : typeArguments_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeArguments_AST = currentAST.root;
    }

    public final void typeArgumentsDiamond() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArgumentsDiamond_AST = null;
        Token first = this.LT(1);
        this.match(72);
        this.match(73);
        if (this.inputState.guessing == 0) {
            typeArgumentsDiamond_AST = currentAST.root;
            currentAST.root = typeArgumentsDiamond_AST = this.astFactory.make(new ASTArray(2).add(this.create(54, "TYPE_ARGUMENTS", first, this.LT(1))).add(typeArgumentsDiamond_AST));
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
            case 67: {
                this.classTypeSpec(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                typeArgumentSpec_AST = currentAST.root;
                break;
            }
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
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
        Token lb = null;
        AST lb_AST = null;
        Token first = this.LT(1);
        this.builtInType();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        int _cnt41 = 0;
        while (true) {
            if (this.LA(1) == 65 && this.LA(2) == 66) {
                lb = this.LT(1);
                lb_AST = this.astFactory.create(lb);
                this.astFactory.makeASTRoot(currentAST, lb_AST);
                this.match(65);
                if (this.inputState.guessing == 0) {
                    lb_AST.setType(16);
                }
            } else {
                if (_cnt41 >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.match(66);
            ++_cnt41;
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
            case 67: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
                this.typeArgumentSpec();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 69: {
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
            currentAST.root = typeArgument_AST = this.astFactory.make(new ASTArray(2).add(this.create(55, "TYPE_ARGUMENT", first, this.LT(1))).add(typeArgument_AST));
            currentAST.child = typeArgument_AST != null && typeArgument_AST.getFirstChild() != null ? typeArgument_AST.getFirstChild() : typeArgument_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeArgument_AST = currentAST.root;
    }

    public final void wildcardType() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST wildcardType_AST = null;
        Token q = null;
        AST q_AST = null;
        q = this.LT(1);
        q_AST = this.astFactory.create(q);
        this.astFactory.makeASTRoot(currentAST, q_AST);
        this.match(69);
        if (this.inputState.guessing == 0) {
            q_AST.setType(58);
        }
        boolean synPredMatched30 = false;
        if ((this.LA(1) == 70 || this.LA(1) == 71) && this.LA(2) == 67) {
            int _m30 = this.mark();
            synPredMatched30 = true;
            ++this.inputState.guessing;
            try {
                switch (this.LA(1)) {
                    case 70: {
                        this.match(70);
                        break;
                    }
                    case 71: {
                        this.match(71);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException pe) {
                synPredMatched30 = false;
            }
            this.rewind(_m30);
            --this.inputState.guessing;
        }
        if (synPredMatched30) {
            this.typeArgumentBounds();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_6.member(this.LA(1)) || !_tokenSet_8.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = wildcardType_AST = currentAST.root;
    }

    public final void typeArgumentBounds() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeArgumentBounds_AST = null;
        boolean isUpperBounds = false;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 70: {
                this.match(70);
                if (this.inputState.guessing != 0) break;
                isUpperBounds = true;
                break;
            }
            case 71: {
                this.match(71);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.classOrInterfaceType(false);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            typeArgumentBounds_AST = currentAST.root;
            typeArgumentBounds_AST = isUpperBounds ? this.astFactory.make(new ASTArray(2).add(this.create(59, "TYPE_UPPER_BOUNDS", first, this.LT(1))).add(typeArgumentBounds_AST)) : this.astFactory.make(new ASTArray(2).add(this.create(60, "TYPE_LOWER_BOUNDS", first, this.LT(1))).add(typeArgumentBounds_AST));
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
            case 73: {
                this.match(73);
                if (this.inputState.guessing == 0) {
                    --this.ltCounter;
                }
                typeArgumentsOrParametersEnd_AST = currentAST.root;
                break;
            }
            case 75: {
                this.match(75);
                if (this.inputState.guessing == 0) {
                    this.ltCounter -= 2;
                }
                typeArgumentsOrParametersEnd_AST = currentAST.root;
                break;
            }
            case 76: {
                this.match(76);
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

    public final void builtInType() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST builtInType_AST = null;
        switch (this.LA(1)) {
            case 77: {
                AST tmp39_AST = null;
                tmp39_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp39_AST);
                this.match(77);
                builtInType_AST = currentAST.root;
                break;
            }
            case 78: {
                AST tmp40_AST = null;
                tmp40_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp40_AST);
                this.match(78);
                builtInType_AST = currentAST.root;
                break;
            }
            case 79: {
                AST tmp41_AST = null;
                tmp41_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp41_AST);
                this.match(79);
                builtInType_AST = currentAST.root;
                break;
            }
            case 80: {
                AST tmp42_AST = null;
                tmp42_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp42_AST);
                this.match(80);
                builtInType_AST = currentAST.root;
                break;
            }
            case 81: {
                AST tmp43_AST = null;
                tmp43_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp43_AST);
                this.match(81);
                builtInType_AST = currentAST.root;
                break;
            }
            case 82: {
                AST tmp44_AST = null;
                tmp44_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp44_AST);
                this.match(82);
                builtInType_AST = currentAST.root;
                break;
            }
            case 83: {
                AST tmp45_AST = null;
                tmp45_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp45_AST);
                this.match(83);
                builtInType_AST = currentAST.root;
                break;
            }
            case 84: {
                AST tmp46_AST = null;
                tmp46_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp46_AST);
                this.match(84);
                builtInType_AST = currentAST.root;
                break;
            }
            case 85: {
                AST tmp47_AST = null;
                tmp47_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp47_AST);
                this.match(85);
                builtInType_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = builtInType_AST;
    }

    public final void type() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST type_AST = null;
        switch (this.LA(1)) {
            case 67: {
                this.classOrInterfaceType(false);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                type_AST = currentAST.root;
                break;
            }
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
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

    public final void modifier() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST modifier_AST = null;
        switch (this.LA(1)) {
            case 87: {
                AST tmp48_AST = null;
                tmp48_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp48_AST);
                this.match(87);
                modifier_AST = currentAST.root;
                break;
            }
            case 88: {
                AST tmp49_AST = null;
                tmp49_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp49_AST);
                this.match(88);
                modifier_AST = currentAST.root;
                break;
            }
            case 89: {
                AST tmp50_AST = null;
                tmp50_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp50_AST);
                this.match(89);
                modifier_AST = currentAST.root;
                break;
            }
            case 64: {
                AST tmp51_AST = null;
                tmp51_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp51_AST);
                this.match(64);
                modifier_AST = currentAST.root;
                break;
            }
            case 90: {
                AST tmp52_AST = null;
                tmp52_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp52_AST);
                this.match(90);
                modifier_AST = currentAST.root;
                break;
            }
            case 38: {
                AST tmp53_AST = null;
                tmp53_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp53_AST);
                this.match(38);
                modifier_AST = currentAST.root;
                break;
            }
            case 39: {
                AST tmp54_AST = null;
                tmp54_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp54_AST);
                this.match(39);
                modifier_AST = currentAST.root;
                break;
            }
            case 91: {
                AST tmp55_AST = null;
                tmp55_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp55_AST);
                this.match(91);
                modifier_AST = currentAST.root;
                break;
            }
            case 92: {
                AST tmp56_AST = null;
                tmp56_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp56_AST);
                this.match(92);
                modifier_AST = currentAST.root;
                break;
            }
            case 93: {
                AST tmp57_AST = null;
                tmp57_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp57_AST);
                this.match(93);
                modifier_AST = currentAST.root;
                break;
            }
            case 94: {
                AST tmp58_AST = null;
                tmp58_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp58_AST);
                this.match(94);
                modifier_AST = currentAST.root;
                break;
            }
            case 40: {
                AST tmp59_AST = null;
                tmp59_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp59_AST);
                this.match(40);
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
        this.match(95);
        this.identifier();
        i_AST = this.returnAST;
        switch (this.LA(1)) {
            case 96: {
                this.match(96);
                switch (this.LA(1)) {
                    case 67: 
                    case 71: 
                    case 72: 
                    case 77: 
                    case 78: 
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 95: 
                    case 96: 
                    case 99: 
                    case 107: 
                    case 147: 
                    case 148: 
                    case 151: 
                    case 152: 
                    case 153: 
                    case 154: 
                    case 155: 
                    case 156: 
                    case 157: 
                    case 158: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 162: 
                    case 163: 
                    case 164: {
                        this.annotationArguments();
                        args_AST = this.returnAST;
                        break;
                    }
                    case 97: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(97);
                break;
            }
            case 38: 
            case 39: 
            case 40: 
            case 61: 
            case 62: 
            case 64: 
            case 67: 
            case 72: 
            case 74: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 97: 
            case 100: 
            case 101: 
            case 102: 
            case 103: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            annotation_AST = currentAST.root;
            currentAST.root = annotation_AST = this.astFactory.make(new ASTArray(3).add(this.create(50, "ANNOTATION", first, this.LT(1))).add(i_AST).add(args_AST));
            currentAST.child = annotation_AST != null && annotation_AST.getFirstChild() != null ? annotation_AST.getFirstChild() : annotation_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotation_AST;
    }

    public final void annotationArguments() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationArguments_AST = null;
        if (_tokenSet_9.member(this.LA(1)) && _tokenSet_10.member(this.LA(2))) {
            this.annotationMemberValueInitializer();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            annotationArguments_AST = currentAST.root;
        } else if (this.LA(1) == 67 && this.LA(2) == 98) {
            this.anntotationMemberValuePairs();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            annotationArguments_AST = currentAST.root;
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.returnAST = annotationArguments_AST;
    }

    public final void annotationMemberValueInitializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationMemberValueInitializer_AST = null;
        switch (this.LA(1)) {
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.conditionalExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                annotationMemberValueInitializer_AST = currentAST.root;
                break;
            }
            case 95: {
                this.annotation();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                annotationMemberValueInitializer_AST = currentAST.root;
                break;
            }
            case 99: {
                this.annotationMemberArrayInitializer();
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

    public final void anntotationMemberValuePairs() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST anntotationMemberValuePairs_AST = null;
        this.annotationMemberValuePair();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 74) {
            this.match(74);
            this.annotationMemberValuePair();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = anntotationMemberValuePairs_AST = currentAST.root;
    }

    public final void annotationMemberValuePair() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationMemberValuePair_AST = null;
        Token i = null;
        AST i_AST = null;
        AST v_AST = null;
        Token first = this.LT(1);
        i = this.LT(1);
        i_AST = this.astFactory.create(i);
        this.match(67);
        this.match(98);
        this.annotationMemberValueInitializer();
        v_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            annotationMemberValuePair_AST = currentAST.root;
            currentAST.root = annotationMemberValuePair_AST = this.astFactory.make(new ASTArray(3).add(this.create(51, "ANNOTATION_MEMBER_VALUE_PAIR", first, this.LT(1))).add(i_AST).add(v_AST));
            currentAST.child = annotationMemberValuePair_AST != null && annotationMemberValuePair_AST.getFirstChild() != null ? annotationMemberValuePair_AST.getFirstChild() : annotationMemberValuePair_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = annotationMemberValuePair_AST;
    }

    public final void conditionalExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST conditionalExpression_AST = null;
        this.logicalOrExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        switch (this.LA(1)) {
            case 69: {
                AST tmp65_AST = null;
                tmp65_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp65_AST);
                this.match(69);
                this.assignmentExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(110);
                this.conditionalExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 62: 
            case 66: 
            case 74: 
            case 97: 
            case 98: 
            case 100: 
            case 110: 
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
            case 136: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = conditionalExpression_AST = currentAST.root;
    }

    public final void annotationMemberArrayInitializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationMemberArrayInitializer_AST = null;
        Token lc = null;
        AST lc_AST = null;
        lc = this.LT(1);
        lc_AST = this.astFactory.create(lc);
        this.astFactory.makeASTRoot(currentAST, lc_AST);
        this.match(99);
        if (this.inputState.guessing == 0) {
            lc_AST.setType(53);
        }
        block0 : switch (this.LA(1)) {
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 95: 
            case 96: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.annotationMemberArrayValueInitializer();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 74 && _tokenSet_11.member(this.LA(2))) {
                    this.match(74);
                    this.annotationMemberArrayValueInitializer();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                switch (this.LA(1)) {
                    case 74: {
                        this.match(74);
                        break block0;
                    }
                    case 100: {
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 100: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(100);
        this.returnAST = annotationMemberArrayInitializer_AST = currentAST.root;
    }

    public final void annotationMemberArrayValueInitializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationMemberArrayValueInitializer_AST = null;
        switch (this.LA(1)) {
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.conditionalExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                annotationMemberArrayValueInitializer_AST = currentAST.root;
                break;
            }
            case 95: {
                this.annotation();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                annotationMemberArrayValueInitializer_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = annotationMemberArrayValueInitializer_AST;
    }

    public final void superClassClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST superClassClause_AST = null;
        AST c_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 70: {
                this.match(70);
                this.classOrInterfaceType(false);
                c_AST = this.returnAST;
                break;
            }
            case 99: 
            case 106: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            superClassClause_AST = currentAST.root;
            currentAST.root = superClassClause_AST = this.astFactory.make(new ASTArray(2).add(this.create(17, "EXTENDS_CLAUSE", first, this.LT(1))).add(c_AST));
            currentAST.child = superClassClause_AST != null && superClassClause_AST.getFirstChild() != null ? superClassClause_AST.getFirstChild() : superClassClause_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = superClassClause_AST;
    }

    public final void typeParameters() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST typeParameters_AST = null;
        int currentLtLevel = 0;
        Token first = this.LT(1);
        if (this.inputState.guessing == 0) {
            currentLtLevel = this.ltCounter;
        }
        this.match(72);
        if (this.inputState.guessing == 0) {
            ++this.ltCounter;
        }
        this.typeParameter();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 74) {
            this.match(74);
            this.typeParameter();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        switch (this.LA(1)) {
            case 73: 
            case 75: 
            case 76: {
                this.typeArgumentsOrParametersEnd();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 67: 
            case 70: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 99: 
            case 106: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (currentLtLevel == 0 && this.ltCounter != currentLtLevel) {
            throw new SemanticException("(currentLtLevel != 0) || ltCounter == currentLtLevel");
        }
        if (this.inputState.guessing == 0) {
            typeParameters_AST = currentAST.root;
            currentAST.root = typeParameters_AST = this.astFactory.make(new ASTArray(2).add(this.create(56, "TYPE_PARAMETERS", first, this.LT(1))).add(typeParameters_AST));
            currentAST.child = typeParameters_AST != null && typeParameters_AST.getFirstChild() != null ? typeParameters_AST.getFirstChild() : typeParameters_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeParameters_AST = currentAST.root;
    }

    public final void implementsClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST implementsClause_AST = null;
        Token i = null;
        AST i_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 106: {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.match(106);
                this.classOrInterfaceType(false);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 74) {
                    this.match(74);
                    this.classOrInterfaceType(false);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                break;
            }
            case 99: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            implementsClause_AST = currentAST.root;
            currentAST.root = implementsClause_AST = this.astFactory.make(new ASTArray(2).add(this.create(18, "IMPLEMENTS_CLAUSE", first, this.LT(1))).add(implementsClause_AST));
            currentAST.child = implementsClause_AST != null && implementsClause_AST.getFirstChild() != null ? implementsClause_AST.getFirstChild() : implementsClause_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = implementsClause_AST = currentAST.root;
    }

    public final void classBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST classBlock_AST = null;
        this.match(99);
        block4: while (true) {
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 40: 
                case 64: 
                case 67: 
                case 72: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 99: 
                case 101: 
                case 102: 
                case 103: {
                    this.classField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 62: {
                    this.match(62);
                    continue block4;
                }
            }
            break;
        }
        this.match(100);
        if (this.inputState.guessing == 0) {
            classBlock_AST = currentAST.root;
            currentAST.root = classBlock_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(6, "OBJBLOCK")).add(classBlock_AST));
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
            case 70: {
                e = this.LT(1);
                e_AST = this.astFactory.create(e);
                this.match(70);
                this.classOrInterfaceType(false);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 74) {
                    this.match(74);
                    this.classOrInterfaceType(false);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                break;
            }
            case 99: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            interfaceExtends_AST = currentAST.root;
            currentAST.root = interfaceExtends_AST = this.astFactory.make(new ASTArray(2).add(this.create(17, "EXTENDS_CLAUSE", first, this.LT(1))).add(interfaceExtends_AST));
            currentAST.child = interfaceExtends_AST != null && interfaceExtends_AST.getFirstChild() != null ? interfaceExtends_AST.getFirstChild() : interfaceExtends_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = interfaceExtends_AST = currentAST.root;
    }

    public final void interfaceBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST interfaceBlock_AST = null;
        this.match(99);
        block4: while (true) {
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 40: 
                case 64: 
                case 67: 
                case 72: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 101: 
                case 102: 
                case 103: {
                    this.interfaceField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 62: {
                    this.match(62);
                    continue block4;
                }
            }
            break;
        }
        this.match(100);
        if (this.inputState.guessing == 0) {
            interfaceBlock_AST = currentAST.root;
            currentAST.root = interfaceBlock_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(6, "OBJBLOCK")).add(interfaceBlock_AST));
            currentAST.child = interfaceBlock_AST != null && interfaceBlock_AST.getFirstChild() != null ? interfaceBlock_AST.getFirstChild() : interfaceBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = interfaceBlock_AST = currentAST.root;
    }

    public final void enumBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumBlock_AST = null;
        this.match(99);
        block0 : switch (this.LA(1)) {
            case 67: 
            case 95: {
                this.enumConstant();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 74 && (this.LA(2) == 67 || this.LA(2) == 95)) {
                    this.match(74);
                    this.enumConstant();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                switch (this.LA(1)) {
                    case 74: {
                        this.match(74);
                        break block0;
                    }
                    case 62: 
                    case 100: {
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 62: 
            case 100: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 62: {
                this.match(62);
                block17: while (true) {
                    switch (this.LA(1)) {
                        case 38: 
                        case 39: 
                        case 40: 
                        case 64: 
                        case 67: 
                        case 72: 
                        case 77: 
                        case 78: 
                        case 79: 
                        case 80: 
                        case 81: 
                        case 82: 
                        case 83: 
                        case 84: 
                        case 85: 
                        case 87: 
                        case 88: 
                        case 89: 
                        case 90: 
                        case 91: 
                        case 92: 
                        case 93: 
                        case 94: 
                        case 95: 
                        case 99: 
                        case 101: 
                        case 102: 
                        case 103: {
                            this.classField();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            continue block17;
                        }
                        case 62: {
                            this.match(62);
                            continue block17;
                        }
                    }
                    break;
                }
                break;
            }
            case 100: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(100);
        if (this.inputState.guessing == 0) {
            enumBlock_AST = currentAST.root;
            currentAST.root = enumBlock_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(6, "OBJBLOCK")).add(enumBlock_AST));
            currentAST.child = enumBlock_AST != null && enumBlock_AST.getFirstChild() != null ? enumBlock_AST.getFirstChild() : enumBlock_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = enumBlock_AST = currentAST.root;
    }

    public final void annotationBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST annotationBlock_AST = null;
        this.match(99);
        block4: while (true) {
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 40: 
                case 64: 
                case 67: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 101: 
                case 102: 
                case 103: {
                    this.annotationField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 62: {
                    this.match(62);
                    continue block4;
                }
            }
            break;
        }
        this.match(100);
        if (this.inputState.guessing == 0) {
            annotationBlock_AST = currentAST.root;
            currentAST.root = annotationBlock_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(6, "OBJBLOCK")).add(annotationBlock_AST));
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
        this.match(67);
        if (this.LA(1) == 70 && this.LA(2) == 67) {
            this.typeParameterBounds();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_12.member(this.LA(1)) || !_tokenSet_13.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            typeParameter_AST = currentAST.root;
            currentAST.root = typeParameter_AST = this.astFactory.make(new ASTArray(2).add(this.create(57, "TYPE_PARAMETER", first, this.LT(1))).add(typeParameter_AST));
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
        this.match(70);
        this.classOrInterfaceType(false);
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 104) {
            this.match(104);
            this.classOrInterfaceType(false);
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            typeParameterBounds_AST = currentAST.root;
            currentAST.root = typeParameterBounds_AST = this.astFactory.make(new ASTArray(2).add(this.create(59, "TYPE_UPPER_BOUNDS", first, this.LT(1))).add(typeParameterBounds_AST));
            currentAST.child = typeParameterBounds_AST != null && typeParameterBounds_AST.getFirstChild() != null ? typeParameterBounds_AST.getFirstChild() : typeParameterBounds_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = typeParameterBounds_AST = currentAST.root;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final void classField() throws RecognitionException, TokenStreamException {
        AST classField_AST;
        block29: {
            Token first;
            AST s4_AST;
            AST s3_AST;
            ASTPair currentAST;
            block28: {
                this.returnAST = null;
                currentAST = new ASTPair();
                classField_AST = null;
                AST mods_AST = null;
                AST td_AST = null;
                AST tp_AST = null;
                AST h_AST = null;
                AST s_AST = null;
                AST t_AST = null;
                AST param_AST = null;
                AST rt_AST = null;
                AST tc_AST = null;
                AST s2_AST = null;
                AST v_AST = null;
                s3_AST = null;
                s4_AST = null;
                first = this.LT(1);
                if (!_tokenSet_14.member(this.LA(1)) || !_tokenSet_15.member(this.LA(2))) break block28;
                this.modifiers();
                mods_AST = this.returnAST;
                switch (this.LA(1)) {
                    case 95: 
                    case 101: 
                    case 102: 
                    case 103: {
                        this.typeDefinitionInternal(mods_AST);
                        td_AST = this.returnAST;
                        if (this.inputState.guessing == 0) {
                            classField_AST = currentAST.root;
                            currentAST.root = classField_AST = td_AST;
                            currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                            currentAST.advanceChildToEnd();
                        }
                        break block29;
                    }
                    case 67: 
                    case 72: 
                    case 77: 
                    case 78: 
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: {
                        switch (this.LA(1)) {
                            case 72: {
                                this.typeParameters();
                                tp_AST = this.returnAST;
                                break;
                            }
                            case 67: 
                            case 77: 
                            case 78: 
                            case 79: 
                            case 80: 
                            case 81: 
                            case 82: 
                            case 83: 
                            case 84: 
                            case 85: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        if (this.LA(1) == 67 && this.LA(2) == 96) {
                            this.ctorHead();
                            h_AST = this.returnAST;
                            this.constructorBody();
                            s_AST = this.returnAST;
                            if (this.inputState.guessing == 0) {
                                classField_AST = currentAST.root;
                                currentAST.root = classField_AST = this.astFactory.make(new ASTArray(5).add(this.create(8, "METHOD_DEF", first, this.LT(1))).add(mods_AST).add(tp_AST).add(h_AST).add(s_AST));
                                currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                                currentAST.advanceChildToEnd();
                            }
                        } else {
                            if (!_tokenSet_16.member(this.LA(1)) || !_tokenSet_17.member(this.LA(2))) throw new NoViableAltException(this.LT(1), this.getFilename());
                            this.typeSpec(false);
                            t_AST = this.returnAST;
                            if (this.LA(1) == 67 && this.LA(2) == 96) {
                                AST tmp92_AST = null;
                                tmp92_AST = this.astFactory.create(this.LT(1));
                                this.match(67);
                                this.match(96);
                                this.parameterDeclarationList();
                                param_AST = this.returnAST;
                                this.match(97);
                                this.declaratorBrackets(t_AST);
                                rt_AST = this.returnAST;
                                switch (this.LA(1)) {
                                    case 108: {
                                        this.throwsClause();
                                        tc_AST = this.returnAST;
                                        break;
                                    }
                                    case 62: 
                                    case 99: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                switch (this.LA(1)) {
                                    case 99: {
                                        this.compoundStatement();
                                        s2_AST = this.returnAST;
                                        break;
                                    }
                                    case 62: {
                                        AST tmp95_AST = null;
                                        tmp95_AST = this.astFactory.create(this.LT(1));
                                        this.match(62);
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                if (this.inputState.guessing == 0) {
                                    classField_AST = currentAST.root;
                                    currentAST.root = classField_AST = this.astFactory.make(new ASTArray(8).add(this.create(8, "METHOD_DEF", first, this.LT(1))).add(mods_AST).add(tp_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(rt_AST))).add(tmp92_AST).add(param_AST).add(tc_AST).add(s2_AST));
                                    currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                                    currentAST.advanceChildToEnd();
                                }
                            } else {
                                if (this.LA(1) != 67 || !_tokenSet_18.member(this.LA(2))) throw new NoViableAltException(this.LT(1), this.getFilename());
                                this.variableDefinitions(mods_AST, t_AST);
                                v_AST = this.returnAST;
                                AST tmp96_AST = null;
                                tmp96_AST = this.astFactory.create(this.LT(1));
                                this.match(62);
                                if (this.inputState.guessing == 0) {
                                    classField_AST = currentAST.root;
                                    currentAST.root = classField_AST = v_AST;
                                    currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                                    currentAST.advanceChildToEnd();
                                }
                            }
                        }
                        break block29;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            if (this.LA(1) == 64 && this.LA(2) == 99) {
                this.match(64);
                this.compoundStatement();
                s3_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    classField_AST = currentAST.root;
                    currentAST.root = classField_AST = this.astFactory.make(new ASTArray(2).add(this.create(11, "STATIC_INIT", first, this.LT(1))).add(s3_AST));
                    currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                    currentAST.advanceChildToEnd();
                }
            } else {
                if (this.LA(1) != 99) throw new NoViableAltException(this.LT(1), this.getFilename());
                this.compoundStatement();
                s4_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    classField_AST = currentAST.root;
                    currentAST.root = classField_AST = this.astFactory.make(new ASTArray(2).add(this.create(10, "INSTANCE_INIT", first, this.LT(1))).add(s4_AST));
                    currentAST.child = classField_AST != null && classField_AST.getFirstChild() != null ? classField_AST.getFirstChild() : classField_AST;
                    currentAST.advanceChildToEnd();
                }
            }
        }
        this.returnAST = classField_AST;
    }

    public final void interfaceField() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST interfaceField_AST = null;
        AST mods_AST = null;
        AST td_AST = null;
        AST tp_AST = null;
        AST t_AST = null;
        AST param_AST = null;
        AST rt_AST = null;
        AST tc_AST = null;
        AST v_AST = null;
        Token first = this.LT(1);
        this.modifiers();
        mods_AST = this.returnAST;
        switch (this.LA(1)) {
            case 95: 
            case 101: 
            case 102: 
            case 103: {
                this.typeDefinitionInternal(mods_AST);
                td_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                interfaceField_AST = currentAST.root;
                currentAST.root = interfaceField_AST = td_AST;
                currentAST.child = interfaceField_AST != null && interfaceField_AST.getFirstChild() != null ? interfaceField_AST.getFirstChild() : interfaceField_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            case 67: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
                switch (this.LA(1)) {
                    case 72: {
                        this.typeParameters();
                        tp_AST = this.returnAST;
                        break;
                    }
                    case 67: 
                    case 77: 
                    case 78: 
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.typeSpec(false);
                t_AST = this.returnAST;
                if (this.LA(1) == 67 && this.LA(2) == 96) {
                    AST tmp98_AST = null;
                    tmp98_AST = this.astFactory.create(this.LT(1));
                    this.match(67);
                    this.match(96);
                    this.parameterDeclarationList();
                    param_AST = this.returnAST;
                    this.match(97);
                    this.declaratorBrackets(t_AST);
                    rt_AST = this.returnAST;
                    switch (this.LA(1)) {
                        case 108: {
                            this.throwsClause();
                            tc_AST = this.returnAST;
                            break;
                        }
                        case 62: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    AST tmp101_AST = null;
                    tmp101_AST = this.astFactory.create(this.LT(1));
                    this.match(62);
                    if (this.inputState.guessing != 0) break;
                    interfaceField_AST = currentAST.root;
                    currentAST.root = interfaceField_AST = this.astFactory.make(new ASTArray(7).add(this.create(8, "METHOD_DEF", first, this.LT(1))).add(mods_AST).add(tp_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(rt_AST))).add(tmp98_AST).add(param_AST).add(tc_AST));
                    currentAST.child = interfaceField_AST != null && interfaceField_AST.getFirstChild() != null ? interfaceField_AST.getFirstChild() : interfaceField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                if (this.LA(1) == 67 && _tokenSet_18.member(this.LA(2))) {
                    this.variableDefinitions(mods_AST, t_AST);
                    v_AST = this.returnAST;
                    AST tmp102_AST = null;
                    tmp102_AST = this.astFactory.create(this.LT(1));
                    this.match(62);
                    if (this.inputState.guessing != 0) break;
                    interfaceField_AST = currentAST.root;
                    currentAST.root = interfaceField_AST = v_AST;
                    currentAST.child = interfaceField_AST != null && interfaceField_AST.getFirstChild() != null ? interfaceField_AST.getFirstChild() : interfaceField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
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
        AST rt_AST = null;
        AST amvi_AST = null;
        AST v_AST = null;
        Token first = this.LT(1);
        this.modifiers();
        mods_AST = this.returnAST;
        switch (this.LA(1)) {
            case 95: 
            case 101: 
            case 102: 
            case 103: {
                this.typeDefinitionInternal(mods_AST);
                td_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                annotationField_AST = currentAST.root;
                currentAST.root = annotationField_AST = td_AST;
                currentAST.child = annotationField_AST != null && annotationField_AST.getFirstChild() != null ? annotationField_AST.getFirstChild() : annotationField_AST;
                currentAST.advanceChildToEnd();
                break;
            }
            case 67: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
                this.typeSpec(false);
                t_AST = this.returnAST;
                if (this.LA(1) == 67 && this.LA(2) == 96) {
                    i = this.LT(1);
                    i_AST = this.astFactory.create(i);
                    this.match(67);
                    this.match(96);
                    this.match(97);
                    this.declaratorBrackets(t_AST);
                    rt_AST = this.returnAST;
                    switch (this.LA(1)) {
                        case 105: {
                            this.match(105);
                            this.annotationMemberValueInitializer();
                            amvi_AST = this.returnAST;
                            break;
                        }
                        case 62: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    AST tmp106_AST = null;
                    tmp106_AST = this.astFactory.create(this.LT(1));
                    this.match(62);
                    if (this.inputState.guessing != 0) break;
                    annotationField_AST = currentAST.root;
                    currentAST.root = annotationField_AST = this.astFactory.make(new ASTArray(5).add(this.create(52, "ANNOTATION_FIELD_DEF", first, this.LT(1))).add(mods_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(rt_AST))).add(i_AST).add(amvi_AST));
                    currentAST.child = annotationField_AST != null && annotationField_AST.getFirstChild() != null ? annotationField_AST.getFirstChild() : annotationField_AST;
                    currentAST.advanceChildToEnd();
                    break;
                }
                if (this.LA(1) == 67 && _tokenSet_18.member(this.LA(2))) {
                    this.variableDefinitions(mods_AST, t_AST);
                    v_AST = this.returnAST;
                    AST tmp107_AST = null;
                    tmp107_AST = this.astFactory.create(this.LT(1));
                    this.match(62);
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

    public final void enumConstant() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumConstant_AST = null;
        AST an_AST = null;
        Token i = null;
        AST i_AST = null;
        AST a_AST = null;
        AST b_AST = null;
        this.annotations();
        an_AST = this.returnAST;
        i = this.LT(1);
        i_AST = this.astFactory.create(i);
        this.match(67);
        switch (this.LA(1)) {
            case 96: {
                this.match(96);
                this.argList();
                a_AST = this.returnAST;
                this.match(97);
                break;
            }
            case 62: 
            case 74: 
            case 99: 
            case 100: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 99: {
                this.enumConstantBlock();
                b_AST = this.returnAST;
                break;
            }
            case 62: 
            case 74: 
            case 100: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            enumConstant_AST = currentAST.root;
            currentAST.root = enumConstant_AST = this.astFactory.make(new ASTArray(5).add(this.astFactory.create(46, "ENUM_CONSTANT_DEF")).add(an_AST).add(i_AST).add(a_AST).add(b_AST));
            currentAST.child = enumConstant_AST != null && enumConstant_AST.getFirstChild() != null ? enumConstant_AST.getFirstChild() : enumConstant_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = enumConstant_AST;
    }

    public final void declaratorBrackets(AST typ) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST declaratorBrackets_AST = null;
        Token lb = null;
        AST lb_AST = null;
        if (this.inputState.guessing == 0) {
            declaratorBrackets_AST = currentAST.root;
            currentAST.root = declaratorBrackets_AST = typ;
            currentAST.child = declaratorBrackets_AST != null && declaratorBrackets_AST.getFirstChild() != null ? declaratorBrackets_AST.getFirstChild() : declaratorBrackets_AST;
            currentAST.advanceChildToEnd();
        }
        while (this.LA(1) == 65) {
            lb = this.LT(1);
            lb_AST = this.astFactory.create(lb);
            this.astFactory.makeASTRoot(currentAST, lb_AST);
            this.match(65);
            if (this.inputState.guessing == 0) {
                lb_AST.setType(16);
            }
            this.match(66);
        }
        this.returnAST = declaratorBrackets_AST = currentAST.root;
    }

    public final void argList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST argList_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.expressionList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 97: {
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
        this.returnAST = argList_AST = currentAST.root;
    }

    public final void enumConstantBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST enumConstantBlock_AST = null;
        this.match(99);
        block4: while (true) {
            switch (this.LA(1)) {
                case 38: 
                case 39: 
                case 40: 
                case 64: 
                case 67: 
                case 72: 
                case 77: 
                case 78: 
                case 79: 
                case 80: 
                case 81: 
                case 82: 
                case 83: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 90: 
                case 91: 
                case 92: 
                case 93: 
                case 94: 
                case 95: 
                case 99: 
                case 101: 
                case 102: 
                case 103: {
                    this.enumConstantField();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    continue block4;
                }
                case 62: {
                    this.match(62);
                    continue block4;
                }
            }
            break;
        }
        this.match(100);
        if (this.inputState.guessing == 0) {
            enumConstantBlock_AST = currentAST.root;
            currentAST.root = enumConstantBlock_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(6, "OBJBLOCK")).add(enumConstantBlock_AST));
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
        AST tp_AST = null;
        AST t_AST = null;
        AST param_AST = null;
        AST rt_AST = null;
        AST tc_AST = null;
        AST s2_AST = null;
        AST v_AST = null;
        AST s4_AST = null;
        Token first = this.LT(1);
        block0 : switch (this.LA(1)) {
            case 38: 
            case 39: 
            case 40: 
            case 64: 
            case 67: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 101: 
            case 102: 
            case 103: {
                this.modifiers();
                mods_AST = this.returnAST;
                switch (this.LA(1)) {
                    case 95: 
                    case 101: 
                    case 102: 
                    case 103: {
                        this.typeDefinitionInternal(mods_AST);
                        td_AST = this.returnAST;
                        if (this.inputState.guessing != 0) break block0;
                        enumConstantField_AST = currentAST.root;
                        currentAST.root = enumConstantField_AST = td_AST;
                        currentAST.child = enumConstantField_AST != null && enumConstantField_AST.getFirstChild() != null ? enumConstantField_AST.getFirstChild() : enumConstantField_AST;
                        currentAST.advanceChildToEnd();
                        break block0;
                    }
                    case 67: 
                    case 72: 
                    case 77: 
                    case 78: 
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: {
                        switch (this.LA(1)) {
                            case 72: {
                                this.typeParameters();
                                tp_AST = this.returnAST;
                                break;
                            }
                            case 67: 
                            case 77: 
                            case 78: 
                            case 79: 
                            case 80: 
                            case 81: 
                            case 82: 
                            case 83: 
                            case 84: 
                            case 85: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        this.typeSpec(false);
                        t_AST = this.returnAST;
                        if (this.LA(1) == 67 && this.LA(2) == 96) {
                            AST tmp114_AST = null;
                            tmp114_AST = this.astFactory.create(this.LT(1));
                            this.match(67);
                            this.match(96);
                            this.parameterDeclarationList();
                            param_AST = this.returnAST;
                            this.match(97);
                            this.declaratorBrackets(t_AST);
                            rt_AST = this.returnAST;
                            switch (this.LA(1)) {
                                case 108: {
                                    this.throwsClause();
                                    tc_AST = this.returnAST;
                                    break;
                                }
                                case 62: 
                                case 99: {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            switch (this.LA(1)) {
                                case 99: {
                                    this.compoundStatement();
                                    s2_AST = this.returnAST;
                                    break;
                                }
                                case 62: {
                                    AST tmp117_AST = null;
                                    tmp117_AST = this.astFactory.create(this.LT(1));
                                    this.match(62);
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            if (this.inputState.guessing != 0) break block0;
                            enumConstantField_AST = currentAST.root;
                            currentAST.root = enumConstantField_AST = this.astFactory.make(new ASTArray(8).add(this.create(8, "METHOD_DEF", first, this.LT(1))).add(mods_AST).add(tp_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(rt_AST))).add(tmp114_AST).add(param_AST).add(tc_AST).add(s2_AST));
                            currentAST.child = enumConstantField_AST != null && enumConstantField_AST.getFirstChild() != null ? enumConstantField_AST.getFirstChild() : enumConstantField_AST;
                            currentAST.advanceChildToEnd();
                            break block0;
                        }
                        if (this.LA(1) == 67 && _tokenSet_18.member(this.LA(2))) {
                            this.variableDefinitions(mods_AST, t_AST);
                            v_AST = this.returnAST;
                            AST tmp118_AST = null;
                            tmp118_AST = this.astFactory.create(this.LT(1));
                            this.match(62);
                            if (this.inputState.guessing != 0) break block0;
                            enumConstantField_AST = currentAST.root;
                            currentAST.root = enumConstantField_AST = v_AST;
                            currentAST.child = enumConstantField_AST != null && enumConstantField_AST.getFirstChild() != null ? enumConstantField_AST.getFirstChild() : enumConstantField_AST;
                            currentAST.advanceChildToEnd();
                            break block0;
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            case 99: {
                this.compoundStatement();
                s4_AST = this.returnAST;
                if (this.inputState.guessing != 0) break;
                enumConstantField_AST = currentAST.root;
                currentAST.root = enumConstantField_AST = this.astFactory.make(new ASTArray(2).add(this.create(10, "INSTANCE_INIT", first, this.LT(1))).add(s4_AST));
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

    public final void parameterDeclarationList() throws RecognitionException, TokenStreamException {
        Token first;
        AST parameterDeclarationList_AST;
        ASTPair currentAST;
        block16: {
            block15: {
                this.returnAST = null;
                currentAST = new ASTPair();
                parameterDeclarationList_AST = null;
                first = this.LT(1);
                boolean synPredMatched177 = false;
                if (_tokenSet_19.member(this.LA(1)) && _tokenSet_20.member(this.LA(2))) {
                    int _m177 = this.mark();
                    synPredMatched177 = true;
                    ++this.inputState.guessing;
                    try {
                        this.parameterDeclaration();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched177 = false;
                    }
                    this.rewind(_m177);
                    --this.inputState.guessing;
                }
                if (!synPredMatched177) break block15;
                this.parameterDeclaration();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (true) {
                    boolean synPredMatched180 = false;
                    if (this.LA(1) == 74 && _tokenSet_19.member(this.LA(2))) {
                        int _m180 = this.mark();
                        synPredMatched180 = true;
                        ++this.inputState.guessing;
                        try {
                            this.match(74);
                            this.parameterDeclaration();
                        }
                        catch (RecognitionException pe) {
                            synPredMatched180 = false;
                        }
                        this.rewind(_m180);
                        --this.inputState.guessing;
                    }
                    if (!synPredMatched180) break;
                    this.match(74);
                    this.parameterDeclaration();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                switch (this.LA(1)) {
                    case 74: {
                        this.match(74);
                        this.variableLengthParameterDeclaration();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break block16;
                    }
                    case 97: {
                        break block16;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            if (_tokenSet_19.member(this.LA(1)) && _tokenSet_21.member(this.LA(2))) {
                this.variableLengthParameterDeclaration();
                this.astFactory.addASTChild(currentAST, this.returnAST);
            } else if (this.LA(1) != 97) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            parameterDeclarationList_AST = currentAST.root;
            currentAST.root = parameterDeclarationList_AST = this.astFactory.make(new ASTArray(2).add(this.create(19, "PARAMETERS", first, this.LT(1))).add(parameterDeclarationList_AST));
            currentAST.child = parameterDeclarationList_AST != null && parameterDeclarationList_AST.getFirstChild() != null ? parameterDeclarationList_AST.getFirstChild() : parameterDeclarationList_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = parameterDeclarationList_AST = currentAST.root;
    }

    public final void throwsClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST throwsClause_AST = null;
        AST tmp121_AST = null;
        tmp121_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp121_AST);
        this.match(108);
        this.identifier();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 74) {
            this.match(74);
            this.identifier();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = throwsClause_AST = currentAST.root;
    }

    public final void compoundStatement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST compoundStatement_AST = null;
        Token lc = null;
        AST lc_AST = null;
        lc = this.LT(1);
        lc_AST = this.astFactory.create(lc);
        this.astFactory.makeASTRoot(currentAST, lc_AST);
        this.match(99);
        if (this.inputState.guessing == 0) {
            lc_AST.setType(7);
        }
        while (_tokenSet_22.member(this.LA(1))) {
            this.statement();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.match(100);
        this.returnAST = compoundStatement_AST = currentAST.root;
    }

    public final void ctorHead() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST ctorHead_AST = null;
        AST tmp124_AST = null;
        tmp124_AST = this.astFactory.create(this.LT(1));
        this.astFactory.addASTChild(currentAST, tmp124_AST);
        this.match(67);
        this.match(96);
        this.parameterDeclarationList();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(97);
        switch (this.LA(1)) {
            case 108: {
                this.throwsClause();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 99: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = ctorHead_AST = currentAST.root;
    }

    public final void constructorBody() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST constructorBody_AST = null;
        Token lc = null;
        AST lc_AST = null;
        lc = this.LT(1);
        lc_AST = this.astFactory.create(lc);
        this.astFactory.makeASTRoot(currentAST, lc_AST);
        this.match(99);
        if (this.inputState.guessing == 0) {
            lc_AST.setType(7);
        }
        if (_tokenSet_23.member(this.LA(1)) && _tokenSet_24.member(this.LA(2))) {
            this.explicitConstructorInvocation();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (!_tokenSet_25.member(this.LA(1)) || !_tokenSet_26.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        while (_tokenSet_22.member(this.LA(1))) {
            this.statement();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.match(100);
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
            case 72: {
                this.typeArguments();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 71: 
            case 107: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 107: {
                this.match(107);
                lp1 = this.LT(1);
                lp1_AST = this.astFactory.create(lp1);
                this.astFactory.makeASTRoot(currentAST, lp1_AST);
                this.match(96);
                this.argList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                this.match(62);
                if (this.inputState.guessing != 0) break;
                lp1_AST.setType(42);
                break;
            }
            case 71: {
                this.match(71);
                lp2 = this.LT(1);
                lp2_AST = this.astFactory.create(lp2);
                this.astFactory.makeASTRoot(currentAST, lp2_AST);
                this.match(96);
                this.argList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                this.match(62);
                if (this.inputState.guessing != 0) break;
                lp2_AST.setType(41);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = explicitConstructorInvocation_AST = currentAST.root;
    }

    public final void statement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST statement_AST = null;
        AST m_AST = null;
        Token c = null;
        AST c_AST = null;
        Token s = null;
        AST s_AST = null;
        switch (this.LA(1)) {
            case 99: {
                this.compoundStatement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                statement_AST = currentAST.root;
                break;
            }
            case 111: {
                AST tmp134_AST = null;
                tmp134_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp134_AST);
                this.match(111);
                this.match(96);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                this.statement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.LA(1) == 112 && _tokenSet_22.member(this.LA(2))) {
                    this.match(112);
                    this.statement();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                } else if (!_tokenSet_27.member(this.LA(1)) || !_tokenSet_28.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                statement_AST = currentAST.root;
                break;
            }
            case 121: {
                this.forStatement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                statement_AST = currentAST.root;
                break;
            }
            case 113: {
                AST tmp138_AST = null;
                tmp138_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp138_AST);
                this.match(113);
                this.match(96);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                this.statement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                statement_AST = currentAST.root;
                break;
            }
            case 114: {
                AST tmp141_AST = null;
                tmp141_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp141_AST);
                this.match(114);
                this.statement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(113);
                this.match(96);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                this.match(62);
                statement_AST = currentAST.root;
                break;
            }
            case 115: {
                AST tmp146_AST = null;
                tmp146_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp146_AST);
                this.match(115);
                switch (this.LA(1)) {
                    case 67: {
                        AST tmp147_AST = null;
                        tmp147_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp147_AST);
                        this.match(67);
                        break;
                    }
                    case 62: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(62);
                statement_AST = currentAST.root;
                break;
            }
            case 116: {
                AST tmp149_AST = null;
                tmp149_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp149_AST);
                this.match(116);
                switch (this.LA(1)) {
                    case 67: {
                        AST tmp150_AST = null;
                        tmp150_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp150_AST);
                        this.match(67);
                        break;
                    }
                    case 62: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(62);
                statement_AST = currentAST.root;
                break;
            }
            case 117: {
                AST tmp152_AST = null;
                tmp152_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp152_AST);
                this.match(117);
                switch (this.LA(1)) {
                    case 67: 
                    case 71: 
                    case 72: 
                    case 77: 
                    case 78: 
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 96: 
                    case 107: 
                    case 147: 
                    case 148: 
                    case 151: 
                    case 152: 
                    case 153: 
                    case 154: 
                    case 155: 
                    case 156: 
                    case 157: 
                    case 158: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 162: 
                    case 163: 
                    case 164: {
                        this.expression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 62: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(62);
                statement_AST = currentAST.root;
                break;
            }
            case 118: {
                AST tmp154_AST = null;
                tmp154_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp154_AST);
                this.match(118);
                this.match(96);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                this.match(99);
                while (this.LA(1) == 105 || this.LA(1) == 122) {
                    this.casesGroup();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                this.match(100);
                statement_AST = currentAST.root;
                break;
            }
            case 123: {
                this.tryBlock();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                statement_AST = currentAST.root;
                break;
            }
            case 119: {
                AST tmp159_AST = null;
                tmp159_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp159_AST);
                this.match(119);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(62);
                statement_AST = currentAST.root;
                break;
            }
            case 120: {
                AST tmp161_AST = null;
                tmp161_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp161_AST);
                this.match(120);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 110: {
                        this.match(110);
                        this.expression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 62: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(62);
                statement_AST = currentAST.root;
                break;
            }
            case 62: {
                s = this.LT(1);
                s_AST = this.astFactory.create(s);
                this.astFactory.addASTChild(currentAST, s_AST);
                this.match(62);
                if (this.inputState.guessing == 0) {
                    s_AST.setType(37);
                }
                statement_AST = currentAST.root;
                break;
            }
            default: {
                boolean synPredMatched196 = false;
                if (_tokenSet_29.member(this.LA(1)) && _tokenSet_30.member(this.LA(2))) {
                    int _m196 = this.mark();
                    synPredMatched196 = true;
                    ++this.inputState.guessing;
                    try {
                        this.declaration();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched196 = false;
                    }
                    this.rewind(_m196);
                    --this.inputState.guessing;
                }
                if (synPredMatched196) {
                    this.declaration();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.match(62);
                    statement_AST = currentAST.root;
                    break;
                }
                if (_tokenSet_31.member(this.LA(1)) && _tokenSet_32.member(this.LA(2))) {
                    this.expression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.match(62);
                    statement_AST = currentAST.root;
                    break;
                }
                if (_tokenSet_33.member(this.LA(1)) && _tokenSet_34.member(this.LA(2))) {
                    this.modifiers();
                    m_AST = this.returnAST;
                    this.classDefinition(m_AST);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                if (this.LA(1) == 67 && this.LA(2) == 110) {
                    AST tmp166_AST = null;
                    tmp166_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.addASTChild(currentAST, tmp166_AST);
                    this.match(67);
                    c = this.LT(1);
                    c_AST = this.astFactory.create(c);
                    this.astFactory.makeASTRoot(currentAST, c_AST);
                    this.match(110);
                    if (this.inputState.guessing == 0) {
                        c_AST.setType(21);
                    }
                    this.statement();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                if (this.LA(1) == 93 && this.LA(2) == 96) {
                    AST tmp167_AST = null;
                    tmp167_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp167_AST);
                    this.match(93);
                    this.match(96);
                    this.expression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.match(97);
                    this.compoundStatement();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    statement_AST = currentAST.root;
                    break;
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = statement_AST;
    }

    public final void variableDeclarator(AST mods, AST t) throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST variableDeclarator_AST = null;
        Token id = null;
        AST id_AST = null;
        AST d_AST = null;
        AST v_AST = null;
        Token first = this.LT(1);
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.match(67);
        this.declaratorBrackets(t);
        d_AST = this.returnAST;
        this.varInitializer();
        v_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            variableDeclarator_AST = currentAST.root;
            currentAST.root = variableDeclarator_AST = this.astFactory.make(new ASTArray(5).add(this.create(9, "VARIABLE_DEF", first, this.LT(1))).add(mods).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(d_AST))).add(id_AST).add(v_AST));
            currentAST.child = variableDeclarator_AST != null && variableDeclarator_AST.getFirstChild() != null ? variableDeclarator_AST.getFirstChild() : variableDeclarator_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = variableDeclarator_AST;
    }

    public final void varInitializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST varInitializer_AST = null;
        switch (this.LA(1)) {
            case 98: {
                AST tmp170_AST = null;
                tmp170_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp170_AST);
                this.match(98);
                this.initializer();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 62: 
            case 74: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = varInitializer_AST = currentAST.root;
    }

    public final void initializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST initializer_AST = null;
        switch (this.LA(1)) {
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                initializer_AST = currentAST.root;
                break;
            }
            case 99: {
                this.arrayInitializer();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                initializer_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = initializer_AST;
    }

    public final void arrayInitializer() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST arrayInitializer_AST = null;
        Token lc = null;
        AST lc_AST = null;
        lc = this.LT(1);
        lc_AST = this.astFactory.create(lc);
        this.astFactory.makeASTRoot(currentAST, lc_AST);
        this.match(99);
        if (this.inputState.guessing == 0) {
            lc_AST.setType(28);
        }
        block0 : switch (this.LA(1)) {
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 99: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.initializer();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 74 && _tokenSet_35.member(this.LA(2))) {
                    this.match(74);
                    this.initializer();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                switch (this.LA(1)) {
                    case 74: {
                        this.match(74);
                        break block0;
                    }
                    case 100: {
                        break block0;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 100: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(100);
        this.returnAST = arrayInitializer_AST = currentAST.root;
    }

    public final void expression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST expression_AST = null;
        Token first = this.LT(1);
        this.assignmentExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            expression_AST = currentAST.root;
            currentAST.root = expression_AST = this.astFactory.make(new ASTArray(2).add(this.create(27, "EXPR", first, this.LT(1))).add(expression_AST));
            currentAST.child = expression_AST != null && expression_AST.getFirstChild() != null ? expression_AST.getFirstChild() : expression_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = expression_AST = currentAST.root;
    }

    public final void parameterDeclaration() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST parameterDeclaration_AST = null;
        AST pm_AST = null;
        AST t_AST = null;
        Token id = null;
        AST id_AST = null;
        AST pd_AST = null;
        Token first = this.LT(1);
        this.parameterModifier();
        pm_AST = this.returnAST;
        this.typeSpec(false);
        t_AST = this.returnAST;
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.match(67);
        this.declaratorBrackets(t_AST);
        pd_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            parameterDeclaration_AST = currentAST.root;
            currentAST.root = parameterDeclaration_AST = this.astFactory.make(new ASTArray(4).add(this.create(20, "PARAMETER_DEF", first, this.LT(1))).add(pm_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(pd_AST))).add(id_AST));
            currentAST.child = parameterDeclaration_AST != null && parameterDeclaration_AST.getFirstChild() != null ? parameterDeclaration_AST.getFirstChild() : parameterDeclaration_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = parameterDeclaration_AST;
    }

    public final void variableLengthParameterDeclaration() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST variableLengthParameterDeclaration_AST = null;
        AST pm_AST = null;
        AST t_AST = null;
        Token id = null;
        AST id_AST = null;
        AST pd_AST = null;
        Token first = this.LT(1);
        this.parameterModifier();
        pm_AST = this.returnAST;
        this.typeSpec(false);
        t_AST = this.returnAST;
        this.match(109);
        id = this.LT(1);
        id_AST = this.astFactory.create(id);
        this.match(67);
        this.declaratorBrackets(t_AST);
        pd_AST = this.returnAST;
        if (this.inputState.guessing == 0) {
            variableLengthParameterDeclaration_AST = currentAST.root;
            currentAST.root = variableLengthParameterDeclaration_AST = this.astFactory.make(new ASTArray(4).add(this.create(43, "VARIABLE_PARAMETER_DEF", first, this.LT(1))).add(pm_AST).add(this.astFactory.make(new ASTArray(2).add(this.create(12, "TYPE", first, this.LT(1))).add(pd_AST))).add(id_AST));
            currentAST.child = variableLengthParameterDeclaration_AST != null && variableLengthParameterDeclaration_AST.getFirstChild() != null ? variableLengthParameterDeclaration_AST.getFirstChild() : variableLengthParameterDeclaration_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = variableLengthParameterDeclaration_AST;
    }

    public final void parameterModifier() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST parameterModifier_AST = null;
        Token f = null;
        AST f_AST = null;
        Token first = this.LT(1);
        while (this.LA(1) == 95 && this.LA(2) == 67) {
            this.annotation();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        switch (this.LA(1)) {
            case 38: {
                f = this.LT(1);
                f_AST = this.astFactory.create(f);
                this.astFactory.addASTChild(currentAST, f_AST);
                this.match(38);
                break;
            }
            case 67: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 95: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        while (this.LA(1) == 95) {
            this.annotation();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            parameterModifier_AST = currentAST.root;
            currentAST.root = parameterModifier_AST = this.astFactory.make(new ASTArray(2).add(this.create(5, "MODIFIERS", first, this.LT(1))).add(parameterModifier_AST));
            currentAST.child = parameterModifier_AST != null && parameterModifier_AST.getFirstChild() != null ? parameterModifier_AST.getFirstChild() : parameterModifier_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = parameterModifier_AST = currentAST.root;
    }

    public final void forStatement() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forStatement_AST = null;
        Token f = null;
        AST f_AST = null;
        f = this.LT(1);
        f_AST = this.astFactory.create(f);
        this.astFactory.makeASTRoot(currentAST, f_AST);
        this.match(121);
        this.match(96);
        boolean synPredMatched207 = false;
        if (_tokenSet_36.member(this.LA(1)) && _tokenSet_37.member(this.LA(2))) {
            int _m207 = this.mark();
            synPredMatched207 = true;
            ++this.inputState.guessing;
            try {
                this.forInit();
                this.match(62);
            }
            catch (RecognitionException pe) {
                synPredMatched207 = false;
            }
            this.rewind(_m207);
            --this.inputState.guessing;
        }
        if (synPredMatched207) {
            this.traditionalForClause();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (_tokenSet_19.member(this.LA(1)) && _tokenSet_20.member(this.LA(2))) {
            this.forEachClause();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.match(97);
        this.statement();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = forStatement_AST = currentAST.root;
    }

    public final void casesGroup() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST casesGroup_AST = null;
        int _cnt212 = 0;
        while (true) {
            if (this.LA(1) != 105 && this.LA(1) != 122 || !_tokenSet_38.member(this.LA(2))) {
                if (_cnt212 >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.aCase();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            ++_cnt212;
        }
        this.caseSList();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            casesGroup_AST = currentAST.root;
            currentAST.root = casesGroup_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(32, "CASE_GROUP")).add(casesGroup_AST));
            currentAST.child = casesGroup_AST != null && casesGroup_AST.getFirstChild() != null ? casesGroup_AST.getFirstChild() : casesGroup_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = casesGroup_AST = currentAST.root;
    }

    public final void tryBlock() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST tryBlock_AST = null;
        AST tmp177_AST = null;
        tmp177_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp177_AST);
        this.match(123);
        this.compoundStatement();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 125) {
            this.handler();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        switch (this.LA(1)) {
            case 124: {
                this.finallyClause();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 38: 
            case 39: 
            case 40: 
            case 62: 
            case 64: 
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 87: 
            case 88: 
            case 89: 
            case 90: 
            case 91: 
            case 92: 
            case 93: 
            case 94: 
            case 95: 
            case 96: 
            case 99: 
            case 100: 
            case 101: 
            case 105: 
            case 107: 
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
            case 123: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = tryBlock_AST = currentAST.root;
    }

    public final void forInit() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forInit_AST = null;
        Token first = this.LT(1);
        boolean synPredMatched221 = false;
        if (_tokenSet_29.member(this.LA(1)) && _tokenSet_30.member(this.LA(2))) {
            int _m221 = this.mark();
            synPredMatched221 = true;
            ++this.inputState.guessing;
            try {
                this.declaration();
            }
            catch (RecognitionException pe) {
                synPredMatched221 = false;
            }
            this.rewind(_m221);
            --this.inputState.guessing;
        }
        if (synPredMatched221) {
            this.declaration();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (_tokenSet_31.member(this.LA(1)) && _tokenSet_39.member(this.LA(2))) {
            this.expressionList();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        } else if (this.LA(1) != 62) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        if (this.inputState.guessing == 0) {
            forInit_AST = currentAST.root;
            currentAST.root = forInit_AST = this.astFactory.make(new ASTArray(2).add(this.create(34, "FOR_INIT", first, this.LT(1))).add(forInit_AST));
            currentAST.child = forInit_AST != null && forInit_AST.getFirstChild() != null ? forInit_AST.getFirstChild() : forInit_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = forInit_AST = currentAST.root;
    }

    public final void traditionalForClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST traditionalForClause_AST = null;
        this.forInit();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(62);
        this.forCond();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(62);
        this.forIter();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = traditionalForClause_AST = currentAST.root;
    }

    public final void forEachClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forEachClause_AST = null;
        AST p_AST = null;
        Token first = this.LT(1);
        this.parameterDeclaration();
        p_AST = this.returnAST;
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(110);
        this.expression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        if (this.inputState.guessing == 0) {
            forEachClause_AST = currentAST.root;
            currentAST.root = forEachClause_AST = this.astFactory.make(new ASTArray(2).add(this.create(47, "FOR_EACH_CLAUSE", first, this.LT(1))).add(forEachClause_AST));
            currentAST.child = forEachClause_AST != null && forEachClause_AST.getFirstChild() != null ? forEachClause_AST.getFirstChild() : forEachClause_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = forEachClause_AST = currentAST.root;
    }

    public final void forCond() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST forCond_AST = null;
        Token first = this.LT(1);
        switch (this.LA(1)) {
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 62: {
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
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 147: 
            case 148: 
            case 151: 
            case 152: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.expressionList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 97: {
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

    public final void aCase() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST aCase_AST = null;
        switch (this.LA(1)) {
            case 122: {
                AST tmp181_AST = null;
                tmp181_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp181_AST);
                this.match(122);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 105: {
                AST tmp182_AST = null;
                tmp182_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp182_AST);
                this.match(105);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(110);
        this.returnAST = aCase_AST = currentAST.root;
    }

    public final void caseSList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST caseSList_AST = null;
        Token first = this.LT(1);
        while (_tokenSet_22.member(this.LA(1))) {
            this.statement();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            caseSList_AST = currentAST.root;
            currentAST.root = caseSList_AST = this.astFactory.make(new ASTArray(2).add(this.create(7, "SLIST", first, this.LT(1))).add(caseSList_AST));
            currentAST.child = caseSList_AST != null && caseSList_AST.getFirstChild() != null ? caseSList_AST.getFirstChild() : caseSList_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = caseSList_AST = currentAST.root;
    }

    public final void expressionList() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST expressionList_AST = null;
        Token first = this.LT(1);
        this.expression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 74) {
            this.match(74);
            this.expression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        if (this.inputState.guessing == 0) {
            expressionList_AST = currentAST.root;
            currentAST.root = expressionList_AST = this.astFactory.make(new ASTArray(2).add(this.create(33, "ELIST", first, this.LT(1))).add(expressionList_AST));
            currentAST.child = expressionList_AST != null && expressionList_AST.getFirstChild() != null ? expressionList_AST.getFirstChild() : expressionList_AST;
            currentAST.advanceChildToEnd();
        }
        this.returnAST = expressionList_AST = currentAST.root;
    }

    public final void handler() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST handler_AST = null;
        AST tmp185_AST = null;
        tmp185_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp185_AST);
        this.match(125);
        this.match(96);
        this.parameterDeclaration();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.match(97);
        this.compoundStatement();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = handler_AST = currentAST.root;
    }

    public final void finallyClause() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST finallyClause_AST = null;
        AST tmp188_AST = null;
        tmp188_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp188_AST);
        this.match(124);
        this.compoundStatement();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        this.returnAST = finallyClause_AST = currentAST.root;
    }

    public final void assignmentExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST assignmentExpression_AST = null;
        this.conditionalExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        switch (this.LA(1)) {
            case 98: 
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
            case 136: {
                switch (this.LA(1)) {
                    case 98: {
                        AST tmp189_AST = null;
                        tmp189_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp189_AST);
                        this.match(98);
                        break;
                    }
                    case 126: {
                        AST tmp190_AST = null;
                        tmp190_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp190_AST);
                        this.match(126);
                        break;
                    }
                    case 127: {
                        AST tmp191_AST = null;
                        tmp191_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp191_AST);
                        this.match(127);
                        break;
                    }
                    case 128: {
                        AST tmp192_AST = null;
                        tmp192_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp192_AST);
                        this.match(128);
                        break;
                    }
                    case 129: {
                        AST tmp193_AST = null;
                        tmp193_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp193_AST);
                        this.match(129);
                        break;
                    }
                    case 130: {
                        AST tmp194_AST = null;
                        tmp194_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp194_AST);
                        this.match(130);
                        break;
                    }
                    case 131: {
                        AST tmp195_AST = null;
                        tmp195_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp195_AST);
                        this.match(131);
                        break;
                    }
                    case 132: {
                        AST tmp196_AST = null;
                        tmp196_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp196_AST);
                        this.match(132);
                        break;
                    }
                    case 133: {
                        AST tmp197_AST = null;
                        tmp197_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp197_AST);
                        this.match(133);
                        break;
                    }
                    case 134: {
                        AST tmp198_AST = null;
                        tmp198_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp198_AST);
                        this.match(134);
                        break;
                    }
                    case 135: {
                        AST tmp199_AST = null;
                        tmp199_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp199_AST);
                        this.match(135);
                        break;
                    }
                    case 136: {
                        AST tmp200_AST = null;
                        tmp200_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp200_AST);
                        this.match(136);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.assignmentExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 62: 
            case 66: 
            case 74: 
            case 97: 
            case 100: 
            case 110: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = assignmentExpression_AST = currentAST.root;
    }

    public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST logicalOrExpression_AST = null;
        this.logicalAndExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 137) {
            AST tmp201_AST = null;
            tmp201_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp201_AST);
            this.match(137);
            this.logicalAndExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = logicalOrExpression_AST = currentAST.root;
    }

    public final void logicalAndExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST logicalAndExpression_AST = null;
        this.inclusiveOrExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 138) {
            AST tmp202_AST = null;
            tmp202_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp202_AST);
            this.match(138);
            this.inclusiveOrExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = logicalAndExpression_AST = currentAST.root;
    }

    public final void inclusiveOrExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST inclusiveOrExpression_AST = null;
        this.exclusiveOrExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 139) {
            AST tmp203_AST = null;
            tmp203_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp203_AST);
            this.match(139);
            this.exclusiveOrExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = inclusiveOrExpression_AST = currentAST.root;
    }

    public final void exclusiveOrExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST exclusiveOrExpression_AST = null;
        this.andExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 140) {
            AST tmp204_AST = null;
            tmp204_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp204_AST);
            this.match(140);
            this.andExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = exclusiveOrExpression_AST = currentAST.root;
    }

    public final void andExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST andExpression_AST = null;
        this.equalityExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 104) {
            AST tmp205_AST = null;
            tmp205_AST = this.astFactory.create(this.LT(1));
            this.astFactory.makeASTRoot(currentAST, tmp205_AST);
            this.match(104);
            this.equalityExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = andExpression_AST = currentAST.root;
    }

    public final void equalityExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST equalityExpression_AST = null;
        this.relationalExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 141 || this.LA(1) == 142) {
            switch (this.LA(1)) {
                case 141: {
                    AST tmp206_AST = null;
                    tmp206_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp206_AST);
                    this.match(141);
                    break;
                }
                case 142: {
                    AST tmp207_AST = null;
                    tmp207_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp207_AST);
                    this.match(142);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.relationalExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = equalityExpression_AST = currentAST.root;
    }

    public final void relationalExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST relationalExpression_AST = null;
        this.shiftExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        switch (this.LA(1)) {
            case 62: 
            case 66: 
            case 69: 
            case 72: 
            case 73: 
            case 74: 
            case 97: 
            case 98: 
            case 100: 
            case 104: 
            case 110: 
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
            case 144: {
                while (_tokenSet_40.member(this.LA(1))) {
                    switch (this.LA(1)) {
                        case 72: {
                            AST tmp208_AST = null;
                            tmp208_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp208_AST);
                            this.match(72);
                            break;
                        }
                        case 73: {
                            AST tmp209_AST = null;
                            tmp209_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp209_AST);
                            this.match(73);
                            break;
                        }
                        case 143: {
                            AST tmp210_AST = null;
                            tmp210_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp210_AST);
                            this.match(143);
                            break;
                        }
                        case 144: {
                            AST tmp211_AST = null;
                            tmp211_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp211_AST);
                            this.match(144);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.shiftExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                break;
            }
            case 145: {
                AST tmp212_AST = null;
                tmp212_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp212_AST);
                this.match(145);
                this.typeSpec(true);
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = relationalExpression_AST = currentAST.root;
    }

    public final void shiftExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST shiftExpression_AST = null;
        this.additiveExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (_tokenSet_41.member(this.LA(1))) {
            switch (this.LA(1)) {
                case 146: {
                    AST tmp213_AST = null;
                    tmp213_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp213_AST);
                    this.match(146);
                    break;
                }
                case 75: {
                    AST tmp214_AST = null;
                    tmp214_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp214_AST);
                    this.match(75);
                    break;
                }
                case 76: {
                    AST tmp215_AST = null;
                    tmp215_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp215_AST);
                    this.match(76);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.additiveExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = shiftExpression_AST = currentAST.root;
    }

    public final void additiveExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST additiveExpression_AST = null;
        this.multiplicativeExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (this.LA(1) == 147 || this.LA(1) == 148) {
            switch (this.LA(1)) {
                case 147: {
                    AST tmp216_AST = null;
                    tmp216_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp216_AST);
                    this.match(147);
                    break;
                }
                case 148: {
                    AST tmp217_AST = null;
                    tmp217_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp217_AST);
                    this.match(148);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.multiplicativeExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = additiveExpression_AST = currentAST.root;
    }

    public final void multiplicativeExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST multiplicativeExpression_AST = null;
        this.unaryExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        while (_tokenSet_42.member(this.LA(1))) {
            switch (this.LA(1)) {
                case 86: {
                    AST tmp218_AST = null;
                    tmp218_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp218_AST);
                    this.match(86);
                    break;
                }
                case 149: {
                    AST tmp219_AST = null;
                    tmp219_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp219_AST);
                    this.match(149);
                    break;
                }
                case 150: {
                    AST tmp220_AST = null;
                    tmp220_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp220_AST);
                    this.match(150);
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.unaryExpression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
        }
        this.returnAST = multiplicativeExpression_AST = currentAST.root;
    }

    public final void unaryExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST unaryExpression_AST = null;
        switch (this.LA(1)) {
            case 151: {
                AST tmp221_AST = null;
                tmp221_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp221_AST);
                this.match(151);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 152: {
                AST tmp222_AST = null;
                tmp222_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp222_AST);
                this.match(152);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 148: {
                AST tmp223_AST = null;
                tmp223_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp223_AST);
                this.match(148);
                if (this.inputState.guessing == 0) {
                    tmp223_AST.setType(30);
                }
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 147: {
                AST tmp224_AST = null;
                tmp224_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp224_AST);
                this.match(147);
                if (this.inputState.guessing == 0) {
                    tmp224_AST.setType(31);
                }
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpression_AST = currentAST.root;
                break;
            }
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.unaryExpressionNotPlusMinus();
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

    public final void unaryExpressionNotPlusMinus() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST unaryExpressionNotPlusMinus_AST = null;
        Token lpb = null;
        AST lpb_AST = null;
        Token lp = null;
        AST lp_AST = null;
        switch (this.LA(1)) {
            case 153: {
                AST tmp225_AST = null;
                tmp225_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp225_AST);
                this.match(153);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpressionNotPlusMinus_AST = currentAST.root;
                break;
            }
            case 154: {
                AST tmp226_AST = null;
                tmp226_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp226_AST);
                this.match(154);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                unaryExpressionNotPlusMinus_AST = currentAST.root;
                break;
            }
            case 67: 
            case 71: 
            case 72: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: 
            case 96: 
            case 107: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                boolean synPredMatched281 = false;
                if (this.LA(1) == 96 && this.LA(2) >= 77 && this.LA(2) <= 85) {
                    int _m281 = this.mark();
                    synPredMatched281 = true;
                    ++this.inputState.guessing;
                    try {
                        this.match(96);
                        this.builtInTypeSpec(true);
                        this.match(97);
                        this.unaryExpression();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched281 = false;
                    }
                    this.rewind(_m281);
                    --this.inputState.guessing;
                }
                if (synPredMatched281) {
                    lpb = this.LT(1);
                    lpb_AST = this.astFactory.create(lpb);
                    this.astFactory.makeASTRoot(currentAST, lpb_AST);
                    this.match(96);
                    if (this.inputState.guessing == 0) {
                        lpb_AST.setType(22);
                    }
                    this.builtInTypeSpec(true);
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    this.match(97);
                    this.unaryExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                } else {
                    boolean synPredMatched283 = false;
                    if (this.LA(1) == 96 && this.LA(2) == 67) {
                        int _m283 = this.mark();
                        synPredMatched283 = true;
                        ++this.inputState.guessing;
                        try {
                            this.match(96);
                            this.classTypeSpec(true);
                            this.match(97);
                            this.unaryExpressionNotPlusMinus();
                        }
                        catch (RecognitionException pe) {
                            synPredMatched283 = false;
                        }
                        this.rewind(_m283);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched283) {
                        lp = this.LT(1);
                        lp_AST = this.astFactory.create(lp);
                        this.astFactory.makeASTRoot(currentAST, lp_AST);
                        this.match(96);
                        if (this.inputState.guessing == 0) {
                            lp_AST.setType(22);
                        }
                        this.classTypeSpec(true);
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        this.match(97);
                        this.unaryExpressionNotPlusMinus();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                    } else if (_tokenSet_43.member(this.LA(1)) && _tokenSet_44.member(this.LA(2))) {
                        this.postfixExpression();
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

    public final void postfixExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST postfixExpression_AST = null;
        Token lp = null;
        AST lp_AST = null;
        Token lp3 = null;
        AST lp3_AST = null;
        Token lps = null;
        AST lps_AST = null;
        Token lb = null;
        AST lb_AST = null;
        Token in = null;
        AST in_AST = null;
        Token de = null;
        AST de_AST = null;
        this.primaryExpression();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        block29: while (true) {
            if (this.LA(1) == 68 && _tokenSet_45.member(this.LA(2))) {
                AST tmp229_AST = null;
                tmp229_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp229_AST);
                this.match(68);
                switch (this.LA(1)) {
                    case 72: {
                        this.typeArguments();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 67: 
                    case 71: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 67: {
                        AST tmp230_AST = null;
                        tmp230_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp230_AST);
                        this.match(67);
                        switch (this.LA(1)) {
                            case 96: {
                                lp = this.LT(1);
                                lp_AST = this.astFactory.create(lp);
                                this.astFactory.makeASTRoot(currentAST, lp_AST);
                                this.match(96);
                                if (this.inputState.guessing == 0) {
                                    lp_AST.setType(26);
                                }
                                this.argList();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                this.match(97);
                                continue block29;
                            }
                            case 62: 
                            case 65: 
                            case 66: 
                            case 68: 
                            case 69: 
                            case 72: 
                            case 73: 
                            case 74: 
                            case 75: 
                            case 76: 
                            case 86: 
                            case 97: 
                            case 98: 
                            case 100: 
                            case 104: 
                            case 110: 
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
                            case 152: {
                                continue block29;
                            }
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    case 71: {
                        AST tmp232_AST = null;
                        tmp232_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp232_AST);
                        this.match(71);
                        switch (this.LA(1)) {
                            case 96: {
                                lp3 = this.LT(1);
                                lp3_AST = this.astFactory.create(lp3);
                                this.astFactory.makeASTRoot(currentAST, lp3_AST);
                                this.match(96);
                                this.argList();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                this.match(97);
                                if (this.inputState.guessing != 0) continue block29;
                                lp3_AST.setType(41);
                                continue block29;
                            }
                            case 68: {
                                AST tmp234_AST = null;
                                tmp234_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.makeASTRoot(currentAST, tmp234_AST);
                                this.match(68);
                                switch (this.LA(1)) {
                                    case 72: {
                                        this.typeArguments();
                                        this.astFactory.addASTChild(currentAST, this.returnAST);
                                        break;
                                    }
                                    case 67: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                AST tmp235_AST = null;
                                tmp235_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp235_AST);
                                this.match(67);
                                switch (this.LA(1)) {
                                    case 96: {
                                        lps = this.LT(1);
                                        lps_AST = this.astFactory.create(lps);
                                        this.astFactory.makeASTRoot(currentAST, lps_AST);
                                        this.match(96);
                                        if (this.inputState.guessing == 0) {
                                            lps_AST.setType(26);
                                        }
                                        this.argList();
                                        this.astFactory.addASTChild(currentAST, this.returnAST);
                                        this.match(97);
                                        continue block29;
                                    }
                                    case 62: 
                                    case 65: 
                                    case 66: 
                                    case 68: 
                                    case 69: 
                                    case 72: 
                                    case 73: 
                                    case 74: 
                                    case 75: 
                                    case 76: 
                                    case 86: 
                                    case 97: 
                                    case 98: 
                                    case 100: 
                                    case 104: 
                                    case 110: 
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
                                    case 152: {
                                        continue block29;
                                    }
                                }
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            if (this.LA(1) == 68 && this.LA(2) == 107) {
                AST tmp237_AST = null;
                tmp237_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp237_AST);
                this.match(68);
                AST tmp238_AST = null;
                tmp238_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp238_AST);
                this.match(107);
                continue;
            }
            if (this.LA(1) == 68 && this.LA(2) == 158) {
                AST tmp239_AST = null;
                tmp239_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp239_AST);
                this.match(68);
                this.newExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                continue;
            }
            if (this.LA(1) != 65) break;
            lb = this.LT(1);
            lb_AST = this.astFactory.create(lb);
            this.astFactory.makeASTRoot(currentAST, lb_AST);
            this.match(65);
            if (this.inputState.guessing == 0) {
                lb_AST.setType(23);
            }
            this.expression();
            this.astFactory.addASTChild(currentAST, this.returnAST);
            this.match(66);
        }
        switch (this.LA(1)) {
            case 151: {
                in = this.LT(1);
                in_AST = this.astFactory.create(in);
                this.astFactory.makeASTRoot(currentAST, in_AST);
                this.match(151);
                if (this.inputState.guessing != 0) break;
                in_AST.setType(24);
                break;
            }
            case 152: {
                de = this.LT(1);
                de_AST = this.astFactory.create(de);
                this.astFactory.makeASTRoot(currentAST, de_AST);
                this.match(152);
                if (this.inputState.guessing != 0) break;
                de_AST.setType(25);
                break;
            }
            case 62: 
            case 66: 
            case 69: 
            case 72: 
            case 73: 
            case 74: 
            case 75: 
            case 76: 
            case 86: 
            case 97: 
            case 98: 
            case 100: 
            case 104: 
            case 110: 
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
            case 150: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = postfixExpression_AST = currentAST.root;
    }

    public final void primaryExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST primaryExpression_AST = null;
        Token lbt = null;
        AST lbt_AST = null;
        switch (this.LA(1)) {
            case 67: 
            case 72: {
                this.identPrimary();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                if (this.LA(1) == 68 && this.LA(2) == 101) {
                    AST tmp241_AST = null;
                    tmp241_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp241_AST);
                    this.match(68);
                    AST tmp242_AST = null;
                    tmp242_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.addASTChild(currentAST, tmp242_AST);
                    this.match(101);
                } else if (!_tokenSet_46.member(this.LA(1)) || !_tokenSet_47.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: {
                this.constant();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 155: {
                AST tmp243_AST = null;
                tmp243_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp243_AST);
                this.match(155);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 156: {
                AST tmp244_AST = null;
                tmp244_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp244_AST);
                this.match(156);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 157: {
                AST tmp245_AST = null;
                tmp245_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp245_AST);
                this.match(157);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 158: {
                this.newExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 107: {
                AST tmp246_AST = null;
                tmp246_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp246_AST);
                this.match(107);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 71: {
                AST tmp247_AST = null;
                tmp247_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp247_AST);
                this.match(71);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 96: {
                this.match(96);
                this.assignmentExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                primaryExpression_AST = currentAST.root;
                break;
            }
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
                this.builtInType();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 65) {
                    lbt = this.LT(1);
                    lbt_AST = this.astFactory.create(lbt);
                    this.astFactory.makeASTRoot(currentAST, lbt_AST);
                    this.match(65);
                    if (this.inputState.guessing == 0) {
                        lbt_AST.setType(16);
                    }
                    this.match(66);
                }
                AST tmp251_AST = null;
                tmp251_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp251_AST);
                this.match(68);
                AST tmp252_AST = null;
                tmp252_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp252_AST);
                this.match(101);
                primaryExpression_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = primaryExpression_AST;
    }

    public final void newExpression() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST newExpression_AST = null;
        AST tmp253_AST = null;
        tmp253_AST = this.astFactory.create(this.LT(1));
        this.astFactory.makeASTRoot(currentAST, tmp253_AST);
        this.match(158);
        switch (this.LA(1)) {
            case 72: {
                this.typeArguments();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                break;
            }
            case 67: 
            case 77: 
            case 78: 
            case 79: 
            case 80: 
            case 81: 
            case 82: 
            case 83: 
            case 84: 
            case 85: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.type();
        this.astFactory.addASTChild(currentAST, this.returnAST);
        block4 : switch (this.LA(1)) {
            case 96: {
                this.match(96);
                this.argList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
                switch (this.LA(1)) {
                    case 99: {
                        this.classBlock();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break block4;
                    }
                    case 62: 
                    case 65: 
                    case 66: 
                    case 68: 
                    case 69: 
                    case 72: 
                    case 73: 
                    case 74: 
                    case 75: 
                    case 76: 
                    case 86: 
                    case 97: 
                    case 98: 
                    case 100: 
                    case 104: 
                    case 110: 
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
                    case 152: {
                        break block4;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            case 65: {
                this.newArrayDeclarator();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 99: {
                        this.arrayInitializer();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break block4;
                    }
                    case 62: 
                    case 65: 
                    case 66: 
                    case 68: 
                    case 69: 
                    case 72: 
                    case 73: 
                    case 74: 
                    case 75: 
                    case 76: 
                    case 86: 
                    case 97: 
                    case 98: 
                    case 100: 
                    case 104: 
                    case 110: 
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
                    case 152: {
                        break block4;
                    }
                }
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = newExpression_AST = currentAST.root;
    }

    public final void identPrimary() throws RecognitionException, TokenStreamException {
        AST identPrimary_AST;
        ASTPair currentAST;
        block28: {
            this.returnAST = null;
            currentAST = new ASTPair();
            identPrimary_AST = null;
            AST ta1_AST = null;
            AST ta2_AST = null;
            Token lp = null;
            AST lp_AST = null;
            Token lbc = null;
            AST lbc_AST = null;
            switch (this.LA(1)) {
                case 72: {
                    this.typeArguments();
                    ta1_AST = this.returnAST;
                    break;
                }
                case 67: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            AST tmp256_AST = null;
            tmp256_AST = this.astFactory.create(this.LT(1));
            this.astFactory.addASTChild(currentAST, tmp256_AST);
            this.match(67);
            while (true) {
                boolean synPredMatched303 = false;
                if (this.LA(1) == 68 && (this.LA(2) == 67 || this.LA(2) == 72)) {
                    int _m303 = this.mark();
                    synPredMatched303 = true;
                    ++this.inputState.guessing;
                    try {
                        this.match(68);
                        switch (this.LA(1)) {
                            case 72: {
                                this.typeArguments();
                                break;
                            }
                            case 67: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        this.match(67);
                    }
                    catch (RecognitionException pe) {
                        synPredMatched303 = false;
                    }
                    this.rewind(_m303);
                    --this.inputState.guessing;
                }
                if (!synPredMatched303) break;
                AST tmp257_AST = null;
                tmp257_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp257_AST);
                this.match(68);
                switch (this.LA(1)) {
                    case 72: {
                        this.typeArguments();
                        ta2_AST = this.returnAST;
                        break;
                    }
                    case 67: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                AST tmp258_AST = null;
                tmp258_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp258_AST);
                this.match(67);
            }
            if (!_tokenSet_48.member(this.LA(1)) || !_tokenSet_47.member(this.LA(2))) {
                // empty if block
            }
            if (this.LA(1) == 96) {
                lp = this.LT(1);
                lp_AST = this.astFactory.create(lp);
                this.astFactory.makeASTRoot(currentAST, lp_AST);
                this.match(96);
                if (this.inputState.guessing == 0) {
                    lp_AST.setType(26);
                }
                if (this.inputState.guessing == 0 && ta2_AST != null) {
                    this.astFactory.addASTChild(currentAST, ta2_AST);
                }
                if (this.inputState.guessing == 0 && ta2_AST == null) {
                    this.astFactory.addASTChild(currentAST, ta1_AST);
                }
                this.argList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(97);
            } else {
                if (this.LA(1) == 65 && this.LA(2) == 66) {
                    int _cnt309 = 0;
                    while (true) {
                        if (this.LA(1) != 65 || this.LA(2) != 66) {
                            if (_cnt309 < 1) {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                            break block28;
                        }
                        lbc = this.LT(1);
                        lbc_AST = this.astFactory.create(lbc);
                        this.astFactory.makeASTRoot(currentAST, lbc_AST);
                        this.match(65);
                        if (this.inputState.guessing == 0) {
                            lbc_AST.setType(16);
                        }
                        this.match(66);
                        ++_cnt309;
                    }
                }
                if (!_tokenSet_46.member(this.LA(1)) || !_tokenSet_47.member(this.LA(2))) {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
        }
        this.returnAST = identPrimary_AST = currentAST.root;
    }

    public final void constant() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST constant_AST = null;
        switch (this.LA(1)) {
            case 159: {
                AST tmp261_AST = null;
                tmp261_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp261_AST);
                this.match(159);
                constant_AST = currentAST.root;
                break;
            }
            case 160: {
                AST tmp262_AST = null;
                tmp262_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp262_AST);
                this.match(160);
                constant_AST = currentAST.root;
                break;
            }
            case 161: {
                AST tmp263_AST = null;
                tmp263_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp263_AST);
                this.match(161);
                constant_AST = currentAST.root;
                break;
            }
            case 162: {
                AST tmp264_AST = null;
                tmp264_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp264_AST);
                this.match(162);
                constant_AST = currentAST.root;
                break;
            }
            case 163: {
                AST tmp265_AST = null;
                tmp265_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp265_AST);
                this.match(163);
                constant_AST = currentAST.root;
                break;
            }
            case 164: {
                AST tmp266_AST = null;
                tmp266_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp266_AST);
                this.match(164);
                constant_AST = currentAST.root;
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.returnAST = constant_AST;
    }

    public final void newArrayDeclarator() throws RecognitionException, TokenStreamException {
        this.returnAST = null;
        ASTPair currentAST = new ASTPair();
        AST newArrayDeclarator_AST = null;
        Token lb = null;
        AST lb_AST = null;
        int _cnt320 = 0;
        while (true) {
            if (this.LA(1) == 65 && _tokenSet_49.member(this.LA(2))) {
                lb = this.LT(1);
                lb_AST = this.astFactory.create(lb);
                this.astFactory.makeASTRoot(currentAST, lb_AST);
                this.match(65);
                if (this.inputState.guessing == 0) {
                    lb_AST.setType(16);
                }
                switch (this.LA(1)) {
                    case 67: 
                    case 71: 
                    case 72: 
                    case 77: 
                    case 78: 
                    case 79: 
                    case 80: 
                    case 81: 
                    case 82: 
                    case 83: 
                    case 84: 
                    case 85: 
                    case 96: 
                    case 107: 
                    case 147: 
                    case 148: 
                    case 151: 
                    case 152: 
                    case 153: 
                    case 154: 
                    case 155: 
                    case 156: 
                    case 157: 
                    case 158: 
                    case 159: 
                    case 160: 
                    case 161: 
                    case 162: 
                    case 163: 
                    case 164: {
                        this.expression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 66: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            } else {
                if (_cnt320 >= 1) break;
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
            this.match(66);
            ++_cnt320;
        }
        this.returnAST = newArrayDeclarator_AST = currentAST.root;
    }

    protected void buildTokenTypeASTClassMap() {
        this.tokenTypeToASTClassMap = null;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[]{-4611684094282039294L, 966359252993L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[]{4611687942572736514L, 966359253001L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[]{4611687942572736512L, 966359252993L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[]{4611687942572736514L, 966359252993L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[]{0x1C000000000L, 2139095041L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[]{0L, 4186152L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_6() {
        long[] data = new long[]{0x4000000000000000L, -4611566038511780098L, 32767L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_7() {
        long[] data = new long[]{0L, 6656L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_8() {
        long[] data = new long[]{6917530951786430464L, -3458782106006585345L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_9() {
        long[] data = new long[]{0L, 8836899398024L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_10() {
        long[] data = new long[]{0L, 9979364899770L, 137438952960L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_11() {
        long[] data = new long[]{0L, 8802539659656L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_12() {
        long[] data = new long[]{0L, 4432410443336L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_13() {
        long[] data = new long[]{4611687942572736512L, 5471784132955L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_14() {
        long[] data = new long[]{0x1C000000000L, 966363439369L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_15() {
        long[] data = new long[]{0x1C000000000L, 970658406683L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_16() {
        long[] data = new long[]{0L, 4186120L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_17() {
        long[] data = new long[]{0L, 282L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_18() {
        long[] data = new long[]{0x4000000000000000L, 0x400000402L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_19() {
        long[] data = new long[]{0x4000000000L, 2151669768L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_20() {
        long[] data = new long[]{0L, 2151670042L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_21() {
        long[] data = new long[]{0L, 35186523758874L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_22() {
        long[] data = new long[]{4611687942572736512L, 864277892467515785L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_23() {
        long[] data = new long[]{0L, 0x80000000180L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_24() {
        long[] data = new long[]{0L, 4299153448L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_25() {
        long[] data = new long[]{4611687942572736512L, 864277961186992521L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_26() {
        long[] data = new long[]{4611687942572736512L, -3747335747166798917L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_27() {
        long[] data = new long[]{4611687942572736512L, 1152792011338670473L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_28() {
        long[] data = new long[]{4611687942572736512L, -57183194580037L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_29() {
        long[] data = new long[]{0x1C000000000L, 4290764809L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_30() {
        long[] data = new long[]{0x1C000000000L, 4290765083L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_31() {
        long[] data = new long[]{0L, 8800392176008L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_32() {
        long[] data = new long[]{0x4000000000000000L, -4611676101339513926L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_33() {
        long[] data = new long[]{0x1C000000000L, 141725532161L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_34() {
        long[] data = new long[]{0x1C000000000L, 141725532169L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_35() {
        long[] data = new long[]{0L, 8834751914376L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_36() {
        long[] data = new long[]{4611687942572736512L, 8804678754697L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_37() {
        long[] data = new long[]{4611687942572736512L, -4611676097052934213L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_38() {
        long[] data = new long[]{0L, 79169136353672L, 137432137728L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_39() {
        long[] data = new long[]{0x4000000000000000L, -4611676101339512902L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_40() {
        long[] data = new long[]{0L, 768L, 98304L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_41() {
        long[] data = new long[]{0L, 6144L, 262144L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_42() {
        long[] data = new long[]{0L, 0x400000L, 0x600000L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_43() {
        long[] data = new long[]{0L, 8800392176008L, 137304735744L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_44() {
        long[] data = new long[]{0x4000000000000000L, -4611605655285923906L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_45() {
        long[] data = new long[]{0L, 392L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_46() {
        long[] data = new long[]{0x4000000000000000L, -4611614455678099658L, 0x1FFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_47() {
        long[] data = new long[]{6917530951786430464L, -3458821688425185345L, 0x1FFFFFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_48() {
        long[] data = new long[]{0x4000000000000000L, -4611614451383132362L, 0x1FFFFFFL, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_49() {
        long[] data = new long[]{0L, 8800392176012L, 137432137728L, 0L, 0L, 0L};
        return data;
    }
}

