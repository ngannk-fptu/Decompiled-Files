/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.params;

import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class XPathContainer
extends SignatureElementProxy
implements TransformParam {
    public XPathContainer(Document doc) {
        super(doc);
    }

    public void setXPath(String xpath) {
        for (Node childNode = this.getElement().getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
            Node nodeToBeRemoved = childNode;
            this.getElement().removeChild(nodeToBeRemoved);
        }
        Text xpathText = this.createText(xpath);
        this.appendSelf(xpathText);
    }

    public String getXPath() {
        return this.getTextFromTextChild();
    }

    @Override
    public String getBaseLocalName() {
        return "XPath";
    }
}

