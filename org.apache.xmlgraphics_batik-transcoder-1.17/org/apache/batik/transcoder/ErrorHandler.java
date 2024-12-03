/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder;

import org.apache.batik.transcoder.TranscoderException;

public interface ErrorHandler {
    public void error(TranscoderException var1) throws TranscoderException;

    public void fatalError(TranscoderException var1) throws TranscoderException;

    public void warning(TranscoderException var1) throws TranscoderException;
}

