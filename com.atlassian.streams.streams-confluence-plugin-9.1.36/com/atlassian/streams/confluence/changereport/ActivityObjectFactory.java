/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityObjectTypes
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityObjectTypes;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.confluence.ConfluenceActivityObjectTypes;
import com.atlassian.streams.confluence.RemoteAttachment;
import com.atlassian.streams.confluence.UriProvider;
import com.atlassian.streams.confluence.changereport.AttachmentActivityItem;
import com.google.common.base.Preconditions;
import java.net.URI;

public class ActivityObjectFactory {
    private final ApplicationProperties applicationProperties;
    private final UriProvider uriProvider;

    public ActivityObjectFactory(ApplicationProperties applicationProperties, UriProvider uriProvider) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.uriProvider = (UriProvider)Preconditions.checkNotNull((Object)uriProvider, (Object)"uriProvider");
    }

    public StreamsEntry.ActivityObject newActivityObject(URI baseUri, BlogPost blog) {
        return this.newActivityObjectForEntity(baseUri, (ContentEntityObject)blog, ActivityObjectTypes.article());
    }

    public StreamsEntry.ActivityObject newActivityObject(URI baseUri, Page page) {
        return this.newActivityObjectForEntity(baseUri, (ContentEntityObject)page, ConfluenceActivityObjectTypes.page());
    }

    public StreamsEntry.ActivityObject newActivityObject(SpaceDescription space) {
        return new StreamsEntry.ActivityObject(StreamsEntry.ActivityObject.params().id(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + space.getUrlPath()).activityObjectType(space.isPersonalSpace() ? ConfluenceActivityObjectTypes.personalSpace() : ConfluenceActivityObjectTypes.space()).title(Option.option((Object)space.getDisplayTitle())).alternateLinkUri(URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + space.getUrlPath())));
    }

    public StreamsEntry.ActivityObject newActivityObject(Space space) {
        return this.newActivityObject(new SpaceDescription(space));
    }

    public StreamsEntry.ActivityObject newActivityObject(RemoteAttachment attachment) {
        return new StreamsEntry.ActivityObject(StreamsEntry.ActivityObject.params().id(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + attachment.getDownloadUrl()).activityObjectType(ActivityObjectTypes.file()).title(Option.option((Object)attachment.getName())).alternateLinkUri(URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + attachment.getDownloadUrl())));
    }

    public StreamsEntry.ActivityObject newActivityObject(AttachmentActivityItem.Entry attachment) {
        return new StreamsEntry.ActivityObject(StreamsEntry.ActivityObject.params().id(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + attachment.getDownloadPath()).activityObjectType(ActivityObjectTypes.file()).title(Option.option((Object)attachment.getName())).alternateLinkUri(URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + attachment.getDownloadPath())));
    }

    public StreamsEntry.ActivityObject newActivityObject(URI baseUri, Comment comment) {
        return this.newActivityObjectForEntity(baseUri, (ContentEntityObject)comment, ActivityObjectTypes.comment());
    }

    private StreamsEntry.ActivityObject newActivityObjectForEntity(URI baseUri, ContentEntityObject entity, ActivityObjectType type) {
        URI uri = this.uriProvider.getEntityUri(baseUri, entity);
        return new StreamsEntry.ActivityObject(StreamsEntry.ActivityObject.params().id(uri.toASCIIString()).activityObjectType(type).title(Option.option((Object)entity.getTitle())).alternateLinkUri(uri));
    }
}

