/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.FilterBuilderFactory;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.queryparser.xml.QueryBuilderFactory;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.BoostingTermBuilder;
import org.apache.lucene.queryparser.xml.builders.CachedFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.ConstantScoreQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.DisjunctionMaxQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.FilteredQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.MatchAllDocsQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.NumericRangeFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.NumericRangeQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.RangeFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanFirstBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanNearBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanNotBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanOrBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanOrTermsBuilder;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilderFactory;
import org.apache.lucene.queryparser.xml.builders.SpanTermBuilder;
import org.apache.lucene.queryparser.xml.builders.TermQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.TermsQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.UserInputQueryBuilder;
import org.apache.lucene.search.Query;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CoreParser
implements QueryBuilder {
    private static final boolean ALLOW_EXTERNAL_ENTITY = Boolean.getBoolean("apache.lucene.allow.external.entity");
    private static final EntityResolver DISALLOW_EXTERNAL_ENTITY_RESOLVER = new EntityResolver(){

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            throw new SAXException(String.format(Locale.ENGLISH, "External Entity resolving unsupported:  publicId=\"%s\" systemId=\"%s\"", publicId, systemId));
        }
    };
    protected Analyzer analyzer;
    protected QueryParser parser;
    protected QueryBuilderFactory queryFactory;
    protected FilterBuilderFactory filterFactory;
    public static int maxNumCachedFilters = 20;

    public CoreParser(Analyzer analyzer, QueryParser parser) {
        this(null, analyzer, parser);
    }

    public CoreParser(String defaultField, Analyzer analyzer) {
        this(defaultField, analyzer, null);
    }

    protected CoreParser(String defaultField, Analyzer analyzer, QueryParser parser) {
        this.analyzer = analyzer;
        this.parser = parser;
        this.filterFactory = new FilterBuilderFactory();
        this.filterFactory.addBuilder("RangeFilter", new RangeFilterBuilder());
        this.filterFactory.addBuilder("NumericRangeFilter", new NumericRangeFilterBuilder());
        this.queryFactory = new QueryBuilderFactory();
        this.queryFactory.addBuilder("TermQuery", new TermQueryBuilder());
        this.queryFactory.addBuilder("TermsQuery", new TermsQueryBuilder(analyzer));
        this.queryFactory.addBuilder("MatchAllDocsQuery", new MatchAllDocsQueryBuilder());
        this.queryFactory.addBuilder("BooleanQuery", new BooleanQueryBuilder(this.queryFactory));
        this.queryFactory.addBuilder("NumericRangeQuery", new NumericRangeQueryBuilder());
        this.queryFactory.addBuilder("DisjunctionMaxQuery", new DisjunctionMaxQueryBuilder(this.queryFactory));
        if (parser != null) {
            this.queryFactory.addBuilder("UserQuery", new UserInputQueryBuilder(parser));
        } else {
            this.queryFactory.addBuilder("UserQuery", new UserInputQueryBuilder(defaultField, analyzer));
        }
        this.queryFactory.addBuilder("FilteredQuery", new FilteredQueryBuilder(this.filterFactory, this.queryFactory));
        this.queryFactory.addBuilder("ConstantScoreQuery", new ConstantScoreQueryBuilder(this.filterFactory));
        this.filterFactory.addBuilder("CachedFilter", new CachedFilterBuilder(this.queryFactory, this.filterFactory, maxNumCachedFilters));
        SpanQueryBuilderFactory sqof = new SpanQueryBuilderFactory();
        SpanNearBuilder snb = new SpanNearBuilder(sqof);
        sqof.addBuilder("SpanNear", snb);
        this.queryFactory.addBuilder("SpanNear", snb);
        BoostingTermBuilder btb = new BoostingTermBuilder();
        sqof.addBuilder("BoostingTermQuery", btb);
        this.queryFactory.addBuilder("BoostingTermQuery", btb);
        SpanTermBuilder snt = new SpanTermBuilder();
        sqof.addBuilder("SpanTerm", snt);
        this.queryFactory.addBuilder("SpanTerm", snt);
        SpanOrBuilder sot = new SpanOrBuilder(sqof);
        sqof.addBuilder("SpanOr", sot);
        this.queryFactory.addBuilder("SpanOr", sot);
        SpanOrTermsBuilder sots = new SpanOrTermsBuilder(analyzer);
        sqof.addBuilder("SpanOrTerms", sots);
        this.queryFactory.addBuilder("SpanOrTerms", sots);
        SpanFirstBuilder sft = new SpanFirstBuilder(sqof);
        sqof.addBuilder("SpanFirst", sft);
        this.queryFactory.addBuilder("SpanFirst", sft);
        SpanNotBuilder snot = new SpanNotBuilder(sqof);
        sqof.addBuilder("SpanNot", snot);
        this.queryFactory.addBuilder("SpanNot", snot);
    }

    public Query parse(InputStream xmlStream) throws ParserException {
        return this.getQuery(this.parseXML(xmlStream).getDocumentElement());
    }

    public void addQueryBuilder(String nodeName, QueryBuilder builder) {
        this.queryFactory.addBuilder(nodeName, builder);
    }

    public void addFilterBuilder(String nodeName, FilterBuilder builder) {
        this.filterFactory.addBuilder(nodeName, builder);
    }

    private Document parseXML(InputStream pXmlFile) throws ParserException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        try {
            dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (ParserConfigurationException parserConfigurationException) {
            // empty catch block
        }
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (Exception se) {
            throw new ParserException("XML Parser configuration error", se);
        }
        Document doc = null;
        try {
            db.setEntityResolver(this.getEntityResolver());
            doc = db.parse(pXmlFile);
        }
        catch (Exception se) {
            throw new ParserException("Error parsing XML stream:" + se, se);
        }
        return doc;
    }

    private EntityResolver getEntityResolver() {
        return ALLOW_EXTERNAL_ENTITY ? null : DISALLOW_EXTERNAL_ENTITY_RESOLVER;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        return this.queryFactory.getQuery(e);
    }
}

