/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrMatcher
 *  org.apache.commons.lang3.text.StrTokenizer
 */
package com.atlassian.confluence.macro.query;

import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.macro.query.InclusionCriteria;
import com.atlassian.confluence.macro.query.SearchQueryInterpreter;
import com.atlassian.confluence.macro.query.SearchQueryInterpreterException;
import com.atlassian.confluence.macro.query.SearchQueryParserException;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.text.StrMatcher;
import org.apache.commons.lang3.text.StrTokenizer;

public final class SimpleSearchQueryParser {
    private static final char DELIMITER_COMMA = ',';
    private static final char DELIMITER_SPACE = ' ';
    private static final StrMatcher DELIMITER_CHARS = StrMatcher.charSetMatcher((char[])new char[]{',', ' '});
    private final Set<SearchQuery> must;
    private final Set<SearchQuery> should;
    private final Set<SearchQuery> mustNot;
    private final SearchQueryInterpreter interpreter;
    private InclusionCriteria defaultInclusionCriteria;

    public SimpleSearchQueryParser(SearchQueryInterpreter interpreter) {
        this(interpreter, InclusionCriteria.ANY);
    }

    public SimpleSearchQueryParser(SearchQueryInterpreter interpreter, InclusionCriteria defaultInclusionCriteria) {
        this.interpreter = interpreter;
        this.defaultInclusionCriteria = defaultInclusionCriteria;
        this.must = new HashSet<SearchQuery>();
        this.should = new HashSet<SearchQuery>();
        this.mustNot = new HashSet<SearchQuery>();
    }

    public void setDefaultInclusionCriteria(InclusionCriteria criteria) {
        this.defaultInclusionCriteria = criteria;
    }

    public BooleanQueryFactory parse(String query) throws SearchQueryParserException {
        StrTokenizer parser = new StrTokenizer(query, DELIMITER_CHARS);
        parser.setIgnoredMatcher(StrMatcher.trimMatcher());
        parser.setIgnoreEmptyTokens(true);
        while (parser.hasNext()) {
            InclusionCriteria nextValueState = this.defaultInclusionCriteria;
            String nextToken = parser.nextToken();
            InclusionCriteria value = InclusionCriteria.get(nextToken.substring(0, 1));
            if (value != null) {
                nextValueState = value;
                nextToken = nextToken.substring(1);
            }
            this.insertValue(nextToken, nextValueState);
        }
        if (this.must.isEmpty() && this.should.isEmpty() && this.mustNot.isEmpty()) {
            throw new SearchQueryParserException("Invalid query: '" + query + "'");
        }
        return new BooleanQueryFactory(this.must, this.should, this.mustNot);
    }

    private void insertValue(String token, InclusionCriteria state) throws SearchQueryParserException {
        try {
            SearchQuery query = this.interpreter.createSearchQuery(token);
            switch (state) {
                case ANY: {
                    this.should.add(query);
                    break;
                }
                case ALL: {
                    this.must.add(query);
                    break;
                }
                case NONE: {
                    this.mustNot.add(query);
                    break;
                }
                default: {
                    throw new SearchQueryParserException("unknown InclusionCriteria state: " + state.getToken());
                }
            }
        }
        catch (SearchQueryInterpreterException sqie) {
            throw new SearchQueryParserException(sqie.getMessage());
        }
    }
}

