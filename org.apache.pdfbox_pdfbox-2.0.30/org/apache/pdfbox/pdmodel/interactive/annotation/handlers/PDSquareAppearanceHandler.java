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
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationSquareCircle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.CloudyBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;

public class PDSquareAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDSquareAppearanceHandler.class);

    public PDSquareAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDSquareAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        float lineWidth = this.getLineWidth();
        PDAnnotationSquareCircle annotation = (PDAnnotationSquareCircle)this.getAnnotation();
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
                cloudyBorder.createCloudyRectangle(annotation.getRectDifference());
                annotation.setRectangle(cloudyBorder.getRectangle());
                annotation.setRectDifference(cloudyBorder.getRectDifference());
                PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                appearanceStream.setBBox(cloudyBorder.getBBox());
                appearanceStream.setMatrix(cloudyBorder.getMatrix());
            } else {
                PDRectangle borderBox = this.handleBorderBox(annotation, lineWidth);
                contentStream.addRect(borderBox.getLowerLeftX(), borderBox.getLowerLeftY(), borderBox.getWidth(), borderBox.getHeight());
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

