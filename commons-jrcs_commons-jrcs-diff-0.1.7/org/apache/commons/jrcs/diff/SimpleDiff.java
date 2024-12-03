/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.Delta;
import org.apache.commons.jrcs.diff.DiffAlgorithm;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;

public class SimpleDiff
implements DiffAlgorithm {
    static final int NOT_FOUND_i = -2;
    static final int NOT_FOUND_j = -1;
    static final int EOS = Integer.MAX_VALUE;

    protected int scan(int[] ndx, int i, int target) {
        while (ndx[i] < target) {
            ++i;
        }
        return i;
    }

    public Revision diff(Object[] orig, Object[] rev) throws DifferentiationFailedException {
        Map eqs = this.buildEqSet(orig, rev);
        int[] indx = this.buildIndex(eqs, orig, -2);
        int[] jndx = this.buildIndex(eqs, rev, -1);
        eqs = null;
        Revision deltas = new Revision();
        int i = 0;
        int j = 0;
        while (indx[i] != Integer.MAX_VALUE && indx[i] == jndx[j]) {
            ++i;
            ++j;
        }
        while (indx[i] != jndx[j]) {
            int ia = i;
            int ja = j;
            while (true) {
                if (jndx[j] < 0 || jndx[j] < indx[i]) {
                    ++j;
                    continue;
                }
                while (indx[i] < 0 || indx[i] < jndx[j]) {
                    ++i;
                }
                if (indx[i] == jndx[j]) break;
            }
            while (i > ia && j > ja && indx[i - 1] == jndx[j - 1]) {
                --i;
                --j;
            }
            deltas.addDelta(Delta.newDelta(new Chunk(orig, ia, i - ia), new Chunk(rev, ja, j - ja)));
            while (indx[i] != Integer.MAX_VALUE && indx[i] == jndx[j]) {
                ++i;
                ++j;
            }
        }
        return deltas;
    }

    protected Map buildEqSet(Object[] orig, Object[] rev) {
        HashSet<Object> items = new HashSet<Object>(Arrays.asList(orig));
        items.retainAll(Arrays.asList(rev));
        HashMap<Object, Integer> eqs = new HashMap<Object, Integer>();
        int i = 0;
        while (i < orig.length) {
            if (items.contains(orig[i])) {
                eqs.put(orig[i], new Integer(i));
                items.remove(orig[i]);
            }
            ++i;
        }
        return eqs;
    }

    protected int[] buildIndex(Map eqs, Object[] seq, int NF) {
        int[] result = new int[seq.length + 1];
        int i = 0;
        while (i < seq.length) {
            Integer value = (Integer)eqs.get(seq[i]);
            result[i] = value == null || value < 0 ? NF : value;
            ++i;
        }
        result[seq.length] = Integer.MAX_VALUE;
        return result;
    }
}

