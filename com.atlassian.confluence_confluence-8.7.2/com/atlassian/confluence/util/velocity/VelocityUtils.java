/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.velocity.context.DirectiveVelocityContext
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timer
 *  com.atlassian.util.profiling.Timers
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.struts.ConfluenceVelocityManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.velocity.context.DirectiveVelocityContext;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timer;
import com.atlassian.util.profiling.Timers;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated(forRemoval=true)
public class VelocityUtils {
    private static final Logger log = LoggerFactory.getLogger(VelocityUtils.class);
    private static final Timer TIMER_RENDERING = Timers.timer((String)"Rendering velocity template");

    public static String getRenderedTemplate(String templateName, Map<?, ?> contextMap) {
        return VelocityUtils.getRenderedTemplate(templateName, (Context)new DirectiveVelocityContext(contextMap));
    }

    public static void writeRenderedTemplate(Writer writer, String templateName, Map<?, ?> contextMap) {
        VelocityUtils.writeRenderedTemplate(writer, templateName, (Context)new DirectiveVelocityContext(contextMap));
    }

    public static String getRenderedTemplate(String templateName, Context context) {
        try {
            return VelocityUtils.getRenderedTemplateWithoutSwallowingErrors(templateName, context);
        }
        catch (Exception e) {
            log.error("Error occurred rendering template: " + templateName, (Throwable)e);
            return "";
        }
    }

    public static void writeRenderedTemplate(Writer writer, String templateName, Context context) {
        try {
            VelocityUtils.renderTemplateWithoutSwallowingErrors(templateName, context, writer);
        }
        catch (Exception e) {
            log.error("Error occurred rendering template: " + templateName, (Throwable)e);
        }
    }

    public static String getRenderedTemplateWithoutSwallowingErrors(String templateName, Map<String, Object> contextMap) throws Exception {
        return VelocityUtils.getRenderedTemplateWithoutSwallowingErrors(templateName, (Context)new DirectiveVelocityContext(contextMap));
    }

    public static String getRenderedTemplateWithoutSwallowingErrors(String templateName, Context context) throws Exception {
        StringWriter writer = new StringWriter(512);
        VelocityUtils.renderTemplateWithoutSwallowingErrors(templateName, context, (Writer)writer);
        return writer.toString();
    }

    public static void renderTemplateWithoutSwallowingErrors(Template template, Context context, Writer writer) throws Exception {
        try (Ticker ignored = TIMER_RENDERING.start(new String[]{template.getName()});){
            ConfluenceVelocityManager.processContextForRendering(context);
            template.merge(context, writer);
        }
    }

    public static void renderTemplateWithoutSwallowingErrors(String templateName, Context context, Writer writer) throws Exception {
        Template template = VelocityUtils.getTemplate(templateName);
        VelocityUtils.renderTemplateWithoutSwallowingErrors(template, context, writer);
    }

    public static Template getTemplate(String templateName) throws Exception {
        String encoding;
        if (!GeneralUtil.isSetupComplete()) {
            encoding = "UTF-8";
        } else {
            SettingsManager settingsManager = (SettingsManager)ContainerManager.getComponent((String)"settingsManager");
            encoding = settingsManager.getGlobalSettings().getDefaultEncoding();
        }
        return VelocityUtils.getVelocityEngine().getTemplate(templateName, encoding);
    }

    @Deprecated(forRemoval=true)
    public static VelocityEngine getVelocityEngine() throws Exception {
        VelocityManager instance = (VelocityManager)BootstrapUtils.getBootstrapContext().getBean("velocityManager", VelocityManager.class);
        VelocityEngine velocityEngine = instance.getVelocityEngine();
        if (velocityEngine == null) {
            log.error("Initialising another velocity engine - should only occur during unit tests!");
            velocityEngine = new VelocityEngine();
            Properties props = new Properties();
            props.load(ClassLoaderUtils.getResourceAsStream((String)"velocity.properties", VelocityUtils.class));
            velocityEngine.init(props);
        }
        return velocityEngine;
    }

    public static String getRenderedContent(String templateContent, Map<?, ?> contextMap) {
        return VelocityUtils.getRenderedContent((CharSequence)templateContent, contextMap);
    }

    public static String getRenderedContent(CharSequence templateContent, Map<?, ?> contextMap) {
        return VelocityUtils.getRenderedContent(templateContent, (Context)new DirectiveVelocityContext(contextMap));
    }

    public static String getRenderedContent(String templateContent, Context velocityContext) {
        return VelocityUtils.getRenderedContent((CharSequence)templateContent, velocityContext);
    }

    public static String getRenderedContent(CharSequence templateContent, Context velocityContext) {
        StringWriter tempWriter = new StringWriter(templateContent.length());
        VelocityUtils.writeRenderedContent((Writer)tempWriter, templateContent, velocityContext);
        return tempWriter.toString();
    }

    public static void writeRenderedContent(Writer writer, String templateContent, Map<?, ?> contextMap) {
        VelocityUtils.writeRenderedContent(writer, (CharSequence)templateContent, contextMap);
    }

    public static void writeRenderedContent(Writer writer, CharSequence templateContent, Map<?, ?> contextMap) {
        VelocityUtils.writeRenderedContent(writer, templateContent, (Context)new DirectiveVelocityContext(contextMap));
    }

    private static void writeRenderedContent(Writer writer, CharSequence templateContent, Context velocityContext) {
        ConfluenceVelocityManager.processContextForRendering(velocityContext);
        try {
            VelocityUtils.getVelocityEngine().evaluate(velocityContext, writer, "getRenderedContent", templateContent.toString());
        }
        catch (MethodInvocationException e) {
            log.error("Error occurred rendering template content ", (Throwable)e);
            if (log.isDebugEnabled()) {
                log.debug("Template content : " + templateContent);
            }
            throw new InfrastructureException("Error occurred rendering template content", (Throwable)e);
        }
        catch (Exception e) {
            log.error("Error occurred rendering template content", (Throwable)e);
            throw new InfrastructureException("Error occurred rendering template content", (Throwable)e);
        }
    }
}

