/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.mime.impl.axiom;

import java.io.OutputStream;
import org.apache.axiom.mime.MultipartWriter;
import org.apache.axiom.mime.MultipartWriterFactory;
import org.apache.axiom.mime.impl.axiom.MultipartWriterImpl;

public class AxiomMultipartWriterFactory
implements MultipartWriterFactory {
    public static final MultipartWriterFactory INSTANCE = new AxiomMultipartWriterFactory();

    public MultipartWriter createMultipartWriter(OutputStream out, String boundary) {
        return new MultipartWriterImpl(out, boundary);
    }
}

