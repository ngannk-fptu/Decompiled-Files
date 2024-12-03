/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Content$IdProperties
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.dragdrop.providers;

import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.dragdrop.service.DragAndDropService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AttachmentEditorHtmlProvider
implements ModelMetadataProvider {
    private static final Logger log = LoggerFactory.getLogger(AttachmentEditorHtmlProvider.class);
    private static final String EDITOR_HTML_EXPAND = "editorHtml";
    private DragAndDropService dragAndDropService;
    private ContentEntityManager contentEntityManager;

    @Autowired
    public AttachmentEditorHtmlProvider(DragAndDropService dragAndDropService, @ComponentImport ContentEntityManager contentEntityManager) {
        this.dragAndDropService = dragAndDropService;
        this.contentEntityManager = contentEntityManager;
    }

    public Map<Object, Map<String, ?>> getMetadataForAll(Iterable<Object> entities, Expansions expansions) {
        Iterable contents = Iterables.filter(entities, Content.class);
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        contents.forEach(content -> {
            try {
                if (ContentType.ATTACHMENT == content.getType()) {
                    ContentEntityObject container = this.contentEntityManager.getById(this.getContainerId((Content)content));
                    String editorHtml = this.dragAndDropService.getAttachmentEditorHtml(content.getTitle(), container);
                    mapBuilder.put(content, Collections.singletonMap(EDITOR_HTML_EXPAND, editorHtml));
                }
            }
            catch (Exception e) {
                log.error("Can not render editor html of attachment id {}", (Object)content.getId(), (Object)e);
            }
        });
        return mapBuilder.build();
    }

    public List<String> getMetadataProperties() {
        return Collections.singletonList(EDITOR_HTML_EXPAND);
    }

    private long getContainerId(Content content) {
        Object containerId;
        Reference reference = content.getContainerRef();
        if (reference != null && (containerId = reference.getIdProperty((Enum)Content.IdProperties.id)) instanceof ContentId) {
            return ((ContentId)containerId).asLong();
        }
        throw new ServiceException("can not get container id");
    }
}

