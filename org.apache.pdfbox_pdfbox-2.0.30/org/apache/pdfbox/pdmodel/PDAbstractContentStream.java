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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDFont;
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
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.NumberFormatUtil;

abstract class PDAbstractContentStream
implements Closeable {
    private static final Log LOG = LogFactory.getLog(PDAbstractContentStream.class);
    protected final PDDocument document;
    protected final OutputStream outputStream;
    protected final PDResources resources;
    protected boolean inTextMode = false;
    protected final Deque<PDFont> fontStack = new ArrayDeque<PDFont>();
    protected final Deque<PDColorSpace> nonStrokingColorSpaceStack = new ArrayDeque<PDColorSpace>();
    protected final Deque<PDColorSpace> strokingColorSpaceStack = new ArrayDeque<PDColorSpace>();
    private final NumberFormat formatDecimal = NumberFormat.getNumberInstance(Locale.US);
    private final byte[] formatBuffer = new byte[32];

    PDAbstractContentStream(PDDocument document, OutputStream outputStream, PDResources resources) {
        this.document = document;
        this.outputStream = outputStream;
        this.resources = resources;
        this.formatDecimal.setMaximumFractionDigits(4);
        this.formatDecimal.setGroupingUsed(false);
    }

    protected void setMaximumFractionDigits(int fractionDigitsNumber) {
        this.formatDecimal.setMaximumFractionDigits(fractionDigitsNumber);
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
            this.fontStack.pop();
            this.fontStack.push(font);
        }
        if (font.willBeSubset()) {
            if (this.document != null) {
                this.document.getFontsToSubset().add(font);
            } else {
                LOG.warn((Object)("Using the subsetted font '" + font.getName() + "' without a PDDocument context; call subset() before saving"));
            }
        }
        this.writeOperand(this.resources.add(font));
        this.writeOperand(fontSize);
        this.writeOperator("Tf");
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
        byte[] encodedText = null;
        if (encodedText == null) {
            encodedText = font.encode(text);
        }
        if (font.willBeSubset()) {
            int codePoint;
            for (int offset = 0; offset < text.length(); offset += Character.charCount(codePoint)) {
                codePoint = text.codePointAt(offset);
                font.addToSubset(codePoint);
            }
        }
        COSWriter.writeString(encodedText, this.outputStream);
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

    public void newLineAtOffset(float tx, float ty) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: must call beginText() before newLineAtOffset()");
        }
        this.writeOperand(tx);
        this.writeOperand(ty);
        this.writeOperator("Td");
    }

    public void setTextMatrix(Matrix matrix) throws IOException {
        if (!this.inTextMode) {
            throw new IllegalStateException("Error: must call beginText() before setTextMatrix");
        }
        this.writeAffineTransform(matrix.createAffineTransform());
        this.writeOperator("Tm");
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

    public void drawImage(PDInlineImage inlineImage, float x, float y) throws IOException {
        this.drawImage(inlineImage, x, y, (float)inlineImage.getWidth(), (float)inlineImage.getHeight());
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

    public void drawForm(PDFormXObject form) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: drawForm is not allowed within a text block.");
        }
        this.writeOperand(this.resources.add(form));
        this.writeOperator("Do");
    }

    public void transform(Matrix matrix) throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: Modifying the current transformation matrix is not allowed within text objects.");
        }
        this.writeAffineTransform(matrix.createAffineTransform());
        this.writeOperator("cm");
    }

    public void saveGraphicsState() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: Saving the graphics state is not allowed within text objects.");
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
            throw new IllegalStateException("Error: Restoring the graphics state is not allowed within text objects.");
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

    protected COSName getName(PDColorSpace colorSpace) {
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

    @Deprecated
    public void setNonStrokingColor(int c, int m, int y, int k) throws IOException {
        if (this.isOutside255Interval(c) || this.isOutside255Interval(m) || this.isOutside255Interval(y) || this.isOutside255Interval(k)) {
            throw new IllegalArgumentException("Parameters must be within 0..255, but are " + String.format("(%d,%d,%d,%d)", c, m, y, k));
        }
        this.setNonStrokingColor((float)c / 255.0f, (float)m / 255.0f, (float)y / 255.0f, (float)k / 255.0f);
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

    public void closePath() throws IOException {
        if (this.inTextMode) {
            throw new IllegalStateException("Error: closePath is not allowed within a text block.");
        }
        this.writeOperator("h");
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

    public void beginMarkedContent(COSName tag) throws IOException {
        this.writeOperand(tag);
        this.writeOperator("BMC");
    }

    public void beginMarkedContent(COSName tag, PDPropertyList propertyList) throws IOException {
        this.writeOperand(tag);
        this.writeOperand(this.resources.add(propertyList));
        this.writeOperator("BDC");
    }

    public void endMarkedContent() throws IOException {
        this.writeOperator("EMC");
    }

    public void setGraphicsStateParameters(PDExtendedGraphicsState state) throws IOException {
        this.writeOperand(this.resources.add(state));
        this.writeOperator("gs");
    }

    public void addComment(String comment) throws IOException {
        if (comment.indexOf(10) >= 0 || comment.indexOf(13) >= 0) {
            throw new IllegalArgumentException("comment should not include a newline");
        }
        this.outputStream.write(37);
        this.outputStream.write(comment.getBytes(Charsets.US_ASCII));
        this.outputStream.write(10);
    }

    protected void writeOperand(float real) throws IOException {
        if (Float.isInfinite(real) || Float.isNaN(real)) {
            throw new IllegalArgumentException(real + " is not a finite number");
        }
        int byteCount = NumberFormatUtil.formatFloatFast(real, this.formatDecimal.getMaximumFractionDigits(), this.formatBuffer);
        if (byteCount == -1) {
            this.write(this.formatDecimal.format(real));
        } else {
            this.outputStream.write(this.formatBuffer, 0, byteCount);
        }
        this.outputStream.write(32);
    }

    protected void writeOperand(int integer) throws IOException {
        this.write(this.formatDecimal.format(integer));
        this.outputStream.write(32);
    }

    protected void writeOperand(COSName name) throws IOException {
        name.writePDF(this.outputStream);
        this.outputStream.write(32);
    }

    protected void writeOperator(String text) throws IOException {
        this.outputStream.write(text.getBytes(Charsets.US_ASCII));
        this.outputStream.write(10);
    }

    protected void write(String text) throws IOException {
        this.outputStream.write(text.getBytes(Charsets.US_ASCII));
    }

    protected void write(byte[] data) throws IOException {
        this.outputStream.write(data);
    }

    protected void writeLine() throws IOException {
        this.outputStream.write(10);
    }

    protected void writeBytes(byte[] data) throws IOException {
        this.outputStream.write(data);
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
        this.outputStream.close();
    }

    protected boolean isOutside255Interval(int val) {
        return val < 0 || val > 255;
    }

    private boolean isOutsideOneInterval(double val) {
        return val < 0.0 || val > 1.0;
    }

    protected void setStrokingColorSpaceStack(PDColorSpace colorSpace) {
        if (this.strokingColorSpaceStack.isEmpty()) {
            this.strokingColorSpaceStack.add(colorSpace);
        } else {
            this.strokingColorSpaceStack.pop();
            this.strokingColorSpaceStack.push(colorSpace);
        }
    }

    protected void setNonStrokingColorSpaceStack(PDColorSpace colorSpace) {
        if (this.nonStrokingColorSpaceStack.isEmpty()) {
            this.nonStrokingColorSpaceStack.add(colorSpace);
        } else {
            this.nonStrokingColorSpaceStack.pop();
            this.nonStrokingColorSpaceStack.push(colorSpace);
        }
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

    public void setRenderingMode(RenderingMode rm) throws IOException {
        this.writeOperand(rm.intValue());
        this.writeOperator("Tr");
    }

    public void setTextRise(float rise) throws IOException {
        this.writeOperand(rise);
        this.writeOperator("Ts");
    }
}

