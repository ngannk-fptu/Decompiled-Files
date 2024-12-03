/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.extensions.soap;

import java.io.Serializable;
import java.util.List;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPHeaderFault;
import javax.xml.namespace.QName;

public interface SOAPHeader
extends ExtensibilityElement,
Serializable {
    public void setMessage(QName var1);

    public QName getMessage();

    public void setPart(String var1);

    public String getPart();

    public void setUse(String var1);

    public String getUse();

    public void setEncodingStyles(List var1);

    public List getEncodingStyles();

    public void setNamespaceURI(String var1);

    public String getNamespaceURI();

    public void addSOAPHeaderFault(SOAPHeaderFault var1);

    public SOAPHeaderFault removeSOAPHeaderFault(SOAPHeaderFault var1);

    public List getSOAPHeaderFaults();
}

