/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util.automaton;

import org.apache.lucene.util.automaton.LevenshteinAutomata;

class Lev1ParametricDescription
extends LevenshteinAutomata.ParametricDescription {
    private static final long[] toStates0 = new long[]{2L};
    private static final long[] offsetIncrs0 = new long[]{0L};
    private static final long[] toStates1 = new long[]{2627L};
    private static final long[] offsetIncrs1 = new long[]{56L};
    private static final long[] toStates2 = new long[]{475737946583105539L};
    private static final long[] offsetIncrs2 = new long[]{0x5555588000L};
    private static final long[] toStates3 = new long[]{1625984326543966211L, 50000099178482249L};
    private static final long[] offsetIncrs3 = new long[]{6148915115578032128L, 21845L};

    @Override
    int transition(int absState, int position, int vector) {
        assert (absState != -1);
        int state = absState / (this.w + 1);
        int offset = absState % (this.w + 1);
        assert (offset >= 0);
        if (position == this.w) {
            if (state < 2) {
                int loc = vector * 2 + state;
                offset += this.unpack(offsetIncrs0, loc, 1);
                state = this.unpack(toStates0, loc, 2) - 1;
            }
        } else if (position == this.w - 1) {
            if (state < 3) {
                int loc = vector * 3 + state;
                offset += this.unpack(offsetIncrs1, loc, 1);
                state = this.unpack(toStates1, loc, 2) - 1;
            }
        } else if (position == this.w - 2) {
            if (state < 5) {
                int loc = vector * 5 + state;
                offset += this.unpack(offsetIncrs2, loc, 2);
                state = this.unpack(toStates2, loc, 3) - 1;
            }
        } else if (state < 5) {
            int loc = vector * 5 + state;
            offset += this.unpack(offsetIncrs3, loc, 2);
            state = this.unpack(toStates3, loc, 3) - 1;
        }
        if (state == -1) {
            return -1;
        }
        return state * (this.w + 1) + offset;
    }

    public Lev1ParametricDescription(int w) {
        super(w, 1, new int[]{0, 1, 0, -1, -1});
    }
}

