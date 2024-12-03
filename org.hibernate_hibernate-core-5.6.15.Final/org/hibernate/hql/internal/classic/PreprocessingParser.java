/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.CollectionProperties;
import org.hibernate.hql.internal.classic.ClauseParser;
import org.hibernate.hql.internal.classic.Parser;
import org.hibernate.hql.internal.classic.ParserHelper;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.internal.util.StringHelper;

public class PreprocessingParser
implements Parser {
    private static final Set<String> HQL_OPERATORS = new HashSet<String>();
    private Map replacements;
    private boolean quoted;
    private StringBuilder quotedString;
    private ClauseParser parser = new ClauseParser();
    private String lastToken;
    private String currentCollectionProp;

    public PreprocessingParser(Map replacements) {
        this.replacements = replacements;
    }

    @Override
    public void token(String token, QueryTranslatorImpl q) throws QueryException {
        if (this.quoted) {
            this.quotedString.append(token);
        }
        if ("'".equals(token)) {
            if (this.quoted) {
                token = this.quotedString.toString();
            } else {
                this.quotedString = new StringBuilder(20).append(token);
            }
            boolean bl = this.quoted = !this.quoted;
        }
        if (this.quoted) {
            return;
        }
        if (ParserHelper.isWhitespace(token)) {
            return;
        }
        String substoken = (String)this.replacements.get(token);
        String string = token = substoken == null ? token : substoken;
        if (this.currentCollectionProp != null) {
            if ("(".equals(token)) {
                return;
            }
            if (")".equals(token)) {
                this.currentCollectionProp = null;
                return;
            }
            token = StringHelper.qualify(token, this.currentCollectionProp);
        } else {
            String prop = CollectionProperties.getNormalizedPropertyName(token.toLowerCase(Locale.ROOT));
            if (prop != null) {
                this.currentCollectionProp = prop;
                return;
            }
        }
        if (this.lastToken == null) {
            this.lastToken = token;
        } else {
            String doubleToken;
            String string2 = doubleToken = token.length() > 1 ? this.lastToken + ' ' + token : this.lastToken + token;
            if (HQL_OPERATORS.contains(doubleToken.toLowerCase(Locale.ROOT))) {
                this.parser.token(doubleToken, q);
                this.lastToken = null;
            } else {
                this.parser.token(this.lastToken, q);
                this.lastToken = token;
            }
        }
    }

    @Override
    public void start(QueryTranslatorImpl q) throws QueryException {
        this.quoted = false;
        this.parser.start(q);
    }

    @Override
    public void end(QueryTranslatorImpl q) throws QueryException {
        if (this.lastToken != null) {
            this.parser.token(this.lastToken, q);
        }
        this.parser.end(q);
        this.lastToken = null;
        this.currentCollectionProp = null;
    }

    static {
        HQL_OPERATORS.add("<=");
        HQL_OPERATORS.add(">=");
        HQL_OPERATORS.add("=>");
        HQL_OPERATORS.add("=<");
        HQL_OPERATORS.add("!=");
        HQL_OPERATORS.add("<>");
        HQL_OPERATORS.add("!#");
        HQL_OPERATORS.add("!~");
        HQL_OPERATORS.add("!<");
        HQL_OPERATORS.add("!>");
        HQL_OPERATORS.add("is not");
        HQL_OPERATORS.add("not like");
        HQL_OPERATORS.add("not in");
        HQL_OPERATORS.add("not between");
        HQL_OPERATORS.add("not exists");
    }
}

