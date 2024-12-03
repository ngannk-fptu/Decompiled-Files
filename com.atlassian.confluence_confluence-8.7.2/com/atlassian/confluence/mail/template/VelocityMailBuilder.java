/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.mail.template;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.mail.template.AbstractMailNotificationQueueItem;
import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@Deprecated(forRemoval=true)
public class VelocityMailBuilder {
    private String templateLocation;
    private String templateName;
    private String toEmail;
    private String subject;
    private String mimeType;
    private Map context = new HashMap();

    public VelocityMailBuilder(String templateLocation, String templateName) {
        this.templateLocation = templateLocation;
        this.templateName = templateName;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public void setUser(User user) {
        this.checkUser(user);
        this.toEmail = user.getEmail();
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void addContextItem(String key, Object value) {
        this.context.put(key, value);
    }

    public ConfluenceMailQueueItem getRenderedMailQueueItem() {
        Map map = this.getInitialContext();
        String body = VelocityUtils.getRenderedTemplate(this.templateLocation + this.templateName, map);
        ConfluenceMailQueueItem item = new ConfluenceMailQueueItem(this.toEmail, this.getRenderedSubject(), body, this.mimeType);
        return item;
    }

    public String getRenderedSubject() {
        return this.getRenderedContent(this.subject);
    }

    public String getRenderedContent(String content) {
        String renderedContent = "";
        Map contextMap = this.getInitialContext();
        AbstractMailNotificationQueueItem.addUtilsToContext(contextMap);
        if (content != null) {
            renderedContent = VelocityUtils.getRenderedContent(content, contextMap);
        }
        return renderedContent;
    }

    private void checkUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("null user for mail notification item");
        }
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("user " + user.getName() + " has null email address for mail notification item");
        }
    }

    private Map getInitialContext() {
        Map<String, Object> context = MacroUtils.defaultVelocityContext();
        String domainName = GeneralUtil.getGlobalSettings().getBaseUrl();
        if (StringUtils.isNotEmpty((CharSequence)domainName) && domainName.endsWith("/")) {
            domainName = domainName.substring(0, domainName.length() - 1);
        }
        context.put("baseurl", domainName);
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        Object contextPath = bootstrapManager.getWebAppContextPath();
        if (StringUtils.isNotEmpty((CharSequence)contextPath) && !((String)contextPath).startsWith("/")) {
            contextPath = "/" + (String)contextPath;
        }
        context.put("contextPath", contextPath);
        context.put("stylesheet", ConfluenceRenderUtils.renderDefaultStylesheet());
        context.putAll(this.context);
        return context;
    }
}

