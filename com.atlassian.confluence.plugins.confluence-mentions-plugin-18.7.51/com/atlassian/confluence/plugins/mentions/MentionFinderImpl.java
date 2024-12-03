/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.storage.link.user.UserMentionsExtractor
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.user.Entity
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.storage.link.user.UserMentionsExtractor;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.mentions.api.MentionFinder;
import com.atlassian.user.Entity;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class MentionFinderImpl
implements MentionFinder {
    private static final Pattern USER_PROFILE_WIKI_MARKUP_LINK_PATTERN = Pattern.compile("\\[~[^\\\\,]+?\\]");
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final UserMentionsExtractor mentionExtractor;

    public MentionFinderImpl(XmlEventReaderFactory xmlEventReaderFactory, UserMentionsExtractor mentionExtractor) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.mentionExtractor = mentionExtractor;
    }

    @Override
    public Set<String> getMentionedUsernames(ContentEntityObject ceo) {
        return this.getMentionedUsernames(ceo.getBodyContent());
    }

    @Override
    public Set<String> getMentionedUsernames(BodyContent content) {
        return this.getMentionedUsernames(content.getBody(), content.getBodyType());
    }

    @Override
    public Set<String> getNewMentionedUsernames(BodyContent oldContent, BodyContent newContent) {
        Set<String> usernames = this.getMentionedUsernames(newContent);
        Set<String> oldUsernames = this.getMentionedUsernames(oldContent);
        for (String oldUsername : oldUsernames) {
            usernames.remove(oldUsername);
        }
        return usernames;
    }

    private Set<String> getMentionedUsernames(String body, BodyType bodyType) {
        if (bodyType == BodyType.WIKI) {
            return this.getMentionedUsernamesFromWikiMarkupContent(body);
        }
        if (bodyType == BodyType.XHTML) {
            return this.getMentionedUsernamesFromXhtmlContent(body);
        }
        return Collections.emptySet();
    }

    private Set<String> getMentionedUsernamesFromWikiMarkupContent(String content) {
        Matcher matcher = USER_PROFILE_WIKI_MARKUP_LINK_PATTERN.matcher(content);
        HashSet<String> usernames = new HashSet<String>();
        while (matcher.find()) {
            String link = content.substring(matcher.start(), matcher.end());
            String username = link.substring(2, link.length() - 1);
            usernames.add(username);
        }
        return usernames;
    }

    private Set<String> getMentionedUsernamesFromXhtmlContent(String content) {
        try {
            XMLEventReader reader = this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(content));
            return this.mentionExtractor.extractMentionedUsers(reader).stream().map(Entity::getName).collect(Collectors.toSet());
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}

