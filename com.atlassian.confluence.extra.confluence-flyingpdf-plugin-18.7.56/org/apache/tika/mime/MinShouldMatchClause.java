/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.util.List;
import org.apache.tika.mime.Clause;

class MinShouldMatchClause
implements Clause {
    private final int min;
    private final List<Clause> clauses;

    MinShouldMatchClause(int min, List<Clause> clauses) {
        if (clauses == null || clauses.size() == 0) {
            throw new IllegalArgumentException("clauses must be not null with size > 0");
        }
        if (min > clauses.size()) {
            throw new IllegalArgumentException("min (" + min + ") cannot be > clauses.size (" + clauses.size() + ")");
        }
        if (min <= 0) {
            throw new IllegalArgumentException("min cannot be <= 0: " + min);
        }
        this.min = min;
        this.clauses = clauses;
    }

    @Override
    public boolean eval(byte[] data) {
        int matches = 0;
        for (Clause clause : this.clauses) {
            if (!clause.eval(data) || ++matches < this.min) continue;
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
        return "minShouldMatch (min: " + this.min + ") " + this.clauses;
    }
}

