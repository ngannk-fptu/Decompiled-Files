/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchableAttachment
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.input.BoundedInputStream
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.impl.search.v2.extractor.AttachmentExtractedTextExtractor;
import com.atlassian.confluence.search.v2.SearchableAttachment;
import com.atlassian.confluence.search.v2.extractor.BaseAttachmentContentExtractor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimitedTextContentExtractor
extends BaseAttachmentContentExtractor {
    private static final Logger log = LoggerFactory.getLogger(LimitedTextContentExtractor.class);
    private final long limit;

    public LimitedTextContentExtractor() {
        this(AttachmentExtractedTextExtractor.getAttachmentSizeLimit());
    }

    LimitedTextContentExtractor(long limit) {
        this.limit = limit;
    }

    @Override
    protected boolean shouldExtractFrom(String fileName, String contentType) {
        return contentType.startsWith("text/") || contentType.startsWith("application/xml") || contentType.startsWith("application/") && contentType.endsWith("+xml");
    }

    @Override
    protected String extractText(InputStream is, SearchableAttachment attachment) {
        try {
            log.debug("returning a maximum of {} bytes from attachment {}", (Object)this.limit, (Object)attachment);
            return IOUtils.toString((InputStream)new BoundedInputStream(is, this.limit), (Charset)StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            log.error("Couldn't extract text from attachment: " + attachment, (Throwable)e);
            return null;
        }
    }
}

