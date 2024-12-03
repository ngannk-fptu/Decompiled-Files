/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1CFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDType3Font;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.blend.BlendMode;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDSeparation;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentGroup;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentMembershipDictionary;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDShadingPattern;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDSoftMask;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.AnnotationFilter;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationUnknown;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.rendering.CIDType0Glyph2D;
import org.apache.pdfbox.rendering.Glyph2D;
import org.apache.pdfbox.rendering.GroupGraphics;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.rendering.RenderDestination;
import org.apache.pdfbox.rendering.SoftMask;
import org.apache.pdfbox.rendering.TTFGlyph2D;
import org.apache.pdfbox.rendering.TilingPaintFactory;
import org.apache.pdfbox.rendering.Type1Glyph2D;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

public class PageDrawer
extends PDFGraphicsStreamEngine {
    private static final Log LOG = LogFactory.getLog(PageDrawer.class);
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS_NAME.startsWith("windows");
    private static final boolean IS_LINUX = OS_NAME.startsWith("linux");
    private final PDFRenderer renderer;
    private final boolean subsamplingAllowed;
    private Graphics2D graphics;
    private AffineTransform xform;
    private float xformScalingFactorX;
    private float xformScalingFactorY;
    private PDRectangle pageSize;
    private boolean flipTG = false;
    private int clipWindingRule = -1;
    private GeneralPath linePath = new GeneralPath();
    private List<Path2D> lastClips;
    private Shape initialClip;
    private List<Shape> textClippings;
    private final Map<PDFont, Glyph2D> fontGlyph2D = new HashMap<PDFont, Glyph2D>();
    private final TilingPaintFactory tilingPaintFactory = new TilingPaintFactory(this);
    private final Deque<TransparencyGroup> transparencyGroupStack = new ArrayDeque<TransparencyGroup>();
    private int nestedHiddenOCGCount;
    private final RenderDestination destination;
    private final RenderingHints renderingHints;
    private final float imageDownscalingOptimizationThreshold;
    private LookupTable invTable = null;
    private AnnotationFilter annotationFilter = new AnnotationFilter(){

        @Override
        public boolean accept(PDAnnotation annotation) {
            return true;
        }
    };

    public PageDrawer(PageDrawerParameters parameters) throws IOException {
        super(parameters.getPage());
        this.renderer = parameters.getRenderer();
        this.subsamplingAllowed = parameters.isSubsamplingAllowed();
        this.destination = parameters.getDestination();
        this.renderingHints = parameters.getRenderingHints();
        this.imageDownscalingOptimizationThreshold = parameters.getImageDownscalingOptimizationThreshold();
    }

    public AnnotationFilter getAnnotationFilter() {
        return this.annotationFilter;
    }

    public void setAnnotationFilter(AnnotationFilter annotationFilter) {
        this.annotationFilter = annotationFilter;
    }

    public final PDFRenderer getRenderer() {
        return this.renderer;
    }

    protected final Graphics2D getGraphics() {
        return this.graphics;
    }

    protected final GeneralPath getLinePath() {
        return this.linePath;
    }

    private void setRenderingHints() {
        this.graphics.addRenderingHints(this.renderingHints);
    }

    public void drawPage(Graphics g, PDRectangle pageSize) throws IOException {
        this.graphics = (Graphics2D)g;
        this.xform = this.graphics.getTransform();
        Matrix m = new Matrix(this.xform);
        this.xformScalingFactorX = Math.abs(m.getScalingFactorX());
        this.xformScalingFactorY = Math.abs(m.getScalingFactorY());
        this.initialClip = this.graphics.getClip();
        this.pageSize = pageSize;
        this.setRenderingHints();
        this.graphics.translate(0.0, pageSize.getHeight());
        this.graphics.scale(1.0, -1.0);
        this.graphics.translate(-pageSize.getLowerLeftX(), -pageSize.getLowerLeftY());
        this.processPage(this.getPage());
        for (PDAnnotation annotation : this.getPage().getAnnotations(this.annotationFilter)) {
            this.showAnnotation(annotation);
        }
        this.graphics = null;
    }

    void drawTilingPattern(Graphics2D g, PDTilingPattern pattern, PDColorSpace colorSpace, PDColor color, Matrix patternMatrix) throws IOException {
        Graphics2D savedGraphics = this.graphics;
        this.graphics = g;
        GeneralPath savedLinePath = this.linePath;
        this.linePath = new GeneralPath();
        int savedClipWindingRule = this.clipWindingRule;
        this.clipWindingRule = -1;
        List<Path2D> savedLastClips = this.lastClips;
        this.lastClips = null;
        Shape savedInitialClip = this.initialClip;
        this.initialClip = null;
        boolean savedFlipTG = this.flipTG;
        this.flipTG = true;
        this.setRenderingHints();
        this.processTilingPattern(pattern, color, colorSpace, patternMatrix);
        this.flipTG = savedFlipTG;
        this.graphics = savedGraphics;
        this.linePath = savedLinePath;
        this.lastClips = savedLastClips;
        this.initialClip = savedInitialClip;
        this.clipWindingRule = savedClipWindingRule;
    }

    private float clampColor(float color) {
        return color < 0.0f ? 0.0f : (color > 1.0f ? 1.0f : color);
    }

    protected Paint getPaint(PDColor color) throws IOException {
        PDColorSpace colorSpace = color.getColorSpace();
        if (colorSpace instanceof PDSeparation && "None".equals(((PDSeparation)colorSpace).getColorantName())) {
            return new Color(0, 0, 0, 0);
        }
        if (!(colorSpace instanceof PDPattern)) {
            float[] rgb = colorSpace.toRGB(color.getComponents());
            return new Color(this.clampColor(rgb[0]), this.clampColor(rgb[1]), this.clampColor(rgb[2]));
        }
        PDPattern patternSpace = (PDPattern)colorSpace;
        PDAbstractPattern pattern = patternSpace.getPattern(color);
        if (pattern instanceof PDTilingPattern) {
            PDTilingPattern tilingPattern = (PDTilingPattern)pattern;
            if (tilingPattern.getPaintType() == 1) {
                return this.tilingPaintFactory.create(tilingPattern, null, null, this.xform);
            }
            return this.tilingPaintFactory.create(tilingPattern, patternSpace.getUnderlyingColorSpace(), color, this.xform);
        }
        PDShadingPattern shadingPattern = (PDShadingPattern)pattern;
        PDShading shading = shadingPattern.getShading();
        if (shading == null) {
            LOG.error((Object)"shadingPattern is null, will be filled with transparency");
            return new Color(0, 0, 0, 0);
        }
        return shading.toPaint(Matrix.concatenate(this.getInitialMatrix(), shadingPattern.getMatrix()));
    }

    protected final void setClip() {
        List<Path2D> clippingPaths = this.getGraphicsState().getCurrentClippingPaths();
        if (clippingPaths != this.lastClips) {
            this.transferClip(this.graphics);
            if (this.initialClip != null) {
                // empty if block
            }
            this.lastClips = clippingPaths;
        }
    }

    protected void transferClip(Graphics2D graphics) {
        Area clippingPath = this.getGraphicsState().getCurrentClippingPath();
        if (clippingPath.getPathIterator(null).isDone()) {
            graphics.setClip(new Rectangle());
        } else {
            graphics.setClip(clippingPath);
        }
    }

    @Override
    public void beginText() throws IOException {
        this.setClip();
        this.beginTextClip();
    }

    @Override
    public void endText() throws IOException {
        this.endTextClip();
    }

    private void beginTextClip() {
        this.textClippings = new ArrayList<Shape>();
    }

    private void endTextClip() {
        PDGraphicsState state = this.getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();
        if (renderingMode.isClip() && !this.textClippings.isEmpty()) {
            GeneralPath path = new GeneralPath(1, this.textClippings.size());
            for (Shape shape : this.textClippings) {
                path.append(shape, false);
            }
            state.intersectClippingPath(path);
            this.textClippings = new ArrayList<Shape>();
            this.lastClips = null;
        }
    }

    @Override
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, Vector displacement) throws IOException {
        AffineTransform at = textRenderingMatrix.createAffineTransform();
        at.concatenate(font.getFontMatrix().createAffineTransform());
        Glyph2D glyph2D = this.createGlyph2D(font);
        try {
            this.drawGlyph2D(glyph2D, font, code, displacement, at);
        }
        catch (IOException ex) {
            LOG.error((Object)("Could not draw glyph for code " + code + " at position (" + at.getTranslateX() + "," + at.getTranslateY() + ")"), (Throwable)ex);
        }
    }

    private void drawGlyph2D(Glyph2D glyph2D, PDFont font, int code, Vector displacement, AffineTransform at) throws IOException {
        PDGraphicsState state = this.getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();
        GeneralPath path = glyph2D.getPathForCharacterCode(code);
        if (path != null) {
            if (!font.isEmbedded() && !font.isVertical() && !font.isStandard14() && font.hasExplicitWidth(code)) {
                float fontWidth = font.getWidthFromFont(code);
                if (displacement.getX() > 0.0f && fontWidth > 0.0f && (double)Math.abs(fontWidth - displacement.getX() * 1000.0f) > 1.0E-4) {
                    float pdfWidth = displacement.getX() * 1000.0f;
                    at.scale(pdfWidth / fontWidth, 1.0);
                }
            }
            Shape glyph = at.createTransformedShape(path);
            if (this.isContentRendered()) {
                if (renderingMode.isFill()) {
                    this.graphics.setComposite(state.getNonStrokingJavaComposite());
                    this.graphics.setPaint(this.getNonStrokingPaint());
                    this.setClip();
                    this.graphics.fill(glyph);
                }
                if (renderingMode.isStroke()) {
                    this.graphics.setComposite(state.getStrokingJavaComposite());
                    this.graphics.setPaint(this.getStrokingPaint());
                    this.graphics.setStroke(this.getStroke());
                    this.setClip();
                    this.graphics.draw(glyph);
                }
            }
            if (renderingMode.isClip()) {
                this.textClippings.add(glyph);
            }
        }
    }

    @Override
    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code, Vector displacement) throws IOException {
        PDGraphicsState state = this.getGraphicsState();
        RenderingMode renderingMode = state.getTextState().getRenderingMode();
        if (!RenderingMode.NEITHER.equals((Object)renderingMode)) {
            super.showType3Glyph(textRenderingMatrix, font, code, displacement);
        }
    }

    private Glyph2D createGlyph2D(PDFont font) throws IOException {
        Glyph2D glyph2D = this.fontGlyph2D.get(font);
        if (glyph2D != null) {
            return glyph2D;
        }
        if (font instanceof PDTrueTypeFont) {
            PDTrueTypeFont ttfFont = (PDTrueTypeFont)font;
            glyph2D = new TTFGlyph2D(ttfFont);
        } else if (font instanceof PDType1Font) {
            PDType1Font pdType1Font = (PDType1Font)font;
            glyph2D = new Type1Glyph2D(pdType1Font);
        } else if (font instanceof PDType1CFont) {
            PDType1CFont type1CFont = (PDType1CFont)font;
            glyph2D = new Type1Glyph2D(type1CFont);
        } else if (font instanceof PDType0Font) {
            PDType0Font type0Font = (PDType0Font)font;
            if (type0Font.getDescendantFont() instanceof PDCIDFontType2) {
                glyph2D = new TTFGlyph2D(type0Font);
            } else if (type0Font.getDescendantFont() instanceof PDCIDFontType0) {
                PDCIDFontType0 cidType0Font = (PDCIDFontType0)type0Font.getDescendantFont();
                glyph2D = new CIDType0Glyph2D(cidType0Font);
            }
        } else {
            throw new IllegalStateException("Bad font type: " + font.getClass().getSimpleName());
        }
        if (glyph2D != null) {
            this.fontGlyph2D.put(font, glyph2D);
        }
        if (glyph2D == null) {
            throw new UnsupportedOperationException("No font for " + font.getName());
        }
        return glyph2D;
    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        this.linePath.moveTo((float)p0.getX(), (float)p0.getY());
        this.linePath.lineTo((float)p1.getX(), (float)p1.getY());
        this.linePath.lineTo((float)p2.getX(), (float)p2.getY());
        this.linePath.lineTo((float)p3.getX(), (float)p3.getY());
        this.linePath.closePath();
    }

    private Paint applySoftMaskToPaint(Paint parentPaint, PDSoftMask softMask) throws IOException {
        TransparencyGroup transparencyGroup;
        BufferedImage image;
        PDTransparencyGroup form;
        PDColorSpace colorSpace;
        COSArray backdropColorArray;
        if (softMask == null || softMask.getGroup() == null) {
            return parentPaint;
        }
        PDColor backdropColor = null;
        if (COSName.LUMINOSITY.equals(softMask.getSubType()) && (backdropColorArray = softMask.getBackdropColor()) != null && (colorSpace = (form = softMask.getGroup()).getGroup().getColorSpace(form.getResources())) != null) {
            backdropColor = new PDColor(backdropColorArray, colorSpace);
        }
        if ((image = (transparencyGroup = new TransparencyGroup(softMask.getGroup(), true, softMask.getInitialTransformationMatrix(), backdropColor)).getImage()) == null) {
            return parentPaint;
        }
        BufferedImage gray = new BufferedImage(image.getWidth(), image.getHeight(), 10);
        if (COSName.ALPHA.equals(softMask.getSubType())) {
            gray.setData(image.getAlphaRaster());
        } else if (COSName.LUMINOSITY.equals(softMask.getSubType())) {
            Graphics g = gray.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
        } else {
            throw new IOException("Invalid soft mask subtype.");
        }
        gray = this.adjustImage(gray);
        Rectangle2D tpgBounds = transparencyGroup.getBounds();
        return new SoftMask(parentPaint, gray, tpgBounds, backdropColor, softMask.getTransferFunction());
    }

    private BufferedImage adjustImage(BufferedImage gray) {
        AffineTransform at = new AffineTransform(this.xform);
        at.scale(1.0 / (double)this.xformScalingFactorX, 1.0 / (double)this.xformScalingFactorY);
        Rectangle originalBounds = new Rectangle(gray.getWidth(), gray.getHeight());
        Rectangle2D transformedBounds = at.createTransformedShape(originalBounds).getBounds2D();
        at.preConcatenate(AffineTransform.getTranslateInstance(-transformedBounds.getMinX(), -transformedBounds.getMinY()));
        int width = (int)Math.ceil(transformedBounds.getWidth());
        int height = (int)Math.ceil(transformedBounds.getHeight());
        if (width == gray.getWidth() && height == gray.getHeight() && at.isIdentity()) {
            return gray;
        }
        BufferedImage transformedGray = new BufferedImage(width, height, 10);
        Graphics2D g2 = (Graphics2D)transformedGray.getGraphics();
        g2.drawImage(gray, at, null);
        g2.dispose();
        return transformedGray;
    }

    private Paint getStrokingPaint() throws IOException {
        return this.applySoftMaskToPaint(this.getPaint(this.getGraphicsState().getStrokingColor()), this.getGraphicsState().getSoftMask());
    }

    protected final Paint getNonStrokingPaint() throws IOException {
        return this.applySoftMaskToPaint(this.getPaint(this.getGraphicsState().getNonStrokingColor()), this.getGraphicsState().getSoftMask());
    }

    private Stroke getStroke() {
        PDLineDashPattern dashPattern;
        float[] dashArray;
        PDGraphicsState state = this.getGraphicsState();
        float lineWidth = this.transformWidth(state.getLineWidth());
        if ((double)lineWidth < 0.25) {
            lineWidth = 0.25f;
        }
        if (this.isAllZeroDash(dashArray = (dashPattern = state.getLineDashPattern()).getDashArray())) {
            return new Stroke(){

                @Override
                public Shape createStrokedShape(Shape p) {
                    return new Area();
                }
            };
        }
        float phaseStart = dashPattern.getPhase();
        dashArray = this.getDashArray(dashPattern);
        phaseStart = this.transformWidth(phaseStart);
        int lineCap = Math.min(2, Math.max(0, state.getLineCap()));
        int lineJoin = Math.min(2, Math.max(0, state.getLineJoin()));
        float miterLimit = state.getMiterLimit();
        if (miterLimit < 1.0f) {
            LOG.warn((Object)("Miter limit must be >= 1, value " + miterLimit + " is ignored"));
            miterLimit = 10.0f;
        }
        phaseStart = Math.min(phaseStart, 32767.0f);
        return new BasicStroke(lineWidth, lineCap, lineJoin, miterLimit, dashArray, phaseStart);
    }

    private boolean isAllZeroDash(float[] dashArray) {
        if (dashArray.length > 0) {
            for (int i = 0; i < dashArray.length; ++i) {
                if (dashArray[i] == 0.0f) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    private float[] getDashArray(PDLineDashPattern dashPattern) {
        int i;
        float[] dashArray = dashPattern.getDashArray();
        if (dashArray.length == 0) {
            return null;
        }
        for (i = 0; i < dashArray.length; ++i) {
            if (!Float.isInfinite(dashArray[i]) && !Float.isNaN(dashArray[i])) continue;
            return null;
        }
        for (i = 0; i < dashArray.length; ++i) {
            float w = this.transformWidth(dashArray[i]);
            dashArray[i] = this.xformScalingFactorX < 0.5f ? Math.max(w, 0.2f) : Math.max(w, 0.062f);
        }
        return dashArray;
    }

    @Override
    public void strokePath() throws IOException {
        if (this.isContentRendered()) {
            this.graphics.setComposite(this.getGraphicsState().getStrokingJavaComposite());
            this.graphics.setPaint(this.getStrokingPaint());
            this.graphics.setStroke(this.getStroke());
            this.setClip();
            this.graphics.draw(this.linePath);
        }
        this.linePath.reset();
    }

    @Override
    public void fillPath(int windingRule) throws IOException {
        Shape shape;
        boolean noAntiAlias;
        PDGraphicsState graphicsState = this.getGraphicsState();
        this.graphics.setComposite(graphicsState.getNonStrokingJavaComposite());
        this.setClip();
        this.linePath.setWindingRule(windingRule);
        Rectangle2D bounds = this.linePath.getBounds2D();
        boolean bl = noAntiAlias = this.isRectangular(this.linePath) && bounds.getWidth() > 1.0 && bounds.getHeight() > 1.0;
        if (noAntiAlias) {
            this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        if (graphicsState.getNonStrokingColorSpace() instanceof PDPattern) {
            Area area = new Area(this.linePath);
            Shape clip = this.graphics.getClip();
            if (clip != null) {
                area.intersect(new Area(clip));
            }
            this.intersectShadingBBox(graphicsState.getNonStrokingColor(), area);
            shape = area;
        } else {
            shape = this.linePath;
        }
        if (this.isContentRendered() && !shape.getPathIterator(null).isDone()) {
            this.graphics.setPaint(this.getNonStrokingPaint());
            this.graphics.fill(shape);
        }
        this.linePath.reset();
        if (noAntiAlias) {
            this.setRenderingHints();
        }
    }

    private void intersectShadingBBox(PDColor color, Area area) throws IOException {
        PDShading shading;
        PDRectangle bbox;
        PDColorSpace colorSpace;
        PDAbstractPattern pat;
        if (color.getColorSpace() instanceof PDPattern && (pat = ((PDPattern)(colorSpace = color.getColorSpace())).getPattern(color)) instanceof PDShadingPattern && (bbox = (shading = ((PDShadingPattern)pat).getShading()).getBBox()) != null) {
            Matrix m = Matrix.concatenate(this.getInitialMatrix(), pat.getMatrix());
            Area bboxArea = new Area(bbox.transform(m));
            area.intersect(bboxArea);
        }
    }

    private boolean isRectangular(GeneralPath path) {
        PathIterator iter = path.getPathIterator(null);
        double[] coords = new double[6];
        int count = 0;
        int[] xs = new int[4];
        int[] ys = new int[4];
        while (!iter.isDone()) {
            switch (iter.currentSegment(coords)) {
                case 0: {
                    if (count != 0) {
                        return false;
                    }
                    xs[count] = (int)Math.floor(coords[0]);
                    ys[count] = (int)Math.floor(coords[1]);
                    ++count;
                    break;
                }
                case 1: {
                    if (count >= 4) {
                        return false;
                    }
                    xs[count] = (int)Math.floor(coords[0]);
                    ys[count] = (int)Math.floor(coords[1]);
                    ++count;
                    break;
                }
                case 3: {
                    return false;
                }
            }
            iter.next();
        }
        if (count == 4) {
            return xs[0] == xs[1] || xs[0] == xs[2] || ys[0] == ys[1] || ys[0] == ys[3];
        }
        return false;
    }

    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
        GeneralPath path = (GeneralPath)this.linePath.clone();
        this.fillPath(windingRule);
        this.linePath = path;
        this.strokePath();
    }

    @Override
    public void clip(int windingRule) {
        this.clipWindingRule = windingRule;
    }

    @Override
    public void moveTo(float x, float y) {
        this.linePath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        this.linePath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        this.linePath.curveTo(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint() {
        return this.linePath.getCurrentPoint();
    }

    @Override
    public void closePath() {
        this.linePath.closePath();
    }

    @Override
    public void endPath() {
        if (this.clipWindingRule != -1) {
            this.linePath.setWindingRule(this.clipWindingRule);
            if (!this.linePath.getPathIterator(null).isDone()) {
                this.getGraphicsState().intersectClippingPath(this.linePath);
            }
            this.lastClips = null;
            this.clipWindingRule = -1;
        }
        this.linePath.reset();
    }

    @Override
    public void drawImage(PDImage pdImage) throws IOException {
        if (pdImage instanceof PDImageXObject && this.isHiddenOCG(((PDImageXObject)pdImage).getOptionalContent())) {
            return;
        }
        if (!this.isContentRendered()) {
            return;
        }
        Matrix ctm = this.getGraphicsState().getCurrentTransformationMatrix();
        AffineTransform at = ctm.createAffineTransform();
        if (!pdImage.getInterpolate()) {
            boolean isScaledUp;
            BufferedImage bim = this.subsamplingAllowed ? pdImage.getImage(null, this.getSubsampling(pdImage, at)) : pdImage.getImage();
            Matrix m = new Matrix(at);
            boolean bl = isScaledUp = bim.getWidth() < Math.abs(Math.round(m.getScalingFactorX())) || bim.getHeight() < Math.abs(Math.round(m.getScalingFactorY()));
            if (isScaledUp) {
                this.graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            }
        }
        this.graphics.setComposite(this.getGraphicsState().getNonStrokingJavaComposite());
        this.setClip();
        if (pdImage.isStencil()) {
            if (this.getGraphicsState().getNonStrokingColor().getColorSpace() instanceof PDPattern) {
                boolean smallMask;
                Paint paint = this.getNonStrokingPaint();
                Rectangle2D.Float unitRect = new Rectangle2D.Float(0.0f, 0.0f, 1.0f, 1.0f);
                Rectangle2D bounds = at.createTransformedShape(unitRect).getBounds2D();
                int w = (int)Math.ceil(bounds.getWidth());
                int h = (int)Math.ceil(bounds.getHeight());
                BufferedImage renderedPaint = new BufferedImage(w, h, 2);
                Graphics2D g = (Graphics2D)renderedPaint.getGraphics();
                g.translate(-bounds.getMinX(), -bounds.getMinY());
                g.setPaint(paint);
                g.setRenderingHints(this.graphics.getRenderingHints());
                g.fill(bounds);
                g.dispose();
                BufferedImage mask = pdImage.getImage();
                AffineTransform imageTransform = new AffineTransform(at);
                imageTransform.scale(1.0 / (double)mask.getWidth(), -1.0 / (double)mask.getHeight());
                imageTransform.translate(0.0, -mask.getHeight());
                AffineTransform full = new AffineTransform(g.getTransform());
                full.concatenate(imageTransform);
                Matrix m = new Matrix(full);
                double scaleX = Math.abs(m.getScalingFactorX());
                double scaleY = Math.abs(m.getScalingFactorY());
                boolean bl = smallMask = mask.getWidth() <= 8 && mask.getHeight() <= 8;
                if (!smallMask) {
                    BufferedImage tmp = new BufferedImage(mask.getWidth(), mask.getHeight(), 1);
                    mask = new LookupOp(this.getInvLookupTable(), this.graphics.getRenderingHints()).filter(mask, tmp);
                }
                BufferedImage renderedMask = new BufferedImage(w, h, 1);
                g = (Graphics2D)renderedMask.getGraphics();
                g.translate(-bounds.getMinX(), -bounds.getMinY());
                g.setRenderingHints(this.graphics.getRenderingHints());
                if (smallMask) {
                    g.drawImage(mask, imageTransform, null);
                } else {
                    while (scaleX < 0.25) {
                        scaleX *= 2.0;
                    }
                    while (scaleY < 0.25) {
                        scaleY *= 2.0;
                    }
                    int w2 = (int)Math.round((double)mask.getWidth() * scaleX);
                    int h2 = (int)Math.round((double)mask.getHeight() * scaleY);
                    Image scaledMask = mask.getScaledInstance(w2, h2, 4);
                    imageTransform.scale(1.0 / Math.abs(scaleX), 1.0 / Math.abs(scaleY));
                    g.drawImage(scaledMask, imageTransform, null);
                }
                g.dispose();
                int[] alphaPixel = null;
                int[] rasterPixel = null;
                WritableRaster raster = renderedPaint.getRaster();
                WritableRaster alpha = renderedMask.getRaster();
                for (int y = 0; y < h; ++y) {
                    for (int x = 0; x < w; ++x) {
                        alphaPixel = alpha.getPixel(x, y, alphaPixel);
                        rasterPixel = raster.getPixel(x, y, rasterPixel);
                        rasterPixel[3] = alphaPixel[0];
                        raster.setPixel(x, y, rasterPixel);
                    }
                }
                this.graphics.drawImage(renderedPaint, AffineTransform.getTranslateInstance(bounds.getMinX(), bounds.getMinY()), null);
            } else {
                BufferedImage image = pdImage.getStencilImage(this.getNonStrokingPaint());
                this.drawBufferedImage(image, at);
            }
        } else if (this.subsamplingAllowed) {
            int subsampling = this.getSubsampling(pdImage, at);
            this.drawBufferedImage(pdImage.getImage(null, subsampling), at);
        } else {
            this.drawBufferedImage(pdImage.getImage(), at);
        }
        if (!pdImage.getInterpolate()) {
            this.setRenderingHints();
        }
    }

    protected int getSubsampling(PDImage pdImage, AffineTransform at) {
        double scale = Math.abs(at.getDeterminant() * this.xform.getDeterminant());
        int subsampling = (int)Math.floor(Math.sqrt((double)(pdImage.getWidth() * pdImage.getHeight()) / scale));
        if (subsampling > 8) {
            subsampling = 8;
        }
        if (subsampling < 1) {
            subsampling = 1;
        }
        if (subsampling > pdImage.getWidth() || subsampling > pdImage.getHeight()) {
            subsampling = Math.min(pdImage.getWidth(), pdImage.getHeight());
        }
        return subsampling;
    }

    private void drawBufferedImage(BufferedImage image, AffineTransform at) throws IOException {
        AffineTransform originalTransform = this.graphics.getTransform();
        AffineTransform imageTransform = new AffineTransform(at);
        int width = image.getWidth();
        int height = image.getHeight();
        imageTransform.scale(1.0 / (double)width, -1.0 / (double)height);
        imageTransform.translate(0.0, -height);
        PDSoftMask softMask = this.getGraphicsState().getSoftMask();
        if (softMask != null) {
            Rectangle2D.Float rectangle = new Rectangle2D.Float(0.0f, 0.0f, width, height);
            Paint awtPaint = new TexturePaint(image, rectangle);
            awtPaint = this.applySoftMaskToPaint(awtPaint, softMask);
            this.graphics.setPaint(awtPaint);
            this.graphics.transform(imageTransform);
            this.graphics.fill(rectangle);
            this.graphics.setTransform(originalTransform);
        } else {
            COSBase transfer = this.getGraphicsState().getTransfer();
            if (transfer instanceof COSArray || transfer instanceof COSDictionary) {
                image = this.applyTransferFunction(image, transfer);
            }
            Matrix imageTransformMatrix = new Matrix(imageTransform);
            Matrix graphicsTransformMatrix = new Matrix(originalTransform);
            float scaleX = Math.abs(imageTransformMatrix.getScalingFactorX() * graphicsTransformMatrix.getScalingFactorX());
            float scaleY = Math.abs(imageTransformMatrix.getScalingFactorY() * graphicsTransformMatrix.getScalingFactorY());
            if ((scaleX < this.imageDownscalingOptimizationThreshold || scaleY < this.imageDownscalingOptimizationThreshold) && RenderingHints.VALUE_RENDER_QUALITY.equals(this.graphics.getRenderingHint(RenderingHints.KEY_RENDERING)) && RenderingHints.VALUE_INTERPOLATION_BICUBIC.equals(this.graphics.getRenderingHint(RenderingHints.KEY_INTERPOLATION))) {
                int w = Math.round((float)image.getWidth() * scaleX);
                int h = Math.round((float)image.getHeight() * scaleY);
                if (w < 1 || h < 1) {
                    this.graphics.drawImage(image, imageTransform, null);
                    return;
                }
                Image imageToDraw = image.getScaledInstance(w, h, 4);
                imageTransform.scale(1.0f / (float)w * (float)image.getWidth(), 1.0f / (float)h * (float)image.getHeight());
                imageTransform.preConcatenate(originalTransform);
                this.graphics.setTransform(new AffineTransform());
                this.graphics.drawImage(imageToDraw, imageTransform, null);
                this.graphics.setTransform(originalTransform);
            } else {
                GraphicsDevice graphicsDevice;
                GraphicsConfiguration graphicsConfiguration = this.graphics.getDeviceConfiguration();
                int deviceType = 0;
                if (graphicsConfiguration != null && (graphicsDevice = graphicsConfiguration.getDevice()) != null) {
                    deviceType = graphicsDevice.getType();
                }
                if (deviceType == 1 && image.getType() != 6 && (IS_WINDOWS || IS_LINUX)) {
                    BufferedImage bim = new BufferedImage(image.getWidth(), image.getHeight(), 6);
                    Graphics g = bim.getGraphics();
                    g.drawImage(image, 0, 0, null);
                    g.dispose();
                    image = bim;
                }
                this.graphics.drawImage(image, imageTransform, null);
            }
        }
    }

    private BufferedImage applyTransferFunction(BufferedImage image, COSBase transfer) throws IOException {
        Integer[] bMap;
        Integer[] gMap;
        Integer[] rMap;
        PDFunction bf;
        PDFunction gf;
        PDFunction rf;
        BufferedImage bim = image.getColorModel().hasAlpha() ? new BufferedImage(image.getWidth(), image.getHeight(), 2) : new BufferedImage(image.getWidth(), image.getHeight(), 1);
        if (transfer instanceof COSArray) {
            COSArray ar = (COSArray)transfer;
            rf = PDFunction.create(ar.getObject(0));
            gf = PDFunction.create(ar.getObject(1));
            bf = PDFunction.create(ar.getObject(2));
            rMap = new Integer[256];
            gMap = new Integer[256];
            bMap = new Integer[256];
        } else {
            gf = rf = PDFunction.create(transfer);
            bf = rf;
            gMap = rMap = new Integer[256];
            bMap = rMap;
        }
        float[] input = new float[1];
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int bo;
                int go;
                int ro;
                int rgb = image.getRGB(x, y);
                int ri = rgb >> 16 & 0xFF;
                int gi = rgb >> 8 & 0xFF;
                int bi = rgb & 0xFF;
                if (rMap[ri] != null) {
                    ro = rMap[ri];
                } else {
                    input[0] = (float)(ri & 0xFF) / 255.0f;
                    ro = (int)(rf.eval(input)[0] * 255.0f);
                    rMap[ri] = ro;
                }
                if (gMap[gi] != null) {
                    go = gMap[gi];
                } else {
                    input[0] = (float)(gi & 0xFF) / 255.0f;
                    go = (int)(gf.eval(input)[0] * 255.0f);
                    gMap[gi] = go;
                }
                if (bMap[bi] != null) {
                    bo = bMap[bi];
                } else {
                    input[0] = (float)(bi & 0xFF) / 255.0f;
                    bo = (int)(bf.eval(input)[0] * 255.0f);
                    bMap[bi] = bo;
                }
                bim.setRGB(x, y, rgb & 0xFF000000 | ro << 16 | go << 8 | bo);
            }
        }
        return bim;
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
        Area area;
        if (!this.isContentRendered()) {
            return;
        }
        PDShading shading = this.getResources().getShading(shadingName);
        if (shading == null) {
            LOG.error((Object)("shading " + shadingName + " does not exist in resources dictionary"));
            return;
        }
        Matrix ctm = this.getGraphicsState().getCurrentTransformationMatrix();
        this.graphics.setComposite(this.getGraphicsState().getNonStrokingJavaComposite());
        Shape savedClip = this.graphics.getClip();
        this.graphics.setClip(null);
        this.lastClips = null;
        PDRectangle bbox = shading.getBBox();
        if (bbox != null) {
            area = new Area(bbox.transform(ctm));
            area.intersect(this.getGraphicsState().getCurrentClippingPath());
        } else {
            Rectangle2D bounds = shading.getBounds(new AffineTransform(), ctm);
            if (bounds != null) {
                bounds.add(new Point2D.Double(Math.floor(bounds.getMinX() - 1.0), Math.floor(bounds.getMinY() - 1.0)));
                bounds.add(new Point2D.Double(Math.ceil(bounds.getMaxX() + 1.0), Math.ceil(bounds.getMaxY() + 1.0)));
                area = new Area(bounds);
                area.intersect(this.getGraphicsState().getCurrentClippingPath());
            } else {
                area = this.getGraphicsState().getCurrentClippingPath();
            }
        }
        if (!area.isEmpty()) {
            Paint paint = shading.toPaint(ctm);
            paint = this.applySoftMaskToPaint(paint, this.getGraphicsState().getSoftMask());
            this.graphics.setPaint(paint);
            this.graphics.fill(area);
        }
        this.graphics.setClip(savedClip);
    }

    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException {
        GraphicsDevice graphicsDevice;
        this.lastClips = null;
        int deviceType = -1;
        GraphicsConfiguration graphicsConfiguration = this.graphics.getDeviceConfiguration();
        if (graphicsConfiguration != null && (graphicsDevice = graphicsConfiguration.getDevice()) != null) {
            deviceType = graphicsDevice.getType();
        }
        if (deviceType == 1 && !annotation.isPrinted()) {
            return;
        }
        if (deviceType == 0 && annotation.isNoView()) {
            return;
        }
        if (annotation.isHidden()) {
            return;
        }
        if (annotation.isInvisible() && annotation instanceof PDAnnotationUnknown) {
            return;
        }
        if (this.isHiddenOCG(annotation.getOptionalContent())) {
            return;
        }
        PDAppearanceDictionary appearance = annotation.getAppearance();
        if (appearance == null || appearance.getNormalAppearance() == null) {
            annotation.constructAppearances(this.renderer.document);
        }
        if (annotation.isNoRotate() && this.getCurrentPage().getRotation() != 0) {
            PDRectangle rect = annotation.getRectangle();
            AffineTransform savedTransform = this.graphics.getTransform();
            this.graphics.rotate(Math.toRadians(this.getCurrentPage().getRotation()), rect.getLowerLeftX(), rect.getUpperRightY());
            super.showAnnotation(annotation);
            this.graphics.setTransform(savedTransform);
        } else {
            super.showAnnotation(annotation);
        }
    }

    @Override
    public void showForm(PDFormXObject form) throws IOException {
        if (this.isHiddenOCG(form.getOptionalContent())) {
            return;
        }
        if (this.isContentRendered()) {
            GeneralPath savedLinePath = this.linePath;
            this.linePath = new GeneralPath();
            super.showForm(form);
            this.linePath = savedLinePath;
        }
    }

    @Override
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException {
        this.showTransparencyGroupOnGraphics(form, this.graphics);
    }

    protected void showTransparencyGroupOnGraphics(PDTransparencyGroup form, Graphics2D graphics) throws IOException {
        if (this.isHiddenOCG(form.getOptionalContent())) {
            return;
        }
        if (!this.isContentRendered()) {
            return;
        }
        TransparencyGroup group = new TransparencyGroup(form, false, this.getGraphicsState().getCurrentTransformationMatrix(), null);
        BufferedImage image = group.getImage();
        if (image == null) {
            return;
        }
        graphics.setComposite(this.getGraphicsState().getNonStrokingJavaComposite());
        this.setClip();
        AffineTransform savedTransform = graphics.getTransform();
        AffineTransform transform = new AffineTransform(this.xform);
        transform.scale(1.0 / (double)this.xformScalingFactorX, 1.0 / (double)this.xformScalingFactorY);
        graphics.setTransform(transform);
        PDRectangle bbox = group.getBBox();
        float x = bbox.getLowerLeftX() - this.pageSize.getLowerLeftX();
        float y = this.pageSize.getUpperRightY() - bbox.getUpperRightY();
        if (this.flipTG) {
            graphics.translate(0, image.getHeight());
            graphics.scale(1.0, -1.0);
        } else {
            graphics.translate(x * this.xformScalingFactorX, y * this.xformScalingFactorY);
        }
        PDSoftMask softMask = this.getGraphicsState().getSoftMask();
        if (softMask != null) {
            Paint awtPaint = new TexturePaint(image, new Rectangle2D.Float(0.0f, 0.0f, image.getWidth(), image.getHeight()));
            awtPaint = this.applySoftMaskToPaint(awtPaint, softMask);
            graphics.setPaint(awtPaint);
            graphics.fill(new Rectangle2D.Float(0.0f, 0.0f, bbox.getWidth() * this.xformScalingFactorX, bbox.getHeight() * this.xformScalingFactorY));
        } else {
            try {
                graphics.drawImage(image, null, null);
            }
            catch (InternalError ie) {
                LOG.error((Object)"Exception drawing image, see JDK-6689349, try rendering into a BufferedImage instead", (Throwable)ie);
            }
        }
        graphics.setTransform(savedTransform);
    }

    private boolean hasBlendMode(PDTransparencyGroup group, Set<COSBase> groupsDone) {
        if (groupsDone.contains(group.getCOSObject())) {
            return false;
        }
        groupsDone.add(group.getCOSObject());
        PDResources resources = group.getResources();
        if (resources == null) {
            return false;
        }
        for (COSName name : resources.getExtGStateNames()) {
            BlendMode blendMode;
            PDExtendedGraphicsState extGState = resources.getExtGState(name);
            if (extGState == null || (blendMode = extGState.getBlendMode()) == BlendMode.NORMAL) continue;
            return true;
        }
        for (COSName name : resources.getXObjectNames()) {
            PDXObject xObject;
            try {
                xObject = resources.getXObject(name);
            }
            catch (IOException ex) {
                continue;
            }
            if (!(xObject instanceof PDTransparencyGroup) || !this.hasBlendMode((PDTransparencyGroup)xObject, groupsDone)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
        if (this.nestedHiddenOCGCount > 0) {
            ++this.nestedHiddenOCGCount;
            return;
        }
        if (tag == null || this.getResources() == null) {
            return;
        }
        if (this.isHiddenOCG(this.getResources().getProperties(tag))) {
            this.nestedHiddenOCGCount = 1;
        }
    }

    @Override
    public void endMarkedContentSequence() {
        if (this.nestedHiddenOCGCount > 0) {
            --this.nestedHiddenOCGCount;
        }
    }

    private boolean isContentRendered() {
        return this.nestedHiddenOCGCount <= 0;
    }

    private boolean isHiddenOCG(PDPropertyList propertyList) {
        if (propertyList instanceof PDOptionalContentGroup) {
            PDOptionalContentGroup group = (PDOptionalContentGroup)propertyList;
            PDOptionalContentGroup.RenderState printState = group.getRenderState(this.destination);
            if (printState == null ? !this.getRenderer().isGroupEnabled(group) : PDOptionalContentGroup.RenderState.OFF.equals((Object)printState)) {
                return true;
            }
        } else if (propertyList instanceof PDOptionalContentMembershipDictionary) {
            return this.isHiddenOCMD((PDOptionalContentMembershipDictionary)propertyList);
        }
        return false;
    }

    private boolean isHiddenOCMD(PDOptionalContentMembershipDictionary ocmd) {
        Iterator iterator;
        List<PDPropertyList> oCGs;
        if (ocmd.getCOSObject().getCOSArray(COSName.VE) != null) {
            LOG.info((Object)"/VE entry ignored in Optional Content Membership Dictionary");
        }
        if ((oCGs = ocmd.getOCGs()).isEmpty()) {
            return false;
        }
        ArrayList<Boolean> visibles = new ArrayList<Boolean>();
        for (PDPropertyList prop : oCGs) {
            visibles.add(!this.isHiddenOCG(prop));
        }
        COSName visibilityPolicy = ocmd.getVisibilityPolicy();
        if (COSName.ANY_OFF.equals(visibilityPolicy)) {
            iterator = visibles.iterator();
            while (iterator.hasNext()) {
                boolean visible = (Boolean)iterator.next();
                if (visible) continue;
                return false;
            }
            return true;
        }
        if (COSName.ALL_ON.equals(visibilityPolicy)) {
            iterator = visibles.iterator();
            while (iterator.hasNext()) {
                boolean visible = (Boolean)iterator.next();
                if (visible) continue;
                return true;
            }
            return false;
        }
        if (COSName.ALL_OFF.equals(visibilityPolicy)) {
            iterator = visibles.iterator();
            while (iterator.hasNext()) {
                boolean visible = (Boolean)iterator.next();
                if (!visible) continue;
                return true;
            }
            return false;
        }
        iterator = visibles.iterator();
        while (iterator.hasNext()) {
            boolean visible = (Boolean)iterator.next();
            if (!visible) continue;
            return false;
        }
        return true;
    }

    private LookupTable getInvLookupTable() {
        if (this.invTable == null) {
            byte[] inv = new byte[256];
            for (int i = 0; i < inv.length; ++i) {
                inv[i] = (byte)(255 - i);
            }
            this.invTable = new ByteLookupTable(0, inv);
        }
        return this.invTable;
    }

    private final class TransparencyGroup {
        private final BufferedImage image;
        private final PDRectangle bbox;
        private final int minX;
        private final int minY;
        private final int maxX;
        private final int maxY;
        private final int width;
        private final int height;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private TransparencyGroup(PDTransparencyGroup form, boolean isSoftMask, Matrix ctm, PDColor backdropColor) throws IOException {
            Graphics2D savedGraphics = PageDrawer.this.graphics;
            List savedLastClips = PageDrawer.this.lastClips;
            Shape savedInitialClip = PageDrawer.this.initialClip;
            Matrix transform = Matrix.concatenate(ctm, form.getMatrix());
            PDRectangle formBBox = form.getBBox();
            if (formBBox == null) {
                LOG.warn((Object)"transparency group ignored because BBox is null");
                formBBox = new PDRectangle();
            }
            GeneralPath transformedBox = formBBox.transform(transform);
            Area transformed = new Area(transformedBox);
            transformed.intersect(PageDrawer.this.getGraphicsState().getCurrentClippingPath());
            Rectangle2D clipRect = transformed.getBounds2D();
            if (clipRect.isEmpty()) {
                this.image = null;
                this.bbox = null;
                this.minX = 0;
                this.minY = 0;
                this.maxX = 0;
                this.maxY = 0;
                this.width = 0;
                this.height = 0;
                return;
            }
            this.bbox = new PDRectangle((float)clipRect.getX(), (float)clipRect.getY(), (float)clipRect.getWidth(), (float)clipRect.getHeight());
            AffineTransform xformOriginal = PageDrawer.this.xform;
            PageDrawer.this.xform = AffineTransform.getScaleInstance(PageDrawer.this.xformScalingFactorX, PageDrawer.this.xformScalingFactorY);
            Rectangle2D bounds = PageDrawer.this.xform.createTransformedShape(clipRect).getBounds2D();
            this.minX = (int)Math.floor(bounds.getMinX());
            this.minY = (int)Math.floor(bounds.getMinY());
            this.maxX = (int)Math.floor(bounds.getMaxX()) + 1;
            this.maxY = (int)Math.floor(bounds.getMaxY()) + 1;
            this.width = this.maxX - this.minX;
            this.height = this.maxY - this.minY;
            this.image = this.isGray(form.getGroup().getColorSpace(form.getResources())) ? this.create2ByteGrayAlphaImage(this.width, this.height) : new BufferedImage(this.width, this.height, 2);
            boolean needsBackdrop = !isSoftMask && !form.getGroup().isIsolated() && PageDrawer.this.hasBlendMode(form, new HashSet());
            BufferedImage backdropImage = null;
            int backdropX = 0;
            int backdropY = 0;
            if (needsBackdrop) {
                if (PageDrawer.this.transparencyGroupStack.isEmpty()) {
                    backdropImage = PageDrawer.this.renderer.getPageImage();
                    if (backdropImage == null) {
                        needsBackdrop = false;
                    } else {
                        backdropX = this.minX;
                        backdropY = backdropImage.getHeight() - this.maxY;
                    }
                } else {
                    TransparencyGroup parentGroup = (TransparencyGroup)PageDrawer.this.transparencyGroupStack.peek();
                    backdropImage = parentGroup.image;
                    backdropX = this.minX - parentGroup.minX;
                    backdropY = parentGroup.maxY - this.maxY;
                }
            }
            Graphics2D g = this.image.createGraphics();
            if (needsBackdrop) {
                g.drawImage(backdropImage, 0, 0, this.width, this.height, backdropX, backdropY, backdropX + this.width, backdropY + this.height, null);
                g = new GroupGraphics(this.image, g);
            }
            if (isSoftMask && backdropColor != null) {
                g.setBackground(new Color(backdropColor.toRGB()));
                g.clearRect(0, 0, this.width, this.height);
            }
            g.translate(0, this.image.getHeight());
            g.scale(1.0, -1.0);
            boolean savedFlipTG = PageDrawer.this.flipTG;
            PageDrawer.this.flipTG = false;
            g.transform(PageDrawer.this.xform);
            PDRectangle pageSizeOriginal = PageDrawer.this.pageSize;
            PageDrawer.this.pageSize = new PDRectangle((float)this.minX / PageDrawer.this.xformScalingFactorX, (float)this.minY / PageDrawer.this.xformScalingFactorY, (float)(bounds.getWidth() / (double)PageDrawer.this.xformScalingFactorX), (float)(bounds.getHeight() / (double)PageDrawer.this.xformScalingFactorY));
            int clipWindingRuleOriginal = PageDrawer.this.clipWindingRule;
            PageDrawer.this.clipWindingRule = -1;
            GeneralPath linePathOriginal = PageDrawer.this.linePath;
            PageDrawer.this.linePath = new GeneralPath();
            g.translate(-clipRect.getX(), -clipRect.getY());
            PageDrawer.this.graphics = g;
            PageDrawer.this.setRenderingHints();
            try {
                if (isSoftMask) {
                    PageDrawer.this.processSoftMask(form);
                } else {
                    PageDrawer.this.transparencyGroupStack.push(this);
                    PageDrawer.this.processTransparencyGroup(form);
                    if (!PageDrawer.this.transparencyGroupStack.isEmpty()) {
                        PageDrawer.this.transparencyGroupStack.pop();
                    }
                }
                if (needsBackdrop) {
                    ((GroupGraphics)PageDrawer.this.graphics).removeBackdrop(backdropImage, backdropX, backdropY);
                }
            }
            finally {
                PageDrawer.this.flipTG = savedFlipTG;
                PageDrawer.this.lastClips = savedLastClips;
                PageDrawer.this.graphics.dispose();
                PageDrawer.this.graphics = savedGraphics;
                PageDrawer.this.initialClip = savedInitialClip;
                PageDrawer.this.clipWindingRule = clipWindingRuleOriginal;
                PageDrawer.this.linePath = linePathOriginal;
                PageDrawer.this.pageSize = pageSizeOriginal;
                PageDrawer.this.xform = xformOriginal;
            }
        }

        private BufferedImage create2ByteGrayAlphaImage(int width, int height) {
            int[] bandOffsets = new int[]{1, 0};
            int bands = bandOffsets.length;
            ComponentColorModel CM_GRAY_ALPHA = new ComponentColorModel(ColorSpace.getInstance(1003), true, false, 3, 0);
            DataBufferByte buffer = new DataBufferByte(width * height * bands);
            WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, width * bands, bands, bandOffsets, new Point(0, 0));
            return new BufferedImage(CM_GRAY_ALPHA, raster, false, null);
        }

        private boolean isGray(PDColorSpace colorSpace) {
            if (colorSpace instanceof PDDeviceGray) {
                return true;
            }
            if (colorSpace instanceof PDICCBased) {
                try {
                    return ((PDICCBased)colorSpace).getAlternateColorSpace() instanceof PDDeviceGray;
                }
                catch (IOException ex) {
                    return false;
                }
            }
            return false;
        }

        BufferedImage getImage() {
            return this.image;
        }

        PDRectangle getBBox() {
            return this.bbox;
        }

        Rectangle2D getBounds() {
            Rectangle2D.Double r = new Rectangle2D.Double((float)this.minX - PageDrawer.this.pageSize.getLowerLeftX() * PageDrawer.this.xformScalingFactorX, (PageDrawer.this.pageSize.getLowerLeftY() + PageDrawer.this.pageSize.getHeight()) * PageDrawer.this.xformScalingFactorY - (float)this.minY - (float)this.height, this.width, this.height);
            AffineTransform adjustedTransform = new AffineTransform(PageDrawer.this.xform);
            adjustedTransform.scale(1.0 / (double)PageDrawer.this.xformScalingFactorX, 1.0 / (double)PageDrawer.this.xformScalingFactorY);
            return adjustedTransform.createTransformedShape(r).getBounds2D();
        }
    }
}

