/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.contentstream;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.contentstream.operator.state.EmptyGraphicsStackException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.filter.MissingImageReaderException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.MissingResourceException;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3CharProc;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public abstract class PDFStreamEngine {
    private static final Log LOG = LogFactory.getLog(PDFStreamEngine.class);
    private final Map<String, OperatorProcessor> operators = new HashMap<String, OperatorProcessor>(80);
    private Deque<PDGraphicsState> graphicsStack = new ArrayDeque<PDGraphicsState>();
    private PDResources resources;
    private PDPage currentPage;
    private boolean isProcessingPage;
    private Matrix initialMatrix;
    private int level = 0;

    protected PDFStreamEngine() {
    }

    @Deprecated
    public void registerOperatorProcessor(String operator, OperatorProcessor op) {
        op.setContext(this);
        this.operators.put(operator, op);
    }

    public final void addOperator(OperatorProcessor op) {
        op.setContext(this);
        this.operators.put(op.getName(), op);
    }

    private void initPage(PDPage page) {
        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }
        this.currentPage = page;
        this.graphicsStack.clear();
        this.graphicsStack.push(new PDGraphicsState(page.getCropBox()));
        this.resources = null;
        this.initialMatrix = page.getMatrix();
    }

    public void processPage(PDPage page) throws IOException {
        this.initPage(page);
        if (page.hasContents()) {
            this.isProcessingPage = true;
            this.processStream(page);
            this.isProcessingPage = false;
        }
    }

    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException {
        this.processTransparencyGroup(form);
    }

    public void showForm(PDFormXObject form) throws IOException {
        if (this.currentPage == null) {
            throw new IllegalStateException("No current page, call #processChildStream(PDContentStream, PDPage) instead");
        }
        if (form.getCOSObject().getLength() > 0L) {
            this.processStream(form);
        }
    }

    protected void processSoftMask(PDTransparencyGroup group) throws IOException {
        this.saveGraphicsState();
        Matrix softMaskCTM = this.getGraphicsState().getSoftMask().getInitialTransformationMatrix();
        this.getGraphicsState().setCurrentTransformationMatrix(softMaskCTM);
        this.getGraphicsState().setTextMatrix(new Matrix());
        this.getGraphicsState().setTextLineMatrix(new Matrix());
        try {
            this.processTransparencyGroup(group);
        }
        finally {
            this.restoreGraphicsState();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processTransparencyGroup(PDTransparencyGroup group) throws IOException {
        if (this.currentPage == null) {
            throw new IllegalStateException("No current page, call #processChildStream(PDContentStream, PDPage) instead");
        }
        PDResources parent = this.pushResources(group);
        Deque<PDGraphicsState> savedStack = this.saveGraphicsStack();
        Matrix parentMatrix = this.initialMatrix;
        PDGraphicsState graphicsState = this.getGraphicsState();
        this.initialMatrix = graphicsState.getCurrentTransformationMatrix().clone();
        graphicsState.getCurrentTransformationMatrix().concatenate(group.getMatrix());
        graphicsState.setBlendMode(BlendMode.NORMAL);
        graphicsState.setAlphaConstant(1.0);
        graphicsState.setNonStrokeAlphaConstant(1.0);
        graphicsState.setSoftMask(null);
        this.clipToRect(group.getBBox());
        try {
            this.processStreamOperators(group);
        }
        finally {
            this.initialMatrix = parentMatrix;
            this.restoreGraphicsStack(savedStack);
            this.popResources(parent);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processType3Stream(PDType3CharProc charProc, Matrix textRenderingMatrix) throws IOException {
        if (this.currentPage == null) {
            throw new IllegalStateException("No current page, call #processChildStream(PDContentStream, PDPage) instead");
        }
        PDResources parent = this.pushResources(charProc);
        Deque<PDGraphicsState> savedStack = this.saveGraphicsStack();
        this.getGraphicsState().setCurrentTransformationMatrix(textRenderingMatrix);
        textRenderingMatrix.concatenate(charProc.getMatrix());
        this.getGraphicsState().setTextMatrix(new Matrix());
        this.getGraphicsState().setTextLineMatrix(new Matrix());
        try {
            this.processStreamOperators(charProc);
        }
        finally {
            this.restoreGraphicsStack(savedStack);
            this.popResources(parent);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processAnnotation(PDAnnotation annotation, PDAppearanceStream appearance) throws IOException {
        PDRectangle bbox = appearance.getBBox();
        PDRectangle rect = annotation.getRectangle();
        if (rect != null && rect.getWidth() > 0.0f && rect.getHeight() > 0.0f && bbox != null && bbox.getWidth() > 0.0f && bbox.getHeight() > 0.0f) {
            PDResources parent = this.pushResources(appearance);
            Deque<PDGraphicsState> savedStack = this.saveGraphicsStack();
            Matrix matrix = appearance.getMatrix();
            Rectangle2D transformedBox = bbox.transform(matrix).getBounds2D();
            Matrix a = Matrix.getTranslateInstance(rect.getLowerLeftX(), rect.getLowerLeftY());
            a.concatenate(Matrix.getScaleInstance((float)((double)rect.getWidth() / transformedBox.getWidth()), (float)((double)rect.getHeight() / transformedBox.getHeight())));
            a.concatenate(Matrix.getTranslateInstance((float)(-transformedBox.getX()), (float)(-transformedBox.getY())));
            Matrix aa = Matrix.concatenate(a, matrix);
            this.getGraphicsState().setCurrentTransformationMatrix(aa);
            this.clipToRect(bbox);
            this.initialMatrix = aa.clone();
            try {
                this.processStreamOperators(appearance);
            }
            finally {
                this.restoreGraphicsStack(savedStack);
                this.popResources(parent);
            }
        }
    }

    protected final void processTilingPattern(PDTilingPattern tilingPattern, PDColor color, PDColorSpace colorSpace) throws IOException {
        this.processTilingPattern(tilingPattern, color, colorSpace, tilingPattern.getMatrix());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void processTilingPattern(PDTilingPattern tilingPattern, PDColor color, PDColorSpace colorSpace, Matrix patternMatrix) throws IOException {
        PDResources parent = this.pushResources(tilingPattern);
        Matrix parentMatrix = this.initialMatrix;
        this.initialMatrix = Matrix.concatenate(this.initialMatrix, patternMatrix);
        Deque<PDGraphicsState> savedStack = this.saveGraphicsStack();
        PDRectangle tilingBBox = tilingPattern.getBBox();
        Rectangle2D bbox = tilingPattern.getBBox().transform(patternMatrix).getBounds2D();
        PDRectangle rect = new PDRectangle((float)bbox.getX(), (float)bbox.getY(), (float)bbox.getWidth(), (float)bbox.getHeight());
        this.graphicsStack.push(new PDGraphicsState(rect));
        PDGraphicsState graphicsState = this.getGraphicsState();
        if (colorSpace != null) {
            color = new PDColor(color.getComponents(), colorSpace);
            graphicsState.setNonStrokingColorSpace(colorSpace);
            graphicsState.setNonStrokingColor(color);
            graphicsState.setStrokingColorSpace(colorSpace);
            graphicsState.setStrokingColor(color);
        }
        graphicsState.getCurrentTransformationMatrix().concatenate(patternMatrix);
        this.clipToRect(tilingBBox);
        try {
            this.processStreamOperators(tilingPattern);
        }
        finally {
            this.initialMatrix = parentMatrix;
            this.restoreGraphicsStack(savedStack);
            this.popResources(parent);
        }
    }

    public void showAnnotation(PDAnnotation annotation) throws IOException {
        PDAppearanceStream appearanceStream = this.getAppearance(annotation);
        if (appearanceStream != null) {
            this.processAnnotation(annotation, appearanceStream);
        }
    }

    public PDAppearanceStream getAppearance(PDAnnotation annotation) {
        return annotation.getNormalAppearanceStream();
    }

    protected void processChildStream(PDContentStream contentStream, PDPage page) throws IOException {
        if (this.isProcessingPage) {
            throw new IllegalStateException("Current page has already been set via  #processPage(PDPage) call #processChildStream(PDContentStream) instead");
        }
        this.initPage(page);
        this.processStream(contentStream);
        this.currentPage = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processStream(PDContentStream contentStream) throws IOException {
        PDResources parent = this.pushResources(contentStream);
        Deque<PDGraphicsState> savedStack = this.saveGraphicsStack();
        Matrix parentMatrix = this.initialMatrix;
        PDGraphicsState graphicsState = this.getGraphicsState();
        graphicsState.getCurrentTransformationMatrix().concatenate(contentStream.getMatrix());
        this.initialMatrix = graphicsState.getCurrentTransformationMatrix().clone();
        PDRectangle bbox = contentStream.getBBox();
        this.clipToRect(bbox);
        try {
            this.processStreamOperators(contentStream);
        }
        finally {
            this.initialMatrix = parentMatrix;
            this.restoreGraphicsStack(savedStack);
            this.popResources(parent);
        }
    }

    private void processStreamOperators(PDContentStream contentStream) throws IOException {
        ArrayList<COSBase> arguments = new ArrayList<COSBase>();
        PDFStreamParser parser = new PDFStreamParser(contentStream);
        Object token = parser.parseNextToken();
        while (token != null) {
            if (token instanceof Operator) {
                this.processOperator((Operator)token, arguments);
                arguments.clear();
            } else {
                arguments.add((COSBase)token);
            }
            token = parser.parseNextToken();
        }
    }

    private PDResources pushResources(PDContentStream contentStream) {
        PDResources parentResources = this.resources;
        PDResources streamResources = contentStream.getResources();
        if (streamResources != null) {
            this.resources = streamResources;
        } else if (this.resources == null) {
            this.resources = this.currentPage.getResources();
            if (this.resources == null) {
                this.resources = new PDResources();
            }
        }
        return parentResources;
    }

    private void popResources(PDResources parentResources) {
        this.resources = parentResources;
    }

    private void clipToRect(PDRectangle rectangle) {
        if (rectangle != null) {
            PDGraphicsState graphicsState = this.getGraphicsState();
            GeneralPath clip = rectangle.transform(graphicsState.getCurrentTransformationMatrix());
            graphicsState.intersectClippingPath(clip);
        }
    }

    public void beginText() throws IOException {
    }

    public void endText() throws IOException {
    }

    public void showTextString(byte[] string) throws IOException {
        this.showText(string);
    }

    public void showTextStrings(COSArray array) throws IOException {
        PDTextState textState = this.getGraphicsState().getTextState();
        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100.0f;
        PDFont font = textState.getFont();
        boolean isVertical = false;
        if (font != null) {
            isVertical = font.isVertical();
        }
        for (COSBase obj : array) {
            if (obj instanceof COSNumber) {
                float ty;
                float tx;
                float tj = ((COSNumber)obj).floatValue();
                if (isVertical) {
                    tx = 0.0f;
                    ty = -tj / 1000.0f * fontSize;
                } else {
                    tx = -tj / 1000.0f * fontSize * horizontalScaling;
                    ty = 0.0f;
                }
                this.applyTextAdjustment(tx, ty);
                continue;
            }
            if (obj instanceof COSString) {
                byte[] string = ((COSString)obj).getBytes();
                this.showText(string);
                continue;
            }
            if (obj instanceof COSArray) {
                LOG.error((Object)("Nested arrays are not allowed in an array for TJ operation: " + obj));
                continue;
            }
            LOG.error((Object)("Unknown type " + obj.getClass().getSimpleName() + " in array for TJ operation: " + obj));
        }
    }

    protected void applyTextAdjustment(float tx, float ty) throws IOException {
        this.getGraphicsState().getTextMatrix().translate(tx, ty);
    }

    protected void showText(byte[] string) throws IOException {
        PDGraphicsState state = this.getGraphicsState();
        PDTextState textState = state.getTextState();
        PDFont font = textState.getFont();
        if (font == null) {
            LOG.warn((Object)"No current font, will use default");
            font = PDType1Font.HELVETICA;
        }
        float fontSize = textState.getFontSize();
        float horizontalScaling = textState.getHorizontalScaling() / 100.0f;
        float charSpacing = textState.getCharacterSpacing();
        Matrix parameters = new Matrix(fontSize * horizontalScaling, 0.0f, 0.0f, fontSize, 0.0f, textState.getRise());
        Matrix textMatrix = this.getGraphicsState().getTextMatrix();
        ByteArrayInputStream in = new ByteArrayInputStream(string);
        while (((InputStream)in).available() > 0) {
            float ty;
            float tx;
            int before = ((InputStream)in).available();
            int code = font.readCode(in);
            int codeLength = before - ((InputStream)in).available();
            float wordSpacing = 0.0f;
            if (codeLength == 1 && code == 32) {
                wordSpacing += textState.getWordSpacing();
            }
            Matrix ctm = state.getCurrentTransformationMatrix();
            Matrix textRenderingMatrix = parameters.multiply(textMatrix).multiply(ctm);
            if (font.isVertical()) {
                Vector v = font.getPositionVector(code);
                textRenderingMatrix.translate(v);
            }
            Vector w = font.getDisplacement(code);
            this.showGlyph(textRenderingMatrix, font, code, w);
            if (font.isVertical()) {
                tx = 0.0f;
                ty = w.getY() * fontSize + charSpacing + wordSpacing;
            } else {
                tx = (w.getX() * fontSize + charSpacing + wordSpacing) * horizontalScaling;
                ty = 0.0f;
            }
            textMatrix.translate(tx, ty);
        }
    }

    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
        if (font instanceof PDType3Font) {
            this.showType3Glyph(textRenderingMatrix, (PDType3Font)font, code, displacement);
        } else {
            this.showFontGlyph(textRenderingMatrix, font, code, displacement);
        }
    }

    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) throws IOException {
        this.showGlyph(textRenderingMatrix, font, code, font.toUnicode(code), displacement);
    }

    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
    }

    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) throws IOException {
        this.showFontGlyph(textRenderingMatrix, font, code, font.toUnicode(code), displacement);
    }

    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code, String unicode, Vector displacement) throws IOException {
        PDType3CharProc charProc = font.getCharProc(code);
        if (charProc != null) {
            this.processType3Stream(charProc, textRenderingMatrix);
        }
    }

    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code, Vector displacement) throws IOException {
        this.showType3Glyph(textRenderingMatrix, font, code, font.toUnicode(code), displacement);
    }

    public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
    }

    public void endMarkedContentSequence() {
    }

    public void processOperator(String operation, List<COSBase> arguments) throws IOException {
        Operator operator = Operator.getOperator(operation);
        this.processOperator(operator, arguments);
    }

    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String name = operator.getName();
        OperatorProcessor processor = this.operators.get(name);
        if (processor != null) {
            processor.setContext(this);
            try {
                processor.process(operator, operands);
            }
            catch (IOException e) {
                this.operatorException(operator, operands, e);
            }
        } else {
            this.unsupportedOperator(operator, operands);
        }
    }

    protected void unsupportedOperator(Operator operator, List<COSBase> operands) throws IOException {
    }

    protected void operatorException(Operator operator, List<COSBase> operands, IOException e) throws IOException {
        if (e instanceof MissingOperandException || e instanceof MissingResourceException || e instanceof MissingImageReaderException) {
            LOG.error((Object)e.getMessage());
        } else if (e instanceof EmptyGraphicsStackException) {
            LOG.warn((Object)e.getMessage());
        } else if (operator.getName().equals("Do")) {
            LOG.warn((Object)e.getMessage());
        } else {
            throw e;
        }
    }

    public void saveGraphicsState() {
        this.graphicsStack.push(this.graphicsStack.peek().clone());
    }

    public void restoreGraphicsState() {
        this.graphicsStack.pop();
    }

    protected final Deque<PDGraphicsState> saveGraphicsStack() {
        Deque<PDGraphicsState> savedStack = this.graphicsStack;
        this.graphicsStack = new ArrayDeque<PDGraphicsState>(1);
        this.graphicsStack.add(savedStack.peek().clone());
        return savedStack;
    }

    protected final void restoreGraphicsStack(Deque<PDGraphicsState> snapshot) {
        this.graphicsStack = snapshot;
    }

    public int getGraphicsStackSize() {
        return this.graphicsStack.size();
    }

    public PDGraphicsState getGraphicsState() {
        return this.graphicsStack.peek();
    }

    public Matrix getTextLineMatrix() {
        return this.getGraphicsState().getTextLineMatrix();
    }

    public void setTextLineMatrix(Matrix value) {
        this.getGraphicsState().setTextLineMatrix(value);
    }

    public Matrix getTextMatrix() {
        return this.getGraphicsState().getTextMatrix();
    }

    public void setTextMatrix(Matrix value) {
        this.getGraphicsState().setTextMatrix(value);
    }

    public void setLineDashPattern(COSArray array, int phase) {
        if (phase < 0) {
            LOG.warn((Object)("Dash phase has negative value " + phase + ", set to 0"));
            phase = 0;
        }
        PDLineDashPattern lineDash = new PDLineDashPattern(array, phase);
        this.getGraphicsState().setLineDashPattern(lineDash);
    }

    public PDResources getResources() {
        return this.resources;
    }

    public PDPage getCurrentPage() {
        return this.currentPage;
    }

    public Matrix getInitialMatrix() {
        return this.initialMatrix;
    }

    public Point2D.Float transformedPoint(float x, float y) {
        float[] position = new float[]{x, y};
        this.getGraphicsState().getCurrentTransformationMatrix().createAffineTransform().transform(position, 0, position, 0, 1);
        return new Point2D.Float(position[0], position[1]);
    }

    protected float transformWidth(float width) {
        Matrix ctm = this.getGraphicsState().getCurrentTransformationMatrix();
        float x = ctm.getScaleX() + ctm.getShearX();
        float y = ctm.getScaleY() + ctm.getShearY();
        return width * (float)Math.sqrt((double)(x * x + y * y) * 0.5);
    }

    public int getLevel() {
        return this.level;
    }

    public void increaseLevel() {
        ++this.level;
    }

    public void decreaseLevel() {
        --this.level;
        if (this.level < 0) {
            LOG.error((Object)("level is " + this.level));
        }
    }
}

