/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.SerializableTemplateBooleanModel;

final class TrueTemplateBooleanModel
implements SerializableTemplateBooleanModel {
    TrueTemplateBooleanModel() {
    }

    @Override
    public boolean getAsBoolean() {
        return true;
    }

    private Object readResolve() {
        return TRUE;
    }
}

