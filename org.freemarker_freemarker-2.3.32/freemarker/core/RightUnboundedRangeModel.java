/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.RangeModel;

abstract class RightUnboundedRangeModel
extends RangeModel {
    RightUnboundedRangeModel(int begin) {
        super(begin);
    }

    @Override
    final int getStep() {
        return 1;
    }

    @Override
    final boolean isRightUnbounded() {
        return true;
    }

    @Override
    final boolean isRightAdaptive() {
        return true;
    }

    @Override
    final boolean isAffectedByStringSlicingBug() {
        return false;
    }
}

