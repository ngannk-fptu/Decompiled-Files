/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.afm;

public class CompositePart {
    private String name;
    private int xDisplacement;
    private int yDisplacement;

    public String getName() {
        return this.name;
    }

    public void setName(String nameValue) {
        this.name = nameValue;
    }

    public int getXDisplacement() {
        return this.xDisplacement;
    }

    public void setXDisplacement(int xDisp) {
        this.xDisplacement = xDisp;
    }

    public int getYDisplacement() {
        return this.yDisplacement;
    }

    public void setYDisplacement(int yDisp) {
        this.yDisplacement = yDisp;
    }
}

