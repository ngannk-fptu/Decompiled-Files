/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.ActivityVerbs
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Link
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.ImmutableNonEmptyList
 *  com.atlassian.streams.api.common.NonEmptyIterable
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.streams.thirdparty;

import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.ActivityVerbs;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.ImmutableNonEmptyList;
import com.atlassian.streams.api.common.NonEmptyIterable;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityObject;
import com.atlassian.streams.thirdparty.api.Application;
import com.atlassian.streams.thirdparty.api.Image;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Iterator;

public class ThirdPartyStreamsEntryBuilder {
    public static final String GENERATOR_KEY_SEPARATOR = "@";
    public static final String WEB_RESOURCES = "com.atlassian.streams.streams-thirdparty-plugin:thirdparty-web-resources";
    public static final URI THIRD_PARTY_APPLICATION_TYPE = URI.create("com.atlassian.streams.thirdparty");
    public static final String DEFAULT_ACTIVITY_ICON = "images/strea-ms-logo-blue.png";
    private final StreamsI18nResolver i18nResolver;
    private final WebResourceManager webResourceManager;
    private Supplier<URI> defaultActivityIconUri = new Supplier<URI>(){

        public URI get() {
            return URI.create(ThirdPartyStreamsEntryBuilder.this.webResourceManager.getStaticPluginResource(ThirdPartyStreamsEntryBuilder.WEB_RESOURCES, ThirdPartyStreamsEntryBuilder.DEFAULT_ACTIVITY_ICON, UrlMode.ABSOLUTE));
        }
    };
    private static final Function<URI, ActivityObjectType> toSimpleActivityObjectType = new Function<URI, ActivityObjectType>(){

        public ActivityObjectType apply(URI from) {
            return new SimpleActivityObjectType(from);
        }
    };
    private static final Function<URI, String> toASCIIString = new Function<URI, String>(){

        public String apply(URI from) {
            return from.toASCIIString();
        }
    };

