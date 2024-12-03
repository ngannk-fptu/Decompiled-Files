/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.Proxy;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;

public final class FinalComponent
extends Proxy {
    private final int finalValue;
    private static final long serialVersionUID = 1L;

    public FinalComponent(XSDatatypeImpl baseType, int finalValue) {
        this(baseType.getNamespaceUri(), baseType.getName(), baseType, finalValue);
    }

    public FinalComponent(String nsUri, String newTypeName, XSDatatypeImpl baseType, int finalValue) {
        super(nsUri, newTypeName, baseType);
        this.finalValue = finalValue;
    }

    public boolean isFinal(int derivationType) {
        if ((this.finalValue & derivationType) != 0) {
            return true;
        }
        return super.isFinal(derivationType);
    }
}

