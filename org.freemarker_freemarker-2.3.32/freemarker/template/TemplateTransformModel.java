/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface TemplateTransformModel
extends TemplateModel {
    public Writer getWriter(Writer var1, Map var2) throws TemplateModelException, IOException;
}

