/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.encryption;

import java.util.Iterator;
import org.w3c.dom.Element;

public interface Reference {
    public String getType();

    public String getURI();

    public void setURI(String var1);

    public Iterator<Element> getElementRetrievalInformation();

    public void addElementRetrievalInformation(Element var1);

    public void removeElementRetrievalInformation(Element var1);
}

