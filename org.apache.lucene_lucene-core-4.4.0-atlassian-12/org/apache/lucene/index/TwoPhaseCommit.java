/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;

public interface TwoPhaseCommit {
    public void prepareCommit() throws IOException;

    public void commit() throws IOException;

    public void rollback() throws IOException;
}

