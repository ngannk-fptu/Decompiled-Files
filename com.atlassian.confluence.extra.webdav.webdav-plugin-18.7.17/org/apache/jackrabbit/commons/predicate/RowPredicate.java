/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Row;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class RowPredicate
implements Predicate {
    private final String selectorName;

    public RowPredicate(String selectorName) {
        this.selectorName = selectorName;
    }

    public RowPredicate() {
        this(null);
    }

    @Override
    public boolean evaluate(Object object) {
        if (object instanceof Row) {
            try {
                return this.evaluate((Row)object);
            }
            catch (RepositoryException e) {
                throw new RuntimeException("Failed to evaluate " + object, e);
            }
        }
        return false;
    }

    protected boolean evaluate(Row row) throws RepositoryException {
        if (this.selectorName != null) {
            return this.evaluate(row.getNode(this.selectorName));
        }
        return true;
    }

    protected boolean evaluate(Node node) throws RepositoryException {
        return true;
    }
}

