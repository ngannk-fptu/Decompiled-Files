/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class RomanianStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final RomanianStemmer methodObject = new RomanianStemmer();
    private static final Among[] a_0 = new Among[]{new Among("", -1, 3, "", methodObject), new Among("I", 0, 1, "", methodObject), new Among("U", 0, 2, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("ea", -1, 3, "", methodObject), new Among("a\u0163ia", -1, 7, "", methodObject), new Among("aua", -1, 2, "", methodObject), new Among("iua", -1, 4, "", methodObject), new Among("a\u0163ie", -1, 7, "", methodObject), new Among("ele", -1, 3, "", methodObject), new Among("ile", -1, 5, "", methodObject), new Among("iile", 6, 4, "", methodObject), new Among("iei", -1, 4, "", methodObject), new Among("atei", -1, 6, "", methodObject), new Among("ii", -1, 4, "", methodObject), new Among("ului", -1, 1, "", methodObject), new Among("ul", -1, 1, "", methodObject), new Among("elor", -1, 3, "", methodObject), new Among("ilor", -1, 4, "", methodObject), new Among("iilor", 14, 4, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("icala", -1, 4, "", methodObject), new Among("iciva", -1, 4, "", methodObject), new Among("ativa", -1, 5, "", methodObject), new Among("itiva", -1, 6, "", methodObject), new Among("icale", -1, 4, "", methodObject), new Among("a\u0163iune", -1, 5, "", methodObject), new Among("i\u0163iune", -1, 6, "", methodObject), new Among("atoare", -1, 5, "", methodObject), new Among("itoare", -1, 6, "", methodObject), new Among("\u0103toare", -1, 5, "", methodObject), new Among("icitate", -1, 4, "", methodObject), new Among("abilitate", -1, 1, "", methodObject), new Among("ibilitate", -1, 2, "", methodObject), new Among("ivitate", -1, 3, "", methodObject), new Among("icive", -1, 4, "", methodObject), new Among("ative", -1, 5, "", methodObject), new Among("itive", -1, 6, "", methodObject), new Among("icali", -1, 4, "", methodObject), new Among("atori", -1, 5, "", methodObject), new Among("icatori", 18, 4, "", methodObject), new Among("itori", -1, 6, "", methodObject), new Among("\u0103tori", -1, 5, "", methodObject), new Among("icitati", -1, 4, "", methodObject), new Among("abilitati", -1, 1, "", methodObject), new Among("ivitati", -1, 3, "", methodObject), new Among("icivi", -1, 4, "", methodObject), new Among("ativi", -1, 5, "", methodObject), new Among("itivi", -1, 6, "", methodObject), new Among("icit\u0103i", -1, 4, "", methodObject), new Among("abilit\u0103i", -1, 1, "", methodObject), new Among("ivit\u0103i", -1, 3, "", methodObject), new Among("icit\u0103\u0163i", -1, 4, "", methodObject), new Among("abilit\u0103\u0163i", -1, 1, "", methodObject), new Among("ivit\u0103\u0163i", -1, 3, "", methodObject), new Among("ical", -1, 4, "", methodObject), new Among("ator", -1, 5, "", methodObject), new Among("icator", 35, 4, "", methodObject), new Among("itor", -1, 6, "", methodObject), new Among("\u0103tor", -1, 5, "", methodObject), new Among("iciv", -1, 4, "", methodObject), new Among("ativ", -1, 5, "", methodObject), new Among("itiv", -1, 6, "", methodObject), new Among("ical\u0103", -1, 4, "", methodObject), new Among("iciv\u0103", -1, 4, "", methodObject), new Among("ativ\u0103", -1, 5, "", methodObject), new Among("itiv\u0103", -1, 6, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("ica", -1, 1, "", methodObject), new Among("abila", -1, 1, "", methodObject), new Among("ibila", -1, 1, "", methodObject), new Among("oasa", -1, 1, "", methodObject), new Among("ata", -1, 1, "", methodObject), new Among("ita", -1, 1, "", methodObject), new Among("anta", -1, 1, "", methodObject), new Among("ista", -1, 3, "", methodObject), new Among("uta", -1, 1, "", methodObject), new Among("iva", -1, 1, "", methodObject), new Among("ic", -1, 1, "", methodObject), new Among("ice", -1, 1, "", methodObject), new Among("abile", -1, 1, "", methodObject), new Among("ibile", -1, 1, "", methodObject), new Among("isme", -1, 3, "", methodObject), new Among("iune", -1, 2, "", methodObject), new Among("oase", -1, 1, "", methodObject), new Among("ate", -1, 1, "", methodObject), new Among("itate", 17, 1, "", methodObject), new Among("ite", -1, 1, "", methodObject), new Among("ante", -1, 1, "", methodObject), new Among("iste", -1, 3, "", methodObject), new Among("ute", -1, 1, "", methodObject), new Among("ive", -1, 1, "", methodObject), new Among("ici", -1, 1, "", methodObject), new Among("abili", -1, 1, "", methodObject), new Among("ibili", -1, 1, "", methodObject), new Among("iuni", -1, 2, "", methodObject), new Among("atori", -1, 1, "", methodObject), new Among("osi", -1, 1, "", methodObject), new Among("ati", -1, 1, "", methodObject), new Among("itati", 30, 1, "", methodObject), new Among("iti", -1, 1, "", methodObject), new Among("anti", -1, 1, "", methodObject), new Among("isti", -1, 3, "", methodObject), new Among("uti", -1, 1, "", methodObject), new Among("i\u015fti", -1, 3, "", methodObject), new Among("ivi", -1, 1, "", methodObject), new Among("it\u0103i", -1, 1, "", methodObject), new Among("o\u015fi", -1, 1, "", methodObject), new Among("it\u0103\u0163i", -1, 1, "", methodObject), new Among("abil", -1, 1, "", methodObject), new Among("ibil", -1, 1, "", methodObject), new Among("ism", -1, 3, "", methodObject), new Among("ator", -1, 1, "", methodObject), new Among("os", -1, 1, "", methodObject), new Among("at", -1, 1, "", methodObject), new Among("it", -1, 1, "", methodObject), new Among("ant", -1, 1, "", methodObject), new Among("ist", -1, 3, "", methodObject), new Among("ut", -1, 1, "", methodObject), new Among("iv", -1, 1, "", methodObject), new Among("ic\u0103", -1, 1, "", methodObject), new Among("abil\u0103", -1, 1, "", methodObject), new Among("ibil\u0103", -1, 1, "", methodObject), new Among("oas\u0103", -1, 1, "", methodObject), new Among("at\u0103", -1, 1, "", methodObject), new Among("it\u0103", -1, 1, "", methodObject), new Among("ant\u0103", -1, 1, "", methodObject), new Among("ist\u0103", -1, 3, "", methodObject), new Among("ut\u0103", -1, 1, "", methodObject), new Among("iv\u0103", -1, 1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("ea", -1, 1, "", methodObject), new Among("ia", -1, 1, "", methodObject), new Among("esc", -1, 1, "", methodObject), new Among("\u0103sc", -1, 1, "", methodObject), new Among("ind", -1, 1, "", methodObject), new Among("\u00e2nd", -1, 1, "", methodObject), new Among("are", -1, 1, "", methodObject), new Among("ere", -1, 1, "", methodObject), new Among("ire", -1, 1, "", methodObject), new Among("\u00e2re", -1, 1, "", methodObject), new Among("se", -1, 2, "", methodObject), new Among("ase", 10, 1, "", methodObject), new Among("sese", 10, 2, "", methodObject), new Among("ise", 10, 1, "", methodObject), new Among("use", 10, 1, "", methodObject), new Among("\u00e2se", 10, 1, "", methodObject), new Among("e\u015fte", -1, 1, "", methodObject), new Among("\u0103\u015fte", -1, 1, "", methodObject), new Among("eze", -1, 1, "", methodObject), new Among("ai", -1, 1, "", methodObject), new Among("eai", 19, 1, "", methodObject), new Among("iai", 19, 1, "", methodObject), new Among("sei", -1, 2, "", methodObject), new Among("e\u015fti", -1, 1, "", methodObject), new Among("\u0103\u015fti", -1, 1, "", methodObject), new Among("ui", -1, 1, "", methodObject), new Among("ezi", -1, 1, "", methodObject), new Among("\u00e2i", -1, 1, "", methodObject), new Among("a\u015fi", -1, 1, "", methodObject), new Among("se\u015fi", -1, 2, "", methodObject), new Among("ase\u015fi", 29, 1, "", methodObject), new Among("sese\u015fi", 29, 2, "", methodObject), new Among("ise\u015fi", 29, 1, "", methodObject), new Among("use\u015fi", 29, 1, "", methodObject), new Among("\u00e2se\u015fi", 29, 1, "", methodObject), new Among("i\u015fi", -1, 1, "", methodObject), new Among("u\u015fi", -1, 1, "", methodObject), new Among("\u00e2\u015fi", -1, 1, "", methodObject), new Among("a\u0163i", -1, 2, "", methodObject), new Among("ea\u0163i", 38, 1, "", methodObject), new Among("ia\u0163i", 38, 1, "", methodObject), new Among("e\u0163i", -1, 2, "", methodObject), new Among("i\u0163i", -1, 2, "", methodObject), new Among("\u00e2\u0163i", -1, 2, "", methodObject), new Among("ar\u0103\u0163i", -1, 1, "", methodObject), new Among("ser\u0103\u0163i", -1, 2, "", methodObject), new Among("aser\u0103\u0163i", 45, 1, "", methodObject), new Among("seser\u0103\u0163i", 45, 2, "", methodObject), new Among("iser\u0103\u0163i", 45, 1, "", methodObject), new Among("user\u0103\u0163i", 45, 1, "", methodObject), new Among("\u00e2ser\u0103\u0163i", 45, 1, "", methodObject), new Among("ir\u0103\u0163i", -1, 1, "", methodObject), new Among("ur\u0103\u0163i", -1, 1, "", methodObject), new Among("\u00e2r\u0103\u0163i", -1, 1, "", methodObject), new Among("am", -1, 1, "", methodObject), new Among("eam", 54, 1, "", methodObject), new Among("iam", 54, 1, "", methodObject), new Among("em", -1, 2, "", methodObject), new Among("asem", 57, 1, "", methodObject), new Among("sesem", 57, 2, "", methodObject), new Among("isem", 57, 1, "", methodObject), new Among("usem", 57, 1, "", methodObject), new Among("\u00e2sem", 57, 1, "", methodObject), new Among("im", -1, 2, "", methodObject), new Among("\u00e2m", -1, 2, "", methodObject), new Among("\u0103m", -1, 2, "", methodObject), new Among("ar\u0103m", 65, 1, "", methodObject), new Among("ser\u0103m", 65, 2, "", methodObject), new Among("aser\u0103m", 67, 1, "", methodObject), new Among("seser\u0103m", 67, 2, "", methodObject), new Among("iser\u0103m", 67, 1, "", methodObject), new Among("user\u0103m", 67, 1, "", methodObject), new Among("\u00e2ser\u0103m", 67, 1, "", methodObject), new Among("ir\u0103m", 65, 1, "", methodObject), new Among("ur\u0103m", 65, 1, "", methodObject), new Among("\u00e2r\u0103m", 65, 1, "", methodObject), new Among("au", -1, 1, "", methodObject), new Among("eau", 76, 1, "", methodObject), new Among("iau", 76, 1, "", methodObject), new Among("indu", -1, 1, "", methodObject), new Among("\u00e2ndu", -1, 1, "", methodObject), new Among("ez", -1, 1, "", methodObject), new Among("easc\u0103", -1, 1, "", methodObject), new Among("ar\u0103", -1, 1, "", methodObject), new Among("ser\u0103", -1, 2, "", methodObject), new Among("aser\u0103", 84, 1, "", methodObject), new Among("seser\u0103", 84, 2, "", methodObject), new Among("iser\u0103", 84, 1, "", methodObject), new Among("user\u0103", 84, 1, "", methodObject), new Among("\u00e2ser\u0103", 84, 1, "", methodObject), new Among("ir\u0103", -1, 1, "", methodObject), new Among("ur\u0103", -1, 1, "", methodObject), new Among("\u00e2r\u0103", -1, 1, "", methodObject), new Among("eaz\u0103", -1, 1, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("a", -1, 1, "", methodObject), new Among("e", -1, 1, "", methodObject), new Among("ie", 1, 1, "", methodObject), new Among("i", -1, 1, "", methodObject), new Among("\u0103", -1, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0002', ' ', '\u0000', '\u0000', '\u0004'};
    private boolean B_standard_suffix_removed;
    private int I_p2;
    private int I_p1;
    private int I_pV;

    private void copy_from(RomanianStemmer other) {
        this.B_standard_suffix_removed = other.B_standard_suffix_removed;
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
                block4: {
                    block6: {
                        int v_3;
                        block5: {
                            v_2 = ++this.cursor;
                            if (!this.in_grouping(g_v, 97, 259)) break block4;
                            this.bra = this.cursor;
                            v_3 = this.cursor;
                            if (!this.eq_s(1, "u")) break block5;
                            this.ket = this.cursor;
                            if (!this.in_grouping(g_v, 97, 259)) break block5;
                            this.slice_from("U");
                            break block6;
                        }
                        this.cursor = v_3;
                        if (!this.eq_s(1, "i")) break block4;
                        this.ket = this.cursor;
                        if (!this.in_grouping(g_v, 97, 259)) break block4;
                        this.slice_from("I");
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
     * Exception decompiling
     */
    private boolean r_mark_regions() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[WHILELOOP]], but top level block is 14[SIMPLE_IF_TAKEN]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_postlude() {
        int v_1;
        block6: while (true) {
            v_1 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_0, 3);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block6;
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
                    if (this.cursor >= this.limit) break block6;
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

    private boolean r_step_0() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 16);
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
                this.slice_from("i");
                break;
            }
            case 5: {
                int v_1 = this.limit - this.cursor;
                if (this.eq_s_b(2, "ab")) {
                    return false;
                }
                this.cursor = this.limit - v_1;
                this.slice_from("i");
                break;
            }
            case 6: {
                this.slice_from("at");
                break;
            }
            case 7: {
                this.slice_from("a\u0163i");
            }
        }
        return true;
    }

    private boolean r_combo_suffix() {
        int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 46);
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
                this.slice_from("abil");
                break;
            }
            case 2: {
                this.slice_from("ibil");
                break;
            }
            case 3: {
                this.slice_from("iv");
                break;
            }
            case 4: {
                this.slice_from("ic");
                break;
            }
            case 5: {
                this.slice_from("at");
                break;
            }
            case 6: {
                this.slice_from("it");
            }
        }
        this.B_standard_suffix_removed = true;
        this.cursor = this.limit - v_1;
        return true;
    }

    private boolean r_standard_suffix() {
        int v_1;
        this.B_standard_suffix_removed = false;
        do {
            v_1 = this.limit - this.cursor;
        } while (this.r_combo_suffix());
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_3, 62);
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
                if (!this.eq_s_b(1, "\u0163")) {
                    return false;
                }
                this.bra = this.cursor;
                this.slice_from("t");
                break;
            }
            case 3: {
                this.slice_from("ist");
            }
        }
        this.B_standard_suffix_removed = true;
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
        int among_var = this.find_among_b(a_4, 94);
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
                int v_3 = this.limit - this.cursor;
                if (!this.out_grouping_b(g_v, 97, 259)) {
                    this.cursor = this.limit - v_3;
                    if (!this.eq_s_b(1, "u")) {
                        this.limit_backward = v_2;
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
        this.limit_backward = v_2;
        return true;
    }

    private boolean r_vowel_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_5, 5);
        if (among_var == 0) {
            return false;
        }
        this.bra = this.cursor;
        if (!this.r_RV()) {
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
        if (!this.r_prelude()) {
            // empty if block
        }
        int v_2 = this.cursor = v_1;
        if (!this.r_mark_regions()) {
            // empty if block
        }
        this.limit_backward = this.cursor = v_2;
        this.cursor = this.limit;
        int v_3 = this.limit - this.cursor;
        if (!this.r_step_0()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_4;
        int v_5 = this.limit - this.cursor;
        int v_6 = this.limit - this.cursor;
        if (!this.B_standard_suffix_removed) {
            this.cursor = this.limit - v_6;
            if (!this.r_verb_suffix()) {
                // empty if block
            }
        }
        this.cursor = this.limit - v_5;
        int v_7 = this.limit - this.cursor;
        if (!this.r_vowel_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_7;
        int v_8 = this.cursor = this.limit_backward;
        if (!this.r_postlude()) {
            // empty if block
        }
        this.cursor = v_8;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof RomanianStemmer;
    }

    public int hashCode() {
        return RomanianStemmer.class.getName().hashCode();
    }
}

