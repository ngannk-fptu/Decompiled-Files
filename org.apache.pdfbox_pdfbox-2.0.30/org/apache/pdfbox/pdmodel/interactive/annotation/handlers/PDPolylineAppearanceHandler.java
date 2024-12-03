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
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.AnnotationBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;
import org.apache.pdfbox.util.Matrix;

public class PDPolylineAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDPolylineAppearanceHandler.class);

    public PDPolylineAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDPolylineAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationMarkup annotation = (PDAnnotationMarkup)this.getAnnotation();
        PDRectangle rect = annotation.getRectangle();
        if (rect == null) {
            return;
        }
        float[] pathsArray = annotation.getVertices();
        if (pathsArray == null || pathsArray.length < 4) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
        PDColor color = annotation.getColor();
        if (color == null || color.getComponents().length == 0 || Float.compare(ab.width, 0.0f) == 0) {
            return;
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
        rect.setLowerLeftX(Math.min(minX - ab.width * 10.0f, rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - ab.width * 10.0f, rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + ab.width * 10.0f, rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + ab.width * 10.0f, rect.getUpperRightY()));
        annotation.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            cs = this.getNormalAppearanceAsContentStream();
            boolean hasBackground = cs.setNonStrokingColorOnDemand(annotation.getInteriorColor());
            this.setOpacity(cs, annotation.getConstantOpacity());
            boolean hasStroke = cs.setStrokingColorOnDemand(color);
            if (ab.dashArray != null) {
                cs.setLineDashPattern(ab.dashArray, 0.0f);
            }
            cs.setLineWidth(ab.width);
            for (int i = 0; i < pathsArray.length / 2; ++i) {
                float len;
                float x = pathsArray[i * 2];
                float y = pathsArray[i * 2 + 1];
                if (i == 0) {
                    if (SHORT_STYLES.contains(annotation.getStartPointEndingStyle())) {
                        float x1 = pathsArray[2];
                        float y1 = pathsArray[3];
                        len = (float)Math.sqrt(Math.pow(x - x1, 2.0) + Math.pow(y - y1, 2.0));
                        if (Float.compare(len, 0.0f) != 0) {
                            x += (x1 - x) / len * ab.width;
                            y += (y1 - y) / len * ab.width;
                        }
                    }
                    cs.moveTo(x, y);
                    continue;
                }
                if (i == pathsArray.length / 2 - 1 && SHORT_STYLES.contains(annotation.getEndPointEndingStyle())) {
                    float x0 = pathsArray[pathsArray.length - 4];
                    float y0 = pathsArray[pathsArray.length - 3];
                    len = (float)Math.sqrt(Math.pow(x0 - x, 2.0) + Math.pow(y0 - y, 2.0));
                    if (Float.compare(len, 0.0f) != 0) {
                        x -= (x - x0) / len * ab.width;
                        y -= (y - y0) / len * ab.width;
                    }
                }
                cs.lineTo(x, y);
            }
            cs.stroke();
            if (!"None".equals(annotation.getStartPointEndingStyle())) {
                float x2 = pathsArray[2];
                float y2 = pathsArray[3];
                float x1 = pathsArray[0];
                float y1 = pathsArray[1];
                cs.saveGraphicsState();
                if (ANGLED_STYLES.contains(annotation.getStartPointEndingStyle())) {
                    double angle = Math.atan2(y2 - y1, x2 - x1);
                    cs.transform(Matrix.getRotateInstance(angle, x1, y1));
                } else {
                    cs.transform(Matrix.getTranslateInstance(x1, y1));
                }
                this.drawStyle(annotation.getStartPointEndingStyle(), cs, 0.0f, 0.0f, ab.width, hasStroke, hasBackground, false);
                cs.restoreGraphicsState();
            }
            if (!"None".equals(annotation.getEndPointEndingStyle())) {
                float x1 = pathsArray[pathsArray.length - 4];
                float y1 = pathsArray[pathsArray.length - 3];
                float x2 = pathsArray[pathsArray.length - 2];
                float y2 = pathsArray[pathsArray.length - 1];
                if (ANGLED_STYLES.contains(annotation.getEndPointEndingStyle())) {
                    double angle = Math.atan2(y2 - y1, x2 - x1);
                    cs.transform(Matrix.getRotateInstance(angle, x2, y2));
                } else {
                    cs.transform(Matrix.getTranslateInstance(x2, y2));
                }
                this.drawStyle(annotation.getEndPointEndingStyle(), cs, 0.0f, 0.0f, ab.width, hasStroke, hasBackground, true);
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

    float getLineWidth() {
        COSBase base;
        PDAnnotationMarkup annotation = (PDAnnotationMarkup)this.getAnnotation();
        PDBorderStyleDictionary bs = annotation.getBorderStyle();
        if (bs != null) {
            return bs.getWidth();
        }
        COSArray borderCharacteristics = annotation.getBorder();
        if (borderCharacteristics.size() >= 3 && (base = borderCharacteristics.getObject(2)) instanceof COSNumber) {
            return ((COSNumber)base).floatValue();
        }
        return 1.0f;
    }
}

