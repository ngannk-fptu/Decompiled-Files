/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;

public interface ErrorHandler {
    public void handleError(SVGGraphics2DIOException var1) throws SVGGraphics2DIOException;

    public void handleError(SVGGraphics2DRuntimeException var1) throws SVGGraphics2DRuntimeException;
}

