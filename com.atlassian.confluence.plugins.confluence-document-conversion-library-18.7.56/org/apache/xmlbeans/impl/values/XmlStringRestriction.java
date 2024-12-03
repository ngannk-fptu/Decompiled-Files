/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;

public class XmlStringRestriction
extends JavaStringHolderEx
implements XmlString {
    public XmlStringRestriction(SchemaType type, boolean complex) {
        super(type, complex);
    }
}

