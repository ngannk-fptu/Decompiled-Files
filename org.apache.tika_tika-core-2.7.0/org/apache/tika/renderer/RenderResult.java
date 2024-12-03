/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.renderer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

public class RenderResult
implements Closeable {
    private final STATUS status;
    private final int id;
    private final Object result;
    private final Metadata metadata;
    TemporaryResources tmp = new TemporaryResources();

    public RenderResult(STATUS status, int id, final Object result, Metadata metadata) {
        this.status = status;
        this.id = id;
        this.result = result;
        this.metadata = metadata;
        if (result instanceof Path) {
            this.tmp.addResource(new Closeable(){

                @Override
                public void close() throws IOException {
                    Files.delete((Path)result);
                }
            });
        } else if (result instanceof Closeable) {
            this.tmp.addResource((Closeable)result);
        }
    }

    public InputStream getInputStream() throws IOException {
        if (this.result instanceof Path) {
            return TikaInputStream.get((Path)this.result, this.metadata);
        }
        TikaInputStream tis = TikaInputStream.get(new byte[0]);
        tis.setOpenContainer(this.result);
        return tis;
    }

    public Metadata getMetadata() {
        return this.metadata;
    }

    public STATUS getStatus() {
        return this.status;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void close() throws IOException {
        this.tmp.close();
    }

    public static enum STATUS {
        SUCCESS,
        EXCEPTION,
        TIMEOUT;

    }
}

