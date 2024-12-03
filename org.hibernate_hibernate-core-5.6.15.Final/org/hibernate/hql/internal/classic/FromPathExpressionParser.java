/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.classic;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.classic.PathExpressionParser;
import org.hibernate.hql.internal.classic.QueryTranslatorImpl;
import org.hibernate.type.Type;

public class FromPathExpressionParser
extends PathExpressionParser {
    @Override
    public void end(QueryTranslatorImpl q) throws QueryException {
        if (!this.isCollectionValued()) {
            Type type = this.getPropertyType();
            if (type.isEntityType()) {
                this.token(".", q);
                this.token(null, q);
            } else if (type.isCollectionType()) {
                this.token(".", q);
                this.token("elements", q);
            }
        }
        super.end(q);
    }

    @Override
    protected void setExpectingCollectionIndex() throws QueryException {
        throw new QueryException("illegal syntax near collection-valued path expression in from: " + this.getCollectionName());
    }
}

