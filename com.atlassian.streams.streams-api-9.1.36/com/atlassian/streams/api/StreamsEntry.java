/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Ordering
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateTime
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.api;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.DateUtil;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.ImmutableNonEmptyList;
import com.atlassian.streams.api.common.NonEmptyIterable;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StreamsEntry {
    private static final Logger log = LoggerFactory.getLogger(StreamsEntry.class);
    private final Parameters<HasId, HasPostedDate, HasAlternateLinkUri, HasApplicationType, HasRenderer, HasVerb, HasAuthors> params;
    private final I18nResolver i18nResolver;
    private final Supplier<Html> titleAsHtml = Suppliers.memoize((Supplier)new Supplier<Html>(){

        public Html get() {
            return ((Parameters)StreamsEntry.this.params).renderer.renderTitleAsHtml(StreamsEntry.this);
        }
    });
    private final Supplier<Option<Html>> summaryAsHtml = Suppliers.memoize((Supplier)new Supplier<Option<Html>>(){

        public Option<Html> get() {
            return ((Parameters)StreamsEntry.this.params).renderer.renderSummaryAsHtml(StreamsEntry.this);
        }
    });
    private final Supplier<Option<Html>> contentAsHtml = Suppliers.memoize((Supplier)new Supplier<Option<Html>>(){

        public Option<Html> get() {
            return ((Parameters)StreamsEntry.this.params).renderer.renderContentAsHtml(StreamsEntry.this);
        }
    });
    private static final Ordering<StreamsEntry> byPostedDate = new Ordering<StreamsEntry>(){

        public int compare(StreamsEntry entry1, StreamsEntry entry2) {
            return entry2.getPostedDate().compareTo((Object)entry1.getPostedDate());
        }
    };

    public StreamsEntry(Parameters<HasId, HasPostedDate, HasAlternateLinkUri, HasApplicationType, HasRenderer, HasVerb, HasAuthors> params, I18nResolver i18nResolver) {
        this.params = (Parameters)Preconditions.checkNotNull(params, (Object)"params");
        this.i18nResolver = (I18nResolver)Preconditions.checkNotNull((Object)i18nResolver, (Object)"i18nResolver");
    }

    public static Parameters<NeedsId, NeedsPostedDate, NeedsAlternateLinkUri, NeedsApplicationType, NeedsRenderer, NeedsVerb, NeedsAuthors> params() {
        return new Parameters<NeedsId, NeedsPostedDate, NeedsAlternateLinkUri, NeedsApplicationType, NeedsRenderer, NeedsVerb, NeedsAuthors>(null, null, null, null, (Iterable)ImmutableList.of(), null, Option.none(ActivityObject.class), null, null, (Iterable)ImmutableList.of(), (Multimap)ImmutableMultimap.of(), Option.none(URI.class), Option.none(URI.class));
    }

    public static Parameters<HasId, HasPostedDate, HasAlternateLinkUri, HasApplicationType, HasRenderer, HasVerb, HasAuthors> params(StreamsEntry entry) {
        return entry.params;
    }

    public StreamsEntry toStaticEntry() {
        StreamsEntry copy = new StreamsEntry(new Parameters<HasId, HasPostedDate, HasAlternateLinkUri, HasApplicationType, HasRenderer, HasVerb, HasAuthors>(((Parameters)this.params).id, ((Parameters)this.params).postedDate, ((Parameters)this.params).applicationType, ((Parameters)this.params).renderer, (Iterable)ImmutableList.copyOf((Iterable)((Parameters)this.params).activityObjects), ((Parameters)this.params).verb, ((Parameters)this.params).target, ((Parameters)this.params).alternateLinkUri, ImmutableNonEmptyList.copyOf(((Parameters)this.params).authors), (Iterable)ImmutableList.copyOf((Iterable)((Parameters)this.params).categories), ((Parameters)this.params).links, ((Parameters)this.params).inReplyTo, ((Parameters)this.params).baseUri), this.i18nResolver);
        copy.renderContentAsHtml();
        copy.renderSummaryAsHtml();
        copy.renderTitleAsHtml();
        return copy;
    }

    @Deprecated
    public Multimap<String, Link> getLinks() {
        return ((Parameters)this.params).links;
    }

    public Map<String, Collection<Link>> getLinksMap() {
        return ((Parameters)this.params).links.asMap();
    }

    public Option<URI> getInReplyTo() {
        return ((Parameters)this.params).inReplyTo;
    }

    @Deprecated
    public DateTime getPostedDate() {
        return ((Parameters)this.params).postedDate;
    }

    public ZonedDateTime getPostedZonedDate() {
        return DateUtil.toZonedDate(((Parameters)this.params).postedDate);
    }

    public URI getAlternateLink() {
        return ((Parameters)this.params).alternateLinkUri;
    }

    public URI getId() {
        return ((Parameters)this.params).id;
    }

    public Iterable<String> getCategories() {
        return ((Parameters)this.params).categories;
    }

    public NonEmptyIterable<UserProfile> getAuthors() {
        return ((Parameters)this.params).authors;
    }

    public ActivityVerb getVerb() {
        return ((Parameters)this.params).verb;
    }

    public Option<ActivityObject> getTarget() {
        return ((Parameters)this.params).target;
    }

    public String getApplicationType() {
        return ((Parameters)this.params).applicationType;
    }

    public Option<URI> getBaseUri() {
        return ((Parameters)this.params).baseUri;
    }

    public Iterable<ActivityObject> getActivityObjects() {
        return ((Parameters)this.params).activityObjects;
    }

    public Html renderTitleAsHtml() {
        try {
            return (Html)this.titleAsHtml.get();
        }
        catch (Exception e) {
            return this.getErrorHtml(e);
        }
    }

    public Option<Html> renderSummaryAsHtml() {
        try {
            return (Option)this.summaryAsHtml.get();
        }
        catch (Exception e) {
            return Option.some(this.getErrorHtml(e));
        }
    }

    public Option<Html> renderContentAsHtml() {
        try {
            return (Option)this.contentAsHtml.get();
        }
        catch (Exception e) {
            return Option.some(this.getErrorHtml(e));
        }
    }

    private Html getErrorHtml(Exception e) {
        log.error("An unknown error occurred while rendering a Streams entry", (Throwable)e);
        return new Html(this.i18nResolver.getText("stream.error.unexpected.rendering.error"));
    }

    public static Ordering<StreamsEntry> byPostedDate() {
        return byPostedDate;
    }

    public static interface Renderer {
        public static final int SUMMARY_LIMIT = 250;

        public Html renderTitleAsHtml(StreamsEntry var1);

        public Option<Html> renderSummaryAsHtml(StreamsEntry var1);

        public Option<Html> renderContentAsHtml(StreamsEntry var1);
    }

    public static final class Link {
        private final URI href;
        private final String rel;
        private final Option<String> title;

        public Link(URI href, String rel, Option<String> title) {
            if (StringUtils.isBlank((CharSequence)((CharSequence)Preconditions.checkNotNull((Object)rel, (Object)"rel")))) {
                throw new IllegalArgumentException("rel cannot be blank");
            }
            this.rel = rel;
            this.href = href;
            this.title = title;
        }

        public String getRel() {
            return this.rel;
        }

        public Option<String> getTitle() {
            return this.title;
        }

        public URI getHref() {
            return this.href;
        }
    }

    public static final class ActivityObject {
        private final Parameters params;

        public ActivityObject(Parameters params) {
            this.params = (Parameters)Preconditions.checkNotNull((Object)params, (Object)"params");
        }

        public static Parameters params() {
            return Parameters.newParams(Option.none(String.class), Option.none(String.class), Option.none(Html.class), Option.none(URI.class), Option.none(ActivityObjectType.class), Option.none(String.class));
        }

        public Option<String> getId() {
            return this.params.id;
        }

        public Option<String> getTitle() {
            return this.params.title;
        }

        public Option<Html> getTitleAsHtml() {
            return this.params.titleAsHtml;
        }

        public Option<URI> getAlternateLinkUri() {
            return this.params.alternateLinkUri;
        }

        public Option<ActivityObjectType> getActivityObjectType() {
            return this.params.activityObjectType;
        }

        public Option<String> getSummary() {
            return this.params.summary;
        }

        public static class Parameters {
            private final Option<String> id;
            private final Option<String> title;
            private final Option<Html> titleAsHtml;
            private final Option<URI> alternateLinkUri;
            private final Option<ActivityObjectType> activityObjectType;
            private final Option<String> summary;

            private Parameters(Option<String> id, Option<String> title, Option<Html> titleAsHtml, Option<URI> alternateLinkUri, Option<ActivityObjectType> type, Option<String> summary) {
                this.id = id;
                this.title = title;
                this.titleAsHtml = titleAsHtml;
                this.alternateLinkUri = alternateLinkUri;
                this.activityObjectType = type;
                this.summary = summary;
            }

            private static Parameters newParams(Option<String> id, Option<String> title, Option<Html> titleAsHtml, Option<URI> alternateLinkUri, Option<ActivityObjectType> type, Option<String> summary) {
                return new Parameters(id, title, titleAsHtml, alternateLinkUri, type, summary);
            }

            public Parameters id(String id) {
                return Parameters.newParams(Option.some(Preconditions.checkNotNull((Object)id, (Object)"id")), this.title, this.titleAsHtml, this.alternateLinkUri, this.activityObjectType, this.summary);
            }

            public Parameters id(Option<String> id) {
                return Parameters.newParams((Option)Preconditions.checkNotNull(id, (Object)"id"), this.title, this.titleAsHtml, this.alternateLinkUri, this.activityObjectType, this.summary);
            }

            public Parameters title(Option<String> title) {
                return Parameters.newParams(this.id, (Option)Preconditions.checkNotNull(title, (Object)"title"), this.titleAsHtml, this.alternateLinkUri, this.activityObjectType, this.summary);
            }

            public Parameters titleAsHtml(Option<Html> titleAsHtml) {
                return Parameters.newParams(this.id, this.title, (Option)Preconditions.checkNotNull(titleAsHtml, (Object)"titleAsHtml"), this.alternateLinkUri, this.activityObjectType, this.summary);
            }

            public Parameters alternateLinkUri(URI alternateLinkUri) {
                return Parameters.newParams(this.id, this.title, this.titleAsHtml, Option.some(Preconditions.checkNotNull((Object)alternateLinkUri, (Object)"alternateLinkUri")), this.activityObjectType, this.summary);
            }

            public Parameters alternateLinkUri(Option<URI> alternateLinkUri) {
                return Parameters.newParams(this.id, this.title, this.titleAsHtml, (Option)Preconditions.checkNotNull(alternateLinkUri, (Object)"alternateLinkUri"), this.activityObjectType, this.summary);
            }

            public Parameters activityObjectType(ActivityObjectType activityObjectType) {
                return Parameters.newParams(this.id, this.title, this.titleAsHtml, this.alternateLinkUri, Option.some(Preconditions.checkNotNull((Object)activityObjectType, (Object)"activityObjectType")), this.summary);
            }

            public Parameters activityObjectType(Option<ActivityObjectType> activityObjectType) {
                return Parameters.newParams(this.id, this.title, this.titleAsHtml, this.alternateLinkUri, (Option)Preconditions.checkNotNull(activityObjectType, (Object)"activityObjectType"), this.summary);
            }

            public Parameters summary(Option<String> summary) {
                return Parameters.newParams(this.id, this.title, this.titleAsHtml, this.alternateLinkUri, this.activityObjectType, summary);
            }
        }
    }

    public static class Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> {
        private final URI id;
        private final URI alternateLinkUri;
        private final Renderer renderer;
        private final DateTime postedDate;
        private final NonEmptyIterable<UserProfile> authors;
        private final Iterable<String> categories;
        private final Multimap<String, Link> links;
        private final String applicationType;
        private final Iterable<ActivityObject> activityObjects;
        private final ActivityVerb verb;
        private final Option<ActivityObject> target;
        private final Option<URI> inReplyTo;
        private final Option<URI> baseUri;

        private Parameters(URI id, DateTime postedDate, String applicationType, Renderer renderer, Iterable<ActivityObject> activityObjects, ActivityVerb verb, Option<ActivityObject> target, URI alternateLinkUri, NonEmptyIterable<UserProfile> authors, Iterable<String> category, Multimap<String, Link> links, Option<URI> inReplyTo, Option<URI> baseUri) {
            this.id = id;
            this.postedDate = postedDate;
            this.applicationType = applicationType;
            this.renderer = renderer;
            this.activityObjects = activityObjects;
            this.verb = verb;
            this.target = target;
            this.alternateLinkUri = alternateLinkUri;
            this.authors = authors;
            this.categories = category;
            this.links = links;
            this.inReplyTo = inReplyTo;
            this.baseUri = baseUri;
        }

        private static <IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> newParams(URI id, DateTime postedDate, String applicationType, Renderer renderer, Iterable<ActivityObject> activityObjects, ActivityVerb verb, Option<ActivityObject> target, URI alternateLinkUri, NonEmptyIterable<UserProfile> authors, Iterable<String> categories, Multimap<String, Link> links, Option<URI> inReplyTo, Option<URI> baseUri) {
            return new Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus>(id, postedDate, applicationType, renderer, activityObjects, verb, target, alternateLinkUri, authors, categories, links, inReplyTo, baseUri);
        }

        public Parameters<HasId, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> id(URI id) {
            return Parameters.newParams((URI)Preconditions.checkNotNull((Object)id, (Object)"id"), this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<HasId, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> baseUri(URI baseUri) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, Option.some(Preconditions.checkNotNull((Object)baseUri, (Object)"baseUri")));
        }

        @Deprecated
        public Parameters<IdStatus, HasPostedDate, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> postedDate(DateTime postedDate) {
            return Parameters.newParams(this.id, (DateTime)Preconditions.checkNotNull((Object)postedDate, (Object)"postedDate"), this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, HasPostedDate, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> postedDate(ZonedDateTime postedDate) {
            return Parameters.newParams(this.id, (DateTime)Preconditions.checkNotNull((Object)DateUtil.fromZonedDate(postedDate), (Object)"postedDate"), this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, HasApplicationType, RendererStatus, VerbStatus, AuthorsStatus> applicationType(String applicationType) {
            return Parameters.newParams(this.id, this.postedDate, (String)Preconditions.checkNotNull((Object)applicationType, (Object)"applicationType"), this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, HasRenderer, VerbStatus, AuthorsStatus> renderer(Renderer renderer) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, (Renderer)Preconditions.checkNotNull((Object)renderer, (Object)"renderer"), this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> addActivityObjects(Iterable<ActivityObject> activityObjects) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, Iterables.concat(this.activityObjects, (Iterable)ImmutableList.copyOf(activityObjects)), this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> addActivityObject(ActivityObject activityObject) {
            return this.addActivityObjects((Iterable<ActivityObject>)ImmutableList.of((Object)activityObject));
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, HasVerb, AuthorsStatus> verb(ActivityVerb verb) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, (ActivityVerb)Preconditions.checkNotNull((Object)verb, (Object)"verb"), this.target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, HasAlternateLinkUri, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> alternateLinkUri(URI alternateLinkUri) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, (URI)Preconditions.checkNotNull((Object)alternateLinkUri, (Object)"alternateLinkUri"), this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, HasAuthors> authors(NonEmptyIterable<UserProfile> authors) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, ImmutableNonEmptyList.copyOf(authors), this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> categories(Iterable<String> category) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, (Iterable<String>)ImmutableList.copyOf(category), this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> addLinks(Iterable<Link> links) {
            ImmutableMultimap newLinks = ImmutableMultimap.builder().putAll(this.links).putAll((Multimap)Multimaps.index(links, (Function)new Function<Link, String>(){

                public String apply(Link link) {
                    return link.getRel();
                }
            })).build();
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, (Multimap<String, Link>)newLinks, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> addLinks(Link ... links) {
            return this.addLinks(Arrays.asList(links));
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> addLink(URI uri, String rel, Option<String> title) {
            return this.addLinks(new Link(uri, rel, title));
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> addLink(Option<URI> uri, String rel, Option<String> title) {
            Iterator<URI> iterator = uri.iterator();
            if (iterator.hasNext()) {
                URI u = iterator.next();
                return this.addLink(u, rel, title);
            }
            return this;
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> target(Option<ActivityObject> target) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, target, this.alternateLinkUri, this.authors, this.categories, this.links, this.inReplyTo, this.baseUri);
        }

        public Parameters<IdStatus, PostedDateStatus, AlternateLinkUriStatus, ApplicationTypeStatus, RendererStatus, VerbStatus, AuthorsStatus> inReplyTo(Option<URI> inReplyTo) {
            return Parameters.newParams(this.id, this.postedDate, this.applicationType, this.renderer, this.activityObjects, this.verb, this.target, this.alternateLinkUri, this.authors, this.categories, this.links, inReplyTo, this.baseUri);
        }
    }

    public static abstract class NeedsAuthors {
        NeedsAuthors() {
        }
    }

    public static abstract class HasAuthors {
        HasAuthors() {
        }
    }

    public static abstract class NeedsVerb {
        NeedsVerb() {
        }
    }

    public static abstract class HasVerb {
        HasVerb() {
        }
    }

    public static abstract class NeedsRenderer {
        NeedsRenderer() {
        }
    }

    public static abstract class HasRenderer {
        HasRenderer() {
        }
    }

    public static abstract class NeedsApplicationType {
        NeedsApplicationType() {
        }
    }

    public static abstract class HasApplicationType {
        HasApplicationType() {
        }
    }

    public static abstract class NeedsAlternateLinkUri {
        NeedsAlternateLinkUri() {
        }
    }

    public static abstract class HasAlternateLinkUri {
        HasAlternateLinkUri() {
        }
    }

    public static abstract class NeedsPostedDate {
        NeedsPostedDate() {
        }
    }

    public static abstract class HasPostedDate {
        HasPostedDate() {
        }
    }

    public static abstract class NeedsId {
        NeedsId() {
        }
    }

    public static abstract class HasId {
        HasId() {
        }
    }
}

