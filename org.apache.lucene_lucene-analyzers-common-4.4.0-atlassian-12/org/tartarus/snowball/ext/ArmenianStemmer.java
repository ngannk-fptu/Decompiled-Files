/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class ArmenianStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final ArmenianStemmer methodObject = new ArmenianStemmer();
    private static final Among[] a_0 = new Among[]{new Among("\u0580\u0578\u0580\u0564", -1, 1, "", methodObject), new Among("\u0565\u0580\u0578\u0580\u0564", 0, 1, "", methodObject), new Among("\u0561\u056c\u056b", -1, 1, "", methodObject), new Among("\u0561\u056f\u056b", -1, 1, "", methodObject), new Among("\u0578\u0580\u0561\u056f", -1, 1, "", methodObject), new Among("\u0565\u0572", -1, 1, "", methodObject), new Among("\u0561\u056f\u0561\u0576", -1, 1, "", methodObject), new Among("\u0561\u0580\u0561\u0576", -1, 1, "", methodObject), new Among("\u0565\u0576", -1, 1, "", methodObject), new Among("\u0565\u056f\u0565\u0576", 8, 1, "", methodObject), new Among("\u0565\u0580\u0565\u0576", 8, 1, "", methodObject), new Among("\u0578\u0580\u0567\u0576", -1, 1, "", methodObject), new Among("\u056b\u0576", -1, 1, "", methodObject), new Among("\u0563\u056b\u0576", 12, 1, "", methodObject), new Among("\u0578\u057e\u056b\u0576", 12, 1, "", methodObject), new Among("\u056c\u0561\u0575\u0576", -1, 1, "", methodObject), new Among("\u057e\u0578\u0582\u0576", -1, 1, "", methodObject), new Among("\u057a\u0565\u057d", -1, 1, "", methodObject), new Among("\u056b\u057e", -1, 1, "", methodObject), new Among("\u0561\u057f", -1, 1, "", methodObject), new Among("\u0561\u057e\u0565\u057f", -1, 1, "", methodObject), new Among("\u056f\u0578\u057f", -1, 1, "", methodObject), new Among("\u0562\u0561\u0580", -1, 1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("\u0561", -1, 1, "", methodObject), new Among("\u0561\u0581\u0561", 0, 1, "", methodObject), new Among("\u0565\u0581\u0561", 0, 1, "", methodObject), new Among("\u057e\u0565", -1, 1, "", methodObject), new Among("\u0561\u0581\u0580\u056b", -1, 1, "", methodObject), new Among("\u0561\u0581\u056b", -1, 1, "", methodObject), new Among("\u0565\u0581\u056b", -1, 1, "", methodObject), new Among("\u057e\u0565\u0581\u056b", 6, 1, "", methodObject), new Among("\u0561\u056c", -1, 1, "", methodObject), new Among("\u0568\u0561\u056c", 8, 1, "", methodObject), new Among("\u0561\u0576\u0561\u056c", 8, 1, "", methodObject), new Among("\u0565\u0576\u0561\u056c", 8, 1, "", methodObject), new Among("\u0561\u0581\u0576\u0561\u056c", 8, 1, "", methodObject), new Among("\u0565\u056c", -1, 1, "", methodObject), new Among("\u0568\u0565\u056c", 13, 1, "", methodObject), new Among("\u0576\u0565\u056c", 13, 1, "", methodObject), new Among("\u0581\u0576\u0565\u056c", 15, 1, "", methodObject), new Among("\u0565\u0581\u0576\u0565\u056c", 16, 1, "", methodObject), new Among("\u0579\u0565\u056c", 13, 1, "", methodObject), new Among("\u057e\u0565\u056c", 13, 1, "", methodObject), new Among("\u0561\u0581\u057e\u0565\u056c", 19, 1, "", methodObject), new Among("\u0565\u0581\u057e\u0565\u056c", 19, 1, "", methodObject), new Among("\u057f\u0565\u056c", 13, 1, "", methodObject), new Among("\u0561\u057f\u0565\u056c", 22, 1, "", methodObject), new Among("\u0578\u057f\u0565\u056c", 22, 1, "", methodObject), new Among("\u056f\u0578\u057f\u0565\u056c", 24, 1, "", methodObject), new Among("\u057e\u0561\u056e", -1, 1, "", methodObject), new Among("\u0578\u0582\u0574", -1, 1, "", methodObject), new Among("\u057e\u0578\u0582\u0574", 27, 1, "", methodObject), new Among("\u0561\u0576", -1, 1, "", methodObject), new Among("\u0581\u0561\u0576", 29, 1, "", methodObject), new Among("\u0561\u0581\u0561\u0576", 30, 1, "", methodObject), new Among("\u0561\u0581\u0580\u056b\u0576", -1, 1, "", methodObject), new Among("\u0561\u0581\u056b\u0576", -1, 1, "", methodObject), new Among("\u0565\u0581\u056b\u0576", -1, 1, "", methodObject), new Among("\u057e\u0565\u0581\u056b\u0576", 34, 1, "", methodObject), new Among("\u0561\u056c\u056b\u057d", -1, 1, "", methodObject), new Among("\u0565\u056c\u056b\u057d", -1, 1, "", methodObject), new Among("\u0561\u057e", -1, 1, "", methodObject), new Among("\u0561\u0581\u0561\u057e", 38, 1, "", methodObject), new Among("\u0565\u0581\u0561\u057e", 38, 1, "", methodObject), new Among("\u0561\u056c\u0578\u057e", -1, 1, "", methodObject), new Among("\u0565\u056c\u0578\u057e", -1, 1, "", methodObject), new Among("\u0561\u0580", -1, 1, "", methodObject), new Among("\u0561\u0581\u0561\u0580", 43, 1, "", methodObject), new Among("\u0565\u0581\u0561\u0580", 43, 1, "", methodObject), new Among("\u0561\u0581\u0580\u056b\u0580", -1, 1, "", methodObject), new Among("\u0561\u0581\u056b\u0580", -1, 1, "", methodObject), new Among("\u0565\u0581\u056b\u0580", -1, 1, "", methodObject), new Among("\u057e\u0565\u0581\u056b\u0580", 48, 1, "", methodObject), new Among("\u0561\u0581", -1, 1, "", methodObject), new Among("\u0565\u0581", -1, 1, "", methodObject), new Among("\u0561\u0581\u0580\u0565\u0581", 51, 1, "", methodObject), new Among("\u0561\u056c\u0578\u0582\u0581", -1, 1, "", methodObject), new Among("\u0565\u056c\u0578\u0582\u0581", -1, 1, "", methodObject), new Among("\u0561\u056c\u0578\u0582", -1, 1, "", methodObject), new Among("\u0565\u056c\u0578\u0582", -1, 1, "", methodObject), new Among("\u0561\u0584", -1, 1, "", methodObject), new Among("\u0581\u0561\u0584", 57, 1, "", methodObject), new Among("\u0561\u0581\u0561\u0584", 58, 1, "", methodObject), new Among("\u0561\u0581\u0580\u056b\u0584", -1, 1, "", methodObject), new Among("\u0561\u0581\u056b\u0584", -1, 1, "", methodObject), new Among("\u0565\u0581\u056b\u0584", -1, 1, "", methodObject), new Among("\u057e\u0565\u0581\u056b\u0584", 62, 1, "", methodObject), new Among("\u0561\u0576\u0584", -1, 1, "", methodObject), new Among("\u0581\u0561\u0576\u0584", 64, 1, "", methodObject), new Among("\u0561\u0581\u0561\u0576\u0584", 65, 1, "", methodObject), new Among("\u0561\u0581\u0580\u056b\u0576\u0584", -1, 1, "", methodObject), new Among("\u0561\u0581\u056b\u0576\u0584", -1, 1, "", methodObject), new Among("\u0565\u0581\u056b\u0576\u0584", -1, 1, "", methodObject), new Among("\u057e\u0565\u0581\u056b\u0576\u0584", 69, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("\u0578\u0580\u0564", -1, 1, "", methodObject), new Among("\u0578\u0582\u0575\u0569", -1, 1, "", methodObject), new Among("\u0578\u0582\u0570\u056b", -1, 1, "", methodObject), new Among("\u0581\u056b", -1, 1, "", methodObject), new Among("\u056b\u056c", -1, 1, "", methodObject), new Among("\u0561\u056f", -1, 1, "", methodObject), new Among("\u0575\u0561\u056f", 5, 1, "", methodObject), new Among("\u0561\u0576\u0561\u056f", 5, 1, "", methodObject), new Among("\u056b\u056f", -1, 1, "", methodObject), new Among("\u0578\u0582\u056f", -1, 1, "", methodObject), new Among("\u0561\u0576", -1, 1, "", methodObject), new Among("\u057a\u0561\u0576", 10, 1, "", methodObject), new Among("\u057d\u057f\u0561\u0576", 10, 1, "", methodObject), new Among("\u0561\u0580\u0561\u0576", 10, 1, "", methodObject), new Among("\u0565\u0572\u0567\u0576", -1, 1, "", methodObject), new Among("\u0575\u0578\u0582\u0576", -1, 1, "", methodObject), new Among("\u0578\u0582\u0569\u0575\u0578\u0582\u0576", 15, 1, "", methodObject), new Among("\u0561\u056e\u0578", -1, 1, "", methodObject), new Among("\u056b\u0579", -1, 1, "", methodObject), new Among("\u0578\u0582\u057d", -1, 1, "", methodObject), new Among("\u0578\u0582\u057d\u057f", -1, 1, "", methodObject), new Among("\u0563\u0561\u0580", -1, 1, "", methodObject), new Among("\u057e\u0578\u0580", -1, 1, "", methodObject), new Among("\u0561\u057e\u0578\u0580", 22, 1, "", methodObject), new Among("\u0578\u0581", -1, 1, "", methodObject), new Among("\u0561\u0576\u0585\u0581", -1, 1, "", methodObject), new Among("\u0578\u0582", -1, 1, "", methodObject), new Among("\u0584", -1, 1, "", methodObject), new Among("\u0579\u0565\u0584", 27, 1, "", methodObject), new Among("\u056b\u0584", 27, 1, "", methodObject), new Among("\u0561\u056c\u056b\u0584", 29, 1, "", methodObject), new Among("\u0561\u0576\u056b\u0584", 29, 1, "", methodObject), new Among("\u057e\u0561\u056e\u0584", 27, 1, "", methodObject), new Among("\u0578\u0582\u0575\u0584", 27, 1, "", methodObject), new Among("\u0565\u0576\u0584", 27, 1, "", methodObject), new Among("\u0578\u0576\u0584", 27, 1, "", methodObject), new Among("\u0578\u0582\u0576\u0584", 27, 1, "", methodObject), new Among("\u0574\u0578\u0582\u0576\u0584", 36, 1, "", methodObject), new Among("\u056b\u0579\u0584", 27, 1, "", methodObject), new Among("\u0561\u0580\u0584", 27, 1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("\u057d\u0561", -1, 1, "", methodObject), new Among("\u057e\u0561", -1, 1, "", methodObject), new Among("\u0561\u0574\u0562", -1, 1, "", methodObject), new Among("\u0564", -1, 1, "", methodObject), new Among("\u0561\u0576\u0564", 3, 1, "", methodObject), new Among("\u0578\u0582\u0569\u0575\u0561\u0576\u0564", 4, 1, "", methodObject), new Among("\u057e\u0561\u0576\u0564", 4, 1, "", methodObject), new Among("\u0578\u057b\u0564", 3, 1, "", methodObject), new Among("\u0565\u0580\u0564", 3, 1, "", methodObject), new Among("\u0576\u0565\u0580\u0564", 8, 1, "", methodObject), new Among("\u0578\u0582\u0564", 3, 1, "", methodObject), new Among("\u0568", -1, 1, "", methodObject), new Among("\u0561\u0576\u0568", 11, 1, "", methodObject), new Among("\u0578\u0582\u0569\u0575\u0561\u0576\u0568", 12, 1, "", methodObject), new Among("\u057e\u0561\u0576\u0568", 12, 1, "", methodObject), new Among("\u0578\u057b\u0568", 11, 1, "", methodObject), new Among("\u0565\u0580\u0568", 11, 1, "", methodObject), new Among("\u0576\u0565\u0580\u0568", 16, 1, "", methodObject), new Among("\u056b", -1, 1, "", methodObject), new Among("\u057e\u056b", 18, 1, "", methodObject), new Among("\u0565\u0580\u056b", 18, 1, "", methodObject), new Among("\u0576\u0565\u0580\u056b", 20, 1, "", methodObject), new Among("\u0561\u0576\u0578\u0582\u0574", -1, 1, "", methodObject), new Among("\u0565\u0580\u0578\u0582\u0574", -1, 1, "", methodObject), new Among("\u0576\u0565\u0580\u0578\u0582\u0574", 23, 1, "", methodObject), new Among("\u0576", -1, 1, "", methodObject), new Among("\u0561\u0576", 25, 1, "", methodObject), new Among("\u0578\u0582\u0569\u0575\u0561\u0576", 26, 1, "", methodObject), new Among("\u057e\u0561\u0576", 26, 1, "", methodObject), new Among("\u056b\u0576", 25, 1, "", methodObject), new Among("\u0565\u0580\u056b\u0576", 29, 1, "", methodObject), new Among("\u0576\u0565\u0580\u056b\u0576", 30, 1, "", methodObject), new Among("\u0578\u0582\u0569\u0575\u0561\u0576\u0576", 25, 1, "", methodObject), new Among("\u0565\u0580\u0576", 25, 1, "", methodObject), new Among("\u0576\u0565\u0580\u0576", 33, 1, "", methodObject), new Among("\u0578\u0582\u0576", 25, 1, "", methodObject), new Among("\u0578\u057b", -1, 1, "", methodObject), new Among("\u0578\u0582\u0569\u0575\u0561\u0576\u057d", -1, 1, "", methodObject), new Among("\u057e\u0561\u0576\u057d", -1, 1, "", methodObject), new Among("\u0578\u057b\u057d", -1, 1, "", methodObject), new Among("\u0578\u057e", -1, 1, "", methodObject), new Among("\u0561\u0576\u0578\u057e", 40, 1, "", methodObject), new Among("\u057e\u0578\u057e", 40, 1, "", methodObject), new Among("\u0565\u0580\u0578\u057e", 40, 1, "", methodObject), new Among("\u0576\u0565\u0580\u0578\u057e", 43, 1, "", methodObject), new Among("\u0565\u0580", -1, 1, "", methodObject), new Among("\u0576\u0565\u0580", 45, 1, "", methodObject), new Among("\u0581", -1, 1, "", methodObject), new Among("\u056b\u0581", 47, 1, "", methodObject), new Among("\u057e\u0561\u0576\u056b\u0581", 48, 1, "", methodObject), new Among("\u0578\u057b\u056b\u0581", 48, 1, "", methodObject), new Among("\u057e\u056b\u0581", 48, 1, "", methodObject), new Among("\u0565\u0580\u056b\u0581", 48, 1, "", methodObject), new Among("\u0576\u0565\u0580\u056b\u0581", 52, 1, "", methodObject), new Among("\u0581\u056b\u0581", 48, 1, "", methodObject), new Among("\u0578\u0581", 47, 1, "", methodObject), new Among("\u0578\u0582\u0581", 47, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u00d1', '\u0004', '\u0080', '\u0000', '\u0012'};
    private int I_p2;
    private int I_pV;

    private void copy_from(ArmenianStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }

    private boolean r_mark_regions() {
        int v_1;
        block8: {
            this.I_pV = this.limit;
            this.I_p2 = this.limit;
            v_1 = this.cursor;
            while (!this.in_grouping(g_v, 1377, 1413)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            this.I_pV = this.cursor;
            while (!this.out_grouping(g_v, 1377, 1413)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            while (!this.in_grouping(g_v, 1377, 1413)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            while (!this.out_grouping(g_v, 1377, 1413)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            this.I_p2 = this.cursor;
        }
        this.cursor = v_1;
        return true;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_adjective() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_0, 23);
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
            }
        }
        return true;
    }

    private boolean r_verb() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 71);
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
            }
        }
        return true;
    }

    private boolean r_noun() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 40);
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
            }
        }
        return true;
    }

    private boolean r_ending() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 57);
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
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        int v_3 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.cursor = this.limit - v_2;
        int v_4 = this.limit - this.cursor;
        if (!this.r_ending()) {
            // empty if block
        }
        this.cursor = this.limit - v_4;
        int v_5 = this.limit - this.cursor;
        if (!this.r_verb()) {
            // empty if block
        }
        this.cursor = this.limit - v_5;
        int v_6 = this.limit - this.cursor;
        if (!this.r_adjective()) {
            // empty if block
        }
        this.cursor = this.limit - v_6;
        int v_7 = this.limit - this.cursor;
        if (!this.r_noun()) {
            // empty if block
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit_backward = v_3;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof ArmenianStemmer;
    }

    public int hashCode() {
        return ArmenianStemmer.class.getName().hashCode();
    }
}

