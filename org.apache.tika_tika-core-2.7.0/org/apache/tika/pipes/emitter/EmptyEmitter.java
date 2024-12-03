/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

import java.io.IOException;
import java.util.List;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.pipes.emitter.Emitter;
import org.apache.tika.pipes.emitter.TikaEmitterException;

public class EmptyEmitter
implements Emitter {
    @Override
    public String getName() {
        return "empty";
    }

    @Override
    public void emit(String emitKey, List<Metadata> metadataList) throws IOException, TikaEmitterException {
    }

    @Override
    public void emit(List<? extends EmitData> emitData) throws IOException, TikaEmitterException {
    }
}

