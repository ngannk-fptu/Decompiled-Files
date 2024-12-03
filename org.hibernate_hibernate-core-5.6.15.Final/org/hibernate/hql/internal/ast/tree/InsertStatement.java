/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import org.hibernate.QueryException;
import org.hibernate.hql.internal.ast.tree.AbstractStatement;
import org.hibernate.hql.internal.ast.tree.IntoClause;
import org.hibernate.hql.internal.ast.tree.QueryNode;
import org.hibernate.hql.internal.ast.tree.SelectClause;

public class InsertStatement
extends AbstractStatement {
    @Override
    public int getStatementType() {
        return 30;
    }

    @Override
    public boolean needsExecutor() {
        return true;
    }

    public void validate() throws QueryException {
        this.getIntoClause().validateTypes(this.getSelectClause());
    }

    public IntoClause getIntoClause() {
        return (IntoClause)this.getFirstChild();
    }

    public SelectClause getSelectClause() {
        return ((QueryNode)this.getIntoClause().getNextSibling()).getSelectClause();
    }
}

