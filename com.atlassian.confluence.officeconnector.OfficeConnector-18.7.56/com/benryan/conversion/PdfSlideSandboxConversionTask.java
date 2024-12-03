/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailRequest
 *  com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailResponse
 *  com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailTask
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus
 *  org.springframework.core.io.Resource
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailRequest;
import com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailResponse;
import com.atlassian.confluence.plugins.conversion.convert.image.SandboxPdfThumbnailTask;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.sandbox.SandboxConversionStatus;
import com.benryan.conversion.AbstractSandboxSlideConversionTask;
import com.benryan.conversion.AttachmentDataTempFile;
import com.benryan.conversion.AttachmentTempFileSupplier;
import com.benryan.conversion.FilePathAwareConversionStore;
import com.benryan.conversion.SlideDocConversionData;
import java.io.File;
import org.springframework.core.io.Resource;

public class PdfSlideSandboxConversionTask
extends AbstractSandboxSlideConversionTask<SandboxPdfThumbnailTask, SandboxPdfThumbnailRequest, SandboxPdfThumbnailResponse> {
    public PdfSlideSandboxConversionTask(FilePathAwareConversionStore conversionStore, Sandbox sandbox, Attachment attachment, String attachmentName, Resource resource, AttachmentTempFileSupplier attachmentTempFileSupplier, int pageNumber, SlideDocConversionData slideDocConversionData) {
        super(conversionStore, sandbox, attachment, attachmentName, resource, attachmentTempFileSupplier, pageNumber, slideDocConversionData);
    }

    @Override
    protected SandboxPdfThumbnailTask getSandboxTask() {
        return new SandboxPdfThumbnailTask();
    }

    @Override
    protected SandboxPdfThumbnailRequest createSandboxRequest(AttachmentDataTempFile attachmentDataTempFile, File tempFile, File convertedFile) {
        return new SandboxPdfThumbnailRequest(attachmentDataTempFile.getFile().toFile(), PdfSlideSandboxConversionTask.getFormat(this.getAttachmentName()), tempFile, convertedFile, this.pageNumber, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    protected boolean verifySandboxResponse(SandboxPdfThumbnailResponse sandboxResponse) {
        return SandboxConversionStatus.ERROR != sandboxResponse.getStatus();
    }

    private static FileFormat getFormat(String attachmentName) throws IllegalArgumentException {
        String lowerCaseAttachmentName = attachmentName.toLowerCase();
        if (lowerCaseAttachmentName.contains(".pdf")) {
            return FileFormat.PDF;
        }
        throw new IllegalArgumentException("Cannot convert slide, can only handle .pdf, but instead got :" + attachmentName);
    }
}

