/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.index.TwoPhaseCommit;

public final class TwoPhaseCommitTool {
    private TwoPhaseCommitTool() {
    }

    private static void rollback(TwoPhaseCommit ... objects) {
        for (TwoPhaseCommit tpc : objects) {
            if (tpc == null) continue;
            try {
                tpc.rollback();
            }
            catch (Throwable throwable) {
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
}

