/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.io.Closeable;
import java.io.IOException;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.PipesResult;
import org.apache.tika.pipes.pipesiterator.TotalCountResult;

public abstract class PipesReporter
implements Closeable {
    public static final PipesReporter NO_OP_REPORTER = new PipesReporter(){

        @Override
        public void report(FetchEmitTuple t, PipesResult result, long elapsed) {
        }

        @Override
        public void error(Throwable t) {
        }

        @Override
        public void error(String msg) {
        }
    };

    public abstract void report(FetchEmitTuple var1, PipesResult var2, long var3);

    public void report(TotalCountResult totalCountResult) {
    }

    public boolean supportsTotalCount() {
        return false;
    }

    @Override
    public void close() throws IOException {
    }

    public abstract void error(Throwable var1);

    public abstract void error(String var1);
}

