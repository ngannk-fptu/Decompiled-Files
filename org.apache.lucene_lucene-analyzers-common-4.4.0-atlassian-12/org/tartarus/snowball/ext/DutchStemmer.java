/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class DutchStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final DutchStemmer methodObject = new DutchStemmer();
    private static final Among[] a_0 = new Among[]{new Among("", -1, 6, "", methodObject), new Among("\u00e1", 0, 1, "", methodObject), new Among("\u00e4", 0, 1, "", methodObject), new Among("\u00e9", 0, 2, "", methodObject), new Among("\u00eb", 0, 2, "", methodObject), new Among("\u00ed", 0, 3, "", methodObject), new Among("\u00ef", 0, 3, "", methodObject), new Among("\u00f3", 0, 4, "", methodObject), new Among("\u00f6", 0, 4, "", methodObject), new Among("\u00fa", 0, 5, "", methodObject), new Among("\u00fc", 0, 5, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("", -1, 3, "", methodObject), new Among("I", 0, 2, "", methodObject), new Among("Y", 0, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("dd", -1, -1, "", methodObject), new Among("kk", -1, -1, "", methodObject), new Among("tt", -1, -1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("ene", -1, 2, "", methodObject), new Among("se", -1, 3, "", methodObject), new Among("en", -1, 2, "", methodObject), new Among("heden", 2, 1, "", methodObject), new Among("s", -1, 3, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("end", -1, 1, "", methodObject), new Among("ig", -1, 2, "", methodObject), new Among("ing", -1, 1, "", methodObject), new Among("lijk", -1, 3, "", methodObject), new Among("baar", -1, 4, "", methodObject), new Among("bar", -1, 5, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("aa", -1, -1, "", methodObject), new Among("ee", -1, -1, "", methodObject), new Among("oo", -1, -1, "", methodObject), new Among("uu", -1, -1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080'};
    private static final char[] g_v_I = new char[]{'\u0001', '\u0000', '\u0000', '\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080'};
    private static final char[] g_v_j = new char[]{'\u0011', 'C', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080'};
    private int I_p2;
    private int I_p1;
    private boolean B_e_found;

    private void copy_from(DutchStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.B_e_found = other.B_e_found;
        super.copy_from(other);
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_prelude() {
        int v_4;
        int v_2;
        int v_1 = this.cursor;
        block9: while (true) {
            v_2 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_0, 11);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block9;
                }
                case 1: {
                    this.slice_from("a");
                    break;
                }
                case 2: {
                    this.slice_from("e");
                    break;
                }
                case 3: {
                    this.slice_from("i");
                    break;
                }
                case 4: {
                    this.slice_from("o");
                    break;
                }
                case 5: {
                    this.slice_from("u");
                    break;
                }
                case 6: {
                    if (this.cursor >= this.limit) break block9;
                }
            }
        }
        this.cursor = v_2;
        int v_3 = this.cursor = v_1;
        this.bra = this.cursor;
        if (!this.eq_s(1, "y")) {
            this.cursor = v_3;
        } else {
            this.ket = this.cursor;
            this.slice_from("Y");
        }
        block10: while (true) {
            v_4 = this.cursor;
            do {
                int v_5;
                block17: {
                    block19: {
                        int v_6;
                        block18: {
                            v_5 = ++this.cursor;
                            if (!this.in_grouping(g_v, 97, 232)) break block17;
                            this.bra = this.cursor;
                            v_6 = this.cursor;
                            if (!this.eq_s(1, "i")) break block18;
                            this.ket = this.cursor;
                            if (!this.in_grouping(g_v, 97, 232)) break block18;
                            this.slice_from("I");
                            break block19;
                        }
                        this.cursor = v_6;
                        if (!this.eq_s(1, "y")) break block17;
                        this.ket = this.cursor;
                        this.slice_from("Y");
                    }
                    this.cursor = v_5;
                    continue block10;
                }
                this.cursor = v_5;
            } while (this.cursor < this.limit);
            break;
        }
        this.cursor = v_4;
        return true;
    }

    private boolean r_mark_regions() {
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        while (!this.in_grouping(g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p1 = this.cursor;
        if (this.I_p1 < 3) {
            this.I_p1 = 3;
        }
        while (!this.in_grouping(g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(g_v, 97, 232)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p2 = this.cursor;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_postlude() {
        int v_1;
        block6: while (true) {
            v_1 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_1, 3);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block6;
                }
                case 1: {
                    this.slice_from("y");
                    break;
                }
                case 2: {
                    this.slice_from("i");
                    break;
                }
                case 3: {
                    if (this.cursor >= this.limit) break block6;
                }
            }
        }
        this.cursor = v_1;
        return true;
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_undouble() {
        int v_1 = this.limit - this.cursor;
        if (this.find_among_b(a_2, 3) == 0) {
            return false;
        }
        this.ket = this.cursor = this.limit - v_1;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        this.bra = this.cursor;
        this.slice_del();
        return true;
    }

    private boolean r_e_ending() {
        this.B_e_found = false;
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "e")) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        int v_1 = this.limit - this.cursor;
        if (!this.out_grouping_b(g_v, 97, 232)) {
            return false;
        }
        this.cursor = this.limit - v_1;
        this.slice_del();
        this.B_e_found = true;
        return this.r_undouble();
    }

    private boolean r_en_ending() {
        if (!this.r_R1()) {
            return false;
        }
        int v_1 = this.limit - this.cursor;
        if (!this.out_grouping_b(g_v, 97, 232)) {
            return false;
        }
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(3, "gem")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        this.slice_del();
        return this.r_undouble();
    }

    private boolean r_standard_suffix() {
        int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 5);
        if (among_var != 0) {
            this.bra = this.cursor;
            switch (among_var) {
                case 0: {
                    break;
                }
                case 1: {
                    if (!this.r_R1()) break;
                    this.slice_from("heid");
                    break;
                }
                case 2: {
                    if (this.r_en_ending()) break;
                    break;
                }
                case 3: {
                    if (!this.r_R1() || !this.out_grouping_b(g_v_j, 97, 232)) break;
                    this.slice_del();
                }
            }
        }
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.r_e_ending()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (this.eq_s_b(4, "heid")) {
            this.bra = this.cursor;
            if (this.r_R2()) {
                int v_4 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "c")) {
                    this.cursor = this.limit - v_4;
                    this.slice_del();
                    this.ket = this.cursor;
                    if (this.eq_s_b(2, "en")) {
                        this.bra = this.cursor;
                        if (!this.r_en_ending()) {
                            // empty if block
                        }
                    }
                }
            }
        }
        this.cursor = this.limit - v_3;
        int v_5 = this.limit - this.cursor;
        this.ket = this.cursor;
        among_var = this.find_among_b(a_4, 6);
        if (among_var != 0) {
            this.bra = this.cursor;
            switch (among_var) {
                case 0: {
                    break;
                }
                case 1: {
                    if (!this.r_R2()) break;
                    this.slice_del();
                    int v_6 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (this.eq_s_b(2, "ig")) {
                        this.bra = this.cursor;
                        if (this.r_R2()) {
                            int v_7 = this.limit - this.cursor;
                            if (!this.eq_s_b(1, "e")) {
                                this.cursor = this.limit - v_7;
                                this.slice_del();
                                break;
                            }
                        }
                    }
                    this.cursor = this.limit - v_6;
                    if (this.r_undouble()) break;
                    break;
                }
                case 2: {
                    if (!this.r_R2()) break;
                    int v_8 = this.limit - this.cursor;
                    if (this.eq_s_b(1, "e")) break;
                    this.cursor = this.limit - v_8;
                    this.slice_del();
                    break;
                }
                case 3: {
                    if (!this.r_R2()) break;
                    this.slice_del();
                    if (this.r_e_ending()) break;
                    break;
                }
                case 4: {
                    if (!this.r_R2()) break;
                    this.slice_del();
                    break;
                }
                case 5: {
                    if (!this.r_R2() || !this.B_e_found) break;
                    this.slice_del();
                }
            }
        }
        this.cursor = this.limit - v_5;
        int v_9 = this.limit - this.cursor;
        if (this.out_grouping_b(g_v_I, 73, 232)) {
            int v_10 = this.limit - this.cursor;
            if (this.find_among_b(a_5, 4) != 0 && this.out_grouping_b(g_v, 97, 232)) {
                this.ket = this.cursor = this.limit - v_10;
                if (this.cursor > this.limit_backward) {
                    --this.cursor;
                    this.bra = this.cursor;
                    this.slice_del();
                }
            }
        }
        this.cursor = this.limit - v_9;
        return true;
    }

    @Override
    public boolean stem() {
        int v_1 = this.cursor;
        if (!this.r_prelude()) {
            // empty if block
        }
        int v_2 = this.cursor = v_1;
        if (!this.r_mark_regions()) {
            // empty if block
        }
        this.limit_backward = this.cursor = v_2;
        this.cursor = this.limit;
        int v_3 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.cursor = this.limit_backward;
        if (!this.r_postlude()) {
            // empty if block
        }
        this.cursor = v_4;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof DutchStemmer;
    }

    public int hashCode() {
        return DutchStemmer.class.getName().hashCode();
    }
}

