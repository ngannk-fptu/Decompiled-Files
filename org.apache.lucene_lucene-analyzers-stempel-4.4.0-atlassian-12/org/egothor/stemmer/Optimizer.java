/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.egothor.stemmer.Cell;
import org.egothor.stemmer.Reduce;
import org.egothor.stemmer.Row;
import org.egothor.stemmer.Trie;

public class Optimizer
extends Reduce {
    @Override
    public Trie optimize(Trie orig) {
        List<CharSequence> cmds = orig.cmds;
        List<Row> rows = new ArrayList<Row>();
        List<Row> orows = orig.rows;
        int[] remap = new int[orows.size()];
        for (int j = orows.size() - 1; j >= 0; --j) {
            Reduce.Remap now = new Reduce.Remap(orows.get(j), remap);
            boolean merged = false;
            for (int i = 0; i < rows.size(); ++i) {
                Row q = this.merge(now, rows.get(i));
                if (q == null) continue;
                rows.set(i, q);
                merged = true;
                remap[j] = i;
                break;
            }
            if (merged) continue;
            remap[j] = rows.size();
            rows.add(now);
        }
        int root = remap[orig.root];
        Arrays.fill(remap, -1);
        rows = this.removeGaps(root, rows, new ArrayList<Row>(), remap);
        return new Trie(orig.forward, remap[root], cmds, rows);
    }

    public Row merge(Row master, Row existing) {
        Iterator<Character> i = master.cells.keySet().iterator();
        Row n = new Row();
        while (i.hasNext()) {
            Cell s;
            Character ch = i.next();
            Cell a = master.cells.get(ch);
            Cell b = existing.cells.get(ch);
            Cell cell = s = b == null ? new Cell(a) : this.merge(a, b);
            if (s == null) {
                return null;
            }
            n.cells.put(ch, s);
        }
        for (Character ch : existing.cells.keySet()) {
            if (master.at(ch) != null) continue;
            n.cells.put(ch, existing.at(ch));
        }
        return n;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Cell merge(Cell m, Cell e) {
        Cell n = new Cell();
        if (m.skip != e.skip) {
            return null;
        }
        if (m.cmd >= 0) {
            if (e.cmd >= 0) {
                if (m.cmd != e.cmd) return null;
                n.cmd = m.cmd;
            } else {
                n.cmd = m.cmd;
            }
        } else {
            n.cmd = e.cmd;
        }
        if (m.ref >= 0) {
            if (e.ref >= 0) {
                if (m.ref != e.ref) return null;
                if (m.skip != e.skip) return null;
                n.ref = m.ref;
            } else {
                n.ref = m.ref;
            }
        } else {
            n.ref = e.ref;
        }
        n.cnt = m.cnt + e.cnt;
        n.skip = m.skip;
        return n;
    }
}

