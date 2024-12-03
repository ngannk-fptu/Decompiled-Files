/*
 * Decompiled with CFR 0.152.
 */
package antlr;

class JavaBlockFinishingInfo {
    String postscript;
    boolean generatedSwitch;
    boolean generatedAnIf;
    boolean needAnErrorClause;

    public JavaBlockFinishingInfo() {
        this.postscript = null;
        this.generatedSwitch = false;
        this.generatedSwitch = false;
        this.needAnErrorClause = true;
    }

    public JavaBlockFinishingInfo(String string, boolean bl, boolean bl2, boolean bl3) {
        this.postscript = string;
        this.generatedSwitch = bl;
        this.generatedAnIf = bl2;
        this.needAnErrorClause = bl3;
    }
}

