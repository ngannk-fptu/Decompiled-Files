/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.mime;

import java.io.OutputStream;
import org.apache.axiom.mime.MultipartWriter;

public interface MultipartWriterFactory {
    public MultipartWriter createMultipartWriter(OutputStream var1, String var2);
}

