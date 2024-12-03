/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  org.apache.velocity.context.Context
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.extra.flyingpdf.html.ExportHtmlService;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.io.Writer;
import org.apache.velocity.context.Context;
import org.springframework.stereotype.Component;

@Component
public class ExportHtmlServiceImpl
implements ExportHtmlService {
    @Override
    public void renderTemplateWithoutSwallowingErrors(String templateName, Context context, Writer writer) throws Exception {
        VelocityUtils.renderTemplateWithoutSwallowingErrors((String)templateName, (Context)context, (Writer)writer);
    }
}

