/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.RightUnboundedRangeModel;
import freemarker.template.TemplateModelException;

final class NonListableRightUnboundedRangeModel
extends RightUnboundedRangeModel {
    NonListableRightUnboundedRangeModel(int begin) {
        super(begin);
    }

    @Override
    public int size() throws TemplateModelException {
        return 0;
    }
}

