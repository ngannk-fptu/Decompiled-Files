/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class NorwegianStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final NorwegianStemmer methodObject = new NorwegianStemmer();
    private static final Among[] a_0 = new Among[]{new Among("a", -1, 1, "", methodObject), new Among("e", -1, 1, "", methodObject), new Among("ede", 1, 1, "", methodObject), new Among("ande", 1, 1, "", methodObject), new Among("ende", 1, 1, "", methodObject), new Among("ane", 1, 1, "", methodObject), new Among("ene", 1, 1, "", methodObject), new Among("hetene", 6, 1, "", methodObject), new Among("erte", 1, 3, "", methodObject), new Among("en", -1, 1, "", methodObject), new Among("heten", 9, 1, "", methodObject), new Among("ar", -1, 1, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("heter", 12, 1, "", methodObject), new Among("s", -1, 2, "", methodObject), new Among("as", 14, 1, "", methodObject), new Among("es", 14, 1, "", methodObject), new Among("edes", 16, 1, "", methodObject), new Among("endes", 16, 1, "", methodObject), new Among("enes", 16, 1, "", methodObject), new Among("hetenes", 19, 1, "", methodObject), new Among("ens", 14, 1, "", methodObject), new Among("hetens", 21, 1, "", methodObject), new Among("ers", 14, 1, "", methodObject), new Among("ets", 14, 1, "", methodObject), new Among("et", -1, 1, "", methodObject), new Among("het", 25, 1, "", methodObject), new Among("ert", -1, 3, "", methodObject), new Among("ast", -1, 1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("dt", -1, -1, "", methodObject), new Among("vt", -1, -1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("leg", -1, 1, "", methodObject), new Among("eleg", 0, 1, "", methodObject), new Among("ig", -1, 1, "", methodObject), new Among("eig", 2, 1, "", methodObject), new Among("lig", 2, 1, "", methodObject), new Among("elig", 4, 1, "", methodObject), new Among("els", -1, 1, "", methodObject), new Among("lov", -1, 1, "", methodObject), new Among("elov", 7, 1, "", methodObject), new Among("slov", 7, 1, "", methodObject), new Among("hetslov", 9, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '0', '\u0000', '\u0080'};
    private static final char[] g_s_ending = new char[]{'w', '}', '\u0095', '\u0001'};
    private int I_x;
    private int I_p1;

    private void copy_from(NorwegianStemmer other) {
        this.I_x = other.I_x;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }

    private boolean r_mark_regions() {
        this.I_p1 = this.limit;
        int v_1 = this.cursor;
        int c = this.cursor + 3;
        if (0 > c || c > this.limit) {
            return false;
        }
        this.I_x = this.cursor = c;
        this.cursor = v_1;
        while (true) {
            int v_2 = ++this.cursor;
            if (this.in_grouping(g_v, 97, 248)) {
                this.cursor = v_2;
                break;
            }
            this.cursor = v_2;
            if (this.cursor < this.limit) continue;
            return false;
        }
        while (!this.out_grouping(g_v, 97, 248)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p1 = this.cursor;
        if (this.I_p1 < this.I_x) {
            this.I_p1 = this.I_x;
        }
        return true;
    }

    private boolean r_main_suffix() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_0, 29);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_2;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                int v_3 = this.limit - this.cursor;
                if (!this.in_grouping_b(g_s_ending, 98, 122)) {
                    this.cursor = this.limit - v_3;
                    if (!this.eq_s_b(1, "k")) {
                        return false;
                    }
                    if (!this.out_grouping_b(g_v, 97, 248)) {
                        return false;
                    }
                }
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_from("er");
            }
        }
        return true;
    }

    private boolean r_consonant_pair() {
        int v_1 = this.limit - this.cursor;
        int v_2 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_3 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_2;
        if (this.find_among_b(a_1, 2) == 0) {
            this.limit_backward = v_3;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_3;
        this.cursor = this.limit - v_1;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        this.bra = this.cursor;
        this.slice_del();
        return true;
    }

    private boolean r_other_suffix() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_2, 11);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_2;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
            }
        }
        return true;
    }

    @Override
    public boolean stem() {
        int v_1 = this.cursor;
        if (!this.r_mark_regions()) {
            // empty if block
        }
        this.limit_backward = this.cursor = v_1;
        this.cursor = this.limit;
        int v_2 = this.limit - this.cursor;
        if (!this.r_main_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (!this.r_consonant_pair()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (!this.r_other_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_4;
        this.cursor = this.limit_backward;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof NorwegianStemmer;
    }

    public int hashCode() {
        return NorwegianStemmer.class.getName().hashCode();
    }
}

