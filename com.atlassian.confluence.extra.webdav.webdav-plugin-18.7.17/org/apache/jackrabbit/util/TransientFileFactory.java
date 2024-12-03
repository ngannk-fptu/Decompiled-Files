/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.util;

import java.io.File;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class TransientFileFactory {
    private static TransientFileFactory INSTANCE;
    private final ReferenceQueue<File> phantomRefQueue = new ReferenceQueue();
    private final Collection<MoribundFileReference> trackedRefs = Collections.synchronizedList(new ArrayList());
    private final ReaperThread reaper = new ReaperThread("Transient File Reaper");
    private static Thread shutdownHook;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static TransientFileFactory getInstance() {
        Class<TransientFileFactory> clazz = TransientFileFactory.class;
        synchronized (TransientFileFactory.class) {
            if (INSTANCE == null) {
                INSTANCE = new TransientFileFactory();
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return INSTANCE;
        }
    }

    private TransientFileFactory() {
        this.reaper.setPriority(1);
        this.reaper.setDaemon(true);
        this.reaper.start();
        try {
            shutdownHook = new Thread(){

                @Override
                public void run() {
                    TransientFileFactory.this.doShutdown();
                }
            };
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    public File createTransientFile(String prefix, String suffix, File directory) throws IOException {
        File f = File.createTempFile(prefix, suffix, directory);
        this.trackedRefs.add(new MoribundFileReference(f, this.phantomRefQueue));
        return f;
    }

    public static void shutdown() {
        TransientFileFactory.getInstance().doShutdown();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void doShutdown() {
        Collection<MoribundFileReference> collection = this.trackedRefs;
        synchronized (collection) {
            Iterator<MoribundFileReference> it = this.trackedRefs.iterator();
            while (it.hasNext()) {
                it.next().delete();
            }
        }
        if (shutdownHook != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            shutdownHook = null;
        }
        this.reaper.stopWorking();
    }

    static {
        shutdownHook = null;
    }

    private static class MoribundFileReference
    extends PhantomReference<File> {
        private final String path;

        MoribundFileReference(File file, ReferenceQueue<File> queue) {
            super(file, queue);
            this.path = file.getPath();
        }

        boolean delete() {
            return new File(this.path).delete();
        }
    }

    private class ReaperThread
    extends Thread {
        private volatile boolean stopping;

        ReaperThread(String name) {
            super(name);
            this.stopping = false;
        }

        @Override
        public void run() {
            while (!this.stopping) {
                MoribundFileReference fileRef = null;
                try {
                    fileRef = (MoribundFileReference)TransientFileFactory.this.phantomRefQueue.remove();
                }
                catch (InterruptedException e) {
                    if (this.stopping) {
                        break;
                    }
                }
                catch (Exception e) {
                    continue;
                }
                fileRef.delete();
                fileRef.clear();
                TransientFileFactory.this.trackedRefs.remove(fileRef);
            }
        }

        public void stopWorking() {
            this.stopping = true;
            this.interrupt();
        }
    }
}

