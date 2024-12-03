/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.XBLOMContentElement
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge.svg12;

import org.apache.batik.anim.dom.XBLOMContentElement;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.svg12.ContentManager;
import org.apache.batik.bridge.svg12.ContentSelectionChangedEvent;
import org.apache.batik.bridge.svg12.ContentSelectionChangedListener;
import org.apache.batik.bridge.svg12.DefaultXBLManager;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XBLContentElementBridge
extends AbstractGraphicsNodeBridge {
    protected ContentChangedListener contentChangedListener;
    protected ContentManager contentManager;

    @Override
    public String getLocalName() {
        return "content";
    }

    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2004/xbl";
    }

    @Override
    public Bridge getInstance() {
        return new XBLContentElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        CompositeGraphicsNode gn = this.buildCompositeGraphicsNode(ctx, e, null);
        return gn;
    }

    public CompositeGraphicsNode buildCompositeGraphicsNode(BridgeContext ctx, Element e, CompositeGraphicsNode cgn) {
        XBLOMContentElement content = (XBLOMContentElement)e;
        AbstractDocument doc = (AbstractDocument)e.getOwnerDocument();
        DefaultXBLManager xm = (DefaultXBLManager)doc.getXBLManager();
        this.contentManager = xm.getContentManager(e);
        if (cgn == null) {
            cgn = new CompositeGraphicsNode();
            this.associateSVGContext(ctx, e, (GraphicsNode)cgn);
        } else {
            int s = cgn.size();
            for (int i = 0; i < s; ++i) {
                cgn.remove(0);
            }
        }
        GVTBuilder builder = ctx.getGVTBuilder();
        NodeList nl = this.contentManager.getSelectedContent(content);
        if (nl != null) {
            for (int i = 0; i < nl.getLength(); ++i) {
                GraphicsNode gn;
                Node n = nl.item(i);
                if (n.getNodeType() != 1 || (gn = builder.build(ctx, (Element)n)) == null) continue;
                cgn.add((Object)gn);
            }
        }
        if (ctx.isDynamic() && this.contentChangedListener == null) {
            this.contentChangedListener = new ContentChangedListener();
            this.contentManager.addContentSelectionChangedListener(content, this.contentChangedListener);
        }
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
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.contentChangedListener != null) {
            this.contentManager.removeContentSelectionChangedListener((XBLOMContentElement)this.e, this.contentChangedListener);
        }
    }

    protected class ContentChangedListener
    implements ContentSelectionChangedListener {
        protected ContentChangedListener() {
        }

        @Override
        public void contentSelectionChanged(ContentSelectionChangedEvent csce) {
            XBLContentElementBridge.this.buildCompositeGraphicsNode(XBLContentElementBridge.this.ctx, XBLContentElementBridge.this.e, (CompositeGraphicsNode)XBLContentElementBridge.this.node);
        }
    }
}

