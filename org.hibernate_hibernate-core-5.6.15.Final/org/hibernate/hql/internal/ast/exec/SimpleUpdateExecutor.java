/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 */
package org.hibernate.hql.internal.ast.exec;

import antlr.RecognitionException;
import java.util.List;
import org.hibernate.AssertionFailure;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.exec.BasicExecutor;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;

public class SimpleUpdateExecutor
extends BasicExecutor {
    private final Queryable persister;
    private final String sql;
    private final List<ParameterSpecification> parameterSpecifications;

    @Override
    public Queryable getPersister() {
        return this.persister;
    }

    @Override
    public String getSql() {
        return this.sql;
    }

    @Override
    public List<ParameterSpecification> getParameterSpecifications() {
        return this.parameterSpecifications;
    }

    public SimpleUpdateExecutor(HqlSqlWalker walker) {
        this.persister = walker.getFinalFromClause().getFromElement().getQueryable();
        if (this.persister.isMultiTable() && walker.getQuerySpaces().size() > 1) {
            throw new AssertionFailure("not a simple update");
        }
        try {
            SqlGenerator gen = new SqlGenerator(walker.getSessionFactoryHelper().getFactory());
            gen.statement(walker.getAST());
            gen.getParseErrorHandler().throwQueryException();
            String alias = walker.getFinalFromClause().getFromElement().getTableAlias();
            this.sql = gen.getSQL().replace(alias + ".", "");
            this.parameterSpecifications = gen.getCollectedParameters();
        }
        catch (RecognitionException e) {
            throw QuerySyntaxException.convert(e);
        }
    }
}

