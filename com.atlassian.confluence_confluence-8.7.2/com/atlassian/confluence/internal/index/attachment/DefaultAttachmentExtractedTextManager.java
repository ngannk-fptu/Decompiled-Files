/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.confluence.core.AbstractVersionedEntityObject;
import com.atlassian.confluence.index.attachment.AttachmentExtractedTextManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.atlassian.confluence.util.io.DataCompressor;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAttachmentExtractedTextManager
implements AttachmentExtractedTextManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultAttachmentExtractedTextManager.class);
    private final AttachmentManager attachmentManager;
    private final DataCompressor compressor;

    public DefaultAttachmentExtractedTextManager(AttachmentManager attachmentManager, DataCompressor compressor) {
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.compressor = Objects.requireNonNull(compressor);
    }

    @Override
    public Optional<InputStreamSource> getContent(Attachment attachment) {
        try {
            AttachmentDataStream attachmentDataStream = this.getAttachmentDataDao().getDataForAttachment(attachment, AttachmentDataStreamType.EXTRACTED_TEXT);
            return Optional.of(this.compressor.uncompress(attachmentDataStream::getInputStream));
        }
        catch (AttachmentDataNotFoundException e) {
            log.debug("Can't read extracted text of attachment {}", (Object)attachment.getId());
            return Optional.empty();
        }
    }

    @Override
    public void saveContent(Attachment attachment, InputStreamSource inputStreamSource) {
        this.getAttachmentDataDao().saveDataForAttachment(attachment, new CompressedAttachmentDataStream(inputStreamSource, this.compressor));
    }

    @Override
    public void removePreviousVersionContent(Attachment attachment) {
        Comparator<Attachment> comparator = Comparator.comparingInt(AbstractVersionedEntityObject::getVersion).reversed();
        this.attachmentManager.getAllVersions(attachment).stream().filter(x -> x.getVersion() < attachment.getVersion()).sorted(comparator).findFirst().ifPresent(this::removeContent);
    }

    @Override
    public void removeContent(Attachment attachment) {
        this.getAttachmentDataDao().removeDataForAttachmentVersion(attachment, attachment.getContainer(), AttachmentDataStreamType.EXTRACTED_TEXT);
    }

    private AttachmentDataDao getAttachmentDataDao() {
        return this.attachmentManager.getAttachmentDao().getDataDao();
    }

    private static class CompressedAttachmentDataStream
    implements AttachmentDataStream {
        private final InputStreamSource inputStreamSource;
        private final DataCompressor compressor;

        private CompressedAttachmentDataStream(InputStreamSource inputStreamSource, DataCompressor compressor) {
            this.inputStreamSource = inputStreamSource;
            this.compressor = compressor;
        }

        @Override
        public AttachmentDataStreamType getType() {
            return AttachmentDataStreamType.EXTRACTED_TEXT;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.compressor.compress(this.inputStreamSource).getInputStream();
        }
    }
}

