/*
 * Decompiled with CFR 0.152.
 */
package org.egothor.stemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.egothor.stemmer.Cell;
import org.egothor.stemmer.Reduce;
import org.egothor.stemmer.Row;
import org.egothor.stemmer.Trie;

public class Gener
extends Reduce {
    @Override
    public Trie optimize(Trie orig) {
        List<CharSequence> cmds = orig.cmds;
        ArrayList<Row> rows = new ArrayList();
        List<Row> orows = orig.rows;
        int[] remap = new int[orows.size()];
        Arrays.fill(remap, 1);
        for (int j = orows.size() - 1; j >= 0; --j) {
            if (!this.eat(orows.get(j), remap)) continue;
            remap[j] = 0;
        }
        Arrays.fill(remap, -1);
        rows = this.removeGaps(orig.root, orows, new ArrayList<Row>(), remap);
        return new Trie(orig.forward, remap[orig.root], cmds, rows);
    }

    public boolean eat(Row in, int[] remap) {
        int sum = 0;
        for (Cell c : in.cells.values()) {
            sum += c.cnt;
            if (c.ref < 0 || remap[c.ref] != 0) continue;
            c.ref = -1;
        }
        int frame = sum / 10;
        boolean live = false;
        for (Cell c : in.cells.values()) {
            if (c.cnt < frame && c.cmd >= 0) {
                c.cnt = 0;
                c.cmd = -1;
            }
            if (c.cmd < 0 && c.ref < 0) continue;
            live |= true;
        }
        return !live;
    }
}

