/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import javax.xml.namespace.QName;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Attribute;
import org.apache.axiom.om.OMAttribute;

public class FOMAttribute
implements Attribute {
    private final OMAttribute attr;

    protected FOMAttribute(OMAttribute attr) {
        this.attr = attr;
    }

    public QName getQName() {
        return this.attr.getQName();
    }

    public String getText() {
        return this.attr.getAttributeValue();
    }

    public Attribute setText(String text) {
        this.attr.setAttributeValue(text);
        return this;
    }

    public Factory getFactory() {
        return (Factory)((Object)this.attr.getOMFactory());
    }

    public int hashCode() {
        int PRIME = 31;
        int result = 1;
        result = 31 * result + (this.attr == null ? 0 : this.attr.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        FOMAttribute other = (FOMAttribute)obj;
        return !(this.attr == null ? other.attr != null : !this.attr.equals(other.attr));
    }
}

