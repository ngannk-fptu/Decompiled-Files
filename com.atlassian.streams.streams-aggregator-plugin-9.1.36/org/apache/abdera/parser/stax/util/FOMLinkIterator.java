/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import javax.xml.namespace.QName;
import org.apache.abdera.model.Element;
import org.apache.abdera.parser.stax.FOMLink;
import org.apache.abdera.parser.stax.util.FOMElementIterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FOMLinkIterator
extends FOMElementIterator {
    public FOMLinkIterator(Element parent, Class<?> _class, QName attribute, String value, String defaultValue) {
        super(parent, _class, attribute, value != null ? FOMLink.getRelEquiv(value) : "alternate", defaultValue);
    }

    public FOMLinkIterator(Element parent, Class<?> _class) {
        super(parent, _class);
    }

    @Override
    protected boolean isMatch(Element el) {
        if (this.attribute != null) {
            String val = FOMLink.getRelEquiv(el.getAttributeValue(this.attribute));
            return val == null && this.value == null || val == null && this.value != null && this.value.equalsIgnoreCase(this.defaultValue) || val != null && val.equalsIgnoreCase(this.value);
        }
        return true;
    }
}

