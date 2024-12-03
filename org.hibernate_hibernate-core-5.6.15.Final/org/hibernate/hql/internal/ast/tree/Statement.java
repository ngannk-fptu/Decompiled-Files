/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.hql.internal.ast.HqlSqlWalker;

public interface Statement {
    public HqlSqlWalker getWalker();

    public int getStatementType();

    public boolean needsExecutor();
}

