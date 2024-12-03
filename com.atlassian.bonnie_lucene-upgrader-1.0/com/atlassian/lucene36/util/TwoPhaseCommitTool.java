/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import com.atlassian.lucene36.util.TwoPhaseCommit;
import java.io.IOException;
import java.util.Map;

public final class TwoPhaseCommitTool {
    private static void rollback(TwoPhaseCommit ... objects) {
        for (TwoPhaseCommit tpc : objects) {
            if (tpc == null) continue;
            try {
                tpc.rollback();
            }
            catch (Throwable t) {
                // empty catch block
            }
        }
    }

    public static void execute(TwoPhaseCommit ... objects) throws PrepareCommitFailException, CommitFailException {
        int i;
        TwoPhaseCommit tpc = null;
        try {
            for (i = 0; i < objects.length; ++i) {
                tpc = objects[i];
                if (tpc == null) continue;
                tpc.prepareCommit();
            }
        }
        catch (Throwable t) {
            TwoPhaseCommitTool.rollback(objects);
            throw new PrepareCommitFailException(t, tpc);
        }
        try {
            for (i = 0; i < objects.length; ++i) {
                tpc = objects[i];
                if (tpc == null) continue;
                tpc.commit();
            }
        }
        catch (Throwable t) {
            TwoPhaseCommitTool.rollback(objects);
            throw new CommitFailException(t, tpc);
        }
    }

    public static class CommitFailException
    extends IOException {
        public CommitFailException(Throwable cause, TwoPhaseCommit obj) {
            super("commit() failed on " + obj);
            this.initCause(cause);
        }
    }

    public static class PrepareCommitFailException
    extends IOException {
        public PrepareCommitFailException(Throwable cause, TwoPhaseCommit obj) {
            super("prepareCommit() failed on " + obj);
            this.initCause(cause);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class TwoPhaseCommitWrapper
    implements TwoPhaseCommit {
        private final TwoPhaseCommit tpc;
        private final Map<String, String> commitData;

        public TwoPhaseCommitWrapper(TwoPhaseCommit tpc, Map<String, String> commitData) {
            this.tpc = tpc;
            this.commitData = commitData;
        }

        @Override
        public void prepareCommit() throws IOException {
            this.prepareCommit(this.commitData);
        }

        @Override
        public void prepareCommit(Map<String, String> commitData) throws IOException {
            this.tpc.prepareCommit(this.commitData);
        }

        @Override
        public void commit() throws IOException {
            this.commit(this.commitData);
        }

        @Override
        public void commit(Map<String, String> commitData) throws IOException {
            this.tpc.commit(this.commitData);
        }

        @Override
        public void rollback() throws IOException {
            this.tpc.rollback();
        }
    }
}

