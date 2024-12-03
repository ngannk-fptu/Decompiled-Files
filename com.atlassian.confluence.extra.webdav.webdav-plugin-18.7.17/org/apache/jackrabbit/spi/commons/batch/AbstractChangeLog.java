/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.batch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Batch;
import org.apache.jackrabbit.spi.commons.batch.ChangeLog;
import org.apache.jackrabbit.spi.commons.batch.Operation;

public abstract class AbstractChangeLog<T extends Operation>
implements ChangeLog {
    protected final List<T> operations = new LinkedList<T>();

    public void addOperation(T op) throws RepositoryException {
        this.operations.add(op);
    }

    @Override
    public Batch apply(Batch batch) throws RepositoryException {
        if (batch == null) {
            throw new IllegalArgumentException("Batch must not be null");
        }
        for (Operation op : this.operations) {
            op.apply(batch);
        }
        return batch;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        Iterator<T> it = this.operations.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (!it.hasNext()) continue;
            b.append(", ");
        }
        return b.toString();
    }

    public boolean equals(Object other) {
        if (null == other) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (other instanceof AbstractChangeLog) {
            return this.equals((AbstractChangeLog)other);
        }
        return false;
    }

    public boolean equals(AbstractChangeLog<?> other) {
        return this.operations.equals(other.operations);
    }

    public int hashCode() {
        throw new IllegalArgumentException("Not hashable");
    }
}

