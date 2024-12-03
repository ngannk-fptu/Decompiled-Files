/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import org.egothor.stemmer.Cell;
import org.egothor.stemmer.Optimizer;

public class Optimizer2
extends Optimizer {
    @Override
    public Cell merge(Cell m, Cell e) {
        if (m.cmd == e.cmd && m.ref == e.ref && m.skip == e.skip) {
            Cell c = new Cell(m);
            c.cnt += e.cnt;
            return c;
        }
        return null;
    }
}

