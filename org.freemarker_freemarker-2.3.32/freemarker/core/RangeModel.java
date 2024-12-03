/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._TemplateModelException;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import java.io.Serializable;

abstract class RangeModel
implements TemplateSequenceModel,
Serializable {
    private final int begin;

    public RangeModel(int begin) {
        this.begin = begin;
    }

    final int getBegining() {
        return this.begin;
    }

    @Override
    public final TemplateModel get(int index) throws TemplateModelException {
        if (index < 0 || index >= this.size()) {
            throw new _TemplateModelException("Range item index ", index, " is out of bounds.");
        }
        long value = (long)this.begin + (long)this.getStep() * (long)index;
        return value <= Integer.MAX_VALUE ? new SimpleNumber((int)value) : new SimpleNumber(value);
    }

    abstract int getStep();

    abstract boolean isRightUnbounded();

    abstract boolean isRightAdaptive();

    abstract boolean isAffectedByStringSlicingBug();
}

