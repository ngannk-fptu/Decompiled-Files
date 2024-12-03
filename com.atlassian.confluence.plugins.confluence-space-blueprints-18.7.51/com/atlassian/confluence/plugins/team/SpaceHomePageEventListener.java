/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.WebItemView
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory
 *  com.atlassian.confluence.core.PluginDataSourceFactory$FilterByType
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.mail.notification.listeners.NotificationTemplate
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem$Builder
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationRenderManager
 *  com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.team;

import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.mail.notification.listeners.NotificationTemplate;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationRenderManager;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpaceHomePageEventListener {
    private static final String EMAIL_RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-email-resources";
    private static final String TEAM_SPACE_EMAIL_RESOURCES = "team-space-email-resources";
    private static final String TEAM_SPACE_EMAIL_COMPLETE_KEY = "com.atlassian.confluence.plugins.confluence-space-blueprints:team-space-email-resources";
    private static final String TEAM_SPACE_EMAIL_SOY_TEMPLATE = "Confluence.Templates.Team.Space.Notifications.teamSpaceEmail.soy";
    private static final String TEAM_SPACE_ICON_KEY = "team-space-icon";
    private static final String TEAM_SPACE_TYPE = "team-space";
    private List<DataSource> iconResources = new ArrayList<DataSource>();
    private final UserAccessor userAccessor;
    private final NotificationManager notificationManager;
    private final DataSourceFactory dataSourceFactory;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final MultiQueueTaskManager taskManager;
    private final NotificationRenderManager notificationRenderManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public SpaceHomePageEventListener(@ComponentImport UserAccessor userAccessor, @ComponentImport NotificationManager notificationManager, @ComponentImport DataSourceFactory dataSourceFactory, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport MultiQueueTaskManager taskManager, @ComponentImport NotificationRenderManager notificationRenderManager, @ComponentImport EventPublisher eventPublisher) {
        this.userAccessor = userAccessor;
        this.notificationManager = notificationManager;
        this.dataSourceFactory = dataSourceFactory;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.taskManager = taskManager;
        this.notificationRenderManager = notificationRenderManager;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void initialise() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onSpaceHomePageCreate(SpaceBlueprintHomePageCreateEvent event) {
        if (!"com.atlassian.confluence.plugins.confluence-space-blueprints:team-space-blueprint".equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }
        String members = (String)event.getContext().get("members");
        String description = (String)event.getContext().get("description");
        Space space = event.getSpace();
        String[] userNames = members.split(",");
        ArrayList<ConfluenceUser> users = new ArrayList<ConfluenceUser>();
        for (String username : userNames) {
            ConfluenceUser user = this.userAccessor.getUserByName(username);
            if (user == null) continue;
            this.notificationManager.addSpaceNotification((User)user, space);
            users.add(user);
        }
        ConfluenceUser creator = event.getCreator();
        if (NotificationTemplate.ADG.isEnabled("team.space")) {
            this.sendNotifications(creator, description, space, users);
        }
    }

    private void sendNotifications(ConfluenceUser creator, String description, Space space, List<ConfluenceUser> users) {
        ArrayList<ConfluenceUser> allTeamMembers = new ArrayList<ConfluenceUser>();
        HashMap<String, DataHandler> allAvatarHandlers = new HashMap<String, DataHandler>();
        HashMap<ConfluenceUser, String> avatarUserMap = new HashMap<ConfluenceUser, String>();
        DataHandler avatarHandler = new DataHandler(this.dataSourceFactory.getAvatar((User)creator));
        allAvatarHandlers.put(avatarHandler.getName(), avatarHandler);
        avatarUserMap.put(creator, avatarHandler.getName());
        for (ConfluenceUser user : users) {
            avatarHandler = new DataHandler(this.dataSourceFactory.getAvatar((User)user));
            allAvatarHandlers.put(avatarHandler.getName(), avatarHandler);
            avatarUserMap.put(user, avatarHandler.getName());
            allTeamMembers.add(user);
        }
        DataHandler senderAvatarDataHandler = new DataHandler(this.dataSourceFactory.getAvatar((User)creator));
        NotificationContext context = new NotificationContext();
        context.put("spaceName", (Object)space.getName());
        context.put("spaceUrl", (Object)space.getUrlPath());
        context.put("teamSpaceType", (Object)TEAM_SPACE_TYPE);
        context.put("sender", (Object)creator);
        context.put("contentSummary", (Object)description);
        context.put("avatarCid", (Object)senderAvatarDataHandler.getName());
        context.put("spaceKey", (Object)space.getKey());
        context.put("homePageUrl", (Object)space.getHomePage().getUrlPath());
        this.notificationRenderManager.attachActionIconImages("email.adg.space.action.links", context);
        this.populateIcons(context);
        for (ConfluenceUser user : users) {
            if (user.equals(creator)) continue;
            ArrayList teamMembers = new ArrayList(allTeamMembers);
            teamMembers.remove(user);
            context.put("userList", teamMembers);
            context.put("actionLinks", this.getWebItemLinks("email.adg.space.action.links", context, this.localeManager.getLocale((User)user)));
            HashMap<String, DataHandler> teamAvatarHandlers = new HashMap<String, DataHandler>(allAvatarHandlers);
            teamAvatarHandlers.remove(avatarUserMap.get(user));
            this.sendTeamSpaceNotification(creator, user, this.convertToDataSource(teamAvatarHandlers), context);
        }
    }

    private List<WebItemView> getWebItemLinks(String section, NotificationContext context, Locale locale) {
        Objects.requireNonNull(locale, "User's locale is not set");
        I18NBean i18n = this.i18NBeanFactory.getI18NBean(locale);
        List webItems = this.notificationRenderManager.getDisplayableItems(section, context);
        return webItems.stream().map(webItem -> {
            String url = webItem.getLink().getRenderedUrl(context.getMap());
            String i18nKey = webItem.getWebLabel().getKey();
            String label = i18n.getText(i18nKey);
            return WebItemView.builder().setModuleKey(webItem.getKey()).setUrl(url).setUrlWithoutContextPath(url).setLabel(label).build();
        }).collect(Collectors.toList());
    }

    private void sendTeamSpaceNotification(ConfluenceUser creator, ConfluenceUser receivingUser, List<DataSource> teamMemberAvatarHandlers, NotificationContext context) {
        I18NBean i18n = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)receivingUser));
        String subject = i18n.getText("confluence.blueprints.space.team.email.subject", (Object[])new String[]{creator.getFullName()});
        this.taskManager.addTask("mail", this.createNotificationTask(creator, receivingUser, subject, context, teamMemberAvatarHandlers));
    }

    private Task createNotificationTask(ConfluenceUser creator, ConfluenceUser receiver, String subject, NotificationContext context, List<DataSource> avatarHandlers) {
        PreRenderedMailNotificationQueueItem.Builder builder = PreRenderedMailNotificationQueueItem.with((User)receiver, (String)TEAM_SPACE_EMAIL_SOY_TEMPLATE, (String)subject).andSender((User)creator).andTemplateLocation(TEAM_SPACE_EMAIL_COMPLETE_KEY).andContext(context.getMap()).andRelatedBodyParts(avatarHandlers).andRelatedBodyParts(this.iconResources).andRelatedBodyParts(this.imagesUsedByChromeTemplate());
        return builder.render();
    }

    private void populateIcons(NotificationContext context) {
        this.iconResources.add((DataSource)((PluginDataSourceFactory)this.dataSourceFactory.createForPlugin("com.atlassian.confluence.plugins.confluence-space-blueprints").get()).getResourceFromModuleByName(TEAM_SPACE_EMAIL_RESOURCES, TEAM_SPACE_ICON_KEY).get());
        this.iconResources.addAll(context.getTemplateImageDataSources());
    }

    private List<DataSource> convertToDataSource(Map<String, DataHandler> avatarDataHandlers) {
        return avatarDataHandlers.values().stream().map(DataHandler::getDataSource).collect(Collectors.toList());
    }

    private Iterable<DataSource> imagesUsedByChromeTemplate() {
        return (Iterable)((PluginDataSourceFactory)this.dataSourceFactory.createForPlugin(EMAIL_RESOURCE_KEY).get()).getResourcesFromModules("chrome-template", arg_0 -> ((PluginDataSourceFactory.FilterByType)PluginDataSourceFactory.FilterByType.IMAGE).test(arg_0)).get();
    }
}

