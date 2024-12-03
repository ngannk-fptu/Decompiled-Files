/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageUserResourceIdentifierUnmarshaller
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageUserResourceIdentifierUnmarshaller;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InlineTaskUserExtractor {
    private static final Logger log = LoggerFactory.getLogger(InlineTaskUserExtractor.class);
    private final UserAccessor userAccessor;
    private final StorageUserResourceIdentifierUnmarshaller userResourceUnmarshaller;

    public InlineTaskUserExtractor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
        this.userResourceUnmarshaller = new StorageUserResourceIdentifierUnmarshaller();
    }

    public List<ConfluenceUser> extractUsersForInlineTask(XMLEventReader xmlEventReader) throws XMLStreamException {
        ArrayList users = Lists.newArrayList();
        while (xmlEventReader.hasNext()) {
            XMLEvent maybeUser;
            StartElement startElement;
            XMLEvent nextEvent = xmlEventReader.nextEvent();
            if (!nextEvent.isStartElement() || !(startElement = nextEvent.asStartElement()).getName().getLocalPart().equals("link") || !(maybeUser = xmlEventReader.peek()).isStartElement() || !this.isUserMention(maybeUser.asStartElement())) continue;
            users.add(this.extractUser(xmlEventReader));
        }
        return Lists.newArrayList((Iterable)Collections2.filter((Collection)users, (Predicate)Predicates.notNull()));
    }

    private boolean isUserMention(StartElement nextEvent) {
        return this.userResourceUnmarshaller.handles(nextEvent, null);
    }

    private ConfluenceUser extractUser(XMLEventReader xmlEventReader) {
        try {
            UserResourceIdentifier userResourceIdentifier = this.userResourceUnmarshaller.unmarshal(xmlEventReader, null, null);
            UserKey userKey = userResourceIdentifier.getUserKey();
            if (userKey != null) {
                return this.userAccessor.getUserByKey(userKey);
            }
            return null;
        }
        catch (XhtmlException ex) {
            log.error("Failed to extract users from storage format", (Throwable)ex);
            return null;
        }
    }
}

