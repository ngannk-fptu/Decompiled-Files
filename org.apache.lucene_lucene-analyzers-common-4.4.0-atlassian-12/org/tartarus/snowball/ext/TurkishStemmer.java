/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class TurkishStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final TurkishStemmer methodObject = new TurkishStemmer();
    private static final Among[] a_0 = new Among[]{new Among("m", -1, -1, "", methodObject), new Among("n", -1, -1, "", methodObject), new Among("miz", -1, -1, "", methodObject), new Among("niz", -1, -1, "", methodObject), new Among("muz", -1, -1, "", methodObject), new Among("nuz", -1, -1, "", methodObject), new Among("m\u00fcz", -1, -1, "", methodObject), new Among("n\u00fcz", -1, -1, "", methodObject), new Among("m\u0131z", -1, -1, "", methodObject), new Among("n\u0131z", -1, -1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("leri", -1, -1, "", methodObject), new Among("lar\u0131", -1, -1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ni", -1, -1, "", methodObject), new Among("nu", -1, -1, "", methodObject), new Among("n\u00fc", -1, -1, "", methodObject), new Among("n\u0131", -1, -1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("in", -1, -1, "", methodObject), new Among("un", -1, -1, "", methodObject), new Among("\u00fcn", -1, -1, "", methodObject), new Among("\u0131n", -1, -1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("a", -1, -1, "", methodObject), new Among("e", -1, -1, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("na", -1, -1, "", methodObject), new Among("ne", -1, -1, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("da", -1, -1, "", methodObject), new Among("ta", -1, -1, "", methodObject), new Among("de", -1, -1, "", methodObject), new Among("te", -1, -1, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("nda", -1, -1, "", methodObject), new Among("nde", -1, -1, "", methodObject)};
    private static final Among[] a_8 = new Among[]{new Among("dan", -1, -1, "", methodObject), new Among("tan", -1, -1, "", methodObject), new Among("den", -1, -1, "", methodObject), new Among("ten", -1, -1, "", methodObject)};
    private static final Among[] a_9 = new Among[]{new Among("ndan", -1, -1, "", methodObject), new Among("nden", -1, -1, "", methodObject)};
    private static final Among[] a_10 = new Among[]{new Among("la", -1, -1, "", methodObject), new Among("le", -1, -1, "", methodObject)};
    private static final Among[] a_11 = new Among[]{new Among("ca", -1, -1, "", methodObject), new Among("ce", -1, -1, "", methodObject)};
    private static final Among[] a_12 = new Among[]{new Among("im", -1, -1, "", methodObject), new Among("um", -1, -1, "", methodObject), new Among("\u00fcm", -1, -1, "", methodObject), new Among("\u0131m", -1, -1, "", methodObject)};
    private static final Among[] a_13 = new Among[]{new Among("sin", -1, -1, "", methodObject), new Among("sun", -1, -1, "", methodObject), new Among("s\u00fcn", -1, -1, "", methodObject), new Among("s\u0131n", -1, -1, "", methodObject)};
    private static final Among[] a_14 = new Among[]{new Among("iz", -1, -1, "", methodObject), new Among("uz", -1, -1, "", methodObject), new Among("\u00fcz", -1, -1, "", methodObject), new Among("\u0131z", -1, -1, "", methodObject)};
    private static final Among[] a_15 = new Among[]{new Among("siniz", -1, -1, "", methodObject), new Among("sunuz", -1, -1, "", methodObject), new Among("s\u00fcn\u00fcz", -1, -1, "", methodObject), new Among("s\u0131n\u0131z", -1, -1, "", methodObject)};
    private static final Among[] a_16 = new Among[]{new Among("lar", -1, -1, "", methodObject), new Among("ler", -1, -1, "", methodObject)};
    private static final Among[] a_17 = new Among[]{new Among("niz", -1, -1, "", methodObject), new Among("nuz", -1, -1, "", methodObject), new Among("n\u00fcz", -1, -1, "", methodObject), new Among("n\u0131z", -1, -1, "", methodObject)};
    private static final Among[] a_18 = new Among[]{new Among("dir", -1, -1, "", methodObject), new Among("tir", -1, -1, "", methodObject), new Among("dur", -1, -1, "", methodObject), new Among("tur", -1, -1, "", methodObject), new Among("d\u00fcr", -1, -1, "", methodObject), new Among("t\u00fcr", -1, -1, "", methodObject), new Among("d\u0131r", -1, -1, "", methodObject), new Among("t\u0131r", -1, -1, "", methodObject)};
    private static final Among[] a_19 = new Among[]{new Among("cas\u0131na", -1, -1, "", methodObject), new Among("cesine", -1, -1, "", methodObject)};
    private static final Among[] a_20 = new Among[]{new Among("di", -1, -1, "", methodObject), new Among("ti", -1, -1, "", methodObject), new Among("dik", -1, -1, "", methodObject), new Among("tik", -1, -1, "", methodObject), new Among("duk", -1, -1, "", methodObject), new Among("tuk", -1, -1, "", methodObject), new Among("d\u00fck", -1, -1, "", methodObject), new Among("t\u00fck", -1, -1, "", methodObject), new Among("d\u0131k", -1, -1, "", methodObject), new Among("t\u0131k", -1, -1, "", methodObject), new Among("dim", -1, -1, "", methodObject), new Among("tim", -1, -1, "", methodObject), new Among("dum", -1, -1, "", methodObject), new Among("tum", -1, -1, "", methodObject), new Among("d\u00fcm", -1, -1, "", methodObject), new Among("t\u00fcm", -1, -1, "", methodObject), new Among("d\u0131m", -1, -1, "", methodObject), new Among("t\u0131m", -1, -1, "", methodObject), new Among("din", -1, -1, "", methodObject), new Among("tin", -1, -1, "", methodObject), new Among("dun", -1, -1, "", methodObject), new Among("tun", -1, -1, "", methodObject), new Among("d\u00fcn", -1, -1, "", methodObject), new Among("t\u00fcn", -1, -1, "", methodObject), new Among("d\u0131n", -1, -1, "", methodObject), new Among("t\u0131n", -1, -1, "", methodObject), new Among("du", -1, -1, "", methodObject), new Among("tu", -1, -1, "", methodObject), new Among("d\u00fc", -1, -1, "", methodObject), new Among("t\u00fc", -1, -1, "", methodObject), new Among("d\u0131", -1, -1, "", methodObject), new Among("t\u0131", -1, -1, "", methodObject)};
    private static final Among[] a_21 = new Among[]{new Among("sa", -1, -1, "", methodObject), new Among("se", -1, -1, "", methodObject), new Among("sak", -1, -1, "", methodObject), new Among("sek", -1, -1, "", methodObject), new Among("sam", -1, -1, "", methodObject), new Among("sem", -1, -1, "", methodObject), new Among("san", -1, -1, "", methodObject), new Among("sen", -1, -1, "", methodObject)};
    private static final Among[] a_22 = new Among[]{new Among("mi\u015f", -1, -1, "", methodObject), new Among("mu\u015f", -1, -1, "", methodObject), new Among("m\u00fc\u015f", -1, -1, "", methodObject), new Among("m\u0131\u015f", -1, -1, "", methodObject)};
    private static final Among[] a_23 = new Among[]{new Among("b", -1, 1, "", methodObject), new Among("c", -1, 2, "", methodObject), new Among("d", -1, 3, "", methodObject), new Among("\u011f", -1, 4, "", methodObject)};
    private static final char[] g_vowel = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', ' ', '\b', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001'};
    private static final char[] g_U = new char[]{'\u0001', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\b', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001'};
    private static final char[] g_vowel1 = new char[]{'\u0001', '@', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001'};
    private static final char[] g_vowel2 = new char[]{'\u0011', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0082'};
    private static final char[] g_vowel3 = new char[]{'\u0001', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001'};
    private static final char[] g_vowel4 = new char[]{'\u0011'};
    private static final char[] g_vowel5 = new char[]{'A'};
    private static final char[] g_vowel6 = new char[]{'A'};
    private boolean B_continue_stemming_noun_suffixes;
    private int I_strlen;

    private void copy_from(TurkishStemmer other) {
        this.B_continue_stemming_noun_suffixes = other.B_continue_stemming_noun_suffixes;
        this.I_strlen = other.I_strlen;
        super.copy_from(other);
    }

    private boolean r_check_vowel_harmony() {
        int v_1;
        block27: {
            int v_2;
            v_1 = this.limit - this.cursor;
            while (true) {
                v_2 = this.limit - this.cursor;
                if (this.in_grouping_b(g_vowel, 97, 305)) break;
                this.cursor = this.limit - v_2;
                if (this.cursor <= this.limit_backward) {
                    return false;
                }
                --this.cursor;
            }
            this.cursor = this.limit - v_2;
            int v_3 = this.limit - this.cursor;
            if (this.eq_s_b(1, "a")) {
                while (true) {
                    int v_4 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_vowel1, 97, 305)) {
                        this.cursor = this.limit - v_4;
                        break block27;
                    }
                    this.cursor = this.limit - v_4;
                    if (this.cursor <= this.limit_backward) break;
                    --this.cursor;
                }
            }
            this.cursor = this.limit - v_3;
            if (this.eq_s_b(1, "e")) {
                while (true) {
                    int v_5 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_vowel2, 101, 252)) {
                        this.cursor = this.limit - v_5;
                        break block27;
                    }
                    this.cursor = this.limit - v_5;
                    if (this.cursor <= this.limit_backward) break;
                    --this.cursor;
                }
            }
            this.cursor = this.limit - v_3;
            if (this.eq_s_b(1, "\u0131")) {
                while (true) {
                    int v_6 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_vowel3, 97, 305)) {
                        this.cursor = this.limit - v_6;
                        break block27;
                    }
                    this.cursor = this.limit - v_6;
                    if (this.cursor <= this.limit_backward) break;
                    --this.cursor;
                }
            }
            this.cursor = this.limit - v_3;
            if (this.eq_s_b(1, "i")) {
                while (true) {
                    int v_7 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_vowel4, 101, 105)) {
                        this.cursor = this.limit - v_7;
                        break block27;
                    }
                    this.cursor = this.limit - v_7;
                    if (this.cursor <= this.limit_backward) break;
                    --this.cursor;
                }
            }
            this.cursor = this.limit - v_3;
            if (this.eq_s_b(1, "o")) {
                while (true) {
                    int v_8 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_vowel5, 111, 117)) {
                        this.cursor = this.limit - v_8;
                        break block27;
                    }
                    this.cursor = this.limit - v_8;
                    if (this.cursor <= this.limit_backward) break;
                    --this.cursor;
                }
            }
            this.cursor = this.limit - v_3;
            if (this.eq_s_b(1, "\u00f6")) {
                while (true) {
                    int v_9 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_vowel6, 246, 252)) {
                        this.cursor = this.limit - v_9;
                        break block27;
                    }
                    this.cursor = this.limit - v_9;
                    if (this.cursor <= this.limit_backward) break;
                    --this.cursor;
                }
            }
            this.cursor = this.limit - v_3;
            if (this.eq_s_b(1, "u")) {
                while (true) {
                    int v_10 = this.limit - this.cursor;
                    if (this.in_grouping_b(g_vowel5, 111, 117)) {
                        this.cursor = this.limit - v_10;
                        break block27;
                    }
                    this.cursor = this.limit - v_10;
                    if (this.cursor <= this.limit_backward) break;
                    --this.cursor;
                }
            }
            this.cursor = this.limit - v_3;
            if (!this.eq_s_b(1, "\u00fc")) {
                return false;
            }
            while (true) {
                int v_11 = this.limit - this.cursor;
                if (this.in_grouping_b(g_vowel6, 246, 252)) {
                    this.cursor = this.limit - v_11;
                    break;
                }
                this.cursor = this.limit - v_11;
                if (this.cursor <= this.limit_backward) {
                    return false;
                }
                --this.cursor;
            }
        }
        this.cursor = this.limit - v_1;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_mark_suffix_with_optional_n_consonant() {
        int v_1 = this.limit - this.cursor;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "n")) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                int v_3 = this.limit - this.cursor;
                if (this.in_grouping_b(g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        int v_4 = this.limit - this.cursor;
        int v_5 = this.limit - this.cursor;
        if (this.eq_s_b(1, "n")) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        int v_7 = this.limit - this.cursor;
        if (!this.in_grouping_b(g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_mark_suffix_with_optional_s_consonant() {
        int v_1 = this.limit - this.cursor;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                int v_3 = this.limit - this.cursor;
                if (this.in_grouping_b(g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        int v_4 = this.limit - this.cursor;
        int v_5 = this.limit - this.cursor;
        if (this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        int v_7 = this.limit - this.cursor;
        if (!this.in_grouping_b(g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_mark_suffix_with_optional_y_consonant() {
        int v_1 = this.limit - this.cursor;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "y")) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                int v_3 = this.limit - this.cursor;
                if (this.in_grouping_b(g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        int v_4 = this.limit - this.cursor;
        int v_5 = this.limit - this.cursor;
        if (this.eq_s_b(1, "y")) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        int v_7 = this.limit - this.cursor;
        if (!this.in_grouping_b(g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_mark_suffix_with_optional_U_vowel() {
        int v_1 = this.limit - this.cursor;
        int v_2 = this.limit - this.cursor;
        if (this.in_grouping_b(g_U, 105, 305)) {
            this.cursor = this.limit - v_2;
            if (this.cursor > this.limit_backward) {
                --this.cursor;
                int v_3 = this.limit - this.cursor;
                if (this.out_grouping_b(g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_3;
                    return true;
                }
            }
        }
        this.cursor = this.limit - v_1;
        int v_4 = this.limit - this.cursor;
        int v_5 = this.limit - this.cursor;
        if (this.in_grouping_b(g_U, 105, 305)) {
            this.cursor = this.limit - v_5;
            return false;
        }
        this.cursor = this.limit - v_4;
        int v_6 = this.limit - this.cursor;
        if (this.cursor <= this.limit_backward) {
            return false;
        }
        --this.cursor;
        int v_7 = this.limit - this.cursor;
        if (!this.out_grouping_b(g_vowel, 97, 305)) {
            return false;
        }
        this.cursor = this.limit - v_7;
        this.cursor = this.limit - v_6;
        return true;
    }

    private boolean r_mark_possessives() {
        if (this.find_among_b(a_0, 10) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_U_vowel();
    }

    private boolean r_mark_sU() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (!this.in_grouping_b(g_U, 105, 305)) {
            return false;
        }
        return this.r_mark_suffix_with_optional_s_consonant();
    }

    private boolean r_mark_lArI() {
        return this.find_among_b(a_1, 2) != 0;
    }

    private boolean r_mark_yU() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (!this.in_grouping_b(g_U, 105, 305)) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_nU() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_2, 4) != 0;
    }

    private boolean r_mark_nUn() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_3, 4) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_n_consonant();
    }

    private boolean r_mark_yA() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_4, 2) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_nA() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_5, 2) != 0;
    }

    private boolean r_mark_DA() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_6, 4) != 0;
    }

    private boolean r_mark_ndA() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_7, 2) != 0;
    }

    private boolean r_mark_DAn() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_8, 4) != 0;
    }

    private boolean r_mark_ndAn() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_9, 2) != 0;
    }

    private boolean r_mark_ylA() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_10, 2) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_ki() {
        return this.eq_s_b(2, "ki");
    }

    private boolean r_mark_ncA() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_11, 2) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_n_consonant();
    }

    private boolean r_mark_yUm() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_12, 4) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_sUn() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_13, 4) != 0;
    }

    private boolean r_mark_yUz() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_14, 4) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_sUnUz() {
        return this.find_among_b(a_15, 4) != 0;
    }

    private boolean r_mark_lAr() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_16, 2) != 0;
    }

    private boolean r_mark_nUz() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_17, 4) != 0;
    }

    private boolean r_mark_DUr() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        return this.find_among_b(a_18, 8) != 0;
    }

    private boolean r_mark_cAsInA() {
        return this.find_among_b(a_19, 2) != 0;
    }

    private boolean r_mark_yDU() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_20, 32) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_ysA() {
        if (this.find_among_b(a_21, 8) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_ymUs_() {
        if (!this.r_check_vowel_harmony()) {
            return false;
        }
        if (this.find_among_b(a_22, 4) == 0) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    private boolean r_mark_yken() {
        if (!this.eq_s_b(3, "ken")) {
            return false;
        }
        return this.r_mark_suffix_with_optional_y_consonant();
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_stem_nominal_verb_suffixes() {
        block17: {
            block23: {
                block22: {
                    int v_1;
                    block21: {
                        block20: {
                            block19: {
                                block18: {
                                    this.ket = this.cursor;
                                    this.B_continue_stemming_noun_suffixes = true;
                                    v_1 = this.limit - this.cursor;
                                    int v_2 = this.limit - this.cursor;
                                    if (this.r_mark_ymUs_()) break block17;
                                    this.cursor = this.limit - v_2;
                                    if (this.r_mark_yDU()) break block17;
                                    this.cursor = this.limit - v_2;
                                    if (this.r_mark_ysA()) break block17;
                                    this.cursor = this.limit - v_2;
                                    if (this.r_mark_yken()) break block17;
                                    this.cursor = this.limit - v_1;
                                    if (!this.r_mark_cAsInA()) break block18;
                                    int v_3 = this.limit - this.cursor;
                                    if (!this.r_mark_sUnUz()) {
                                        this.cursor = this.limit - v_3;
                                        if (!this.r_mark_lAr()) {
                                            this.cursor = this.limit - v_3;
                                            if (!this.r_mark_yUm()) {
                                                this.cursor = this.limit - v_3;
                                                if (!this.r_mark_sUn()) {
                                                    this.cursor = this.limit - v_3;
                                                    if (!this.r_mark_yUz()) {
                                                        this.cursor = this.limit - v_3;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (this.r_mark_ymUs_()) break block17;
                                }
                                this.cursor = this.limit - v_1;
                                if (this.r_mark_lAr()) break block19;
                                this.cursor = this.limit - v_1;
                                if (this.r_mark_nUz()) break block20;
                                break block21;
                            }
                            this.bra = this.cursor;
                            this.slice_del();
                            int v_4 = this.limit - this.cursor;
                            this.ket = this.cursor;
                            int v_5 = this.limit - this.cursor;
                            if (!this.r_mark_DUr()) {
                                this.cursor = this.limit - v_5;
                                if (!this.r_mark_yDU()) {
                                    this.cursor = this.limit - v_5;
                                    if (!this.r_mark_ysA()) {
                                        this.cursor = this.limit - v_5;
                                        if (!this.r_mark_ymUs_()) {
                                            this.cursor = this.limit - v_4;
                                        }
                                    }
                                }
                            }
                            this.B_continue_stemming_noun_suffixes = false;
                            break block17;
                        }
                        int v_6 = this.limit - this.cursor;
                        if (this.r_mark_yDU()) break block17;
                        this.cursor = this.limit - v_6;
                        if (this.r_mark_ysA()) break block17;
                    }
                    this.cursor = this.limit - v_1;
                    int v_7 = this.limit - this.cursor;
                    if (this.r_mark_sUnUz()) break block22;
                    this.cursor = this.limit - v_7;
                    if (this.r_mark_yUz()) break block22;
                    this.cursor = this.limit - v_7;
                    if (this.r_mark_sUn()) break block22;
                    this.cursor = this.limit - v_7;
                    if (this.r_mark_yUm()) break block22;
                    this.cursor = this.limit - v_1;
                    if (!this.r_mark_DUr()) {
                        return false;
                    }
                    break block23;
                }
                this.bra = this.cursor;
                this.slice_del();
                int v_8 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.r_mark_ymUs_()) {
                    this.cursor = this.limit - v_8;
                }
                break block17;
            }
            this.bra = this.cursor;
            this.slice_del();
            int v_9 = this.limit - this.cursor;
            this.ket = this.cursor;
            int v_10 = this.limit - this.cursor;
            if (!this.r_mark_sUnUz()) {
                this.cursor = this.limit - v_10;
                if (!this.r_mark_lAr()) {
                    this.cursor = this.limit - v_10;
                    if (!this.r_mark_yUm()) {
                        this.cursor = this.limit - v_10;
                        if (!this.r_mark_sUn()) {
                            this.cursor = this.limit - v_10;
                            if (!this.r_mark_yUz()) {
                                this.cursor = this.limit - v_10;
                            }
                        }
                    }
                }
            }
            if (!this.r_mark_ymUs_()) {
                this.cursor = this.limit - v_9;
            }
        }
        this.bra = this.cursor;
        this.slice_del();
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_stem_suffix_chain_before_ki() {
        block15: {
            block13: {
                int v_1;
                block14: {
                    block12: {
                        this.ket = this.cursor;
                        if (!this.r_mark_ki()) {
                            return false;
                        }
                        v_1 = this.limit - this.cursor;
                        if (this.r_mark_DA()) break block12;
                        this.cursor = this.limit - v_1;
                        if (this.r_mark_nUn()) break block13;
                        break block14;
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    int v_2 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    int v_3 = this.limit - this.cursor;
                    if (this.r_mark_lAr()) {
                        this.bra = this.cursor;
                        this.slice_del();
                        int v_4 = this.limit - this.cursor;
                        if (this.r_stem_suffix_chain_before_ki()) return true;
                        this.cursor = this.limit - v_4;
                        return true;
                    }
                    this.cursor = this.limit - v_3;
                    if (!this.r_mark_possessives()) {
                        this.cursor = this.limit - v_2;
                        return true;
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    int v_5 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (!this.r_mark_lAr()) {
                        this.cursor = this.limit - v_5;
                        return true;
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    if (this.r_stem_suffix_chain_before_ki()) return true;
                    this.cursor = this.limit - v_5;
                    return true;
                }
                this.cursor = this.limit - v_1;
                if (!this.r_mark_ndA()) {
                    return false;
                }
                break block15;
            }
            this.bra = this.cursor;
            this.slice_del();
            int v_6 = this.limit - this.cursor;
            this.ket = this.cursor;
            int v_7 = this.limit - this.cursor;
            if (this.r_mark_lArI()) {
                this.bra = this.cursor;
                this.slice_del();
                return true;
            }
            this.ket = this.cursor = this.limit - v_7;
            int v_8 = this.limit - this.cursor;
            if (!this.r_mark_possessives()) {
                this.cursor = this.limit - v_8;
                if (!this.r_mark_sU()) {
                    this.cursor = this.limit - v_7;
                    if (this.r_stem_suffix_chain_before_ki()) return true;
                    this.cursor = this.limit - v_6;
                    return true;
                }
            }
            this.bra = this.cursor;
            this.slice_del();
            int v_9 = this.limit - this.cursor;
            this.ket = this.cursor;
            if (!this.r_mark_lAr()) {
                this.cursor = this.limit - v_9;
                return true;
            }
            this.bra = this.cursor;
            this.slice_del();
            if (this.r_stem_suffix_chain_before_ki()) return true;
            this.cursor = this.limit - v_9;
            return true;
        }
        int v_10 = this.limit - this.cursor;
        if (this.r_mark_lArI()) {
            this.bra = this.cursor;
            this.slice_del();
            return true;
        }
        this.cursor = this.limit - v_10;
        if (!this.r_mark_sU()) {
            this.cursor = this.limit - v_10;
            if (this.r_stem_suffix_chain_before_ki()) return true;
            return false;
        }
        this.bra = this.cursor;
        this.slice_del();
        int v_11 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.r_mark_lAr()) {
            this.cursor = this.limit - v_11;
            return true;
        }
        this.bra = this.cursor;
        this.slice_del();
        if (this.r_stem_suffix_chain_before_ki()) return true;
        this.cursor = this.limit - v_11;
        return true;
    }

    /*
     * Unable to fully structure code
     */
    private boolean r_stem_noun_suffixes() {
        block40: {
            block56: {
                block57: {
                    block55: {
                        block54: {
                            block52: {
                                block53: {
                                    block51: {
                                        block50: {
                                            block48: {
                                                block49: {
                                                    block47: {
                                                        block44: {
                                                            block46: {
                                                                block45: {
                                                                    block43: {
                                                                        block41: {
                                                                            block42: {
                                                                                block39: {
                                                                                    v_1 = this.limit - this.cursor;
                                                                                    this.ket = this.cursor;
                                                                                    if (!this.r_mark_lAr()) break block39;
                                                                                    this.bra = this.cursor;
                                                                                    this.slice_del();
                                                                                    v_2 = this.limit - this.cursor;
                                                                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                                                                        this.cursor = this.limit - v_2;
                                                                                    }
                                                                                    break block40;
                                                                                }
                                                                                this.ket = this.cursor = this.limit - v_1;
                                                                                if (!this.r_mark_ncA()) break block41;
                                                                                this.bra = this.cursor;
                                                                                this.slice_del();
                                                                                v_3 = this.limit - this.cursor;
                                                                                v_4 = this.limit - this.cursor;
                                                                                this.ket = this.cursor;
                                                                                if (!this.r_mark_lArI()) break block42;
                                                                                this.bra = this.cursor;
                                                                                this.slice_del();
                                                                                break block40;
                                                                            }
                                                                            this.ket = this.cursor = this.limit - v_4;
                                                                            v_5 = this.limit - this.cursor;
                                                                            if (this.r_mark_possessives()) ** GOTO lbl-1000
                                                                            this.cursor = this.limit - v_5;
                                                                            if (this.r_mark_sU()) lbl-1000:
                                                                            // 2 sources

                                                                            {
                                                                                this.bra = this.cursor;
                                                                                this.slice_del();
                                                                                v_6 = this.limit - this.cursor;
                                                                                this.ket = this.cursor;
                                                                                if (!this.r_mark_lAr()) {
                                                                                    this.cursor = this.limit - v_6;
                                                                                } else {
                                                                                    this.bra = this.cursor;
                                                                                    this.slice_del();
                                                                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                                                                        this.cursor = this.limit - v_6;
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                this.ket = this.cursor = this.limit - v_4;
                                                                                if (!this.r_mark_lAr()) {
                                                                                    this.cursor = this.limit - v_3;
                                                                                } else {
                                                                                    this.bra = this.cursor;
                                                                                    this.slice_del();
                                                                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                                                                        this.cursor = this.limit - v_3;
                                                                                    }
                                                                                }
                                                                            }
                                                                            break block40;
                                                                        }
                                                                        this.ket = this.cursor = this.limit - v_1;
                                                                        v_7 = this.limit - this.cursor;
                                                                        if (this.r_mark_ndA()) break block43;
                                                                        this.cursor = this.limit - v_7;
                                                                        if (!this.r_mark_nA()) break block44;
                                                                    }
                                                                    v_8 = this.limit - this.cursor;
                                                                    if (!this.r_mark_lArI()) break block45;
                                                                    this.bra = this.cursor;
                                                                    this.slice_del();
                                                                    break block40;
                                                                }
                                                                this.cursor = this.limit - v_8;
                                                                if (!this.r_mark_sU()) break block46;
                                                                this.bra = this.cursor;
                                                                this.slice_del();
                                                                v_9 = this.limit - this.cursor;
                                                                this.ket = this.cursor;
                                                                if (!this.r_mark_lAr()) {
                                                                    this.cursor = this.limit - v_9;
                                                                } else {
                                                                    this.bra = this.cursor;
                                                                    this.slice_del();
                                                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                                                        this.cursor = this.limit - v_9;
                                                                    }
                                                                }
                                                                break block40;
                                                            }
                                                            this.cursor = this.limit - v_8;
                                                            if (this.r_stem_suffix_chain_before_ki()) break block40;
                                                        }
                                                        this.ket = this.cursor = this.limit - v_1;
                                                        v_10 = this.limit - this.cursor;
                                                        if (this.r_mark_ndAn()) break block47;
                                                        this.cursor = this.limit - v_10;
                                                        if (!this.r_mark_nU()) break block48;
                                                    }
                                                    v_11 = this.limit - this.cursor;
                                                    if (!this.r_mark_sU()) break block49;
                                                    this.bra = this.cursor;
                                                    this.slice_del();
                                                    v_12 = this.limit - this.cursor;
                                                    this.ket = this.cursor;
                                                    if (!this.r_mark_lAr()) {
                                                        this.cursor = this.limit - v_12;
                                                    } else {
                                                        this.bra = this.cursor;
                                                        this.slice_del();
                                                        if (!this.r_stem_suffix_chain_before_ki()) {
                                                            this.cursor = this.limit - v_12;
                                                        }
                                                    }
                                                    break block40;
                                                }
                                                this.cursor = this.limit - v_11;
                                                if (this.r_mark_lArI()) break block40;
                                            }
                                            this.ket = this.cursor = this.limit - v_1;
                                            if (!this.r_mark_DAn()) break block50;
                                            this.bra = this.cursor;
                                            this.slice_del();
                                            v_13 = this.limit - this.cursor;
                                            this.ket = this.cursor;
                                            v_14 = this.limit - this.cursor;
                                            if (this.r_mark_possessives()) {
                                                this.bra = this.cursor;
                                                this.slice_del();
                                                v_15 = this.limit - this.cursor;
                                                this.ket = this.cursor;
                                                if (!this.r_mark_lAr()) {
                                                    this.cursor = this.limit - v_15;
                                                } else {
                                                    this.bra = this.cursor;
                                                    this.slice_del();
                                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                                        this.cursor = this.limit - v_15;
                                                    }
                                                }
                                            } else {
                                                this.cursor = this.limit - v_14;
                                                if (this.r_mark_lAr()) {
                                                    this.bra = this.cursor;
                                                    this.slice_del();
                                                    v_16 = this.limit - this.cursor;
                                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                                        this.cursor = this.limit - v_16;
                                                    }
                                                } else {
                                                    this.cursor = this.limit - v_14;
                                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                                        this.cursor = this.limit - v_13;
                                                    }
                                                }
                                            }
                                            break block40;
                                        }
                                        this.ket = this.cursor = this.limit - v_1;
                                        v_17 = this.limit - this.cursor;
                                        if (this.r_mark_nUn()) break block51;
                                        this.cursor = this.limit - v_17;
                                        if (!this.r_mark_ylA()) break block52;
                                    }
                                    this.bra = this.cursor;
                                    this.slice_del();
                                    v_18 = this.limit - this.cursor;
                                    v_19 = this.limit - this.cursor;
                                    this.ket = this.cursor;
                                    if (!this.r_mark_lAr()) break block53;
                                    this.bra = this.cursor;
                                    this.slice_del();
                                    if (this.r_stem_suffix_chain_before_ki()) break block40;
                                }
                                this.ket = this.cursor = this.limit - v_19;
                                v_20 = this.limit - this.cursor;
                                if (this.r_mark_possessives()) ** GOTO lbl-1000
                                this.cursor = this.limit - v_20;
                                if (this.r_mark_sU()) lbl-1000:
                                // 2 sources

                                {
                                    this.bra = this.cursor;
                                    this.slice_del();
                                    v_21 = this.limit - this.cursor;
                                    this.ket = this.cursor;
                                    if (!this.r_mark_lAr()) {
                                        this.cursor = this.limit - v_21;
                                    } else {
                                        this.bra = this.cursor;
                                        this.slice_del();
                                        if (!this.r_stem_suffix_chain_before_ki()) {
                                            this.cursor = this.limit - v_21;
                                        }
                                    }
                                } else {
                                    this.cursor = this.limit - v_19;
                                    if (!this.r_stem_suffix_chain_before_ki()) {
                                        this.cursor = this.limit - v_18;
                                    }
                                }
                                break block40;
                            }
                            this.ket = this.cursor = this.limit - v_1;
                            if (!this.r_mark_lArI()) break block54;
                            this.bra = this.cursor;
                            this.slice_del();
                            break block40;
                        }
                        this.cursor = this.limit - v_1;
                        if (this.r_stem_suffix_chain_before_ki()) break block40;
                        this.ket = this.cursor = this.limit - v_1;
                        v_22 = this.limit - this.cursor;
                        if (this.r_mark_DA()) break block55;
                        this.cursor = this.limit - v_22;
                        if (this.r_mark_yU()) break block55;
                        this.cursor = this.limit - v_22;
                        if (!this.r_mark_yA()) break block56;
                    }
                    this.bra = this.cursor;
                    this.slice_del();
                    v_23 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    v_24 = this.limit - this.cursor;
                    if (!this.r_mark_possessives()) break block57;
                    this.bra = this.cursor;
                    this.slice_del();
                    v_25 = this.limit - this.cursor;
                    this.ket = this.cursor;
                    if (!this.r_mark_lAr()) {
                        this.cursor = this.limit - v_25;
                    }
                    ** GOTO lbl-1000
                }
                this.cursor = this.limit - v_24;
                if (!this.r_mark_lAr()) {
                    this.cursor = this.limit - v_23;
                } else lbl-1000:
                // 2 sources

                {
                    this.bra = this.cursor;
                    this.slice_del();
                    this.ket = this.cursor;
                    if (!this.r_stem_suffix_chain_before_ki()) {
                        this.cursor = this.limit - v_23;
                    }
                }
                break block40;
            }
            this.ket = this.cursor = this.limit - v_1;
            v_26 = this.limit - this.cursor;
            if (!this.r_mark_possessives()) {
                this.cursor = this.limit - v_26;
                if (!this.r_mark_sU()) {
                    return false;
                }
            }
            this.bra = this.cursor;
            this.slice_del();
            v_27 = this.limit - this.cursor;
            this.ket = this.cursor;
            if (!this.r_mark_lAr()) {
                this.cursor = this.limit - v_27;
            } else {
                this.bra = this.cursor;
                this.slice_del();
                if (!this.r_stem_suffix_chain_before_ki()) {
                    this.cursor = this.limit - v_27;
                }
            }
        }
        return true;
    }

    private boolean r_post_process_last_consonants() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_23, 4);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("p");
                break;
            }
            case 2: {
                this.slice_from("\u00e7");
                break;
            }
            case 3: {
                this.slice_from("t");
                break;
            }
            case 4: {
                this.slice_from("k");
            }
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_append_U_to_stems_ending_with_d_or_g() {
        int v_13;
        block47: {
            int v_15;
            int v_3;
            block45: {
                int v_10;
                block46: {
                    int v_12;
                    block43: {
                        int v_7;
                        block44: {
                            int v_9;
                            block41: {
                                int v_4;
                                block42: {
                                    int v_6;
                                    int v_1 = this.limit - this.cursor;
                                    int v_2 = this.limit - this.cursor;
                                    if (!this.eq_s_b(1, "d")) {
                                        this.cursor = this.limit - v_2;
                                        if (!this.eq_s_b(1, "g")) {
                                            return false;
                                        }
                                    }
                                    this.cursor = this.limit - v_1;
                                    v_3 = this.limit - this.cursor;
                                    v_4 = this.limit - this.cursor;
                                    while (true) {
                                        int v_5 = this.limit - this.cursor;
                                        if (!this.in_grouping_b(g_vowel, 97, 305)) {
                                            this.cursor = this.limit - v_5;
                                            if (this.cursor <= this.limit_backward) {
                                                break block41;
                                            }
                                        } else {
                                            this.cursor = this.limit - v_5;
                                            v_6 = this.limit - this.cursor;
                                            if (!this.eq_s_b(1, "a")) {
                                                break;
                                            }
                                            break block42;
                                        }
                                        --this.cursor;
                                    }
                                    this.cursor = this.limit - v_6;
                                    if (!this.eq_s_b(1, "\u0131")) break block41;
                                }
                                int c = this.cursor = this.limit - v_4;
                                this.insert(this.cursor, this.cursor, "\u0131");
                                this.cursor = c;
                                return true;
                            }
                            this.cursor = this.limit - v_3;
                            v_7 = this.limit - this.cursor;
                            while (true) {
                                int v_8 = this.limit - this.cursor;
                                if (!this.in_grouping_b(g_vowel, 97, 305)) {
                                    this.cursor = this.limit - v_8;
                                    if (this.cursor <= this.limit_backward) {
                                        break block43;
                                    }
                                } else {
                                    this.cursor = this.limit - v_8;
                                    v_9 = this.limit - this.cursor;
                                    if (!this.eq_s_b(1, "e")) {
                                        break;
                                    }
                                    break block44;
                                }
                                --this.cursor;
                            }
                            this.cursor = this.limit - v_9;
                            if (!this.eq_s_b(1, "i")) break block43;
                        }
                        int c = this.cursor = this.limit - v_7;
                        this.insert(this.cursor, this.cursor, "i");
                        this.cursor = c;
                        return true;
                    }
                    this.cursor = this.limit - v_3;
                    v_10 = this.limit - this.cursor;
                    while (true) {
                        int v_11 = this.limit - this.cursor;
                        if (!this.in_grouping_b(g_vowel, 97, 305)) {
                            this.cursor = this.limit - v_11;
                            if (this.cursor <= this.limit_backward) {
                                break block45;
                            }
                        } else {
                            this.cursor = this.limit - v_11;
                            v_12 = this.limit - this.cursor;
                            if (!this.eq_s_b(1, "o")) {
                                break;
                            }
                            break block46;
                        }
                        --this.cursor;
                    }
                    this.cursor = this.limit - v_12;
                    if (!this.eq_s_b(1, "u")) break block45;
                }
                int c = this.cursor = this.limit - v_10;
                this.insert(this.cursor, this.cursor, "u");
                this.cursor = c;
                return true;
            }
            this.cursor = this.limit - v_3;
            v_13 = this.limit - this.cursor;
            while (true) {
                int v_14 = this.limit - this.cursor;
                if (!this.in_grouping_b(g_vowel, 97, 305)) {
                    this.cursor = this.limit - v_14;
                    if (this.cursor <= this.limit_backward) {
                        return false;
                    }
                } else {
                    this.cursor = this.limit - v_14;
                    v_15 = this.limit - this.cursor;
                    if (!this.eq_s_b(1, "\u00f6")) {
                        break;
                    }
                    break block47;
                }
                --this.cursor;
            }
            this.cursor = this.limit - v_15;
            if (!this.eq_s_b(1, "\u00fc")) {
                return false;
            }
        }
        int c = this.cursor = this.limit - v_13;
        this.insert(this.cursor, this.cursor, "\u00fc");
        this.cursor = c;
        return true;
    }

    private boolean r_more_than_one_syllable_word() {
        int v_3;
        int v_1 = this.cursor;
        int v_2 = 2;
        block0: while (true) {
            v_3 = this.cursor;
            while (!this.in_grouping(g_vowel, 97, 305)) {
                if (this.cursor >= this.limit) break block0;
                ++this.cursor;
            }
            --v_2;
        }
        this.cursor = v_3;
        if (v_2 > 0) {
            return false;
        }
        this.cursor = v_1;
        return true;
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_is_reserved_word() {
        int v_1;
        block6: {
            v_1 = this.cursor;
            int v_2 = this.cursor;
            while (!this.eq_s(2, "ad")) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block6;
            }
            this.I_strlen = 2;
            if (this.I_strlen == this.limit) {
                this.cursor = v_2;
                return true;
            }
        }
        int v_4 = this.cursor = v_1;
        while (!this.eq_s(5, "soyad")) {
            if (this.cursor >= this.limit) {
                return false;
            }
            ++this.cursor;
        }
        this.I_strlen = 5;
        if (this.I_strlen != this.limit) {
            return false;
        }
        this.cursor = v_4;
        return true;
    }

    private boolean r_postlude() {
        int v_1 = this.cursor;
        if (this.r_is_reserved_word()) {
            return false;
        }
        this.limit_backward = this.cursor = v_1;
        this.cursor = this.limit;
        int v_2 = this.limit - this.cursor;
        if (!this.r_append_U_to_stems_ending_with_d_or_g()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (!this.r_post_process_last_consonants()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        this.cursor = this.limit_backward;
        return true;
    }

    @Override
    public boolean stem() {
        if (!this.r_more_than_one_syllable_word()) {
            return false;
        }
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        int v_1 = this.limit - this.cursor;
        if (!this.r_stem_nominal_verb_suffixes()) {
            // empty if block
        }
        this.cursor = this.limit - v_1;
        if (!this.B_continue_stemming_noun_suffixes) {
            return false;
        }
        int v_2 = this.limit - this.cursor;
        if (!this.r_stem_noun_suffixes()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        this.cursor = this.limit_backward;
        return this.r_postlude();
    }

    public boolean equals(Object o) {
        return o instanceof TurkishStemmer;
    }

    public int hashCode() {
        return TurkishStemmer.class.getName().hashCode();
    }
}

