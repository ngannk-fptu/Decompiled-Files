/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStream;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataStreamType;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyAttachmentDataDaoSupport {
    private static final Logger log = LoggerFactory.getLogger(LegacyAttachmentDataDaoSupport.class);
    private final AttachmentDataDao delegateDao;

    public LegacyAttachmentDataDaoSupport(AttachmentDataDao delegateDao) {
        this.delegateDao = (AttachmentDataDao)Preconditions.checkNotNull((Object)delegateDao);
    }

    public AttachmentDataStream getAttachmentDataStream(Attachment attachment, AttachmentDataStreamType dataStreamType) throws AttachmentDataNotFoundException {
        if (dataStreamType == AttachmentDataStreamType.RAW_BINARY) {
            return new AttachmentDataStream.InputStreamWrapper(dataStreamType, this.delegateDao.getDataForAttachment(attachment));
        }
        throw new AttachmentDataNotFoundException(String.format("Only raw_binary streams are supported by %s.", this.delegateDao.getClass().getName()));
    }

    public void saveDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        LegacyAttachmentDataDaoSupport.runWithinTryWithResource(dataStream, attachment, inputStream -> this.executeOnlyIfRawBinary(dataStream, () -> this.delegateDao.saveDataForAttachment(attachment, (InputStream)inputStream)));
    }

    public void saveDataForAttachmentVersion(Attachment attachment, Attachment previousVersion, AttachmentDataStream dataStream) {
        LegacyAttachmentDataDaoSupport.runWithinTryWithResource(dataStream, attachment, inputStream -> this.executeOnlyIfRawBinary(dataStream, () -> this.delegateDao.saveDataForAttachmentVersion(attachment, previousVersion, (InputStream)inputStream)));
    }

    public void replaceDataForAttachment(Attachment attachment, AttachmentDataStream dataStream) {
        LegacyAttachmentDataDaoSupport.runWithinTryWithResource(dataStream, attachment, inputStream -> this.executeOnlyIfRawBinary(dataStream, () -> this.delegateDao.replaceDataForAttachment(attachment, (InputStream)inputStream)));
    }

    private void executeOnlyIfRawBinary(AttachmentDataStream dataStreamType, Runnable task) {
        if (dataStreamType.getType() == AttachmentDataStreamType.RAW_BINARY) {
            task.run();
        } else {
            this.logUnsupportedDataStreamType(dataStreamType.getType());
        }
    }

    protected static void assertIsRawBinary(AttachmentDataStreamType dataStreamType) {
        Preconditions.checkArgument((dataStreamType == AttachmentDataStreamType.RAW_BINARY ? 1 : 0) != 0, (Object)"Only raw_binary streams supported");
    }

    private static InputStream getInputStream(AttachmentDataStream dataStream, Attachment attachment) {
        try {
            return dataStream.getInputStream();
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to open InputStream for " + attachment, e);
        }
    }

    private static void runWithinTryWithResource(AttachmentDataStream dataStream, Attachment attachment, Consumer<InputStream> consumer) {
        try (InputStream is = LegacyAttachmentDataDaoSupport.getInputStream(dataStream, attachment);){
            consumer.accept(is);
        }
        catch (IOException e) {
            log.error("Unable to read from input stream: {}", (Object)e.getMessage());
        }
    }

    private void logUnsupportedDataStreamType(AttachmentDataStreamType dataStreamType) {
        log.info("Only raw_binary streams are supported by {}. Attachment data stream of type {} will not be stored.", (Object)this.delegateDao.getClass().getName(), (Object)dataStreamType);
    }
}

