/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.surround.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.queryparser.surround.parser.CharStream;
import org.apache.lucene.queryparser.surround.parser.FastCharStream;
import org.apache.lucene.queryparser.surround.parser.ParseException;
import org.apache.lucene.queryparser.surround.parser.QueryParserConstants;
import org.apache.lucene.queryparser.surround.parser.QueryParserTokenManager;
import org.apache.lucene.queryparser.surround.parser.Token;
import org.apache.lucene.queryparser.surround.parser.TokenMgrError;
import org.apache.lucene.queryparser.surround.query.AndQuery;
import org.apache.lucene.queryparser.surround.query.DistanceQuery;
import org.apache.lucene.queryparser.surround.query.FieldsQuery;
import org.apache.lucene.queryparser.surround.query.NotQuery;
import org.apache.lucene.queryparser.surround.query.OrQuery;
import org.apache.lucene.queryparser.surround.query.SrndPrefixQuery;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.queryparser.surround.query.SrndTermQuery;
import org.apache.lucene.queryparser.surround.query.SrndTruncQuery;

public class QueryParser
implements QueryParserConstants {
    final int minimumPrefixLength = 3;
    final int minimumCharsInTrunc = 3;
    final String truncationErrorMessage = "Too unrestrictive truncation: ";
    final String boostErrorMessage = "Cannot handle boost value: ";
    final char truncator = (char)42;
    final char anyChar = (char)63;
    final char quote = (char)34;
    final char fieldOperator = (char)58;
    final char comma = (char)44;
    final char carat = (char)94;
    public QueryParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[10];
    private static int[] jj_la1_0;
    private final JJCalls[] jj_2_rtns = new JJCalls[1];
    private boolean jj_rescan = false;
    private int jj_gc = 0;
    private final LookaheadSuccess jj_ls = new LookaheadSuccess();
    private List<int[]> jj_expentries = new ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    public static SrndQuery parse(String query) throws ParseException {
        QueryParser parser = new QueryParser();
        return parser.parse2(query);
    }

    public QueryParser() {
        this(new FastCharStream(new StringReader("")));
    }

    public SrndQuery parse2(String query) throws ParseException {
        this.ReInit(new FastCharStream(new StringReader(query)));
        try {
            return this.TopSrndQuery();
        }
        catch (TokenMgrError tme) {
            throw new ParseException(tme.getMessage());
        }
    }

    protected SrndQuery getFieldsQuery(SrndQuery q, ArrayList<String> fieldNames) {
        return new FieldsQuery(q, fieldNames, ':');
    }

    protected SrndQuery getOrQuery(List<SrndQuery> queries, boolean infix, Token orToken) {
        return new OrQuery(queries, infix, orToken.image);
    }

    protected SrndQuery getAndQuery(List<SrndQuery> queries, boolean infix, Token andToken) {
        return new AndQuery(queries, infix, andToken.image);
    }

    protected SrndQuery getNotQuery(List<SrndQuery> queries, Token notToken) {
        return new NotQuery(queries, notToken.image);
    }

    protected static int getOpDistance(String distanceOp) {
        return distanceOp.length() == 1 ? 1 : Integer.parseInt(distanceOp.substring(0, distanceOp.length() - 1));
    }

    protected static void checkDistanceSubQueries(DistanceQuery distq, String opName) throws ParseException {
        String m = distq.distanceSubQueryNotAllowed();
        if (m != null) {
            throw new ParseException("Operator " + opName + ": " + m);
        }
    }

    protected SrndQuery getDistanceQuery(List<SrndQuery> queries, boolean infix, Token dToken, boolean ordered) throws ParseException {
        DistanceQuery dq = new DistanceQuery(queries, infix, QueryParser.getOpDistance(dToken.image), dToken.image, ordered);
        QueryParser.checkDistanceSubQueries(dq, dToken.image);
        return dq;
    }

    protected SrndQuery getTermQuery(String term, boolean quoted) {
        return new SrndTermQuery(term, quoted);
    }

    protected boolean allowedSuffix(String suffixed) {
        return suffixed.length() - 1 >= 3;
    }

    protected SrndQuery getPrefixQuery(String prefix, boolean quoted) {
        return new SrndPrefixQuery(prefix, quoted, '*');
    }

    protected boolean allowedTruncation(String truncated) {
        int nrNormalChars = 0;
        for (int i = 0; i < truncated.length(); ++i) {
            char c = truncated.charAt(i);
            if (c == '*' || c == '?') continue;
            ++nrNormalChars;
        }
        return nrNormalChars >= 3;
    }

    protected SrndQuery getTruncQuery(String truncated) {
        return new SrndTruncQuery(truncated, '*', '?');
    }

    public final SrndQuery TopSrndQuery() throws ParseException {
        SrndQuery q = this.FieldsQuery();
        this.jj_consume_token(0);
        return q;
    }

    public final SrndQuery FieldsQuery() throws ParseException {
        ArrayList<String> fieldNames = this.OptionalFields();
        SrndQuery q = this.OrQuery();
        return fieldNames == null ? q : this.getFieldsQuery(q, fieldNames);
    }

    public final ArrayList<String> OptionalFields() throws ParseException {
        ArrayList<String> fieldNames = null;
        while (this.jj_2_1(2)) {
            Token fieldName = this.jj_consume_token(22);
            this.jj_consume_token(16);
            if (fieldNames == null) {
                fieldNames = new ArrayList<String>();
            }
            fieldNames.add(fieldName.image);
        }
        return fieldNames;
    }

    public final SrndQuery OrQuery() throws ParseException {
        ArrayList<SrndQuery> queries = null;
        Token oprt = null;
        SrndQuery q = this.AndQuery();
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 8: {
                    break;
                }
                default: {
                    this.jj_la1[0] = this.jj_gen;
                    break block3;
                }
            }
            oprt = this.jj_consume_token(8);
            if (queries == null) {
                queries = new ArrayList<SrndQuery>();
                queries.add(q);
            }
            q = this.AndQuery();
            queries.add(q);
        }
        return queries == null ? q : this.getOrQuery(queries, true, oprt);
    }

    public final SrndQuery AndQuery() throws ParseException {
        ArrayList<SrndQuery> queries = null;
        Token oprt = null;
        SrndQuery q = this.NotQuery();
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 9: {
                    break;
                }
                default: {
                    this.jj_la1[1] = this.jj_gen;
                    break block3;
                }
            }
            oprt = this.jj_consume_token(9);
            if (queries == null) {
                queries = new ArrayList<SrndQuery>();
                queries.add(q);
            }
            q = this.NotQuery();
            queries.add(q);
        }
        return queries == null ? q : this.getAndQuery(queries, true, oprt);
    }

    public final SrndQuery NotQuery() throws ParseException {
        ArrayList<SrndQuery> queries = null;
        Token oprt = null;
        SrndQuery q = this.NQuery();
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 10: {
                    break;
                }
                default: {
                    this.jj_la1[2] = this.jj_gen;
                    break block3;
                }
            }
            oprt = this.jj_consume_token(10);
            if (queries == null) {
                queries = new ArrayList<SrndQuery>();
                queries.add(q);
            }
            q = this.NQuery();
            queries.add(q);
        }
        return queries == null ? q : this.getNotQuery(queries, oprt);
    }

    public final SrndQuery NQuery() throws ParseException {
        SrndQuery q = this.WQuery();
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 12: {
                    break;
                }
                default: {
                    this.jj_la1[3] = this.jj_gen;
                    break block3;
                }
            }
            Token dt = this.jj_consume_token(12);
            ArrayList<SrndQuery> queries = new ArrayList<SrndQuery>();
            queries.add(q);
            q = this.WQuery();
            queries.add(q);
            q = this.getDistanceQuery(queries, true, dt, false);
        }
        return q;
    }

    public final SrndQuery WQuery() throws ParseException {
        SrndQuery q = this.PrimaryQuery();
        block3: while (true) {
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 11: {
                    break;
                }
                default: {
                    this.jj_la1[4] = this.jj_gen;
                    break block3;
                }
            }
            Token wt = this.jj_consume_token(11);
            ArrayList<SrndQuery> queries = new ArrayList<SrndQuery>();
            queries.add(q);
            q = this.PrimaryQuery();
            queries.add(q);
            q = this.getDistanceQuery(queries, true, wt, true);
        }
        return q;
    }

    public final SrndQuery PrimaryQuery() throws ParseException {
        SrndQuery q;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13: {
                this.jj_consume_token(13);
                q = this.FieldsQuery();
                this.jj_consume_token(14);
                break;
            }
            case 8: 
            case 9: 
            case 11: 
            case 12: {
                q = this.PrefixOperatorQuery();
                break;
            }
            case 18: 
            case 19: 
            case 20: 
            case 21: 
            case 22: {
                q = this.SimpleTerm();
                break;
            }
            default: {
                this.jj_la1[5] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        this.OptionalWeights(q);
        return q;
    }

    public final SrndQuery PrefixOperatorQuery() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 8: {
                Token oprt = this.jj_consume_token(8);
                List<SrndQuery> queries = this.FieldsQueryList();
                return this.getOrQuery(queries, false, oprt);
            }
            case 9: {
                Token oprt = this.jj_consume_token(9);
                List<SrndQuery> queries = this.FieldsQueryList();
                return this.getAndQuery(queries, false, oprt);
            }
            case 12: {
                Token oprt = this.jj_consume_token(12);
                List<SrndQuery> queries = this.FieldsQueryList();
                return this.getDistanceQuery(queries, false, oprt, false);
            }
            case 11: {
                Token oprt = this.jj_consume_token(11);
                List<SrndQuery> queries = this.FieldsQueryList();
                return this.getDistanceQuery(queries, false, oprt, true);
            }
        }
        this.jj_la1[6] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final List<SrndQuery> FieldsQueryList() throws ParseException {
        ArrayList<SrndQuery> queries = new ArrayList<SrndQuery>();
        this.jj_consume_token(13);
        SrndQuery q = this.FieldsQuery();
        queries.add(q);
        block3: while (true) {
            this.jj_consume_token(15);
            q = this.FieldsQuery();
            queries.add(q);
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 15: {
                    continue block3;
                }
            }
            break;
        }
        this.jj_la1[7] = this.jj_gen;
        this.jj_consume_token(14);
        return queries;
    }

    public final SrndQuery SimpleTerm() throws ParseException {
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 22: {
                Token term = this.jj_consume_token(22);
                return this.getTermQuery(term.image, false);
            }
            case 19: {
                Token term = this.jj_consume_token(19);
                return this.getTermQuery(term.image.substring(1, term.image.length() - 1), true);
            }
            case 20: {
                Token term = this.jj_consume_token(20);
                if (!this.allowedSuffix(term.image)) {
                    throw new ParseException("Too unrestrictive truncation: " + term.image);
                }
                return this.getPrefixQuery(term.image.substring(0, term.image.length() - 1), false);
            }
            case 21: {
                Token term = this.jj_consume_token(21);
                if (!this.allowedTruncation(term.image)) {
                    throw new ParseException("Too unrestrictive truncation: " + term.image);
                }
                return this.getTruncQuery(term.image);
            }
            case 18: {
                Token term = this.jj_consume_token(18);
                if (term.image.length() - 3 < 3) {
                    throw new ParseException("Too unrestrictive truncation: " + term.image);
                }
                return this.getPrefixQuery(term.image.substring(1, term.image.length() - 2), true);
            }
        }
        this.jj_la1[8] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }

    public final void OptionalWeights(SrndQuery q) throws ParseException {
        Token weight = null;
        block5: while (true) {
            float f;
            switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                case 17: {
                    break;
                }
                default: {
                    this.jj_la1[9] = this.jj_gen;
                    break block5;
                }
            }
            this.jj_consume_token(17);
            weight = this.jj_consume_token(23);
            try {
                f = Float.valueOf(weight.image).floatValue();
            }
            catch (Exception floatExc) {
                throw new ParseException("Cannot handle boost value: " + weight.image + " (" + floatExc + ")");
            }
            if ((double)f <= 0.0) {
                throw new ParseException("Cannot handle boost value: " + weight.image);
            }
            q.setWeight(f * q.getWeight());
        }
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

    private boolean jj_3_1() {
        if (this.jj_scan_token(22)) {
            return true;
        }
        return this.jj_scan_token(16);
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{256, 512, 1024, 4096, 2048, 8141568, 6912, 32768, 0x7C0000, 131072};
    }

    public QueryParser(CharStream stream) {
        int i;
        this.token_source = new QueryParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 10; ++i) {
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
        for (i = 0; i < 10; ++i) {
            this.jj_la1[i] = -1;
        }
        for (i = 0; i < this.jj_2_rtns.length; ++i) {
            this.jj_2_rtns[i] = new JJCalls();
        }
    }

    public QueryParser(QueryParserTokenManager tm) {
        int i;
        this.token_source = tm;
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 10; ++i) {
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
        for (i = 0; i < 10; ++i) {
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
        boolean[] la1tokens = new boolean[24];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 10; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) == 0) continue;
                la1tokens[j] = true;
            }
        }
        for (i = 0; i < 24; ++i) {
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

