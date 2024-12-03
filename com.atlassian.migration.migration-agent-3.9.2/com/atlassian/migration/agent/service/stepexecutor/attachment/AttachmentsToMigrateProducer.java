/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.attachment;

import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import org.slf4j.Logger;

class AttachmentsToMigrateProducer
implements Runnable {
    private static final Logger log = ContextLoggerFactory.getLogger(AttachmentsToMigrateProducer.class);
    private final BlockingQueue<Attachment> buffer;
    private final Stream<Attachment> attachmentStream;
    private final BooleanSupplier forceStop;

    AttachmentsToMigrateProducer(Stream<Attachment> attachmentStream, BlockingQueue<Attachment> buffer, BooleanSupplier forceStop) {
        this.buffer = buffer;
        this.attachmentStream = attachmentStream;
        this.forceStop = forceStop;
    }

    @Override
    public void run() {
        Iterator attachments = this.attachmentStream.iterator();
        while (this.canRun() && attachments.hasNext()) {
            Attachment attachment = (Attachment)attachments.next();
            boolean added = false;
            while (!added && this.canRun()) {
                added = this.addToBuffer(attachment);
                if (!added) continue;
                log.info("Added attachment {} to upload buffer", (Object)attachment.getId());
            }
        }
    }

    private boolean canRun() {
        return !this.forceStop.getAsBoolean();
    }

    private boolean addToBuffer(Attachment attachmentCandidate) {
        try {
            return this.buffer.offer(attachmentCandidate, 200L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ex) {
            throw new IllegalStateException("Failed to add attachment to buffer because action was interrupted.", ex);
        }
    }
}

