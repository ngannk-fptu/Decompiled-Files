/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class SwedishStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final SwedishStemmer methodObject = new SwedishStemmer();
    private static final Among[] a_0 = new Among[]{new Among("a", -1, 1, "", methodObject), new Among("arna", 0, 1, "", methodObject), new Among("erna", 0, 1, "", methodObject), new Among("heterna", 2, 1, "", methodObject), new Among("orna", 0, 1, "", methodObject), new Among("ad", -1, 1, "", methodObject), new Among("e", -1, 1, "", methodObject), new Among("ade", 6, 1, "", methodObject), new Among("ande", 6, 1, "", methodObject), new Among("arne", 6, 1, "", methodObject), new Among("are", 6, 1, "", methodObject), new Among("aste", 6, 1, "", methodObject), new Among("en", -1, 1, "", methodObject), new Among("anden", 12, 1, "", methodObject), new Among("aren", 12, 1, "", methodObject), new Among("heten", 12, 1, "", methodObject), new Among("ern", -1, 1, "", methodObject), new Among("ar", -1, 1, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("heter", 18, 1, "", methodObject), new Among("or", -1, 1, "", methodObject), new Among("s", -1, 2, "", methodObject), new Among("as", 21, 1, "", methodObject), new Among("arnas", 22, 1, "", methodObject), new Among("ernas", 22, 1, "", methodObject), new Among("ornas", 22, 1, "", methodObject), new Among("es", 21, 1, "", methodObject), new Among("ades", 26, 1, "", methodObject), new Among("andes", 26, 1, "", methodObject), new Among("ens", 21, 1, "", methodObject), new Among("arens", 29, 1, "", methodObject), new Among("hetens", 29, 1, "", methodObject), new Among("erns", 21, 1, "", methodObject), new Among("at", -1, 1, "", methodObject), new Among("andet", -1, 1, "", methodObject), new Among("het", -1, 1, "", methodObject), new Among("ast", -1, 1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("dd", -1, -1, "", methodObject), new Among("gd", -1, -1, "", methodObject), new Among("nn", -1, -1, "", methodObject), new Among("dt", -1, -1, "", methodObject), new Among("gt", -1, -1, "", methodObject), new Among("kt", -1, -1, "", methodObject), new Among("tt", -1, -1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ig", -1, 1, "", methodObject), new Among("lig", 0, 1, "", methodObject), new Among("els", -1, 1, "", methodObject), new Among("fullt", -1, 3, "", methodObject), new Among("l\u00f6st", -1, 2, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0018', '\u0000', ' '};
    private static final char[] g_s_ending = new char[]{'w', '\u007f', '\u0095'};
    private int I_x;
    private int I_p1;

    private void copy_from(SwedishStemmer other) {
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
            if (this.in_grouping(g_v, 97, 246)) {
                this.cursor = v_2;
                break;
            }
            this.cursor = v_2;
            if (this.cursor < this.limit) continue;
            return false;
        }
        while (!this.out_grouping(g_v, 97, 246)) {
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
        int among_var = this.find_among_b(a_0, 37);
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
                if (!this.in_grouping_b(g_s_ending, 98, 121)) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_consonant_pair() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_1;
        int v_3 = this.limit - this.cursor;
        if (this.find_among_b(a_1, 7) == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.ket = this.cursor = this.limit - v_3;
        if (this.cursor <= this.limit_backward) {
            this.limit_backward = v_2;
            return false;
        }
        --this.cursor;
        this.bra = this.cursor;
        this.slice_del();
        this.limit_backward = v_2;
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
        int among_var = this.find_among_b(a_2, 5);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                this.limit_backward = v_2;
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("l\u00f6s");
                break;
            }
            case 3: {
                this.slice_from("full");
            }
        }
        this.limit_backward = v_2;
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
        return o instanceof SwedishStemmer;
    }

    public int hashCode() {
        return SwedishStemmer.class.getName().hashCode();
    }
}

