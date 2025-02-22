/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.spi;

public class ImageWriterParams {
    private Integer resolution;
    private Float jpegQuality;
    private Boolean jpegForceBaseline;
    private String compressionMethod;

    public Integer getResolution() {
        return this.resolution;
    }

    public Float getJPEGQuality() {
        return this.jpegQuality;
    }

    public Boolean getJPEGForceBaseline() {
        return this.jpegForceBaseline;
    }

    public String getCompressionMethod() {
        return this.compressionMethod;
    }

    public void setResolution(int dpi) {
        this.resolution = dpi;
    }

    public void setJPEGQuality(float quality, boolean forceBaseline) {
        this.jpegQuality = Float.valueOf(quality);
        this.jpegForceBaseline = forceBaseline ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setCompressionMethod(String method) {
        this.compressionMethod = method;
    }
}

