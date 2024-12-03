/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap12;

import java.io.Serializable;
import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap12.SOAP12HeaderFault;
import javax.xml.namespace.QName;

public interface SOAP12Header
extends ExtensibilityElement,
Serializable {
    public void setMessage(QName var1);

    public QName getMessage();

    public void setPart(String var1);

    public String getPart();

    public void setUse(String var1);

    public String getUse();

    public void setEncodingStyle(String var1);

    public String getEncodingStyle();

    public void setNamespaceURI(String var1);

    public String getNamespaceURI();

    public void addSOAP12HeaderFault(SOAP12HeaderFault var1);

    public List getSOAP12HeaderFaults();

    public SOAP12HeaderFault removeSOAP12HeaderFault(SOAP12HeaderFault var1);
}

