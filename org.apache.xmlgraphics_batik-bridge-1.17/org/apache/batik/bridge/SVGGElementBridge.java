/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class SVGGElementBridge
extends AbstractGraphicsNodeBridge {
    @Override
    public String getLocalName() {
        return "g";
    }

    @Override
    public Bridge getInstance() {
        return new SVGGElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        Rectangle2D r;
        CompositeGraphicsNode gn = (CompositeGraphicsNode)super.createGraphicsNode(ctx, e);
        if (gn == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, (GraphicsNode)gn);
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        if (hints != null) {
            gn.setRenderingHints(hints);
        }
        if ((r = CSSUtilities.convertEnableBackground(e)) != null) {
            gn.setBackgroundEnable(r);
        }
        return gn;
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new CompositeGraphicsNode();
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        if (evt.getTarget() instanceof Element) {
            this.handleElementAdded((CompositeGraphicsNode)this.node, this.e, (Element)((Object)evt.getTarget()));
        } else {
            super.handleDOMNodeInsertedEvent(evt);
        }
    }

    protected void handleElementAdded(CompositeGraphicsNode gn, Node parent, Element childElt) {
        GVTBuilder builder = this.ctx.getGVTBuilder();
        GraphicsNode childNode = builder.build(this.ctx, childElt);
        if (childNode == null) {
            return;
        }
        int idx = -1;
        for (Node ps = childElt.getPreviousSibling(); ps != null; ps = ps.getPreviousSibling()) {
            GraphicsNode psgn;
            if (ps.getNodeType() != 1) continue;
            Element pse = (Element)ps;
            for (psgn = this.ctx.getGraphicsNode(pse); psgn != null && psgn.getParent() != gn; psgn = psgn.getParent()) {
            }
            if (psgn != null && (idx = gn.indexOf((Object)psgn)) != -1) break;
        }
        gn.add(++idx, (Object)childNode);
    }
}

