/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.gvt.GraphicsNode
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.Messages;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public class BridgeException
extends RuntimeException {
    protected Element e;
    protected String code;
    protected String message;
    protected Object[] params;
    protected int line;
    protected GraphicsNode node;

    public BridgeException(BridgeContext ctx, LiveAttributeException ex) {
        switch (ex.getCode()) {
            case 0: {
                this.code = "attribute.missing";
                break;
            }
            case 1: {
                this.code = "attribute.malformed";
                break;
            }
            case 2: {
                this.code = "length.negative";
                break;
            }
            default: {
                throw new IllegalStateException("Unknown LiveAttributeException error code " + ex.getCode());
            }
        }
        this.e = ex.getElement();
        this.params = new Object[]{ex.getAttributeName(), ex.getValue()};
        if (this.e != null && ctx != null) {
            this.line = ctx.getDocumentLoader().getLineNumber(this.e);
        }
    }

    public BridgeException(BridgeContext ctx, Element e, String code, Object[] params) {
        this.e = e;
        this.code = code;
        this.params = params;
        if (e != null && ctx != null) {
            this.line = ctx.getDocumentLoader().getLineNumber(e);
        }
    }

    public BridgeException(BridgeContext ctx, Element e, Exception ex, String code, Object[] params) {
        this.e = e;
        this.message = ex.getMessage();
        this.code = code;
        this.params = params;
        if (e != null && ctx != null) {
            this.line = ctx.getDocumentLoader().getLineNumber(e);
        }
    }

    public BridgeException(BridgeContext ctx, Element e, String message) {
        this.e = e;
        this.message = message;
        if (e != null && ctx != null) {
            this.line = ctx.getDocumentLoader().getLineNumber(e);
        }
    }

    public Element getElement() {
        return this.e;
    }

    public void setGraphicsNode(GraphicsNode node) {
        this.node = node;
    }

    public GraphicsNode getGraphicsNode() {
        return this.node;
    }

    @Override
    public String getMessage() {
        if (this.message != null) {
            return this.message;
        }
        String lname = "<Unknown Element>";
        SVGDocument doc = null;
        if (this.e != null) {
            doc = (SVGDocument)this.e.getOwnerDocument();
            lname = this.e.getLocalName();
        }
        String uri = doc == null ? "<Unknown Document>" : doc.getURL();
        Object[] fullparams = new Object[this.params.length + 3];
        fullparams[0] = uri;
        fullparams[1] = this.line;
        fullparams[2] = lname;
        System.arraycopy(this.params, 0, fullparams, 3, this.params.length);
        return Messages.formatMessage(this.code, fullparams);
    }

    public String getCode() {
        return this.code;
    }
}

