/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class IrishStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final IrishStemmer methodObject = new IrishStemmer();
    private static final Among[] a_0 = new Among[]{new Among("b'", -1, 4, "", methodObject), new Among("bh", -1, 14, "", methodObject), new Among("bhf", 1, 9, "", methodObject), new Among("bp", -1, 11, "", methodObject), new Among("ch", -1, 15, "", methodObject), new Among("d'", -1, 2, "", methodObject), new Among("d'fh", 5, 3, "", methodObject), new Among("dh", -1, 16, "", methodObject), new Among("dt", -1, 13, "", methodObject), new Among("fh", -1, 17, "", methodObject), new Among("gc", -1, 7, "", methodObject), new Among("gh", -1, 18, "", methodObject), new Among("h-", -1, 1, "", methodObject), new Among("m'", -1, 4, "", methodObject), new Among("mb", -1, 6, "", methodObject), new Among("mh", -1, 19, "", methodObject), new Among("n-", -1, 1, "", methodObject), new Among("nd", -1, 8, "", methodObject), new Among("ng", -1, 10, "", methodObject), new Among("ph", -1, 20, "", methodObject), new Among("sh", -1, 5, "", methodObject), new Among("t-", -1, 1, "", methodObject), new Among("th", -1, 21, "", methodObject), new Among("ts", -1, 12, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("\u00edochta", -1, 1, "", methodObject), new Among("a\u00edochta", 0, 1, "", methodObject), new Among("ire", -1, 2, "", methodObject), new Among("aire", 2, 2, "", methodObject), new Among("abh", -1, 1, "", methodObject), new Among("eabh", 4, 1, "", methodObject), new Among("ibh", -1, 1, "", methodObject), new Among("aibh", 6, 1, "", methodObject), new Among("amh", -1, 1, "", methodObject), new Among("eamh", 8, 1, "", methodObject), new Among("imh", -1, 1, "", methodObject), new Among("aimh", 10, 1, "", methodObject), new Among("\u00edocht", -1, 1, "", methodObject), new Among("a\u00edocht", 12, 1, "", methodObject), new Among("ir\u00ed", -1, 2, "", methodObject), new Among("air\u00ed", 14, 2, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("\u00f3ideacha", -1, 6, "", methodObject), new Among("patacha", -1, 5, "", methodObject), new Among("achta", -1, 1, "", methodObject), new Among("arcachta", 2, 2, "", methodObject), new Among("eachta", 2, 1, "", methodObject), new Among("grafa\u00edochta", -1, 4, "", methodObject), new Among("paite", -1, 5, "", methodObject), new Among("ach", -1, 1, "", methodObject), new Among("each", 7, 1, "", methodObject), new Among("\u00f3ideach", 8, 6, "", methodObject), new Among("gineach", 8, 3, "", methodObject), new Among("patach", 7, 5, "", methodObject), new Among("grafa\u00edoch", -1, 4, "", methodObject), new Among("pataigh", -1, 5, "", methodObject), new Among("\u00f3idigh", -1, 6, "", methodObject), new Among("acht\u00fail", -1, 1, "", methodObject), new Among("eacht\u00fail", 15, 1, "", methodObject), new Among("gineas", -1, 3, "", methodObject), new Among("ginis", -1, 3, "", methodObject), new Among("acht", -1, 1, "", methodObject), new Among("arcacht", 19, 2, "", methodObject), new Among("eacht", 19, 1, "", methodObject), new Among("grafa\u00edocht", -1, 4, "", methodObject), new Among("arcachta\u00ed", -1, 2, "", methodObject), new Among("grafa\u00edochta\u00ed", -1, 4, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("imid", -1, 1, "", methodObject), new Among("aimid", 0, 1, "", methodObject), new Among("\u00edmid", -1, 1, "", methodObject), new Among("a\u00edmid", 2, 1, "", methodObject), new Among("adh", -1, 2, "", methodObject), new Among("eadh", 4, 2, "", methodObject), new Among("faidh", -1, 1, "", methodObject), new Among("fidh", -1, 1, "", methodObject), new Among("\u00e1il", -1, 2, "", methodObject), new Among("ain", -1, 2, "", methodObject), new Among("tear", -1, 2, "", methodObject), new Among("tar", -1, 2, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001', '\u0011', '\u0004', '\u0002'};
    private int I_p2;
    private int I_p1;
    private int I_pV;

    private void copy_from(IrishStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }

    private boolean r_mark_regions() {
        int v_3;
        block11: {
            int v_1;
            block10: {
                this.I_pV = this.limit;
                this.I_p1 = this.limit;
                this.I_p2 = this.limit;
                v_1 = this.cursor;
                while (!this.in_grouping(g_v, 97, 250)) {
                    if (this.cursor < this.limit) {
                        ++this.cursor;
                        continue;
                    }
                    break block10;
                }
                this.I_pV = this.cursor;
            }
            v_3 = this.cursor = v_1;
            while (!this.in_grouping(g_v, 97, 250)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block11;
            }
            while (!this.out_grouping(g_v, 97, 250)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block11;
            }
            this.I_p1 = this.cursor;
            while (!this.in_grouping(g_v, 97, 250)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block11;
            }
            while (!this.out_grouping(g_v, 97, 250)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block11;
            }
            this.I_p2 = this.cursor;
        }
        this.cursor = v_3;
        return true;
    }

    private boolean r_initial_morph() {
        this.bra = this.cursor;
        int among_var = this.find_among(a_0, 24);
        if (among_var == 0) {
            return false;
        }
        this.ket = this.cursor;
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
                this.slice_from("f");
                break;
            }
            case 4: {
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("s");
                break;
            }
            case 6: {
                this.slice_from("b");
                break;
            }
            case 7: {
                this.slice_from("c");
                break;
            }
            case 8: {
                this.slice_from("d");
                break;
            }
            case 9: {
                this.slice_from("f");
                break;
            }
            case 10: {
                this.slice_from("g");
                break;
            }
            case 11: {
                this.slice_from("p");
                break;
            }
            case 12: {
                this.slice_from("s");
                break;
            }
            case 13: {
                this.slice_from("t");
                break;
            }
            case 14: {
                this.slice_from("b");
                break;
            }
            case 15: {
                this.slice_from("c");
                break;
            }
            case 16: {
                this.slice_from("d");
                break;
            }
            case 17: {
                this.slice_from("f");
                break;
            }
            case 18: {
                this.slice_from("g");
                break;
            }
            case 19: {
                this.slice_from("m");
                break;
            }
            case 20: {
                this.slice_from("p");
                break;
            }
            case 21: {
                this.slice_from("t");
            }
        }
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

    private boolean r_noun_sfx() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 16);
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
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_deriv() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 25);
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
                this.slice_from("arc");
                break;
            }
            case 3: {
                this.slice_from("gin");
                break;
            }
            case 4: {
                this.slice_from("graf");
                break;
            }
            case 5: {
                this.slice_from("paite");
                break;
            }
            case 6: {
                this.slice_from("\u00f3id");
            }
        }
        return true;
    }

    private boolean r_verb_sfx() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 12);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    @Override
    public boolean stem() {
        int v_1 = this.cursor;
        if (!this.r_initial_morph()) {
            // empty if block
        }
        int v_2 = this.cursor = v_1;
        if (!this.r_mark_regions()) {
            // empty if block
        }
        this.limit_backward = this.cursor = v_2;
        this.cursor = this.limit;
        int v_3 = this.limit - this.cursor;
        if (!this.r_noun_sfx()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (!this.r_deriv()) {
            // empty if block
        }
        this.cursor = this.limit - v_4;
        int v_5 = this.limit - this.cursor;
        if (!this.r_verb_sfx()) {
            // empty if block
        }
        this.cursor = this.limit - v_5;
        this.cursor = this.limit_backward;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof IrishStemmer;
    }

    public int hashCode() {
        return IrishStemmer.class.getName().hashCode();
    }
}

