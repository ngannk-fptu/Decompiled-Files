/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

public class InlineIdsOrClauseBuilder
extends IdsClauseBuilder {
    public InlineIdsOrClauseBuilder(Dialect dialect, Type identifierType, TypeResolver typeResolver, String[] columns, List<Object[]> ids) {
        super(dialect, identifierType, typeResolver, columns, ids);
    }

    @Override
    public String toStatement() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < this.getIds().size(); ++i) {
            Object[] idTokens = this.getIds().get(i);
            if (i > 0) {
                buffer.append(" or ");
            }
            buffer.append("(");
            for (int j = 0; j < this.getColumns().length; ++j) {
                if (j > 0) {
                    buffer.append(" and ");
                }
                buffer.append(this.getColumns()[j]);
                buffer.append(" = ");
                buffer.append(this.quoteIdentifier(idTokens[j]));
            }
            buffer.append(")");
        }
        return buffer.toString();
    }
}

