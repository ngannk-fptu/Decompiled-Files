/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;

public class PDFileAttachmentAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDFileAttachmentAppearanceHandler.class);

    public PDFileAttachmentAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDFileAttachmentAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationFileAttachment annotation = (PDAnnotationFileAttachment)this.getAnnotation();
        PDRectangle rect = this.getRectangle();
        if (rect == null) {
            return;
        }
        PDAppearanceContentStream contentStream = null;
        try {
            contentStream = this.getNormalAppearanceAsContentStream();
            this.setOpacity(contentStream, annotation.getConstantOpacity());
            int size = 18;
            rect.setUpperRightX(rect.getLowerLeftX() + (float)size);
            rect.setLowerLeftY(rect.getUpperRightY() - (float)size);
            annotation.setRectangle(rect);
            annotation.getNormalAppearanceStream().setBBox(new PDRectangle(size, size));
            this.drawPaperclip(contentStream);
        }
        catch (IOException e) {
            LOG.error((Object)e);
        }
        finally {
            IOUtils.closeQuietly(contentStream);
        }
    }

    private void drawPaperclip(PDAppearanceContentStream contentStream) throws IOException {
        contentStream.moveTo(13.574f, 9.301f);
        contentStream.lineTo(8.926f, 13.949f);
        contentStream.curveTo(7.648f, 15.227f, 5.625f, 15.227f, 4.426f, 13.949f);
        contentStream.curveTo(3.148f, 12.676f, 3.148f, 10.648f, 4.426f, 9.449f);
        contentStream.lineTo(10.426f, 3.449f);
        contentStream.curveTo(11.176f, 2.773f, 12.301f, 2.773f, 13.051f, 3.449f);
        contentStream.curveTo(13.801f, 4.199f, 13.801f, 5.398f, 13.051f, 6.074f);
        contentStream.lineTo(7.875f, 11.25f);
        contentStream.curveTo(7.648f, 11.477f, 7.273f, 11.477f, 7.051f, 11.25f);
        contentStream.curveTo(6.824f, 11.023f, 6.824f, 10.648f, 7.051f, 10.426f);
        contentStream.lineTo(10.875f, 6.602f);
        contentStream.curveTo(11.176f, 6.301f, 11.176f, 5.852f, 10.875f, 5.551f);
        contentStream.curveTo(10.574f, 5.25f, 10.125f, 5.25f, 9.824f, 5.551f);
        contentStream.lineTo(6.0f, 9.449f);
        contentStream.curveTo(5.176f, 10.273f, 5.176f, 11.551f, 6.0f, 12.375f);
        contentStream.curveTo(6.824f, 13.125f, 8.102f, 13.125f, 8.926f, 12.375f);
        contentStream.lineTo(14.102f, 7.199f);
        contentStream.curveTo(15.449f, 5.852f, 15.449f, 3.75f, 14.102f, 2.398f);
        contentStream.curveTo(12.75f, 1.051f, 10.648f, 1.051f, 9.301f, 2.398f);
        contentStream.lineTo(3.301f, 8.398f);
        contentStream.curveTo(2.398f, 9.301f, 1.949f, 10.5f, 1.949f, 11.699f);
        contentStream.curveTo(1.949f, 14.324f, 4.051f, 16.352f, 6.676f, 16.352f);
        contentStream.curveTo(7.949f, 16.352f, 9.074f, 15.824f, 9.977f, 15.0f);
        contentStream.lineTo(14.625f, 10.352f);
        contentStream.curveTo(14.926f, 10.051f, 14.926f, 9.602f, 14.625f, 9.301f);
        contentStream.curveTo(14.324f, 9.0f, 13.875f, 9.0f, 13.574f, 9.301f);
        contentStream.closePath();
        contentStream.fill();
    }

    @Override
    public void generateRolloverAppearance() {
    }

    @Override
    public void generateDownAppearance() {
    }
}

