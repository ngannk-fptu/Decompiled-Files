/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import org.apache.http.client.cache.Resource;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.util.Args;

class CombinedEntity
extends AbstractHttpEntity {
    private final Resource resource;
    private final InputStream combinedStream;

    CombinedEntity(Resource resource, InputStream inStream) throws IOException {
        this.resource = resource;
        this.combinedStream = new SequenceInputStream(new ResourceStream(resource.getInputStream()), inStream);
    }

    @Override
    public long getContentLength() {
        return -1L;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return this.combinedStream;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void writeTo(OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        InputStream inStream = this.getContent();
        try {
            int l;
            byte[] tmp = new byte[2048];
            while ((l = inStream.read(tmp)) != -1) {
                outStream.write(tmp, 0, l);
            }
        }
        finally {
            inStream.close();
        }
    }

    private void dispose() {
        this.resource.dispose();
    }

    class ResourceStream
    extends FilterInputStream {
        protected ResourceStream(InputStream in) {
            super(in);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void close() throws IOException {
            try {
                super.close();
            }
            finally {
                CombinedEntity.this.dispose();
            }
        }
    }
}

