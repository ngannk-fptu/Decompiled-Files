/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.RootGraphicsNode
 *  org.apache.batik.util.HaltingThread
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.DocumentBridge;
import org.apache.batik.bridge.GenericBridge;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.InterruptedBridgeException;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.util.HaltingThread;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GVTBuilder
implements SVGConstants {
    public GraphicsNode build(BridgeContext ctx, Document document) {
        ctx.setDocument(document);
        ctx.initializeDocument(document);
        ctx.setGVTBuilder(this);
        DocumentBridge dBridge = ctx.getDocumentBridge();
        RootGraphicsNode rootNode = null;
        try {
            rootNode = dBridge.createGraphicsNode(ctx, document);
            Element svgElement = document.getDocumentElement();
            GraphicsNode topNode = null;
            Bridge bridge = ctx.getBridge(svgElement);
            if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
                return null;
            }
            GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
            topNode = gnBridge.createGraphicsNode(ctx, svgElement);
            if (topNode == null) {
                return null;
            }
            rootNode.getChildren().add(topNode);
            this.buildComposite(ctx, svgElement, (CompositeGraphicsNode)topNode);
            gnBridge.buildGraphicsNode(ctx, svgElement, topNode);
            dBridge.buildGraphicsNode(ctx, document, rootNode);
        }
        catch (BridgeException ex) {
            ex.setGraphicsNode((GraphicsNode)rootNode);
            throw ex;
        }
        if (ctx.isInteractive()) {
            ctx.addUIEventListeners(document);
            ctx.addGVTListener(document);
        }
        if (ctx.isDynamic()) {
            ctx.addDOMListeners();
        }
        return rootNode;
    }

    public GraphicsNode build(BridgeContext ctx, Element e) {
        Bridge bridge = ctx.getBridge(e);
        if (bridge instanceof GenericBridge) {
            ((GenericBridge)bridge).handleElement(ctx, e);
            this.handleGenericBridges(ctx, e);
            return null;
        }
        if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            this.handleGenericBridges(ctx, e);
            return null;
        }
        GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
        if (!gnBridge.getDisplay(e)) {
            this.handleGenericBridges(ctx, e);
            return null;
        }
        GraphicsNode gn = gnBridge.createGraphicsNode(ctx, e);
        if (gn != null) {
            if (gnBridge.isComposite()) {
                this.buildComposite(ctx, e, (CompositeGraphicsNode)gn);
            } else {
                this.handleGenericBridges(ctx, e);
            }
            gnBridge.buildGraphicsNode(ctx, e, gn);
        }
        if (ctx.isDynamic()) {
            // empty if block
        }
        return gn;
    }

    protected void buildComposite(BridgeContext ctx, Element e, CompositeGraphicsNode parentNode) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            this.buildGraphicsNode(ctx, (Element)n, parentNode);
        }
    }

    protected void buildGraphicsNode(BridgeContext ctx, Element e, CompositeGraphicsNode parentNode) {
        if (HaltingThread.hasBeenHalted()) {
            throw new InterruptedBridgeException();
        }
        Bridge bridge = ctx.getBridge(e);
        if (bridge instanceof GenericBridge) {
            ((GenericBridge)bridge).handleElement(ctx, e);
            this.handleGenericBridges(ctx, e);
            return;
        }
        if (bridge == null || !(bridge instanceof GraphicsNodeBridge)) {
            this.handleGenericBridges(ctx, e);
            return;
        }
        if (!CSSUtilities.convertDisplay(e)) {
            this.handleGenericBridges(ctx, e);
            return;
        }
        GraphicsNodeBridge gnBridge = (GraphicsNodeBridge)bridge;
        try {
            GraphicsNode gn = gnBridge.createGraphicsNode(ctx, e);
            if (gn != null) {
                parentNode.getChildren().add(gn);
                if (gnBridge.isComposite()) {
                    this.buildComposite(ctx, e, (CompositeGraphicsNode)gn);
                } else {
                    this.handleGenericBridges(ctx, e);
                }
                gnBridge.buildGraphicsNode(ctx, e, gn);
            } else {
                this.handleGenericBridges(ctx, e);
            }
        }
        catch (BridgeException ex) {
            GraphicsNode errNode = ex.getGraphicsNode();
            if (errNode != null) {
                parentNode.getChildren().add(errNode);
                gnBridge.buildGraphicsNode(ctx, e, errNode);
                ex.setGraphicsNode(null);
            }
            throw ex;
        }
    }

    protected void handleGenericBridges(BridgeContext ctx, Element e) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (!(n instanceof Element)) continue;
            Element e2 = (Element)n;
            Bridge b = ctx.getBridge(e2);
            if (b instanceof GenericBridge) {
                ((GenericBridge)b).handleElement(ctx, e2);
            }
            this.handleGenericBridges(ctx, e2);
        }
    }
}

