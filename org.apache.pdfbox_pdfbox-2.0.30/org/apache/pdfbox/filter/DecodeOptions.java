/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.filter;

import java.awt.Rectangle;

public class DecodeOptions {
    public static final DecodeOptions DEFAULT = new FinalDecodeOptions(true);
    private Rectangle sourceRegion = null;
    private int subsamplingX = 1;
    private int subsamplingY = 1;
    private int subsamplingOffsetX = 0;
    private int subsamplingOffsetY = 0;
    private boolean filterSubsampled = false;

    public DecodeOptions() {
    }

    public DecodeOptions(Rectangle sourceRegion) {
        this.sourceRegion = sourceRegion;
    }

    public DecodeOptions(int x, int y, int width, int height) {
        this(new Rectangle(x, y, width, height));
    }

    public DecodeOptions(int subsampling) {
        this.subsamplingX = subsampling;
        this.subsamplingY = subsampling;
    }

    public Rectangle getSourceRegion() {
        return this.sourceRegion;
    }

    public void setSourceRegion(Rectangle sourceRegion) {
        this.sourceRegion = sourceRegion;
    }

    public int getSubsamplingX() {
        return this.subsamplingX;
    }

    public void setSubsamplingX(int ssX) {
        this.subsamplingX = ssX;
    }

    public int getSubsamplingY() {
        return this.subsamplingY;
    }

    public void setSubsamplingY(int ssY) {
        this.subsamplingY = ssY;
    }

    public int getSubsamplingOffsetX() {
        return this.subsamplingOffsetX;
    }

    public void setSubsamplingOffsetX(int ssOffsetX) {
        this.subsamplingOffsetX = ssOffsetX;
    }

    public int getSubsamplingOffsetY() {
        return this.subsamplingOffsetY;
    }

    public void setSubsamplingOffsetY(int ssOffsetY) {
        this.subsamplingOffsetY = ssOffsetY;
    }

    public boolean isFilterSubsampled() {
        return this.filterSubsampled;
    }

    void setFilterSubsampled(boolean filterSubsampled) {
        this.filterSubsampled = filterSubsampled;
    }

    private static class FinalDecodeOptions
    extends DecodeOptions {
        FinalDecodeOptions(boolean filterSubsampled) {
            super.setFilterSubsampled(filterSubsampled);
        }

        @Override
        public void setSourceRegion(Rectangle sourceRegion) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override
        public void setSubsamplingX(int ssX) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override
        public void setSubsamplingY(int ssY) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override
        public void setSubsamplingOffsetX(int ssOffsetX) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override
        public void setSubsamplingOffsetY(int ssOffsetY) {
            throw new UnsupportedOperationException("This instance may not be modified.");
        }

        @Override
        void setFilterSubsampled(boolean filterSubsampled) {
        }
    }
}

