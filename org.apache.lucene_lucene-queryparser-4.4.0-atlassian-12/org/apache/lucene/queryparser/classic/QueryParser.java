/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.queryparser.classic;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.CharStream;
import org.apache.lucene.queryparser.classic.FastCharStream;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParserBase;
import org.apache.lucene.queryparser.classic.QueryParserConstants;
import org.apache.lucene.queryparser.classic.QueryParserTokenManager;
import org.apache.lucene.queryparser.classic.Token;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class QueryParser
extends QueryParserBase
implements QueryParserConstants {
    public QueryParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[21];
    private static int[] jj_la1_0;
    private static int[] jj_la1_1;
    private final JJCalls[] jj_2_rtns = new JJCalls[1];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public QueryParser(Version matchVersion, String f, Analyzer a) {
        this(new FastCharStream(new StringReader("")));
        this.init(matchVersion, f, a);
    }

    public final int Conjunction() throws ParseException {
        int ret = 0;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: 
            case 9: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 8: {
                        this.jj_consume_token(8);
                        ret = 1;
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

    public final int Modifiers() throws ParseException {
        int ret = 0;
        block0 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 10: 
            case 11: 
            case 12: {
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 11: {
                        this.jj_consume_token(11);
                        ret = 11;
                        break block0;
                    }
                    case 12: {
                        this.jj_consume_token(12);
                        ret = 10;
                        break block0;
                    }
                    case 10: {
                        this.jj_consume_token(10);
                        ret = 10;
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

    @Override
    public final Query TopLevelQuery(String field) throws ParseException {
        Query q = this.Query(field);
        this.jj_consume_token(0);
        return q;
    }

    public final Query Query(String field) throws ParseException {
        ArrayList<BooleanClause> clauses = new ArrayList<BooleanClause>();
        Query firstQuery = null;
        int mods = this.Modifiers();
        Query q = this.Clause(field);
        this.addClause(clauses, 0, mods, q);
        if (mods == 0) {
            firstQuery = q;
        }
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 17: 
                case 19: 
                case 20: 
                case 22: 
                case 23: 
                case 24: 
                case 25: 
                case 26: 
                case 27: {
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    break block3;
                }
            }
            int conj = this.Conjunction();
            mods = this.Modifiers();
            q = this.Clause(field);
            this.addClause(clauses, conj, mods, q);
        }
        if (clauses.size() == 1 && firstQuery != null) {
            return firstQuery;
        }
        return this.getBooleanQuery(clauses);
    }

    public final Query Clause(String field) throws ParseException {
        Query q;
        Token fieldToken = null;
        Token boost = null;
        if (this.jj_2_1(2)) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 20: {
                    fieldToken = this.jj_consume_token(20);
                    this.jj_consume_token(16);
                    field = this.discardEscapeChar(fieldToken.image);
                    break;
                }
                case 17: {
                    this.jj_consume_token(17);
                    this.jj_consume_token(16);
                    field = "*";
                    break;
                }
                default: {
                    this.jj_la1[5] = this.jj_gen;
                    this.jj_consume_token(-1);
                    throw new ParseException();
                }
            }
        }
        block4 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13: 
            case 17: 
            case 19: 
            case 20: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: 
            case 27: {
                q = this.Term(field);
                break;
            }
            case 14: {
                this.jj_consume_token(14);
                q = this.Query(field);
                this.jj_consume_token(15);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(27);
                        break block4;
                    }
                }
                this.jj_la1[6] = this.jj_gen;
                break;
            }
            default: {
                this.jj_la1[7] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return this.handleBoost(q, boost);
    }

    public final Query Term(String field) throws ParseException {
        Query q;
        Token boost = null;
        Token fuzzySlop = null;
        boolean prefix = false;
        boolean wildcard = false;
        boolean fuzzy = false;
        boolean regexp = false;
        boolean startInc = false;
        boolean endInc = false;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13: 
            case 17: 
            case 20: 
            case 22: 
            case 23: 
            case 24: 
            case 27: {
                Token term;
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 20: {
                        term = this.jj_consume_token(20);
                        break;
                    }
                    case 17: {
                        term = this.jj_consume_token(17);
                        wildcard = true;
                        break;
                    }
                    case 22: {
                        term = this.jj_consume_token(22);
                        prefix = true;
                        break;
                    }
                    case 23: {
                        term = this.jj_consume_token(23);
                        wildcard = true;
                        break;
                    }
                    case 24: {
                        term = this.jj_consume_token(24);
                        regexp = true;
                        break;
                    }
                    case 27: {
                        term = this.jj_consume_token(27);
                        break;
                    }
                    case 13: {
                        term = this.jj_consume_token(13);
                        term.image = term.image.substring(0, 1);
                        break;
                    }
                    default: {
                        this.jj_la1[8] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        fuzzySlop = this.jj_consume_token(21);
                        fuzzy = true;
                        break;
                    }
                    default: {
                        this.jj_la1[9] = this.jj_gen;
                    }
                }
                block17 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(27);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 21: {
                                fuzzySlop = this.jj_consume_token(21);
                                fuzzy = true;
                                break block17;
                            }
                        }
                        this.jj_la1[10] = this.jj_gen;
                        break;
                    }
                    default: {
                        this.jj_la1[11] = this.jj_gen;
                    }
                }
                q = this.handleBareTokenQuery(field, term, fuzzySlop, prefix, wildcard, fuzzy, regexp);
                break;
            }
            case 25: 
            case 26: {
                Token goop2;
                Token goop1;
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 25: {
                        this.jj_consume_token(25);
                        startInc = true;
                        break;
                    }
                    case 26: {
                        this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[12] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 32: {
                        goop1 = this.jj_consume_token(32);
                        break;
                    }
                    case 31: {
                        goop1 = this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[13] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 28: {
                        this.jj_consume_token(28);
                        break;
                    }
                    default: {
                        this.jj_la1[14] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 32: {
                        goop2 = this.jj_consume_token(32);
                        break;
                    }
                    case 31: {
                        goop2 = this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 29: {
                        this.jj_consume_token(29);
                        endInc = true;
                        break;
                    }
                    case 30: {
                        this.jj_consume_token(30);
                        break;
                    }
                    default: {
                        this.jj_la1[16] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(27);
                        break;
                    }
                    default: {
                        this.jj_la1[17] = this.jj_gen;
                    }
                }
                boolean startOpen = false;
                boolean endOpen = false;
                if (goop1.kind == 31) {
                    goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
                } else if ("*".equals(goop1.image)) {
                    startOpen = true;
                }
                if (goop2.kind == 31) {
                    goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
                } else if ("*".equals(goop2.image)) {
                    endOpen = true;
                }
                q = this.getRangeQuery(field, startOpen ? null : this.discardEscapeChar(goop1.image), endOpen ? null : this.discardEscapeChar(goop2.image), startInc, endInc);
                break;
            }
            case 19: {
                Token term = this.jj_consume_token(19);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 21: {
                        fuzzySlop = this.jj_consume_token(21);
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(27);
                        break;
                    }
                    default: {
                        this.jj_la1[19] = this.jj_gen;
                    }
                }
                q = this.handleQuotedTerm(field, term, fuzzySlop);
                break;
            }
            default: {
                this.jj_la1[20] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        return this.handleBoost(q, boost);
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

    private boolean jj_3R_2() {
        if (this.jj_scan_token(20)) {
            return true;
        }
        return this.jj_scan_token(16);
    }

    private boolean jj_3_1() {
        Token xsp = this.jj_scanpos;
        if (this.jj_3R_2()) {
            this.jj_scanpos = xsp;
            if (this.jj_3R_3()) {
                return true;
            }
        }
        return false;
    }

    private boolean jj_3R_3() {
        if (this.jj_scan_token(17)) {
            return true;
        }
        return this.jj_scan_token(16);
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{768, 768, 7168, 7168, 265977600, 0x120000, 262144, 265969664, 164765696, 0x200000, 0x200000, 262144, 0x6000000, Integer.MIN_VALUE, 0x10000000, Integer.MIN_VALUE, 0x60000000, 262144, 0x200000, 262144, 265953280};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0};
    }

    protected QueryParser(CharStream stream) {
        int i;
        this.token_source = new QueryParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 21; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    @Override
    public void ReInit(CharStream stream) {
        int i;
        this.token_source.ReInit(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 21; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    protected QueryParser(QueryParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 21; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public void ReInit(QueryParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 21; ++i) {
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
        boolean[] la1tokens = new boolean[33];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 21; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) == 0) continue;
                la1tokens[32 + j] = true;
            }
        }
        for (i = 0; i < 33; ++i) {
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
        for (int i = 0; i < 1; ++i) {
            try {
                JJCalls p = this.jj_2_rtns[i];
                do {
                    if (p.gen <= this.jj_gen) continue;
                    this.jj_la = p.arg;
                    this.jj_lastpos = this.jj_scanpos = p.first;
                    switch (i) {
                        case 0: {
                            this.jj_3_1();
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
        QueryParser.jj_la1_init_0();
        QueryParser.jj_la1_init_1();
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

    public static enum Operator {
        OR,
        AND;

    }
}

