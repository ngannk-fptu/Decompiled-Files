/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Searchable
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.index.attachment.AttachmentExtractedTextManager;
import com.atlassian.confluence.index.attachment.AttachmentTextExtraction;
import com.atlassian.confluence.index.attachment.AttachmentTextExtractionService;
import com.atlassian.confluence.index.attachment.AttachmentTextExtractor;
import com.atlassian.confluence.internal.index.attachment.AttachmentExtractedTextHelper;
import com.atlassian.confluence.internal.index.attachment.AttachmentStatus;
import com.atlassian.confluence.internal.index.attachment.AttachmentStatusManager;
import com.atlassian.confluence.internal.index.attachment.PluginAttachmentTextExtractorsProvider;
import com.atlassian.confluence.internal.index.attachment.ShouldExtractAttachmentTextPredicate;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.server.ApplicationState;
import com.atlassian.confluence.server.ApplicationStatusService;
import com.atlassian.confluence.util.io.InputStreamSource;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class AttachmentExtractedTextExtractor
implements Extractor2 {
    public static final String ATLASSIAN_INDEXING_ATTACHMENT_MAXSIZE = "atlassian.indexing.attachment.maxsize";
    public static final long ATLASSIAN_INDEXING_ATTACHMENT_MAXSIZE_DEFAULT = 0x6400000L;
    private static final Logger log = LoggerFactory.getLogger(AttachmentExtractedTextExtractor.class);
    private final long sizeLimit = AttachmentExtractedTextExtractor.getAttachmentSizeLimit();
    private final AttachmentExtractedTextManager attachmentExtractedTextManager;
    private final AttachmentTextExtractionService remoteAttachmentTextExtractionService;
    private final AttachmentStatusManager attachmentStatusManager;
    private final ApplicationStatusService applicationStatusService;
    private final PluginAttachmentTextExtractorsProvider pluginAttachmentTextExtractorsProvider;
    private final ShouldExtractAttachmentTextPredicate shouldExtractAttachmentTextPredicate;

    public AttachmentExtractedTextExtractor(AttachmentExtractedTextManager attachmentExtractedTextManager, AttachmentStatusManager attachmentStatusManager, ApplicationStatusService applicationStatusService, AttachmentTextExtractionService remoteAttachmentTextExtractionService, PluginAttachmentTextExtractorsProvider pluginAttachmentTextExtractorsProvider, ShouldExtractAttachmentTextPredicate shouldExtractAttachmentTextPredicate) {
        log.debug("AttachmentExtractedTextExtractor value for \"atlassian.indexing.attachment.maxsize\" is {}", (Object)this.sizeLimit);
        this.attachmentExtractedTextManager = attachmentExtractedTextManager;
        this.attachmentStatusManager = attachmentStatusManager;
        this.applicationStatusService = applicationStatusService;
        this.remoteAttachmentTextExtractionService = remoteAttachmentTextExtractionService;
        this.pluginAttachmentTextExtractorsProvider = pluginAttachmentTextExtractorsProvider;
        this.shouldExtractAttachmentTextPredicate = shouldExtractAttachmentTextPredicate;
    }

    protected static long getAttachmentSizeLimit() {
        return Long.getLong(ATLASSIAN_INDEXING_ATTACHMENT_MAXSIZE, 0x6400000L);
    }

    private boolean skipContentIndexingBecauseSizeLimit(Searchable searchable) {
        return this.sizeLimit < ((Attachment)searchable).getFileSize();
    }

    private boolean skipContentIndexingBecauseLastAttemptFailed(Searchable searchable) {
        return this.attachmentStatusManager.getAttachmentStatus(searchable.getId()).filter(AttachmentStatus.EXTRACTION_ERROR::equals).isPresent();
    }

    private boolean shouldExtract(Attachment attachment) {
        return this.pluginAttachmentTextExtractorsProvider.get().anyMatch(attachmentTextExtractor -> this.shouldExtractAttachmentTextPredicate.test((AttachmentTextExtractor)attachmentTextExtractor, attachment));
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        StringBuilder resultBuilder;
        block8: {
            resultBuilder = new StringBuilder();
            if (!(searchable instanceof Attachment)) {
                return resultBuilder;
            }
            Attachment attachment = (Attachment)searchable;
            if (!this.shouldExtract(attachment)) {
                return resultBuilder;
            }
            Optional<InputStreamSource> extracted = this.attachmentExtractedTextManager.getContent(attachment);
            if (extracted.isPresent()) {
                log.debug("Extracted text of {} is available, reuse it", (Object)attachment.getId());
                extracted.flatMap(AttachmentExtractedTextHelper::toString).ifPresent(resultBuilder::append);
                return resultBuilder;
            }
            boolean skipContentIndexingBecauseSizeLimit = this.skipContentIndexingBecauseSizeLimit(attachment);
            boolean skipContentIndexingBecauseLastAttemptFailed = this.skipContentIndexingBecauseLastAttemptFailed(attachment);
            if (skipContentIndexingBecauseLastAttemptFailed) {
                log.debug("Skip text extraction for {} due to an error in the last attempt", (Object)attachment.getId());
                return resultBuilder;
            }
            if (skipContentIndexingBecauseSizeLimit) {
                log.debug("Content indexing is skipped for {} because attachment content size is bigger than configured limit ({})", searchable, (Object)this.sizeLimit);
                return resultBuilder;
            }
            try {
                log.debug("Extracted text of {} is not available, request an extraction", (Object)attachment.getId());
                CompletionStage<AttachmentTextExtraction> extractionStage = this.remoteAttachmentTextExtractionService.submit(attachment.getId(), attachment.getVersion());
                AttachmentTextExtraction extraction = extractionStage.toCompletableFuture().get();
                extraction.getText().ifPresent(resultBuilder::append);
            }
            catch (InterruptedException e) {
                log.error("Text extraction text for {} is interrupted by {}", (Object)attachment.getId(), (Object)e.getMessage());
                log.debug("Text extraction text for {} is interrupted", (Object)attachment.getId(), (Object)e);
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException e) {
                String errorMessage = "Error when extracting text for " + attachment.getId();
                log.debug(errorMessage, e.getCause());
                if (this.applicationStatusService.getState() != ApplicationState.RUNNING) break block8;
                log.debug("Attachment {} is marked as having failed and will not be retried next time", (Object)attachment.getFileName());
                this.attachmentStatusManager.updateAttachmentStatus(attachment.getId(), AttachmentStatus.EXTRACTION_ERROR);
            }
        }
        return resultBuilder;
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        return ImmutableList.builder().build();
    }
}

