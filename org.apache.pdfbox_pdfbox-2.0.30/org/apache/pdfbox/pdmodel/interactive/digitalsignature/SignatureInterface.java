/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.IOException;
import java.io.InputStream;

public interface SignatureInterface {
    public byte[] sign(InputStream var1) throws IOException;
}

