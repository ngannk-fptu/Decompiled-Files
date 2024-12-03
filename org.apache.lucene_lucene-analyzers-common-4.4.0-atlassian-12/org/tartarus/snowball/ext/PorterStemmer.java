/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class PorterStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final PorterStemmer methodObject = new PorterStemmer();
    private static final Among[] a_0 = new Among[]{new Among("s", -1, 3, "", methodObject), new Among("ies", 0, 2, "", methodObject), new Among("sses", 0, 1, "", methodObject), new Among("ss", 0, -1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("", -1, 3, "", methodObject), new Among("bb", 0, 2, "", methodObject), new Among("dd", 0, 2, "", methodObject), new Among("ff", 0, 2, "", methodObject), new Among("gg", 0, 2, "", methodObject), new Among("bl", 0, 1, "", methodObject), new Among("mm", 0, 2, "", methodObject), new Among("nn", 0, 2, "", methodObject), new Among("pp", 0, 2, "", methodObject), new Among("rr", 0, 2, "", methodObject), new Among("at", 0, 1, "", methodObject), new Among("tt", 0, 2, "", methodObject), new Among("iz", 0, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ed", -1, 2, "", methodObject), new Among("eed", 0, 1, "", methodObject), new Among("ing", -1, 2, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("anci", -1, 3, "", methodObject), new Among("enci", -1, 2, "", methodObject), new Among("abli", -1, 4, "", methodObject), new Among("eli", -1, 6, "", methodObject), new Among("alli", -1, 9, "", methodObject), new Among("ousli", -1, 12, "", methodObject), new Among("entli", -1, 5, "", methodObject), new Among("aliti", -1, 10, "", methodObject), new Among("biliti", -1, 14, "", methodObject), new Among("iviti", -1, 13, "", methodObject), new Among("tional", -1, 1, "", methodObject), new Among("ational", 10, 8, "", methodObject), new Among("alism", -1, 10, "", methodObject), new Among("ation", -1, 8, "", methodObject), new Among("ization", 13, 7, "", methodObject), new Among("izer", -1, 7, "", methodObject), new Among("ator", -1, 8, "", methodObject), new Among("iveness", -1, 13, "", methodObject), new Among("fulness", -1, 11, "", methodObject), new Among("ousness", -1, 12, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("icate", -1, 2, "", methodObject), new Among("ative", -1, 3, "", methodObject), new Among("alize", -1, 1, "", methodObject), new Among("iciti", -1, 2, "", methodObject), new Among("ical", -1, 2, "", methodObject), new Among("ful", -1, 3, "", methodObject), new Among("ness", -1, 3, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("ic", -1, 1, "", methodObject), new Among("ance", -1, 1, "", methodObject), new Among("ence", -1, 1, "", methodObject), new Among("able", -1, 1, "", methodObject), new Among("ible", -1, 1, "", methodObject), new Among("ate", -1, 1, "", methodObject), new Among("ive", -1, 1, "", methodObject), new Among("ize", -1, 1, "", methodObject), new Among("iti", -1, 1, "", methodObject), new Among("al", -1, 1, "", methodObject), new Among("ism", -1, 1, "", methodObject), new Among("ion", -1, 2, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("ous", -1, 1, "", methodObject), new Among("ant", -1, 1, "", methodObject), new Among("ent", -1, 1, "", methodObject), new Among("ment", 15, 1, "", methodObject), new Among("ement", 16, 1, "", methodObject), new Among("ou", -1, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001'};
    private static final char[] g_v_WXY = new char[]{'\u0001', '\u0011', 'A', '\u00d0', '\u0001'};
    private boolean B_Y_found;
    private int I_p2;
    private int I_p1;

    private void copy_from(PorterStemmer other) {
        this.B_Y_found = other.B_Y_found;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }

    private boolean r_shortv() {
        if (!this.out_grouping_b(g_v_WXY, 89, 121)) {
            return false;
        }
        if (!this.in_grouping_b(g_v, 97, 121)) {
            return false;
        }
        return this.out_grouping_b(g_v, 97, 121);
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_Step_1a() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_0, 4);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("ss");
                break;
            }
            case 2: {
                this.slice_from("i");
                break;
            }
            case 3: {
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_Step_1b() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 3);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        block0 : switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("ee");
                break;
            }
            case 2: {
                int v_1 = this.limit - this.cursor;
                while (!this.in_grouping_b(g_v, 97, 121)) {
                    if (this.cursor <= this.limit_backward) {
                        return false;
                    }
                    --this.cursor;
                }
                this.cursor = this.limit - v_1;
                this.slice_del();
                int v_3 = this.limit - this.cursor;
                among_var = this.find_among_b(a_1, 13);
                if (among_var == 0) {
                    return false;
                }
                this.cursor = this.limit - v_3;
                switch (among_var) {
                    case 0: {
                        return false;
                    }
                    case 1: {
                        int c = this.cursor;
                        this.insert(this.cursor, this.cursor, "e");
                        this.cursor = c;
                        break block0;
                    }
                    case 2: {
                        this.ket = this.cursor;
                        if (this.cursor <= this.limit_backward) {
                            return false;
                        }
                        --this.cursor;
                        this.bra = this.cursor;
                        this.slice_del();
                        break block0;
                    }
                    case 3: {
                        if (this.cursor != this.I_p1) {
                            return false;
                        }
                        int v_4 = this.limit - this.cursor;
                        if (!this.r_shortv()) {
                            return false;
                        }
                        int c = this.cursor = this.limit - v_4;
                        this.insert(this.cursor, this.cursor, "e");
                        this.cursor = c;
                    }
                }
            }
        }
        return true;
    }

    private boolean r_Step_1c() {
        this.ket = this.cursor;
        int v_1 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "y")) {
            this.cursor = this.limit - v_1;
            if (!this.eq_s_b(1, "Y")) {
                return false;
            }
        }
        this.bra = this.cursor;
        while (!this.in_grouping_b(g_v, 97, 121)) {
            if (this.cursor <= this.limit_backward) {
                return false;
            }
            --this.cursor;
        }
        this.slice_from("i");
        return true;
    }

    private boolean r_Step_2() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 20);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("tion");
                break;
            }
            case 2: {
                this.slice_from("ence");
                break;
            }
            case 3: {
                this.slice_from("ance");
                break;
            }
            case 4: {
                this.slice_from("able");
                break;
            }
            case 5: {
                this.slice_from("ent");
                break;
            }
            case 6: {
                this.slice_from("e");
                break;
            }
            case 7: {
                this.slice_from("ize");
                break;
            }
            case 8: {
                this.slice_from("ate");
                break;
            }
            case 9: {
                this.slice_from("al");
                break;
            }
            case 10: {
                this.slice_from("al");
                break;
            }
            case 11: {
                this.slice_from("ful");
                break;
            }
            case 12: {
                this.slice_from("ous");
                break;
            }
            case 13: {
                this.slice_from("ive");
                break;
            }
            case 14: {
                this.slice_from("ble");
            }
        }
        return true;
    }

    private boolean r_Step_3() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_4, 7);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("al");
                break;
            }
            case 2: {
                this.slice_from("ic");
                break;
            }
            case 3: {
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_Step_4() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_5, 19);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R2()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                int v_1 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "s")) {
                    this.cursor = this.limit - v_1;
                    if (!this.eq_s_b(1, "t")) {
                        return false;
                    }
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_Step_5a() {
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "e")) {
            return false;
        }
        this.bra = this.cursor;
        int v_1 = this.limit - this.cursor;
        if (!this.r_R2()) {
            this.cursor = this.limit - v_1;
            if (!this.r_R1()) {
                return false;
            }
            int v_2 = this.limit - this.cursor;
            if (this.r_shortv()) {
                return false;
            }
            this.cursor = this.limit - v_2;
        }
        this.slice_del();
        return true;
    }

    private boolean r_Step_5b() {
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "l")) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R2()) {
            return false;
        }
        if (!this.eq_s_b(1, "l")) {
            return false;
        }
        this.slice_del();
        return true;
    }

    @Override
    public boolean stem() {
        int v_5;
        block23: {
            int v_3;
            this.B_Y_found = false;
            int v_1 = this.cursor;
            this.bra = this.cursor;
            if (this.eq_s(1, "y")) {
                this.ket = this.cursor;
                this.slice_from("Y");
                this.B_Y_found = true;
            }
            int v_2 = this.cursor = v_1;
            block0: while (true) {
                int v_4;
                v_3 = this.cursor;
                while (true) {
                    v_4 = ++this.cursor;
                    if (this.in_grouping(g_v, 97, 121)) {
                        this.bra = this.cursor;
                        if (this.eq_s(1, "y")) break;
                    }
                    this.cursor = v_4;
                    if (this.cursor >= this.limit) break block0;
                }
                this.ket = this.cursor;
                this.cursor = v_4;
                this.slice_from("Y");
                this.B_Y_found = true;
            }
            this.cursor = v_3;
            this.cursor = v_2;
            this.I_p1 = this.limit;
            this.I_p2 = this.limit;
            v_5 = this.cursor;
            while (!this.in_grouping(g_v, 97, 121)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block23;
            }
            while (!this.out_grouping(g_v, 97, 121)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block23;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(g_v, 97, 121)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block23;
            }
            while (!this.out_grouping(g_v, 97, 121)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block23;
            }
            this.I_p2 = this.cursor;
        }
        this.limit_backward = this.cursor = v_5;
        this.cursor = this.limit;
        int v_10 = this.limit - this.cursor;
        if (!this.r_Step_1a()) {
            // empty if block
        }
        this.cursor = this.limit - v_10;
        int v_11 = this.limit - this.cursor;
        if (!this.r_Step_1b()) {
            // empty if block
        }
        this.cursor = this.limit - v_11;
        int v_12 = this.limit - this.cursor;
        if (!this.r_Step_1c()) {
            // empty if block
        }
        this.cursor = this.limit - v_12;
        int v_13 = this.limit - this.cursor;
        if (!this.r_Step_2()) {
            // empty if block
        }
        this.cursor = this.limit - v_13;
        int v_14 = this.limit - this.cursor;
        if (!this.r_Step_3()) {
            // empty if block
        }
        this.cursor = this.limit - v_14;
        int v_15 = this.limit - this.cursor;
        if (!this.r_Step_4()) {
            // empty if block
        }
        this.cursor = this.limit - v_15;
        int v_16 = this.limit - this.cursor;
        if (!this.r_Step_5a()) {
            // empty if block
        }
        this.cursor = this.limit - v_16;
        int v_17 = this.limit - this.cursor;
        if (!this.r_Step_5b()) {
            // empty if block
        }
        this.cursor = this.limit - v_17;
        int v_18 = this.cursor = this.limit_backward;
        if (this.B_Y_found) {
            int v_19;
            block6: while (true) {
                int v_20;
                v_19 = this.cursor;
                while (true) {
                    v_20 = ++this.cursor;
                    this.bra = this.cursor;
                    if (this.eq_s(1, "Y")) break;
                    this.cursor = v_20;
                    if (this.cursor >= this.limit) break block6;
                }
                this.ket = this.cursor;
                this.cursor = v_20;
                this.slice_from("y");
            }
            this.cursor = v_19;
        }
        this.cursor = v_18;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof PorterStemmer;
    }

    public int hashCode() {
        return PorterStemmer.class.getName().hashCode();
    }
}

