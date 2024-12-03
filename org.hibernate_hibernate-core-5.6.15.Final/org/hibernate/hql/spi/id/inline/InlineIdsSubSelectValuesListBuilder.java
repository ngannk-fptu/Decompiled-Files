/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

public class InlineIdsSubSelectValuesListBuilder
extends IdsClauseBuilder {
    public InlineIdsSubSelectValuesListBuilder(Dialect dialect, Type identifierType, TypeResolver typeResolver, String[] columns, List<Object[]> ids) {
        super(dialect, identifierType, typeResolver, columns, ids);
    }

    @Override
    public String toStatement() {
        StringBuilder buffer = new StringBuilder();
        String columnNames = String.join((CharSequence)",", this.getColumns());
        buffer.append("select ").append(columnNames).append(" from ( values ");
        for (int i = 0; i < this.getIds().size(); ++i) {
            Object[] idTokens = this.getIds().get(i);
            if (i > 0) {
                buffer.append(",");
            }
            buffer.append("(");
            buffer.append(this.quoteIdentifier(idTokens));
            buffer.append(")");
        }
        buffer.append(") as HT (").append(columnNames).append(") ");
        return buffer.toString();
    }
}

