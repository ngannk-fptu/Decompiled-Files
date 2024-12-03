/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentBody$ContentBodyBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.service.exceptions.InvalidRepresentationException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.fugue.Pair
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.ContentBodyConversionManager;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.service.exceptions.InvalidRepresentationException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.fugue.Pair;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContentBodyFactory {
    private static final Map<BodyType, Collection<ContentRepresentation>> ALLOWED_REPRESENTATIONS;
    private final ContentBodyConversionManager contentBodyConversionManager;

    public ContentBodyFactory(ContentBodyConversionManager contentBodyConversionManager) {
        this.contentBodyConversionManager = contentBodyConversionManager;
    }

    public ContentBody build(ContentEntityObject ceo, BodyContent bodyContent, ContentRepresentation representation) throws ServiceException {
        BodyType bodyType = bodyContent.getBodyType();
        if (!ALLOWED_REPRESENTATIONS.get(bodyType).contains(representation)) {
            throw new InvalidRepresentationException(representation, (Iterable)ALLOWED_REPRESENTATIONS.get(bodyType));
        }
        return this.makeContentBody(ceo, representation, bodyContent, null, new Expansion[0]);
    }

    public Map<ContentRepresentation, ContentBody> makeContentBodies(ContentEntityObject ceo, BodyContent bodyContent, Expansions expansions, ContentFactory contentFactory) {
        Collection<ContentRepresentation> representations = ALLOWED_REPRESENTATIONS.get(bodyContent.getBodyType());
        if (representations == null) {
            throw new IllegalStateException("Do not know how to represent " + bodyContent.getBodyType());
        }
        ModelMapBuilder mapBuilder = ModelMapBuilder.newInstance();
        for (ContentRepresentation representation : representations) {
            if (expansions.canExpand(representation.getRepresentation())) {
                Reference contentReference;
                Expansions representationExpansions = expansions.getSubExpansions(representation.toString());
                if (representationExpansions.canExpand("content")) {
                    Expansions contentExpansions = representationExpansions.getSubExpansions("content");
                    contentReference = Reference.to((Object)contentFactory.buildFrom(bodyContent.getContent(), contentExpansions));
                } else {
                    contentReference = Content.buildReference((ContentSelector)bodyContent.getContent().getSelector());
                }
                ContentBody contentBody = this.makeContentBody(ceo, representation, bodyContent, (Reference<Content>)contentReference, representationExpansions.toArray());
                mapBuilder.put((Object)representation, (Object)contentBody);
                continue;
            }
            mapBuilder.addCollapsedEntry((Object)representation);
        }
        return mapBuilder.build();
    }

    private ContentBody makeContentBody(ContentEntityObject ceo, ContentRepresentation targetRepresentation, BodyContent bodyContent, Reference<Content> contentReference, Expansion ... expansions) {
        ContentBody contentBody = this.makeContentBody(bodyContent, contentReference);
        if (contentBody.getRepresentation().equals((Object)targetRepresentation)) {
            return contentBody;
        }
        Pair<String, Reference<WebResourceDependencies>> convertedValue = this.contentBodyConversionManager.convert(contentBody.getRepresentation(), contentBody.getValue(), targetRepresentation, ceo, expansions);
        Reference webresource = (Reference)convertedValue.right();
        return ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().representation(contentBody.getRepresentation())).value((String)convertedValue.left())).content(ceo.getSelector()).webresource(webresource)).build();
    }

    private ContentBody makeContentBody(BodyContent bodyContent, Reference<Content> contentReference) {
        if (contentReference == null) {
            contentReference = Content.buildReference((ContentSelector)bodyContent.getContent().getSelector());
        }
        return ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().representation(bodyContent.getBodyType().toContentRepresentation())).value(bodyContent.getBody())).content(contentReference).build();
    }

    static {
        HashMap<BodyType, ImmutableList> allowedRepresentations = new HashMap<BodyType, ImmutableList>();
        allowedRepresentations.put(BodyType.XHTML, ImmutableList.of((Object)ContentRepresentation.STORAGE, (Object)ContentRepresentation.EDITOR, (Object)ContentRepresentation.VIEW, (Object)ContentRepresentation.EXPORT_VIEW, (Object)ContentRepresentation.ANONYMOUS_EXPORT_VIEW, (Object)ContentRepresentation.STYLED_VIEW));
        allowedRepresentations.put(BodyType.RAW, ImmutableList.of((Object)ContentRepresentation.RAW));
        allowedRepresentations.put(BodyType.WIKI, ImmutableList.of((Object)ContentRepresentation.RAW, (Object)ContentRepresentation.VIEW, (Object)ContentRepresentation.STORAGE, (Object)ContentRepresentation.EDITOR, (Object)ContentRepresentation.EXPORT_VIEW, (Object)ContentRepresentation.ANONYMOUS_EXPORT_VIEW, (Object)ContentRepresentation.STYLED_VIEW));
        ALLOWED_REPRESENTATIONS = Collections.unmodifiableMap(allowedRepresentations);
    }
}

