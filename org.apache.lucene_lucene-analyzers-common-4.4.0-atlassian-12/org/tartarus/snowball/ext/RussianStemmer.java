/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class RussianStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final RussianStemmer methodObject = new RussianStemmer();
    private static final Among[] a_0 = new Among[]{new Among("\u0432", -1, 1, "", methodObject), new Among("\u0438\u0432", 0, 2, "", methodObject), new Among("\u044b\u0432", 0, 2, "", methodObject), new Among("\u0432\u0448\u0438", -1, 1, "", methodObject), new Among("\u0438\u0432\u0448\u0438", 3, 2, "", methodObject), new Among("\u044b\u0432\u0448\u0438", 3, 2, "", methodObject), new Among("\u0432\u0448\u0438\u0441\u044c", -1, 1, "", methodObject), new Among("\u0438\u0432\u0448\u0438\u0441\u044c", 6, 2, "", methodObject), new Among("\u044b\u0432\u0448\u0438\u0441\u044c", 6, 2, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("\u0435\u0435", -1, 1, "", methodObject), new Among("\u0438\u0435", -1, 1, "", methodObject), new Among("\u043e\u0435", -1, 1, "", methodObject), new Among("\u044b\u0435", -1, 1, "", methodObject), new Among("\u0438\u043c\u0438", -1, 1, "", methodObject), new Among("\u044b\u043c\u0438", -1, 1, "", methodObject), new Among("\u0435\u0439", -1, 1, "", methodObject), new Among("\u0438\u0439", -1, 1, "", methodObject), new Among("\u043e\u0439", -1, 1, "", methodObject), new Among("\u044b\u0439", -1, 1, "", methodObject), new Among("\u0435\u043c", -1, 1, "", methodObject), new Among("\u0438\u043c", -1, 1, "", methodObject), new Among("\u043e\u043c", -1, 1, "", methodObject), new Among("\u044b\u043c", -1, 1, "", methodObject), new Among("\u0435\u0433\u043e", -1, 1, "", methodObject), new Among("\u043e\u0433\u043e", -1, 1, "", methodObject), new Among("\u0435\u043c\u0443", -1, 1, "", methodObject), new Among("\u043e\u043c\u0443", -1, 1, "", methodObject), new Among("\u0438\u0445", -1, 1, "", methodObject), new Among("\u044b\u0445", -1, 1, "", methodObject), new Among("\u0435\u044e", -1, 1, "", methodObject), new Among("\u043e\u044e", -1, 1, "", methodObject), new Among("\u0443\u044e", -1, 1, "", methodObject), new Among("\u044e\u044e", -1, 1, "", methodObject), new Among("\u0430\u044f", -1, 1, "", methodObject), new Among("\u044f\u044f", -1, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("\u0435\u043c", -1, 1, "", methodObject), new Among("\u043d\u043d", -1, 1, "", methodObject), new Among("\u0432\u0448", -1, 1, "", methodObject), new Among("\u0438\u0432\u0448", 2, 2, "", methodObject), new Among("\u044b\u0432\u0448", 2, 2, "", methodObject), new Among("\u0449", -1, 1, "", methodObject), new Among("\u044e\u0449", 5, 1, "", methodObject), new Among("\u0443\u044e\u0449", 6, 2, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("\u0441\u044c", -1, 1, "", methodObject), new Among("\u0441\u044f", -1, 1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("\u043b\u0430", -1, 1, "", methodObject), new Among("\u0438\u043b\u0430", 0, 2, "", methodObject), new Among("\u044b\u043b\u0430", 0, 2, "", methodObject), new Among("\u043d\u0430", -1, 1, "", methodObject), new Among("\u0435\u043d\u0430", 3, 2, "", methodObject), new Among("\u0435\u0442\u0435", -1, 1, "", methodObject), new Among("\u0438\u0442\u0435", -1, 2, "", methodObject), new Among("\u0439\u0442\u0435", -1, 1, "", methodObject), new Among("\u0435\u0439\u0442\u0435", 7, 2, "", methodObject), new Among("\u0443\u0439\u0442\u0435", 7, 2, "", methodObject), new Among("\u043b\u0438", -1, 1, "", methodObject), new Among("\u0438\u043b\u0438", 10, 2, "", methodObject), new Among("\u044b\u043b\u0438", 10, 2, "", methodObject), new Among("\u0439", -1, 1, "", methodObject), new Among("\u0435\u0439", 13, 2, "", methodObject), new Among("\u0443\u0439", 13, 2, "", methodObject), new Among("\u043b", -1, 1, "", methodObject), new Among("\u0438\u043b", 16, 2, "", methodObject), new Among("\u044b\u043b", 16, 2, "", methodObject), new Among("\u0435\u043c", -1, 1, "", methodObject), new Among("\u0438\u043c", -1, 2, "", methodObject), new Among("\u044b\u043c", -1, 2, "", methodObject), new Among("\u043d", -1, 1, "", methodObject), new Among("\u0435\u043d", 22, 2, "", methodObject), new Among("\u043b\u043e", -1, 1, "", methodObject), new Among("\u0438\u043b\u043e", 24, 2, "", methodObject), new Among("\u044b\u043b\u043e", 24, 2, "", methodObject), new Among("\u043d\u043e", -1, 1, "", methodObject), new Among("\u0435\u043d\u043e", 27, 2, "", methodObject), new Among("\u043d\u043d\u043e", 27, 1, "", methodObject), new Among("\u0435\u0442", -1, 1, "", methodObject), new Among("\u0443\u0435\u0442", 30, 2, "", methodObject), new Among("\u0438\u0442", -1, 2, "", methodObject), new Among("\u044b\u0442", -1, 2, "", methodObject), new Among("\u044e\u0442", -1, 1, "", methodObject), new Among("\u0443\u044e\u0442", 34, 2, "", methodObject), new Among("\u044f\u0442", -1, 2, "", methodObject), new Among("\u043d\u044b", -1, 1, "", methodObject), new Among("\u0435\u043d\u044b", 37, 2, "", methodObject), new Among("\u0442\u044c", -1, 1, "", methodObject), new Among("\u0438\u0442\u044c", 39, 2, "", methodObject), new Among("\u044b\u0442\u044c", 39, 2, "", methodObject), new Among("\u0435\u0448\u044c", -1, 1, "", methodObject), new Among("\u0438\u0448\u044c", -1, 2, "", methodObject), new Among("\u044e", -1, 2, "", methodObject), new Among("\u0443\u044e", 44, 2, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("\u0430", -1, 1, "", methodObject), new Among("\u0435\u0432", -1, 1, "", methodObject), new Among("\u043e\u0432", -1, 1, "", methodObject), new Among("\u0435", -1, 1, "", methodObject), new Among("\u0438\u0435", 3, 1, "", methodObject), new Among("\u044c\u0435", 3, 1, "", methodObject), new Among("\u0438", -1, 1, "", methodObject), new Among("\u0435\u0438", 6, 1, "", methodObject), new Among("\u0438\u0438", 6, 1, "", methodObject), new Among("\u0430\u043c\u0438", 6, 1, "", methodObject), new Among("\u044f\u043c\u0438", 6, 1, "", methodObject), new Among("\u0438\u044f\u043c\u0438", 10, 1, "", methodObject), new Among("\u0439", -1, 1, "", methodObject), new Among("\u0435\u0439", 12, 1, "", methodObject), new Among("\u0438\u0435\u0439", 13, 1, "", methodObject), new Among("\u0438\u0439", 12, 1, "", methodObject), new Among("\u043e\u0439", 12, 1, "", methodObject), new Among("\u0430\u043c", -1, 1, "", methodObject), new Among("\u0435\u043c", -1, 1, "", methodObject), new Among("\u0438\u0435\u043c", 18, 1, "", methodObject), new Among("\u043e\u043c", -1, 1, "", methodObject), new Among("\u044f\u043c", -1, 1, "", methodObject), new Among("\u0438\u044f\u043c", 21, 1, "", methodObject), new Among("\u043e", -1, 1, "", methodObject), new Among("\u0443", -1, 1, "", methodObject), new Among("\u0430\u0445", -1, 1, "", methodObject), new Among("\u044f\u0445", -1, 1, "", methodObject), new Among("\u0438\u044f\u0445", 26, 1, "", methodObject), new Among("\u044b", -1, 1, "", methodObject), new Among("\u044c", -1, 1, "", methodObject), new Among("\u044e", -1, 1, "", methodObject), new Among("\u0438\u044e", 30, 1, "", methodObject), new Among("\u044c\u044e", 30, 1, "", methodObject), new Among("\u044f", -1, 1, "", methodObject), new Among("\u0438\u044f", 33, 1, "", methodObject), new Among("\u044c\u044f", 33, 1, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("\u043e\u0441\u0442", -1, 1, "", methodObject), new Among("\u043e\u0441\u0442\u044c", -1, 1, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("\u0435\u0439\u0448\u0435", -1, 1, "", methodObject), new Among("\u043d", -1, 2, "", methodObject), new Among("\u0435\u0439\u0448", -1, 1, "", methodObject), new Among("\u044c", -1, 3, "", methodObject)};
    private static final char[] g_v = new char[]{'!', 'A', '\b', '\u00e8'};
    private int I_p2;
    private int I_pV;

    private void copy_from(RussianStemmer other) {
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
            while (!this.in_grouping(g_v, 1072, 1103)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            this.I_pV = this.cursor;
            while (!this.out_grouping(g_v, 1072, 1103)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            while (!this.in_grouping(g_v, 1072, 1103)) {
                if (this.cursor < this.limit) {
                    ++this.cursor;
                    continue;
                }
                break block8;
            }
            while (!this.out_grouping(g_v, 1072, 1103)) {
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

    private boolean r_perfective_gerund() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_0, 9);
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
                if (!this.eq_s_b(1, "\u0430")) {
                    this.cursor = this.limit - v_1;
                    if (!this.eq_s_b(1, "\u044f")) {
                        return false;
                    }
                }
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_adjective() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 26);
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

    private boolean r_adjectival() {
        if (!this.r_adjective()) {
            return false;
        }
        int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 8);
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
                    int v_2 = this.limit - this.cursor;
                    if (!this.eq_s_b(1, "\u0430")) {
                        this.cursor = this.limit - v_2;
                        if (!this.eq_s_b(1, "\u044f")) {
                            this.cursor = this.limit - v_1;
                            break;
                        }
                    }
                    this.slice_del();
                    break;
                }
                case 2: {
                    this.slice_del();
                }
            }
        }
        return true;
    }

    private boolean r_reflexive() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 2);
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
        int among_var = this.find_among_b(a_4, 46);
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
                if (!this.eq_s_b(1, "\u0430")) {
                    this.cursor = this.limit - v_1;
                    if (!this.eq_s_b(1, "\u044f")) {
                        return false;
                    }
                }
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_noun() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_5, 36);
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

    private boolean r_derivational() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_6, 2);
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

    private boolean r_tidy_up() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_7, 4);
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
                this.ket = this.cursor;
                if (!this.eq_s_b(1, "\u043d")) {
                    return false;
                }
                this.bra = this.cursor;
                if (!this.eq_s_b(1, "\u043d")) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                if (!this.eq_s_b(1, "\u043d")) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
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
        int v_5 = this.limit - this.cursor;
        if (!this.r_perfective_gerund()) {
            this.cursor = this.limit - v_5;
            int v_6 = this.limit - this.cursor;
            if (!this.r_reflexive()) {
                this.cursor = this.limit - v_6;
            }
            int v_7 = this.limit - this.cursor;
            if (!this.r_adjectival()) {
                this.cursor = this.limit - v_7;
                if (!this.r_verb()) {
                    this.cursor = this.limit - v_7;
                    if (!this.r_noun()) {
                        // empty if block
                    }
                }
            }
        }
        this.cursor = this.limit - v_4;
        int v_8 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "\u0438")) {
            this.cursor = this.limit - v_8;
        } else {
            this.bra = this.cursor;
            this.slice_del();
        }
        int v_9 = this.limit - this.cursor;
        if (!this.r_derivational()) {
            // empty if block
        }
        this.cursor = this.limit - v_9;
        int v_10 = this.limit - this.cursor;
        if (!this.r_tidy_up()) {
            // empty if block
        }
        this.cursor = this.limit - v_10;
        this.cursor = this.limit_backward = v_3;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof RussianStemmer;
    }

    public int hashCode() {
        return RussianStemmer.class.getName().hashCode();
    }
}

