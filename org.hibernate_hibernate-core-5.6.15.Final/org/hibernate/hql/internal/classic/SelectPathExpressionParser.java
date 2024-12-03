/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.PathExpressionParser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;

public class SelectPathExpressionParser
extends PathExpressionParser {
    @Override
    public void end(QueryTranslatorImpl q) throws QueryException {
        if (this.getCurrentProperty() != null && !q.isShallowQuery()) {
            this.token(".", q);
            this.token(null, q);
        }
        super.end(q);
    }

    @Override
    protected void setExpectingCollectionIndex() throws QueryException {
        throw new QueryException("illegal syntax near collection-valued path expression in select: " + this.getCollectionName());
    }

    public String getSelectName() {
        return this.getCurrentName();
    }
}

