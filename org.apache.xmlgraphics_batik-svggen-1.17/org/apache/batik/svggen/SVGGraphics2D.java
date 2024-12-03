/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.AbstractGraphics2D
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.AttributedCharacterIterator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.batik.ext.awt.g2d.AbstractGraphics2D;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.DefaultErrorHandler;
import org.apache.batik.svggen.DefaultStyleHandler;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.ExtensionHandler;
import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.ImageHandler;
import org.apache.batik.svggen.SVGCSSStyler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicContext;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.svggen.SVGIDGenerator;
import org.apache.batik.svggen.SVGShape;
import org.apache.batik.svggen.SVGSyntax;
import org.apache.batik.svggen.XmlWriter;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGGraphics2D
extends AbstractGraphics2D
implements Cloneable,
SVGSyntax,
ErrorConstants {
    public static final String DEFAULT_XML_ENCODING = "ISO-8859-1";
    public static final int DEFAULT_MAX_GC_OVERRIDES = 3;
    protected DOMTreeManager domTreeManager;
    protected DOMGroupManager domGroupManager;
    protected SVGGeneratorContext generatorCtx;
    protected SVGShape shapeConverter;
    protected Dimension svgCanvasSize;
    protected Graphics2D fmg;
    protected Set unsupportedAttributes;

    public final Dimension getSVGCanvasSize() {
        return this.svgCanvasSize;
    }

    public final void setSVGCanvasSize(Dimension svgCanvasSize) {
        this.svgCanvasSize = new Dimension(svgCanvasSize);
    }

    public final SVGGeneratorContext getGeneratorContext() {
        return this.generatorCtx;
    }

    public final SVGShape getShapeConverter() {
        return this.shapeConverter;
    }

    public final DOMTreeManager getDOMTreeManager() {
        return this.domTreeManager;
    }

    protected final void setDOMTreeManager(DOMTreeManager treeMgr) {
        this.domTreeManager = treeMgr;
        this.generatorCtx.genericImageHandler.setDOMTreeManager(this.domTreeManager);
    }

    protected final DOMGroupManager getDOMGroupManager() {
        return this.domGroupManager;
    }

    protected final void setDOMGroupManager(DOMGroupManager groupMgr) {
        this.domGroupManager = groupMgr;
    }

    public final Document getDOMFactory() {
        return this.generatorCtx.domFactory;
    }

    public final ImageHandler getImageHandler() {
        return this.generatorCtx.imageHandler;
    }

    public final GenericImageHandler getGenericImageHandler() {
        return this.generatorCtx.genericImageHandler;
    }

    public final ExtensionHandler getExtensionHandler() {
        return this.generatorCtx.extensionHandler;
    }

    public final void setExtensionHandler(ExtensionHandler extensionHandler) {
        this.generatorCtx.setExtensionHandler(extensionHandler);
    }

    public SVGGraphics2D(Document domFactory) {
        this(SVGGeneratorContext.createDefault(domFactory), false);
    }

    public SVGGraphics2D(Document domFactory, ImageHandler imageHandler, ExtensionHandler extensionHandler, boolean textAsShapes) {
        this(SVGGraphics2D.buildSVGGeneratorContext(domFactory, imageHandler, extensionHandler), textAsShapes);
    }

    public static SVGGeneratorContext buildSVGGeneratorContext(Document domFactory, ImageHandler imageHandler, ExtensionHandler extensionHandler) {
        SVGGeneratorContext generatorCtx = new SVGGeneratorContext(domFactory);
        generatorCtx.setIDGenerator(new SVGIDGenerator());
        generatorCtx.setExtensionHandler(extensionHandler);
        generatorCtx.setImageHandler(imageHandler);
        generatorCtx.setStyleHandler(new DefaultStyleHandler());
        generatorCtx.setComment("Generated by the Batik Graphics2D SVG Generator");
        generatorCtx.setErrorHandler(new DefaultErrorHandler());
        return generatorCtx;
    }

    public SVGGraphics2D(SVGGeneratorContext generatorCtx, boolean textAsShapes) {
        super(textAsShapes);
        BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
        this.unsupportedAttributes = new HashSet();
        this.unsupportedAttributes.add(TextAttribute.BACKGROUND);
        this.unsupportedAttributes.add(TextAttribute.BIDI_EMBEDDING);
        this.unsupportedAttributes.add(TextAttribute.CHAR_REPLACEMENT);
        this.unsupportedAttributes.add(TextAttribute.JUSTIFICATION);
        this.unsupportedAttributes.add(TextAttribute.RUN_DIRECTION);
        this.unsupportedAttributes.add(TextAttribute.SUPERSCRIPT);
        this.unsupportedAttributes.add(TextAttribute.SWAP_COLORS);
        this.unsupportedAttributes.add(TextAttribute.TRANSFORM);
        this.unsupportedAttributes.add(TextAttribute.WIDTH);
        if (generatorCtx == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.setGeneratorContext(generatorCtx);
    }

    protected void setGeneratorContext(SVGGeneratorContext generatorCtx) {
        this.generatorCtx = generatorCtx;
        this.gc = new GraphicContext(new AffineTransform());
        SVGGeneratorContext.GraphicContextDefaults gcDefaults = generatorCtx.getGraphicContextDefaults();
        if (gcDefaults != null) {
            if (gcDefaults.getPaint() != null) {
                this.gc.setPaint(gcDefaults.getPaint());
            }
            if (gcDefaults.getStroke() != null) {
                this.gc.setStroke(gcDefaults.getStroke());
            }
            if (gcDefaults.getComposite() != null) {
                this.gc.setComposite(gcDefaults.getComposite());
            }
            if (gcDefaults.getClip() != null) {
                this.gc.setClip(gcDefaults.getClip());
            }
            if (gcDefaults.getRenderingHints() != null) {
                this.gc.setRenderingHints((Map)gcDefaults.getRenderingHints());
            }
            if (gcDefaults.getFont() != null) {
                this.gc.setFont(gcDefaults.getFont());
            }
            if (gcDefaults.getBackground() != null) {
                this.gc.setBackground(gcDefaults.getBackground());
            }
        }
        this.shapeConverter = new SVGShape(generatorCtx);
        this.domTreeManager = new DOMTreeManager(this.gc, generatorCtx, 3);
        this.domGroupManager = new DOMGroupManager(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
        generatorCtx.genericImageHandler.setDOMTreeManager(this.domTreeManager);
    }

    public SVGGraphics2D(SVGGraphics2D g) {
        super((AbstractGraphics2D)g);
        BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
        this.unsupportedAttributes = new HashSet();
        this.unsupportedAttributes.add(TextAttribute.BACKGROUND);
        this.unsupportedAttributes.add(TextAttribute.BIDI_EMBEDDING);
        this.unsupportedAttributes.add(TextAttribute.CHAR_REPLACEMENT);
        this.unsupportedAttributes.add(TextAttribute.JUSTIFICATION);
        this.unsupportedAttributes.add(TextAttribute.RUN_DIRECTION);
        this.unsupportedAttributes.add(TextAttribute.SUPERSCRIPT);
        this.unsupportedAttributes.add(TextAttribute.SWAP_COLORS);
        this.unsupportedAttributes.add(TextAttribute.TRANSFORM);
        this.unsupportedAttributes.add(TextAttribute.WIDTH);
        this.generatorCtx = g.generatorCtx;
        this.gc.validateTransformStack();
        this.shapeConverter = g.shapeConverter;
        this.domTreeManager = g.domTreeManager;
        this.domGroupManager = new DOMGroupManager(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
    }

    public void stream(String svgFileName) throws SVGGraphics2DIOException {
        this.stream(svgFileName, false);
    }

    public void stream(String svgFileName, boolean useCss) throws SVGGraphics2DIOException {
        try {
            OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(svgFileName), DEFAULT_XML_ENCODING);
            this.stream(writer, useCss);
            writer.flush();
            writer.close();
        }
        catch (SVGGraphics2DIOException io) {
            throw io;
        }
        catch (IOException e) {
            this.generatorCtx.errorHandler.handleError(new SVGGraphics2DIOException(e));
        }
    }

    public void stream(Writer writer) throws SVGGraphics2DIOException {
        this.stream(writer, false);
    }

    public void stream(Writer writer, boolean useCss, boolean escaped) throws SVGGraphics2DIOException {
        Element svgRoot = this.getRoot();
        this.stream(svgRoot, writer, useCss, escaped);
    }

    public void stream(Writer writer, boolean useCss) throws SVGGraphics2DIOException {
        Element svgRoot = this.getRoot();
        this.stream(svgRoot, writer, useCss, false);
    }

    public void stream(Element svgRoot, Writer writer) throws SVGGraphics2DIOException {
        this.stream(svgRoot, writer, false, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stream(Element svgRoot, Writer writer, boolean useCss, boolean escaped) throws SVGGraphics2DIOException {
        Node rootParent = svgRoot.getParentNode();
        Node nextSibling = svgRoot.getNextSibling();
        try {
            svgRoot.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/svg");
            svgRoot.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
            DocumentFragment svgDocument = svgRoot.getOwnerDocument().createDocumentFragment();
            svgDocument.appendChild(svgRoot);
            if (useCss) {
                SVGCSSStyler.style(svgDocument);
            }
            XmlWriter.writeXml(svgDocument, writer, escaped);
            writer.flush();
        }
        catch (SVGGraphics2DIOException e) {
            this.generatorCtx.errorHandler.handleError(e);
        }
        catch (IOException io) {
            this.generatorCtx.errorHandler.handleError(new SVGGraphics2DIOException(io));
        }
        finally {
            if (rootParent != null) {
                if (nextSibling == null) {
                    rootParent.appendChild(svgRoot);
                } else {
                    rootParent.insertBefore(svgRoot, nextSibling);
                }
            }
        }
    }

    public List getDefinitionSet() {
        return this.domTreeManager.getDefinitionSet();
    }

    public Element getTopLevelGroup() {
        return this.getTopLevelGroup(true);
    }

    public Element getTopLevelGroup(boolean includeDefinitionSet) {
        return this.domTreeManager.getTopLevelGroup(includeDefinitionSet);
    }

    public void setTopLevelGroup(Element topLevelGroup) {
        this.domTreeManager.setTopLevelGroup(topLevelGroup);
    }

    public Element getRoot() {
        return this.getRoot(null);
    }

    public Element getRoot(Element svgRoot) {
        svgRoot = this.domTreeManager.getRoot(svgRoot);
        if (this.svgCanvasSize != null) {
            svgRoot.setAttributeNS(null, "width", String.valueOf(this.svgCanvasSize.width));
            svgRoot.setAttributeNS(null, "height", String.valueOf(this.svgCanvasSize.height));
        }
        return svgRoot;
    }

    public Graphics create() {
        return new SVGGraphics2D(this);
    }

    public void setXORMode(Color c1) {
        this.generatorCtx.errorHandler.handleError(new SVGGraphics2DRuntimeException("XOR Mode is not supported by Graphics2D SVG Generator"));
    }

    public FontMetrics getFontMetrics(Font f) {
        return this.fmg.getFontMetrics(f);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        Element imageElement = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        AffineTransform xform = this.getGenericImageHandler().handleImage(img, imageElement, x, y, img.getWidth(null), img.getHeight(null), this.getGeneratorContext());
        if (xform == null) {
            this.domGroupManager.addElement(imageElement);
        } else {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(imageElement);
            this.gc.transform(inverseTransform);
        }
        return true;
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        Element imageElement = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        AffineTransform xform = this.getGenericImageHandler().handleImage(img, imageElement, x, y, width, height, this.getGeneratorContext());
        if (xform == null) {
            this.domGroupManager.addElement(imageElement);
        } else {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(imageElement);
            this.gc.transform(inverseTransform);
        }
        return true;
    }

    public void dispose() {
        this.domTreeManager.removeGroupManager(this.domGroupManager);
    }

    public void draw(Shape s) {
        Stroke stroke = this.gc.getStroke();
        if (stroke instanceof BasicStroke) {
            Element svgShape = this.shapeConverter.toSVG(s);
            if (svgShape != null) {
                this.domGroupManager.addElement(svgShape, (short)1);
            }
        } else {
            Shape strokedShape = stroke.createStrokedShape(s);
            this.fill(strokedShape);
        }
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        boolean retVal = true;
        if (xform == null) {
            retVal = this.drawImage(img, 0, 0, null);
        } else if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.transform(inverseTransform);
        } else {
            AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.setTransform(savTransform);
        }
        return retVal;
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        img = op.filter(img, null);
        this.drawImage((Image)img, x, y, null);
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform trans2) {
        AffineTransform xform;
        Element image = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        AffineTransform trans1 = this.getGenericImageHandler().handleImage(img, image, img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight(), this.getGeneratorContext());
        if (trans2 == null) {
            xform = trans1;
        } else if (trans1 == null) {
            xform = trans2;
        } else {
            xform = new AffineTransform(trans2);
            xform.concatenate(trans1);
        }
        if (xform == null) {
            this.domGroupManager.addElement(image);
        } else if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.transform(inverseTransform);
        } else {
            AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.setTransform(savTransform);
        }
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform trans2) {
        AffineTransform xform;
        Element image = this.getGenericImageHandler().createElement(this.getGeneratorContext());
        AffineTransform trans1 = this.getGenericImageHandler().handleImage(img, image, img.getMinX(), img.getMinY(), img.getWidth(), img.getHeight(), this.getGeneratorContext());
        if (trans2 == null) {
            xform = trans1;
        } else if (trans1 == null) {
            xform = trans2;
        } else {
            xform = new AffineTransform(trans2);
            xform.concatenate(trans1);
        }
        if (xform == null) {
            this.domGroupManager.addElement(image);
        } else if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new SVGGraphics2DRuntimeException("unexpected exception");
            }
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.transform(inverseTransform);
        } else {
            AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            this.domGroupManager.addElement(image);
            this.gc.setTransform(savTransform);
        }
    }

    public void drawString(String s, float x, float y) {
        if (this.textAsShapes) {
            GlyphVector gv = this.getFont().createGlyphVector(this.getFontRenderContext(), s);
            this.drawGlyphVector(gv, x, y);
            return;
        }
        if (this.generatorCtx.svgFont) {
            this.domTreeManager.gcConverter.getFontConverter().recordFontUsage(s, this.getFont());
        }
        AffineTransform savTxf = this.getTransform();
        AffineTransform txtTxf = this.transformText(x, y);
        Element text = this.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttributeNS(null, "x", this.generatorCtx.doubleString(x));
        text.setAttributeNS(null, "y", this.generatorCtx.doubleString(y));
        text.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
        text.appendChild(this.getDOMFactory().createTextNode(s));
        this.domGroupManager.addElement(text, (short)16);
        if (txtTxf != null) {
            this.setTransform(savTxf);
        }
    }

    private AffineTransform transformText(float x, float y) {
        AffineTransform txtTxf = null;
        Font font = this.getFont();
        if (font != null) {
            txtTxf = font.getTransform();
            if (txtTxf != null && !txtTxf.isIdentity()) {
                AffineTransform t = new AffineTransform();
                t.translate(x, y);
                t.concatenate(txtTxf);
                t.translate(-x, -y);
                this.transform(t);
            } else {
                txtTxf = null;
            }
        }
        return txtTxf;
    }

    public void drawString(AttributedCharacterIterator ati, float x, float y) {
        if (this.textAsShapes || this.usesUnsupportedAttributes(ati)) {
            TextLayout layout = new TextLayout(ati, this.getFontRenderContext());
            layout.draw((Graphics2D)((Object)this), x, y);
            return;
        }
        boolean multiSpans = false;
        if (ati.getRunLimit() < ati.getEndIndex()) {
            multiSpans = true;
        }
        Element text = this.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "text");
        text.setAttributeNS(null, "x", this.generatorCtx.doubleString(x));
        text.setAttributeNS(null, "y", this.generatorCtx.doubleString(y));
        text.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", "preserve");
        Font baseFont = this.getFont();
        Paint basePaint = this.getPaint();
        char ch = ati.first();
        this.setTextElementFill(ati);
        this.setTextFontAttributes(ati, baseFont);
        SVGGraphicContext textGC = this.domTreeManager.getGraphicContextConverter().toSVG(this.gc);
        this.domGroupManager.addElement(text, (short)16);
        textGC.getContext().put("stroke", "none");
        textGC.getGroupContext().put("stroke", "none");
        boolean firstSpan = true;
        AffineTransform savTxf = this.getTransform();
        AffineTransform txtTxf = null;
        while (ch != '\uffff') {
            Element tspan = text;
            if (multiSpans) {
                tspan = this.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "tspan");
                text.appendChild(tspan);
            }
            this.setTextElementFill(ati);
            boolean resetTransform = this.setTextFontAttributes(ati, baseFont);
            if (resetTransform || firstSpan) {
                txtTxf = this.transformText(x, y);
                firstSpan = false;
            }
            int start = ati.getIndex();
            int end = ati.getRunLimit() - 1;
            StringBuffer buf = new StringBuffer(end - start);
            buf.append(ch);
            for (int i = start; i < end; ++i) {
                ch = ati.next();
                buf.append(ch);
            }
            String s = buf.toString();
            if (this.generatorCtx.isEmbeddedFontsOn()) {
                this.getDOMTreeManager().getGraphicContextConverter().getFontConverter().recordFontUsage(s, this.getFont());
            }
            SVGGraphicContext elementGC = this.domTreeManager.gcConverter.toSVG(this.gc);
            elementGC.getGroupContext().put("stroke", "none");
            SVGGraphicContext deltaGC = DOMGroupManager.processDeltaGC(elementGC, textGC);
            this.setTextElementAttributes(deltaGC, ati);
            this.domTreeManager.getStyleHandler().setStyle(tspan, deltaGC.getContext(), this.domTreeManager.getGeneratorContext());
            tspan.appendChild(this.getDOMFactory().createTextNode(s));
            if ((resetTransform || firstSpan) && txtTxf != null) {
                this.setTransform(savTxf);
            }
            ch = ati.next();
        }
        this.setFont(baseFont);
        this.setPaint(basePaint);
    }

    public void fill(Shape s) {
        Element svgShape = this.shapeConverter.toSVG(s);
        if (svgShape != null) {
            this.domGroupManager.addElement(svgShape, (short)16);
        }
    }

    private boolean setTextFontAttributes(AttributedCharacterIterator ati, Font baseFont) {
        boolean resetTransform = false;
        if (ati.getAttribute(TextAttribute.FONT) != null || ati.getAttribute(TextAttribute.FAMILY) != null || ati.getAttribute(TextAttribute.WEIGHT) != null || ati.getAttribute(TextAttribute.POSTURE) != null || ati.getAttribute(TextAttribute.SIZE) != null) {
            Map<AttributedCharacterIterator.Attribute, Object> m = ati.getAttributes();
            Font f = baseFont.deriveFont(m);
            this.setFont(f);
            resetTransform = true;
        }
        return resetTransform;
    }

    private void setTextElementFill(AttributedCharacterIterator ati) {
        if (ati.getAttribute(TextAttribute.FOREGROUND) != null) {
            Color color = (Color)ati.getAttribute(TextAttribute.FOREGROUND);
            this.setPaint(color);
        }
    }

    private void setTextElementAttributes(SVGGraphicContext tspanGC, AttributedCharacterIterator ati) {
        int len;
        String decoration = "";
        if (this.isUnderline(ati)) {
            decoration = decoration + "underline ";
        }
        if (this.isStrikeThrough(ati)) {
            decoration = decoration + "line-through ";
        }
        if ((len = decoration.length()) != 0) {
            tspanGC.getContext().put("text-decoration", decoration.substring(0, len - 1));
        }
    }

    private boolean isBold(AttributedCharacterIterator ati) {
        Object weight = ati.getAttribute(TextAttribute.WEIGHT);
        if (weight == null) {
            return false;
        }
        if (weight.equals(TextAttribute.WEIGHT_REGULAR)) {
            return false;
        }
        if (weight.equals(TextAttribute.WEIGHT_DEMILIGHT)) {
            return false;
        }
        if (weight.equals(TextAttribute.WEIGHT_EXTRA_LIGHT)) {
            return false;
        }
        return !weight.equals(TextAttribute.WEIGHT_LIGHT);
    }

    private boolean isItalic(AttributedCharacterIterator ati) {
        Object attr = ati.getAttribute(TextAttribute.POSTURE);
        return TextAttribute.POSTURE_OBLIQUE.equals(attr);
    }

    private boolean isUnderline(AttributedCharacterIterator ati) {
        Object attr = ati.getAttribute(TextAttribute.UNDERLINE);
        return TextAttribute.UNDERLINE_ON.equals(attr);
    }

    private boolean isStrikeThrough(AttributedCharacterIterator ati) {
        Object attr = ati.getAttribute(TextAttribute.STRIKETHROUGH);
        return TextAttribute.STRIKETHROUGH_ON.equals(attr);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }

    public void setUnsupportedAttributes(Set attrs) {
        this.unsupportedAttributes = attrs == null ? null : new HashSet(attrs);
    }

    public boolean usesUnsupportedAttributes(AttributedCharacterIterator aci) {
        if (this.unsupportedAttributes == null) {
            return false;
        }
        Set<AttributedCharacterIterator.Attribute> allAttrs = aci.getAllAttributeKeys();
        for (AttributedCharacterIterator.Attribute allAttr : allAttrs) {
            if (!this.unsupportedAttributes.contains(allAttr)) continue;
            return true;
        }
        return false;
    }
}

