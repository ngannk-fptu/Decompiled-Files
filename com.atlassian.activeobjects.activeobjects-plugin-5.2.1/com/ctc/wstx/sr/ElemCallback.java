/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.sr.ElemAttrs;
import com.ctc.wstx.util.BaseNsContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public abstract class ElemCallback {
    public abstract Object withStartElement(Location var1, QName var2, BaseNsContext var3, ElemAttrs var4, boolean var5);
}

