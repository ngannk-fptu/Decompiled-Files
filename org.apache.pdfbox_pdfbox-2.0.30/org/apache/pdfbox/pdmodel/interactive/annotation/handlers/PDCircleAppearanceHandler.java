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

public class PDCircleAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDCircleAppearanceHandler.class);

    public PDCircleAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDCircleAppearanceHandler(PDAnnotation annotation, PDDocument document) {
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
                cloudyBorder.createCloudyEllipse(annotation.getRectDifference());
                annotation.setRectangle(cloudyBorder.getRectangle());
                annotation.setRectDifference(cloudyBorder.getRectDifference());
                PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                appearanceStream.setBBox(cloudyBorder.getBBox());
                appearanceStream.setMatrix(cloudyBorder.getMatrix());
            } else {
                PDRectangle borderBox = this.handleBorderBox(annotation, lineWidth);
                float x0 = borderBox.getLowerLeftX();
                float y0 = borderBox.getLowerLeftY();
                float x1 = borderBox.getUpperRightX();
                float y1 = borderBox.getUpperRightY();
                float xm = x0 + borderBox.getWidth() / 2.0f;
                float ym = y0 + borderBox.getHeight() / 2.0f;
                float magic = 0.55555415f;
                float vOffset = borderBox.getHeight() / 2.0f * magic;
                float hOffset = borderBox.getWidth() / 2.0f * magic;
                contentStream.moveTo(xm, y1);
                contentStream.curveTo(xm + hOffset, y1, x1, ym + vOffset, x1, ym);
                contentStream.curveTo(x1, ym - vOffset, xm + hOffset, y0, xm, y0);
                contentStream.curveTo(xm - hOffset, y0, x0, ym - vOffset, x0, ym);
                contentStream.curveTo(x0, ym + vOffset, xm - hOffset, y1, xm, y1);
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

