/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class KpStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final KpStemmer methodObject = new KpStemmer();
    private static final Among[] a_0 = new Among[]{new Among("nde", -1, 7, "", methodObject), new Among("en", -1, 6, "", methodObject), new Among("s", -1, 2, "", methodObject), new Among("'s", 2, 1, "", methodObject), new Among("es", 2, 4, "", methodObject), new Among("ies", 4, 3, "", methodObject), new Among("aus", 2, 5, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("de", -1, 5, "", methodObject), new Among("ge", -1, 2, "", methodObject), new Among("ische", -1, 4, "", methodObject), new Among("je", -1, 1, "", methodObject), new Among("lijke", -1, 3, "", methodObject), new Among("le", -1, 9, "", methodObject), new Among("ene", -1, 10, "", methodObject), new Among("re", -1, 8, "", methodObject), new Among("se", -1, 7, "", methodObject), new Among("te", -1, 6, "", methodObject), new Among("ieve", -1, 11, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("heid", -1, 3, "", methodObject), new Among("fie", -1, 7, "", methodObject), new Among("gie", -1, 8, "", methodObject), new Among("atie", -1, 1, "", methodObject), new Among("isme", -1, 5, "", methodObject), new Among("ing", -1, 5, "", methodObject), new Among("arij", -1, 6, "", methodObject), new Among("erij", -1, 5, "", methodObject), new Among("sel", -1, 3, "", methodObject), new Among("rder", -1, 4, "", methodObject), new Among("ster", -1, 3, "", methodObject), new Among("iteit", -1, 2, "", methodObject), new Among("dst", -1, 10, "", methodObject), new Among("tst", -1, 9, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("end", -1, 10, "", methodObject), new Among("atief", -1, 2, "", methodObject), new Among("erig", -1, 10, "", methodObject), new Among("achtig", -1, 9, "", methodObject), new Among("ioneel", -1, 1, "", methodObject), new Among("baar", -1, 3, "", methodObject), new Among("laar", -1, 5, "", methodObject), new Among("naar", -1, 4, "", methodObject), new Among("raar", -1, 6, "", methodObject), new Among("eriger", -1, 10, "", methodObject), new Among("achtiger", -1, 9, "", methodObject), new Among("lijker", -1, 8, "", methodObject), new Among("tant", -1, 7, "", methodObject), new Among("erigst", -1, 10, "", methodObject), new Among("achtigst", -1, 9, "", methodObject), new Among("lijkst", -1, 8, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("ig", -1, 1, "", methodObject), new Among("iger", -1, 1, "", methodObject), new Among("igst", -1, 1, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("ft", -1, 2, "", methodObject), new Among("kt", -1, 1, "", methodObject), new Among("pt", -1, 3, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("bb", -1, 1, "", methodObject), new Among("cc", -1, 2, "", methodObject), new Among("dd", -1, 3, "", methodObject), new Among("ff", -1, 4, "", methodObject), new Among("gg", -1, 5, "", methodObject), new Among("hh", -1, 6, "", methodObject), new Among("jj", -1, 7, "", methodObject), new Among("kk", -1, 8, "", methodObject), new Among("ll", -1, 9, "", methodObject), new Among("mm", -1, 10, "", methodObject), new Among("nn", -1, 11, "", methodObject), new Among("pp", -1, 12, "", methodObject), new Among("qq", -1, 13, "", methodObject), new Among("rr", -1, 14, "", methodObject), new Among("ss", -1, 15, "", methodObject), new Among("tt", -1, 16, "", methodObject), new Among("v", -1, 21, "", methodObject), new Among("vv", 16, 17, "", methodObject), new Among("ww", -1, 18, "", methodObject), new Among("xx", -1, 19, "", methodObject), new Among("z", -1, 22, "", methodObject), new Among("zz", 20, 20, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("d", -1, 1, "", methodObject), new Among("t", -1, 2, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001'};
    private static final char[] g_v_WX = new char[]{'\u0011', 'A', '\u00d0', '\u0001'};
    private static final char[] g_AOU = new char[]{'\u0001', '@', '\u0010'};
    private static final char[] g_AIOU = new char[]{'\u0001', 'A', '\u0010'};
    private boolean B_GE_removed;
    private boolean B_stemmed;
    private boolean B_Y_found;
    private int I_p2;
    private int I_p1;
    private int I_x;
    private StringBuilder S_ch = new StringBuilder();

    private void copy_from(KpStemmer other) {
        this.B_GE_removed = other.B_GE_removed;
        this.B_stemmed = other.B_stemmed;
        this.B_Y_found = other.B_Y_found;
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_x = other.I_x;
        this.S_ch = other.S_ch;
        super.copy_from(other);
    }

    private boolean r_R1() {
        this.I_x = this.cursor;
        return this.I_x >= this.I_p1;
    }

    private boolean r_R2() {
        this.I_x = this.cursor;
        return this.I_x >= this.I_p2;
    }

    private boolean r_V() {
        int v_1 = this.limit - this.cursor;
        int v_2 = this.limit - this.cursor;
        if (!this.in_grouping_b(g_v, 97, 121)) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(2, "ij")) {
                return false;
            }
        }
        this.cursor = this.limit - v_1;
        return true;
    }

    private boolean r_VX() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        int v_2 = this.limit - this.cursor;
        if (!this.in_grouping_b(g_v, 97, 121)) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(2, "ij")) {
                return false;
            }
        }
        this.cursor = this.limit - v_1;
        return true;
    }

    private boolean r_C() {
        int v_1 = this.limit - this.cursor;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(2, "ij")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        if (!this.out_grouping_b(g_v, 97, 121)) {
            return false;
        }
        this.cursor = this.limit - v_1;
        return true;
    }

    private boolean r_lengthen_V() {
        int v_1;
        block2: {
            block5: {
                int v_8;
                int v_5;
                block7: {
                    block6: {
                        int v_2;
                        block3: {
                            int v_3;
                            block4: {
                                v_1 = this.limit - this.cursor;
                                if (!this.out_grouping_b(g_v_WX, 97, 121)) break block2;
                                this.ket = this.cursor;
                                v_2 = this.limit - this.cursor;
                                if (!this.in_grouping_b(g_AOU, 97, 117)) break block3;
                                this.bra = this.cursor;
                                v_3 = this.limit - this.cursor;
                                int v_4 = this.limit - this.cursor;
                                if (this.out_grouping_b(g_v, 97, 121)) break block4;
                                this.cursor = this.limit - v_4;
                                if (this.cursor > this.limit_backward) break block3;
                            }
                            this.cursor = this.limit - v_3;
                            break block5;
                        }
                        this.cursor = this.limit - v_2;
                        if (!this.eq_s_b(1, "e")) break block2;
                        this.bra = this.cursor;
                        v_5 = this.limit - this.cursor;
                        int v_6 = this.limit - this.cursor;
                        if (this.out_grouping_b(g_v, 97, 121)) break block6;
                        this.cursor = this.limit - v_6;
                        if (this.cursor > this.limit_backward) break block2;
                    }
                    int v_7 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_AIOU, 97, 117)) break block2;
                    this.cursor = this.limit - v_7;
                    v_8 = this.limit - this.cursor;
                    if (this.cursor <= this.limit_backward) break block7;
                    --this.cursor;
                    if (this.in_grouping_b(g_AIOU, 97, 117) && this.out_grouping_b(g_v, 97, 121)) break block2;
                }
                this.cursor = this.limit - v_8;
                this.cursor = this.limit - v_5;
            }
            this.S_ch = this.slice_to(this.S_ch);
            int c = this.cursor;
            this.insert(this.cursor, this.cursor, this.S_ch);
            this.cursor = c;
        }
        this.cursor = this.limit - v_1;
        return true;
    }

    /*
     * Unable to fully structure code
     */
    private boolean r_Step_1() {
        this.ket = this.cursor;
        among_var = this.find_among_b(KpStemmer.a_0, 7);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                v_1 = this.limit - this.cursor;
                if (this.eq_s_b(1, "t") && this.r_R1()) {
                    return false;
                }
                this.cursor = this.limit - v_1;
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("ie");
                break;
            }
            case 4: {
                v_2 = this.limit - this.cursor;
                if (this.eq_s_b(2, "ar") && this.r_R1() && this.r_C()) {
                    this.bra = this.cursor;
                    this.slice_del();
                    if (this.r_lengthen_V()) break;
                }
                this.cursor = this.limit - v_2;
                if (this.eq_s_b(2, "er") && this.r_R1() && this.r_C()) {
                    this.bra = this.cursor;
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_2;
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("e");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_V()) {
                    return false;
                }
                this.slice_from("au");
                break;
            }
            case 6: {
                v_3 = this.limit - this.cursor;
                if (this.eq_s_b(3, "hed") && this.r_R1()) {
                    this.bra = this.cursor;
                    this.slice_from("heid");
                    break;
                }
                this.cursor = this.limit - v_3;
                if (this.eq_s_b(2, "nd")) {
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_3;
                if (this.eq_s_b(1, "d") && this.r_R1() && this.r_C()) {
                    this.bra = this.cursor;
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_3;
                v_4 = this.limit - this.cursor;
                if (this.eq_s_b(1, "i")) ** GOTO lbl73
                this.cursor = this.limit - v_4;
                if (!this.eq_s_b(1, "j")) ** GOTO lbl76
lbl73:
                // 2 sources

                if (this.r_V()) {
                    this.slice_del();
                    break;
                }
lbl76:
                // 3 sources

                this.cursor = this.limit - v_3;
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                if (this.r_lengthen_V()) break;
                return false;
            }
            case 7: {
                this.slice_from("nd");
            }
        }
        return true;
    }

    private boolean r_Step_2() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 11);
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
                if (this.eq_s_b(2, "'t")) {
                    this.bra = this.cursor;
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(2, "et")) {
                    this.bra = this.cursor;
                    if (this.r_R1() && this.r_C()) {
                        this.slice_del();
                        break;
                    }
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(3, "rnt")) {
                    this.bra = this.cursor;
                    this.slice_from("rn");
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(1, "t")) {
                    this.bra = this.cursor;
                    if (this.r_R1() && this.r_VX()) {
                        this.slice_del();
                        break;
                    }
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(3, "ink")) {
                    this.bra = this.cursor;
                    this.slice_from("ing");
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(2, "mp")) {
                    this.bra = this.cursor;
                    this.slice_from("m");
                    break;
                }
                this.cursor = this.limit - v_1;
                if (this.eq_s_b(1, "'")) {
                    this.bra = this.cursor;
                    if (this.r_R1()) {
                        this.slice_del();
                        break;
                    }
                }
                this.bra = this.cursor = this.limit - v_1;
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("g");
                break;
            }
            case 3: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("lijk");
                break;
            }
            case 4: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("isch");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 6: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("t");
                break;
            }
            case 7: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("s");
                break;
            }
            case 8: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("r");
                break;
            }
            case 9: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "l");
                if (this.r_lengthen_V()) break;
                return false;
            }
            case 10: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "en");
                if (this.r_lengthen_V()) break;
                return false;
            }
            case 11: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("ief");
            }
        }
        return true;
    }

    private boolean r_Step_3() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 14);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("eer");
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                if (this.r_lengthen_V()) break;
                return false;
            }
            case 3: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 4: {
                this.slice_from("r");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                if (this.r_lengthen_V()) break;
                return false;
            }
            case 6: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("aar");
                break;
            }
            case 7: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "f");
                if (this.r_lengthen_V()) break;
                return false;
            }
            case 8: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                this.insert(this.cursor, this.cursor, "g");
                if (this.r_lengthen_V()) break;
                return false;
            }
            case 9: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("t");
                break;
            }
            case 10: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_from("d");
            }
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_Step_4() {
        int among_var;
        int v_1;
        block31: {
            v_1 = this.limit - this.cursor;
            this.ket = this.cursor;
            among_var = this.find_among_b(a_3, 16);
            if (among_var != 0) {
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        break block31;
                    }
                    case 1: {
                        if (this.r_R1()) {
                            this.slice_from("ie");
                            break;
                        }
                        break block31;
                    }
                    case 2: {
                        if (this.r_R1()) {
                            this.slice_from("eer");
                            break;
                        }
                        break block31;
                    }
                    case 3: {
                        if (this.r_R1()) {
                            this.slice_del();
                            break;
                        }
                        break block31;
                    }
                    case 4: {
                        if (this.r_R1() && this.r_V()) {
                            this.slice_from("n");
                            break;
                        }
                        break block31;
                    }
                    case 5: {
                        if (this.r_R1() && this.r_V()) {
                            this.slice_from("l");
                            break;
                        }
                        break block31;
                    }
                    case 6: {
                        if (this.r_R1() && this.r_V()) {
                            this.slice_from("r");
                            break;
                        }
                        break block31;
                    }
                    case 7: {
                        if (this.r_R1()) {
                            this.slice_from("teer");
                            break;
                        }
                        break block31;
                    }
                    case 8: {
                        if (this.r_R1()) {
                            this.slice_from("lijk");
                            break;
                        }
                        break block31;
                    }
                    case 9: {
                        if (this.r_R1()) {
                            this.slice_del();
                            break;
                        }
                        break block31;
                    }
                    case 10: {
                        if (this.r_R1() && this.r_C()) {
                            this.slice_del();
                            if (this.r_lengthen_V()) return true;
                        }
                        break block31;
                    }
                }
                return true;
            }
        }
        this.ket = this.cursor = this.limit - v_1;
        among_var = this.find_among_b(a_4, 3);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.r_C()) {
                    return false;
                }
                this.slice_del();
                if (this.r_lengthen_V()) return true;
                return false;
            }
        }
        return true;
    }

    private boolean r_Step_7() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_5, 3);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("k");
                break;
            }
            case 2: {
                this.slice_from("f");
                break;
            }
            case 3: {
                this.slice_from("p");
            }
        }
        return true;
    }

    private boolean r_Step_6() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_6, 22);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("b");
                break;
            }
            case 2: {
                this.slice_from("c");
                break;
            }
            case 3: {
                this.slice_from("d");
                break;
            }
            case 4: {
                this.slice_from("f");
                break;
            }
            case 5: {
                this.slice_from("g");
                break;
            }
            case 6: {
                this.slice_from("h");
                break;
            }
            case 7: {
                this.slice_from("j");
                break;
            }
            case 8: {
                this.slice_from("k");
                break;
            }
            case 9: {
                this.slice_from("l");
                break;
            }
            case 10: {
                this.slice_from("m");
                break;
            }
            case 11: {
                this.slice_from("n");
                break;
            }
            case 12: {
                this.slice_from("p");
                break;
            }
            case 13: {
                this.slice_from("q");
                break;
            }
            case 14: {
                this.slice_from("r");
                break;
            }
            case 15: {
                this.slice_from("s");
                break;
            }
            case 16: {
                this.slice_from("t");
                break;
            }
            case 17: {
                this.slice_from("v");
                break;
            }
            case 18: {
                this.slice_from("w");
                break;
            }
            case 19: {
                this.slice_from("x");
                break;
            }
            case 20: {
                this.slice_from("z");
                break;
            }
            case 21: {
                this.slice_from("f");
                break;
            }
            case 22: {
                this.slice_from("s");
            }
        }
        return true;
    }

    private boolean r_Step_1c() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_7, 2);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_R1()) {
            return false;
        }
        if (!this.r_C()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                int v_1 = this.limit - this.cursor;
                if (this.eq_s_b(1, "n") && this.r_R1()) {
                    return false;
                }
                this.cursor = this.limit - v_1;
                this.slice_del();
                break;
            }
            case 2: {
                int v_2 = this.limit - this.cursor;
                if (this.eq_s_b(1, "h") && this.r_R1()) {
                    return false;
                }
                this.cursor = this.limit - v_2;
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_Lose_prefix() {
        int v_3;
        int v_2;
        this.bra = this.cursor;
        if (!this.eq_s(2, "ge")) {
            return false;
        }
        this.ket = this.cursor;
        int v_1 = this.cursor;
        int c = this.cursor + 3;
        if (0 > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = v_1;
        while (true) {
            v_2 = ++this.cursor;
            if (this.in_grouping(g_v, 97, 121)) break;
            this.cursor = v_2;
            if (this.cursor < this.limit) continue;
            return false;
        }
        this.cursor = v_2;
        while (true) {
            v_3 = ++this.cursor;
            if (this.out_grouping(g_v, 97, 121)) break;
            this.cursor = v_3;
            if (this.cursor < this.limit) continue;
            return false;
        }
        this.cursor = v_3;
        this.B_GE_removed = true;
        this.slice_del();
        return true;
    }

    private boolean r_Lose_infix() {
        int v_4;
        int v_3;
        if (this.cursor >= this.limit) {
            return false;
        }
        ++this.cursor;
        while (true) {
            this.bra = ++this.cursor;
            if (this.eq_s(2, "ge")) break;
            if (this.cursor < this.limit) continue;
            return false;
        }
        this.ket = this.cursor;
        int v_2 = this.cursor;
        int c = this.cursor + 3;
        if (0 > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = v_2;
        while (true) {
            v_3 = ++this.cursor;
            if (this.in_grouping(g_v, 97, 121)) break;
            this.cursor = v_3;
            if (this.cursor < this.limit) continue;
            return false;
        }
        this.cursor = v_3;
        while (true) {
            v_4 = ++this.cursor;
            if (this.out_grouping(g_v, 97, 121)) break;
            this.cursor = v_4;
            if (this.cursor < this.limit) continue;
            return false;
        }
        this.cursor = v_4;
        this.B_GE_removed = true;
        this.slice_del();
        return true;
    }

    private boolean r_measure() {
        int v_5;
        int v_1 = this.cursor;
        this.I_p1 = this.cursor = this.limit;
        this.I_p2 = this.cursor;
        int v_2 = this.cursor = v_1;
        while (this.out_grouping(g_v, 97, 121)) {
        }
        int v_4 = 1;
        while (true) {
            v_5 = this.cursor;
            int v_6 = this.cursor;
            if (!this.eq_s(2, "ij")) {
                this.cursor = v_6;
                if (!this.in_grouping(g_v, 97, 121)) break;
            }
            --v_4;
        }
        this.cursor = v_5;
        if (v_4 <= 0 && this.out_grouping(g_v, 97, 121)) {
            int v_9;
            this.I_p1 = this.cursor;
            while (this.out_grouping(g_v, 97, 121)) {
            }
            int v_8 = 1;
            while (true) {
                v_9 = this.cursor;
                int v_10 = this.cursor;
                if (!this.eq_s(2, "ij")) {
                    this.cursor = v_10;
                    if (!this.in_grouping(g_v, 97, 121)) break;
                }
                --v_8;
            }
            this.cursor = v_9;
            if (v_8 <= 0 && this.out_grouping(g_v, 97, 121)) {
                this.I_p2 = this.cursor;
            }
        }
        this.cursor = v_2;
        return true;
    }

    @Override
    public boolean stem() {
        int v_3;
        this.B_Y_found = false;
        this.B_stemmed = false;
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
        if (!this.r_measure()) {
            return false;
        }
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        int v_5 = this.limit - this.cursor;
        if (this.r_Step_1()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_5;
        int v_6 = this.limit - this.cursor;
        if (this.r_Step_2()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_6;
        int v_7 = this.limit - this.cursor;
        if (this.r_Step_3()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_7;
        int v_8 = this.limit - this.cursor;
        if (this.r_Step_4()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_8;
        this.cursor = this.limit_backward;
        this.B_GE_removed = false;
        int v_9 = this.cursor;
        int v_10 = this.cursor;
        if (this.r_Lose_prefix()) {
            this.cursor = v_10;
            if (!this.r_measure()) {
                // empty if block
            }
        }
        this.limit_backward = this.cursor = v_9;
        this.cursor = this.limit;
        int v_11 = this.limit - this.cursor;
        if (!this.B_GE_removed || !this.r_Step_1c()) {
            // empty if block
        }
        this.cursor = this.limit - v_11;
        this.cursor = this.limit_backward;
        this.B_GE_removed = false;
        int v_12 = this.cursor;
        int v_13 = this.cursor;
        if (this.r_Lose_infix()) {
            this.cursor = v_13;
            if (!this.r_measure()) {
                // empty if block
            }
        }
        this.limit_backward = this.cursor = v_12;
        this.cursor = this.limit;
        int v_14 = this.limit - this.cursor;
        if (!this.B_GE_removed || !this.r_Step_1c()) {
            // empty if block
        }
        this.cursor = this.limit - v_14;
        this.limit_backward = this.cursor = this.limit_backward;
        this.cursor = this.limit;
        int v_15 = this.limit - this.cursor;
        if (this.r_Step_7()) {
            this.B_stemmed = true;
        }
        this.cursor = this.limit - v_15;
        int v_16 = this.limit - this.cursor;
        if (!this.B_stemmed && !this.B_GE_removed || !this.r_Step_6()) {
            // empty if block
        }
        this.cursor = this.limit - v_16;
        int v_18 = this.cursor = this.limit_backward;
        if (this.B_Y_found) {
            int v_19;
            block2: while (true) {
                int v_20;
                v_19 = this.cursor;
                while (true) {
                    v_20 = ++this.cursor;
                    this.bra = this.cursor;
                    if (this.eq_s(1, "Y")) break;
                    this.cursor = v_20;
                    if (this.cursor >= this.limit) break block2;
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
        return o instanceof KpStemmer;
    }

    public int hashCode() {
        return KpStemmer.class.getName().hashCode();
    }
}

