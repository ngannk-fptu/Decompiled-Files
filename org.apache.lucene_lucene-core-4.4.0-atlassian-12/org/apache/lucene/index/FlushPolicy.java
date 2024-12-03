/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.Iterator;
import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.DocumentsWriterFlushControl;
import org.apache.lucene.index.DocumentsWriterPerThreadPool;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.util.SetOnce;

abstract class FlushPolicy
implements Cloneable {
    protected SetOnce<DocumentsWriter> writer = new SetOnce();
    protected LiveIndexWriterConfig indexWriterConfig;

    FlushPolicy() {
    }

    public abstract void onDelete(DocumentsWriterFlushControl var1, DocumentsWriterPerThreadPool.ThreadState var2);

    public void onUpdate(DocumentsWriterFlushControl control, DocumentsWriterPerThreadPool.ThreadState state) {
        this.onInsert(control, state);
        this.onDelete(control, state);
    }

    public abstract void onInsert(DocumentsWriterFlushControl var1, DocumentsWriterPerThreadPool.ThreadState var2);

    protected synchronized void init(DocumentsWriter docsWriter) {
        this.writer.set(docsWriter);
        this.indexWriterConfig = docsWriter.indexWriter.getConfig();
    }

    protected DocumentsWriterPerThreadPool.ThreadState findLargestNonPendingWriter(DocumentsWriterFlushControl control, DocumentsWriterPerThreadPool.ThreadState perThreadState) {
        assert (perThreadState.dwpt.getNumDocsInRAM() > 0);
        long maxRamSoFar = perThreadState.bytesUsed;
        DocumentsWriterPerThreadPool.ThreadState maxRamUsingThreadState = perThreadState;
        assert (!perThreadState.flushPending) : "DWPT should have flushed";
        Iterator<DocumentsWriterPerThreadPool.ThreadState> activePerThreadsIterator = control.allActiveThreadStates();
        while (activePerThreadsIterator.hasNext()) {
            long nextRam;
            DocumentsWriterPerThreadPool.ThreadState next = activePerThreadsIterator.next();
            if (next.flushPending || (nextRam = next.bytesUsed) <= maxRamSoFar || next.dwpt.getNumDocsInRAM() <= 0) continue;
            maxRamSoFar = nextRam;
            maxRamUsingThreadState = next;
        }
        assert (this.assertMessage("set largest ram consuming thread pending on lower watermark"));
        return maxRamUsingThreadState;
    }

    private boolean assertMessage(String s) {
        if (this.writer.get().infoStream.isEnabled("FP")) {
            this.writer.get().infoStream.message("FP", s);
        }
        return true;
    }

    public FlushPolicy clone() {
        FlushPolicy clone;
        try {
            clone = (FlushPolicy)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        clone.writer = new SetOnce();
        clone.indexWriterConfig = null;
        return clone;
    }
}

