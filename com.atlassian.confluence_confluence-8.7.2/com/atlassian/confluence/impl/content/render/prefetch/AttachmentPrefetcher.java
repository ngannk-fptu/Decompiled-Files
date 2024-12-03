/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.util.FileTypeUtil
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.content.render.prefetch.ImageDetailsPrefetchDao;
import com.atlassian.confluence.impl.content.render.prefetch.ResourcePrefetcher;
import com.atlassian.confluence.impl.content.render.prefetch.event.AttachmentPrefetchEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.DefaultThumbnailManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.util.FileTypeUtil;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentPrefetcher
implements ResourcePrefetcher<AttachmentResourceIdentifier> {
    private static final Logger log = LoggerFactory.getLogger(AttachmentPrefetcher.class);
    private static final String CONTEXT_KEY = "prefetchedAttachments";
    private final AttachmentManager attachmentManager;
    private final EventPublisher eventPublisher;
    private final ImageDetailsPrefetchDao imageDetailsPrefetcher;

    public AttachmentPrefetcher(AttachmentManager attachmentManager, EventPublisher eventPublisher, ImageDetailsPrefetchDao imageDetailsPrefetcher) {
        this.imageDetailsPrefetcher = imageDetailsPrefetcher;
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
    }

    public static @NonNull Function<AttachmentResourceIdentifier, Optional<Attachment>> prefetchedAttachments(ConversionContext conversionContext) {
        return ri -> AttachmentPrefetcher.getPrefetchedAttachments(conversionContext).map(attachments -> (Attachment)attachments.get(ri));
    }

    private static Optional<Map<AttachmentResourceIdentifier, Attachment>> getPrefetchedAttachments(ConversionContext conversionContext) {
        return Optional.ofNullable((Map)conversionContext.getProperty(CONTEXT_KEY));
    }

    @Override
    public Class<AttachmentResourceIdentifier> getResourceItentifierType() {
        return AttachmentResourceIdentifier.class;
    }

    @Override
    public void prefetch(Set<AttachmentResourceIdentifier> attachmentIdentifiers, ConversionContext conversionContext) {
        HashMap prefetchMap = new HashMap();
        HashSet discards = new HashSet();
        ContentEntityObject contentEntity = conversionContext.getEntity();
        Stopwatch stopwatch = Stopwatch.createStarted();
        Collection<Attachment> fetchedAttachments = this.fetchAttachments(contentEntity);
        fetchedAttachments.forEach(attachment -> this.asResourceIdentifier((Attachment)attachment, conversionContext).ifPresent(identifier -> {
            if (attachmentIdentifiers.contains(identifier)) {
                prefetchMap.put(identifier, attachment);
            } else {
                discards.add(identifier);
            }
        }));
        List<Attachment> imageAttachments = AttachmentPrefetcher.getImageAttachments(prefetchMap.values());
        log.debug("Pre-fetching details for {} image attachments", (Object)imageAttachments.size());
        int prefetchedImages = this.imageDetailsPrefetcher.prefetchImageDetails(imageAttachments);
        log.debug("Storing {} pre-fetched attachment identifiers in context: {}", (Object)prefetchMap.size(), prefetchMap.keySet());
        conversionContext.setProperty(CONTEXT_KEY, prefetchMap);
        Sets.SetView unfetched = Sets.difference(attachmentIdentifiers, prefetchMap.keySet());
        if (!unfetched.isEmpty()) {
            log.debug("{} requested attachment identifiers could not be pre-fetched: {}", (Object)unfetched.size(), (Object)unfetched);
        }
        if (!discards.isEmpty()) {
            log.debug("{} fetched attachment identifiers discarded as unrequested: {}", (Object)discards.size(), discards);
        }
        if (prefetchedImages > 0) {
            log.debug("{} image details pre-fetched for {} image attachments", (Object)prefetchedImages, (Object)imageAttachments.size());
        }
        this.eventPublisher.publish((Object)AttachmentPrefetchEvent.builder(contentEntity).preFetchedAttachmentCount(prefetchMap.size()).totalAttachmentLoadCount(fetchedAttachments.size()).discardedAttachmentCount(discards.size()).unfetchedAttachmentCount(unfetched.size()).preFetchedImageDetailsCount(prefetchedImages).elapsedTime(Duration.ofMillis(stopwatch.elapsed(TimeUnit.MILLISECONDS))).build());
    }

    private static List<Attachment> getImageAttachments(Collection<Attachment> attachments) {
        return attachments.stream().filter(attachment -> AttachmentPrefetcher.isImage(attachment)).collect(Collectors.toList());
    }

    private static boolean isImage(Attachment attachment) {
        return DefaultThumbnailManager.isThumbnailable(FileTypeUtil.getContentType((String)attachment.getFileName()));
    }

    private Optional<AttachmentResourceIdentifier> asResourceIdentifier(Attachment attachment, ConversionContext context) {
        return Objects.equals(attachment.getContainer(), context.getEntity()) ? Optional.of(new AttachmentResourceIdentifier(attachment.getFileName())) : Optional.empty();
    }

    private Collection<Attachment> fetchAttachments(@Nullable ContentEntityObject contextEntity) {
        if (contextEntity != null) {
            List<Attachment> attachments = this.attachmentManager.getLatestVersionsOfAttachments(contextEntity);
            log.debug("Pre-fetched all {} attachments for context entity {}", (Object)attachments.size(), (Object)contextEntity);
            return Collections.unmodifiableList(attachments);
        }
        log.debug("Cannot prefetch attachments, ConversionContext entity is not present");
        return Collections.emptyList();
    }
}

