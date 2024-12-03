/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.image;

import java.awt.image.ReplicateScaleFilter;

public class SubsamplingFilter
extends ReplicateScaleFilter {
    private int xSub;
    private int ySub;

    public SubsamplingFilter(int n, int n2) {
        super(1, 1);
        if (n < 1 || n2 < 1) {
            throw new IllegalArgumentException("Subsampling factors must be positive.");
        }
        this.xSub = n;
        this.ySub = n2;
    }

    @Override
    public void setDimensions(int n, int n2) {
        this.destWidth = (n + this.xSub - 1) / this.xSub;
        this.destHeight = (n2 + this.ySub - 1) / this.ySub;
        super.setDimensions(n, n2);
    }
}

