/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.CompressedResource;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.bzip2.CBZip2OutputStream;

public class BZip2Resource
extends CompressedResource {
    private static final char[] MAGIC = new char[]{'B', 'Z'};

    public BZip2Resource() {
    }

    public BZip2Resource(ResourceCollection other) {
        super(other);
    }

    @Override
    protected InputStream wrapStream(InputStream in) throws IOException {
        for (char ch : MAGIC) {
            if (in.read() == ch) continue;
            throw new IOException("Invalid bz2 stream.");
        }
        return new CBZip2InputStream(in);
    }

    @Override
    protected OutputStream wrapStream(OutputStream out) throws IOException {
        for (char ch : MAGIC) {
            out.write(ch);
        }
        return new CBZip2OutputStream(out);
    }

    @Override
    protected String getCompressionName() {
        return "Bzip2";
    }
}

