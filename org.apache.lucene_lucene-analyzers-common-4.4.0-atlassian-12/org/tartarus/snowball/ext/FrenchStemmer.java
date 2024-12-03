/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class FrenchStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final FrenchStemmer methodObject = new FrenchStemmer();
    private static final Among[] a_0 = new Among[]{new Among("col", -1, -1, "", methodObject), new Among("par", -1, -1, "", methodObject), new Among("tap", -1, -1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("", -1, 4, "", methodObject), new Among("I", 0, 1, "", methodObject), new Among("U", 0, 2, "", methodObject), new Among("Y", 0, 3, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("iqU", -1, 3, "", methodObject), new Among("abl", -1, 3, "", methodObject), new Among("I\u00e8r", -1, 4, "", methodObject), new Among("i\u00e8r", -1, 4, "", methodObject), new Among("eus", -1, 2, "", methodObject), new Among("iv", -1, 1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("ic", -1, 2, "", methodObject), new Among("abil", -1, 1, "", methodObject), new Among("iv", -1, 3, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("iqUe", -1, 1, "", methodObject), new Among("atrice", -1, 2, "", methodObject), new Among("ance", -1, 1, "", methodObject), new Among("ence", -1, 5, "", methodObject), new Among("logie", -1, 3, "", methodObject), new Among("able", -1, 1, "", methodObject), new Among("isme", -1, 1, "", methodObject), new Among("euse", -1, 11, "", methodObject), new Among("iste", -1, 1, "", methodObject), new Among("ive", -1, 8, "", methodObject), new Among("if", -1, 8, "", methodObject), new Among("usion", -1, 4, "", methodObject), new Among("ation", -1, 2, "", methodObject), new Among("ution", -1, 4, "", methodObject), new Among("ateur", -1, 2, "", methodObject), new Among("iqUes", -1, 1, "", methodObject), new Among("atrices", -1, 2, "", methodObject), new Among("ances", -1, 1, "", methodObject), new Among("ences", -1, 5, "", methodObject), new Among("logies", -1, 3, "", methodObject), new Among("ables", -1, 1, "", methodObject), new Among("ismes", -1, 1, "", methodObject), new Among("euses", -1, 11, "", methodObject), new Among("istes", -1, 1, "", methodObject), new Among("ives", -1, 8, "", methodObject), new Among("ifs", -1, 8, "", methodObject), new Among("usions", -1, 4, "", methodObject), new Among("ations", -1, 2, "", methodObject), new Among("utions", -1, 4, "", methodObject), new Among("ateurs", -1, 2, "", methodObject), new Among("ments", -1, 15, "", methodObject), new Among("ements", 30, 6, "", methodObject), new Among("issements", 31, 12, "", methodObject), new Among("it\u00e9s", -1, 7, "", methodObject), new Among("ment", -1, 15, "", methodObject), new Among("ement", 34, 6, "", methodObject), new Among("issement", 35, 12, "", methodObject), new Among("amment", 34, 13, "", methodObject), new Among("emment", 34, 14, "", methodObject), new Among("aux", -1, 10, "", methodObject), new Among("eaux", 39, 9, "", methodObject), new Among("eux", -1, 1, "", methodObject), new Among("it\u00e9", -1, 7, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("ira", -1, 1, "", methodObject), new Among("ie", -1, 1, "", methodObject), new Among("isse", -1, 1, "", methodObject), new Among("issante", -1, 1, "", methodObject), new Among("i", -1, 1, "", methodObject), new Among("irai", 4, 1, "", methodObject), new Among("ir", -1, 1, "", methodObject), new Among("iras", -1, 1, "", methodObject), new Among("ies", -1, 1, "", methodObject), new Among("\u00eemes", -1, 1, "", methodObject), new Among("isses", -1, 1, "", methodObject), new Among("issantes", -1, 1, "", methodObject), new Among("\u00eetes", -1, 1, "", methodObject), new Among("is", -1, 1, "", methodObject), new Among("irais", 13, 1, "", methodObject), new Among("issais", 13, 1, "", methodObject), new Among("irions", -1, 1, "", methodObject), new Among("issions", -1, 1, "", methodObject), new Among("irons", -1, 1, "", methodObject), new Among("issons", -1, 1, "", methodObject), new Among("issants", -1, 1, "", methodObject), new Among("it", -1, 1, "", methodObject), new Among("irait", 21, 1, "", methodObject), new Among("issait", 21, 1, "", methodObject), new Among("issant", -1, 1, "", methodObject), new Among("iraIent", -1, 1, "", methodObject), new Among("issaIent", -1, 1, "", methodObject), new Among("irent", -1, 1, "", methodObject), new Among("issent", -1, 1, "", methodObject), new Among("iront", -1, 1, "", methodObject), new Among("\u00eet", -1, 1, "", methodObject), new Among("iriez", -1, 1, "", methodObject), new Among("issiez", -1, 1, "", methodObject), new Among("irez", -1, 1, "", methodObject), new Among("issez", -1, 1, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("a", -1, 3, "", methodObject), new Among("era", 0, 2, "", methodObject), new Among("asse", -1, 3, "", methodObject), new Among("ante", -1, 3, "", methodObject), new Among("\u00e9e", -1, 2, "", methodObject), new Among("ai", -1, 3, "", methodObject), new Among("erai", 5, 2, "", methodObject), new Among("er", -1, 2, "", methodObject), new Among("as", -1, 3, "", methodObject), new Among("eras", 8, 2, "", methodObject), new Among("\u00e2mes", -1, 3, "", methodObject), new Among("asses", -1, 3, "", methodObject), new Among("antes", -1, 3, "", methodObject), new Among("\u00e2tes", -1, 3, "", methodObject), new Among("\u00e9es", -1, 2, "", methodObject), new Among("ais", -1, 3, "", methodObject), new Among("erais", 15, 2, "", methodObject), new Among("ions", -1, 1, "", methodObject), new Among("erions", 17, 2, "", methodObject), new Among("assions", 17, 3, "", methodObject), new Among("erons", -1, 2, "", methodObject), new Among("ants", -1, 3, "", methodObject), new Among("\u00e9s", -1, 2, "", methodObject), new Among("ait", -1, 3, "", methodObject), new Among("erait", 23, 2, "", methodObject), new Among("ant", -1, 3, "", methodObject), new Among("aIent", -1, 3, "", methodObject), new Among("eraIent", 26, 2, "", methodObject), new Among("\u00e8rent", -1, 2, "", methodObject), new Among("assent", -1, 3, "", methodObject), new Among("eront", -1, 2, "", methodObject), new Among("\u00e2t", -1, 3, "", methodObject), new Among("ez", -1, 2, "", methodObject), new Among("iez", 32, 2, "", methodObject), new Among("eriez", 33, 2, "", methodObject), new Among("assiez", 33, 3, "", methodObject), new Among("erez", 32, 2, "", methodObject), new Among("\u00e9", -1, 2, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("e", -1, 3, "", methodObject), new Among("I\u00e8re", 0, 2, "", methodObject), new Among("i\u00e8re", 0, 2, "", methodObject), new Among("ion", -1, 1, "", methodObject), new Among("Ier", -1, 2, "", methodObject), new Among("ier", -1, 2, "", methodObject), new Among("\u00eb", -1, 4, "", methodObject)};
    private static final Among[] a_8 = new Among[]{new Among("ell", -1, -1, "", methodObject), new Among("eill", -1, -1, "", methodObject), new Among("enn", -1, -1, "", methodObject), new Among("onn", -1, -1, "", methodObject), new Among("ett", -1, -1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080', '\u0082', 'g', '\b', '\u0005'};
    private static final char[] g_keep_with_s = new char[]{'\u0001', 'A', '\u0014', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080'};
    private int I_p2;
    private int I_p1;
    private int I_pV;

    private void copy_from(FrenchStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }

    private boolean r_prelude() {
        int v_1;
        block0: while (true) {
            v_1 = this.cursor;
            while (true) {
                int v_2;
                block9: {
                    block6: {
                        int v_3;
                        block8: {
                            block4: {
                                int v_4;
                                block7: {
                                    block5: {
                                        v_2 = ++this.cursor;
                                        v_3 = this.cursor;
                                        if (!this.in_grouping(g_v, 97, 251)) break block4;
                                        this.bra = this.cursor;
                                        v_4 = this.cursor;
                                        if (!this.eq_s(1, "u")) break block5;
                                        this.ket = this.cursor;
                                        if (!this.in_grouping(g_v, 97, 251)) break block5;
                                        this.slice_from("U");
                                        break block6;
                                    }
                                    this.cursor = v_4;
                                    if (!this.eq_s(1, "i")) break block7;
                                    this.ket = this.cursor;
                                    if (!this.in_grouping(g_v, 97, 251)) break block7;
                                    this.slice_from("I");
                                    break block6;
                                }
                                this.cursor = v_4;
                                if (!this.eq_s(1, "y")) break block4;
                                this.ket = this.cursor;
                                this.slice_from("Y");
                                break block6;
                            }
                            this.bra = this.cursor = v_3;
                            if (!this.eq_s(1, "y")) break block8;
                            this.ket = this.cursor;
                            if (!this.in_grouping(g_v, 97, 251)) break block8;
                            this.slice_from("Y");
                            break block6;
                        }
                        this.cursor = v_3;
                        if (!this.eq_s(1, "q")) break block9;
                        this.bra = this.cursor;
                        if (!this.eq_s(1, "u")) break block9;
                        this.ket = this.cursor;
                        this.slice_from("U");
                    }
                    this.cursor = v_2;
                    continue block0;
                }
                this.cursor = v_2;
                if (this.cursor >= this.limit) break block0;
            }
            break;
        }
        this.cursor = v_1;
        return true;
    }

    /*
     * Unable to fully structure code
     */
    private boolean r_mark_regions() {
        block14: {
            block15: {
                this.I_pV = this.limit;
                this.I_p1 = this.limit;
                this.I_p2 = this.limit;
                v_1 = this.cursor;
                v_2 = this.cursor;
                if (!this.in_grouping(FrenchStemmer.g_v, 97, 251) || !this.in_grouping(FrenchStemmer.g_v, 97, 251) || this.cursor >= this.limit) break block15;
                ++this.cursor;
                ** GOTO lbl-1000
            }
            this.cursor = v_2;
            if (this.find_among(FrenchStemmer.a_0, 3) == 0) {
                this.cursor = v_2;
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    while (!this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                        if (this.cursor < this.limit) {
                            ++this.cursor;
                            continue;
                        }
                        break;
                    }
                }
            } else lbl-1000:
            // 3 sources

            {
                this.I_pV = this.cursor;
            }
            v_4 = this.cursor = v_1;
            while (!this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block14;
            }
            while (!this.out_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block14;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block14;
            }
            while (!this.out_grouping(FrenchStemmer.g_v, 97, 251)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block14;
            }
            this.I_p2 = this.cursor;
        }
        this.cursor = v_4;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_postlude() {
        int v_1;
        block7: while (true) {
            v_1 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_1, 4);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block7;
                }
                case 1: {
                    this.slice_from("i");
                    break;
                }
                case 2: {
                    this.slice_from("u");
                    break;
                }
                case 3: {
                    this.slice_from("y");
                    break;
                }
                case 4: {
                    if (this.cursor >= this.limit) break block7;
                }
            }
        }
        this.cursor = v_1;
        return true;
    }

    private boolean r_RV() {
        return this.I_pV <= this.cursor;
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_standard_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_4, 43);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_1 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "ic")) {
                    this.cursor = this.limit - v_1;
                    break;
                }
                this.bra = this.cursor;
                int v_2 = this.limit - this.cursor;
                if (this.r_R2()) {
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_2;
                this.slice_from("iqU");
                break;
            }
            case 3: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("log");
                break;
            }
            case 4: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("u");
                break;
            }
            case 5: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("ent");
                break;
            }
            case 6: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                int v_3 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_2, 6);
                if (among_var == 0) {
                    this.cursor = this.limit - v_3;
                    break;
                }
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_3;
                        break;
                    }
                    case 1: {
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_del();
                        this.ket = this.cursor;
                        if (!this.eq_s_b(2, "at")) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.bra = this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 2: {
                        int v_4 = this.limit - this.cursor;
                        if (this.r_R2()) {
                            this.slice_del();
                            break;
                        }
                        this.cursor = this.limit - v_4;
                        if (!this.r_R1()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_from("eux");
                        break;
                    }
                    case 3: {
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_del();
                        break;
                    }
                    case 4: {
                        if (!this.r_RV()) {
                            this.cursor = this.limit - v_3;
                            break;
                        }
                        this.slice_from("i");
                    }
                }
                break;
            }
            case 7: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_5 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_3, 3);
                if (among_var == 0) {
                    this.cursor = this.limit - v_5;
                    break;
                }
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_5;
                        break;
                    }
                    case 1: {
                        int v_6 = this.limit - this.cursor;
                        if (this.r_R2()) {
                            this.slice_del();
                            break;
                        }
                        this.cursor = this.limit - v_6;
                        this.slice_from("abl");
                        break;
                    }
                    case 2: {
                        int v_7 = this.limit - this.cursor;
                        if (this.r_R2()) {
                            this.slice_del();
                            break;
                        }
                        this.cursor = this.limit - v_7;
                        this.slice_from("iqU");
                        break;
                    }
                    case 3: {
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_5;
                            break;
                        }
                        this.slice_del();
                    }
                }
                break;
            }
            case 8: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_8 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "at")) {
                    this.cursor = this.limit - v_8;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_8;
                    break;
                }
                this.slice_del();
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "ic")) {
                    this.cursor = this.limit - v_8;
                    break;
                }
                this.bra = this.cursor;
                int v_9 = this.limit - this.cursor;
                if (this.r_R2()) {
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_9;
                this.slice_from("iqU");
                break;
            }
            case 9: {
                this.slice_from("eau");
                break;
            }
            case 10: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("al");
                break;
            }
            case 11: {
                int v_10 = this.limit - this.cursor;
                if (this.r_R2()) {
                    this.slice_del();
                    break;
                }
                this.cursor = this.limit - v_10;
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_from("eux");
                break;
            }
            case 12: {
                if (!this.r_R1()) {
                    return false;
                }
                if (!this.out_grouping_b(g_v, 97, 251)) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 13: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_from("ant");
                return false;
            }
            case 14: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_from("ent");
                return false;
            }
            case 15: {
                int v_11 = this.limit - this.cursor;
                if (!this.in_grouping_b(g_v, 97, 251)) {
                    return false;
                }
                if (!this.r_RV()) {
                    return false;
                }
                this.cursor = this.limit - v_11;
                this.slice_del();
                return false;
            }
        }
        return true;
    }

    private boolean r_i_verb_suffix() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_5, 35);
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
                if (!this.out_grouping_b(g_v, 97, 251)) {
                    this.limit_backward = v_2;
                    return false;
                }
                this.slice_del();
            }
        }
        this.limit_backward = v_2;
        return true;
    }

    private boolean r_verb_suffix() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_6, 38);
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
                if (!this.r_R2()) {
                    this.limit_backward = v_2;
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_del();
                int v_3 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(1, "e")) {
                    this.cursor = this.limit - v_3;
                    break;
                }
                this.bra = this.cursor;
                this.slice_del();
            }
        }
        this.limit_backward = v_2;
        return true;
    }

    private boolean r_residual_suffix() {
        int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_1;
        } else {
            this.bra = this.cursor;
            int v_2 = this.limit - this.cursor;
            if (!this.out_grouping_b(g_keep_with_s, 97, 232)) {
                this.cursor = this.limit - v_1;
            } else {
                this.cursor = this.limit - v_2;
                this.slice_del();
            }
        }
        int v_3 = this.limit - this.cursor;
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        int v_4 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_3;
        int among_var = this.find_among_b(a_7, 7);
        if (among_var == 0) {
            this.limit_backward = v_4;
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                this.limit_backward = v_4;
                return false;
            }
            case 1: {
                if (!this.r_R2()) {
                    this.limit_backward = v_4;
                    return false;
                }
                int v_5 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "s")) {
                    this.cursor = this.limit - v_5;
                    if (!this.eq_s_b(1, "t")) {
                        this.limit_backward = v_4;
                        return false;
                    }
                }
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("i");
                break;
            }
            case 3: {
                this.slice_del();
                break;
            }
            case 4: {
                if (!this.eq_s_b(2, "gu")) {
                    this.limit_backward = v_4;
                    return false;
                }
                this.slice_del();
            }
        }
        this.limit_backward = v_4;
        return true;
    }

    private boolean r_un_double() {
        int v_1 = this.limit - this.cursor;
        if (this.find_among_b(a_8, 5) == 0) {
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

    private boolean r_un_accent() {
        int v_1 = 1;
        while (this.out_grouping_b(g_v, 97, 251)) {
            --v_1;
        }
        if (v_1 > 0) {
            return false;
        }
        this.ket = this.cursor;
        int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "\u00e9")) {
            this.cursor = this.limit - v_3;
            if (!this.eq_s_b(1, "\u00e8")) {
                return false;
            }
        }
        this.bra = this.cursor;
        this.slice_from("e");
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    public boolean stem() {
        int v_3;
        block7: {
            block9: {
                int v_7;
                block10: {
                    block8: {
                        int v_5;
                        block6: {
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
                            v_3 = this.limit - this.cursor;
                            int v_4 = this.limit - this.cursor;
                            v_5 = this.limit - this.cursor;
                            int v_6 = this.limit - this.cursor;
                            if (this.r_standard_suffix()) break block6;
                            this.cursor = this.limit - v_6;
                            if (this.r_i_verb_suffix()) break block6;
                            this.cursor = this.limit - v_6;
                            if (this.r_verb_suffix()) break block6;
                            this.cursor = this.limit - v_4;
                            if (!this.r_residual_suffix()) {
                                // empty if block
                            }
                            break block7;
                        }
                        this.cursor = this.limit - v_5;
                        v_7 = this.limit - this.cursor;
                        this.ket = this.cursor;
                        int v_8 = this.limit - this.cursor;
                        if (this.eq_s_b(1, "Y")) break block8;
                        this.cursor = this.limit - v_8;
                        if (this.eq_s_b(1, "\u00e7")) break block9;
                        break block10;
                    }
                    this.bra = this.cursor;
                    this.slice_from("i");
                    break block7;
                }
                this.cursor = this.limit - v_7;
                break block7;
            }
            this.bra = this.cursor;
            this.slice_from("c");
        }
        this.cursor = this.limit - v_3;
        int v_9 = this.limit - this.cursor;
        if (!this.r_un_double()) {
            // empty if block
        }
        this.cursor = this.limit - v_9;
        int v_10 = this.limit - this.cursor;
        if (!this.r_un_accent()) {
            // empty if block
        }
        this.cursor = this.limit - v_10;
        int v_11 = this.cursor = this.limit_backward;
        if (!this.r_postlude()) {
            // empty if block
        }
        this.cursor = v_11;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof FrenchStemmer;
    }

    public int hashCode() {
        return FrenchStemmer.class.getName().hashCode();
    }
}

