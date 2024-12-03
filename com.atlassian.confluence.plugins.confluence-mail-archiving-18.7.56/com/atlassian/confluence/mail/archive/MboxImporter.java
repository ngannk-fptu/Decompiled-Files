/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.BatchOperationManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask
 *  com.google.common.base.Function
 *  javax.mail.MessagingException
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.mail.archive.MailContentManager;
import com.atlassian.confluence.mail.archive.MboxIterator;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.longrunning.ConfluenceAbstractLongRunningTask;
import com.google.common.base.Function;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MboxImporter
extends ConfluenceAbstractLongRunningTask {
    private static final Logger log = LoggerFactory.getLogger(MboxImporter.class);
    private long spaceId;
    private int imported = 0;
    private final MailContentManager mailContentManager;
    private final SpaceManager spaceManager;
    private final BatchOperationManager batchOperationManager;
    private File file;

    public MboxImporter(BatchOperationManager batchOperationManager, SpaceManager spaceManager, MailContentManager mailContentManager) {
        this.batchOperationManager = batchOperationManager;
        this.spaceManager = spaceManager;
        this.mailContentManager = mailContentManager;
    }

    public void setSpace(Space space) {
        this.spaceId = space.getId();
    }

    public void setFile(File file) {
        this.file = file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runInternal() {
        block7: {
            try {
                if (this.file == null) break block7;
                this.progress.setStatus("Scanning mailbox");
                int estimatedTotal = this.estimateTotalMailCount(this.file);
                this.progress.setTotalObjects(estimatedTotal);
                FileInputStream mbox = null;
                try {
                    mbox = new FileInputStream(this.file);
                    this.batchOperationManager.performAsBatch(MboxIterator.iterable(mbox), estimatedTotal, (Function)new ImportSingleMail());
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(mbox);
                    throw throwable;
                }
                IOUtils.closeQuietly((InputStream)mbox);
                this.progress.setStatus("Import complete");
            }
            catch (Exception e) {
                log.error("Mailbox import failed: " + e.getMessage(), (Throwable)e);
                this.progress.setStatus("Import failed: " + e.getMessage());
            }
            finally {
                this.progress.setPercentage(100);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int estimateTotalMailCount(File mboxFile) throws IOException {
        int n;
        int count = 0;
        BufferedReader in = null;
        try {
            String line;
            in = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(mboxFile), "ISO-8859-1"));
            while ((line = in.readLine()) != null) {
                if (!line.startsWith("From ")) continue;
                ++count;
            }
            n = count;
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(in);
            throw throwable;
        }
        IOUtils.closeQuietly((Reader)in);
        return n;
    }

    private String buildIdentificationStringForEmail(MimeMessage mimeMessage) {
        if (mimeMessage == null) {
            return "MIME Message is null";
        }
        try {
            String subject = mimeMessage.getSubject();
            return "Subject: " + subject;
        }
        catch (MessagingException e) {
            log.error("Unable to get subject from MIME message", (Throwable)e);
            return "Message subject unreadable";
        }
    }

    public String getName() {
        return "Importing mailbox file";
    }

    private class ImportSingleMail
    implements Function<byte[], Void> {
        private ImportSingleMail() {
        }

        public Void apply(byte[] message) {
            Space space = MboxImporter.this.spaceManager.getSpace(MboxImporter.this.spaceId);
            MimeMessage mimeMessage = null;
            try {
                mimeMessage = new MimeMessage(null, (InputStream)new ByteArrayInputStream(message));
                if (log.isDebugEnabled()) {
                    log.debug("Importing mail message " + mimeMessage.getMessageID());
                }
                MboxImporter.this.progress.setStatus("Importing message " + mimeMessage.getMessageID() + "(" + (MboxImporter.this.imported + 1) + "/" + MboxImporter.this.progress.getTotal() + ")");
                MboxImporter.this.progress.setCurrentCount(MboxImporter.this.imported);
                MboxImporter.this.progress.setPercentage(MboxImporter.this.imported, MboxImporter.this.progress.getTotal());
                MboxImporter.this.mailContentManager.storeIncomingMail(space, mimeMessage);
                ++MboxImporter.this.imported;
            }
            catch (Exception e) {
                log.error("Failed to import a message, index: " + (MboxImporter.this.imported + 1) + ". " + MboxImporter.this.buildIdentificationStringForEmail(mimeMessage), (Throwable)e);
            }
            return null;
        }

        public String toString() {
            return "Import single mail";
        }
    }
}

