/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.plugin.services;

import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public interface VelocityHelperService {
    public String getRenderedTemplate(String var1, Map<String, Object> var2);

    public String getRenderedTemplateWithoutSwallowingErrors(String var1, Map<String, Object> var2) throws Exception;

    default public String getRenderedContent(String templateContent, Map<String, Object> context) {
        return this.getRenderedContent(templateContent, (Context)new VelocityContext(context));
    }

    public String getRenderedContent(String var1, Context var2);

    public String getRenderedContentWithoutSwallowingErrors(String var1, Context var2) throws Exception;

    public Map<String, Object> createDefaultVelocityContext();
}

