/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.tree.AbstractRestrictableStatement;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class DeleteStatement
extends AbstractRestrictableStatement {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DeleteStatement.class);

    @Override
    public int getStatementType() {
        return 13;
    }

    @Override
    public boolean needsExecutor() {
        return true;
    }

    @Override
    protected int getWhereClauseParentTokenType() {
        return 23;
    }

    @Override
    protected CoreMessageLogger getLog() {
        return LOG;
    }
}

