/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.RangeModel;

final class BoundedRangeModel
extends RangeModel {
    private final int step;
    private final int size;
    private final boolean rightAdaptive;
    private final boolean affectedByStringSlicingBug;

    BoundedRangeModel(int begin, int end, boolean inclusiveEnd, boolean rightAdaptive) {
        super(begin);
        this.step = begin <= end ? 1 : -1;
        this.size = Math.abs(end - begin) + (inclusiveEnd ? 1 : 0);
        this.rightAdaptive = rightAdaptive;
        this.affectedByStringSlicingBug = inclusiveEnd;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    int getStep() {
        return this.step;
    }

    @Override
    boolean isRightUnbounded() {
        return false;
    }

    @Override
    boolean isRightAdaptive() {
        return this.rightAdaptive;
    }

    @Override
    boolean isAffectedByStringSlicingBug() {
        return this.affectedByStringSlicingBug;
    }
}

