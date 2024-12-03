/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx;
import org.apache.xmlbeans.impl.xb.xsdschema.DerivationControl;

public class DerivationControlImpl
extends JavaStringEnumerationHolderEx
implements DerivationControl {
    private static final long serialVersionUID = 1L;

    public DerivationControlImpl(SchemaType sType) {
        super(sType, false);
    }

    protected DerivationControlImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}

