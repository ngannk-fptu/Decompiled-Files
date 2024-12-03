/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.Resource
 */
package com.benryan.conversion;

import com.atlassian.confluence.pages.Attachment;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

public abstract class AbstractSlideConversionTask<T>
implements Callable<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractSlideConversionTask.class);
    private final Attachment attachment;
    private String attachmentName;
    private Resource inputResource;

    protected abstract T convertFile() throws Exception;

    public AbstractSlideConversionTask(Attachment attachment, String attachmentName, Resource inputResource) {
        this.attachment = attachment;
        this.attachmentName = attachmentName.toLowerCase();
        this.inputResource = inputResource;
    }

    protected String getAttachmentDescription() {
        return String.format("attachment %s ('%s'), from page %s ('%s')", this.attachment.getId(), this.attachment.getFileName(), this.attachment.getContainer().getId(), this.attachment.getContainer().getTitle());
    }

    protected String getAttachmentName() {
        return this.attachmentName;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    @Override
    public final T call() {
        try {
            return this.convertFile();
        }
        catch (Exception t) {
            log.error("problem while converting " + this.attachmentName, (Throwable)t);
            return null;
        }
    }

    protected Resource getInputResource() {
        return this.inputResource;
    }
}

