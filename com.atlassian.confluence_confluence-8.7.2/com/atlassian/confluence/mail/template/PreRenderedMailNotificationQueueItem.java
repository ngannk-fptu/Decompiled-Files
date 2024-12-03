/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.Email
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.queue.MailQueueItem
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.base.Throwables
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Maybe
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Pair
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.mail.template;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.None;
import com.atlassian.confluence.jmx.JmxSMTPMailServer;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.embed.MimeBodyPartRecorder;
import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.mail.notification.NotificationEmailHelper;
import com.atlassian.confluence.mail.template.MailNotificationQueueItem;
import com.atlassian.confluence.mail.template.MultipartBuilder;
import com.atlassian.confluence.user.AuthenticatedUserImpersonator;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.mail.Email;
import com.atlassian.mail.MailException;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Maybe;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import javax.activation.DataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class PreRenderedMailNotificationQueueItem
extends MailNotificationQueueItem
implements MailQueueItem {
    private static final ModuleCompleteKey USE_CUSTOM_SITE_LOGO_FUNCTION_KEY = new ModuleCompleteKey("com.atlassian.confluence.plugins.confluence-email-resources", "use-custom-site-logo-function");
    private static final String[] FOOTER_LOGO_KEYS = new String[]{"footer-desktop-logo", "footer-mobile-logo"};
    private String renderedSubject;
    private String mimeType;
    private final String email;
    private Collection<DataSource> templateImageDataSources;
    private final User recipient;
    private User modifier;
    private String replyTo;
    private boolean enableEmailReply;
    private Long contentId;

    private PreRenderedMailNotificationQueueItem(User recipient, String templateLocation, String templateFileName, String subject) {
        super(recipient, templateLocation, templateFileName, subject);
        this.recipient = recipient;
        this.email = recipient.getEmail();
        this.mimeType = this.getMimeType();
    }

    @Deprecated
    public static PreRenderedMailNotificationQueueItem createFromTemplateFile(User recipient, String templateFileName, String subject) {
        String templateLocation = PreRenderedMailNotificationQueueItem.getDefaultTemplateLocation(recipient, templateFileName);
        return new PreRenderedMailNotificationQueueItem(recipient, templateLocation, templateFileName, subject);
    }

    @Deprecated
    public static MailNotificationQueueItem createFromTemplateFileAndLocation(User recipient, String templateLocation, String templateFileName, String subject) {
        return new PreRenderedMailNotificationQueueItem(recipient, templateLocation, templateFileName, subject);
    }

    public static Builder with(User recipient, String templateFileName, String subject) {
        return new Builder(recipient, templateFileName, subject);
    }

    public void render() {
        this.renderedSubject = StringEscapeUtils.unescapeHtml4((String)this.getSubject());
        this.addVelocityContextParam("subject", this.renderedSubject);
        this.body = this.getRenderedContent();
        this.clearContext();
    }

    @Override
    public final void send() throws MailException {
        if (this.body == null) {
            throw new IllegalStateException("Prerendered Queue Item has not been rendered");
        }
        super.send();
    }

    @Override
    protected Email createMailObject() {
        Email mail = new Email(this.email);
        mail.setEncoding("UTF-8");
        mail.setSubject(this.renderedSubject);
        mail.setBody(this.body);
        mail.setMimeType(this.mimeType);
        mail.setFromName(this.getServerFromName());
        mail.setMultipart(MultipartBuilder.INSTANCE.makeMultipart(this.templateImageDataSources));
        if (this.replyTo != null) {
            mail.setReplyTo(this.replyTo);
        }
        if (this.enableEmailReply) {
            NotificationEmailHelper.newNotificationEmailHelper().populateTrackingHeaders(mail, this.contentId);
        }
        return mail;
    }

    private String getServerFromName() {
        String from = "${fullname} (Confluence)";
        try {
            JmxSMTPMailServer server = (JmxSMTPMailServer)this.retrieveMailServer();
            if (server != null) {
                from = server.getFromName();
            }
        }
        catch (MailException e) {
            throw new RuntimeException(e);
        }
        String name = this.modifier != null ? this.modifier.getFullName() : this.getTextUsingLocaleOfRecipient("anonymous.name");
        String emailAddress = this.modifier != null ? this.modifier.getEmail() : "";
        String hostname = this.modifier != null && StringUtils.isNotBlank((CharSequence)emailAddress) ? emailAddress.substring(emailAddress.indexOf("@") + 1) : "";
        from = StringUtils.replace((String)StringUtils.defaultString((String)from), (String)"${fullname}", (String)name);
        from = StringUtils.replace((String)from, (String)"${email}", (String)emailAddress);
        from = StringUtils.replace((String)from, (String)"${email.hostname}", (String)hostname);
        return from;
    }

    private String getTextUsingLocaleOfRecipient(String key) {
        I18NBeanFactory factory = (I18NBeanFactory)ContainerManager.getComponent((String)"i18NBeanFactory");
        LocaleManager localeManager = (LocaleManager)ContainerManager.getComponent((String)"localeManager");
        return null != factory && null != localeManager ? factory.getI18NBean(localeManager.getLocale(this.recipient)).getText(key) : key;
    }

    @Override
    protected String renderTemplate(String templateLocation, String templateName, Map<String, Object> contextMap) {
        this.filterFooterLogoDataSourcesIfCustomSiteLogoIsUsed();
        try {
            return this.recordDataSources(() -> this.renderTemplateForRecipient(templateLocation, templateName, contextMap));
        }
        catch (Exception e) {
            Throwables.propagateIfPossible((Throwable)e);
            throw new RuntimeException(e);
        }
    }

    private <T> T recordDataSources(Callable<T> callback) throws Exception {
        MimeBodyPartRecorder mimeBodyPartRecorderBean = (MimeBodyPartRecorder)ContainerManager.getComponent((String)"mimeBodyPartRecorder");
        Pair<Optional<T>, Iterable<MimeBodyPartReference>> recordingResult = mimeBodyPartRecorderBean.startRecording(callback);
        Iterator bodyPartReferenceIterator = ((Iterable)recordingResult.right()).iterator();
        if (bodyPartReferenceIterator.hasNext()) {
            LinkedHashMap<String, DataSource> compiledDataSources = new LinkedHashMap<String, DataSource>();
            for (DataSource registeredDataSource : this.templateImageDataSources) {
                compiledDataSources.put(registeredDataSource.getName(), registeredDataSource);
            }
            while (bodyPartReferenceIterator.hasNext()) {
                DataSource recordedDataSource = ((MimeBodyPartReference)bodyPartReferenceIterator.next()).getSource();
                compiledDataSources.put(recordedDataSource.getName(), recordedDataSource);
            }
            this.templateImageDataSources = compiledDataSources.values();
        }
        Optional callbackResult = (Optional)recordingResult.left();
        return callbackResult.orElse(null);
    }

    private void filterFooterLogoDataSourcesIfCustomSiteLogoIsUsed() {
        boolean useCustomSiteLogo;
        Maybe<SoyServerFunction<Boolean>> useCustomSiteLogoFunction = this.getUseCustomSiteLogoFunction();
        if ((useCustomSiteLogoFunction.isDefined() || ConfluenceSystemProperties.isDevMode()) && (useCustomSiteLogo = ((Boolean)((SoyServerFunction)useCustomSiteLogoFunction.get()).apply(new Object[0])).booleanValue())) {
            this.templateImageDataSources = Lists.newArrayList((Iterable)Iterables.filter(this.templateImageDataSources, dataSource -> !ArrayUtils.contains((Object[])FOOTER_LOGO_KEYS, (Object)dataSource.getName())));
        }
    }

    private Maybe<SoyServerFunction<Boolean>> getUseCustomSiteLogoFunction() {
        PluginAccessor pluginAccessor = (PluginAccessor)ContainerManager.getComponent((String)"pluginAccessor", PluginAccessor.class);
        ModuleDescriptor customSiteLogoFunctionDescriptor = pluginAccessor.getEnabledPluginModule(USE_CUSTOM_SITE_LOGO_FUNCTION_KEY.getCompleteKey());
        if (customSiteLogoFunctionDescriptor == null) {
            return None.becauseOf("Expected module [%s] to be available", USE_CUSTOM_SITE_LOGO_FUNCTION_KEY);
        }
        Object useCustomSiteLogoFunction = customSiteLogoFunctionDescriptor.getModule();
        if (useCustomSiteLogoFunction instanceof SoyServerFunction) {
            return Option.some((Object)((SoyServerFunction)useCustomSiteLogoFunction));
        }
        return None.becauseOf("Module [%s] returned by descriptor [%] retrieved from [%s] is not a [%s]", useCustomSiteLogoFunction, customSiteLogoFunctionDescriptor, USE_CUSTOM_SITE_LOGO_FUNCTION_KEY, SoyServerFunction.class.getName());
    }

    private String renderTemplateForRecipient(String templateLocation, String templateName, Map<String, Object> contextMap) {
        return AuthenticatedUserImpersonator.REQUEST_AGNOSTIC.asUser(() -> PreRenderedMailNotificationQueueItem.super.renderTemplate(templateLocation, templateName, contextMap), this.recipient);
    }

    public static class Builder {
        private User recipient;
        private String templateLocation;
        private String templateFileName;
        private String subject;
        private User sender;
        private String replyTo;
        private Map<String, Object> bodyRenderContext = new HashMap<String, Object>();
        private Map<String, DataSource> relatedBodyParts = new HashMap<String, DataSource>();

        private Builder(User recipient, String templateFileName, String subject) {
            this.recipient = recipient;
            this.templateFileName = templateFileName;
            this.subject = subject;
        }

        public Builder andTemplateLocation(String templateLocation) {
            this.templateLocation = templateLocation;
            return this;
        }

        public Builder andSender(User sender) {
            this.sender = sender;
            return this;
        }

        public Builder andReplyTo(String replyToEmail) {
            this.replyTo = replyToEmail;
            return this;
        }

        public Builder andRelatedBodyPart(DataSource relatedBodyPart) {
            this.relatedBodyParts.put(relatedBodyPart.getName(), relatedBodyPart);
            return this;
        }

        public Builder andRelatedBodyParts(Iterable<DataSource> relatedBodyParts) {
            for (DataSource relatedBodyPart : relatedBodyParts) {
                this.andRelatedBodyPart(relatedBodyPart);
            }
            return this;
        }

        public Builder andContextEntry(String key, Object value) {
            this.bodyRenderContext.put(key, value);
            return this;
        }

        public Builder andContext(Map<String, Object> context) {
            this.bodyRenderContext.putAll(context);
            return this;
        }

        public PreRenderedMailNotificationQueueItem build() {
            PreRenderedMailNotificationQueueItem preRenderedMailQueueItem = this.buildWithoutContext();
            for (Map.Entry<String, Object> entry : this.bodyRenderContext.entrySet()) {
                preRenderedMailQueueItem.addVelocityContextParam(entry.getKey(), entry.getValue());
            }
            preRenderedMailQueueItem.contentId = (Long)this.bodyRenderContext.get("contentId");
            preRenderedMailQueueItem.enableEmailReply = preRenderedMailQueueItem.contentId != null && this.bodyRenderContext.get("enableEmailReply") == Boolean.TRUE;
            return preRenderedMailQueueItem;
        }

        public PreRenderedMailNotificationQueueItem render() {
            PreRenderedMailNotificationQueueItem preRenderedMailQueueItem = this.buildWithoutContext();
            preRenderedMailQueueItem.contentId = (Long)this.bodyRenderContext.get("contentId");
            preRenderedMailQueueItem.enableEmailReply = preRenderedMailQueueItem.contentId != null && this.bodyRenderContext.get("enableEmailReply") == Boolean.TRUE;
            preRenderedMailQueueItem.renderedSubject = this.subject;
            preRenderedMailQueueItem.preRenderBody(this.bodyRenderContext);
            return preRenderedMailQueueItem;
        }

        private PreRenderedMailNotificationQueueItem buildWithoutContext() {
            if (this.templateLocation == null) {
                this.templateLocation = MailNotificationQueueItem.getDefaultTemplateLocation(this.recipient, this.templateFileName);
            }
            PreRenderedMailNotificationQueueItem preRenderedMailQueueItem = new PreRenderedMailNotificationQueueItem(this.recipient, this.templateLocation, this.templateFileName, this.subject);
            preRenderedMailQueueItem.modifier = this.sender;
            preRenderedMailQueueItem.replyTo = this.replyTo;
            preRenderedMailQueueItem.templateImageDataSources = new ArrayList<DataSource>(this.relatedBodyParts.values());
            return preRenderedMailQueueItem;
        }
    }
}

