/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.spi.id.inline;

import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.hql.spi.id.inline.IdsClauseBuilder;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

public class InlineIdsInClauseBuilder
extends IdsClauseBuilder {
    private final int chunkLimit;

    public InlineIdsInClauseBuilder(Dialect dialect, Type identifierType, TypeResolver typeResolver, String[] columns, List<Object[]> ids) {
        super(dialect, identifierType, typeResolver, columns, ids);
        this.chunkLimit = dialect.getInExpressionCountLimit();
    }

    @Override
    public String toStatement() {
        StringBuilder buffer = new StringBuilder();
        String columnNames = String.join((CharSequence)",", this.getColumns());
        for (int i = 0; i < this.getIds().size(); ++i) {
            Object[] idTokens = this.getIds().get(i);
            if (i > 0) {
                if (this.chunkLimit > 0 && i % this.chunkLimit == 0) {
                    buffer.append(" ) or ( ");
                    buffer.append(columnNames);
                    buffer.append(" ) in (");
                } else {
                    buffer.append(",");
                }
            }
            buffer.append("(");
            buffer.append(this.quoteIdentifier(idTokens));
            buffer.append(")");
        }
        return buffer.toString();
    }
}

