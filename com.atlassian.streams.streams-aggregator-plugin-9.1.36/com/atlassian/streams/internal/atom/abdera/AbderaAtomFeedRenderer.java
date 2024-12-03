/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.ActivityObjectType
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.FeedContentSanitizer
 *  com.atlassian.streams.api.Html
 *  com.atlassian.streams.api.StreamsEntry
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Link
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uri
 *  com.atlassian.streams.spi.FormatPreferenceProvider
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 *  org.joda.time.format.DateTimeFormat
 *  org.joda.time.format.DateTimeFormatter
 */
package com.atlassian.streams.internal.atom.abdera;

import com.atlassian.streams.api.ActivityObjectType;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.FeedContentSanitizer;
import com.atlassian.streams.api.Html;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uri;
import com.atlassian.streams.internal.Predicates;
import com.atlassian.streams.internal.atom.abdera.ActivityObject;
import com.atlassian.streams.internal.atom.abdera.AtomConstants;
import com.atlassian.streams.internal.atom.abdera.AuthorisationMessage;
import com.atlassian.streams.internal.atom.abdera.StreamsAbdera;
import com.atlassian.streams.internal.feed.ActivitySourceBannedFeedHeader;
import com.atlassian.streams.internal.feed.ActivitySourceThrottledFeedHeader;
import com.atlassian.streams.internal.feed.ActivitySourceTimeOutFeedHeader;
import com.atlassian.streams.internal.feed.AuthRequiredFeedHeader;
import com.atlassian.streams.internal.feed.FeedEntry;
import com.atlassian.streams.internal.feed.FeedHeader;
import com.atlassian.streams.internal.feed.FeedModel;
import com.atlassian.streams.internal.feed.FeedRenderer;
import com.atlassian.streams.internal.feed.FeedRendererContext;
import com.atlassian.streams.spi.FormatPreferenceProvider;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.abdera.Abdera;
import org.apache.abdera.ext.thread.InReplyTo;
import org.apache.abdera.ext.thread.ThreadConstants;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Text;
import org.apache.abdera.parser.stax.FOMFactory;
import org.apache.abdera.parser.stax.FOMGenerator;
import org.apache.abdera.util.Constants;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AbderaAtomFeedRenderer
implements FeedRenderer {
    private static final String FEED_CONTENT_TYPE = "application/atom+xml";
    private final Abdera abdera = StreamsAbdera.getAbdera();
    private final FOMFactory factory = new FOMFactory(this.abdera);
    private final FormatPreferenceProvider formatPreferenceProvider;
    private final DateTimeFormatter timeZoneFormatter = DateTimeFormat.forPattern((String)"Z");
    private final FeedContentSanitizer sanitizer;

    public AbderaAtomFeedRenderer(FormatPreferenceProvider formatPreferenceProvider, FeedContentSanitizer sanitizer) {
        this.formatPreferenceProvider = (FormatPreferenceProvider)Preconditions.checkNotNull((Object)formatPreferenceProvider, (Object)"formatPreferenceProvider");
        this.sanitizer = (FeedContentSanitizer)Preconditions.checkNotNull((Object)sanitizer, (Object)"sanitizer");
    }

    @Override
    public void writeFeed(URI baseUri, FeedModel feed, Writer writer, FeedRendererContext context) throws IOException {
        Feed outputFeed = feed.getEncodedContent().isDefined() ? (Feed)feed.getEncodedContent().get() : new AtomFeedBuilder(baseUri, context).build(feed);
        Object abderaWriter = context.isDeveloperMode() ? this.abdera.getWriterFactory().getWriter("PrettyXML") : this.abdera.getWriterFactory().getWriter();
        outputFeed.writeTo((org.apache.abdera.writer.Writer)abderaWriter, writer);
    }

    @Override
    public String getContentType() {
        return FEED_CONTENT_TYPE;
    }

    private final class AtomFeedBuilder {
        private final FeedRendererContext context;
        private final Feed outputFeed;
        private final URI baseUri;

        AtomFeedBuilder(URI baseUri, FeedRendererContext context) {
            this.baseUri = baseUri;
            this.context = context;
            this.outputFeed = AbderaAtomFeedRenderer.this.abdera.newFeed();
        }

        Feed build(FeedModel feed) {
            this.outputFeed.setId(feed.getUri().toString());
            this.outputFeed.addLink(feed.getUri().toString(), "self");
            this.outputFeed.setTitle((String)feed.getTitle().getOrElse((Object)this.context.getDefaultFeedTitle()));
            this.addTimeZoneOffset(this.outputFeed, new DateTime());
            for (String subtitle : feed.getSubtitle()) {
                this.outputFeed.setSubtitle(subtitle);
            }
            for (DateTime updated : feed.getUpdated()) {
                this.outputFeed.setUpdated(updated.toDate());
            }
            for (FeedHeader header : feed.getHeaders()) {
                this.addHeaderExtension(this.outputFeed, header);
            }
            for (Map.Entry link : feed.getLinks().entries()) {
                this.outputFeed.addLink(((Uri)link.getValue()).toString(), (String)link.getKey());
            }
            for (FeedEntry entry : feed.getEntries()) {
                Entry atomEntry = entry instanceof StreamsAbdera.AtomParsedFeedEntry ? ((StreamsAbdera.AtomParsedFeedEntry)entry).getAtomEntry() : this.getAtomEntry(entry.getStreamsEntry());
                this.addTimeZoneOffset(atomEntry, entry.getEntryDate());
                for (FeedModel sourceFeed : entry.getSourceFeed()) {
                    for (Object sourceAtomFeed : sourceFeed.getEncodedContent()) {
                        atomEntry.addExtension(((Feed)sourceAtomFeed).getAsSource());
                    }
                }
                this.outputFeed.addEntry(atomEntry);
            }
            if (this.outputFeed.getEntries().isEmpty()) {
                this.outputFeed.addAuthor(this.context.getDefaultFeedAuthor());
            }
            return this.outputFeed;
        }

        private void addHeaderExtension(Feed outputFeed, FeedHeader header) {
            if (header instanceof StreamsAbdera.AtomParsedFeedHeader) {
                outputFeed.addExtension(((StreamsAbdera.AtomParsedFeedHeader)header).getElement());
            }
            if (header instanceof AuthRequiredFeedHeader) {
                this.addAuthRequiredExtension(outputFeed, (AuthRequiredFeedHeader)header);
            }
            if (header instanceof ActivitySourceTimeOutFeedHeader) {
                this.addTimedOutSource(outputFeed, ((ActivitySourceTimeOutFeedHeader)header).getSourceName());
            }
            if (header instanceof ActivitySourceThrottledFeedHeader) {
                this.addThrottledSource(outputFeed, ((ActivitySourceThrottledFeedHeader)header).getSourceName());
            }
            if (header instanceof ActivitySourceBannedFeedHeader) {
                this.addBannedSource(outputFeed, ((ActivitySourceBannedFeedHeader)header).getSourceName());
            }
        }

        private void addAuthRequiredExtension(Feed outputFeed, AuthRequiredFeedHeader header) {
            AuthorisationMessage authRequest = (AuthorisationMessage)outputFeed.addExtension(AtomConstants.ATLASSIAN_AUTHORISATION_MESSAGE);
            authRequest.setApplicationId(header.getApplicationId());
            authRequest.setApplicationName(header.getApplicationName());
            authRequest.setApplicationUri(header.getApplicationUri());
            authRequest.setAuthorisationUri(header.getAuthUri());
        }

        private Entry getAtomEntry(StreamsEntry streamEntry) {
            Entry atomEntry = AbderaAtomFeedRenderer.this.abdera.newEntry();
            Html title = streamEntry.renderTitleAsHtml();
            Option summary = streamEntry.renderSummaryAsHtml();
            Option content = streamEntry.renderContentAsHtml();
            atomEntry.setId(streamEntry.getId().toASCIIString());
            atomEntry.setTitle(title.toString(), Text.Type.HTML);
            for (Html s : summary) {
                atomEntry.setSummary(AbderaAtomFeedRenderer.this.sanitizer.sanitize(s.toString()), Text.Type.HTML);
            }
            for (Html c : content) {
                atomEntry.setContent(AbderaAtomFeedRenderer.this.sanitizer.sanitize(c.toString()), Content.Type.HTML);
            }
            for (Person person : this.buildAuthors(streamEntry)) {
                atomEntry.addAuthor(person);
            }
            atomEntry.setPublished(streamEntry.getPostedDate().toDate());
            atomEntry.setUpdated(streamEntry.getPostedDate().toDate());
            this.addCategories(atomEntry, streamEntry.getCategories());
            if (Predicates.isAbsolute(streamEntry.getAlternateLink())) {
                atomEntry.addLink(streamEntry.getAlternateLink().toASCIIString(), "alternate");
            }
            for (StreamsEntry.Link link : Iterables.filter((Iterable)streamEntry.getLinks().values(), (Predicate)com.google.common.base.Predicates.and(Predicates.linkHref(Predicates.isAbsolute()), Predicates.linkRel((Predicate<String>)com.google.common.base.Predicates.not((Predicate)com.google.common.base.Predicates.equalTo((Object)"alternate")))))) {
                if (link.getTitle().isDefined()) {
                    atomEntry.addLink(AbderaAtomFeedRenderer.this.factory.newLink().setHref(link.getHref().toASCIIString()).setRel(link.getRel()).setTitle((String)link.getTitle().get()));
                    continue;
                }
                atomEntry.addLink(link.getHref().toASCIIString(), link.getRel());
            }
            for (URI uri : Iterables.filter((Iterable)streamEntry.getInReplyTo(), Predicates.isAbsolute())) {
                InReplyTo inReplyTo = (InReplyTo)atomEntry.addExtension(ThreadConstants.IN_REPLY_TO);
                inReplyTo.setRef(uri.toASCIIString());
            }
            atomEntry.addExtension(this.buildGenerator(this.baseUri.toASCIIString()));
            atomEntry.addSimpleExtension(AtomConstants.ATLASSIAN_APPLICATION, streamEntry.getApplicationType());
            this.addActivityVerbs(atomEntry, streamEntry.getVerb());
            this.addActivityObjectElements(atomEntry, streamEntry.getActivityObjects());
            this.addActivityTargetElement(atomEntry, (Option<StreamsEntry.ActivityObject>)streamEntry.getTarget());
            return atomEntry;
        }

        private void addTimeZoneOffset(ExtensibleElement element, DateTime updated) {
            String offsetValue = AbderaAtomFeedRenderer.this.timeZoneFormatter.withZone(AbderaAtomFeedRenderer.this.formatPreferenceProvider.getUserTimeZone()).print((ReadableInstant)updated.toInstant());
            Object existingOffset = element.getExtension(AtomConstants.ATLASSIAN_TIMEZONE_OFFSET);
            if (existingOffset == null) {
                element.addSimpleExtension(AtomConstants.ATLASSIAN_TIMEZONE_OFFSET, offsetValue);
            } else {
                existingOffset.setText(offsetValue);
            }
        }

        private void addTimedOutSource(ExtensibleElement element, String sourceName) {
            ExtensibleElement timedOutSourceList = (ExtensibleElement)element.getExtension(AtomConstants.ATLASSIAN_TIMED_OUT_ACTIVITY_SOURCE_LIST);
            if (timedOutSourceList == null) {
                timedOutSourceList = (ExtensibleElement)element.addExtension(AtomConstants.ATLASSIAN_TIMED_OUT_ACTIVITY_SOURCE_LIST);
            }
            timedOutSourceList.addSimpleExtension(AtomConstants.ATLASSIAN_TIMED_OUT_ACTIVITY_SOURCE, sourceName);
        }

        private void addThrottledSource(ExtensibleElement element, String sourceName) {
            ExtensibleElement timedOutSourceList = (ExtensibleElement)element.getExtension(AtomConstants.ATLASSIAN_THROTTLED_ACTIVITY_SOURCE_LIST);
            if (timedOutSourceList == null) {
                timedOutSourceList = (ExtensibleElement)element.addExtension(AtomConstants.ATLASSIAN_THROTTLED_ACTIVITY_SOURCE_LIST);
            }
            timedOutSourceList.addSimpleExtension(AtomConstants.ATLASSIAN_THROTTLED_ACTIVITY_SOURCE, sourceName);
        }

        private void addBannedSource(ExtensibleElement element, String sourceName) {
            ExtensibleElement bannedSourceList = (ExtensibleElement)element.getExtension(AtomConstants.ATLASSIAN_BANNED_ACTIVITY_SOURCE_LIST);
            if (bannedSourceList == null) {
                bannedSourceList = (ExtensibleElement)element.addExtension(AtomConstants.ATLASSIAN_BANNED_ACTIVITY_SOURCE_LIST);
            }
            bannedSourceList.addSimpleExtension(AtomConstants.ATLASSIAN_BANNED_ACTIVITY_SOURCE, sourceName);
        }

        private void addCategories(Entry atomEntry, Iterable<String> categories) {
            if (categories != null) {
                for (String category : categories) {
                    atomEntry.addCategory(category);
                }
            }
        }

        private void addActivityVerbs(Entry atomEntry, ActivityVerb verb) {
            for (ActivityVerb parent : verb.parent()) {
                this.addActivityVerbs(atomEntry, parent);
            }
            atomEntry.addSimpleExtension(AtomConstants.ACTIVITY_VERB, verb.iri().toASCIIString());
        }

        private void addActivityObjectElements(Entry atomEntry, Iterable<? extends StreamsEntry.ActivityObject> activityObjects) {
            for (StreamsEntry.ActivityObject activityObject : activityObjects) {
                this.addActivityObjectAsElement(atomEntry, activityObject, AtomConstants.ACTIVITY_OBJECT);
            }
        }

        private void addActivityTargetElement(Entry atomEntry, Option<StreamsEntry.ActivityObject> target) {
            for (StreamsEntry.ActivityObject o : target) {
                this.addActivityObjectAsElement(atomEntry, o, AtomConstants.ACTIVITY_TARGET);
            }
        }

        private void addActivityObjectAsElement(Entry atomEntry, StreamsEntry.ActivityObject object, QName element) {
            ActivityObject ao = (ActivityObject)atomEntry.addExtension(element);
            for (String id : object.getId()) {
                ao.setId(id);
            }
            for (String title : Iterables.filter((Iterable)object.getTitle(), (Predicate)com.google.common.base.Predicates.not(Predicates.blank()))) {
                ao.setTitle(title);
            }
            for (Html titleAsHtml : object.getTitleAsHtml()) {
                ao.setTitle(titleAsHtml.toString());
            }
            for (String summary : Iterables.filter((Iterable)object.getSummary(), (Predicate)com.google.common.base.Predicates.not(Predicates.blank()))) {
                ao.setSummary(summary);
            }
            for (URI uri : object.getAlternateLinkUri()) {
                if (!Predicates.isAbsolute(uri)) continue;
                ao.setAlternateLink(uri);
            }
            for (ActivityObjectType type : object.getActivityObjectType()) {
                ao.setObjectType(type);
            }
        }

        private List<Person> buildAuthors(StreamsEntry entry) {
            ArrayList<Person> people = new ArrayList<Person>();
            for (UserProfile author : entry.getAuthors()) {
                Person person = this.buildPerson(author, entry.getApplicationType());
                people.add(person);
            }
            if (people.isEmpty()) {
                people.add(this.buildAnonPerson(entry.getApplicationType()));
            }
            return people;
        }

        private Person buildPerson(UserProfile profile, String application) {
            Person person = AbderaAtomFeedRenderer.this.abdera.getFactory().newAuthor();
            person.setName(profile.getFullName() != null ? profile.getFullName() : profile.getUsername());
            for (String email : Iterables.filter((Iterable)profile.getEmail(), (Predicate)com.google.common.base.Predicates.not(Predicates.blank()))) {
                person.setEmail(email);
            }
            for (URI uri : profile.getProfilePageUri()) {
                person.setUri(uri.toASCIIString());
            }
            this.addProfilePictureLinks(person, (Option<URI>)profile.getProfilePictureUri(), application);
            person.addSimpleExtension(AtomConstants.USR_USERNAME, profile.getUsername());
            person.addSimpleExtension(AtomConstants.ACTIVITY_OBJECT_TYPE, "http://activitystrea.ms/schema/1.0/person");
            return person;
        }

        private FOMGenerator buildGenerator(String uri) {
            FOMGenerator generator = (FOMGenerator)AbderaAtomFeedRenderer.this.abdera.getFactory().newExtensionElement(Constants.GENERATOR);
            generator.setUri(uri);
            return generator;
        }

        private Person buildAnonPerson(String application) {
            Person person = AbderaAtomFeedRenderer.this.abdera.getFactory().newAuthor();
            person.setName(this.context.getAnonymousUserName());
            this.addProfilePictureLinks(person, (Option<URI>)Option.none(URI.class), application);
            return person;
        }

        private void addProfilePictureLinks(Person person, Option<URI> pictureUri, String application) {
            for (int size : this.context.getDefaultUserPictureSizes()) {
                Option<URI> maybeLinkUri = this.context.getUserPictureUri(pictureUri, size, application);
                for (URI linkUri : maybeLinkUri) {
                    Link link = AbderaAtomFeedRenderer.this.abdera.getFactory().newLink();
                    link.setRel("photo");
                    link.setHref(linkUri.toASCIIString());
                    String sizeStr = String.valueOf(size);
                    link.setAttributeValue(AtomConstants.MEDIA_HEIGHT, sizeStr);
                    link.setAttributeValue(AtomConstants.MEDIA_WIDTH, sizeStr);
                    person.addExtension(link);
                }
            }
        }
    }
}