    public ThirdPartyStreamsEntryBuilder(StreamsI18nResolver i18nResolver, WebResourceManager webResourceManager) {
        this.i18nResolver = (StreamsI18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
        this.webResourceManager = (WebResourceManager)Preconditions.checkNotNull((Object)webResourceManager, (Object)"webResourceManager");
    }

    public StreamsEntry buildStreamsEntry(Activity activity) {
        return new StreamsEntry(StreamsEntry.params().id(ThirdPartyStreamsEntryBuilder.getActivityId(activity)).postedDate(activity.getPostedDate()).applicationType(activity.getApplication().getId().toASCIIString()).alternateLinkUri(ThirdPartyStreamsEntryBuilder.getLinkUri(activity)).categories(this.getActivityCategories(activity)).addActivityObjects(this.getActivityObjects(activity)).verb(this.getActivityVerb(activity)).target(this.getActivityTarget(activity)).addLinks(this.getLinks(activity)).renderer(this.getRenderer(activity)).authors((NonEmptyIterable)ImmutableNonEmptyList.of((Object)activity.getUser())), (I18nResolver)this.i18nResolver);
    }

    static URI getActivityId(Activity activity) {
        Iterator iterator = activity.getId().iterator();
        if (iterator.hasNext()) {
            URI uri = (URI)iterator.next();
            return uri;
        }
        return URI.create("");
    }

    static String getProviderIdAndName(Application application) {
        return application.getId().toASCIIString() + GENERATOR_KEY_SEPARATOR + application.getDisplayName();
    }

    static URI getLinkUri(Activity activity) {
        Iterator iterator = activity.getUrl().iterator();
        if (iterator.hasNext()) {
            URI uri = (URI)iterator.next();
            return uri;
        }
        return URI.create("");
    }

    Iterable<String> getActivityCategories(Activity activity) {
        return ImmutableList.of();
    }

    Iterable<StreamsEntry.ActivityObject> getActivityObjects(Activity activity) {
        Iterator iterator = activity.getObject().iterator();
        if (iterator.hasNext()) {
            ActivityObject object = (ActivityObject)iterator.next();
            return ImmutableList.of((Object)this.buildActivityObject(object));
        }
        return ImmutableList.of();
    }

    StreamsEntry.ActivityObject buildActivityObject(ActivityObject object) {
        return new StreamsEntry.ActivityObject(StreamsEntry.ActivityObject.params().id(object.getId().map(toASCIIString)).activityObjectType(object.getType().map(toSimpleActivityObjectType)).title(object.getDisplayName()).alternateLinkUri(object.getUrl()));
    }

    private ActivityVerb getActivityVerb(Activity activity) {
        Iterator iterator = activity.getVerb().iterator();
        if (iterator.hasNext()) {
            URI verb = (URI)iterator.next();
            return new SimpleActivityVerb(verb);
        }
        return ActivityVerbs.post();
    }

    private Option<StreamsEntry.ActivityObject> getActivityTarget(Activity activity) {
        Iterator iterator = activity.getTarget().iterator();
        if (iterator.hasNext()) {
            ActivityObject object = (ActivityObject)iterator.next();
            return Option.some((Object)this.buildActivityObject(object));
        }
        return Option.none();
    }

    private Iterable<StreamsEntry.Link> getLinks(Activity activity) {
        Iterator iterator = activity.getIcon().iterator();
        if (iterator.hasNext()) {
            Image icon = (Image)iterator.next();
            return ImmutableList.of((Object)new StreamsEntry.Link(icon.getUrl(), "http://streams.atlassian.com/syndication/icon", Option.some((Object)activity.getApplication().getDisplayName())));
        }
        return ImmutableList.of((Object)new StreamsEntry.Link((URI)this.defaultActivityIconUri.get(), "http://streams.atlassian.com/syndication/icon", Option.some((Object)activity.getApplication().getDisplayName())));
    }

    private StreamsEntry.Renderer getRenderer(Activity activity) {
        return new ActivityRenderer(activity);
    }

    private static String getKeyFromIRIOrSimpleName(URI iriOrSimpleName) {
        String iriString = iriOrSimpleName.toASCIIString();
        if (iriString.indexOf("/") >= 0) {
            return iriString.substring(iriString.lastIndexOf(47) + 1);
        }
        return iriString;
    }

    private static final class SimpleActivityVerb
    extends SimpleActivityTag<ActivityVerb>
    implements ActivityVerb {
        public SimpleActivityVerb(URI iri) {
            super(iri);
        }
    }

    private static final class SimpleActivityObjectType
    extends SimpleActivityTag<ActivityObjectType>
    implements ActivityObjectType {
        public SimpleActivityObjectType(URI iri) {
            super(iri);
        }
    }

    private static class SimpleActivityTag<TParent> {
        private URI uri;
        private String key;

        public SimpleActivityTag(URI iri) {
            this.uri = iri;
            this.key = ThirdPartyStreamsEntryBuilder.getKeyFromIRIOrSimpleName(iri);
        }

        public URI iri() {
            return this.uri;
        }

        public String key() {
            return this.key;
        }

        public Option<TParent> parent() {
            return Option.none();
        }

        public boolean equals(Object other) {
            return this.getClass().isAssignableFrom(other.getClass()) && ((SimpleActivityTag)other).uri.equals(this.uri);
        }

        public int hashCode() {
            return this.uri.hashCode();
        }
    }

    private class ActivityRenderer
    implements StreamsEntry.Renderer {
        private final Activity activity;

        public ActivityRenderer(Activity activity) {
            this.activity = activity;
        }

        public Html renderTitleAsHtml(StreamsEntry entry) {
            return (Html)this.activity.getTitle().getOrElse((Object)Html.html((String)""));
        }

        public Option<Html> renderSummaryAsHtml(StreamsEntry entry) {
            return Option.none();
        }

        public Option<Html> renderContentAsHtml(StreamsEntry entry) {
            return this.activity.getContent();
        }
    }
}

