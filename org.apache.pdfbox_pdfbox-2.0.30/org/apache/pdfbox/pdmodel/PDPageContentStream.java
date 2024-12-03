/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Stack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceN;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDSeparation;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDInlineImage;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.NumberFormatUtil;

public final class PDPageContentStream
implements Closeable {
    private static final Log LOG = LogFactory.getLog(PDPageContentStream.class);
    private final PDDocument document;
    private OutputStream output;
    private PDResources resources;
    private boolean inTextMode = false;
    private final Stack<PDFont> fontStack = new Stack();
    private final Stack<PDColorSpace> nonStrokingColorSpaceStack = new Stack();
    private final Stack<PDColorSpace> strokingColorSpaceStack = new Stack();
    private final NumberFormat formatDecimal = NumberFormat.getNumberInstance(Locale.US);
    private final byte[] formatBuffer = new byte[32];
    private boolean sourcePageHadContents = false;

    public PDPageContentStream(PDDocument document, PDPage sourcePage) throws IOException {
        this(document, sourcePage, AppendMode.OVERWRITE, true, false);
        if (this.sourcePageHadContents) {
            LOG.warn((Object)"You are overwriting an existing content, you should use the append mode");
        }
    }

    @Deprecated
    public PDPageContentStream(PDDocument document, PDPage sourcePage, boolean appendContent, boolean compress) throws IOException {
        this(document, sourcePage, appendContent, compress, false);
    }

    public PDPageContentStream(PDDocument document, PDPage sourcePage, AppendMode appendContent, boolean compress) throws IOException {
        this(document, sourcePage, appendContent, compress, false);
    }

    @Deprecated
    public PDPageContentStream(PDDocument document, PDPage sourcePage, boolean appendContent, boolean compress, boolean resetContext) throws IOException {
        this(document, sourcePage, appendContent ? AppendMode.APPEND : AppendMode.OVERWRITE, compress, resetContext);
    }

    public PDPageContentStream(PDDocument document, PDPage sourcePage, AppendMode appendContent, boolean compress, boolean resetContext) throws IOException {
        COSName filter;
        this.document = document;
        COSName cOSName = filter = compress ? COSName.FLATE_DECODE : null;
        if (!appendContent.isOverwrite() && sourcePage.hasContents()) {
            COSArray array;
            PDStream contentsToAppend = new PDStream(document);
            COSBase contents = sourcePage.getCOSObject().getDictionaryObject(COSName.CONTENTS);
            if (contents instanceof COSArray) {
                array = (COSArray)contents;
            } else {
                array = new COSArray();
                array.add(contents);
            }
            if (appendContent.isPrepend()) {
                array.add(0, contentsToAppend.getCOSObject());
            } else {
                array.add(contentsToAppend);
            }
            if (resetContext) {
                PDStream saveGraphics = new PDStream(document);
                this.output = saveGraphics.createOutputStream(filter);
                this.saveGraphicsState();
                this.close();
                array.add(0, saveGraphics.getCOSObject());
            }
            sourcePage.getCOSObject().setItem(COSName.CONTENTS, (COSBase)array);
            this.output = contentsToAppend.createOutputStream(filter);
            if (resetContext) {
                this.restoreGraphicsState();
            }
        } else {
            this.sourcePageHadContents = sourcePage.hasContents();
            PDStream contents = new PDStream(document);
            sourcePage.setContents(contents);
            this.output = contents.createOutputStream(filter);
        }
        this.resources = sourcePage.getResources();
        if (this.resources == null) {
            this.resources = new PDResources();
            sourcePage.setResources(this.resources);
        }
        this.formatDecimal.setMaximumFractionDigits(5);
        this.formatDecimal.setGroupingUsed(false);
    }

    public PDPageContentStream(PDDocument doc, PDAppearanceStream appearance) throws IOException {
        this(doc, appearance, appearance.getStream().createOutputStream());
    }

    public PDPageContentStream(PDDocument doc, PDAppearanceStream appearance, OutputStream outputStream) throws IOException {
        this.document = doc;
        this.output = outputStream;
        this.resources = appearance.getResources();
        this.formatDecimal.setMaximumFractionDigits(4);
        this.formatDecimal.setGroupingUsed(false);
    }

    public PDPageContentStream(PDDocument doc, PDFormXObject form, OutputStream outputStream) throws IOException {
        this.document = doc;
        this.output = outputStream;
        this.resources = form.getResources();
        this.formatDecimal.setMaximumFractionDigits(4);
        this.formatDecimal.setGroupingUsed(false);
    }

    public PDPageContentStream(PDDocument doc, PDTilingPattern pattern, OutputStream outputStream) throws IOException {
        this.document = doc;
        this.output = outputStream;
        this.resources = pattern.getResources();
        this.formatDecimal.setMaximumFractionDigits(4);
        this.formatDecimal.setGroupingUsed(false);
    }

    public void beginText() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: Nested beginText() calls are not allowed.");
        }
        this.writeOperator("BT");
        this.inTextMode = true;
    }

    public void endText() throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: You must call beginText() before calling endText.");
        }
        this.writeOperator("ET");
        this.inTextMode = false;
    }

    public void setFont(PDFont font, float fontSize) throws IOException {
        if (this.fontStack.isEmpty()) {
            this.fontStack.add(font);
        } else {
            this.fontStack.setElementAt(font, this.fontStack.size() - 1);
        }
        if (font.willBeSubset()) {
            this.document.getFontsToSubset().add(font);
        }
        this.writeOperand(this.resources.add(font));
        this.writeOperand(fontSize);
        this.writeOperator("Tf");
    }

    @Deprecated
    public void drawString(String text) throws IOException {
        this.showText(text);
    }

    public void showTextWithPositioning(Object[] textWithPositioningArray) throws IOException {
        this.write("[");
        for (Object obj : textWithPositioningArray) {
            if (obj instanceof String) {
                this.showTextInternal((String)obj);
                continue;
            }
            if (obj instanceof Float) {
                this.writeOperand(((Float)obj).floatValue());
                continue;
            }
            throw new IllegalArgumentException("Argument must consist of array of Float and String types");
        }
        this.write("] ");
        this.writeOperator("TJ");
    }

    public void showText(String text) throws IOException {
        this.showTextInternal(text);
        this.write(" ");
        this.writeOperator("Tj");
    }

    protected void showTextInternal(String text) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Must call beginText() before showText()");
        }
        if (this.fontStack.isEmpty()) {
            throw new IllegalStateException("Must call setFont() before showText()");
        }
        PDFont font = this.fontStack.peek();
        if (font.willBeSubset()) {
            int codePoint;
            for (int offset = 0; offset < text.length(); offset += Character.charCount(codePoint)) {
                codePoint = text.codePointAt(offset);
                font.addToSubset(codePoint);
            }
        }
        COSWriter.writeString(font.encode(text), this.output);
    }

    @Deprecated
    public void setLeading(double leading) throws IOException {
        this.setLeading((float)leading);
    }

    public void setLeading(float leading) throws IOException {
        this.writeOperand(leading);
        this.writeOperator("TL");
    }

    public void newLine() throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Must call beginText() before newLine()");
        }
        this.writeOperator("T*");
    }

    @Deprecated
    public void moveTextPositionByAmount(float tx, float ty) throws IOException {
        this.newLineAtOffset(tx, ty);
    }

    public void newLineAtOffset(float tx, float ty) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: must call beginText() before newLineAtOffset()");
        }
        this.writeOperand(tx);
        this.writeOperand(ty);
        this.writeOperator("Td");
    }

    @Deprecated
    public void setTextMatrix(double a, double b, double c, double d, double e, double f) throws IOException {
        this.setTextMatrix(new Matrix((float)a, (float)b, (float)c, (float)d, (float)e, (float)f));
    }

    @Deprecated
    public void setTextMatrix(AffineTransform matrix) throws IOException {
        this.setTextMatrix(new Matrix(matrix));
    }

    public void setTextMatrix(Matrix matrix) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: must call beginText() before setTextMatrix");
        }
        this.writeAffineTransform(matrix.createAffineTransform());
        this.writeOperator("Tm");
    }

    @Deprecated
    public void setTextScaling(double sx, double sy, double tx, double ty) throws IOException {
        this.setTextMatrix(new Matrix((float)sx, 0.0f, 0.0f, (float)sy, (float)tx, (float)ty));
    }

    @Deprecated
    public void setTextTranslation(double tx, double ty) throws IOException {
        this.setTextMatrix(Matrix.getTranslateInstance((float)tx, (float)ty));
    }

    @Deprecated
    public void setTextRotation(double angle, double tx, double ty) throws IOException {
        this.setTextMatrix(Matrix.getRotateInstance(angle, (float)tx, (float)ty));
    }

    public void drawImage(PDImageXObject image, float x, float y) throws IOException {
        this.drawImage(image, x, y, (float)image.getWidth(), (float)image.getHeight());
    }

    public void drawImage(PDImageXObject image, float x, float y, float width, float height) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawImage is not allowed within a text block.");
        }
        this.saveGraphicsState();
        AffineTransform transform = new AffineTransform(width, 0.0f, 0.0f, height, x, y);
        this.transform(new Matrix(transform));
        this.writeOperand(this.resources.add(image));
        this.writeOperator("Do");
        this.restoreGraphicsState();
    }

    public void drawImage(PDImageXObject image, Matrix matrix) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawImage is not allowed within a text block.");
        }
        this.saveGraphicsState();
        AffineTransform transform = matrix.createAffineTransform();
        this.transform(new Matrix(transform));
        this.writeOperand(this.resources.add(image));
        this.writeOperator("Do");
        this.restoreGraphicsState();
    }

    @Deprecated
    public void drawInlineImage(PDInlineImage inlineImage, float x, float y) throws IOException {
        this.drawImage(inlineImage, x, y, (float)inlineImage.getWidth(), (float)inlineImage.getHeight());
    }

    public void drawImage(PDInlineImage inlineImage, float x, float y) throws IOException {
        this.drawImage(inlineImage, x, y, (float)inlineImage.getWidth(), (float)inlineImage.getHeight());
    }

    @Deprecated
    public void drawInlineImage(PDInlineImage inlineImage, float x, float y, float width, float height) throws IOException {
        this.drawImage(inlineImage, x, y, width, height);
    }

    public void drawImage(PDInlineImage inlineImage, float x, float y, float width, float height) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawImage is not allowed within a text block.");
        }
        this.saveGraphicsState();
        this.transform(new Matrix(width, 0.0f, 0.0f, height, x, y));
        StringBuilder sb = new StringBuilder();
        sb.append("BI");
        sb.append("\n /W ");
        sb.append(inlineImage.getWidth());
        sb.append("\n /H ");
        sb.append(inlineImage.getHeight());
        sb.append("\n /CS ");
        sb.append('/');
        sb.append(inlineImage.getColorSpace().getName());
        COSArray decodeArray = inlineImage.getDecode();
        if (decodeArray != null && decodeArray.size() > 0) {
            sb.append("\n /D ");
            sb.append('[');
            for (COSBase base : decodeArray) {
                sb.append(((COSNumber)base).intValue());
                sb.append(' ');
            }
            sb.append(']');
        }
        if (inlineImage.isStencil()) {
            sb.append("\n /IM true");
        }
        sb.append("\n /BPC ");
        sb.append(inlineImage.getBitsPerComponent());
        this.write(sb.toString());
        this.writeLine();
        this.writeOperator("ID");
        this.writeBytes(inlineImage.getData());
        this.writeLine();
        this.writeOperator("EI");
        this.restoreGraphicsState();
    }

    @Deprecated
    public void drawXObject(PDXObject xobject, float x, float y, float width, float height) throws IOException {
        AffineTransform transform = new AffineTransform(width, 0.0f, 0.0f, height, x, y);
        this.drawXObject(xobject, transform);
    }

    @Deprecated
    public void drawXObject(PDXObject xobject, AffineTransform transform) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawXObject is not allowed within a text block.");
        }
        String xObjectPrefix = xobject instanceof PDImageXObject ? "Im" : "Form";
        COSName objMapping = this.resources.add(xobject, xObjectPrefix);
        this.saveGraphicsState();
        this.transform(new Matrix(transform));
        this.writeOperand(objMapping);
        this.writeOperator("Do");
        this.restoreGraphicsState();
    }

    public void drawForm(PDFormXObject form) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawForm is not allowed within a text block.");
        }
        this.writeOperand(this.resources.add(form));
        this.writeOperator("Do");
    }

    @Deprecated
    public void concatenate2CTM(double a, double b, double c, double d, double e, double f) throws IOException {
        this.transform(new Matrix((float)a, (float)b, (float)c, (float)d, (float)e, (float)f));
    }

    @Deprecated
    public void concatenate2CTM(AffineTransform at) throws IOException {
        this.transform(new Matrix(at));
    }

    public void transform(Matrix matrix) throws IOException {
        if (this.inTextMode) {
            LOG.warn((Object)"Modifying the current transformation matrix is not allowed within text objects.");
        }
        this.writeAffineTransform(matrix.createAffineTransform());
        this.writeOperator("cm");
    }

    public void saveGraphicsState() throws IOException {
        if (this.inTextMode) {
            LOG.warn((Object)"Saving the graphics state is not allowed within text objects.");
        }
        if (!this.fontStack.isEmpty()) {
            this.fontStack.push(this.fontStack.peek());
        }
        if (!this.strokingColorSpaceStack.isEmpty()) {
            this.strokingColorSpaceStack.push(this.strokingColorSpaceStack.peek());
        }
        if (!this.nonStrokingColorSpaceStack.isEmpty()) {
            this.nonStrokingColorSpaceStack.push(this.nonStrokingColorSpaceStack.peek());
        }
        this.writeOperator("q");
    }

    public void restoreGraphicsState() throws IOException {
        if (this.inTextMode) {
            LOG.warn((Object)"Restoring the graphics state is not allowed within text objects.");
        }
        if (!this.fontStack.isEmpty()) {
            this.fontStack.pop();
        }
        if (!this.strokingColorSpaceStack.isEmpty()) {
            this.strokingColorSpaceStack.pop();
        }
        if (!this.nonStrokingColorSpaceStack.isEmpty()) {
            this.nonStrokingColorSpaceStack.pop();
        }
        this.writeOperator("Q");
    }

    @Deprecated
    public void setStrokingColorSpace(PDColorSpace colorSpace) throws IOException {
        this.setStrokingColorSpaceStack(colorSpace);
        this.writeOperand(this.getName(colorSpace));
        this.writeOperator("CS");
    }

    @Deprecated
    public void setNonStrokingColorSpace(PDColorSpace colorSpace) throws IOException {
        this.setNonStrokingColorSpaceStack(colorSpace);
        this.writeOperand(this.getName(colorSpace));
        this.writeOperator("cs");
    }

    private COSName getName(PDColorSpace colorSpace) throws IOException {
        if (colorSpace instanceof PDDeviceGray || colorSpace instanceof PDDeviceRGB || colorSpace instanceof PDDeviceCMYK) {
            return COSName.getPDFName(colorSpace.getName());
        }
        return this.resources.add(colorSpace);
    }

    public void setStrokingColor(PDColor color) throws IOException {
        if (this.strokingColorSpaceStack.isEmpty() || this.strokingColorSpaceStack.peek() != color.getColorSpace()) {
            this.writeOperand(this.getName(color.getColorSpace()));
            this.writeOperator("CS");
            this.setStrokingColorSpaceStack(color.getColorSpace());
        }
        for (float value : color.getComponents()) {
            this.writeOperand(value);
        }
        if (color.getColorSpace() instanceof PDPattern) {
            this.writeOperand(color.getPatternName());
        }
        if (color.getColorSpace() instanceof PDPattern || color.getColorSpace() instanceof PDSeparation || color.getColorSpace() instanceof PDDeviceN || color.getColorSpace() instanceof PDICCBased) {
            this.writeOperator("SCN");
        } else {
            this.writeOperator("SC");
        }
    }

    public void setStrokingColor(Color color) throws IOException {
        float[] components = new float[]{(float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f};
        PDColor pdColor = new PDColor(components, (PDColorSpace)PDDeviceRGB.INSTANCE);
        this.setStrokingColor(pdColor);
    }

    @Deprecated
    public void setStrokingColor(float[] components) throws IOException {
        if (this.strokingColorSpaceStack.isEmpty()) {
            throw new IllegalStateException("The color space must be set before setting a color");
        }
        for (float component : components) {
            this.writeOperand(component);
        }
        PDColorSpace currentStrokingColorSpace = this.strokingColorSpaceStack.peek();
        if (currentStrokingColorSpace instanceof PDSeparation || currentStrokingColorSpace instanceof PDPattern || currentStrokingColorSpace instanceof PDICCBased) {
            this.writeOperator("SCN");
        } else {
            this.writeOperator("SC");
        }
    }

    public void setStrokingColor(float r, float g, float b) throws IOException {
        if (this.isOutsideOneInterval(r) || this.isOutsideOneInterval(g) || this.isOutsideOneInterval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f)", Float.valueOf(r), Float.valueOf(g), Float.valueOf(b)));
        }
        this.writeOperand(r);
        this.writeOperand(g);
        this.writeOperand(b);
        this.writeOperator("RG");
        this.setStrokingColorSpaceStack(PDDeviceRGB.INSTANCE);
    }

    @Deprecated
    public void setStrokingColor(int r, int g, int b) throws IOException {
        if (this.isOutside255Interval(r) || this.isOutside255Interval(g) || this.isOutside255Interval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d)", r, g, b));
        }
        this.setStrokingColor((float)r / 255.0f, (float)g / 255.0f, (float)b / 255.0f);
    }

    @Deprecated
    public void setStrokingColor(int c, int m, int y, int k) throws IOException {
        if (this.isOutside255Interval(c) || this.isOutside255Interval(m) || this.isOutside255Interval(y) || this.isOutside255Interval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d,%d)", c, m, y, k));
        }
        this.setStrokingColor((float)c / 255.0f, (float)m / 255.0f, (float)y / 255.0f, (float)k / 255.0f);
    }

    public void setStrokingColor(float c, float m, float y, float k) throws IOException {
        if (this.isOutsideOneInterval(c) || this.isOutsideOneInterval(m) || this.isOutsideOneInterval(y) || this.isOutsideOneInterval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f,%.2f)", Float.valueOf(c), Float.valueOf(m), Float.valueOf(y), Float.valueOf(k)));
        }
        this.writeOperand(c);
        this.writeOperand(m);
        this.writeOperand(y);
        this.writeOperand(k);
        this.writeOperator("K");
        this.setStrokingColorSpaceStack(PDDeviceCMYK.INSTANCE);
    }

    @Deprecated
    public void setStrokingColor(int g) throws IOException {
        if (this.isOutside255Interval(g)) {
            throw new IllegalArgumentException("Parameter must be within 0..255, but is " + g);
        }
        this.setStrokingColor((float)g / 255.0f);
    }

    @Deprecated
    public void setStrokingColor(double g) throws IOException {
        this.setStrokingColor((float)g);
    }

    public void setStrokingColor(float g) throws IOException {
        if (this.isOutsideOneInterval(g)) {
            throw new IllegalArgumentException("Parameter must be within 0..1, but is " + g);
        }
        this.writeOperand(g);
        this.writeOperator("G");
        this.setStrokingColorSpaceStack(PDDeviceGray.INSTANCE);
    }

    public void setNonStrokingColor(PDColor color) throws IOException {
        if (this.nonStrokingColorSpaceStack.isEmpty() || this.nonStrokingColorSpaceStack.peek() != color.getColorSpace()) {
            this.writeOperand(this.getName(color.getColorSpace()));
            this.writeOperator("cs");
            this.setNonStrokingColorSpaceStack(color.getColorSpace());
        }
        for (float value : color.getComponents()) {
            this.writeOperand(value);
        }
        if (color.getColorSpace() instanceof PDPattern) {
            this.writeOperand(color.getPatternName());
        }
        if (color.getColorSpace() instanceof PDPattern || color.getColorSpace() instanceof PDSeparation || color.getColorSpace() instanceof PDDeviceN || color.getColorSpace() instanceof PDICCBased) {
            this.writeOperator("scn");
        } else {
            this.writeOperator("sc");
        }
    }

    public void setNonStrokingColor(Color color) throws IOException {
        float[] components = new float[]{(float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f};
        PDColor pdColor = new PDColor(components, (PDColorSpace)PDDeviceRGB.INSTANCE);
        this.setNonStrokingColor(pdColor);
    }

    @Deprecated
    public void setNonStrokingColor(float[] components) throws IOException {
        if (this.nonStrokingColorSpaceStack.isEmpty()) {
            throw new IllegalStateException("The color space must be set before setting a color");
        }
        for (float component : components) {
            this.writeOperand(component);
        }
        PDColorSpace currentNonStrokingColorSpace = this.nonStrokingColorSpaceStack.peek();
        if (currentNonStrokingColorSpace instanceof PDSeparation || currentNonStrokingColorSpace instanceof PDPattern || currentNonStrokingColorSpace instanceof PDICCBased) {
            this.writeOperator("scn");
        } else {
            this.writeOperator("sc");
        }
    }

    public void setNonStrokingColor(float r, float g, float b) throws IOException {
        if (this.isOutsideOneInterval(r) || this.isOutsideOneInterval(g) || this.isOutsideOneInterval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f)", Float.valueOf(r), Float.valueOf(g), Float.valueOf(b)));
        }
        this.writeOperand(r);
        this.writeOperand(g);
        this.writeOperand(b);
        this.writeOperator("rg");
        this.setNonStrokingColorSpaceStack(PDDeviceRGB.INSTANCE);
    }

    @Deprecated
    public void setNonStrokingColor(int r, int g, int b) throws IOException {
        if (this.isOutside255Interval(r) || this.isOutside255Interval(g) || this.isOutside255Interval(b)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d)", r, g, b));
        }
        this.setNonStrokingColor((float)r / 255.0f, (float)g / 255.0f, (float)b / 255.0f);
    }

    public void setNonStrokingColor(int c, int m, int y, int k) throws IOException {
        if (this.isOutside255Interval(c) || this.isOutside255Interval(m) || this.isOutside255Interval(y) || this.isOutside255Interval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d,%d)", c, m, y, k));
        }
        this.setNonStrokingColor((float)c / 255.0f, (float)m / 255.0f, (float)y / 255.0f, (float)k / 255.0f);
    }

    @Deprecated
    public void setNonStrokingColor(double c, double m, double y, double k) throws IOException {
        this.setNonStrokingColor((float)c, (float)m, (float)y, (float)k);
    }

    public void setNonStrokingColor(float c, float m, float y, float k) throws IOException {
        if (this.isOutsideOneInterval(c) || this.isOutsideOneInterval(m) || this.isOutsideOneInterval(y) || this.isOutsideOneInterval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..1, but are " + String.format("(%.2f,%.2f,%.2f,%.2f)", Float.valueOf(c), Float.valueOf(m), Float.valueOf(y), Float.valueOf(k)));
        }
        this.writeOperand(c);
        this.writeOperand(m);
        this.writeOperand(y);
        this.writeOperand(k);
        this.writeOperator("k");
        this.setNonStrokingColorSpaceStack(PDDeviceCMYK.INSTANCE);
    }

    public void setNonStrokingColor(int g) throws IOException {
        if (this.isOutside255Interval(g)) {
            throw new IllegalArgumentException("Parameter must be within 0..255, but is " + g);
        }
        this.setNonStrokingColor((float)g / 255.0f);
    }

    @Deprecated
    public void setNonStrokingColor(double g) throws IOException {
        this.setNonStrokingColor((float)g);
    }

    public void setNonStrokingColor(float g) throws IOException {
        if (this.isOutsideOneInterval(g)) {
            throw new IllegalArgumentException("Parameter must be within 0..1, but is " + g);
        }
        this.writeOperand(g);
        this.writeOperator("g");
        this.setNonStrokingColorSpaceStack(PDDeviceGray.INSTANCE);
    }

    public void addRect(float x, float y, float width, float height) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: addRect is not allowed within a text block.");
        }
        this.writeOperand(x);
        this.writeOperand(y);
        this.writeOperand(width);
        this.writeOperand(height);
        this.writeOperator("re");
    }

    @Deprecated
    public void fillRect(float x, float y, float width, float height) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillRect is not allowed within a text block.");
        }
        this.addRect(x, y, width, height);
        this.fill();
    }

    @Deprecated
    public void addBezier312(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        this.curveTo(x1, y1, x2, y2, x3, y3);
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: curveTo is not allowed within a text block.");
        }
        this.writeOperand(x1);
        this.writeOperand(y1);
        this.writeOperand(x2);
        this.writeOperand(y2);
        this.writeOperand(x3);
        this.writeOperand(y3);
        this.writeOperator("c");
    }

    @Deprecated
    public void addBezier32(float x2, float y2, float x3, float y3) throws IOException {
        this.curveTo2(x2, y2, x3, y3);
    }

    public void curveTo2(float x2, float y2, float x3, float y3) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: curveTo2 is not allowed within a text block.");
        }
        this.writeOperand(x2);
        this.writeOperand(y2);
        this.writeOperand(x3);
        this.writeOperand(y3);
        this.writeOperator("v");
    }

    @Deprecated
    public void addBezier31(float x1, float y1, float x3, float y3) throws IOException {
        this.curveTo1(x1, y1, x3, y3);
    }

    public void curveTo1(float x1, float y1, float x3, float y3) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: curveTo1 is not allowed within a text block.");
        }
        this.writeOperand(x1);
        this.writeOperand(y1);
        this.writeOperand(x3);
        this.writeOperand(y3);
        this.writeOperator("y");
    }

    public void moveTo(float x, float y) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: moveTo is not allowed within a text block.");
        }
        this.writeOperand(x);
        this.writeOperand(y);
        this.writeOperator("m");
    }

    public void lineTo(float x, float y) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: lineTo is not allowed within a text block.");
        }
        this.writeOperand(x);
        this.writeOperand(y);
        this.writeOperator("l");
    }

    @Deprecated
    public void addLine(float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: addLine is not allowed within a text block.");
        }
        this.moveTo(xStart, yStart);
        this.lineTo(xEnd, yEnd);
    }

    @Deprecated
    public void drawLine(float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawLine is not allowed within a text block.");
        }
        this.moveTo(xStart, yStart);
        this.lineTo(xEnd, yEnd);
        this.stroke();
    }

    @Deprecated
    public void addPolygon(float[] x, float[] y) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: addPolygon is not allowed within a text block.");
        }
        if (x.length != y.length) {
            throw new IllegalArgumentException("Error: some points are missing coordinate");
        }
        for (int i = 0; i < x.length; ++i) {
            if (i == 0) {
                this.moveTo(x[i], y[i]);
                continue;
            }
            this.lineTo(x[i], y[i]);
        }
        this.closeSubPath();
    }

    @Deprecated
    public void drawPolygon(float[] x, float[] y) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawPolygon is not allowed within a text block.");
        }
        this.addPolygon(x, y);
        this.stroke();
    }

    @Deprecated
    public void fillPolygon(float[] x, float[] y) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillPolygon is not allowed within a text block.");
        }
        this.addPolygon(x, y);
        this.fill();
    }

    public void stroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: stroke is not allowed within a text block.");
        }
        this.writeOperator("S");
    }

    public void closeAndStroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closeAndStroke is not allowed within a text block.");
        }
        this.writeOperator("s");
    }

    @Deprecated
    public void fill(int windingRule) throws IOException {
        if (windingRule == 1) {
            this.fill();
        } else if (windingRule == 0) {
            this.fillEvenOdd();
        } else {
            throw new IllegalArgumentException("Error: unknown value for winding rule");
        }
    }

    public void fill() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fill is not allowed within a text block.");
        }
        this.writeOperator("f");
    }

    public void fillEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillEvenOdd is not allowed within a text block.");
        }
        this.writeOperator("f*");
    }

    public void fillAndStroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillAndStroke is not allowed within a text block.");
        }
        this.writeOperator("B");
    }

    public void fillAndStrokeEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: fillAndStrokeEvenOdd is not allowed within a text block.");
        }
        this.writeOperator("B*");
    }

    public void closeAndFillAndStroke() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closeAndFillAndStroke is not allowed within a text block.");
        }
        this.writeOperator("b");
    }

    public void closeAndFillAndStrokeEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closeAndFillAndStrokeEvenOdd is not allowed within a text block.");
        }
        this.writeOperator("b*");
    }

    public void shadingFill(PDShading shading) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: shadingFill is not allowed within a text block.");
        }
        this.writeOperand(this.resources.add(shading));
        this.writeOperator("sh");
    }

    @Deprecated
    public void closeSubPath() throws IOException {
        this.closePath();
    }

    public void closePath() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closePath is not allowed within a text block.");
        }
        this.writeOperator("h");
    }

    @Deprecated
    public void clipPath(int windingRule) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: clipPath is not allowed within a text block.");
        }
        if (windingRule == 1) {
            this.writeOperator("W");
        } else if (windingRule == 0) {
            this.writeOperator("W*");
        } else {
            throw new IllegalArgumentException("Error: unknown value for winding rule");
        }
        this.writeOperator("n");
    }

    public void clip() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: clip is not allowed within a text block.");
        }
        this.writeOperator("W");
        this.writeOperator("n");
    }

    public void clipEvenOdd() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: clipEvenOdd is not allowed within a text block.");
        }
        this.writeOperator("W*");
        this.writeOperator("n");
    }

    public void setLineWidth(float lineWidth) throws IOException {
        this.writeOperand(lineWidth);
        this.writeOperator("w");
    }

    public void setLineJoinStyle(int lineJoinStyle) throws IOException {
        if (lineJoinStyle < 0 || lineJoinStyle > 2) {
            throw new IllegalArgumentException("Error: unknown value for line join style");
        }
        this.writeOperand(lineJoinStyle);
        this.writeOperator("j");
    }

    public void setLineCapStyle(int lineCapStyle) throws IOException {
        if (lineCapStyle < 0 || lineCapStyle > 2) {
            throw new IllegalArgumentException("Error: unknown value for line cap style");
        }
        this.writeOperand(lineCapStyle);
        this.writeOperator("J");
    }

    public void setLineDashPattern(float[] pattern, float phase) throws IOException {
        this.write("[");
        for (float value : pattern) {
            this.writeOperand(value);
        }
        this.write("] ");
        this.writeOperand(phase);
        this.writeOperator("d");
    }

    public void setMiterLimit(float miterLimit) throws IOException {
        if ((double)miterLimit <= 0.0) {
            throw new IllegalArgumentException("A miter limit <= 0 is invalid and will not render in Acrobat Reader");
        }
        this.writeOperand(miterLimit);
        this.writeOperator("M");
    }

    @Deprecated
    public void beginMarkedContentSequence(COSName tag) throws IOException {
        this.beginMarkedContent(tag);
    }

    public void beginMarkedContent(COSName tag) throws IOException {
        this.writeOperand(tag);
        this.writeOperator("BMC");
    }

    @Deprecated
    public void beginMarkedContentSequence(COSName tag, COSName propsName) throws IOException {
        this.writeOperand(tag);
        this.writeOperand(propsName);
        this.writeOperator("BDC");
    }

    public void beginMarkedContent(COSName tag, PDPropertyList propertyList) throws IOException {
        this.writeOperand(tag);
        this.writeOperand(this.resources.add(propertyList));
        this.writeOperator("BDC");
    }

    @Deprecated
    public void endMarkedContentSequence() throws IOException {
        this.endMarkedContent();
    }

    public void endMarkedContent() throws IOException {
        this.writeOperator("EMC");
    }

    @Deprecated
    public void appendRawCommands(String commands) throws IOException {
        this.output.write(commands.getBytes(Charsets.US_ASCII));
    }

    @Deprecated
    public void appendRawCommands(byte[] commands) throws IOException {
        this.output.write(commands);
    }

    @Deprecated
    public void appendRawCommands(int data) throws IOException {
        this.output.write(data);
    }

    @Deprecated
    public void appendRawCommands(double data) throws IOException {
        this.output.write(this.formatDecimal.format(data).getBytes(Charsets.US_ASCII));
    }

    @Deprecated
    public void appendRawCommands(float data) throws IOException {
        this.output.write(this.formatDecimal.format(data).getBytes(Charsets.US_ASCII));
    }

    @Deprecated
    public void appendCOSName(COSName name) throws IOException {
        name.writePDF(this.output);
    }

    public void setGraphicsStateParameters(PDExtendedGraphicsState state) throws IOException {
        this.writeOperand(this.resources.add(state));
        this.writeOperator("gs");
    }

    public void addComment(String comment) throws IOException {
        if (comment.indexOf(10) >= 0 || comment.indexOf(13) >= 0) {
            throw new IllegalArgumentException("comment should not include a newline");
        }
        this.output.write(37);
        this.output.write(comment.getBytes(Charsets.US_ASCII));
        this.output.write(10);
    }

    protected void writeOperand(float real) throws IOException {
        if (Float.isInfinite(real) || Float.isNaN(real)) {
            throw new IllegalArgumentException(real + " is not a finite number");
        }
        int byteCount = NumberFormatUtil.formatFloatFast(real, this.formatDecimal.getMaximumFractionDigits(), this.formatBuffer);
        if (byteCount == -1) {
            this.write(this.formatDecimal.format(real));
        } else {
            this.output.write(this.formatBuffer, 0, byteCount);
        }
        this.output.write(32);
    }

    private void writeOperand(int integer) throws IOException {
        this.write(this.formatDecimal.format(integer));
        this.output.write(32);
    }

    private void writeOperand(COSName name) throws IOException {
        name.writePDF(this.output);
        this.output.write(32);
    }

    private void writeOperator(String text) throws IOException {
        this.output.write(text.getBytes(Charsets.US_ASCII));
        this.output.write(10);
    }

    private void write(String text) throws IOException {
        this.output.write(text.getBytes(Charsets.US_ASCII));
    }

    private void writeLine() throws IOException {
        this.output.write(10);
    }

    private void writeBytes(byte[] data) throws IOException {
        this.output.write(data);
    }

    private void writeAffineTransform(AffineTransform transform) throws IOException {
        double[] values = new double[6];
        transform.getMatrix(values);
        for (double v : values) {
            this.writeOperand((float)v);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.inTextMode) {
            LOG.warn((Object)"You did not call endText(), some viewers won't display your text");
        }
        if (this.output != null) {
            this.output.close();
            this.output = null;
        }
    }

    private boolean isOutside255Interval(int val) {
        return val < 0 || val > 255;
    }

    private boolean isOutsideOneInterval(double val) {
        return val < 0.0 || val > 1.0;
    }

    private void setStrokingColorSpaceStack(PDColorSpace colorSpace) {
        if (this.strokingColorSpaceStack.isEmpty()) {
            this.strokingColorSpaceStack.add(colorSpace);
        } else {
            this.strokingColorSpaceStack.setElementAt(colorSpace, this.strokingColorSpaceStack.size() - 1);
        }
    }

    private void setNonStrokingColorSpaceStack(PDColorSpace colorSpace) {
        if (this.nonStrokingColorSpaceStack.isEmpty()) {
            this.nonStrokingColorSpaceStack.add(colorSpace);
        } else {
            this.nonStrokingColorSpaceStack.setElementAt(colorSpace, this.nonStrokingColorSpaceStack.size() - 1);
        }
    }

    public void setRenderingMode(RenderingMode rm) throws IOException {
        this.writeOperand(rm.intValue());
        this.writeOperator("Tr");
    }

    public void setCharacterSpacing(float spacing) throws IOException {
        this.writeOperand(spacing);
        this.writeOperator("Tc");
    }

    public void setWordSpacing(float spacing) throws IOException {
        this.writeOperand(spacing);
        this.writeOperator("Tw");
    }

    public void setHorizontalScaling(float scale) throws IOException {
        this.writeOperand(scale);
        this.writeOperator("Tz");
    }

    public void setTextRise(float rise) throws IOException {
        this.writeOperand(rise);
        this.writeOperator("Ts");
    }

    public static enum AppendMode {
        OVERWRITE,
        APPEND,
        PREPEND;


        public boolean isOverwrite() {
            return this == OVERWRITE;
        }

        public boolean isPrepend() {
            return this == PREPEND;
        }
    }
}

