/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.mime;

import java.util.Arrays;
import org.apache.tika.mime.Clause;

class AndClause
implements Clause {
    private final Clause[] clauses;

    AndClause(Clause ... clauses) {
        this.clauses = clauses;
    }

    @Override
    public boolean eval(byte[] data) {
        for (Clause clause : this.clauses) {
            if (clause.eval(data)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        int size = 0;
        for (Clause clause : this.clauses) {
            size += clause.size();
        }
        return size;
    }

    public String toString() {
        return "and" + Arrays.toString(this.clauses);
    }
}

