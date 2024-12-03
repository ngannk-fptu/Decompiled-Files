/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

public class SVGGraphics2DRuntimeException
extends RuntimeException {
    private Exception embedded;

    public SVGGraphics2DRuntimeException(String s) {
        this(s, null);
    }

    public SVGGraphics2DRuntimeException(Exception ex) {
        this(null, ex);
    }

    public SVGGraphics2DRuntimeException(String s, Exception ex) {
        super(s);
        this.embedded = ex;
    }

    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (msg != null) {
            return msg;
        }
        if (this.embedded != null) {
            return this.embedded.getMessage();
        }
        return null;
    }

    public Exception getException() {
        return this.embedded;
    }
}

