/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.MismatchedTokenException
 *  antlr.NoViableAltException
 *  antlr.RecognitionException
 *  antlr.TreeParser
 *  antlr.collections.AST
 *  antlr.collections.impl.BitSet
 */
package org.hibernate.hql.internal.antlr;

import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.TreeParser;
import antlr.collections.AST;
import antlr.collections.impl.BitSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.hql.internal.antlr.SqlTokenTypes;

public class SqlGeneratorBase
extends TreeParser
implements SqlTokenTypes {
    private StringBuilder buf = new StringBuilder();
    private boolean captureExpression = false;
    protected List<StringBuilder> exprs = new ArrayList<StringBuilder>(Arrays.asList(new StringBuilder()));
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"all\"", "\"any\"", "\"and\"", "\"as\"", "\"asc\"", "\"avg\"", "\"between\"", "\"class\"", "\"count\"", "\"delete\"", "\"desc\"", "DOT", "\"distinct\"", "\"elements\"", "\"escape\"", "\"exists\"", "\"false\"", "\"fetch\"", "FK_REF", "\"from\"", "\"full\"", "\"group\"", "\"having\"", "\"in\"", "\"indices\"", "\"inner\"", "\"insert\"", "\"into\"", "\"is\"", "\"join\"", "\"left\"", "\"like\"", "\"max\"", "\"min\"", "\"new\"", "\"not\"", "\"null\"", "\"or\"", "\"order\"", "\"outer\"", "\"properties\"", "\"right\"", "\"select\"", "\"set\"", "\"some\"", "\"sum\"", "\"true\"", "\"update\"", "\"versioned\"", "\"where\"", "\"nulls\"", "FIRST", "LAST", "\"case\"", "\"end\"", "\"else\"", "\"then\"", "\"when\"", "\"on\"", "\"with\"", "\"both\"", "\"empty\"", "\"leading\"", "\"member\"", "\"object\"", "\"of\"", "\"trailing\"", "KEY", "VALUE", "ENTRY", "AGGREGATE", "ALIAS", "CONSTRUCTOR", "CASE2", "CAST", "COLL_PATH", "EXPR_LIST", "FILTER_ENTITY", "IN_LIST", "INDEX_OP", "IS_NOT_NULL", "IS_NULL", "METHOD_CALL", "NOT_BETWEEN", "NOT_IN", "NOT_LIKE", "ORDER_ELEMENT", "QUERY", "RANGE", "ROW_STAR", "SELECT_FROM", "COLL_SIZE", "UNARY_MINUS", "UNARY_PLUS", "VECTOR_EXPR", "WEIRD_IDENT", "CONSTANT", "NUM_DOUBLE", "NUM_FLOAT", "NUM_LONG", "NUM_BIG_INTEGER", "NUM_BIG_DECIMAL", "JAVA_CONSTANT", "COMMA", "EQ", "OPEN", "CLOSE", "IDENT", "\"by\"", "\"ascending\"", "\"descending\"", "NE", "SQL_NE", "LT", "GT", "LE", "GE", "CONCAT", "PLUS", "MINUS", "STAR", "DIV", "MOD", "OPEN_BRACKET", "CLOSE_BRACKET", "\"fk\"", "QUOTED_STRING", "COLON", "PARAM", "NUM_INT", "ID_START_LETTER", "ID_LETTER", "ESCqs", "WS", "HEX_DIGIT", "EXPONENT", "FLOAT_SUFFIX", "FROM_FRAGMENT", "IMPLIED_FROM", "JOIN_FRAGMENT", "ENTITY_JOIN", "SELECT_CLAUSE", "LEFT_OUTER", "RIGHT_OUTER", "ALIAS_REF", "PROPERTY_REF", "SQL_TOKEN", "SELECT_COLUMNS", "SELECT_EXPR", "THETA_JOINS", "FILTERS", "METHOD_NAME", "NAMED_PARAM", "BOGUS", "RESULT_VARIABLE_REF"};
    public static final BitSet _tokenSet_0 = new BitSet(SqlGeneratorBase.mk_tokenSet_0());
    public static final BitSet _tokenSet_1 = new BitSet(SqlGeneratorBase.mk_tokenSet_1());
    public static final BitSet _tokenSet_2 = new BitSet(SqlGeneratorBase.mk_tokenSet_2());
    public static final BitSet _tokenSet_3 = new BitSet(SqlGeneratorBase.mk_tokenSet_3());
    public static final BitSet _tokenSet_4 = new BitSet(SqlGeneratorBase.mk_tokenSet_4());
    public static final BitSet _tokenSet_5 = new BitSet(SqlGeneratorBase.mk_tokenSet_5());

    protected void out(String s) {
        this.getStringBuilder().append(s);
    }

    protected int getLastChar() {
        int len = this.buf.length();
        if (len == 0) {
            return -1;
        }
        return this.buf.charAt(len - 1);
    }

    protected void optionalSpace() {
    }

    protected void out(AST n) {
        this.out(n.getText());
    }

    protected void separator(AST n, String sep) {
        if (n.getNextSibling() != null) {
            this.out(sep);
        }
    }

    protected boolean hasText(AST a) {
        String t = a.getText();
        return t != null && t.length() > 0;
    }

    protected void fromFragmentSeparator(AST a) {
    }

    protected void nestedFromFragment(AST d, AST parent) {
    }

    protected StringBuilder getStringBuilder() {
        return this.captureExpression ? this.exprs.get(this.exprs.size() - 1) : this.buf;
    }

    protected void nyi(AST n) {
        throw new UnsupportedOperationException("Unsupported node: " + n);
    }

    protected void beginFunctionTemplate(AST m, AST i) {
        this.out(i);
        this.out("(");
    }

    protected void endFunctionTemplate(AST m) {
        this.out(")");
    }

    protected void betweenFunctionArguments() {
        this.out(", ");
    }

    protected void captureExpressionStart() {
        if (this.captureExpression) {
            this.exprs.add(new StringBuilder());
        } else {
            this.captureExpression = true;
        }
    }

    protected void captureExpressionFinish() {
        if (this.exprs.size() == 1) {
            this.captureExpression = false;
        }
    }

    protected String resetCapture() {
        StringBuilder sb = this.exprs.remove(this.exprs.size() - 1);
        String expression = sb.toString();
        if (this.exprs.isEmpty()) {
            sb.setLength(0);
            this.exprs.add(sb);
        }
        return expression;
    }

    protected String renderOrderByElement(String expression, String order, String nulls) {
        throw new UnsupportedOperationException("Concrete SQL generator should override this method.");
    }

    protected void renderCollectionSize(AST collectionSizeNode) {
        throw new UnsupportedOperationException("Concrete SQL generator should override this method.");
    }

    public SqlGeneratorBase() {
        this.tokenNames = _tokenNames;
    }

    public final void statement(AST _t) throws RecognitionException {
        AST statement_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 46: {
                    this.selectStatement(_t);
                    _t = this._retTree;
                    break;
                }
                case 51: {
                    this.updateStatement(_t);
                    _t = this._retTree;
                    break;
                }
                case 13: {
                    this.deleteStatement(_t);
                    _t = this._retTree;
                    break;
                }
                case 30: {
                    this.insertStatement(_t);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void selectStatement(AST _t) throws RecognitionException {
        AST selectStatement_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t576 = _t;
            AST tmp1_AST_in = _t;
            this.match(_t, 46);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out("select ");
            }
            this.selectClause(_t);
            _t = this._retTree;
            this.from(_t);
            _t = this._retTree;
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 53: {
                    AST __t578 = _t;
                    AST tmp2_AST_in = _t;
                    this.match(_t, 53);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(" where ");
                    }
                    this.whereExpr(_t);
                    _t = this._retTree;
                    _t = __t578;
                    _t = _t.getNextSibling();
                    break;
                }
                case 3: 
                case 25: 
                case 42: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 25: {
                    AST __t580 = _t;
                    AST tmp3_AST_in = _t;
                    this.match(_t, 25);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(" group by ");
                    }
                    this.groupExprs(_t);
                    _t = this._retTree;
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 26: {
                            AST __t582 = _t;
                            AST tmp4_AST_in = _t;
                            this.match(_t, 26);
                            _t = _t.getFirstChild();
                            if (this.inputState.guessing == 0) {
                                this.out(" having ");
                            }
                            this.booleanExpr(_t, false);
                            _t = this._retTree;
                            _t = __t582;
                            _t = _t.getNextSibling();
                            break;
                        }
                        case 3: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(_t);
                        }
                    }
                    _t = __t580;
                    _t = _t.getNextSibling();
                    break;
                }
                case 3: 
                case 42: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 42: {
                    AST __t584 = _t;
                    AST tmp5_AST_in = _t;
                    this.match(_t, 42);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(" order by ");
                    }
                    this.orderExprs(_t);
                    _t = this._retTree;
                    _t = __t584;
                    _t = _t.getNextSibling();
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            _t = __t576;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void updateStatement(AST _t) throws RecognitionException {
        AST updateStatement_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t586 = _t;
            AST tmp6_AST_in = _t;
            this.match(_t, 51);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out("update ");
            }
            AST __t587 = _t;
            AST tmp7_AST_in = _t;
            this.match(_t, 23);
            _t = _t.getFirstChild();
            this.fromTable(_t);
            _t = this._retTree;
            _t = __t587;
            _t = _t.getNextSibling();
            this.setClause(_t);
            _t = this._retTree;
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 53: {
                    this.whereClause(_t);
                    _t = this._retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            _t = __t586;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void deleteStatement(AST _t) throws RecognitionException {
        AST deleteStatement_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t590 = _t;
            AST tmp8_AST_in = _t;
            this.match(_t, 13);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out("delete");
            }
            this.from(_t);
            _t = this._retTree;
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 53: {
                    this.whereClause(_t);
                    _t = this._retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            _t = __t590;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void insertStatement(AST _t) throws RecognitionException {
        AST insertStatement_AST_in = _t == ASTNULL ? null : _t;
        AST i = null;
        try {
            AST __t593 = _t;
            AST tmp9_AST_in = _t;
            this.match(_t, 30);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out("insert ");
            }
            i = _t;
            this.match(_t, 31);
            _t = _t.getNextSibling();
            if (this.inputState.guessing == 0) {
                this.out(i);
                this.out(" ");
            }
            this.selectStatement(_t);
            _t = this._retTree;
            _t = __t593;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void selectClause(AST _t) throws RecognitionException {
        AST selectClause_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t624 = _t;
            AST tmp10_AST_in = _t;
            this.match(_t, 145);
            _t = _t.getFirstChild();
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 4: 
                case 16: {
                    this.distinctOrAll(_t);
                    _t = this._retTree;
                    break;
                }
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 27: 
                case 35: 
                case 39: 
                case 41: 
                case 46: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 76: 
                case 77: 
                case 78: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 152: 
                case 156: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            int _cnt627 = 0;
            while (true) {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (!_tokenSet_0.member(_t.getType())) {
                    if (_cnt627 >= 1) break;
                    throw new NoViableAltException(_t);
                }
                this.selectColumn(_t);
                _t = this._retTree;
                ++_cnt627;
            }
            _t = __t624;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void from(AST _t) throws RecognitionException {
        AST from_AST_in = _t == ASTNULL ? null : _t;
        AST f = null;
        try {
            AST __t643 = _t;
            f = _t == ASTNULL ? null : _t;
            this.match(_t, 23);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out(" from ");
            }
            while (true) {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (_t.getType() != 141 && _t.getType() != 143 && _t.getType() != 144) break;
                this.fromTable(_t);
                _t = this._retTree;
            }
            _t = __t643;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void whereExpr(AST _t) throws RecognitionException {
        AST whereExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            block1 : switch (_t.getType()) {
                case 154: {
                    this.filters(_t);
                    _t = this._retTree;
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 153: {
                            if (this.inputState.guessing == 0) {
                                this.out(" and ");
                            }
                            this.thetaJoins(_t);
                            _t = this._retTree;
                            break;
                        }
                        case 3: 
                        case 6: 
                        case 10: 
                        case 19: 
                        case 27: 
                        case 35: 
                        case 39: 
                        case 41: 
                        case 84: 
                        case 85: 
                        case 87: 
                        case 88: 
                        case 89: 
                        case 108: 
                        case 115: 
                        case 117: 
                        case 118: 
                        case 119: 
                        case 120: 
                        case 150: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(_t);
                        }
                    }
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 6: 
                        case 10: 
                        case 19: 
                        case 27: 
                        case 35: 
                        case 39: 
                        case 41: 
                        case 84: 
                        case 85: 
                        case 87: 
                        case 88: 
                        case 89: 
                        case 108: 
                        case 115: 
                        case 117: 
                        case 118: 
                        case 119: 
                        case 120: 
                        case 150: {
                            if (this.inputState.guessing == 0) {
                                this.out(" and ");
                            }
                            this.booleanExpr(_t, true);
                            _t = this._retTree;
                            break block1;
                        }
                        case 3: {
                            break block1;
                        }
                    }
                    throw new NoViableAltException(_t);
                }
                case 153: {
                    this.thetaJoins(_t);
                    _t = this._retTree;
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 6: 
                        case 10: 
                        case 19: 
                        case 27: 
                        case 35: 
                        case 39: 
                        case 41: 
                        case 84: 
                        case 85: 
                        case 87: 
                        case 88: 
                        case 89: 
                        case 108: 
                        case 115: 
                        case 117: 
                        case 118: 
                        case 119: 
                        case 120: 
                        case 150: {
                            if (this.inputState.guessing == 0) {
                                this.out(" and ");
                            }
                            this.booleanExpr(_t, true);
                            _t = this._retTree;
                            break block1;
                        }
                        case 3: {
                            break block1;
                        }
                    }
                    throw new NoViableAltException(_t);
                }
                case 6: 
                case 10: 
                case 19: 
                case 27: 
                case 35: 
                case 39: 
                case 41: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 108: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 150: {
                    this.booleanExpr(_t, false);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void groupExprs(AST _t) throws RecognitionException {
        AST groupExprs_AST_in = _t == ASTNULL ? null : _t;
        try {
            this.expr(_t);
            _t = this._retTree;
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 4: 
                case 5: 
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 46: 
                case 48: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 98: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    if (this.inputState.guessing == 0) {
                        this.out(" , ");
                    }
                    this.groupExprs(_t);
                    _t = this._retTree;
                    break;
                }
                case 3: 
                case 26: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void booleanExpr(AST _t, boolean parens) throws RecognitionException {
        AST booleanExpr_AST_in = _t == ASTNULL ? null : _t;
        AST st = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 6: 
                case 39: 
                case 41: {
                    this.booleanOp(_t, parens);
                    _t = this._retTree;
                    break;
                }
                case 10: 
                case 19: 
                case 27: 
                case 35: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 108: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: {
                    this.comparisonExpr(_t, parens);
                    _t = this._retTree;
                    break;
                }
                case 150: {
                    st = _t;
                    this.match(_t, 150);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(st);
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void orderExprs(AST _t) throws RecognitionException {
        AST orderExprs_AST_in = _t == ASTNULL ? null : _t;
        AST e = null;
        AST dir = null;
        String ordExp = null;
        String ordDir = null;
        String ordNul = null;
        try {
            if (this.inputState.guessing == 0) {
                this.captureExpressionStart();
            }
            e = _t == ASTNULL ? null : _t;
            this.expr(_t);
            _t = this._retTree;
            if (this.inputState.guessing == 0) {
                this.captureExpressionFinish();
                ordExp = this.resetCapture();
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 8: 
                case 14: {
                    dir = _t == ASTNULL ? null : _t;
                    this.orderDirection(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing != 0) break;
                    ordDir = dir.getText();
                    break;
                }
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 46: 
                case 48: 
                case 50: 
                case 54: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 98: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 54: {
                    ordNul = this.nullOrdering(_t);
                    _t = this._retTree;
                    break;
                }
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 46: 
                case 48: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 98: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            if (this.inputState.guessing == 0) {
                this.out(e.getType() == 150 && ordDir == null && ordNul == null ? ordExp : this.renderOrderByElement(ordExp, ordDir, ordNul));
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 4: 
                case 5: 
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 46: 
                case 48: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 98: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    if (this.inputState.guessing == 0) {
                        this.out(", ");
                    }
                    this.orderExprs(_t);
                    _t = this._retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void fromTable(AST _t) throws RecognitionException {
        AST fromTable_AST_in = _t == ASTNULL ? null : _t;
        AST a = null;
        AST b = null;
        AST c = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 141: {
                    AST __t647 = _t;
                    a = _t == ASTNULL ? null : _t;
                    this.match(_t, 141);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(a);
                    }
                    while (true) {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() != 141 && _t.getType() != 143 && _t.getType() != 144) break;
                        this.tableJoin(_t, a);
                        _t = this._retTree;
                    }
                    if (this.inputState.guessing == 0) {
                        this.fromFragmentSeparator(a);
                    }
                    _t = __t647;
                    _t = _t.getNextSibling();
                    break;
                }
                case 143: {
                    AST __t650 = _t;
                    b = _t == ASTNULL ? null : _t;
                    this.match(_t, 143);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(b);
                    }
                    while (true) {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() != 141 && _t.getType() != 143 && _t.getType() != 144) break;
                        this.tableJoin(_t, b);
                        _t = this._retTree;
                    }
                    if (this.inputState.guessing == 0) {
                        this.fromFragmentSeparator(b);
                    }
                    _t = __t650;
                    _t = _t.getNextSibling();
                    break;
                }
                case 144: {
                    AST __t653 = _t;
                    c = _t == ASTNULL ? null : _t;
                    this.match(_t, 144);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(c);
                    }
                    while (true) {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() != 141 && _t.getType() != 143 && _t.getType() != 144) break;
                        this.tableJoin(_t, c);
                        _t = this._retTree;
                    }
                    if (this.inputState.guessing == 0) {
                        this.fromFragmentSeparator(c);
                    }
                    _t = __t653;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void setClause(AST _t) throws RecognitionException {
        AST setClause_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t595 = _t;
            AST tmp11_AST_in = _t;
            this.match(_t, 47);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out(" set ");
            }
            this.comparisonExpr(_t, false);
            _t = this._retTree;
            while (true) {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (!_tokenSet_1.member(_t.getType())) break;
                if (this.inputState.guessing == 0) {
                    this.out(", ");
                }
                this.comparisonExpr(_t, false);
                _t = this._retTree;
            }
            _t = __t595;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void whereClause(AST _t) throws RecognitionException {
        AST whereClause_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t599 = _t;
            AST tmp12_AST_in = _t;
            this.match(_t, 53);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out(" where ");
            }
            this.whereClauseExpr(_t);
            _t = this._retTree;
            _t = __t599;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void comparisonExpr(AST _t, boolean parens) throws RecognitionException {
        AST comparisonExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 108: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: {
                    this.binaryComparisonExpression(_t);
                    _t = this._retTree;
                    break;
                }
                case 10: 
                case 19: 
                case 27: 
                case 35: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: {
                    if (this.inputState.guessing == 0 && parens) {
                        this.out("(");
                    }
                    this.exoticComparisonExpression(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0 && parens) {
                        this.out(")");
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void whereClauseExpr(AST _t) throws RecognitionException {
        block10: {
            AST whereClauseExpr_AST_in = _t == ASTNULL ? null : _t;
            try {
                boolean synPredMatched602 = false;
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (_t.getType() == 150) {
                    AST __t602 = _t;
                    synPredMatched602 = true;
                    ++this.inputState.guessing;
                    try {
                        AST tmp13_AST_in = _t;
                        this.match(_t, 150);
                        _t = _t.getNextSibling();
                    }
                    catch (RecognitionException pe) {
                        synPredMatched602 = false;
                    }
                    _t = __t602;
                    --this.inputState.guessing;
                }
                if (synPredMatched602) {
                    this.conditionList(_t);
                    _t = this._retTree;
                    break block10;
                }
                if (_tokenSet_2.member(_t.getType())) {
                    this.booleanExpr(_t, false);
                    _t = this._retTree;
                    break block10;
                }
                throw new NoViableAltException(_t);
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    if (_t != null) {
                        _t = _t.getNextSibling();
                    }
                }
                throw ex;
            }
        }
        this._retTree = _t;
    }

    public final void conditionList(AST _t) throws RecognitionException {
        AST conditionList_AST_in = _t == ASTNULL ? null : _t;
        try {
            this.sqlToken(_t);
            _t = this._retTree;
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 150: {
                    if (this.inputState.guessing == 0) {
                        this.out(" and ");
                    }
                    this.conditionList(_t);
                    _t = this._retTree;
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void expr(AST _t) throws RecognitionException {
        AST expr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    this.simpleExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 98: {
                    this.tupleExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 46: {
                    this.parenSelect(_t);
                    _t = this._retTree;
                    break;
                }
                case 5: {
                    AST __t701 = _t;
                    AST tmp14_AST_in = _t;
                    this.match(_t, 5);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out("any ");
                    }
                    this.quantified(_t);
                    _t = this._retTree;
                    _t = __t701;
                    _t = _t.getNextSibling();
                    break;
                }
                case 4: {
                    AST __t702 = _t;
                    AST tmp15_AST_in = _t;
                    this.match(_t, 4);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out("all ");
                    }
                    this.quantified(_t);
                    _t = this._retTree;
                    _t = __t702;
                    _t = _t.getNextSibling();
                    break;
                }
                case 48: {
                    AST __t703 = _t;
                    AST tmp16_AST_in = _t;
                    this.match(_t, 48);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out("some ");
                    }
                    this.quantified(_t);
                    _t = this._retTree;
                    _t = __t703;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void orderDirection(AST _t) throws RecognitionException {
        AST orderDirection_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 8: {
                    AST tmp17_AST_in = _t;
                    this.match(_t, 8);
                    _t = _t.getNextSibling();
                    break;
                }
                case 14: {
                    AST tmp18_AST_in = _t;
                    this.match(_t, 14);
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final String nullOrdering(AST _t) throws RecognitionException {
        String nullOrdExp = null;
        AST nullOrdering_AST_in = _t == ASTNULL ? null : _t;
        AST fl = null;
        try {
            AST tmp19_AST_in = _t;
            this.match(_t, 54);
            _t = _t.getNextSibling();
            fl = _t == ASTNULL ? null : _t;
            this.nullPrecedence(_t);
            _t = this._retTree;
            if (this.inputState.guessing == 0) {
                nullOrdExp = fl.getText();
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
        return nullOrdExp;
    }

    public final void nullPrecedence(AST _t) throws RecognitionException {
        AST nullPrecedence_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 55: {
                    AST tmp20_AST_in = _t;
                    this.match(_t, 55);
                    _t = _t.getNextSibling();
                    break;
                }
                case 56: {
                    AST tmp21_AST_in = _t;
                    this.match(_t, 56);
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void filters(AST _t) throws RecognitionException {
        AST filters_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t618 = _t;
            AST tmp22_AST_in = _t;
            this.match(_t, 154);
            _t = _t.getFirstChild();
            this.conditionList(_t);
            _t = this._retTree;
            _t = __t618;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void thetaJoins(AST _t) throws RecognitionException {
        AST thetaJoins_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t620 = _t;
            AST tmp23_AST_in = _t;
            this.match(_t, 153);
            _t = _t.getFirstChild();
            this.conditionList(_t);
            _t = this._retTree;
            _t = __t620;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void sqlToken(AST _t) throws RecognitionException {
        AST sqlToken_AST_in = _t == ASTNULL ? null : _t;
        AST t = null;
        try {
            t = _t;
            this.match(_t, 150);
            _t = _t.getNextSibling();
            if (this.inputState.guessing == 0) {
                this.out(t);
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void distinctOrAll(AST _t) throws RecognitionException {
        AST distinctOrAll_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 16: {
                    AST tmp24_AST_in = _t;
                    this.match(_t, 16);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out("distinct ");
                    }
                    break;
                }
                case 4: {
                    AST tmp25_AST_in = _t;
                    this.match(_t, 4);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out("all ");
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void selectColumn(AST _t) throws RecognitionException {
        AST selectColumn_AST_in = _t == ASTNULL ? null : _t;
        AST p = null;
        AST sc = null;
        try {
            p = _t == ASTNULL ? null : _t;
            this.selectExpr(_t);
            _t = this._retTree;
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 151: {
                    sc = _t;
                    this.match(_t, 151);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing != 0) break;
                    this.out(sc);
                    break;
                }
                case 3: 
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 27: 
                case 35: 
                case 39: 
                case 41: 
                case 46: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 76: 
                case 77: 
                case 78: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 152: 
                case 156: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            if (this.inputState.guessing == 0) {
                this.separator(sc != null ? sc : p, ", ");
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void selectExpr(AST _t) throws RecognitionException {
        AST selectExpr_AST_in = _t == ASTNULL ? null : _t;
        AST e = null;
        AST mcr = null;
        AST c = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 15: 
                case 148: 
                case 150: 
                case 152: {
                    e = _t == ASTNULL ? null : _t;
                    this.selectAtom(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(e);
                    }
                    break;
                }
                case 71: 
                case 72: 
                case 73: {
                    mcr = _t == ASTNULL ? null : _t;
                    this.mapComponentReference(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(mcr);
                    }
                    break;
                }
                case 12: {
                    this.count(_t);
                    _t = this._retTree;
                    break;
                }
                case 76: {
                    AST __t631 = _t;
                    AST tmp26_AST_in = _t;
                    this.match(_t, 76);
                    _t = _t.getFirstChild();
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 15: {
                            AST tmp27_AST_in = _t;
                            this.match(_t, 15);
                            _t = _t.getNextSibling();
                            break;
                        }
                        case 111: {
                            AST tmp28_AST_in = _t;
                            this.match(_t, 111);
                            _t = _t.getNextSibling();
                            break;
                        }
                        default: {
                            throw new NoViableAltException(_t);
                        }
                    }
                    int _cnt634 = 0;
                    while (true) {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (!_tokenSet_0.member(_t.getType())) {
                            if (_cnt634 >= 1) break;
                            throw new NoViableAltException(_t);
                        }
                        this.selectColumn(_t);
                        _t = this._retTree;
                        ++_cnt634;
                    }
                    _t = __t631;
                    _t = _t.getNextSibling();
                    break;
                }
                case 78: 
                case 86: 
                case 95: {
                    this.methodCall(_t);
                    _t = this._retTree;
                    break;
                }
                case 74: {
                    this.aggregate(_t);
                    _t = this._retTree;
                    break;
                }
                case 20: 
                case 50: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 111: 
                case 130: 
                case 133: {
                    c = _t == ASTNULL ? null : _t;
                    this.constant(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(c);
                    }
                    break;
                }
                case 57: 
                case 77: 
                case 96: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: {
                    this.arithmeticExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 6: 
                case 10: 
                case 19: 
                case 27: 
                case 35: 
                case 39: 
                case 41: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 108: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: {
                    this.selectBooleanExpr(_t, false);
                    _t = this._retTree;
                    break;
                }
                case 132: 
                case 156: {
                    this.parameter(_t);
                    _t = this._retTree;
                    break;
                }
                case 46: {
                    if (this.inputState.guessing == 0) {
                        this.out("(");
                    }
                    this.selectStatement(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(")");
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void selectAtom(AST _t) throws RecognitionException {
        AST selectAtom_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 15: {
                    AST tmp29_AST_in = _t;
                    this.match(_t, 15);
                    _t = _t.getNextSibling();
                    break;
                }
                case 150: {
                    AST tmp30_AST_in = _t;
                    this.match(_t, 150);
                    _t = _t.getNextSibling();
                    break;
                }
                case 148: {
                    AST tmp31_AST_in = _t;
                    this.match(_t, 148);
                    _t = _t.getNextSibling();
                    break;
                }
                case 152: {
                    AST tmp32_AST_in = _t;
                    this.match(_t, 152);
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void mapComponentReference(AST _t) throws RecognitionException {
        AST mapComponentReference_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 71: {
                    AST tmp33_AST_in = _t;
                    this.match(_t, 71);
                    _t = _t.getNextSibling();
                    break;
                }
                case 72: {
                    AST tmp34_AST_in = _t;
                    this.match(_t, 72);
                    _t = _t.getNextSibling();
                    break;
                }
                case 73: {
                    AST tmp35_AST_in = _t;
                    this.match(_t, 73);
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void count(AST _t) throws RecognitionException {
        AST count_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t636 = _t;
            AST tmp36_AST_in = _t;
            this.match(_t, 12);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out("count(");
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 4: 
                case 16: {
                    this.distinctOrAll(_t);
                    _t = this._retTree;
                    break;
                }
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 93: 
                case 95: 
                case 96: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            this.countExpr(_t);
            _t = this._retTree;
            if (this.inputState.guessing == 0) {
                this.out(")");
            }
            _t = __t636;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void methodCall(AST _t) throws RecognitionException {
        AST methodCall_AST_in = _t == ASTNULL ? null : _t;
        AST m = null;
        AST i = null;
        AST c = null;
        AST cs = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 86: {
                    AST __t744 = _t;
                    m = _t == ASTNULL ? null : _t;
                    this.match(_t, 86);
                    i = _t = _t.getFirstChild();
                    this.match(_t, 155);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.beginFunctionTemplate(m, i);
                    }
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 80: {
                            AST __t746 = _t;
                            AST tmp37_AST_in = _t;
                            this.match(_t, 80);
                            _t = _t.getFirstChild();
                            if (_t == null) {
                                _t = ASTNULL;
                            }
                            switch (_t.getType()) {
                                case 4: 
                                case 5: 
                                case 6: 
                                case 10: 
                                case 12: 
                                case 15: 
                                case 19: 
                                case 20: 
                                case 22: 
                                case 27: 
                                case 35: 
                                case 39: 
                                case 40: 
                                case 41: 
                                case 46: 
                                case 48: 
                                case 50: 
                                case 57: 
                                case 71: 
                                case 72: 
                                case 73: 
                                case 74: 
                                case 77: 
                                case 78: 
                                case 83: 
                                case 84: 
                                case 85: 
                                case 86: 
                                case 87: 
                                case 88: 
                                case 89: 
                                case 95: 
                                case 96: 
                                case 98: 
                                case 100: 
                                case 101: 
                                case 102: 
                                case 103: 
                                case 104: 
                                case 105: 
                                case 106: 
                                case 108: 
                                case 111: 
                                case 115: 
                                case 117: 
                                case 118: 
                                case 119: 
                                case 120: 
                                case 122: 
                                case 123: 
                                case 124: 
                                case 125: 
                                case 126: 
                                case 130: 
                                case 132: 
                                case 133: 
                                case 148: 
                                case 150: 
                                case 156: 
                                case 158: {
                                    this.arguments(_t);
                                    _t = this._retTree;
                                    break;
                                }
                                case 3: {
                                    break;
                                }
                                default: {
                                    throw new NoViableAltException(_t);
                                }
                            }
                            _t = __t746;
                            _t = _t.getNextSibling();
                            break;
                        }
                        case 3: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(_t);
                        }
                    }
                    if (this.inputState.guessing == 0) {
                        this.endFunctionTemplate(m);
                    }
                    _t = __t744;
                    _t = _t.getNextSibling();
                    break;
                }
                case 78: {
                    AST __t748 = _t;
                    c = _t == ASTNULL ? null : _t;
                    this.match(_t, 78);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.beginFunctionTemplate(c, c);
                    }
                    this.castExpression(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.betweenFunctionArguments();
                    }
                    this.castTargetType(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.endFunctionTemplate(c);
                    }
                    _t = __t748;
                    _t = _t.getNextSibling();
                    break;
                }
                case 95: {
                    cs = _t;
                    this.match(_t, 95);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.renderCollectionSize(cs);
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void aggregate(AST _t) throws RecognitionException {
        AST aggregate_AST_in = _t == ASTNULL ? null : _t;
        AST a = null;
        try {
            AST __t742 = _t;
            a = _t == ASTNULL ? null : _t;
            this.match(_t, 74);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.beginFunctionTemplate(a, a);
            }
            this.expr(_t);
            _t = this._retTree;
            if (this.inputState.guessing == 0) {
                this.endFunctionTemplate(a);
            }
            _t = __t742;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void constant(AST _t) throws RecognitionException {
        AST constant_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 101: {
                    AST tmp38_AST_in = _t;
                    this.match(_t, 101);
                    _t = _t.getNextSibling();
                    break;
                }
                case 102: {
                    AST tmp39_AST_in = _t;
                    this.match(_t, 102);
                    _t = _t.getNextSibling();
                    break;
                }
                case 133: {
                    AST tmp40_AST_in = _t;
                    this.match(_t, 133);
                    _t = _t.getNextSibling();
                    break;
                }
                case 103: {
                    AST tmp41_AST_in = _t;
                    this.match(_t, 103);
                    _t = _t.getNextSibling();
                    break;
                }
                case 104: {
                    AST tmp42_AST_in = _t;
                    this.match(_t, 104);
                    _t = _t.getNextSibling();
                    break;
                }
                case 105: {
                    AST tmp43_AST_in = _t;
                    this.match(_t, 105);
                    _t = _t.getNextSibling();
                    break;
                }
                case 130: {
                    AST tmp44_AST_in = _t;
                    this.match(_t, 130);
                    _t = _t.getNextSibling();
                    break;
                }
                case 100: {
                    AST tmp45_AST_in = _t;
                    this.match(_t, 100);
                    _t = _t.getNextSibling();
                    break;
                }
                case 106: {
                    AST tmp46_AST_in = _t;
                    this.match(_t, 106);
                    _t = _t.getNextSibling();
                    break;
                }
                case 50: {
                    AST tmp47_AST_in = _t;
                    this.match(_t, 50);
                    _t = _t.getNextSibling();
                    break;
                }
                case 20: {
                    AST tmp48_AST_in = _t;
                    this.match(_t, 20);
                    _t = _t.getNextSibling();
                    break;
                }
                case 111: {
                    AST tmp49_AST_in = _t;
                    this.match(_t, 111);
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void arithmeticExpr(AST _t) throws RecognitionException {
        AST arithmeticExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 122: 
                case 123: {
                    this.additiveExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 124: 
                case 125: 
                case 126: {
                    this.multiplicativeExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 96: {
                    AST __t714 = _t;
                    AST tmp50_AST_in = _t;
                    this.match(_t, 96);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out("-");
                    }
                    this.nestedExprAfterMinusDiv(_t);
                    _t = this._retTree;
                    _t = __t714;
                    _t = _t.getNextSibling();
                    break;
                }
                case 57: 
                case 77: {
                    this.caseExpr(_t);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void selectBooleanExpr(AST _t, boolean parens) throws RecognitionException {
        AST selectBooleanExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 6: 
                case 39: 
                case 41: {
                    this.booleanOp(_t, parens);
                    _t = this._retTree;
                    break;
                }
                case 10: 
                case 19: 
                case 27: 
                case 35: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 108: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: {
                    this.comparisonExpr(_t, parens);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void parameter(AST _t) throws RecognitionException {
        AST parameter_AST_in = _t == ASTNULL ? null : _t;
        AST n = null;
        AST p = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 156: {
                    n = _t;
                    this.match(_t, 156);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(n);
                    }
                    break;
                }
                case 132: {
                    p = _t;
                    this.match(_t, 132);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(p);
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void countExpr(AST _t) throws RecognitionException {
        AST countExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 93: {
                    AST tmp51_AST_in = _t;
                    this.match(_t, 93);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out("*");
                    }
                    break;
                }
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    this.simpleExpr(_t);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void simpleExpr(AST _t) throws RecognitionException {
        AST simpleExpr_AST_in = _t == ASTNULL ? null : _t;
        AST c = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 20: 
                case 50: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 111: 
                case 130: 
                case 133: {
                    c = _t == ASTNULL ? null : _t;
                    this.constant(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(c);
                    }
                    break;
                }
                case 40: {
                    AST tmp52_AST_in = _t;
                    this.match(_t, 40);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out("null");
                    }
                    break;
                }
                case 15: 
                case 22: 
                case 71: 
                case 72: 
                case 73: 
                case 83: 
                case 148: 
                case 158: {
                    this.addrExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 150: {
                    this.sqlToken(_t);
                    _t = this._retTree;
                    break;
                }
                case 74: {
                    this.aggregate(_t);
                    _t = this._retTree;
                    break;
                }
                case 78: 
                case 86: 
                case 95: {
                    this.methodCall(_t);
                    _t = this._retTree;
                    break;
                }
                case 12: {
                    this.count(_t);
                    _t = this._retTree;
                    break;
                }
                case 132: 
                case 156: {
                    this.parameter(_t);
                    _t = this._retTree;
                    break;
                }
                case 57: 
                case 77: 
                case 96: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: {
                    this.arithmeticExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 6: 
                case 10: 
                case 19: 
                case 27: 
                case 35: 
                case 39: 
                case 41: 
                case 84: 
                case 85: 
                case 87: 
                case 88: 
                case 89: 
                case 108: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: {
                    this.selectBooleanExpr(_t, false);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void tableJoin(AST _t, AST parent) throws RecognitionException {
        AST tableJoin_AST_in = _t == ASTNULL ? null : _t;
        AST d = null;
        AST e = null;
        AST f = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 143: {
                    AST __t657 = _t;
                    d = _t == ASTNULL ? null : _t;
                    this.match(_t, 143);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(" ");
                        this.out(d);
                    }
                    while (true) {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() != 141 && _t.getType() != 143 && _t.getType() != 144) break;
                        this.tableJoin(_t, d);
                        _t = this._retTree;
                    }
                    _t = __t657;
                    _t = _t.getNextSibling();
                    break;
                }
                case 141: {
                    AST __t660 = _t;
                    e = _t == ASTNULL ? null : _t;
                    this.match(_t, 141);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.nestedFromFragment(e, parent);
                    }
                    while (true) {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() != 141 && _t.getType() != 143 && _t.getType() != 144) break;
                        this.tableJoin(_t, e);
                        _t = this._retTree;
                    }
                    _t = __t660;
                    _t = _t.getNextSibling();
                    break;
                }
                case 144: {
                    AST __t663 = _t;
                    f = _t == ASTNULL ? null : _t;
                    this.match(_t, 144);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(" ");
                        this.out(f);
                    }
                    while (true) {
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() != 141 && _t.getType() != 143 && _t.getType() != 144) break;
                        this.tableJoin(_t, f);
                        _t = this._retTree;
                    }
                    _t = __t663;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void booleanOp(AST _t, boolean parens) throws RecognitionException {
        AST booleanOp_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 6: {
                    AST __t667 = _t;
                    AST tmp53_AST_in = _t;
                    this.match(_t, 6);
                    _t = _t.getFirstChild();
                    this.booleanExpr(_t, true);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" and ");
                    }
                    this.booleanExpr(_t, true);
                    _t = this._retTree;
                    _t = __t667;
                    _t = _t.getNextSibling();
                    break;
                }
                case 41: {
                    AST __t668 = _t;
                    AST tmp54_AST_in = _t;
                    this.match(_t, 41);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0 && parens) {
                        this.out("(");
                    }
                    this.booleanExpr(_t, false);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" or ");
                    }
                    this.booleanExpr(_t, false);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0 && parens) {
                        this.out(")");
                    }
                    _t = __t668;
                    _t = _t.getNextSibling();
                    break;
                }
                case 39: {
                    AST __t669 = _t;
                    AST tmp55_AST_in = _t;
                    this.match(_t, 39);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(" not (");
                    }
                    this.booleanExpr(_t, false);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(")");
                    }
                    _t = __t669;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void binaryComparisonExpression(AST _t) throws RecognitionException {
        AST binaryComparisonExpression_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 108: {
                    AST __t674 = _t;
                    AST tmp56_AST_in = _t;
                    this.match(_t, 108);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("=");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t674;
                    _t = _t.getNextSibling();
                    break;
                }
                case 115: {
                    AST __t675 = _t;
                    AST tmp57_AST_in = _t;
                    this.match(_t, 115);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("<>");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t675;
                    _t = _t.getNextSibling();
                    break;
                }
                case 118: {
                    AST __t676 = _t;
                    AST tmp58_AST_in = _t;
                    this.match(_t, 118);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(">");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t676;
                    _t = _t.getNextSibling();
                    break;
                }
                case 120: {
                    AST __t677 = _t;
                    AST tmp59_AST_in = _t;
                    this.match(_t, 120);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(">=");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t677;
                    _t = _t.getNextSibling();
                    break;
                }
                case 117: {
                    AST __t678 = _t;
                    AST tmp60_AST_in = _t;
                    this.match(_t, 117);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("<");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t678;
                    _t = _t.getNextSibling();
                    break;
                }
                case 119: {
                    AST __t679 = _t;
                    AST tmp61_AST_in = _t;
                    this.match(_t, 119);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("<=");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t679;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void exoticComparisonExpression(AST _t) throws RecognitionException {
        AST exoticComparisonExpression_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 35: {
                    AST __t681 = _t;
                    AST tmp62_AST_in = _t;
                    this.match(_t, 35);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" like ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    this.likeEscape(_t);
                    _t = this._retTree;
                    _t = __t681;
                    _t = _t.getNextSibling();
                    break;
                }
                case 89: {
                    AST __t682 = _t;
                    AST tmp63_AST_in = _t;
                    this.match(_t, 89);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" not like ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    this.likeEscape(_t);
                    _t = this._retTree;
                    _t = __t682;
                    _t = _t.getNextSibling();
                    break;
                }
                case 10: {
                    AST __t683 = _t;
                    AST tmp64_AST_in = _t;
                    this.match(_t, 10);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" between ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" and ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t683;
                    _t = _t.getNextSibling();
                    break;
                }
                case 87: {
                    AST __t684 = _t;
                    AST tmp65_AST_in = _t;
                    this.match(_t, 87);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" not between ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" and ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t684;
                    _t = _t.getNextSibling();
                    break;
                }
                case 27: {
                    AST __t685 = _t;
                    AST tmp66_AST_in = _t;
                    this.match(_t, 27);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" in");
                    }
                    this.inList(_t);
                    _t = this._retTree;
                    _t = __t685;
                    _t = _t.getNextSibling();
                    break;
                }
                case 88: {
                    AST __t686 = _t;
                    AST tmp67_AST_in = _t;
                    this.match(_t, 88);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" not in ");
                    }
                    this.inList(_t);
                    _t = this._retTree;
                    _t = __t686;
                    _t = _t.getNextSibling();
                    break;
                }
                case 19: {
                    AST __t687 = _t;
                    AST tmp68_AST_in = _t;
                    this.match(_t, 19);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.optionalSpace();
                        this.out("exists ");
                    }
                    this.quantified(_t);
                    _t = this._retTree;
                    _t = __t687;
                    _t = _t.getNextSibling();
                    break;
                }
                case 85: {
                    AST __t688 = _t;
                    AST tmp69_AST_in = _t;
                    this.match(_t, 85);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t688;
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(" is null");
                    }
                    break;
                }
                case 84: {
                    AST __t689 = _t;
                    AST tmp70_AST_in = _t;
                    this.match(_t, 84);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t689;
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(" is not null");
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void likeEscape(AST _t) throws RecognitionException {
        AST likeEscape_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 18: {
                    AST __t692 = _t;
                    AST tmp71_AST_in = _t;
                    this.match(_t, 18);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out(" escape ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t692;
                    _t = _t.getNextSibling();
                    break;
                }
                case 3: {
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void inList(AST _t) throws RecognitionException {
        AST inList_AST_in = _t == ASTNULL ? null : _t;
        try {
            AST __t694 = _t;
            AST tmp72_AST_in = _t;
            this.match(_t, 82);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out(" ");
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 46: {
                    this.parenSelect(_t);
                    _t = this._retTree;
                    break;
                }
                case 3: 
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 98: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    this.simpleExprList(_t);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            _t = __t694;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void quantified(AST _t) throws RecognitionException {
        AST quantified_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (this.inputState.guessing == 0) {
                this.out("(");
            }
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 150: {
                    this.sqlToken(_t);
                    _t = this._retTree;
                    break;
                }
                case 46: {
                    this.selectStatement(_t);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
            if (this.inputState.guessing == 0) {
                this.out(")");
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void parenSelect(AST _t) throws RecognitionException {
        AST parenSelect_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (this.inputState.guessing == 0) {
                this.out("(");
            }
            this.selectStatement(_t);
            _t = this._retTree;
            if (this.inputState.guessing == 0) {
                this.out(")");
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void simpleExprList(AST _t) throws RecognitionException {
        AST simpleExprList_AST_in = _t == ASTNULL ? null : _t;
        AST e = null;
        try {
            if (this.inputState.guessing == 0) {
                this.out("(");
            }
            while (true) {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (!_tokenSet_3.member(_t.getType())) break;
                e = _t == ASTNULL ? null : _t;
                this.simpleOrTupleExpr(_t);
                _t = this._retTree;
                if (this.inputState.guessing != 0) continue;
                this.separator(e, " , ");
            }
            if (this.inputState.guessing == 0) {
                this.out(")");
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void simpleOrTupleExpr(AST _t) throws RecognitionException {
        AST simpleOrTupleExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 22: 
                case 27: 
                case 35: 
                case 39: 
                case 40: 
                case 41: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 77: 
                case 78: 
                case 83: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 156: 
                case 158: {
                    this.simpleExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 98: {
                    this.tupleExpr(_t);
                    _t = this._retTree;
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void tupleExpr(AST _t) throws RecognitionException {
        AST tupleExpr_AST_in = _t == ASTNULL ? null : _t;
        AST e = null;
        try {
            AST __t705 = _t;
            AST tmp73_AST_in = _t;
            this.match(_t, 98);
            _t = _t.getFirstChild();
            if (this.inputState.guessing == 0) {
                this.out("(");
            }
            while (true) {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (!_tokenSet_4.member(_t.getType())) break;
                e = _t == ASTNULL ? null : _t;
                this.expr(_t);
                _t = this._retTree;
                if (this.inputState.guessing != 0) continue;
                this.separator(e, " , ");
            }
            if (this.inputState.guessing == 0) {
                this.out(")");
            }
            _t = __t705;
            _t = _t.getNextSibling();
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void addrExpr(AST _t) throws RecognitionException {
        AST addrExpr_AST_in = _t == ASTNULL ? null : _t;
        AST r = null;
        AST fk = null;
        AST i = null;
        AST j = null;
        AST v = null;
        AST mcr = null;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 15: {
                    AST __t756 = _t;
                    r = _t == ASTNULL ? null : _t;
                    this.match(_t, 15);
                    AST tmp74_AST_in = _t = _t.getFirstChild();
                    if (_t == null) {
                        throw new MismatchedTokenException();
                    }
                    AST tmp75_AST_in = _t = _t.getNextSibling();
                    if (_t == null) {
                        throw new MismatchedTokenException();
                    }
                    _t = _t.getNextSibling();
                    _t = __t756;
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(r);
                    }
                    break;
                }
                case 22: {
                    AST __t757 = _t;
                    fk = _t == ASTNULL ? null : _t;
                    this.match(_t, 22);
                    AST tmp76_AST_in = _t = _t.getFirstChild();
                    if (_t == null) {
                        throw new MismatchedTokenException();
                    }
                    _t = _t.getNextSibling();
                    _t = __t757;
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(fk);
                    }
                    break;
                }
                case 148: {
                    i = _t;
                    this.match(_t, 148);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(i);
                    }
                    break;
                }
                case 83: {
                    j = _t;
                    this.match(_t, 83);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(j);
                    }
                    break;
                }
                case 158: {
                    v = _t;
                    this.match(_t, 158);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out(v);
                    }
                    break;
                }
                case 71: 
                case 72: 
                case 73: {
                    mcr = _t == ASTNULL ? null : _t;
                    this.mapComponentReference(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(mcr);
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void additiveExpr(AST _t) throws RecognitionException {
        AST additiveExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 122: {
                    AST __t716 = _t;
                    AST tmp77_AST_in = _t;
                    this.match(_t, 122);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("+");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    _t = __t716;
                    _t = _t.getNextSibling();
                    break;
                }
                case 123: {
                    AST __t717 = _t;
                    AST tmp78_AST_in = _t;
                    this.match(_t, 123);
                    _t = _t.getFirstChild();
                    this.expr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("-");
                    }
                    this.nestedExprAfterMinusDiv(_t);
                    _t = this._retTree;
                    _t = __t717;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void multiplicativeExpr(AST _t) throws RecognitionException {
        AST multiplicativeExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 124: {
                    AST __t719 = _t;
                    AST tmp79_AST_in = _t;
                    this.match(_t, 124);
                    _t = _t.getFirstChild();
                    this.nestedExpr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("*");
                    }
                    this.nestedExpr(_t);
                    _t = this._retTree;
                    _t = __t719;
                    _t = _t.getNextSibling();
                    break;
                }
                case 125: {
                    AST __t720 = _t;
                    AST tmp80_AST_in = _t;
                    this.match(_t, 125);
                    _t = _t.getFirstChild();
                    this.nestedExpr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out("/");
                    }
                    this.nestedExprAfterMinusDiv(_t);
                    _t = this._retTree;
                    _t = __t720;
                    _t = _t.getNextSibling();
                    break;
                }
                case 126: {
                    AST __t721 = _t;
                    AST tmp81_AST_in = _t;
                    this.match(_t, 126);
                    _t = _t.getFirstChild();
                    this.nestedExpr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(" % ");
                    }
                    this.nestedExprAfterMinusDiv(_t);
                    _t = this._retTree;
                    _t = __t721;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void nestedExprAfterMinusDiv(AST _t) throws RecognitionException {
        block12: {
            AST nestedExprAfterMinusDiv_AST_in = _t == ASTNULL ? null : _t;
            try {
                boolean synPredMatched727 = false;
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (_tokenSet_5.member(_t.getType())) {
                    AST __t727 = _t;
                    synPredMatched727 = true;
                    ++this.inputState.guessing;
                    try {
                        this.arithmeticExpr(_t);
                        _t = this._retTree;
                    }
                    catch (RecognitionException pe) {
                        synPredMatched727 = false;
                    }
                    _t = __t727;
                    --this.inputState.guessing;
                }
                if (synPredMatched727) {
                    if (this.inputState.guessing == 0) {
                        this.out("(");
                    }
                    this.arithmeticExpr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(")");
                    }
                    break block12;
                }
                if (_tokenSet_4.member(_t.getType())) {
                    this.expr(_t);
                    _t = this._retTree;
                    break block12;
                }
                throw new NoViableAltException(_t);
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    if (_t != null) {
                        _t = _t.getNextSibling();
                    }
                }
                throw ex;
            }
        }
        this._retTree = _t;
    }

    public final void caseExpr(AST _t) throws RecognitionException {
        AST caseExpr_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 57: {
                    AST __t729 = _t;
                    AST tmp82_AST_in = _t;
                    this.match(_t, 57);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out("case");
                    }
                    int _cnt732 = 0;
                    while (true) {
                        AST __t731;
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() == 61) {
                            __t731 = _t;
                            AST tmp83_AST_in = _t;
                            this.match(_t, 61);
                            _t = _t.getFirstChild();
                            if (this.inputState.guessing == 0) {
                                this.out(" when ");
                            }
                            this.booleanExpr(_t, false);
                            _t = this._retTree;
                            if (this.inputState.guessing == 0) {
                                this.out(" then ");
                            }
                        } else {
                            if (_cnt732 >= 1) break;
                            throw new NoViableAltException(_t);
                        }
                        this.expr(_t);
                        _t = this._retTree;
                        _t = __t731;
                        _t = _t.getNextSibling();
                        ++_cnt732;
                    }
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 59: {
                            AST __t734 = _t;
                            AST tmp84_AST_in = _t;
                            this.match(_t, 59);
                            _t = _t.getFirstChild();
                            if (this.inputState.guessing == 0) {
                                this.out(" else ");
                            }
                            this.expr(_t);
                            _t = this._retTree;
                            _t = __t734;
                            _t = _t.getNextSibling();
                            break;
                        }
                        case 3: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(_t);
                        }
                    }
                    if (this.inputState.guessing == 0) {
                        this.out(" end");
                    }
                    _t = __t729;
                    _t = _t.getNextSibling();
                    break;
                }
                case 77: {
                    AST __t735 = _t;
                    AST tmp85_AST_in = _t;
                    this.match(_t, 77);
                    _t = _t.getFirstChild();
                    if (this.inputState.guessing == 0) {
                        this.out("case ");
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    int _cnt738 = 0;
                    while (true) {
                        AST __t737;
                        if (_t == null) {
                            _t = ASTNULL;
                        }
                        if (_t.getType() == 61) {
                            __t737 = _t;
                            AST tmp86_AST_in = _t;
                            this.match(_t, 61);
                            _t = _t.getFirstChild();
                            if (this.inputState.guessing == 0) {
                                this.out(" when ");
                            }
                            this.expr(_t);
                            _t = this._retTree;
                            if (this.inputState.guessing == 0) {
                                this.out(" then ");
                            }
                        } else {
                            if (_cnt738 >= 1) break;
                            throw new NoViableAltException(_t);
                        }
                        this.expr(_t);
                        _t = this._retTree;
                        _t = __t737;
                        _t = _t.getNextSibling();
                        ++_cnt738;
                    }
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    switch (_t.getType()) {
                        case 59: {
                            AST __t740 = _t;
                            AST tmp87_AST_in = _t;
                            this.match(_t, 59);
                            _t = _t.getFirstChild();
                            if (this.inputState.guessing == 0) {
                                this.out(" else ");
                            }
                            this.expr(_t);
                            _t = this._retTree;
                            _t = __t740;
                            _t = _t.getNextSibling();
                            break;
                        }
                        case 3: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(_t);
                        }
                    }
                    if (this.inputState.guessing == 0) {
                        this.out(" end");
                    }
                    _t = __t735;
                    _t = _t.getNextSibling();
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void nestedExpr(AST _t) throws RecognitionException {
        block12: {
            AST nestedExpr_AST_in = _t == ASTNULL ? null : _t;
            try {
                boolean synPredMatched724 = false;
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (_t.getType() == 122 || _t.getType() == 123) {
                    AST __t724 = _t;
                    synPredMatched724 = true;
                    ++this.inputState.guessing;
                    try {
                        this.additiveExpr(_t);
                        _t = this._retTree;
                    }
                    catch (RecognitionException pe) {
                        synPredMatched724 = false;
                    }
                    _t = __t724;
                    --this.inputState.guessing;
                }
                if (synPredMatched724) {
                    if (this.inputState.guessing == 0) {
                        this.out("(");
                    }
                    this.additiveExpr(_t);
                    _t = this._retTree;
                    if (this.inputState.guessing == 0) {
                        this.out(")");
                    }
                    break block12;
                }
                if (_tokenSet_4.member(_t.getType())) {
                    this.expr(_t);
                    _t = this._retTree;
                    break block12;
                }
                throw new NoViableAltException(_t);
            }
            catch (RecognitionException ex) {
                if (this.inputState.guessing == 0) {
                    this.reportError(ex);
                    if (_t != null) {
                        _t = _t.getNextSibling();
                    }
                }
                throw ex;
            }
        }
        this._retTree = _t;
    }

    public final void arguments(AST _t) throws RecognitionException {
        AST arguments_AST_in = _t == ASTNULL ? null : _t;
        try {
            this.expr(_t);
            _t = this._retTree;
            while (true) {
                if (_t == null) {
                    _t = ASTNULL;
                }
                if (_tokenSet_4.member(_t.getType())) {
                    if (this.inputState.guessing == 0) {
                        this.betweenFunctionArguments();
                    }
                    this.expr(_t);
                    _t = this._retTree;
                    continue;
                }
                break;
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void castExpression(AST _t) throws RecognitionException {
        AST castExpression_AST_in = _t == ASTNULL ? null : _t;
        try {
            if (_t == null) {
                _t = ASTNULL;
            }
            switch (_t.getType()) {
                case 6: 
                case 10: 
                case 12: 
                case 15: 
                case 19: 
                case 20: 
                case 27: 
                case 35: 
                case 39: 
                case 41: 
                case 46: 
                case 50: 
                case 57: 
                case 71: 
                case 72: 
                case 73: 
                case 74: 
                case 76: 
                case 77: 
                case 78: 
                case 84: 
                case 85: 
                case 86: 
                case 87: 
                case 88: 
                case 89: 
                case 95: 
                case 96: 
                case 100: 
                case 101: 
                case 102: 
                case 103: 
                case 104: 
                case 105: 
                case 106: 
                case 108: 
                case 111: 
                case 115: 
                case 117: 
                case 118: 
                case 119: 
                case 120: 
                case 122: 
                case 123: 
                case 124: 
                case 125: 
                case 126: 
                case 130: 
                case 132: 
                case 133: 
                case 148: 
                case 150: 
                case 152: 
                case 156: {
                    this.selectExpr(_t);
                    _t = this._retTree;
                    break;
                }
                case 40: {
                    AST tmp88_AST_in = _t;
                    this.match(_t, 40);
                    _t = _t.getNextSibling();
                    if (this.inputState.guessing == 0) {
                        this.out("null");
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(_t);
                }
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    public final void castTargetType(AST _t) throws RecognitionException {
        AST castTargetType_AST_in = _t == ASTNULL ? null : _t;
        AST i = null;
        try {
            i = _t;
            this.match(_t, 111);
            _t = _t.getNextSibling();
            if (this.inputState.guessing == 0) {
                this.out(i);
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing == 0) {
                this.reportError(ex);
                if (_t != null) {
                    _t = _t.getNextSibling();
                }
            }
            throw ex;
        }
        this._retTree = _t;
    }

    private static final long[] mk_tokenSet_0() {
        long[] data = new long[]{145314240001512512L, 9072668512894351232L, 290455604L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_1() {
        long[] data = new long[]{0x808080400L, 137377380882710528L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_2() {
        long[] data = new long[]{2783273550912L, 137377380882710528L, 0x400000L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_3() {
        long[] data = new long[]{145244970773156928L, 9072668530074740608L, 1347420212L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_4() {
        long[] data = new long[]{145596814494045296L, 9072668530074740608L, 1347420212L, 0L, 0L, 0L};
        return data;
    }

    private static final long[] mk_tokenSet_5() {
        long[] data = new long[]{0x200000000000000L, 8935141664998039552L, 0L, 0L};
        return data;
    }
}

