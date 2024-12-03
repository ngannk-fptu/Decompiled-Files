/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 */
package org.apache.lucene.queryparser.xml;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.CoreParser;
import org.apache.lucene.queryparser.xml.builders.BooleanFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.BoostingQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.DuplicateFilterBuilder;
import org.apache.lucene.queryparser.xml.builders.FuzzyLikeThisQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.LikeThisQueryBuilder;
import org.apache.lucene.queryparser.xml.builders.TermsFilterBuilder;

public class CorePlusExtensionsParser
extends CoreParser {
    public CorePlusExtensionsParser(Analyzer analyzer, QueryParser parser) {
        this(null, analyzer, parser);
    }

    public CorePlusExtensionsParser(String defaultField, Analyzer analyzer) {
        this(defaultField, analyzer, null);
    }

    private CorePlusExtensionsParser(String defaultField, Analyzer analyzer, QueryParser parser) {
        super(defaultField, analyzer, parser);
        this.filterFactory.addBuilder("TermsFilter", new TermsFilterBuilder(analyzer));
        this.filterFactory.addBuilder("BooleanFilter", new BooleanFilterBuilder(this.filterFactory));
        this.filterFactory.addBuilder("DuplicateFilter", new DuplicateFilterBuilder());
        String[] fields = new String[]{"contents"};
        this.queryFactory.addBuilder("LikeThisQuery", new LikeThisQueryBuilder(analyzer, fields));
        this.queryFactory.addBuilder("BoostingQuery", new BoostingQueryBuilder(this.queryFactory));
        this.queryFactory.addBuilder("FuzzyLikeThisQuery", new FuzzyLikeThisQueryBuilder(analyzer));
    }
}

