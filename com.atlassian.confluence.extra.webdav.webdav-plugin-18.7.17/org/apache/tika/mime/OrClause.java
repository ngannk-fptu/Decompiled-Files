/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.util.List;
import org.apache.tika.mime.Clause;

class OrClause
implements Clause {
    private final List<Clause> clauses;

    OrClause(List<Clause> clauses) {
        this.clauses = clauses;
    }

    @Override
    public boolean eval(byte[] data) {
        for (Clause clause : this.clauses) {
            if (!clause.eval(data)) continue;
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        int size = 0;
        for (Clause clause : this.clauses) {
            size = Math.max(size, clause.size());
        }
        return size;
    }

    public String toString() {
        return "or" + this.clauses;
    }
}

