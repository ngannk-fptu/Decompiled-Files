/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.hql.internal.ast.tree;

import java.io.Serializable;
import java.util.Iterator;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.HqlSqlWalkerNode;
import org.hibernate.hql.internal.ast.tree.Statement;

public abstract class AbstractStatement
extends HqlSqlWalkerNode
implements DisplayableNode,
Statement {
    @Override
    public String getDisplayText() {
        StringBuilder buf = new StringBuilder();
        if (this.getWalker().getQuerySpaces().size() > 0) {
            buf.append(" querySpaces (");
            Iterator<Serializable> iterator = this.getWalker().getQuerySpaces().iterator();
            while (iterator.hasNext()) {
                buf.append(iterator.next());
                if (!iterator.hasNext()) continue;
                buf.append(",");
            }
            buf.append(")");
        }
        return buf.toString();
    }
}

