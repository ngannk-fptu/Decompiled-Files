/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.block;

import org.jfree.chart.block.EntityBlockParams;

public class BlockParams
implements EntityBlockParams {
    private boolean generateEntities = false;
    private double translateX = 0.0;
    private double translateY = 0.0;

    public boolean getGenerateEntities() {
        return this.generateEntities;
    }

    public void setGenerateEntities(boolean generate) {
        this.generateEntities = generate;
    }

    public double getTranslateX() {
        return this.translateX;
    }

    public void setTranslateX(double x) {
        this.translateX = x;
    }

    public double getTranslateY() {
        return this.translateY;
    }

    public void setTranslateY(double y) {
        this.translateY = y;
    }
}

