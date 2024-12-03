/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentBody$ContentBodyBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.service.content.ContentBodyConversionService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 *  com.atlassian.fugue.Pair
 */
package com.atlassian.confluence.api.impl.service.content;

import com.atlassian.confluence.api.impl.service.content.ContentBodyConversionManager;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.service.content.ContentBodyConversionService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentEntityManagerInternal;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import com.atlassian.fugue.Pair;
import java.util.Map;
import java.util.Optional;

public class ContentBodyConversionServiceImpl
implements ContentBodyConversionService {
    private final ContentEntityManagerInternal contentEntityManager;
    private final ContentBodyConversionManager contentBodyConversionManager;

    public ContentBodyConversionServiceImpl(ContentEntityManagerInternal contentEntityManager, ContentBodyConversionManager contentBodyConversionManager) {
        this.contentEntityManager = contentEntityManager;
        this.contentBodyConversionManager = contentBodyConversionManager;
    }

    public ContentBody convertBody(Content content, ContentRepresentation toFormat) throws ServiceException {
        return this.convertBody(content, toFormat, ExpansionsParser.parse((String)""));
    }

    public ContentBody convertBody(Content content, ContentRepresentation toFormat, Expansion ... expansions) throws ServiceException {
        ContentBody bodyToConvert = this.selectBodyForRepresentation(content, toFormat).orElseThrow(() -> new BadRequestException("Can't convert Content with no suitable body for conversion to: " + toFormat));
        return this.convert(bodyToConvert, toFormat, expansions);
    }

    public ContentBody convert(ContentBody body, ContentRepresentation toFormat) throws ServiceException {
        return this.convert(body, toFormat, ExpansionsParser.parse((String)""));
    }

    public ContentBody convert(ContentBody contentBody, ContentRepresentation toFormat, Expansion ... expansions) throws ServiceException {
        ContentSelector selector = Content.getSelector((Reference)contentBody.getContentRef());
        try {
            ContentEntityObject ceo = this.getContent(selector);
            if (ceo == null && !selector.isEmpty()) {
                throw new NotFoundException("Cannot find content with selector " + selector.toString());
            }
            Pair<String, Reference<WebResourceDependencies>> convertedValue = this.contentBodyConversionManager.convert(contentBody.getRepresentation(), contentBody.getValue(), toFormat, ceo, expansions);
            Reference webresource = (Reference)convertedValue.right();
            return ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().representation(toFormat)).value((String)convertedValue.left())).content(selector).webresource(webresource)).build();
        }
        catch (ServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException((Throwable)e);
        }
    }

    public Optional<ContentBody> selectBodyForRepresentation(Content content, ContentRepresentation toFormat) {
        Map bodyMap = content.getBody();
        if (bodyMap.isEmpty()) {
            return Optional.empty();
        }
        if (!toFormat.convertsFromStorage()) {
            return Optional.empty();
        }
        for (ContentRepresentation inputFormat : ContentRepresentation.INPUT_CONVERSION_TO_STORAGE_ORDER) {
            if (!bodyMap.containsKey(inputFormat)) continue;
            return Optional.of((ContentBody)bodyMap.get(inputFormat));
        }
        return Optional.empty();
    }

    private ContentEntityObject getContent(ContentSelector selector) {
        ContentEntityObject ceo = this.contentEntityManager.getById(selector.getId().asLong());
        if (ceo == null || ceo instanceof CustomContentEntityObject) {
            return ceo;
        }
        ContentType contentType = ContentType.valueOf((String)ceo.getType());
        if (ContentType.BUILT_IN.contains(contentType) || "draft".equals(contentType.getType())) {
            return ceo;
        }
        return null;
    }
}

