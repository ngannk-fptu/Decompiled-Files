/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class LovinsStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final LovinsStemmer methodObject = new LovinsStemmer();
    private static final Among[] a_0 = new Among[]{new Among("d", -1, -1, "", methodObject), new Among("f", -1, -1, "", methodObject), new Among("ph", -1, -1, "", methodObject), new Among("th", -1, -1, "", methodObject), new Among("l", -1, -1, "", methodObject), new Among("er", -1, -1, "", methodObject), new Among("or", -1, -1, "", methodObject), new Among("es", -1, -1, "", methodObject), new Among("t", -1, -1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("s'", -1, 1, "r_A", methodObject), new Among("a", -1, 1, "r_A", methodObject), new Among("ia", 1, 1, "r_A", methodObject), new Among("ata", 1, 1, "r_A", methodObject), new Among("ic", -1, 1, "r_A", methodObject), new Among("aic", 4, 1, "r_A", methodObject), new Among("allic", 4, 1, "r_BB", methodObject), new Among("aric", 4, 1, "r_A", methodObject), new Among("atic", 4, 1, "r_B", methodObject), new Among("itic", 4, 1, "r_H", methodObject), new Among("antic", 4, 1, "r_C", methodObject), new Among("istic", 4, 1, "r_A", methodObject), new Among("alistic", 11, 1, "r_B", methodObject), new Among("aristic", 11, 1, "r_A", methodObject), new Among("ivistic", 11, 1, "r_A", methodObject), new Among("ed", -1, 1, "r_E", methodObject), new Among("anced", 15, 1, "r_B", methodObject), new Among("enced", 15, 1, "r_A", methodObject), new Among("ished", 15, 1, "r_A", methodObject), new Among("ied", 15, 1, "r_A", methodObject), new Among("ened", 15, 1, "r_E", methodObject), new Among("ioned", 15, 1, "r_A", methodObject), new Among("ated", 15, 1, "r_I", methodObject), new Among("ented", 15, 1, "r_C", methodObject), new Among("ized", 15, 1, "r_F", methodObject), new Among("arized", 24, 1, "r_A", methodObject), new Among("oid", -1, 1, "r_A", methodObject), new Among("aroid", 26, 1, "r_A", methodObject), new Among("hood", -1, 1, "r_A", methodObject), new Among("ehood", 28, 1, "r_A", methodObject), new Among("ihood", 28, 1, "r_A", methodObject), new Among("elihood", 30, 1, "r_E", methodObject), new Among("ward", -1, 1, "r_A", methodObject), new Among("e", -1, 1, "r_A", methodObject), new Among("ae", 33, 1, "r_A", methodObject), new Among("ance", 33, 1, "r_B", methodObject), new Among("icance", 35, 1, "r_A", methodObject), new Among("ence", 33, 1, "r_A", methodObject), new Among("ide", 33, 1, "r_L", methodObject), new Among("icide", 38, 1, "r_A", methodObject), new Among("otide", 38, 1, "r_A", methodObject), new Among("age", 33, 1, "r_B", methodObject), new Among("able", 33, 1, "r_A", methodObject), new Among("atable", 42, 1, "r_A", methodObject), new Among("izable", 42, 1, "r_E", methodObject), new Among("arizable", 44, 1, "r_A", methodObject), new Among("ible", 33, 1, "r_A", methodObject), new Among("encible", 46, 1, "r_A", methodObject), new Among("ene", 33, 1, "r_E", methodObject), new Among("ine", 33, 1, "r_M", methodObject), new Among("idine", 49, 1, "r_I", methodObject), new Among("one", 33, 1, "r_R", methodObject), new Among("ature", 33, 1, "r_E", methodObject), new Among("eature", 52, 1, "r_Z", methodObject), new Among("ese", 33, 1, "r_A", methodObject), new Among("wise", 33, 1, "r_A", methodObject), new Among("ate", 33, 1, "r_A", methodObject), new Among("entiate", 56, 1, "r_A", methodObject), new Among("inate", 56, 1, "r_A", methodObject), new Among("ionate", 56, 1, "r_D", methodObject), new Among("ite", 33, 1, "r_AA", methodObject), new Among("ive", 33, 1, "r_A", methodObject), new Among("ative", 61, 1, "r_A", methodObject), new Among("ize", 33, 1, "r_F", methodObject), new Among("alize", 63, 1, "r_A", methodObject), new Among("icalize", 64, 1, "r_A", methodObject), new Among("ialize", 64, 1, "r_A", methodObject), new Among("entialize", 66, 1, "r_A", methodObject), new Among("ionalize", 64, 1, "r_A", methodObject), new Among("arize", 63, 1, "r_A", methodObject), new Among("ing", -1, 1, "r_N", methodObject), new Among("ancing", 70, 1, "r_B", methodObject), new Among("encing", 70, 1, "r_A", methodObject), new Among("aging", 70, 1, "r_B", methodObject), new Among("ening", 70, 1, "r_E", methodObject), new Among("ioning", 70, 1, "r_A", methodObject), new Among("ating", 70, 1, "r_I", methodObject), new Among("enting", 70, 1, "r_C", methodObject), new Among("ying", 70, 1, "r_B", methodObject), new Among("izing", 70, 1, "r_F", methodObject), new Among("arizing", 79, 1, "r_A", methodObject), new Among("ish", -1, 1, "r_C", methodObject), new Among("yish", 81, 1, "r_A", methodObject), new Among("i", -1, 1, "r_A", methodObject), new Among("al", -1, 1, "r_BB", methodObject), new Among("ical", 84, 1, "r_A", methodObject), new Among("aical", 85, 1, "r_A", methodObject), new Among("istical", 85, 1, "r_A", methodObject), new Among("oidal", 84, 1, "r_A", methodObject), new Among("eal", 84, 1, "r_Y", methodObject), new Among("ial", 84, 1, "r_A", methodObject), new Among("ancial", 90, 1, "r_A", methodObject), new Among("arial", 90, 1, "r_A", methodObject), new Among("ential", 90, 1, "r_A", methodObject), new Among("ional", 84, 1, "r_A", methodObject), new Among("ational", 94, 1, "r_B", methodObject), new Among("izational", 95, 1, "r_A", methodObject), new Among("ental", 84, 1, "r_A", methodObject), new Among("ful", -1, 1, "r_A", methodObject), new Among("eful", 98, 1, "r_A", methodObject), new Among("iful", 98, 1, "r_A", methodObject), new Among("yl", -1, 1, "r_R", methodObject), new Among("ism", -1, 1, "r_B", methodObject), new Among("icism", 102, 1, "r_A", methodObject), new Among("oidism", 102, 1, "r_A", methodObject), new Among("alism", 102, 1, "r_B", methodObject), new Among("icalism", 105, 1, "r_A", methodObject), new Among("ionalism", 105, 1, "r_A", methodObject), new Among("inism", 102, 1, "r_J", methodObject), new Among("ativism", 102, 1, "r_A", methodObject), new Among("um", -1, 1, "r_U", methodObject), new Among("ium", 110, 1, "r_A", methodObject), new Among("ian", -1, 1, "r_A", methodObject), new Among("ician", 112, 1, "r_A", methodObject), new Among("en", -1, 1, "r_F", methodObject), new Among("ogen", 114, 1, "r_A", methodObject), new Among("on", -1, 1, "r_S", methodObject), new Among("ion", 116, 1, "r_Q", methodObject), new Among("ation", 117, 1, "r_B", methodObject), new Among("ication", 118, 1, "r_G", methodObject), new Among("entiation", 118, 1, "r_A", methodObject), new Among("ination", 118, 1, "r_A", methodObject), new Among("isation", 118, 1, "r_A", methodObject), new Among("arisation", 122, 1, "r_A", methodObject), new Among("entation", 118, 1, "r_A", methodObject), new Among("ization", 118, 1, "r_F", methodObject), new Among("arization", 125, 1, "r_A", methodObject), new Among("action", 117, 1, "r_G", methodObject), new Among("o", -1, 1, "r_A", methodObject), new Among("ar", -1, 1, "r_X", methodObject), new Among("ear", 129, 1, "r_Y", methodObject), new Among("ier", -1, 1, "r_A", methodObject), new Among("ariser", -1, 1, "r_A", methodObject), new Among("izer", -1, 1, "r_F", methodObject), new Among("arizer", 133, 1, "r_A", methodObject), new Among("or", -1, 1, "r_T", methodObject), new Among("ator", 135, 1, "r_A", methodObject), new Among("s", -1, 1, "r_W", methodObject), new Among("'s", 137, 1, "r_A", methodObject), new Among("as", 137, 1, "r_B", methodObject), new Among("ics", 137, 1, "r_A", methodObject), new Among("istics", 140, 1, "r_A", methodObject), new Among("es", 137, 1, "r_E", methodObject), new Among("ances", 142, 1, "r_B", methodObject), new Among("ences", 142, 1, "r_A", methodObject), new Among("ides", 142, 1, "r_L", methodObject), new Among("oides", 145, 1, "r_A", methodObject), new Among("ages", 142, 1, "r_B", methodObject), new Among("ies", 142, 1, "r_P", methodObject), new Among("acies", 148, 1, "r_A", methodObject), new Among("ancies", 148, 1, "r_A", methodObject), new Among("encies", 148, 1, "r_A", methodObject), new Among("aries", 148, 1, "r_A", methodObject), new Among("ities", 148, 1, "r_A", methodObject), new Among("alities", 153, 1, "r_A", methodObject), new Among("ivities", 153, 1, "r_A", methodObject), new Among("ines", 142, 1, "r_M", methodObject), new Among("nesses", 142, 1, "r_A", methodObject), new Among("ates", 142, 1, "r_A", methodObject), new Among("atives", 142, 1, "r_A", methodObject), new Among("ings", 137, 1, "r_N", methodObject), new Among("is", 137, 1, "r_A", methodObject), new Among("als", 137, 1, "r_BB", methodObject), new Among("ials", 162, 1, "r_A", methodObject), new Among("entials", 163, 1, "r_A", methodObject), new Among("ionals", 162, 1, "r_A", methodObject), new Among("isms", 137, 1, "r_B", methodObject), new Among("ians", 137, 1, "r_A", methodObject), new Among("icians", 167, 1, "r_A", methodObject), new Among("ions", 137, 1, "r_B", methodObject), new Among("ations", 169, 1, "r_B", methodObject), new Among("arisations", 170, 1, "r_A", methodObject), new Among("entations", 170, 1, "r_A", methodObject), new Among("izations", 170, 1, "r_A", methodObject), new Among("arizations", 173, 1, "r_A", methodObject), new Among("ars", 137, 1, "r_O", methodObject), new Among("iers", 137, 1, "r_A", methodObject), new Among("izers", 137, 1, "r_F", methodObject), new Among("ators", 137, 1, "r_A", methodObject), new Among("less", 137, 1, "r_A", methodObject), new Among("eless", 179, 1, "r_A", methodObject), new Among("ness", 137, 1, "r_A", methodObject), new Among("eness", 181, 1, "r_E", methodObject), new Among("ableness", 182, 1, "r_A", methodObject), new Among("eableness", 183, 1, "r_E", methodObject), new Among("ibleness", 182, 1, "r_A", methodObject), new Among("ateness", 182, 1, "r_A", methodObject), new Among("iteness", 182, 1, "r_A", methodObject), new Among("iveness", 182, 1, "r_A", methodObject), new Among("ativeness", 188, 1, "r_A", methodObject), new Among("ingness", 181, 1, "r_A", methodObject), new Among("ishness", 181, 1, "r_A", methodObject), new Among("iness", 181, 1, "r_A", methodObject), new Among("ariness", 192, 1, "r_E", methodObject), new Among("alness", 181, 1, "r_A", methodObject), new Among("icalness", 194, 1, "r_A", methodObject), new Among("antialness", 194, 1, "r_A", methodObject), new Among("entialness", 194, 1, "r_A", methodObject), new Among("ionalness", 194, 1, "r_A", methodObject), new Among("fulness", 181, 1, "r_A", methodObject), new Among("lessness", 181, 1, "r_A", methodObject), new Among("ousness", 181, 1, "r_A", methodObject), new Among("eousness", 201, 1, "r_A", methodObject), new Among("iousness", 201, 1, "r_A", methodObject), new Among("itousness", 201, 1, "r_A", methodObject), new Among("entness", 181, 1, "r_A", methodObject), new Among("ants", 137, 1, "r_B", methodObject), new Among("ists", 137, 1, "r_A", methodObject), new Among("icists", 207, 1, "r_A", methodObject), new Among("us", 137, 1, "r_V", methodObject), new Among("ous", 209, 1, "r_A", methodObject), new Among("eous", 210, 1, "r_A", methodObject), new Among("aceous", 211, 1, "r_A", methodObject), new Among("antaneous", 211, 1, "r_A", methodObject), new Among("ious", 210, 1, "r_A", methodObject), new Among("acious", 214, 1, "r_B", methodObject), new Among("itous", 210, 1, "r_A", methodObject), new Among("ant", -1, 1, "r_B", methodObject), new Among("icant", 217, 1, "r_A", methodObject), new Among("ent", -1, 1, "r_C", methodObject), new Among("ement", 219, 1, "r_A", methodObject), new Among("izement", 220, 1, "r_A", methodObject), new Among("ist", -1, 1, "r_A", methodObject), new Among("icist", 222, 1, "r_A", methodObject), new Among("alist", 222, 1, "r_A", methodObject), new Among("icalist", 224, 1, "r_A", methodObject), new Among("ialist", 224, 1, "r_A", methodObject), new Among("ionist", 222, 1, "r_A", methodObject), new Among("entist", 222, 1, "r_A", methodObject), new Among("y", -1, 1, "r_B", methodObject), new Among("acy", 229, 1, "r_A", methodObject), new Among("ancy", 229, 1, "r_B", methodObject), new Among("ency", 229, 1, "r_A", methodObject), new Among("ly", 229, 1, "r_B", methodObject), new Among("ealy", 233, 1, "r_Y", methodObject), new Among("ably", 233, 1, "r_A", methodObject), new Among("ibly", 233, 1, "r_A", methodObject), new Among("edly", 233, 1, "r_E", methodObject), new Among("iedly", 237, 1, "r_A", methodObject), new Among("ely", 233, 1, "r_E", methodObject), new Among("ately", 239, 1, "r_A", methodObject), new Among("ively", 239, 1, "r_A", methodObject), new Among("atively", 241, 1, "r_A", methodObject), new Among("ingly", 233, 1, "r_B", methodObject), new Among("atingly", 243, 1, "r_A", methodObject), new Among("ily", 233, 1, "r_A", methodObject), new Among("lily", 245, 1, "r_A", methodObject), new Among("arily", 245, 1, "r_A", methodObject), new Among("ally", 233, 1, "r_B", methodObject), new Among("ically", 248, 1, "r_A", methodObject), new Among("aically", 249, 1, "r_A", methodObject), new Among("allically", 249, 1, "r_C", methodObject), new Among("istically", 249, 1, "r_A", methodObject), new Among("alistically", 252, 1, "r_B", methodObject), new Among("oidally", 248, 1, "r_A", methodObject), new Among("ially", 248, 1, "r_A", methodObject), new Among("entially", 255, 1, "r_A", methodObject), new Among("ionally", 248, 1, "r_A", methodObject), new Among("ationally", 257, 1, "r_B", methodObject), new Among("izationally", 258, 1, "r_B", methodObject), new Among("entally", 248, 1, "r_A", methodObject), new Among("fully", 233, 1, "r_A", methodObject), new Among("efully", 261, 1, "r_A", methodObject), new Among("ifully", 261, 1, "r_A", methodObject), new Among("enly", 233, 1, "r_E", methodObject), new Among("arly", 233, 1, "r_K", methodObject), new Among("early", 265, 1, "r_Y", methodObject), new Among("lessly", 233, 1, "r_A", methodObject), new Among("ously", 233, 1, "r_A", methodObject), new Among("eously", 268, 1, "r_A", methodObject), new Among("iously", 268, 1, "r_A", methodObject), new Among("ently", 233, 1, "r_A", methodObject), new Among("ary", 229, 1, "r_F", methodObject), new Among("ery", 229, 1, "r_E", methodObject), new Among("icianry", 229, 1, "r_A", methodObject), new Among("atory", 229, 1, "r_A", methodObject), new Among("ity", 229, 1, "r_A", methodObject), new Among("acity", 276, 1, "r_A", methodObject), new Among("icity", 276, 1, "r_A", methodObject), new Among("eity", 276, 1, "r_A", methodObject), new Among("ality", 276, 1, "r_A", methodObject), new Among("icality", 280, 1, "r_A", methodObject), new Among("iality", 280, 1, "r_A", methodObject), new Among("antiality", 282, 1, "r_A", methodObject), new Among("entiality", 282, 1, "r_A", methodObject), new Among("ionality", 280, 1, "r_A", methodObject), new Among("elity", 276, 1, "r_A", methodObject), new Among("ability", 276, 1, "r_A", methodObject), new Among("izability", 287, 1, "r_A", methodObject), new Among("arizability", 288, 1, "r_A", methodObject), new Among("ibility", 276, 1, "r_A", methodObject), new Among("inity", 276, 1, "r_CC", methodObject), new Among("arity", 276, 1, "r_B", methodObject), new Among("ivity", 276, 1, "r_A", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("bb", -1, -1, "", methodObject), new Among("dd", -1, -1, "", methodObject), new Among("gg", -1, -1, "", methodObject), new Among("ll", -1, -1, "", methodObject), new Among("mm", -1, -1, "", methodObject), new Among("nn", -1, -1, "", methodObject), new Among("pp", -1, -1, "", methodObject), new Among("rr", -1, -1, "", methodObject), new Among("ss", -1, -1, "", methodObject), new Among("tt", -1, -1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("uad", -1, 18, "", methodObject), new Among("vad", -1, 19, "", methodObject), new Among("cid", -1, 20, "", methodObject), new Among("lid", -1, 21, "", methodObject), new Among("erid", -1, 22, "", methodObject), new Among("pand", -1, 23, "", methodObject), new Among("end", -1, 24, "", methodObject), new Among("ond", -1, 25, "", methodObject), new Among("lud", -1, 26, "", methodObject), new Among("rud", -1, 27, "", methodObject), new Among("ul", -1, 9, "", methodObject), new Among("her", -1, 28, "", methodObject), new Among("metr", -1, 7, "", methodObject), new Among("istr", -1, 6, "", methodObject), new Among("urs", -1, 5, "", methodObject), new Among("uct", -1, 2, "", methodObject), new Among("et", -1, 32, "", methodObject), new Among("mit", -1, 29, "", methodObject), new Among("ent", -1, 30, "", methodObject), new Among("umpt", -1, 3, "", methodObject), new Among("rpt", -1, 4, "", methodObject), new Among("ert", -1, 31, "", methodObject), new Among("yt", -1, 33, "", methodObject), new Among("iev", -1, 1, "", methodObject), new Among("olv", -1, 8, "", methodObject), new Among("ax", -1, 14, "", methodObject), new Among("ex", -1, 15, "", methodObject), new Among("bex", 26, 10, "", methodObject), new Among("dex", 26, 11, "", methodObject), new Among("pex", 26, 12, "", methodObject), new Among("tex", 26, 13, "", methodObject), new Among("ix", -1, 16, "", methodObject), new Among("lux", -1, 17, "", methodObject), new Among("yz", -1, 34, "", methodObject)};

    private void copy_from(LovinsStemmer other) {
        super.copy_from(other);
    }

    private boolean r_A() {
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }

    private boolean r_B() {
        int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }

    private boolean r_C() {
        int c = this.cursor - 4;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }

    private boolean r_D() {
        int c = this.cursor - 5;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        return true;
    }

    private boolean r_E() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "e")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        return true;
    }

    private boolean r_F() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "e")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        return true;
    }

    private boolean r_G() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(1, "f");
    }

    private boolean r_H() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "t")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(2, "ll")) {
                return false;
            }
        }
        return true;
    }

    private boolean r_I() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "o")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "e")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        return true;
    }

    private boolean r_J() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "a")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "e")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        return true;
    }

    private boolean r_K() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "i")) {
                this.cursor = this.limit - v_2;
                if (!this.eq_s_b(1, "e")) {
                    return false;
                }
                if (this.cursor <= this.limit_backward) {
                    return false;
                }
                --this.cursor;
                if (!this.eq_s_b(1, "u")) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean r_L() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "u")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "x")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (this.eq_s_b(1, "s")) {
            int v_5 = this.limit - this.cursor;
            if (!this.eq_s_b(1, "o")) {
                this.cursor = this.limit - v_5;
                return false;
            }
        }
        this.cursor = this.limit - v_4;
        return true;
    }

    private boolean r_M() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "a")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "c")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (this.eq_s_b(1, "e")) {
            return false;
        }
        this.cursor = this.limit - v_4;
        int v_5 = this.limit - this.cursor;
        if (this.eq_s_b(1, "m")) {
            return false;
        }
        this.cursor = this.limit - v_5;
        return true;
    }

    private boolean r_N() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        int v_2 = this.limit - this.cursor;
        int v_3 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_3;
        } else {
            this.cursor = this.limit - v_2;
            c = this.cursor - 2;
            if (this.limit_backward > c || c > this.limit) {
                return false;
            }
            this.cursor = c;
        }
        return true;
    }

    private boolean r_O() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "i")) {
                return false;
            }
        }
        return true;
    }

    private boolean r_P() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "c")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        return true;
    }

    private boolean r_Q() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "l")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (this.eq_s_b(1, "n")) {
            return false;
        }
        this.cursor = this.limit - v_4;
        return true;
    }

    private boolean r_R() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "n")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "r")) {
                return false;
            }
        }
        return true;
    }

    private boolean r_S() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(2, "dr")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "t")) {
                return false;
            }
            int v_3 = this.limit - this.cursor;
            if (this.eq_s_b(1, "t")) {
                return false;
            }
            this.cursor = this.limit - v_3;
        }
        return true;
    }

    private boolean r_T() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "s")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "t")) {
                return false;
            }
            int v_3 = this.limit - this.cursor;
            if (this.eq_s_b(1, "o")) {
                return false;
            }
            this.cursor = this.limit - v_3;
        }
        return true;
    }

    private boolean r_U() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "m")) {
                this.cursor = this.limit - v_2;
                if (!this.eq_s_b(1, "n")) {
                    this.cursor = this.limit - v_2;
                    if (!this.eq_s_b(1, "r")) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean r_V() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(1, "c");
    }

    private boolean r_W() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "s")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(1, "u")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        return true;
    }

    private boolean r_X() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.eq_s_b(1, "l")) {
            this.cursor = this.limit - v_2;
            if (!this.eq_s_b(1, "i")) {
                this.cursor = this.limit - v_2;
                if (!this.eq_s_b(1, "e")) {
                    return false;
                }
                if (this.cursor <= this.limit_backward) {
                    return false;
                }
                --this.cursor;
                if (!this.eq_s_b(1, "u")) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean r_Y() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(2, "in");
    }

    private boolean r_Z() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(1, "f")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        return true;
    }

    private boolean r_AA() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.find_among_b(a_0, 9) != 0;
    }

    private boolean r_BB() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 3;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (this.eq_s_b(3, "met")) {
            return false;
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (this.eq_s_b(4, "ryst")) {
            return false;
        }
        this.cursor = this.limit - v_3;
        return true;
    }

    private boolean r_CC() {
        int v_1 = this.limit - this.cursor;
        int c = this.cursor - 2;
        if (this.limit_backward > c || c > this.limit) {
            return false;
        }
        this.cursor = c;
        this.cursor = this.limit - v_1;
        return this.eq_s_b(1, "l");
    }

    private boolean r_endings() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 294);
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

    private boolean r_undouble() {
        int v_1 = this.limit - this.cursor;
        if (this.find_among_b(a_2, 10) == 0) {
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

    private boolean r_respell() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_3, 34);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.slice_from("ief");
                break;
            }
            case 2: {
                this.slice_from("uc");
                break;
            }
            case 3: {
                this.slice_from("um");
                break;
            }
            case 4: {
                this.slice_from("rb");
                break;
            }
            case 5: {
                this.slice_from("ur");
                break;
            }
            case 6: {
                this.slice_from("ister");
                break;
            }
            case 7: {
                this.slice_from("meter");
                break;
            }
            case 8: {
                this.slice_from("olut");
                break;
            }
            case 9: {
                int v_1 = this.limit - this.cursor;
                if (this.eq_s_b(1, "a")) {
                    return false;
                }
                this.cursor = this.limit - v_1;
                int v_2 = this.limit - this.cursor;
                if (this.eq_s_b(1, "i")) {
                    return false;
                }
                this.cursor = this.limit - v_2;
                int v_3 = this.limit - this.cursor;
                if (this.eq_s_b(1, "o")) {
                    return false;
                }
                this.cursor = this.limit - v_3;
                this.slice_from("l");
                break;
            }
            case 10: {
                this.slice_from("bic");
                break;
            }
            case 11: {
                this.slice_from("dic");
                break;
            }
            case 12: {
                this.slice_from("pic");
                break;
            }
            case 13: {
                this.slice_from("tic");
                break;
            }
            case 14: {
                this.slice_from("ac");
                break;
            }
            case 15: {
                this.slice_from("ec");
                break;
            }
            case 16: {
                this.slice_from("ic");
                break;
            }
            case 17: {
                this.slice_from("luc");
                break;
            }
            case 18: {
                this.slice_from("uas");
                break;
            }
            case 19: {
                this.slice_from("vas");
                break;
            }
            case 20: {
                this.slice_from("cis");
                break;
            }
            case 21: {
                this.slice_from("lis");
                break;
            }
            case 22: {
                this.slice_from("eris");
                break;
            }
            case 23: {
                this.slice_from("pans");
                break;
            }
            case 24: {
                int v_4 = this.limit - this.cursor;
                if (this.eq_s_b(1, "s")) {
                    return false;
                }
                this.cursor = this.limit - v_4;
                this.slice_from("ens");
                break;
            }
            case 25: {
                this.slice_from("ons");
                break;
            }
            case 26: {
                this.slice_from("lus");
                break;
            }
            case 27: {
                this.slice_from("rus");
                break;
            }
            case 28: {
                int v_5 = this.limit - this.cursor;
                if (this.eq_s_b(1, "p")) {
                    return false;
                }
                this.cursor = this.limit - v_5;
                int v_6 = this.limit - this.cursor;
                if (this.eq_s_b(1, "t")) {
                    return false;
                }
                this.cursor = this.limit - v_6;
                this.slice_from("hes");
                break;
            }
            case 29: {
                this.slice_from("mis");
                break;
            }
            case 30: {
                int v_7 = this.limit - this.cursor;
                if (this.eq_s_b(1, "m")) {
                    return false;
                }
                this.cursor = this.limit - v_7;
                this.slice_from("ens");
                break;
            }
            case 31: {
                this.slice_from("ers");
                break;
            }
            case 32: {
                int v_8 = this.limit - this.cursor;
                if (this.eq_s_b(1, "n")) {
                    return false;
                }
                this.cursor = this.limit - v_8;
                this.slice_from("es");
                break;
            }
            case 33: {
                this.slice_from("ys");
                break;
            }
            case 34: {
                this.slice_from("ys");
            }
        }
        return true;
    }

    @Override
    public boolean stem() {
        this.limit_backward = this.cursor;
        this.cursor = this.limit;
        int v_1 = this.limit - this.cursor;
        if (!this.r_endings()) {
            // empty if block
        }
        this.cursor = this.limit - v_1;
        int v_2 = this.limit - this.cursor;
        if (!this.r_undouble()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        if (!this.r_respell()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        this.cursor = this.limit_backward;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof LovinsStemmer;
    }

    public int hashCode() {
        return LovinsStemmer.class.getName().hashCode();
    }
}

