/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.CompressedResource;

public class GZipResource
extends CompressedResource {
    public GZipResource() {
    }

    public GZipResource(ResourceCollection other) {
        super(other);
    }

    @Override
    protected InputStream wrapStream(InputStream in) throws IOException {
        return new GZIPInputStream(in);
    }

    @Override
    protected OutputStream wrapStream(OutputStream out) throws IOException {
        return new GZIPOutputStream(out);
    }

    @Override
    protected String getCompressionName() {
        return "GZip";
    }
}

