/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentOwningContentResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.prefetch.AttachmentPrefetcher;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentResourceIdentifierResolver
implements ResourceIdentifierResolver<AttachmentResourceIdentifier, Attachment> {
    private static final Logger log = LoggerFactory.getLogger(AttachmentResourceIdentifierResolver.class);
    private final AttachmentManager attachmentManager;
    private final ResourceIdentifierResolver<AttachmentResourceIdentifier, ContentEntityObject> attachmentOwningContentResolver;
    private final BiFunction<AttachmentResourceIdentifier, ConversionContext, Optional<Attachment>> prefetcher;

    public AttachmentResourceIdentifierResolver(AttachmentManager attachmentManager, AttachmentOwningContentResolver attachmentOwningContentResolver) {
        this(attachmentManager, attachmentOwningContentResolver, (resourceIdentifer, context) -> AttachmentPrefetcher.prefetchedAttachments(context).apply((AttachmentResourceIdentifier)resourceIdentifer));
    }

    @VisibleForTesting
    AttachmentResourceIdentifierResolver(AttachmentManager attachmentManager, ResourceIdentifierResolver<AttachmentResourceIdentifier, ContentEntityObject> attachmentOwningContentResolver, BiFunction<AttachmentResourceIdentifier, ConversionContext, Optional<Attachment>> prefetcher) {
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.attachmentOwningContentResolver = Objects.requireNonNull(attachmentOwningContentResolver);
        this.prefetcher = Objects.requireNonNull(prefetcher);
    }

    @Override
    public Attachment resolve(AttachmentResourceIdentifier attachmentResourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        Optional<Attachment> prefetchedAttachment = this.prefetcher.apply(attachmentResourceIdentifier, conversionContext);
        if (prefetchedAttachment.isPresent()) {
            log.debug("Resolved pre-fetched {}", (Object)attachmentResourceIdentifier);
            return prefetchedAttachment.get();
        }
        log.debug("No pre-fetch, continuing with resolution of {}", (Object)attachmentResourceIdentifier);
        ContentEntityObject owningContent = this.attachmentOwningContentResolver.resolve(attachmentResourceIdentifier, conversionContext);
        return this.attachmentManager.getAttachment(owningContent, attachmentResourceIdentifier.getResourceName());
    }
}

