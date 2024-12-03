/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp;

import java.util.Iterator;
import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.XMPProperty;

public interface PropertyAccess {
    public void setProperty(XMPProperty var1);

    public XMPProperty getProperty(String var1, String var2);

    public XMPProperty getProperty(QName var1);

    public XMPProperty removeProperty(QName var1);

    public XMPProperty getValueProperty();

    public int getPropertyCount();

    public Iterator iterator();
}

