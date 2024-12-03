/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.util.Charsets
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.util.Charsets;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDAppearanceContentStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationMarkup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderEffectDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.AnnotationBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.CloudyBorder;
import org.apache.pdfbox.pdmodel.interactive.annotation.handlers.PDAbstractAppearanceHandler;
import org.apache.pdfbox.pdmodel.interactive.annotation.layout.AppearanceStyle;
import org.apache.pdfbox.pdmodel.interactive.annotation.layout.PlainText;
import org.apache.pdfbox.pdmodel.interactive.annotation.layout.PlainTextFormatter;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.util.Matrix;

public class PDFreeTextAppearanceHandler
extends PDAbstractAppearanceHandler {
    private static final Log LOG = LogFactory.getLog(PDFreeTextAppearanceHandler.class);
    private static final Pattern COLOR_PATTERN = Pattern.compile(".*color\\:\\s*\\#([0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]).*");
    private float fontSize = 10.0f;
    private COSName fontName = COSName.HELV;

    public PDFreeTextAppearanceHandler(PDAnnotation annotation) {
        super(annotation);
    }

    public PDFreeTextAppearanceHandler(PDAnnotation annotation, PDDocument document) {
        super(annotation, document);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateNormalAppearance() {
        float[] pathsArray;
        PDAnnotationMarkup annotation = (PDAnnotationMarkup)this.getAnnotation();
        if ("FreeTextCallout".equals(annotation.getIntent())) {
            pathsArray = annotation.getCallout();
            if (pathsArray == null || pathsArray.length != 4 && pathsArray.length != 6) {
                pathsArray = new float[]{};
            }
        } else {
            pathsArray = new float[]{};
        }
        AnnotationBorder ab = AnnotationBorder.getAnnotationBorder(annotation, annotation.getBorderStyle());
        PDAppearanceContentStream cs = null;
        try {
            float clipY;
            float yOffset;
            float xOffset;
            PDFont defaultResourcesFont;
            PDResources defaultResources;
            PDAcroForm acroForm;
            PDRectangle borderBox;
            PDBorderEffectDictionary borderEffect;
            Matcher m;
            cs = this.getNormalAppearanceAsContentStream(true);
            boolean hasBackground = cs.setNonStrokingColorOnDemand(annotation.getColor());
            this.setOpacity(cs, annotation.getConstantOpacity());
            PDColor strokingColor = this.extractNonStrokingColor(annotation);
            boolean hasStroke = cs.setStrokingColorOnDemand(strokingColor);
            PDColor textColor = strokingColor;
            String defaultStyleString = annotation.getDefaultStyleString();
            if (defaultStyleString != null && (m = COLOR_PATTERN.matcher(defaultStyleString)).find()) {
                int color = Integer.parseInt(m.group(1), 16);
                float r = (float)(color >> 16 & 0xFF) / 255.0f;
                float g = (float)(color >> 8 & 0xFF) / 255.0f;
                float b = (float)(color & 0xFF) / 255.0f;
                textColor = new PDColor(new float[]{r, g, b}, (PDColorSpace)PDDeviceRGB.INSTANCE);
            }
            if (ab.dashArray != null) {
                cs.setLineDashPattern(ab.dashArray, 0.0f);
            }
            cs.setLineWidth(ab.width);
            for (int i = 0; i < pathsArray.length / 2; ++i) {
                float x = pathsArray[i * 2];
                float y = pathsArray[i * 2 + 1];
                if (i == 0) {
                    if (SHORT_STYLES.contains(annotation.getLineEndingStyle())) {
                        float x1 = pathsArray[2];
                        float y1 = pathsArray[3];
                        float len = (float)Math.sqrt(Math.pow(x - x1, 2.0) + Math.pow(y - y1, 2.0));
                        if (Float.compare(len, 0.0f) != 0) {
                            x += (x1 - x) / len * ab.width;
                            y += (y1 - y) / len * ab.width;
                        }
                    }
                    cs.moveTo(x, y);
                    continue;
                }
                cs.lineTo(x, y);
            }
            if (pathsArray.length > 0) {
                cs.stroke();
            }
            if ("FreeTextCallout".equals(annotation.getIntent()) && !"None".equals(annotation.getLineEndingStyle()) && pathsArray.length >= 4) {
                float x2 = pathsArray[2];
                float y2 = pathsArray[3];
                float x1 = pathsArray[0];
                float y1 = pathsArray[1];
                cs.saveGraphicsState();
                if (ANGLED_STYLES.contains(annotation.getLineEndingStyle())) {
                    double angle = Math.atan2(y2 - y1, x2 - x1);
                    cs.transform(Matrix.getRotateInstance(angle, x1, y1));
                } else {
                    cs.transform(Matrix.getTranslateInstance(x1, y1));
                }
                this.drawStyle(annotation.getLineEndingStyle(), cs, 0.0f, 0.0f, ab.width, hasStroke, hasBackground, false);
                cs.restoreGraphicsState();
            }
            if ((borderEffect = annotation.getBorderEffect()) != null && borderEffect.getStyle().equals("C")) {
                borderBox = this.applyRectDifferences(this.getRectangle(), annotation.getRectDifferences());
                CloudyBorder cloudyBorder = new CloudyBorder(cs, borderEffect.getIntensity(), ab.width, this.getRectangle());
                cloudyBorder.createCloudyRectangle(annotation.getRectDifference());
                annotation.setRectangle(cloudyBorder.getRectangle());
                annotation.setRectDifference(cloudyBorder.getRectDifference());
                PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                appearanceStream.setBBox(cloudyBorder.getBBox());
                appearanceStream.setMatrix(cloudyBorder.getMatrix());
            } else {
                borderBox = this.applyRectDifferences(this.getRectangle(), annotation.getRectDifferences());
                annotation.getNormalAppearanceStream().setBBox(borderBox);
                PDRectangle paddedRectangle = this.getPaddedRectangle(borderBox, ab.width / 2.0f);
                cs.addRect(paddedRectangle.getLowerLeftX(), paddedRectangle.getLowerLeftY(), paddedRectangle.getWidth(), paddedRectangle.getHeight());
            }
            cs.drawShape(ab.width, hasStroke, hasBackground);
            int rotation = annotation.getCOSObject().getInt(COSName.ROTATE, 0);
            cs.transform(Matrix.getRotateInstance(Math.toRadians(rotation), 0.0f, 0.0f));
            float width = rotation == 90 || rotation == 270 ? borderBox.getHeight() : borderBox.getWidth();
            PDFont font = PDType1Font.HELVETICA;
            float clipWidth = width - ab.width * 4.0f;
            float clipHeight = rotation == 90 || rotation == 270 ? borderBox.getWidth() - ab.width * 4.0f : borderBox.getHeight() - ab.width * 4.0f;
            this.extractFontDetails(annotation);
            if (this.document != null && (acroForm = this.document.getDocumentCatalog().getAcroForm()) != null && (defaultResources = acroForm.getDefaultResources()) != null && (defaultResourcesFont = defaultResources.getFont(this.fontName)) != null) {
                font = defaultResourcesFont;
            }
            float yDelta = 0.7896f;
            switch (rotation) {
                case 180: {
                    xOffset = -borderBox.getUpperRightX() + ab.width * 2.0f;
                    yOffset = -borderBox.getLowerLeftY() - ab.width * 2.0f - yDelta * this.fontSize;
                    clipY = -borderBox.getUpperRightY() + ab.width * 2.0f;
                    break;
                }
                case 90: {
                    xOffset = borderBox.getLowerLeftY() + ab.width * 2.0f;
                    yOffset = -borderBox.getLowerLeftX() - ab.width * 2.0f - yDelta * this.fontSize;
                    clipY = -borderBox.getUpperRightX() + ab.width * 2.0f;
                    break;
                }
                case 270: {
                    xOffset = -borderBox.getUpperRightY() + ab.width * 2.0f;
                    yOffset = borderBox.getUpperRightX() - ab.width * 2.0f - yDelta * this.fontSize;
                    clipY = borderBox.getLowerLeftX() + ab.width * 2.0f;
                    break;
                }
                default: {
                    xOffset = borderBox.getLowerLeftX() + ab.width * 2.0f;
                    yOffset = borderBox.getUpperRightY() - ab.width * 2.0f - yDelta * this.fontSize;
                    clipY = borderBox.getLowerLeftY() + ab.width * 2.0f;
                }
            }
            cs.addRect(xOffset, clipY, clipWidth, clipHeight);
            cs.clip();
            if (annotation.getContents() != null) {
                cs.beginText();
                cs.setFont(font, this.fontSize);
                cs.setNonStrokingColor(textColor.getComponents());
                AppearanceStyle appearanceStyle = new AppearanceStyle();
                appearanceStyle.setFont(font);
                appearanceStyle.setFontSize(this.fontSize);
                PlainTextFormatter formatter = new PlainTextFormatter.Builder(cs).style(appearanceStyle).text(new PlainText(annotation.getContents())).width(width - ab.width * 4.0f).wrapLines(true).initialOffset(xOffset, yOffset).build();
                try {
                    formatter.format();
                }
                catch (IllegalArgumentException ex) {
                    throw new IOException(ex);
                }
                finally {
                    cs.endText();
                }
            }
            if (pathsArray.length > 0) {
                PDRectangle rect = this.getRectangle();
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
                annotation.getNormalAppearanceStream().setBBox(this.getRectangle());
            }
        }
        catch (IOException ex) {
            LOG.error((Object)ex);
        }
        finally {
            IOUtils.closeQuietly(cs);
        }
    }

    private PDColor extractNonStrokingColor(PDAnnotationMarkup annotation) {
        PDColor strokingColor = new PDColor(new float[]{0.0f}, (PDColorSpace)PDDeviceGray.INSTANCE);
        String defaultAppearance = annotation.getDefaultAppearance();
        if (defaultAppearance == null) {
            return strokingColor;
        }
        try {
            PDFStreamParser parser = new PDFStreamParser(defaultAppearance.getBytes(Charsets.US_ASCII));
            COSArray arguments = new COSArray();
            COSArray colors = null;
            Operator graphicOp = null;
            Object token = parser.parseNextToken();
            while (token != null) {
                if (token instanceof Operator) {
                    Operator op = (Operator)token;
                    String name = op.getName();
                    if ("g".equals(name) || "rg".equals(name) || "k".equals(name)) {
                        graphicOp = op;
                        colors = arguments;
                    }
                    arguments = new COSArray();
                } else {
                    arguments.add((COSBase)token);
                }
                token = parser.parseNextToken();
            }
            if (graphicOp != null) {
                String graphicOpName = graphicOp.getName();
                if ("g".equals(graphicOpName)) {
                    strokingColor = new PDColor(colors, (PDColorSpace)PDDeviceGray.INSTANCE);
                } else if ("rg".equals(graphicOpName)) {
                    strokingColor = new PDColor(colors, (PDColorSpace)PDDeviceRGB.INSTANCE);
                } else if ("k".equals(graphicOpName)) {
                    strokingColor = new PDColor(colors, (PDColorSpace)PDDeviceCMYK.INSTANCE);
                }
            }
        }
        catch (IOException ex) {
            LOG.warn((Object)"Problem parsing /DA, will use default black", (Throwable)ex);
        }
        return strokingColor;
    }

    private void extractFontDetails(PDAnnotationMarkup annotation) {
        PDAcroForm pdAcroForm;
        String defaultAppearance = annotation.getDefaultAppearance();
        if (defaultAppearance == null && this.document != null && (pdAcroForm = this.document.getDocumentCatalog().getAcroForm()) != null) {
            defaultAppearance = pdAcroForm.getDefaultAppearance();
        }
        if (defaultAppearance == null) {
            return;
        }
        try {
            PDFStreamParser parser = new PDFStreamParser(defaultAppearance.getBytes(Charsets.US_ASCII));
            COSArray arguments = new COSArray();
            COSArray fontArguments = new COSArray();
            Object token = parser.parseNextToken();
            while (token != null) {
                if (token instanceof Operator) {
                    Operator op = (Operator)token;
                    String name = op.getName();
                    if ("Tf".equals(name)) {
                        fontArguments = arguments;
                    }
                    arguments = new COSArray();
                } else {
                    arguments.add((COSBase)token);
                }
                token = parser.parseNextToken();
            }
            if (fontArguments.size() >= 2) {
                COSBase base = fontArguments.get(0);
                if (base instanceof COSName) {
                    this.fontName = (COSName)base;
                }
                if ((base = fontArguments.get(1)) instanceof COSNumber) {
                    this.fontSize = ((COSNumber)base).floatValue();
                }
            }
        }
        catch (IOException ex) {
            LOG.warn((Object)"Problem parsing /DA, will use default 'Helv 10'", (Throwable)ex);
        }
    }

    @Override
    public void generateRolloverAppearance() {
    }

    @Override
    public void generateDownAppearance() {
    }
}

