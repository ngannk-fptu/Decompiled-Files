/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.index.attachment.AttachmentExtractedTextManager;
import com.atlassian.confluence.index.attachment.AttachmentTextExtraction;
import com.atlassian.confluence.internal.index.attachment.DefaultAttachmentTextExtraction;
import com.atlassian.confluence.internal.index.attachment.DelegatingAttachmentTextExtractor;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class AttachmentTextExtractionFunction
implements BiFunction<Long, Integer, AttachmentTextExtraction> {
    private static final Logger log = LoggerFactory.getLogger(AttachmentTextExtractionFunction.class);
    private final AttachmentDao attachmentDao;
    private final AttachmentExtractedTextManager attachmentExtractedTextManager;
    private final DelegatingAttachmentTextExtractor delegatingAttachmentTextExtractor;
    private final Supplier<Boolean> shouldCompressTextExtraction;

    public AttachmentTextExtractionFunction(AttachmentDao attachmentDao, AttachmentExtractedTextManager attachmentExtractedTextManager, DelegatingAttachmentTextExtractor delegatingAttachmentTextExtractor, Supplier<Boolean> shouldCompressTextExtraction) {
        this.attachmentDao = Objects.requireNonNull(attachmentDao);
        this.attachmentExtractedTextManager = Objects.requireNonNull(attachmentExtractedTextManager);
        this.delegatingAttachmentTextExtractor = Objects.requireNonNull(delegatingAttachmentTextExtractor);
        this.shouldCompressTextExtraction = Objects.requireNonNull(shouldCompressTextExtraction);
    }

    @Override
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public AttachmentTextExtraction apply(Long attachmentId, Integer version) {
        log.debug("Text extraction for {} starting", (Object)attachmentId);
        long start = System.currentTimeMillis();
        Attachment attachment = this.attachmentDao.getById(attachmentId);
        if (attachment == null) {
            log.debug("Attachment {} has been removed", (Object)attachmentId);
            return DefaultAttachmentTextExtraction.empty();
        }
        if (attachment.getVersion() > version) {
            log.debug("Attachment {} has been updated to from version {} to version {}", new Object[]{attachmentId, version, attachment.getVersion()});
            return DefaultAttachmentTextExtraction.empty();
        }
        Optional<InputStreamSource> extracted = this.attachmentExtractedTextManager.getContent(attachment);
        boolean compressed = this.shouldCompressTextExtraction.get();
        if (extracted.isPresent()) {
            log.debug("Extracted text of {} is available", (Object)attachmentId);
            return DefaultAttachmentTextExtraction.of(extracted.get(), compressed);
        }
        extracted = this.delegatingAttachmentTextExtractor.extract(attachment);
        if (extracted.isPresent()) {
            log.debug("Text extraction for {} took {} ms", (Object)attachmentId, (Object)(System.currentTimeMillis() - start));
            this.attachmentExtractedTextManager.saveContent(attachment, extracted.get());
            this.attachmentExtractedTextManager.removePreviousVersionContent(attachment);
            return DefaultAttachmentTextExtraction.of(extracted.get(), compressed);
        }
        return DefaultAttachmentTextExtraction.empty();
    }
}

