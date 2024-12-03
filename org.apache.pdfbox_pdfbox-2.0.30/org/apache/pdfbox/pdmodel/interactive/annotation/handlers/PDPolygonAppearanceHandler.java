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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.CloudyBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;

public class PDPolygonAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDPolygonAppearanceHandler.class);

    public PDPolygonAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDPolygonAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationMarkup annotation = (PDAnnotationMarkup)this.getAnnotation();
        float lineWidth = this.getLineWidth();
        PDRectangle rect = annotation.getRectangle();
        if (rect == null) {
            return;
        }
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;
        float[][] pathArray = this.getPathArray(annotation);
        if (pathArray == null) {
            return;
        }
        for (int i = 0; i < pathArray.length; ++i) {
            for (int j = 0; j < pathArray[i].length / 2; ++j) {
                float x = pathArray[i][j * 2];
                float y = pathArray[i][j * 2 + 1];
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        rect.setLowerLeftX(Math.min(minX - lineWidth, rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - lineWidth, rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + lineWidth, rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + lineWidth, rect.getUpperRightY()));
        annotation.setRectangle(rect);
        PDAppearanceContentStream contentStream = null;
        try {
            contentStream = this.getNormalAppearanceAsContentStream();
            boolean hasStroke = contentStream.setStrokingColorOnDemand(this.getColor());
            boolean hasBackground = contentStream.setNonStrokingColorOnDemand(annotation.getInteriorColor());
            this.setOpacity(contentStream, annotation.getConstantOpacity());
            contentStream.setBorderLine(lineWidth, annotation.getBorderStyle(), annotation.getBorder());
            PDBorderEffectDictionary borderEffect = annotation.getBorderEffect();
            if (borderEffect != null && borderEffect.getStyle().equals("C")) {
                CloudyBorder cloudyBorder = new CloudyBorder(contentStream, borderEffect.getIntensity(), lineWidth, this.getRectangle());
                cloudyBorder.createCloudyPolygon(pathArray);
                annotation.setRectangle(cloudyBorder.getRectangle());
                PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                appearanceStream.setBBox(cloudyBorder.getBBox());
                appearanceStream.setMatrix(cloudyBorder.getMatrix());
            } else {
                for (int i = 0; i < pathArray.length; ++i) {
                    float[] pointsArray = pathArray[i];
                    if (i == 0 && pointsArray.length == 2) {
                        contentStream.moveTo(pointsArray[0], pointsArray[1]);
                        continue;
                    }
                    if (pointsArray.length == 2) {
                        contentStream.lineTo(pointsArray[0], pointsArray[1]);
                        continue;
                    }
                    if (pointsArray.length != 6) continue;
                    contentStream.curveTo(pointsArray[0], pointsArray[1], pointsArray[2], pointsArray[3], pointsArray[4], pointsArray[5]);
                }
                contentStream.closePath();
            }
            contentStream.drawShape(lineWidth, hasStroke, hasBackground);
        }
        catch (IOException e) {
            LOG.error((Object)e);
        }
        finally {
            IOUtils.closeQuietly(contentStream);
        }
    }

    private float[][] getPathArray(PDAnnotationMarkup annotation) {
        float[][] pathArray = annotation.getPath();
        if (pathArray == null) {
            float[] verticesArray = annotation.getVertices();
            if (verticesArray == null) {
                return null;
            }
            int points = verticesArray.length / 2;
            pathArray = new float[points][2];
            for (int i = 0; i < points; ++i) {
                pathArray[i][0] = verticesArray[i * 2];
                pathArray[i][1] = verticesArray[i * 2 + 1];
            }
        }
        return pathArray;
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

