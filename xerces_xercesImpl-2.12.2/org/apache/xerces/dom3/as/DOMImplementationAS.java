/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASModel;
import org.apache.xerces.dom3.as.DOMASBuilder;
import org.apache.xerces.dom3.as.DOMASWriter;

public interface DOMImplementationAS {
    public ASModel createAS(boolean var1);

    public DOMASBuilder createDOMASBuilder();

    public DOMASWriter createDOMASWriter();
}

