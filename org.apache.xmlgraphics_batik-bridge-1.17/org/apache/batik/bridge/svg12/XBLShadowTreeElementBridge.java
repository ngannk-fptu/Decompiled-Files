/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge.svg12;

import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class XBLShadowTreeElementBridge
extends AbstractGraphicsNodeBridge {
    @Override
    public String getLocalName() {
        return "shadowTree";
    }

    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2004/xbl";
    }

    @Override
    public Bridge getInstance() {
        return new XBLShadowTreeElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        CompositeGraphicsNode cgn = new CompositeGraphicsNode();
        this.associateSVGContext(ctx, e, (GraphicsNode)cgn);
        return cgn;
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return null;
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        this.initializeDynamicSupport(ctx, e, node);
    }

    @Override
    public boolean getDisplay(Element e) {
        return true;
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        if (evt.getTarget() instanceof Element) {
            this.handleElementAdded((CompositeGraphicsNode)this.node, this.e, (Element)((Object)evt.getTarget()));
        }
    }

    public void handleElementAdded(CompositeGraphicsNode gn, Node parent, Element childElt) {
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

