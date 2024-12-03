/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

import java.io.IOException;
import java.util.List;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.pipes.emitter.Emitter;
import org.apache.tika.pipes.emitter.TikaEmitterException;

public abstract class AbstractEmitter
implements Emitter {
    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void emit(List<? extends EmitData> emitData) throws IOException, TikaEmitterException {
        for (EmitData emitData2 : emitData) {
            this.emit(emitData2.getEmitKey().getEmitKey(), emitData2.getMetadataList());
        }
    }
}

