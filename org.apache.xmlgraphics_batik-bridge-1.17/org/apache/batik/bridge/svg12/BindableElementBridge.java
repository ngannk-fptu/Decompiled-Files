/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.BindableElement
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge.svg12;

import org.apache.batik.anim.dom.BindableElement;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.ScriptingEnvironment;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.bridge.svg12.ContentSelectionChangedEvent;
import org.apache.batik.bridge.svg12.SVG12BridgeUpdateHandler;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class BindableElementBridge
extends AbstractGraphicsNodeBridge
implements SVG12BridgeUpdateHandler {
    @Override
    public String getNamespaceURI() {
        return "*";
    }

    @Override
    public String getLocalName() {
        return "*";
    }

    @Override
    public Bridge getInstance() {
        return new BindableElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        CompositeGraphicsNode gn = this.buildCompositeGraphicsNode(ctx, e, null);
        return gn;
    }

    public CompositeGraphicsNode buildCompositeGraphicsNode(BridgeContext ctx, Element e, CompositeGraphicsNode gn) {
        ScriptingEnvironment se;
        BindableElement be = (BindableElement)e;
        Element shadowTree = be.getXblShadowTree();
        UpdateManager um = ctx.getUpdateManager();
        ScriptingEnvironment scriptingEnvironment = se = um == null ? null : um.getScriptingEnvironment();
        if (se != null && shadowTree != null) {
            se.addScriptingListeners(shadowTree);
        }
        if (gn == null) {
            gn = new CompositeGraphicsNode();
            this.associateSVGContext(ctx, e, (GraphicsNode)gn);
        } else {
            int s = gn.size();
            for (int i = 0; i < s; ++i) {
                gn.remove(0);
            }
        }
        GVTBuilder builder = ctx.getGVTBuilder();
        if (shadowTree != null) {
            GraphicsNode shadowNode = builder.build(ctx, shadowTree);
            if (shadowNode != null) {
                gn.add((Object)shadowNode);
            }
        } else {
            for (Node m = e.getFirstChild(); m != null; m = m.getNextSibling()) {
                GraphicsNode n;
                if (m.getNodeType() != 1 || (n = builder.build(ctx, (Element)m)) == null) continue;
                gn.add((Object)n);
            }
        }
        return gn;
    }

    @Override
    public void dispose() {
        BindableElement be = (BindableElement)this.e;
        if (be != null && be.getCSSFirstChild() != null) {
            BindableElementBridge.disposeTree(be.getCSSFirstChild());
        }
        super.dispose();
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return null;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        this.initializeDynamicSupport(ctx, e, node);
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        BindableElement be = (BindableElement)this.e;
        Element shadowTree = be.getXblShadowTree();
        if (shadowTree == null && evt.getTarget() instanceof Element) {
            this.handleElementAdded((CompositeGraphicsNode)this.node, this.e, (Element)((Object)evt.getTarget()));
        }
    }

    @Override
    public void handleBindingEvent(Element bindableElement, Element shadowTree) {
        CompositeGraphicsNode gn = this.node.getParent();
        gn.remove((Object)this.node);
        BindableElementBridge.disposeTree(this.e);
        this.handleElementAdded(gn, this.e.getParentNode(), this.e);
    }

    @Override
    public void handleContentSelectionChangedEvent(ContentSelectionChangedEvent csce) {
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

