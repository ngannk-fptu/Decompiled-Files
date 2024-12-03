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
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;
import org.apache.pdfbox.util.Matrix;

public class PDCaretAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDCaretAppearanceHandler.class);

    public PDCaretAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDCaretAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationMarkup annotation = (PDAnnotationMarkup)this.getAnnotation();
        PDAppearanceContentStream contentStream = null;
        try {
            contentStream = this.getNormalAppearanceAsContentStream();
            contentStream.setStrokingColor(this.getColor());
            contentStream.setNonStrokingColor(this.getColor());
            this.setOpacity(contentStream, annotation.getConstantOpacity());
            PDRectangle rect = this.getRectangle();
            PDRectangle bbox = new PDRectangle(rect.getWidth(), rect.getHeight());
            PDAppearanceStream pdAppearanceStream = annotation.getNormalAppearanceStream();
            if (!annotation.getCOSObject().containsKey(COSName.RD)) {
                float rd = Math.min(rect.getHeight() / 10.0f, 5.0f);
                annotation.setRectDifferences(rd);
                bbox = new PDRectangle(-rd, -rd, rect.getWidth() + 2.0f * rd, rect.getHeight() + 2.0f * rd);
                Matrix matrix = pdAppearanceStream.getMatrix();
                pdAppearanceStream.setMatrix(matrix.createAffineTransform());
                PDRectangle rect2 = new PDRectangle(rect.getLowerLeftX() - rd, rect.getLowerLeftY() - rd, rect.getWidth() + 2.0f * rd, rect.getHeight() + 2.0f * rd);
                annotation.setRectangle(rect2);
            }
            pdAppearanceStream.setBBox(bbox);
            float halfX = rect.getWidth() / 2.0f;
            float halfY = rect.getHeight() / 2.0f;
            contentStream.moveTo(0.0f, 0.0f);
            contentStream.curveTo(halfX, 0.0f, halfX, halfY, halfX, rect.getHeight());
            contentStream.curveTo(halfX, halfY, halfX, 0.0f, rect.getWidth(), 0.0f);
            contentStream.closePath();
            contentStream.fill();
        }
        catch (IOException e) {
            LOG.error((Object)e);
        }
        finally {
            IOUtils.closeQuietly(contentStream);
        }
    }

    @Override
    public void generateRolloverAppearance() {
    }

    @Override
    public void generateDownAppearance() {
    }
}

