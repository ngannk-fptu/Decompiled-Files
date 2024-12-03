/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.convert.image;

import com.sun.pdfview.ImageInfo;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryAwarePDFRenderer
extends PDFRenderer {
    private static final int BYTES_PER_PIXEL = 4;
    private static final float PROCESSING_HEADROOM = 1.5f;
    private static final float MAXIMUM_IMAGE_SIZE = 2.097152E7f;
    public static final String PDF_RENDER_MEMORY_GUARD_DISABLED_PROPERTY_NAME = "pdf.render.memory.guard.disabled";
    private static final Logger log = LoggerFactory.getLogger(MemoryAwarePDFRenderer.class);
    private final boolean pdfRenderMemoryGuardDisabled = Boolean.getBoolean("pdf.render.memory.guard.disabled");

    public MemoryAwarePDFRenderer(PDFPage page, ImageInfo imageinfo, BufferedImage bi) {
        super(page, imageinfo, bi);
    }

    public MemoryAwarePDFRenderer(PDFPage page, Graphics2D g, Rectangle imgbounds, Rectangle2D clip, Color bgColor) {
        super(page, g, imgbounds, clip, bgColor);
    }

    @Override
    public Rectangle2D drawImage(PDFImage image) {
        if (!this.enoughMemoryToDraw(image)) {
            log.warn("Image of size {}*{} px dropped for memory protection", (Object)image.getWidth(), (Object)image.getHeight());
            return new Rectangle();
        }
        return super.drawImage(image);
    }

    private boolean enoughMemoryToDraw(PDFImage image) {
        if (this.pdfRenderMemoryGuardDisabled) {
            return true;
        }
        float neededMemory = 1.5f * (float)image.getWidth() * (float)image.getHeight() * 4.0f;
        return neededMemory < (float)this.freeMemory() && neededMemory < 2.097152E7f;
    }

    long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}

