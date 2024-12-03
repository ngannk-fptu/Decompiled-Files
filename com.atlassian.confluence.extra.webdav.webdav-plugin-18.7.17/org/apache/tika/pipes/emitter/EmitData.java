/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

import java.io.Serializable;
import java.util.List;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.EmitKey;

public class EmitData
implements Serializable {
    private static final long serialVersionUID = -3861669115439125268L;
    private final EmitKey emitKey;
    private final List<Metadata> metadataList;

    public EmitData(EmitKey emitKey, List<Metadata> metadataList) {
        this.emitKey = emitKey;
        this.metadataList = metadataList;
    }

    public EmitKey getEmitKey() {
        return this.emitKey;
    }

    public List<Metadata> getMetadataList() {
        return this.metadataList;
    }

    public long getEstimatedSizeBytes() {
        return EmitData.estimateSizeInBytes(this.getEmitKey().getEmitKey(), this.getMetadataList());
    }

    private static long estimateSizeInBytes(String id, List<Metadata> metadataList) {
        long sz = 36 + id.length() * 2;
        for (Metadata m : metadataList) {
            for (String n : m.names()) {
                sz += (long)(36 + n.length() * 2);
                for (String v : m.getValues(n)) {
                    sz += (long)(36 + v.length() * 2);
                }
            }
        }
        return sz;
    }

    public String toString() {
        return "EmitData{emitKey=" + this.emitKey + ", metadataList=" + this.metadataList + '}';
    }
}

