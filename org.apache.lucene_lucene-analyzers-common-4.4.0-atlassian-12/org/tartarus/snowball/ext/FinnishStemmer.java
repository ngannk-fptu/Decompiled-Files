/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class FinnishStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final FinnishStemmer methodObject = new FinnishStemmer();
    private static final Among[] a_0 = new Among[]{new Among("pa", -1, 1, "", methodObject), new Among("sti", -1, 2, "", methodObject), new Among("kaan", -1, 1, "", methodObject), new Among("han", -1, 1, "", methodObject), new Among("kin", -1, 1, "", methodObject), new Among("h\u00e4n", -1, 1, "", methodObject), new Among("k\u00e4\u00e4n", -1, 1, "", methodObject), new Among("ko", -1, 1, "", methodObject), new Among("p\u00e4", -1, 1, "", methodObject), new Among("k\u00f6", -1, 1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("lla", -1, -1, "", methodObject), new Among("na", -1, -1, "", methodObject), new Among("ssa", -1, -1, "", methodObject), new Among("ta", -1, -1, "", methodObject), new Among("lta", 3, -1, "", methodObject), new Among("sta", 3, -1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ll\u00e4", -1, -1, "", methodObject), new Among("n\u00e4", -1, -1, "", methodObject), new Among("ss\u00e4", -1, -1, "", methodObject), new Among("t\u00e4", -1, -1, "", methodObject), new Among("lt\u00e4", 3, -1, "", methodObject), new Among("st\u00e4", 3, -1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("lle", -1, -1, "", methodObject), new Among("ine", -1, -1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("nsa", -1, 3, "", methodObject), new Among("mme", -1, 3, "", methodObject), new Among("nne", -1, 3, "", methodObject), new Among("ni", -1, 2, "", methodObject), new Among("si", -1, 1, "", methodObject), new Among("an", -1, 4, "", methodObject), new Among("en", -1, 6, "", methodObject), new Among("\u00e4n", -1, 5, "", methodObject), new Among("ns\u00e4", -1, 3, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("aa", -1, -1, "", methodObject), new Among("ee", -1, -1, "", methodObject), new Among("ii", -1, -1, "", methodObject), new Among("oo", -1, -1, "", methodObject), new Among("uu", -1, -1, "", methodObject), new Among("\u00e4\u00e4", -1, -1, "", methodObject), new Among("\u00f6\u00f6", -1, -1, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("a", -1, 8, "", methodObject), new Among("lla", 0, -1, "", methodObject), new Among("na", 0, -1, "", methodObject), new Among("ssa", 0, -1, "", methodObject), new Among("ta", 0, -1, "", methodObject), new Among("lta", 4, -1, "", methodObject), new Among("sta", 4, -1, "", methodObject), new Among("tta", 4, 9, "", methodObject), new Among("lle", -1, -1, "", methodObject), new Among("ine", -1, -1, "", methodObject), new Among("ksi", -1, -1, "", methodObject), new Among("n", -1, 7, "", methodObject), new Among("han", 11, 1, "", methodObject), new Among("den", 11, -1, "r_VI", methodObject), new Among("seen", 11, -1, "r_LONG", methodObject), new Among("hen", 11, 2, "", methodObject), new Among("tten", 11, -1, "r_VI", methodObject), new Among("hin", 11, 3, "", methodObject), new Among("siin", 11, -1, "r_VI", methodObject), new Among("hon", 11, 4, "", methodObject), new Among("h\u00e4n", 11, 5, "", methodObject), new Among("h\u00f6n", 11, 6, "", methodObject), new Among("\u00e4", -1, 8, "", methodObject), new Among("ll\u00e4", 22, -1, "", methodObject), new Among("n\u00e4", 22, -1, "", methodObject), new Among("ss\u00e4", 22, -1, "", methodObject), new Among("t\u00e4", 22, -1, "", methodObject), new Among("lt\u00e4", 26, -1, "", methodObject), new Among("st\u00e4", 26, -1, "", methodObject), new Among("tt\u00e4", 26, 9, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("eja", -1, -1, "", methodObject), new Among("mma", -1, 1, "", methodObject), new Among("imma", 1, -1, "", methodObject), new Among("mpa", -1, 1, "", methodObject), new Among("impa", 3, -1, "", methodObject), new Among("mmi", -1, 1, "", methodObject), new Among("immi", 5, -1, "", methodObject), new Among("mpi", -1, 1, "", methodObject), new Among("impi", 7, -1, "", methodObject), new Among("ej\u00e4", -1, -1, "", methodObject), new Among("mm\u00e4", -1, 1, "", methodObject), new Among("imm\u00e4", 10, -1, "", methodObject), new Among("mp\u00e4", -1, 1, "", methodObject), new Among("imp\u00e4", 12, -1, "", methodObject)};
    private static final Among[] a_8 = new Among[]{new Among("i", -1, -1, "", methodObject), new Among("j", -1, -1, "", methodObject)};
    private static final Among[] a_9 = new Among[]{new Among("mma", -1, 1, "", methodObject), new Among("imma", 0, -1, "", methodObject)};
    private static final char[] g_AEI = new char[]{'\u0011', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\b'};
    private static final char[] g_V1 = new char[]{'\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\b', '\u0000', ' '};
    private static final char[] g_V2 = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\b', '\u0000', ' '};
    private static final char[] g_particle_end = new char[]{'\u0011', 'a', '\u0018', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\b', '\u0000', ' '};
    private boolean B_ending_removed;
    private StringBuilder S_x = new StringBuilder();
    private int I_p2;
    private int I_p1;

    private void copy_from(FinnishStemmer other) {
        this.B_ending_removed = other.B_ending_removed;
        this.S_x = other.S_x;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }

    private boolean r_mark_regions() {
        this.I_p1 = this.limit;
        this.I_p2 = this.limit;
        while (true) {
            int v_1 = ++this.cursor;
            if (this.in_grouping(g_V1, 97, 246)) {
                this.cursor = v_1;
                break;
            }
            this.cursor = v_1;
            if (this.cursor < this.limit) continue;
            return false;
        }
        while (!this.out_grouping(g_V1, 97, 246)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p1 = this.cursor;
        while (true) {
            int v_3 = ++this.cursor;
            if (this.in_grouping(g_V1, 97, 246)) {
                this.cursor = v_3;
                break;
            }
            this.cursor = v_3;
            if (this.cursor < this.limit) continue;
            return false;
        }
        while (!this.out_grouping(g_V1, 97, 246)) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_p2 = this.cursor;
        return true;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_particle_etc() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_0, 10);
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
                if (this.in_grouping_b(g_particle_end, 97, 246)) break;
                return false;
            }
            case 2: {
                if (this.r_R2()) break;
                return false;
            }
        }
        this.slice_del();
        return true;
    }

    private boolean r_possessive() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_4, 9);
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
                int v_3 = this.limit - this.cursor;
                if (this.eq_s_b(1, "k")) {
                    return false;
                }
                this.cursor = this.limit - v_3;
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
                this.ket = this.cursor;
                if (!this.eq_s_b(3, "kse")) {
                    return false;
                }
                this.bra = this.cursor;
                this.slice_from("ksi");
                break;
            }
            case 3: {
                this.slice_del();
                break;
            }
            case 4: {
                if (this.find_among_b(a_1, 6) == 0) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 5: {
                if (this.find_among_b(a_2, 6) == 0) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 6: {
                if (this.find_among_b(a_3, 2) == 0) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_LONG() {
        return this.find_among_b(a_5, 7) != 0;
    }

    private boolean r_VI() {
        if (!this.eq_s_b(1, "i")) {
            return false;
        }
        return this.in_grouping_b(g_V2, 97, 246);
    }

    private boolean r_case_ending() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_6, 30);
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
                if (this.eq_s_b(1, "a")) break;
                return false;
            }
            case 2: {
                if (this.eq_s_b(1, "e")) break;
                return false;
            }
            case 3: {
                if (this.eq_s_b(1, "i")) break;
                return false;
            }
            case 4: {
                if (this.eq_s_b(1, "o")) break;
                return false;
            }
            case 5: {
                if (this.eq_s_b(1, "\u00e4")) break;
                return false;
            }
            case 6: {
                if (this.eq_s_b(1, "\u00f6")) break;
                return false;
            }
            case 7: {
                int v_3 = this.limit - this.cursor;
                int v_4 = this.limit - this.cursor;
                int v_5 = this.limit - this.cursor;
                if (!this.r_LONG()) {
                    this.cursor = this.limit - v_5;
                    if (!this.eq_s_b(2, "ie")) {
                        this.cursor = this.limit - v_3;
                        break;
                    }
                }
                this.cursor = this.limit - v_4;
                if (this.cursor <= this.limit_backward) {
                    this.cursor = this.limit - v_3;
                    break;
                }
                --this.cursor;
                this.bra = this.cursor;
                break;
            }
            case 8: {
                if (!this.in_grouping_b(g_V1, 97, 246)) {
                    return false;
                }
                if (this.out_grouping_b(g_V1, 97, 246)) break;
                return false;
            }
            case 9: {
                if (this.eq_s_b(1, "e")) break;
                return false;
            }
        }
        this.slice_del();
        this.B_ending_removed = true;
        return true;
    }

    private boolean r_other_endings() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p2) {
            return false;
        }
        this.cursor = this.I_p2;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_7, 14);
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
                int v_3 = this.limit - this.cursor;
                if (this.eq_s_b(2, "po")) {
                    return false;
                }
                this.cursor = this.limit - v_3;
            }
        }
        this.slice_del();
        return true;
    }

    private boolean r_i_plural() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        if (this.find_among_b(a_8, 2) == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_2;
        this.slice_del();
        return true;
    }

    private boolean r_t_plural() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_p1) {
            return false;
        }
        this.cursor = this.I_p1;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        if (!this.eq_s_b(1, "t")) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        int v_3 = this.limit - this.cursor;
        if (!this.in_grouping_b(g_V1, 97, 246)) {
            this.limit_backward = v_2;
            return false;
        }
        this.cursor = this.limit - v_3;
        this.slice_del();
        this.limit_backward = v_2;
        int v_4 = this.limit - this.cursor;
        if (this.cursor < this.I_p2) {
            return false;
        }
        this.cursor = this.I_p2;
        int v_5 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_4;
        int among_var = this.find_among_b(a_9, 2);
        if (among_var == 0) {
            this.limit_backward = v_5;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_5;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                int v_6 = this.limit - this.cursor;
                if (this.eq_s_b(2, "po")) {
                    return false;
                }
                this.cursor = this.limit - v_6;
            }
        }
        this.slice_del();
        return true;
    }

    private boolean r_tidy() {
        int v_9;
        int v_6;
        int v_2;
        block14: {
            block15: {
                int v_1 = this.limit - this.cursor;
                if (this.cursor < this.I_p1) {
                    return false;
                }
                this.cursor = this.I_p1;
                v_2 = this.limit_backward;
                this.limit_backward = this.cursor;
                this.cursor = this.limit - v_1;
                int v_3 = this.limit - this.cursor;
                int v_4 = this.limit - this.cursor;
                if (this.r_LONG()) {
                    this.ket = this.cursor = this.limit - v_4;
                    if (this.cursor > this.limit_backward) {
                        --this.cursor;
                        this.bra = this.cursor;
                        this.slice_del();
                    }
                }
                this.cursor = this.limit - v_3;
                int v_5 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (this.in_grouping_b(g_AEI, 97, 228)) {
                    this.bra = this.cursor;
                    if (this.out_grouping_b(g_V1, 97, 246)) {
                        this.slice_del();
                    }
                }
                this.cursor = this.limit - v_5;
                v_6 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(1, "j")) break block14;
                this.bra = this.cursor;
                int v_7 = this.limit - this.cursor;
                if (this.eq_s_b(1, "o")) break block15;
                this.cursor = this.limit - v_7;
                if (!this.eq_s_b(1, "u")) break block14;
            }
            this.slice_del();
        }
        this.cursor = this.limit - v_6;
        int v_8 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (this.eq_s_b(1, "o")) {
            this.bra = this.cursor;
            if (this.eq_s_b(1, "j")) {
                this.slice_del();
            }
        }
        this.cursor = this.limit - v_8;
        this.limit_backward = v_2;
        while (true) {
            v_9 = this.limit - this.cursor;
            if (this.out_grouping_b(g_V1, 97, 246)) break;
            this.cursor = this.limit - v_9;
            if (this.cursor <= this.limit_backward) {
                return false;
            }
            --this.cursor;
        }
        this.cursor = this.limit - v_9;
        this.ket = this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        this.bra = this.cursor;
        this.S_x = this.slice_to(this.S_x);
        if (!this.eq_v_b(this.S_x)) {
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
        this.cursor = v_1;
        this.B_ending_removed = false;
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        int v_2 = this.limit - this.cursor;
        if (!this.r_particle_etc()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (!this.r_possessive()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (!this.r_case_ending()) {
            // empty if block
        }
        this.cursor = this.limit - v_4;
        int v_5 = this.limit - this.cursor;
        if (!this.r_other_endings()) {
            // empty if block
        }
        this.cursor = this.limit - v_5;
        int v_6 = this.limit - this.cursor;
        if (this.B_ending_removed) {
            int v_7 = this.limit - this.cursor;
            if (!this.r_i_plural()) {
                // empty if block
            }
            this.cursor = this.limit - v_7;
        } else {
            this.cursor = this.limit - v_6;
            int v_8 = this.limit - this.cursor;
            if (!this.r_t_plural()) {
                // empty if block
            }
            this.cursor = this.limit - v_8;
        }
        int v_9 = this.limit - this.cursor;
        if (!this.r_tidy()) {
            // empty if block
        }
        this.cursor = this.limit - v_9;
        this.cursor = this.limit_backward;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof FinnishStemmer;
    }

    public int hashCode() {
        return FinnishStemmer.class.getName().hashCode();
    }
}

