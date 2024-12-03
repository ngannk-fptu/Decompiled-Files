/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.util;

import org.hibernate.hql.internal.antlr.HqlTokenTypes;
import org.hibernate.hql.internal.antlr.SqlTokenTypes;
import org.hibernate.hql.internal.ast.util.ASTPrinter;
import org.hibernate.hql.internal.ast.util.ASTReferencedTablesPrinter;
import org.hibernate.sql.ordering.antlr.GeneratedOrderByFragmentRendererTokenTypes;

public interface TokenPrinters {
    public static final ASTPrinter HQL_TOKEN_PRINTER = new ASTPrinter(HqlTokenTypes.class);
    public static final ASTPrinter SQL_TOKEN_PRINTER = new ASTPrinter(SqlTokenTypes.class);
    public static final ASTPrinter ORDERBY_FRAGMENT_PRINTER = new ASTPrinter(GeneratedOrderByFragmentRendererTokenTypes.class);
    public static final ASTPrinter REFERENCED_TABLES_PRINTER = new ASTReferencedTablesPrinter(SqlTokenTypes.class);
}

