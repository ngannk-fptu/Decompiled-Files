/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateBooleanModel;

public class BooleanModel
extends BeanModel
implements TemplateBooleanModel {
    private final boolean value;

    public BooleanModel(Boolean bool, BeansWrapper wrapper) {
        super(bool, wrapper, false);
        this.value = bool;
    }

    @Override
    public boolean getAsBoolean() {
        return this.value;
    }
}

