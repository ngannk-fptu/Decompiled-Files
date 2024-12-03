/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.MarkupOutputFormat;
import freemarker.template.TemplateModel;

public interface TemplateMarkupOutputModel<MO extends TemplateMarkupOutputModel<MO>>
extends TemplateModel {
    public MarkupOutputFormat<MO> getOutputFormat();
}

