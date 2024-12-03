/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.DocumentsWriter;
import org.apache.lucene.index.DocumentsWriterFlushControl;
import org.apache.lucene.index.DocumentsWriterPerThreadPool;
import org.apache.lucene.index.FlushPolicy;

class FlushByRamOrCountsPolicy
extends FlushPolicy {
    FlushByRamOrCountsPolicy() {
    }

    @Override
    public void onDelete(DocumentsWriterFlushControl control, DocumentsWriterPerThreadPool.ThreadState state) {
        if (this.flushOnDeleteTerms()) {
            int maxBufferedDeleteTerms = this.indexWriterConfig.getMaxBufferedDeleteTerms();
            if (control.getNumGlobalTermDeletes() >= maxBufferedDeleteTerms) {
                control.setApplyAllDeletes();
            }
        }
        DocumentsWriter writer = (DocumentsWriter)this.writer.get();
        if (this.flushOnRAM() && (double)control.getDeleteBytesUsed() > 1048576.0 * this.indexWriterConfig.getRAMBufferSizeMB()) {
            control.setApplyAllDeletes();
            if (writer.infoStream.isEnabled("FP")) {
                writer.infoStream.message("FP", "force apply deletes bytesUsed=" + control.getDeleteBytesUsed() + " vs ramBuffer=" + 1048576.0 * this.indexWriterConfig.getRAMBufferSizeMB());
            }
        }
    }

    @Override
    public void onInsert(DocumentsWriterFlushControl control, DocumentsWriterPerThreadPool.ThreadState state) {
        if (this.flushOnDocCount() && state.dwpt.getNumDocsInRAM() >= this.indexWriterConfig.getMaxBufferedDocs()) {
            control.setFlushPending(state);
        } else if (this.flushOnRAM()) {
            long limit = (long)(this.indexWriterConfig.getRAMBufferSizeMB() * 1024.0 * 1024.0);
            long totalRam = control.activeBytes() + control.getDeleteBytesUsed();
            if (totalRam >= limit) {
                DocumentsWriter writer = (DocumentsWriter)this.writer.get();
                if (writer.infoStream.isEnabled("FP")) {
                    writer.infoStream.message("FP", "flush: activeBytes=" + control.activeBytes() + " deleteBytes=" + control.getDeleteBytesUsed() + " vs limit=" + limit);
                }
                this.markLargestWriterPending(control, state, totalRam);
            }
        }
    }

    protected void markLargestWriterPending(DocumentsWriterFlushControl control, DocumentsWriterPerThreadPool.ThreadState perThreadState, long currentBytesPerThread) {
        control.setFlushPending(this.findLargestNonPendingWriter(control, perThreadState));
    }

    protected boolean flushOnDocCount() {
        return this.indexWriterConfig.getMaxBufferedDocs() != -1;
    }

    protected boolean flushOnDeleteTerms() {
        return this.indexWriterConfig.getMaxBufferedDeleteTerms() != -1;
    }

    protected boolean flushOnRAM() {
        return this.indexWriterConfig.getRAMBufferSizeMB() != -1.0;
    }
}

