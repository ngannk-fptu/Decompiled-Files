/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;

@Deprecated
public interface TemplateMethodModel
extends TemplateModel {
    public Object exec(List var1) throws TemplateModelException;
}

