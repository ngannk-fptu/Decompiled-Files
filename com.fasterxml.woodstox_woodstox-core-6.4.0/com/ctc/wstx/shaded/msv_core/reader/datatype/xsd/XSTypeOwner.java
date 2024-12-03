/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;

public interface XSTypeOwner {
    public String getTargetNamespaceUri();

    public void onEndChild(XSDatatypeExp var1);
}

