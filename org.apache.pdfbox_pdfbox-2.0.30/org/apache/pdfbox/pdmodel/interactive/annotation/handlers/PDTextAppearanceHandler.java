/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;
import org.apache.pdfbox.util.Matrix;

public class PDTextAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDTextAppearanceHandler.class);
    private static final Set<String> SUPPORTED_NAMES = new HashSet<String>();

    public PDTextAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDTextAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        PDAnnotationText annotation = (PDAnnotationText)this.getAnnotation();
        if (!SUPPORTED_NAMES.contains(annotation.getName())) {
            return;
        }
        PDAppearanceContentStream contentStream = null;
        try {
            contentStream = this.getNormalAppearanceAsContentStream();
            PDColor bgColor = this.getColor();
            if (bgColor == null) {
                contentStream.setNonStrokingColor(1.0f);
            } else {
                contentStream.setNonStrokingColor(bgColor);
            }
            this.setOpacity(contentStream, annotation.getConstantOpacity());
            String annotationTypeName = annotation.getName();
            if ("Note".equals(annotationTypeName)) {
                this.drawNote(annotation, contentStream);
            } else if ("Cross".equals(annotationTypeName)) {
                this.drawCross(annotation, contentStream);
            } else if ("Circle".equals(annotationTypeName)) {
                this.drawCircles(annotation, contentStream);
            } else if ("Insert".equals(annotationTypeName)) {
                this.drawInsert(annotation, contentStream);
            } else if ("Help".equals(annotationTypeName)) {
                this.drawHelp(annotation, contentStream);
            } else if ("Paragraph".equals(annotationTypeName)) {
                this.drawParagraph(annotation, contentStream);
            } else if ("NewParagraph".equals(annotationTypeName)) {
                this.drawNewParagraph(annotation, contentStream);
            } else if ("Star".equals(annotationTypeName)) {
                this.drawStar(annotation, contentStream);
            } else if ("Check".equals(annotationTypeName)) {
                this.drawCheck(annotation, contentStream);
            } else if ("RightArrow".equals(annotationTypeName)) {
                this.drawRightArrow(annotation, contentStream);
            } else if ("RightPointer".equals(annotationTypeName)) {
                this.drawRightPointer(annotation, contentStream);
            } else if ("CrossHairs".equals(annotationTypeName)) {
                this.drawCrossHairs(annotation, contentStream);
            } else if ("UpArrow".equals(annotationTypeName)) {
                this.drawUpArrow(annotation, contentStream);
            } else if ("UpLeftArrow".equals(annotationTypeName)) {
                this.drawUpLeftArrow(annotation, contentStream);
            } else if ("Comment".equals(annotationTypeName)) {
                this.drawComment(annotation, contentStream);
            } else if ("Key".equals(annotationTypeName)) {
                this.drawKey(annotation, contentStream);
            }
        }
        catch (IOException e) {
            LOG.error((Object)e);
        }
        finally {
            IOUtils.closeQuietly(contentStream);
        }
    }

    private PDRectangle adjustRectAndBBox(PDAnnotationText annotation, float width, float height) {
        PDRectangle rect = this.getRectangle();
        if (!annotation.isNoZoom()) {
            rect.setUpperRightX(rect.getLowerLeftX() + width);
            rect.setLowerLeftY(rect.getUpperRightY() - height);
            annotation.setRectangle(rect);
        }
        if (!annotation.getCOSObject().containsKey(COSName.F)) {
            annotation.setNoRotate(true);
            annotation.setNoZoom(true);
        }
        PDRectangle bbox = new PDRectangle(width, height);
        annotation.getNormalAppearanceStream().setBBox(bbox);
        return bbox;
    }

    private void drawNote(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 18.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.61f);
        float width = bbox.getWidth();
        float height = bbox.getHeight();
        contentStream.addRect(1.0f, 1.0f, width - 2.0f, height - 2.0f);
        contentStream.moveTo(width / 4.0f, height / 7.0f * 2.0f);
        contentStream.lineTo(width * 3.0f / 4.0f - 1.0f, height / 7.0f * 2.0f);
        contentStream.moveTo(width / 4.0f, height / 7.0f * 3.0f);
        contentStream.lineTo(width * 3.0f / 4.0f - 1.0f, height / 7.0f * 3.0f);
        contentStream.moveTo(width / 4.0f, height / 7.0f * 4.0f);
        contentStream.lineTo(width * 3.0f / 4.0f - 1.0f, height / 7.0f * 4.0f);
        contentStream.moveTo(width / 4.0f, height / 7.0f * 5.0f);
        contentStream.lineTo(width * 3.0f / 4.0f - 1.0f, height / 7.0f * 5.0f);
        contentStream.fillAndStroke();
    }

    private void drawCircles(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float smallR = 6.36f;
        float largeR = 9.756f;
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        float width = bbox.getWidth() / 2.0f;
        float height = bbox.getHeight() / 2.0f;
        this.drawCircle(contentStream, width, height, smallR);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.setLineWidth(0.59f);
        this.drawCircle(contentStream, width, height, smallR);
        this.drawCircle2(contentStream, width, height, largeR);
        contentStream.fillAndStroke();
    }

    private void drawInsert(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 17.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(0);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo(bbox.getWidth() / 2.0f - 1.0f, bbox.getHeight() - 2.0f);
        contentStream.lineTo(1.0f, 1.0f);
        contentStream.lineTo(bbox.getWidth() - 2.0f, 1.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawCross(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 19.0f, 19.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        float small = min / 10.0f;
        float large = min / 5.0f;
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo(small, large);
        contentStream.lineTo(large, small);
        contentStream.lineTo(min / 2.0f, min / 2.0f - small);
        contentStream.lineTo(min - large, small);
        contentStream.lineTo(min - small, large);
        contentStream.lineTo(min / 2.0f + small, min / 2.0f);
        contentStream.lineTo(min - small, min - large);
        contentStream.lineTo(min - large, min - small);
        contentStream.lineTo(min / 2.0f, min / 2.0f + small);
        contentStream.lineTo(large, min - small);
        contentStream.lineTo(small, min - large);
        contentStream.lineTo(min / 2.0f - small, min / 2.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawHelp(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        this.drawCircle2(contentStream, min / 2.0f, min / 2.0f, min / 2.0f - 1.0f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.saveGraphicsState();
        contentStream.transform(Matrix.getScaleInstance(0.001f * min / 2.25f, 0.001f * min / 2.25f));
        contentStream.transform(Matrix.getTranslateInstance(500.0f, 375.0f));
        GeneralPath path = PDType1Font.HELVETICA_BOLD.getPath("question");
        this.addPath(contentStream, path);
        contentStream.restoreGraphicsState();
        this.drawCircle2(contentStream, min / 2.0f, min / 2.0f, min / 2.0f - 1.0f);
        contentStream.fillAndStroke();
    }

    private void drawParagraph(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        this.drawCircle2(contentStream, min / 2.0f, min / 2.0f, min / 2.0f - 1.0f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.saveGraphicsState();
        contentStream.transform(Matrix.getScaleInstance(0.001f * min / 3.0f, 0.001f * min / 3.0f));
        contentStream.transform(Matrix.getTranslateInstance(850.0f, 900.0f));
        GeneralPath path = PDType1Font.HELVETICA.getPath("paragraph");
        this.addPath(contentStream, path);
        contentStream.restoreGraphicsState();
        contentStream.fillAndStroke();
        this.drawCircle(contentStream, min / 2.0f, min / 2.0f, min / 2.0f - 1.0f);
        contentStream.stroke();
    }

    private void drawNewParagraph(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        this.adjustRectAndBBox(annotation, 13.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(0);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo(6.4995f, 20.0f);
        contentStream.lineTo(0.295f, 7.287f);
        contentStream.lineTo(12.705f, 7.287f);
        contentStream.closeAndFillAndStroke();
        contentStream.transform(Matrix.getScaleInstance(0.004f, 0.004f));
        contentStream.transform(Matrix.getTranslateInstance(200.0f, 0.0f));
        this.addPath(contentStream, PDType1Font.HELVETICA_BOLD.getPath("N"));
        contentStream.transform(Matrix.getTranslateInstance(1300.0f, 0.0f));
        this.addPath(contentStream, PDType1Font.HELVETICA_BOLD.getPath("P"));
        contentStream.fill();
    }

    private void drawStar(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 19.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getScaleInstance(0.001f * min / 0.8f, 0.001f * min / 0.8f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a35");
        this.addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawCheck(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 19.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getScaleInstance(0.001f * min / 0.8f, 0.001f * min / 0.8f));
        contentStream.transform(Matrix.getTranslateInstance(0.0f, 50.0f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a20");
        this.addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawRightPointer(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 17.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getScaleInstance(0.001f * min / 0.8f, 0.001f * min / 0.8f));
        contentStream.transform(Matrix.getTranslateInstance(0.0f, 50.0f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a174");
        this.addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawCrossHairs(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(0);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.61f);
        contentStream.transform(Matrix.getScaleInstance(0.001f * min / 1.5f, 0.001f * min / 1.5f));
        contentStream.transform(Matrix.getTranslateInstance(0.0f, 50.0f));
        GeneralPath path = PDType1Font.SYMBOL.getPath("circleplus");
        this.addPath(contentStream, path);
        contentStream.fillAndStroke();
    }

    private void drawUpArrow(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        this.adjustRectAndBBox(annotation, 17.0f, 20.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.moveTo(1.0f, 7.0f);
        contentStream.lineTo(5.0f, 7.0f);
        contentStream.lineTo(5.0f, 1.0f);
        contentStream.lineTo(12.0f, 1.0f);
        contentStream.lineTo(12.0f, 7.0f);
        contentStream.lineTo(16.0f, 7.0f);
        contentStream.lineTo(8.5f, 19.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawUpLeftArrow(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        this.adjustRectAndBBox(annotation, 17.0f, 17.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.transform(Matrix.getRotateInstance(Math.toRadians(45.0), 8.0f, -4.0f));
        contentStream.moveTo(1.0f, 7.0f);
        contentStream.lineTo(5.0f, 7.0f);
        contentStream.lineTo(5.0f, 1.0f);
        contentStream.lineTo(12.0f, 1.0f);
        contentStream.lineTo(12.0f, 7.0f);
        contentStream.lineTo(16.0f, 7.0f);
        contentStream.lineTo(8.5f, 19.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawRightArrow(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        PDRectangle bbox = this.adjustRectAndBBox(annotation, 20.0f, 20.0f);
        float min = Math.min(bbox.getWidth(), bbox.getHeight());
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(0.59f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        this.drawCircle2(contentStream, min / 2.0f, min / 2.0f, min / 2.0f - 1.0f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.saveGraphicsState();
        contentStream.transform(Matrix.getScaleInstance(0.001f * min / 1.3f, 0.001f * min / 1.3f));
        contentStream.transform(Matrix.getTranslateInstance(200.0f, 300.0f));
        GeneralPath path = PDType1Font.ZAPF_DINGBATS.getPath("a160");
        this.addPath(contentStream, path);
        contentStream.restoreGraphicsState();
        this.drawCircle(contentStream, min / 2.0f, min / 2.0f, min / 2.0f - 1.0f);
        contentStream.fillAndStroke();
    }

    private void drawComment(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        this.adjustRectAndBBox(annotation, 18.0f, 18.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(200.0f);
        contentStream.saveGraphicsState();
        contentStream.setLineWidth(1.0f);
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setAlphaSourceFlag(false);
        gs.setStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setNonStrokingAlphaConstant(Float.valueOf(0.6f));
        gs.setBlendMode(BlendMode.NORMAL);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(1.0f);
        contentStream.addRect(0.3f, 0.3f, 17.4f, 17.4f);
        contentStream.fill();
        contentStream.restoreGraphicsState();
        contentStream.transform(Matrix.getScaleInstance(0.003f, 0.003f));
        contentStream.transform(Matrix.getTranslateInstance(500.0f, -300.0f));
        contentStream.moveTo(2549.0f, 5269.0f);
        contentStream.curveTo(1307.0f, 5269.0f, 300.0f, 4451.0f, 300.0f, 3441.0f);
        contentStream.curveTo(300.0f, 3023.0f, 474.0f, 2640.0f, 764.0f, 2331.0f);
        contentStream.curveTo(633.0f, 1985.0f, 361.0f, 1691.0f, 357.0f, 1688.0f);
        contentStream.curveTo(299.0f, 1626.0f, 283.0f, 1537.0f, 316.0f, 1459.0f);
        contentStream.curveTo(350.0f, 1382.0f, 426.0f, 1332.0f, 510.0f, 1332.0f);
        contentStream.curveTo(1051.0f, 1332.0f, 1477.0f, 1558.0f, 1733.0f, 1739.0f);
        contentStream.curveTo(1987.0f, 1659.0f, 2261.0f, 1613.0f, 2549.0f, 1613.0f);
        contentStream.curveTo(3792.0f, 1613.0f, 4799.0f, 2431.0f, 4799.0f, 3441.0f);
        contentStream.curveTo(4799.0f, 4451.0f, 3792.0f, 5269.0f, 2549.0f, 5269.0f);
        contentStream.closePath();
        contentStream.moveTo(-400.0f, 400.0f);
        contentStream.lineTo(-400.0f, 6200.0f);
        contentStream.lineTo(5400.0f, 6200.0f);
        contentStream.lineTo(5400.0f, 400.0f);
        contentStream.closeAndFillAndStroke();
    }

    private void drawKey(PDAnnotationText annotation, PDAppearanceContentStream contentStream) throws IOException {
        this.adjustRectAndBBox(annotation, 13.0f, 18.0f);
        contentStream.setMiterLimit(4.0f);
        contentStream.setLineJoinStyle(1);
        contentStream.setLineCapStyle(0);
        contentStream.setLineWidth(200.0f);
        contentStream.transform(Matrix.getScaleInstance(0.003f, 0.003f));
        contentStream.transform(Matrix.getRotateInstance(Math.toRadians(45.0), 2500.0f, -800.0f));
        contentStream.moveTo(4799.0f, 4004.0f);
        contentStream.curveTo(4799.0f, 3149.0f, 4107.0f, 2457.0f, 3253.0f, 2457.0f);
        contentStream.curveTo(3154.0f, 2457.0f, 3058.0f, 2466.0f, 2964.0f, 2484.0f);
        contentStream.lineTo(2753.0f, 2246.0f);
        contentStream.curveTo(2713.0f, 2201.0f, 2656.0f, 2175.0f, 2595.0f, 2175.0f);
        contentStream.lineTo(2268.0f, 2175.0f);
        contentStream.lineTo(2268.0f, 1824.0f);
        contentStream.curveTo(2268.0f, 1707.0f, 2174.0f, 1613.0f, 2057.0f, 1613.0f);
        contentStream.lineTo(1706.0f, 1613.0f);
        contentStream.lineTo(1706.0f, 1261.0f);
        contentStream.curveTo(1706.0f, 1145.0f, 1611.0f, 1050.0f, 1495.0f, 1050.0f);
        contentStream.lineTo(510.0f, 1050.0f);
        contentStream.curveTo(394.0f, 1050.0f, 300.0f, 1145.0f, 300.0f, 1261.0f);
        contentStream.lineTo(300.0f, 1947.0f);
        contentStream.curveTo(300.0f, 2003.0f, 322.0f, 2057.0f, 361.0f, 2097.0f);
        contentStream.lineTo(1783.0f, 3519.0f);
        contentStream.curveTo(1733.0f, 3671.0f, 1706.0f, 3834.0f, 1706.0f, 4004.0f);
        contentStream.curveTo(1706.0f, 4858.0f, 2398.0f, 5550.0f, 3253.0f, 5550.0f);
        contentStream.curveTo(4109.0f, 5550.0f, 4799.0f, 4860.0f, 4799.0f, 4004.0f);
        contentStream.closePath();
        contentStream.moveTo(3253.0f, 4425.0f);
        contentStream.curveTo(3253.0f, 4192.0f, 3441.0f, 4004.0f, 3674.0f, 4004.0f);
        contentStream.curveTo(3907.0f, 4004.0f, 4096.0f, 4192.0f, 4096.0f, 4425.0f);
        contentStream.curveTo(4096.0f, 4658.0f, 3907.0f, 4847.0f, 3674.0f, 4847.0f);
        contentStream.curveTo(3441.0f, 4847.0f, 3253.0f, 4658.0f, 3253.0f, 4425.0f);
        contentStream.fillAndStroke();
    }

    private void addPath(PDAppearanceContentStream contentStream, GeneralPath path) throws IOException {
        double curX = 0.0;
        double curY = 0.0;
        PathIterator it = path.getPathIterator(new AffineTransform());
        double[] coords = new double[6];
        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            switch (type) {
                case 4: {
                    contentStream.closePath();
                    break;
                }
                case 3: {
                    contentStream.curveTo((float)coords[0], (float)coords[1], (float)coords[2], (float)coords[3], (float)coords[4], (float)coords[5]);
                    curX = coords[4];
                    curY = coords[5];
                    break;
                }
                case 2: {
                    double cp1x = curX + 0.6666666666666666 * (coords[0] - curX);
                    double cp1y = curY + 0.6666666666666666 * (coords[1] - curY);
                    double cp2x = coords[2] + 0.6666666666666666 * (coords[0] - coords[2]);
                    double cp2y = coords[3] + 0.6666666666666666 * (coords[1] - coords[3]);
                    contentStream.curveTo((float)cp1x, (float)cp1y, (float)cp2x, (float)cp2y, (float)coords[2], (float)coords[3]);
                    curX = coords[2];
                    curY = coords[3];
                    break;
                }
                case 1: {
                    contentStream.lineTo((float)coords[0], (float)coords[1]);
                    curX = coords[0];
                    curY = coords[1];
                    break;
                }
                case 0: {
                    contentStream.moveTo((float)coords[0], (float)coords[1]);
                    curX = coords[0];
                    curY = coords[1];
                    break;
                }
            }
            it.next();
        }
    }

    @Override
    public void generateRolloverAppearance() {
    }

    @Override
    public void generateDownAppearance() {
    }

    static {
        SUPPORTED_NAMES.add("Note");
        SUPPORTED_NAMES.add("Insert");
        SUPPORTED_NAMES.add("Cross");
        SUPPORTED_NAMES.add("Help");
        SUPPORTED_NAMES.add("Circle");
        SUPPORTED_NAMES.add("Paragraph");
        SUPPORTED_NAMES.add("NewParagraph");
        SUPPORTED_NAMES.add("Check");
        SUPPORTED_NAMES.add("Star");
        SUPPORTED_NAMES.add("RightArrow");
        SUPPORTED_NAMES.add("RightPointer");
        SUPPORTED_NAMES.add("CrossHairs");
        SUPPORTED_NAMES.add("UpArrow");
        SUPPORTED_NAMES.add("UpLeftArrow");
        SUPPORTED_NAMES.add("Comment");
        SUPPORTED_NAMES.add("Key");
    }
}

