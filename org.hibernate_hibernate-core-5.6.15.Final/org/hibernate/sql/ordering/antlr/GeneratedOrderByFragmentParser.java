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
package org.hibernate.sql.ordering.antlr;

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
import org.hibernate.internal.build.AllowSysOut;
import org.hibernate.sql.ordering.antlr.OrderByTemplateTokenTypes;

public class GeneratedOrderByFragmentParser
extends LLkParser
implements OrderByTemplateTokenTypes {
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "ORDER_BY", "SORT_SPEC", "ORDER_SPEC", "NULL_ORDER", "SORT_KEY", "EXPR_LIST", "DOT", "IDENT_LIST", "COLUMN_REF", "\"collate\"", "\"asc\"", "\"desc\"", "\"nulls\"", "FIRST", "LAST", "COMMA", "HARD_QUOTE", "IDENT", "OPEN_PAREN", "CLOSE_PAREN", "NUM_DOUBLE", "NUM_FLOAT", "NUM_INT", "NUM_LONG", "QUOTED_STRING", "\"ascending\"", "\"descending\"", "ID_START_LETTER", "ID_LETTER", "ESCqs", "HEX_DIGIT", "EXPONENT", "FLOAT_SUFFIX", "WS"};
    public static final BitSet _tokenSet_0 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_5());
    public static final BitSet _tokenSet_6 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_6());
    public static final BitSet _tokenSet_7 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_7());
    public static final BitSet _tokenSet_8 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_8());
    public static final BitSet _tokenSet_9 = new BitSet(GeneratedOrderByFragmentParser.mk_tokenSet_9());

    @AllowSysOut
    protected void trace(String msg) {
        System.out.println(msg);
    }

    protected final String extractText(AST ast) {
        return ast.getText();
    }

    protected AST quotedIdentifier(AST ident) {
        return ident;
    }

    protected AST quotedString(AST ident) {
        return ident;
    }

    protected boolean isFunctionName(AST ast) {
        return false;
    }

    protected AST resolveFunction(AST ast) {
        return ast;
    }

    protected AST resolveIdent(AST ident) {
        return ident;
    }

    protected AST postProcessSortSpecification(AST sortSpec) {
        return sortSpec;
    }

    protected GeneratedOrderByFragmentParser(TokenBuffer tokenBuf, int k) {
        super(tokenBuf, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public GeneratedOrderByFragmentParser(TokenBuffer tokenBuf) {
        this(tokenBuf, 3);
    }

    protected GeneratedOrderByFragmentParser(TokenStream lexer, int k) {
        super(lexer, k);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    public GeneratedOrderByFragmentParser(TokenStream lexer) {
        this(lexer, 3);
    }

    public GeneratedOrderByFragmentParser(ParserSharedInputState state) {
        super(state, 3);
        this.tokenNames = _tokenNames;
        this.buildTokenTypeASTClassMap();
        this.astFactory = new ASTFactory(this.getTokenTypeToASTClassMap());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void orderByFragment() throws RecognitionException, TokenStreamException {
        this.traceIn("orderByFragment");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST orderByFragment_AST = null;
            this.trace("orderByFragment");
            try {
                this.sortSpecification();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 19) {
                    this.match(19);
                    this.sortSpecification();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                if (this.inputState.guessing == 0) {
                    orderByFragment_AST = currentAST.root;
                    currentAST.root = orderByFragment_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(4, "order-by")).add(orderByFragment_AST));
                    currentAST.child = orderByFragment_AST != null && orderByFragment_AST.getFirstChild() != null ? orderByFragment_AST.getFirstChild() : orderByFragment_AST;
                    currentAST.advanceChildToEnd();
                }
                orderByFragment_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_0);
                }
                throw ex;
            }
            this.returnAST = orderByFragment_AST;
        }
        finally {
            this.traceOut("orderByFragment");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void sortSpecification() throws RecognitionException, TokenStreamException {
        this.traceIn("sortSpecification");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST sortSpecification_AST = null;
            this.trace("sortSpecification");
            try {
                this.sortKey();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                switch (this.LA(1)) {
                    case 13: {
                        this.collationSpecification();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 14: 
                    case 15: 
                    case 16: 
                    case 19: 
                    case 29: 
                    case 30: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 14: 
                    case 15: 
                    case 29: 
                    case 30: {
                        this.orderingSpecification();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 16: 
                    case 19: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                switch (this.LA(1)) {
                    case 16: {
                        this.nullOrdering();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        break;
                    }
                    case 1: 
                    case 19: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    sortSpecification_AST = currentAST.root;
                    sortSpecification_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(5, "{sort specification}")).add(sortSpecification_AST));
                    currentAST.root = sortSpecification_AST = this.postProcessSortSpecification(sortSpecification_AST);
                    currentAST.child = sortSpecification_AST != null && sortSpecification_AST.getFirstChild() != null ? sortSpecification_AST.getFirstChild() : sortSpecification_AST;
                    currentAST.advanceChildToEnd();
                }
                sortSpecification_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_1);
                }
                throw ex;
            }
            this.returnAST = sortSpecification_AST;
        }
        finally {
            this.traceOut("sortSpecification");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void sortKey() throws RecognitionException, TokenStreamException {
        this.traceIn("sortKey");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST sortKey_AST = null;
            AST e_AST = null;
            this.trace("sortKey");
            try {
                this.expression();
                e_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    sortKey_AST = currentAST.root;
                    currentAST.root = sortKey_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(8, "sort key")).add(e_AST));
                    currentAST.child = sortKey_AST != null && sortKey_AST.getFirstChild() != null ? sortKey_AST.getFirstChild() : sortKey_AST;
                    currentAST.advanceChildToEnd();
                }
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_2);
                }
                throw ex;
            }
            this.returnAST = sortKey_AST;
        }
        finally {
            this.traceOut("sortKey");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void collationSpecification() throws RecognitionException, TokenStreamException {
        this.traceIn("collationSpecification");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST collationSpecification_AST = null;
            Token c = null;
            AST c_AST = null;
            AST cn_AST = null;
            this.trace("collationSpecification");
            try {
                c = this.LT(1);
                c_AST = this.astFactory.create(c);
                this.match(13);
                this.collationName();
                cn_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    collationSpecification_AST = currentAST.root;
                    currentAST.root = collationSpecification_AST = this.astFactory.make(new ASTArray(1).add(this.astFactory.create(13, this.extractText(cn_AST))));
                    currentAST.child = collationSpecification_AST != null && collationSpecification_AST.getFirstChild() != null ? collationSpecification_AST.getFirstChild() : collationSpecification_AST;
                    currentAST.advanceChildToEnd();
                }
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_3);
                }
                throw ex;
            }
            this.returnAST = collationSpecification_AST;
        }
        finally {
            this.traceOut("collationSpecification");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void orderingSpecification() throws RecognitionException, TokenStreamException {
        this.traceIn("orderingSpecification");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST orderingSpecification_AST = null;
            this.trace("orderingSpecification");
            try {
                switch (this.LA(1)) {
                    case 14: 
                    case 29: {
                        switch (this.LA(1)) {
                            case 14: {
                                this.match(14);
                                break;
                            }
                            case 29: {
                                this.match(29);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        if (this.inputState.guessing == 0) {
                            orderingSpecification_AST = currentAST.root;
                            currentAST.root = orderingSpecification_AST = this.astFactory.make(new ASTArray(1).add(this.astFactory.create(6, "asc")));
                            currentAST.child = orderingSpecification_AST != null && orderingSpecification_AST.getFirstChild() != null ? orderingSpecification_AST.getFirstChild() : orderingSpecification_AST;
                            currentAST.advanceChildToEnd();
                        }
                        break;
                    }
                    case 15: 
                    case 30: {
                        switch (this.LA(1)) {
                            case 15: {
                                this.match(15);
                                break;
                            }
                            case 30: {
                                this.match(30);
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        if (this.inputState.guessing == 0) {
                            orderingSpecification_AST = currentAST.root;
                            currentAST.root = orderingSpecification_AST = this.astFactory.make(new ASTArray(1).add(this.astFactory.create(6, "desc")));
                            currentAST.child = orderingSpecification_AST != null && orderingSpecification_AST.getFirstChild() != null ? orderingSpecification_AST.getFirstChild() : orderingSpecification_AST;
                            currentAST.advanceChildToEnd();
                        }
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
                    this.recover(ex, _tokenSet_4);
                }
                throw ex;
            }
            this.returnAST = orderingSpecification_AST;
        }
        finally {
            this.traceOut("orderingSpecification");
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
            AST n_AST = null;
            this.trace("nullOrdering");
            try {
                AST tmp6_AST = null;
                tmp6_AST = this.astFactory.create(this.LT(1));
                this.match(16);
                this.nullPrecedence();
                n_AST = this.returnAST;
                if (this.inputState.guessing == 0) {
                    nullOrdering_AST = currentAST.root;
                    currentAST.root = nullOrdering_AST = this.astFactory.make(new ASTArray(1).add(this.astFactory.create(7, this.extractText(n_AST))));
                    currentAST.child = nullOrdering_AST != null && nullOrdering_AST.getFirstChild() != null ? nullOrdering_AST.getFirstChild() : nullOrdering_AST;
                    currentAST.advanceChildToEnd();
                }
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_1);
                }
                throw ex;
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
    public final void expression() throws RecognitionException, TokenStreamException {
        this.traceIn("expression");
        try {
            AST expression_AST;
            block18: {
                this.returnAST = null;
                ASTPair currentAST = new ASTPair();
                expression_AST = null;
                Token qi = null;
                AST qi_AST = null;
                AST f_AST = null;
                AST p_AST = null;
                Token i = null;
                AST i_AST = null;
                this.trace("expression");
                try {
                    if (this.LA(1) == 20) {
                        AST tmp7_AST = null;
                        tmp7_AST = this.astFactory.create(this.LT(1));
                        this.match(20);
                        qi = this.LT(1);
                        qi_AST = this.astFactory.create(qi);
                        this.match(21);
                        AST tmp8_AST = null;
                        tmp8_AST = this.astFactory.create(this.LT(1));
                        this.match(20);
                        if (this.inputState.guessing == 0) {
                            expression_AST = currentAST.root;
                            currentAST.root = expression_AST = this.quotedIdentifier(qi_AST);
                            currentAST.child = expression_AST != null && expression_AST.getFirstChild() != null ? expression_AST.getFirstChild() : expression_AST;
                            currentAST.advanceChildToEnd();
                        }
                        break block18;
                    }
                    boolean synPredMatched13 = false;
                    if (this.LA(1) == 21 && (this.LA(2) == 10 || this.LA(2) == 22) && _tokenSet_5.member(this.LA(3))) {
                        int _m13 = this.mark();
                        synPredMatched13 = true;
                        ++this.inputState.guessing;
                        try {
                            this.match(21);
                            while (this.LA(1) == 10) {
                                this.match(10);
                                this.match(21);
                            }
                            this.match(22);
                        }
                        catch (RecognitionException pe) {
                            synPredMatched13 = false;
                        }
                        this.rewind(_m13);
                        --this.inputState.guessing;
                    }
                    if (synPredMatched13) {
                        this.functionCall();
                        f_AST = this.returnAST;
                        if (this.inputState.guessing == 0) {
                            expression_AST = currentAST.root;
                            currentAST.root = expression_AST = f_AST;
                            currentAST.child = expression_AST != null && expression_AST.getFirstChild() != null ? expression_AST.getFirstChild() : expression_AST;
                            currentAST.advanceChildToEnd();
                        }
                        break block18;
                    }
                    if (this.LA(1) == 21 && this.LA(2) == 10 && this.LA(3) == 21) {
                        this.simplePropertyPath();
                        p_AST = this.returnAST;
                        if (this.inputState.guessing == 0) {
                            expression_AST = currentAST.root;
                            currentAST.root = expression_AST = this.resolveIdent(p_AST);
                            currentAST.child = expression_AST != null && expression_AST.getFirstChild() != null ? expression_AST.getFirstChild() : expression_AST;
                            currentAST.advanceChildToEnd();
                        }
                        break block18;
                    }
                    if (this.LA(1) == 21 && _tokenSet_6.member(this.LA(2))) {
                        i = this.LT(1);
                        i_AST = this.astFactory.create(i);
                        this.match(21);
                        if (this.inputState.guessing == 0) {
                            expression_AST = currentAST.root;
                            expression_AST = this.isFunctionName(i_AST) ? this.resolveFunction(i_AST) : this.resolveIdent(i_AST);
                            currentAST.root = expression_AST;
                            currentAST.child = expression_AST != null && expression_AST.getFirstChild() != null ? expression_AST.getFirstChild() : expression_AST;
                            currentAST.advanceChildToEnd();
                        }
                        break block18;
                    }
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
                catch (RecognitionException ex) {
                    if (this.inputState.guessing == 0) {
                        this.reportError(ex);
                        this.recover(ex, _tokenSet_6);
                    }
                    throw ex;
                }
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
    public final void functionCall() throws RecognitionException, TokenStreamException {
        this.traceIn("functionCall");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST functionCall_AST = null;
            AST fn_AST = null;
            AST pl_AST = null;
            this.trace("functionCall");
            try {
                this.functionName();
                fn_AST = this.returnAST;
                AST tmp9_AST = null;
                tmp9_AST = this.astFactory.create(this.LT(1));
                this.match(22);
                this.functionParameterList();
                pl_AST = this.returnAST;
                AST tmp10_AST = null;
                tmp10_AST = this.astFactory.create(this.LT(1));
                this.match(23);
                if (this.inputState.guessing == 0) {
                    functionCall_AST = currentAST.root;
                    functionCall_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(21, this.extractText(fn_AST))).add(pl_AST));
                    currentAST.root = functionCall_AST = this.resolveFunction(functionCall_AST);
                    currentAST.child = functionCall_AST != null && functionCall_AST.getFirstChild() != null ? functionCall_AST.getFirstChild() : functionCall_AST;
                    currentAST.advanceChildToEnd();
                }
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_6);
                }
                throw ex;
            }
            this.returnAST = functionCall_AST;
        }
        finally {
            this.traceOut("functionCall");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void simplePropertyPath() throws RecognitionException, TokenStreamException {
        this.traceIn("simplePropertyPath");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST simplePropertyPath_AST = null;
            Token i = null;
            AST i_AST = null;
            Token i2 = null;
            AST i2_AST = null;
            this.trace("simplePropertyPath");
            StringBuilder buffer = new StringBuilder();
            try {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.astFactory.addASTChild(currentAST, i_AST);
                this.match(21);
                if (this.inputState.guessing == 0) {
                    buffer.append(i.getText());
                }
                int _cnt34 = 0;
                while (true) {
                    if (this.LA(1) == 10) {
                        AST tmp11_AST = null;
                        tmp11_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp11_AST);
                        this.match(10);
                        i2 = this.LT(1);
                        i2_AST = this.astFactory.create(i2);
                        this.astFactory.addASTChild(currentAST, i2_AST);
                        this.match(21);
                        if (this.inputState.guessing == 0) {
                            buffer.append('.').append(i2.getText());
                        }
                    } else {
                        if (_cnt34 >= 1) break;
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    ++_cnt34;
                }
                if (this.inputState.guessing == 0) {
                    simplePropertyPath_AST = currentAST.root;
                    currentAST.root = simplePropertyPath_AST = this.astFactory.make(new ASTArray(1).add(this.astFactory.create(21, buffer.toString())));
                    currentAST.child = simplePropertyPath_AST != null && simplePropertyPath_AST.getFirstChild() != null ? simplePropertyPath_AST.getFirstChild() : simplePropertyPath_AST;
                    currentAST.advanceChildToEnd();
                }
                simplePropertyPath_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_6);
                }
                throw ex;
            }
            this.returnAST = simplePropertyPath_AST;
        }
        finally {
            this.traceOut("simplePropertyPath");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void functionCallCheck() throws RecognitionException, TokenStreamException {
        this.traceIn("functionCallCheck");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            Object functionCallCheck_AST = null;
            this.trace("functionCallCheck");
            try {
                AST tmp12_AST = null;
                tmp12_AST = this.astFactory.create(this.LT(1));
                this.match(21);
                while (this.LA(1) == 10) {
                    AST tmp13_AST = null;
                    tmp13_AST = this.astFactory.create(this.LT(1));
                    this.match(10);
                    AST tmp14_AST = null;
                    tmp14_AST = this.astFactory.create(this.LT(1));
                    this.match(21);
                }
                AST tmp15_AST = null;
                tmp15_AST = this.astFactory.create(this.LT(1));
                this.match(22);
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_0);
                }
                throw ex;
            }
            this.returnAST = functionCallCheck_AST;
        }
        finally {
            this.traceOut("functionCallCheck");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void functionName() throws RecognitionException, TokenStreamException {
        this.traceIn("functionName");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST functionName_AST = null;
            Token i = null;
            AST i_AST = null;
            Token i2 = null;
            AST i2_AST = null;
            this.trace("functionName");
            StringBuilder buffer = new StringBuilder();
            try {
                i = this.LT(1);
                i_AST = this.astFactory.create(i);
                this.astFactory.addASTChild(currentAST, i_AST);
                this.match(21);
                if (this.inputState.guessing == 0) {
                    buffer.append(i.getText());
                }
                while (this.LA(1) == 10) {
                    AST tmp16_AST = null;
                    tmp16_AST = this.astFactory.create(this.LT(1));
                    this.astFactory.addASTChild(currentAST, tmp16_AST);
                    this.match(10);
                    i2 = this.LT(1);
                    i2_AST = this.astFactory.create(i2);
                    this.astFactory.addASTChild(currentAST, i2_AST);
                    this.match(21);
                    if (this.inputState.guessing != 0) continue;
                    buffer.append('.').append(i2.getText());
                }
                if (this.inputState.guessing == 0) {
                    functionName_AST = currentAST.root;
                    currentAST.root = functionName_AST = this.astFactory.make(new ASTArray(1).add(this.astFactory.create(21, buffer.toString())));
                    currentAST.child = functionName_AST != null && functionName_AST.getFirstChild() != null ? functionName_AST.getFirstChild() : functionName_AST;
                    currentAST.advanceChildToEnd();
                }
                functionName_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_7);
                }
                throw ex;
            }
            this.returnAST = functionName_AST;
        }
        finally {
            this.traceOut("functionName");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void functionParameterList() throws RecognitionException, TokenStreamException {
        this.traceIn("functionParameterList");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST functionParameterList_AST = null;
            this.trace("functionParameterList");
            try {
                this.functionParameter();
                this.astFactory.addASTChild(currentAST, this.returnAST);
                while (this.LA(1) == 19) {
                    this.match(19);
                    this.functionParameter();
                    this.astFactory.addASTChild(currentAST, this.returnAST);
                }
                if (this.inputState.guessing == 0) {
                    functionParameterList_AST = currentAST.root;
                    currentAST.root = functionParameterList_AST = this.astFactory.make(new ASTArray(2).add(this.astFactory.create(9, "{param list}")).add(functionParameterList_AST));
                    currentAST.child = functionParameterList_AST != null && functionParameterList_AST.getFirstChild() != null ? functionParameterList_AST.getFirstChild() : functionParameterList_AST;
                    currentAST.advanceChildToEnd();
                }
                functionParameterList_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_8);
                }
                throw ex;
            }
            this.returnAST = functionParameterList_AST;
        }
        finally {
            this.traceOut("functionParameterList");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void functionParameter() throws RecognitionException, TokenStreamException {
        this.traceIn("functionParameter");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST functionParameter_AST = null;
            this.trace("functionParameter");
            try {
                switch (this.LA(1)) {
                    case 20: 
                    case 21: {
                        this.expression();
                        this.astFactory.addASTChild(currentAST, this.returnAST);
                        functionParameter_AST = currentAST.root;
                        break;
                    }
                    case 24: {
                        AST tmp18_AST = null;
                        tmp18_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp18_AST);
                        this.match(24);
                        functionParameter_AST = currentAST.root;
                        break;
                    }
                    case 25: {
                        AST tmp19_AST = null;
                        tmp19_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp19_AST);
                        this.match(25);
                        functionParameter_AST = currentAST.root;
                        break;
                    }
                    case 26: {
                        AST tmp20_AST = null;
                        tmp20_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp20_AST);
                        this.match(26);
                        functionParameter_AST = currentAST.root;
                        break;
                    }
                    case 27: {
                        AST tmp21_AST = null;
                        tmp21_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp21_AST);
                        this.match(27);
                        functionParameter_AST = currentAST.root;
                        break;
                    }
                    case 28: {
                        AST tmp22_AST = null;
                        tmp22_AST = this.astFactory.create(this.LT(1));
                        this.astFactory.addASTChild(currentAST, tmp22_AST);
                        this.match(28);
                        if (this.inputState.guessing == 0) {
                            functionParameter_AST = currentAST.root;
                            currentAST.root = functionParameter_AST = this.quotedString(functionParameter_AST);
                            currentAST.child = functionParameter_AST != null && functionParameter_AST.getFirstChild() != null ? functionParameter_AST.getFirstChild() : functionParameter_AST;
                            currentAST.advanceChildToEnd();
                        }
                        functionParameter_AST = currentAST.root;
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
                    this.recover(ex, _tokenSet_9);
                }
                throw ex;
            }
            this.returnAST = functionParameter_AST;
        }
        finally {
            this.traceOut("functionParameter");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void collationName() throws RecognitionException, TokenStreamException {
        this.traceIn("collationName");
        try {
            this.returnAST = null;
            ASTPair currentAST = new ASTPair();
            AST collationName_AST = null;
            this.trace("collationSpecification");
            try {
                AST tmp23_AST = null;
                tmp23_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp23_AST);
                this.match(21);
                collationName_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_3);
                }
                throw ex;
            }
            this.returnAST = collationName_AST;
        }
        finally {
            this.traceOut("collationName");
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
            this.trace("nullPrecedence");
            try {
                AST tmp24_AST = null;
                tmp24_AST = this.astFactory.create(this.LT(1));
                this.astFactory.addASTChild(currentAST, tmp24_AST);
                this.match(21);
                if (this.inputState.guessing == 0) {
                    nullPrecedence_AST = currentAST.root;
                    if ("first".equalsIgnoreCase(nullPrecedence_AST.getText())) {
                        nullPrecedence_AST.setType(17);
                    } else if ("last".equalsIgnoreCase(nullPrecedence_AST.getText())) {
                        nullPrecedence_AST.setType(18);
                    } else {
                        throw new SemanticException("Expecting 'first' or 'last', but found '" + nullPrecedence_AST.getText() + "' as null ordering precedence.");
                    }
                }
                nullPrecedence_AST = currentAST.root;
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    this.recover(ex, _tokenSet_1);
                }
                throw ex;
            }
            this.returnAST = nullPrecedence_AST;
        }
        finally {
            this.traceOut("nullPrecedence");
        }
    }

    protected void buildTokenTypeASTClassMap() {
        this.tokenTypeToASTClassMap = null;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[]{2L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[]{524290L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[]{1611259906L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[]{1611251714L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[]{589826L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[]{523239424L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_6() {
        long[] data = new long[]{1619648514L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_7() {
        long[] data = new long[]{0x400000L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_8() {
        long[] data = new long[]{0x800000L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_9() {
        long[] data = new long[]{0x880000L, 0L};
        return data;
    }
}

