/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Document
 *  org.jdom.Element
 */
package org.apache.velocity.anakia;

import java.util.List;
import org.apache.velocity.anakia.NodeList;
import org.apache.velocity.anakia.XPathCache;
import org.jdom.Document;
import org.jdom.Element;

public class XPathTool {
    public NodeList applyTo(String xpathSpec, Document doc) {
        return new NodeList(XPathCache.getXPath(xpathSpec).applyTo(doc), false);
    }

    public NodeList applyTo(String xpathSpec, Element elem) {
        return new NodeList(XPathCache.getXPath(xpathSpec).applyTo(elem), false);
    }

    public NodeList applyTo(String xpathSpec, List nodeSet) {
        return new NodeList(XPathCache.getXPath(xpathSpec).applyTo(nodeSet), false);
    }
}

