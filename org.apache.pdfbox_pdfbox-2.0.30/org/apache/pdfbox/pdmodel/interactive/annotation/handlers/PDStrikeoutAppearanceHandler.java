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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.AnnotationBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;

public class PDStrikeoutAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDStrikeoutAppearanceHandler.class);

    public PDStrikeoutAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDStrikeoutAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationTextMarkup annotation = (PDAnnotationTextMarkup)this.getAnnotation();
        PDRectangle rect = annotation.getRectangle();
        if (rect == null) {
            return;
        }
        float[] pathsArray = annotation.getQuadPoints();
        if (pathsArray == null) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
        PDColor color = annotation.getColor();
        if (color == null || color.getComponents().length == 0) {
            return;
        }
        if (Float.compare(ab.width, 0.0f) == 0) {
            ab.width = 1.5f;
        }
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        for (int i = 0; i < pathsArray.length / 2; ++i) {
            float x = pathsArray[i * 2];
            float y = pathsArray[i * 2 + 1];
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        rect.setLowerLeftX(Math.min(minX - ab.width / 2.0f, rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - ab.width / 2.0f, rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + ab.width / 2.0f, rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + ab.width / 2.0f, rect.getUpperRightY()));
        annotation.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            cs = this.getNormalAppearanceAsContentStream();
            this.setOpacity(cs, annotation.getConstantOpacity());
            cs.setStrokingColor(color);
            if (ab.dashArray != null) {
                cs.setLineDashPattern(ab.dashArray, 0.0f);
            }
            cs.setLineWidth(ab.width);
            for (int i = 0; i < pathsArray.length / 8; ++i) {
                float len0 = (float)Math.sqrt(Math.pow(pathsArray[i * 8] - pathsArray[i * 8 + 4], 2.0) + Math.pow(pathsArray[i * 8 + 1] - pathsArray[i * 8 + 5], 2.0));
                float x0 = pathsArray[i * 8 + 4];
                float y0 = pathsArray[i * 8 + 5];
                if (Float.compare(len0, 0.0f) != 0) {
                    x0 += (pathsArray[i * 8] - pathsArray[i * 8 + 4]) / len0 * (len0 / 2.0f - ab.width);
                    y0 += (pathsArray[i * 8 + 1] - pathsArray[i * 8 + 5]) / len0 * (len0 / 2.0f - ab.width);
                }
                float len1 = (float)Math.sqrt(Math.pow(pathsArray[i * 8 + 2] - pathsArray[i * 8 + 6], 2.0) + Math.pow(pathsArray[i * 8 + 3] - pathsArray[i * 8 + 7], 2.0));
                float x1 = pathsArray[i * 8 + 6];
                float y1 = pathsArray[i * 8 + 7];
                if (Float.compare(len1, 0.0f) != 0) {
                    x1 += (pathsArray[i * 8 + 2] - pathsArray[i * 8 + 6]) / len1 * (len1 / 2.0f - ab.width);
                    y1 += (pathsArray[i * 8 + 3] - pathsArray[i * 8 + 7]) / len1 * (len1 / 2.0f - ab.width);
                }
                cs.moveTo(x0, y0);
                cs.lineTo(x1, y1);
            }
            cs.stroke();
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

