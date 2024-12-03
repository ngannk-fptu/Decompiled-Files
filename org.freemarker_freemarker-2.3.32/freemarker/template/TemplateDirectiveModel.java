/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.io.IOException;
import java.util.Map;

public interface TemplateDirectiveModel
extends TemplateModel {
    public void execute(Environment var1, Map var2, TemplateModel[] var3, TemplateDirectiveBody var4) throws TemplateException, IOException;
}

