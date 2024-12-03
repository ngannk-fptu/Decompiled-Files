/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentDataNotFoundException
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.persistence.dao.AttachmentDao
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.hibernate.ObjectNotFoundException
 *  org.slf4j.Logger
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataNotFoundException;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;
import java.io.InputStream;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.springframework.web.util.UriComponentsBuilder;

public class AttachmentDataProvider {
    private final AttachmentManager attachmentManager;
    private final TransactionTemplate transactionTemplate;
    private final String baseUrl;

    public AttachmentDataProvider(AttachmentManager attachmentManager, TransactionTemplate transactionTemplate, SystemInformationService systemInformationService) {
        this.attachmentManager = attachmentManager;
        this.transactionTemplate = transactionTemplate;
        this.baseUrl = systemInformationService.getConfluenceInfo().getBaseUrl();
    }

    AttachmentData getAttachmentData(long attachmentId) {
        return (AttachmentData)this.transactionTemplate.execute(() -> {
            Attachment attachment = this.attachmentManager.getAttachment(attachmentId);
            if (attachment == null) {
                throw new AttachmentDataRetrievalException(String.format("Attachment %s is null", attachmentId));
            }
            if (attachment.getContainer() == null) {
                throw new AttachmentDataRetrievalException(String.format("Attachment's %s container is null", attachmentId));
            }
            AttachmentDao attachmentDao = this.attachmentManager.getAttachmentDao();
            try {
                InputStream attachmentStream = attachmentDao.getAttachmentData(attachment);
                return new AttachmentData(attachment.getFileName(), attachmentStream, attachment.getFileSize());
            }
            catch (AttachmentDataNotFoundException | ObjectNotFoundException e) {
                throw new AttachmentDataRetrievalException(String.format("Could not get attachment %s data. Attachment download path: %s please open and verify", attachmentId, this.getAttachmentUrl(attachment)), e);
            }
            catch (IllegalArgumentException e) {
                throw new AttachmentDataRetrievalException(String.format("Attachment %s not found. Attachment download path: %s please open and verify.", attachmentId, this.getAttachmentUrl(attachment)), e);
            }
        });
    }

    private String getAttachmentUrl(Attachment attachment) {
        return UriComponentsBuilder.fromHttpUrl((String)this.baseUrl).path(attachment.getDownloadPathWithoutEncoding()).queryParam("version", new Object[]{attachment.getVersion()}).toUriString();
    }

    public static class AttachmentData
    implements AutoCloseable {
        private static final Logger log = ContextLoggerFactory.getLogger(AttachmentData.class);
        final String fileName;
        final InputStream inputStream;
        final Long fileSize;

        AttachmentData(String fileName, InputStream inputStream, Long fileSize) {
            this.fileName = fileName;
            this.inputStream = inputStream;
            this.fileSize = fileSize;
        }

        @Override
        public void close() {
            try {
                this.inputStream.close();
            }
            catch (IOException e) {
                log.warn("Failed to close attachment input stream", (Throwable)e);
            }
        }
    }

    static class AttachmentDataRetrievalException
    extends RuntimeException {
        AttachmentDataRetrievalException(String message) {
            super(message);
        }

        AttachmentDataRetrievalException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

