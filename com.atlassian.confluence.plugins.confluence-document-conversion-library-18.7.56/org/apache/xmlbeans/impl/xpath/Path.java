/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.xpath.XPathEngine;

public interface Path {
    public XPathEngine execute(Cur var1, XmlOptions var2);
}

