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
import org.apache.jackrabbit.spi.commons.query.sql.SimpleNode;

public interface JCRSQLParserVisitor {
    public Object visit(SimpleNode var1, Object var2);

    public Object visit(ASTQuery var1, Object var2);

    public Object visit(ASTSelectList var1, Object var2);

    public Object visit(ASTFromClause var1, Object var2);

    public Object visit(ASTWhereClause var1, Object var2);

    public Object visit(ASTPredicate var1, Object var2);

    public Object visit(ASTLowerFunction var1, Object var2);

    public Object visit(ASTUpperFunction var1, Object var2);

    public Object visit(ASTOrExpression var1, Object var2);

    public Object visit(ASTAndExpression var1, Object var2);

    public Object visit(ASTNotExpression var1, Object var2);

    public Object visit(ASTBracketExpression var1, Object var2);

    public Object visit(ASTContainsExpression var1, Object var2);

    public Object visit(ASTLiteral var1, Object var2);

    public Object visit(ASTIdentifier var1, Object var2);

    public Object visit(ASTExcerptFunction var1, Object var2);

    public Object visit(ASTOrderByClause var1, Object var2);

    public Object visit(ASTOrderSpec var1, Object var2);

    public Object visit(ASTAscendingOrderSpec var1, Object var2);

    public Object visit(ASTDescendingOrderSpec var1, Object var2);
}

