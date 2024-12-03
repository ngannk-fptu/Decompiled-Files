/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.botocss.Botocss
 *  com.atlassian.botocss.BotocssStyles
 *  com.atlassian.confluence.api.model.web.WebItemView
 *  com.atlassian.confluence.api.model.web.WebItemView$Builder
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.InputStreamSerializer
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory$FilterByType
 *  com.atlassian.confluence.core.PluginDataSourceFactory$ResourceView
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.mail.notification.listeners.NotificationTemplate
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem$Builder
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationRenderManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.setup.settings.DarkFeatures
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.task.Task
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor
 *  com.atlassian.plugin.web.model.WebPanel
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.activation.DataSource
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.dailysummary.components.impl;

import com.atlassian.botocss.Botocss;
import com.atlassian.botocss.BotocssStyles;
import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.InputStreamSerializer;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.notification.listeners.NotificationTemplate;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationRenderManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.dailysummary.components.SingleUseUnsubscribeTokenManager;
import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailTaskFactory;
import com.atlassian.confluence.plugins.dailysummary.components.TemplateContextHelper;
import com.atlassian.confluence.plugins.dailysummary.content.SummaryEmailPanelData;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.task.Task;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebPanelModuleDescriptor;
import com.atlassian.plugin.web.model.WebPanel;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSummaryEmailTaskFactory
implements SummaryEmailTaskFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultSummaryEmailTaskFactory.class);
    private static final ModuleCompleteKey POPULAR_CONTENT_WEB_PANEL_MODULE_COMPLETEY_KEY = new ModuleCompleteKey("com.atlassian.confluence.plugins.confluence-daily-summary-email:daily-summary-popular-content");
    private static final String TEMPLATE_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-daily-summary-email";
    private static final String TEMPLATE_MODULE_KEY = "chrome-template";
    private static final String TEMPLATE_LOCATION = "com.atlassian.confluence.plugins.confluence-daily-summary-email:chrome-template";
    private static final String TITLE_METADATA_SECTION = "atl.daily-summary-email.title.metadata";
    private static final String TEMPLATE_NAME = "Confluence.Templates.Mail.Recommended.chrome.soy";
    private static final String CXT_UNSUBSCRIBE_TOKEN = "summary-unsubscribe-token";
    private static final String CXT_DATE_FORMATTER = "dateFormatter";
    private static final String CXT_EXCERPTS = "contentExcerpts";
    private static final String CXT_CSS_RESOURCES = "cssResources";
    private static final String CXT_MOBILE_CSS_RESOURCES = "mobileCssResources";
    private static final String CXT_BOILERPLATE_CSS_RESOURCES = "boilerPlateCssResources";
    private static final String CXT_FORMATTED_DATE = "formattedDate";
    private static final String CXT_TITLE_METADATA = "titleMetadataItems";
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final UserAccessor userAccessor;
    private final SettingsManager settingsManager;
    private final I18nResolver i18n;
    private final SingleUseUnsubscribeTokenManager singleUseTokenManager;
    private final DataSourceFactory dataSourceFactory;
    private final VelocityHelperService velocityHelper;
    private final WebInterfaceManager webInterfaceManager;
    private final NotificationRenderManager notificationRenderManager;

    public DefaultSummaryEmailTaskFactory(@ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport LocaleManager localeManager, @ComponentImport UserAccessor userAccessor, @ComponentImport SettingsManager settingsManager, @ComponentImport I18nResolver i18n, SingleUseUnsubscribeTokenManager singleUseTokenManager, @ComponentImport DataSourceFactory dataSourceFactory, @ComponentImport VelocityHelperService velocityHelper, @ComponentImport WebInterfaceManager webInterfaceManager, @ComponentImport NotificationRenderManager notificationRenderManager) {
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
        this.settingsManager = settingsManager;
        this.i18n = i18n;
        this.singleUseTokenManager = singleUseTokenManager;
        this.dataSourceFactory = dataSourceFactory;
        this.velocityHelper = velocityHelper;
        this.webInterfaceManager = webInterfaceManager;
        this.notificationRenderManager = notificationRenderManager;
    }

    @Override
    public Optional<Task> createEmailTask(User user, Date date) {
        return this.createEmailTask(user, date, null);
    }

    @Override
    public Optional<Task> createEmailTask(User user, Date date, @Nullable Space space) {
        PreRenderedMailNotificationQueueItem item;
        List webPanels = this.webInterfaceManager.getWebPanelDescriptors("atl.daily-summary-email");
        if (webPanels.isEmpty()) {
            return Optional.empty();
        }
        Map<String, Object> context = this.getInitialContext(user, date, space, webPanels);
        String subject = this.getSubject(user, date, (DateFormatter)context.get(CXT_DATE_FORMATTER));
        PreRenderedMailNotificationQueueItem.Builder itemBuilder = PreRenderedMailNotificationQueueItem.with((User)user, (String)this.getTemplateName(), (String)subject).andSender(this.getFromUser(user));
        if (NotificationTemplate.ADG.isEnabled("recommended")) {
            itemBuilder.andTemplateLocation(this.getTemplateLocation()).andRelatedBodyParts(this.getDataSources(context)).andRelatedBodyParts(this.getWebPanelDataSources(context)).andRelatedBodyParts(this.imagesUsedByChromeTemplate()).andContext(TemplateContextHelper.VELOCITY2SOY.convert(context));
        } else {
            itemBuilder.andContext(context);
        }
        if (!NotificationTemplate.ADG.isEnabled("recommended")) {
            String body = itemBuilder.render().getBody();
            itemBuilder.andRelatedBodyParts(this.getDataSources(context));
            item = itemBuilder.render();
            item.setBody(Botocss.inject((String)body, (BotocssStyles)this.serialiseLegacyCss()));
        } else {
            item = itemBuilder.render();
        }
        item.setBody(this.formatMicrosoftOutlookConditionalComments(item.getBody()));
        if (this.hasContent(context)) {
            return Optional.of(item);
        }
        return Optional.empty();
    }

    private String formatMicrosoftOutlookConditionalComments(String body) {
        return body.replace("<!--[if !mso]>-->", "<!--[if !mso]><!-- -->");
    }

    private boolean hasContent(Map<String, Object> context) {
        for (SummaryEmailPanelData data : this.getPanelDatum(context)) {
            if (!data.hasContent()) continue;
            return true;
        }
        return false;
    }

    private User getFromUser(User recipient) {
        String instanceName = this.settingsManager.getGlobalSettings().getSiteTitle();
        String fromSuffix = this.i18n.getText(this.localeManager.getLocale(recipient), "daily.summary.email.from.suffix");
        String from = "Confluence".equals(instanceName) ? fromSuffix : String.format("%s %s", instanceName, fromSuffix);
        if (!StringUtils.isBlank((CharSequence)from)) {
            return new DefaultUser(null, from, "");
        }
        return null;
    }

    private Collection<DataSource> getWebPanelDataSources(Map<String, Object> context) {
        if (context.containsKey("WebPanelDataSources")) {
            return (Collection)context.get("WebPanelDataSources");
        }
        return Collections.emptyList();
    }

    private Collection<DataSource> getDataSources(Map<String, Object> context) {
        HashMap<String, DataSource> dataSourcesByName = new HashMap<String, DataSource>();
        for (SummaryEmailPanelData data : this.getPanelDatum(context)) {
            for (DataSource ds : data.getImageDatasources()) {
                dataSourcesByName.put(ds.getName(), ds);
            }
        }
        return dataSourcesByName.values();
    }

    private Iterable<DataSource> imagesUsedByChromeTemplate() {
        return (Iterable)((PluginDataSourceFactory)this.dataSourceFactory.forPlugin("com.atlassian.confluence.plugins.confluence-email-resources").get()).getResourcesFromModules(TEMPLATE_MODULE_KEY, (Predicate)PluginDataSourceFactory.FilterByType.IMAGE).get();
    }

    private List<SummaryEmailPanelData> getPanelDatum(Map<String, Object> context) {
        return (List)context.get("summary-panel-data");
    }

    private String getTemplateName() {
        if (NotificationTemplate.ADG.isEnabled("recommended")) {
            return TEMPLATE_NAME;
        }
        return "daily-summary-template.vm";
    }

    private String getTemplateLocation() {
        return TEMPLATE_LOCATION;
    }

    private String getSubject(User user, Date date, DateFormatter formatter) {
        String siteName = this.settingsManager.getGlobalSettings().getSiteTitle();
        if (siteName == null) {
            siteName = "Confluence";
        }
        return this.i18n.getText("daily.summary.email.subject", new Serializable[]{siteName, user.getFullName(), formatter.format(date)});
    }

    private Map<String, Object> getInitialContext(User user, Date date, Space space, List<WebPanelModuleDescriptor> webPanels) {
        if (user == null) {
            throw new IllegalArgumentException("user should not be null");
        }
        NotificationContext initContext = new NotificationContext();
        initContext.put("WebPanelDataSources", new ArrayList());
        initContext.put("summary-recipient", (Object)user);
        initContext.put("summary-date", (Object)date);
        initContext.put("summary-space", (Object)space);
        initContext.put("summary-panel-data", new ArrayList());
        String schedule = this.userAccessor.getUserPreferences(user).getString("confluence.prefs.daily.summary.schedule");
        if (schedule == null) {
            schedule = "weekly";
        }
        initContext.put("summary-schedule", (Object)schedule);
        initContext.put(CXT_UNSUBSCRIBE_TOKEN, (Object)this.singleUseTokenManager.getUserToken(user));
        DateFormatter dateFormatter = this.userAccessor.getConfluenceUserPreferences(user).getDateFormatter(this.formatSettingsManager, this.localeManager);
        initContext.put(CXT_DATE_FORMATTER, (Object)dateFormatter);
        if (NotificationTemplate.ADG.isEnabled("recommended")) {
            if (DarkFeatures.isDarkFeatureEnabled((String)"email-tracking")) {
                initContext.setAction("daily-summary");
                initContext.setRecipient(user);
                initContext.addWebFragmentContext();
                initContext.addToWebFragmentContext("schedule", schedule);
            }
            initContext.put(CXT_FORMATTED_DATE, (Object)dateFormatter.format(date));
            initContext.put(CXT_EXCERPTS, this.renderContentExcerpts(webPanels, initContext.getMap()));
            List<ModuleCompleteKey> resourceProviderModuleKeys = this.resourceProviderModuleKeys(webPanels);
            initContext.put(CXT_CSS_RESOURCES, this.serialiseCssResources(resourceProviderModuleKeys));
            initContext.put(CXT_MOBILE_CSS_RESOURCES, this.serialiseCssResources(resourceProviderModuleKeys, "mobile"));
            initContext.put(CXT_BOILERPLATE_CSS_RESOURCES, this.serialiseCssResources(resourceProviderModuleKeys, "boilerplate"));
            initContext.put("settingsManager", (Object)this.settingsManager);
            initContext.put(CXT_TITLE_METADATA, this.renderTitleMetadata(initContext));
        } else {
            initContext.put("emailContext", new HashMap(initContext.getMap()));
        }
        return initContext.getMap();
    }

    private List<ModuleCompleteKey> resourceProviderModuleKeys(List<WebPanelModuleDescriptor> webPanels) {
        return ImmutableList.builder().add((Object)new ModuleCompleteKey(TEMPLATE_PLUGIN_KEY, TEMPLATE_MODULE_KEY)).addAll((Iterable)webPanels.stream().map(panel -> new ModuleCompleteKey(panel.getPluginKey(), panel.getKey())).collect(Collectors.toList())).build();
    }

    private Iterable<String> serialiseCssResources(Iterable<ModuleCompleteKey> moduleKeys) {
        return this.serialiseCssResources(moduleKeys, null);
    }

    private Iterable<String> serialiseCssResources(Iterable<ModuleCompleteKey> moduleKeys, String type) {
        Predicate<PluginDataSourceFactory.ResourceView> filter = type == null ? resource -> PluginDataSourceFactory.FilterByType.CSS.evaluate(resource) && StringUtils.isBlank((CharSequence)resource.type()) : resource -> PluginDataSourceFactory.FilterByType.CSS.evaluate(resource) && type.equals(resource.type());
        return StreamSupport.stream(moduleKeys.spliterator(), false).map(key -> {
            Optional maybePlugin = this.dataSourceFactory.createForPlugin(key.getPluginKey());
            if (!maybePlugin.isPresent() && !ConfluenceSystemProperties.isDevMode()) {
                return "";
            }
            Optional maybeResources = ((PluginDataSourceFactory)maybePlugin.get()).getResourcesFromModules(key.getModuleKey(), filter);
            if (!maybeResources.isPresent() && !ConfluenceSystemProperties.isDevMode()) {
                return "";
            }
            return InputStreamSerializer.eagerInDevMode().addAllDataSources((Iterable)maybeResources.get()).toString();
        }).collect(Collectors.toList());
    }

    private List<WebItemView> renderTitleMetadata(NotificationContext initContext) {
        return this.notificationRenderManager.getDisplayableItems(TITLE_METADATA_SECTION, initContext).stream().map(webItemModuleDescriptor -> {
            WebItemView.Builder builder = new WebItemView.Builder();
            String label = webItemModuleDescriptor.getWebLabel().getKey();
            String url = webItemModuleDescriptor.getLink().getRenderedUrl(initContext.getMap());
            return builder.create(webItemModuleDescriptor.getKey(), url, label, webItemModuleDescriptor.getWeight());
        }).collect(Collectors.toList());
    }

    private BotocssStyles serialiseLegacyCss() {
        Iterable<String> dailySummaryLegacyCss = this.serialiseCssResources((Iterable<ModuleCompleteKey>)ImmutableList.builder().add((Object)new ModuleCompleteKey(TEMPLATE_PLUGIN_KEY, "daily-summary-legacy-css")).build());
        String emailHeaderCss = this.velocityHelper.getRenderedTemplate("/templates/email/html/daily-summary-header-styles.vm", this.velocityHelper.createDefaultVelocityContext());
        ImmutableList legacyCss = ImmutableList.builder().addAll(dailySummaryLegacyCss).add((Object)emailHeaderCss).build();
        return Botocss.parse((String[])((String[])legacyCss.toArray((Object[])new String[legacyCss.size()])));
    }

    private Iterable<String> renderContentExcerpts(List<WebPanelModuleDescriptor> webPanel, Map<String, Object> context) {
        context = new HashMap<String, Object>(context);
        ImmutableList.Builder contentExcerpts = ImmutableList.builder();
        for (WebPanelModuleDescriptor item : webPanel) {
            try {
                String renderedWebPanel = ((WebPanel)item.getModule()).getHtml(context);
                contentExcerpts.add((Object)renderedWebPanel);
            }
            catch (RuntimeException e) {
                if (POPULAR_CONTENT_WEB_PANEL_MODULE_COMPLETEY_KEY.getCompleteKey().equals(item.getCompleteKey())) {
                    throw e;
                }
                log.warn("Skipping web panel [{}] since it escaped with message [{}]. Enable TRACE for this log to see the full stack trace of this exception.", (Object)item.getCompleteKey(), (Object)e.getMessage());
                log.trace(e.getMessage(), (Throwable)e);
            }
        }
        return contentExcerpts.build();
    }
}

