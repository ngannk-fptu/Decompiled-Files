/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.BodyContent
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.legacyapi.model.content.ContentBody
 *  com.atlassian.confluence.legacyapi.model.content.ContentRepresentation
 *  com.atlassian.confluence.legacyapi.service.Expansions
 *  com.atlassian.confluence.legacyapi.service.content.InvalidRepresentationException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.Lists
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.builder;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.legacyapi.model.content.ContentBody;
import com.atlassian.confluence.legacyapi.model.content.ContentRepresentation;
import com.atlassian.confluence.legacyapi.service.Expansions;
import com.atlassian.confluence.legacyapi.service.content.InvalidRepresentationException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class LegacyContentBodyBuilder {
    private static final Map<BodyType, Collection<ContentRepresentation>> ALLOWED_REPRESENTATIONS;
    private final Renderer viewRenderer;
    private final FormatConverter formatConverter;

    @Autowired
    public LegacyContentBodyBuilder(@ComponentImport Renderer viewRenderer, @ComponentImport FormatConverter formatConverter) {
        this.viewRenderer = viewRenderer;
        this.formatConverter = formatConverter;
    }

    public ContentBody build(BodyContent bodyContent, ContentRepresentation representation) {
        BodyType bodyType = bodyContent.getBodyType();
        if (!ALLOWED_REPRESENTATIONS.get(bodyType).contains(representation)) {
            throw new InvalidRepresentationException(representation, (Iterable)ALLOWED_REPRESENTATIONS.get(bodyType));
        }
        return this.makeContentBody(representation, bodyContent);
    }

    public Map<ContentRepresentation, ContentBody> makeContentBodies(BodyContent bodyContent, Expansions expansions) {
        Collection<ContentRepresentation> representations = ALLOWED_REPRESENTATIONS.get(bodyContent.getBodyType());
        if (representations == null) {
            throw new IllegalStateException("Do not know how to represent " + bodyContent.getBodyType());
        }
        HashMap<ContentRepresentation, ContentBody> bodies = new HashMap<ContentRepresentation, ContentBody>(representations.size());
        for (ContentRepresentation representation : representations) {
            if (expansions.canExpand(representation.toString())) {
                bodies.put(representation, this.makeContentBody(representation, bodyContent));
                continue;
            }
            bodies.put(representation, null);
        }
        return bodies;
    }

    private ContentBody makeContentBody(ContentRepresentation representation, BodyContent bodyContent) {
        switch (representation) {
            case RAW: {
                return this.makeRawBody(bodyContent);
            }
            case STORAGE: {
                return this.makeStorageBody(bodyContent);
            }
            case EDITOR: {
                return this.makeEditorBody(bodyContent);
            }
            case VIEW: {
                return this.makeViewBody(bodyContent);
            }
        }
        throw new IllegalArgumentException("Unknown body content type: " + bodyContent);
    }

    private ContentBody makeEditorBody(BodyContent bodyContent) {
        return new ContentBody(ContentRepresentation.EDITOR, this.formatConverter.convertToEditorFormat(bodyContent.getBody(), (RenderContext)bodyContent.getContent().toPageContext()));
    }

    private ContentBody makeViewBody(BodyContent bodyContent) {
        if (BodyType.WIKI.equals((Object)bodyContent.getBodyType())) {
            throw new UnsupportedOperationException("Viewing legacy wiki markup content not yet implemented");
        }
        return new ContentBody(ContentRepresentation.VIEW, this.viewRenderer.render(bodyContent.getContent()));
    }

    private ContentBody makeStorageBody(BodyContent bodyContent) {
        return new ContentBody(ContentRepresentation.STORAGE, bodyContent.getBody());
    }

    private ContentBody makeRawBody(BodyContent bodyContent) {
        return new ContentBody(ContentRepresentation.RAW, bodyContent.getBody());
    }

    static {
        HashMap allowedRepresentations = new HashMap();
        allowedRepresentations.put(BodyType.XHTML, Collections.unmodifiableList(Lists.newArrayList((Object[])new ContentRepresentation[]{ContentRepresentation.STORAGE, ContentRepresentation.EDITOR, ContentRepresentation.VIEW})));
        allowedRepresentations.put(BodyType.RAW, Collections.unmodifiableList(Lists.newArrayList((Object[])new ContentRepresentation[]{ContentRepresentation.RAW})));
        allowedRepresentations.put(BodyType.WIKI, Collections.unmodifiableList(Lists.newArrayList((Object[])new ContentRepresentation[]{ContentRepresentation.RAW, ContentRepresentation.VIEW})));
        ALLOWED_REPRESENTATIONS = Collections.unmodifiableMap(allowedRepresentations);
    }
}

