/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

class TempFileCleanupThread
extends Thread {
    private HashSet tempFiles = null;

    TempFileCleanupThread() {
        this.setPriority(1);
    }

    public void run() {
        if (this.tempFiles != null && this.tempFiles.size() > 0) {
            Iterator fileIter = this.tempFiles.iterator();
            while (fileIter.hasNext()) {
                try {
                    File file = (File)fileIter.next();
                    file.delete();
                }
                catch (Exception exception) {}
            }
        }
    }

    synchronized void addFile(File file) {
        if (this.tempFiles == null) {
            this.tempFiles = new HashSet();
        }
        this.tempFiles.add(file);
    }

    synchronized void removeFile(File file) {
        this.tempFiles.remove(file);
    }
}

