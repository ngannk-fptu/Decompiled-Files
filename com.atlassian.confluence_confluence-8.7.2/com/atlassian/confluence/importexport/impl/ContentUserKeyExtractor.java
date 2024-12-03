/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.importexport.impl.StorageFormatUserRewriter;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import javax.xml.stream.XMLStreamException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentUserKeyExtractor {
    private static final Logger log = LoggerFactory.getLogger(ContentUserKeyExtractor.class);
    private final StorageFormatUserRewriter storageFormatUserKeyScanner;

    public ContentUserKeyExtractor(@NonNull StorageFormatUserRewriter storageFormatUserRewriter) {
        this.storageFormatUserKeyScanner = (StorageFormatUserRewriter)Preconditions.checkNotNull((Object)storageFormatUserRewriter);
    }

    public Set<UserKey> extractUserKeysFromContentEntities(Iterable<? extends ContentEntityObject> contentEntities, boolean includeComments) {
        HashSet userKeys = Sets.newHashSet();
        for (ContentEntityObject contentEntityObject : contentEntities) {
            userKeys.addAll(this.extractUserKeysFromContentEntity(contentEntityObject));
            if (!includeComments) continue;
            List<Comment> comments = contentEntityObject.getComments();
            userKeys.addAll(this.extractUserKeysFromContentEntities(comments, includeComments));
        }
        return userKeys;
    }

    private Set<UserKey> extractUserKeysFromContentEntity(ContentEntityObject contentEntity) {
        BodyContent bodyContent = contentEntity.getBodyContent();
        if (bodyContent.getBodyType() != BodyType.XHTML) {
            return Collections.emptySet();
        }
        return this.extractUserKeys(contentEntity, bodyContent);
    }

    private Set<UserKey> extractUserKeys(ContentEntityObject contentEntity, BodyContent xhtmlBodyContent) {
        try {
            return this.extractUserKeys(xhtmlBodyContent.getBody());
        }
        catch (XhtmlException | XMLStreamException ex) {
            log.warn("Failed to extract user references from {}. Enable debug logging to see details.", (Object)contentEntity);
            log.debug(ex.getMessage());
            return Collections.emptySet();
        }
    }

    public Set<UserKey> extractUserKeys(String storageFormatContent) throws XMLStreamException, XhtmlException {
        try {
            return this.storageFormatUserKeyScanner.transformUserKeysInContent(storageFormatContent, Function.identity(), new DefaultConversionContext(new RenderContext())).getOriginalUserKeys();
        }
        catch (IOException ex) {
            log.error("Failed to extract user keys from storage format", (Throwable)ex);
            return Collections.emptySet();
        }
    }
}

