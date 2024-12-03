/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.builder;

import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;

public interface CustomBuilder {
    public OMElement create(String var1, String var2, OMContainer var3, XMLStreamReader var4, OMFactory var5) throws OMException;
}

