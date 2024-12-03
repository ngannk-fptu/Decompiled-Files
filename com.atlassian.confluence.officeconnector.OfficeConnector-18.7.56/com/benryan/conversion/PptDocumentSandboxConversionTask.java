/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus
 *  com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailRequest
 *  com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailResponse
 *  com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailRequest;
import com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailResponse;
import com.atlassian.plugins.conversion.sandbox.thumbnail.SandboxThumbnailTask;
import com.benryan.conversion.AbstractSandboxSlideConversionTask;
import com.benryan.conversion.AttachmentDataTempFile;
import com.benryan.conversion.AttachmentTempFileSupplier;
import com.benryan.conversion.FilePathAwareConversionStore;
import com.benryan.conversion.PPtDocumentConversionTask;
import com.benryan.conversion.SlideDocConversionData;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public class PptDocumentSandboxConversionTask
extends AbstractSandboxSlideConversionTask<SandboxThumbnailTask, SandboxThumbnailRequest, SandboxThumbnailResponse> {
    private static final Logger logger = LoggerFactory.getLogger(PptDocumentSandboxConversionTask.class);

    public PptDocumentSandboxConversionTask(FilePathAwareConversionStore conversionStore, Sandbox sandbox, Attachment attachment, String attachmentName, Resource resource, AttachmentTempFileSupplier attachmentTempFileSupplier, int pageNumber, SlideDocConversionData slideDocConversionData) {
        super(conversionStore, sandbox, attachment, attachmentName, resource, attachmentTempFileSupplier, pageNumber, slideDocConversionData);
    }

    @Override
    protected SandboxThumbnailTask getSandboxTask() {
        return new SandboxThumbnailTask();
    }

    @Override
    protected SandboxThumbnailRequest createSandboxRequest(AttachmentDataTempFile attachmentDataTempFile, File tempFile, File convertedFile) {
        return new SandboxThumbnailRequest(attachmentDataTempFile.getFile().toFile(), PPtDocumentConversionTask.getPptFormat(this.getAttachmentName()), tempFile, convertedFile, this.pageNumber + 1, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    protected boolean verifySandboxResponse(SandboxThumbnailResponse sandboxResponse) {
        return SandboxConversionStatus.ERROR != sandboxResponse.getStatus();
    }
}

