/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class SpanishStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final SpanishStemmer methodObject = new SpanishStemmer();
    private static final Among[] a_0 = new Among[]{new Among("", -1, 6, "", methodObject), new Among("\u00e1", 0, 1, "", methodObject), new Among("\u00e9", 0, 2, "", methodObject), new Among("\u00ed", 0, 3, "", methodObject), new Among("\u00f3", 0, 4, "", methodObject), new Among("\u00fa", 0, 5, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("la", -1, -1, "", methodObject), new Among("sela", 0, -1, "", methodObject), new Among("le", -1, -1, "", methodObject), new Among("me", -1, -1, "", methodObject), new Among("se", -1, -1, "", methodObject), new Among("lo", -1, -1, "", methodObject), new Among("selo", 5, -1, "", methodObject), new Among("las", -1, -1, "", methodObject), new Among("selas", 7, -1, "", methodObject), new Among("les", -1, -1, "", methodObject), new Among("los", -1, -1, "", methodObject), new Among("selos", 10, -1, "", methodObject), new Among("nos", -1, -1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ando", -1, 6, "", methodObject), new Among("iendo", -1, 6, "", methodObject), new Among("yendo", -1, 7, "", methodObject), new Among("\u00e1ndo", -1, 2, "", methodObject), new Among("i\u00e9ndo", -1, 1, "", methodObject), new Among("ar", -1, 6, "", methodObject), new Among("er", -1, 6, "", methodObject), new Among("ir", -1, 6, "", methodObject), new Among("\u00e1r", -1, 3, "", methodObject), new Among("\u00e9r", -1, 4, "", methodObject), new Among("\u00edr", -1, 5, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("ic", -1, -1, "", methodObject), new Among("ad", -1, -1, "", methodObject), new Among("os", -1, -1, "", methodObject), new Among("iv", -1, 1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("able", -1, 1, "", methodObject), new Among("ible", -1, 1, "", methodObject), new Among("ante", -1, 1, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("ic", -1, 1, "", methodObject), new Among("abil", -1, 1, "", methodObject), new Among("iv", -1, 1, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("ica", -1, 1, "", methodObject), new Among("ancia", -1, 2, "", methodObject), new Among("encia", -1, 5, "", methodObject), new Among("adora", -1, 2, "", methodObject), new Among("osa", -1, 1, "", methodObject), new Among("ista", -1, 1, "", methodObject), new Among("iva", -1, 9, "", methodObject), new Among("anza", -1, 1, "", methodObject), new Among("log\u00eda", -1, 3, "", methodObject), new Among("idad", -1, 8, "", methodObject), new Among("able", -1, 1, "", methodObject), new Among("ible", -1, 1, "", methodObject), new Among("ante", -1, 2, "", methodObject), new Among("mente", -1, 7, "", methodObject), new Among("amente", 13, 6, "", methodObject), new Among("aci\u00f3n", -1, 2, "", methodObject), new Among("uci\u00f3n", -1, 4, "", methodObject), new Among("ico", -1, 1, "", methodObject), new Among("ismo", -1, 1, "", methodObject), new Among("oso", -1, 1, "", methodObject), new Among("amiento", -1, 1, "", methodObject), new Among("imiento", -1, 1, "", methodObject), new Among("ivo", -1, 9, "", methodObject), new Among("ador", -1, 2, "", methodObject), new Among("icas", -1, 1, "", methodObject), new Among("ancias", -1, 2, "", methodObject), new Among("encias", -1, 5, "", methodObject), new Among("adoras", -1, 2, "", methodObject), new Among("osas", -1, 1, "", methodObject), new Among("istas", -1, 1, "", methodObject), new Among("ivas", -1, 9, "", methodObject), new Among("anzas", -1, 1, "", methodObject), new Among("log\u00edas", -1, 3, "", methodObject), new Among("idades", -1, 8, "", methodObject), new Among("ables", -1, 1, "", methodObject), new Among("ibles", -1, 1, "", methodObject), new Among("aciones", -1, 2, "", methodObject), new Among("uciones", -1, 4, "", methodObject), new Among("adores", -1, 2, "", methodObject), new Among("antes", -1, 2, "", methodObject), new Among("icos", -1, 1, "", methodObject), new Among("ismos", -1, 1, "", methodObject), new Among("osos", -1, 1, "", methodObject), new Among("amientos", -1, 1, "", methodObject), new Among("imientos", -1, 1, "", methodObject), new Among("ivos", -1, 9, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("ya", -1, 1, "", methodObject), new Among("ye", -1, 1, "", methodObject), new Among("yan", -1, 1, "", methodObject), new Among("yen", -1, 1, "", methodObject), new Among("yeron", -1, 1, "", methodObject), new Among("yendo", -1, 1, "", methodObject), new Among("yo", -1, 1, "", methodObject), new Among("yas", -1, 1, "", methodObject), new Among("yes", -1, 1, "", methodObject), new Among("yais", -1, 1, "", methodObject), new Among("yamos", -1, 1, "", methodObject), new Among("y\u00f3", -1, 1, "", methodObject)};
    private static final Among[] a_8 = new Among[]{new Among("aba", -1, 2, "", methodObject), new Among("ada", -1, 2, "", methodObject), new Among("ida", -1, 2, "", methodObject), new Among("ara", -1, 2, "", methodObject), new Among("iera", -1, 2, "", methodObject), new Among("\u00eda", -1, 2, "", methodObject), new Among("ar\u00eda", 5, 2, "", methodObject), new Among("er\u00eda", 5, 2, "", methodObject), new Among("ir\u00eda", 5, 2, "", methodObject), new Among("ad", -1, 2, "", methodObject), new Among("ed", -1, 2, "", methodObject), new Among("id", -1, 2, "", methodObject), new Among("ase", -1, 2, "", methodObject), new Among("iese", -1, 2, "", methodObject), new Among("aste", -1, 2, "", methodObject), new Among("iste", -1, 2, "", methodObject), new Among("an", -1, 2, "", methodObject), new Among("aban", 16, 2, "", methodObject), new Among("aran", 16, 2, "", methodObject), new Among("ieran", 16, 2, "", methodObject), new Among("\u00edan", 16, 2, "", methodObject), new Among("ar\u00edan", 20, 2, "", methodObject), new Among("er\u00edan", 20, 2, "", methodObject), new Among("ir\u00edan", 20, 2, "", methodObject), new Among("en", -1, 1, "", methodObject), new Among("asen", 24, 2, "", methodObject), new Among("iesen", 24, 2, "", methodObject), new Among("aron", -1, 2, "", methodObject), new Among("ieron", -1, 2, "", methodObject), new Among("ar\u00e1n", -1, 2, "", methodObject), new Among("er\u00e1n", -1, 2, "", methodObject), new Among("ir\u00e1n", -1, 2, "", methodObject), new Among("ado", -1, 2, "", methodObject), new Among("ido", -1, 2, "", methodObject), new Among("ando", -1, 2, "", methodObject), new Among("iendo", -1, 2, "", methodObject), new Among("ar", -1, 2, "", methodObject), new Among("er", -1, 2, "", methodObject), new Among("ir", -1, 2, "", methodObject), new Among("as", -1, 2, "", methodObject), new Among("abas", 39, 2, "", methodObject), new Among("adas", 39, 2, "", methodObject), new Among("idas", 39, 2, "", methodObject), new Among("aras", 39, 2, "", methodObject), new Among("ieras", 39, 2, "", methodObject), new Among("\u00edas", 39, 2, "", methodObject), new Among("ar\u00edas", 45, 2, "", methodObject), new Among("er\u00edas", 45, 2, "", methodObject), new Among("ir\u00edas", 45, 2, "", methodObject), new Among("es", -1, 1, "", methodObject), new Among("ases", 49, 2, "", methodObject), new Among("ieses", 49, 2, "", methodObject), new Among("abais", -1, 2, "", methodObject), new Among("arais", -1, 2, "", methodObject), new Among("ierais", -1, 2, "", methodObject), new Among("\u00edais", -1, 2, "", methodObject), new Among("ar\u00edais", 55, 2, "", methodObject), new Among("er\u00edais", 55, 2, "", methodObject), new Among("ir\u00edais", 55, 2, "", methodObject), new Among("aseis", -1, 2, "", methodObject), new Among("ieseis", -1, 2, "", methodObject), new Among("asteis", -1, 2, "", methodObject), new Among("isteis", -1, 2, "", methodObject), new Among("\u00e1is", -1, 2, "", methodObject), new Among("\u00e9is", -1, 1, "", methodObject), new Among("ar\u00e9is", 64, 2, "", methodObject), new Among("er\u00e9is", 64, 2, "", methodObject), new Among("ir\u00e9is", 64, 2, "", methodObject), new Among("ados", -1, 2, "", methodObject), new Among("idos", -1, 2, "", methodObject), new Among("amos", -1, 2, "", methodObject), new Among("\u00e1bamos", 70, 2, "", methodObject), new Among("\u00e1ramos", 70, 2, "", methodObject), new Among("i\u00e9ramos", 70, 2, "", methodObject), new Among("\u00edamos", 70, 2, "", methodObject), new Among("ar\u00edamos", 74, 2, "", methodObject), new Among("er\u00edamos", 74, 2, "", methodObject), new Among("ir\u00edamos", 74, 2, "", methodObject), new Among("emos", -1, 1, "", methodObject), new Among("aremos", 78, 2, "", methodObject), new Among("eremos", 78, 2, "", methodObject), new Among("iremos", 78, 2, "", methodObject), new Among("\u00e1semos", 78, 2, "", methodObject), new Among("i\u00e9semos", 78, 2, "", methodObject), new Among("imos", -1, 2, "", methodObject), new Among("ar\u00e1s", -1, 2, "", methodObject), new Among("er\u00e1s", -1, 2, "", methodObject), new Among("ir\u00e1s", -1, 2, "", methodObject), new Among("\u00eds", -1, 2, "", methodObject), new Among("ar\u00e1", -1, 2, "", methodObject), new Among("er\u00e1", -1, 2, "", methodObject), new Among("ir\u00e1", -1, 2, "", methodObject), new Among("ar\u00e9", -1, 2, "", methodObject), new Among("er\u00e9", -1, 2, "", methodObject), new Among("ir\u00e9", -1, 2, "", methodObject), new Among("i\u00f3", -1, 2, "", methodObject)};
    private static final Among[] a_9 = new Among[]{new Among("a", -1, 1, "", methodObject), new Among("e", -1, 2, "", methodObject), new Among("o", -1, 1, "", methodObject), new Among("os", -1, 1, "", methodObject), new Among("\u00e1", -1, 1, "", methodObject), new Among("\u00e9", -1, 2, "", methodObject), new Among("\u00ed", -1, 1, "", methodObject), new Among("\u00f3", -1, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0001', '\u0011', '\u0004', '\n'};
    private int I_p2;
    private int I_p1;
    private int I_pV;

    private void copy_from(SpanishStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
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
        block9: while (true) {
            v_1 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_0, 6);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block9;
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
                    this.slice_from("i");
                    break;
                }
                case 4: {
                    this.slice_from("o");
                    break;
                }
                case 5: {
                    this.slice_from("u");
                    break;
                }
                case 6: {
                    if (this.cursor >= this.limit) break block9;
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

    private boolean r_attached_pronoun() {
        this.ket = this.cursor;
        if (this.find_among_b(a_1, 13) == 0) {
            return false;
        }
        this.bra = this.cursor;
        int among_var = this.find_among_b(a_2, 11);
        if (among_var == 0) {
            return false;
        }
        if (!this.r_RV()) {
            return false;
        }
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                this.bra = this.cursor;
                this.slice_from("iendo");
                break;
            }
            case 2: {
                this.bra = this.cursor;
                this.slice_from("ando");
                break;
            }
            case 3: {
                this.bra = this.cursor;
                this.slice_from("ar");
                break;
            }
            case 4: {
                this.bra = this.cursor;
                this.slice_from("er");
                break;
            }
            case 5: {
                this.bra = this.cursor;
                this.slice_from("ir");
                break;
            }
            case 6: {
                this.slice_del();
                break;
            }
            case 7: {
                if (!this.eq_s_b(1, "u")) {
                    return false;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_standard_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_6, 46);
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
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_1;
                    break;
                }
                this.slice_del();
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
                this.slice_from("ente");
                break;
            }
            case 6: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                int v_2 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_3, 4);
                if (among_var == 0) {
                    this.cursor = this.limit - v_2;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_2;
                    break;
                }
                this.slice_del();
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_2;
                        break;
                    }
                    case 1: {
                        this.ket = this.cursor;
                        if (!this.eq_s_b(2, "at")) {
                            this.cursor = this.limit - v_2;
                            break;
                        }
                        this.bra = this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_2;
                            break;
                        }
                        this.slice_del();
                    }
                }
                break;
            }
            case 7: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_3 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_4, 3);
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
                    }
                }
                break;
            }
            case 8: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_4 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_5, 3);
                if (among_var == 0) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_4;
                        break;
                    }
                    case 1: {
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_4;
                            break;
                        }
                        this.slice_del();
                    }
                }
                break;
            }
            case 9: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_5 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "at")) {
                    this.cursor = this.limit - v_5;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_5;
                    break;
                }
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_y_verb_suffix() {
        int v_1 = this.limit - this.cursor;
        if (this.cursor < this.I_pV) {
            return false;
        }
        this.cursor = this.I_pV;
        int v_2 = this.limit_backward;
        this.limit_backward = this.cursor;
        this.ket = this.cursor = this.limit - v_1;
        int among_var = this.find_among_b(a_7, 12);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_2;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                if (!this.eq_s_b(1, "u")) {
                    return false;
                }
                this.slice_del();
            }
        }
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
        int among_var = this.find_among_b(a_8, 96);
        if (among_var == 0) {
            this.limit_backward = v_2;
            return false;
        }
        this.bra = this.cursor;
        this.limit_backward = v_2;
        switch (among_var) {
            case 0: {
                return false;
            }
            case 1: {
                int v_3 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "u")) {
                    this.cursor = this.limit - v_3;
                } else {
                    int v_4 = this.limit - this.cursor;
                    this.cursor = !this.eq_s_b(1, "g") ? this.limit - v_3 : this.limit - v_4;
                }
                this.bra = this.cursor;
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_del();
            }
        }
        return true;
    }

    private boolean r_residual_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_9, 8);
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
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                int v_1 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(1, "u")) {
                    this.cursor = this.limit - v_1;
                    break;
                }
                this.bra = this.cursor;
                int v_2 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "g")) {
                    this.cursor = this.limit - v_1;
                    break;
                }
                this.cursor = this.limit - v_2;
                if (!this.r_RV()) {
                    this.cursor = this.limit - v_1;
                    break;
                }
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
        if (!this.r_attached_pronoun()) {
            // empty if block
        }
        this.cursor = this.limit - v_2;
        int v_3 = this.limit - this.cursor;
        int v_4 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {
            this.cursor = this.limit - v_4;
            if (!this.r_y_verb_suffix()) {
                this.cursor = this.limit - v_4;
                if (!this.r_verb_suffix()) {
                    // empty if block
                }
            }
        }
        this.cursor = this.limit - v_3;
        int v_5 = this.limit - this.cursor;
        if (!this.r_residual_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_5;
        int v_6 = this.cursor = this.limit_backward;
        if (!this.r_postlude()) {
            // empty if block
        }
        this.cursor = v_6;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof SpanishStemmer;
    }

    public int hashCode() {
        return SpanishStemmer.class.getName().hashCode();
    }
}

