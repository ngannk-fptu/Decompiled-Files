/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

class PythonBlockFinishingInfo {
    String postscript;
    boolean generatedSwitch;
    boolean generatedAnIf;
    boolean needAnErrorClause;

    public PythonBlockFinishingInfo() {
        this.postscript = null;
        this.generatedSwitch = false;
        this.generatedSwitch = false;
        this.needAnErrorClause = true;
    }

    public PythonBlockFinishingInfo(String string, boolean bl, boolean bl2, boolean bl3) {
        this.postscript = string;
        this.generatedSwitch = bl;
        this.generatedAnIf = bl2;
        this.needAnErrorClause = bl3;
    }
}

