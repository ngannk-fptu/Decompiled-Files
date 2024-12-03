/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.values;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.impl.values.JavaQNameHolderEx;

public class XmlQNameRestriction
extends JavaQNameHolderEx
implements XmlQName {
    public XmlQNameRestriction(SchemaType type, boolean complex) {
        super(type, complex);
    }
}

