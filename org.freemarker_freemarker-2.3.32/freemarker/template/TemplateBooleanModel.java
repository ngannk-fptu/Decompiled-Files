/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.FalseTemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TrueTemplateBooleanModel;

public interface TemplateBooleanModel
extends TemplateModel {
    public static final TemplateBooleanModel FALSE = new FalseTemplateBooleanModel();
    public static final TemplateBooleanModel TRUE = new TrueTemplateBooleanModel();

    public boolean getAsBoolean() throws TemplateModelException;
}

