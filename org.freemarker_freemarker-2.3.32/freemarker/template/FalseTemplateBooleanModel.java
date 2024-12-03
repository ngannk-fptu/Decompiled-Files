/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.SerializableTemplateBooleanModel;

final class FalseTemplateBooleanModel
implements SerializableTemplateBooleanModel {
    FalseTemplateBooleanModel() {
    }

    @Override
    public boolean getAsBoolean() {
        return false;
    }

    private Object readResolve() {
        return FALSE;
    }
}

