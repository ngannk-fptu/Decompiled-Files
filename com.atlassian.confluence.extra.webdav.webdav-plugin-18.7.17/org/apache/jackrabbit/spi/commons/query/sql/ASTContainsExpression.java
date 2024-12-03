/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParser;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleNode;

public class ASTContainsExpression
extends SimpleNode {
    private String query;
    private Name property;

    public ASTContainsExpression(int id) {
        super(id);
    }

    public ASTContainsExpression(JCRSQLParser p, int id) {
        super(p, id);
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Name getPropertyName() {
        return this.property;
    }

    public void setPropertyName(Name property) {
        this.property = property;
    }

    @Override
    public Object jjtAccept(JCRSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

