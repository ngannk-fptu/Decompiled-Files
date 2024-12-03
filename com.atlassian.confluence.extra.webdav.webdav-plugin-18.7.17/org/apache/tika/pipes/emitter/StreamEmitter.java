/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.pipes.emitter;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.Emitter;
import org.apache.tika.pipes.emitter.TikaEmitterException;

public interface StreamEmitter
extends Emitter {
    public void emit(String var1, InputStream var2, Metadata var3) throws IOException, TikaEmitterException;
}

