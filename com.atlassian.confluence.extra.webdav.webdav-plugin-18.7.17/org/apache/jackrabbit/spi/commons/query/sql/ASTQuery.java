/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParser;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleNode;

public class ASTQuery
extends SimpleNode {
    public ASTQuery(int id) {
        super(id);
    }

    public ASTQuery(JCRSQLParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JCRSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

