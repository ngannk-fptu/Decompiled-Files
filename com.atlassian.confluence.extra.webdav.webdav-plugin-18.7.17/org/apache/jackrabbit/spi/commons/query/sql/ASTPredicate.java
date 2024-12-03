/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParser;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleNode;

public class ASTPredicate
extends SimpleNode {
    private int operationType;
    private boolean negate = false;
    private Name identifier;
    private String identifierOperand;
    private String escapeString;

    public ASTPredicate(int id) {
        super(id);
    }

    public ASTPredicate(JCRSQLParser p, int id) {
        super(p, id);
    }

    public void setOperationType(int type) {
        this.operationType = type;
    }

    public int getOperationType() {
        return this.operationType;
    }

    public void setNegate(boolean b) {
        this.negate = b;
    }

    public boolean isNegate() {
        return this.negate;
    }

    public void setIdentifier(Name identifier) {
        this.identifier = identifier;
    }

    public Name getIdentifier() {
        return this.identifier;
    }

    public void setIdentifierOperand(String identifier) {
        this.identifierOperand = identifier;
    }

    public String getIdentifierOperand() {
        return this.identifierOperand;
    }

    public void setEscapeString(String esc) {
        this.escapeString = esc;
    }

    public String getEscapeString() {
        return this.escapeString;
    }

    @Override
    public Object jjtAccept(JCRSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String toString() {
        return super.toString() + " type: " + this.operationType + " negate: " + this.negate;
    }
}

