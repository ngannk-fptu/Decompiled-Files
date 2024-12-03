/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.w3c.dom.Element;

public class UserInputQueryBuilder
implements QueryBuilder {
    private QueryParser unSafeParser;
    private Analyzer analyzer;
    private String defaultField;

    public UserInputQueryBuilder(QueryParser parser) {
        this.unSafeParser = parser;
    }

    public UserInputQueryBuilder(String defaultField, Analyzer analyzer) {
        this.analyzer = analyzer;
        this.defaultField = defaultField;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Query getQuery(Element e) throws ParserException {
        String text = DOMUtils.getText(e);
        try {
            Query q = null;
            if (this.unSafeParser != null) {
                QueryParser queryParser = this.unSafeParser;
                synchronized (queryParser) {
                    q = this.unSafeParser.parse(text);
                }
            } else {
                String fieldName = DOMUtils.getAttribute(e, "fieldName", this.defaultField);
                QueryParser parser = this.createQueryParser(fieldName, this.analyzer);
                q = parser.parse(text);
            }
            q.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
            return q;
        }
        catch (ParseException e1) {
            throw new ParserException(e1.getMessage());
        }
    }

    protected QueryParser createQueryParser(String fieldName, Analyzer analyzer) {
        return new QueryParser(Version.LUCENE_CURRENT, fieldName, analyzer);
    }
}

