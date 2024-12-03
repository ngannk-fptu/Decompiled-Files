/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.queryParser;

import com.atlassian.lucene36.analysis.Analyzer;
import com.atlassian.lucene36.analysis.CachingTokenFilter;
import com.atlassian.lucene36.analysis.SimpleAnalyzer;
import com.atlassian.lucene36.analysis.TokenStream;
import com.atlassian.lucene36.analysis.tokenattributes.CharTermAttribute;
import com.atlassian.lucene36.analysis.tokenattributes.PositionIncrementAttribute;
import com.atlassian.lucene36.document.DateField;
import com.atlassian.lucene36.document.DateTools;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.queryParser.CharStream;
import com.atlassian.lucene36.queryParser.FastCharStream;
import com.atlassian.lucene36.queryParser.ParseException;
import com.atlassian.lucene36.queryParser.QueryParserConstants;
import com.atlassian.lucene36.queryParser.QueryParserTokenManager;
import com.atlassian.lucene36.queryParser.Token;
import com.atlassian.lucene36.queryParser.TokenMgrError;
import com.atlassian.lucene36.search.BooleanClause;
import com.atlassian.lucene36.search.BooleanQuery;
import com.atlassian.lucene36.search.FuzzyQuery;
import com.atlassian.lucene36.search.MatchAllDocsQuery;
import com.atlassian.lucene36.search.MultiPhraseQuery;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.PhraseQuery;
import com.atlassian.lucene36.search.PrefixQuery;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.TermQuery;
import com.atlassian.lucene36.search.TermRangeQuery;
import com.atlassian.lucene36.search.WildcardQuery;
import com.atlassian.lucene36.util.Version;
import com.atlassian.lucene36.util.VirtualMethod;
import java.io.IOException;
import java.io.StringReader;
import java.text.Collator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class QueryParser
implements QueryParserConstants {
    private static final int CONJ_NONE = 0;
    private static final int CONJ_AND = 1;
    private static final int CONJ_OR = 2;
    private static final int MOD_NONE = 0;
    private static final int MOD_NOT = 10;
    private static final int MOD_REQ = 11;
    public static final Operator AND_OPERATOR = Operator.AND;
    public static final Operator OR_OPERATOR = Operator.OR;
    private Operator operator = OR_OPERATOR;
    boolean lowercaseExpandedTerms = true;
    MultiTermQuery.RewriteMethod multiTermRewriteMethod = MultiTermQuery.CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
    boolean allowLeadingWildcard = false;
    boolean enablePositionIncrements = true;
    Analyzer analyzer;
    String field;
    int phraseSlop = 0;
    float fuzzyMinSim = 0.5f;
    int fuzzyPrefixLength = 0;
    Locale locale = Locale.getDefault();
    DateTools.Resolution dateResolution = null;
    Map<String, DateTools.Resolution> fieldToDateResolution = null;
    Collator rangeCollator = null;
    @Deprecated
    private static final VirtualMethod<QueryParser> getFieldQueryMethod = new VirtualMethod<QueryParser>(QueryParser.class, "getFieldQuery", String.class, String.class);
    @Deprecated
    private static final VirtualMethod<QueryParser> getFieldQueryWithQuotedMethod = new VirtualMethod<QueryParser>(QueryParser.class, "getFieldQuery", String.class, String.class, Boolean.TYPE);
    @Deprecated
    private final boolean hasNewAPI = VirtualMethod.compareImplementationDistance(this.getClass(), getFieldQueryWithQuotedMethod, getFieldQueryMethod) >= 0;
    private boolean autoGeneratePhraseQueries;
    public QueryParserTokenManager token_source;
    public Token token;
    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos;
    private Token jj_lastpos;
    private int jj_la;
    private int jj_gen;
    private final int[] jj_la1 = new int[23];
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
        this.analyzer = a;
        this.field = f;
        this.enablePositionIncrements = matchVersion.onOrAfter(Version.LUCENE_29);
        if (matchVersion.onOrAfter(Version.LUCENE_31)) {
            this.setAutoGeneratePhraseQueries(false);
        } else {
            this.setAutoGeneratePhraseQueries(true);
        }
    }

    public Query parse(String query) throws ParseException {
        this.ReInit(new FastCharStream(new StringReader(query)));
        try {
            Query res = this.TopLevelQuery(this.field);
            return res != null ? res : this.newBooleanQuery(false);
        }
        catch (ParseException tme) {
            ParseException e = new ParseException("Cannot parse '" + query + "': " + tme.getMessage());
            e.initCause(tme);
            throw e;
        }
        catch (TokenMgrError tme) {
            ParseException e = new ParseException("Cannot parse '" + query + "': " + tme.getMessage());
            e.initCause(tme);
            throw e;
        }
        catch (BooleanQuery.TooManyClauses tmc) {
            ParseException e = new ParseException("Cannot parse '" + query + "': too many boolean clauses");
            e.initCause(tmc);
            throw e;
        }
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }

    public String getField() {
        return this.field;
    }

    public final boolean getAutoGeneratePhraseQueries() {
        return this.autoGeneratePhraseQueries;
    }

    public final void setAutoGeneratePhraseQueries(boolean value) {
        if (!value && !this.hasNewAPI) {
            throw new IllegalArgumentException("You must implement the new API: getFieldQuery(String,String,boolean) to use setAutoGeneratePhraseQueries(false)");
        }
        this.autoGeneratePhraseQueries = value;
    }

    public float getFuzzyMinSim() {
        return this.fuzzyMinSim;
    }

    public void setFuzzyMinSim(float fuzzyMinSim) {
        this.fuzzyMinSim = fuzzyMinSim;
    }

    public int getFuzzyPrefixLength() {
        return this.fuzzyPrefixLength;
    }

    public void setFuzzyPrefixLength(int fuzzyPrefixLength) {
        this.fuzzyPrefixLength = fuzzyPrefixLength;
    }

    public void setPhraseSlop(int phraseSlop) {
        this.phraseSlop = phraseSlop;
    }

    public int getPhraseSlop() {
        return this.phraseSlop;
    }

    public void setAllowLeadingWildcard(boolean allowLeadingWildcard) {
        this.allowLeadingWildcard = allowLeadingWildcard;
    }

    public boolean getAllowLeadingWildcard() {
        return this.allowLeadingWildcard;
    }

    public void setEnablePositionIncrements(boolean enable) {
        this.enablePositionIncrements = enable;
    }

    public boolean getEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }

    public void setDefaultOperator(Operator op) {
        this.operator = op;
    }

    public Operator getDefaultOperator() {
        return this.operator;
    }

    public void setLowercaseExpandedTerms(boolean lowercaseExpandedTerms) {
        this.lowercaseExpandedTerms = lowercaseExpandedTerms;
    }

    public boolean getLowercaseExpandedTerms() {
        return this.lowercaseExpandedTerms;
    }

    public void setMultiTermRewriteMethod(MultiTermQuery.RewriteMethod method) {
        this.multiTermRewriteMethod = method;
    }

    public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod() {
        return this.multiTermRewriteMethod;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setDateResolution(DateTools.Resolution dateResolution) {
        this.dateResolution = dateResolution;
    }

    public void setDateResolution(String fieldName, DateTools.Resolution dateResolution) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Field cannot be null.");
        }
        if (this.fieldToDateResolution == null) {
            this.fieldToDateResolution = new HashMap<String, DateTools.Resolution>();
        }
        this.fieldToDateResolution.put(fieldName, dateResolution);
    }

    public DateTools.Resolution getDateResolution(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("Field cannot be null.");
        }
        if (this.fieldToDateResolution == null) {
            return this.dateResolution;
        }
        DateTools.Resolution resolution = this.fieldToDateResolution.get(fieldName);
        if (resolution == null) {
            resolution = this.dateResolution;
        }
        return resolution;
    }

    public void setRangeCollator(Collator rc) {
        this.rangeCollator = rc;
    }

    public Collator getRangeCollator() {
        return this.rangeCollator;
    }

    protected void addClause(List<BooleanClause> clauses, int conj, int mods, Query q) {
        boolean required;
        boolean prohibited;
        BooleanClause c;
        if (clauses.size() > 0 && conj == 1 && !(c = clauses.get(clauses.size() - 1)).isProhibited()) {
            c.setOccur(BooleanClause.Occur.MUST);
        }
        if (clauses.size() > 0 && this.operator == AND_OPERATOR && conj == 2 && !(c = clauses.get(clauses.size() - 1)).isProhibited()) {
            c.setOccur(BooleanClause.Occur.SHOULD);
        }
        if (q == null) {
            return;
        }
        if (this.operator == OR_OPERATOR) {
            prohibited = mods == 10;
            boolean bl = required = mods == 11;
            if (conj == 1 && !prohibited) {
                required = true;
            }
        } else {
            prohibited = mods == 10;
            boolean bl = required = !prohibited && conj != 2;
        }
        if (required && !prohibited) {
            clauses.add(this.newBooleanClause(q, BooleanClause.Occur.MUST));
        } else if (!required && !prohibited) {
            clauses.add(this.newBooleanClause(q, BooleanClause.Occur.SHOULD));
        } else if (!required && prohibited) {
            clauses.add(this.newBooleanClause(q, BooleanClause.Occur.MUST_NOT));
        } else {
            throw new RuntimeException("Clause cannot be both required and prohibited");
        }
    }

    @Deprecated
    protected Query getFieldQuery(String field, String queryText) throws ParseException {
        return this.getFieldQuery(field, queryText, true);
    }

    protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
        TokenStream source;
        try {
            source = this.analyzer.reusableTokenStream(field, new StringReader(queryText));
            source.reset();
        }
        catch (IOException e) {
            source = this.analyzer.tokenStream(field, new StringReader(queryText));
        }
        CachingTokenFilter buffer = new CachingTokenFilter(source);
        CharTermAttribute termAtt = null;
        PositionIncrementAttribute posIncrAtt = null;
        int numTokens = 0;
        boolean success = false;
        try {
            buffer.reset();
            success = true;
        }
        catch (IOException e) {
            // empty catch block
        }
        if (success) {
            if (buffer.hasAttribute(CharTermAttribute.class)) {
                termAtt = buffer.getAttribute(CharTermAttribute.class);
            }
            if (buffer.hasAttribute(PositionIncrementAttribute.class)) {
                posIncrAtt = buffer.getAttribute(PositionIncrementAttribute.class);
            }
        }
        int positionCount = 0;
        boolean severalTokensAtSamePosition = false;
        boolean hasMoreTokens = false;
        if (termAtt != null) {
            try {
                hasMoreTokens = buffer.incrementToken();
                while (hasMoreTokens) {
                    int positionIncrement;
                    ++numTokens;
                    int n = positionIncrement = posIncrAtt != null ? posIncrAtt.getPositionIncrement() : 1;
                    if (positionIncrement != 0) {
                        positionCount += positionIncrement;
                    } else {
                        severalTokensAtSamePosition = true;
                    }
                    hasMoreTokens = buffer.incrementToken();
                }
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        try {
            buffer.reset();
            source.close();
        }
        catch (IOException e) {
            // empty catch block
        }
        if (numTokens == 0) {
            return null;
        }
        if (numTokens == 1) {
            String term = null;
            try {
                boolean hasNext = buffer.incrementToken();
                assert (hasNext);
                term = termAtt.toString();
            }
            catch (IOException e) {
                // empty catch block
            }
            return this.newTermQuery(new Term(field, term));
        }
        if (severalTokensAtSamePosition || !quoted && !this.autoGeneratePhraseQueries) {
            if (positionCount == 1 || !quoted && !this.autoGeneratePhraseQueries) {
                BooleanQuery q = this.newBooleanQuery(positionCount == 1);
                BooleanClause.Occur occur = positionCount > 1 && this.operator == AND_OPERATOR ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD;
                for (int i = 0; i < numTokens; ++i) {
                    String term = null;
                    try {
                        boolean hasNext = buffer.incrementToken();
                        assert (hasNext);
                        term = termAtt.toString();
                    }
                    catch (IOException e) {
                        // empty catch block
                    }
                    Query currentQuery = this.newTermQuery(new Term(field, term));
                    q.add(currentQuery, occur);
                }
                return q;
            }
            MultiPhraseQuery mpq = this.newMultiPhraseQuery();
            mpq.setSlop(this.phraseSlop);
            ArrayList<Term> multiTerms = new ArrayList<Term>();
            int position = -1;
            for (int i = 0; i < numTokens; ++i) {
                String term = null;
                int positionIncrement = 1;
                try {
                    boolean hasNext = buffer.incrementToken();
                    assert (hasNext);
                    term = termAtt.toString();
                    if (posIncrAtt != null) {
                        positionIncrement = posIncrAtt.getPositionIncrement();
                    }
                }
                catch (IOException e) {
                    // empty catch block
                }
                if (positionIncrement > 0 && multiTerms.size() > 0) {
                    if (this.enablePositionIncrements) {
                        mpq.add(multiTerms.toArray(new Term[0]), position);
                    } else {
                        mpq.add(multiTerms.toArray(new Term[0]));
                    }
                    multiTerms.clear();
                }
                position += positionIncrement;
                multiTerms.add(new Term(field, term));
            }
            if (this.enablePositionIncrements) {
                mpq.add(multiTerms.toArray(new Term[0]), position);
            } else {
                mpq.add(multiTerms.toArray(new Term[0]));
            }
            return mpq;
        }
        PhraseQuery pq = this.newPhraseQuery();
        pq.setSlop(this.phraseSlop);
        int position = -1;
        for (int i = 0; i < numTokens; ++i) {
            String term = null;
            int positionIncrement = 1;
            try {
                boolean hasNext = buffer.incrementToken();
                assert (hasNext);
                term = termAtt.toString();
                if (posIncrAtt != null) {
                    positionIncrement = posIncrAtt.getPositionIncrement();
                }
            }
            catch (IOException e) {
                // empty catch block
            }
            if (this.enablePositionIncrements) {
                pq.add(new Term(field, term), position += positionIncrement);
                continue;
            }
            pq.add(new Term(field, term));
        }
        return pq;
    }

    protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {
        Query query;
        Query query2 = query = this.hasNewAPI ? this.getFieldQuery(field, queryText, true) : this.getFieldQuery(field, queryText);
        if (query instanceof PhraseQuery) {
            ((PhraseQuery)query).setSlop(slop);
        }
        if (query instanceof MultiPhraseQuery) {
            ((MultiPhraseQuery)query).setSlop(slop);
        }
        return query;
    }

    protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            part1 = part1 == null ? null : part1.toLowerCase();
            part2 = part2 == null ? null : part2.toLowerCase();
        }
        DateFormat df = DateFormat.getDateInstance(3, this.locale);
        df.setLenient(true);
        DateTools.Resolution resolution = this.getDateResolution(field);
        try {
            Date d1 = df.parse(part1);
            part1 = resolution == null ? DateField.dateToString(d1) : DateTools.dateToString(d1, resolution);
        }
        catch (Exception e) {
            // empty catch block
        }
        try {
            Date d2 = df.parse(part2);
            if (inclusive) {
                Calendar cal = Calendar.getInstance(this.locale);
                cal.setTime(d2);
                cal.set(11, 23);
                cal.set(12, 59);
                cal.set(13, 59);
                cal.set(14, 999);
                d2 = cal.getTime();
            }
            part2 = resolution == null ? DateField.dateToString(d2) : DateTools.dateToString(d2, resolution);
        }
        catch (Exception e) {
            // empty catch block
        }
        return this.newRangeQuery(field, part1, part2, inclusive);
    }

    protected BooleanQuery newBooleanQuery(boolean disableCoord) {
        return new BooleanQuery(disableCoord);
    }

    protected BooleanClause newBooleanClause(Query q, BooleanClause.Occur occur) {
        return new BooleanClause(q, occur);
    }

    protected Query newTermQuery(Term term) {
        return new TermQuery(term);
    }

    protected PhraseQuery newPhraseQuery() {
        return new PhraseQuery();
    }

    protected MultiPhraseQuery newMultiPhraseQuery() {
        return new MultiPhraseQuery();
    }

    protected Query newPrefixQuery(Term prefix) {
        PrefixQuery query = new PrefixQuery(prefix);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return query;
    }

    protected Query newFuzzyQuery(Term term, float minimumSimilarity, int prefixLength) {
        return new FuzzyQuery(term, minimumSimilarity, prefixLength);
    }

    protected Query newRangeQuery(String field, String part1, String part2, boolean inclusive) {
        TermRangeQuery query = new TermRangeQuery(field, part1, part2, inclusive, inclusive, this.rangeCollator);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return query;
    }

    protected Query newMatchAllDocsQuery() {
        return new MatchAllDocsQuery();
    }

    protected Query newWildcardQuery(Term t) {
        WildcardQuery query = new WildcardQuery(t);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return query;
    }

    protected Query getBooleanQuery(List<BooleanClause> clauses) throws ParseException {
        return this.getBooleanQuery(clauses, false);
    }

    protected Query getBooleanQuery(List<BooleanClause> clauses, boolean disableCoord) throws ParseException {
        if (clauses.size() == 0) {
            return null;
        }
        BooleanQuery query = this.newBooleanQuery(disableCoord);
        for (BooleanClause clause : clauses) {
            query.add(clause);
        }
        return query;
    }

    protected Query getWildcardQuery(String field, String termStr) throws ParseException {
        if ("*".equals(field) && "*".equals(termStr)) {
            return this.newMatchAllDocsQuery();
        }
        if (!this.allowLeadingWildcard && (termStr.startsWith("*") || termStr.startsWith("?"))) {
            throw new ParseException("'*' or '?' not allowed as first character in WildcardQuery");
        }
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase();
        }
        Term t = new Term(field, termStr);
        return this.newWildcardQuery(t);
    }

    protected Query getPrefixQuery(String field, String termStr) throws ParseException {
        if (!this.allowLeadingWildcard && termStr.startsWith("*")) {
            throw new ParseException("'*' not allowed as first character in PrefixQuery");
        }
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase();
        }
        Term t = new Term(field, termStr);
        return this.newPrefixQuery(t);
    }

    protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase();
        }
        Term t = new Term(field, termStr);
        return this.newFuzzyQuery(t, minSimilarity, this.fuzzyPrefixLength);
    }

    private String discardEscapeChar(String input) throws ParseException {
        char[] output = new char[input.length()];
        int length = 0;
        boolean lastCharWasEscapeChar = false;
        int codePointMultiplier = 0;
        int codePoint = 0;
        for (int i = 0; i < input.length(); ++i) {
            char curChar = input.charAt(i);
            if (codePointMultiplier > 0) {
                codePoint += QueryParser.hexToInt(curChar) * codePointMultiplier;
                if ((codePointMultiplier >>>= 4) != 0) continue;
                output[length++] = (char)codePoint;
                codePoint = 0;
                continue;
            }
            if (lastCharWasEscapeChar) {
                if (curChar == 'u') {
                    codePointMultiplier = 4096;
                } else {
                    output[length] = curChar;
                    ++length;
                }
                lastCharWasEscapeChar = false;
                continue;
            }
            if (curChar == '\\') {
                lastCharWasEscapeChar = true;
                continue;
            }
            output[length] = curChar;
            ++length;
        }
        if (codePointMultiplier > 0) {
            throw new ParseException("Truncated unicode escape sequence.");
        }
        if (lastCharWasEscapeChar) {
            throw new ParseException("Term can not end with escape character.");
        }
        return new String(output, 0, length);
    }

    private static final int hexToInt(char c) throws ParseException {
        if ('0' <= c && c <= '9') {
            return c - 48;
        }
        if ('a' <= c && c <= 'f') {
            return c - 97 + 10;
        }
        if ('A' <= c && c <= 'F') {
            return c - 65 + 10;
        }
        throw new ParseException("None-hex character in unicode escape sequence: " + c);
    }

    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java org.apache.lucene.queryParser.QueryParser <input>");
            System.exit(0);
        }
        QueryParser qp = new QueryParser(Version.LUCENE_CURRENT, "field", new SimpleAnalyzer());
        Query q = qp.parse(args[0]);
        System.out.println(q.toString("field"));
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
                case 26: {
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
        block5 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13: 
            case 17: 
            case 19: 
            case 20: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 26: {
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
                        boost = this.jj_consume_token(26);
                        break block5;
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
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image).floatValue();
                q.setBoost(f);
            }
            catch (Exception ignored) {
                // empty catch block
            }
        }
        return q;
    }

    public final Query Term(String field) throws ParseException {
        Query q;
        Token boost = null;
        Token fuzzySlop = null;
        boolean prefix = false;
        boolean wildcard = false;
        boolean fuzzy = false;
        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
            case 13: 
            case 17: 
            case 20: 
            case 22: 
            case 23: 
            case 26: {
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
                    case 26: {
                        term = this.jj_consume_token(26);
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
                block20 : switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(26);
                        switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                            case 21: {
                                fuzzySlop = this.jj_consume_token(21);
                                fuzzy = true;
                                break block20;
                            }
                        }
                        this.jj_la1[10] = this.jj_gen;
                        break;
                    }
                    default: {
                        this.jj_la1[11] = this.jj_gen;
                    }
                }
                String termImage = this.discardEscapeChar(term.image);
                if (wildcard) {
                    q = this.getWildcardQuery(field, termImage);
                    break;
                }
                if (prefix) {
                    q = this.getPrefixQuery(field, this.discardEscapeChar(term.image.substring(0, term.image.length() - 1)));
                    break;
                }
                if (fuzzy) {
                    float fms = this.fuzzyMinSim;
                    try {
                        fms = Float.valueOf(fuzzySlop.image.substring(1)).floatValue();
                    }
                    catch (Exception ignored) {
                        // empty catch block
                    }
                    if (fms < 0.0f || fms > 1.0f) {
                        throw new ParseException("Minimum similarity for a FuzzyQuery has to be between 0.0f and 1.0f !");
                    }
                    q = this.getFuzzyQuery(field, termImage, fms);
                    break;
                }
                q = this.hasNewAPI ? this.getFieldQuery(field, termImage, false) : this.getFieldQuery(field, termImage);
                break;
            }
            case 24: {
                Token goop2;
                Token goop1;
                this.jj_consume_token(24);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 30: {
                        goop1 = this.jj_consume_token(30);
                        break;
                    }
                    case 29: {
                        goop1 = this.jj_consume_token(29);
                        break;
                    }
                    default: {
                        this.jj_la1[12] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 27: {
                        this.jj_consume_token(27);
                        break;
                    }
                    default: {
                        this.jj_la1[13] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 30: {
                        goop2 = this.jj_consume_token(30);
                        break;
                    }
                    case 29: {
                        goop2 = this.jj_consume_token(29);
                        break;
                    }
                    default: {
                        this.jj_la1[14] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.jj_consume_token(28);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[15] = this.jj_gen;
                    }
                }
                boolean startOpen = false;
                boolean endOpen = false;
                if (goop1.kind == 29) {
                    goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
                } else if ("*".equals(goop1.image)) {
                    startOpen = true;
                }
                if (goop2.kind == 29) {
                    goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
                } else if ("*".equals(goop2.image)) {
                    endOpen = true;
                }
                q = this.getRangeQuery(field, startOpen ? null : this.discardEscapeChar(goop1.image), endOpen ? null : this.discardEscapeChar(goop2.image), true);
                break;
            }
            case 25: {
                Token goop2;
                Token goop1;
                this.jj_consume_token(25);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 34: {
                        goop1 = this.jj_consume_token(34);
                        break;
                    }
                    case 33: {
                        goop1 = this.jj_consume_token(33);
                        break;
                    }
                    default: {
                        this.jj_la1[16] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 31: {
                        this.jj_consume_token(31);
                        break;
                    }
                    default: {
                        this.jj_la1[17] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 34: {
                        goop2 = this.jj_consume_token(34);
                        break;
                    }
                    case 33: {
                        goop2 = this.jj_consume_token(33);
                        break;
                    }
                    default: {
                        this.jj_la1[18] = this.jj_gen;
                        this.jj_consume_token(-1);
                        throw new ParseException();
                    }
                }
                this.jj_consume_token(32);
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[19] = this.jj_gen;
                    }
                }
                boolean startOpen = false;
                boolean endOpen = false;
                if (goop1.kind == 33) {
                    goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
                } else if ("*".equals(goop1.image)) {
                    startOpen = true;
                }
                if (goop2.kind == 33) {
                    goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
                } else if ("*".equals(goop2.image)) {
                    endOpen = true;
                }
                q = this.getRangeQuery(field, startOpen ? null : this.discardEscapeChar(goop1.image), endOpen ? null : this.discardEscapeChar(goop2.image), false);
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
                        this.jj_la1[20] = this.jj_gen;
                    }
                }
                switch (this.jj_ntk == -1 ? this.jj_ntk() : this.jj_ntk) {
                    case 18: {
                        this.jj_consume_token(18);
                        boost = this.jj_consume_token(26);
                        break;
                    }
                    default: {
                        this.jj_la1[21] = this.jj_gen;
                    }
                }
                int s = this.phraseSlop;
                if (fuzzySlop != null) {
                    try {
                        s = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
                    }
                    catch (Exception ignored) {
                        // empty catch block
                    }
                }
                q = this.getFieldQuery(field, this.discardEscapeChar(term.image.substring(1, term.image.length() - 1)), s);
                break;
            }
            default: {
                this.jj_la1[22] = this.jj_gen;
                this.jj_consume_token(-1);
                throw new ParseException();
            }
        }
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image).floatValue();
            }
            catch (Exception ignored) {
                // empty catch block
            }
            if (q != null) {
                q.setBoost(f);
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
            Object var5_4 = null;
            this.jj_save(0, xla);
            return bl;
        }
        catch (LookaheadSuccess ls) {
            try {
                boolean bl = true;
                Object var5_5 = null;
                this.jj_save(0, xla);
                return bl;
            }
            catch (Throwable throwable) {
                Object var5_6 = null;
                this.jj_save(0, xla);
                throw throwable;
            }
        }
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

    private boolean jj_3R_2() {
        if (this.jj_scan_token(20)) {
            return true;
        }
        return this.jj_scan_token(16);
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{768, 768, 7168, 7168, 131759872, 0x120000, 262144, 131751936, 80879616, 0x200000, 0x200000, 262144, 0x60000000, 0x8000000, 0x60000000, 262144, 0, Integer.MIN_VALUE, 0, 262144, 0x200000, 262144, 131735552};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 6, 0, 0, 0, 0};
    }

    protected QueryParser(CharStream stream) {
        int i;
        this.token_source = new QueryParserTokenManager(stream);
        this.token = new Token();
        this.jj_ntk = -1;
        this.jj_gen = 0;
        for (i = 0; i < 23; ++i) {
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
        for (i = 0; i < 23; ++i) {
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
        for (i = 0; i < 23; ++i) {
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
        for (i = 0; i < 23; ++i) {
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
        boolean[] la1tokens = new boolean[35];
        if (this.jj_kind >= 0) {
            la1tokens[this.jj_kind] = true;
            this.jj_kind = -1;
        }
        for (i = 0; i < 23; ++i) {
            if (this.jj_la1[i] != this.jj_gen) continue;
            for (int j = 0; j < 32; ++j) {
                if ((jj_la1_0[i] & 1 << j) != 0) {
                    la1tokens[j] = true;
                }
                if ((jj_la1_1[i] & 1 << j) == 0) continue;
                la1tokens[32 + j] = true;
            }
        }
        for (i = 0; i < 35; ++i) {
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

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Operator {
        OR,
        AND;

    }
}

