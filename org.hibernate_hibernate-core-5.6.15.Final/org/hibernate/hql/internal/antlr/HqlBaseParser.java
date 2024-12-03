/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.ASTFactory
 *  antlr.ASTPair
 *  antlr.LLkParser
 *  antlr.NoViableAltException
 *  antlr.ParserSharedInputState
 *  antlr.RecognitionException
 *  antlr.SemanticException
 *  antlr.Token
 *  antlr.TokenBuffer
 *  antlr.TokenStream
 *  antlr.TokenStreamException
 *  antlr.collections.AST
 *  antlr.collections.impl.ASTArray
 *  antlr.collections.impl.BitSet
 */
package org.hibernate.hql.internal.antlr;

import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.LLkParser;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.SemanticException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.AST;
import antlr.collections.impl.ASTArray;
import antlr.collections.impl.BitSet;
import org.hibernate.hql.internal.antlr.HqlTokenTypes;
import org.hibernate.hql.internal.ast.util.ASTUtil;

public class HqlBaseParser
extends LLkParser
implements HqlTokenTypes {
    private boolean filter = false;
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"all\"", "\"any\"", "\"and\"", "\"as\"", "\"asc\"", "\"avg\"", "\"between\"", "\"class\"", "\"count\"", "\"delete\"", "\"desc\"", "DOT", "\"distinct\"", "\"elements\"", "\"escape\"", "\"exists\"", "\"false\"", "\"fetch\"", "FK_REF", "\"from\"", "\"full\"", "\"group\"", "\"having\"", "\"in\"", "\"indices\"", "\"inner\"", "\"insert\"", "\"into\"", "\"is\"", "\"join\"", "\"left\"", "\"like\"", "\"max\"", "\"min\"", "\"new\"", "\"not\"", "\"null\"", "\"or\"", "\"order\"", "\"outer\"", "\"properties\"", "\"right\"", "\"select\"", "\"set\"", "\"some\"", "\"sum\"", "\"true\"", "\"update\"", "\"versioned\"", "\"where\"", "\"nulls\"", "FIRST", "LAST", "\"case\"", "\"end\"", "\"else\"", "\"then\"", "\"when\"", "\"on\"", "\"with\"", "\"both\"", "\"empty\"", "\"leading\"", "\"member\"", "\"object\"", "\"of\"", "\"trailing\"", "KEY", "VALUE", "ENTRY", "AGGREGATE", "ALIAS", "CONSTRUCTOR", "CASE2", "CAST", "COLL_PATH", "EXPR_LIST", "FILTER_ENTITY", "IN_LIST", "INDEX_OP", "IS_NOT_NULL", "IS_NULL", "METHOD_CALL", "NOT_BETWEEN", "NOT_IN", "NOT_LIKE", "ORDER_ELEMENT", "QUERY", "RANGE", "ROW_STAR", "SELECT_FROM", "COLL_SIZE", "UNARY_MINUS", "UNARY_PLUS", "VECTOR_EXPR", "WEIRD_IDENT", "CONSTANT", "NUM_DOUBLE", "NUM_FLOAT", "NUM_LONG", "NUM_BIG_INTEGER", "NUM_BIG_DECIMAL", "JAVA_CONSTANT", "COMMA", "EQ", "OPEN", "CLOSE", "IDENT", "\"by\"", "\"ascending\"", "\"descending\"", "NE", "SQL_NE", "LT", "GT", "LE", "GE", "CONCAT", "PLUS", "MINUS", "STAR", "DIV", "MOD", "OPEN_BRACKET", "CLOSE_BRACKET", "\"fk\"", "QUOTED_STRING", "COLON", "PARAM", "NUM_INT", "ID_START_LETTER", "ID_LETTER", "ESCqs", "WS", "HEX_DIGIT", "EXPONENT", "FLOAT_SUFFIX"};
    public static final BitSet _tokenSet_0 = new BitSet(HqlBaseParser.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(HqlBaseParser.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(HqlBaseParser.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(HqlBaseParser.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(HqlBaseParser.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(HqlBaseParser.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(HqlBaseParser.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(HqlBaseParser.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(HqlBaseParser.mk_tokenSet_8());
    public static final BitSet _tokenSet_9 = new BitSet(HqlBaseParser.mk_tokenSet_9());
    public static final BitSet _tokenSet_10 = new BitSet(HqlBaseParser.mk_tokenSet_10());
    public static final BitSet _tokenSet_11 = new BitSet(HqlBaseParser.mk_tokenSet_11());
    public static final BitSet _tokenSet_12 = new BitSet(HqlBaseParser.mk_tokenSet_12());
    public static final BitSet _tokenSet_13 = new BitSet(HqlBaseParser.mk_tokenSet_13());
    public static final BitSet _tokenSet_14 = new BitSet(HqlBaseParser.mk_tokenSet_14());
    public static final BitSet _tokenSet_15 = new BitSet(HqlBaseParser.mk_tokenSet_15());
    public static final BitSet _tokenSet_16 = new BitSet(HqlBaseParser.mk_tokenSet_16());
    public static final BitSet _tokenSet_17 = new BitSet(HqlBaseParser.mk_tokenSet_17());
    public static final BitSet _tokenSet_18 = new BitSet(HqlBaseParser.mk_tokenSet_18());
    public static final BitSet _tokenSet_19 = new BitSet(HqlBaseParser.mk_tokenSet_19());
    public static final BitSet _tokenSet_20 = new BitSet(HqlBaseParser.mk_tokenSet_20());
    public static final BitSet _tokenSet_21 = new BitSet(HqlBaseParser.mk_tokenSet_21());
    public static final BitSet _tokenSet_22 = new BitSet(HqlBaseParser.mk_tokenSet_22());
    public static final BitSet _tokenSet_23 = new BitSet(HqlBaseParser.mk_tokenSet_23());
    public static final BitSet _tokenSet_24 = new BitSet(HqlBaseParser.mk_tokenSet_24());
    public static final BitSet _tokenSet_25 = new BitSet(HqlBaseParser.mk_tokenSet_25());
    public static final BitSet _tokenSet_26 = new BitSet(HqlBaseParser.mk_tokenSet_26());
    public static final BitSet _tokenSet_27 = new BitSet(HqlBaseParser.mk_tokenSet_27());
    public static final BitSet _tokenSet_28 = new BitSet(HqlBaseParser.mk_tokenSet_28());
    public static final BitSet _tokenSet_29 = new BitSet(HqlBaseParser.mk_tokenSet_29());
    public static final BitSet _tokenSet_30 = new BitSet(HqlBaseParser.mk_tokenSet_30());
    public static final BitSet _tokenSet_31 = new BitSet(HqlBaseParser.mk_tokenSet_31());
    public static final BitSet _tokenSet_32 = new BitSet(HqlBaseParser.mk_tokenSet_32());
    public static final BitSet _tokenSet_33 = new BitSet(HqlBaseParser.mk_tokenSet_33());
    public static final BitSet _tokenSet_34 = new BitSet(HqlBaseParser.mk_tokenSet_34());
    public static final BitSet _tokenSet_35 = new BitSet(HqlBaseParser.mk_tokenSet_35());
    public static final BitSet _tokenSet_36 = new BitSet(HqlBaseParser.mk_tokenSet_36());
    public static final BitSet _tokenSet_37 = new BitSet(HqlBaseParser.mk_tokenSet_37());
    public static final BitSet _tokenSet_38 = new BitSet(HqlBaseParser.mk_tokenSet_38());
    public static final BitSet _tokenSet_39 = new BitSet(HqlBaseParser.mk_tokenSet_39());
    public static final BitSet _tokenSet_40 = new BitSet(HqlBaseParser.mk_tokenSet_40());
    public static final BitSet _tokenSet_41 = new BitSet(HqlBaseParser.mk_tokenSet_41());
    public static final BitSet _tokenSet_42 = new BitSet(HqlBaseParser.mk_tokenSet_42());
    public static final BitSet _tokenSet_43 = new BitSet(HqlBaseParser.mk_tokenSet_43());
    public static final BitSet _tokenSet_44 = new BitSet(HqlBaseParser.mk_tokenSet_44());
    public static final BitSet _tokenSet_45 = new BitSet(HqlBaseParser.mk_tokenSet_45());
    public static final BitSet _tokenSet_46 = new BitSet(HqlBaseParser.mk_tokenSet_46());
    public static final BitSet _tokenSet_47 = new BitSet(HqlBaseParser.mk_tokenSet_47());
    public static final BitSet _tokenSet_48 = new BitSet(HqlBaseParser.mk_tokenSet_48());

    public void setFilter(boolean f) {
        this.filter = f;
    }

    public boolean isFilter() {
        return this.filter;
    }

    public AST handleIdentifierError(Token token, RecognitionException ex) throws RecognitionException, TokenStreamException {
        throw ex;
    }

    public void handleDotIdent() throws TokenStreamException {
    }

    public AST negateNode(AST x) {
        return ASTUtil.createParent(this.astFactory, 39, "not", x);
    }

    public AST processEqualityExpression(AST x) throws RecognitionException {
        return x;
    }

    public void weakKeywords() throws TokenStreamException {
    }

    public void firstPathTokenWeakKeywords() throws TokenStreamException {
    }

    public void handlePrimaryExpressionDotIdent() throws TokenStreamException {
    }

    public void matchOptionalFrom() throws RecognitionException, TokenStreamException {
    }

    public void expectNamedParameterName() throws TokenStreamException {
    }

    public void processMemberOf(Token n, AST p, ASTPair currentAST) {
    }

    protected boolean validateSoftKeyword(String text) throws TokenStreamException {
        return this.validateLookAheadText(1, text);
    }

    protected boolean validateLookAheadText(int lookAheadPosition, String text) throws TokenStreamException {
        String text2Validate = this.retrieveLookAheadText(lookAheadPosition);
        return text2Validate == null ? false : text2Validate.equalsIgnoreCase(text);
    }

    protected String retrieveLookAheadText(int lookAheadPosition) throws TokenStreamException {
        Token token = this.LT(lookAheadPosition);
        return token == null ? null : token.getText();
    }

    protected String unquote(String text) {
        return text.substring(1, text.length() - 1);
    }

    protected void registerTreat(AST pathToTreat, AST treatAs) {
    }

    protected HqlBaseParser(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public HqlBaseParser(TokenBuffer tokenBuf) {
        this(tokenBuf, 3);
    }

    protected HqlBaseParser(TokenStream lexer, int k) {
        super(lexer, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public HqlBaseParser(TokenStream lexer) {
        this(lexer, 3);
    }

    public HqlBaseParser(ParserSharedInputState state) {
        super(state, 3);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void statement() throws RecognitionException, TokenStreamException {
        this.traceIn("statement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST statement_AST = null;
            try {
                switch (this.LA(1)) {
                    case 51: {
                        this.updateStatement();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 13: {
                        this.deleteStatement();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 23: 
                    case 25: 
                    case 42: 
                    case 46: 
                    case 53: {
                        this.selectStatement();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 30: {
                        this.insertStatement();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(1);
                statement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
            this.returnAST = statement_AST;
        }
        finally {
            this.traceOut("statement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void updateStatement() throws RecognitionException, TokenStreamException {
        this.traceIn("updateStatement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST updateStatement_AST = null;
            try {
                AST tmp2_AST = null;
                tmp2_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp2_AST);
                this.match(51);
                this.optionalVersioned();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.optionalFromTokenFromClause();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.setClause();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 53: {
                        this.whereClause();
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
                updateStatement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
            this.returnAST = updateStatement_AST;
        }
        finally {
            this.traceOut("updateStatement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void deleteStatement() throws RecognitionException, TokenStreamException {
        this.traceIn("deleteStatement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST deleteStatement_AST = null;
            try {
                AST tmp3_AST = null;
                tmp3_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp3_AST);
                this.match(13);
                this.optionalFromTokenFromClause();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 53: {
                        this.whereClause();
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
                deleteStatement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
            this.returnAST = deleteStatement_AST;
        }
        finally {
            this.traceOut("deleteStatement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void selectStatement() throws RecognitionException, TokenStreamException {
        this.traceIn("selectStatement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST selectStatement_AST = null;
            try {
                this.queryRule();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                selectStatement_AST = currentAST.root;
                currentAST.root = selectStatement_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(91, "query")).add(selectStatement_AST));
                currentAST.child = selectStatement_AST != null && selectStatement_AST.getFirstChild() != null ? selectStatement_AST.getFirstChild() : selectStatement_AST;
                currentAST.advanceChildToEnd();
                selectStatement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
            this.returnAST = selectStatement_AST;
        }
        finally {
            this.traceOut("selectStatement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void insertStatement() throws RecognitionException, TokenStreamException {
        this.traceIn("insertStatement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST insertStatement_AST = null;
            try {
                AST tmp4_AST = null;
                tmp4_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp4_AST);
                this.match(30);
                this.intoClause();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.selectStatement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                insertStatement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_0);
            }
            this.returnAST = insertStatement_AST;
        }
        finally {
            this.traceOut("insertStatement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void optionalVersioned() throws RecognitionException, TokenStreamException {
        this.traceIn("optionalVersioned");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST optionalVersioned_AST = null;
            try {
                switch (this.LA(1)) {
                    case 52: {
                        AST tmp5_AST = null;
                        tmp5_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp5_AST);
                        this.match(52);
                        break;
                    }
                    case 111: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                optionalVersioned_AST = currentAST.root;
            }
            catch (NoViableAltException noViableAltException) {
                // empty catch block
            }
            this.returnAST = optionalVersioned_AST;
        }
        finally {
            this.traceOut("optionalVersioned");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void optionalFromTokenFromClause() throws RecognitionException, TokenStreamException {
        this.traceIn("optionalFromTokenFromClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST optionalFromTokenFromClause_AST = null;
            AST f_AST = null;
            AST a_AST = null;
            try {
                this.matchOptionalFrom();
                this.path();
                f_AST = this.returnAST;
                switch (this.LA(1)) {
                    case 7: 
                    case 111: {
                        this.asAlias();
                        a_AST = this.returnAST;
                        break;
                    }
                    case 1: 
                    case 47: 
                    case 53: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                optionalFromTokenFromClause_AST = currentAST.root;
                AST range = this.astFactory.make(new ASTArray(3).add(this.astFactory.create(92, "RANGE")).add(f_AST).add(a_AST));
                currentAST.root = optionalFromTokenFromClause_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(23, "FROM")).add(range));
                currentAST.child = optionalFromTokenFromClause_AST != null && optionalFromTokenFromClause_AST.getFirstChild() != null ? optionalFromTokenFromClause_AST.getFirstChild() : optionalFromTokenFromClause_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_1);
            }
            this.returnAST = optionalFromTokenFromClause_AST;
        }
        finally {
            this.traceOut("optionalFromTokenFromClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void setClause() throws RecognitionException, TokenStreamException {
        this.traceIn("setClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST setClause_AST = null;
            try {
                AST tmp6_AST = null;
                tmp6_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp6_AST);
                this.match(47);
                this.assignment();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 107) {
                    this.match(107);
                    this.assignment();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                setClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_2);
            }
            this.returnAST = setClause_AST;
        }
        finally {
            this.traceOut("setClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void whereClause() throws RecognitionException, TokenStreamException {
        this.traceIn("whereClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST whereClause_AST = null;
            try {
                AST tmp8_AST = null;
                tmp8_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp8_AST);
                this.match(53);
                this.logicalExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                whereClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_3);
            }
            this.returnAST = whereClause_AST;
        }
        finally {
            this.traceOut("whereClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void assignment() throws RecognitionException, TokenStreamException {
        this.traceIn("assignment");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST assignment_AST = null;
            try {
                this.stateField();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                AST tmp9_AST = null;
                tmp9_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp9_AST);
                this.match(108);
                this.newValue();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                assignment_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_4);
            }
            this.returnAST = assignment_AST;
        }
        finally {
            this.traceOut("assignment");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void stateField() throws RecognitionException, TokenStreamException {
        this.traceIn("stateField");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST stateField_AST = null;
            try {
                this.path();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                stateField_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_5);
            }
            this.returnAST = stateField_AST;
        }
        finally {
            this.traceOut("stateField");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void newValue() throws RecognitionException, TokenStreamException {
        this.traceIn("newValue");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST newValue_AST = null;
            try {
                this.concatenation();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                newValue_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_4);
            }
            this.returnAST = newValue_AST;
        }
        finally {
            this.traceOut("newValue");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void path() throws RecognitionException, TokenStreamException {
        this.traceIn("path");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST path_AST = null;
            try {
                this.firstPathTokenWeakKeywords();
                this.identifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 15) {
                    AST tmp10_AST = null;
                    tmp10_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp10_AST);
                    this.match(15);
                    this.weakKeywords();
                    this.identifier();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                path_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_6);
            }
            this.returnAST = path_AST;
        }
        finally {
            this.traceOut("path");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void concatenation() throws RecognitionException, TokenStreamException {
        this.traceIn("concatenation");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST concatenation_AST = null;
            Token c = null;
            AST c_AST = null;
            try {
                this.additiveExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 121: {
                        c = this.LT(1);
                        c_AST = this.astFactory.create(c);
                        this.astFactory.makeASTRoot(currentAST, c_AST);
                        this.match(121);
                        c_AST.setType(80);
                        c_AST.setText("concatList");
                        this.additiveExpression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        while (this.LA(1) == 121) {
                            this.match(121);
                            this.additiveExpression();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                        }
                        concatenation_AST = currentAST.root;
                        currentAST.root = concatenation_AST = this.astFactory.make(new ASTArray(3).add(this.astFactory.create(86, "||")).add(this.astFactory.make(new ASTArray(1).add(this.astFactory.create(111, "concat")))).add(c_AST));
                        currentAST.child = concatenation_AST != null && concatenation_AST.getFirstChild() != null ? concatenation_AST.getFirstChild() : concatenation_AST;
                        currentAST.advanceChildToEnd();
                        break;
                    }
                    case 1: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 10: 
                    case 14: 
                    case 18: 
                    case 23: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 27: 
                    case 29: 
                    case 32: 
                    case 33: 
                    case 34: 
                    case 35: 
                    case 39: 
                    case 41: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 54: 
                    case 60: 
                    case 67: 
                    case 107: 
                    case 108: 
                    case 110: 
                    case 111: 
                    case 113: 
                    case 114: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 128: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                concatenation_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_7);
            }
            this.returnAST = concatenation_AST;
        }
        finally {
            this.traceOut("concatenation");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void asAlias() throws RecognitionException, TokenStreamException {
        this.traceIn("asAlias");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST asAlias_AST = null;
            try {
                switch (this.LA(1)) {
                    case 7: {
                        this.match(7);
                        break;
                    }
                    case 111: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.alias();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                asAlias_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_8);
            }
            this.returnAST = asAlias_AST;
        }
        finally {
            this.traceOut("asAlias");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void queryRule() throws RecognitionException, TokenStreamException {
        this.traceIn("queryRule");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST queryRule_AST = null;
            try {
                this.selectFrom();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 53: {
                        this.whereClause();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 25: 
                    case 42: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 25: {
                        this.groupByClause();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 42: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 42: {
                        this.orderByClause();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                queryRule_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_9);
            }
            this.returnAST = queryRule_AST;
        }
        finally {
            this.traceOut("queryRule");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void intoClause() throws RecognitionException, TokenStreamException {
        this.traceIn("intoClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST intoClause_AST = null;
            try {
                AST tmp13_AST = null;
                tmp13_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp13_AST);
                this.match(31);
                this.path();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.weakKeywords();
                this.insertablePropertySpec();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                intoClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_10);
            }
            this.returnAST = intoClause_AST;
        }
        finally {
            this.traceOut("intoClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void insertablePropertySpec() throws RecognitionException, TokenStreamException {
        this.traceIn("insertablePropertySpec");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST insertablePropertySpec_AST = null;
            try {
                this.match(109);
                this.primaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 107) {
                    this.match(107);
                    this.primaryExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                this.match(110);
                insertablePropertySpec_AST = currentAST.root;
                currentAST.root = insertablePropertySpec_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(92, "column-spec")).add(insertablePropertySpec_AST));
                currentAST.child = insertablePropertySpec_AST != null && insertablePropertySpec_AST.getFirstChild() != null ? insertablePropertySpec_AST.getFirstChild() : insertablePropertySpec_AST;
                currentAST.advanceChildToEnd();
                insertablePropertySpec_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_10);
            }
            this.returnAST = insertablePropertySpec_AST;
        }
        finally {
            this.traceOut("insertablePropertySpec");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void primaryExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("primaryExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST primaryExpression_AST = null;
            try {
                switch (this.LA(1)) {
                    case 20: 
                    case 40: 
                    case 50: 
                    case 65: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 130: 
                    case 133: {
                        this.constant();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        primaryExpression_AST = currentAST.root;
                        break;
                    }
                    case 131: 
                    case 132: {
                        this.parameter();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        primaryExpression_AST = currentAST.root;
                        break;
                    }
                    case 109: {
                        this.match(109);
                        switch (this.LA(1)) {
                            case 4: 
                            case 5: 
                            case 9: 
                            case 12: 
                            case 17: 
                            case 19: 
                            case 20: 
                            case 28: 
                            case 36: 
                            case 37: 
                            case 39: 
                            case 40: 
                            case 48: 
                            case 49: 
                            case 50: 
                            case 57: 
                            case 65: 
                            case 101: 
                            case 102: 
                            case 103: 
                            case 104: 
                            case 105: 
                            case 109: 
                            case 111: 
                            case 122: 
                            case 123: 
                            case 129: 
                            case 130: 
                            case 131: 
                            case 132: 
                            case 133: {
                                this.expressionOrVector();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break;
                            }
                            case 23: 
                            case 25: 
                            case 42: 
                            case 46: 
                            case 53: 
                            case 110: {
                                this.subQuery();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        this.match(110);
                        primaryExpression_AST = currentAST.root;
                        break;
                    }
                    default: {
                        if (this.LA(1) == 129 && this.validateSoftKeyword("fk") && this.LA(2) == 109) {
                            this.fkRefPath();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            primaryExpression_AST = currentAST.root;
                            break;
                        }
                        if (this.LA(1) == 111 && this.LA(2) == 109 && this.LA(3) == 130 && this.validateSoftKeyword("function") && this.LA(2) == 109 && this.LA(3) == 130) {
                            this.jpaFunctionSyntax();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            primaryExpression_AST = currentAST.root;
                            break;
                        }
                        if (this.LA(1) == 111 && this.LA(2) == 109 && _tokenSet_11.member(this.LA(3)) && this.validateSoftKeyword("cast") && this.LA(2) == 109) {
                            this.castFunction();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            primaryExpression_AST = currentAST.root;
                            break;
                        }
                        if (this.LA(1) == 111 && this.LA(2) == 109 && this.LA(3) == 111 && this.validateSoftKeyword("size") && this.LA(2) == 109) {
                            this.collectionSizeFunction();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            primaryExpression_AST = currentAST.root;
                            break;
                        }
                        if (_tokenSet_12.member(this.LA(1)) && _tokenSet_13.member(this.LA(2)) && _tokenSet_14.member(this.LA(3))) {
                            this.identPrimary();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            if (this.LA(1) == 15 && this.LA(2) == 11) {
                                AST tmp19_AST = null;
                                tmp19_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.makeASTRoot(currentAST, tmp19_AST);
                                this.match(15);
                                AST tmp20_AST = null;
                                tmp20_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp20_AST);
                                this.match(11);
                            } else if (!_tokenSet_15.member(this.LA(1)) || !_tokenSet_16.member(this.LA(2))) {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                            primaryExpression_AST = currentAST.root;
                            break;
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = primaryExpression_AST;
        }
        finally {
            this.traceOut("primaryExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void selectFrom() throws RecognitionException, TokenStreamException {
        this.traceIn("selectFrom");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST selectFrom_AST = null;
            AST s_AST = null;
            AST f_AST = null;
            try {
                switch (this.LA(1)) {
                    case 46: {
                        this.selectClause();
                        s_AST = this.returnAST;
                        break;
                    }
                    case 1: 
                    case 23: 
                    case 25: 
                    case 42: 
                    case 53: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 23: {
                        this.fromClause();
                        f_AST = this.returnAST;
                        break;
                    }
                    case 1: 
                    case 25: 
                    case 42: 
                    case 53: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                selectFrom_AST = currentAST.root;
                if (f_AST == null) {
                    if (this.filter) {
                        f_AST = this.astFactory.make(new ASTArray(1).add(this.astFactory.create(23, "{filter-implied FROM}")));
                    } else {
                        throw new SemanticException("FROM expected (non-filter queries must contain a FROM clause)");
                    }
                }
                currentAST.root = selectFrom_AST = this.astFactory.make(new ASTArray(3).add(this.astFactory.create(94, "SELECT_FROM")).add(f_AST).add(s_AST));
                currentAST.child = selectFrom_AST != null && selectFrom_AST.getFirstChild() != null ? selectFrom_AST.getFirstChild() : selectFrom_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_17);
            }
            this.returnAST = selectFrom_AST;
        }
        finally {
            this.traceOut("selectFrom");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void groupByClause() throws RecognitionException, TokenStreamException {
        this.traceIn("groupByClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST groupByClause_AST = null;
            try {
                AST tmp21_AST = null;
                tmp21_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp21_AST);
                this.match(25);
                this.match(112);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 107) {
                    this.match(107);
                    this.expression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                switch (this.LA(1)) {
                    case 26: {
                        this.havingClause();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 42: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                groupByClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_18);
            }
            this.returnAST = groupByClause_AST;
        }
        finally {
            this.traceOut("groupByClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void orderByClause() throws RecognitionException, TokenStreamException {
        this.traceIn("orderByClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST orderByClause_AST = null;
            try {
                AST tmp24_AST = null;
                tmp24_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp24_AST);
                this.match(42);
                this.match(112);
                this.orderElement();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 107) {
                    this.match(107);
                    this.orderElement();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                orderByClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_9);
            }
            this.returnAST = orderByClause_AST;
        }
        finally {
            this.traceOut("orderByClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void selectClause() throws RecognitionException, TokenStreamException {
        this.traceIn("selectClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST selectClause_AST = null;
            try {
                AST tmp27_AST = null;
                tmp27_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp27_AST);
                this.match(46);
                this.weakKeywords();
                switch (this.LA(1)) {
                    case 16: {
                        AST tmp28_AST = null;
                        tmp28_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp28_AST);
                        this.match(16);
                        break;
                    }
                    case 4: 
                    case 5: 
                    case 9: 
                    case 12: 
                    case 17: 
                    case 19: 
                    case 20: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 38: 
                    case 39: 
                    case 40: 
                    case 48: 
                    case 49: 
                    case 50: 
                    case 57: 
                    case 65: 
                    case 68: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 109: 
                    case 111: 
                    case 122: 
                    case 123: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 133: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 4: 
                    case 5: 
                    case 9: 
                    case 12: 
                    case 17: 
                    case 19: 
                    case 20: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 39: 
                    case 40: 
                    case 48: 
                    case 49: 
                    case 50: 
                    case 57: 
                    case 65: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 109: 
                    case 111: 
                    case 122: 
                    case 123: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 133: {
                        this.selectedPropertiesList();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 38: {
                        this.newExpression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 68: {
                        this.selectObject();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                selectClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_19);
            }
            this.returnAST = selectClause_AST;
        }
        finally {
            this.traceOut("selectClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void fromClause() throws RecognitionException, TokenStreamException {
        this.traceIn("fromClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST fromClause_AST = null;
            try {
                AST tmp29_AST = null;
                tmp29_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp29_AST);
                this.match(23);
                this.weakKeywords();
                this.fromRange();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                block9: while (true) {
                    switch (this.LA(1)) {
                        case 24: 
                        case 29: 
                        case 33: 
                        case 34: 
                        case 45: {
                            this.fromJoin();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            continue block9;
                        }
                        case 107: {
                            this.match(107);
                            this.weakKeywords();
                            this.fromRange();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            continue block9;
                        }
                    }
                    break;
                }
                fromClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_17);
            }
            this.returnAST = fromClause_AST;
        }
        finally {
            this.traceOut("fromClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void selectedPropertiesList() throws RecognitionException, TokenStreamException {
        this.traceIn("selectedPropertiesList");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST selectedPropertiesList_AST = null;
            try {
                this.aliasedExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 107) {
                    this.match(107);
                    this.aliasedExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                selectedPropertiesList_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_19);
            }
            this.returnAST = selectedPropertiesList_AST;
        }
        finally {
            this.traceOut("selectedPropertiesList");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void newExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("newExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST newExpression_AST = null;
            Token op = null;
            AST op_AST = null;
            try {
                this.match(38);
                this.path();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                op = this.LT(1);
                op_AST = this.astFactory.create(op);
                this.astFactory.makeASTRoot(currentAST, op_AST);
                this.match(109);
                op_AST.setType(76);
                this.selectedPropertiesList();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(110);
                newExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_19);
            }
            this.returnAST = newExpression_AST;
        }
        finally {
            this.traceOut("newExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void selectObject() throws RecognitionException, TokenStreamException {
        this.traceIn("selectObject");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST selectObject_AST = null;
            try {
                AST tmp34_AST = null;
                tmp34_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp34_AST);
                this.match(68);
                this.match(109);
                this.identifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(110);
                selectObject_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_19);
            }
            this.returnAST = selectObject_AST;
        }
        finally {
            this.traceOut("selectObject");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void identifier() throws RecognitionException, TokenStreamException {
        this.traceIn("identifier");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST identifier_AST = null;
            try {
                AST tmp37_AST = null;
                tmp37_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp37_AST);
                this.match(111);
                identifier_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                identifier_AST = this.handleIdentifierError(this.LT(1), ex);
            }
            this.returnAST = identifier_AST;
        }
        finally {
            this.traceOut("identifier");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void fromRange() throws RecognitionException, TokenStreamException {
        this.traceIn("fromRange");
        try {
            AST fromRange_AST;
            block9: {
                this.returnAST = null;
                ASTPair currentAST = new ASTPair();
                fromRange_AST = null;
                try {
                    if (this.LA(1) == 111 && _tokenSet_20.member(this.LA(2))) {
                        this.fromClassOrOuterQueryPath();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        fromRange_AST = currentAST.root;
                        break block9;
                    }
                    if (this.LA(1) == 111 && this.LA(2) == 27 && this.LA(3) == 11) {
                        this.inClassDeclaration();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        fromRange_AST = currentAST.root;
                        break block9;
                    }
                    if (this.LA(1) == 27) {
                        this.inCollectionDeclaration();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        fromRange_AST = currentAST.root;
                        break block9;
                    }
                    if (this.LA(1) == 111 && this.LA(2) == 27 && this.LA(3) == 17) {
                        this.inCollectionElementsDeclaration();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        fromRange_AST = currentAST.root;
                        break block9;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException ex) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_21);
                }
            }
            this.returnAST = fromRange_AST;
        }
        finally {
            this.traceOut("fromRange");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void fromJoin() throws RecognitionException, TokenStreamException {
        this.traceIn("fromJoin");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST fromJoin_AST = null;
            try {
                block2 : switch (this.LA(1)) {
                    case 34: 
                    case 45: {
                        switch (this.LA(1)) {
                            case 34: {
                                AST tmp38_AST = null;
                                tmp38_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp38_AST);
                                this.match(34);
                                break;
                            }
                            case 45: {
                                AST tmp39_AST = null;
                                tmp39_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp39_AST);
                                this.match(45);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        switch (this.LA(1)) {
                            case 43: {
                                AST tmp40_AST = null;
                                tmp40_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp40_AST);
                                this.match(43);
                                break block2;
                            }
                            case 33: {
                                break block2;
                            }
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    case 24: {
                        AST tmp41_AST = null;
                        tmp41_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp41_AST);
                        this.match(24);
                        break;
                    }
                    case 29: {
                        AST tmp42_AST = null;
                        tmp42_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp42_AST);
                        this.match(29);
                        break;
                    }
                    case 33: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                AST tmp43_AST = null;
                tmp43_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp43_AST);
                this.match(33);
                switch (this.LA(1)) {
                    case 21: {
                        AST tmp44_AST = null;
                        tmp44_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp44_AST);
                        this.match(21);
                        break;
                    }
                    case 111: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.joinPath();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 7: 
                    case 111: {
                        this.asAlias();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 29: 
                    case 33: 
                    case 34: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 62: 
                    case 63: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 21: {
                        this.propertyFetch();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 24: 
                    case 25: 
                    case 29: 
                    case 33: 
                    case 34: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 62: 
                    case 63: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 62: 
                    case 63: {
                        this.withClause();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 24: 
                    case 25: 
                    case 29: 
                    case 33: 
                    case 34: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                fromJoin_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_21);
            }
            this.returnAST = fromJoin_AST;
        }
        finally {
            this.traceOut("fromJoin");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void joinPath() throws RecognitionException, TokenStreamException {
        this.traceIn("joinPath");
        try {
            AST joinPath_AST;
            block7: {
                this.returnAST = null;
                ASTPair currentAST = new ASTPair();
                joinPath_AST = null;
                try {
                    if (this.LA(1) == 111 && this.LA(2) == 109 && this.validateSoftKeyword("treat") && this.LA(2) == 109) {
                        this.castedJoinPath();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        joinPath_AST = currentAST.root;
                        break block7;
                    }
                    if (this.LA(1) == 111 && _tokenSet_22.member(this.LA(2))) {
                        this.path();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        joinPath_AST = currentAST.root;
                        break block7;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException ex) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_23);
                }
            }
            this.returnAST = joinPath_AST;
        }
        finally {
            this.traceOut("joinPath");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void propertyFetch() throws RecognitionException, TokenStreamException {
        this.traceIn("propertyFetch");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST propertyFetch_AST = null;
            try {
                AST tmp45_AST = null;
                tmp45_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp45_AST);
                this.match(21);
                this.match(4);
                this.match(44);
                propertyFetch_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_24);
            }
            this.returnAST = propertyFetch_AST;
        }
        finally {
            this.traceOut("propertyFetch");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void withClause() throws RecognitionException, TokenStreamException {
        this.traceIn("withClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST withClause_AST = null;
            AST le_AST = null;
            try {
                switch (this.LA(1)) {
                    case 63: {
                        AST tmp48_AST = null;
                        tmp48_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp48_AST);
                        this.match(63);
                        this.logicalExpression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        withClause_AST = currentAST.root;
                        break;
                    }
                    case 62: {
                        this.match(62);
                        this.logicalExpression();
                        le_AST = this.returnAST;
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        withClause_AST = currentAST.root;
                        currentAST.root = withClause_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(63, "with")).add(le_AST));
                        currentAST.child = withClause_AST != null && withClause_AST.getFirstChild() != null ? withClause_AST.getFirstChild() : withClause_AST;
                        currentAST.advanceChildToEnd();
                        withClause_AST = currentAST.root;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_21);
            }
            this.returnAST = withClause_AST;
        }
        finally {
            this.traceOut("withClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void castedJoinPath() throws RecognitionException, TokenStreamException {
        this.traceIn("castedJoinPath");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST castedJoinPath_AST = null;
            Token i = null;
            AST i_AST = null;
            AST p_AST = null;
            AST a_AST = null;
            try {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.match(111);
                this.match(109);
                this.path();
                p_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(7);
                this.path();
                a_AST = this.returnAST;
                this.match(110);
                if (!i.getText().equalsIgnoreCase("treat")) {
                    throw new SemanticException("i.getText().equalsIgnoreCase(\"treat\") ");
                }
                this.registerTreat(p_AST, a_AST);
                castedJoinPath_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_23);
            }
            this.returnAST = castedJoinPath_AST;
        }
        finally {
            this.traceOut("castedJoinPath");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void logicalExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("logicalExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST logicalExpression_AST = null;
            try {
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                logicalExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_25);
            }
            this.returnAST = logicalExpression_AST;
        }
        finally {
            this.traceOut("logicalExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void fromClassOrOuterQueryPath() throws RecognitionException, TokenStreamException {
        this.traceIn("fromClassOrOuterQueryPath");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST fromClassOrOuterQueryPath_AST = null;
            AST c_AST = null;
            AST a_AST = null;
            AST p_AST = null;
            try {
                this.path();
                c_AST = this.returnAST;
                this.weakKeywords();
                switch (this.LA(1)) {
                    case 7: 
                    case 111: {
                        this.asAlias();
                        a_AST = this.returnAST;
                        break;
                    }
                    case 1: 
                    case 21: 
                    case 24: 
                    case 25: 
                    case 29: 
                    case 33: 
                    case 34: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 21: {
                        this.propertyFetch();
                        p_AST = this.returnAST;
                        break;
                    }
                    case 1: 
                    case 24: 
                    case 25: 
                    case 29: 
                    case 33: 
                    case 34: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                fromClassOrOuterQueryPath_AST = currentAST.root;
                currentAST.root = fromClassOrOuterQueryPath_AST = this.astFactory.make(new ASTArray(4).add(this.astFactory.create(92, "RANGE")).add(c_AST).add(a_AST).add(p_AST));
                currentAST.child = fromClassOrOuterQueryPath_AST != null && fromClassOrOuterQueryPath_AST.getFirstChild() != null ? fromClassOrOuterQueryPath_AST.getFirstChild() : fromClassOrOuterQueryPath_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_21);
            }
            this.returnAST = fromClassOrOuterQueryPath_AST;
        }
        finally {
            this.traceOut("fromClassOrOuterQueryPath");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void inClassDeclaration() throws RecognitionException, TokenStreamException {
        this.traceIn("inClassDeclaration");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST inClassDeclaration_AST = null;
            AST a_AST = null;
            AST c_AST = null;
            try {
                this.alias();
                a_AST = this.returnAST;
                this.match(27);
                this.match(11);
                this.path();
                c_AST = this.returnAST;
                inClassDeclaration_AST = currentAST.root;
                currentAST.root = inClassDeclaration_AST = this.astFactory.make(new ASTArray(3).add(this.astFactory.create(92, "RANGE")).add(c_AST).add(a_AST));
                currentAST.child = inClassDeclaration_AST != null && inClassDeclaration_AST.getFirstChild() != null ? inClassDeclaration_AST.getFirstChild() : inClassDeclaration_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_21);
            }
            this.returnAST = inClassDeclaration_AST;
        }
        finally {
            this.traceOut("inClassDeclaration");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void inCollectionDeclaration() throws RecognitionException, TokenStreamException {
        this.traceIn("inCollectionDeclaration");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST inCollectionDeclaration_AST = null;
            AST p_AST = null;
            AST a_AST = null;
            try {
                this.match(27);
                this.match(109);
                this.path();
                p_AST = this.returnAST;
                this.match(110);
                this.asAlias();
                a_AST = this.returnAST;
                inCollectionDeclaration_AST = currentAST.root;
                currentAST.root = inCollectionDeclaration_AST = this.astFactory.make(new ASTArray(4).add(this.astFactory.create(33, "join")).add(this.astFactory.create(29, "inner")).add(p_AST).add(a_AST));
                currentAST.child = inCollectionDeclaration_AST != null && inCollectionDeclaration_AST.getFirstChild() != null ? inCollectionDeclaration_AST.getFirstChild() : inCollectionDeclaration_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_21);
            }
            this.returnAST = inCollectionDeclaration_AST;
        }
        finally {
            this.traceOut("inCollectionDeclaration");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void inCollectionElementsDeclaration() throws RecognitionException, TokenStreamException {
        this.traceIn("inCollectionElementsDeclaration");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST inCollectionElementsDeclaration_AST = null;
            AST a_AST = null;
            AST p_AST = null;
            try {
                this.alias();
                a_AST = this.returnAST;
                this.match(27);
                this.match(17);
                this.match(109);
                this.path();
                p_AST = this.returnAST;
                this.match(110);
                inCollectionElementsDeclaration_AST = currentAST.root;
                currentAST.root = inCollectionElementsDeclaration_AST = this.astFactory.make(new ASTArray(4).add(this.astFactory.create(33, "join")).add(this.astFactory.create(29, "inner")).add(p_AST).add(a_AST));
                currentAST.child = inCollectionElementsDeclaration_AST != null && inCollectionElementsDeclaration_AST.getFirstChild() != null ? inCollectionElementsDeclaration_AST.getFirstChild() : inCollectionElementsDeclaration_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_21);
            }
            this.returnAST = inCollectionElementsDeclaration_AST;
        }
        finally {
            this.traceOut("inCollectionElementsDeclaration");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void alias() throws RecognitionException, TokenStreamException {
        this.traceIn("alias");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST alias_AST = null;
            AST a_AST = null;
            try {
                this.identifier();
                a_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                a_AST.setType(75);
                alias_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_26);
            }
            this.returnAST = alias_AST;
        }
        finally {
            this.traceOut("alias");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void expression() throws RecognitionException, TokenStreamException {
        this.traceIn("expression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST expression_AST = null;
            try {
                this.logicalOrExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                expression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_27);
            }
            this.returnAST = expression_AST;
        }
        finally {
            this.traceOut("expression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void havingClause() throws RecognitionException, TokenStreamException {
        this.traceIn("havingClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST havingClause_AST = null;
            try {
                AST tmp62_AST = null;
                tmp62_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp62_AST);
                this.match(26);
                this.logicalExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                havingClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_18);
            }
            this.returnAST = havingClause_AST;
        }
        finally {
            this.traceOut("havingClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void orderElement() throws RecognitionException, TokenStreamException {
        this.traceIn("orderElement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST orderElement_AST = null;
            try {
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 8: 
                    case 14: 
                    case 113: 
                    case 114: {
                        this.ascendingOrDescending();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 54: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 54: {
                        this.nullOrdering();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                orderElement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_28);
            }
            this.returnAST = orderElement_AST;
        }
        finally {
            this.traceOut("orderElement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void ascendingOrDescending() throws RecognitionException, TokenStreamException {
        this.traceIn("ascendingOrDescending");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST ascendingOrDescending_AST = null;
            try {
                switch (this.LA(1)) {
                    case 8: 
                    case 113: {
                        switch (this.LA(1)) {
                            case 8: {
                                AST tmp63_AST = null;
                                tmp63_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp63_AST);
                                this.match(8);
                                break;
                            }
                            case 113: {
                                AST tmp64_AST = null;
                                tmp64_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp64_AST);
                                this.match(113);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        ascendingOrDescending_AST = currentAST.root;
                        ascendingOrDescending_AST.setType(8);
                        ascendingOrDescending_AST = currentAST.root;
                        break;
                    }
                    case 14: 
                    case 114: {
                        switch (this.LA(1)) {
                            case 14: {
                                AST tmp65_AST = null;
                                tmp65_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp65_AST);
                                this.match(14);
                                break;
                            }
                            case 114: {
                                AST tmp66_AST = null;
                                tmp66_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp66_AST);
                                this.match(114);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        ascendingOrDescending_AST = currentAST.root;
                        ascendingOrDescending_AST.setType(14);
                        ascendingOrDescending_AST = currentAST.root;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_29);
            }
            this.returnAST = ascendingOrDescending_AST;
        }
        finally {
            this.traceOut("ascendingOrDescending");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void nullOrdering() throws RecognitionException, TokenStreamException {
        this.traceIn("nullOrdering");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST nullOrdering_AST = null;
            try {
                AST tmp67_AST = null;
                tmp67_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp67_AST);
                this.match(54);
                this.nullPrecedence();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                nullOrdering_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_28);
            }
            this.returnAST = nullOrdering_AST;
        }
        finally {
            this.traceOut("nullOrdering");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void nullPrecedence() throws RecognitionException, TokenStreamException {
        this.traceIn("nullPrecedence");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST nullPrecedence_AST = null;
            try {
                AST tmp68_AST = null;
                tmp68_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp68_AST);
                this.match(111);
                nullPrecedence_AST = currentAST.root;
                if ("first".equalsIgnoreCase(nullPrecedence_AST.getText())) {
                    nullPrecedence_AST.setType(55);
                } else if ("last".equalsIgnoreCase(nullPrecedence_AST.getText())) {
                    nullPrecedence_AST.setType(56);
                } else {
                    throw new SemanticException("Expecting 'first' or 'last', but found '" + nullPrecedence_AST.getText() + "' as null ordering precedence.");
                }
                nullPrecedence_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_28);
            }
            this.returnAST = nullPrecedence_AST;
        }
        finally {
            this.traceOut("nullPrecedence");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void aliasedExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("aliasedExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST aliasedExpression_AST = null;
            try {
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 7: {
                        AST tmp69_AST = null;
                        tmp69_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp69_AST);
                        this.match(7);
                        this.identifier();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 23: 
                    case 25: 
                    case 42: 
                    case 53: 
                    case 107: 
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                aliasedExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_30);
            }
            this.returnAST = aliasedExpression_AST;
        }
        finally {
            this.traceOut("aliasedExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("logicalOrExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST logicalOrExpression_AST = null;
            try {
                this.logicalAndExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 41) {
                    AST tmp70_AST = null;
                    tmp70_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp70_AST);
                    this.match(41);
                    this.logicalAndExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                logicalOrExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_27);
            }
            this.returnAST = logicalOrExpression_AST;
        }
        finally {
            this.traceOut("logicalOrExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void logicalAndExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("logicalAndExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST logicalAndExpression_AST = null;
            try {
                this.negatedExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 6) {
                    AST tmp71_AST = null;
                    tmp71_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp71_AST);
                    this.match(6);
                    this.negatedExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                logicalAndExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_31);
            }
            this.returnAST = logicalAndExpression_AST;
        }
        finally {
            this.traceOut("logicalAndExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void negatedExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("negatedExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST negatedExpression_AST = null;
            AST x_AST = null;
            AST y_AST = null;
            this.weakKeywords();
            try {
                switch (this.LA(1)) {
                    case 39: {
                        AST tmp72_AST = null;
                        tmp72_AST = this.astFactory.create(this.LT(1));
                        this.match(39);
                        this.negatedExpression();
                        x_AST = this.returnAST;
                        negatedExpression_AST = currentAST.root;
                        currentAST.root = negatedExpression_AST = this.negateNode(x_AST);
                        currentAST.child = negatedExpression_AST != null && negatedExpression_AST.getFirstChild() != null ? negatedExpression_AST.getFirstChild() : negatedExpression_AST;
                        currentAST.advanceChildToEnd();
                        break;
                    }
                    case 4: 
                    case 5: 
                    case 9: 
                    case 12: 
                    case 17: 
                    case 19: 
                    case 20: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 40: 
                    case 48: 
                    case 49: 
                    case 50: 
                    case 57: 
                    case 65: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 109: 
                    case 111: 
                    case 122: 
                    case 123: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 133: {
                        this.equalityExpression();
                        y_AST = this.returnAST;
                        negatedExpression_AST = currentAST.root;
                        currentAST.root = negatedExpression_AST = y_AST;
                        currentAST.child = negatedExpression_AST != null && negatedExpression_AST.getFirstChild() != null ? negatedExpression_AST.getFirstChild() : negatedExpression_AST;
                        currentAST.advanceChildToEnd();
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_32);
            }
            this.returnAST = negatedExpression_AST;
        }
        finally {
            this.traceOut("negatedExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void equalityExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("equalityExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST equalityExpression_AST = null;
            AST x_AST = null;
            Token is = null;
            AST is_AST = null;
            Token ne = null;
            AST ne_AST = null;
            AST y_AST = null;
            try {
                this.relationalExpression();
                x_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (_tokenSet_33.member(this.LA(1))) {
                    block2 : switch (this.LA(1)) {
                        case 108: {
                            AST tmp73_AST = null;
                            tmp73_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp73_AST);
                            this.match(108);
                            break;
                        }
                        case 32: {
                            is = this.LT(1);
                            is_AST = this.astFactory.create(is);
                            this.astFactory.makeASTRoot(currentAST, is_AST);
                            this.match(32);
                            is_AST.setType(108);
                            switch (this.LA(1)) {
                                case 39: {
                                    this.match(39);
                                    is_AST.setType(115);
                                    break block2;
                                }
                                case 4: 
                                case 5: 
                                case 9: 
                                case 12: 
                                case 17: 
                                case 19: 
                                case 20: 
                                case 28: 
                                case 36: 
                                case 37: 
                                case 40: 
                                case 48: 
                                case 49: 
                                case 50: 
                                case 57: 
                                case 65: 
                                case 101: 
                                case 102: 
                                case 103: 
                                case 104: 
                                case 105: 
                                case 109: 
                                case 111: 
                                case 122: 
                                case 123: 
                                case 129: 
                                case 130: 
                                case 131: 
                                case 132: 
                                case 133: {
                                    break block2;
                                }
                            }
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        case 115: {
                            AST tmp75_AST = null;
                            tmp75_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp75_AST);
                            this.match(115);
                            break;
                        }
                        case 116: {
                            ne = this.LT(1);
                            ne_AST = this.astFactory.create(ne);
                            this.astFactory.makeASTRoot(currentAST, ne_AST);
                            this.match(116);
                            ne_AST.setType(115);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.relationalExpression();
                    y_AST = this.returnAST;
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                equalityExpression_AST = currentAST.root;
                currentAST.root = equalityExpression_AST = this.processEqualityExpression(equalityExpression_AST);
                currentAST.child = equalityExpression_AST != null && equalityExpression_AST.getFirstChild() != null ? equalityExpression_AST.getFirstChild() : equalityExpression_AST;
                currentAST.advanceChildToEnd();
                equalityExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_32);
            }
            this.returnAST = equalityExpression_AST;
        }
        finally {
            this.traceOut("equalityExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void relationalExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("relationalExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST relationalExpression_AST = null;
            Token n = null;
            AST n_AST = null;
            Token i = null;
            AST i_AST = null;
            Token b = null;
            AST b_AST = null;
            Token l = null;
            AST l_AST = null;
            AST p_AST = null;
            try {
                switch (this.LA(1)) {
                    case 9: 
                    case 12: 
                    case 17: 
                    case 20: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 40: 
                    case 49: 
                    case 50: 
                    case 57: 
                    case 65: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 109: 
                    case 111: 
                    case 122: 
                    case 123: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 133: {
                        this.concatenation();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 4: 
                    case 5: 
                    case 19: 
                    case 48: {
                        this.quantifiedExpression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                block6 : switch (this.LA(1)) {
                    case 1: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 14: 
                    case 23: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 29: 
                    case 32: 
                    case 33: 
                    case 34: 
                    case 41: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 54: 
                    case 60: 
                    case 107: 
                    case 108: 
                    case 110: 
                    case 111: 
                    case 113: 
                    case 114: 
                    case 115: 
                    case 116: 
                    case 117: 
                    case 118: 
                    case 119: 
                    case 120: 
                    case 128: {
                        block37: while (this.LA(1) >= 117 && this.LA(1) <= 120) {
                            switch (this.LA(1)) {
                                case 117: {
                                    AST tmp76_AST = null;
                                    tmp76_AST = this.astFactory.create(this.LT(1));
                                    this.astFactory.makeASTRoot(currentAST, tmp76_AST);
                                    this.match(117);
                                    break;
                                }
                                case 118: {
                                    AST tmp77_AST = null;
                                    tmp77_AST = this.astFactory.create(this.LT(1));
                                    this.astFactory.makeASTRoot(currentAST, tmp77_AST);
                                    this.match(118);
                                    break;
                                }
                                case 119: {
                                    AST tmp78_AST = null;
                                    tmp78_AST = this.astFactory.create(this.LT(1));
                                    this.astFactory.makeASTRoot(currentAST, tmp78_AST);
                                    this.match(119);
                                    break;
                                }
                                case 120: {
                                    AST tmp79_AST = null;
                                    tmp79_AST = this.astFactory.create(this.LT(1));
                                    this.astFactory.makeASTRoot(currentAST, tmp79_AST);
                                    this.match(120);
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            switch (this.LA(1)) {
                                case 9: 
                                case 12: 
                                case 17: 
                                case 20: 
                                case 28: 
                                case 36: 
                                case 37: 
                                case 40: 
                                case 49: 
                                case 50: 
                                case 57: 
                                case 65: 
                                case 101: 
                                case 102: 
                                case 103: 
                                case 104: 
                                case 105: 
                                case 109: 
                                case 111: 
                                case 122: 
                                case 123: 
                                case 129: 
                                case 130: 
                                case 131: 
                                case 132: 
                                case 133: {
                                    this.additiveExpression();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    continue block37;
                                }
                                case 4: 
                                case 5: 
                                case 19: 
                                case 48: {
                                    this.quantifiedExpression();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    continue block37;
                                }
                            }
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        break;
                    }
                    case 10: 
                    case 27: 
                    case 35: 
                    case 39: 
                    case 67: {
                        switch (this.LA(1)) {
                            case 39: {
                                n = this.LT(1);
                                n_AST = this.astFactory.create(n);
                                this.match(39);
                                break;
                            }
                            case 10: 
                            case 27: 
                            case 35: 
                            case 67: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        switch (this.LA(1)) {
                            case 27: {
                                i = this.LT(1);
                                i_AST = this.astFactory.create(i);
                                this.astFactory.makeASTRoot(currentAST, i_AST);
                                this.match(27);
                                i_AST.setType(n == null ? 27 : 88);
                                i_AST.setText(n == null ? "in" : "not in");
                                this.inList();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break block6;
                            }
                            case 10: {
                                b = this.LT(1);
                                b_AST = this.astFactory.create(b);
                                this.astFactory.makeASTRoot(currentAST, b_AST);
                                this.match(10);
                                b_AST.setType(n == null ? 10 : 87);
                                b_AST.setText(n == null ? "between" : "not between");
                                this.betweenList();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break block6;
                            }
                            case 35: {
                                l = this.LT(1);
                                l_AST = this.astFactory.create(l);
                                this.astFactory.makeASTRoot(currentAST, l_AST);
                                this.match(35);
                                l_AST.setType(n == null ? 35 : 89);
                                l_AST.setText(n == null ? "like" : "not like");
                                this.concatenation();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                this.likeEscape();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break block6;
                            }
                            case 67: {
                                this.match(67);
                                switch (this.LA(1)) {
                                    case 69: {
                                        this.match(69);
                                        break;
                                    }
                                    case 111: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                this.memberOfPath();
                                p_AST = this.returnAST;
                                this.processMemberOf(n, p_AST, currentAST);
                                break block6;
                            }
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                relationalExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_34);
            }
            this.returnAST = relationalExpression_AST;
        }
        finally {
            this.traceOut("relationalExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void quantifiedExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("quantifiedExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST quantifiedExpression_AST = null;
            try {
                switch (this.LA(1)) {
                    case 48: {
                        AST tmp82_AST = null;
                        tmp82_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp82_AST);
                        this.match(48);
                        break;
                    }
                    case 19: {
                        AST tmp83_AST = null;
                        tmp83_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp83_AST);
                        this.match(19);
                        break;
                    }
                    case 4: {
                        AST tmp84_AST = null;
                        tmp84_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp84_AST);
                        this.match(4);
                        break;
                    }
                    case 5: {
                        AST tmp85_AST = null;
                        tmp85_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp85_AST);
                        this.match(5);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 111: {
                        this.identifier();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 17: 
                    case 28: {
                        this.collectionExpr();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 109: {
                        this.match(109);
                        this.subQuery();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        this.match(110);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                quantifiedExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_35);
            }
            this.returnAST = quantifiedExpression_AST;
        }
        finally {
            this.traceOut("quantifiedExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void additiveExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("additiveExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST additiveExpression_AST = null;
            try {
                this.multiplyExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 122 || this.LA(1) == 123) {
                    switch (this.LA(1)) {
                        case 122: {
                            AST tmp88_AST = null;
                            tmp88_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp88_AST);
                            this.match(122);
                            break;
                        }
                        case 123: {
                            AST tmp89_AST = null;
                            tmp89_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp89_AST);
                            this.match(123);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.multiplyExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                additiveExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_36);
            }
            this.returnAST = additiveExpression_AST;
        }
        finally {
            this.traceOut("additiveExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void inList() throws RecognitionException, TokenStreamException {
        this.traceIn("inList");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST inList_AST = null;
            AST x_AST = null;
            try {
                this.compoundExpr();
                x_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                inList_AST = currentAST.root;
                currentAST.root = inList_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(82, "inList")).add(inList_AST));
                currentAST.child = inList_AST != null && inList_AST.getFirstChild() != null ? inList_AST.getFirstChild() : inList_AST;
                currentAST.advanceChildToEnd();
                inList_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_34);
            }
            this.returnAST = inList_AST;
        }
        finally {
            this.traceOut("inList");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void betweenList() throws RecognitionException, TokenStreamException {
        this.traceIn("betweenList");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST betweenList_AST = null;
            try {
                this.concatenation();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(6);
                this.concatenation();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                betweenList_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_34);
            }
            this.returnAST = betweenList_AST;
        }
        finally {
            this.traceOut("betweenList");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void likeEscape() throws RecognitionException, TokenStreamException {
        this.traceIn("likeEscape");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST likeEscape_AST = null;
            try {
                switch (this.LA(1)) {
                    case 18: {
                        AST tmp91_AST = null;
                        tmp91_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp91_AST);
                        this.match(18);
                        this.concatenation();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 6: 
                    case 7: 
                    case 8: 
                    case 14: 
                    case 23: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 29: 
                    case 32: 
                    case 33: 
                    case 34: 
                    case 41: 
                    case 42: 
                    case 45: 
                    case 53: 
                    case 54: 
                    case 60: 
                    case 107: 
                    case 108: 
                    case 110: 
                    case 111: 
                    case 113: 
                    case 114: 
                    case 115: 
                    case 116: 
                    case 128: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                likeEscape_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_34);
            }
            this.returnAST = likeEscape_AST;
        }
        finally {
            this.traceOut("likeEscape");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void memberOfPath() throws RecognitionException, TokenStreamException {
        this.traceIn("memberOfPath");
        try {
            AST memberOfPath_AST;
            block7: {
                this.returnAST = null;
                ASTPair currentAST = new ASTPair();
                memberOfPath_AST = null;
                Token i = null;
                AST i_AST = null;
                AST p_AST = null;
                AST a_AST = null;
                try {
                    if (this.LA(1) == 111 && this.LA(2) == 109 && this.validateSoftKeyword("treat") && this.LA(2) == 109) {
                        i = this.LT(1);
                        i_AST = this.astFactory.create(i);
                        this.match(111);
                        this.match(109);
                        this.path();
                        p_AST = this.returnAST;
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        this.match(7);
                        this.path();
                        a_AST = this.returnAST;
                        this.match(110);
                        AST tmp95_AST = null;
                        tmp95_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp95_AST);
                        this.match(15);
                        this.path();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        memberOfPath_AST = currentAST.root;
                        break block7;
                    }
                    if (this.LA(1) == 111 && _tokenSet_37.member(this.LA(2))) {
                        this.path();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        memberOfPath_AST = currentAST.root;
                        break block7;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException ex) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_34);
                }
            }
            this.returnAST = memberOfPath_AST;
        }
        finally {
            this.traceOut("memberOfPath");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void compoundExpr() throws RecognitionException, TokenStreamException {
        this.traceIn("compoundExpr");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST compoundExpr_AST = null;
            try {
                switch (this.LA(1)) {
                    case 17: 
                    case 28: {
                        this.collectionExpr();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        compoundExpr_AST = currentAST.root;
                        break;
                    }
                    case 111: {
                        this.path();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        compoundExpr_AST = currentAST.root;
                        break;
                    }
                    case 131: 
                    case 132: {
                        this.parameter();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        compoundExpr_AST = currentAST.root;
                        break;
                    }
                    default: {
                        if (this.LA(1) == 109 && this.LA(2) == 110 && _tokenSet_34.member(this.LA(3)) && this.LA(1) == 109 && this.LA(2) == 110) {
                            this.match(109);
                            this.match(110);
                            compoundExpr_AST = currentAST.root;
                            break;
                        }
                        if (this.LA(1) == 109 && _tokenSet_38.member(this.LA(2)) && _tokenSet_39.member(this.LA(3))) {
                            this.match(109);
                            switch (this.LA(1)) {
                                case 4: 
                                case 5: 
                                case 9: 
                                case 12: 
                                case 17: 
                                case 19: 
                                case 20: 
                                case 28: 
                                case 36: 
                                case 37: 
                                case 39: 
                                case 40: 
                                case 48: 
                                case 49: 
                                case 50: 
                                case 57: 
                                case 65: 
                                case 101: 
                                case 102: 
                                case 103: 
                                case 104: 
                                case 105: 
                                case 109: 
                                case 111: 
                                case 122: 
                                case 123: 
                                case 129: 
                                case 130: 
                                case 131: 
                                case 132: 
                                case 133: {
                                    this.expression();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    while (this.LA(1) == 107) {
                                        this.match(107);
                                        this.expression();
                                        this.astFactory.addASTChild(currentAST, this.returnAST);
                                    }
                                    break;
                                }
                                case 23: 
                                case 25: 
                                case 42: 
                                case 46: 
                                case 53: 
                                case 110: {
                                    this.subQuery();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(this.LT(1), this.getFilename());
                                }
                            }
                            this.match(110);
                            compoundExpr_AST = currentAST.root;
                            break;
                        }
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_34);
            }
            this.returnAST = compoundExpr_AST;
        }
        finally {
            this.traceOut("compoundExpr");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void multiplyExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("multiplyExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST multiplyExpression_AST = null;
            try {
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) >= 124 && this.LA(1) <= 126) {
                    switch (this.LA(1)) {
                        case 124: {
                            AST tmp101_AST = null;
                            tmp101_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp101_AST);
                            this.match(124);
                            break;
                        }
                        case 125: {
                            AST tmp102_AST = null;
                            tmp102_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp102_AST);
                            this.match(125);
                            break;
                        }
                        case 126: {
                            AST tmp103_AST = null;
                            tmp103_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp103_AST);
                            this.match(126);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.unaryExpression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                multiplyExpression_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_40);
            }
            this.returnAST = multiplyExpression_AST;
        }
        finally {
            this.traceOut("multiplyExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void unaryExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("unaryExpression");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST unaryExpression_AST = null;
            try {
                switch (this.LA(1)) {
                    case 123: {
                        AST tmp104_AST = null;
                        tmp104_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp104_AST);
                        this.match(123);
                        tmp104_AST.setType(96);
                        this.unaryExpression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        unaryExpression_AST = currentAST.root;
                        break;
                    }
                    case 122: {
                        AST tmp105_AST = null;
                        tmp105_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp105_AST);
                        this.match(122);
                        tmp105_AST.setType(97);
                        this.unaryExpression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        unaryExpression_AST = currentAST.root;
                        break;
                    }
                    case 57: {
                        this.caseExpression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        unaryExpression_AST = currentAST.root;
                        break;
                    }
                    case 9: 
                    case 12: 
                    case 17: 
                    case 20: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 40: 
                    case 49: 
                    case 50: 
                    case 65: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 109: 
                    case 111: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 133: {
                        this.atom();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        unaryExpression_AST = currentAST.root;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_41);
            }
            this.returnAST = unaryExpression_AST;
        }
        finally {
            this.traceOut("unaryExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void caseExpression() throws RecognitionException, TokenStreamException {
        this.traceIn("caseExpression");
        try {
            AST caseExpression_AST;
            block7: {
                this.returnAST = null;
                ASTPair currentAST = new ASTPair();
                caseExpression_AST = null;
                try {
                    if (this.LA(1) == 57 && _tokenSet_42.member(this.LA(2))) {
                        this.simpleCaseStatement();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        caseExpression_AST = currentAST.root;
                        break block7;
                    }
                    if (this.LA(1) == 57 && this.LA(2) == 61) {
                        this.searchedCaseStatement();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        caseExpression_AST = currentAST.root;
                        break block7;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException ex) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_41);
                }
            }
            this.returnAST = caseExpression_AST;
        }
        finally {
            this.traceOut("caseExpression");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void atom() throws RecognitionException, TokenStreamException {
        this.traceIn("atom");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST atom_AST = null;
            Token op = null;
            AST op_AST = null;
            Token lb = null;
            AST lb_AST = null;
            try {
                this.handlePrimaryExpressionDotIdent();
                this.primaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                block13: while (true) {
                    switch (this.LA(1)) {
                        case 15: {
                            AST tmp106_AST = null;
                            tmp106_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp106_AST);
                            this.match(15);
                            this.identifier();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            switch (this.LA(1)) {
                                case 109: {
                                    op = this.LT(1);
                                    op_AST = this.astFactory.create(op);
                                    this.astFactory.makeASTRoot(currentAST, op_AST);
                                    this.match(109);
                                    op_AST.setType(86);
                                    this.exprList();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    this.match(110);
                                    continue block13;
                                }
                                case 1: 
                                case 6: 
                                case 7: 
                                case 8: 
                                case 10: 
                                case 14: 
                                case 15: 
                                case 18: 
                                case 23: 
                                case 24: 
                                case 25: 
                                case 26: 
                                case 27: 
                                case 29: 
                                case 32: 
                                case 33: 
                                case 34: 
                                case 35: 
                                case 39: 
                                case 41: 
                                case 42: 
                                case 45: 
                                case 53: 
                                case 54: 
                                case 58: 
                                case 59: 
                                case 60: 
                                case 61: 
                                case 67: 
                                case 107: 
                                case 108: 
                                case 110: 
                                case 111: 
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
                                case 124: 
                                case 125: 
                                case 126: 
                                case 127: 
                                case 128: {
                                    continue block13;
                                }
                            }
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        case 127: {
                            lb = this.LT(1);
                            lb_AST = this.astFactory.create(lb);
                            this.astFactory.makeASTRoot(currentAST, lb_AST);
                            this.match(127);
                            lb_AST.setType(83);
                            this.expression();
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            this.match(128);
                            continue block13;
                        }
                    }
                    break;
                }
                atom_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_41);
            }
            this.returnAST = atom_AST;
        }
        finally {
            this.traceOut("atom");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void simpleCaseStatement() throws RecognitionException, TokenStreamException {
        this.traceIn("simpleCaseStatement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST simpleCaseStatement_AST = null;
            try {
                AST tmp109_AST = null;
                tmp109_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp109_AST);
                this.match(57);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                int _cnt222 = 0;
                while (true) {
                    if (this.LA(1) != 61) {
                        if (_cnt222 >= 1) break;
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    this.simpleCaseWhenClause();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    ++_cnt222;
                }
                switch (this.LA(1)) {
                    case 59: {
                        this.elseClause();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 58: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(58);
                simpleCaseStatement_AST = currentAST.root;
                simpleCaseStatement_AST.setType(77);
                simpleCaseStatement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_41);
            }
            this.returnAST = simpleCaseStatement_AST;
        }
        finally {
            this.traceOut("simpleCaseStatement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void searchedCaseStatement() throws RecognitionException, TokenStreamException {
        this.traceIn("searchedCaseStatement");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST searchedCaseStatement_AST = null;
            try {
                AST tmp111_AST = null;
                tmp111_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp111_AST);
                this.match(57);
                int _cnt230 = 0;
                while (true) {
                    if (this.LA(1) != 61) {
                        if (_cnt230 >= 1) break;
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    this.searchedCaseWhenClause();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                    ++_cnt230;
                }
                switch (this.LA(1)) {
                    case 59: {
                        this.elseClause();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 58: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(58);
                searchedCaseStatement_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_41);
            }
            this.returnAST = searchedCaseStatement_AST;
        }
        finally {
            this.traceOut("searchedCaseStatement");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void simpleCaseWhenClause() throws RecognitionException, TokenStreamException {
        this.traceIn("simpleCaseWhenClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST simpleCaseWhenClause_AST = null;
            try {
                AST tmp113_AST = null;
                tmp113_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp113_AST);
                this.match(61);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(60);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                simpleCaseWhenClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_43);
            }
            this.returnAST = simpleCaseWhenClause_AST;
        }
        finally {
            this.traceOut("simpleCaseWhenClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void elseClause() throws RecognitionException, TokenStreamException {
        this.traceIn("elseClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST elseClause_AST = null;
            try {
                AST tmp115_AST = null;
                tmp115_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp115_AST);
                this.match(59);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                elseClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_44);
            }
            this.returnAST = elseClause_AST;
        }
        finally {
            this.traceOut("elseClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void searchedCaseWhenClause() throws RecognitionException, TokenStreamException {
        this.traceIn("searchedCaseWhenClause");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST searchedCaseWhenClause_AST = null;
            try {
                AST tmp116_AST = null;
                tmp116_AST = this.astFactory.create(this.LT(1));
                this.astFactory.makeASTRoot(currentAST, tmp116_AST);
                this.match(61);
                this.logicalExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(60);
                this.unaryExpression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                searchedCaseWhenClause_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_43);
            }
            this.returnAST = searchedCaseWhenClause_AST;
        }
        finally {
            this.traceOut("searchedCaseWhenClause");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void collectionExpr() throws RecognitionException, TokenStreamException {
        this.traceIn("collectionExpr");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST collectionExpr_AST = null;
            try {
                switch (this.LA(1)) {
                    case 17: {
                        AST tmp118_AST = null;
                        tmp118_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp118_AST);
                        this.match(17);
                        break;
                    }
                    case 28: {
                        AST tmp119_AST = null;
                        tmp119_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp119_AST);
                        this.match(28);
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(109);
                this.path();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(110);
                collectionExpr_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = collectionExpr_AST;
        }
        finally {
            this.traceOut("collectionExpr");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void subQuery() throws RecognitionException, TokenStreamException {
        this.traceIn("subQuery");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST subQuery_AST = null;
            try {
                this.queryRule();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                subQuery_AST = currentAST.root;
                currentAST.root = subQuery_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(91, "query")).add(subQuery_AST));
                currentAST.child = subQuery_AST != null && subQuery_AST.getFirstChild() != null ? subQuery_AST.getFirstChild() : subQuery_AST;
                currentAST.advanceChildToEnd();
                subQuery_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_45);
            }
            this.returnAST = subQuery_AST;
        }
        finally {
            this.traceOut("subQuery");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void exprList() throws RecognitionException, TokenStreamException {
        this.traceIn("exprList");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST exprList_AST = null;
            Token t = null;
            AST t_AST = null;
            Token l = null;
            AST l_AST = null;
            Token b = null;
            AST b_AST = null;
            AST trimSpec = null;
            try {
                switch (this.LA(1)) {
                    case 70: {
                        t = this.LT(1);
                        t_AST = this.astFactory.create(t);
                        this.astFactory.addASTChild(currentAST, t_AST);
                        this.match(70);
                        trimSpec = t_AST;
                        break;
                    }
                    case 66: {
                        l = this.LT(1);
                        l_AST = this.astFactory.create(l);
                        this.astFactory.addASTChild(currentAST, l_AST);
                        this.match(66);
                        trimSpec = l_AST;
                        break;
                    }
                    case 64: {
                        b = this.LT(1);
                        b_AST = this.astFactory.create(b);
                        this.astFactory.addASTChild(currentAST, b_AST);
                        this.match(64);
                        trimSpec = b_AST;
                        break;
                    }
                    case 4: 
                    case 5: 
                    case 9: 
                    case 12: 
                    case 17: 
                    case 19: 
                    case 20: 
                    case 23: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 39: 
                    case 40: 
                    case 48: 
                    case 49: 
                    case 50: 
                    case 57: 
                    case 65: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 109: 
                    case 110: 
                    case 111: 
                    case 122: 
                    case 123: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 133: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (trimSpec != null) {
                    trimSpec.setType(111);
                }
                block8 : switch (this.LA(1)) {
                    case 4: 
                    case 5: 
                    case 9: 
                    case 12: 
                    case 17: 
                    case 19: 
                    case 20: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 39: 
                    case 40: 
                    case 48: 
                    case 49: 
                    case 50: 
                    case 57: 
                    case 65: 
                    case 101: 
                    case 102: 
                    case 103: 
                    case 104: 
                    case 105: 
                    case 109: 
                    case 111: 
                    case 122: 
                    case 123: 
                    case 129: 
                    case 130: 
                    case 131: 
                    case 132: 
                    case 133: {
                        this.expression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        switch (this.LA(1)) {
                            case 107: {
                                int _cnt297 = 0;
                                while (true) {
                                    if (this.LA(1) != 107) {
                                        if (_cnt297 >= 1) break block8;
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                    this.match(107);
                                    this.expression();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    ++_cnt297;
                                }
                            }
                            case 23: {
                                AST tmp123_AST = null;
                                tmp123_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp123_AST);
                                this.match(23);
                                tmp123_AST.setType(111);
                                this.expression();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break block8;
                            }
                            case 7: {
                                this.match(7);
                                this.identifier();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break block8;
                            }
                            case 110: {
                                break block8;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                    }
                    case 23: {
                        AST tmp125_AST = null;
                        tmp125_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp125_AST);
                        this.match(23);
                        tmp125_AST.setType(111);
                        this.expression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                exprList_AST = currentAST.root;
                currentAST.root = exprList_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(80, "exprList")).add(exprList_AST));
                currentAST.child = exprList_AST != null && exprList_AST.getFirstChild() != null ? exprList_AST.getFirstChild() : exprList_AST;
                currentAST.advanceChildToEnd();
                exprList_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_45);
            }
            this.returnAST = exprList_AST;
        }
        finally {
            this.traceOut("exprList");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void fkRefPath() throws RecognitionException, TokenStreamException {
        this.traceIn("fkRefPath");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST fkRefPath_AST = null;
            AST p_AST = null;
            try {
                this.match(129);
                AST tmp127_AST = null;
                tmp127_AST = this.astFactory.create(this.LT(1));
                this.match(109);
                this.identPrimary();
                p_AST = this.returnAST;
                AST tmp128_AST = null;
                tmp128_AST = this.astFactory.create(this.LT(1));
                this.match(110);
                fkRefPath_AST = currentAST.root;
                currentAST.root = fkRefPath_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(22)).add(p_AST));
                currentAST.child = fkRefPath_AST != null && fkRefPath_AST.getFirstChild() != null ? fkRefPath_AST.getFirstChild() : fkRefPath_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = fkRefPath_AST;
        }
        finally {
            this.traceOut("fkRefPath");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void jpaFunctionSyntax() throws RecognitionException, TokenStreamException {
        this.traceIn("jpaFunctionSyntax");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST jpaFunctionSyntax_AST = null;
            Token i = null;
            AST i_AST = null;
            Token n = null;
            AST n_AST = null;
            AST a_AST = null;
            try {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.match(111);
                AST tmp129_AST = null;
                tmp129_AST = this.astFactory.create(this.LT(1));
                this.match(109);
                n = this.LT(1);
                n_AST = this.astFactory.create(n);
                this.match(130);
                switch (this.LA(1)) {
                    case 107: {
                        AST tmp130_AST = null;
                        tmp130_AST = this.astFactory.create(this.LT(1));
                        this.match(107);
                        this.exprList();
                        a_AST = this.returnAST;
                        break;
                    }
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                AST tmp131_AST = null;
                tmp131_AST = this.astFactory.create(this.LT(1));
                this.match(110);
                jpaFunctionSyntax_AST = currentAST.root;
                String functionName = this.unquote(n_AST.getText());
                if (functionName.equalsIgnoreCase("cast")) {
                    i_AST.setType(78);
                    i_AST.setText(i_AST.getText() + " (" + functionName + ")");
                    AST expression = a_AST.getFirstChild();
                    AST type = expression.getNextSibling();
                    jpaFunctionSyntax_AST = this.astFactory.make(new ASTArray(3).add(i_AST).add(expression).add(type));
                } else {
                    i_AST.setType(86);
                    i_AST.setText(i_AST.getText() + " (" + functionName + ")");
                    jpaFunctionSyntax_AST = this.astFactory.make(new ASTArray(3).add(i_AST).add(this.astFactory.create(111, this.unquote(n.getText()))).add(a_AST));
                }
                currentAST.root = jpaFunctionSyntax_AST;
                currentAST.child = jpaFunctionSyntax_AST != null && jpaFunctionSyntax_AST.getFirstChild() != null ? jpaFunctionSyntax_AST.getFirstChild() : jpaFunctionSyntax_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = jpaFunctionSyntax_AST;
        }
        finally {
            this.traceOut("jpaFunctionSyntax");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void castFunction() throws RecognitionException, TokenStreamException {
        this.traceIn("castFunction");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST castFunction_AST = null;
            Token c = null;
            AST c_AST = null;
            AST e_AST = null;
            AST t_AST = null;
            try {
                c = this.LT(1);
                c_AST = this.astFactory.create(c);
                this.match(111);
                AST tmp132_AST = null;
                tmp132_AST = this.astFactory.create(this.LT(1));
                this.match(109);
                this.expression();
                e_AST = this.returnAST;
                switch (this.LA(1)) {
                    case 7: {
                        AST tmp133_AST = null;
                        tmp133_AST = this.astFactory.create(this.LT(1));
                        this.match(7);
                        break;
                    }
                    case 111: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.castTargetType();
                t_AST = this.returnAST;
                AST tmp134_AST = null;
                tmp134_AST = this.astFactory.create(this.LT(1));
                this.match(110);
                castFunction_AST = currentAST.root;
                c_AST.setType(78);
                currentAST.root = castFunction_AST = this.astFactory.make(new ASTArray(3).add(c_AST).add(e_AST).add(t_AST));
                currentAST.child = castFunction_AST != null && castFunction_AST.getFirstChild() != null ? castFunction_AST.getFirstChild() : castFunction_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = castFunction_AST;
        }
        finally {
            this.traceOut("castFunction");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void collectionSizeFunction() throws RecognitionException, TokenStreamException {
        this.traceIn("collectionSizeFunction");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST collectionSizeFunction_AST = null;
            Token s = null;
            AST s_AST = null;
            AST p_AST = null;
            try {
                s = this.LT(1);
                s_AST = this.astFactory.create(s);
                this.match(111);
                AST tmp135_AST = null;
                tmp135_AST = this.astFactory.create(this.LT(1));
                this.match(109);
                this.collectionPath();
                p_AST = this.returnAST;
                AST tmp136_AST = null;
                tmp136_AST = this.astFactory.create(this.LT(1));
                this.match(110);
                collectionSizeFunction_AST = currentAST.root;
                assert (s_AST.getText().equalsIgnoreCase("size"));
                currentAST.root = collectionSizeFunction_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(95)).add(p_AST));
                currentAST.child = collectionSizeFunction_AST != null && collectionSizeFunction_AST.getFirstChild() != null ? collectionSizeFunction_AST.getFirstChild() : collectionSizeFunction_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = collectionSizeFunction_AST;
        }
        finally {
            this.traceOut("collectionSizeFunction");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void identPrimary() throws RecognitionException, TokenStreamException {
        this.traceIn("identPrimary");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST identPrimary_AST = null;
            AST i_AST = null;
            Token o = null;
            AST o_AST = null;
            Token op = null;
            AST op_AST = null;
            AST e_AST = null;
            try {
                switch (this.LA(1)) {
                    case 111: {
                        this.identPrimaryBase();
                        i_AST = this.returnAST;
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        this.handleDotIdent();
                        block14: while (this.LA(1) == 15 && (this.LA(2) == 17 || this.LA(2) == 68 || this.LA(2) == 111) && _tokenSet_13.member(this.LA(3))) {
                            AST tmp137_AST = null;
                            tmp137_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp137_AST);
                            this.match(15);
                            switch (this.LA(1)) {
                                case 111: {
                                    this.identifier();
                                    this.astFactory.addASTChild(currentAST, this.returnAST);
                                    this.handleDotIdent();
                                    continue block14;
                                }
                                case 17: {
                                    AST tmp138_AST = null;
                                    tmp138_AST = this.astFactory.create(this.LT(1));
                                    this.astFactory.addASTChild(currentAST, tmp138_AST);
                                    this.match(17);
                                    continue block14;
                                }
                                case 68: {
                                    o = this.LT(1);
                                    o_AST = this.astFactory.create(o);
                                    this.astFactory.addASTChild(currentAST, o_AST);
                                    this.match(68);
                                    o_AST.setType(111);
                                    continue block14;
                                }
                            }
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        if (this.LA(1) == 109) {
                            op = this.LT(1);
                            op_AST = this.astFactory.create(op);
                            this.astFactory.makeASTRoot(currentAST, op_AST);
                            this.match(109);
                            op_AST.setType(86);
                            this.exprList();
                            e_AST = this.returnAST;
                            this.astFactory.addASTChild(currentAST, this.returnAST);
                            this.match(110);
                            identPrimary_AST = currentAST.root;
                            AST path = e_AST.getFirstChild();
                            if (i_AST.getText().equalsIgnoreCase("key")) {
                                identPrimary_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(71)).add(path));
                            } else if (i_AST.getText().equalsIgnoreCase("value")) {
                                identPrimary_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(72)).add(path));
                            } else if (i_AST.getText().equalsIgnoreCase("entry")) {
                                identPrimary_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(73)).add(path));
                            }
                            currentAST.root = identPrimary_AST;
                            currentAST.child = identPrimary_AST != null && identPrimary_AST.getFirstChild() != null ? identPrimary_AST.getFirstChild() : identPrimary_AST;
                            currentAST.advanceChildToEnd();
                        } else if (this.LA(1) == 15 && this.LA(2) == 22) {
                            AST tmp140_AST = null;
                            tmp140_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.makeASTRoot(currentAST, tmp140_AST);
                            this.match(15);
                            AST tmp141_AST = null;
                            tmp141_AST = this.astFactory.create(this.LT(1));
                            this.astFactory.addASTChild(currentAST, tmp141_AST);
                            this.match(22);
                        } else if (!_tokenSet_15.member(this.LA(1)) || !_tokenSet_46.member(this.LA(2))) {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        identPrimary_AST = currentAST.root;
                        break;
                    }
                    case 9: 
                    case 12: 
                    case 17: 
                    case 28: 
                    case 36: 
                    case 37: 
                    case 49: {
                        this.aggregate();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        identPrimary_AST = currentAST.root;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = identPrimary_AST;
        }
        finally {
            this.traceOut("identPrimary");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void constant() throws RecognitionException, TokenStreamException {
        this.traceIn("constant");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST constant_AST = null;
            try {
                switch (this.LA(1)) {
                    case 133: {
                        AST tmp142_AST = null;
                        tmp142_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp142_AST);
                        this.match(133);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 102: {
                        AST tmp143_AST = null;
                        tmp143_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp143_AST);
                        this.match(102);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 103: {
                        AST tmp144_AST = null;
                        tmp144_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp144_AST);
                        this.match(103);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 101: {
                        AST tmp145_AST = null;
                        tmp145_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp145_AST);
                        this.match(101);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 104: {
                        AST tmp146_AST = null;
                        tmp146_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp146_AST);
                        this.match(104);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 105: {
                        AST tmp147_AST = null;
                        tmp147_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp147_AST);
                        this.match(105);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 130: {
                        AST tmp148_AST = null;
                        tmp148_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp148_AST);
                        this.match(130);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 40: {
                        AST tmp149_AST = null;
                        tmp149_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp149_AST);
                        this.match(40);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 50: {
                        AST tmp150_AST = null;
                        tmp150_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp150_AST);
                        this.match(50);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 20: {
                        AST tmp151_AST = null;
                        tmp151_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp151_AST);
                        this.match(20);
                        constant_AST = currentAST.root;
                        break;
                    }
                    case 65: {
                        AST tmp152_AST = null;
                        tmp152_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp152_AST);
                        this.match(65);
                        constant_AST = currentAST.root;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = constant_AST;
        }
        finally {
            this.traceOut("constant");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void parameter() throws RecognitionException, TokenStreamException {
        this.traceIn("parameter");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST parameter_AST = null;
            try {
                switch (this.LA(1)) {
                    case 131: {
                        AST tmp153_AST = null;
                        tmp153_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp153_AST);
                        this.match(131);
                        this.expectNamedParameterName();
                        AST tmp154_AST = null;
                        tmp154_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp154_AST);
                        this.match(111);
                        parameter_AST = currentAST.root;
                        break;
                    }
                    case 132: {
                        AST tmp155_AST = null;
                        tmp155_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp155_AST);
                        this.match(132);
                        switch (this.LA(1)) {
                            case 133: {
                                AST tmp156_AST = null;
                                tmp156_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp156_AST);
                                this.match(133);
                                break;
                            }
                            case 1: 
                            case 6: 
                            case 7: 
                            case 8: 
                            case 10: 
                            case 14: 
                            case 15: 
                            case 18: 
                            case 23: 
                            case 24: 
                            case 25: 
                            case 26: 
                            case 27: 
                            case 29: 
                            case 32: 
                            case 33: 
                            case 34: 
                            case 35: 
                            case 39: 
                            case 41: 
                            case 42: 
                            case 45: 
                            case 53: 
                            case 54: 
                            case 58: 
                            case 59: 
                            case 60: 
                            case 61: 
                            case 67: 
                            case 107: 
                            case 108: 
                            case 110: 
                            case 111: 
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
                            case 124: 
                            case 125: 
                            case 126: 
                            case 127: 
                            case 128: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        parameter_AST = currentAST.root;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = parameter_AST;
        }
        finally {
            this.traceOut("parameter");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void expressionOrVector() throws RecognitionException, TokenStreamException {
        this.traceIn("expressionOrVector");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST expressionOrVector_AST = null;
            AST e_AST = null;
            AST v_AST = null;
            try {
                this.expression();
                e_AST = this.returnAST;
                switch (this.LA(1)) {
                    case 107: {
                        this.vectorExpr();
                        v_AST = this.returnAST;
                        break;
                    }
                    case 110: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                expressionOrVector_AST = currentAST.root;
                expressionOrVector_AST = v_AST != null ? this.astFactory.make(new ASTArray(3).add(this.astFactory.create(98, "{vector}")).add(e_AST).add(v_AST)) : e_AST;
                currentAST.root = expressionOrVector_AST;
                currentAST.child = expressionOrVector_AST != null && expressionOrVector_AST.getFirstChild() != null ? expressionOrVector_AST.getFirstChild() : expressionOrVector_AST;
                currentAST.advanceChildToEnd();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_45);
            }
            this.returnAST = expressionOrVector_AST;
        }
        finally {
            this.traceOut("expressionOrVector");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void castTargetType() throws RecognitionException, TokenStreamException {
        this.traceIn("castTargetType");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST castTargetType_AST = null;
            try {
                this.identifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.handleDotIdent();
                while (this.LA(1) == 15) {
                    AST tmp157_AST = null;
                    tmp157_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp157_AST);
                    this.match(15);
                    this.identifier();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                castTargetType_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_45);
            }
            this.returnAST = castTargetType_AST;
        }
        finally {
            this.traceOut("castTargetType");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void collectionPath() throws RecognitionException, TokenStreamException {
        this.traceIn("collectionPath");
        try {
            AST collectionPath_AST;
            block7: {
                this.returnAST = null;
                ASTPair currentAST = new ASTPair();
                collectionPath_AST = null;
                AST simpleRef_AST = null;
                AST qualifier_AST = null;
                AST propertyName_AST = null;
                try {
                    if (this.LA(1) == 111 && this.LA(2) == 110) {
                        this.identifier();
                        simpleRef_AST = this.returnAST;
                        collectionPath_AST = currentAST.root;
                        currentAST.root = collectionPath_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(79)).add(simpleRef_AST));
                        currentAST.child = collectionPath_AST != null && collectionPath_AST.getFirstChild() != null ? collectionPath_AST.getFirstChild() : collectionPath_AST;
                        currentAST.advanceChildToEnd();
                        break block7;
                    }
                    if (this.LA(1) == 111 && this.LA(2) == 15) {
                        this.collectionPathQualifier();
                        qualifier_AST = this.returnAST;
                        AST tmp158_AST = null;
                        tmp158_AST = this.astFactory.create(this.LT(1));
                        this.match(15);
                        this.identifier();
                        propertyName_AST = this.returnAST;
                        collectionPath_AST = currentAST.root;
                        currentAST.root = collectionPath_AST = this.astFactory.make(new ASTArray(3).add(this.astFactory.create(79)).add(propertyName_AST).add(qualifier_AST));
                        currentAST.child = collectionPath_AST != null && collectionPath_AST.getFirstChild() != null ? collectionPath_AST.getFirstChild() : collectionPath_AST;
                        currentAST.advanceChildToEnd();
                        break block7;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException ex) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_45);
                }
            }
            this.returnAST = collectionPath_AST;
        }
        finally {
            this.traceOut("collectionPath");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void collectionPathQualifier() throws RecognitionException, TokenStreamException {
        this.traceIn("collectionPathQualifier");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST collectionPathQualifier_AST = null;
            try {
                this.identifier();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 15 && this.LA(2) == 111 && this.LA(3) == 15) {
                    AST tmp159_AST = null;
                    tmp159_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.makeASTRoot(currentAST, tmp159_AST);
                    this.match(15);
                    this.weakKeywords();
                    this.identifier();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                collectionPathQualifier_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_47);
            }
            this.returnAST = collectionPathQualifier_AST;
        }
        finally {
            this.traceOut("collectionPathQualifier");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void vectorExpr() throws RecognitionException, TokenStreamException {
        this.traceIn("vectorExpr");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST vectorExpr_AST = null;
            try {
                this.match(107);
                this.expression();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 107) {
                    this.match(107);
                    this.expression();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                vectorExpr_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_45);
            }
            this.returnAST = vectorExpr_AST;
        }
        finally {
            this.traceOut("vectorExpr");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void identPrimaryBase() throws RecognitionException, TokenStreamException {
        this.traceIn("identPrimaryBase");
        try {
            AST identPrimaryBase_AST;
            block7: {
                this.returnAST = null;
                ASTPair currentAST = new ASTPair();
                identPrimaryBase_AST = null;
                AST i_AST = null;
                try {
                    if (this.LA(1) == 111 && this.LA(2) == 109 && this.LA(3) == 111 && this.validateSoftKeyword("treat") && this.LA(2) == 109) {
                        this.castedIdentPrimaryBase();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        identPrimaryBase_AST = currentAST.root;
                        break block7;
                    }
                    if (this.LA(1) == 111 && _tokenSet_13.member(this.LA(2)) && _tokenSet_48.member(this.LA(3))) {
                        this.identifier();
                        i_AST = this.returnAST;
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        identPrimaryBase_AST = currentAST.root;
                        break block7;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException ex) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_13);
                }
            }
            this.returnAST = identPrimaryBase_AST;
        }
        finally {
            this.traceOut("identPrimaryBase");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void aggregate() throws RecognitionException, TokenStreamException {
        this.traceIn("aggregate");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST aggregate_AST = null;
            try {
                switch (this.LA(1)) {
                    case 9: 
                    case 36: 
                    case 37: 
                    case 49: {
                        switch (this.LA(1)) {
                            case 49: {
                                AST tmp162_AST = null;
                                tmp162_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.makeASTRoot(currentAST, tmp162_AST);
                                this.match(49);
                                break;
                            }
                            case 9: {
                                AST tmp163_AST = null;
                                tmp163_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.makeASTRoot(currentAST, tmp163_AST);
                                this.match(9);
                                break;
                            }
                            case 36: {
                                AST tmp164_AST = null;
                                tmp164_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.makeASTRoot(currentAST, tmp164_AST);
                                this.match(36);
                                break;
                            }
                            case 37: {
                                AST tmp165_AST = null;
                                tmp165_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.makeASTRoot(currentAST, tmp165_AST);
                                this.match(37);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        this.match(109);
                        this.concatenation();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        this.match(110);
                        aggregate_AST = currentAST.root;
                        aggregate_AST.setType(74);
                        aggregate_AST = currentAST.root;
                        break;
                    }
                    case 12: {
                        AST tmp168_AST = null;
                        tmp168_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.makeASTRoot(currentAST, tmp168_AST);
                        this.match(12);
                        this.match(109);
                        switch (this.LA(1)) {
                            case 124: {
                                AST tmp170_AST = null;
                                tmp170_AST = this.astFactory.create(this.LT(1));
                                this.astFactory.addASTChild(currentAST, tmp170_AST);
                                this.match(124);
                                tmp170_AST.setType(93);
                                break;
                            }
                            case 4: 
                            case 9: 
                            case 12: 
                            case 16: 
                            case 17: 
                            case 20: 
                            case 28: 
                            case 36: 
                            case 37: 
                            case 40: 
                            case 49: 
                            case 50: 
                            case 57: 
                            case 65: 
                            case 101: 
                            case 102: 
                            case 103: 
                            case 104: 
                            case 105: 
                            case 109: 
                            case 111: 
                            case 122: 
                            case 123: 
                            case 129: 
                            case 130: 
                            case 131: 
                            case 132: 
                            case 133: {
                                switch (this.LA(1)) {
                                    case 16: {
                                        AST tmp171_AST = null;
                                        tmp171_AST = this.astFactory.create(this.LT(1));
                                        this.astFactory.addASTChild(currentAST, tmp171_AST);
                                        this.match(16);
                                        break;
                                    }
                                    case 4: {
                                        AST tmp172_AST = null;
                                        tmp172_AST = this.astFactory.create(this.LT(1));
                                        this.astFactory.addASTChild(currentAST, tmp172_AST);
                                        this.match(4);
                                        break;
                                    }
                                    case 9: 
                                    case 12: 
                                    case 17: 
                                    case 20: 
                                    case 28: 
                                    case 36: 
                                    case 37: 
                                    case 40: 
                                    case 49: 
                                    case 50: 
                                    case 57: 
                                    case 65: 
                                    case 101: 
                                    case 102: 
                                    case 103: 
                                    case 104: 
                                    case 105: 
                                    case 109: 
                                    case 111: 
                                    case 122: 
                                    case 123: 
                                    case 129: 
                                    case 130: 
                                    case 131: 
                                    case 132: 
                                    case 133: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                this.concatenation();
                                this.astFactory.addASTChild(currentAST, this.returnAST);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        this.match(110);
                        aggregate_AST = currentAST.root;
                        break;
                    }
                    case 17: 
                    case 28: {
                        this.collectionExpr();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        aggregate_AST = currentAST.root;
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_15);
            }
            this.returnAST = aggregate_AST;
        }
        finally {
            this.traceOut("aggregate");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void castedIdentPrimaryBase() throws RecognitionException, TokenStreamException {
        this.traceIn("castedIdentPrimaryBase");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST castedIdentPrimaryBase_AST = null;
            Token i = null;
            AST i_AST = null;
            AST p_AST = null;
            AST a_AST = null;
            try {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.match(111);
                this.match(109);
                this.path();
                p_AST = this.returnAST;
                this.astFactory.addASTChild(currentAST, this.returnAST);
                this.match(7);
                this.path();
                a_AST = this.returnAST;
                this.match(110);
                if (!i.getText().equalsIgnoreCase("treat")) {
                    throw new SemanticException(" i.getText().equalsIgnoreCase(\"treat\") ");
                }
                this.registerTreat(p_AST, a_AST);
                castedIdentPrimaryBase_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, _tokenSet_13);
            }
            this.returnAST = castedIdentPrimaryBase_AST;
        }
        finally {
            this.traceOut("castedIdentPrimaryBase");
        }
    }

    protected void buildTokenTypeASTClassMap() {
        this.tokenTypeToASTClassMap = null;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[]{2L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[]{0x20800000000002L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[]{0x20000000000002L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[]{0x40002000002L, 0x400000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[]{0x20000000000002L, 0x80000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[]{0L, 0x100000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_6() {
        long[] data = new long[]{-3431560366396522046L, 8716928185008128L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_7() {
        long[] data = new long[]{1179985498790446530L, 143789732634034184L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_8() {
        long[] data = new long[]{-4602498472906588158L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_9() {
        long[] data = new long[]{2L, 0x400000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_10() {
        long[] data = new long[]{9081966087372802L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_11() {
        long[] data = new long[]{146087368608846384L, 864871310923137026L, 62L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_12() {
        long[] data = new long[]{563156380422656L, 0x800000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_13() {
        long[] data = new long[]{4350519636459308482L, -290271069732856L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_14() {
        long[] data = new long[]{4496685620155899890L, -4535485464449L, 63L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_15() {
        long[] data = new long[]{4350519636459308482L, -325455441821688L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_16() {
        long[] data = new long[]{4496685620151638002L, -4535485464534L, 63L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_17() {
        long[] data = new long[]{0x20040002000002L, 0x400000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_18() {
        long[] data = new long[]{0x40000000002L, 0x400000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_19() {
        long[] data = new long[]{9011597343195138L, 0x400000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_20() {
        long[] data = new long[]{9046808032477314L, 0xC80000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_21() {
        long[] data = new long[]{9046808030347266L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_22() {
        long[] data = new long[]{-4602639210394910590L, 0xC80000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_23() {
        long[] data = new long[]{-4602639210394943358L, 0xC80000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_24() {
        long[] data = new long[]{-4602639210397040638L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_25() {
        long[] data = new long[]{1161968312637194242L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_26() {
        long[] data = new long[]{-4602498472772370430L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_27() {
        long[] data = new long[]{1179982711222190466L, 1908752185819136L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_28() {
        long[] data = new long[]{2L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_29() {
        long[] data = new long[]{0x40000000000002L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_30() {
        long[] data = new long[]{9011597343195138L, 0x480000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_31() {
        long[] data = new long[]{1179984910245446018L, 1908752185819136L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_32() {
        long[] data = new long[]{1179984910245446082L, 1908752185819136L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_33() {
        long[] data = new long[]{0x100000000L, 0x18100000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_34() {
        long[] data = new long[]{1179984914540413378L, 8681743812919296L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_35() {
        long[] data = new long[]{1179985498790184386L, 143789732634034184L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_36() {
        long[] data = new long[]{1179985498790446530L, 287904920709890056L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_37() {
        long[] data = new long[]{1179984914540446146L, 8681743812919296L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_38() {
        long[] data = new long[]{155169334696219184L, 864941679667314690L, 62L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_39() {
        long[] data = new long[]{3631985970479093746L, -4535485464550L, 63L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_40() {
        long[] data = new long[]{1179985498790446530L, 1152596049165025288L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_41() {
        long[] data = new long[]{4350519636459275714L, 9223046581412954120L, 1L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_42() {
        long[] data = new long[]{145805343875797504L, 864871310923137026L, 62L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_43() {
        long[] data = new long[]{0x2C00000000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_44() {
        long[] data = new long[]{0x400000000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_45() {
        long[] data = new long[]{0L, 0x400000000000L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_46() {
        long[] data = new long[]{4496685620151640050L, -4535485464534L, 63L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_47() {
        long[] data = new long[]{32768L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_48() {
        long[] data = new long[]{4496685620155834354L, -4535485464449L, 63L, 0L, 0L, 0L};
        return data;
    }
}

