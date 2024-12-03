/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.w3c.dom.svg.SVGTests
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.SVGGElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGTests;

public class SVGSwitchElementBridge
extends SVGGElementBridge {
    protected Element selectedChild;

    @Override
    public String getLocalName() {
        return "switch";
    }

    @Override
    public Bridge getInstance() {
        return new SVGSwitchElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        GraphicsNode refNode = null;
        GVTBuilder builder = ctx.getGVTBuilder();
        this.selectedChild = null;
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            Element ref = (Element)n;
            if (!(n instanceof SVGTests) || !SVGUtilities.matchUserAgent(ref, ctx.getUserAgent())) continue;
            this.selectedChild = ref;
            refNode = builder.build(ctx, ref);
            break;
        }
        if (refNode == null) {
            return null;
        }
        CompositeGraphicsNode group = (CompositeGraphicsNode)super.createGraphicsNode(ctx, e);
        if (group == null) {
            return null;
        }
        group.add(refNode);
        return group;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void dispose() {
        this.selectedChild = null;
        super.dispose();
    }

    @Override
    protected void handleElementAdded(CompositeGraphicsNode gn, Node parent, Element childElt) {
        for (Node n = childElt.getPreviousSibling(); n != null; n = n.getPreviousSibling()) {
            if (n != childElt) continue;
            return;
        }
        if (childElt instanceof SVGTests && SVGUtilities.matchUserAgent(childElt, this.ctx.getUserAgent())) {
            if (this.selectedChild != null) {
                gn.remove(0);
                SVGSwitchElementBridge.disposeTree(this.selectedChild);
            }
            this.selectedChild = childElt;
            GVTBuilder builder = this.ctx.getGVTBuilder();
            GraphicsNode refNode = builder.build(this.ctx, childElt);
            if (refNode != null) {
                gn.add((Object)refNode);
            }
        }
    }

    protected void handleChildElementRemoved(Element e) {
        CompositeGraphicsNode gn = (CompositeGraphicsNode)this.node;
        if (this.selectedChild == e) {
            gn.remove(0);
            SVGSwitchElementBridge.disposeTree(this.selectedChild);
            this.selectedChild = null;
            GraphicsNode refNode = null;
            GVTBuilder builder = this.ctx.getGVTBuilder();
            for (Node n = e.getNextSibling(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() != 1) continue;
                Element ref = (Element)n;
                if (!(n instanceof SVGTests) || !SVGUtilities.matchUserAgent(ref, this.ctx.getUserAgent())) continue;
                refNode = builder.build(this.ctx, ref);
                this.selectedChild = ref;
                break;
            }
            if (refNode != null) {
                gn.add(refNode);
            }
        }
    }
}

