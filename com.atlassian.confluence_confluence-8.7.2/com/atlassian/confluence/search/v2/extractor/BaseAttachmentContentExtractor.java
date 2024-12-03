/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchableAttachment
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchableAttachment;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAttachmentContentExtractor
implements Extractor2 {
    private static final Logger log = LoggerFactory.getLogger(BaseAttachmentContentExtractor.class);

    @Nonnull
    protected Optional<CharSequence> extractText(SearchableAttachment attachment) {
        block10: {
            Optional<CharSequence> optional;
            block9: {
                InputStream is = attachment.getContentsAsStream();
                try {
                    log.debug("Starting to index attachment: {}", (Object)attachment.getFileName());
                    optional = Optional.ofNullable(this.extractText(is, attachment));
                    if (is == null) break block9;
                }
                catch (Throwable throwable) {
                    try {
                        if (is != null) {
                            try {
                                is.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        log.warn("Error reading attachment ({})", (Object)attachment, (Object)e);
                        break block10;
                    }
                    catch (Exception e) {
                        log.warn("Error loading/indexing attachment ({})", (Object)attachment, (Object)e);
                    }
                }
                is.close();
            }
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        return ImmutableList.builder().build();
    }

    @Override
    public StringBuilder extractText(Object searchable) {
        String contentType;
        SearchableAttachment attachment;
        String fileName;
        StringBuilder resultBuilder = new StringBuilder();
        if (searchable instanceof SearchableAttachment && this.shouldExtractFrom(fileName = (attachment = (SearchableAttachment)searchable).getFileName() == null ? "" : attachment.getFileName().toLowerCase(), contentType = attachment.getContentType())) {
            this.extractText(attachment).ifPresent(resultBuilder::append);
        }
        return resultBuilder;
    }

    protected abstract boolean shouldExtractFrom(String var1, String var2);

    protected abstract String extractText(InputStream var1, SearchableAttachment var2);
}

