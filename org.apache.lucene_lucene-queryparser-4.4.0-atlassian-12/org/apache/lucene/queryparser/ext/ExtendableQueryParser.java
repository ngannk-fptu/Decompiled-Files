/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.queryparser.ext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.ext.ExtensionQuery;
import org.apache.lucene.queryparser.ext.Extensions;
import org.apache.lucene.queryparser.ext.ParserExtension;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

public class ExtendableQueryParser
extends QueryParser {
    private final String defaultField;
    private final Extensions extensions;
    private static final Extensions DEFAULT_EXTENSION = new Extensions();

    public ExtendableQueryParser(Version matchVersion, String f, Analyzer a) {
        this(matchVersion, f, a, DEFAULT_EXTENSION);
    }

    public ExtendableQueryParser(Version matchVersion, String f, Analyzer a, Extensions ext) {
        super(matchVersion, f, a);
        this.defaultField = f;
        this.extensions = ext;
    }

    public char getExtensionFieldDelimiter() {
        return this.extensions.getExtensionFieldDelimiter();
    }

    @Override
    protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
        Extensions.Pair<String, String> splitExtensionField = this.extensions.splitExtensionField(this.defaultField, field);
        ParserExtension extension = this.extensions.getExtension((String)splitExtensionField.cud);
        if (extension != null) {
            return extension.parse(new ExtensionQuery(this, (String)splitExtensionField.cur, queryText));
        }
        return super.getFieldQuery(field, queryText, quoted);
    }
}

