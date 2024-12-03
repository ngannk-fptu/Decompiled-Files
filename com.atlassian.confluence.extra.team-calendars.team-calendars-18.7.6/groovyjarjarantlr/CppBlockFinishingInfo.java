/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

class CppBlockFinishingInfo {
    String postscript;
    boolean generatedSwitch;
    boolean generatedAnIf;
    boolean needAnErrorClause;

    public CppBlockFinishingInfo() {
        this.postscript = null;
        this.generatedSwitch = false;
        this.needAnErrorClause = true;
    }

    public CppBlockFinishingInfo(String string, boolean bl, boolean bl2, boolean bl3) {
        this.postscript = string;
        this.generatedSwitch = bl;
        this.generatedAnIf = bl2;
        this.needAnErrorClause = bl3;
    }
}

