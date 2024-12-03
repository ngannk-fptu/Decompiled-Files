/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.inject.util;

import com.opensymphony.xwork2.inject.util.FinalizableReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

class FinalizableReferenceQueue
extends ReferenceQueue<Object> {
    private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());
    static ReferenceQueue<Object> instance = FinalizableReferenceQueue.createAndStart();

    private FinalizableReferenceQueue() {
    }

    void cleanUp(Reference reference) {
        try {
            ((FinalizableReference)((Object)reference)).finalizeReferent();
        }
        catch (Throwable t) {
            this.deliverBadNews(t);
        }
    }

    void deliverBadNews(Throwable t) {
        logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
    }

    void start() {
        Thread thread = new Thread("FinalizableReferenceQueue"){

            @Override
            public void run() {
                while (true) {
                    try {
                        while (true) {
                            FinalizableReferenceQueue.this.cleanUp(FinalizableReferenceQueue.this.remove());
                        }
                    }
                    catch (InterruptedException interruptedException) {
                        continue;
                    }
                    break;
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    static FinalizableReferenceQueue createAndStart() {
        FinalizableReferenceQueue queue = new FinalizableReferenceQueue();
        queue.start();
        return queue;
    }

    public static ReferenceQueue<Object> getInstance() {
        return instance;
    }
}

