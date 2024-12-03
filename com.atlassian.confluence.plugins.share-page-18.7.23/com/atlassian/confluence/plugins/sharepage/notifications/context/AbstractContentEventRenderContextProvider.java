/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.PlainTextToHtmlConverter
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Eithers
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.sharepage.notifications.context;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.sharepage.ContentTypeResolver;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.notifications.context.ShareNotificationAddress;
import com.atlassian.confluence.plugins.sharepage.notifications.payload.ShareContentPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Eithers;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class AbstractContentEventRenderContextProvider
extends RenderContextProviderTemplate<ShareContentPayload> {
    private static final String DEFAULT_CONTENT_TYPE_ICONS_MODULE = "com.atlassian.confluence.plugins.confluence-email-resources:content-type-icons";
    private static final String RESOURCES_FOR_SHARE_PAGE_PLUGIN_MODULE = "resources-for-share-page-plugin";
    protected final ContentEntityManager contentEntityManager;
    protected final ShareGroupEmailManager shareGroupEmailManager;
    protected final ContentTypeResolver contentTypeResolver;
    protected final TransactionTemplate transactionTemplate;
    protected final I18NBeanFactory i18NBeanFactory;
    protected final LocaleManager localeManager;
    private final UserAccessor userAccessor;

    public AbstractContentEventRenderContextProvider(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentTypeResolver contentTypeResolver, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, TransactionTemplate transactionTemplate, ShareGroupEmailManager shareGroupEmailManager) {
        this.contentEntityManager = contentEntityManager;
        this.contentTypeResolver = contentTypeResolver;
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.transactionTemplate = transactionTemplate;
        this.shareGroupEmailManager = shareGroupEmailManager;
    }

    protected abstract Content getContentForEntityId(Long var1, Long var2);

    protected Maybe<Map<String, Object>> checkedCreate(Notification<ShareContentPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> recipientData) {
        if (recipientData.isEmpty()) {
            return MaybeNot.becauseOf((String)"No recipient provided. Need to have either an NotificationAddress, or a RoleRecipient", (Object[])new Object[0]);
        }
        ShareContentPayload payload = (ShareContentPayload)notification.getPayload();
        Content content = this.getContentForEntityId(payload.getEntityId(), payload.getContextualPageId());
        ConfluenceUser originator = this.userAccessor.getExistingUserByKey((UserKey)notification.getOriginator().getOrNull());
        Map<String, Object> context = this.buildBasicContext(notification, content, originator);
        Maybe<Map<String, Object>> maybeRecipientContext = this.buildRecipientContext(payload, recipientData, content, originator);
        if (maybeRecipientContext.isEmpty()) {
            return maybeRecipientContext;
        }
        Map<String, Object> contentContext = this.buildContentSpecificContext(payload);
        Map<String, Object> mediumContext = this.buildMediumSpecificContext(payload);
        context.putAll((Map)maybeRecipientContext.get());
        context.putAll(contentContext);
        context.putAll(mediumContext);
        return Option.option(context);
    }

    protected Map<String, Object> buildBasicContext(Notification<ShareContentPayload> notification, Content content, ConfluenceUser originator) {
        LinkType linkType = content.getStatus().equals((Object)ContentStatus.DRAFT) ? LinkType.EDIT_UI : LinkType.WEB_UI;
        NotificationContext context = new NotificationContext();
        context.put("sharedContentUrl", (Object)((Link)content.getLinks().get(linkType)).getPath());
        context.put("sharedContentDisplayTitle", (Object)content.getTitle());
        context.put("content", (Object)content);
        context.put("hasComment", (Object)StringUtils.isNotEmpty((CharSequence)((ShareContentPayload)notification.getPayload()).getNote()));
        context.put("sender", (Object)originator);
        context.setActor((User)originator);
        return context.getMap();
    }

    private Maybe<Map<String, Object>> buildRecipientContext(ShareContentPayload payload, Maybe<Either<NotificationAddress, RoleRecipient>> recipientData, Content content, ConfluenceUser sender) {
        List<ConfluenceUser> users = payload.getUsers().stream().map(this::userKeyToConfluenceUser).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        if (((Either)recipientData.get()).isLeft()) {
            NotificationAddress notificationAddress = (NotificationAddress)((Either)recipientData.get()).left().get();
            String group = notificationAddress instanceof ShareNotificationAddress ? ((ShareNotificationAddress)notificationAddress).getGroupName() : "";
            String currentEmailAddress = notificationAddress.getAddressData();
            Either groupOrEmail = Eithers.cond((boolean)StringUtils.isBlank((CharSequence)group), (Object)group, (Object)currentEmailAddress);
            return Option.some(this.buildContextForNotificationAddress(payload, users, (Either<String, String>)groupOrEmail, content, sender));
        }
        RoleRecipient roleRecipient = (RoleRecipient)((Either)recipientData.get()).right().get();
        Optional<ConfluenceUser> receivingUser = this.userKeyToConfluenceUser(roleRecipient.getUserKey().getStringValue());
        if (!receivingUser.isPresent()) {
            return MaybeNot.becauseOf((String)("User " + roleRecipient.getUserKey() + " is not found."), (Object[])new Object[0]);
        }
        return Option.some(this.buildContextForRoleRecipient(receivingUser.get(), payload, users, content, sender));
    }

    protected Map<String, Object> buildContextForNotificationAddress(ShareContentPayload payload, Iterable<ConfluenceUser> users, Either<String, String> recipientData, Content content, ConfluenceUser sender) {
        Object sharedWithName;
        Set<String> otherEmails = payload.getOriginalRequestEmails();
        Stream<Object> groups = payload.getGroups().stream();
        if (recipientData.isRight()) {
            otherEmails = payload.getOriginalRequestEmails().stream().filter(email -> !email.equals(recipientData.right().get())).collect(Collectors.toSet());
            String emailAddress = GeneralUtil.htmlEncode((String)((String)recipientData.right().get()));
            sharedWithName = "<a href='mailto:" + emailAddress + "' class='mailto-link'>" + emailAddress + "</a>";
        } else {
            groups = payload.getGroups().stream().filter(group -> !group.equals(recipientData.left().get()));
            sharedWithName = (String)recipientData.left().get();
        }
        Set<Group> otherGroups = groups.map(this::groupNameToGroup).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        boolean sharedWithOthers = !payload.getUsers().isEmpty() || !otherEmails.isEmpty() || !otherGroups.isEmpty();
        return this.receiverSpecificContextHelper(users, otherEmails, otherGroups, sharedWithOthers, (String)sharedWithName, null, content, sender);
    }

    protected Map<String, Object> buildMediumSpecificContext(ShareContentPayload payload) {
        if (StringUtils.isNotEmpty((CharSequence)payload.getNote())) {
            return ImmutableMap.builder().put((Object)"comment", (Object)payload.getNote()).put((Object)"commentHtml", (Object)PlainTextToHtmlConverter.toHtml((String)payload.getNote())).build();
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> buildContextForRoleRecipient(ConfluenceUser recipient, ShareContentPayload payload, List<ConfluenceUser> users, Content content, ConfluenceUser sender) {
        String sharedWithName;
        Set<String> filteredGroupNames;
        Set<ConfluenceUser> filteredUsers = users.stream().filter(user -> !user.equals(recipient)).collect(Collectors.toSet());
        Sets.SetView unmappedGroups = Sets.difference(payload.getGroups(), this.shareGroupEmailManager.getMappedGroupNames());
        Sets.SetView matchingGroups = Sets.intersection((Set)unmappedGroups, new HashSet(this.userAccessor.getGroupNames((User)recipient)));
        if (matchingGroups.isEmpty()) {
            filteredGroupNames = payload.getGroups();
            sharedWithName = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)recipient)).getText("share.mail.share.you");
        } else {
            String matchingGroup = (String)matchingGroups.iterator().next();
            filteredGroupNames = payload.getGroups().stream().filter(group -> !group.equals(matchingGroup)).collect(Collectors.toSet());
            sharedWithName = matchingGroup;
        }
        Set<Group> filteredGroups = filteredGroupNames.stream().map(this::groupNameToGroup).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        boolean sharedWithOthers = !filteredUsers.isEmpty() || !payload.getEmails().isEmpty() || !filteredGroups.isEmpty();
        return this.receiverSpecificContextHelper(filteredUsers, payload.getOriginalRequestEmails(), filteredGroups, sharedWithOthers, sharedWithName, recipient, content, sender);
    }

    private Map<String, Object> receiverSpecificContextHelper(Iterable<ConfluenceUser> users, Set<String> otherEmails, Set<Group> otherGroups, boolean sharedWithOthers, String sharedWithName, ConfluenceUser recipient, Content content, ConfluenceUser sender) {
        return ImmutableMap.builder().put((Object)"users", users).put((Object)"emails", otherEmails).put((Object)"groups", otherGroups).put((Object)"sharedWithOthers", (Object)sharedWithOthers).put((Object)"sharedWithName", (Object)sharedWithName).putAll(this.buildSubjectContext(recipient, content, sender)).build();
    }

    protected Map<String, Object> buildSubjectContext(ConfluenceUser recipient, Content content, ConfluenceUser sender) {
        return Collections.emptyMap();
    }

    protected Map<String, Object> buildContentSpecificContext(ShareContentPayload payload) {
        return Collections.singletonMap("contentIconResourceModule", this.getContentIconResourceModule(payload.getEntityId()));
    }

    protected String getContentIconResourceModule(long entityId) {
        ContentEntityObject ceo = this.contentEntityManager.getById(entityId);
        if (ceo instanceof CustomContentEntityObject) {
            return this.extractPluginKeyFromModuleKey((CustomContentEntityObject)ceo) + ":resources-for-share-page-plugin";
        }
        return DEFAULT_CONTENT_TYPE_ICONS_MODULE;
    }

    private String extractPluginKeyFromModuleKey(CustomContentEntityObject customEntity) {
        return new ModuleCompleteKey(customEntity.getPluginModuleKey()).getPluginKey();
    }

    public Map<String, Object> createMessageMetadata(Notification<ShareContentPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        ImmutableMap.Builder context = ImmutableMap.builder();
        Optional<String> email = this.userKeyToEmail((UserKey)notification.getOriginator().getOrNull());
        context.put((Object)"replyToAddress", email.orElse(null));
        return context.build();
    }

    private Optional<ConfluenceUser> userKeyToConfluenceUser(String userKeyOrName) {
        ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKeyOrName));
        if (user == null) {
            user = this.userAccessor.getUserByName(userKeyOrName);
        }
        return Optional.ofNullable(user);
    }

    private Optional<Group> groupNameToGroup(String groupName) {
        return Optional.ofNullable(this.userAccessor.getGroup(groupName));
    }

    protected Optional<String> userKeyToEmail(UserKey userKey) {
        ConfluenceUser confluenceUser = this.userAccessor.getExistingUserByKey(userKey);
        if (confluenceUser == null) {
            return Optional.empty();
        }
        String email = confluenceUser.getEmail();
        if (email != null) {
            return Optional.of(email);
        }
        return Optional.empty();
    }
}

