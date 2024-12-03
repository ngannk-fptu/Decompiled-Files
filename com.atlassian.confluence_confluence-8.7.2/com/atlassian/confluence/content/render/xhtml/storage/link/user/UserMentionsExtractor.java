/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.link.user;

import com.atlassian.confluence.user.ConfluenceUser;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public interface UserMentionsExtractor {
    public List<ConfluenceUser> extractMentionedUsers(XMLEventReader var1) throws XMLStreamException;
}

