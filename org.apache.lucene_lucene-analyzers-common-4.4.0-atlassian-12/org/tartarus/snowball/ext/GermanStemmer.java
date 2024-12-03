/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class GermanStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final GermanStemmer methodObject = new GermanStemmer();
    private static final Among[] a_0 = new Among[]{new Among("", -1, 6, "", methodObject), new Among("U", 0, 2, "", methodObject), new Among("Y", 0, 1, "", methodObject), new Among("\u00e4", 0, 3, "", methodObject), new Among("\u00f6", 0, 4, "", methodObject), new Among("\u00fc", 0, 5, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("e", -1, 1, "", methodObject), new Among("em", -1, 1, "", methodObject), new Among("en", -1, 1, "", methodObject), new Among("ern", -1, 1, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("s", -1, 2, "", methodObject), new Among("es", 5, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("en", -1, 1, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("st", -1, 2, "", methodObject), new Among("est", 2, 1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("ig", -1, 1, "", methodObject), new Among("lich", -1, 1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("end", -1, 1, "", methodObject), new Among("ig", -1, 2, "", methodObject), new Among("ung", -1, 1, "", methodObject), new Among("lich", -1, 3, "", methodObject), new Among("isch", -1, 2, "", methodObject), new Among("ik", -1, 2, "", methodObject), new Among("heit", -1, 3, "", methodObject), new Among("keit", -1, 4, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\b', '\u0000', ' ', '\b'};
    private static final char[] g_s_ending = new char[]{'u', '\u001e', '\u0005'};
    private static final char[] g_st_ending = new char[]{'u', '\u001e', '\u0004'};
    private int I_x;
    private int I_p2;
    private int I_p1;

    private void copy_from(GermanStemmer other) {
        this.I_x = other.I_x;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }

    private boolean r_prelude() {
        int v_4;
        int v_2;
        int v_1 = this.cursor;
        while (true) {
            v_2 = ++this.cursor;
            int v_3 = this.cursor;
            this.bra = this.cursor;
            if (this.eq_s(1, "\u00df")) {
                this.ket = this.cursor;
                this.slice_from("ss");
                continue;
            }
            this.cursor = v_3;
            if (this.cursor >= this.limit) break;
        }
        this.cursor = v_2;
        this.cursor = v_1;
        block1: while (true) {
            v_4 = this.cursor;
            while (true) {
                int v_5;
                block7: {
                    block9: {
                        int v_6;
                        block8: {
                            v_5 = ++this.cursor;
                            if (!this.in_grouping(g_v, 97, 252)) break block7;
                            this.bra = this.cursor;
                            v_6 = this.cursor;
                            if (!this.eq_s(1, "u")) break block8;
                            this.ket = this.cursor;
                            if (!this.in_grouping(g_v, 97, 252)) break block8;
                            this.slice_from("U");
                            break block9;
                        }
                        this.cursor = v_6;
                        if (!this.eq_s(1, "y")) break block7;
                        this.ket = this.cursor;
                        if (!this.in_grouping(g_v, 97, 252)) break block7;
                        this.slice_from("Y");
                    }
                    this.cursor = v_5;
                    continue block1;
                }
                this.cursor = v_5;
                if (this.cursor >= this.limit) break block1;
            }
            break;
        }
        this.cursor = v_4;
        return true;
    }

    private boolean r_mark_regions() {
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        int v_1 = this.cursor;
        int c = this.cursor + 3;
        if (0 > c || c > this.limit) {
            return false;
        }
        this.I_x = this.cursor = c;
        this.cursor = v_1;
        while (!this.in_grouping(g_v, 97, 252)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(g_v, 97, 252)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p1 = this.cursor;
        if (this.I_p1 < this.I_x) {
            this.I_p1 = this.I_x;
        }
        while (!this.in_grouping(g_v, 97, 252)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        while (!this.out_grouping(g_v, 97, 252)) {
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
        block9: while (true) {
            v_1 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_0, 6);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block9;
                }
                case 1: {
                    this.slice_from("y");
                    break;
                }
                case 2: {
                    this.slice_from("u");
                    break;
                }
                case 3: {
                    this.slice_from("a");
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
        this.cursor = v_1;
        return true;
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_standard_suffix() {
        int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 7);
        if (among_var != 0) {
            this.bra = this.cursor;
            if (this.r_R1()) {
                switch (among_var) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        if (!this.in_grouping_b(g_s_ending, 98, 116)) break;
                        this.slice_del();
                    }
                }
            }
        }
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        this.ket = this.cursor;
        among_var = this.find_among_b(a_2, 4);
        if (among_var != 0) {
            this.bra = this.cursor;
            if (this.r_R1()) {
                switch (among_var) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        int c;
                        if (!this.in_grouping_b(g_st_ending, 98, 116) || this.limit_backward > (c = this.cursor - 3) || c > this.limit) break;
                        this.cursor = c;
                        this.slice_del();
                    }
                }
            }
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        this.ket = this.cursor;
        among_var = this.find_among_b(a_4, 8);
        if (among_var != 0) {
            this.bra = this.cursor;
            if (this.r_R2()) {
                block10 : switch (among_var) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        this.slice_del();
                        int v_4 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        if (!this.eq_s_b(2, "ig")) {
                            this.cursor = this.limit - v_4;
                            break;
                        }
                        this.bra = this.cursor;
                        int v_5 = this.limit - this.cursor;
                        if (this.eq_s_b(1, "e")) {
                            this.cursor = this.limit - v_4;
                            break;
                        }
                        this.cursor = this.limit - v_5;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_4;
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        int v_6 = this.limit - this.cursor;
                        if (this.eq_s_b(1, "e")) break;
                        this.cursor = this.limit - v_6;
                        this.slice_del();
                        break;
                    }
                    case 3: {
                        this.slice_del();
                        int v_7 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        int v_8 = this.limit - this.cursor;
                        if (!this.eq_s_b(2, "er")) {
                            this.cursor = this.limit - v_8;
                            if (!this.eq_s_b(2, "en")) {
                                this.cursor = this.limit - v_7;
                                break;
                            }
                        }
                        this.bra = this.cursor;
                        if (!this.r_R1()) {
                            this.cursor = this.limit - v_7;
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 4: {
                        this.slice_del();
                        int v_9 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        among_var = this.find_among_b(a_3, 2);
                        if (among_var == 0) {
                            this.cursor = this.limit - v_9;
                            break;
                        }
                        this.bra = this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_9;
                            break;
                        }
                        switch (among_var) {
                            case 0: {
                                this.cursor = this.limit - v_9;
                                break block10;
                            }
                            case 1: {
                                this.slice_del();
                            }
                        }
                    }
                }
            }
        }
        this.cursor = this.limit - v_3;
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
        return o instanceof GermanStemmer;
    }

    public int hashCode() {
        return GermanStemmer.class.getName().hashCode();
    }
}

