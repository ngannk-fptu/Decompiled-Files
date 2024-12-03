/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.transcoder;

import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;

public class DefaultErrorHandler
implements ErrorHandler {
    @Override
    public void error(TranscoderException ex) throws TranscoderException {
        System.err.println("ERROR: " + ex.getMessage());
    }

    @Override
    public void fatalError(TranscoderException ex) throws TranscoderException {
        throw ex;
    }

    @Override
    public void warning(TranscoderException ex) throws TranscoderException {
        System.err.println("WARNING: " + ex.getMessage());
    }
}

