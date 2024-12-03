/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.util.ErrorMessages;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class PdfExportSemaphore {
    private static final Integer PERMITS_SIZE = Integer.getInteger("confluence.pdfexport.permits.size", Runtime.getRuntime().availableProcessors());
    private static final Integer TIMEOUT_SECONDS = Integer.getInteger("confluence.pdfexport.timeout.seconds", 30);
    private final Semaphore semaphore = new Semaphore(PERMITS_SIZE, true);
    private final ErrorMessages errorMessages;

    public PdfExportSemaphore(ErrorMessages errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void run(Runnable runnable) {
        if (this.acquire()) {
            try {
                runnable.run();
            }
            finally {
                this.release();
            }
        } else {
            String message = this.errorMessages.tooManyConcurrentExports();
            throw new RuntimeException(message);
        }
    }

    private boolean acquire() {
        try {
            return this.semaphore.tryAcquire(TIMEOUT_SECONDS.intValue(), TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            return false;
        }
    }

    private void release() {
        this.semaphore.release();
    }
}

