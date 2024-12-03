/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.NoViableAltException
 *  antlr.RecognitionException
 *  antlr.TreeParser
 *  antlr.collections.AST
 */
package org.hibernate.sql.ordering.antlr;

import antlr.NoViableAltException;
import antlr.RecognitionException;
import antlr.TreeParser;
import antlr.collections.AST;
import org.hibernate.sql.ordering.antlr.GeneratedOrderByFragmentRendererTokenTypes;

public class GeneratedOrderByFragmentRenderer
extends TreeParser
implements GeneratedOrderByFragmentRendererTokenTypes {
    private StringBuilder buffer = new StringBuilder();
    public static final String[] _tokenNames = new String[]{"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "ORDER_BY", "SORT_SPEC", "ORDER_SPEC", "NULL_ORDER", "SORT_KEY", "EXPR_LIST", "DOT", "IDENT_LIST", "COLUMN_REF", "\"collate\"", "\"asc\"", "\"desc\"", "\"nulls\"", "FIRST", "LAST", "COMMA", "HARD_QUOTE", "IDENT", "OPEN_PAREN", "CLOSE_PAREN", "NUM_DOUBLE", "NUM_FLOAT", "NUM_INT", "NUM_LONG", "QUOTED_STRING", "\"ascending\"", "\"descending\"", "ID_START_LETTER", "ID_LETTER", "ESCqs", "HEX_DIGIT", "EXPONENT", "FLOAT_SUFFIX", "WS"};

    protected void out(String text) {
        this.buffer.append(text);
    }

    protected void out(AST ast) {
        this.buffer.append(ast.getText());
    }

    String getRenderedFragment() {
        return this.buffer.toString();
    }

    protected String renderOrderByElement(String expression, String collation, String order, String nulls) {
        throw new UnsupportedOperationException("Concrete ORDER BY renderer should override this method.");
    }

    public GeneratedOrderByFragmentRenderer() {
        this.tokenNames = _tokenNames;
    }

    public final void orderByFragment(AST _t) throws RecognitionException {
        block4: {
            AST orderByFragment_AST_in = _t == ASTNULL ? null : _t;
            try {
                AST __t784 = _t;
                AST tmp1_AST_in = _t;
                this.match(_t, 4);
                _t = _t.getFirstChild();
                this.sortSpecification(_t);
                _t = this._retTree;
                while (true) {
                    if (_t == null) {
                        _t = ASTNULL;
                    }
                    if (_t.getType() != 5) break;
                    this.out(", ");
                    this.sortSpecification(_t);
                    _t = this._retTree;
                }
                _t = __t784;
                _t = _t.getNextSibling();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t == null) break block4;
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
    }

    public final void sortSpecification(AST _t) throws RecognitionException {
        block17: {
            AST sortSpecification_AST_in = _t == ASTNULL ? null : _t;
            String sortKeySpec = null;
            String collSpec = null;
            String ordSpec = null;
            String nullOrd = null;
            try {
                AST __t788 = _t;
                AST tmp2_AST_in = _t;
                this.match(_t, 5);
                _t = _t.getFirstChild();
                sortKeySpec = this.sortKeySpecification(_t);
                _t = this._retTree;
                if (_t == null) {
                    _t = ASTNULL;
                }
                switch (_t.getType()) {
                    case 13: {
                        collSpec = this.collationSpecification(_t);
                        _t = this._retTree;
                        break;
                    }
                    case 3: 
                    case 6: 
                    case 7: {
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
                    case 6: {
                        ordSpec = this.orderingSpecification(_t);
                        _t = this._retTree;
                        break;
                    }
                    case 3: 
                    case 7: {
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
                    case 7: {
                        nullOrd = this.nullOrdering(_t);
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
                this.out(this.renderOrderByElement(sortKeySpec, collSpec, ordSpec, nullOrd));
                _t = __t788;
                _t = _t.getNextSibling();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t == null) break block17;
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
    }

    public final String sortKeySpecification(AST _t) throws RecognitionException {
        String sortKeyExp;
        block2: {
            sortKeyExp = null;
            AST sortKeySpecification_AST_in = _t == ASTNULL ? null : _t;
            AST s = null;
            try {
                AST __t793 = _t;
                AST tmp3_AST_in = _t;
                this.match(_t, 8);
                _t = _t.getFirstChild();
                s = _t == ASTNULL ? null : _t;
                this.sortKey(_t);
                _t = this._retTree;
                _t = __t793;
                _t = _t.getNextSibling();
                sortKeyExp = s.getText();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t == null) break block2;
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return sortKeyExp;
    }

    public final String collationSpecification(AST _t) throws RecognitionException {
        String collSpecExp;
        block2: {
            collSpecExp = null;
            AST collationSpecification_AST_in = _t == ASTNULL ? null : _t;
            AST c = null;
            try {
                c = _t;
                this.match(_t, 13);
                _t = _t.getNextSibling();
                collSpecExp = "collate " + c.getText();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t == null) break block2;
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return collSpecExp;
    }

    public final String orderingSpecification(AST _t) throws RecognitionException {
        String ordSpecExp;
        block2: {
            ordSpecExp = null;
            AST orderingSpecification_AST_in = _t == ASTNULL ? null : _t;
            AST o = null;
            try {
                o = _t;
                this.match(_t, 6);
                _t = _t.getNextSibling();
                ordSpecExp = o.getText();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t == null) break block2;
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return ordSpecExp;
    }

    public final String nullOrdering(AST _t) throws RecognitionException {
        String nullOrdExp;
        block2: {
            nullOrdExp = null;
            AST nullOrdering_AST_in = _t == ASTNULL ? null : _t;
            AST n = null;
            try {
                n = _t;
                this.match(_t, 7);
                _t = _t.getNextSibling();
                nullOrdExp = n.getText();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t == null) break block2;
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
        return nullOrdExp;
    }

    public final void sortKey(AST _t) throws RecognitionException {
        block2: {
            AST sortKey_AST_in = _t == ASTNULL ? null : _t;
            try {
                AST tmp4_AST_in = _t;
                this.match(_t, 21);
                _t = _t.getNextSibling();
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                if (_t == null) break block2;
                _t = _t.getNextSibling();
            }
        }
        this._retTree = _t;
    }
}

