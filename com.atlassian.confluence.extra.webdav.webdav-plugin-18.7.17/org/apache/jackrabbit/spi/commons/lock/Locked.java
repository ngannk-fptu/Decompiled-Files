/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.lock;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;

public abstract class Locked {
    public static final Object TIMED_OUT = new Object();

    public Object with(Node lockable, boolean isDeep) throws RepositoryException, InterruptedException {
        return this.with(lockable, isDeep, Long.MAX_VALUE);
    }

    /*
     * Exception decompiling
     */
    public Object with(Node lockable, boolean isDeep, long timeout) throws UnsupportedRepositoryOperationException, RepositoryException, InterruptedException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 12[UNCONDITIONALDOLOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
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

    protected abstract Object run(Node var1) throws RepositoryException;

    private Object runAndUnlock(Lock lock) throws RepositoryException {
        try {
            Object object = this.run(lock.getNode());
            return object;
        }
        finally {
            lock.getNode().unlock();
        }
    }

    private static Lock tryLock(Node lockable, boolean isDeep) throws UnsupportedRepositoryOperationException, RepositoryException {
        try {
            return lockable.lock(isDeep, true);
        }
        catch (LockException lockException) {
            return null;
        }
    }

    private static boolean isObservationSupported(Session s) {
        return "true".equalsIgnoreCase(s.getRepository().getDescriptor("option.observation.supported"));
    }
}

