/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class OMChildrenWithSpecificAttributeIterator
extends OMFilterIterator {
    private QName attributeName;
    private String attributeValue;
    private boolean detach;
    private boolean doCaseSensitiveValueChecks = true;

    public OMChildrenWithSpecificAttributeIterator(OMNode currentChild, QName attributeName, String attributeValue, boolean detach) {
        super(new OMChildrenIterator(currentChild));
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.detach = detach;
    }

    public void setCaseInsensitiveValueChecks(boolean val) {
        this.doCaseSensitiveValueChecks = val;
    }

    protected boolean matches(OMNode node) {
        if (node instanceof OMElement) {
            OMAttribute attr = ((OMElement)node).getAttribute(this.attributeName);
            return attr != null && (this.doCaseSensitiveValueChecks ? attr.getAttributeValue().equals(this.attributeValue) : attr.getAttributeValue().equalsIgnoreCase(this.attributeValue));
        }
        return false;
    }

    public Object next() {
        Object result = super.next();
        if (this.detach) {
            this.remove();
        }
        return result;
    }
}

