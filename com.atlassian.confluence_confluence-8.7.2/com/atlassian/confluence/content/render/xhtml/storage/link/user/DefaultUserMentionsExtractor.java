/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.storage.link.user;

import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.inlinetask.StorageInlineTaskConstants;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.content.render.xhtml.storage.link.user.MentionsParser;
import com.atlassian.confluence.content.render.xhtml.storage.link.user.UserMentionsExtractor;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageResourceIdentifierConstants;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.fugue.Iterables;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUserMentionsExtractor
implements UserMentionsExtractor {
    private static final Logger log = LoggerFactory.getLogger(DefaultUserMentionsExtractor.class);
    private final XmlEventReaderFactory readerFactory;
    private final List<? extends MentionsParser> delegates;
    private final ConfluenceUserResolver userResolver;
    private final Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller;

    public DefaultUserMentionsExtractor(ConfluenceUserResolver userResolver, XmlEventReaderFactory readerFactory, Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller) {
        this(userResolver, readerFactory, userResourceIdentifierUnmarshaller, Collections.emptyList());
    }

    @VisibleForTesting
    DefaultUserMentionsExtractor(ConfluenceUserResolver userResolver, XmlEventReaderFactory readerFactory, Unmarshaller<UserResourceIdentifier> userResourceIdentifierUnmarshaller, List<? extends MentionsParser> delegates) {
        this.userResolver = userResolver;
        this.readerFactory = readerFactory;
        this.delegates = delegates;
        this.userResourceIdentifierUnmarshaller = userResourceIdentifierUnmarshaller;
    }

    @Override
    public List<ConfluenceUser> extractMentionedUsers(XMLEventReader reader) throws XMLStreamException {
        return this.extractFilteredUsersFromContent(reader, DefaultUserMentionsExtractor.defaultMentionsPredicate());
    }

    private static Predicate<List<QName>> defaultMentionsPredicate() {
        return DefaultUserMentionsExtractor.ancestorElementNamesAre(StorageLinkConstants.LINK_ELEMENT, StorageResourceIdentifierConstants.USER_RESOURCE_QNAME).and(DefaultUserMentionsExtractor.ancestorElementNamesContains(StorageMacroConstants.MACRO_PARAMETER_ELEMENT).or(DefaultUserMentionsExtractor.ancestorElementNamesContains(StorageInlineTaskConstants.TASK_BODY_ELEMENT)).negate());
    }

    private static Predicate<List<QName>> ancestorElementNamesAre(final QName ... matchingElementNames) {
        return new Predicate<List<QName>>(){

            @Override
            public boolean test(@NonNull List<QName> elementPath) {
                if (elementPath.size() >= matchingElementNames.length) {
                    List<QName> lastNElements = this.lastNEntries(elementPath, matchingElementNames.length);
                    return Arrays.asList(matchingElementNames).equals(lastNElements);
                }
                return false;
            }

            private List<QName> lastNEntries(List<QName> elementPath, int entries) {
                return elementPath.subList(elementPath.size() - entries, elementPath.size());
            }
        };
    }

    private static Predicate<List<QName>> ancestorElementNamesContains(QName elementName) {
        return elementPath -> elementPath.contains(elementName);
    }

    private List<ConfluenceUser> extractFilteredUsersFromContent(XMLEventReader xmlEventReader, Predicate<List<QName>> decisionPredicate) throws XMLStreamException {
        ArrayList<ConfluenceUser> users = new ArrayList<ConfluenceUser>();
        LinkedList<QName> elementPath = new LinkedList<QName>();
        while (xmlEventReader.hasNext()) {
            XMLEvent nextEvent = xmlEventReader.peek();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                elementPath.add(startElement.getName());
                MentionsParser delegate = this.getHandler(startElement);
                if (delegate != null) {
                    XMLEventReader fragmentReader = this.readerFactory.createXmlFragmentEventReader(xmlEventReader);
                    List<String> userNames = delegate.extractUserMentions(fragmentReader, this);
                    users.addAll(userNames.stream().map(username -> this.userResolver.getUserByName((String)username)).collect(Collectors.toList()));
                    continue;
                }
                if (this.isUserMention(startElement, elementPath, decisionPredicate)) {
                    users.add(this.extractUser(xmlEventReader));
                    xmlEventReader.nextEvent();
                    continue;
                }
                xmlEventReader.nextEvent();
                continue;
            }
            if (nextEvent.isEndElement()) {
                elementPath.removeLast();
                xmlEventReader.nextEvent();
                continue;
            }
            xmlEventReader.nextEvent();
        }
        return users.stream().filter(user -> user != null).collect(Collectors.toList());
    }

    private boolean isUserMention(StartElement nextEvent, Deque<QName> elementTrail, Predicate<List<QName>> decisionPredicate) {
        return this.userResourceIdentifierUnmarshaller.handles(nextEvent, null) && decisionPredicate.test(Collections.unmodifiableList(Lists.newArrayList(elementTrail)));
    }

    private ConfluenceUser extractUser(XMLEventReader xmlEventReader) {
        try {
            UserResourceIdentifier userResourceIdentifier = this.userResourceIdentifierUnmarshaller.unmarshal(xmlEventReader, null, null);
            UserKey userKey = userResourceIdentifier.getUserKey();
            if (userKey != null) {
                return this.userResolver.getUserByKey(userKey);
            }
            return null;
        }
        catch (XhtmlException ex) {
            log.error("Failed to extract users from storage format", (Throwable)ex);
            return null;
        }
    }

    private MentionsParser getHandler(StartElement startElement) {
        return (MentionsParser)Iterables.findFirst(this.delegates, delegate -> delegate.handles(startElement)).getOrNull();
    }
}

