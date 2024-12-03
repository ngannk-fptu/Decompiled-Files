/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class DanishStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final DanishStemmer methodObject = new DanishStemmer();
    private static final Among[] a_0 = new Among[]{new Among("hed", -1, 1, "", methodObject), new Among("ethed", 0, 1, "", methodObject), new Among("ered", -1, 1, "", methodObject), new Among("e", -1, 1, "", methodObject), new Among("erede", 3, 1, "", methodObject), new Among("ende", 3, 1, "", methodObject), new Among("erende", 5, 1, "", methodObject), new Among("ene", 3, 1, "", methodObject), new Among("erne", 3, 1, "", methodObject), new Among("ere", 3, 1, "", methodObject), new Among("en", -1, 1, "", methodObject), new Among("heden", 10, 1, "", methodObject), new Among("eren", 10, 1, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("heder", 13, 1, "", methodObject), new Among("erer", 13, 1, "", methodObject), new Among("s", -1, 2, "", methodObject), new Among("heds", 16, 1, "", methodObject), new Among("es", 16, 1, "", methodObject), new Among("endes", 18, 1, "", methodObject), new Among("erendes", 19, 1, "", methodObject), new Among("enes", 18, 1, "", methodObject), new Among("ernes", 18, 1, "", methodObject), new Among("eres", 18, 1, "", methodObject), new Among("ens", 16, 1, "", methodObject), new Among("hedens", 24, 1, "", methodObject), new Among("erens", 24, 1, "", methodObject), new Among("ers", 16, 1, "", methodObject), new Among("ets", 16, 1, "", methodObject), new Among("erets", 28, 1, "", methodObject), new Among("et", -1, 1, "", methodObject), new Among("eret", 30, 1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("gd", -1, -1, "", methodObject), new Among("dt", -1, -1, "", methodObject), new Among("gt", -1, -1, "", methodObject), new Among("kt", -1, -1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ig", -1, 1, "", methodObject), new Among("lig", 0, 1, "", methodObject), new Among("elig", 1, 1, "", methodObject), new Among("els", -1, 1, "", methodObject), new Among("l\u00f8st", -1, 2, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '0', '\u0000', '\u0080'};
    private static final char[] g_s_ending = new char[]{'\u00ef', '\u00fe', '*', '\u0003', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0010'};
    private int I_x;
    private int I_p1;
    private StringBuilder S_ch = new StringBuilder();

    private void copy_from(DanishStemmer other) {
        this.I_x = other.I_x;
        this.I_p1 = other.I_p1;
        this.S_ch = other.S_ch;
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
        int among_var = this.find_among_b(a_0, 32);
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
                if (!this.in_grouping_b(g_s_ending, 97, 229)) {
                    return false;
                }
                this.slice_del();
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
        if (this.find_among_b(a_1, 4) == 0) {
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
        this.ket = this.cursor;
        if (this.eq_s_b(2, "st")) {
            this.bra = this.cursor;
            if (this.eq_s_b(2, "ig")) {
                this.slice_del();
            }
        }
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_3 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_2;
        int among_var = this.find_among_b(a_2, 5);
        if (among_var == 0) {
            this.limit_backward = v_3;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_3;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                int v_4 = this.limit - this.cursor;
                if (!this.r_consonant_pair()) {
                    // empty if block
                }
                this.cursor = this.limit - v_4;
                break;
            }
            case 2: {
                this.slice_from("l\u00f8s");
            }
        }
        return true;
    }

    private boolean r_undouble() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        if (!this.out_grouping_b(g_v, 97, 248)) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.S_ch = this.slice_to(this.S_ch);
        this.limit_backward = v_2;
        if (!this.eq_v_b(this.S_ch)) {
            return false;
        }
        this.slice_del();
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
        int v_5 = this.limit - this.cursor;
        if (!this.r_undouble()) {
            // empty if block
        }
        this.cursor = this.limit - v_5;
        this.cursor = this.limit_backward;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof DanishStemmer;
    }

    public int hashCode() {
        return DanishStemmer.class.getName().hashCode();
    }
}

