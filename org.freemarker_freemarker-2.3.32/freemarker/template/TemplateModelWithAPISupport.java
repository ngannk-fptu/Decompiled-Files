/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface TemplateModelWithAPISupport
extends TemplateModel {
    public TemplateModel getAPI() throws TemplateModelException;
}

