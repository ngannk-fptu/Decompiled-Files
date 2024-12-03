/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.Writer;

public interface TemplateDirectiveBody {
    public void render(Writer var1) throws TemplateException, IOException;
}

