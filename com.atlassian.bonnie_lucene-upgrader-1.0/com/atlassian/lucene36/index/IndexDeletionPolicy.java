/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexCommit;
import java.io.IOException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface IndexDeletionPolicy {
    public void onInit(List<? extends IndexCommit> var1) throws IOException;

    public void onCommit(List<? extends IndexCommit> var1) throws IOException;
}

