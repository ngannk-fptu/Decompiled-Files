/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.util.Uu;

public class XHTMLPrintable
implements Printable {
    protected XHTMLPanel panel;
    protected Graphics2DRenderer g2r = null;

    public XHTMLPrintable(XHTMLPanel panel) {
        this.panel = panel;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) {
        try {
            Graphics2D g2 = (Graphics2D)g;
            if (this.g2r == null) {
                this.g2r = new Graphics2DRenderer();
                this.g2r.getSharedContext().setPrint(true);
                this.g2r.getSharedContext().setInteractive(false);
                this.g2r.getSharedContext().setDPI(72.0f);
                this.g2r.getSharedContext().getTextRenderer().setSmoothingThreshold(0.0f);
                this.g2r.getSharedContext().setUserAgentCallback(this.panel.getSharedContext().getUserAgentCallback());
                this.g2r.setDocument(this.panel.getDocument(), this.panel.getSharedContext().getUac().getBaseURL());
                this.g2r.getSharedContext().setReplacedElementFactory(this.panel.getSharedContext().getReplacedElementFactory());
                this.g2r.layout(g2, null);
                this.g2r.getPanel().assignPagePrintPositions(g2);
            }
            if (page >= this.g2r.getPanel().getRootLayer().getPages().size()) {
                return 1;
            }
            this.g2r.getPanel().paintPage(g2, page);
            return 0;
        }
        catch (Exception ex) {
            Uu.p(ex);
            return 1;
        }
    }
}

