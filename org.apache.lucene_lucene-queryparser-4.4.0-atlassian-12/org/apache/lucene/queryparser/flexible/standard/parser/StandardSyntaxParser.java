/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.lucene.queryparser.flexible.core.QueryNodeParseException;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.nodes.QuotedFieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.SyntaxParser;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.parser.CharStream;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.queryparser.flexible.standard.parser.FastCharStream;
import org.apache.lucene.queryparser.flexible.standard.parser.ParseException;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParserConstants;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParserTokenManager;
import org.apache.lucene.queryparser.flexible.standard.parser.Token;

public class StandardSyntaxParser
implements SyntaxParser,
StandardSyntaxParserConstants {
    private static final int CONJ_NONE = 0;
    private static final int CONJ_AND = 2;
    private static final int CONJ_OR = 2;
    public StandardSyntaxParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[28];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns = new JJCalls[2];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public StandardSyntaxParser() {
        this(new FastCharStream(new StringReader("")));
    }

    @Override
    public QueryNode parse(CharSequence query, CharSequence field) throws QueryNodeParseException {
        this.ReInit(new FastCharStream(new StringReader(query.toString())));
        try {
            QueryNode querynode = this.TopLevelQuery(field);
            return querynode;
        }
        catch (ParseException tme) {
            tme.setQuery(query);
            throw tme;
        }
        catch (Error tme) {
            MessageImpl message = new MessageImpl(QueryParserMessages.INVALID_SYNTAX_CANNOT_PARSE, query, tme.getMessage());
            QueryNodeParseException e = new QueryNodeParseException(tme);
            e.setQuery(query);
            e.setNonLocalizedMessage(message);
            throw e;
        }
    }

    public final int Conjunction() throws ParseException {
        int ret = 0;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: 
            case 9: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 8: {
                        this.jj_consume_token(8);
                        ret = 2;
                        break block0;
                    }
                    case 9: {
                        this.jj_consume_token(9);
                        ret = 2;
                        break block0;
                    }
                }
                this.jj_la1[0] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[1] = this.jj_gen;
            }
        }
        return ret;
    }

    public final ModifierQueryNode.Modifier Modifiers() throws ParseException {
        ModifierQueryNode.Modifier ret = ModifierQueryNode.Modifier.MOD_NONE;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 10: 
            case 11: 
            case 12: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 11: {
                        this.jj_consume_token(11);
                        ret = ModifierQueryNode.Modifier.MOD_REQ;
                        break block0;
                    }
                    case 12: {
                        this.jj_consume_token(12);
                        ret = ModifierQueryNode.Modifier.MOD_NOT;
                        break block0;
                    }
                    case 10: {
                        this.jj_consume_token(10);
                        ret = ModifierQueryNode.Modifier.MOD_NOT;
                        break block0;
                    }
                }
                this.jj_la1[2] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
            default: {
                this.jj_la1[3] = this.jj_gen;
            }
        }
        return ret;
    }

    public final QueryNode TopLevelQuery(CharSequence field) throws ParseException {
        QueryNode q = this.Query(field);
        this.jj_consume_token(0);
        return q;
    }

    public final QueryNode Query(CharSequence field) throws ParseException {
        Vector<QueryNode> clauses = null;
        QueryNode first = null;
        first = this.DisjQuery(field);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 22: 
                case 23: 
                case 25: 
                case 26: 
                case 27: 
                case 28: {
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    break block3;
                }
            }
            QueryNode c = this.DisjQuery(field);
            if (clauses == null) {
                clauses = new Vector<QueryNode>();
                clauses.addElement(first);
            }
            clauses.addElement(c);
        }
        if (clauses != null) {
            return new BooleanQueryNode(clauses);
        }
        return first;
    }

    public final QueryNode DisjQuery(CharSequence field) throws ParseException {
        Vector<QueryNode> clauses = null;
        QueryNode first = this.ConjQuery(field);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    break;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(9);
            QueryNode c = this.ConjQuery(field);
            if (clauses == null) {
                clauses = new Vector<QueryNode>();
                clauses.addElement(first);
            }
            clauses.addElement(c);
        }
        if (clauses != null) {
            return new OrQueryNode(clauses);
        }
        return first;
    }

    public final QueryNode ConjQuery(CharSequence field) throws ParseException {
        Vector<QueryNode> clauses = null;
        QueryNode first = this.ModClause(field);
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    break;
                }
                default: {
                    this.jj_la1[6] = this.jj_gen;
                    break block3;
                }
            }
            this.jj_consume_token(8);
            QueryNode c = this.ModClause(field);
            if (clauses == null) {
                clauses = new Vector<QueryNode>();
                clauses.addElement(first);
            }
            clauses.addElement(c);
        }
        if (clauses != null) {
            return new AndQueryNode(clauses);
        }
        return first;
    }

    public final QueryNode ModClause(CharSequence field) throws ParseException {
        ModifierQueryNode.Modifier mods = this.Modifiers();
        QueryNode q = this.Clause(field);
        if (mods != ModifierQueryNode.Modifier.MOD_NONE) {
            q = new ModifierQueryNode(q, mods);
        }
        return q;
    }

    public final QueryNode Clause(CharSequence field) throws ParseException {
        QueryNode q;
        boolean group;
        Token boost;
        block47: {
            Token fieldToken;
            block46: {
                fieldToken = null;
                boost = null;
                Token operator = null;
                Token term = null;
                group = false;
                if (!this.jj_2_2(3)) break block46;
                fieldToken = this.jj_consume_token(23);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 15: 
                    case 16: {
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 15: {
                                this.jj_consume_token(15);
                                break;
                            }
                            case 16: {
                                this.jj_consume_token(16);
                                break;
                            }
                            default: {
                                this.jj_la1[7] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        field = EscapeQuerySyntaxImpl.discardEscapeChar(fieldToken.image);
                        q = this.Term(field);
                        break block47;
                    }
                    case 17: 
                    case 18: 
                    case 19: 
                    case 20: {
                        FieldQueryNode qUpper;
                        FieldQueryNode qLower;
                        boolean upperInclusive;
                        boolean lowerInclusive;
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 17: {
                                operator = this.jj_consume_token(17);
                                break;
                            }
                            case 18: {
                                operator = this.jj_consume_token(18);
                                break;
                            }
                            case 19: {
                                operator = this.jj_consume_token(19);
                                break;
                            }
                            case 20: {
                                operator = this.jj_consume_token(20);
                                break;
                            }
                            default: {
                                this.jj_la1[8] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        field = EscapeQuerySyntaxImpl.discardEscapeChar(fieldToken.image);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 23: {
                                term = this.jj_consume_token(23);
                                break;
                            }
                            case 22: {
                                term = this.jj_consume_token(22);
                                break;
                            }
                            case 28: {
                                term = this.jj_consume_token(28);
                                break;
                            }
                            default: {
                                this.jj_la1[9] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        if (term.kind == 22) {
                            term.image = term.image.substring(1, term.image.length() - 1);
                        }
                        switch (operator.kind) {
                            case 17: {
                                lowerInclusive = true;
                                upperInclusive = false;
                                qLower = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                break;
                            }
                            case 18: {
                                lowerInclusive = true;
                                upperInclusive = true;
                                qLower = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                break;
                            }
                            case 19: {
                                lowerInclusive = false;
                                upperInclusive = true;
                                qLower = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                break;
                            }
                            case 20: {
                                lowerInclusive = true;
                                upperInclusive = true;
                                qLower = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                                qUpper = new FieldQueryNode(field, "*", term.beginColumn, term.endColumn);
                                break;
                            }
                            default: {
                                throw new Error("Unhandled case: operator=" + operator.toString());
                            }
                        }
                        q = new TermRangeQueryNode(qLower, qUpper, lowerInclusive, upperInclusive);
                        break block47;
                    }
                    default: {
                        this.jj_la1[10] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
            }
            block26 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 13: 
                case 22: 
                case 23: 
                case 25: 
                case 26: 
                case 27: 
                case 28: {
                    if (this.jj_2_1(2)) {
                        fieldToken = this.jj_consume_token(23);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 15: {
                                this.jj_consume_token(15);
                                break;
                            }
                            case 16: {
                                this.jj_consume_token(16);
                                break;
                            }
                            default: {
                                this.jj_la1[11] = this.jj_gen;
                                this.jj_consume_token(-1);
                                throw new ParseException();
                            }
                        }
                        field = EscapeQuerySyntaxImpl.discardEscapeChar(fieldToken.image);
                    }
                    switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                        case 22: 
                        case 23: 
                        case 25: 
                        case 26: 
                        case 27: 
                        case 28: {
                            q = this.Term(field);
                            break block26;
                        }
                        case 13: {
                            this.jj_consume_token(13);
                            q = this.Query(field);
                            this.jj_consume_token(14);
                            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                                case 21: {
                                    this.jj_consume_token(21);
                                    boost = this.jj_consume_token(28);
                                    break;
                                }
                                default: {
                                    this.jj_la1[12] = this.jj_gen;
                                }
                            }
                            group = true;
                            break block26;
                        }
                    }
                    this.jj_la1[13] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
                default: {
                    this.jj_la1[14] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image).floatValue();
                if (q != null) {
                    q = new BoostQueryNode(q, f);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (group) {
            q = new GroupQueryNode(q);
        }
        return q;
    }

    public final QueryNode Term(CharSequence field) throws ParseException {
        Token boost = null;
        Token fuzzySlop = null;
        boolean fuzzy = false;
        boolean regexp = false;
        boolean startInc = false;
        boolean endInc = false;
        QueryNodeImpl q = null;
        float defaultMinSimilarity = 2.0f;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 23: 
            case 25: 
            case 28: {
                Token term;
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 23: {
                        term = this.jj_consume_token(23);
                        q = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), term.beginColumn, term.endColumn);
                        break;
                    }
                    case 25: {
                        term = this.jj_consume_token(25);
                        regexp = true;
                        break;
                    }
                    case 28: {
                        term = this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 24: {
                        fuzzySlop = this.jj_consume_token(24);
                        fuzzy = true;
                        break;
                    }
                    default: {
                        this.jj_la1[16] = this.jj_gen;
                    }
                }
                block16 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        this.jj_consume_token(21);
                        boost = this.jj_consume_token(28);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 24: {
                                fuzzySlop = this.jj_consume_token(24);
                                fuzzy = true;
                                break block16;
                            }
                        }
                        this.jj_la1[17] = this.jj_gen;
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                    }
                }
                if (fuzzy) {
                    float fms = defaultMinSimilarity;
                    try {
                        fms = Float.valueOf(fuzzySlop.image.substring(1)).floatValue();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (fms < 0.0f) {
                        throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_FUZZY_LIMITS));
                    }
                    if (fms >= 1.0f && fms != (float)((int)fms)) {
                        throw new ParseException(new MessageImpl(QueryParserMessages.INVALID_SYNTAX_FUZZY_EDITS));
                    }
                    q = new FuzzyQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image), fms, term.beginColumn, term.endColumn);
                    break;
                }
                if (!regexp) break;
                String re = term.image.substring(1, term.image.length() - 1);
                q = new RegexpQueryNode(field, re, 0, re.length());
                break;
            }
            case 26: 
            case 27: {
                Token goop2;
                Token goop1;
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 26: {
                        this.jj_consume_token(26);
                        startInc = true;
                        break;
                    }
                    case 27: {
                        this.jj_consume_token(27);
                        break;
                    }
                    default: {
                        this.jj_la1[19] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 33: {
                        goop1 = this.jj_consume_token(33);
                        break;
                    }
                    case 32: {
                        goop1 = this.jj_consume_token(32);
                        break;
                    }
                    default: {
                        this.jj_la1[20] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 29: {
                        this.jj_consume_token(29);
                        break;
                    }
                    default: {
                        this.jj_la1[21] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 33: {
                        goop2 = this.jj_consume_token(33);
                        break;
                    }
                    case 32: {
                        goop2 = this.jj_consume_token(32);
                        break;
                    }
                    default: {
                        this.jj_la1[22] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 30: {
                        this.jj_consume_token(30);
                        endInc = true;
                        break;
                    }
                    case 31: {
                        this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[23] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        this.jj_consume_token(21);
                        boost = this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[24] = this.jj_gen;
                    }
                }
                if (goop1.kind == 32) {
                    goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
                }
                if (goop2.kind == 32) {
                    goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
                }
                FieldQueryNode qLower = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(goop1.image), goop1.beginColumn, goop1.endColumn);
                FieldQueryNode qUpper = new FieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(goop2.image), goop2.beginColumn, goop2.endColumn);
                q = new TermRangeQueryNode(qLower, qUpper, startInc, endInc);
                break;
            }
            case 22: {
                Token term = this.jj_consume_token(22);
                q = new QuotedFieldQueryNode(field, EscapeQuerySyntaxImpl.discardEscapeChar(term.image.substring(1, term.image.length() - 1)), term.beginColumn + 1, term.endColumn - 1);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 24: {
                        fuzzySlop = this.jj_consume_token(24);
                        break;
                    }
                    default: {
                        this.jj_la1[25] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        this.jj_consume_token(21);
                        boost = this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[26] = this.jj_gen;
                    }
                }
                int phraseSlop = 0;
                if (fuzzySlop == null) break;
                try {
                    phraseSlop = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
                    q = new SlopQueryNode(q, phraseSlop);
                }
                catch (Exception exception) {}
                break;
            }
            default: {
                this.jj_la1[27] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image).floatValue();
                if (q != null) {
                    q = new BoostQueryNode(q, f);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return q;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_1(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_1();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(0, xla);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean jj_2_2(int xla) {
        this.jj_la = xla;
        this.jj_lastpos = this.jj_scanpos = this.token;
        try {
            boolean bl = !this.jj_3_2();
            return bl;
        }
        catch (LookaheadSuccess ls) {
            boolean bl = true;
            return bl;
        }
        finally {
            this.jj_save(1, xla);
        }
    }

    private boolean jj_3_2() {
        if (this.jj_scan_token(23)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_4()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_5()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_12() {
        return this.jj_scan_token(26);
    }

    private boolean jj_3R_11() {
        return this.jj_scan_token(25);
    }

    private boolean jj_3_1() {
        if (this.jj_scan_token(23)) {
            return true;
        }
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(15)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(16)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_8() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_12()) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(27)) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_10() {
        return this.jj_scan_token(23);
    }

    private boolean jj_3R_7() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_10()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_11()) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(28)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3R_9() {
        return this.jj_scan_token(22);
    }

    private boolean jj_3R_5() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(17)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(18)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(19)) {
                    this.jj_scanpos = xsp;
                    if (this.jj_scan_token(20)) {
                        return true;
                    }
                }
            }
        }
        xsp = this.jj_scanpos;
        if (this.jj_scan_token(23)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(22)) {
                this.jj_scanpos = xsp;
                if (this.jj_scan_token(28)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean jj_3R_4() {
        Token xsp = this.jj_scanpos;
        if (this.jj_scan_token(15)) {
            this.jj_scanpos = xsp;
            if (this.jj_scan_token(16)) {
                return true;
            }
        }
        return this.jj_3R_6();
    }

    private boolean jj_3R_6() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_7()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_8()) {
                this.jj_scanpos = xsp;
                if (this.jj_3R_9()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{768, 768, 7168, 7168, 515914752, 512, 256, 98304, 0x1E0000, 0x10C00000, 2064384, 98304, 0x200000, 515907584, 515907584, 310378496, 0x1000000, 0x1000000, 0x200000, 0xC000000, 0, 0x20000000, 0, -1073741824, 0x200000, 0x1000000, 0x200000, 515899392};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 3, 0, 0, 0, 0, 0};
    }

    public StandardSyntaxParser(CharStream stream) {
        int i;
        this.token_source = new StandardSyntaxParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 28; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(CharStream stream) {
        int i;
        this.token_source.ReInit(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 28; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public StandardSyntaxParser(StandardSyntaxParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 28; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(StandardSyntaxParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 28; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken = this.token;
        this.token = oldToken.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        if (this.token.kind == kind) {
            ++this.jj_gen;
            if (++this.jj_gc > 100) {
                this.jj_gc = 0;
                for (int i = 0; i < this.jj_2_rtns.length; ++i) {
                    JJCalls c = this.jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < this.jj_gen) {
                            c.first = null;
                        }
                        c = c.next;
                    }
                }
            }
            return this.token;
        }
        this.token = oldToken;
        this.jj_kind = kind;
        throw this.generateParseException();
    }

    private boolean jj_scan_token(int kind) {
        if (this.jj_scanpos == this.jj_lastpos) {
            --this.jj_la;
            if (this.jj_scanpos.next == null) {
                this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
                this.jj_lastpos = this.jj_scanpos.next;
            } else {
                this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
            }
        } else {
            this.jj_scanpos = this.jj_scanpos.next;
        }
        if (this.jj_rescan) {
            int i = 0;
            Token tok = this.token;
            while (tok != null && tok != this.jj_scanpos) {
                ++i;
                tok = tok.next;
            }
            if (tok != null) {
                this.jj_add_error_token(kind, i);
            }
        }
        if (this.jj_scanpos.kind != kind) {
            return true;
        }
        if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
            throw this.jj_ls;
        }
        return false;
    }

    public final Token getNextToken() {
        this.token = this.token.next != null ? this.token.next : (this.token.next = this.token_source.getNextToken());
        this.jj_ntk = -1;
        ++this.jj_gen;
        return this.token;
    }

    public final Token getToken(int index) {
        Token t = this.token;
        for (int i = 0; i < index; ++i) {
            t = t.next != null ? t.next : (t.next = this.token_source.getNextToken());
        }
        return t;
    }

    private int jj_ntk() {
        this.jj_nt = this.token.next;
        if (this.jj_nt == null) {
            this.token.next = this.token_source.getNextToken();
            this.jj_ntk = this.token.next.kind;
            return this.jj_ntk;
        }
        this.jj_ntk = this.jj_nt.kind;
        return this.jj_ntk;
    }

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) {
            return;
        }
        if (pos == this.jj_endpos + 1) {
            this.jj_lasttokens[this.jj_endpos++] = kind;
        } else if (this.jj_endpos != 0) {
            this.jj_expentry = new int[this.jj_endpos];
            for (int i = 0; i < this.jj_endpos; ++i) {
                this.jj_expentry[i] = this.jj_lasttokens[i];
            }
            block1: for (int[] oldentry : this.jj_expentries) {
                if (oldentry.length != this.jj_expentry.length) continue;
                for (int i = 0; i < this.jj_expentry.length; ++i) {
                    if (oldentry[i] != this.jj_expentry[i]) continue block1;
                }
                this.jj_expentries.add(this.jj_expentry);
                break;
            }
            if (pos != 0) {
                this.jj_endpos = pos;
                this.jj_lasttokens[this.jj_endpos - 1] = kind;
            }
        }
    }

    public ParseException generateParseException() {
        int i;
        this.jj_expentries.clear();
        boolean[] la1tokens = new boolean[34];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 28; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) == 0) continue;
                la1tokens[32 + j] = true;
            }
        }
        for (i = 0; i < 34; ++i) {
            if (!la1tokens[i]) continue;
            this.jj_expentry = new int[1];
            this.jj_expentry[0] = i;
            this.jj_expentries.add(this.jj_expentry);
        }
        this.jj_endpos = 0;
        this.jj_rescan_token();
        this.jj_add_error_token(0, 0);
        int[][] exptokseq = new int[this.jj_expentries.size()][];
        for (int i2 = 0; i2 < this.jj_expentries.size(); ++i2) {
            exptokseq[i2] = this.jj_expentries.get(i2);
        }
        return new ParseException(this.token, exptokseq, tokenImage);
    }

    public final void enable_tracing() {
    }

    public final void disable_tracing() {
    }

    private void jj_rescan_token() {
        this.jj_rescan = true;
        for (int i = 0; i < 2; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen <= this.jj_gen) continue;
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
                            break;
                        }
                        case 1: {
                            this.jj_3_2();
                        }
                    }
                } while ((p = p.next) != null);
                continue;
            }
            catch (LookaheadSuccess lookaheadSuccess) {
                // empty catch block
            }
        }
        this.jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        JJCalls p = this.jj_2_rtns[index];
        while (p.gen > this.jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = this.jj_gen + xla - this.jj_la;
        p.first = this.token;
        p.arg = xla;
    }

    static {
        StandardSyntaxParser.jj_la1_init_0();
        StandardSyntaxParser.jj_la1_init_1();
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;

        JJCalls() {
        }
    }

    private static final class LookaheadSuccess
    extends Error {
        private LookaheadSuccess() {
        }
    }
}

