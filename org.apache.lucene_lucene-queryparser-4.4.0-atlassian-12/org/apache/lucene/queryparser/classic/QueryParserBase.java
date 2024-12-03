/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.CachingTokenFilter
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute
 *  org.apache.lucene.document.DateTools
 *  org.apache.lucene.document.DateTools$Resolution
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.BooleanQuery$TooManyClauses
 *  org.apache.lucene.search.FuzzyQuery
 *  org.apache.lucene.search.MatchAllDocsQuery
 *  org.apache.lucene.search.MultiPhraseQuery
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.PhraseQuery
 *  org.apache.lucene.search.PrefixQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.RegexpQuery
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.search.TermRangeQuery
 *  org.apache.lucene.search.WildcardQuery
 *  org.apache.lucene.util.BytesRef
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.queryparser.classic;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.CharStream;
import org.apache.lucene.queryparser.classic.FastCharStream;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.Token;
import org.apache.lucene.queryparser.classic.TokenMgrError;
import org.apache.lucene.queryparser.flexible.standard.CommonQueryParserConfiguration;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public abstract class QueryParserBase
implements CommonQueryParserConfiguration {
    static final int CONJ_NONE = 0;
    static final int CONJ_AND = 1;
    static final int CONJ_OR = 2;
    static final int MOD_NONE = 0;
    static final int MOD_NOT = 10;
    static final int MOD_REQ = 11;
    public static final QueryParser.Operator AND_OPERATOR = QueryParser.Operator.AND;
    public static final QueryParser.Operator OR_OPERATOR = QueryParser.Operator.OR;
    QueryParser.Operator operator = OR_OPERATOR;
    boolean lowercaseExpandedTerms = true;
    MultiTermQuery.RewriteMethod multiTermRewriteMethod = MultiTermQuery.CONSTANT_SCORE_AUTO_REWRITE_DEFAULT;
    boolean allowLeadingWildcard = false;
    boolean enablePositionIncrements = true;
    Analyzer analyzer;
    String field;
    int phraseSlop = 0;
    float fuzzyMinSim = 2.0f;
    int fuzzyPrefixLength = 0;
    Locale locale = Locale.getDefault();
    TimeZone timeZone = TimeZone.getDefault();
    DateTools.Resolution dateResolution = null;
    Map<String, DateTools.Resolution> fieldToDateResolution = null;
    boolean analyzeRangeTerms = false;
    boolean autoGeneratePhraseQueries;

    protected QueryParserBase() {
    }

    public void init(Version matchVersion, String f, Analyzer a) {
        this.analyzer = a;
        this.field = f;
        if (matchVersion.onOrAfter(Version.LUCENE_31)) {
            this.setAutoGeneratePhraseQueries(false);
        } else {
            this.setAutoGeneratePhraseQueries(true);
        }
    }

    public abstract void ReInit(CharStream var1);

    public abstract Query TopLevelQuery(String var1) throws ParseException;

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

    @Override
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
        this.autoGeneratePhraseQueries = value;
    }

    @Override
    public float getFuzzyMinSim() {
        return this.fuzzyMinSim;
    }

    @Override
    public void setFuzzyMinSim(float fuzzyMinSim) {
        this.fuzzyMinSim = fuzzyMinSim;
    }

    @Override
    public int getFuzzyPrefixLength() {
        return this.fuzzyPrefixLength;
    }

    @Override
    public void setFuzzyPrefixLength(int fuzzyPrefixLength) {
        this.fuzzyPrefixLength = fuzzyPrefixLength;
    }

    @Override
    public void setPhraseSlop(int phraseSlop) {
        this.phraseSlop = phraseSlop;
    }

    @Override
    public int getPhraseSlop() {
        return this.phraseSlop;
    }

    @Override
    public void setAllowLeadingWildcard(boolean allowLeadingWildcard) {
        this.allowLeadingWildcard = allowLeadingWildcard;
    }

    @Override
    public boolean getAllowLeadingWildcard() {
        return this.allowLeadingWildcard;
    }

    @Override
    public void setEnablePositionIncrements(boolean enable) {
        this.enablePositionIncrements = enable;
    }

    @Override
    public boolean getEnablePositionIncrements() {
        return this.enablePositionIncrements;
    }

    public void setDefaultOperator(QueryParser.Operator op) {
        this.operator = op;
    }

    public QueryParser.Operator getDefaultOperator() {
        return this.operator;
    }

    @Override
    public void setLowercaseExpandedTerms(boolean lowercaseExpandedTerms) {
        this.lowercaseExpandedTerms = lowercaseExpandedTerms;
    }

    @Override
    public boolean getLowercaseExpandedTerms() {
        return this.lowercaseExpandedTerms;
    }

    @Override
    public void setMultiTermRewriteMethod(MultiTermQuery.RewriteMethod method) {
        this.multiTermRewriteMethod = method;
    }

    @Override
    public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod() {
        return this.multiTermRewriteMethod;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override
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

    public void setAnalyzeRangeTerms(boolean analyzeRangeTerms) {
        this.analyzeRangeTerms = analyzeRangeTerms;
    }

    public boolean getAnalyzeRangeTerms() {
        return this.analyzeRangeTerms;
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

    protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
        return this.newFieldQuery(this.analyzer, field, queryText, quoted);
    }

    protected Query newFieldQuery(Analyzer analyzer, String field, String queryText, boolean quoted) throws ParseException {
        BytesRef bytes;
        TokenStream source;
        try {
            source = analyzer.tokenStream(field, queryText);
            source.reset();
        }
        catch (IOException e) {
            ParseException p = new ParseException("Unable to initialize TokenStream to analyze query text");
            p.initCause(e);
            throw p;
        }
        CachingTokenFilter buffer = new CachingTokenFilter(source);
        TermToBytesRefAttribute termAtt = null;
        PositionIncrementAttribute posIncrAtt = null;
        int numTokens = 0;
        buffer.reset();
        if (buffer.hasAttribute(TermToBytesRefAttribute.class)) {
            termAtt = (TermToBytesRefAttribute)buffer.getAttribute(TermToBytesRefAttribute.class);
        }
        if (buffer.hasAttribute(PositionIncrementAttribute.class)) {
            posIncrAtt = (PositionIncrementAttribute)buffer.getAttribute(PositionIncrementAttribute.class);
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
            catch (IOException positionIncrement) {
                // empty catch block
            }
        }
        try {
            buffer.reset();
            source.close();
        }
        catch (IOException e) {
            ParseException p = new ParseException("Cannot close TokenStream analyzing query text");
            p.initCause(e);
            throw p;
        }
        BytesRef bytesRef = bytes = termAtt == null ? null : termAtt.getBytesRef();
        if (numTokens == 0) {
            return null;
        }
        if (numTokens == 1) {
            try {
                boolean hasNext = buffer.incrementToken();
                assert (hasNext);
                termAtt.fillBytesRef();
            }
            catch (IOException hasNext) {
                // empty catch block
            }
            return this.newTermQuery(new Term(field, BytesRef.deepCopyOf((BytesRef)bytes)));
        }
        if (severalTokensAtSamePosition || !quoted && !this.autoGeneratePhraseQueries) {
            if (positionCount == 1 || !quoted && !this.autoGeneratePhraseQueries) {
                if (positionCount == 1) {
                    BooleanQuery q = this.newBooleanQuery(true);
                    for (int i = 0; i < numTokens; ++i) {
                        try {
                            boolean hasNext = buffer.incrementToken();
                            assert (hasNext);
                            termAtt.fillBytesRef();
                        }
                        catch (IOException hasNext) {
                            // empty catch block
                        }
                        Query currentQuery = this.newTermQuery(new Term(field, BytesRef.deepCopyOf((BytesRef)bytes)));
                        q.add(currentQuery, BooleanClause.Occur.SHOULD);
                    }
                    return q;
                }
                BooleanQuery q = this.newBooleanQuery(false);
                BooleanClause.Occur occur = this.operator == QueryParser.Operator.AND ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD;
                Query currentQuery = null;
                for (int i = 0; i < numTokens; ++i) {
                    try {
                        boolean hasNext = buffer.incrementToken();
                        assert (hasNext);
                        termAtt.fillBytesRef();
                    }
                    catch (IOException hasNext) {
                        // empty catch block
                    }
                    if (posIncrAtt != null && posIncrAtt.getPositionIncrement() == 0) {
                        if (!(currentQuery instanceof BooleanQuery)) {
                            Query t = currentQuery;
                            currentQuery = this.newBooleanQuery(true);
                            ((BooleanQuery)currentQuery).add(t, BooleanClause.Occur.SHOULD);
                        }
                        ((BooleanQuery)currentQuery).add(this.newTermQuery(new Term(field, BytesRef.deepCopyOf((BytesRef)bytes))), BooleanClause.Occur.SHOULD);
                        continue;
                    }
                    if (currentQuery != null) {
                        q.add(currentQuery, occur);
                    }
                    currentQuery = this.newTermQuery(new Term(field, BytesRef.deepCopyOf((BytesRef)bytes)));
                }
                q.add(currentQuery, occur);
                return q;
            }
            MultiPhraseQuery mpq = this.newMultiPhraseQuery();
            mpq.setSlop(this.phraseSlop);
            ArrayList<Term> multiTerms = new ArrayList<Term>();
            int position = -1;
            for (int i = 0; i < numTokens; ++i) {
                int positionIncrement = 1;
                try {
                    boolean hasNext = buffer.incrementToken();
                    assert (hasNext);
                    termAtt.fillBytesRef();
                    if (posIncrAtt != null) {
                        positionIncrement = posIncrAtt.getPositionIncrement();
                    }
                }
                catch (IOException iOException) {
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
                multiTerms.add(new Term(field, BytesRef.deepCopyOf((BytesRef)bytes)));
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
            int positionIncrement = 1;
            try {
                boolean hasNext = buffer.incrementToken();
                assert (hasNext);
                termAtt.fillBytesRef();
                if (posIncrAtt != null) {
                    positionIncrement = posIncrAtt.getPositionIncrement();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (this.enablePositionIncrements) {
                pq.add(new Term(field, BytesRef.deepCopyOf((BytesRef)bytes)), position += positionIncrement);
                continue;
            }
            pq.add(new Term(field, BytesRef.deepCopyOf((BytesRef)bytes)));
        }
        return pq;
    }

    protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {
        Query query = this.getFieldQuery(field, queryText, true);
        if (query instanceof PhraseQuery) {
            ((PhraseQuery)query).setSlop(slop);
        }
        if (query instanceof MultiPhraseQuery) {
            ((MultiPhraseQuery)query).setSlop(slop);
        }
        return query;
    }

    protected Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            part1 = part1 == null ? null : part1.toLowerCase(this.locale);
            part2 = part2 == null ? null : part2.toLowerCase(this.locale);
        }
        DateFormat df = DateFormat.getDateInstance(3, this.locale);
        df.setLenient(true);
        DateTools.Resolution resolution = this.getDateResolution(field);
        try {
            part1 = DateTools.dateToString((Date)df.parse(part1), (DateTools.Resolution)resolution);
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            Date d2 = df.parse(part2);
            if (endInclusive) {
                Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
                cal.setTime(d2);
                cal.set(11, 23);
                cal.set(12, 59);
                cal.set(13, 59);
                cal.set(14, 999);
                d2 = cal.getTime();
            }
            part2 = DateTools.dateToString((Date)d2, (DateTools.Resolution)resolution);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return this.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
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

    protected Query newRegexpQuery(Term regexp) {
        RegexpQuery query = new RegexpQuery(regexp);
        query.setRewriteMethod(this.multiTermRewriteMethod);
        return query;
    }

    protected Query newFuzzyQuery(Term term, float minimumSimilarity, int prefixLength) {
        String text = term.text();
        int numEdits = FuzzyQuery.floatToEdits((float)minimumSimilarity, (int)text.codePointCount(0, text.length()));
        return new FuzzyQuery(term, numEdits, prefixLength);
    }

    private BytesRef analyzeMultitermTerm(String field, String part) {
        return this.analyzeMultitermTerm(field, part, this.analyzer);
    }

    protected BytesRef analyzeMultitermTerm(String field, String part, Analyzer analyzerIn) {
        TokenStream source;
        if (analyzerIn == null) {
            analyzerIn = this.analyzer;
        }
        try {
            source = analyzerIn.tokenStream(field, part);
            source.reset();
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to initialize TokenStream to analyze multiTerm term: " + part, e);
        }
        TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)source.getAttribute(TermToBytesRefAttribute.class);
        BytesRef bytes = termAtt.getBytesRef();
        try {
            if (!source.incrementToken()) {
                throw new IllegalArgumentException("analyzer returned no terms for multiTerm term: " + part);
            }
            termAtt.fillBytesRef();
            if (source.incrementToken()) {
                throw new IllegalArgumentException("analyzer returned too many terms for multiTerm term: " + part);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("error analyzing range part: " + part, e);
        }
        try {
            source.end();
            source.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to end & close TokenStream after analyzing multiTerm term: " + part, e);
        }
        return BytesRef.deepCopyOf((BytesRef)bytes);
    }

    protected Query newRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) {
        BytesRef start;
        if (part1 == null) {
            start = null;
        } else {
            BytesRef bytesRef = start = this.analyzeRangeTerms ? this.analyzeMultitermTerm(field, part1) : new BytesRef((CharSequence)part1);
        }
        BytesRef end = part2 == null ? null : (this.analyzeRangeTerms ? this.analyzeMultitermTerm(field, part2) : new BytesRef((CharSequence)part2));
        TermRangeQuery query = new TermRangeQuery(field, start, end, startInclusive, endInclusive);
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
            termStr = termStr.toLowerCase(this.locale);
        }
        Term t = new Term(field, termStr);
        return this.newWildcardQuery(t);
    }

    protected Query getRegexpQuery(String field, String termStr) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase(this.locale);
        }
        Term t = new Term(field, termStr);
        return this.newRegexpQuery(t);
    }

    protected Query getPrefixQuery(String field, String termStr) throws ParseException {
        if (!this.allowLeadingWildcard && termStr.startsWith("*")) {
            throw new ParseException("'*' not allowed as first character in PrefixQuery");
        }
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase(this.locale);
        }
        Term t = new Term(field, termStr);
        return this.newPrefixQuery(t);
    }

    protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException {
        if (this.lowercaseExpandedTerms) {
            termStr = termStr.toLowerCase(this.locale);
        }
        Term t = new Term(field, termStr);
        return this.newFuzzyQuery(t, minSimilarity, this.fuzzyPrefixLength);
    }

    Query handleBareTokenQuery(String qfield, Token term, Token fuzzySlop, boolean prefix, boolean wildcard, boolean fuzzy, boolean regexp) throws ParseException {
        String termImage = this.discardEscapeChar(term.image);
        Query q = wildcard ? this.getWildcardQuery(qfield, term.image) : (prefix ? this.getPrefixQuery(qfield, this.discardEscapeChar(term.image.substring(0, term.image.length() - 1))) : (regexp ? this.getRegexpQuery(qfield, term.image.substring(1, term.image.length() - 1)) : (fuzzy ? this.handleBareFuzzy(qfield, fuzzySlop, termImage) : this.getFieldQuery(qfield, termImage, false))));
        return q;
    }

    Query handleBareFuzzy(String qfield, Token fuzzySlop, String termImage) throws ParseException {
        float fms = this.fuzzyMinSim;
        try {
            fms = Float.valueOf(fuzzySlop.image.substring(1)).floatValue();
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (fms < 0.0f) {
            throw new ParseException("Minimum similarity for a FuzzyQuery has to be between 0.0f and 1.0f !");
        }
        if (fms >= 1.0f && fms != (float)((int)fms)) {
            throw new ParseException("Fractional edit distances are not allowed!");
        }
        Query q = this.getFuzzyQuery(qfield, termImage, fms);
        return q;
    }

    Query handleQuotedTerm(String qfield, Token term, Token fuzzySlop) throws ParseException {
        int s = this.phraseSlop;
        if (fuzzySlop != null) {
            try {
                s = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.getFieldQuery(qfield, this.discardEscapeChar(term.image.substring(1, term.image.length() - 1)), s);
    }

    Query handleBoost(Query q, Token boost) {
        if (boost != null) {
            float f = 1.0f;
            try {
                f = Float.valueOf(boost.image).floatValue();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (q != null) {
                q.setBoost(f);
            }
        }
        return q;
    }

    String discardEscapeChar(String input) throws ParseException {
        char[] output = new char[input.length()];
        int length = 0;
        boolean lastCharWasEscapeChar = false;
        int codePointMultiplier = 0;
        int codePoint = 0;
        for (int i = 0; i < input.length(); ++i) {
            char curChar = input.charAt(i);
            if (codePointMultiplier > 0) {
                codePoint += QueryParserBase.hexToInt(curChar) * codePointMultiplier;
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

    static final int hexToInt(char c) throws ParseException {
        if ('0' <= c && c <= '9') {
            return c - 48;
        }
        if ('a' <= c && c <= 'f') {
            return c - 97 + 10;
        }
        if ('A' <= c && c <= 'F') {
            return c - 65 + 10;
        }
        throw new ParseException("Non-hex character in Unicode escape sequence: " + c);
    }

    public static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static class MethodRemovedUseAnother
    extends Throwable {
    }
}

