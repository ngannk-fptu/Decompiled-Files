/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.transformers.template;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.InitializingBean;

public class UnderscoreTemplateRenderer
implements InitializingBean {
    private String underscoreTemplateSource = "";
    private static final String UNDERSCORE_TEMPLATE_LOCATION = "templates/underscoreTemplate.vm";
    private final I18nResolver i18nResolver;
    private final JavascriptHelper javascriptHelper;
    private final TemplateRenderer renderer;

    public UnderscoreTemplateRenderer(I18nResolver i18nResolver, TemplateRenderer renderer) {
        this.i18nResolver = i18nResolver;
        this.renderer = renderer;
        this.javascriptHelper = new JavascriptHelper();
    }

    public void afterPropertiesSet() throws Exception {
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(UNDERSCORE_TEMPLATE_LOCATION);){
            this.underscoreTemplateSource = IOUtils.toString((InputStream)stream, (Charset)StandardCharsets.UTF_8);
        }
    }

    public String renderUnderscoreTemplate(String resourceLocation, CharSequence templateContent) {
        String renderedTemplate;
        String moduleName = resourceLocation.substring(resourceLocation.lastIndexOf("/") + 1, resourceLocation.lastIndexOf("."));
        Map<String, JavascriptHelper> templateParams = Collections.singletonMap("js", this.javascriptHelper);
        if (templateContent.toString().contains("#parse")) {
            StringWriter buf = new StringWriter();
            try {
                this.renderer.render(resourceLocation, templateParams, (Writer)buf);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            renderedTemplate = buf.toString();
        } else {
            renderedTemplate = this.renderer.renderFragment(templateContent.toString(), templateParams);
        }
        String escapedTemplate = StringEscapeUtils.escapeJava((String)renderedTemplate);
        HashMap<String, String> context = new HashMap<String, String>();
        context.put("moduleName", moduleName);
        context.put("templateContentWithHtml", escapedTemplate);
        return this.renderer.renderFragment(this.underscoreTemplateSource, context);
    }

    public final class JavascriptHelper {
        public String i18nStringHtml(String key) {
            return "\"" + StringEscapeUtils.escapeJava((String)UnderscoreTemplateRenderer.this.i18nResolver.getText(key)) + "\"";
        }
    }
}

