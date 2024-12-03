/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class HungarianStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final HungarianStemmer methodObject = new HungarianStemmer();
    private static final Among[] a_0 = new Among[]{new Among("cs", -1, -1, "", methodObject), new Among("dzs", -1, -1, "", methodObject), new Among("gy", -1, -1, "", methodObject), new Among("ly", -1, -1, "", methodObject), new Among("ny", -1, -1, "", methodObject), new Among("sz", -1, -1, "", methodObject), new Among("ty", -1, -1, "", methodObject), new Among("zs", -1, -1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("\u00e1", -1, 1, "", methodObject), new Among("\u00e9", -1, 2, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("bb", -1, -1, "", methodObject), new Among("cc", -1, -1, "", methodObject), new Among("dd", -1, -1, "", methodObject), new Among("ff", -1, -1, "", methodObject), new Among("gg", -1, -1, "", methodObject), new Among("jj", -1, -1, "", methodObject), new Among("kk", -1, -1, "", methodObject), new Among("ll", -1, -1, "", methodObject), new Among("mm", -1, -1, "", methodObject), new Among("nn", -1, -1, "", methodObject), new Among("pp", -1, -1, "", methodObject), new Among("rr", -1, -1, "", methodObject), new Among("ccs", -1, -1, "", methodObject), new Among("ss", -1, -1, "", methodObject), new Among("zzs", -1, -1, "", methodObject), new Among("tt", -1, -1, "", methodObject), new Among("vv", -1, -1, "", methodObject), new Among("ggy", -1, -1, "", methodObject), new Among("lly", -1, -1, "", methodObject), new Among("nny", -1, -1, "", methodObject), new Among("tty", -1, -1, "", methodObject), new Among("ssz", -1, -1, "", methodObject), new Among("zz", -1, -1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("al", -1, 1, "", methodObject), new Among("el", -1, 2, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("ba", -1, -1, "", methodObject), new Among("ra", -1, -1, "", methodObject), new Among("be", -1, -1, "", methodObject), new Among("re", -1, -1, "", methodObject), new Among("ig", -1, -1, "", methodObject), new Among("nak", -1, -1, "", methodObject), new Among("nek", -1, -1, "", methodObject), new Among("val", -1, -1, "", methodObject), new Among("vel", -1, -1, "", methodObject), new Among("ul", -1, -1, "", methodObject), new Among("n\u00e1l", -1, -1, "", methodObject), new Among("n\u00e9l", -1, -1, "", methodObject), new Among("b\u00f3l", -1, -1, "", methodObject), new Among("r\u00f3l", -1, -1, "", methodObject), new Among("t\u00f3l", -1, -1, "", methodObject), new Among("b\u00f5l", -1, -1, "", methodObject), new Among("r\u00f5l", -1, -1, "", methodObject), new Among("t\u00f5l", -1, -1, "", methodObject), new Among("\u00fcl", -1, -1, "", methodObject), new Among("n", -1, -1, "", methodObject), new Among("an", 19, -1, "", methodObject), new Among("ban", 20, -1, "", methodObject), new Among("en", 19, -1, "", methodObject), new Among("ben", 22, -1, "", methodObject), new Among("k\u00e9ppen", 22, -1, "", methodObject), new Among("on", 19, -1, "", methodObject), new Among("\u00f6n", 19, -1, "", methodObject), new Among("k\u00e9pp", -1, -1, "", methodObject), new Among("kor", -1, -1, "", methodObject), new Among("t", -1, -1, "", methodObject), new Among("at", 29, -1, "", methodObject), new Among("et", 29, -1, "", methodObject), new Among("k\u00e9nt", 29, -1, "", methodObject), new Among("ank\u00e9nt", 32, -1, "", methodObject), new Among("enk\u00e9nt", 32, -1, "", methodObject), new Among("onk\u00e9nt", 32, -1, "", methodObject), new Among("ot", 29, -1, "", methodObject), new Among("\u00e9rt", 29, -1, "", methodObject), new Among("\u00f6t", 29, -1, "", methodObject), new Among("hez", -1, -1, "", methodObject), new Among("hoz", -1, -1, "", methodObject), new Among("h\u00f6z", -1, -1, "", methodObject), new Among("v\u00e1", -1, -1, "", methodObject), new Among("v\u00e9", -1, -1, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("\u00e1n", -1, 2, "", methodObject), new Among("\u00e9n", -1, 1, "", methodObject), new Among("\u00e1nk\u00e9nt", -1, 3, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("stul", -1, 2, "", methodObject), new Among("astul", 0, 1, "", methodObject), new Among("\u00e1stul", 0, 3, "", methodObject), new Among("st\u00fcl", -1, 2, "", methodObject), new Among("est\u00fcl", 3, 1, "", methodObject), new Among("\u00e9st\u00fcl", 3, 4, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("\u00e1", -1, 1, "", methodObject), new Among("\u00e9", -1, 2, "", methodObject)};
    private static final Among[] a_8 = new Among[]{new Among("k", -1, 7, "", methodObject), new Among("ak", 0, 4, "", methodObject), new Among("ek", 0, 6, "", methodObject), new Among("ok", 0, 5, "", methodObject), new Among("\u00e1k", 0, 1, "", methodObject), new Among("\u00e9k", 0, 2, "", methodObject), new Among("\u00f6k", 0, 3, "", methodObject)};
    private static final Among[] a_9 = new Among[]{new Among("\u00e9i", -1, 7, "", methodObject), new Among("\u00e1\u00e9i", 0, 6, "", methodObject), new Among("\u00e9\u00e9i", 0, 5, "", methodObject), new Among("\u00e9", -1, 9, "", methodObject), new Among("k\u00e9", 3, 4, "", methodObject), new Among("ak\u00e9", 4, 1, "", methodObject), new Among("ek\u00e9", 4, 1, "", methodObject), new Among("ok\u00e9", 4, 1, "", methodObject), new Among("\u00e1k\u00e9", 4, 3, "", methodObject), new Among("\u00e9k\u00e9", 4, 2, "", methodObject), new Among("\u00f6k\u00e9", 4, 1, "", methodObject), new Among("\u00e9\u00e9", 3, 8, "", methodObject)};
    private static final Among[] a_10 = new Among[]{new Among("a", -1, 18, "", methodObject), new Among("ja", 0, 17, "", methodObject), new Among("d", -1, 16, "", methodObject), new Among("ad", 2, 13, "", methodObject), new Among("ed", 2, 13, "", methodObject), new Among("od", 2, 13, "", methodObject), new Among("\u00e1d", 2, 14, "", methodObject), new Among("\u00e9d", 2, 15, "", methodObject), new Among("\u00f6d", 2, 13, "", methodObject), new Among("e", -1, 18, "", methodObject), new Among("je", 9, 17, "", methodObject), new Among("nk", -1, 4, "", methodObject), new Among("unk", 11, 1, "", methodObject), new Among("\u00e1nk", 11, 2, "", methodObject), new Among("\u00e9nk", 11, 3, "", methodObject), new Among("\u00fcnk", 11, 1, "", methodObject), new Among("uk", -1, 8, "", methodObject), new Among("juk", 16, 7, "", methodObject), new Among("\u00e1juk", 17, 5, "", methodObject), new Among("\u00fck", -1, 8, "", methodObject), new Among("j\u00fck", 19, 7, "", methodObject), new Among("\u00e9j\u00fck", 20, 6, "", methodObject), new Among("m", -1, 12, "", methodObject), new Among("am", 22, 9, "", methodObject), new Among("em", 22, 9, "", methodObject), new Among("om", 22, 9, "", methodObject), new Among("\u00e1m", 22, 10, "", methodObject), new Among("\u00e9m", 22, 11, "", methodObject), new Among("o", -1, 18, "", methodObject), new Among("\u00e1", -1, 19, "", methodObject), new Among("\u00e9", -1, 20, "", methodObject)};
    private static final Among[] a_11 = new Among[]{new Among("id", -1, 10, "", methodObject), new Among("aid", 0, 9, "", methodObject), new Among("jaid", 1, 6, "", methodObject), new Among("eid", 0, 9, "", methodObject), new Among("jeid", 3, 6, "", methodObject), new Among("\u00e1id", 0, 7, "", methodObject), new Among("\u00e9id", 0, 8, "", methodObject), new Among("i", -1, 15, "", methodObject), new Among("ai", 7, 14, "", methodObject), new Among("jai", 8, 11, "", methodObject), new Among("ei", 7, 14, "", methodObject), new Among("jei", 10, 11, "", methodObject), new Among("\u00e1i", 7, 12, "", methodObject), new Among("\u00e9i", 7, 13, "", methodObject), new Among("itek", -1, 24, "", methodObject), new Among("eitek", 14, 21, "", methodObject), new Among("jeitek", 15, 20, "", methodObject), new Among("\u00e9itek", 14, 23, "", methodObject), new Among("ik", -1, 29, "", methodObject), new Among("aik", 18, 26, "", methodObject), new Among("jaik", 19, 25, "", methodObject), new Among("eik", 18, 26, "", methodObject), new Among("jeik", 21, 25, "", methodObject), new Among("\u00e1ik", 18, 27, "", methodObject), new Among("\u00e9ik", 18, 28, "", methodObject), new Among("ink", -1, 20, "", methodObject), new Among("aink", 25, 17, "", methodObject), new Among("jaink", 26, 16, "", methodObject), new Among("eink", 25, 17, "", methodObject), new Among("jeink", 28, 16, "", methodObject), new Among("\u00e1ink", 25, 18, "", methodObject), new Among("\u00e9ink", 25, 19, "", methodObject), new Among("aitok", -1, 21, "", methodObject), new Among("jaitok", 32, 20, "", methodObject), new Among("\u00e1itok", -1, 22, "", methodObject), new Among("im", -1, 5, "", methodObject), new Among("aim", 35, 4, "", methodObject), new Among("jaim", 36, 1, "", methodObject), new Among("eim", 35, 4, "", methodObject), new Among("jeim", 38, 1, "", methodObject), new Among("\u00e1im", 35, 2, "", methodObject), new Among("\u00e9im", 35, 3, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001', '\u0011', '4', '\u000e'};
    private int I_p1;

    private void copy_from(HungarianStemmer other) {
        this.I_p1 = other.I_p1;
        super.copy_from(other);
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_mark_regions() {
        int v_1;
        block9: {
            block10: {
                int v_2;
                block8: {
                    this.I_p1 = this.limit;
                    v_1 = this.cursor;
                    if (!this.in_grouping(g_v, 97, 252)) break block9;
                    do {
                        v_2 = ++this.cursor;
                        if (this.out_grouping(g_v, 97, 252)) break block8;
                        this.cursor = v_2;
                    } while (this.cursor < this.limit);
                    break block9;
                }
                this.cursor = v_2;
                int v_3 = this.cursor;
                if (this.find_among(a_0, 8) != 0) break block10;
                this.cursor = v_3;
                if (this.cursor >= this.limit) break block9;
                ++this.cursor;
            }
            this.I_p1 = this.cursor;
            return true;
        }
        this.cursor = v_1;
        if (!this.out_grouping(g_v, 97, 252)) {
            return false;
        }
        while (true) {
            if (this.in_grouping(g_v, 97, 252)) {
                this.I_p1 = this.cursor;
                return true;
            }
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_v_ending() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 2);
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
                this.slice_from("a");
                break;
            }
            case 2: {
                this.slice_from("e");
            }
        }
        return true;
    }

    private boolean r_double() {
        int v_1 = this.limit - this.cursor;
        if (this.find_among_b(a_2, 23) == 0) {
            return false;
        }
        this.cursor = this.limit - v_1;
        return true;
    }

    private boolean r_undouble() {
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        this.ket = this.cursor;
        int c = this.cursor - 1;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.bra = this.cursor = c;
        this.slice_del();
        return true;
    }

    private boolean r_instrum() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 2);
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
                if (this.r_double()) break;
                return false;
            }
            case 2: {
                if (this.r_double()) break;
                return false;
            }
        }
        this.slice_del();
        return this.r_undouble();
    }

    private boolean r_case() {
        this.ket = this.cursor;
        if (this.find_among_b(a_4, 44) == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        this.slice_del();
        return this.r_v_ending();
    }

    private boolean r_case_special() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_5, 3);
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
                this.slice_from("e");
                break;
            }
            case 2: {
                this.slice_from("a");
                break;
            }
            case 3: {
                this.slice_from("a");
            }
        }
        return true;
    }

    private boolean r_case_other() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_6, 6);
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
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_from("a");
                break;
            }
            case 4: {
                this.slice_from("e");
            }
        }
        return true;
    }

    private boolean r_factive() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_7, 2);
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
                if (this.r_double()) break;
                return false;
            }
            case 2: {
                if (this.r_double()) break;
                return false;
            }
        }
        this.slice_del();
        return this.r_undouble();
    }

    private boolean r_plural() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_8, 7);
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
                this.slice_from("a");
                break;
            }
            case 2: {
                this.slice_from("e");
                break;
            }
            case 3: {
                this.slice_del();
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_del();
                break;
            }
            case 6: {
                this.slice_del();
                break;
            }
            case 7: {
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_owned() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_9, 12);
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
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("e");
                break;
            }
            case 3: {
                this.slice_from("a");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("e");
                break;
            }
            case 6: {
                this.slice_from("a");
                break;
            }
            case 7: {
                this.slice_del();
                break;
            }
            case 8: {
                this.slice_from("e");
                break;
            }
            case 9: {
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_sing_owner() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_10, 31);
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
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("a");
                break;
            }
            case 3: {
                this.slice_from("e");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("a");
                break;
            }
            case 6: {
                this.slice_from("e");
                break;
            }
            case 7: {
                this.slice_del();
                break;
            }
            case 8: {
                this.slice_del();
                break;
            }
            case 9: {
                this.slice_del();
                break;
            }
            case 10: {
                this.slice_from("a");
                break;
            }
            case 11: {
                this.slice_from("e");
                break;
            }
            case 12: {
                this.slice_del();
                break;
            }
            case 13: {
                this.slice_del();
                break;
            }
            case 14: {
                this.slice_from("a");
                break;
            }
            case 15: {
                this.slice_from("e");
                break;
            }
            case 16: {
                this.slice_del();
                break;
            }
            case 17: {
                this.slice_del();
                break;
            }
            case 18: {
                this.slice_del();
                break;
            }
            case 19: {
                this.slice_from("a");
                break;
            }
            case 20: {
                this.slice_from("e");
            }
        }
        return true;
    }

    private boolean r_plur_owner() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_11, 42);
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
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("a");
                break;
            }
            case 3: {
                this.slice_from("e");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_del();
                break;
            }
            case 6: {
                this.slice_del();
                break;
            }
            case 7: {
                this.slice_from("a");
                break;
            }
            case 8: {
                this.slice_from("e");
                break;
            }
            case 9: {
                this.slice_del();
                break;
            }
            case 10: {
                this.slice_del();
                break;
            }
            case 11: {
                this.slice_del();
                break;
            }
            case 12: {
                this.slice_from("a");
                break;
            }
            case 13: {
                this.slice_from("e");
                break;
            }
            case 14: {
                this.slice_del();
                break;
            }
            case 15: {
                this.slice_del();
                break;
            }
            case 16: {
                this.slice_del();
                break;
            }
            case 17: {
                this.slice_del();
                break;
            }
            case 18: {
                this.slice_from("a");
                break;
            }
            case 19: {
                this.slice_from("e");
                break;
            }
            case 20: {
                this.slice_del();
                break;
            }
            case 21: {
                this.slice_del();
                break;
            }
            case 22: {
                this.slice_from("a");
                break;
            }
            case 23: {
                this.slice_from("e");
                break;
            }
            case 24: {
                this.slice_del();
                break;
            }
            case 25: {
                this.slice_del();
                break;
            }
            case 26: {
                this.slice_del();
                break;
            }
            case 27: {
                this.slice_from("a");
                break;
            }
            case 28: {
                this.slice_from("e");
                break;
            }
            case 29: {
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
        if (!this.r_instrum()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (!this.r_case()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (!this.r_case_special()) {
            // empty if block
        }
        this.cursor = this.limit - v_4;
        int v_5 = this.limit - this.cursor;
        if (!this.r_case_other()) {
            // empty if block
        }
        this.cursor = this.limit - v_5;
        int v_6 = this.limit - this.cursor;
        if (!this.r_factive()) {
            // empty if block
        }
        this.cursor = this.limit - v_6;
        int v_7 = this.limit - this.cursor;
        if (!this.r_owned()) {
            // empty if block
        }
        this.cursor = this.limit - v_7;
        int v_8 = this.limit - this.cursor;
        if (!this.r_sing_owner()) {
            // empty if block
        }
        this.cursor = this.limit - v_8;
        int v_9 = this.limit - this.cursor;
        if (!this.r_plur_owner()) {
            // empty if block
        }
        this.cursor = this.limit - v_9;
        int v_10 = this.limit - this.cursor;
        if (!this.r_plural()) {
            // empty if block
        }
        this.cursor = this.limit - v_10;
        this.cursor = this.limit_backward;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof HungarianStemmer;
    }

    public int hashCode() {
        return HungarianStemmer.class.getName().hashCode();
    }
}

