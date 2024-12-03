/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import org.w3c.dom.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.simple.XHTMLPanel;

public class Graphics2DRenderer {
    protected XHTMLPanel panel = new XHTMLPanel();
    protected Dimension dim;

    public Graphics2DRenderer() {
        this.panel.setInteractive(false);
    }

    public void layout(Graphics2D g2, Dimension dim) {
        this.dim = dim;
        if (dim != null) {
            this.panel.setSize(dim);
        }
        this.panel.doDocumentLayout(g2);
    }

    public void render(Graphics2D g2) {
        if (g2.getClip() == null) {
            g2.setClip(this.getMinimumSize());
        }
        this.panel.paintComponent(g2);
    }

    public void setDocument(String url) {
        this.panel.setDocument(url);
    }

    public void setDocument(Document doc, String base_url) {
        this.panel.setDocument(doc, base_url);
    }

    public void setSharedContext(SharedContext ctx) {
        this.panel.setSharedContext(ctx);
    }

    public Rectangle getMinimumSize() {
        if (this.panel.getPreferredSize() != null) {
            return new Rectangle(0, 0, (int)this.panel.getPreferredSize().getWidth(), (int)this.panel.getPreferredSize().getHeight());
        }
        return new Rectangle(0, 0, this.panel.getWidth(), this.panel.getHeight());
    }

    public SharedContext getSharedContext() {
        return this.panel.getSharedContext();
    }

    public XHTMLPanel getPanel() {
        return this.panel;
    }

    public static BufferedImage renderToImage(String url, int width, int height) {
        return Graphics2DRenderer.renderToImage(url, width, height, 2);
    }

    public static BufferedImage renderToImage(String url, int width, int height, int bufferedImageType) {
        Graphics2DRenderer g2r = new Graphics2DRenderer();
        g2r.setDocument(url);
        Dimension dim = new Dimension(width, height);
        BufferedImage buff = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), bufferedImageType);
        Graphics2D g = (Graphics2D)buff.getGraphics();
        g2r.layout(g, dim);
        g2r.render(g);
        g.dispose();
        return buff;
    }

    public static BufferedImage renderToImageAutoSize(String url, int width) {
        return Graphics2DRenderer.renderToImageAutoSize(url, width, 2);
    }

    public static BufferedImage renderToImageAutoSize(String url, int width, int bufferedImageType) {
        Graphics2DRenderer g2r = new Graphics2DRenderer();
        g2r.setDocument(url);
        Dimension dim = new Dimension(width, 1000);
        BufferedImage buff = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), bufferedImageType);
        Graphics2D g = (Graphics2D)buff.getGraphics();
        g2r.layout(g, new Dimension(width, 1000));
        g.dispose();
        Rectangle rect = g2r.getMinimumSize();
        buff = new BufferedImage((int)rect.getWidth(), (int)rect.getHeight(), bufferedImageType);
        g = (Graphics2D)buff.getGraphics();
        g2r.render(g);
        g.dispose();
        return buff;
    }
}

