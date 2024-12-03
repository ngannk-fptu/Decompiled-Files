/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.Locale;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.Parser;
import org.hibernate.hql.internal.classic.ParserHelper;
import org.hibernate.hql.internal.classic.PathExpressionParser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.internal.util.StringHelper;

public class OrderByParser
implements Parser {
    private final PathExpressionParser pathExpressionParser = new PathExpressionParser();

    public OrderByParser() {
        this.pathExpressionParser.setUseThetaStyleJoin(true);
    }

    @Override
    public void token(String token, QueryTranslatorImpl q) throws QueryException {
        if (q.isName(StringHelper.root(token))) {
            ParserHelper.parse(this.pathExpressionParser, q.unalias(token), ".", q);
            q.appendOrderByToken(this.pathExpressionParser.getWhereColumn());
            this.pathExpressionParser.addAssociation(q);
        } else if (token.startsWith(":")) {
            q.addNamedParameter(token.substring(1));
            q.appendOrderByToken("?");
        } else if (token.startsWith("?")) {
            if (token.length() == 1) {
                throw new QueryException(String.format(Locale.ROOT, "Legacy-style query parameters (`?`) are no longer supported; use JPA-style ordinal parameters (e.g., `?1`) instead : %s", q.getQueryString()));
            }
            String labelString = token.substring(1);
            try {
                int label = Integer.parseInt(labelString);
                q.addOrdinalParameter(label);
                q.appendOrderByToken("?");
            }
            catch (NumberFormatException e) {
                throw new QueryException("Ordinal parameter label must be numeric : " + labelString, e);
            }
        } else {
            q.appendOrderByToken(token);
        }
    }

    @Override
    public void start(QueryTranslatorImpl q) throws QueryException {
    }

    @Override
    public void end(QueryTranslatorImpl q) throws QueryException {
    }
}

