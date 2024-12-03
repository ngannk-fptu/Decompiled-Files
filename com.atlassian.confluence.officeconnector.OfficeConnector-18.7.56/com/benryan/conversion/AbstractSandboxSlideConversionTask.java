/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.util.sandbox.Sandbox
 *  com.atlassian.confluence.util.sandbox.SandboxTask
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.sandbox.Sandbox;
import com.atlassian.confluence.util.sandbox.SandboxTask;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.benryan.conversion.AbstractSlideConversionTask;
import com.benryan.conversion.AttachmentDataTempFile;
import com.benryan.conversion.AttachmentTempFileSupplier;
import com.benryan.conversion.FilePathAwareConversionStore;
import com.benryan.conversion.SlideConversionDataHolder;
import com.benryan.conversion.SlideDocConversionData;
import com.benryan.conversion.SlidePageConversionData;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public abstract class AbstractSandboxSlideConversionTask<S extends SandboxTask<T, R>, T, R>
extends AbstractSlideConversionTask<SlideConversionDataHolder> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSandboxSlideConversionTask.class);
    protected final FilePathAwareConversionStore conversionStore;
    protected final Sandbox sandbox;
    protected final AttachmentTempFileSupplier attachmentTempFileSupplier;
    protected final int pageNumber;
    protected final SlideDocConversionData slideDocConversionData;

    public AbstractSandboxSlideConversionTask(FilePathAwareConversionStore conversionStore, Sandbox sandbox, Attachment attachment, String attachmentName, Resource resource, AttachmentTempFileSupplier attachmentTempFileSupplier, int pageNumber, SlideDocConversionData slideDocConversionData) {
        super(attachment, attachmentName, resource);
        this.conversionStore = conversionStore;
        this.sandbox = sandbox;
        this.attachmentTempFileSupplier = attachmentTempFileSupplier;
        this.pageNumber = pageNumber;
        this.slideDocConversionData = slideDocConversionData;
    }

    protected abstract S getSandboxTask();

    protected abstract T createSandboxRequest(AttachmentDataTempFile var1, File var2, File var3);

    protected abstract boolean verifySandboxResponse(R var1);

    @Override
    protected SlideConversionDataHolder convertFile() throws Exception {
        UUID tempFileId = UUID.randomUUID();
        this.createTempFile(tempFileId);
        File tempFile = this.conversionStore.getFilePath(tempFileId).toFile();
        UUID convertedFileId = UUID.randomUUID();
        File convertedFile = this.conversionStore.getFilePath(convertedFileId).toFile();
        Object sandboxResponse = null;
        try {
            SlideConversionDataHolder slideConversionDataHolder;
            block15: {
                SlideConversionDataHolder serializer;
                AttachmentDataTempFile attachmentDataTempFile;
                block13: {
                    SlideConversionDataHolder slideConversionDataHolder2;
                    block14: {
                        attachmentDataTempFile = this.attachmentTempFileSupplier.createAttachmentTempFile(this.getInputResource());
                        try {
                            T sandboxThumbnailRequest = this.createSandboxRequest(attachmentDataTempFile, tempFile, convertedFile);
                            S sandboxTask = this.getSandboxTask();
                            sandboxResponse = this.sandbox.execute(sandboxTask, sandboxThumbnailRequest);
                            if (this.verifySandboxResponse(sandboxResponse)) break block13;
                            logger.error("Sandbox conversion is failed. Please check sandbox log for more detail");
                            slideConversionDataHolder2 = new SlideConversionDataHolder(Collections.EMPTY_LIST);
                            if (attachmentDataTempFile == null) break block14;
                        }
                        catch (Throwable throwable) {
                            if (attachmentDataTempFile != null) {
                                try {
                                    attachmentDataTempFile.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        attachmentDataTempFile.close();
                    }
                    return slideConversionDataHolder2;
                }
                SlidePageConversionData slidePageConversionData = new SlidePageConversionData(this.slideDocConversionData, convertedFileId, this.pageNumber, this.getAttachmentName(), FileFormat.JPG);
                slideConversionDataHolder = serializer = new SlideConversionDataHolder(Arrays.asList(slidePageConversionData));
                if (attachmentDataTempFile == null) break block15;
                attachmentDataTempFile.close();
            }
            return slideConversionDataHolder;
        }
        finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            if (sandboxResponse != null && !this.verifySandboxResponse(sandboxResponse) && convertedFile.exists()) {
                convertedFile.delete();
            }
        }
    }

    private void createTempFile(UUID tempFileId) throws IOException {
        try (OutputStream unused = null;){
            unused = this.conversionStore.createFile(tempFileId);
        }
    }
}

