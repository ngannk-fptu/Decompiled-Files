/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.store.Cur;

public interface XQuery {
    public XmlObject[] objectExecute(Cur var1, XmlOptions var2);

    public XmlCursor cursorExecute(Cur var1, XmlOptions var2);
}

