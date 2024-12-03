/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDFormContentStream;
import org.apache.pdfbox.pdmodel.PDPatternContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.AnnotationBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;
import org.apache.pdfbox.util.Matrix;

public class PDSquigglyAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDSquigglyAppearanceHandler.class);

    public PDSquigglyAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDSquigglyAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
            for (int i = 0; i < pathsArray.length / 8; ++i) {
                float height = pathsArray[i * 8 + 1] - pathsArray[i * 8 + 5];
                cs.transform(new Matrix(height / 40.0f, 0.0f, 0.0f, height / 40.0f / 1.8f, pathsArray[i * 8 + 4], pathsArray[i * 8 + 5]));
                PDFormXObject form = new PDFormXObject(this.createCOSStream());
                form.setBBox(new PDRectangle(-0.5f, -0.5f, (pathsArray[i * 8 + 2] - pathsArray[i * 8]) / height * 40.0f + 0.5f, 13.0f));
                form.setResources(new PDResources());
                form.setMatrix(AffineTransform.getTranslateInstance(0.5, 0.5));
                cs.drawForm(form);
                PDFormContentStream formCS = null;
                try {
                    formCS = new PDFormContentStream(form);
                    PDTilingPattern pattern = new PDTilingPattern();
                    pattern.setBBox(new PDRectangle(0.0f, 0.0f, 10.0f, 12.0f));
                    pattern.setXStep(10.0f);
                    pattern.setYStep(13.0f);
                    pattern.setTilingType(3);
                    pattern.setPaintType(2);
                    PDPatternContentStream patternCS = null;
                    try {
                        patternCS = new PDPatternContentStream(pattern);
                        patternCS.setLineCapStyle(1);
                        patternCS.setLineJoinStyle(1);
                        patternCS.setLineWidth(1.0f);
                        patternCS.setMiterLimit(10.0f);
                        patternCS.moveTo(0.0f, 1.0f);
                        patternCS.lineTo(5.0f, 11.0f);
                        patternCS.lineTo(10.0f, 1.0f);
                        patternCS.stroke();
                    }
                    catch (Throwable throwable) {
                        IOUtils.closeQuietly(patternCS);
                        throw throwable;
                    }
                    IOUtils.closeQuietly(patternCS);
                    COSName patternName = form.getResources().add(pattern);
                    PDPattern patternColorSpace = new PDPattern(null, PDDeviceRGB.INSTANCE);
                    PDColor patternColor = new PDColor(color.getComponents(), patternName, patternColorSpace);
                    formCS.setNonStrokingColor(patternColor);
                    formCS.addRect(0.0f, 0.0f, (pathsArray[i * 8 + 2] - pathsArray[i * 8]) / height * 40.0f, 12.0f);
                    formCS.fill();
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(formCS);
                    throw throwable;
                }
                IOUtils.closeQuietly(formCS);
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

