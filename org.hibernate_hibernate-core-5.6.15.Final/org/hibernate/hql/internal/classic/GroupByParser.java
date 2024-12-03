/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.Parser;
import org.hibernate.hql.internal.classic.ParserHelper;
import org.hibernate.hql.internal.classic.PathExpressionParser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.internal.util.StringHelper;

public class GroupByParser
implements Parser {
    private final PathExpressionParser pathExpressionParser = new PathExpressionParser();

    public GroupByParser() {
        this.pathExpressionParser.setUseThetaStyleJoin(true);
    }

    @Override
    public void token(String token, QueryTranslatorImpl q) throws QueryException {
        if (q.isName(StringHelper.root(token))) {
            ParserHelper.parse(this.pathExpressionParser, q.unalias(token), ".", q);
            q.appendGroupByToken(this.pathExpressionParser.getWhereColumn());
            this.pathExpressionParser.addAssociation(q);
        } else {
            q.appendGroupByToken(token);
        }
    }

    @Override
    public void start(QueryTranslatorImpl q) throws QueryException {
    }

    @Override
    public void end(QueryTranslatorImpl q) throws QueryException {
    }
}

