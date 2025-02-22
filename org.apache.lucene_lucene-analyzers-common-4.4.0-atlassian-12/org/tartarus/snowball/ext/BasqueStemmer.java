/*
 * Decompiled with CFR 0.152.
 */
package org.tartarus.snowball.ext;

import org.tartarus.snowball.Among;
import org.tartarus.snowball.SnowballProgram;

public class BasqueStemmer
extends SnowballProgram {
    private static final long serialVersionUID = 1L;
    private static final BasqueStemmer methodObject = new BasqueStemmer();
    private static final Among[] a_0 = new Among[]{new Among("idea", -1, 1, "", methodObject), new Among("bidea", 0, 1, "", methodObject), new Among("kidea", 0, 1, "", methodObject), new Among("pidea", 0, 1, "", methodObject), new Among("kundea", -1, 1, "", methodObject), new Among("galea", -1, 1, "", methodObject), new Among("tailea", -1, 1, "", methodObject), new Among("tzailea", -1, 1, "", methodObject), new Among("gunea", -1, 1, "", methodObject), new Among("kunea", -1, 1, "", methodObject), new Among("tzaga", -1, 1, "", methodObject), new Among("gaia", -1, 1, "", methodObject), new Among("aldia", -1, 1, "", methodObject), new Among("taldia", 12, 1, "", methodObject), new Among("karia", -1, 1, "", methodObject), new Among("garria", -1, 2, "", methodObject), new Among("karria", -1, 1, "", methodObject), new Among("ka", -1, 1, "", methodObject), new Among("tzaka", 17, 1, "", methodObject), new Among("la", -1, 1, "", methodObject), new Among("mena", -1, 1, "", methodObject), new Among("pena", -1, 1, "", methodObject), new Among("kina", -1, 1, "", methodObject), new Among("ezina", -1, 1, "", methodObject), new Among("tezina", 23, 1, "", methodObject), new Among("kuna", -1, 1, "", methodObject), new Among("tuna", -1, 1, "", methodObject), new Among("kizuna", -1, 1, "", methodObject), new Among("era", -1, 1, "", methodObject), new Among("bera", 28, 1, "", methodObject), new Among("arabera", 29, 4, "", methodObject), new Among("kera", 28, 1, "", methodObject), new Among("pera", 28, 1, "", methodObject), new Among("orra", -1, 1, "", methodObject), new Among("korra", 33, 1, "", methodObject), new Among("dura", -1, 1, "", methodObject), new Among("gura", -1, 1, "", methodObject), new Among("kura", -1, 1, "", methodObject), new Among("tura", -1, 1, "", methodObject), new Among("eta", -1, 1, "", methodObject), new Among("keta", 39, 1, "", methodObject), new Among("gailua", -1, 1, "", methodObject), new Among("eza", -1, 1, "", methodObject), new Among("erreza", 42, 1, "", methodObject), new Among("tza", -1, 2, "", methodObject), new Among("gaitza", 44, 1, "", methodObject), new Among("kaitza", 44, 1, "", methodObject), new Among("kuntza", 44, 1, "", methodObject), new Among("ide", -1, 1, "", methodObject), new Among("bide", 48, 1, "", methodObject), new Among("kide", 48, 1, "", methodObject), new Among("pide", 48, 1, "", methodObject), new Among("kunde", -1, 1, "", methodObject), new Among("tzake", -1, 1, "", methodObject), new Among("tzeke", -1, 1, "", methodObject), new Among("le", -1, 1, "", methodObject), new Among("gale", 55, 1, "", methodObject), new Among("taile", 55, 1, "", methodObject), new Among("tzaile", 55, 1, "", methodObject), new Among("gune", -1, 1, "", methodObject), new Among("kune", -1, 1, "", methodObject), new Among("tze", -1, 1, "", methodObject), new Among("atze", 61, 1, "", methodObject), new Among("gai", -1, 1, "", methodObject), new Among("aldi", -1, 1, "", methodObject), new Among("taldi", 64, 1, "", methodObject), new Among("ki", -1, 1, "", methodObject), new Among("ari", -1, 1, "", methodObject), new Among("kari", 67, 1, "", methodObject), new Among("lari", 67, 1, "", methodObject), new Among("tari", 67, 1, "", methodObject), new Among("etari", 70, 1, "", methodObject), new Among("garri", -1, 2, "", methodObject), new Among("karri", -1, 1, "", methodObject), new Among("arazi", -1, 1, "", methodObject), new Among("tarazi", 74, 1, "", methodObject), new Among("an", -1, 1, "", methodObject), new Among("ean", 76, 1, "", methodObject), new Among("rean", 77, 1, "", methodObject), new Among("kan", 76, 1, "", methodObject), new Among("etan", 76, 1, "", methodObject), new Among("atseden", -1, 3, "", methodObject), new Among("men", -1, 1, "", methodObject), new Among("pen", -1, 1, "", methodObject), new Among("kin", -1, 1, "", methodObject), new Among("rekin", 84, 1, "", methodObject), new Among("ezin", -1, 1, "", methodObject), new Among("tezin", 86, 1, "", methodObject), new Among("tun", -1, 1, "", methodObject), new Among("kizun", -1, 1, "", methodObject), new Among("go", -1, 1, "", methodObject), new Among("ago", 90, 1, "", methodObject), new Among("tio", -1, 1, "", methodObject), new Among("dako", -1, 1, "", methodObject), new Among("or", -1, 1, "", methodObject), new Among("kor", 94, 1, "", methodObject), new Among("tzat", -1, 1, "", methodObject), new Among("du", -1, 1, "", methodObject), new Among("gailu", -1, 1, "", methodObject), new Among("tu", -1, 1, "", methodObject), new Among("atu", 99, 1, "", methodObject), new Among("aldatu", 100, 1, "", methodObject), new Among("tatu", 100, 1, "", methodObject), new Among("baditu", 99, 5, "", methodObject), new Among("ez", -1, 1, "", methodObject), new Among("errez", 104, 1, "", methodObject), new Among("tzez", 104, 1, "", methodObject), new Among("gaitz", -1, 1, "", methodObject), new Among("kaitz", -1, 1, "", methodObject)};
    private static final Among[] a_1 = new Among[]{new Among("ada", -1, 1, "", methodObject), new Among("kada", 0, 1, "", methodObject), new Among("anda", -1, 1, "", methodObject), new Among("denda", -1, 1, "", methodObject), new Among("gabea", -1, 1, "", methodObject), new Among("kabea", -1, 1, "", methodObject), new Among("aldea", -1, 1, "", methodObject), new Among("kaldea", 6, 1, "", methodObject), new Among("taldea", 6, 1, "", methodObject), new Among("ordea", -1, 1, "", methodObject), new Among("zalea", -1, 1, "", methodObject), new Among("tzalea", 10, 1, "", methodObject), new Among("gilea", -1, 1, "", methodObject), new Among("emea", -1, 1, "", methodObject), new Among("kumea", -1, 1, "", methodObject), new Among("nea", -1, 1, "", methodObject), new Among("enea", 15, 1, "", methodObject), new Among("zionea", 15, 1, "", methodObject), new Among("unea", 15, 1, "", methodObject), new Among("gunea", 18, 1, "", methodObject), new Among("pea", -1, 1, "", methodObject), new Among("aurrea", -1, 1, "", methodObject), new Among("tea", -1, 1, "", methodObject), new Among("kotea", 22, 1, "", methodObject), new Among("artea", 22, 1, "", methodObject), new Among("ostea", 22, 1, "", methodObject), new Among("etxea", -1, 1, "", methodObject), new Among("ga", -1, 1, "", methodObject), new Among("anga", 27, 1, "", methodObject), new Among("gaia", -1, 1, "", methodObject), new Among("aldia", -1, 1, "", methodObject), new Among("taldia", 30, 1, "", methodObject), new Among("handia", -1, 1, "", methodObject), new Among("mendia", -1, 1, "", methodObject), new Among("geia", -1, 1, "", methodObject), new Among("egia", -1, 1, "", methodObject), new Among("degia", 35, 1, "", methodObject), new Among("tegia", 35, 1, "", methodObject), new Among("nahia", -1, 1, "", methodObject), new Among("ohia", -1, 1, "", methodObject), new Among("kia", -1, 1, "", methodObject), new Among("tokia", 40, 1, "", methodObject), new Among("oia", -1, 1, "", methodObject), new Among("koia", 42, 1, "", methodObject), new Among("aria", -1, 1, "", methodObject), new Among("karia", 44, 1, "", methodObject), new Among("laria", 44, 1, "", methodObject), new Among("taria", 44, 1, "", methodObject), new Among("eria", -1, 1, "", methodObject), new Among("keria", 48, 1, "", methodObject), new Among("teria", 48, 1, "", methodObject), new Among("garria", -1, 2, "", methodObject), new Among("larria", -1, 1, "", methodObject), new Among("kirria", -1, 1, "", methodObject), new Among("duria", -1, 1, "", methodObject), new Among("asia", -1, 1, "", methodObject), new Among("tia", -1, 1, "", methodObject), new Among("ezia", -1, 1, "", methodObject), new Among("bizia", -1, 1, "", methodObject), new Among("ontzia", -1, 1, "", methodObject), new Among("ka", -1, 1, "", methodObject), new Among("joka", 60, 3, "", methodObject), new Among("aurka", 60, 10, "", methodObject), new Among("ska", 60, 1, "", methodObject), new Among("xka", 60, 1, "", methodObject), new Among("zka", 60, 1, "", methodObject), new Among("gibela", -1, 1, "", methodObject), new Among("gela", -1, 1, "", methodObject), new Among("kaila", -1, 1, "", methodObject), new Among("skila", -1, 1, "", methodObject), new Among("tila", -1, 1, "", methodObject), new Among("ola", -1, 1, "", methodObject), new Among("na", -1, 1, "", methodObject), new Among("kana", 72, 1, "", methodObject), new Among("ena", 72, 1, "", methodObject), new Among("garrena", 74, 1, "", methodObject), new Among("gerrena", 74, 1, "", methodObject), new Among("urrena", 74, 1, "", methodObject), new Among("zaina", 72, 1, "", methodObject), new Among("tzaina", 78, 1, "", methodObject), new Among("kina", 72, 1, "", methodObject), new Among("mina", 72, 1, "", methodObject), new Among("garna", 72, 1, "", methodObject), new Among("una", 72, 1, "", methodObject), new Among("duna", 83, 1, "", methodObject), new Among("asuna", 83, 1, "", methodObject), new Among("tasuna", 85, 1, "", methodObject), new Among("ondoa", -1, 1, "", methodObject), new Among("kondoa", 87, 1, "", methodObject), new Among("ngoa", -1, 1, "", methodObject), new Among("zioa", -1, 1, "", methodObject), new Among("koa", -1, 1, "", methodObject), new Among("takoa", 91, 1, "", methodObject), new Among("zkoa", 91, 1, "", methodObject), new Among("noa", -1, 1, "", methodObject), new Among("zinoa", 94, 1, "", methodObject), new Among("aroa", -1, 1, "", methodObject), new Among("taroa", 96, 1, "", methodObject), new Among("zaroa", 96, 1, "", methodObject), new Among("eroa", -1, 1, "", methodObject), new Among("oroa", -1, 1, "", methodObject), new Among("osoa", -1, 1, "", methodObject), new Among("toa", -1, 1, "", methodObject), new Among("ttoa", 102, 1, "", methodObject), new Among("ztoa", 102, 1, "", methodObject), new Among("txoa", -1, 1, "", methodObject), new Among("tzoa", -1, 1, "", methodObject), new Among("\u00f1oa", -1, 1, "", methodObject), new Among("ra", -1, 1, "", methodObject), new Among("ara", 108, 1, "", methodObject), new Among("dara", 109, 1, "", methodObject), new Among("liara", 109, 1, "", methodObject), new Among("tiara", 109, 1, "", methodObject), new Among("tara", 109, 1, "", methodObject), new Among("etara", 113, 1, "", methodObject), new Among("tzara", 109, 1, "", methodObject), new Among("bera", 108, 1, "", methodObject), new Among("kera", 108, 1, "", methodObject), new Among("pera", 108, 1, "", methodObject), new Among("ora", 108, 2, "", methodObject), new Among("tzarra", 108, 1, "", methodObject), new Among("korra", 108, 1, "", methodObject), new Among("tra", 108, 1, "", methodObject), new Among("sa", -1, 1, "", methodObject), new Among("osa", 123, 1, "", methodObject), new Among("ta", -1, 1, "", methodObject), new Among("eta", 125, 1, "", methodObject), new Among("keta", 126, 1, "", methodObject), new Among("sta", 125, 1, "", methodObject), new Among("dua", -1, 1, "", methodObject), new Among("mendua", 129, 1, "", methodObject), new Among("ordua", 129, 1, "", methodObject), new Among("lekua", -1, 1, "", methodObject), new Among("burua", -1, 1, "", methodObject), new Among("durua", -1, 1, "", methodObject), new Among("tsua", -1, 1, "", methodObject), new Among("tua", -1, 1, "", methodObject), new Among("mentua", 136, 1, "", methodObject), new Among("estua", 136, 1, "", methodObject), new Among("txua", -1, 1, "", methodObject), new Among("zua", -1, 1, "", methodObject), new Among("tzua", 140, 1, "", methodObject), new Among("za", -1, 1, "", methodObject), new Among("eza", 142, 1, "", methodObject), new Among("eroza", 142, 1, "", methodObject), new Among("tza", 142, 2, "", methodObject), new Among("koitza", 145, 1, "", methodObject), new Among("antza", 145, 1, "", methodObject), new Among("gintza", 145, 1, "", methodObject), new Among("kintza", 145, 1, "", methodObject), new Among("kuntza", 145, 1, "", methodObject), new Among("gabe", -1, 1, "", methodObject), new Among("kabe", -1, 1, "", methodObject), new Among("kide", -1, 1, "", methodObject), new Among("alde", -1, 1, "", methodObject), new Among("kalde", 154, 1, "", methodObject), new Among("talde", 154, 1, "", methodObject), new Among("orde", -1, 1, "", methodObject), new Among("ge", -1, 1, "", methodObject), new Among("zale", -1, 1, "", methodObject), new Among("tzale", 159, 1, "", methodObject), new Among("gile", -1, 1, "", methodObject), new Among("eme", -1, 1, "", methodObject), new Among("kume", -1, 1, "", methodObject), new Among("ne", -1, 1, "", methodObject), new Among("zione", 164, 1, "", methodObject), new Among("une", 164, 1, "", methodObject), new Among("gune", 166, 1, "", methodObject), new Among("pe", -1, 1, "", methodObject), new Among("aurre", -1, 1, "", methodObject), new Among("te", -1, 1, "", methodObject), new Among("kote", 170, 1, "", methodObject), new Among("arte", 170, 1, "", methodObject), new Among("oste", 170, 1, "", methodObject), new Among("etxe", -1, 1, "", methodObject), new Among("gai", -1, 1, "", methodObject), new Among("di", -1, 1, "", methodObject), new Among("aldi", 176, 1, "", methodObject), new Among("taldi", 177, 1, "", methodObject), new Among("geldi", 176, 8, "", methodObject), new Among("handi", 176, 1, "", methodObject), new Among("mendi", 176, 1, "", methodObject), new Among("gei", -1, 1, "", methodObject), new Among("egi", -1, 1, "", methodObject), new Among("degi", 183, 1, "", methodObject), new Among("tegi", 183, 1, "", methodObject), new Among("nahi", -1, 1, "", methodObject), new Among("ohi", -1, 1, "", methodObject), new Among("ki", -1, 1, "", methodObject), new Among("toki", 188, 1, "", methodObject), new Among("oi", -1, 1, "", methodObject), new Among("goi", 190, 1, "", methodObject), new Among("koi", 190, 1, "", methodObject), new Among("ari", -1, 1, "", methodObject), new Among("kari", 193, 1, "", methodObject), new Among("lari", 193, 1, "", methodObject), new Among("tari", 193, 1, "", methodObject), new Among("garri", -1, 2, "", methodObject), new Among("larri", -1, 1, "", methodObject), new Among("kirri", -1, 1, "", methodObject), new Among("duri", -1, 1, "", methodObject), new Among("asi", -1, 1, "", methodObject), new Among("ti", -1, 1, "", methodObject), new Among("ontzi", -1, 1, "", methodObject), new Among("\u00f1i", -1, 1, "", methodObject), new Among("ak", -1, 1, "", methodObject), new Among("ek", -1, 1, "", methodObject), new Among("tarik", -1, 1, "", methodObject), new Among("gibel", -1, 1, "", methodObject), new Among("ail", -1, 1, "", methodObject), new Among("kail", 209, 1, "", methodObject), new Among("kan", -1, 1, "", methodObject), new Among("tan", -1, 1, "", methodObject), new Among("etan", 212, 1, "", methodObject), new Among("en", -1, 4, "", methodObject), new Among("ren", 214, 2, "", methodObject), new Among("garren", 215, 1, "", methodObject), new Among("gerren", 215, 1, "", methodObject), new Among("urren", 215, 1, "", methodObject), new Among("ten", 214, 4, "", methodObject), new Among("tzen", 214, 4, "", methodObject), new Among("zain", -1, 1, "", methodObject), new Among("tzain", 221, 1, "", methodObject), new Among("kin", -1, 1, "", methodObject), new Among("min", -1, 1, "", methodObject), new Among("dun", -1, 1, "", methodObject), new Among("asun", -1, 1, "", methodObject), new Among("tasun", 226, 1, "", methodObject), new Among("aizun", -1, 1, "", methodObject), new Among("ondo", -1, 1, "", methodObject), new Among("kondo", 229, 1, "", methodObject), new Among("go", -1, 1, "", methodObject), new Among("ngo", 231, 1, "", methodObject), new Among("zio", -1, 1, "", methodObject), new Among("ko", -1, 1, "", methodObject), new Among("trako", 234, 5, "", methodObject), new Among("tako", 234, 1, "", methodObject), new Among("etako", 236, 1, "", methodObject), new Among("eko", 234, 1, "", methodObject), new Among("tariko", 234, 1, "", methodObject), new Among("sko", 234, 1, "", methodObject), new Among("tuko", 234, 1, "", methodObject), new Among("minutuko", 241, 6, "", methodObject), new Among("zko", 234, 1, "", methodObject), new Among("no", -1, 1, "", methodObject), new Among("zino", 244, 1, "", methodObject), new Among("ro", -1, 1, "", methodObject), new Among("aro", 246, 1, "", methodObject), new Among("igaro", 247, 9, "", methodObject), new Among("taro", 247, 1, "", methodObject), new Among("zaro", 247, 1, "", methodObject), new Among("ero", 246, 1, "", methodObject), new Among("giro", 246, 1, "", methodObject), new Among("oro", 246, 1, "", methodObject), new Among("oso", -1, 1, "", methodObject), new Among("to", -1, 1, "", methodObject), new Among("tto", 255, 1, "", methodObject), new Among("zto", 255, 1, "", methodObject), new Among("txo", -1, 1, "", methodObject), new Among("tzo", -1, 1, "", methodObject), new Among("gintzo", 259, 1, "", methodObject), new Among("\u00f1o", -1, 1, "", methodObject), new Among("zp", -1, 1, "", methodObject), new Among("ar", -1, 1, "", methodObject), new Among("dar", 263, 1, "", methodObject), new Among("behar", 263, 1, "", methodObject), new Among("zehar", 263, 7, "", methodObject), new Among("liar", 263, 1, "", methodObject), new Among("tiar", 263, 1, "", methodObject), new Among("tar", 263, 1, "", methodObject), new Among("tzar", 263, 1, "", methodObject), new Among("or", -1, 2, "", methodObject), new Among("kor", 271, 1, "", methodObject), new Among("os", -1, 1, "", methodObject), new Among("ket", -1, 1, "", methodObject), new Among("du", -1, 1, "", methodObject), new Among("mendu", 275, 1, "", methodObject), new Among("ordu", 275, 1, "", methodObject), new Among("leku", -1, 1, "", methodObject), new Among("buru", -1, 2, "", methodObject), new Among("duru", -1, 1, "", methodObject), new Among("tsu", -1, 1, "", methodObject), new Among("tu", -1, 1, "", methodObject), new Among("tatu", 282, 4, "", methodObject), new Among("mentu", 282, 1, "", methodObject), new Among("estu", 282, 1, "", methodObject), new Among("txu", -1, 1, "", methodObject), new Among("zu", -1, 1, "", methodObject), new Among("tzu", 287, 1, "", methodObject), new Among("gintzu", 288, 1, "", methodObject), new Among("z", -1, 1, "", methodObject), new Among("ez", 290, 1, "", methodObject), new Among("eroz", 290, 1, "", methodObject), new Among("tz", 290, 1, "", methodObject), new Among("koitz", 293, 1, "", methodObject)};
    private static final Among[] a_2 = new Among[]{new Among("zlea", -1, 2, "", methodObject), new Among("keria", -1, 1, "", methodObject), new Among("la", -1, 1, "", methodObject), new Among("era", -1, 1, "", methodObject), new Among("dade", -1, 1, "", methodObject), new Among("tade", -1, 1, "", methodObject), new Among("date", -1, 1, "", methodObject), new Among("tate", -1, 1, "", methodObject), new Among("gi", -1, 1, "", methodObject), new Among("ki", -1, 1, "", methodObject), new Among("ik", -1, 1, "", methodObject), new Among("lanik", 10, 1, "", methodObject), new Among("rik", 10, 1, "", methodObject), new Among("larik", 12, 1, "", methodObject), new Among("ztik", 10, 1, "", methodObject), new Among("go", -1, 1, "", methodObject), new Among("ro", -1, 1, "", methodObject), new Among("ero", 16, 1, "", methodObject), new Among("to", -1, 1, "", methodObject)};
    private static final char[] g_v = new char[]{'\u0011', 'A', '\u0010'};
    private int I_p2;
    private int I_p1;
    private int I_pV;

    private void copy_from(BasqueStemmer other) {
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

    private boolean r_RV() {
        return this.I_pV <= this.cursor;
    }

    private boolean r_R2() {
        return this.I_p2 <= this.cursor;
    }

    private boolean r_R1() {
        return this.I_p1 <= this.cursor;
    }

    private boolean r_aditzak() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_0, 109);
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
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_from("atseden");
                break;
            }
            case 4: {
                this.slice_from("arabera");
                break;
            }
            case 5: {
                this.slice_from("baditu");
            }
        }
        return true;
    }

    private boolean r_izenak() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_1, 295);
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
                if (!this.r_R2()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 3: {
                this.slice_from("jok");
                break;
            }
            case 4: {
                if (!this.r_R1()) {
                    return false;
                }
                this.slice_del();
                break;
            }
            case 5: {
                this.slice_from("tra");
                break;
            }
            case 6: {
                this.slice_from("minutu");
                break;
            }
            case 7: {
                this.slice_from("zehar");
                break;
            }
            case 8: {
                this.slice_from("geldi");
                break;
            }
            case 9: {
                this.slice_from("igaro");
                break;
            }
            case 10: {
                this.slice_from("aurka");
            }
        }
        return true;
    }

    private boolean r_adjetiboak() {
        this.ket = this.cursor;
        int among_var = this.find_among_b(a_2, 19);
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
                this.slice_from("z");
            }
        }
        return true;
    }

    @Override
    public boolean stem() {
        int v_3;
        int v_2;
        int v_1 = this.cursor;
        if (!this.r_mark_regions()) {
            // empty if block
        }
        this.limit_backward = this.cursor = v_1;
        this.cursor = this.limit;
        do {
            v_2 = this.limit - this.cursor;
        } while (this.r_aditzak());
        this.cursor = this.limit - v_2;
        do {
            v_3 = this.limit - this.cursor;
        } while (this.r_izenak());
        this.cursor = this.limit - v_3;
        int v_4 = this.limit - this.cursor;
        if (!this.r_adjetiboak()) {
            // empty if block
        }
        this.cursor = this.limit - v_4;
        this.cursor = this.limit_backward;
        return true;
    }

    public boolean equals(Object o) {
        return o instanceof BasqueStemmer;
    }

    public int hashCode() {
        return BasqueStemmer.class.getName().hashCode();
    }
}

