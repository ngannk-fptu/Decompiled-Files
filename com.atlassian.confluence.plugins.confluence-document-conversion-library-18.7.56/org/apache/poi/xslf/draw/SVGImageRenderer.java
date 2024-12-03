/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SAXSVGDocumentFactory
 *  org.apache.batik.bridge.BridgeContext
 *  org.apache.batik.bridge.DocumentLoader
 *  org.apache.batik.bridge.GVTBuilder
 *  org.apache.batik.bridge.UserAgent
 *  org.apache.batik.ext.awt.RenderingHintsKeyExt
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.ext.awt.image.renderable.ClipRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.util.XMLResourceDescriptor
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.poi.xslf.draw;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.draw.SVGUserAgent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

public class SVGImageRenderer
implements ImageRenderer {
    private final SAXSVGDocumentFactory svgFact;
    private final GVTBuilder builder = new GVTBuilder();
    private final BridgeContext context;
    private double alpha = 1.0;

    public SVGImageRenderer() {
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        this.svgFact = new SAXSVGDocumentFactory(parser);
        SVGUserAgent agent = new SVGUserAgent();
        DocumentLoader loader = new DocumentLoader((UserAgent)agent);
        this.context = new BridgeContext((UserAgent)agent, loader);
        this.context.setDynamic(true);
    }

    @Override
    public void loadImage(InputStream data, String contentType) throws IOException {
        SVGDocument document = (SVGDocument)this.svgFact.createDocument("", data);
        ((SVGUserAgent)this.context.getUserAgent()).initViewbox(document);
        this.builder.build(this.context, (Document)document);
    }

    @Override
    public void loadImage(byte[] data, String contentType) throws IOException {
        this.loadImage((InputStream)new UnsynchronizedByteArrayInputStream(data), contentType);
    }

    @Override
    public Rectangle2D getBounds() {
        return ((SVGUserAgent)this.context.getUserAgent()).getViewbox();
    }

    @Override
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    @Override
    public BufferedImage getImage() {
        return this.getImage(this.getDimension());
    }

    @Override
    public BufferedImage getImage(Dimension2D dim) {
        BufferedImage bi = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), 2);
        Graphics2D g2d = (Graphics2D)bi.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, new WeakReference<BufferedImage>(bi));
        Dimension2D dimSVG = this.getDimension();
        double scaleX = dim.getWidth() / dimSVG.getWidth();
        double scaleY = dim.getHeight() / dimSVG.getHeight();
        g2d.scale(scaleX, scaleY);
        this.getSVGRoot().paint(g2d);
        g2d.dispose();
        return bi;
    }

    @Override
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor) {
        return this.drawImage(graphics, anchor, null);
    }

    @Override
    public boolean drawImage(Graphics2D graphics, Rectangle2D anchor, Insets clip) {
        graphics.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, graphics.getRenderingHint(Drawable.BUFFERED_IMAGE));
        GraphicsNode svgRoot = this.getSVGRoot();
        Dimension2D bounds = this.getDimension();
        AffineTransform at = new AffineTransform();
        at.translate(anchor.getX(), anchor.getY());
        at.scale(anchor.getWidth() / bounds.getWidth(), anchor.getHeight() / bounds.getHeight());
        svgRoot.setTransform(at);
        if (clip == null) {
            svgRoot.setClip(null);
        } else {
            Rectangle2D.Double clippedRect = new Rectangle2D.Double(anchor.getX() + (double)clip.left, anchor.getY() + (double)clip.top, anchor.getWidth() - (double)(clip.left + clip.right), anchor.getHeight() - (double)(clip.top + clip.bottom));
            svgRoot.setClip((ClipRable)new ClipRable8Bit(null, (Shape)clippedRect));
        }
        svgRoot.paint(graphics);
        return true;
    }

    @Override
    public boolean canRender(String contentType) {
        return PictureData.PictureType.SVG.contentType.equalsIgnoreCase(contentType);
    }

    @Override
    public Rectangle2D getNativeBounds() {
        return this.getSVGRoot().getPrimitiveBounds();
    }

    public GraphicsNode getSVGRoot() {
        return this.context.getGraphicsNode((Node)this.context.getDocument());
    }
}

