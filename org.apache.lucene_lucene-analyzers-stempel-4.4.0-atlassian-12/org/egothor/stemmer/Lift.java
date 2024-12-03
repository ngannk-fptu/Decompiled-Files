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

public class Lift
extends Reduce {
    boolean changeSkip;

    public Lift(boolean changeSkip) {
        this.changeSkip = changeSkip;
    }

    @Override
    public Trie optimize(Trie orig) {
        List<CharSequence> cmds = orig.cmds;
        ArrayList<Row> rows = new ArrayList();
        List<Row> orows = orig.rows;
        int[] remap = new int[orows.size()];
        for (int j = orows.size() - 1; j >= 0; --j) {
            this.liftUp(orows.get(j), orows);
        }
        Arrays.fill(remap, -1);
        rows = this.removeGaps(orig.root, orows, new ArrayList<Row>(), remap);
        return new Trie(orig.forward, remap[orig.root], cmds, rows);
    }

    public void liftUp(Row in, List<Row> nodes) {
        for (Cell c : in.cells.values()) {
            Row to;
            int sum;
            if (c.ref < 0 || (sum = (to = nodes.get(c.ref)).uniformCmd(this.changeSkip)) < 0) continue;
            if (sum == c.cmd) {
                if (this.changeSkip) {
                    if (c.skip != to.uniformSkip + 1) continue;
                    c.skip = to.uniformSkip + 1;
                } else {
                    c.skip = 0;
                }
                c.cnt += to.uniformCnt;
                c.ref = -1;
                continue;
            }
            if (c.cmd >= 0) continue;
            c.cnt = to.uniformCnt;
            c.cmd = sum;
            c.ref = -1;
            if (this.changeSkip) {
                c.skip = to.uniformSkip + 1;
                continue;
            }
            c.skip = 0;
        }
    }
}

