/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;

public interface OMDocument
extends OMContainer {
    public static final String XML_10 = "1.0";
    public static final String XML_11 = "1.1";

    public OMElement getOMDocumentElement();

    public void setOMDocumentElement(OMElement var1);

    public String getCharsetEncoding();

    public void setCharsetEncoding(String var1);

    public String getXMLVersion();

    public void setXMLVersion(String var1);

    public String getXMLEncoding();

    public void setXMLEncoding(String var1);

    public String isStandalone();

    public void setStandalone(String var1);
}

