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
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDFormContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationTextMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.AnnotationBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;

public class PDHighlightAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDHighlightAppearanceHandler.class);

    public PDHighlightAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDHighlightAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationTextMarkup annotation = (PDAnnotationTextMarkup)this.getAnnotation();
        float[] pathsArray = annotation.getQuadPoints();
        if (pathsArray == null) {
            return;
        }
        PDColor color = annotation.getColor();
        if (color == null || color.getComponents().length == 0) {
            return;
        }
        PDRectangle rect = annotation.getRectangle();
        if (rect == null) {
            return;
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
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
        float maxDelta = 0.0f;
        for (int i = 0; i < pathsArray.length / 8; ++i) {
            float delta = Math.max((pathsArray[i + 0] - pathsArray[i + 4]) / 4.0f, (pathsArray[i + 1] - pathsArray[i + 5]) / 4.0f);
            maxDelta = Math.max(delta, maxDelta);
        }
        rect.setLowerLeftX(Math.min(minX - ab.width / 2.0f - maxDelta, rect.getLowerLeftX()));
        rect.setLowerLeftY(Math.min(minY - ab.width / 2.0f - maxDelta, rect.getLowerLeftY()));
        rect.setUpperRightX(Math.max(maxX + ab.width + maxDelta, rect.getUpperRightX()));
        rect.setUpperRightY(Math.max(maxY + ab.width + maxDelta, rect.getUpperRightY()));
        annotation.setRectangle(rect);
        PDAppearanceContentStream cs = null;
        try {
            cs = this.getNormalAppearanceAsContentStream();
            PDExtendedGraphicsState r0 = new PDExtendedGraphicsState();
            PDExtendedGraphicsState r1 = new PDExtendedGraphicsState();
            r0.setAlphaSourceFlag(false);
            r0.setStrokingAlphaConstant(Float.valueOf(annotation.getConstantOpacity()));
            r0.setNonStrokingAlphaConstant(Float.valueOf(annotation.getConstantOpacity()));
            r1.setAlphaSourceFlag(false);
            r1.setBlendMode(BlendMode.MULTIPLY);
            cs.setGraphicsStateParameters(r0);
            cs.setGraphicsStateParameters(r1);
            PDFormXObject frm1 = new PDFormXObject(this.createCOSStream());
            PDFormXObject frm2 = new PDFormXObject(this.createCOSStream());
            frm1.setResources(new PDResources());
            PDFormContentStream mwfofrmCS = null;
            try {
                mwfofrmCS = new PDFormContentStream(frm1);
                mwfofrmCS.drawForm(frm2);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(mwfofrmCS);
                throw throwable;
            }
            IOUtils.closeQuietly(mwfofrmCS);
            frm1.setBBox(annotation.getRectangle());
            COSDictionary groupDict = new COSDictionary();
            groupDict.setItem(COSName.S, (COSBase)COSName.TRANSPARENCY);
            frm1.getCOSObject().setItem(COSName.GROUP, (COSBase)groupDict);
            cs.drawForm(frm1);
            frm2.setBBox(annotation.getRectangle());
            PDFormContentStream frm2CS = null;
            try {
                frm2CS = new PDFormContentStream(frm2);
                frm2CS.setNonStrokingColor(color);
                int of = 0;
                while (of + 7 < pathsArray.length) {
                    float delta = 0.0f;
                    if (Float.compare(pathsArray[of + 0], pathsArray[of + 4]) == 0 && Float.compare(pathsArray[of + 1], pathsArray[of + 3]) == 0 && Float.compare(pathsArray[of + 2], pathsArray[of + 6]) == 0 && Float.compare(pathsArray[of + 5], pathsArray[of + 7]) == 0) {
                        delta = (pathsArray[of + 1] - pathsArray[of + 5]) / 4.0f;
                    } else if (Float.compare(pathsArray[of + 1], pathsArray[of + 5]) == 0 && Float.compare(pathsArray[of + 0], pathsArray[of + 2]) == 0 && Float.compare(pathsArray[of + 3], pathsArray[of + 7]) == 0 && Float.compare(pathsArray[of + 4], pathsArray[of + 6]) == 0) {
                        delta = (pathsArray[of + 0] - pathsArray[of + 4]) / 4.0f;
                    }
                    frm2CS.moveTo(pathsArray[of + 4], pathsArray[of + 5]);
                    if (Float.compare(pathsArray[of + 0], pathsArray[of + 4]) == 0) {
                        frm2CS.curveTo(pathsArray[of + 4] - delta, pathsArray[of + 5] + delta, pathsArray[of + 0] - delta, pathsArray[of + 1] - delta, pathsArray[of + 0], pathsArray[of + 1]);
                    } else if (Float.compare(pathsArray[of + 5], pathsArray[of + 1]) == 0) {
                        frm2CS.curveTo(pathsArray[of + 4] + delta, pathsArray[of + 5] + delta, pathsArray[of + 0] - delta, pathsArray[of + 1] + delta, pathsArray[of + 0], pathsArray[of + 1]);
                    } else {
                        frm2CS.lineTo(pathsArray[of + 0], pathsArray[of + 1]);
                    }
                    frm2CS.lineTo(pathsArray[of + 2], pathsArray[of + 3]);
                    if (Float.compare(pathsArray[of + 2], pathsArray[of + 6]) == 0) {
                        frm2CS.curveTo(pathsArray[of + 2] + delta, pathsArray[of + 3] - delta, pathsArray[of + 6] + delta, pathsArray[of + 7] + delta, pathsArray[of + 6], pathsArray[of + 7]);
                    } else if (Float.compare(pathsArray[of + 3], pathsArray[of + 7]) == 0) {
                        frm2CS.curveTo(pathsArray[of + 2] - delta, pathsArray[of + 3] - delta, pathsArray[of + 6] + delta, pathsArray[of + 7] - delta, pathsArray[of + 6], pathsArray[of + 7]);
                    } else {
                        frm2CS.lineTo(pathsArray[of + 6], pathsArray[of + 7]);
                    }
                    frm2CS.fill();
                    of += 8;
                }
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(frm2CS);
                throw throwable;
            }
            IOUtils.closeQuietly(frm2CS);
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

