/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Index
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.EmbeddedSubCalendarsParser;
import com.atlassian.confluence.extra.calendar3.events.SpaceCalendarsEmbeddedEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class EmbeddedSubCalendarsExtractor
implements Extractor2 {
    public static final String EMBEDDED_SUB_CALENDAR_ID_FIELD_NAME = "embeddedSubCalendarId";
    private final EmbeddedSubCalendarsParser embeddedSubCalendarsParser;
    private EventPublisher eventPublisher;

    @Autowired
    public EmbeddedSubCalendarsExtractor(EmbeddedSubCalendarsParser embeddedSubCalendarsParser, @ComponentImport EventPublisher eventPublisher) {
        this.embeddedSubCalendarsParser = embeddedSubCalendarsParser;
        this.eventPublisher = eventPublisher;
    }

    public StringBuilder extractText(Object searchable) {
        return null;
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof ContentEntityObject)) {
            return Collections.emptyList();
        }
        HashSet<String> subCalendarIds = new HashSet<String>(this.embeddedSubCalendarsParser.getEmbeddedSubCalendarIds((ContentEntityObject)searchable));
        ContentEntityObject content = (ContentEntityObject)searchable;
        if (subCalendarIds.size() > 0) {
            ContentEntityObject container = content instanceof Comment ? ((Comment)content).getContainer() : content;
            String spaceKey = null;
            if (container instanceof AbstractPage) {
                spaceKey = ((AbstractPage)container).getSpaceKey();
            }
            if (spaceKey != null) {
                this.eventPublisher.publish((Object)new SpaceCalendarsEmbeddedEvent(subCalendarIds, spaceKey));
            }
        }
        return subCalendarIds.stream().map(id -> new FieldDescriptor(EMBEDDED_SUB_CALENDAR_ID_FIELD_NAME, id, FieldDescriptor.Store.YES, FieldDescriptor.Index.ANALYZED)).collect(Collectors.toList());
    }
}

