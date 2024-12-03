/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.sql;

import org.apache.jackrabbit.spi.commons.query.sql.ASTAndExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTAscendingOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTBracketExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTContainsExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTDescendingOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTExcerptFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTFromClause;
import org.apache.jackrabbit.spi.commons.query.sql.ASTIdentifier;
import org.apache.jackrabbit.spi.commons.query.sql.ASTLiteral;
import org.apache.jackrabbit.spi.commons.query.sql.ASTLowerFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTNotExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrExpression;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrderByClause;
import org.apache.jackrabbit.spi.commons.query.sql.ASTOrderSpec;
import org.apache.jackrabbit.spi.commons.query.sql.ASTPredicate;
import org.apache.jackrabbit.spi.commons.query.sql.ASTQuery;
import org.apache.jackrabbit.spi.commons.query.sql.ASTSelectList;
import org.apache.jackrabbit.spi.commons.query.sql.ASTUpperFunction;
import org.apache.jackrabbit.spi.commons.query.sql.ASTWhereClause;
import org.apache.jackrabbit.spi.commons.query.sql.JCRSQLParserVisitor;
import org.apache.jackrabbit.spi.commons.query.sql.SimpleNode;

class DefaultParserVisitor
implements JCRSQLParserVisitor {
    DefaultParserVisitor() {
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTQuery node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTSelectList node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTFromClause node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTWhereClause node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTPredicate node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTOrExpression node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTAndExpression node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTNotExpression node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTBracketExpression node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTOrderByClause node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTContainsExpression node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTOrderSpec node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTAscendingOrderSpec node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTDescendingOrderSpec node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTLowerFunction node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTUpperFunction node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTExcerptFunction node, Object data) {
        return data;
    }
}

