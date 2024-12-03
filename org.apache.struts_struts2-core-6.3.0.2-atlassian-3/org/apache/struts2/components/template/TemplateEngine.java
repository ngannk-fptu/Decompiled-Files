/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components.template;

import java.util.Map;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateRenderingContext;

public interface TemplateEngine {
    public void renderTemplate(TemplateRenderingContext var1) throws Exception;

    public Map getThemeProps(Template var1);
}

