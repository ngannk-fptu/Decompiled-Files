/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import org.apache.xerces.xni.parser.XMLInputSource;
import org.w3c.dom.Node;

public final class DOMInputSource
extends XMLInputSource {
    private Node fNode;

    public DOMInputSource() {
        this((Node)null);
    }

    public DOMInputSource(Node node) {
        super(null, DOMInputSource.getSystemIdFromNode(node), null);
        this.fNode = node;
    }

    public DOMInputSource(Node node, String string) {
        super(null, string, null);
        this.fNode = node;
    }

    public Node getNode() {
        return this.fNode;
    }

    public void setNode(Node node) {
        this.fNode = node;
    }

    private static String getSystemIdFromNode(Node node) {
        if (node != null) {
            try {
                return node.getBaseURI();
            }
            catch (NoSuchMethodError noSuchMethodError) {
                return null;
            }
            catch (Exception exception) {
                return null;
            }
        }
        return null;
    }
}

