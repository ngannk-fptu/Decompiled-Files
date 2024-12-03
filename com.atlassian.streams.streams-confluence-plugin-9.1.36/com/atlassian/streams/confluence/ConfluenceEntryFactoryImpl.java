/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.CaptchaManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.streams.api.ActivityObjectTypes
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$Link
 *  com.atlassian.streams.api.common.ImmutableNonEmptyList
 *  com.atlassian.streams.api.common.NonEmptyIterable
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uris
 *  com.atlassian.streams.spi.ServletPath
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.atlassian.streams.spi.UserProfileAccessor
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.streams.api.ActivityObjectTypes;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.ImmutableNonEmptyList;
import com.atlassian.streams.api.common.NonEmptyIterable;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uris;
import com.atlassian.streams.confluence.ConfluenceActivityObjectTypes;
import com.atlassian.streams.confluence.ConfluenceEntryFactory;
import com.atlassian.streams.confluence.UriProvider;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.spi.ServletPath;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.spi.UserProfileAccessor;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;
import org.joda.time.DateTime;

public class ConfluenceEntryFactoryImpl
implements ConfluenceEntryFactory {
    public static final String CONFLUENCE_APPLICATION_TYPE = "com.atlassian.confluence";
    public static final String PAGE_WATCH_ACTION = "page-watch";
    public static final String SPACE_WATCH_ACTION = "space-watch";
    private final ApplicationProperties applicationProperties;
    private final NotificationManager notificationManager;
    private final PageManager pageManager;
    private final SpaceManager spaceManager;
    private final UserProfileAccessor userProfileAccessor;
    private final CaptchaManager captchaManager;
    private final UserManager userManager;
    private final UriProvider uriProvider;
    private final StreamsI18nResolver i18nResolver;

    public ConfluenceEntryFactoryImpl(ApplicationProperties applicationProperties, NotificationManager notificationManager, PageManager pageManager, SpaceManager spaceManager, UserProfileAccessor userProfileAccessor, CaptchaManager captchaManager, UserManager salUserManager, UriProvider uriProvider, StreamsI18nResolver i18nResolver) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.notificationManager = (NotificationManager)Preconditions.checkNotNull((Object)notificationManager, (Object)"notificationManager");
        this.pageManager = (PageManager)Preconditions.checkNotNull((Object)pageManager, (Object)"pageManager");
        this.spaceManager = (SpaceManager)Preconditions.checkNotNull((Object)spaceManager, (Object)"spaceManager");
        this.userProfileAccessor = (UserProfileAccessor)Preconditions.checkNotNull((Object)userProfileAccessor, (Object)"userProfileAccessor");
        this.captchaManager = (CaptchaManager)Preconditions.checkNotNull((Object)captchaManager, (Object)"captchaManager");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)salUserManager, (Object)"salUserManager");
        this.uriProvider = (UriProvider)Preconditions.checkNotNull((Object)uriProvider, (Object)"uriProvider");
        this.i18nResolver = (StreamsI18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
    }

    @Override
    public StreamsEntry buildStreamsEntry(URI baseUri, ActivityItem activityItem) {
        URI url = URI.create(baseUri.toASCIIString() + activityItem.getUrlPath());
        return new StreamsEntry(StreamsEntry.params().id(url).postedDate(new DateTime((Object)activityItem.getModified())).applicationType(CONFLUENCE_APPLICATION_TYPE).alternateLinkUri(url).inReplyTo(this.buildReplyTo(activityItem)).addLink(this.buildReplyTo(activityItem), "http://streams.atlassian.com/syndication/reply-to", Option.none(String.class)).addLink(this.buildIconUrl(activityItem), "http://streams.atlassian.com/syndication/icon", Option.some((Object)this.i18nResolver.getText("streams.item.confluence.tooltip." + activityItem.getContentType()))).addLinks(this.getWatchLink(activityItem)).addLinks(Iterables.transform(this.uriProvider.getContentCssUris(), this.toLink("http://streams.atlassian.com/syndication/css"))).addLinks(Iterables.transform(this.uriProvider.getPanelCssUris(), this.toLink("http://streams.atlassian.com/syndication/css"))).addLinks(Iterables.transform(this.uriProvider.getIconCssUris(), this.toLink("http://streams.atlassian.com/syndication/css"))).categories(this.buildCategory(activityItem)).addActivityObjects(activityItem.getActivityObjects()).verb(activityItem.getVerb()).target(activityItem.getTarget()).renderer(activityItem.getRenderer()).baseUri(baseUri).authors((NonEmptyIterable)ImmutableNonEmptyList.of((Object)this.userProfileAccessor.getUserProfile(baseUri, activityItem.getChangedBy()))), (I18nResolver)this.i18nResolver);
    }

    private Function<URI, StreamsEntry.Link> toLink(String rel) {
        return uri -> new StreamsEntry.Link(uri, rel, Option.none(String.class));
    }

    protected Option<URI> buildReplyTo(ActivityItem item) {
        UserProfile remoteUser = this.userManager.getRemoteUser();
        if (!this.captchaManager.showCaptchaForCurrentUser() && item.isAcceptingCommentsFromUser(remoteUser != null ? remoteUser.getUsername() : null)) {
            return Option.some((Object)URI.create(this.getBaseUrl() + ServletPath.COMMENTS.getPath() + '/' + Uris.encode((String)"wiki") + '/' + Uris.encode((String)item.getContentType()) + '/' + item.getId()).normalize());
        }
        return Option.none();
    }

    private Iterable<String> buildCategory(ActivityItem activityItem) {
        String type = activityItem.getType();
        int separatorPos = type.indexOf(46);
        return separatorPos >= 0 ? ImmutableList.of((Object)type.substring(0, separatorPos)) : ImmutableList.of((Object)type);
    }

    private URI buildIconUrl(ActivityItem activityItem) {
        return URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + activityItem.getIconPath());
    }

    private String getBaseUrl() {
        return this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
    }

    protected Option<StreamsEntry.Link> getWatchLink(ActivityItem activityItem) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return Option.none();
        }
        Iterable activityItemTypes = ActivityObjectTypes.getActivityObjectTypes(activityItem.getActivityObjects());
        if (Iterables.contains((Iterable)activityItemTypes, (Object)ConfluenceActivityObjectTypes.page()) || Iterables.contains((Iterable)activityItemTypes, (Object)ActivityObjectTypes.article())) {
            return this.getPageWatchLink((User)user, activityItem);
        }
        if (Iterables.contains((Iterable)activityItemTypes, (Object)ConfluenceActivityObjectTypes.space()) || Iterables.contains((Iterable)activityItemTypes, (Object)ConfluenceActivityObjectTypes.personalSpace())) {
            return this.getSpaceWatchLink((User)user, activityItem);
        }
        return Option.none();
    }

    private Option<StreamsEntry.Link> getPageWatchLink(User user, ActivityItem activityItem) {
        Long pageId = activityItem.getId();
        AbstractPage page = this.pageManager.getAbstractPage(pageId.longValue());
        if (page == null) {
            return Option.none();
        }
        if (this.notificationManager.isUserWatchingPageOrSpace(user, page.getSpace(), page)) {
            return Option.none();
        }
        return Option.some((Object)new StreamsEntry.Link(this.buildWatchUrl(String.valueOf(pageId), PAGE_WATCH_ACTION), "http://streams.atlassian.com/syndication/watch", Option.none(String.class)));
    }

    private Option<StreamsEntry.Link> getSpaceWatchLink(User user, ActivityItem activityItem) {
        String spaceKey = (String)activityItem.getSpaceKey().get();
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return Option.none();
        }
        if (this.notificationManager.isUserWatchingPageOrSpace(user, space, null)) {
            return Option.none();
        }
        return Option.some((Object)new StreamsEntry.Link(this.buildWatchUrl(spaceKey, SPACE_WATCH_ACTION), "http://streams.atlassian.com/syndication/watch", Option.none(String.class)));
    }

    private URI buildWatchUrl(String key, String action) {
        return URI.create(String.format("%s/rest/confluence-activity-stream/1.0/actions/%s/%s", this.getBaseUrl(), Uris.encode((String)action), Uris.encode((String)key))).normalize();
    }
}

