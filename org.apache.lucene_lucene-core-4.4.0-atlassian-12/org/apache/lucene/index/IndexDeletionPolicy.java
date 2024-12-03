/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.IndexCommit;

public abstract class IndexDeletionPolicy
implements Cloneable {
    protected IndexDeletionPolicy() {
    }

    public abstract void onInit(List<? extends IndexCommit> var1) throws IOException;

    public abstract void onCommit(List<? extends IndexCommit> var1) throws IOException;

    public IndexDeletionPolicy clone() {
        try {
            return (IndexDeletionPolicy)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}

