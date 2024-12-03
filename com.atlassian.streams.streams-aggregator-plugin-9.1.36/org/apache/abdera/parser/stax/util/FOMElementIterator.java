/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import javax.xml.namespace.QName;
import org.apache.abdera.model.Element;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class FOMElementIterator
extends OMFilterIterator {
    protected QName attribute = null;
    protected String value = null;
    protected String defaultValue = null;
    protected Class _class = null;

    public FOMElementIterator(Element parent, Class _class) {
        super(((OMElement)((Object)parent)).getChildren());
        this._class = _class;
    }

    public FOMElementIterator(Element parent, Class _class, QName attribute, String value, String defaultValue) {
        this(parent, _class);
        this.attribute = attribute;
        this.value = value;
        this.defaultValue = defaultValue;
    }

    protected boolean matches(OMNode node) {
        return (this._class != null && this._class.isAssignableFrom(node.getClass()) || this._class == null) && this.isMatch((Element)((Object)node));
    }

    protected boolean isMatch(Element el) {
        if (this.attribute != null) {
            String val = el.getAttributeValue(this.attribute);
            return val == null && this.value == null || val == null && this.value != null && this.value.equals(this.defaultValue) || val != null && val.equals(this.value);
        }
        return true;
    }
}

