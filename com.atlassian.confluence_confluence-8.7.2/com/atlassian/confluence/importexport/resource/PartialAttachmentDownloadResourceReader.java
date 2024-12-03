/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.AttachmentDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceReader;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import java.util.Objects;
import org.springframework.core.io.InputStreamSource;

public class PartialAttachmentDownloadResourceReader
extends AttachmentDownloadResourceReader
implements PartialDownloadResourceReader {
    private final RangeRequest rangeRequest;

    public PartialAttachmentDownloadResourceReader(Attachment attachment, InputStreamSource inputStreamSource, RangeRequest range) {
        super(attachment, inputStreamSource);
        this.rangeRequest = Objects.requireNonNull(range);
    }

    @Override
    public RangeRequest getRequestRange() {
        return this.rangeRequest;
    }
}

