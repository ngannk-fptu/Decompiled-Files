/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.views.velocity.VelocityManager
 *  org.apache.velocity.Template
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.lookandfeel.AbstractLookAndFeelAction;
import com.atlassian.confluence.util.velocity.ConfluenceVelocityResourceCache;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.core.filters.ServletContextThreadLocal;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDecoratorAction
extends AbstractLookAndFeelAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractDecoratorAction.class);
    public static final String KLUDGE_WWRESOURCE_TEMPLATE = "decorators/wwloader.vmd";
    public static final String KLUDGE_CLASSPATH_TEMPLATE = "com/atlassian/confluence/cploader.vm";
    String content;
    protected String decoratorName;
    private VelocityManager velocityManager;

    protected String readDefaultTemplate() {
        String templateSource = this.getTemplateFromResourceLoader(KLUDGE_WWRESOURCE_TEMPLATE, this.decoratorName);
        if (templateSource == null) {
            templateSource = this.getTemplateFromResourceLoader(KLUDGE_CLASSPATH_TEMPLATE, this.decoratorName);
        }
        if (templateSource == null) {
            log.warn("Couldn't load default template source for " + this.decoratorName);
        }
        return templateSource;
    }

    protected String getTemplateFromResourceLoader(String knownTemplatePath, String templateToRetrieve) {
        if (!"".equals(this.decoratorName) && this.isUnderConfluenceApp(ServletContextThreadLocal.getRequest(), this.decoratorName) && (this.decoratorName.endsWith(".vm") || this.decoratorName.endsWith(".vmd"))) {
            try {
                Template t = this.getVelocityEngine().getTemplate(knownTemplatePath);
                return this.getTemplateSource(templateToRetrieve, t.getResourceLoader(), t.getEncoding());
            }
            catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    protected String getTemplateSource(String template, ResourceLoader resourceLoader, String encoding) {
        try (InputStream is = resourceLoader.getResourceStream(template);){
            String string;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));){
                String s;
                StringBuilder result = new StringBuilder();
                while ((s = br.readLine()) != null) {
                    result.append(s).append(System.getProperty("line.separator"));
                }
                string = result.toString();
            }
            return string;
        }
        catch (Exception e) {
            log.warn("Trouble reading velocity template", (Throwable)e);
            return null;
        }
    }

    @HtmlSafe
    public String getContent() {
        return this.content;
    }

    public String getDecoratorName() {
        return this.decoratorName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDecoratorName(String decoratorName) {
        this.decoratorName = decoratorName;
    }

    protected void removeFromVelocityCache(String decorator) {
        ConfluenceVelocityResourceCache.removeFromCaches(decorator);
    }

    private boolean isUnderConfluenceApp(HttpServletRequest request, String decoratorName) {
        try {
            if (URI.create(decoratorName).isAbsolute()) {
                return false;
            }
            String[] excludedPatterns = new String[]{"\\..\\", "/../", "WEB-INF", "META-INF"};
            URI contextUrl = URI.create(request.getRequestURL().toString()).resolve(request.getContextPath());
            String resolvedUrl = contextUrl.resolve(decoratorName).normalize().toString();
            for (String excludedPattern : excludedPatterns) {
                if (!resolvedUrl.contains(excludedPattern)) continue;
                return false;
            }
            return true;
        }
        catch (IllegalArgumentException iae) {
            return false;
        }
    }

    public void setVelocityManager(VelocityManager velocityManager) {
        this.velocityManager = velocityManager;
    }

    private VelocityEngine getVelocityEngine() throws Exception {
        if (this.velocityManager != null) {
            return this.velocityManager.getVelocityEngine();
        }
        return VelocityUtils.getVelocityEngine();
    }
}

