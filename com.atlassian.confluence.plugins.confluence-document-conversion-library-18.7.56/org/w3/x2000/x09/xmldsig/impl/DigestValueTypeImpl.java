/*
 * Decompiled with CFR 0.152.
 */
package org.w3.x2000.x09.xmldsig.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaBase64HolderEx;
import org.w3.x2000.x09.xmldsig.DigestValueType;

public class DigestValueTypeImpl
extends JavaBase64HolderEx
implements DigestValueType {
    private static final long serialVersionUID = 1L;

    public DigestValueTypeImpl(SchemaType sType) {
        super(sType, false);
    }

    protected DigestValueTypeImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}

