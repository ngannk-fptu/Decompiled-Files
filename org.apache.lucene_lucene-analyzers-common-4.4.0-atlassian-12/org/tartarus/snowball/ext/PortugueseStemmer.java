/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class PortugueseStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final PortugueseStemmer methodObject = new PortugueseStemmer();
    private static final Among[] a_0 = new Among[]{new Among("", -1, 3, "", methodObject), new Among("\u00e3", 0, 1, "", methodObject), new Among("\u00f5", 0, 2, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("", -1, 3, "", methodObject), new Among("a~", 0, 1, "", methodObject), new Among("o~", 0, 2, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("ic", -1, -1, "", methodObject), new Among("ad", -1, -1, "", methodObject), new Among("os", -1, -1, "", methodObject), new Among("iv", -1, 1, "", methodObject)};
    private static final Among[] a_3 = new Among[]{new Among("ante", -1, 1, "", methodObject), new Among("avel", -1, 1, "", methodObject), new Among("\u00edvel", -1, 1, "", methodObject)};
    private static final Among[] a_4 = new Among[]{new Among("ic", -1, 1, "", methodObject), new Among("abil", -1, 1, "", methodObject), new Among("iv", -1, 1, "", methodObject)};
    private static final Among[] a_5 = new Among[]{new Among("ica", -1, 1, "", methodObject), new Among("\u00e2ncia", -1, 1, "", methodObject), new Among("\u00eancia", -1, 4, "", methodObject), new Among("ira", -1, 9, "", methodObject), new Among("adora", -1, 1, "", methodObject), new Among("osa", -1, 1, "", methodObject), new Among("ista", -1, 1, "", methodObject), new Among("iva", -1, 8, "", methodObject), new Among("eza", -1, 1, "", methodObject), new Among("log\u00eda", -1, 2, "", methodObject), new Among("idade", -1, 7, "", methodObject), new Among("ante", -1, 1, "", methodObject), new Among("mente", -1, 6, "", methodObject), new Among("amente", 12, 5, "", methodObject), new Among("\u00e1vel", -1, 1, "", methodObject), new Among("\u00edvel", -1, 1, "", methodObject), new Among("uci\u00f3n", -1, 3, "", methodObject), new Among("ico", -1, 1, "", methodObject), new Among("ismo", -1, 1, "", methodObject), new Among("oso", -1, 1, "", methodObject), new Among("amento", -1, 1, "", methodObject), new Among("imento", -1, 1, "", methodObject), new Among("ivo", -1, 8, "", methodObject), new Among("a\u00e7a~o", -1, 1, "", methodObject), new Among("ador", -1, 1, "", methodObject), new Among("icas", -1, 1, "", methodObject), new Among("\u00eancias", -1, 4, "", methodObject), new Among("iras", -1, 9, "", methodObject), new Among("adoras", -1, 1, "", methodObject), new Among("osas", -1, 1, "", methodObject), new Among("istas", -1, 1, "", methodObject), new Among("ivas", -1, 8, "", methodObject), new Among("ezas", -1, 1, "", methodObject), new Among("log\u00edas", -1, 2, "", methodObject), new Among("idades", -1, 7, "", methodObject), new Among("uciones", -1, 3, "", methodObject), new Among("adores", -1, 1, "", methodObject), new Among("antes", -1, 1, "", methodObject), new Among("a\u00e7o~es", -1, 1, "", methodObject), new Among("icos", -1, 1, "", methodObject), new Among("ismos", -1, 1, "", methodObject), new Among("osos", -1, 1, "", methodObject), new Among("amentos", -1, 1, "", methodObject), new Among("imentos", -1, 1, "", methodObject), new Among("ivos", -1, 8, "", methodObject)};
    private static final Among[] a_6 = new Among[]{new Among("ada", -1, 1, "", methodObject), new Among("ida", -1, 1, "", methodObject), new Among("ia", -1, 1, "", methodObject), new Among("aria", 2, 1, "", methodObject), new Among("eria", 2, 1, "", methodObject), new Among("iria", 2, 1, "", methodObject), new Among("ara", -1, 1, "", methodObject), new Among("era", -1, 1, "", methodObject), new Among("ira", -1, 1, "", methodObject), new Among("ava", -1, 1, "", methodObject), new Among("asse", -1, 1, "", methodObject), new Among("esse", -1, 1, "", methodObject), new Among("isse", -1, 1, "", methodObject), new Among("aste", -1, 1, "", methodObject), new Among("este", -1, 1, "", methodObject), new Among("iste", -1, 1, "", methodObject), new Among("ei", -1, 1, "", methodObject), new Among("arei", 16, 1, "", methodObject), new Among("erei", 16, 1, "", methodObject), new Among("irei", 16, 1, "", methodObject), new Among("am", -1, 1, "", methodObject), new Among("iam", 20, 1, "", methodObject), new Among("ariam", 21, 1, "", methodObject), new Among("eriam", 21, 1, "", methodObject), new Among("iriam", 21, 1, "", methodObject), new Among("aram", 20, 1, "", methodObject), new Among("eram", 20, 1, "", methodObject), new Among("iram", 20, 1, "", methodObject), new Among("avam", 20, 1, "", methodObject), new Among("em", -1, 1, "", methodObject), new Among("arem", 29, 1, "", methodObject), new Among("erem", 29, 1, "", methodObject), new Among("irem", 29, 1, "", methodObject), new Among("assem", 29, 1, "", methodObject), new Among("essem", 29, 1, "", methodObject), new Among("issem", 29, 1, "", methodObject), new Among("ado", -1, 1, "", methodObject), new Among("ido", -1, 1, "", methodObject), new Among("ando", -1, 1, "", methodObject), new Among("endo", -1, 1, "", methodObject), new Among("indo", -1, 1, "", methodObject), new Among("ara~o", -1, 1, "", methodObject), new Among("era~o", -1, 1, "", methodObject), new Among("ira~o", -1, 1, "", methodObject), new Among("ar", -1, 1, "", methodObject), new Among("er", -1, 1, "", methodObject), new Among("ir", -1, 1, "", methodObject), new Among("as", -1, 1, "", methodObject), new Among("adas", 47, 1, "", methodObject), new Among("idas", 47, 1, "", methodObject), new Among("ias", 47, 1, "", methodObject), new Among("arias", 50, 1, "", methodObject), new Among("erias", 50, 1, "", methodObject), new Among("irias", 50, 1, "", methodObject), new Among("aras", 47, 1, "", methodObject), new Among("eras", 47, 1, "", methodObject), new Among("iras", 47, 1, "", methodObject), new Among("avas", 47, 1, "", methodObject), new Among("es", -1, 1, "", methodObject), new Among("ardes", 58, 1, "", methodObject), new Among("erdes", 58, 1, "", methodObject), new Among("irdes", 58, 1, "", methodObject), new Among("ares", 58, 1, "", methodObject), new Among("eres", 58, 1, "", methodObject), new Among("ires", 58, 1, "", methodObject), new Among("asses", 58, 1, "", methodObject), new Among("esses", 58, 1, "", methodObject), new Among("isses", 58, 1, "", methodObject), new Among("astes", 58, 1, "", methodObject), new Among("estes", 58, 1, "", methodObject), new Among("istes", 58, 1, "", methodObject), new Among("is", -1, 1, "", methodObject), new Among("ais", 71, 1, "", methodObject), new Among("eis", 71, 1, "", methodObject), new Among("areis", 73, 1, "", methodObject), new Among("ereis", 73, 1, "", methodObject), new Among("ireis", 73, 1, "", methodObject), new Among("\u00e1reis", 73, 1, "", methodObject), new Among("\u00e9reis", 73, 1, "", methodObject), new Among("\u00edreis", 73, 1, "", methodObject), new Among("\u00e1sseis", 73, 1, "", methodObject), new Among("\u00e9sseis", 73, 1, "", methodObject), new Among("\u00edsseis", 73, 1, "", methodObject), new Among("\u00e1veis", 73, 1, "", methodObject), new Among("\u00edeis", 73, 1, "", methodObject), new Among("ar\u00edeis", 84, 1, "", methodObject), new Among("er\u00edeis", 84, 1, "", methodObject), new Among("ir\u00edeis", 84, 1, "", methodObject), new Among("ados", -1, 1, "", methodObject), new Among("idos", -1, 1, "", methodObject), new Among("amos", -1, 1, "", methodObject), new Among("\u00e1ramos", 90, 1, "", methodObject), new Among("\u00e9ramos", 90, 1, "", methodObject), new Among("\u00edramos", 90, 1, "", methodObject), new Among("\u00e1vamos", 90, 1, "", methodObject), new Among("\u00edamos", 90, 1, "", methodObject), new Among("ar\u00edamos", 95, 1, "", methodObject), new Among("er\u00edamos", 95, 1, "", methodObject), new Among("ir\u00edamos", 95, 1, "", methodObject), new Among("emos", -1, 1, "", methodObject), new Among("aremos", 99, 1, "", methodObject), new Among("eremos", 99, 1, "", methodObject), new Among("iremos", 99, 1, "", methodObject), new Among("\u00e1ssemos", 99, 1, "", methodObject), new Among("\u00eassemos", 99, 1, "", methodObject), new Among("\u00edssemos", 99, 1, "", methodObject), new Among("imos", -1, 1, "", methodObject), new Among("armos", -1, 1, "", methodObject), new Among("ermos", -1, 1, "", methodObject), new Among("irmos", -1, 1, "", methodObject), new Among("\u00e1mos", -1, 1, "", methodObject), new Among("ar\u00e1s", -1, 1, "", methodObject), new Among("er\u00e1s", -1, 1, "", methodObject), new Among("ir\u00e1s", -1, 1, "", methodObject), new Among("eu", -1, 1, "", methodObject), new Among("iu", -1, 1, "", methodObject), new Among("ou", -1, 1, "", methodObject), new Among("ar\u00e1", -1, 1, "", methodObject), new Among("er\u00e1", -1, 1, "", methodObject), new Among("ir\u00e1", -1, 1, "", methodObject)};
    private static final Among[] a_7 = new Among[]{new Among("a", -1, 1, "", methodObject), new Among("i", -1, 1, "", methodObject), new Among("o", -1, 1, "", methodObject), new Among("os", -1, 1, "", methodObject), new Among("\u00e1", -1, 1, "", methodObject), new Among("\u00ed", -1, 1, "", methodObject), new Among("\u00f3", -1, 1, "", methodObject)};
    private static final Among[] a_8 = new Among[]{new Among("e", -1, 1, "", methodObject), new Among("\u00e7", -1, 2, "", methodObject), new Among("\u00e9", -1, 1, "", methodObject), new Among("\u00ea", -1, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0003', '\u0013', '\f', '\u0002'};
    private int I_p2;
    private int I_p1;
    private int I_pV;

    private void copy_from(PortugueseStemmer other) {
        this.I_p2 = other.I_p2;
        this.I_p1 = other.I_p1;
        this.I_pV = other.I_pV;
        super.copy_from(other);
    }

    /*
     * Enabled aggressive block sorting
     */
    private boolean r_prelude() {
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
                    this.slice_from("a~");
                    break;
                }
                case 2: {
                    this.slice_from("o~");
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
                    this.slice_from("\u00e3");
                    break;
                }
                case 2: {
                    this.slice_from("\u00f5");
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

    private boolean r_standard_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_5, 45);
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
                this.slice_from("log");
                break;
            }
            case 3: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("u");
                break;
            }
            case 4: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_from("ente");
                break;
            }
            case 5: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                int v_1 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_2, 4);
                if (among_var == 0) {
                    this.cursor = this.limit - v_1;
                    break;
                }
                this.bra = this.cursor;
                if (!this.r_R2()) {
                    this.cursor = this.limit - v_1;
                    break;
                }
                this.slice_del();
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_1;
                        break;
                    }
                    case 1: {
                        this.ket = this.cursor;
                        if (!this.eq_s_b(2, "at")) {
                            this.cursor = this.limit - v_1;
                            break;
                        }
                        this.bra = this.cursor;
                        if (!this.r_R2()) {
                            this.cursor = this.limit - v_1;
                            break;
                        }
                        this.slice_del();
                    }
                }
                break;
            }
            case 6: {
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                int v_2 = this.limit - this.cursor;
                this.ket = this.cursor;
                among_var = this.find_among_b(a_3, 3);
                if (among_var == 0) {
                    this.cursor = this.limit - v_2;
                    break;
                }
                this.bra = this.cursor;
                switch (among_var) {
                    case 0: {
                        this.cursor = this.limit - v_2;
                        break;
                    }
                    case 1: {
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
                break;
            }
            case 9: {
                if (!this.r_RV()) {
                    return false;
                }
                if (!this.eq_s_b(1, "e")) {
                    return false;
                }
                this.slice_from("ir");
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
        int among_var = this.find_among_b(a_6, 120);
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

    private boolean r_residual_suffix() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_7, 7);
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
            }
        }
        return true;
    }

    /*
     * Unable to fully structure code
     */
    private boolean r_residual_form() {
        this.ket = this.cursor;
        among_var = this.find_among_b(PortugueseStemmer.a_8, 4);
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
                this.ket = this.cursor;
                v_1 = this.limit - this.cursor;
                if (!this.eq_s_b(1, "u")) ** GOTO lbl-1000
                this.bra = this.cursor;
                v_2 = this.limit - this.cursor;
                if (this.eq_s_b(1, "g")) {
                    this.cursor = this.limit - v_2;
                } else lbl-1000:
                // 2 sources

                {
                    this.cursor = this.limit - v_1;
                    if (!this.eq_s_b(1, "i")) {
                        return false;
                    }
                    this.bra = this.cursor;
                    v_3 = this.limit - this.cursor;
                    if (!this.eq_s_b(1, "c")) {
                        return false;
                    }
                    this.cursor = this.limit - v_3;
                }
                if (!this.r_RV()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 2: {
                this.slice_from("c");
            }
        }
        return true;
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public boolean stem() {
        v_1 = this.cursor;
        if (!this.r_prelude()) {
            // empty if block
        }
        v_2 = this.cursor = v_1;
        if (!this.r_mark_regions()) {
            // empty if block
        }
        this.limit_backward = this.cursor = v_2;
        this.cursor = this.limit;
        v_3 = this.limit - this.cursor;
        v_4 = this.limit - this.cursor;
        v_5 = this.limit - this.cursor;
        v_6 = this.limit - this.cursor;
        if (this.r_standard_suffix()) ** GOTO lbl-1000
        this.cursor = this.limit - v_6;
        if (this.r_verb_suffix()) lbl-1000:
        // 2 sources

        {
            this.cursor = this.limit - v_5;
            v_7 = this.limit - this.cursor;
            this.ket = this.cursor;
            if (this.eq_s_b(1, "i")) {
                this.bra = this.cursor;
                v_8 = this.limit - this.cursor;
                if (this.eq_s_b(1, "c")) {
                    this.cursor = this.limit - v_8;
                    if (this.r_RV()) {
                        this.slice_del();
                    }
                }
            }
            this.cursor = this.limit - v_7;
        } else {
            this.cursor = this.limit - v_4;
            if (!this.r_residual_suffix()) {
                // empty if block
            }
        }
        this.cursor = this.limit - v_3;
        v_9 = this.limit - this.cursor;
        if (!this.r_residual_form()) {
            // empty if block
        }
        this.cursor = this.limit - v_9;
        v_10 = this.cursor = this.limit_backward;
        if (!this.r_postlude()) {
            // empty if block
        }
        this.cursor = v_10;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof PortugueseStemmer;
    }

    public int hashCode() {
        return PortugueseStemmer.class.getName().hashCode();
    }
}

