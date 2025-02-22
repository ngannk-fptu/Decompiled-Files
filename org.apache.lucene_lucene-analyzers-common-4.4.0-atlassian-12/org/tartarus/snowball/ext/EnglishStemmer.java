/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class EnglishStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final EnglishStemmer methodObject = new EnglishStemmer();
    private static final Among[] a_0 = new Among[]{new Among("arsen", -1, -1, "", methodObject), new Among("commun", -1, -1, "", methodObject), new Among("gener", -1, -1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("'", -1, 1, "", methodObject), new Among("'s'", 0, 1, "", methodObject), new Among("'s", -1, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ied", -1, 2, "", methodObject), new Among("s", -1, 3, "", methodObject), new Among("ies", 1, 2, "", methodObject), new Among("sses", 1, 1, "", methodObject), new Among("ss", 1, -1, "", methodObject), new Among("us", 1, -1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("", -1, 3, "", methodObject), new Among("bb", 0, 2, "", methodObject), new Among("dd", 0, 2, "", methodObject), new Among("ff", 0, 2, "", methodObject), new Among("gg", 0, 2, "", methodObject), new Among("bl", 0, 1, "", methodObject), new Among("mm", 0, 2, "", methodObject), new Among("nn", 0, 2, "", methodObject), new Among("pp", 0, 2, "", methodObject), new Among("rr", 0, 2, "", methodObject), new Among("at", 0, 1, "", methodObject), new Among("tt", 0, 2, "", methodObject), new Among("iz", 0, 1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("ed", -1, 2, "", methodObject), new Among("eed", 0, 1, "", methodObject), new Among("ing", -1, 2, "", methodObject), new Among("edly", -1, 2, "", methodObject), new Among("eedly", 3, 1, "", methodObject), new Among("ingly", -1, 2, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("anci", -1, 3, "", methodObject), new Among("enci", -1, 2, "", methodObject), new Among("ogi", -1, 13, "", methodObject), new Among("li", -1, 16, "", methodObject), new Among("bli", 3, 12, "", methodObject), new Among("abli", 4, 4, "", methodObject), new Among("alli", 3, 8, "", methodObject), new Among("fulli", 3, 14, "", methodObject), new Among("lessli", 3, 15, "", methodObject), new Among("ousli", 3, 10, "", methodObject), new Among("entli", 3, 5, "", methodObject), new Among("aliti", -1, 8, "", methodObject), new Among("biliti", -1, 12, "", methodObject), new Among("iviti", -1, 11, "", methodObject), new Among("tional", -1, 1, "", methodObject), new Among("ational", 14, 7, "", methodObject), new Among("alism", -1, 8, "", methodObject), new Among("ation", -1, 7, "", methodObject), new Among("ization", 17, 6, "", methodObject), new Among("izer", -1, 6, "", methodObject), new Among("ator", -1, 7, "", methodObject), new Among("iveness", -1, 11, "", methodObject), new Among("fulness", -1, 9, "", methodObject), new Among("ousness", -1, 10, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("icate", -1, 4, "", methodObject), new Among("ative", -1, 6, "", methodObject), new Among("alize", -1, 3, "", methodObject), new Among("iciti", -1, 4, "", methodObject), new Among("ical", -1, 4, "", methodObject), new Among("tional", -1, 1, "", methodObject), new Among("ational", 5, 2, "", methodObject), new Among("ful", -1, 5, "", methodObject), new Among("ness", -1, 5, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("ic", -1, 1, "", methodObject), new Among("ance", -1, 1, "", methodObject), new Among("ence", -1, 1, "", methodObject), new Among("able", -1, 1, "", methodObject), new Among("ible", -1, 1, "", methodObject), new Among("ate", -1, 1, "", methodObject), new Among("ive", -1, 1, "", methodObject), new Among("ize", -1, 1, "", methodObject), new Among("iti", -1, 1, "", methodObject), new Among("al", -1, 1, "", methodObject), new Among("ism", -1, 1, "", methodObject), new Among("ion", -1, 2, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("ous", -1, 1, "", methodObject), new Among("ant", -1, 1, "", methodObject), new Among("ent", -1, 1, "", methodObject), new Among("ment", 15, 1, "", methodObject), new Among("ement", 16, 1, "", methodObject)};
    private static final Among[] a_8 = new Among[]{new Among("e", -1, 1, "", methodObject), new Among("l", -1, 2, "", methodObject)};
    private static final Among[] a_9 = new Among[]{new Among("succeed", -1, -1, "", methodObject), new Among("proceed", -1, -1, "", methodObject), new Among("exceed", -1, -1, "", methodObject), new Among("canning", -1, -1, "", methodObject), new Among("inning", -1, -1, "", methodObject), new Among("earring", -1, -1, "", methodObject), new Among("herring", -1, -1, "", methodObject), new Among("outing", -1, -1, "", methodObject)};
    private static final Among[] a_10 = new Among[]{new Among("andes", -1, -1, "", methodObject), new Among("atlas", -1, -1, "", methodObject), new Among("bias", -1, -1, "", methodObject), new Among("cosmos", -1, -1, "", methodObject), new Among("dying", -1, 3, "", methodObject), new Among("early", -1, 9, "", methodObject), new Among("gently", -1, 7, "", methodObject), new Among("howe", -1, -1, "", methodObject), new Among("idly", -1, 6, "", methodObject), new Among("lying", -1, 4, "", methodObject), new Among("news", -1, -1, "", methodObject), new Among("only", -1, 10, "", methodObject), new Among("singly", -1, 11, "", methodObject), new Among("skies", -1, 2, "", methodObject), new Among("skis", -1, 1, "", methodObject), new Among("sky", -1, -1, "", methodObject), new Among("tying", -1, 5, "", methodObject), new Among("ugly", -1, 8, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001'};
    private static final char[] g_v_WXY = new char[]{'\u0001', '\u0011', 'A', '\u00d0', '\u0001'};
    private static final char[] g_valid_LI = new char[]{'7', '\u008d', '\u0002'};
    private boolean B_Y_found;
    private int I_p2;
    private int I_p1;

    private void copy_from(EnglishStemmer other) {
        this.B_Y_found = other.B_Y_found;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }

    private boolean r_prelude() {
        int v_4;
        this.B_Y_found = false;
        int v_1 = this.cursor;
        this.bra = this.cursor;
        if (this.eq_s(1, "'")) {
            this.ket = this.cursor;
            this.slice_del();
        }
        int v_2 = this.cursor = v_1;
        this.bra = this.cursor;
        if (this.eq_s(1, "y")) {
            this.ket = this.cursor;
            this.slice_from("Y");
            this.B_Y_found = true;
        }
        int v_3 = this.cursor = v_2;
        block0: while (true) {
            int v_5;
            v_4 = this.cursor;
            while (true) {
                v_5 = ++this.cursor;
                if (this.in_grouping(g_v, 97, 121)) {
                    this.bra = this.cursor;
                    if (this.eq_s(1, "y")) break;
                }
                this.cursor = v_5;
                if (this.cursor >= this.limit) break block0;
            }
            this.ket = this.cursor;
            this.cursor = v_5;
            this.slice_from("Y");
            this.B_Y_found = true;
        }
        this.cursor = v_4;
        this.cursor = v_3;
        return true;
    }

    private boolean r_mark_regions() {
        int v_1;
        block9: {
            this.I_p1 = this.limit;
            this.I_p2 = this.limit;
            v_1 = this.cursor;
            int v_2 = this.cursor;
            if (this.find_among(a_0, 3) == 0) {
                this.cursor = v_2;
                while (!this.in_grouping(g_v, 97, 121)) {
                    if (this.cursor < this.limit) {
                        ++this.cursor;
                        continue;
                    }
                    break block9;
                }
                while (!this.out_grouping(g_v, 97, 121)) {
                    if (this.cursor < this.limit) {
                        ++this.cursor;
                        continue;
                    }
                    break block9;
                }
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(g_v, 97, 121)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block9;
            }
            while (!this.out_grouping(g_v, 97, 121)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block9;
            }
            this.I_p2 = this.cursor;
        }
        this.cursor = v_1;
        return true;
    }

    private boolean r_shortv() {
        int v_1 = this.limit - this.cursor;
        if (!(this.out_grouping_b(g_v_WXY, 89, 121) && this.in_grouping_b(g_v, 97, 121) && this.out_grouping_b(g_v, 97, 121))) {
            this.cursor = this.limit - v_1;
            if (!this.out_grouping_b(g_v, 97, 121)) {
                return false;
            }
            if (!this.in_grouping_b(g_v, 97, 121)) {
                return false;
            }
            if (this.cursor > this.limit_backward) {
                return false;
            }
        }
        return true;
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_Step_1a() {
        int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 3);
        if (among_var == 0) {
            this.cursor = this.limit - v_1;
        } else {
            this.bra = this.cursor;
            switch (among_var) {
                case 0: {
                    this.cursor = this.limit - v_1;
                    break;
                }
                case 1: {
                    this.slice_del();
                }
            }
        }
        this.ket = this.cursor;
        among_var = this.find_among_b(a_2, 6);
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
                int v_2 = this.limit - this.cursor;
                int c = this.cursor - 2;
                if (this.limit_backward <= c && c <= this.limit) {
                    this.cursor = c;
                    this.slice_from("i");
                    break;
                }
                this.cursor = this.limit - v_2;
                this.slice_from("ie");
                break;
            }
            case 3: {
                if (this.cursor <= this.limit_backward) {
                    return false;
                }
                --this.cursor;
                while (!this.in_grouping_b(g_v, 97, 121)) {
                    if (this.cursor <= this.limit_backward) {
                        return false;
                    }
                    --this.cursor;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_Step_1b() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_4, 6);
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
                among_var = this.find_among_b(a_3, 13);
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
        if (!this.out_grouping_b(g_v, 97, 121)) {
            return false;
        }
        int v_2 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        this.cursor = this.limit - v_2;
        this.slice_from("i");
        return true;
    }

    private boolean r_Step_2() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_5, 24);
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
                this.slice_from("ize");
                break;
            }
            case 7: {
                this.slice_from("ate");
                break;
            }
            case 8: {
                this.slice_from("al");
                break;
            }
            case 9: {
                this.slice_from("ful");
                break;
            }
            case 10: {
                this.slice_from("ous");
                break;
            }
            case 11: {
                this.slice_from("ive");
                break;
            }
            case 12: {
                this.slice_from("ble");
                break;
            }
            case 13: {
                if (!this.eq_s_b(1, "l")) {
                    return false;
                }
                this.slice_from("og");
                break;
            }
            case 14: {
                this.slice_from("ful");
                break;
            }
            case 15: {
                this.slice_from("less");
                break;
            }
            case 16: {
                if (!this.in_grouping_b(g_valid_LI, 99, 116)) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_Step_3() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_6, 9);
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
                this.slice_from("ate");
                break;
            }
            case 3: {
                this.slice_from("al");
                break;
            }
            case 4: {
                this.slice_from("ic");
                break;
            }
            case 5: {
                this.slice_del();
                break;
            }
            case 6: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_Step_4() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_7, 18);
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

    private boolean r_Step_5() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_8, 2);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
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
                break;
            }
            case 2: {
                if (!this.r_R2()) {
                    return false;
                }
                if (!this.eq_s_b(1, "l")) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_exception2() {
        this.ket = this.cursor;
        if (this.find_among_b(a_9, 8) == 0) {
            return false;
        }
        this.bra = this.cursor;
        return this.cursor <= this.limit_backward;
    }

    private boolean r_exception1() {
        this.bra = this.cursor;
        int among_var = this.find_among(a_10, 18);
        if (among_var == 0) {
            return false;
        }
        this.ket = this.cursor;
        if (this.cursor < this.limit) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("ski");
                break;
            }
            case 2: {
                this.slice_from("sky");
                break;
            }
            case 3: {
                this.slice_from("die");
                break;
            }
            case 4: {
                this.slice_from("lie");
                break;
            }
            case 5: {
                this.slice_from("tie");
                break;
            }
            case 6: {
                this.slice_from("idl");
                break;
            }
            case 7: {
                this.slice_from("gentl");
                break;
            }
            case 8: {
                this.slice_from("ugli");
                break;
            }
            case 9: {
                this.slice_from("earli");
                break;
            }
            case 10: {
                this.slice_from("onli");
                break;
            }
            case 11: {
                this.slice_from("singl");
            }
        }
        return true;
    }

    private boolean r_postlude() {
        int v_1;
        if (!this.B_Y_found) {
            return false;
        }
        block0: while (true) {
            int v_2;
            v_1 = this.cursor;
            while (true) {
                v_2 = ++this.cursor;
                this.bra = this.cursor;
                if (this.eq_s(1, "Y")) break;
                this.cursor = v_2;
                if (this.cursor >= this.limit) break block0;
            }
            this.ket = this.cursor;
            this.cursor = v_2;
            this.slice_from("y");
        }
        this.cursor = v_1;
        return true;
    }

    @Override
    public boolean stem() {
        int v_1 = this.cursor;
        if (!this.r_exception1()) {
            int v_2 = this.cursor = v_1;
            int c = this.cursor + 3;
            if (0 > c || c > this.limit) {
                this.cursor = v_2;
            } else {
                this.cursor = c;
                int v_3 = this.cursor = v_1;
                if (!this.r_prelude()) {
                    // empty if block
                }
                int v_4 = this.cursor = v_3;
                if (!this.r_mark_regions()) {
                    // empty if block
                }
                this.limit_backward = this.cursor = v_4;
                this.cursor = this.limit;
                int v_5 = this.limit - this.cursor;
                if (!this.r_Step_1a()) {
                    // empty if block
                }
                this.cursor = this.limit - v_5;
                int v_6 = this.limit - this.cursor;
                if (!this.r_exception2()) {
                    this.cursor = this.limit - v_6;
                    int v_7 = this.limit - this.cursor;
                    if (!this.r_Step_1b()) {
                        // empty if block
                    }
                    this.cursor = this.limit - v_7;
                    int v_8 = this.limit - this.cursor;
                    if (!this.r_Step_1c()) {
                        // empty if block
                    }
                    this.cursor = this.limit - v_8;
                    int v_9 = this.limit - this.cursor;
                    if (!this.r_Step_2()) {
                        // empty if block
                    }
                    this.cursor = this.limit - v_9;
                    int v_10 = this.limit - this.cursor;
                    if (!this.r_Step_3()) {
                        // empty if block
                    }
                    this.cursor = this.limit - v_10;
                    int v_11 = this.limit - this.cursor;
                    if (!this.r_Step_4()) {
                        // empty if block
                    }
                    this.cursor = this.limit - v_11;
                    int v_12 = this.limit - this.cursor;
                    if (!this.r_Step_5()) {
                        // empty if block
                    }
                    this.cursor = this.limit - v_12;
                }
                int v_13 = this.cursor = this.limit_backward;
                if (!this.r_postlude()) {
                    // empty if block
                }
                this.cursor = v_13;
            }
        }
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof EnglishStemmer;
    }

    public int hashCode() {
        return EnglishStemmer.class.getName().hashCode();
    }
}

