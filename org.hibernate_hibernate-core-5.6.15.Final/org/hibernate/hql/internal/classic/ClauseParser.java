/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.FromParser;
import org.hibernate.hql.internal.classic.GroupByParser;
import org.hibernate.hql.internal.classic.HavingParser;
import org.hibernate.hql.internal.classic.OrderByParser;
import org.hibernate.hql.internal.classic.Parser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.hql.internal.classic.SelectParser;
import org.hibernate.hql.internal.classic.WhereParser;

public class ClauseParser
implements Parser {
    private Parser child;
    private List<String> selectTokens;
    private boolean cacheSelectTokens;
    private boolean byExpected;
    private int parenCount;

    @Override
    public void token(String token, QueryTranslatorImpl q) throws QueryException {
        boolean isClauseStart;
        String lcToken = token.toLowerCase(Locale.ROOT);
        if ("(".equals(token)) {
            ++this.parenCount;
        } else if (")".equals(token)) {
            --this.parenCount;
        }
        if (this.byExpected && !lcToken.equals("by")) {
            throw new QueryException("BY expected after GROUP or ORDER: " + token);
        }
        boolean bl = isClauseStart = this.parenCount == 0;
        if (isClauseStart) {
            if (lcToken.equals("select")) {
                this.selectTokens = new ArrayList<String>();
                this.cacheSelectTokens = true;
            } else if (lcToken.equals("from")) {
                this.child = new FromParser();
                this.child.start(q);
                this.cacheSelectTokens = false;
            } else if (lcToken.equals("where")) {
                this.endChild(q);
                this.child = new WhereParser();
                this.child.start(q);
            } else if (lcToken.equals("order")) {
                this.endChild(q);
                this.child = new OrderByParser();
                this.byExpected = true;
            } else if (lcToken.equals("having")) {
                this.endChild(q);
                this.child = new HavingParser();
                this.child.start(q);
            } else if (lcToken.equals("group")) {
                this.endChild(q);
                this.child = new GroupByParser();
                this.byExpected = true;
            } else if (lcToken.equals("by")) {
                if (!this.byExpected) {
                    throw new QueryException("GROUP or ORDER expected before BY");
                }
                this.child.start(q);
                this.byExpected = false;
            } else {
                isClauseStart = false;
            }
        }
        if (!isClauseStart) {
            if (this.cacheSelectTokens) {
                this.selectTokens.add(token);
            } else {
                if (this.child == null) {
                    throw new QueryException("query must begin with SELECT or FROM: " + token);
                }
                this.child.token(token, q);
            }
        }
    }

    private void endChild(QueryTranslatorImpl q) throws QueryException {
        if (this.child == null) {
            this.cacheSelectTokens = false;
        } else {
            this.child.end(q);
        }
    }

    @Override
    public void start(QueryTranslatorImpl q) {
    }

    @Override
    public void end(QueryTranslatorImpl q) throws QueryException {
        this.endChild(q);
        if (this.selectTokens != null) {
            this.child = new SelectParser();
            this.child.start(q);
            for (String selectToken : this.selectTokens) {
                this.token(selectToken, q);
            }
            this.child.end(q);
        }
        this.byExpected = false;
        this.parenCount = 0;
        this.cacheSelectTokens = false;
    }
}

