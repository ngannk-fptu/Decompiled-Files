/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParser;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleNode;

public class ASTIdentifier
extends SimpleNode {
    private Name name;

    public ASTIdentifier(int id) {
        super(id);
    }

    public ASTIdentifier(JCRSQLParser p, int id) {
        super(p, id);
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Name getName() {
        return this.name;
    }

    @Override
    public Object jjtAccept(JCRSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String toString() {
        return super.toString() + ": " + this.name;
    }
}

