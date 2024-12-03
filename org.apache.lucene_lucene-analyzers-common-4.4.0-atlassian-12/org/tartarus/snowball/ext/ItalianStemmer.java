/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class ItalianStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final ItalianStemmer methodObject = new ItalianStemmer();
    private static final Among[] a_0 = new Among[]{new Among("", -1, 7, "", methodObject), new Among("qu", 0, 6, "", methodObject), new Among("\u00e1", 0, 1, "", methodObject), new Among("\u00e9", 0, 2, "", methodObject), new Among("\u00ed", 0, 3, "", methodObject), new Among("\u00f3", 0, 4, "", methodObject), new Among("\u00fa", 0, 5, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("", -1, 3, "", methodObject), new Among("I", 0, 1, "", methodObject), new Among("U", 0, 2, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("la", -1, -1, "", methodObject), new Among("cela", 0, -1, "", methodObject), new Among("gliela", 0, -1, "", methodObject), new Among("mela", 0, -1, "", methodObject), new Among("tela", 0, -1, "", methodObject), new Among("vela", 0, -1, "", methodObject), new Among("le", -1, -1, "", methodObject), new Among("cele", 6, -1, "", methodObject), new Among("gliele", 6, -1, "", methodObject), new Among("mele", 6, -1, "", methodObject), new Among("tele", 6, -1, "", methodObject), new Among("vele", 6, -1, "", methodObject), new Among("ne", -1, -1, "", methodObject), new Among("cene", 12, -1, "", methodObject), new Among("gliene", 12, -1, "", methodObject), new Among("mene", 12, -1, "", methodObject), new Among("sene", 12, -1, "", methodObject), new Among("tene", 12, -1, "", methodObject), new Among("vene", 12, -1, "", methodObject), new Among("ci", -1, -1, "", methodObject), new Among("li", -1, -1, "", methodObject), new Among("celi", 20, -1, "", methodObject), new Among("glieli", 20, -1, "", methodObject), new Among("meli", 20, -1, "", methodObject), new Among("teli", 20, -1, "", methodObject), new Among("veli", 20, -1, "", methodObject), new Among("gli", 20, -1, "", methodObject), new Among("mi", -1, -1, "", methodObject), new Among("si", -1, -1, "", methodObject), new Among("ti", -1, -1, "", methodObject), new Among("vi", -1, -1, "", methodObject), new Among("lo", -1, -1, "", methodObject), new Among("celo", 31, -1, "", methodObject), new Among("glielo", 31, -1, "", methodObject), new Among("melo", 31, -1, "", methodObject), new Among("telo", 31, -1, "", methodObject), new Among("velo", 31, -1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("ando", -1, 1, "", methodObject), new Among("endo", -1, 1, "", methodObject), new Among("ar", -1, 2, "", methodObject), new Among("er", -1, 2, "", methodObject), new Among("ir", -1, 2, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("ic", -1, -1, "", methodObject), new Among("abil", -1, -1, "", methodObject), new Among("os", -1, -1, "", methodObject), new Among("iv", -1, 1, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("ic", -1, 1, "", methodObject), new Among("abil", -1, 1, "", methodObject), new Among("iv", -1, 1, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("ica", -1, 1, "", methodObject), new Among("logia", -1, 3, "", methodObject), new Among("osa", -1, 1, "", methodObject), new Among("ista", -1, 1, "", methodObject), new Among("iva", -1, 9, "", methodObject), new Among("anza", -1, 1, "", methodObject), new Among("enza", -1, 5, "", methodObject), new Among("ice", -1, 1, "", methodObject), new Among("atrice", 7, 1, "", methodObject), new Among("iche", -1, 1, "", methodObject), new Among("logie", -1, 3, "", methodObject), new Among("abile", -1, 1, "", methodObject), new Among("ibile", -1, 1, "", methodObject), new Among("usione", -1, 4, "", methodObject), new Among("azione", -1, 2, "", methodObject), new Among("uzione", -1, 4, "", methodObject), new Among("atore", -1, 2, "", methodObject), new Among("ose", -1, 1, "", methodObject), new Among("ante", -1, 1, "", methodObject), new Among("mente", -1, 1, "", methodObject), new Among("amente", 19, 7, "", methodObject), new Among("iste", -1, 1, "", methodObject), new Among("ive", -1, 9, "", methodObject), new Among("anze", -1, 1, "", methodObject), new Among("enze", -1, 5, "", methodObject), new Among("ici", -1, 1, "", methodObject), new Among("atrici", 25, 1, "", methodObject), new Among("ichi", -1, 1, "", methodObject), new Among("abili", -1, 1, "", methodObject), new Among("ibili", -1, 1, "", methodObject), new Among("ismi", -1, 1, "", methodObject), new Among("usioni", -1, 4, "", methodObject), new Among("azioni", -1, 2, "", methodObject), new Among("uzioni", -1, 4, "", methodObject), new Among("atori", -1, 2, "", methodObject), new Among("osi", -1, 1, "", methodObject), new Among("anti", -1, 1, "", methodObject), new Among("amenti", -1, 6, "", methodObject), new Among("imenti", -1, 6, "", methodObject), new Among("isti", -1, 1, "", methodObject), new Among("ivi", -1, 9, "", methodObject), new Among("ico", -1, 1, "", methodObject), new Among("ismo", -1, 1, "", methodObject), new Among("oso", -1, 1, "", methodObject), new Among("amento", -1, 6, "", methodObject), new Among("imento", -1, 6, "", methodObject), new Among("ivo", -1, 9, "", methodObject), new Among("it\u00e0", -1, 8, "", methodObject), new Among("ist\u00e0", -1, 1, "", methodObject), new Among("ist\u00e8", -1, 1, "", methodObject), new Among("ist\u00ec", -1, 1, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("isca", -1, 1, "", methodObject), new Among("enda", -1, 1, "", methodObject), new Among("ata", -1, 1, "", methodObject), new Among("ita", -1, 1, "", methodObject), new Among("uta", -1, 1, "", methodObject), new Among("ava", -1, 1, "", methodObject), new Among("eva", -1, 1, "", methodObject), new Among("iva", -1, 1, "", methodObject), new Among("erebbe", -1, 1, "", methodObject), new Among("irebbe", -1, 1, "", methodObject), new Among("isce", -1, 1, "", methodObject), new Among("ende", -1, 1, "", methodObject), new Among("are", -1, 1, "", methodObject), new Among("ere", -1, 1, "", methodObject), new Among("ire", -1, 1, "", methodObject), new Among("asse", -1, 1, "", methodObject), new Among("ate", -1, 1, "", methodObject), new Among("avate", 16, 1, "", methodObject), new Among("evate", 16, 1, "", methodObject), new Among("ivate", 16, 1, "", methodObject), new Among("ete", -1, 1, "", methodObject), new Among("erete", 20, 1, "", methodObject), new Among("irete", 20, 1, "", methodObject), new Among("ite", -1, 1, "", methodObject), new Among("ereste", -1, 1, "", methodObject), new Among("ireste", -1, 1, "", methodObject), new Among("ute", -1, 1, "", methodObject), new Among("erai", -1, 1, "", methodObject), new Among("irai", -1, 1, "", methodObject), new Among("isci", -1, 1, "", methodObject), new Among("endi", -1, 1, "", methodObject), new Among("erei", -1, 1, "", methodObject), new Among("irei", -1, 1, "", methodObject), new Among("assi", -1, 1, "", methodObject), new Among("ati", -1, 1, "", methodObject), new Among("iti", -1, 1, "", methodObject), new Among("eresti", -1, 1, "", methodObject), new Among("iresti", -1, 1, "", methodObject), new Among("uti", -1, 1, "", methodObject), new Among("avi", -1, 1, "", methodObject), new Among("evi", -1, 1, "", methodObject), new Among("ivi", -1, 1, "", methodObject), new Among("isco", -1, 1, "", methodObject), new Among("ando", -1, 1, "", methodObject), new Among("endo", -1, 1, "", methodObject), new Among("Yamo", -1, 1, "", methodObject), new Among("iamo", -1, 1, "", methodObject), new Among("avamo", -1, 1, "", methodObject), new Among("evamo", -1, 1, "", methodObject), new Among("ivamo", -1, 1, "", methodObject), new Among("eremo", -1, 1, "", methodObject), new Among("iremo", -1, 1, "", methodObject), new Among("assimo", -1, 1, "", methodObject), new Among("ammo", -1, 1, "", methodObject), new Among("emmo", -1, 1, "", methodObject), new Among("eremmo", 54, 1, "", methodObject), new Among("iremmo", 54, 1, "", methodObject), new Among("immo", -1, 1, "", methodObject), new Among("ano", -1, 1, "", methodObject), new Among("iscano", 58, 1, "", methodObject), new Among("avano", 58, 1, "", methodObject), new Among("evano", 58, 1, "", methodObject), new Among("ivano", 58, 1, "", methodObject), new Among("eranno", -1, 1, "", methodObject), new Among("iranno", -1, 1, "", methodObject), new Among("ono", -1, 1, "", methodObject), new Among("iscono", 65, 1, "", methodObject), new Among("arono", 65, 1, "", methodObject), new Among("erono", 65, 1, "", methodObject), new Among("irono", 65, 1, "", methodObject), new Among("erebbero", -1, 1, "", methodObject), new Among("irebbero", -1, 1, "", methodObject), new Among("assero", -1, 1, "", methodObject), new Among("essero", -1, 1, "", methodObject), new Among("issero", -1, 1, "", methodObject), new Among("ato", -1, 1, "", methodObject), new Among("ito", -1, 1, "", methodObject), new Among("uto", -1, 1, "", methodObject), new Among("avo", -1, 1, "", methodObject), new Among("evo", -1, 1, "", methodObject), new Among("ivo", -1, 1, "", methodObject), new Among("ar", -1, 1, "", methodObject), new Among("ir", -1, 1, "", methodObject), new Among("er\u00e0", -1, 1, "", methodObject), new Among("ir\u00e0", -1, 1, "", methodObject), new Among("er\u00f2", -1, 1, "", methodObject), new Among("ir\u00f2", -1, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080', '\u0080', '\b', '\u0002', '\u0001'};
    private static final char[] g_AEIO = new char[]{'\u0011', 'A', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0080', '\u0080', '\b', '\u0002'};
    private static final char[] g_CG = new char[]{'\u0011'};
    private int I_p2;
    private int I_p1;
    private int I_pV;

    private void copy_from(ItalianStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_prelude() {
        int v_3;
        int v_2;
        int v_1 = this.cursor;
        block10: while (true) {
            v_2 = ++this.cursor;
            this.bra = this.cursor;
            int among_var = this.find_among(a_0, 7);
            if (among_var == 0) break;
            this.ket = this.cursor;
            switch (among_var) {
                case 0: {
                    break block10;
                }
                case 1: {
                    this.slice_from("\u00e0");
                    break;
                }
                case 2: {
                    this.slice_from("\u00e8");
                    break;
                }
                case 3: {
                    this.slice_from("\u00ec");
                    break;
                }
                case 4: {
                    this.slice_from("\u00f2");
                    break;
                }
                case 5: {
                    this.slice_from("\u00f9");
                    break;
                }
                case 6: {
                    this.slice_from("qU");
                    break;
                }
                case 7: {
                    if (this.cursor >= this.limit) break block10;
                }
            }
        }
        this.cursor = v_2;
        this.cursor = v_1;
        block11: while (true) {
            v_3 = this.cursor;
            do {
                int v_4;
                block15: {
                    block17: {
                        int v_5;
                        block16: {
                            v_4 = ++this.cursor;
                            if (!this.in_grouping(g_v, 97, 249)) break block15;
                            this.bra = this.cursor;
                            v_5 = this.cursor;
                            if (!this.eq_s(1, "u")) break block16;
                            this.ket = this.cursor;
                            if (!this.in_grouping(g_v, 97, 249)) break block16;
                            this.slice_from("U");
                            break block17;
                        }
                        this.cursor = v_5;
                        if (!this.eq_s(1, "i")) break block15;
                        this.ket = this.cursor;
                        if (!this.in_grouping(g_v, 97, 249)) break block15;
                        this.slice_from("I");
                    }
                    this.cursor = v_4;
                    continue block11;
                }
                this.cursor = v_4;
            } while (this.cursor < this.limit);
            break;
        }
        this.cursor = v_3;
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
            int among_var = this.find_among(a_1, 3);
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

    private boolean r_attached_pronoun() {
        this.ket = this.cursor;
        if (this.find_among_b(a_2, 37) == 0) {
            return false;
        }
        this.bra = this.cursor;
        int among_var = this.find_among_b(a_3, 5);
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
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("e");
            }
        }
        return true;
    }

    private boolean r_standard_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_6, 51);
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
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 7: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                int v_2 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_4, 4);
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
            case 8: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_3 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_5, 3);
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
            case 9: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_4 = this.limit - this.cursor;
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "at")) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.slice_del();
                this.ket = this.cursor;
                if (!this.eq_s_b(2, "ic")) {
                    this.cursor = this.limit - v_4;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_4;
                    break;
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
        int among_var = this.find_among_b(a_7, 87);
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
                this.slice_del();
            }
        }
        this.limit_backward = v_2;
        return true;
    }

    private boolean r_vowel_suffix() {
        int v_1 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.in_grouping_b(g_AEIO, 97, 242)) {
            this.cursor = this.limit - v_1;
        } else {
            this.bra = this.cursor;
            if (!this.r_RV()) {
                this.cursor = this.limit - v_1;
            } else {
                this.slice_del();
                this.ket = this.cursor;
                if (!this.eq_s_b(1, "i")) {
                    this.cursor = this.limit - v_1;
                } else {
                    this.bra = this.cursor;
                    if (!this.r_RV()) {
                        this.cursor = this.limit - v_1;
                    } else {
                        this.slice_del();
                    }
                }
            }
        }
        int v_2 = this.limit - this.cursor;
        this.ket = this.cursor;
        if (!this.eq_s_b(1, "h")) {
            this.cursor = this.limit - v_2;
        } else {
            this.bra = this.cursor;
            if (!this.in_grouping_b(g_CG, 99, 103)) {
                this.cursor = this.limit - v_2;
            } else if (!this.r_RV()) {
                this.cursor = this.limit - v_2;
            } else {
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
        if (!this.r_attached_pronoun()) {
            // empty if block
        }
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        int v_5 = this.limit - this.cursor;
        if (!this.r_standard_suffix()) {
            this.cursor = this.limit - v_5;
            if (!this.r_verb_suffix()) {
                // empty if block
            }
        }
        this.cursor = this.limit - v_4;
        int v_6 = this.limit - this.cursor;
        if (!this.r_vowel_suffix()) {
            // empty if block
        }
        this.cursor = this.limit - v_6;
        int v_7 = this.cursor = this.limit_backward;
        if (!this.r_postlude()) {
            // empty if block
        }
        this.cursor = v_7;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof ItalianStemmer;
    }

    public int hashCode() {
        return ItalianStemmer.class.getName().hashCode();
    }
}

