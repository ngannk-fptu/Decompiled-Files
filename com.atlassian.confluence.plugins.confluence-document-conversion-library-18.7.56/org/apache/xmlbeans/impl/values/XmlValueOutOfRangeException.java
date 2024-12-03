/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.XmlError;

public class XmlValueOutOfRangeException
extends IllegalArgumentException {
    public XmlValueOutOfRangeException() {
    }

    public XmlValueOutOfRangeException(String message) {
        super(message);
    }

    public XmlValueOutOfRangeException(String code, Object[] args) {
        super(XmlError.formattedMessage(code, args));
    }
}

