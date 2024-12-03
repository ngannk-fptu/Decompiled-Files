/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes;

import java.io.Closeable;
import java.io.IOException;
import org.apache.tika.pipes.FetchEmitTuple;
import org.apache.tika.pipes.PipesResult;

public abstract class PipesReporter
implements Closeable {
    public static final PipesReporter NO_OP_REPORTER = new PipesReporter(){

        @Override
        public void report(FetchEmitTuple t, PipesResult result, long elapsed) {
        }
    };

    public abstract void report(FetchEmitTuple var1, PipesResult var2, long var3);

    @Override
    public void close() throws IOException {
    }
}

