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
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.AnnotationBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;

public class PDInkAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDInkAppearanceHandler.class);

    public PDInkAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDInkAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationMarkup ink = (PDAnnotationMarkup)this.getAnnotation();
        PDColor color = ink.getColor();
        if (color == null || color.getComponents().length == 0) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(ink, ink.getBorderStyle());
        if (Float.compare(ab.width, 0.0f) == 0) {
            return;
        }
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        for (float[] pathArray : ink.getInkList()) {
            int nPoints = pathArray.length / 2;
            for (int i = 0; i < nPoints; ++i) {
                float x = pathArray[i * 2];
                float y = pathArray[i * 2 + 1];
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        PDRectangle rect = ink.getRectangle();
        if (rect == null) {
            return;
        }
        rect.setLowerLeftX(Math.min(minX - ab.width * 2.0f, rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - ab.width * 2.0f, rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + ab.width * 2.0f, rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + ab.width * 2.0f, rect.getUpperRightY()));
        ink.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            cs = this.getNormalAppearanceAsContentStream();
            this.setOpacity(cs, ink.getConstantOpacity());
            cs.setStrokingColor(color);
            if (ab.dashArray != null) {
                cs.setLineDashPattern(ab.dashArray, 0.0f);
            }
            cs.setLineWidth(ab.width);
            for (float[] pathArray : ink.getInkList()) {
                int nPoints = pathArray.length / 2;
                for (int i = 0; i < nPoints; ++i) {
                    float x = pathArray[i * 2];
                    float y = pathArray[i * 2 + 1];
                    if (i == 0) {
                        cs.moveTo(x, y);
                        continue;
                    }
                    cs.lineTo(x, y);
                }
                cs.stroke();
            }
        }
        catch (IOException ex) {
            LOG.error((Object)ex);
        }
        finally {
            IOUtils.closeQuietly(cs);
        }
    }

    @Override
    public void generateRolloverAppearance() {
    }

    @Override
    public void generateDownAppearance() {
    }
}

