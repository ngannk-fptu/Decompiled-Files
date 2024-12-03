/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

import java.io.IOException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TwoPhaseCommit {
    public void prepareCommit() throws IOException;

    public void prepareCommit(Map<String, String> var1) throws IOException;

    public void commit() throws IOException;

    public void commit(Map<String, String> var1) throws IOException;

    public void rollback() throws IOException;
}

