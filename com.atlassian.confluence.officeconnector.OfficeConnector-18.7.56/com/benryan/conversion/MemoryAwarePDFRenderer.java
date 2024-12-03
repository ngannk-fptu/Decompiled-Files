/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.pdfview.ImageInfo
 *  com.sun.pdfview.PDFImage
 *  com.sun.pdfview.PDFPage
 *  com.sun.pdfview.PDFRenderer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.conversion;

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
    public static final String PDF_MEMORY_GUARD_ENABLED_PROPERTY_KEY = "officeconnector.pdf.memory.guard.enabled";
    private static final Logger log = LoggerFactory.getLogger(MemoryAwarePDFRenderer.class);
    private final boolean pdfMemoryGuardEnabled = Boolean.getBoolean("officeconnector.pdf.memory.guard.enabled");

    public MemoryAwarePDFRenderer(PDFPage page, ImageInfo imageinfo, BufferedImage bi) {
        super(page, imageinfo, bi);
    }

    public MemoryAwarePDFRenderer(PDFPage page, Graphics2D g, Rectangle imgbounds, Rectangle2D clip, Color bgColor) {
        super(page, g, imgbounds, clip, bgColor);
    }

    public Rectangle2D drawImage(PDFImage image) {
        if (!this.enoughMemoryToDraw(image)) {
            log.warn("Image of size {}*{} px dropped for memory protection", (Object)image.getWidth(), (Object)image.getHeight());
            return new Rectangle();
        }
        return super.drawImage(image);
    }

    private boolean enoughMemoryToDraw(PDFImage image) {
        if (this.pdfMemoryGuardEnabled) {
            float neededMemory = 1.5f * (float)image.getWidth() * (float)image.getHeight() * 4.0f;
            return neededMemory < (float)this.freeMemory() && neededMemory < 2.097152E7f;
        }
        return true;
    }

    long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}

