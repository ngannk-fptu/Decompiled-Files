/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;

public class XmlValueNotSupportedException
extends XmlValueOutOfRangeException {
    public XmlValueNotSupportedException() {
    }

    public XmlValueNotSupportedException(String message) {
        super(message);
    }

    public XmlValueNotSupportedException(String code, Object[] args) {
        super(XmlError.formattedMessage(code, args));
    }
}

