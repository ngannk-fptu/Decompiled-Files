/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DefaultSkipListWriter;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.index.FormatPostingsFieldsConsumer;
import com.atlassian.lucene36.index.FormatPostingsTermsConsumer;
import com.atlassian.lucene36.index.FormatPostingsTermsWriter;
import com.atlassian.lucene36.index.SegmentWriteState;
import com.atlassian.lucene36.index.TermInfosWriter;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.util.IOUtils;
import java.io.IOException;

final class FormatPostingsFieldsWriter
extends FormatPostingsFieldsConsumer {
    final Directory dir;
    final String segment;
    TermInfosWriter termsOut;
    final FieldInfos fieldInfos;
    FormatPostingsTermsWriter termsWriter;
    final DefaultSkipListWriter skipListWriter;
    final int totalNumDocs;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public FormatPostingsFieldsWriter(SegmentWriteState state, FieldInfos fieldInfos) throws IOException {
        this.dir = state.directory;
        this.segment = state.segmentName;
        this.totalNumDocs = state.numDocs;
        this.fieldInfos = fieldInfos;
        boolean success = false;
        try {
            this.termsOut = new TermInfosWriter(this.dir, this.segment, fieldInfos, state.termIndexInterval);
            this.skipListWriter = new DefaultSkipListWriter(this.termsOut.skipInterval, this.termsOut.maxSkipLevels, this.totalNumDocs, null, null);
            this.termsWriter = new FormatPostingsTermsWriter(state, this);
            return;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            if (success) throw throwable;
            IOUtils.closeWhileHandlingException(this.termsOut, this.termsWriter);
            throw throwable;
        }
    }

    FormatPostingsTermsConsumer addField(FieldInfo field) {
        this.termsWriter.setField(field);
        return this.termsWriter;
    }

    void finish() throws IOException {
        IOUtils.close(this.termsOut, this.termsWriter);
    }
}

