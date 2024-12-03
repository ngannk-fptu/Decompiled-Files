/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import org.apache.batik.svggen.ErrorHandler;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;

public class DefaultErrorHandler
implements ErrorHandler {
    @Override
    public void handleError(SVGGraphics2DIOException ex) throws SVGGraphics2DIOException {
        throw ex;
    }

    @Override
    public void handleError(SVGGraphics2DRuntimeException ex) throws SVGGraphics2DRuntimeException {
        System.err.println(ex.getMessage());
    }
}

