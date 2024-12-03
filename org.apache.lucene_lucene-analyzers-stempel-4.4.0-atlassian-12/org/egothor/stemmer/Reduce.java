/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.egothor.stemmer.Cell;
import org.egothor.stemmer.Row;
import org.egothor.stemmer.Trie;

public class Reduce {
    public Trie optimize(Trie orig) {
        List<CharSequence> cmds = orig.cmds;
        List<Row> rows = new ArrayList<Row>();
        List<Row> orows = orig.rows;
        int[] remap = new int[orows.size()];
        Arrays.fill(remap, -1);
        rows = this.removeGaps(orig.root, rows, new ArrayList<Row>(), remap);
        return new Trie(orig.forward, remap[orig.root], cmds, rows);
    }

    List<Row> removeGaps(int ind, List<Row> old, List<Row> to, int[] remap) {
        remap[ind] = to.size();
        Row now = old.get(ind);
        to.add(now);
        for (Cell c : now.cells.values()) {
            if (c.ref < 0 || remap[c.ref] >= 0) continue;
            this.removeGaps(c.ref, old, to, remap);
        }
        to.set(remap[ind], new Remap(now, remap));
        return to;
    }

    class Remap
    extends Row {
        public Remap(Row old, int[] remap) {
            for (Character ch : old.cells.keySet()) {
                Cell nc;
                Cell c = old.at(ch);
                if (c.ref >= 0) {
                    nc = new Cell(c);
                    nc.ref = remap[nc.ref];
                } else {
                    nc = new Cell(c);
                }
                this.cells.put(ch, nc);
            }
        }
    }
}

