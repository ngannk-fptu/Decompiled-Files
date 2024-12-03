/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.RecognitionException
 */
package org.hibernate.hql.internal.ast.exec;

import antlr.RecognitionException;
import java.util.List;
import org.hibernate.hql.internal.ast.HqlSqlWalker;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.exec.BasicExecutor;
import org.hibernate.hql.internal.ast.tree.InsertStatement;
import org.hibernate.param.ParameterSpecification;
import org.hibernate.persister.entity.Queryable;

public class InsertExecutor
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

    public InsertExecutor(HqlSqlWalker walker) {
        this.persister = ((InsertStatement)walker.getAST()).getIntoClause().getQueryable();
        try {
            SqlGenerator gen = new SqlGenerator(walker.getSessionFactoryHelper().getFactory());
            gen.statement(walker.getAST());
            this.sql = gen.getSQL();
            gen.getParseErrorHandler().throwQueryException();
            this.parameterSpecifications = gen.getCollectedParameters();
        }
        catch (RecognitionException e) {
            throw QuerySyntaxException.convert(e);
        }
    }
}

