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
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;

public class PDLinkAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDLinkAppearanceHandler.class);

    public PDLinkAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDLinkAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationLink annotation = (PDAnnotationLink)this.getAnnotation();
        PDRectangle rect = annotation.getRectangle();
        if (rect == null) {
            return;
        }
        float lineWidth = this.getLineWidth();
        PDAppearanceContentStream contentStream = null;
        try {
            PDBorderStyleDictionary borderStyleDic;
            contentStream = this.getNormalAppearanceAsContentStream();
            PDColor color = annotation.getColor();
            if (color == null) {
                color = new PDColor(new float[]{0.0f}, (PDColorSpace)PDDeviceGray.INSTANCE);
            }
            boolean hasStroke = contentStream.setStrokingColorOnDemand(color);
            contentStream.setBorderLine(lineWidth, annotation.getBorderStyle(), annotation.getBorder());
            PDRectangle borderEdge = this.getPaddedRectangle(this.getRectangle(), lineWidth / 2.0f);
            float[] pathsArray = annotation.getQuadPoints();
            if (pathsArray != null) {
                for (int i = 0; i < pathsArray.length / 2; ++i) {
                    if (rect.contains(pathsArray[i * 2], pathsArray[i * 2 + 1])) continue;
                    LOG.warn((Object)("At least one /QuadPoints entry (" + pathsArray[i * 2] + ";" + pathsArray[i * 2 + 1] + ") is outside of rectangle, " + rect + ", /QuadPoints are ignored and /Rect is used instead"));
                    pathsArray = null;
                    break;
                }
            }
            if (pathsArray == null) {
                pathsArray = new float[]{borderEdge.getLowerLeftX(), borderEdge.getLowerLeftY(), borderEdge.getUpperRightX(), borderEdge.getLowerLeftY(), borderEdge.getUpperRightX(), borderEdge.getUpperRightY(), borderEdge.getLowerLeftX(), borderEdge.getUpperRightY()};
            }
            boolean underline = false;
            if (pathsArray.length >= 8 && (borderStyleDic = annotation.getBorderStyle()) != null) {
                underline = "U".equals(borderStyleDic.getStyle());
            }
            int of = 0;
            while (of + 7 < pathsArray.length) {
                contentStream.moveTo(pathsArray[of], pathsArray[of + 1]);
                contentStream.lineTo(pathsArray[of + 2], pathsArray[of + 3]);
                if (!underline) {
                    contentStream.lineTo(pathsArray[of + 4], pathsArray[of + 5]);
                    contentStream.lineTo(pathsArray[of + 6], pathsArray[of + 7]);
                    contentStream.closePath();
                }
                of += 8;
            }
            contentStream.drawShape(lineWidth, hasStroke, false);
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
        PDAnnotationLink annotation = (PDAnnotationLink)this.getAnnotation();
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

