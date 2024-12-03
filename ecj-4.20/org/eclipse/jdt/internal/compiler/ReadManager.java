/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

public class ReadManager
implements Runnable {
    ICompilationUnit[] units;
    int nextFileToRead;
    ICompilationUnit[] filesRead;
    char[][] contentsRead;
    int readyToReadPosition;
    int nextAvailablePosition;
    Thread[] readingThreads;
    char[] readInProcessMarker = new char[0];
    int sleepingThreadCount;
    private Throwable caughtException;
    static final int START_CUSHION = 5;
    public static final int THRESHOLD = 10;
    static final int CACHE_SIZE = 15;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ReadManager(ICompilationUnit[] files, int length) {
        int threadCount = Runtime.getRuntime().availableProcessors() + 1;
        if (threadCount < 2) {
            threadCount = 0;
        } else if (threadCount > 15) {
            threadCount = 15;
        }
        if (threadCount > 0) {
            ReadManager readManager = this;
            synchronized (readManager) {
                this.units = new ICompilationUnit[length];
                System.arraycopy(files, 0, this.units, 0, length);
                this.nextFileToRead = 5;
                this.filesRead = new ICompilationUnit[15];
                this.contentsRead = new char[15][];
                this.readyToReadPosition = 0;
                this.nextAvailablePosition = 0;
                this.sleepingThreadCount = 0;
                this.readingThreads = new Thread[threadCount];
                int i = threadCount;
                while (--i >= 0) {
                    this.readingThreads[i] = new Thread((Runnable)this, "Compiler Source File Reader");
                    this.readingThreads[i].setDaemon(true);
                    this.readingThreads[i].start();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public char[] getContents(ICompilationUnit unit) throws Error {
        Thread[] rThreads = this.readingThreads;
        if (rThreads == null || this.units.length == 0) {
            if (this.caughtException != null) {
                if (this.caughtException instanceof Error) {
                    throw (Error)this.caughtException;
                }
                throw (RuntimeException)this.caughtException;
            }
            return unit.getContents();
        }
        boolean yield = this.sleepingThreadCount == rThreads.length;
        char[] result = null;
        ReadManager readManager = this;
        synchronized (readManager) {
            if (unit == this.filesRead[this.readyToReadPosition]) {
                result = this.contentsRead[this.readyToReadPosition];
                while (result == this.readInProcessMarker || result == null) {
                    this.contentsRead[this.readyToReadPosition] = null;
                    try {
                        this.wait(250L);
                    }
                    catch (InterruptedException interruptedException) {}
                    if (this.caughtException != null) {
                        if (this.caughtException instanceof Error) {
                            throw (Error)this.caughtException;
                        }
                        throw (RuntimeException)this.caughtException;
                    }
                    result = this.contentsRead[this.readyToReadPosition];
                }
                this.filesRead[this.readyToReadPosition] = null;
                this.contentsRead[this.readyToReadPosition] = null;
                if (++this.readyToReadPosition >= this.contentsRead.length) {
                    this.readyToReadPosition = 0;
                }
                if (this.sleepingThreadCount > 0) {
                    this.notify();
                }
            } else {
                int unitIndex = 0;
                int l = this.units.length;
                while (unitIndex < l) {
                    if (this.units[unitIndex] == unit) break;
                    ++unitIndex;
                }
                if (unitIndex == this.units.length) {
                    this.units = new ICompilationUnit[0];
                } else if (unitIndex >= this.nextFileToRead) {
                    this.nextFileToRead = unitIndex + 5;
                    this.readyToReadPosition = 0;
                    this.nextAvailablePosition = 0;
                    this.filesRead = new ICompilationUnit[15];
                    this.contentsRead = new char[15][];
                    this.notifyAll();
                }
            }
        }
        if (yield) {
            Thread.yield();
        }
        if (result != null) {
            return result;
        }
        return unit.getContents();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    public void run() {
        try {
            while (this.readingThreads != null && this.nextFileToRead < this.units.length) {
                unit = null;
                position = -1;
                var3_5 = this;
                synchronized (var3_5) {
                    if (this.readingThreads != null) ** GOTO lbl18
                    return;
lbl-1000:
                    // 1 sources

                    {
                        ++this.sleepingThreadCount;
                        try {
                            this.wait(250L);
                        }
                        catch (InterruptedException v0) {}
                        --this.sleepingThreadCount;
                        if (this.readingThreads != null) continue;
                        return;
lbl18:
                        // 2 sources

                        ** while (this.filesRead[this.nextAvailablePosition] != null)
                    }
lbl19:
                    // 1 sources

                    if (this.nextFileToRead >= this.units.length) {
                        return;
                    }
                    unit = this.units[this.nextFileToRead++];
                    position = this.nextAvailablePosition++;
                    if (this.nextAvailablePosition >= this.contentsRead.length) {
                        this.nextAvailablePosition = 0;
                    }
                    this.filesRead[position] = unit;
                    this.contentsRead[position] = this.readInProcessMarker;
                }
                result = unit.getContents();
                var4_6 = this;
                synchronized (var4_6) {
                    if (this.filesRead[position] == unit) {
                        if (this.contentsRead[position] == null) {
                            this.notifyAll();
                        }
                        this.contentsRead[position] = result;
                    }
                }
            }
        }
        catch (Error | RuntimeException e) {
            var2_4 = this;
            synchronized (var2_4) {
                this.caughtException = e;
                this.shutdown();
            }
            return;
        }
    }

    public synchronized void shutdown() {
        this.readingThreads = null;
        this.notifyAll();
    }
}

