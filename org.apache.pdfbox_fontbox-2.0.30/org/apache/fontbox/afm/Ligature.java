/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.afm;

public class Ligature {
    private String successor;
    private String ligature;

    public String getLigature() {
        return this.ligature;
    }

    public void setLigature(String lig) {
        this.ligature = lig;
    }

    public String getSuccessor() {
        return this.successor;
    }

    public void setSuccessor(String successorValue) {
        this.successor = successorValue;
    }
}

