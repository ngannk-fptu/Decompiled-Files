/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.core.task.Task
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.MailFactory
 *  com.atlassian.mail.queue.MailQueueItem
 *  com.atlassian.mail.server.SMTPMailServer
 *  com.atlassian.spring.container.ContainerManager
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.template;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.mail.MailContentProcessor;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.core.task.Task;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.MailFactory;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.spring.container.ContainerManager;
import com.opensymphony.util.TextUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMailNotificationQueueItem
implements MailQueueItem,
Task {
    private static final Logger log = LoggerFactory.getLogger(AbstractMailNotificationQueueItem.class);
    private static final TextUtils TEXT_UTILS_INSTANCE = new TextUtils();
    private String lastError = null;
    protected Date dateQueued;
    private int timesSent = 0;
    private String subject;
    public static final String MIME_TYPE_HTML = "text/html";
    public static final String MIME_TYPE_TEXT = "text/plain";
    private String templateFileName;
    private String templateContent;
    private String templateLocation;
    protected String body;
    private final Map<String, Object> context = new HashMap<String, Object>();

    public int compareTo(MailQueueItem that) {
        return this.timesSent - that.getSendCount();
    }

    public AbstractMailNotificationQueueItem(String templateContent) {
        this.templateContent = templateContent;
    }

    public AbstractMailNotificationQueueItem(String templateLocation, String templateFileName) {
        if (templateFileName.endsWith(".vm") && !((String)templateLocation).endsWith("/")) {
            templateLocation = (String)templateLocation + "/";
        }
        this.templateLocation = templateLocation;
        this.templateFileName = templateFileName;
    }

    public String getLastError() {
        return this.lastError;
    }

    protected void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public String getSubject() {
        return this.subject;
    }

    public Date getDateQueued() {
        return this.dateQueued;
    }

    public int getSendCount() {
        return this.timesSent;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean hasError() {
        return this.timesSent > 0;
    }

    public void execute() throws Exception {
        this.send();
    }

    public void send() throws MailException {
        this.addVelocityContextParam("subject", this.subject);
        Email mail = this.createMailObject();
        if (mail == null) {
            return;
        }
        SMTPMailServer mailServer = this.retrieveMailServer();
        if (mailServer == null) {
            String errorMessage = "Unable to send email since no mail server has been configured.";
            log.warn(errorMessage);
            this.setLastError(errorMessage);
            return;
        }
        if (MailFactory.getSettings().isSendingDisabled()) {
            log.info("Not sending email because sending is disabled via system property.");
            return;
        }
        try {
            mailServer.send(mail);
        }
        catch (MailException me) {
            this.setLastError(me.toString());
            throw me;
        }
    }

    protected abstract Email createMailObject();

    protected SMTPMailServer retrieveMailServer() throws MailException {
        return MailFactory.getServerManager().getDefaultSMTPMailServer();
    }

    protected void incrementSendCount() {
        ++this.timesSent;
    }

    protected static boolean isRecognisedMimeType(String mimePref) {
        return mimePref.equals(MIME_TYPE_HTML) || mimePref.equals(MIME_TYPE_TEXT);
    }

    private Map<String, Object> getInitialContext() {
        Map<String, Object> context = MacroUtils.defaultVelocityContext();
        String domainName = GeneralUtil.getGlobalSettings().getBaseUrl();
        if (StringUtils.isNotBlank((CharSequence)domainName) && domainName.endsWith("/")) {
            domainName = domainName.substring(0, domainName.length() - 1);
        }
        if (StringUtils.isBlank((CharSequence)domainName)) {
            log.warn("The base url was not retrieved from Global Settings. This will lead to $baseurl appearing in mail notifications");
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

    public void addVelocityContextParam(String name, Object value) {
        this.context.put(name, value);
    }

    public Object removeVelocityContextParam(String name) {
        return this.context.remove(name);
    }

    public void clearContext() {
        this.context.clear();
    }

    public Object getVelocityContextParam(String name) {
        return this.context.get(name);
    }

    public void preRenderBody(Map<String, Object> contextMap) {
        Map<String, Object> map = this.getInitialContext();
        map.putAll(contextMap);
        this.body = this.transformForEmail(this.renderLegacyTemplate(this.templateLocation, this.templateFileName, map));
    }

    public String getRenderedContent(String content) {
        Map<String, Object> contextMap = this.getInitialContext();
        AbstractMailNotificationQueueItem.addUtilsToContext(contextMap);
        if (content != null) {
            this.body = this.transformForEmail(VelocityUtils.getRenderedContent(content, contextMap));
        }
        return this.body;
    }

    public String getRenderedContent() {
        if (this.templateContent != null) {
            return this.getRenderedContent(this.templateContent);
        }
        Map<String, Object> contextMap = this.getInitialContext();
        AbstractMailNotificationQueueItem.addUtilsToContext(contextMap);
        this.body = this.transformForEmail(this.renderLegacyTemplate(this.templateLocation, this.templateFileName, contextMap));
        return this.body;
    }

    public static void addUtilsToContext(Map<String, Object> contextParams) {
        contextParams.put("velocityhelper", EncodeUtil.SINGLETON);
        contextParams.put("textutils", TEXT_UTILS_INSTANCE);
    }

    public String getTemplateFileName() {
        return this.templateFileName;
    }

    public void setTemplateFileName(String templateFileName) {
        this.templateFileName = templateFileName;
    }

    public String getTemplateContent() {
        return this.templateContent;
    }

    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }

    public String getTemplateLocation() {
        return this.templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    protected String transformForEmail(String input) {
        MailContentProcessor contentProcessor = (MailContentProcessor)ContainerManager.getComponent((String)"mailContentProcessor");
        return contentProcessor.process(input);
    }

    private String renderLegacyTemplate(String templateLocation, String templateName, Map<String, Object> contextMap) {
        if (templateName.endsWith(".vm")) {
            return VelocityUtils.getRenderedTemplate(templateLocation + templateName, contextMap);
        }
        return this.renderTemplate(templateLocation, templateName, contextMap);
    }

    protected String renderTemplate(String templateLocation, String templateName, Map<String, Object> contextMap) {
        TemplateRenderer templateRenderer = (TemplateRenderer)ContainerManager.getComponent((String)"templateRenderer");
        StringBuilder content = new StringBuilder();
        templateRenderer.renderTo(content, templateLocation, templateName, contextMap);
        return content.toString();
    }

    public static class EncodeUtil {
        public static final EncodeUtil SINGLETON = new EncodeUtil();

        private EncodeUtil() {
        }

        public String encode(String s, String enc) throws UnsupportedEncodingException {
            return URLEncoder.encode(s, enc);
        }
    }
}

