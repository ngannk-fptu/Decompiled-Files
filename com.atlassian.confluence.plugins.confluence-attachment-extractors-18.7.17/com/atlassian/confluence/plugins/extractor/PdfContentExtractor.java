/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.index.attachment.AttachmentTextExtractor
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.search.v2.extractor.util.AbstractLengthLimitedStringBuilder
 *  com.atlassian.confluence.search.v2.extractor.util.AbstractLengthLimitedStringBuilder$LIMIT_BEHAVIOUR
 *  com.atlassian.confluence.search.v2.extractor.util.StaticLengthLimitedStringBuilder
 *  com.atlassian.confluence.search.v2.extractor.util.StringBuilderWriter
 *  com.atlassian.confluence.util.io.InputStreamSource
 *  org.apache.commons.io.IOUtils
 *  org.apache.pdfbox.pdmodel.PDDocument
 *  org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException
 *  org.apache.pdfbox.text.PDFTextStripper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.extractor;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.index.attachment.AttachmentTextExtractor;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.search.v2.extractor.util.AbstractLengthLimitedStringBuilder;
import com.atlassian.confluence.search.v2.extractor.util.StaticLengthLimitedStringBuilder;
import com.atlassian.confluence.search.v2.extractor.util.StringBuilderWriter;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfContentExtractor
implements AttachmentTextExtractor {
    private static final Logger log = LoggerFactory.getLogger(PdfContentExtractor.class);
    private static final String[] EXTENSIONS = new String[]{"pdf"};
    private static final String[] CONTENT_TYPES = new String[]{"application/pdf"};
    private static final int DEFAULT_MAX_RESULT_SIZE = 0x800000;
    private final int maxResultSize;
    private final AttachmentManager attachmentManager;

    public PdfContentExtractor(AttachmentManager attachmentManager) {
        this(attachmentManager, 0x800000);
    }

    PdfContentExtractor(AttachmentManager attachmentManager, int maxResultSize) {
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.maxResultSize = maxResultSize;
    }

    public List<String> getFileExtensions() {
        return Arrays.asList(EXTENSIONS);
    }

    public List<String> getMimeTypes() {
        return Arrays.asList(CONTENT_TYPES);
    }

    public Optional<InputStreamSource> extract(Attachment attachment) {
        return Optional.of(() -> {
            String text = "";
            try (InputStream is = this.attachmentManager.getAttachmentData(attachment);){
                if (is == null) {
                    log.warn("Encountered attachment with null stream: " + attachment.getFileName());
                }
                log.debug("Starting to index attachment: " + attachment.getFileName());
                text = this.extractText(is);
            }
            catch (IOException e) {
                log.warn("Error reading attachment (" + attachment + ")", (Throwable)e);
            }
            catch (RuntimeException e) {
                log.warn("Error indexing attachment (" + attachment + ")", (Throwable)e);
            }
            return IOUtils.toInputStream((String)text, (Charset)StandardCharsets.UTF_8);
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    String extractText(InputStream is) {
        String string;
        block15: {
            PDDocument pdfDocument = PDDocument.load((InputStream)is);
            try {
                PDFTextStripper stripper = new PDFTextStripper();
                try (StringBuilderWriter writer = new StringBuilderWriter((AbstractLengthLimitedStringBuilder)new StaticLengthLimitedStringBuilder(this.maxResultSize / 2, AbstractLengthLimitedStringBuilder.LIMIT_BEHAVIOUR.THROW));){
                    stripper.writeText(pdfDocument, (Writer)writer);
                }
                string = writer.toString();
                if (pdfDocument == null) break block15;
            }
            catch (Throwable throwable) {
                try {
                    if (pdfDocument != null) {
                        try {
                            pdfDocument.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (InvalidPasswordException e) {
                    throw new RuntimeException("Password required for encrypted PDF document", e);
                }
                catch (Exception e) {
                    throw new RuntimeException("Error getting content of PDF document", e);
                }
            }
            pdfDocument.close();
        }
        return string;
    }
}

