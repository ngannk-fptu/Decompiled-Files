/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.link.user;

import com.atlassian.confluence.content.render.xhtml.storage.link.user.UserMentionsExtractor;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

@Deprecated
public interface MentionsParser {
    @Deprecated
    public List<String> extractUserMentions(XMLEventReader var1, UserMentionsExtractor var2) throws XMLStreamException;

    public boolean handles(StartElement var1);
}

