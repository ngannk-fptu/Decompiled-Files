/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockReleaseFailedException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashSet;

class NativeFSLock
extends Lock {
    private RandomAccessFile f;
    private FileChannel channel;
    private FileLock lock;
    private File path;
    private File lockDir;
    private static HashSet<String> LOCK_HELD = new HashSet();

    public NativeFSLock(File lockDir, String lockFileName) {
        this.lockDir = lockDir;
        this.path = new File(lockDir, lockFileName);
    }

    private synchronized boolean lockExists() {
        return this.lock != null;
    }

    /*
     * Exception decompiling
     */
    public synchronized boolean obtain() throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Missing node tying up JSR block
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.tieUpRelations(Op02WithProcessedDataAndRefs.java:2900)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.copyBlock(Op02WithProcessedDataAndRefs.java:2889)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.inlineJSR(Op02WithProcessedDataAndRefs.java:2845)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.processJSRs(Op02WithProcessedDataAndRefs.java:2591)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.processJSR(Op02WithProcessedDataAndRefs.java:2481)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:444)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public synchronized void release() throws IOException {
        if (this.lockExists()) {
            Object v3;
            try {
                this.lock.release();
                Object var2_1 = null;
                this.lock = null;
            }
            catch (Throwable throwable) {
                Object v1;
                Object var2_2 = null;
                this.lock = null;
                try {
                    this.channel.close();
                    Object var4_5 = null;
                    this.channel = null;
                }
                catch (Throwable throwable2) {
                    Object v0;
                    Object var4_6 = null;
                    this.channel = null;
                    try {
                        this.f.close();
                        v0 = null;
                    }
                    catch (Throwable throwable3) {
                        v0 = null;
                    }
                    Object var6_14 = v0;
                    this.f = null;
                    HashSet<String> hashSet = LOCK_HELD;
                    synchronized (hashSet) {
                        LOCK_HELD.remove(this.path.getCanonicalPath());
                        throw throwable2;
                    }
                }
                try {
                    this.f.close();
                    v1 = null;
                }
                catch (Throwable throwable4) {
                    v1 = null;
                }
                Object var6_13 = v1;
                this.f = null;
                HashSet<String> hashSet = LOCK_HELD;
                synchronized (hashSet) {
                    LOCK_HELD.remove(this.path.getCanonicalPath());
                    throw throwable;
                }
            }
            try {
                this.channel.close();
                Object var4_3 = null;
                this.channel = null;
            }
            catch (Throwable throwable) {
                Object v2;
                Object var4_4 = null;
                this.channel = null;
                try {
                    this.f.close();
                    v2 = null;
                }
                catch (Throwable throwable5) {
                    v2 = null;
                }
                Object var6_12 = v2;
                this.f = null;
                HashSet<String> hashSet = LOCK_HELD;
                synchronized (hashSet) {
                    LOCK_HELD.remove(this.path.getCanonicalPath());
                    throw throwable;
                }
            }
            try {
                this.f.close();
                v3 = null;
            }
            catch (Throwable throwable) {
                v3 = null;
            }
            Object var6_11 = v3;
            this.f = null;
            HashSet<String> hashSet = LOCK_HELD;
            synchronized (hashSet) {
                LOCK_HELD.remove(this.path.getCanonicalPath());
            }
            this.path.delete();
            return;
        }
        boolean obtained = false;
        try {
            obtained = this.obtain();
            if (!obtained) {
                throw new LockReleaseFailedException("Cannot forcefully unlock a NativeFSLock which is held by another indexer component: " + this.path);
            }
            Object var10_27 = null;
            if (!obtained) return;
        }
        catch (Throwable throwable) {
            Object var10_28 = null;
            if (!obtained) throw throwable;
            this.release();
            throw throwable;
        }
        this.release();
    }

    public synchronized boolean isLocked() {
        if (this.lockExists()) {
            return true;
        }
        if (!this.path.exists()) {
            return false;
        }
        try {
            boolean obtained = this.obtain();
            if (obtained) {
                this.release();
            }
            return !obtained;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public String toString() {
        return "NativeFSLock@" + this.path;
    }
}

