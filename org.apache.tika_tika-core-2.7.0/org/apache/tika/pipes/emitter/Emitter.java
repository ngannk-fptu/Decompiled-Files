/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

import java.io.IOException;
import java.util.List;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.pipes.emitter.TikaEmitterException;

public interface Emitter {
    public String getName();

    public void emit(String var1, List<Metadata> var2) throws IOException, TikaEmitterException;

    public void emit(List<? extends EmitData> var1) throws IOException, TikaEmitterException;
}

